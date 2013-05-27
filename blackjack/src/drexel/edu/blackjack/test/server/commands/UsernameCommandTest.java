/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - UsernameCommandTest.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 * 
 * Purpose: Unit test of UsernameCommand.java
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

import drexel.edu.blackjack.server.BlackjackProtocol;
import drexel.edu.blackjack.server.ResponseCode;
import drexel.edu.blackjack.server.BlackjackProtocol.STATE;
import drexel.edu.blackjack.server.commands.CommandMetadata;
import drexel.edu.blackjack.server.commands.UsernameCommand;

public class UsernameCommandTest {
	
	//Class to test
	private UsernameCommand command;
	
	// I'll test three different types of command metadata
		private CommandMetadata metadataWithParameters;
		private CommandMetadata metadataWithoutParameters;
		private CommandMetadata metadataWithTwoParameters;
		
		//Test three different types of protocol
		//Test with null-state protocol
		private BlackjackProtocol nullStateProtocol;
		
		// I'll test with the blackjack protocol in authorized state
		private BlackjackProtocol authorizedStateProtocol;
		
		// I'll test with the blackjack protocol in unauthorized state
		private BlackjackProtocol nonauthorizedStateProtocol;
		
	
	

	@Before
	public void setUp() throws Exception {
		//Class to test
		command = new UsernameCommand();
		
		// My different metadata objects. I don't have to worry about testing
		// that the right command word is set, as that's done somewhere else.
		// I just have to test about differences in parameters -- either having
		// none, or having some.
		
		//Test some parameter
		metadataWithParameters = new CommandMetadata( command.getCommandWord() );
		metadataWithParameters.addParameter( "Some parameter" );
		
		//Test with more than one parameter
		metadataWithTwoParameters = new CommandMetadata( command.getCommandWord() );
		metadataWithTwoParameters.addParameter( "only tests presence 1" );
		metadataWithTwoParameters.addParameter( "only tests presence 2" );
		
		
		//Test with No parameter
		metadataWithoutParameters = new CommandMetadata( command.getCommandWord() );
		
		// Now I want to test the protocol in different states.
		//With null parameter
		nullStateProtocol = new BlackjackProtocol(null); 
		
		//With expected parameter		
		authorizedStateProtocol = new BlackjackProtocol( null );
		authorizedStateProtocol.setState( STATE.WAITING_FOR_USERNAME );
				
		//With unexpected parameter
		nonauthorizedStateProtocol = new BlackjackProtocol( null );
		nonauthorizedStateProtocol.setState( STATE.IN_SESSION_DEALER_BLACKJACK );
		

				
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testGetCommandWord() {
		assertEquals( command.getCommandWord(), "USERNAME" );
		
		// I'll just test one of the words that the command word SHOULDN'T be
		assertNotEquals( command.getCommandWord(), "HIT" );
	}
	
	@Test
	public void testGetValidStates() {
		
		Set<STATE> states = command.getValidStates();
		
		// Test the ONLY state that it should be valid in
		assertTrue( states.contains(STATE.WAITING_FOR_USERNAME ));
		
		
		// And states that it should NOT be valid in
		assertFalse( states.contains(STATE.WAITING_FOR_PASSWORD) );
		assertFalse( states.contains(STATE.NOT_IN_SESSION) );
		assertFalse( states.contains(STATE.IN_SESSION_AWAITING_BETS) );
		assertFalse( states.contains(STATE.IN_SESSION_AND_YOUR_TURN) );
		assertFalse( states.contains(STATE.IN_SESSION_AFTER_YOUR_TURN) );
		assertFalse( states.contains(STATE.IN_SESSION_AS_OBSERVER) );
	}
	
	
	
	@Test
	public void testGetRequiredParameterNames() {
		List<String> user = new ArrayList<String>();
		 user.add("username");
		 //String u = user.get(0);
		
		// Assert parameter is equal to username
		assertEquals( command.getRequiredParameterNames(),  user);  
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
			command.processCommand(authorizedStateProtocol , null));
	assertTrue( response.getCode() != null && 
			response.getCode().equals(ResponseCode.CODE.INTERNAL_ERROR.getCode() ) );
	
	// Now test response when it's not in an authorized state
			response = ResponseCode.getCodeFromString(
					command.processCommand( nonauthorizedStateProtocol, metadataWithoutParameters ) );
			assertTrue( response.getCode() != null && 
					response.getCode().equals(ResponseCode.CODE.NOT_EXPECTING_USERNAME.getCode() ) );
			
	//Test when username parameter is not exactly one string
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
			
	//Finally, test proper response generation
			response = ResponseCode.getCodeFromString(
					command.processCommand( authorizedStateProtocol, metadataWithParameters ) );
			assertTrue( response.getCode() != null && 
					response.getCode().equals(ResponseCode.CODE.WAITING_FOR_PASSWORD.getCode() ) ); 
			
			
			
	}
	}
	


