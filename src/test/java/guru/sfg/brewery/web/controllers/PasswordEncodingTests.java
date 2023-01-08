package guru.sfg.brewery.web.controllers;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.util.DigestUtils;

@SuppressWarnings("deprecation")
public class PasswordEncodingTests {
	static final String PASSWORD = "password";
	
	@Test
	void testBcrypt() {
		PasswordEncoder bcrypt = new BCryptPasswordEncoder();
		System.out.println(bcrypt.encode("guru"));
		System.out.println(bcrypt.encode("password"));
		System.out.println(bcrypt.encode("tiger"));
	}

	@Test
	void testBcrypt15() {
		PasswordEncoder bcrypt = new BCryptPasswordEncoder(15);
		System.out.println(bcrypt.encode("guru"));
		System.out.println(bcrypt.encode("password"));
		System.out.println(bcrypt.encode("tiger"));
	}

	@Test
	void testSha256() {
		PasswordEncoder sha256 = new StandardPasswordEncoder();
		System.out.println(sha256.encode("guru"));
		System.out.println(sha256.encode("password"));
		System.out.println(sha256.encode("tiger"));
	}
	
	@Test
	void testLdap() {
		PasswordEncoder ldap = new LdapShaPasswordEncoder();
		System.out.println(ldap.encode("guru"));
		System.out.println(ldap.encode("password"));
		System.out.println(ldap.encode("tiger"));
		String encodedPassword = ldap.encode(PASSWORD);
		
		assertTrue(ldap.matches(PASSWORD, encodedPassword));
	}
	
	@Test
	void testNoOp() throws Exception {
		PasswordEncoder noOp = NoOpPasswordEncoder.getInstance();
		System.out.println(noOp.encode(PASSWORD));
	}
	
	@Test
	void hashingExample() {
		System.out.println(DigestUtils.md5DigestAsHex(PASSWORD.getBytes()));
		String salted = PASSWORD + "ThisIsMySALTVALUE";
		System.out.println(DigestUtils.md5DigestAsHex(salted.getBytes()));
		
	}

}
