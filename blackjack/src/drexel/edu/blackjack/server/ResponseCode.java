/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - ResponseCode.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 * 
 * Purpose: This is an object-oriented view of a response sent from the server.
 * It has methods for serializing and deserializing messages (so they can be
 * sent over the socket), along with a bunch of convenience methods for getting
 * information about the response, such as what type it is (e.g., informative,
 * error, etc.), what its parameters are, and (if multiline) what the different
 * lines of response are.
 ******************************************************************************/
package drexel.edu.blackjack.server;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import drexel.edu.blackjack.server.commands.PasswordCommand;

/**
 * This class is used to generate or interpret response
 * codes. It's mostly a set of convenience functions, though
 * by encapsulating the functionality we can change codes
 * in just one place if we get them wrong.
 * <P>
 * <b>STATEFUL</b>: The enumeration lists the various messages
 * that are sent. Some of these messages signify transitions
 * in the DFA for the protocol, though the actual code that
 * does the transition reside elsewhere, in the various classes
 * in the {@link drexel.edu.blackjack.server.commands} package.
 * 
 * @author Jennifer
 */
public class ResponseCode {

	/***************************************************************
	 * Code definitions go here
	 **************************************************************/
	
	/**
	 * This lists all the response codes, with the enumeration value they are
	 * known by, the code (as a 3-digit number), and the default message.
	 * 
	 * NOTE THAT IF THE DEFAULT MESSAGE IS NULL THE CONSTRUCTOR WHERE YOU
	 * JUST PASS IN A CODE ENUMERATION CANNOT BE USED.
	 * 
	 * This is becuse there's a parameter expected, and then optional
	 * text. So either: a) use the constructor where you specify the
	 * code AND the overridden text, which you can use to specify
	 * the parameter(s); or,b) use a convenience function to generate
	 * the response code.
	 */
	public enum CODE {
		
		/**
		 * Successful response to CAPABILITIES command. No state
		 * change occurs with this response.
		 */
		CAPABILITIES_FOLLOW ( 101, "Capabilities list follows." ),
		/**
		 * Successful response to a LISTGAMES command. It signals the
		 * start of a multiline message where a list of games, in a 
		 * format specified by the protocol, follows. No state
		 * change occurs with this response.
		 */
		GAMES_FOLLOW ( 102, null ),
		/**
		 * Successful response to a VERSION command. No state
		 * change occurs with this response.
		 */
		VERSION ( 103, null ),
		/**
		 * Successful response to an ACCOUNT command. No state
		 * change occurs with this response.
		 */
		ACCOUNT_BALANCE ( 104, null ),
		/**
		 * A successful, but single-line, response to the LISTGAMES
		 * command, which indicates that there are no games. No state
		 * change occurs with this response.
		 */
		NO_GAMES_HOSTED ( 105, "There are no games hosted on this server." ),
		/**
		 * Sent to the client without a prompting request, to indicate when
		 * the client has not bet in time and has been dropped from the game
		 * session. This also signifies a state change in the DFA, from the
		 * {@link drexel.edu.blackjack.server.BlackjackProtocol.STATE#IN_SESSION_AWAITING_BETS}
		 * to the 
		 * {@link drexel.edu.blackjack.server.BlackjackProtocol.STATE#NOT_IN_SESSION}
		 * state.
		 */
		TIMEOUT_EXCEEDED_WHILE_BETTING ( 106, "Client idle too long while server was expecting a BET." ),
		/**
		 * Sent to the client without a prompting request, to indicate when
		 * the client has not specified a game play in time and has been dropped from the game
		 * session. This also signifies a state change in the DFA, from the
		 * {@link drexel.edu.blackjack.server.BlackjackProtocol.STATE#IN_SESSION_AND_YOUR_TURN}
		 * to the 
		 * {@link drexel.edu.blackjack.server.BlackjackProtocol.STATE#NOT_IN_SESSION}
		 * state.
		 */
		TIMEOUT_EXCEEDED_WHILE_PLAYING ( 107, "Client idle too long while server was expecting HIT or STAND." ),
		
