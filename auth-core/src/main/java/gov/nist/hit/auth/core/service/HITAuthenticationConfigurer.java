package gov.nist.hit.auth.core.service;

import gov.nist.hit.auth.core.filter.JWTAuthenticationFilter;
import gov.nist.hit.auth.core.filter.JWTPasswordLoginFilter;
import gov.nist.hit.auth.core.filter.LogoutFilter;
import gov.nist.hit.auth.core.model.HITToolPrincipal;
import gov.nist.hit.auth.core.model.HITToolUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Service;

@Service
public class HITAuthenticationConfigurer<E extends HITToolPrincipal, T extends HITToolUser<E>> {
	public static class Builder<E extends HITToolPrincipal, T extends HITToolUser<E>> {
		private boolean addJwtAuthenticationFilter = false;
		private boolean addPasswordLoginFilter = false;
		private boolean addOAuth2Login = false;
		private boolean addLogoutFilter = false;
		private boolean addOAuth2DefaultFailureHandler = false;
		private String oauth2FailureErrorPageResource;
		private String logoutUrl;
		private String passwordLoginURL;

		private final HttpSecurity http;
		private final HITAuthenticationConfigurer<E, T> configurer;

		private Builder(HttpSecurity httpSecurity, HITAuthenticationConfigurer<E, T> configurer) {
			this.http = httpSecurity;
			this.configurer = configurer;
		}

		public Builder<E, T> addJwtAuthenticationFilter() {
			addJwtAuthenticationFilter = true;
			return this;
		}

		public Builder<E, T> addDefaultOAuth2FailureHandler(String oauth2FailureErrorPageResource) throws Exception {
			addOAuth2DefaultFailureHandler = true;
			assertion(oauth2FailureErrorPageResource != null && !oauth2FailureErrorPageResource.isEmpty(), "Resource location can't be null or empty");
			this.oauth2FailureErrorPageResource = oauth2FailureErrorPageResource;
			return this;
		}

		public Builder<E, T> addDefaultOAuth2FailureHandler() {
			addOAuth2DefaultFailureHandler = true;
			return this;
		}


		public Builder<E, T> addPasswordLogin(String url) throws Exception {
			addPasswordLoginFilter = true;
			addJwtAuthenticationFilter = true;
			assertion(url != null && !url.isEmpty(), "Login URL can't be null or empty");
			this.passwordLoginURL = url;
			return this;
		}

		public Builder<E, T> addLogout(String logoutURL) throws Exception {
			addLogoutFilter = true;
			addJwtAuthenticationFilter = true;
			assertion(logoutURL != null && !logoutURL.isEmpty(), "Logout URL can't be null or empty");
			this.logoutUrl = logoutURL;
			return this;
		}

		public Builder<E, T> addOAuth2Login() {
			addOAuth2Login = true;
			addJwtAuthenticationFilter = true;
			return this;
		}

		public HttpSecurity configure() throws Exception {
			http.exceptionHandling((customizer) -> {
				customizer.authenticationEntryPoint(configurer.handler);
			});

			if(addLogoutFilter) {
				assertion(configurer.authenticationService != null,  "No HITAuthenticationService was configured, required for LogoutFilter");
				http.addFilterAfter(new LogoutFilter(
						configurer.authenticationService,
						logoutUrl
				), AuthorizationFilter.class);
			}

			if (addPasswordLoginFilter) {
				assertion(configurer.authenticationService != null,  "No HITAuthenticationService was configured, required for PasswordLoginFilter");
				assertion(configurer.authenticationManager != null, "No AuthenticationManager was configured, required for PasswordLoginFilter");
				assertion(configurer.handler != null, "No HITAuthenticationEntryPoint was configured, required for PasswordLoginFilter");

				http.addFilterBefore(new JWTPasswordLoginFilter<>(
						this.passwordLoginURL,
						configurer.authenticationManager,
						configurer.authenticationService,
						configurer.handler
				), UsernamePasswordAuthenticationFilter.class);
			}
			if(addJwtAuthenticationFilter) {
				assertion(configurer.jwtTokenAuthenticationService != null, "No JWTTokenAuthenticationService  was configured, required for JWTAuthenticationFilter");

				http.addFilterBefore(new JWTAuthenticationFilter<>(
						configurer.jwtTokenAuthenticationService
				), UsernamePasswordAuthenticationFilter.class);
			}
			if(addOAuth2Login) {
				assertion(configurer.userService != null, "No HITUserAccountService was configured, required for OAuth2Login");
				assertion(configurer.authenticationService != null, "No HITAuthenticationService was configured, required for OAuth2Login");

				http.oauth2Login((customizer) -> {
					customizer.successHandler(new OAuth2AuthenticationSuccessHandler<>(
							configurer.userService,
							configurer.authenticationService,
							configurer.userPreRequirementManager
					));

					if(addOAuth2DefaultFailureHandler) {
						if(oauth2FailureErrorPageResource != null && !oauth2FailureErrorPageResource.isEmpty()) {
							customizer.failureHandler(new DefaultOAuth2AuthenticationFailureHandler(
									oauth2FailureErrorPageResource
							));
						} else {
							customizer.failureHandler(new DefaultOAuth2AuthenticationFailureHandler());
						}
					}
				});
			}

			http.sessionManagement((customizer) -> {
				customizer.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
			});

			http.csrf(AbstractHttpConfigurer::disable);

			return http;
		}
	}

	private final JWTTokenAuthenticationService<E, T> jwtTokenAuthenticationService;
	private final HITAuthenticationEntryPoint<E, T> handler;
	private final HITAuthenticationService<E, T> authenticationService;
	private final HITUserAccountService<E, T> userService;
	private final AuthenticationManager authenticationManager;
	private final UserPreRequirementManager<E, T> userPreRequirementManager;


	public HITAuthenticationConfigurer(
			@Autowired(required = false) JWTTokenAuthenticationService<E, T> jwtTokenAuthenticationService,
			@Autowired(required = false) HITAuthenticationEntryPoint<E, T> handler,
			@Autowired(required = false) HITAuthenticationService<E, T> authenticationService,
			@Autowired(required = false) HITUserAccountService<E, T> userService,
			@Autowired(required = false) AuthenticationManager authenticationManager,
			@Autowired(required = false) UserPreRequirementManager<E, T> userPreRequirementManager
	) {

		this.jwtTokenAuthenticationService = jwtTokenAuthenticationService;
		this.handler = handler;
		this.authenticationService = authenticationService;
		this.userService = userService;
		this.authenticationManager = authenticationManager;
		this.userPreRequirementManager = userPreRequirementManager;
	}

	public Builder<E, T> configure(HttpSecurity http) {
		return new Builder<>(http, this);
	}

	private static void assertion(boolean condition, String message) throws Exception {
		if(!condition) {
			throw new Exception(message);
		}
	}
}
