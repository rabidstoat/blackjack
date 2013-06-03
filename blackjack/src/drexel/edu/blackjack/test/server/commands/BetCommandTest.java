/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - BetCommandTest.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 * 
 * Purpose: Unit test of BetCommand.java
 ******************************************************************************/
package drexel.edu.blackjack.test.server.commands;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import drexel.edu.blackjack.db.game.GameMetadata;
import drexel.edu.blackjack.db.user.UserMetadata;
import drexel.edu.blackjack.server.BlackjackProtocol;
import drexel.edu.blackjack.server.ResponseCode;
import drexel.edu.blackjack.server.BlackjackProtocol.STATE;
import drexel.edu.blackjack.server.commands.BetCommand;
import drexel.edu.blackjack.server.commands.CommandMetadata;
import drexel.edu.blackjack.server.game.Game;
import drexel.edu.blackjack.server.game.User;

/**
 * @author Constantine
 * */
public class BetCommandTest {
	
	// These are the account balances I'll use for my 'poor user' and 'rich user'
	// that I use to test the account balance command with
	private static final int POOR_BALANCE	= 20;
	private static final int RICH_BALANCE	= 2000;
	
	
	//GameMetadata
	private final String id = "one";
	private final int numDecks = 1;
	private final ArrayList<String> rules = new ArrayList<String>();
	private final int minBet = 25;
	private final int maxBet = 1000;
	private final int minPlayers = 1;
	private final int maxPlayers = 4; 
	
	
	private GameMetadata gameMetadata; 
	private GameMetadata.Builder gameBuilder;
	
	//A game 
	Game game; 
	
	//Class to test 
	private BetCommand command;
	
	//Test four different types of command metadata
	private CommandMetadata metadataWithParameters;
	private CommandMetadata metadataWithoutParameters;
	private CommandMetadata metadataWithTwoParameters;
	private CommandMetadata cannotParseOutNumberMetadata;
	private CommandMetadata betHigherThanBalance;
	private CommandMetadata betLowerThanMinBet;
	private CommandMetadata betHigherThanMaxbet;
	private CommandMetadata validBet;
	
	// I'll test with the blackjack protocol in a few different states
	private BlackjackProtocol nonauthorizedStateProtocol;
	private BlackjackProtocol authorizedStateProtocolPoorUser;
	private BlackjackProtocol authorizedStateProtocolRichUser;
	
	@Before
	public void setUp() throws Exception {
		
		//Class to test
		command = new BetCommand();
		
		//Add some rules to rules array.
		rules.add("some rules");
		
		//Metadata for GameMetadata.Builder
		gameBuilder = new GameMetadata.Builder();
		gameBuilder.setId(id);
		gameBuilder.setNumDecks(numDecks);
		gameBuilder.setRules(rules);
		gameBuilder.setMinBet(minBet);
		gameBuilder.setMaxBet(maxBet);
		gameBuilder.setMinPlayers(minPlayers);
		gameBuilder.setMaxPlayers(maxPlayers);
	
		gameMetadata = gameBuilder.build();
		
		//Pass game metadata to new game
		game = new Game(gameMetadata);
		
		//Test different scenarios of metadata 
		//1. Test with null parameter; it's an internal error		
		metadataWithoutParameters = new CommandMetadata( command.getCommandWord() );
		//1. test when protocol is parameter is null only
		metadataWithParameters = new CommandMetadata( command.getCommandWord() );
		metadataWithParameters.addParameter( "30" );
		
		//2. Test with not exactly one parameter; this is a syntax error
		metadataWithTwoParameters = new CommandMetadata( command.getCommandWord() );
		metadataWithTwoParameters.addParameter( "10" );
		metadataWithTwoParameters.addParameter( "2" );
				
		//3. Test with parameter that cannot be parsed out to a number
		cannotParseOutNumberMetadata = new CommandMetadata( command.getCommandWord() );	
		cannotParseOutNumberMetadata.addParameter("twenty");
		
		//4. Test when funds are insufficient -bet higher than poor balance
		betHigherThanBalance = new CommandMetadata( command.getCommandWord() );
		betHigherThanBalance.addParameter("21");
		
		//5. Test with desired bet lower than MINBET allowed
		betLowerThanMinBet = new CommandMetadata( command.getCommandWord() );
		betLowerThanMinBet.addParameter("17");
		
		
		//6. Test with bet higher than MAXBET allowed
		betHigherThanMaxbet = new CommandMetadata( command.getCommandWord() );
		betHigherThanMaxbet.addParameter("1001");
		
		//7. Test successful case
		validBet = new CommandMetadata( command.getCommandWord() );
		validBet.addParameter("999");
		
		
		// Now I want to test the protocol in different states. First, I'll need a
		// couple of user objects. Since the BET command tests bets in relation to balances 
		//also, I'll create a poor user and a rich user. The poor user has little money, the
		// rich user has a lot of money
		UserMetadata.Builder userBuilder = new UserMetadata.Builder();
		//Pass user metadata		
		userBuilder.setBalance( POOR_BALANCE );
		userBuilder.setFullname( "Poor User" );
		userBuilder.setPassword( "password" );
		userBuilder.setUsername( "pooruser" );
		UserMetadata poorUserMetadata = userBuilder.build();
		
		User poorUser = new User( poorUserMetadata );
		//Set the game user is in (can only be in one)
		poorUser.setGame(game);

		userBuilder.setBalance( RICH_BALANCE );
		userBuilder.setFullname( "Rich User" );
		userBuilder.setPassword( "password" );
		userBuilder.setUsername( "richuser" );
		UserMetadata richUserMetadata = userBuilder.build();
		
		User richUser = new User( richUserMetadata ); 
		//Set the game user is in (can only be in one)
		richUser.setGame(game);

		// Now that I have my users, I can create my protocol states that I want to test
		
		nonauthorizedStateProtocol = new BlackjackProtocol( null );
		nonauthorizedStateProtocol.setUser( poorUser );
		nonauthorizedStateProtocol.setState( STATE.WAITING_FOR_USERNAME );

		authorizedStateProtocolPoorUser = new BlackjackProtocol( null );
		authorizedStateProtocolPoorUser.setUser( poorUser );
		authorizedStateProtocolPoorUser.setState( STATE.IN_SESSION_AWAITING_BETS );

		authorizedStateProtocolRichUser = new BlackjackProtocol( null );
		authorizedStateProtocolRichUser.setUser( richUser );
		authorizedStateProtocolRichUser.setState( STATE.IN_SESSION_AWAITING_BETS );

		
	}

