package fr.perso.springserie.interceptor.exception;

public class FileException extends RuntimeException {
    public FileException(Throwable e) {
        super(e);
    }
}
