package gov.nist.hit.auth.demo.repository;

import gov.nist.hit.auth.demo.model.UserRole;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends MongoRepository<UserRole, String> {
	UserRole findByRole(String role);
}
