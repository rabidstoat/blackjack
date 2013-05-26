/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - AccountCommandTest.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 * 
 * Purpose: Unit test of AccountCommand.java
 ******************************************************************************/
package drexel.edu.blackjack.test.server.commands;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import drexel.edu.blackjack.db.user.UserMetadata;
import drexel.edu.blackjack.server.BlackjackProtocol;
import drexel.edu.blackjack.server.ResponseCode;
import drexel.edu.blackjack.server.BlackjackProtocol.STATE;
import drexel.edu.blackjack.server.commands.AccountCommand;
import drexel.edu.blackjack.server.commands.CommandMetadata;
import drexel.edu.blackjack.server.game.User;

public class AccountCommandTest {

	// These are the account balances I'll use for my 'poor user' and 'rich user'
	// that I use to test the account balance command with
	private static final int POOR_BALANCE	= 20;
	private static final int RICH_BALANCE	= 20000000;
	
	// Class to test
	private AccountCommand command;
	
	// I'll test two different types of command metadata
	private CommandMetadata metadataWithParameters;
	private CommandMetadata metadataWithoutParameters;
	
	// I'll test with the blackjack protocol in a few different states
	private BlackjackProtocol nonauthorizedStateProtocol;
	private BlackjackProtocol authorizedStateProtocolPoorUser;
	private BlackjackProtocol authorizedStateProtocolRichUser;
	private BlackjackProtocol authorizedStateProtocolNoUser;
	
