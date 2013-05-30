/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - CapabilitiesCommandTest.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 * 
 * Purpose: Unit test of CapabilitiesCommand.java
 ******************************************************************************/
/** 
 * @author Constantine 
 * 
 * */

package drexel.edu.blackjack.test.server.commands;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import drexel.edu.blackjack.server.BlackjackProtocol;
import drexel.edu.blackjack.server.ResponseCode;
import drexel.edu.blackjack.server.BlackjackProtocol.STATE;
import drexel.edu.blackjack.server.commands.CapabilitiesCommand;
import drexel.edu.blackjack.server.commands.CommandMetadata;

public class CapabilitiesCommandTest {
	
	//Class to test
	private CapabilitiesCommand command;
	
	// I'll test two different types of command metadata
	private CommandMetadata metadataWithParameters;
	private CommandMetadata metadataWithoutParameters;
	
	//I'll test two states: 
	//Waiting for username (WFU) 
	
	private BlackjackProtocol nonauthorizedStateProtocolWFU;
	
	//and waiting for password (WFP)
	private BlackjackProtocol nonauthorizedStateProtocolWFP;
	
	/**These are the String Builders I want to test containing a set of
	 * capabilities which can be called from a specific state. I will then 
	 * set the protocol in one of these states and compare the response 
	 * returned from the processCommand String to the set of capability command words appended to 
	 * the commensurate list created.  */
	
	//StringBuilder for state WAITING_FOR_USERNAME (WFU)
	StringBuilder capsForWFU; 
	
	//StringBuilder for state WAITING_FOR_PASSWORD (WFP)
	StringBuilder capsForWFP;                                
	

	@Before
	public void setUp() throws Exception {
		
		//Class to test
		command = new CapabilitiesCommand();
		
		// My different metadata objects. I don't have to worry about testing
		// that the right command word is set, as that's done somewhere else.
		// I just have to test about differences in parameters -- either having
		// none, or having some.
		metadataWithParameters = new CommandMetadata( command.getCommandWord() );
		metadataWithParameters.addParameter( "a parameter" );
				
		metadataWithoutParameters = new CommandMetadata( command.getCommandWord());
		
					
		//Capabilities allowed for state WAITING_FOR_USERNAME (WFU)
		capsForWFU = new StringBuilder();
		capsForWFU.append("CAPABILITIES");
		capsForWFU.append("\n");
		capsForWFU.append("VERSION");
		capsForWFU.append("\n");
		capsForWFU.append("USERNAME");
		capsForWFU.append(" ");
		capsForWFU.append("username");
		capsForWFU.append("\n");
		capsForWFU.append("QUIT");
			
		
		//Capabilities allowed for state WAITING_FOR_PASSWORD (WFP)
		capsForWFP = new StringBuilder();
		capsForWFP.append("PASSWORD");
		capsForWFP.append(" ");
		capsForWFP.append("password");
		capsForWFP.append("\n");
		capsForWFP.append("VERSION");
		capsForWFP.append("\n");
		capsForWFP.append("CAPABILITIES");
		capsForWFP.append("\n");
		capsForWFP.append("QUIT");
		
		
		
		// Now that I have the capabilities allowed in selected states to test , 
		//I can create my protocol states that I want to test.
		
		nonauthorizedStateProtocolWFU = new BlackjackProtocol( null );
		nonauthorizedStateProtocolWFU.setState( STATE.WAITING_FOR_USERNAME );
		
		
		nonauthorizedStateProtocolWFP = new BlackjackProtocol( null );
		nonauthorizedStateProtocolWFP.setState( STATE.WAITING_FOR_PASSWORD );     
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetCommandWord() {
		assertEquals( command.getCommandWord(), "CAPABILITIES" );
		
		// I'll just test one of the words that the command word SHOULDN'T be
		assertNotEquals( command.getCommandWord(), "VERSION" );
	}
	
	@Test
	public void testGetValidStates() {
		Set<STATE> states = command.getValidStates();
		
		// Test a few of the states that it should be valid in
		assertTrue( states.contains( STATE.IN_SESSION_AND_YOUR_TURN ));
		assertTrue( states.contains( STATE.NOT_IN_SESSION ));
		assertTrue( states.contains( STATE.WAITING_FOR_PASSWORD) );
		assertTrue( states.contains( STATE.WAITING_FOR_USERNAME) );
		
		// And a state that it should NOT be valid in
		assertFalse( states.contains( STATE.DISCONNECTED) );
			
	}
	
	@Test
	public void testGetValidParameterNames() {
		// There shoudn't be any
		assertNull( command.getRequiredParameterNames() );
	}
	
	@Test
	public void testProcessCommand() {	
		
		ResponseCode response;	// Store responses in this

		// Test what happens if one of the parameters is null -- I should get an INTERNAL ERROR
		response = ResponseCode.getCodeFromString(
				command.processCommand( null, metadataWithParameters ));
		assertTrue( response.getCode() != null && 
				response.getCode().equals(ResponseCode.CODE.INTERNAL_ERROR.getCode() ) );
		
		response = ResponseCode.getCodeFromString(
				command.processCommand( nonauthorizedStateProtocolWFU, null));
		assertTrue( response.getCode() != null && 
				response.getCode().equals(ResponseCode.CODE.INTERNAL_ERROR.getCode() ) );
			
		
		/**Test for username state:
		 * Add usernameStateString -the expected response- to ArrayList<String> usernameCaps (i.e. usernme state capabilities)
		 * Add what String is returned by command.processCommand() in username state to ArrayList contentReturnedUN
		 * Assert that one array list containsAll() of the other. */
		
		String usernameStateString = "101 " + "CapabilitiesCommand.processCommand() List of capabilities allowed in state: " +
				"\n" + capsForWFU.toString();
		
				ArrayList<String> usernameCaps = new ArrayList<String>();
				ArrayList<String> contentReturnedUN = new ArrayList<String>();

				Scanner sc1 = new Scanner( usernameStateString );
				
						while(sc1.hasNext()) {
							String cap1 = sc1.next();
							usernameCaps.add(cap1);	
						}
						
				Scanner sc2 = new Scanner(command.processCommand( nonauthorizedStateProtocolWFU, metadataWithoutParameters));
						
						while(sc2.hasNext()) {
							String cap2 = sc2.next();
							contentReturnedUN.add( cap2 );	
						}
						assertTrue(contentReturnedUN.containsAll( usernameCaps ));
		
		/**Test for password state:
		* Add passwordStateString -the expected response- to ArrayList<String> passwordCaps (i.e. password state capabilities) 
		* Add what String is returned by command.processCommand() in password state to ArrayList contentReturnedPW.
		* Assert that one array list containsAll() of the other. */
		
		String passwordStateString = "101 " + "CapabilitiesCommand.processCommand() List of capabilities allowed in state: " +
								"\n" + capsForWFP.toString();				
						
						ArrayList<String> passwordCaps = new ArrayList<String>();
						ArrayList<String> contentReturnedPW = new ArrayList<String>();

						Scanner sc3 = new Scanner( passwordStateString );
						
								while(sc3.hasNext()) {
									String cap3 = sc3.next();
									passwordCaps.add( cap3 );	
								}
								
						Scanner sc4 = new Scanner( command.processCommand( nonauthorizedStateProtocolWFP, metadataWithoutParameters ));
								
								while(sc4.hasNext()) {
									String cap4 = sc4.next();
									contentReturnedPW.add( cap4 );	
								}
								assertTrue(contentReturnedPW.containsAll( passwordCaps )); 
	}
}

