package cz.vsb.and0153.campmanagement.server.security;

import org.springframework.stereotype.Component;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

@Component
public final class PasswordEncryptor {
	private static final SecureRandom RNG = new SecureRandom();
	private static final int SALT_LEN = 16;
	private static final int ITERATIONS = 120_000;
	private static final int KEY_LEN = 256;

	public String hash(String password) throws GeneralSecurityException {
		byte[] salt = new byte[SALT_LEN];
		RNG.nextBytes(salt);
		byte[] dk = derive(password.toCharArray(), salt, ITERATIONS);
		return "pbkdf2$" + ITERATIONS + "$" +
			Base64.getEncoder().encodeToString(salt) + "$" +
			Base64.getEncoder().encodeToString(dk);
	}

	public boolean verify(String password, String stored) throws GeneralSecurityException {
		String[] parts = stored.split("\\$");
		int iter = Integer.parseInt(parts[1]);
		byte[] salt = Base64.getDecoder().decode(parts[2]);
		byte[] expected = Base64.getDecoder().decode(parts[3]);
		byte[] dk = derive(password.toCharArray(), salt, iter);
		return MessageDigest.isEqual(dk, expected);
	}

	private static byte[] derive(char[] pwd, byte[] salt, int iter) throws GeneralSecurityException {
		PBEKeySpec spec = new PBEKeySpec(pwd, salt, iter, KEY_LEN);
		SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		return skf.generateSecret(spec).getEncoded();
	}
}
