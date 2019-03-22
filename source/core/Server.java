package core;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.apache.mina.common.IoAcceptor;
import org.apache.mina.transport.socket.nio.SocketAcceptor;
import org.apache.mina.transport.socket.nio.SocketAcceptorConfig;

import com.google.common.collect.ImmutableList;
import com.zaxxer.hikari.HikariConfig;

import core.benchmark.GameBenchmark;
import core.maintick.Task;
import core.maintick.TaskScheduler;
import game.bot.BotManager;
import game.content.achievement.AchievementDefinitions;
import game.content.clanchat.ClanChatHandler;
import game.content.clanchat.ClanChatStartUp;
import game.content.commands.NormalCommand;
import game.content.commands.newcommandsystem.CommandHandler;
import game.content.degrading.DegradingItemJSONLoader;
import game.content.donator.MysteryBox;
import game.content.highscores.Highscores;
import game.content.highscores.HighscoresDaily;
import game.content.highscores.HighscoresHallOfFame;
import game.content.highscores.HighscoresInterface;
import game.content.interfaces.NpcDoubleItemsInterface.DoubleItemsLog;
import game.content.interfaces.donator.DonatorMainTab;
import game.content.interfaces.donator.DonatorMainTab.AccountOffersGiven;
import game.content.interfaces.donator.DonatorShop;
import game.content.item.ItemInteractionMap;
import game.content.minigame.WorldMinigameCodeLoader;
import game.content.minigame.WorldMinigameManager;
import game.content.minigame.lottery.Lottery;
import game.content.minigame.zombie.ZombieWaveInstance;
import game.content.miscellaneous.Announcement;
import game.content.miscellaneous.Artefacts;
import game.content.miscellaneous.ClaimPrize;
import game.content.miscellaneous.DiceSystem;
import game.content.miscellaneous.GiveAway;
import game.content.miscellaneous.GuideBook;
import game.content.miscellaneous.RevenantCavesNpc;
import game.content.miscellaneous.ServerShutDownUpdate;
import game.content.miscellaneous.SnowPile;
import game.content.miscellaneous.Teleport;
import game.content.miscellaneous.TeleportInterface;
import game.content.miscellaneous.Web;
import game.content.miscellaneous.WelcomeMessage;
import game.content.miscellaneous.YoutubePaid;
import game.content.miscellaneous.YoutubeRank;
import game.content.quest.Quest;
import game.content.quest.tab.InformationTab;
import game.content.shop.ShopHandler;
import game.content.skilling.Farming;
import game.content.skilling.Firemaking;
import game.content.skilling.fishing.FishingOld;
import game.content.staff.StaffActivity;
import game.content.staff.StaffManagement;
import game.content.staff.StaffPresence;
import game.content.title.TitleDefinitions;
import game.content.tradingpost.TradingPost;
import game.content.tradingpost.TradingPostItems;
import game.content.wildernessbonus.KillReward;
import game.content.worldevent.BloodKey;
import game.content.worldevent.Tournament;
import game.content.worldevent.WorldEvent;
import game.entity.attributes.PermanentAttributeKeyManager;
import game.item.BloodMoneyPrice;
import game.item.GlobalItemSpawn;
import game.item.ItemDefinition;
import game.item.ItemHandler;
import game.log.CoinEconomyTracker;
import game.log.FlaggedData;
import game.log.GameTickLog;
import game.log.NewPlayerIpTracker;
import game.npc.CustomNpcMap;
import game.npc.NpcHandler;
import game.npc.combat.DamageQueue;
import game.npc.pet.Pet;
import game.object.clip.ObjectDefinitionServer;
import game.object.clip.Region;
import game.object.custom.Door;
import game.object.custom.ObjectManagerServer;
import game.object.lever.Lever;
import game.player.PlayerHandler;
import game.player.event.CycleEventHandler;
import game.player.event.impl.IdentifierSetAnalysisEvent;
import game.player.pet.PlayerPetManager;
import game.player.punishment.Ban;
import game.player.punishment.BannedData;
import game.player.punishment.Blacklist;
import game.player.punishment.DuelArenaBan;
import game.player.punishment.IpMute;
import game.player.punishment.RagBan;
import network.connection.ConnectionHandler;
import network.connection.ConnectionThrottleFilter;
import network.connection.DonationManager;
import network.connection.InvalidAttempt.AutoBlacklisted;
import network.connection.VoteManager;
import network.packet.PacketHandler;
import network.sql.MockSQLNetwork;
import network.sql.SQLNetwork;
import network.sql.batch.impl.LongStringBatchUpdateEvent;
import network.sql.batch.impl.OutputBatchUpdateEvent;
import network.sql.create.SQLTableCreationHandler;
import tools.discord.api.DiscordBot;
import utility.CharacterBackup;
import utility.ChargebackPlayerAutoJail;
import utility.DatedErrorPrintStream;
import utility.DatedPrintStream;
import utility.DonationChargebackDb;
import utility.EmailSystem;
import utility.ErrorManager;
import utility.FileUtility;
import utility.HackLogHistory;
import utility.HighestPlayerCount;
import utility.LeakedSourceApi;
import utility.LoadTextWidth;
import utility.Misc;
import utility.OsBotCommunication;
import utility.SimpleTimer;
import utility.WebsiteLogInDetails;
import utility.WebsiteRead;

