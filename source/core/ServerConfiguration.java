package core;

/**
 * Game configurations.
 *
 * @author MGT Madness, created on 20-12-2013.
 */
public class ServerConfiguration {
	/**
	 * True, when developing.
	 */
	public static boolean DEBUG_MODE = true;

	/**
	 * Determines whether or not basic information should be benchmarked or not.
	 */
	public static boolean BENCHMARK = false;

	/**
	 * If true, the SQL network is mocked.
	 */
	public static boolean MOCK_SQL = false;

	/**
	 * Verify every login?
	 */
	public static boolean VERIFICATION_MODE = false;

	/**
	 * True if using dedicated server, will active email system & discord bot.
	 */
	public static boolean FORCE_DEDICATED_SERVER = false;

	/**
	 * Port used.
	 */
	public static int PORT = 43595;

	/**
	 * True, to send a packet to the client informing it to force update items every
	 * game tick.
	 */
	public static boolean FORCE_ITEM_UPDATE = false;

	/**
	 * True to enable bots.
	 */
	public static boolean ENABLE_BOTS = true;

	/**
	 * True to enable discord integration.
	 */
	public static boolean DISCORD = false;

	/**
	 * True, for stability testing.
	 */
	public static boolean STABILITY_TEST = false;

	/**
	 * True, to print out packet identities in-game.
	 */
	public static boolean SHOW_PACKETS = false;

	/**
	 * Maximum amount of packets received per game tick per player. Switching fast
	 * can be up to 10 packets per tick. This is used to prevent packet abusing
	 * where they send too many packets to crash the server.
	 */
	public static int MAXIMUM_PACKETS_PER_TICK = 18;

	/**
	 * Instant switching packet.
	 */
	public static boolean INSTANT_SWITCHING = false;

	/**
	 * Unique identity of the server. If client matches this value, accept
	 * connection.
	 */
	public final static int UID = 675_908_213;
}
