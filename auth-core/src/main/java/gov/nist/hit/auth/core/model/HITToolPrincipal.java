package gov.nist.hit.auth.core.model;

import java.util.HashSet;
import java.util.Set;

public class HITToolPrincipal {
	String id;
	String username;
	Set<String> roles;
	Set<String> preRequirements;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Set<String> getRoles() {
		return roles;
	}

	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Set<String> getPreRequirements() {
		if(preRequirements == null) {
			preRequirements = new HashSet<>();
		}
		return preRequirements;
	}

	public void setPreRequirements(Set<String> preRequirements) {
		this.preRequirements = preRequirements;
	}
}
