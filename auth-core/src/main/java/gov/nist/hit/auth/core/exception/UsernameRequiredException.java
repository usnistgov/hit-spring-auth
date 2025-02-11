package gov.nist.hit.auth.core.exception;

import org.springframework.security.core.AuthenticationException;

public class UsernameRequiredException extends AuthenticationException {
	public UsernameRequiredException(String msg) {
		super(msg);
	}
}