	@After
	public void tearDown() throws Exception {
	}

		@Test
		public void testGetCommandWord() {
			assertEquals( command.getCommandWord(), "BET" );
			
			// I'll just test one of the words that the command word SHOULDN'T be
			assertNotEquals( command.getCommandWord(), "ACCOUNT" );
		}
		
		@Test
		public void testGetValidStates() {
		
			Set<STATE> states = command.getValidStates();
			
			// I'll test the ONLY state that it should be valid in
			assertTrue( states.contains(STATE.IN_SESSION_AWAITING_BETS));
			
			
			// And states that it should NOT be valid in
			assertFalse( states.contains(STATE.WAITING_FOR_PASSWORD) );
			assertFalse( states.contains(STATE.WAITING_FOR_USERNAME) );
			assertFalse( states.contains(STATE.NOT_IN_SESSION ));
		}
		
		@Test
		public void testGetValidParameterNames() {
			
			// The valid parameter is "amount"
			List<String> names = new ArrayList<String>();
			names.add( "amount" );
			assertEquals( command.getRequiredParameterNames(), names  );
		}
		
		@Test
		public void testProcessCommand() {
			
			ResponseCode response;	// Store responses in this
			
			//1. Test with null parameter; it's an internal error	
			response = ResponseCode.getCodeFromString(
					command.processCommand( null, metadataWithParameters));
			assertTrue( response.getCode() != null && 
					response.getCode().equals(ResponseCode.CODE.INTERNAL_ERROR.getCode() ) );
			
			response = ResponseCode.getCodeFromString(
					command.processCommand( authorizedStateProtocolPoorUser, null));
			assertTrue( response.getCode() != null && 
					response.getCode().equals(ResponseCode.CODE.INTERNAL_ERROR.getCode() ) );
			
			//Test invalid state for betting
			response = ResponseCode.getCodeFromString(
					command.processCommand( nonauthorizedStateProtocol, metadataWithParameters ) );
			assertTrue( response.getCode() != null && 
					response.getCode().equals(ResponseCode.CODE.INVALID_BET_NOT_EXPECTED.getCode() ) );	
			
			//Test when bet parameter is not exactly one string
			//2. First when it is null
			response = ResponseCode.getCodeFromString(
					command.processCommand( authorizedStateProtocolPoorUser, metadataWithoutParameters ) );
			assertTrue( response.getCode() != null && 
					response.getCode().equals(ResponseCode.CODE.SYNTAX_ERROR.getCode() ) );		
				
			//2. Test with more than one parameter; this is a syntax error
			response = ResponseCode.getCodeFromString(
					command.processCommand( authorizedStateProtocolPoorUser, metadataWithTwoParameters ) );
			assertTrue( response.getCode() != null && 
					response.getCode().equals(ResponseCode.CODE.SYNTAX_ERROR.getCode() ) );  
			
			//3. Test with parameter that cannot be parsed out to a number
			response = ResponseCode.getCodeFromString(
					command.processCommand( authorizedStateProtocolPoorUser, cannotParseOutNumberMetadata ) );
			assertTrue( response.getCode() != null && 
					response.getCode().equals(ResponseCode.CODE.SYNTAX_ERROR.getCode() ) ); 
					
			//4. Test for insufficient user funds -desired bet higher than poor balance
			response = ResponseCode.getCodeFromString(
					command.processCommand( authorizedStateProtocolPoorUser, betHigherThanBalance ) );
			assertTrue( response.getCode() != null && 
					response.getCode().equals(ResponseCode.CODE.INVALID_BET_TOO_POOR.getCode() ) ); 
			
			//5. Test with desired bet lower than MINBET allowed
			response = ResponseCode.getCodeFromString(
					command.processCommand( authorizedStateProtocolPoorUser, betLowerThanMinBet ) );
			assertTrue( response.getCode() != null && 
					response.getCode().equals(ResponseCode.CODE.INVALID_BET_OUTSIDE_RANGE.getCode() ) ); 
			
			//6. Test with bet higher than MAXBET allowed
			response = ResponseCode.getCodeFromString(
					command.processCommand( authorizedStateProtocolRichUser, betHigherThanMaxbet ) );
			assertTrue( response.getCode() != null && 
					response.getCode().equals(ResponseCode.CODE.INVALID_BET_OUTSIDE_RANGE.getCode() ) ); 
			
			//7. Test valid, successful bet made
			response = ResponseCode.getCodeFromString(
					command.processCommand( authorizedStateProtocolRichUser, validBet ) );
			assertTrue( response.getCode() != null && 
					response.getCode().equals(ResponseCode.CODE.SUCCESSFULLY_BET.getCode() ) ); 
		
			
		}

	

}
