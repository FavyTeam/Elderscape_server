package network.login;

import core.Server;
import core.ServerConfiguration;
import core.ServerConstants;
import game.bot.BotManager;
import game.content.combat.Combat;
import game.content.commands.AdministratorCommand;
import game.entity.EntityType;
import game.player.Player;
import game.player.PlayerHandler;
import game.player.PlayerSave;
import game.player.event.impl.IdentifierSetAnalysisEvent;
import game.player.punishment.Blacklist;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import network.connection.InvalidAttempt;
import network.packet.Packet;
import network.packet.PacketHandler;
import network.packet.StaticPacketBuilder;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoFuture;
import org.apache.mina.common.IoFutureListener;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import utility.FileUtility;
import utility.HackLogHistory;
import utility.ISAACRandomGen;
import utility.Misc;

/**
 * Login protocol decoder.
 *
 * @author Graham
 * @author Ryan / Lmctruck30 <- login Protocol fixes
 */
public class RS2LoginProtocolDecoder extends CumulativeProtocolDecoder {

	public static boolean printOutAddress;

	/**
	 * Parses the data in the provided byte buffer and writes it to
	 * <code>out</code> as a <code>Packet</code>.
	 *
	 * @param session The IoSession the data was read from
	 * @param in The buffer
	 * @param out The decoder output stream to which to write the <code>Packet</code>
	 * @return Whether enough data was available to create a packet
	 */
	@Override
	public boolean doDecode(IoSession session, ByteBuffer in, ProtocolDecoderOutput out) {

		synchronized (session) {
			Object loginStageObj = session.getAttribute("LOGIN_STAGE");
			int loginStage = 0;
			if (loginStageObj != null) {
				loginStage = (Integer) loginStageObj;
			}
			switch (loginStage) {
				case 0:
					if (2 <= in.remaining()) {
						int protocol = in.get() & 0xff;
						@SuppressWarnings("unused")
						int nameHash = in.get() & 0xff;
						if (protocol == 14) {
							long serverSessionKey = ((long) (java.lang.Math.random() * 99999999D) << 32) + (long) (java.lang.Math.random() * 99999999D);
							StaticPacketBuilder s1Response = new StaticPacketBuilder();
							s1Response.setBare(true).addBytes(new byte[]
									                                  {0, 0, 0, 0, 0, 0, 0, 0}).addByte((byte) 0).addLong(serverSessionKey);
							session.setAttribute("SERVER_SESSION_KEY", serverSessionKey);
							session.write(s1Response.toPacket());
							session.setAttribute("LOGIN_STAGE", 1);
						}
						return true;
					} else {
						in.rewind();
						return false;
					}
				case 1:
					@SuppressWarnings("unused")
					int loginType = -1, loginPacketSize = -1, loginEncryptPacketSize = -1;
					if (2 <= in.remaining()) {
						loginType = in.get() & 0xff; //should be 16 or 18
						loginPacketSize = in.getUnsignedShort();
						Misc.printDebugOnly("Log in packet size: " + loginPacketSize);
						loginEncryptPacketSize = loginPacketSize - (36 + 1 + 1 + 2);
						if (loginPacketSize <= 0 || loginEncryptPacketSize <= 0) {
							Misc.print("Zero or negative login size.");
							session.close();
							return false;
						}
					} else {
						in.rewind();
						return false;
					}
					if (loginPacketSize <= in.remaining()) {
						int magic = in.get() & 0xff;
						int version = in.getUnsignedShort();
						if (magic != 255) {
							Misc.print("Wrong magic id.");
							session.close();
							return false;
						}
						@SuppressWarnings("unused")
						int lowMem = in.get() & 0xff;
						for (int i = 0; i < 9; i++) {
							in.getInt();
						}

						loginEncryptPacketSize--;
						int value = in.get() & 0xff;
						Misc.printDebugOnly("Packet log in info: " + loginEncryptPacketSize + ", " + value);
						if (loginEncryptPacketSize != value) {
							Misc.printDebugOnly("Incorrect packet size match: " + loginEncryptPacketSize + ", " + value);
							//session.close();
							//return false;
						}
						value = in.get() & 0xff;
						if (value != 10) {
							Misc.print("!= 10: " + value);
							session.close();
							return false;
						}
						long clientSessionKey = in.getLong();
						long serverSessionKey = in.getLong();
						int clientUid = in.getInt();
						String addressMac = readRS2String(in);
						String uidAddress = readRS2String(in);
						int clientVersion = in.getInt();
						String osName = readRS2String(in);
						String serial = readRS2String(in);
						String windowsUidBasic = readRS2String(in);
						readRS2String(in); // Old windows_sn
						String windowsSnDifferent = readRS2String(in);
						readRS2String(in); // Old windows_c_drive_uid
						String baseBoardSerialId = readRS2String(in);
						readRS2String(in); // Old processor_id
						int amount = 0;

						ArrayList<String> hardDiskSerials = new ArrayList<String>();
						amount = in.get() & 0xff;
						for (int index = 0; index < amount; index++) {
							String string = readRS2String(in);
							hardDiskSerials.add(string);
						}
						
						ArrayList<String> fileStoreUuids = new ArrayList<String>();
						amount = in.get() & 0xff;
						for (int index = 0; index < amount; index++) {
							String string = readRS2String(in);
							fileStoreUuids.add(string);
						}
						
						ArrayList<String> uuids = new ArrayList<String>();
						amount = in.get() & 0xff;
						for (int index = 0; index < amount; index++) {
							String uuid = readRS2String(in);
							uuids.add(uuid);
						}

						String name = readRS2String(in);
						if (ServerConfiguration.DEBUG_MODE) {
							if (name.toLowerCase().equals("m")) {
								name = "mgt madness";
							}
						}
						String pass = readRS2String(in);
						int sessionKey[] = new int[4];
						sessionKey[0] = (int) (clientSessionKey >> 32);
						sessionKey[1] = (int) clientSessionKey;
						sessionKey[2] = (int) (serverSessionKey >> 32);
						sessionKey[3] = (int) serverSessionKey;
						ISAACRandomGen inC = new ISAACRandomGen(sessionKey);
						for (int i = 0; i < 4; i++) {
							sessionKey[i] += 50;
						}
						ISAACRandomGen outC = new ISAACRandomGen(sessionKey);
						boolean outdated = false;
						if (clientUid != ServerConfiguration.UID) {
							outdated = true;
						}

						if (Verification.verify(name, addressMac, clientVersion)) {
							load(session, clientUid, name, pass, inC, outC, version, outdated, addressMac.toLowerCase(), uidAddress.toLowerCase(), clientVersion, serial, uuids, osName, windowsUidBasic, windowsSnDifferent, baseBoardSerialId, hardDiskSerials, fileStoreUuids);
							session.getFilterChain().remove("protocolFilter");
							session.getFilterChain().addLast("protocolFilter", new ProtocolCodecFilter(new GameCodecFactory(inC)));
							return true;
						} else {
							return false;
						}
					} else {
						in.rewind();
						return false;
					}
			}
		}
		return false;
	}

