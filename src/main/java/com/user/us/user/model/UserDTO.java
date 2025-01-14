package com.user.us.user.model;

import java.util.List;
import java.util.stream.Collectors;

public class UserDTO {
    private Integer id;
    private String login;
    private String password;
    private String lastName;
    private String firstName;
    private String email;
    private House house;
    // Think if relevant or not to put it in dto
    private float account;
    private Integer wins;
    private Integer defeats;
    //Maybe will change
    private List<String> roleList;

    // Constructeur par défaut requis par Jackson
    public UserDTO() {
    }

    public UserDTO(UserModel user) {
        this.id = user.getId();
        this.login = user.getLogin();
        this.password = user.getPassword();
        this.lastName = user.getLastName();
        this.firstName = user.getFirstName();
        this.email = user.getEmail();
        this.house = House.valueOf(user.getHouse());
        this.defeats = user.getDefeats();
        this.wins = user.getWins();
        this.account = user.getAccount();
        this.roleList = user.getRoleList().stream()
                .map(Role::getRoleName)
                .collect(Collectors.toList());
    }

    public List<String> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<String> roleList) {
        this.roleList = roleList;
    }

    public float getAccount() {
        return account;
    }

    public void setAccount(float account) {
        this.account = account;
    }

    public Integer getWins() {
        return wins;
    }

    public void setWins(Integer wins) {
        this.wins = wins;
    }

    public Integer getDefeats() {
        return defeats;
    }

    public void setDefeats(Integer defeats) {
        this.defeats = defeats;
    }

    public House getHouse() {
        return house;
    }

    public void setHouse(House house) {
        this.house = house;
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

	public String getEmail() {
		return email;
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

    public String getFirstName() {
        return firstName;
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

    @Override
    public String toString() {
        return "UserDto [username=" +
                login +
                "]";
    }
//    @Override
//    public String toString() {
//        return "UserDto [username=" +
//                login +
//                "Roles : "+getRoleList()+"]";
//    }
}

