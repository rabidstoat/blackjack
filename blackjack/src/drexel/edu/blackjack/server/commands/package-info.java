/*******************************************************************************
 * Base package for code related to the blackjack server, including the main class.
 * <P>
 * CS544 Computer Networks Spring 2013
 * <P>
 * 5/26/2013
 * <P>
 * <b>Purpose:</b> This package contains implementations of all the commands
 * defined in the protocol. There is a base abstract command to define common
 * functionality and methods all the classes must implement. Also included
 * is a class that provides an object-oriented view of the parameters sent with
 * a command.
 * 
 * <b>STATEFUL:</b> All of these commands are very involved with the stateful
 * nature of the protocol. The basic template of their processing algorithm
 * involves checking that the protocol is in a valid state before performing
 * any work. Also, the commands all advertise the states they are valid in 
 * through a method call on the abstract base class. 
 * Some of the commands explicitly change the protocol state in response to 
 * processing the command. Finally, the CapabilitiesCommand class
 * provides lists of capabilities that are dependent on the protocol's state.
 * 
 * @author Jennifer Lautenschlager
 * @author Constantine Lazarakis
 * @author Carol Greco
 * @author Duc Anh Nguyen
 * @version 1.0
 ******************************************************************************/
package drexel.edu.blackjack.server.commands;