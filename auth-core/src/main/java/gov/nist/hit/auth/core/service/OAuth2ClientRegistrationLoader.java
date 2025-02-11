package gov.nist.hit.auth.core.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nist.hit.auth.core.model.OAuth2ClientRegistration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

//@Service
public class OAuth2ClientRegistrationLoader implements ClientRegistrationRepository {

	private final HashMap<String, ClientRegistration> clients;

	public OAuth2ClientRegistrationLoader(@Value("${hit.auth.core.clients.json}") String jsonConfigurationFile) throws IOException {
		this.clients = new HashMap<>();
		ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().build();
		List<OAuth2ClientRegistration> registrations = mapper.readValue(
				new File(jsonConfigurationFile),
				new TypeReference<List<OAuth2ClientRegistration>>() {}
		);
		for (OAuth2ClientRegistration registration : registrations) {
			clients.put(
					registration.getRegistrationId(),
					ClientRegistration.withRegistrationId(registration.getRegistrationId())
					                  .clientId(registration.getClientId())
							          .clientSecret(registration.getClientSecret())
							          .authorizationGrantType(new AuthorizationGrantType(registration.getAuthorizationGrantType()))
							          .scope(registration.getScopes())
							          .issuerUri(registration.getProviderURL())
							          .build()
			);
		}
	}

	@Override
	public ClientRegistration findByRegistrationId(String registrationId) {
		return clients.get(registrationId);
	}
}
