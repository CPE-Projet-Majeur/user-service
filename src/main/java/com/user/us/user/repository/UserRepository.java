package com.user.us.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.user.us.user.model.UserModel;

public interface UserRepository extends CrudRepository<UserModel, Integer> {

    List<UserModel> findByLoginAndPassword(String login,String password);
    Optional<UserModel> findByLogin(String username);
    void deleteByLogin(String username);
}

//public interface UserRepository extends JpaRepository<UserModel, Integer> {
//
//    List<UserModel> findByLoginAndPwd(String login,String pwd);
//
//}

