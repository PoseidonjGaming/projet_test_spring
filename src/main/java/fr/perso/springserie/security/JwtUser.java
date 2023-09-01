package fr.perso.springserie.security;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtUser {
    private String username;
    private String password;
}
