package be.henallux.janvier.configuration;

import org.apache.tomcat.util.http.Rfc6265CookieProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    /** Pages accessibles a tout visiteur, authentifie ou non. */
    private static final String[] ACCESSIBLE_A_TOUS = new String[] {
        "/", "/a-propos", "/produits/**", "/connexion/**", "/inscription/**", "/panier/**"
    };

    /** Ressources statiques. */
    private static final String[] RESSOURCES_STATIQUES = new String[] {
        "/css/**", "/js/**", "/images/**"
    };

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt : algorithme de hachage adaptatif recommande pour les mots de passe.
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

        // Protection CSRF active (comportement par defaut de Spring Security).
        // Chaque formulaire POST transmet le jeton via <form:form> ou ${_csrf}.
        http.csrf(Customizer.withDefaults());

        http
            .authorizeRequests()
                .antMatchers(RESSOURCES_STATIQUES).permitAll()
                .antMatchers(ACCESSIBLE_A_TOUS).permitAll()
                // Seuls les clients authentifies peuvent commander et payer.
                .antMatchers("/commandes/**").authenticated()
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
                // Avec la protection CSRF active, Spring Security n'accepte la
                // deconnexion qu'en POST : une simple visite d'URL ne deconnecte pas.
                .logoutUrl("/deconnexion")
                .logoutSuccessUrl("/?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            ;

        // En-tetes de securite HTTP.
        http
            .headers()
                .contentSecurityPolicy(contentSecurityPolicyConfig -> contentSecurityPolicyConfig
                        .policyDirectives("default-src 'self'; "
                                        + "script-src 'self'; "
                                        + "style-src 'self'; "
                                        + "img-src 'self' data:; "
                                        // Le formulaire de confirmation est d'abord traite localement,
                                        // puis redirige vers la page d'approbation PayPal.
                                        + "form-action 'self' https://www.sandbox.paypal.com "
                                        + "https://www.paypal.com; "
                                        + "base-uri 'self'; "
                                        + "frame-ancestors 'none'"));

        return http.build();
    }

    /**
     * Attribut SameSite sur les cookies (protection supplementaire contre le CSRF).
     *
     * "Lax" et non "Strict" : au retour de PayPal, le navigateur effectue une
     * navigation depuis un site tiers ; avec "Strict" le cookie de session ne
     * serait pas renvoye et le client reviendrait deconnecte.
     */
    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> cookieProcessorCustomizer() {
        return factory -> factory.addContextCustomizers(context -> {
            Rfc6265CookieProcessor cookieProcessor = new Rfc6265CookieProcessor();
            cookieProcessor.setSameSiteCookies("Lax");
            context.setCookieProcessor(cookieProcessor);
        });
    }
}
