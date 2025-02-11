package gov.nist.hit.auth.core.filter;

import gov.nist.hit.auth.core.model.HITToolPrincipal;
import gov.nist.hit.auth.core.model.HITToolUser;
import gov.nist.hit.auth.core.service.JWTTokenAuthenticationService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

public class JWTAuthenticationFilter<E extends HITToolPrincipal, T extends HITToolUser<E>> extends GenericFilterBean {
	
	private final JWTTokenAuthenticationService<E, T> tokenService;
	Logger logger = LoggerFactory.getLogger(JWTAuthenticationFilter.class);


	public JWTAuthenticationFilter(JWTTokenAuthenticationService<E, T> tokenService) {
		this.tokenService = tokenService;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		try {
			Cookie auth = tokenService.getAuthCookie((HttpServletRequest) request);
			Jws<Claims> jwt =  tokenService.getClaims(auth);
			AbstractAuthenticationToken authentication = tokenService.getAuthentication(jwt, auth);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
		catch (Exception exception) {
			SecurityContextHolder.clearContext();
			this.tokenService.clearAuthCookie((HttpServletResponse) response);
			logger.error("There was an error when authenticating the request: {}", exception.getMessage(), exception);
		}
		filterChain.doFilter(request, response);
	}
}