package com.mrj.keycloak.springmultitenancy.configuration;

import java.util.Arrays;

import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakSecurityComponents;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.preauth.x509.X509AuthenticationFilter;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class SpringKeycloakSecurityConfiguration {

    @Configuration
    @EnableWebSecurity
    @ConditionalOnProperty(name = "keycloak.enabled", havingValue = "true", matchIfMissing = true)
    @ComponentScan(basePackageClasses = KeycloakSecurityComponents.class)
    public static class KeycloakConfigurationAdapter extends KeycloakWebSecurityConfigurerAdapter {

        @Bean
        protected CustomSecurityCorsFilter customCorsFilter(){
            return new CustomSecurityCorsFilter();
        }

        /**
         * Defines the session authentication strategy.
         */
        @Bean
        @Override
        protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
            // required for bearer-only applications.
            return new NullAuthenticatedSessionStrategy();
        }

        /**
         * Registers the KeycloakAuthenticationProvider with the authentication manager.
         */
        @Autowired
        public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
            KeycloakAuthenticationProvider keycloakAuthenticationProvider = keycloakAuthenticationProvider();
            // simple Authority Mapper to avoid ROLE_
            keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
            auth.authenticationProvider(keycloakAuthenticationProvider);
        }

        /**
         * Required to handle spring boot configurations
         * @return
         */
        @Bean
        public KeycloakConfigResolver KeycloakConfigResolver() {
            return new HeaderBasedConfigResolver();
        }

        /**
         * Configuration spécifique à keycloak (ajouts de filtres, etc)
         * @param http
         * @throws Exception
         */
        @Override
        public void configure(HttpSecurity http) throws Exception
        {
            http
                    .sessionManagement()
                        // use previously declared bean
                        .sessionAuthenticationStrategy(sessionAuthenticationStrategy())


                    // keycloak filters for securisation
                    .and()
                        // this is needed to pass the preflight CORS security on BROWSER
                        .addFilterBefore(customCorsFilter(), SecurityContextPersistenceFilter.class)
                        .addFilterBefore(keycloakPreAuthActionsFilter(), LogoutFilter.class)
                        .addFilterBefore(keycloakAuthenticationProcessingFilter(), X509AuthenticationFilter.class)
                        .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint())

                    // add cors options
                    .and().cors()
                    // delegate logout endpoint to spring security

                    .and()
                        .logout()
                        .addLogoutHandler(keycloakLogoutHandler())
                        .logoutUrl("/logout").logoutSuccessHandler(
                            // logout handler for API
                            (HttpServletRequest request, HttpServletResponse response, Authentication authentication) ->
                                    response.setStatus(HttpServletResponse.SC_OK)
                         )
                    .and().apply(new CommonSpringKeycloakTutorialsSecuritAdapter());


        }


        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedOrigins(Arrays.asList("*"));
            configuration.setAllowedMethods(Arrays.asList(HttpMethod.OPTIONS.name(), "GET","POST"));
            configuration.setAllowedHeaders(Arrays.asList("Access-Control-Allow-Headers", "Access-Control-Allow-Origin", "Authorization", "realm"));
            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", configuration);
            return source;
        }

		@Override
		public void init(WebSecurity builder) throws Exception {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void configure(WebSecurity builder) throws Exception {
			// TODO Auto-generated method stub
			
		}
    }



    /**
     * See https://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/#jc-custom-dsls
     * <ul><li>Manage paths securisation here !You must use this configuration in tests to validate routes securisation</li>
     * <li>Use with                 .and().apply(new CommonVitodocSecuritAdapter()) on http dsl</li>
     * </ul>
     */
    public static class CommonSpringKeycloakTutorialsSecuritAdapter extends AbstractHttpConfigurer<CommonSpringKeycloakTutorialsSecuritAdapter, HttpSecurity> {

        @Override
        public void init(HttpSecurity http) throws Exception {
            // any method that adds another configurer
            // must be done in the init method
            http
                    // disable csrf because of API mode
                    .csrf().disable()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                    .and()
                        // manage routes securisation here
                        .authorizeRequests().requestMatchers(HttpMethod.OPTIONS).permitAll()

                    // manage routes securisation here
                    .and()
                        .authorizeRequests()
                            .requestMatchers(HttpMethod.OPTIONS).permitAll()
                            .requestMatchers("/logout", "/", "/unsecured").permitAll()
                            .requestMatchers("/user").hasRole("USER")
                            .requestMatchers("/admin").hasRole("ADMIN")

                            .anyRequest().denyAll();

        }

    }

}