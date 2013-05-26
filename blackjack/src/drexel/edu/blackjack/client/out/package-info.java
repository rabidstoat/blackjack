/*******************************************************************************
 * Handles the client sending messages to the server
 * <P>
 * CS544 Computer Networks Spring 2013
 * <P>
 * 5/26/2013
 * <P>
 * <b>Purpose:</b> This package represents the single point where all messages
 * from the client to the server are sent, using a writer to the socket. There
 * is a listener so that any client class can register interest in the messages
 * sent, to implement the Observer design pattern.
 * @author Jennifer Lautenschlager
 * @author Constantine Lazarakis
 * @author Carol Greco
 * @author Duc Anh Nguyen
 * @version 1.0
 ******************************************************************************/
package drexel.edu.blackjack.client.out;