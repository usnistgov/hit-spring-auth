package gov.nist.hit.auth.core.filter;

import gov.nist.hit.auth.core.model.HITToolPrincipal;
import gov.nist.hit.auth.core.service.HITAuthenticationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class LogoutFilter extends OncePerRequestFilter {

	private final HITAuthenticationService authenticationService;
	private final AntPathRequestMatcher pathMatcher;

	public LogoutFilter(HITAuthenticationService authenticationService, String logoutUrl) {
		this.pathMatcher = new AntPathRequestMatcher(logoutUrl);
		this.authenticationService = authenticationService;
	}

	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain
	) throws ServletException, IOException {
		if(pathMatcher.matches(request)) {
			Authentication authenticationToken = SecurityContextHolder.getContext().getAuthentication();
			if(authenticationToken instanceof UsernamePasswordAuthenticationToken) {
				HITToolPrincipal principal = (HITToolPrincipal) authenticationToken.getPrincipal();
				authenticationService.logout(request, response, principal);
				return;
			}
		}
		filterChain.doFilter(request, response);
	}
}
