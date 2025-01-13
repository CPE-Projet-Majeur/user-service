package com.user.us.user.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="id")
@Table(name="users")
public class UserModel implements Serializable, UserDetails {

    private static final long serialVersionUID = 2733795832476568049L;
    @Id
    //@GeneratedValue(strategy = GenerationType.AUTO)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(unique = true, nullable = false)
    private String login;
    private String password;
    private String lastName;
    private String firstName;
    private String email;
    private String house;
    private float account;
    private Integer wins;
    private Integer defeats;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "USER_ROLE",
            joinColumns = {@JoinColumn(name = "USER_ID")},
            inverseJoinColumns = {@JoinColumn(name = "ROLE_ID")})
    private List<Role> roleList;

    public List<Role> getRoleList() {
        return roleList;
    }

    public List<String> getRoleListString() {
        return roleList.stream()
                .map(Role::getRoleName)
                .collect(Collectors.toList());
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }

    public void addRoleToList(Role role){
        this.roleList.add(role);
    }

    public UserModel() {
        this.login = "";
        this.password = "";
        this.lastName="lastname_default";
        this.firstName ="firstname_default";
        this.email="email_default";
    }

    public UserModel(String login, String pwd) {
        super();
        this.login = login;
        this.password = pwd;
        this.lastName="lastname_default";
        this.firstName ="firstname_default";
        this.email="email_default";
        Role userRole = new Role("USER_ROLE");
        this.addRoleToList(userRole);
        this.defeats = 0;
        this.wins = 0;
        this.account = 100;
        this.house = "Gryffindor";
    }

    public UserModel(UserDTO user) {
        this.id=user.getId();
        this.login=user.getLogin();
        this.password =user.getPassword();
        this.lastName=user.getLastName();
        this.firstName =user.getFirstName();
        this.email=user.getEmail();
        this.house = user.getHouse();
//        this.defeats = user.getDefeats();
//        this.wins = user.getWins();
//        this.account = user.getAccount();
        // Valeurs par défauts pour la création
        this.defeats = 0;
        this.wins = 0;
        this.account = 100;

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

//    public String getPassword() {
//        return password;
//    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getDefeats() {
        return defeats;
    }

    public void setDefeats(Integer defeats) {
        this.defeats = defeats;
    }

    public Integer getWins() {
        return wins;
    }

    public void setWins(Integer wins) {
        this.wins = wins;
    }

    public float getAccount() {
        return account;
    }

    public void setAccount(float account) {
        this.account = account;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> roleListAuthorities= new ArrayList<>();
        for(Role r: roleList){
            roleListAuthorities.add(new SimpleGrantedAuthority(r.getRoleName()));
        }
        return roleListAuthorities;
    }


    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }
    @Override
    public boolean isAccountNonLocked() {
        return false;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }
    @Override
    public boolean isEnabled() {
        return false;
    }

}
