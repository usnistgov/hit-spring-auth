package gov.nist.hit.auth.core.model;

import org.springframework.security.core.userdetails.UserDetails;

public interface HITToolUser<T extends HITToolPrincipal> extends UserDetails {
	T getPrincipal();
}
