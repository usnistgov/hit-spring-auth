package gov.nist.hit.auth.core.exception;


import org.springframework.security.core.AuthenticationException;

public class OAuth2LoginAuthenticationException extends AuthenticationException {
	public OAuth2LoginAuthenticationException(Exception exception) {
		super(exception.getMessage(), exception);
	}
}