		/**
		 * Successful response to a PASSWORD command. In the DFA,
		 * it transitions from the 
		 * {@link drexel.edu.blackjack.server.BlackjackProtocol.STATE#WAITING_FOR_PASSWORD}
		 * to the 
		 * {@link drexel.edu.blackjack.server.BlackjackProtocol.STATE#NOT_IN_SESSION}
		 * state.
		 */
		SUCCESSFULLY_AUTHENTICATED( 200, "The username and password were correct. Welcome to the game!" ),
		/**
		 * Successful response to a QUIT command. In the DFA,
		 * it transitions from the current state to the
		 * {@link drexel.edu.blackjack.server.BlackjackProtocol.STATE#DISCONNECTED}
		 * state.
		 */
		SUCCESSFULLY_QUIT( 201, "Come back soon!" ),
		/**
		 * Successful response to a JOINSESSION command. In the DFA,
		 * it transitions from the 
		 * {@link drexel.edu.blackjack.server.BlackjackProtocol.STATE#NOT_IN_SESSION}
		 * to the 
		 * {@link drexel.edu.blackjack.server.BlackjackProtocol.STATE#IN_SESSION_AS_OBSERVER}
		 * state.
		 */
		SUCCESSFULLY_JOINED_SESSION( 211, "Successfully joined game session." ),
		/**
		 * Successful response to a BET command. In the DFA, it involves
		 * no state transition.
		 */
		SUCCESSFULLY_BET( 220, "Bet successfully placed. Good luck!" ),
		/**
		 * Successful response to a LEAVESESSION command. In the DFA, it involves
		 * a transition from either the
		 * {@link drexel.edu.blackjack.server.BlackjackProtocol.STATE#IN_SESSION_AS_OBSERVER}
		 * or the
		 * {@link drexel.edu.blackjack.server.BlackjackProtocol.STATE#IN_SESSION_AWAITING_BETS}
		 * state to the
		 * {@link drexel.edu.blackjack.server.BlackjackProtocol.STATE#NOT_IN_SESSION}
		 * state.
		 */
		SUCCESSFULLY_LEFT_SESSION_NOT_MIDPLAY( 221, "Successfully left the game session. No bet was forfeited." ),
		/**
		 * Successful response to a LEAVESESSION command. In the DFA, it involves
		 * a transition from one of the
		 * {@link drexel.edu.blackjack.server.BlackjackProtocol.STATE#IN_SESSION_AND_YOUR_TURN},
		 * {@link drexel.edu.blackjack.server.BlackjackProtocol.STATE#IN_SESSION_AFTER_YOUR_TURN}, or
		 * {@link drexel.edu.blackjack.server.BlackjackProtocol.STATE#IN_SESSION_BEFORE_YOUR_TURN}
		 * states to the
		 * {@link drexel.edu.blackjack.server.BlackjackProtocol.STATE#NOT_IN_SESSION}
		 * state.
		 */
		SUCCESSFULLY_LEFT_SESSION_FORFEIT_BET( 222, null ),
		/**
		 * Successful response to a STAND command. In the DFA, it involves
		 * a transition from the
		 * {@link drexel.edu.blackjack.server.BlackjackProtocol.STATE#IN_SESSION_AND_YOUR_TURN}
		 * state to the
		 * {@link drexel.edu.blackjack.server.BlackjackProtocol.STATE#IN_SESSION_AFTER_YOUR_TURN}
		 * state.
		 */
		SUCCESSFULLY_STAND( 223, "Okay, you stand. No more cards will be dealt." ),

		/**
		 * Successful response to a USERNAME command. In the DFA, it involves
		 * a transition from the
		 * {@link drexel.edu.blackjack.server.BlackjackProtocol.STATE#WAITING_FOR_USERNAME}
		 * state to the
		 * {@link drexel.edu.blackjack.server.BlackjackProtocol.STATE#WAITING_FOR_PASSWORD}
		 * state.
		 */
		WAITING_FOR_PASSWORD( 300, "User acknowledged. Send PASSWORD." ),
		/**
		 * Successful response to a HIT command. In the DFA, it involves
		 * no state transition.
		 */
		SUCCESSFULLY_HIT( 320, null ),

