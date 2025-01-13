package com.user.us.user.service;

import java.util.*;

import com.user.us.user.common.tools.DTOMapper;
import com.user.us.user.errors.RoleNotFoundException;
import com.user.us.user.model.Role;
import com.user.us.user.model.RoleDTO;
import com.user.us.user.repository.RoleRepository;
import com.user.us.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.user.us.user.model.UserDTO;
import com.user.us.user.model.UserModel;
import org.springframework.transaction.annotation.Transactional;


// Ne pas renvoyer le pwd lors des get ou put ? (meme si encrypted)
@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public List<UserModel> getAllUsers() {
        List<UserModel> userList = new ArrayList<>();
        userRepository.findAll().forEach(userList::add);
        return userList;
    }

    public List<UserModel> getAllUserNoPwd() {
        List<UserModel> userList = new ArrayList<>();
        userRepository.findAll().forEach(s -> {
            s.setPassword("*************");
            userList.add(s);
        });
        return userList;
    }

    public Optional<UserModel> getUser(String id) {
        return userRepository.findById(Integer.valueOf(id));
    }

    public Optional<UserModel> getUserByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    public Optional<UserModel> getUserNoPwdByLogin(String login) {
        Optional<UserModel> uOpt =userRepository.findByLogin(login);
        if( uOpt.isPresent()){
            UserModel u=uOpt.get();
            u.setPassword("*************");
            return Optional.of(u);
        }
        return null;
    }

    public Optional<UserModel> getUserNoPwdById(Integer id) {
        Optional<UserModel> uOpt =userRepository.findById(id);
        if( uOpt.isPresent()){
            UserModel u=uOpt.get();
            u.setPassword("*************");
            return Optional.of(u);
        }
        return null;
    }


    @Transactional
    public UserDTO addUser(UserDTO user) {
        UserModel u = fromUDtoToUModel(user);
        // Encodage du password
        u.setPassword(passwordEncoder.encode(u.getPassword()));
        // needed to avoid detached entity passed to persist error
        // Role
//        List<Role> authorities = new ArrayList<>();
//        user.getRoleList().forEach(roleName -> {
//            Role role = roleRepository.findByRoleName(roleName)
//                    .orElseGet(() -> roleRepository.save(new Role(roleName))); // Crée le rôle s'il n'existe pas
//            authorities.add(role);
//        });

        Role defaultRole = roleRepository.findByRoleName("ROLE_USER")
                .orElseThrow(() -> new RoleNotFoundException("ROLE_USER"));
        u.setRoleList(List.of(defaultRole));

        try {
            UserModel u_saved = userRepository.save(u);
            return DTOMapper.fromUserModelToUserDTO(u_saved);
//            UserModel u_saved = userRepository.save(u); // Premier appel pour gérer l'entité
//            UserModel uBd = userRepository.save(u_saved); // Deuxième appel pour éviter "detached entity" error
//            return DTOMapper.fromUserModelToUserDTO(uBd);
        } catch (DataIntegrityViolationException e) {
            // Gérer l'erreur : le login existe déjà
            System.out.println("DataIntegrity violation");
            //TODO : check if true
            throw new IllegalStateException(Objects.requireNonNull(e.getRootCause()).getMessage());
        } catch (Exception e) {
                System.out.println("Erreur complète : " + e.getMessage());
                e.printStackTrace(); // Pour afficher la pile complète dans les logs
                throw new RuntimeException("Une erreur s'est produite lors de la sauvegarde de l'utilisateur : " + e.getMessage(), e);
        }
    }

    @Transactional
    public UserDTO updateUser(UserDTO user) {
        UserModel u = fromUDtoToUModel(user);
        // Encodage du pwd
        u.setPassword(passwordEncoder.encode(u.getPassword()));

        // gets the current roles to not erase or update them
        UserModel currentUser = userRepository.findByLogin(user.getLogin())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<Role> authorities = new ArrayList<>();
        currentUser.getRoleListString().forEach(roleName -> {
            Role role = roleRepository.findByRoleName(roleName)
                    .orElseGet(() -> roleRepository.save(new Role(roleName))); // Pour ne pas renvoyer d'erreur mais ne devrait pas
            authorities.add(role);
        });
        u.setRoleList(authorities);

        try {
            UserModel uBd =userRepository.save(u);
            return DTOMapper.fromUserModelToUserDTO(uBd);
        } catch (DataIntegrityViolationException e) {
            // Gérer l'erreur : le login existe déjà
            System.out.println("DataIntegrity violation");
            throw new IllegalStateException(Objects.requireNonNull(e.getRootCause()).getMessage());
        } catch (Exception e) {
            System.out.println("Erreur complète : " + e.getMessage());
            e.printStackTrace(); // Pour afficher la pile complète dans les logs
            throw new RuntimeException("Une erreur s'est produite lors de la sauvegarde de l'utilisateur : " + e.getMessage(), e);
        }

    }


    @Transactional
    public UserDTO updateUser(UserModel user) {
        UserModel uBd = userRepository.save(user);
        uBd.setPassword(passwordEncoder.encode(uBd.getPassword()));

        return DTOMapper.fromUserModelToUserDTO(uBd);
    }

    public UserDTO addRoleToUser(RoleDTO roles, Integer id) {
        UserModel currentUser = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Parcourir les rôles dans RoleDTO
        for (String roleName : roles.getRoleList()) {
            Role role = roleRepository.findByRoleName(roleName)
                    .orElseThrow(() -> new RoleNotFoundException("Role not found: " + roleName));

            // Ajouter le rôle à l'utilisateur
            currentUser.addRoleToList(role);
        }
        // Sauvegarder l'utilisateur mis à jour
        userRepository.save(currentUser);
        return DTOMapper.fromUserModelToUserDTO(currentUser);
    }

    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }
    public void deleteUser(String login) {
        userRepository.deleteByLogin(login);
    }

    private UserModel fromUDtoToUModel(UserDTO user) {
        UserModel u = new UserModel(user);
        return u;
    }

    public Map<String, List<String>> addRoles(RoleDTO roles) {
        List<String> existingRoles = new ArrayList<>();
        List<String> createdRoles = new ArrayList<>();

        for (String roleName : roles.getRoleList()) {
            // Vérifie si le role existe déjà
            Optional<Role> existingRole = roleRepository.findByRoleName(roleName);

            if (existingRole.isPresent()) {
                // S'il existe on le emt dans la liste prévue pour
                existingRoles.add(roleName);
            } else {
                // Sinon on le crée et l'ajoute dans l'autre liste
                roleRepository.save(new Role(roleName));
                createdRoles.add(roleName);
            }
        }

        Map<String, List<String>> response = new HashMap<>();
        response.put("alreadyExistingRoles", existingRoles);
        response.put("rolesCreated", createdRoles);

        return response;
    }

}
