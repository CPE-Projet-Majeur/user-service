package com.user.us.user.model;

import java.util.HashSet;
import java.util.Set;

public class UserDTO {
    private Integer id;
    private String login;
    private String pwd;
    private float account;
    private String lastName;
    private String surName;
    private String email;

    // Exemple security gitlab à voir
    public Integer role;
    public Integer getRole() {
        return role;
    }

    public void setRole(final Integer role) {
        this.role = role;
    }
    // Fin exemple

    public UserDTO() {
    }

    public UserDTO(UserModel user) {
        this.id = user.getId();
        this.login = user.getLogin();
        this.pwd = user.getPwd();
        this.account = user.getAccount();
        this.lastName = user.getLastName();
        this.surName = user.getSurName();
        this.email = user.getEmail();
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

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public float getAccount() {
        return account;
    }

    public void setAccount(float account) {
        this.account = account;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSurName() {
        return surName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "UserDto [username=" +
                login +
                ", role=" +
                role + "]";
    }

}
