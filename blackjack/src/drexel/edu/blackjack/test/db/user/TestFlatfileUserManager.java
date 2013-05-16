package drexel.edu.blackjack.test.db.user;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import drexel.edu.blackjack.db.user.FlatfileUserManager;
import drexel.edu.blackjack.db.user.UserManagerInterface;
import drexel.edu.blackjack.db.user.UserMetadata;

public class TestFlatfileUserManager {
	
	UserMetadata u1, u2;
	String[] u1a = new String[]{"name1","pass1","fullname1"};
	String[] u2a = new String[]{"fdsfadsf", "fdsf3adf", "fdsf3fdsf"};

	@Before
	public void setUp() throws Exception {
		u1 = new UserMetadata.Builder()
			.setBalance(20)
			.setUsername(u1a[0])
			.setPassword(u1a[1])
			.setFullname(u1a[2])
			.build();
		u2 = new UserMetadata.Builder()
		.setBalance(20)
		.setUsername(u2a[0])
		.setPassword(u2a[1])
		.setFullname(u2a[2])
		.build();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		UserManagerInterface f = FlatfileUserManager.getDefaultUserManager();
		f.add(u1);
		assertTrue(f.save());
		assertTrue(f.load());
		assertTrue(f.loginUser(u1.getUsername(), u1.getPassword()).getFullname().equals(u1a[2]));
		f.add(u2);
		assertTrue(f.save());
		assertTrue(f.load());
		assertTrue(f.loginUser(u2.getUsername(), u2.getPassword()).getFullname().equals(u2a[2]));
	}

}
