package gov.nist.hit.auth.core.service;

import gov.nist.hit.auth.core.model.HITToolPrincipal;
import gov.nist.hit.auth.core.model.HITToolUser;
import io.jsonwebtoken.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.util.Date;
import java.util.stream.Collectors;

public abstract class HITAuthenticationService<E extends HITToolPrincipal, T extends HITToolUser<E>> {

	private final UserPreRequirementManager<E, T> userPreRequirementManager;
	private final String COOKIE_NAME;
	private final long COOKIE_MAX_AGE_MS;
	private final CryptoKey keys;

	public HITAuthenticationService(
			UserPreRequirementManager<E, T> userPreRequirementManager,
			@Value("${hit.auth.cookie.name}") String cookieName,
			@Value("${hit.auth.cookie.duration-minutes}") long duration,
			CryptoKey cryptoKey
	) {
		this.userPreRequirementManager = userPreRequirementManager;
		COOKIE_NAME = cookieName;
		COOKIE_MAX_AGE_MS = duration * 60 * 1000;
		this.keys = cryptoKey;
	}

	public HITAuthenticationService(
			@Value("${hit.auth.cookie.name}") String cookieName,
			@Value("${hit.auth.cookie.duration-minutes}") long duration,
			CryptoKey cryptoKey
	) {
		this.userPreRequirementManager = null;
		COOKIE_NAME = cookieName;
		COOKIE_MAX_AGE_MS = duration * 60 * 1000;
		this.keys = cryptoKey;
	}

	public abstract void verifyAccountAndHandleLoginResponse(
		HttpServletRequest request,
		HttpServletResponse response,
		T account
	) throws IOException, ServletException, AuthenticationException;

	public abstract T findAccount(Jws<Claims> token);

	public abstract void verifyAccount(T account) throws AuthenticationException;

	public abstract void logout(HttpServletRequest request, HttpServletResponse response, HITToolPrincipal principal) throws ServletException, IOException;

	public E validateAccountAndGetPrincipal(T account) throws AuthenticationException {
		this.verifyAccount(account);
		return this.createPrincipal(account);
	}

	public E validateTokenAndGetPrincipal(Jws<Claims> token) throws AuthenticationException {
		T account = findAccount(token);
		if(account == null) {
			throw new BadCredentialsException("Invalid credentials");
		}
		this.verifyAccount(account);
		return this.createPrincipal(account);
	}

	public void addAuthenticationCookie(E principal, HttpServletResponse response) throws Exception {
		Cookie cookie = this.createAuthCookie(principal, new Date(System.currentTimeMillis() + COOKIE_MAX_AGE_MS));
		response.addCookie(cookie);
	}

	public AbstractAuthenticationToken createAuthenticationToken(
			E principal,
			Object credentials
	) throws AuthenticationException {
		return new UsernamePasswordAuthenticationToken(
				principal,
				credentials,
				principal.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet())
		);
	}

	public HITToolPrincipal login(HttpServletRequest request, HttpServletResponse response, T account) throws Exception {
		E user = this.validateAccountAndGetPrincipal(account);
		Cookie authCookie = this.createAuthCookieWithDefaultDuration(user);
		response.setContentType("application/json");
		response.addCookie(authCookie);
		return user;
	}

	public E createPrincipal(T account) {
		E principal = account.getPrincipal();
		if(userPreRequirementManager != null) {
			userPreRequirementManager.setUserPreRequirements(account, principal);
		}
		return principal;
	}

	public Cookie createAuthCookieWithDefaultDuration(HITToolPrincipal principal) throws Exception {
		return this.createAuthCookie(principal, new Date(System.currentTimeMillis() + COOKIE_MAX_AGE_MS));
	}

	public Cookie createAuthCookie(HITToolPrincipal principal, Date expiresAt) throws Exception {
		Claims claims = Jwts.claims();
		claims.put("roles", principal.getRoles());

		int duration = (int) ((expiresAt.getTime() - System.currentTimeMillis()) / 1000);

		JwtBuilder jwtBuilder = Jwts.builder()
		                            .setSubject(principal.getId())
		                            .setExpiration(expiresAt)
		                            .claim("roles", principal.getRoles());

		String JWT = jwtBuilder.signWith(SignatureAlgorithm.RS256, keys.getPrivateKey()).compact();

		//-- Create Cookie
		Cookie authCookie = new Cookie(COOKIE_NAME, JWT);
		authCookie.setPath("/");
		authCookie.setMaxAge(duration);
		authCookie.setHttpOnly(true);

		return authCookie;
	}

	public void clearLoginCookie(HttpServletResponse response) {
		Cookie authCookie = new Cookie(COOKIE_NAME, "");
		authCookie.setPath("/");
		authCookie.setMaxAge(0);
		authCookie.setHttpOnly(true);
		response.addCookie(authCookie);
	}
}