/**
 * Main launch of the Server.
 *
 * @author Sanity
 * @author Graham
 * @author Blake
 * @author Ryan Lmctruck30
 * @author Sponjebubu
 * @author MGT Madness
 */
public class Server {

	private static SQLNetwork sqlNetwork;

	private static IoAcceptor acceptor;

	private static ConnectionHandler connectionHandler;

	private static ConnectionThrottleFilter throttleFilter;

	public static ItemHandler itemHandler = new ItemHandler();

	public static PlayerHandler playerHandler = new PlayerHandler();

	public static NpcHandler npcHandler = new NpcHandler();

	public static ShopHandler shopHandler = new ShopHandler();

	public static ObjectManagerServer objectManager = new ObjectManagerServer();

	public static ClanChatHandler clanChat = new ClanChatHandler();

	public static final TaskScheduler scheduler = new TaskScheduler();

	public static CommandHandler commands = new CommandHandler();

	private static WorldMinigameManager minigameManager = new WorldMinigameManager(
			new WorldMinigameCodeLoader().load());

	/**
	 * True, if a server update countdown timer is active.
	 */
	public static boolean UpdateServer;

	/**
	 * How long the server has been online for.
	 */
	public static long timeServerOnline;

	/**
	 * Read the client version from the website, to warn the player of outdated
	 * client.
	 */
	public static int clientVersion;

	public static String speedDebug = "";

	/**
	 * Launch the server.
	 */
	public static void main(java.lang.String args[]) throws NullPointerException, IOException {
		serverArguments(args);
		new SimpleTimer();
		System.setOut(new DatedPrintStream(System.out));
		System.setErr(new DatedErrorPrintStream(System.err));
		ErrorManager.loadErrorFile("./backup/logs/system log/error/history.txt",
				"./backup/logs/system log/error/error");
		shutDownButtons();
		long start = System.currentTimeMillis();
		timeServerOnline = start;
		Misc.print("Server starting up...");
		new GameBenchmark("website-login-details", WebsiteLogInDetails::readLatestWebsiteLogInDetails, 100,
				TimeUnit.MILLISECONDS, ServerConfiguration.BENCHMARK).execute();
		loadSqlNetwork();
		sqlNetwork.start();
		sqlNetwork.blockingTest();
		Server.loadSystems();
		initiateConnections();
		gameTick();

		long timeTaken = (System.currentTimeMillis() - start);
		if (timeTaken > 4000) {
			Misc.print(speedDebug);
		}
		timeTaken /= 100;
		String time = Long.toString(timeTaken);
		if (time.length() == 2) {
			time = time.substring(0, 1) + "." + time.substring(1);
		} else if (time.length() == 3) {
			time = time.substring(0, 2) + "." + time.substring(2);
		}
		Misc.print("Server finished loading: " + time + " seconds.");
		Misc.print("Client version: " + clientVersion);
		Misc.print("Port: " + ServerConfiguration.PORT);
		Misc.print("Mode: " + (GameType.isOsrsEco() ? "Economy" : GameType.isPreEoc() ? "Pre Eoc" : "Pvp"));
		Misc.print((ServerConfiguration.DEBUG_MODE ? "Running as debug mode" : "Running as live mode"));
		Teleport.loadPreEocTeleportsDebug();
	}

