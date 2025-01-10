package com.user.us.user.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

@Entity
@JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="id")
@Table(name="users")
public class UserModel implements Serializable {

    private static final long serialVersionUID = 2733795832476568049L;
    @Id
    //@GeneratedValue(strategy = GenerationType.AUTO)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String login;
    private String password;
    private String lastName;
    private String firstName;
    private String email;
    private String house;

    public UserModel() {
        this.login = "";
        this.password = "";
        this.lastName="lastname_default";
        this.firstName="firstName_default";
        this.email="email_default";
        this.house = "default_house";
    }

    public UserModel(String login, String pwd) {
        super();
        this.login = login;
        this.password = pwd;
        this.lastName="lastname_default";
        this.firstName="firstName_default";
        this.email="email_default";
        this.house = "default_house";
    }

    public UserModel(UserDTO user) {
        this.id=user.getId();
        this.login=user.getLogin();
        this.password=user.getPassword();
        this.house=user.getHouse();
        this.lastName=user.getLastName();
        this.firstName=user.getFirstName();
        this.email=user.getEmail();
    }

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Integer getId() {
		return id;
	}

	public String getLogin() {
		return login;
	}

	public String getPassword() {
		return password;
	}

	public String getLastName() {
		return lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getEmail() {
		return email;
	}

	public String getHouse() {
		return house;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setHouse(String house) {
		this.house = house;
	}

}
