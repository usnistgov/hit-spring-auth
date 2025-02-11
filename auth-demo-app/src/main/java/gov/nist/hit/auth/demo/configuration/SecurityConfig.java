package gov.nist.hit.auth.demo.configuration;

import gov.nist.hit.auth.core.service.*;
import gov.nist.hit.auth.core.service.impl.SimpleKeyPair;
import gov.nist.hit.auth.demo.model.User;
import gov.nist.hit.auth.demo.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AuthenticatedAuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	private HITAuthenticationConfigurer<UserInfo, User> hitAuthenticationConfigurer;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(authorizeRequests ->
				                           authorizeRequests
						                           .requestMatchers("/api/login").not().authenticated()
						                           .requestMatchers("/api/me").fullyAuthenticated()
						                           .requestMatchers("/api/setup-profile").fullyAuthenticated()
						                           .requestMatchers("/api/register").not().authenticated()
						                           .requestMatchers("/api/**").access(
														   HITAuthenticationManager.meetsPreRequirements(
																   AuthenticatedAuthorizationManager.fullyAuthenticated(),
																   "username"
								                           )
						                           )
						                           .anyRequest().permitAll()
		);

		return hitAuthenticationConfigurer
				.configure(http)
				.addPasswordLogin("/api/login")
				.addOAuth2Login()
				.addDefaultOAuth2FailureHandler()
				.addLogout("/api/logout")
				.configure()
				.build();
	}



	// http://localhost:8080/oauth2/authorization/nist-okta
}