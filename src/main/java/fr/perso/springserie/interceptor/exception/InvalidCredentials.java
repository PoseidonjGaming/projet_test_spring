package fr.perso.springserie.interceptor.exception;

public class InvalidCredentials extends RuntimeException {
    public InvalidCredentials() {
        super("Invalid Credentials");
    }
}
