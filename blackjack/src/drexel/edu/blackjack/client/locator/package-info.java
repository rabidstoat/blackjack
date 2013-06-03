/*******************************************************************************
 * Functionality for the client using a server locator service
 * <P>
 * CS544 Computer Networks Spring 2013
 * <P>
 * 6/3/2013
 * <P>
 * <b>EXTRACREDIT</b>: This package provides the client-side support for the
 * extra credit problem of how to find a server to connect to without being
 * given a host. See the class files for more detail on how this is 
 * accomplished (though it's basically by multicasting a request to a specific
 * group and port, and then monitoring for any useful responses).
 * @author Jennifer Lautenschlager
 * @author Constantine Lazarakis
 * @author Carol Greco
 * @author Duc Anh Nguyen
 * @version 1.0
 ******************************************************************************/
package drexel.edu.blackjack.client.locator;