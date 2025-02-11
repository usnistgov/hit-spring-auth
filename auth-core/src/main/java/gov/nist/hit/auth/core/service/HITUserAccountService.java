package gov.nist.hit.auth.core.service;

import gov.nist.hit.auth.core.model.HITToolPrincipal;
import gov.nist.hit.auth.core.model.HITToolUser;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public interface HITUserAccountService<E extends HITToolPrincipal, T extends HITToolUser<E>> extends UserDetailsService {
	T findAccount(OidcUser user);
	T createAccountForUser(OidcUser user) throws AuthenticationException;
}