		/**
		 * Unsuccessful response to any command in any state, it
		 * involves no state transition. It signifies that an unknown
		 * internal error (like a null pointer) occurred in the server,
		 * presumably as the result of a bug.
		 */
		INTERNAL_ERROR ( 400, "An internal error occured in the server." ),
		/**
		 * Unsuccessful response to any command that is valid only in
		 * an authenticated DFA state, if the protocol is not in such
		 * a state. It involves no state transition.
		 */
		NEED_TO_BE_AUTHENTICATED( 401, "The client must authenticate before using this command." ),
		/**
		 * Unsuccessful response to a PASSWORD command, it involves
		 * a state transition from the 
		 * {@link drexel.edu.blackjack.server.BlackjackProtocol.STATE#WAITING_FOR_PASSWORD}
		 * to the 
		 * {@link drexel.edu.blackjack.server.BlackjackProtocol.STATE#WAITING_FOR_USERNAME}
		 * state. It signifies that either the username was invalid, or the
		 * password was not correct.
		 */
		INVALID_LOGIN_CREDENTIALS( 402, "The username and password are incorrect." ),
		/**
		 * Unsuccessful response to a PASSWORD command if the protocol state is not
		 * {@link drexel.edu.blackjack.server.BlackjackProtocol.STATE#WAITING_FOR_PASSWORD}.
		 * It involves no state transition.
		 */
		NOT_EXPECTING_PASSWORD( 403, "Server was not expected to receive a PASSWORD command just now." ),
		/**
		 * Unsuccessful response to a USERNAME command if the protocol state is not
		 * {@link drexel.edu.blackjack.server.BlackjackProtocol.STATE#WAITING_FOR_USERNAME}.
		 * It involves no state transition.
		 */
		NOT_EXPECTING_USERNAME( 404, "Server was not expected to receive a USERNAME command just now." ),
		/**
		 * Unsuccessful response to a PASSWORD command, it involves
		 * a state transition from the 
		 * {@link drexel.edu.blackjack.server.BlackjackProtocol.STATE#WAITING_FOR_PASSWORD}
		 * to the 
		 * {@link drexel.edu.blackjack.server.BlackjackProtocol.STATE#DISCONNECTED} state.
		 * It signifies that the user has failed the login attempt too many times.
		 */
		LOGIN_ATTEMPTS_EXCEEDED( 405, "Exceeded allowed " + PasswordCommand.INCORRECT_LOGIN_LIMIT + " incorrect login attempts" ),
		/**
		 * Unsuccessful response to a PASSWORD command, it involves
		 * a state transition from the 
		 * {@link drexel.edu.blackjack.server.BlackjackProtocol.STATE#WAITING_FOR_PASSWORD}
		 * to the 
		 * {@link drexel.edu.blackjack.server.BlackjackProtocol.STATE#DISCONNECTED} state.
		 * It signifies that the user is already logged in with a different connection
		 * elsewhere.
		 */
		ALREADY_LOGGED_IN( 406, "Already logged in elsewhere" ),
		/**
		 * Unsuccessful response to a JOINSESSION command, it involves
		 * no state change. It signifies that the session ID used does
		 * not exist.
		 */
		JOIN_SESSION_DOES_NOT_EXIST( 410, "Tried to join a non-existent game session." ),
		/**
		 * Unsuccessful response to a JOINSESSION command, it involves
		 * no state change. It signifies that there are too many players
		 * already in the game session.
		 */
		JOIN_SESSION_AT_MAX_PLAYERS( 411, "Cannot join a session at the maximum number of players." ),
		/**
		 * Unsuccessful response to a JOINSESSION command, it involves
		 * no state change. It signifies that the user does not have a large
		 * enough account balance to meet the minimum bet of the game.
		 */
		JOIN_SESSION_TOO_POOR( 412, "Cannot join the session as bank account is too low." ),
		/**
		 * Unsuccessful response to a LEAVESESSION command if the user is not
		 * in a game session. It involves no state transition.
		 */
		USER_NOT_IN_GAME_ERROR( 413, "That command can't be used if not in a game session." ),
		/**
		 * Unsuccessful response to a BET command if the protocol state is not
		 * {@link drexel.edu.blackjack.server.BlackjackProtocol.STATE#IN_SESSION_AWAITING_BETS}.
		 * It involves no state transition.
		 */
		INVALID_BET_NOT_EXPECTED( 420, "The server was not expecting a BET command now. Be patient." ),
		/**
		 * Unsuccessful response to a BET command if the bet is below
		 * the minimum value or above the maximum value. It involves no
		 * state transition.
		 */
		INVALID_BET_OUTSIDE_RANGE( 421, "That bet amount is either below the minimum or above the maximum allowed." ),
		/**
		 * Unsuccessful response to a BET command if the user's account
		 * is not large enough to cover th bet. It involves no
		 * state transition.
		 */
		INVALID_BET_TOO_POOR( 422, "That bet amount is more than the user's account balance." ),
		/**
		 * Unsuccessful response to a HIT command when the user goes
		 * over 21 points. It signals a state transition from the 
		 * {@link drexel.edu.blackjack.server.BlackjackProtocol.STATE#IN_SESSION_AND_YOUR_TURN}
		 * state to the
		 * {@link drexel.edu.blackjack.server.BlackjackProtocol.STATE#IN_SESSION_AFTER_YOUR_TURN}
		 * state.
		 */
		USER_BUSTED( 423, null ),
		/**
		 * Unsuccessful response to a HIT command if the protocol state is not
		 * {@link drexel.edu.blackjack.server.BlackjackProtocol.STATE#IN_SESSION_AND_YOUR_TURN} state.
		 * It involves no state transition.
		 */
		NOT_EXPECTING_HIT( 424, "The server was not expecting a HIT command now. Be patient." ),
		/**
		 * Unsuccessful response to a STAND command if the protocol state is not
		 * {@link drexel.edu.blackjack.server.BlackjackProtocol.STATE#IN_SESSION_AND_YOUR_TURN} state.
		 * It involves no state transition.
		 */
		NOT_EXPECTING_STAND( 425, "The server was not expecting a STAND command now. Be patient." ),
		/**
		 * Unsuccessful response to a JOINSESSION command if the protocol state is not
		 * {@link drexel.edu.blackjack.server.BlackjackProtocol.STATE#NOT_IN_SESSION} state.
		 * It involves no state transition.
		 */
		ALREADY_IN_SESSION( 426, "Cannot JOINSESSION when already in a session." ),
		/**
		 * Unsuccessful response to a GAMESTATUS command if the protocol state is not
		 * in one of the IN_SESSION* states. It involves no state transition.
		 */
		NOT_EXPECTING_GAMESTATUS( 427, "The server was not expecting a GAMESTATUS command now. Are you even in a session?" ),
		/**
		 * Unsuccessful response to a GAMESTATUS command if the game session
		 * identified does not exist. It involves no state transition.
		 */
		GAMESTATUS_DOES_NOT_EXIST( 428, "Tried to request status of a non-existent game session." ),

