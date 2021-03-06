/*******************************************************************************
 * Handles monitoring the client connections for idle connections
 * <P>
 * CS544 Computer Networks Spring 2013
 * <P>
 * 5/26/2013
 * <P>
 * <b>Purpose:</b> Various protocol states have different timeout periods where, 
 * if there has been no client activity for a set amount of time, the connection
 * is dropped. A daemon thread continually monitors connection threads that are
 * registered with it, for this purpose.
 * <P>
 * <b>STATEFUL:</b> All classes in this package are concerned with the stateful
 * requirement, as the timeout durations differ from state to state in the protocol.
 * <b>SECURITY:</b> Since every client connection consumes a port on the server
 * machine, along with a thread and other resources, there is only a set number of
 * client connections that a single machine can server. Removing idle client
 * connections is one way the server guards against a denial of service attack,
 * which might be possible if a malicious attacker kept opening valid client
 * connections and doing nothing with them, thus tying up system resources.
 *  
 * @author Jennifer Lautenschlager
 * @author Constantine Lazarakis
 * @author Carol Greco
 * @author Duc Anh Nguyen
 * @version 1.0
 ******************************************************************************/

package drexel.edu.blackjack.server.timeouts;