	private final static int CAN_LOG_IN = 2;

	private final static int INVALID_PASSWORD_OR_USER = 3;

	private final static int INVALID_USERNAME = 4;

	private final static int ALREADY_LOGGED_IN = 5;

	private final static int WORLD_FULL = 7;

	private final static int LOG_IN_SEVER_OFFLINE = 8;

	private final static int BANNED = 10;

	private final static int SERVER_IS_BUSY_TRY_AGAIN = 11;

	private final static int SERVER_BEING_UPDATED = 14;

	private final static int CANNOT_ACCESS_ANY_ACCOUNT_FOR_1_MIN_BECAUSE_WRONG_PASS = 16;

	private final static int BANNED_FROM_LOGGING_INTO_PEOPLES_ACCOUNTS = 18;

	private final static int OFFENSIVE_NAME = 19;

	private final static int JUST_LEFT_ANOTHER_WORLD = 21;

	private final static int OUTDATED_CLIENT = 24;

	/**
	 * @param processorId
	 * 	This is not used, due to many players sharing the same processorId
	 */
	private synchronized void load(final IoSession session, final int uid, String name, String pass, final ISAACRandomGen inC, ISAACRandomGen outC, int version,
			boolean uidOutdated, String macAddress, String uidAddress, int clientVersion, String serial, ArrayList<String> uuids, String osName, String windowsUidBasic, String windowsSnDifferent, String baseBoardSerialId, ArrayList<String> hardDiskSerials, ArrayList<String> fileStoreUuids) {

		session.setAttribute("opcode", -1);
		session.setAttribute("size", -1);
		int loginDelay = 1;
		int returnCode = CAN_LOG_IN; // 255 is maximum, i've set all mine to null...
		String responseText = null; // none by default

		name = name.trim();
		pass = pass.toLowerCase();

		if (Misc.invalidUsername(name)) {
			returnCode = INVALID_USERNAME;
		}

		if (name.length() > 12) {
			returnCode = LOG_IN_SEVER_OFFLINE;
		}
		if (uidOutdated) {
			returnCode = OUTDATED_CLIENT;
		}

		// File names on Windows such as AUX, Com1, Com2 etc all give errors.
		if (!PlayerSave.isValidName(name.toLowerCase() + ".txt")) {
			returnCode = OFFENSIVE_NAME;
		}
		uidOutdated = false;
		Player player = new Player(session, -1, false, EntityType.PLAYER);
		player.setPlayerName(name);
		player.playerPass = pass;
		player.clientVersion = clientVersion;
		player.setInStreamDecryption(inC);
		player.setOutStreamDecryption(outC);
		player.getOutStream().packetEncryption = outC;
		player.addressIp = ((InetSocketAddress) player.getSession().getRemoteAddress()).getAddress().getHostAddress();
		boolean riggedDetails = false;
		if (riggedDetails) {
			player.addressIp = "456";
			uidAddress = "invalid";
			uuids.clear();
			uuids.add("          ");
			windowsUidBasic = "invalid";
			windowsSnDifferent = "45trg";
			baseBoardSerialId = "s";
			hardDiskSerials.clear();
			hardDiskSerials.add("ff");
			hardDiskSerials.add("ff");
			hardDiskSerials.add("ff");
			fileStoreUuids.clear();
			fileStoreUuids.add("f");
		}

		// Format all client input to make sure they are not rigged.
		macAddress = Misc.formatUid(macAddress);
		uidAddress = Misc.formatUid(uidAddress);
		windowsUidBasic = Misc.formatUid(windowsUidBasic);
		windowsSnDifferent = Misc.formatUid(windowsSnDifferent);
		baseBoardSerialId = Misc.formatUid(baseBoardSerialId);
		for (int index = 0; index < uuids.size(); index++) {
			String old = uuids.get(index);
			uuids.remove(index);
			uuids.add(index, Misc.formatUid(old));
		}
		for (int index = 0; index < hardDiskSerials.size(); index++) {
			String old = hardDiskSerials.get(index);
			hardDiskSerials.remove(index);
			hardDiskSerials.add(index, Misc.formatUid(old));
		}
		for (int index = 0; index < fileStoreUuids.size(); index++) {
			String old = fileStoreUuids.get(index);
			fileStoreUuids.remove(index);
			fileStoreUuids.add(index, Misc.formatUid(old));
		}

		player.saveCharacter = false;
		if (PlayerHandler.isPlayerOn(name)) {
			returnCode = ALREADY_LOGGED_IN;
		}

		if (PlayerHandler.getRealPlayerCount() > ServerConstants.MAXIMUM_PLAYERS) {
			returnCode = WORLD_FULL;
		}

		if (Server.UpdateServer) {
			returnCode = SERVER_BEING_UPDATED;
		}
		if (!Misc.isUidSaveableIntoCharacterFile(uidAddress)) {
			uidAddress = "";
		}
		// Compile all uid.
		for (int index = 0; index < uuids.size(); index++) {
			String extra = uidAddress.isEmpty() ? "" : ServerConstants.UUID_SEPERATOR;
			if (!uuids.get(index).equals("invalid")) {
				uidAddress = uidAddress + extra + uuids.get(index);
			}
		}
		
		boolean enableAllUidSaved = true;
		if (enableAllUidSaved) {
			String extra = null;

			extra = uidAddress.isEmpty() ? "" : ServerConstants.UUID_SEPERATOR;
			if (!windowsUidBasic.equals("invalid")) {
				uidAddress = uidAddress + extra + windowsUidBasic;
			}
			extra = uidAddress.isEmpty() ? "" : ServerConstants.UUID_SEPERATOR;
			if (!windowsSnDifferent.equals("invalid")) {
				uidAddress = uidAddress + extra + windowsSnDifferent;
			}
			extra = uidAddress.isEmpty() ? "" : ServerConstants.UUID_SEPERATOR;
			if (!baseBoardSerialId.equals("invalid")) {
				uidAddress = uidAddress + extra + baseBoardSerialId;
			}
			for (int index = 0; index < hardDiskSerials.size(); index++) {
				extra = uidAddress.isEmpty() ? "" : ServerConstants.UUID_SEPERATOR;
				if (!hardDiskSerials.get(index).equals("invalid")) {
					uidAddress = uidAddress + extra + hardDiskSerials.get(index);
				}
			}
			for (int index = 0; index < fileStoreUuids.size(); index++) {
				extra = uidAddress.isEmpty() ? "" : ServerConstants.UUID_SEPERATOR;
				if (!fileStoreUuids.get(index).equals("invalid")) {
					uidAddress = uidAddress + extra + fileStoreUuids.get(index);
				}
			}
		}

		if (printOutAddress) {
			Misc.printDontSave("Print: [" + player.addressIp + "] [" + uidAddress + "] [" + name + "]");
		}
		if (returnCode == CAN_LOG_IN) {
			Blacklist.bannedReasonResponse = null;
			if (Blacklist.isBlacklisted(name, player.addressIp, macAddress, player.playerPass, uidAddress, true)) {
				returnCode = BANNED;
				if (Blacklist.bannedReasonResponse != null) {
					responseText = Blacklist.bannedReasonResponse;
				}
			}
		}
		if (returnCode == CAN_LOG_IN) {
			if (Misc.isFlaggedOffensiveName(name)) {
				returnCode = OFFENSIVE_NAME;
			}
		}
		if (returnCode == CAN_LOG_IN) {
			if (!InvalidAttempt.canConnect(player.addressIp, macAddress, uidAddress, name)) {
				returnCode = CANNOT_ACCESS_ANY_ACCOUNT_FOR_1_MIN_BECAUSE_WRONG_PASS;
			}
		}

		if (returnCode == CAN_LOG_IN) {
			if (InvalidAttempt.autoBlackListed(player.addressIp, uidAddress)) {
				String originalMac = Blacklist.readOfflinePlayerData(ServerConstants.getCharacterLocation() + name + ".txt", "addressMac", 3);

				boolean notSamePerson = true;
				if (Misc.macMatches(macAddress, originalMac)) {
					notSamePerson = false;
				}
				if (notSamePerson) {
					if (HackLogHistory.hasPreviousAccessToAccount(name, player.addressIp, uidAddress)) {
						notSamePerson = false;
					}
				}
				if (notSamePerson) {
					if (FileUtility.fileExists(ServerConstants.getCharacterLocation() + name + ".txt")) {
						returnCode = BANNED_FROM_LOGGING_INTO_PEOPLES_ACCOUNTS;
						InvalidAttempt.addBannedLogInAttempt(player.addressIp, macAddress, uidAddress, name, player.playerPass, "Autoblacklisted");
					}
				}
			}
		}

		if (returnCode == CAN_LOG_IN) {
			int load = PlayerSave.loadGame(player, player.getPlayerName(), player.playerPass, false);
			if (System.currentTimeMillis() - player.timeLoggedOff < 1300) {
				if (!ServerConfiguration.DEBUG_MODE && System.currentTimeMillis() - player.timeLoggedOff >= 0) {
					returnCode = ALREADY_LOGGED_IN;
				}
			}
			if (returnCode != ALREADY_LOGGED_IN) {
				//load 1 == successfull.
				if (load == 0) {
					player.setPlayerName(Misc.capitalize(player.getPlayerName()));
				}
				boolean skip = false;
				if (!skip) {
					if (load == 3) // 3 = wrong password.
					{
						player.saveFile = false;
						String originalUid = Blacklist.readOfflinePlayerData(ServerConstants.getCharacterLocation() + name + ".txt", "addressUid", 3);
						String originalMac = Blacklist.readOfflinePlayerData(ServerConstants.getCharacterLocation() + name + ".txt", "addressMac", 3);
						String originalIp = Blacklist.readOfflinePlayerData(ServerConstants.getCharacterLocation() + name + ".txt", "lastSavedIpAddress", 3);
						boolean notSamePerson = true;
						if (originalIp.equalsIgnoreCase(player.addressIp) || originalUid.equalsIgnoreCase(uidAddress)) {
							notSamePerson = false;
						}
						if (!InvalidAttempt.canConnect(player.addressIp, macAddress, uidAddress, name)) {
							returnCode = CANNOT_ACCESS_ANY_ACCOUNT_FOR_1_MIN_BECAUSE_WRONG_PASS;
						} else {
							returnCode = INVALID_PASSWORD_OR_USER;
						}
						if (notSamePerson) {
							InvalidAttempt.addToLog(name, player.addressIp, macAddress, uidAddress, pass, originalMac, originalUid, originalIp);
						}
						InvalidAttempt.invalidAttempts.add(new InvalidAttempt(player.addressIp, macAddress, uidAddress, name, notSamePerson, pass));

						if (InvalidAttempt.isBruteforceHacker(player.addressIp, macAddress, uidAddress, name)) {
							returnCode = BANNED_FROM_LOGGING_INTO_PEOPLES_ACCOUNTS;
							InvalidAttempt.addBannedLogInAttempt(player.addressIp, macAddress, uidAddress, name, player.playerPass, "Autoblacklisted");
						}
					} else {
						boolean flaggedMacAddress = false;
						if (!Misc.validMacAddress(macAddress)) {
							flaggedMacAddress = true;
						}

						boolean floodBot = false;
						boolean stopLogIn = false;
						String reasonStopped = "";
						for (int index = 0; index < Blacklist.floodIps.size(); index++) {
							if (Blacklist.floodIps.get(index).equals(player.addressIp)) {
								floodBot = true;
								reasonStopped = "Flood ip";
								break;
							}
						}
						for (int index = 0; index < Blacklist.floodAccountBans.size(); index++) {
							if (Blacklist.floodAccountBans.get(index).equalsIgnoreCase(player.getPlayerName())) {
								floodBot = true;
								reasonStopped = "Flood account";
								break;
							}
						}
						if (!player.isTutorialComplete() && !AdministratorCommand.enableRegistering) {
							stopLogIn = true;
							reasonStopped = "Registering disabled";
						} else if (flaggedMacAddress && AdministratorCommand.fakeMacDeclined) {
							floodBot = true;
							reasonStopped = "Fake Mac declined: " + macAddress;
						} else if (!player.isTutorialComplete() && AdministratorCommand.limitRegisteringSpeed && System.currentTimeMillis() - lastPlayerRegisteredTime <= 1000) {
							stopLogIn = true;
							Misc.print("New account to register stopped: " + player.getPlayerName() + ", " + uidAddress);
							reasonStopped = "Account registering speed limit";
						}
						if (floodBot) {
							if (player.secondsBeenOnline >= 3600) {
								floodBot = false;
							}
							if (Blacklist.floodIpsWhitelist.contains(player.addressIp)) {
								floodBot = false;
							}
						}

						if (floodBot) {
							if (!Blacklist.floodAccountBans.contains(player.getPlayerName().toLowerCase())) {
								Blacklist.floodAccountBans.add(player.getPlayerName().toLowerCase());
							}
							if (!Blacklist.floodIps.contains(player.addressIp)) {
								Blacklist.floodIps.add(player.addressIp);
							}
						}
						if (floodBot || stopLogIn) {
							returnCode = SERVER_IS_BUSY_TRY_AGAIN;
							Blacklist.floodBlockReason
									.add("[" + Misc.getDateAndTime() + "] " + player.getPlayerName() + ", " + player.addressIp + ", " + uidAddress + ": " + reasonStopped);
						} else {
							if (!player.isTutorialComplete() && AdministratorCommand.flagNewPlayers) {
								PacketHandler.packetLogPlayerList.add(player.getPlayerName());
							}
							if (!player.isTutorialComplete()) {
								lastPlayerRegisteredTime = System.currentTimeMillis();
							}
							for (int i = 0; i < player.playerEquipment.length; i++) {
								if (player.playerEquipment[i] == 0) {
									player.playerEquipment[i] = -1;
									player.playerEquipmentN[i] = 0;
								}
							}
							if (!Server.playerHandler.newPlayerClient(player)) {
								returnCode = WORLD_FULL;
								player.saveFile = false;
							} else {
								player.saveFile = true;
							}

							Combat.updatePlayerStance(player); // Added here so the animation updates for everyone straight away.
						}
					}
				}
			}
		}

		if (returnCode == CAN_LOG_IN) {
			UniqueIdentifierSet.playerLoggedIn(player, serial, osName, windowsUidBasic, windowsSnDifferent, baseBoardSerialId, hardDiskSerials, fileStoreUuids, uuids);
		}

		player.lastUidAddress = uidAddress;
		player.addressMac = macAddress;
		player.addressUid = uidAddress;
		player.packetType = -1;
		player.packetSize = 0;
		StaticPacketBuilder bldr = new StaticPacketBuilder();
		bldr.setBare(true);

		bldr.addByte((byte) returnCode);
		bldr.addByte(responseText == null ? (byte) 0 : (byte) 1);
		if (responseText != null) {
			bldr.addString(responseText);
		}
		Blacklist.bannedReasonResponse = null;
		if (returnCode == CAN_LOG_IN) {
			player.saveCharacter = true;
			bldr.addByte((byte) player.playerRights);
		} else if (returnCode == JUST_LEFT_ANOTHER_WORLD) {
			bldr.addByte((byte) loginDelay);
		} else {
			bldr.addByte((byte) 0);
		}
		player.setActive(true);
		bldr.addByte((byte) 0);
		Packet pkt = bldr.toPacket();
		session.setAttachment(player);
		session.write(pkt).addListener(new IoFutureListener() {
			@Override
			public void operationComplete(IoFuture arg0) {
				session.getFilterChain().remove("protocolFilter");
				session.getFilterChain().addFirst("protocolFilter", new ProtocolCodecFilter(new GameCodecFactory(inC)));
			}
		});
	}