		/**
		 * Unsuccessful response to any command if the server does not
		 * recognize it as a required or optional command in the protocol.
		 * It involves no state transition.
		 */
		UNKNOWN_COMMAND( 500, "That command is unknown to the server." ),
		/**
		 * Unsuccessful response to any optional command that is not
		 * implemented by the server. It involves no state transition.
		 */
		UNSUPPORTED_COMMAND( 501, "That command is not supported on this server." ),
		/**
		 * Unsuccessful response to any command that required parameters to
		 * be specified, and did not, or had invalid parameters specified.
		 * In involves no state transition.
		 */
		SYNTAX_ERROR( 502, "That command had a syntax error." ),	
		
		/**
		 * Informative message alerting the client that another user
		 * has joined the game session they have joined.
		 * In involves no state transition.
		 */
		PLAYER_JOINED( 620, null ),
		/**
		 * Informative message alerting the client that another user
		 * has left the game session they have joined.
		 * In involves no state transition.
		 */
		PLAYER_LEFT( 621, null ),
		/**
		 * Informative message alerting the client that another user
		 * has made a bet in the game session they have joined.
		 * In involves no state transition.
		 */
		PLAYER_BET( 622, null ),
		/**
		 * Informative message alerting the client that another user
		 * or the dealer has performed some action in the game session 
		 * they have joined.
		 * In involves no state transition.
		 */
		PLAYER_ACTION( 623, null ),
		/**
		 * Informative message alerting the client that new cards
		 * have been dealt in the current game session.
		 * In involves no state transition.
		 */
		CARDS_DEALT(624, null ),
		/**
		 * Informative message alerting the client that, while no
		 * new cards have been dealt, the hand has been updatd in
		 * that previously facedown cards are now visible.
		 * In involves no state transition.
		 */
		UPDATED_HAND( 625, null),
		/**
		 * Informative message alerting the client as to the outcome
		 * of the game session they have joined.
		 * In involves no state transition.
		 */
		GAME_OUTCOME( 626, null ),
		/**
		 * Informative message that is a response to the GAMESTATUS
		 * command, while the protocol is in one of the IN_SESSION*
		 * states. In involves no state transition.
		 */
		GAME_STATUS( 627, null ),
		
