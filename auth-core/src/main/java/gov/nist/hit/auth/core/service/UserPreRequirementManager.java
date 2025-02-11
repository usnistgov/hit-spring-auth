package gov.nist.hit.auth.core.service;

import gov.nist.hit.auth.core.model.HITToolPrincipal;
import gov.nist.hit.auth.core.model.HITToolUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Set;

public interface UserPreRequirementManager<E extends HITToolPrincipal, T extends HITToolUser<E>> {
	void setUserPreRequirements(T user, E principal);
	void handlePreRequirementNotMet(E principal, Set<String> preRequirements, HttpServletRequest request, HttpServletResponse response) throws IOException;
	void handlePreRequirementOnLogin(E principal, Set<String> preRequirements, HttpServletRequest request, HttpServletResponse response) throws IOException;

}
