package gov.nist.hit.auth.core.exception;


import org.springframework.security.core.AuthenticationException;

public class InvalidPrincipal extends AuthenticationException {
	public InvalidPrincipal(String msg) {
		super(msg);
	}
}