		/**
		 * Alert to the client that the server is expecting a BET
		 * command. It involves a transition from the 
		 * {@link drexel.edu.blackjack.server.BlackjackProtocol.STATE#IN_SESSION_AS_OBSERVER}
		 * to the
		 * {@link drexel.edu.blackjack.server.BlackjackProtocol.STATE#IN_SESSION_AWAITING_BETS}
		 * state.
		 */
		REQUEST_FOR_BET( 720, "What amount do you wish to bet?" ),
		/**
		 * Alert to the client that the server is expecting either a HIT or STAND
		 * command. It involves a transition from the 
		 * {@link drexel.edu.blackjack.server.BlackjackProtocol.STATE#IN_SESSION_BEFORE_YOUR_TURN}
		 * to the
		 * {@link drexel.edu.blackjack.server.BlackjackProtocol.STATE#IN_SESSION_AND_YOUR_TURN}
		 * state.
		 */
		REQUEST_FOR_GAME_ACTION( 721, "What game action would you like to take?" );

		// 3-digit response code
		private final int code;
		// A somewhat human-understandable explanation
		private final String message;
		// Simple constructor
		CODE( int code, String message ) {
			this.code = code;
			this.message = message;
		}
		
		public int getCode() {
			return code;
		}
		
		public String getCodeAsString() {
			return Integer.valueOf(code).toString();
		}
		
		public String getMessage() {
			return message;
		}

	}

	/***************************************************************
	 * Local variables go here
	 **************************************************************/
	
	// The code is the 3-digit number, represented as an integer
	private Integer code = null;
	
	// And here is the optional text -- sometimes it's parameters, 
	// sometimes it's just extra text, it depends on the error code
	private String text = null;
	
	/***************************************************************
	 * Constructors!
	 **************************************************************/

	/**
	 * Constructs an empty response code. This is
	 * purposefully private so others don't use it.
	 */
	private ResponseCode() {
	}
	
	/**
	 * Constructs a type of response code with the
	 * default message. If the code doesn't have a
	 * default message, then an exception is thrown.
	 * 
	 * @param code Must be non-null, and must be a code
	 * that has a default message.
	 * @throws IllegalArgumentException If the code is null
	 * OR if the code requires a message to be specified
	 */
	public ResponseCode( CODE code ) {

		// Make sure the argument is correct
		if( code == null ) {
			throw new IllegalArgumentException( "The code parameter cannot be null." );
		}
		if( code.getMessage() == null ) {
			throw new IllegalArgumentException( "The code used requires you to use " +
						"the constructor where you specify a message, for the parameter" );
		}
		
		this.code = code.getCode();
		this.text = code.getMessage();
	}
	
	/**
	 * Constructs a type of response code, overriding
	 * the message. If you don't want to include the
	 * default message, and don't want to include any
	 * message, pass in a null
	 * 
	 * @param code A valid code
	 * @param message The message to include after it.
	 * Could be null if you don't want a message
	 */
	public ResponseCode( CODE code, String message ) {

		if( code == null ) {
			throw new IllegalArgumentException( "The code parameter cannot be null." );
		}
		
		this.code = code.getCode();
		this.text = message;
	}
	
