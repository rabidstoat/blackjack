/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - ClientSideGameStatus.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 * 
 * Purpose: This helper class is used to translate the raw protocol message
 * that is the response to the GAMESTATUS command into an object-oriented
 * view of the game status.
 ******************************************************************************/
package drexel.edu.blackjack.client.screens.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import drexel.edu.blackjack.server.BlackjackServer;
import drexel.edu.blackjack.server.ResponseCode;
import drexel.edu.blackjack.server.game.Game;
import drexel.edu.blackjack.server.game.GameState;
import drexel.edu.blackjack.util.BlackjackLogger;

/**
 * This class creates a nice object-oriented view of 
 * the game status. It takes a ResponseCode that represents
 * it, then parses it out, and is able to present information
 * in a nice manner to users of the class. Information
 * exprssed include items like the active and observer
 * players, their bets, their card hands, and the dealer
 * hands. Only the players need to be specified. The rest
 * of the data is optional, depending on the game state.
 * That is, if bets haven't been placed, they obviously
 * can't be shown.
 * <P>
 * <b>UI:</b> It's important to the UI because the UI uses 
 * the {@link #toString()} method to display information 
 * about the game status.
 * 
 * @author Jennifer
 */
public class ClientSideGameStatus {

	/*********************************************************
	 * Local variables here.
	 ********************************************************/
	
	// The outer map has a key that is the username
	// The inner map stores properties about the user, where the 
	// key is the property name, and the value is its value. 
	private Map<String,Map<String,String>> map = new HashMap<String,Map<String,String>>();
	
	// Our logger
	private final static Logger LOGGER = BlackjackLogger.createLogger(ClientSideGameStatus.class.getName()); 
	
	// Name for our 'player status' property
	private static final String PLAYER_STATUS	= "STATUS";
	
	// What stage the server reports the game being in
	private String gameStage = null;
	
	/*********************************************************
	 * Constructor
	 ********************************************************/
	
	/**
	 * The constructor parses out the response code into a nice
	 * map, that can be used later internally.
	 * 
	 * @param code Hopefully it's a game status response!
	 */
	public ClientSideGameStatus( ResponseCode code ) {

		if( code != null && code.getText() != null ) {
			
			// Lines in the multiline response
			String[] lines = code.getText().split( BlackjackServer.EOL );
			
			// Index 0 is the status code, index 1 should be GAMESTAGE
			if( lines.length >= 2 ) {
				StringTokenizer strtok = new StringTokenizer(lines[1]);
				if( strtok.hasMoreTokens() ) {
					// Ignore the GAMESTAGE line
					String expectedGamestage = strtok.nextToken();
					if( !expectedGamestage.equalsIgnoreCase(Game.GAMESTAGE_KEYWORD ) ) {
						LOGGER.warning( "We don't seem to have the expected GAMESTAGE line, uh oh." );
					} else if( !strtok.hasMoreTokens() ) {
						LOGGER.warning( "A gamestage was not actually given, uh oh." );
					} else {
						gameStage = strtok.nextToken();
					}
				} 
			}
			
			// Hopefully we read a game stage
			if( gameStage == null ) {
				LOGGER.warning( "Did not receive the expected GAMESTAGE leading line." );
			}
			
			// Index 0 is the status code, 1 is gamestage, and user info starts at line 2
			for( int i = 2; i < lines.length; i++ ) {
				// Pick out the words on the line
				StringTokenizer strtok = new StringTokenizer( lines[i] );
				
				// Used for printing logger warnings
				boolean parsed = false;
				if( strtok.hasMoreTokens() ) {
					String keyword = strtok.nextToken().trim();
					if( strtok.hasMoreTokens() ) {
						String username = strtok.nextToken().trim();
						
						// Good, we have a username, and we know what we're storing about them!
						// Get the map of properties to store for them, creating if necessary
						Map<String,String> propertyMap = map.get( username );
						if( propertyMap == null ) {
							// The first time we'd have to create it
							propertyMap = new HashMap<String,String>();
							map.put( username, propertyMap );
						}
						
						// Now we can figure out what to do with the keyword
						if( keyword.equalsIgnoreCase( Game.ACTIVE_PLAYER ) || 
								keyword.equalsIgnoreCase( Game.OBSERVER_KEYWORD ) ) {
							// This is the 'active versus observer' status
							propertyMap.put( PLAYER_STATUS, keyword );
							parsed = true;
						} else if( keyword.equalsIgnoreCase( Game.BET_KEYWORD ) ) {
							// Here, the third token is their bet
							if( strtok.hasMoreTokens() ) {
								propertyMap.put( keyword, strtok.nextToken() );
								parsed = true;
							}
						} else if( keyword.equalsIgnoreCase( Game.HAND_KEYWORD) ) {
							// Need to figure out their hand
							int index = lines[i].indexOf( username );
							if( index != -1 ) {
								String hand = lines[i].substring( username.length() + index + 1);
								if( hand != null && hand.length() > 0 ) {
									propertyMap.put( keyword, hand );
									parsed = true;
								}
							}
						}
					}
				} 
				
				if( !parsed ) {
					LOGGER.warning( "Could not parse the status line: " + lines[i] );
				}
			}
		}
	}

	
	/*********************************************************
	 * Public methods
	 ********************************************************/

	
	/**
	 * Gets a list of usernames about whom game status is known
	 * 
	 * @return A set of usernames. Guaranteed non-null. 
	 */
	public Set<String> getUsernames() {
		if( map != null ) {
			return map.keySet();
		}
		LOGGER.severe( "Our internal map was null. This should never happen!" );
		return new HashSet<String>();
	}

