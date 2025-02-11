package gov.nist.hit.auth.core.service;

import gov.nist.hit.auth.core.exception.UserPreRequirementNotMetException;
import gov.nist.hit.auth.core.model.HITToolPrincipal;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class HITAuthenticationManager {

	private static class MeetRequirementManager<E extends HITToolPrincipal, T> implements AuthorizationManager<T> {

		private final AuthorizationManager<T> preCondition;
		private final Set<String> preRequirements;

		private MeetRequirementManager(AuthorizationManager<T> preCondition, Set<String> preRequirements) {
			this.preCondition = preCondition;
			this.preRequirements = preRequirements;
		}

		private MeetRequirementManager(Set<String> preRequirements) {
			this.preCondition = null;
			this.preRequirements = preRequirements;
		}


		@Override
		public AuthorizationDecision check(Supplier<Authentication> authentication, T object) throws AuthenticationException  {
			if(preCondition != null) {
				AuthorizationDecision decision = preCondition.check(authentication, object);
				if(decision != null && !decision.isGranted()) {
					return decision;
				}
			}
			Authentication auth = authentication.get();
			HITToolPrincipal principal = (E) auth.getPrincipal();
			Set<String> notMet = principal
					.getPreRequirements()
					.stream()
					.filter(preRequirements::contains)
					.collect(Collectors.toSet());
			if(!notMet.isEmpty()) {
				throw new UserPreRequirementNotMetException(preRequirements, principal);
			}
			return new AuthorizationDecision(true);
		}
	}

	public static <T> AuthorizationManager<T> meetsPreRequirements(String ...preRequirement) {
		return new MeetRequirementManager<>(new HashSet<>(Arrays.asList(preRequirement)));
	}

	public static <T> AuthorizationManager<T> meetsPreRequirements(AuthorizationManager<T> preCondition, String ...preRequirement) {
		return new MeetRequirementManager<>(preCondition, new HashSet<>(Arrays.asList(preRequirement)));
	}

}
