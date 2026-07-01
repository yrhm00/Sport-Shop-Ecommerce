package be.henallux.janvier.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                // Pages accessibles à tous
                .antMatchers("/", "/accueil", "/produits/**", "/connexion/**", "/inscription/**", "/panier/**", "/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                // Pages nécessitant une authentification
                .antMatchers("/commandes/**").authenticated()
                // Toutes les autres requêtes nécessitent une authentification
                .anyRequest().authenticated()
            .and()
            .formLogin()
                .loginPage("/connexion")
                .loginProcessingUrl("/connexion")
                .defaultSuccessUrl("/", true)
                .failureUrl("/connexion?error")
                .permitAll()
            .and()
            .logout()
                .logoutUrl("/deconnexion")
                .logoutSuccessUrl("/?logout")
                .permitAll();
            // La protection CSRF est activée par défaut (Spring Security).
            // Chaque formulaire POST envoie le jeton via ${_csrf} ou <form:form>.

        return http.build();
    }
}