	private static void serverArguments(String[] args) {
		String combinedArguments = "";
		for (int index = 0; index < args.length; index++) {
			combinedArguments = combinedArguments + args[index];
		}

		if (args.length > 0) {
			List<String> argumentsAsList = Arrays.asList(args);

			int port = Integer.parseInt(args[0]);

			ServerConfiguration.PORT = port;
			if (combinedArguments.contains("ECO")) {
				GameType.gameType = GameType.OSRS_ECO;
			} else if (combinedArguments.contains("PVP")) {
				GameType.gameType = GameType.OSRS_PVP;

			} else if (combinedArguments.contains("PRE_EOC")) {
				GameType.gameType = GameType.PRE_EOC;

			}
			if (combinedArguments.contains("LIVE")) {
				ServerConfiguration.DEBUG_MODE = false;
			}

			if (argumentsAsList.contains("BENCHMARK")) {
				ServerConfiguration.BENCHMARK = true;
			}
			if (!combinedArguments.contains("SQL") && !combinedArguments.contains("HIKARI_SQL")) {
				ServerConfiguration.MOCK_SQL = true;
			}
		}

		if (FileUtility.fileExists("dedicated_server.txt")) {
			ServerConfiguration.FORCE_DEDICATED_SERVER = true;
		}
	}

	private static void loadSqlNetwork() {
		Properties properties = new Properties();

		Path path = Paths.get("data", "sql", "sql.properties");
//		Misc.print("Mysql file : " + path.toString());

//		try {
//			Misc.createFileIfAbsent(path, "dataSource.user=root", "dataSource.password=",
//					"dataSource.databaseName=exturia",
//					"dataSourceClassName=com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource",
//					"dataSource.url=jdbc:mysql://localhost:3306/exturia?useSSL=false");
//		} catch (IOException e) {
//			if (!Files.exists(path)) {
//				throw new RuntimeException("Unable to create sql.properties default file.", e);
//			}
//		}

		try (BufferedReader reader = Files.newBufferedReader(path)) {
			properties.load(reader);
//			Misc.print("mysql infor => " + properties.toString());
		} catch (IOException ioe) {
			throw new RuntimeException("Unable to load properties: " + properties, ioe);
		}

		if (ServerConfiguration.DEBUG_MODE && ServerConfiguration.MOCK_SQL) {
			sqlNetwork = new MockSQLNetwork(new HikariConfig(properties));
			return;
		}
		sqlNetwork = new SQLNetwork(new HikariConfig(properties), 200, TimeUnit.MILLISECONDS, 20);
		Misc.print("Mysql Connector : " + sqlNetwork.toString());
	}

	/**
	 * The tasks that occur during a game tick.
	 */
	private static final List<GameTickTask> GAME_TICK_TASKS = ImmutableList
			.copyOf(Arrays.asList(new GameTickTask(GameTickLog.class, "loopDebugPart1", GameTickLog::loopDebugPart1),
					new GameTickTask(NpcHandler.class, "clearNpcFlags", npcHandler::clearNpcFlags),
					new GameTickTask(PlayerHandler.class, "packetProcessing", playerHandler::packetProcessing),
					new GameTickTask(CycleEventHandler.class, "cycle", CycleEventHandler.getSingleton()::cycle),
					new GameTickTask(ItemHandler.class, "itemGameTick", itemHandler::itemGameTick),
					new GameTickTask(DamageQueue.class, "execute", DamageQueue::execute),
					new GameTickTask(NpcHandler.class, "npcGameTick", npcHandler::npcGameTick),
					new GameTickTask(PlayerHandler.class, "playerGameTick", playerHandler::playerGameTick),
					new GameTickTask(ObjectManagerServer.class, "objectGameTick", objectManager::objectGameTick),
					new GameTickTask(WorldEvent.class, "worldEventTick", WorldEvent::worldEventTick),
					new GameTickTask(CharacterBackup.class, "backUpSystem", CharacterBackup::backUpSystem),
					new GameTickTask(ServerCrashAlert.class, "gameTickLoop", ServerCrashAlert::gameTickLoop),
					new GameTickTask(WorldMinigameManager.class, "minigameManager", minigameManager::cycle),
					new GameTickTask(GameTickLog.class, "loopDebugPart2", GameTickLog::loopDebugPart2)));