	@Before
	public void setUp() throws Exception {
		
		// Class to test
		command = new AccountCommand();
		
		// My different metadata objects. I don't have to worry about testing
		// that the right command word is set, as that's done somewhere else.
		// I just have to test about differences in parameters -- either having
		// none, or having some.
		metadataWithParameters = new CommandMetadata( command.getCommandWord() );
		metadataWithParameters.addParameter( "This will be ignored anyway" );
		
		metadataWithoutParameters = new CommandMetadata( command.getCommandWord() );
		
		// Now I want to test the protocol in different states. First, I'll need a
		// couple of user objects. Since the ACCOUNT command tests balances, I'll
		// create a poor user and a rich user. The poor user has little money, the
		// rich user has a lot of money
		UserMetadata.Builder userBuilder = new UserMetadata.Builder();
		
		userBuilder.setBalance( POOR_BALANCE );
		userBuilder.setFullname( "Poor User" );
		userBuilder.setPassword( "password" );
		userBuilder.setUsername( "pooruser" );
		UserMetadata poorUserMetadata = userBuilder.build();
		User poorUser = new User( poorUserMetadata );

		userBuilder.setBalance( RICH_BALANCE );
		userBuilder.setFullname( "Rich User" );
		userBuilder.setPassword( "password" );
		userBuilder.setUsername( "richuser" );
		UserMetadata richUserMetadata = userBuilder.build();
		User richUser = new User( richUserMetadata );

		// Now that I have my users, I can create my protocol states that I want to test
		nonauthorizedStateProtocol = new BlackjackProtocol( null );
		nonauthorizedStateProtocol.setUser( poorUser );
		nonauthorizedStateProtocol.setState( STATE.WAITING_FOR_PASSWORD );

		authorizedStateProtocolPoorUser = new BlackjackProtocol( null );
		authorizedStateProtocolPoorUser.setUser( poorUser );
		authorizedStateProtocolPoorUser.setState( STATE.IN_SESSION_AS_OBSERVER );

		authorizedStateProtocolRichUser = new BlackjackProtocol( null );
		authorizedStateProtocolRichUser.setUser( richUser );
		authorizedStateProtocolRichUser.setState( STATE.NOT_IN_SESSION );

		// IF I forget to set the user, this will be an invalid protocol
		authorizedStateProtocolNoUser = new BlackjackProtocol( null );
		authorizedStateProtocolNoUser.setState( STATE.IN_SESSION_AS_OBSERVER );
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetCommandWord() {
		assertEquals( command.getCommandWord(), "ACCOUNT" );
		
		// I'll just test one of the words that the command word SHOULDN'T be
		assertNotEquals( command.getCommandWord(), "BET" );
	}

	@Test
	public void testGetValidStates() {
		Set<STATE> states = command.getValidStates();
		
		// I'll just test a few of the states that it should be valid in
		assertTrue( states.contains(STATE.IN_SESSION_AND_YOUR_TURN ));
		assertTrue( states.contains(STATE.NOT_IN_SESSION ));
		
		// And both states that it should NOT be valid in
		assertFalse( states.contains(STATE.WAITING_FOR_PASSWORD) );
		assertFalse( states.contains(STATE.WAITING_FOR_USERNAME) );
	}

	@Test
	public void testGetValidParameterNames() {
		// This is easy, there shoudn't be any
		assertNull( command.getRequiredParameterNames() );
	}
	
	@Test
	public void testProcessCommand() {
		
		ResponseCode response;	// Store responses in this

		// Test what happens if one of the parameters is null -- I should get an INTERNAL ERROR
		response = ResponseCode.getCodeFromString(
				command.processCommand( null, metadataWithParameters));
		assertTrue( response.getCode() != null && 
				response.getCode().equals(ResponseCode.CODE.INTERNAL_ERROR.getCode() ) );
		response = ResponseCode.getCodeFromString(
				command.processCommand( authorizedStateProtocolPoorUser, null));
		assertTrue( response.getCode() != null && 
				response.getCode().equals(ResponseCode.CODE.INTERNAL_ERROR.getCode() ) );
		
		// Or if there's a protocol with a problem in it, like a null user
		response = ResponseCode.getCodeFromString(
				command.processCommand( authorizedStateProtocolNoUser, metadataWithoutParameters) );
		assertTrue( response.getCode() != null && 
				response.getCode().equals(ResponseCode.CODE.INTERNAL_ERROR.getCode() ) );
		
		// Now test response when it's not in an authorized state
		response = ResponseCode.getCodeFromString(
				command.processCommand( nonauthorizedStateProtocol, metadataWithoutParameters ) );
		assertTrue( response.getCode() != null && 
				response.getCode().equals(ResponseCode.CODE.NEED_TO_BE_AUTHENTICATED.getCode() ) );
		
		// Now test for the rich user
		response = ResponseCode.getCodeFromString(
				command.processCommand( authorizedStateProtocolRichUser, metadataWithoutParameters ) );
		assertTrue( response.getCode() != null && 
				response.getCode().equals(ResponseCode.CODE.ACCOUNT_BALANCE.getCode() ) );
		assertEquals( response.getFirstParameterAsInteger(), Integer.valueOf(RICH_BALANCE) );
		assertNotEquals( response.getFirstParameterAsInteger(), Integer.valueOf(POOR_BALANCE) );

		// Now test for the poor user
		response = ResponseCode.getCodeFromString(
				command.processCommand( authorizedStateProtocolPoorUser, metadataWithoutParameters ) );
		assertTrue( response.getCode() != null && 
				response.getCode().equals(ResponseCode.CODE.ACCOUNT_BALANCE.getCode() ) );
		assertEquals( response.getFirstParameterAsInteger(), Integer.valueOf(POOR_BALANCE) );
		assertNotEquals( response.getFirstParameterAsInteger(), Integer.valueOf(RICH_BALANCE) );

		// No difference even if we include parameters with the command metadata,
		// as they just get ignored
		response = ResponseCode.getCodeFromString(
				command.processCommand( authorizedStateProtocolPoorUser, metadataWithParameters ) );
		assertTrue( response.getCode() != null && 
				response.getCode().equals(ResponseCode.CODE.ACCOUNT_BALANCE.getCode() ) );
		assertEquals( response.getFirstParameterAsInteger(), Integer.valueOf(POOR_BALANCE) );
		assertNotEquals( response.getFirstParameterAsInteger(), Integer.valueOf(RICH_BALANCE) );
	}
}
