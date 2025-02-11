package gov.nist.hit.auth.core.service;

import gov.nist.hit.auth.core.exception.UserPreRequirementNotMetException;
import gov.nist.hit.auth.core.model.HITToolPrincipal;
import gov.nist.hit.auth.core.model.HITToolUser;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public abstract class HITAuthenticationEntryPoint<E extends HITToolPrincipal, T extends HITToolUser<E>> implements AuthenticationEntryPoint {
	private final UserPreRequirementManager<E, T> userPreRequirementManager;

	public HITAuthenticationEntryPoint(UserPreRequirementManager<E, T> userPreRequirementManager) {
		this.userPreRequirementManager = userPreRequirementManager;
	}

	public abstract void handleException(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException;

	@Override
	public void commence(
			HttpServletRequest request,
			HttpServletResponse response,
			AuthenticationException authException
	) throws IOException, ServletException {
		if(authException instanceof UserPreRequirementNotMetException && userPreRequirementManager != null) {
			UserPreRequirementNotMetException userPreRequirementNotMetException = (UserPreRequirementNotMetException) authException;
			userPreRequirementManager.handlePreRequirementNotMet((E) userPreRequirementNotMetException.getPrincipal(), userPreRequirementNotMetException.getPreRequirements(), request, response);
		} else {
			handleException(request, response, authException);
		}
	}
}
