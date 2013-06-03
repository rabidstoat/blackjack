/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - BlackjackServer.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 * 
 * Purpose: Monitors an agreed upon port and multicast group to listen for
 * viable BJP servers broadcasting their location information.
 ******************************************************************************/
package drexel.edu.blackjack.client.locator;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

import drexel.edu.blackjack.server.locator.BlackjackLocatorThread;

/**
 * <b>EXTRACREDIT:</b> This class monitors, in a separate thread, an
 * agreed-upon multicast port and group, listening for any BJP servers
 * that might be broadcasting their location information. If it hears
 * of any, it alerts the host seeker by updating its host value
 * 
 * @author Jennifer
 *
 */
public class SeekerMonitor extends Thread {

	/************************************************************
	 * Local class variables
	 ***********************************************************/
	
	// Who we alert if we found something
	private BlackjackHostSeeker seeker = null;
	
	// What we found
	private String host = null;
		
	/************************************************************
	 * Constructor goes here
	 ***********************************************************/

	/**
	 * Simple constructor to record the host seeker we need to
	 * notify on hearing of a BJP server.
	 * @param seeker Who to notify
	 */
	public SeekerMonitor( BlackjackHostSeeker seeker ) {
		this.seeker = seeker;
	}

	/************************************************************
	 * The runner for the thread
	 ***********************************************************/

	@Override
	public void run() {
		
		MulticastSocket socket = null;
		InetAddress group = null;
		
		try {
			// Open the socket on the default port
			socket = new MulticastSocket(BlackjackLocatorThread.PORT);
			
			// Figure out the group to join
			group = InetAddress.getByName(BlackjackLocatorThread.MULTICAST_GROUP);
			socket.joinGroup( group );
			
			// Create a byte buffer for dealing with the data
			byte[] inputData	= new byte[BlackjackLocatorThread.DEFAULT_BUFFER_LENGTH];
			
			// Fall into an endless loop, reading data (unless we couldn't figure
			// out any host addresses and have no output data to send requesters)
			while( host == null ) {
				
				// For the incoming packet
				DatagramPacket inputPacket = new DatagramPacket( 
						inputData, inputData.length );
				
				// Read the next bit of input into it; not sure what happens if 
				// it's over 1024 bytes, though
				socket.receive( inputPacket );
				
				// I guess we use the default encoding to translate...
				String input = new String( inputPacket.getData() );
				
				// Is it something we're interested in?
				if( input != null && input.startsWith(BlackjackLocatorThread.BJP_LOCATOR_RESPONSE) ) {
					
					// Split up the response into space-delimited tokens
					String trimmedInput = input.trim();
					String[] tokens = trimmedInput.split(" ");
					
					// The address will be the 4th token
					if( tokens != null && tokens.length >= 4 ) {
						host = tokens[3].trim();
					}
				}		
			}
			
			// Alert our interested seeker that we found something
			seeker.setHost(host);
		} catch (SocketException e) {
			// Silently fail
		} catch (IOException e) {
			// Silently fail
		} finally {
			// Clean up after ourselves
			if( socket != null ) {
				if( group != null ) {
					try {
						socket.leaveGroup(group);
					} catch (IOException e) {
						// At this point, we just ignore it
					}
				}
				socket.close();
			}
		}
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}
}
