package com.user.us.user.configuration;

import com.user.us.user.provider.AppAuthProvider;
import com.user.us.user.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.*;

import java.io.IOException;


@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {
    @Autowired
    AuthService authService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authConfiguration) throws Exception {
        return authConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        //use to allow direct login call without hidden value csfr (Cross Site Request Forgery) needed
//        http.csrf().disable();
        http.csrf(csrf->csrf.disable());
        http.authenticationProvider(getProvider())
                .exceptionHandling(ex ->ex.authenticationEntryPoint(new Http403ForbiddenEntryPoint()))
                .formLogin(flogin->flogin
                        .loginProcessingUrl("/login")
                        .successHandler(successHandler())
                        .failureHandler(failureHandler())
                        .permitAll())
                .logout(lout->lout.logoutUrl("/logout")
                        .permitAll()
                        .invalidateHttpSession(true))
                .authorizeHttpRequests(auth->
                        auth.requestMatchers("/heroes").authenticated()
                                .anyRequest().authenticated());

        return http.build();
    }

    @Bean
    public AuthenticationProvider getProvider() {
        AppAuthProvider provider = new AppAuthProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(authService);
        return provider;
    }


    //Use to redefine return in case of good or bad auth
    private AuthenticationFailureHandler failureHandler() {
        System.out.println("failure");
        return new SimpleUrlAuthenticationFailureHandler() {
            public void onAuthenticationFailure(HttpServletRequest request,
                                                HttpServletResponse response, AuthenticationException exception)
                    throws IOException, ServletException {
                response.setContentType("text/html;charset=UTF-8");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication Failed. Wrong username or password or both");
            }
        };
    }


    private AuthenticationSuccessHandler successHandler() {
        return new SimpleUrlAuthenticationSuccessHandler() {
            public void onAuthenticationSuccess(HttpServletRequest request,
                                                HttpServletResponse response, Authentication authentication)
                    throws IOException, ServletException {
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().println("LoginSuccessful");
            }
        };
    }

}
