/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - TestFlatfileUserManager.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 ******************************************************************************/
package drexel.edu.blackjack.test.db.user;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import drexel.edu.blackjack.db.user.AlreadyLoggedInException;
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
		UserManagerInterface f = FlatfileUserManager.getDefaultTestUserManager();
		f.add(u1);
		assertTrue(f.save());
		assertTrue(f.load());
		try {
			assertTrue(f.loginUser(u1.getUsername(), u1.getPassword()).getFullname().equals(u1a[2]));
		} catch (AlreadyLoggedInException e) {
			fail( "Received an unexpected exception when logging in a user." );
		}
		try {
			f.loginUser(u1.getUsername(), u1.getPassword()).getFullname().equals(u1a[2]);
			fail( "Expected to receive an AlreadyLoggedInException for a second login, and did not." );
		} catch (AlreadyLoggedInException e) {
			// This is expected
		}
		assertTrue( f.logoutUser(u1.getUsername() ) );
		try {
			assertTrue(f.loginUser(u1.getUsername(), u1.getPassword()).getFullname().equals(u1a[2]));
		} catch (AlreadyLoggedInException e) {
			fail( "Received an unexpected exception when logging in a user after logging them out." );
		}
		f.add(u2);
		assertTrue(f.save());
		assertTrue(f.load());
		try {
			assertTrue(f.loginUser(u2.getUsername(), u2.getPassword()).getFullname().equals(u2a[2]));
		} catch (AlreadyLoggedInException e) {
			fail( "Received an unexpected exception when logging in a user." );
		}
		try {
			f.loginUser(u2.getUsername(), u2.getPassword()).getFullname().equals(u2a[2]);
			fail( "Expected to receive an AlreadyLoggedInException for a second login, and did not." );
		} catch (AlreadyLoggedInException e) {
			// This was expected
		}
		assertTrue( f.logoutUser(u2.getUsername() ) );
		try {
			assertTrue(f.loginUser(u2.getUsername(), u2.getPassword()).getFullname().equals(u2a[2]));
		} catch (AlreadyLoggedInException e) {
			fail( "Received an unexpected exception when logging in a user after logging them out." );
		}
	}

}
