package drexel.edu.blackjack.client;

import drexel.edu.blackjack.client.in.ClientInputFromServerThread;
import drexel.edu.blackjack.client.out.ClientOutputToServerHelper;
import drexel.edu.blackjack.client.screens.AbstractScreen;
import drexel.edu.blackjack.server.ResponseCode;

public class DebugClientScreen extends AbstractScreen {

	public DebugClientScreen(BlackjackCLClient client,
			ClientInputFromServerThread thread,
			ClientOutputToServerHelper helper) {
		super(client, thread, helper);
	}

	@Override
	public void displayMenu() {
		System.out.println("Debug mode = ON. Messages will be shown here exactly as sent or received.");
	}

	@Override
	public void reset() {
	}

	@Override
	public void processMessage(ResponseCode code) {
		System.out.println(code.toString());
	}

	@Override
	public void handleUserInput(String str) {
		this.helper.sendRawText(str);
	}

}
