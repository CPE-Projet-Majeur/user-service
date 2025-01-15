package com.user.us.user.common.tools;
import com.user.us.user.model.Role;
import com.user.us.user.model.UserDTO;
import com.user.us.user.model.UserModel;

import java.util.List;
import java.util.stream.Collectors;

public class DTOMapper {

    public static UserDTO fromUserModelToUserDTO(UserModel uM) {
        UserDTO uDto =new UserDTO(uM);
        return uDto;
    }

    public static List<Role> fromListStringToListRole(List<String> roleList){
        return roleList.stream()
                .map(Role::new) // Suppose que le constructeur de Role accepte une String (ex: Role(String roleName))
                .collect(Collectors.toList());
    }

    public static List<String> fromListRoleToListString(List<Role> roleList){
        return roleList.stream()
                .map(Role::getRoleName) // GetRoleName pour donner le nom format string
                .collect(Collectors.toList());
    }

}

