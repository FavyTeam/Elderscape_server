package network.connection;


import core.ServerConfiguration;
import core.ServerConstants;
import org.apache.mina.common.IoSession;

import java.net.InetSocketAddress;
import java.util.ArrayList;

public class HostList {

	private static HostList list = new HostList();

	public static HostList getHostList() {
		return list;
	}

	/**
	 * Save all connected ip addresses to this arraylist, to be used for limiting connections per ip address to the server.
	 */
	public static ArrayList<String> connections = new ArrayList<String>();

	public boolean add(IoSession session) {
		String address = ((InetSocketAddress) session.getRemoteAddress()).getAddress().getHostAddress();
		int amount = countConnections(address);
		if (amount > (ServerConfiguration.DEBUG_MODE ? 2000 : ServerConstants.IPS_ALLOWED)) {
			return false;
		} else {
			return true;
		}
	}

	public static int countConnections(String address) {
		int amount = 1;

		// Start at 1 because the connection.add is one at the log-in content update. Not here.
		// So putting it at 1 will calculate correctly.

		for (int i = 0; i < HostList.connections.size(); i++) {
			if (HostList.connections.get(i).equals(address)) {
				amount++;
			}
		}
		return amount;
	}

	public void remove(IoSession session) {

		if (session.getAttribute("inList") != Boolean.TRUE) {
			return;
		}
	}

}
