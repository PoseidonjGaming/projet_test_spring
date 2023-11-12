package fr.perso.springserie.interceptor;

import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ErrorInterceptor extends ResponseEntityExceptionHandler {
    private ResponseEntity<?> handleError(Exception ex, WebRequest request, int code, LogLevel level, String type, String message, HttpStatus status){

        ex.printStackTrace();
        return handleExceptionInternal(ex, new ErrorMessage(level,code,message, type), new HttpHeaders(), status, request);
    }

    @ExceptionHandler(value={NullPointerException.class})
    protected ResponseEntity<?> handleIllegalArgument(Exception ex, WebRequest request){
        return handleError(ex, request, 400, LogLevel.ERROR, "ERROR", ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