	/***************************************************************
	 * Public methods for moving between strings and ResponseCode
	 * objects go here
	 **************************************************************/
	
	/**
	 * Given an instantiated response code, generate a string
	 * that represents this. Mostly this is concatenating the
	 * code and the text.
	 * 
	 * @return A valid response code string that the server could
	 * send to the client
	 */
	@Override
	public String toString() {
		
		// Best practice to use a string builder for appending
		StringBuilder str = new StringBuilder();
		
		// If the code isn't set, that's an internal error right there
		if( code == null ) {
			str.append( CODE.INTERNAL_ERROR.toString() );
		} else {
			// Otherwise just start with the code
			str.append( code.toString() );
		}
		
		// Any non-null text is added
		if( text != null ) {
			str.append( " " );
			str.append( text );
		}
		
		return str.toString();
	}
	
	/**
	 * Given a string -- for example, one that a client received --
	 * instantiate a ResponseCode for the string. If there is a
	 * syntax error, which basically means that the string did
	 * not start with a 3-digit code, then a null is returned.
	 * 
	 * @param str The string, which should be something like
	 * "500 something went wrong!" 
	 * 
	 * @return If the string started with a 3-digit integer then
	 * return a ResponseCode object, parsing that and any text
	 * out from the string. Otherwise, return null.
	 */
	public static ResponseCode getCodeFromString( String str ) {
		
		// Nulls are bad, obviously, and we need at least 3 characters for the response code
		if( str == null || str.length() < 3) {
			return null;
		}
		
		// Figure out if it starts with a valid 3-digit response code
		String numberAsString = str.substring(0,3);
		Integer number = null;
		try {
			number = Integer.valueOf( Integer.parseInt( numberAsString ) );
		} catch( Exception e ) {
			// Don't worry about reporting this
		}
		
		// We need a number to be set, if not, that's ad
		if( number == null ) {
			return null;
		}
		
		// Now we set up the response code
		ResponseCode code = new ResponseCode();
		// This just says "and everything after the third character, unless there's nothing
		// there, in which case just set it to a null
		code.setText( str.length() > 3 ? str.substring(3).trim() : null );
		code.setCode( number );

		return code;
	}
	
	/*************************************************************************************
	 * Some convenience methods for creating response codes
	 ************************************************************************************/
	
	/**
	 * Generate a response with the appropriate code for
	 * reporting the balance, with the account balance
	 * (as an integer) following.
	 * 
	 * @param balance The user's account balance
	 * @return A proper response code, for the given balance
	 */
	public static ResponseCode createAccountBalanceResponseCode( int balance ) {
		return new ResponseCode( CODE.ACCOUNT_BALANCE, "" + balance );
	}

	/*************************************************************************************
	 * Some convenience methods for interpreting response codes passed back as string
	 ************************************************************************************/
	
	/**
	 * Given a string that represents the response code, create an actual ResponseCode
	 * object from it.
	 * 
	 * @param responseString A string like one would receive from a BlackjackCommand.processCommand()
	 * method call
	 * @return The ResponseCode object that can be constructed from it
	 */
	public static ResponseCode createResponseCodeFromString( String responseString ) {
		if( responseString == null ) {
			return null;
		}
		
		ResponseCode responseCode = new ResponseCode();
		
		// The first 'token' is the code, which would be an integer
		StringTokenizer strtok = new StringTokenizer(responseString);
		String codeAsString = null;
		if( strtok.hasMoreTokens() ) {
			codeAsString = strtok.nextToken();
			try {
				Integer codeAsInteger = Integer.parseInt(codeAsString);
				responseCode.setCode( codeAsInteger );
			} catch( NumberFormatException e ) {
				// TODO: Add an error reported
			}
		}
		
		// The rest of the string after this first token is the text
		if( codeAsString != null ) {
			responseCode.setText( responseString.substring(codeAsString.length()).trim() );
		}
		
		return responseCode;
	}

	/*************************************************************************************
	 * Autogenerated getters and setters.
	 ************************************************************************************/

