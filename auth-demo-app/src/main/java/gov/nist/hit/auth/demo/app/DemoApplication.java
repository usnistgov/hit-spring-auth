package gov.nist.hit.auth.demo.app;

import gov.nist.hit.auth.demo.model.UserRole;
import gov.nist.hit.auth.demo.repository.UserRoleRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages="gov.nist.hit")
public class DemoApplication {

	@Autowired
	private UserRoleRepository userRoleRepository;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@PostConstruct
	public void init() {
		if(userRoleRepository.count() == 0) {
			UserRole userRole = new UserRole();
			userRole.setRole("REGULAR");
			userRoleRepository.save(userRole);
		}
	}

}
