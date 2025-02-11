package gov.nist.hit.auth.demo.repository;

import gov.nist.hit.auth.demo.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
	User findByUsernameIgnoreCase(String username);
	User findByEmailIgnoreCase(String email);
	@Query("{ 'identities': { $elemMatch: { 'issuer': ?0, 'uid': ?1 } } }")
	User findByIdentity(String issuer, String uid);
}