	public static Player loadBot(String name, String pass, boolean pkBot) {
		BotManager.currentBotNumber++;
		int loginDelay = 1;
		int returnCode = 2; // 255 is maximum

		name = name.trim();

		pass = pass.toLowerCase();

		String responseText = null;

		Player player = new Player(null, -1, true, EntityType.PLAYER_BOT);

		if (ServerConfiguration.STABILITY_TEST) {

		} else {
			if (pkBot) {
				if (BotManager.currentBotNumber <= 1) {
					player.botPkType = "PURE";
				} else if (BotManager.currentBotNumber <= 2) {
					player.botPkType = "INITIATE";
				} else if (BotManager.currentBotNumber <= 3) {
					player.botPkType = "BERSERKER";
				} else if (BotManager.currentBotNumber <= 4) {
					player.botPkType = "RANGED TANK";
				} else if (BotManager.currentBotNumber <= 5) {
					player.botPkType = "MELEE";
				} else if (BotManager.currentBotNumber <= 10) {
					player.botPkType = "";
				}
				player.gameModeTitle = "[Bot]";
			}
		}
		player.setPlayerName(name);
		player.playerPass = pass;
		player.setTutorialComplete(true);
		player.getOutStream().packetEncryption = null;

		player.saveCharacter = false;

		if (PlayerHandler.isPlayerOn(name)) {
			returnCode = 5;
		}

		if (returnCode == CAN_LOG_IN) {
			int load = 13;
			if (!ServerConfiguration.STABILITY_TEST) {
				load = PlayerSave.loadGame(player, player.getPlayerName(), player.playerPass, true);
			}
			//load = PlayerSave.loadGame(player, player.getPlayerName(), player.playerPass, true);

			if (load == 3) // 3 = wrong password.
			{
				returnCode = 3;
				player.saveFile = false;
			} else {
				for (int i = 0; i < player.playerEquipment.length; i++) {
					if (player.playerEquipment[i] == 0) {
						player.playerEquipment[i] = -1;
						player.playerEquipmentN[i] = 0;
					}
				}
				if (!Server.playerHandler.newPlayerClient(player)) {
					returnCode = 7;
					player.saveFile = false;
				} else {
					player.saveFile = true;
				}
			}
		}

		player.packetType = -1;
		player.packetSize = 0;

		StaticPacketBuilder bldr = new StaticPacketBuilder();
		bldr.setBare(true);
		bldr.addByte((byte) returnCode);
		if (returnCode == CAN_LOG_IN) {
			player.saveCharacter = true;
			bldr.addByte((byte) player.playerRights);
		} else if (returnCode == 21) {
			bldr.addByte((byte) loginDelay);
		} else {
			bldr.addByte((byte) 0);
		}
		player.setActive(true);
		bldr.addByte((byte) 0);

		return player;
	}


