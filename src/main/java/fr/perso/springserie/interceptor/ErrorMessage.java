package fr.perso.springserie.interceptor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.logging.LogLevel;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorMessage {
    private LogLevel level;
    private int statusCode;
    private String message;
    private String type;

}
