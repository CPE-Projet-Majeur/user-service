package com.user.us.user.common.tools;
import com.user.us.user.model.UserDTO;
import com.user.us.user.model.UserModel;

public class DTOMapper {

    public static UserDTO fromUserModelToUserDTO(UserModel uM) {
        UserDTO uDto =new UserDTO(uM);
        return uDto;
    }

}

