package com.user.us.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.user.us.user.model.UserModel;

public interface UserRepository extends CrudRepository<UserModel, Integer> {

    List<UserModel> findByLoginAndPwd(String login,String pwd);

}

//public interface UserRepository extends JpaRepository<UserModel, Integer> {
//
//    List<UserModel> findByLoginAndPwd(String login,String pwd);
//
//}

