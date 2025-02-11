package gov.nist.hit.auth.core.service.impl;

import gov.nist.hit.auth.core.service.CryptoKey;

import java.security.PrivateKey;
import java.security.PublicKey;

public class SimpleKeyPair implements CryptoKey {
	PrivateKey privateKey;
	PublicKey publicKey;

	public SimpleKeyPair(PrivateKey privateKey, PublicKey publicKey) {
		this.privateKey = privateKey;
		this.publicKey = publicKey;
	}


	@Override
	public PrivateKey getPrivateKey() throws Exception {
		return privateKey;
	}

	@Override
	public PublicKey getPublicKey() throws Exception {
		return publicKey;
	}
}
