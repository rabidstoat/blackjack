package drexel.edu.blackjack.db.user;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class UserHelper {
	public static int main(String[] args) {
		UserManagerInterface um = FlatfileUserManager.getDefaultUserManager();
		um.load();
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
			um.add(b.build());
			um.save();
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
		
		return 0;
	}
}
