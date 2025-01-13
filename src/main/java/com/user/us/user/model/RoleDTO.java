package com.user.us.user.model;

import java.util.Collections;
import java.util.List;

public class RoleDTO {
    private List<String> roleList;

    public List<String> getRoleList() {
        if (roleList == null) {
            return Collections.emptyList();
        }
        return roleList;
    }

    public void setRoleList(List<String> roleList) {
        this.roleList = roleList;
    }
}