	public static long lastPlayerRegisteredTime;

	private synchronized String readRS2String(ByteBuffer in) {
		StringBuilder sb = new StringBuilder();
		byte b;
		while ((b = in.get()) != 10) {
			sb.append((char) b);
		}
		return sb.toString();
	}



	/**
	 * Releases the buffer used by the given session.
	 *
	 * @param session The session for which to release the buffer
	 * @throws Exception if failed to dispose all resources
	 */
	@Override
	public void dispose(IoSession session) throws Exception {
		super.dispose(session);
	}

	public static class UniqueIdentifierSet {

		private final String date;

		private final String username;

		private final String serial;

		private final String ipAddress;

		public final String osName;

		public final String windowsUidBasic;

		public final String windowsSnDifferent;

		public final String baseBoardSerialId;

		public ArrayList<String> hardDiskSerials = new ArrayList<>();

		public ArrayList<String> fileStoreUuids = new ArrayList<>();

		public ArrayList<String> uuids = new ArrayList<>();

		public UniqueIdentifierSet(String date, String username, String serial, String ipAddress, String osName, String windowsUidBasic, String windowsSnDifferent, String baseBoardSerialId, ArrayList<String> hardDiskSerials, ArrayList<String> fileStoreUuids, ArrayList<String> uuids) {
			this.date = date;
			this.username = username;
			this.serial = serial;
			this.ipAddress = ipAddress;
			this.osName = osName;
			this.windowsUidBasic = windowsUidBasic;
			this.windowsSnDifferent = windowsSnDifferent;
			this.baseBoardSerialId = baseBoardSerialId;
			this.hardDiskSerials = hardDiskSerials;
			this.fileStoreUuids = fileStoreUuids;
			this.uuids = uuids;
		}

		public String getDate() {
			return date;
		}

		public String getUsername() {
			return username;
		}

		public String getSerial() {
			return serial;
		}

		public String getIpAddress() {
			return ipAddress;
		}

		public static void playerLoggedIn(Player player, String serial, String osName, String windowsUidBasic, String windowsSnDifferent, String baseBoardSerialId, ArrayList<String> hardDiskSerials, ArrayList<String> fileStoreUuids, ArrayList<String> uuids) {
			UniqueIdentifierSet set = new UniqueIdentifierSet(Misc.getDateAndTime(), player.getPlayerName(), serial, player.addressIp, osName, windowsUidBasic, windowsSnDifferent, baseBoardSerialId, hardDiskSerials, fileStoreUuids, uuids);
			IdentifierSetAnalysisEvent.getInstance().add(set);
		}
	}

}
