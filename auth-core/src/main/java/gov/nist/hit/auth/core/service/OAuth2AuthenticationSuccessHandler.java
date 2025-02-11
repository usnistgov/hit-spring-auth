package gov.nist.hit.auth.core.service;

import gov.nist.hit.auth.core.exception.InvalidPrincipal;
import gov.nist.hit.auth.core.exception.OAuth2LoginAuthenticationException;
import gov.nist.hit.auth.core.model.HITToolPrincipal;
import gov.nist.hit.auth.core.model.HITToolUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

public class OAuth2AuthenticationSuccessHandler<E extends HITToolPrincipal, T extends HITToolUser<E>> implements AuthenticationSuccessHandler {

	private final HITUserAccountService<E, T> userService;
	private final HITAuthenticationService<E, T> authenticationService;
	private final UserPreRequirementManager<E, T> userPreRequirementManager;

	public OAuth2AuthenticationSuccessHandler(
			HITUserAccountService<E, T> userService,
			HITAuthenticationService<E, T> authenticationService,
			UserPreRequirementManager<E, T> userPreRequirementManager
	) {
		this.userService = userService;
		this.authenticationService = authenticationService;
		this.userPreRequirementManager = userPreRequirementManager;
	}

	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		try {
			if(authentication.getPrincipal() instanceof OidcUser) {
				OidcUser user = (OidcUser) authentication.getPrincipal();
				T account = userService.findAccount(user);
				if(account == null) {
					account = userService.createAccountForUser(user);
				}
				E principal = authenticationService.validateAccountAndGetPrincipal(account);
				authenticationService.addAuthenticationCookie(principal, response);
				if(principal.getPreRequirements() != null && !principal.getPreRequirements().isEmpty() && userPreRequirementManager != null) {
					userPreRequirementManager.handlePreRequirementOnLogin(
							principal,
							principal.getPreRequirements(),
							request,
							response
					);
				} else {
					response.sendRedirect("/");
				}
			} else {
				throw new OAuth2LoginAuthenticationException(
						new InvalidPrincipal("Principal is not OIDC user")
				);
			}
		} catch(Exception ex) {
			throw new OAuth2LoginAuthenticationException(ex);
		}
	}
}
