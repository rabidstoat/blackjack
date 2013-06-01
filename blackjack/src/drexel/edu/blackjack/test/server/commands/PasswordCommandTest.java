/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - PasswordCommandTest.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 * 
 * Purpose: Unit test of PasswordCommand.java
 ******************************************************************************/
package drexel.edu.blackjack.test.server.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import drexel.edu.blackjack.db.user.AlreadyLoggedInException;
import drexel.edu.blackjack.db.user.FlatfileUserManager;
import drexel.edu.blackjack.db.user.UserManagerInterface;
import drexel.edu.blackjack.db.user.UserMetadata;
import drexel.edu.blackjack.server.BlackjackProtocol;
import drexel.edu.blackjack.server.BlackjackProtocol.STATE;
import drexel.edu.blackjack.server.ResponseCode;
import drexel.edu.blackjack.server.commands.CommandMetadata;
import drexel.edu.blackjack.server.commands.PasswordCommand;
import drexel.edu.blackjack.server.game.User;

public class PasswordCommandTest {
	
	//Class to test
		private PasswordCommand command;
		
		// I'll test three different types of command metadata
		private CommandMetadata metadataWithParameter;
		private CommandMetadata metadataWithoutParameters;
		private CommandMetadata metadataWithTwoParameters;
				
		//Test three different types of protocol
		//Test with null-state protocol
		private BlackjackProtocol nullStateProtocol;
				
		// I'll test with the blackjack protocol in authorized state
		private BlackjackProtocol authorizedStateProtocol;
				
		// I'll test with the blackjack protocol in unauthorized state
		private BlackjackProtocol nonauthorizedStateProtocol;
		
		//Test with blackjack protocol in not-in-session state
		private BlackjackProtocol notInSessionStateProtocol;
		
