/*******************************************************************************
 * Contains the client command-line user interface functionality
 * <P>
 * CS544 Computer Networks Spring 2013
 * <P>
 * 5/26/2013
 * <P>
 * <b>Purpose:</b> This package contains most of the code related to the
 * command-line client user interface. It's organized as classes that
 * represents user interface 'screens' which extend a base class that define
 * the functionality the 'screen' is required to implement. Mostly this is
 * related to displaying a menu, responding to user input (which is listened
 * to as it comes on a separate thread), and responding to server messages
 * (which again are listened to on another thread).
 * 
 * <b>STATEFUL:</b> The concrete classes in this package all deal with the
 * stateful requirement of the protocol specification. There are screens that
 * roughly correspond to different protocol states, and the messages that 
 * are sent to he server are the ones that are valid in that state.
 * 
 * <b>UI:</b> All of the classes in this package are involved in creating
 * the User Interface that is used. The end user never is exposed to raw
 * protocol messages, and uses a menu-based system as opposed to typing out
 * raw protocol commands.
 * 
 * @author Jennifer Lautenschlager
 * @author Constantine Lazarakis
 * @author Carol Greco
 * @author Duc Anh Nguyen
 * @version 1.0
 ******************************************************************************/
package drexel.edu.blackjack.client.screens;