package gov.nist.hit.auth.core.service;

import java.security.PrivateKey;
import java.security.PublicKey;

public interface CryptoKey {
	PrivateKey getPrivateKey() throws Exception;
	PublicKey getPublicKey() throws Exception;
}
