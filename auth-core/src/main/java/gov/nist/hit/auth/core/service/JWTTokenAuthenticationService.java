package gov.nist.hit.auth.core.service;

import gov.nist.hit.auth.core.model.HITToolPrincipal;
import gov.nist.hit.auth.core.model.HITToolUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.web.util.WebUtils;

public class JWTTokenAuthenticationService<E extends HITToolPrincipal, T extends HITToolUser<E>> {

	private final String COOKIE_NAME;
	private final CryptoKey keys;
	private final HITAuthenticationService<E, T> authenticationService;

	public JWTTokenAuthenticationService(String COOKIE_NAME, CryptoKey keys, HITAuthenticationService<E, T> authenticationService) {
		this.COOKIE_NAME = COOKIE_NAME;
		this.keys = keys;
		this.authenticationService = authenticationService;
	}

	public AbstractAuthenticationToken getAuthentication(HttpServletRequest request) throws Exception {
		Cookie token = WebUtils.getCookie(request, COOKIE_NAME);
		if (token != null && token.getValue() != null && !token.getValue().isEmpty()) {
			Jws<Claims> jwt = Jwts.parser()
			                      .setSigningKey(keys.getPublicKey())
			                      .parseClaimsJws(token.getValue());

			E principal = this.authenticationService.validateTokenAndGetPrincipal(jwt);
			return this.authenticationService.createAuthenticationToken(principal, token.getValue());
		} else {
			return null;
		}
	}

	public Cookie getAuthCookie(HttpServletRequest request) {
		return WebUtils.getCookie(request, COOKIE_NAME);
	}

	public Jws<Claims> getClaims(Cookie token) throws Exception {
		if (token != null && token.getValue() != null && !token.getValue().isEmpty()) {
			return Jwts.parser()
	                      .setSigningKey(keys.getPublicKey())
	                      .parseClaimsJws(token.getValue());
		} else {
			return null;
		}
	}

	public AbstractAuthenticationToken getAuthentication(Jws<Claims> jwt, Cookie token) throws Exception {
		if(jwt != null) {
			E principal = this.authenticationService.validateTokenAndGetPrincipal(jwt);
			return this.authenticationService.createAuthenticationToken(principal, token.getValue());
		}
		return null;
	}

	public void clearAuthCookie(HttpServletResponse response) {
		Cookie authCookie = new Cookie(COOKIE_NAME, "");
		authCookie.setPath("/");
		authCookie.setMaxAge(0);
		authCookie.setHttpOnly(true);
		authCookie.setAttribute("SameSite", "Strict");
		response.addCookie(authCookie);
	}
}