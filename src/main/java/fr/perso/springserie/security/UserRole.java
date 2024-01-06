package fr.perso.springserie.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum UserRole {

    USER_ROLE("ROLE_user"),
    ADMIN_ROLE("ROLE_admin"),
    SUPER_ADMIN_ROLE("ROLE_super_admin");
    private String name;

    public static String[] getRoles(UserRole... roles){
        return  Arrays.stream(roles).map(userRole -> userRole.getName().replace("ROLE", "")).toArray(String[]::new);
    }
}
