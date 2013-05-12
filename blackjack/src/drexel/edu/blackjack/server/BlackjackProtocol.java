package drexel.edu.blackjack.server;

import drexel.edu.blackjack.server.commands.BlackjackCommand;
import drexel.edu.blackjack.server.commands.CommandMetadata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Our protocol class is where we keep track of the user
 * that is attached to this connection, along with the
 * protocol state. The user is initially null until they
 * authenticate. Local variables connected to state are
 * also stored here.
 */
public class BlackjackProtocol {

	/*************************************************************
	 * These static variables are saved across all protocol
	 * classes that are instantiated.
	 ************************************************************/
	
	// Only want to initialize it once
	private static boolean isInitialized					= false;
	
	// This file should be in the classpath
	private static String COMMAND_FILENAME					= "commands.txt";
	
	// We keep a map of command classes to the command words that invoke them. We'll hash
	// on an all lowercase version of the command word, since our server is case-insensitive
	private static Map<String,BlackjackCommand> commands	= new HashMap<String,BlackjackCommand>();
	
	// This is the command to invoke if we can't find a valid command
	private static BlackjackCommand unknownCommand = null;

	// And our logger
	private final static Logger LOGGER = Logger.getLogger(BlackjackProtocol.class.getName()); 

	/*************************************************************
	 * Constructor goes  here
	 ************************************************************/
	
	/**
	 * We initialize the protocol with the list of commands
	 * that it knows how to handle, if that hasn't been done
	 * already. Since everyone can share that list, it's done
	 * as static variables.
	 */
	public BlackjackProtocol() {
		
		// Surely there is a way to do this in a config file
		LOGGER.setLevel(Level.INFO); 
		
		// Only initialize it once
		if( !isInitialized ) {
			LOGGER.info( "About to initialize the commands" );
			isInitialized = initializeCommands();
			if( !isInitialized ) {
				LOGGER.severe( "Could not initialize config file for the protocol. This is probably bad..." );
			}
		}
	}

	/*************************************************************
	 * Private methods go here
	 ************************************************************/

	private boolean initializeCommands() {
		
		// The file of commands should be on the classpath
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream( COMMAND_FILENAME );
		BufferedReader reader = new BufferedReader( new InputStreamReader(inputStream) );
		
		try {
			// Keep reading lines in the file to process
			String line = reader.readLine();
			while( line != null ) {
				
				// Process anyline that isn't blank or starting with '#'
				if( line.trim().length() > 0 && !line.startsWith("#") ) {
					
					// This should have a class name that we need to instantiate
					String classname = line.trim();

					try {
						// This uses java reflection to get the class object offf the name
						Class<?> c = Class.forName( classname );
						
						// Make sure class is something that extends BlackjackComand
						if( !BlackjackCommand.class.isAssignableFrom(c) ) {
							LOGGER.warning( c.getCanonicalName() + " did not subclass BlackjackCommand and couldn't be used." );
							LOGGER.warning( "Will continue, but that command won't work on the server." );
						} else {
						
							Object obj = c.newInstance();						
							BlackjackCommand command = (BlackjackCommand)obj;
							
							// Now we hash it using its command word -- unless it's a null, which
							// means it's the special "we don't know what to do" command.
							if( command.getCommandWord() == null ) {
								// Of course, if we already had something designated with a null, well, that's bad
								if( unknownCommand != null ) {
									LOGGER.warning( "Had two specified command define themselves as the 'unknown' command." );
									LOGGER.warning( "First one was: " + unknownCommand.getClass().getName() );
									LOGGER.warning( "Second one was: " + command.getClass().getName() );
									LOGGER.warning( "Will ignore the second one and use the first." );
								} else {
									// But if we didn't have something designated with a null, we do now
									unknownCommand = command;
								}
							} else {
								
								// Remember that it's case-insensitive, so convert to all lowercase
								String commandWord = command.getCommandWord().toLowerCase();
								
								// IF we already hashed it, that's bad
								if( commands.get( commandWord ) != null ) {
									BlackjackCommand originalCommand = commands.get( commandWord );
									LOGGER.warning( "Had two specified command define their command word as: " + commandWord );
									LOGGER.warning( "First one was: " + originalCommand.getClass().getName() );
									LOGGER.warning( "Second one was: " + command.getClass().getName() );
									LOGGER.warning( "Will ignore the second one and use the first." );
								} else {
									// At this point, it's unique in its command word, so put it in the map
									commands.put( commandWord, command );
								}
							}
						}
					} catch (InstantiationException e) {
						LOGGER.warning( "Could not instantiate class " + classname );
						LOGGER.warning( "Will continue, but that command won't work on the server." );
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						LOGGER.warning( "Could not access class " + classname );
						LOGGER.warning( "Will continue, but that command won't work on the server." );
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						LOGGER.warning( "Could not find class " + classname );
						LOGGER.warning( "Will continue, but that command won't work on the server." );
						e.printStackTrace();
					}
				}
				
				// Read the next line
				line = reader.readLine();
			}
		} catch (IOException e ) {
			LOGGER.severe( "Had a problem reading the command file. The server probably won't work." );
			e.printStackTrace();
			return false;
		}
		
		// Recap what was read in
		LOGGER.info( "We do " + (unknownCommand == null ? "NOT " : "") + "have an unknown command set." );
		LOGGER.info( "We have " + commands.size() + " commands loaded successfully." );
		// Assume if you got this far, that it's all good
		return true;
	}

	/*************************************************************
	 * Protected methods go here
	 ************************************************************/

	/**
	 * This is how we handle messages.
	 * 
	 * @param inputLine
	 * @return
	 */
	protected String processInput(String inputLine) {
		// This basically parses out the command word, and the parameters
		CommandMetadata metadata = new CommandMetadata(inputLine);

		// We'll keep whatever command we decide to use here
		BlackjackCommand command = null;

		// We use the command word to look up the command to use
		String commandWord = metadata.getCommandWord();
		if( commandWord == null ) {
			LOGGER.info( "Command word was null. That's pretty odd." );
		} else {
			// Make it lowercase as we're case-insensitive, and hashed them in lowercase
			commandWord = commandWord.toLowerCase();
			command = commands.get( commandWord );
		}
		
		// If we can't find it, we have to use our unknown command. Hope it's not null!
		if( command == null ) {
			command = unknownCommand;
		}
		
		// We are going to use this response if we had a null command, at this point
		String response = "Received an unknown command, but had no command handler defined for it.";
		if( command != null ) {
			// We really shouldn't pass in a null user, but it's okay for debugging
			response = command.processCommand( null, metadata );
		}
		
		// Whatever we have, we return
		return response;
	}

}
