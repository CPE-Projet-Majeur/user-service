package com.user.us.user.model;

import java.util.HashSet;
import java.util.Set;

public class UserDTO {
    private Integer id;
    private String login;
    private String password;
    private String lastName;
    private String firstName;
    private String email;
    private String house;
    
    public UserDTO() {
    }

    public UserDTO(UserModel user) {
        this.id = user.getId();
        this.login = user.getLogin();
        this.password = user.getPassword();
        this.lastName = user.getLastName();
        this.firstName = user.getFirstName();
        this.email = user.getEmail();
        this.house = user.getHouse();
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