	static class GameTickTask {

		final Class<?> identifyingClass;
		final String identifyingMethod;
		final Runnable task;

		GameTickTask(Class<?> identifyingClass, String identifyingMethod, Runnable task) {
			this.identifyingClass = identifyingClass;
			this.identifyingMethod = identifyingMethod;
			this.task = task;
		}

		public Class<?> getIdentifyingClass() {
			return identifyingClass;
		}

		public String getIdentifyingMethod() {
			return identifyingMethod;
		}

		public Runnable getTask() {
			return task;
		}
	}

	/**
	 * The main game tick.
	 */
	public static void gameTick() {
		scheduler.schedule(new Task() {
			@Override
			protected void execute() {
				if (ServerConfiguration.DEBUG_MODE) {
					GAME_TICK_TASKS.forEach(task -> {
						// any task that takes 60ms or longer
						// TODO cache these GameBenchmark objects, we're creating a good amount of them
						new GameBenchmark(
								String.format("%s#%s", task.getIdentifyingClass(), task.getIdentifyingMethod()),
								task.task, 60, TimeUnit.MILLISECONDS).execute();
					});
				} else {
					GAME_TICK_TASKS.forEach(task -> task.getTask().run());
				}
			}
		});
	}

	public static int index = 0;

	private static void shutDownButtons() {
		if (ServerConfiguration.DEBUG_MODE) {
			return;
		}
		JFrame frame = new JFrame("Control panel");
		frame.setResizable(false);
		frame.setSize(286, 96);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel panel = new JPanel();

		JButton button = new JButton("Save & Close");
		panel.add(button);
		button.addActionListener(new Action1());

		button = new JButton("Save all logs");
		panel.add(button);
		button.addActionListener(new Action2());

		button = new JButton("Update version & pass");
		panel.add(button);
		button.addActionListener(new Action3());

		frame.add(panel);
		frame.setVisible(true);

	}

	private static String textCombined = "";

	static JTextArea textArea = new JTextArea(6, 20);

	public static boolean saveAndClosedClicked;

	public static int saveTimer = 20;