	/**
	 * Given a username, return a single line indicating infomation that
	 * is known about them.
	 * 
	 * @param username The username
	 * @return A single line representing what we know about that user.
	 * Guaranteed to be non-null
	 */
	public String getStatusForUser(String username) {
		
		// Hopefully we never return this!
		StringBuilder response = new StringBuilder( "A mysterious stranger is a player in the game." );
		
		// We'll eventually need this
		Map<String,String> propertyMap = (username == null ? null : map.get(username) );
		
		if( propertyMap != null ) {
			
			response = new StringBuilder();
			if( username.equals( GameState.DEALER_USERNAME ) ) {
				// If it's the dealer, start with this string
				String hand = propertyMap.get( Game.HAND_KEYWORD );
				if( hand != null ) {
					response.append( "The dealer holds: " );
					response.append( hand );
				}
			} else {
				// For players, though, start with their name
				response.append( username );
				response.append( " is " );
				
				// Actively playing or waiting to play or unknown?
				String status = propertyMap.get(PLAYER_STATUS);
				if( status == null ) {
					LOGGER.warning( "Could not find PLAYER_STATUS of " + username + " to report." );
					response.append( "in" );
				} else if( status.equals( Game.ACTIVE_PLAYER ) ) {
					response.append( "playing" );
				} else if( status.equals( Game.OBSERVER_KEYWORD ) ) {
					response.append( "watching" );
				} else {
					response.append( "at" );
				}
				response.append( " the game." );

				// Have they bet?
				String bet = propertyMap.get( Game.BET_KEYWORD );
				if( bet != null ) {
					response.append( " Their bet is $" );
					response.append( bet );
					response.append( "." );
				}
				
				// Do they have a hand?
				String hand = propertyMap.get( Game.HAND_KEYWORD );
				if( hand != null ) {
					response.append( " Their hand is: " );
					response.append( hand );
				}
			}
		}
		
		return response.toString();
	}


	/**
	 * <b>UI:</b> This returns a summary line about the game, that 
	 * says what state it is in and how many players there are.
	 * The user interface uses this to get a string that is 
	 * presented, unmodified, to end users.
	 * 
	 * @return A string representing a summary status, guaranteed
	 * to be non-null
	 */
	public String getSummaryStatus() {
		
		// Hopefully we never return this..
		StringBuilder response = new StringBuilder( "An internal error prevents us from viewing game info." );
		
		if( map != null ) {
			if( map.size() == 1 ) {
				response = new StringBuilder( "You are the only player" );
			} else {
				response = new StringBuilder( "There are " );
				response.append( map.size() );
				response.append( " players" );
			}
			response.append( " in this game." );
			
			// Something about the gamestage
			if( gameStage != null && gameStage.equals( GameState.STARTED_KEYWORD ) ) {
				response.append( " The game is in progress." );
			} else if( gameStage != null && gameStage.equals( GameState.NOT_STARTED_KEYWORD ) ) {
				response.append( "The game is starting soon." );
			} else {
				LOGGER.warning( "We didn't correctly parse out the gamestage, or else didn't recognize it." );
			}
		}
		
		return response.toString();
	}
}
