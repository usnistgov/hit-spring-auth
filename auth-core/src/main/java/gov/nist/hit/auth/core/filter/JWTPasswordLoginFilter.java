package gov.nist.hit.auth.core.filter;

import java.io.IOException;
import java.util.Collections;

import gov.nist.hit.auth.core.exception.InvalidPrincipal;
import gov.nist.hit.auth.core.model.HITToolPrincipal;
import gov.nist.hit.auth.core.service.HITAuthenticationEntryPoint;
import gov.nist.hit.auth.core.service.HITAuthenticationService;
import gov.nist.hit.auth.core.model.HITToolUser;
import gov.nist.hit.auth.core.model.PasswordLoginRequest;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import com.fasterxml.jackson.databind.ObjectMapper;


public class JWTPasswordLoginFilter<E extends HITToolPrincipal, T extends HITToolUser<E>> extends AbstractAuthenticationProcessingFilter {
	private final HITAuthenticationService<E, T> authenticationService;
	private final HITAuthenticationEntryPoint<E, T> exceptionEntryPoint;

	public JWTPasswordLoginFilter(
			String url,
			AuthenticationManager authManager,
			HITAuthenticationService<E, T> authenticationService,
			HITAuthenticationEntryPoint<E, T> exceptionEntryPoint
	) {
		super(new AntPathRequestMatcher(url));
		this.authenticationService = authenticationService;
		this.exceptionEntryPoint = exceptionEntryPoint;
		setAuthenticationManager(authManager);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) throws AuthenticationException, IOException {
		PasswordLoginRequest credentials = new ObjectMapper().readValue(req.getInputStream(), PasswordLoginRequest.class);
		return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(credentials.getUsername(),credentials.getPassword(), Collections.emptyList()));
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication auth) throws IOException, ServletException {
		T userAccount = getPrincipal(auth);
		if(userAccount != null) {
			this.authenticationService.verifyAccountAndHandleLoginResponse(req, res, userAccount);
		}
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
		this.exceptionEntryPoint.commence(request, response, failed);
	}

	private T getPrincipal(Authentication auth) {
		try {
			return (T) auth.getPrincipal();
		} catch(Exception e) {
			throw new InvalidPrincipal(e.getMessage());
		}
	}
	
}