	static class Action1 implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			saveAndCloseAction("JFrame Close");
		}
	}

	public static void saveAndCloseAction(String reason) {
		if (saveAndClosedClicked) {
			Misc.print("You have already clicked on Save & Close!");
			return;
		}
		saveAndClosedClicked = true;
		ServerShutDownUpdate.serverRestartContentUpdate(reason, false, true);
		Misc.print("Players online: " + PlayerHandler.getRealPlayerCount());

	}

	static class Action2 implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			ServerShutDownUpdate.serverRestartContentUpdate("JFrame Save", false, false);
		}
	}

	static class Action3 implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			readDataFileFromWebsite();
			WebsiteLogInDetails.readLatestWebsiteLogInDetails();
			Misc.print("JFrame used: Read data.txt from website and dedicated_server_config folder from server.");
		}
	}

	static class TextInputField implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			textCombined = textCombined + e.getActionCommand() + "\n";
			textArea.setText(textCombined);
		}
	}

	/**
	 * Initiate all the connections.
	 */
	private static void initiateConnections() {
		acceptor = new SocketAcceptor();
		connectionHandler = new ConnectionHandler();
		SocketAcceptorConfig sac = new SocketAcceptorConfig();
		sac.getSessionConfig().setTcpNoDelay(false);
		sac.setReuseAddress(true);
		sac.setBacklog(100);
		throttleFilter = new ConnectionThrottleFilter(ServerConstants.CONNECTION_DELAY);
		sac.getFilterChain().addFirst("throttleFilter", throttleFilter);
		try {
			acceptor.bind(new InetSocketAddress(ServerConfiguration.PORT), connectionHandler, sac);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void readDataFileFromWebsite() {
		WebsiteRead.forceUpdate = true;
		readWebsiteClientVersion();
	}

	public static void restart() {
		File execute = new File("server_jar.bat");
		try {
			Desktop.getDesktop().open(execute);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Change all ServerConfiguration settings to live version for players if debug
	 * mode is off. And also read the client version from the website if live
	 * version.
	 */
	public static void setServerConfiguration() {
		if (ServerConfiguration.DEBUG_MODE) {
			return;
		}
		ServerConfiguration.DISCORD = false;
		ServerConfiguration.ENABLE_BOTS = true;
		ServerConfiguration.FORCE_ITEM_UPDATE = false;
		ServerConfiguration.INSTANT_SWITCHING = false;
		ServerConfiguration.SHOW_PACKETS = false;
		ServerConfiguration.STABILITY_TEST = false;
	}

	private static void readWebsiteClientVersion() {
		if (ServerConfiguration.DEBUG_MODE) {
			return;
		}
		WebsiteRead newThread = new WebsiteRead();
		Thread thread = new Thread(newThread);
		thread.start();
	}

	/**
	 * Load all systems.
	 */
	public static void loadSystems() throws IOException {
		boolean printTime = false;
		if (!ServerConfiguration.DEBUG_MODE) {
			FileUtility.addLineOnTxt("backup/logs/system log/save log.txt", Misc.getDateAndTime() + " Booted.");
		}
		setServerConfiguration();
		long time = System.currentTimeMillis();
		ObjectDefinitionServer.loadConfig();
		if (printTime) {
			speedDebug = "Object definitions: " + (System.currentTimeMillis() - time);
		}
		time = System.currentTimeMillis();
		Farming.loadFarmingSettings();
		new GameBenchmark("region", Region::load, 100, TimeUnit.MILLISECONDS, ServerConfiguration.BENCHMARK).execute();
		if (printTime) {
			speedDebug = speedDebug + "\nRegion: " + (System.currentTimeMillis() - time);
		}
		time = System.currentTimeMillis();
		new GameBenchmark("loading-items", ItemDefinition::loadItemDefinitionsAll, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("loading-degrading-items", DegradingItemJSONLoader::new, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("blood-money-price", BloodMoneyPrice::loadBloodMoneyPrice, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("highscore-daily", () -> HighscoresDaily.getInstance().readDailyHighscoresType(), 100,
				TimeUnit.MILLISECONDS, ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("coin-economy-tracker", CoinEconomyTracker::readCoinEconomyLog, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("fishing-fill-current", FishingOld::fillCurrentFishingSpots, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("highscores", Highscores::initiateHighscoresInstance, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("blacklist", Blacklist::loadStartUpBlacklistedData, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("global-item", GlobalItemSpawn::initialize, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("welcome-message", WelcomeMessage::loadWelcomeMessage, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("custom-npc-map", () -> CustomNpcMap.getSingleton().load(), 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("item-interactions", () -> ItemInteractionMap.getSingleton().load(), 100,
				TimeUnit.MILLISECONDS, ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("npc-handler", NpcHandler::loadNpcData, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("shop-handler", ShopHandler::loadShops, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("plugin", Plugin::load, 100, TimeUnit.MILLISECONDS, ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("quest", Quest::loadQuests, 100, TimeUnit.MILLISECONDS, ServerConfiguration.BENCHMARK)
				.execute();
		new GameBenchmark("packet-handler", PacketHandler::defaultFlaggedPlayers, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("achievements", AchievementDefinitions::loadAllAchievements, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("title-definition", TitleDefinitions::loadAllTitles, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("bot-manager", BotManager::logInBots, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("clan-chat-startup", ClanChatStartUp::loadClanChatStartUp, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("teleport-interface", TeleportInterface::teleportStartUp, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("highsores-hof", HighscoresHallOfFame::loadHallOfFameData, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("new-player-ip-tracker", NewPlayerIpTracker::loadNewPlayerIpTrackerFiles, 100,
				TimeUnit.MILLISECONDS, ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("zombie-wave-instance", ZombieWaveInstance::loadZombieContent, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("claim-prize", ClaimPrize::loadFile, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("guide-book", GuideBook::loadGuideDataFile, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("firemaking", Firemaking::defaultFiremakingSpots, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("bloodkey", BloodKey::bloodKeyInitialize, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("tournament", Tournament::tournamentStartUp, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("ipmute", IpMute::readIpMuteLog, 100, TimeUnit.MILLISECONDS, ServerConfiguration.BENCHMARK)
				.execute();
		new GameBenchmark("ban", Ban::readBanLog, 100, TimeUnit.MILLISECONDS, ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("ragban", RagBan::readBanLog, 100, TimeUnit.MILLISECONDS, ServerConfiguration.BENCHMARK)
				.execute();
		new GameBenchmark("loadAutoBlacklist", AutoBlacklisted::loadAutoBlacklist, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("youtube-rank", YoutubeRank::readYoutuberRanks, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("pet-load", Pet::loadPetsToClaim, 100, TimeUnit.MILLISECONDS, ServerConfiguration.BENCHMARK)
				.execute();
		new GameBenchmark("duel-arena-ban", DuelArenaBan::readDuelBans, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("kill-reward", KillReward::readKillTypeLog, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("flagged-data", FlaggedData::loadFlaggedData, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("donation-manager-donation", DonationManager::readDailyDonationsProgress, 100,
				TimeUnit.MILLISECONDS, ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("donation-manager-osrs", DonationManager::readDailyOsrsProgress, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("donation-manager-bmt", DonationManager::readDailyBmtPayments, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("donation-manager-custom", DonationManager::readDailyCustomPayments, 100,
				TimeUnit.MILLISECONDS, ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("lever", Lever::readLeverObjectData, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("tournament", Tournament::loadEventTimes, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("dice", DiceSystem::readDepositVaultFile, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("ragban-clear-old", RagBan::clearOldRagBanList, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("trading-post-offers", TradingPost::readTradingPostOffers, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("trading-post-items", TradingPostItems::readTradingPostItems, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("donation-manager-osrs-balance", DonationManager::readCurrentOsrsBalance, 100,
				TimeUnit.MILLISECONDS, ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("doors", Door::loadDoorData, 100, TimeUnit.MILLISECONDS, ServerConfiguration.BENCHMARK)
				.execute();
		new GameBenchmark("highest-player-count", HighestPlayerCount::readHighestPlayerCountFiles, 100,
				TimeUnit.MILLISECONDS, ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("announcement-game-tick", Announcement::announcementGameTick, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("announcement-event", Announcement::donateAnnouncementEvent, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("announcement-pending-event", Announcement::announcementPendingEvent, 100,
				TimeUnit.MILLISECONDS, ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("commands", () -> commands.loadCommands(), 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("load-text", LoadTextWidth::LoadTextWidthFile, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("lottery", Lottery::readLotteryFiles, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("mystery-box", MysteryBox::loadMysteryBoxFiles, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("chargeback", ChargebackPlayerAutoJail::loadNotifyList, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("staff-management", StaffManagement::readStaffManagementFiles, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("donation-chargeback", DonationChargebackDb::readFile, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("donation-task",
				() -> DonationChargebackDb.donationChargebackTimer.schedule(DonationChargebackDb.donationChargebackTask,
						0, Misc.getMinutesToMilliseconds(2)),
				100, TimeUnit.MILLISECONDS, ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("read-client-version", Server::readWebsiteClientVersion, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("information-tab", InformationTab::loadQuestTabInformationTabConfig, 100,
				TimeUnit.MILLISECONDS, ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("staff-presence", StaffPresence::readStaffPresence, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("staff-activity", StaffActivity::readStaffActivity, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("vote-manager", VoteManager::readVoteSettings, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("blacklist", Blacklist::readLatestFloodIps, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("banned", BannedData::readBannedData, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("hack-log", HackLogHistory::readHackLog, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("snowpile", SnowPile::readLocations, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("read-web-data", Web::readWebData, 100, TimeUnit.MILLISECONDS, ServerConfiguration.BENCHMARK)
				.execute();
		new GameBenchmark("artfacts", Artefacts::loadPkingLootData, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("normal-command", NormalCommand::loadCommandsList, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("highscore-interface", HighscoresInterface::loadHighscoresTextCategory, 100,
				TimeUnit.MILLISECONDS, ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("staff-management-load-signup", StaffManagement::loadSignUpData, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("donator-shop-file", DonatorShop::loadDonatorShopFile, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("donator-main-tab", DonatorMainTab::readItemsOnOfferFile, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("donation-manager-latest 07", DonationManager::readLatest07Rates, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("account-offers", AccountOffersGiven::readAccountOffersGivenFile, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("revenant-caves", RevenantCavesNpc::storeEmblemTraderNpcIndex, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("leaked-source-api", LeakedSourceApi::loadFromFile, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("permanent-key-manager", () -> PermanentAttributeKeyManager.getSingleton().populate(), 100,
				TimeUnit.MILLISECONDS, ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("osbot-communication-task", OsBotCommunication::startScheduledTask, 100,
				TimeUnit.MILLISECONDS, ServerConfiguration.BENCHMARK).execute();
		new GameBenchmark("osbot-file-read", OsBotCommunication::readFile, 100, TimeUnit.MILLISECONDS,
				ServerConfiguration.BENCHMARK).execute();
		if (!ServerConfiguration.MOCK_SQL) {
			sqlNetwork.reschedule(TimeUnit.MILLISECONDS, 50);

			SQLTableCreationHandler sqlTableCreationHandler = SQLTableCreationHandler.getInstance();

			sqlTableCreationHandler.addAll(Paths.get("data/", "sql", "create"));

			sqlTableCreationHandler.updateBlocking(50, TimeUnit.MILLISECONDS);

			sqlNetwork.reschedule();
		}
		CycleEventHandler.getSingleton().addEvent(new Object(), IdentifierSetAnalysisEvent.getInstance(), 5);
		YoutubePaid.readPaidYoutubersFile();
		GiveAway.loadGiveAwayFiles();
		CycleEventHandler.getSingleton().addEvent(new Object(), IdentifierSetAnalysisEvent.getInstance(), 100);
		CycleEventHandler.getSingleton().addEvent(new Object(), LongStringBatchUpdateEvent.getInstance(), 25);
		CycleEventHandler.getSingleton().addEvent(new Object(), OutputBatchUpdateEvent.getInstance(), 50);
		PlayerPetManager.ensureAccountsExist();
		DoubleItemsLog.readLog();
		// PlayerSqlIndex.loadPlayerSqlIndex();
		if (!ServerConfiguration.DEBUG_MODE) {
			new GameBenchmark("logs-clean-up", Server::logsCleanUp, 100, TimeUnit.MILLISECONDS,
					ServerConfiguration.BENCHMARK).execute();
		}
		ServerShutDownUpdate.timeServerAutoSaved = System.currentTimeMillis();
		if (ServerConfiguration.FORCE_DEDICATED_SERVER) {
			ServerCrashAlert.serverCrashTimer.schedule(ServerCrashAlert.serverCrashTask,
					Misc.getSecondsToMilliseconds(60), Misc.getSecondsToMilliseconds(1));
			if (!WebsiteLogInDetails.SPAWN_VERSION) {
				EmailSystem.timer.schedule(EmailSystem.myTask, Misc.getMinutesToMilliseconds(1),
						Misc.getMinutesToMilliseconds(1));
				DiscordBot.startDiscordBot("SERVER_BOT");
				DonationManager.read07WebsiteTimer.schedule(DonationManager.read07WebsiteTask,
						Misc.getSecondsToMilliseconds(5), Misc.getHoursToMilliseconds(12));
			}
		}
		if (printTime) {
			speedDebug = speedDebug + "\nRest: " + (System.currentTimeMillis() - time);
		}
	}

	public static void logsCleanUp() {
		FileUtility.deleteInactiveFilesInDirectory(false, "backup/logs/dice/history", 2, ".txt");
		FileUtility.deleteInactiveFilesInDirectory(false, "backup/logs/logout", 1, ".txt");
		FileUtility.deleteInactiveFilesInDirectory(true, "backup/logs/pm", 3, ".txt");
	}

	public static SQLNetwork getSqlNetwork() {
		return sqlNetwork;
	}

	public static WorldMinigameManager getMinigameManager() {
		return minigameManager;
	}

}
