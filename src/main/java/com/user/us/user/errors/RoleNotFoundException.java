package com.user.us.user.errors;

public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException(String roleName) {
        super("Le rôle " + roleName + " n'existe pas, veuillez le créer au préalable " +
                "ou demander à un administrateur de le faire.");
    }
}