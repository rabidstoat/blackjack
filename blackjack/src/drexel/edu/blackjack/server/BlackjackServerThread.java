package drexel.edu.blackjack.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * One thread per client. Better comments needed.
 * @author Jennifer
 *
 */
public class BlackjackServerThread extends Thread {

	/**********************************************************
	 * Local variables go here
	 *********************************************************/
	
	// Need to keep track of client's socket for writing responses
	private Socket socket = null;
	
	/**********************************************************
	 * Constructor goes here
	 *********************************************************/

	public BlackjackServerThread( Socket socket ) {
		super( "BlackjackServerThread" );
		this.socket = socket;
	}

	/**********************************************************
	 * This is the meat of the thread, the run() method
	 *********************************************************/
	@Override
	public void run() {
		
		PrintWriter out		= null;
		BufferedReader in	= null;
		
		try {
			// This is used to write responses to the client
			out = new PrintWriter(socket.getOutputStream(), true);
			
			// And this is how responses are read from the client
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	
			// This is our protocol
			BlackjackProtocol protocol = new BlackjackProtocol();
	       
			// Keep reading single-line commands as long as we can
			String inputLine = in.readLine();
			while ( inputLine != null ) {
				
				// We pass it to our protcol to figure out what to do
				String outputLine = protocol.processInput(inputLine);
				
				// They give us the response to send back
				out.println(outputLine);
				
				// And we read another line
				inputLine = in.readLine();
	       }	
		} catch (IOException e) {
			System.err.println( "Something went wrong in our BlackjackServerThread for a client" );
			e.printStackTrace();
		} finally {
			// Always nice to clean up after ourselves
			try {
				in.close();
				out.close();
				socket.close();
			} catch (IOException e) {
				// At this point we're about to end anyway, so ignore it
			}
		}
	}
}
