/*******************************************************************************
 * Handles the client receiving messages from the server
 * <P>
 * CS544 Computer Networks Spring 2013
 * <P>
 * 5/26/2013
 * <P>
 * <b>Purpose:</b> This package has classes related to the client receiving
 * messages from the server. There is a thread that uses a reader on the client's
 * socket to constantly pull messages from the server, and a listener interface
 * so that the Observer pattern can be implemented.
 * @author Jennifer Lautenschlager
 * @author Constantine Lazarakis
 * @author Carol Greco
 * @author Duc Anh Nguyen
 * @version 1.0
 ******************************************************************************/
package drexel.edu.blackjack.client.in;