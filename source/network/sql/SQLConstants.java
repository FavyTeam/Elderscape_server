package network.sql;

/**
 * All table names added here for easy renaming.
 * @author MGT Madness, created on 13-07-2018.
 */
public enum SQLConstants {

	OSRS_BOT,
	STATS_CONSOLE_PRINT_CRASH,
	STATS_CONSOLE_PRINT_GAME,
	STATS_PAYMENT_DAILY_TOTAL,
	STATS_PAYMENT_LATEST,
	STATS_PLAYER_ONLINE,
	STATS_TIME,
	STATS_TOPLIST,
	STATS_TOPLIST_ANTICAPTCHA_BALANCE,
	STATS_UNIQUE_IDENTIFIER_ANALYSIS,
	STATS_UNIQUE_IDENTIFIER_ANALYSIS_ALL,
	STATS_DOUBLE_ITEMS_NPC,
	STATS_DOUBLE_ITEMS_NPC_DAILY,
	STATS_DOUBLE_ITEMS_NPC_PLAYER,
	STATS_GIVEAWAY,
	STATS_PAID_YOUTUBER,
	STATS_OSRS_HISTORY,
	LOGS_LONG_STRING,
	OUTPUT_LOG;

	public static String getServerSchemaTable(SQLConstants entry) {
		return SCHEMA + entry.toTableName();
	}

	public String toTableName() {
		return name().toLowerCase();
	}

	public static String SCHEMA = "server.";
}