	/**
	 * Get the numeric code of this response code
	 * @return the code
	 */
	public Integer getCode() {
		return code;
	}

	/**
	 * Set the numeric code of this response code
	 * @param code the code to set
	 */
	public void setCode(Integer code) {
		this.code = code;
	}

	/**
	 * Get the text of this response messages
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * Set the text of this response messages
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}
	
	/*************************************************************************************
	 * Smarter getters
	 ************************************************************************************/
	
	/**
	 * The text of the response code might just be optional text, but in some
	 * cases the first 'word' should be interpreted as a parameter to the response.
	 * Return this first word, if present. (Note that if the response code doesn't
	 * have parameters, this method doesn't really make sense to use.)
	 * 
	 * @return The first parameter of the response string, or null if there is
	 * nothing that could be interpreted as a parameter
	 */
	public String getFirstParameterAsString() {
		
		String parameter = null;
		
		String text = getText();
		// Only can parse out a parameter if there's some text
		if( text != null ) {
			StringTokenizer strtok = new StringTokenizer(text);
			if( strtok.hasMoreTokens() ) {
				parameter = strtok.nextToken();
			}
		}
		
		return parameter;
	}

	/**
	 * The text of the response code might just be optional text, but in some
	 * cases the first 'word' should be interpreted as a parameter to the response.
	 * Moreover sometimes this word should be a number. Try to find a number that
	 * could be returned
	 * 
	 * @return The first parameter of the response string interpreted as an integer.
	 * If there is no first parameter, or it's not a number, returns null.
	 */
	public Integer getFirstParameterAsInteger() {
		
		Integer parameter = null;

		String parameterAsString = getFirstParameterAsString();
		if( parameterAsString != null ) {
			try {
				parameter = Integer.parseInt( parameterAsString );
			} catch( NumberFormatException e ) {
				// It's okay to silently fail here
			}
		}
		
		return parameter;
	}
	
	/**
	 * The text of the response code might just be optional text, but in some
	 * cases the 'words' should be interpreted as parameters to the response.
	 * Return this tokenized list of words, if present. (Note that if the 
	 * response code doesn't have parameters, this method doesn't really make 
	 * sense to use, though it will still return something.)
	 * 
	 * @return The parameters of the response string, or null if there is
	 * nothing that could be interpreted as a parameter
	 */
	public List<String> getParameters() {
		
		// Will try to return in this
		List<String> params = null;
		
		// By tokenizing this
		String text = getText();
		
		// Only can parse out a parameter if there's some text
		if( text != null ) {
			StringTokenizer strtok = new StringTokenizer(text);
			params = new ArrayList<String>();;
			while( strtok.hasMoreTokens() ) {
				params.add( strtok.nextToken() );
			}
		}
		
		return params;
	}

	/*************************************************************************************
	 * For figuring out things about this type of response code
	 ************************************************************************************/
	
	/** 
	 * A syntactically correct error code is anything in the 
	 * 4xx ("Command was syntactically correct but failed for 
	 * some reason" range.
	 * 
	 * @return True if it's this type or error, else false
	 */
	public boolean isSyntacticallyCorrectError() {
		
		return code != null && code >= 400 && code < 500;
		
	}

	/** 
	 * A syntactically INcorrect error code is anything in the 
	 * 5xx ("Command unknown, unsupported, unavailable, or 
	 * syntax error") range.
	 * 
	 * @return True if it's this type or error, else false
	 */
	public boolean isSyntacticallyIncorrectError() {
		
		return code != null && code >= 500 && code < 600;
		
	}

	/** 
	 * An error code is anything in the 4xx ("Command was syntactically
	 * correct but failed for some reason" or 5xx ("Command unknown,
	 * unsupported, unavailable, or syntax error") range.
	 * 
	 * @return True if it represents an error, else false
	 */
	public boolean isError() {
		
		return isSyntacticallyCorrectError() || isSyntacticallyIncorrectError();
		
	}

	/** 
	 * A command completed code is anything in the 2xx range.
	 * 
	 * @return True if it represents an completed command, else false
	 */
	public boolean isCommandComplete() {
		
		return code != null && code >= 200 && code < 300;
		
	}

