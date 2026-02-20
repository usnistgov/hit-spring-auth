package gov.nist.hit.auth.demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nist.hit.auth.core.model.HITToolPrincipal;
import gov.nist.hit.auth.core.service.CryptoKey;
import gov.nist.hit.auth.core.service.HITAuthenticationService;
import gov.nist.hit.auth.core.service.UserPreRequirementManager;
import gov.nist.hit.auth.demo.model.*;
import gov.nist.hit.auth.demo.repository.UserRepository;
import io.jsonwebtoken.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.stream.Collectors;


@Service
public class DemoAuthenticationService extends HITAuthenticationService<UserInfo, User> {

	private final UserRepository userRepository;

	public DemoAuthenticationService(
			UserPreRequirementManager<UserInfo, User> userPreRequirementManager,
			UserRepository userRepository,
			@Value("${hit.auth.cookie.name}") String cookieName,
			@Value("${hit.auth.cookie.duration-minutes}") long duration,
			CryptoKey cryptoKey) {
		super(userPreRequirementManager, cookieName, duration, cryptoKey);
		this.userRepository = userRepository;
	}

	@Override
	public void verifyAccountAndHandleLoginResponse(
			HttpServletRequest request,
			HttpServletResponse response,
			User account
	) throws AuthenticationException {
		try {
			HITToolPrincipal user = this.login(request, response, account);
			OpAck<HITToolPrincipal> userOpAck = new OpAck<>(AckStatus.SUCCESS, "Login Success", "LOGIN", user);
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.writeValue(response.getWriter(), userOpAck);
		} catch (AuthenticationException e) {
			throw e;
		} catch (Exception exception) {
			throw new AuthenticationServiceException(exception.getMessage(), exception);
		}
	}

	@Override
	public User findAccount(Jws<Claims> token) {
		return this.userRepository.findById(token.getBody().getSubject()).orElse(null);
	}

	@Override
	public void verifyAccount(User account) throws AuthenticationException {

	}

	@Override
	public void logout(
			HttpServletRequest request,
			HttpServletResponse response,
			HITToolPrincipal principal
	) throws ServletException, IOException {
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		this.clearLoginCookie(response);
		OpAck<HITToolPrincipal> userOpAck = new OpAck<>(AckStatus.SUCCESS, "Login Success", "LOGIN", null);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.writeValue(response.getWriter(), userOpAck);
	}

	@Override
	public UserInfo toUserPrincipal(User account) {
		UserInfo userInfo = new UserInfo();
		userInfo.setUsername(account.getUsername());
		userInfo.setId(account.getId());
		userInfo.setRoles(account.getRoles().stream().map(UserRole::getRole).collect(Collectors.toSet()));
		return userInfo;
	}
}
