package gov.nist.hit.auth.demo.app;

import gov.nist.hit.auth.demo.model.*;
import gov.nist.hit.auth.demo.service.DemoAuthenticationService;
import gov.nist.hit.auth.demo.service.DemoUserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

	@Autowired
	DemoUserAccountService demoUserAccountService;
//	@Autowired
//	DemoAuthenticationService authenticationService;

	@GetMapping("/api/me")
	public UserInfo me(@AuthenticationPrincipal UserInfo principal) {
		return principal;
	}

	@GetMapping("/api/data")
	public Data secret() {
		return new Data("Super Secret!");
	}

	@PostMapping("/api/setup-profile")
	public OpAck<UserInfo> profileSetup(@RequestBody ProfileSetup profile, @AuthenticationPrincipal UserInfo principal) throws Exception {
		User user = demoUserAccountService.setupUserProfile(profile, principal.getId());
		return new OpAck<>(AckStatus.SUCCESS, "User profile updated successfully", "user-profile-setup", user.getPrincipal());
	}

	@PostMapping("/api/register")
	public OpAck<Void> profileSetup(@RequestBody RegistrationRequest registration) throws Exception {
		demoUserAccountService.register(registration);
		return new OpAck<>(AckStatus.SUCCESS, "Your account has been created. Please login to start using the tool.", "user-registration", null);
	}
}