	/** 
	 * A game updated code is in the 6xx range
	 * 
	 * @return True if it represents a game update message, else false
	 */
	public boolean isGameUpate() {
		
		return code != null && code >= 600 && code < 700;
		
	}

	/** 
	 * A game action request command is in the 7xx range
	 * 
	 * @return True if it represents a game action request, else false
	 */
	public boolean isGameActionRequest() {
		
		return code != null && code >= 700 && code < 800;
		
	}

	/** 
	 * An informative code is anything in the 1xx range.
	 * 
	 * @return True if it represents an informative
	 * code, else false
	 */
	public boolean isInformative() {
		
		return code != null && code >= 100 && code < 200;
		
	}

	/** 
	 * A malformed code has a null code number.
	 * 
	 * @return True if this is a malformed code
	 */
	public boolean isMalformed() {
		
		return code == null;
				
	}
	
	/** 
	 * A game state message is something in the 6xx
	 * range.
	 * 
	 * @return True if this is a game state code
	 */
	public boolean isGameState() {
		
		return code != null && code >= 600 && code < 700;		
		
	}


	/*************************************************************************************
	 * So we can do equals() on it.
	 ************************************************************************************/

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		
		// Something is equal only if the code AND the text are equal
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResponseCode other = (ResponseCode) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		return true;
	}
	
	/**
	 * This basically is like an equality on the code attribute,
	 * ignoring whether or not the text is equal.
	 * 
	 * @param compareToCode The response code comparing to
	 * @return True if just the code part of the ResponseCode
	 * is equal (ignoring any text differences), false otherwise
	 */
	public boolean hasSameCode( ResponseCode.CODE compareToCode ) {
		
		if( compareToCode != null ) {
			return getCode() == compareToCode.getCode();
		}
		
		return false;
	}

	/**
	 * Certain response codes inolve multiline responses. If that is
	 * the case for the current code, return true, else false.
	 * @return True if a multiline response code, false otherwise
	 */
	public boolean isMultilineCode() {
		
		return this.hasSameCode(CODE.CAPABILITIES_FOLLOW) ||
				hasSameCode(CODE.GAMES_FOLLOW) ||
				hasSameCode( CODE.GAME_STATUS );
	}
	
	/**
	 * If this response code is a multiline message, return one
	 * of those multiple lines with this method call. It will
	 * return null if either the index is out of range OR
	 * if the message isn't multiline.
	 * 
	 * @param index 0 is the message code, then starting at index 1
	 * is the real meat of the response
	 * @return Null if not a multiline message or index out of range,
	 * else the line at that index
	 */
	public String getMultiline( int index ) {
		
		// Has to be a multiline coded message
		if( !isMultilineCode() ) {
			return null;
		}
		
		// If the text is null, that's bad
		if( text == null ) {
			return null;
		}
		
		// Otherwise we count
		String[] lines = text.split( "\n" );
		if( index < 0 || index >= lines.length ) {
			return null;
		}
		
		return lines[index];
	}
	
	/**
	 * Get number of lines in the message. Unless this is
	 * a multiline message, it should be 1. If it's a multiline
	 * message, it should be > 1
	 * 
	 * @return Number of lines in the message
	 */
	public int getNumberOfLines() {
		
		// If it's not a multiline code, it's one line
		if( !isMultilineCode() ) {
			return 1;
		}
		
		// If the text is null, that's bad
		if( text == null ) {
			return 0;
		}
		
		// Otherwise we count
		String[] lines = text.split( "\n" );
		return lines.length;
	}

	/**
	 * Does this code indicate that a disconnect is needed? Certain
	 * CODE value indicate that
	 * 
	 * @return True if this code's presence requires a disconnect
	 * of the client, falseotherwise
	 */
	public boolean requiresDisconnect() {
		
		if( code != null ) {
			// Or together all the 'you are going to be disconnected' codes here
			return hasSameCode(CODE.LOGIN_ATTEMPTS_EXCEEDED) || hasSameCode(CODE.ALREADY_LOGGED_IN);
		}
		
		// Get this far, must be fine
		return false;
	}
}
