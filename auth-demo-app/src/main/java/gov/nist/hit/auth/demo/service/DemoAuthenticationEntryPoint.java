package gov.nist.hit.auth.demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nist.hit.auth.core.service.HITAuthenticationEntryPoint;
import gov.nist.hit.auth.demo.model.AckStatus;
import gov.nist.hit.auth.demo.model.OpAck;
import gov.nist.hit.auth.demo.model.User;
import gov.nist.hit.auth.demo.model.UserInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class DemoAuthenticationEntryPoint extends HITAuthenticationEntryPoint<UserInfo, User> {

	public DemoAuthenticationEntryPoint(DemoUserPreRequirementService demoUserPreRequirementService) {
		super(demoUserPreRequirementService);
	}

	@Override
	public void handleException(
			HttpServletRequest request,
			HttpServletResponse response,
			AuthenticationException authException
	) throws IOException {
		authException.printStackTrace();
		OpAck<String> userOpAck = new OpAck<>(AckStatus.FAILED, authException.getMessage(), "", "");
		ObjectMapper objectMapper = new ObjectMapper();
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json");
		objectMapper.writeValue(response.getWriter(), userOpAck);
	}
}
