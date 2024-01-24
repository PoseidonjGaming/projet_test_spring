package fr.perso.springserie.config;

import fr.perso.springserie.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {
    private static final String[] WHITE_LISTED_URLS = new String[]{
            "user/authenticate", "user/save",
            "file/load", "season/bySeries/**",
            "episode/bySeasons/**", "user/generateuser",
            "category/list", "character/**",
            "series/sort", "series/detail/**",
            "series/byCategories", "series/search",
            "series/list",
            "actor/byIds", "season/byIds",
            "episode/byIds", "actor/detail/**",
            "movie/sort", "movie/search",
            "episode/search", "episode/list",
            "user/registration", "user/**",
            "review/search", "user/search"
    };

    private static final String[] USER_ADMIN_ROUTES = new String[]{
            "series/byIds","review/save", "review/delete/*"
    };
    public static final String[] USER_ROUTES=new String[]{
            "user/watch/**"
    };
    private final JwtFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    @Lazy
    public WebSecurityConfig(JwtFilter jwtAuthFilter, UserDetailsService userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource())).csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                            try {

                                auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                        .requestMatchers(USER_ADMIN_ROUTES).hasAnyRole("user", "super_admin")
                                        .requestMatchers(WHITE_LISTED_URLS).permitAll()
                                        .anyRequest().hasRole("super_admin");
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                ).addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .userDetailsService(userDetailsService)
                .build();
    }

}
