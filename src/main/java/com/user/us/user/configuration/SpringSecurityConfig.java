package com.user.us.user.configuration;

import com.user.us.user.controller.jwt.JwtAuthEntryPoint;
import com.user.us.user.controller.jwt.JwtRequestFilter;
import com.user.us.user.provider.AppAuthProvider;
import com.user.us.user.service.AuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.*;


@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {
    private final AuthService authService;
    private final JwtAuthEntryPoint jwtAuthenticationEntryPoint;
    private final JwtRequestFilter jwtRequestFilter;
    private final PasswordEncoder passwordEncoder;

    public SpringSecurityConfig( AuthService authService,
                           JwtAuthEntryPoint jwtAuthenticationEntryPoint,
                           JwtRequestFilter jwtRequestFilter,
                           PasswordEncoder passwordEncoder) {
        this.authService=authService;
        this.jwtAuthenticationEntryPoint=jwtAuthenticationEntryPoint;
        this.jwtRequestFilter=jwtRequestFilter;
        this.passwordEncoder=passwordEncoder;
    }


    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authConfiguration) throws Exception {
        return authConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        //use to allow direct login call without hidden value csfr (Cross Site Request Forgery) needed
//        http.csrf().disable();
        http.csrf(csrf->csrf.disable())
                .authenticationProvider(getProvider())
                .exceptionHandling(ex ->ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .sessionManagement(sess->sess
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        auth-> auth
                                .requestMatchers("/heroes").authenticated()
                                .requestMatchers("/heroes2").hasAuthority("ROLE_USER")
                                .requestMatchers("/users").permitAll() // Get les users
                                .requestMatchers("/login").permitAll() // Se log
                                .requestMatchers(HttpMethod.GET, "/users/**").permitAll() // Get un user specific
                                .requestMatchers(HttpMethod.PUT, "/users/**").authenticated() // update un user specific
                                .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                                .requestMatchers(HttpMethod.DELETE,"/users/{id}").authenticated()
                                .anyRequest().authenticated())
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider getProvider() {
        AppAuthProvider provider = new AppAuthProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(authService);
        return provider;
    }

}
