/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - UserHelper.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 * 
 * Purpose: This implements a simple menu-driven UI that an administrator can
 * use for creating system users by defining their metadata.
 ******************************************************************************/
package drexel.edu.blackjack.db.user;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import drexel.edu.blackjack.db.game.GameHelper;
import drexel.edu.blackjack.db.user.UserManagerInterface.UserNotFoundException;

public class UserHelper {
	
	private void remove() {
		System.out.println("Username: ");
		BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
		UserManagerInterface um = FlatfileUserManager.getDefaultUserManager();
		try {
			String s = r.readLine();
			if( um.remove(s.trim()) ) {
				System.out.println( "User removed." );
			} else {
				System.out.println( "Cannot remove user. Please try again" );
			}
		} catch (IOException e) {
			System.out.println("Input ERROR");
		} catch (UserNotFoundException e) {
			System.out.println( "User not found" );
		}
	}
	
	private void list() {
		UserManagerInterface um = FlatfileUserManager.getDefaultUserManager();
		for (UserMetadata g: um.getUsers()) {
			System.out.println(g.toString());
		}
	}
	
	private void add() {
		UserManagerInterface um = FlatfileUserManager.getDefaultUserManager();
		UserMetadata.Builder b = new UserMetadata.Builder();
		System.out.println("Add one more user");
		BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
		String s;
		try {
			System.out.println("username:");
			s = r.readLine(); b.setUsername(s);
			System.out.println("password:");
			s = r.readLine(); b.setPassword(s);
			System.out.println("full name:");
			s = r.readLine(); b.setFullname(s);
			System.out.println("balance:");
			s = r.readLine(); b.setBalance(Integer.parseInt(s));
			if (um.add(b.build())) {
				System.out.println("Added successfully");
			}
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
		
	}
	
	private void changePassword() {
		System.out.println("Username: ");
		BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
		UserManagerInterface um = FlatfileUserManager.getDefaultUserManager();
		try {
			String username = r.readLine();
			System.out.println("New password: ");
			String newPassword = r.readLine(); 
			um.changePassword(username, newPassword);
		} catch (IOException e) {
			System.out.println("Input ERROR");
		} catch (UserNotFoundException e) {
			System.out.println( "User not found" );
		}
	}
	
	public static void main(String[] args) {
		UserHelper uh = new UserHelper();
		BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
		
		while (true) {
			System.out.println("1. List all users");
			System.out.println("2. Add user");
			System.out.println("3. Remove a user");
			System.out.println("4. Change password a user");
			System.out.println("0. Exit");
			try {
				int i = Integer.parseInt(r.readLine());
				switch (i) {
				case 1:
					uh.list(); break;
				case 2:
					uh.add(); break;
				case 3:
					uh.remove(); break;
				case 4:
					uh.changePassword(); break;
				default:
					System.exit(1);	
				}
			} catch (Exception e) {
				
			}
		}
	}
}
