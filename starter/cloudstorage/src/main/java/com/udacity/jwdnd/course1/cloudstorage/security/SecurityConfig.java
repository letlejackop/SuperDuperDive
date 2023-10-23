package com.udacity.jwdnd.course1.cloudstorage.security;

import com.udacity.jwdnd.course1.cloudstorage.services.AuthenticationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig  {
    //this is the new security config for the current
    //springboot version that I am working on
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((auth) -> auth.requestMatchers(
                        new AntPathRequestMatcher("/login"),
                        new AntPathRequestMatcher("/signup")).permitAll()
                ).formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/home", true)
                ).logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout/**", HttpMethod.GET.name()))
                        .logoutSuccessUrl("/login?logout").permitAll()
                ).authorizeHttpRequests((auth) -> auth.requestMatchers(
                        new AntPathRequestMatcher("/home/**"),
                        new AntPathRequestMatcher("/result"),
                        new AntPathRequestMatcher("/credential/**"),
                        new AntPathRequestMatcher("/errors"),
                        new AntPathRequestMatcher("/error"),
                        new AntPathRequestMatcher("/file/**"),
                        new AntPathRequestMatcher("/favicon.ico"),
                        new AntPathRequestMatcher("/note/**"))
                        .authenticated()
                );
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
                new AntPathRequestMatcher("/js/**"),
                new AntPathRequestMatcher("/h2-console/**"),
                new AntPathRequestMatcher("/css/**"),
                new AntPathRequestMatcher("/static/**")

        );
    }
}