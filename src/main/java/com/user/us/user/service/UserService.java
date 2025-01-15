package com.user.us.user.service;

import java.util.*;

import com.user.us.user.common.tools.DTOMapper;
import com.user.us.user.common.tools.JwtTokenUtil;
import com.user.us.user.errors.InvalidTokenException;
import com.user.us.user.errors.RoleNotFoundException;
import com.user.us.user.model.*;
import com.user.us.user.repository.RoleRepository;
import com.user.us.user.repository.UserRepository;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import static com.user.us.user.common.tools.DTOMapper.fromListRoleToListString;


// Ne pas renvoyer le pwd lors des get ou put ? (meme si encrypted)
@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    AuthService authService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;


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
        Optional<UserModel> uOpt = userRepository.findByLogin(login);
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


    // Can change any attribute of the UserDTO,
    @Transactional
    public UserDTO addUserAsAdmin(UserDTO user) {
        UserModel u = fromUDtoToUModel(user);
        setAttributesUser(u,user);
        // Encodage du password
        u.setPassword(passwordEncoder.encode(u.getPassword()));

        try {
            return this.addUser(u);
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
    public LoginResponse addUserGiveToken(UserDTO user) {
        UserModel u = fromUDtoToUModel(user);
        initializeNewUser(u);
        // Encodage du password
        u.setPassword(passwordEncoder.encode(u.getPassword()));
        try {
            UserDTO uSaved = this.addUser(u);
            final UserDetails userDetails = authService.loadUserByUsername(uSaved.getLogin());
            final String token = jwtTokenUtil.generateToken(userDetails);
            uSaved.setPassword("*************");
            return new LoginResponse(token, uSaved);

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

    // Transaction et erreurs doivent être gérées dans les functions parentes
    public UserDTO addUser(UserModel u){
        // Encodage du password
        u.setPassword(passwordEncoder.encode(u.getPassword()));

        UserModel userSaved = userRepository.save(u);
        return DTOMapper.fromUserModelToUserDTO(userSaved);
    }

    @Transactional
    public UserDTO updateUser(UserDTO user, String token) {
        // TODO : warning not letting ppl set everything
        UserModel u = fromUDtoToUModel(user);
        // Encodage du pwd
        u.setPassword(passwordEncoder.encode(u.getPassword()));
        String login = user.getLogin();

        UserDetails userDetails = authService.loadUserByUsername(login);
        String jwtToken = null;
        // try catch redundant with filter
        if (token != null && token.startsWith("Bearer ")) {
            jwtToken = token.substring(7);
        } else {
//            logger.warn("JWT Token does not begin with Bearer String");
        }

        if (!jwtTokenUtil.validateToken(jwtToken, userDetails)) {
            throw new InvalidTokenException("Le token JWT est invalide ou a expiré.");
        }

        UserModel userBeforeUpdate = userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        u.setRoleList(userBeforeUpdate.getRoleList());
        u.setWins(userBeforeUpdate.getWins());
        u.setDefeats(userBeforeUpdate.getDefeats());
        u.setAccount(userBeforeUpdate.getAccount());

        try {
            UserModel uBd = userRepository.save(u);

            uBd.setPassword("*************");

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
    public UserDTO updateUserAsAdmin(UserDTO user) {
        // TODO : if the attributes isn't specified not update it
        UserModel u = fromUDtoToUModel(user);
        setAttributesUser(u,user);
        // Encodage du pwd
        u.setPassword(passwordEncoder.encode(u.getPassword()));


//        // gets the current roles to not erase or update them
//        UserModel currentUser = userRepository.findByLogin(user.getLogin())
//                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//
//        List<Role> authorities = new ArrayList<>();
//        currentUser.getRoleListString().forEach(roleName -> {
//            Role role = roleRepository.findByRoleName(roleName)
//                    .orElseGet(() -> roleRepository.save(new Role(roleName))); // Pour ne pas renvoyer d'erreur mais ne devrait pas
//            authorities.add(role);
//        });
//        u.setRoleList(authorities);

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

    private void initializeNewUser(UserModel userModel) {
        userModel.setAccount(100);
        userModel.setWins(0);
        userModel.setDefeats(0);
        Role defaultRole = roleRepository.findByRoleName("ROLE_USER")
                .orElseThrow(() -> new RoleNotFoundException("ROLE_USER"));
        userModel.setRoleList(List.of(defaultRole));
    }

    private void setAttributesUser(UserModel user, UserDTO userDto){
        user.setAccount(userDto.getAccount());
        user.setWins(userDto.getWins());
        user.setDefeats(userDto.getDefeats());
        this.rolesExistingCheck(fromListRoleToListString(user.getRoleList()),user);

    }

    private void rolesExistingCheck(List<String> roleList, UserModel user){
        // Parcourir les rôles dans RoleDTO
        //TODO : pour l'instant throw error des qu'une erreur rencontrée, faire en sorte qu'il throw a la fin avec une liste des roles qui ne vont pas
        for (String roleName : roleList) {
            Role role = roleRepository.findByRoleName(roleName)
                    .orElseThrow(() -> new RoleNotFoundException("Role not found: " + roleName));

            // Ajouter le rôle à l'utilisateur
            user.addRoleToList(role);
        }
    }
}
