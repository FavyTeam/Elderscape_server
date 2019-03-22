package network.connection;



import org.apache.mina.common.IoFilter;
import org.apache.mina.common.IoFilterAdapter;
import org.apache.mina.common.IoSession;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A {@link IoFilter} which blocks connections from connecting
 * at a rate faster than the specified interval.
 *
 * @author The Apache MINA Project (dev@mina.apache.org)
 * @version $Rev$, $Date$
 */
public class ConnectionThrottleFilter extends IoFilterAdapter {
	private long allowedInterval;

	private Map<InetAddress, Long> clients;

	private Map<InetAddress, Integer> counts;

	/**
	 * Constructor that takes in a specified wait time.
	 *
	 * @param allowedInterval The number of milliseconds a client is allowed to wait
	 * before making another successful connection
	 */
	public ConnectionThrottleFilter(long allowedInterval) {
		this.allowedInterval = allowedInterval;
		clients = Collections.synchronizedMap(new HashMap<InetAddress, Long>());
		counts = Collections.synchronizedMap(new HashMap<InetAddress, Integer>());
	}

	private InetAddress getAddress(IoSession io) {
		return ((InetSocketAddress) io.getRemoteAddress()).getAddress();
	}

	/**
	 * Method responsible for deciding if a connection is OK
	 * to continue
	 *
	 * @param session The new session that will be verified
	 * @return True if the session meets the criteria, otherwise false
	 */
	public boolean isConnectionOk(IoSession session) {
		InetAddress addr = getAddress(session);
		long now = System.currentTimeMillis();
		if (clients.containsKey(addr)) {
			long lastConnTime = clients.get(addr);
			if (now - lastConnTime < allowedInterval) {
				int c = 0;
				if (!counts.containsKey(addr))
					counts.put(addr, 0);
				else
					c = counts.get(addr) + 1;
				if (c >= 350)
					c = 0;
				counts.put(addr, c);
				return false;
			} else {
				clients.put(addr, now);
				return true;
			}
		} else {
			clients.put(addr, now);
			return true;
		}
	}

}
