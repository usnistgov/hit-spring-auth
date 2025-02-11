package gov.nist.hit.auth.core.exception;

import gov.nist.hit.auth.core.model.HITToolPrincipal;
import org.springframework.security.core.AuthenticationException;

import java.util.Set;

public class UserPreRequirementNotMetException extends AuthenticationException {
	private Set<String> preRequirements;
	private HITToolPrincipal principal;

	public UserPreRequirementNotMetException(Set<String> preRequirements, HITToolPrincipal principal) {
		super("User pre-requirement not met");
		this.preRequirements = preRequirements;
		this.principal = principal;
	}

	public Set<String> getPreRequirements() {
		return preRequirements;
	}

	public HITToolPrincipal getPrincipal() {
		return principal;
	}
}
