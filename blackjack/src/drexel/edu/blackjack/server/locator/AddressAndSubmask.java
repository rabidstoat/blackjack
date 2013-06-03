/*******************************************************************************
 * CS544 Computer Networks Spring 2013
 * 5/26/2013 - BlackjackProtocol.java
 * Group Members
 * o Jennifer Lautenschlager
 * o Constantine Lazarakis
 * o Carol Greco
 * o Duc Anh Nguyen
 * 
 * Purpose: Pairs an address together with a submask. That way, we can pass
 * in another address and use the mask to tell if they're on the same subnet.
 ******************************************************************************/
package drexel.edu.blackjack.server.locator;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
* EXTRACREDIT: Pairs an address together with a submask. That way, we can pass
* in another address and use the mask to tell if they're on the same subnet.
* We use this for tracking all the address/subnets that this BJP server
* is known under, for the purpose of returning the most appropriate one when
* addresses are received.
**/
public class AddressAndSubmask {
	
	/*********************************************************
	 * Local variables go here.
	 ********************************************************/
	
	private String subnetMask	= null;
	private String address		= null;
	
	/*********************************************************
	 * Constructor goes here.
	 ********************************************************/

	/**
	 * Pair an address and a subnet mask together.
	 * 
	 * @param address
	 * @param subnetMask
	 */
	public AddressAndSubmask(String address, String subnetMask) {
		super();
		this.address = address;
		this.subnetMask = subnetMask;
	}
	
	/*********************************************************************
	 * Public methods here.
	 ********************************************************************/

	/**
	 * @return the subnetMask
	 */
	public String getSubnetMask() {
		return subnetMask;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * Given the address here and its submask, do some
	 * math stuff to figure out if the input address is
	 * on the same subnet.
	 * 
	 * @param otherAddress What to compare
	 * @return True if on the same subnet, false otherwise
	 */
	public boolean isOnSameSubnet(String otherAddress) {
		
		boolean response = false;
		
		if( address != null && otherAddress != null ) {
			
			// Get both addresses and the mask as byte arrays
			try {
				byte[] address1 = InetAddress.getByName( address ) == null 
						? null 
						: InetAddress.getByName( address ).getAddress();
				byte[] address2 = InetAddress.getByName( otherAddress ) == null 
						? null 
						: InetAddress.getByName( otherAddress ).getAddress();
				byte[] mask = InetAddress.getByName( subnetMask) == null 
						? null 
						: InetAddress.getByName( subnetMask ).getAddress();
				
				// Make sure it's valid, if not, give up
				if( address1 != null && address2 != null && mask != null 
						&& address1.length == address2.length 
						&& address2.length == mask.length ) {
					
					// Now we become optimistic!
					response = true;
					
					// Compare bit by bit, making sure they're all the same
					for( int i = 0; i < address1.length; i++ ) {
						if( (address1[i] & mask[i]) != (address2[i] & mask[i]) ) {
							response = false;
						}
					}
				}
			} catch (UnknownHostException e) {
				// Not sure what to do here, except give up....
			}
		}
		
		return response;
	}
}
