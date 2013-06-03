/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - BlackjackServer.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 * 
 * Purpose: Isolates the logic related to looking for an available BJP server
 * on the LAN via UDP multicast messages.
 ******************************************************************************/
package drexel.edu.blackjack.client.locator;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import drexel.edu.blackjack.server.locator.BlackjackLocatorThread;

/**
 * <b>EXTRACREDIT:</b> Isolates the logic related to looking for an available 
 * BJP server on the LAN, by multicasting a UDP request for this information 
 * over a pre-established port and multicast group. It uses a back-off strategy 
 * to avoid flooding the network, and will eventually give up if it can't find
 * a host. Otherwise, it blindly accepts the first host that it receives.
 */
public class BlackjackHostSeeker {

	/************************************************************
	 * Local class variables
	 ***********************************************************/
	
	// If we use the locator service, this is our start delay in millisecond
	private static final int LOCATOR_SERVICE_START_DELAY	= 50;
	
	// And this is how many attempts we will make to query for it. Each
	// attempt, we double the time we wait
	private static final int LOCATOR_SERVICE_REPETITIONS	= 10;
	
	// This is where we will store the host we found
	private String host = null;
	
	/************************************************************
	 * Class methods go here
	 ***********************************************************/
	
	/**
	 * EXTRACREDIT: Here we try to find a local blackjack server on the
	 * LAN. We do this by broadcasting a UDP request message on the
	 * default locator service port, and monitor for a response. We
	 * use a backoff approach to avoid flooding the network. The start
	 * time, and number of iterations to attempt, are defined as static
	 * variables. Every time we re-request, we double the wait time.
	 * 
	 * @return The blackjack server address as a string if we found it,
	 * or null if we timed out trying to locate it.
	 * @throws IOException 
	 */
	public String tryToLocateServerOnLAN() throws IOException {
		
		// Give the user an idea of how long it will look
		reportOnLocatorDelay();

		// Bind to a multicast socket
		MulticastSocket socket = new MulticastSocket(BlackjackLocatorThread.PORT);
		InetAddress group = InetAddress.getByName(BlackjackLocatorThread.MULTICAST_GROUP);
		socket.joinGroup( group );

		// Launch a thread to monitor for responses
		SeekerMonitor monitor = new SeekerMonitor(this,socket);
		monitor.start();
		
		// Immediately do the first broadcast
		broadcastLocatorRequest( socket, group );

		// Then enter a loop
		long currentWait = (long)LOCATOR_SERVICE_START_DELAY;
		for( int i = 1; i < LOCATOR_SERVICE_REPETITIONS && host == null; i++ ) {
			
			// Pause
			try {
				Thread.sleep( currentWait );
			} catch (InterruptedException e) {
				// Wakey wakey
			}
			
			// Send the request
			broadcastLocatorRequest( socket, group );
			
			// And double the wait
			currentWait = currentWait * 2;
		}
		
		// At this point, we give up on the monitor
		monitor.setStopped(true);
		monitor.interrupt();
		
		// Close out the socket stuff best we can
		if( socket != null ) {
			socket.close();
		}
		
		return host;
	}

	/**
	 * Broadcasts a multicast request over port 55556,
	 * asking for the whereabouts of a BJP server.
	 * 
	 * @param socket The datagram socket to send it on
	 * @param group The multicast group to send to
	 * @throws IOException 
	 */
	private void broadcastLocatorRequest(DatagramSocket socket, InetAddress group) throws IOException {
		
		// Get the bytes of the message
		byte[] byteBuffer = BlackjackLocatorThread.BJP_LOCATOR_REQUEST.getBytes();
		
		// Shove it in a datagram packet
		DatagramPacket packet = new DatagramPacket( byteBuffer, byteBuffer.length, group, BlackjackLocatorThread.PORT );
		
		// And send
		socket.send( packet );
	}

	/**
	 * EXTRACREDIT: This is just a nicety, it tells the user how long
	 * it will spend looking for a blackjack server.
	 */
	private void reportOnLocatorDelay() {
		// First, calculate how long we will attempt to look for the service
		long currentWait = (long)LOCATOR_SERVICE_START_DELAY;
		long totalWait = currentWait;
		for( int i = 2; i < LOCATOR_SERVICE_REPETITIONS; i++ ) {
			currentWait = currentWait * 2;
			totalWait += currentWait;
		}
		
		// Report how long we wait
		System.out.println( "Will spend roughly " + (int)(totalWait/1000) +
				" seconds looking for a local blackjack server." );
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}
	
}