		private BlackjackProtocol tooManyLogins;
		
		
		
		
		@Before
	public void setUp() throws Exception {
				//Class to test
				command = new PasswordCommand();
				
				// My different metadata objects. I don't have to worry about testing
				// that the right command word is set, as that's done somewhere else.
				// I just have to test about differences in parameters -- either having
				// none, or having some.
				
				//Test some parameter
				metadataWithParameter = new CommandMetadata( command.getCommandWord() );
				metadataWithParameter.addParameter( "password" );
				
				//Test with more than one parameter
				metadataWithTwoParameters = new CommandMetadata( command.getCommandWord() );
				metadataWithTwoParameters.addParameter( "only tests param1 presence" );
				metadataWithTwoParameters.addParameter( "only tests param2 presence" );
				
				//Test with No parameter
				metadataWithoutParameters = new CommandMetadata( command.getCommandWord() );
				
				// Now I want to test the protocol in different states.
				//With null parameter
				nullStateProtocol = new BlackjackProtocol(null); 
				
				
				
				//With expected parameter		
				authorizedStateProtocol = new BlackjackProtocol( null );
				authorizedStateProtocol.setState( STATE.WAITING_FOR_PASSWORD );
						
				//With unexpected parameter
				nonauthorizedStateProtocol = new BlackjackProtocol( null );
				nonauthorizedStateProtocol.setState( STATE.IN_SESSION_AFTER_YOUR_TURN );
				
				//With not-in-session parameter
				notInSessionStateProtocol = new BlackjackProtocol( null );
				notInSessionStateProtocol.setState(STATE.NOT_IN_SESSION);
				
				//Test login threshold
				tooManyLogins = new BlackjackProtocol( null );
				tooManyLogins.setState(STATE.WAITING_FOR_PASSWORD);
			
					
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetCommandWord() {
		assertEquals( command.getCommandWord(), "PASSWORD" );
		
		// Test one of the words that the command word SHOULDN'T be
		assertNotEquals( command.getCommandWord(), "USERNAME" );
	}
	
	@Test
	public void testGetValidStates() {
		
		Set<STATE> states = command.getValidStates();
		
		// Test the ONLY state that it should be valid in
		assertTrue( states.contains(STATE.WAITING_FOR_PASSWORD ));
		
		
		// And states that it should NOT be valid in
		assertFalse( states.contains(STATE.WAITING_FOR_USERNAME) );
		assertFalse( states.contains(STATE.NOT_IN_SESSION) );
		assertFalse( states.contains(STATE.IN_SESSION_AWAITING_BETS) );
		assertFalse( states.contains(STATE.IN_SESSION_AND_YOUR_TURN) );
		assertFalse( states.contains(STATE.IN_SESSION_AFTER_YOUR_TURN) );
		assertFalse( states.contains(STATE.IN_SESSION_AS_OBSERVER) );
	}
		
	@Test
	public void testGetRequiredParameterNames() {
		List<String> user = new ArrayList<String>();
		 user.add("password");
		 //String u = user.get(0);
		
		// Assert parameter is equal to username
		assertEquals( command.getRequiredParameterNames(),  user);  
	}
	
	@Test
	public void testProcessCommand() throws AlreadyLoggedInException {
		
		ResponseCode response;	// Store responses in this
	
	// Test what happens if one of the parameters is null -- I should get an INTERNAL ERROR
	response = ResponseCode.getCodeFromString(
			command.processCommand( null, metadataWithParameter));
	assertTrue( response.getCode() != null && 
			response.getCode().equals(ResponseCode.CODE.INTERNAL_ERROR.getCode() ) );
	
	response = ResponseCode.getCodeFromString(
			command.processCommand(authorizedStateProtocol , null));
	assertTrue( response.getCode() != null && 
			response.getCode().equals(ResponseCode.CODE.INTERNAL_ERROR.getCode() ) );
	
	// Now test response when it's not in an authorized state
			response = ResponseCode.getCodeFromString(
					command.processCommand( nonauthorizedStateProtocol, metadataWithoutParameters ) );
			assertTrue( response.getCode() != null && 
					response.getCode().equals(ResponseCode.CODE.NOT_EXPECTING_PASSWORD.getCode() ) );
			
	//Test when password parameter is not exactly one string
	//First when it is null
			response = ResponseCode.getCodeFromString(
					command.processCommand( authorizedStateProtocol, metadataWithoutParameters ) );
			
			assertTrue( response.getCode() != null && 
					response.getCode().equals(ResponseCode.CODE.SYNTAX_ERROR.getCode() ) );	
		
	//Then if it has more than one parameter
			response = ResponseCode.getCodeFromString(
					command.processCommand( authorizedStateProtocol, metadataWithTwoParameters ) );
			
			assertTrue( response.getCode() != null && 
					response.getCode().equals(ResponseCode.CODE.SYNTAX_ERROR.getCode() ) ); 
			
			
			//Test the login threshold which is max 3 times.
			UserManagerInterface userManager;
			
			//A user
			User userOne;
			
			//User metadata
			UserMetadata user1;
			String[] u1 = new String[]{"name1","pass1","fullname1"};
			
			user1 = new UserMetadata.Builder()
				.setBalance(20)
				.setUsername(u1[0])
				.setPassword(u1[1])
				.setFullname(u1[2])
				.build();
			
			
			userManager = FlatfileUserManager.getDefaultUserManager();
			
			//Set user's metadata
			userOne = new User(user1);
			
			//Test for too many incorrect logins (max 3 allowed)
				tooManyLogins.incrementIncorrectLogins();
				tooManyLogins.incrementIncorrectLogins();
				tooManyLogins.incrementIncorrectLogins();
				tooManyLogins.incrementIncorrectLogins();			
			
			response = ResponseCode.getCodeFromString(command.processCommand( tooManyLogins, metadataWithParameter ) );
			
			assertTrue( response.getCode() != null && 
					response.getCode().equals(ResponseCode.CODE.LOGIN_ATTEMPTS_EXCEEDED.getCode() ) );
			
			//Test invalid login credentials: User incorrectly entered fullname for username; username for password.
			user1 = userManager.loginUser(user1.getFullname(), user1.getUsername() );	
			
						response = ResponseCode.getCodeFromString(
								command.processCommand( authorizedStateProtocol, metadataWithParameter ) );
						
					assertTrue( response.getCode() != null && 
							response.getCode().equals(ResponseCode.CODE.INVALID_LOGIN_CREDENTIALS.getCode() ) ); 
		
			
	} 
	
}
