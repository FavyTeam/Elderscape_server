package network.packet;

import core.ServerConfiguration;
import game.content.packet.*;
import game.item.ItemAssistant;
import game.log.GameTickLog;
import game.player.Player;
import game.player.movement.PathFinder;
import utility.FileUtility;
import utility.Misc;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;


public class PacketHandler {

	private static PacketType packetId[] = new PacketType[256];

	static {
		SilentPacket u = new SilentPacket();
		packetId[3] = u;
		packetId[202] = u;
		packetId[77] = u;
		packetId[86] = u;
		packetId[74] = u;
		packetId[78] = u;
		packetId[36] = u;
		packetId[226] = u;
		packetId[234] = u;
		packetId[246] = u;
		packetId[148] = u;
		packetId[228] = u;
		packetId[183] = u;
		packetId[18] = u;
		packetId[230] = u;
		packetId[136] = u;
		packetId[189] = u;
		packetId[153] = new SecondPlayerClick();
		packetId[152] = u;
		packetId[200] = u;
		packetId[85] = u;
		packetId[165] = u;
		packetId[238] = u;
		packetId[150] = u;
		packetId[253] = new SecondClickGroundItem();
		packetId[40] = new DialoguePacket();
		ClickObjectPacket co = new ClickObjectPacket();
		packetId[132] = co;
		packetId[252] = co;
		packetId[70] = co;
		packetId[234] = co;
		packetId[57] = new ItemOnNpcPacket();
		ClickNpcPacket cn = new ClickNpcPacket();
		packetId[18] = cn;
		packetId[72] = cn;
		packetId[131] = cn;
		packetId[155] = cn;
		packetId[17] = cn;
		packetId[21] = cn;
		packetId[16] = new SecondClickItemPacket();
		packetId[75] = new ThirdClickItemPacket();
		packetId[122] = new FirstClickItemPacket();
		packetId[241] = new ClickingInGamePacket();
		packetId[4] = new ChatPacket();
		packetId[236] = new PickUpItemPacket();
		packetId[87] = new DropItemPacket();
		packetId[185] = new ClickingButtonPacket();
		packetId[130] = new ClickingOtherPacket();
		packetId[103] = new CommandPacket();
		packetId[213] = new MoveItemFromContainerPacket();
		packetId[214] = new MoveItemsPacket();
		packetId[237] = new MagicOnItemsPacket();
		packetId[181] = new MagicOnFloorItemPacket();
		packetId[202] = new IdleLogoutPacket();
		AttackPlayerPacket ap = new AttackPlayerPacket();
		packetId[220] = new PlaceholderChangePacket();
		packetId[73] = ap;
		packetId[249] = ap;
		packetId[128] = new ChallengePlayerPacket();
		packetId[39] = new TradePacket();
		packetId[139] = new FollowPlayerPacket();
		packetId[41] = new WearItemPacket();
		packetId[145] = new RemoveItemPacket();
		packetId[117] = new Bank5Packet();
		packetId[43] = new Bank10Packet();
		packetId[129] = new BankAllPacket();
		packetId[101] = new ChangeAppearancePacket();
		packetId[14] = new ItemOnPlayerPacket();
		PrivateMessagingPacket pm = new PrivateMessagingPacket();
		packetId[188] = pm;
		packetId[126] = pm;
		packetId[215] = pm;
		packetId[74] = pm;
		packetId[95] = pm;
		packetId[133] = pm;
		packetId[135] = new BankX1Packet();
		packetId[208] = new BankXPacket();
		WalkingPacket w = new WalkingPacket();
		packetId[98] = w;
		packetId[164] = w;
		packetId[248] = w;
		packetId[53] = new ItemOnItemPacket();
		packetId[192] = new ItemOnObjectPacket();
		packetId[25] = new ItemOnGroundItemPacket();
		ChangeRegionPacket cr = new ChangeRegionPacket();
		packetId[121] = cr;
		packetId[210] = cr;
		packetId[60] = new ClanChatPacket();

	}

	/**
	 * Players that have been added via command to be tracked and the default players logged from the moparscape thread.
	 */
	public static ArrayList<String> packetLogPlayerList = new ArrayList<String>();

	public static ArrayList<String> packetLogData = new ArrayList<String>();

	/**
	 * Store the flagged players inventory and bank.
	 */
	public static ArrayList<String> packetLogItemsData = new ArrayList<String>();

	public static ArrayList<String> invalidPacketLog = new ArrayList<String>();

	public static ArrayList<String> spellbookLog = new ArrayList<String>();

	public static ArrayList<String> chatAndPmLog = new ArrayList<String>();

	public static ArrayList<String> diceLog = new ArrayList<String>();

	public static ArrayList<String> stringAbuseLog = new ArrayList<String>();

	public static ArrayList<String> unUsedObject = new ArrayList<String>();

	public static boolean showIndividualPackets;

	public static void saveCurrentFlaggedPlayers() {
		FileUtility.deleteAllLines("backup/logs/packet abuse/flagged players.txt");
		FileUtility.saveArrayContents("backup/logs/packet abuse/flagged players.txt", packetLogPlayerList);
	}

	public static void saveData(String name, String data) {
		String string = "";
		if (data.contains("Sending")) {
			string = "-------------------------------------------------------------";
			packetLogData.add(string);
		}
		string = name + " at " + Misc.getDateAndTime() + ": " + data;
		packetLogData.add(string);

	}

	private static String getPacketName(int packetType) {
		switch (packetType) {
			case 40:
				return "Dialogue";
			case 132:
				return "ClickObjectFirst";
			case 252:
				return "ClickObjectSecond";
			case 70:
				return "ClickObjectThird";
			case 57:
				return "ItemOnNpc";
			case 72:
				return "ClickNpcAttack";
			case 131:
				return "ClickNpcMage";
			case 155:
				return "ClickNpcFirst";
			case 17:
				return "ClickNpcSecond";
			case 21:
				return "ClickNpcThird";
			case 16:
				return "ItemClick2";
			case 75:
				return "ItemClick3";
			case 122:
				return "ClickItem";
			case 241:
				return "ClickingInGame";
			case 4:
				return "Chat";
			case 236:
				return "PickUpItem";
			case 87:
				return "DropItem";
			case 185:
				return "ClickingButton";
			case 130:
				return "ClickingOther";
			case 103:
				return "Command";
			case 214:
				return "MoveItems";
			case 237:
				return "MagicOnItems";
			case 181:
				return "MagicOnFloorItems";
			case 202:
				return "IdleLogout";
			case 73:
				return "AttackPlayerNormal";
			case 249:
				return "AttackPlayerMage";
			case 128:
				return "ChallengePlayer";
			case 39:
				return "Trade";
			case 139:
				return "FollowPlayer";
			case 41:
				return "WearItem";
			case 145:
				return "RemoveItem";
			case 117:
				return "Bank5";
			case 43:
				return "Bank10";
			case 129:
				return "BankAll";
			case 101:
				return "ChangeAppearance";
			case 188:
				return "PrivateMessagingAddFriend";
			case 126:
				return "PrivateMessagingSendPm";
			case 215:
				return "PrivateMessagingRemoveFriend";
			case 95:
				return "PrivateMessagingChangePmStatus";
			case 74:
				return "PrivateMessagingRemoveIgnore";
			case 133:
				return "PrivateMessagingAddIgnore";
			case 135:
				return "BankX1";
			case 208:
				return "BankX";
			case 98:
				return "WalkingNpcOrObjectClick";
			case 164:
				return "WalkingTileClick";
			case 248:
				return "WalkingMinimapClick";
			case 53:
				return "ItemOnItem";
			case 192:
				return "ItemOnObject";
			case 25:
				return "ItemOnGroundItem";
			case 121:
				return "ChangeRegion1";
			case 210:
				return "ChangeRegion2";
			case 60:
				return "ClanChat";

			case 3:
			case 77:
			case 86:
			case 78:
			case 36:
			case 226:
			case 234:
			case 246:
			case 148:
			case 228:
			case 183:
			case 18:
			case 230:
			case 136:
			case 189:
			case 152:
			case 200:
			case 85:
			case 165:
			case 238:
			case 150:
			case 253:
				return "Silent packet";
		}
		return "Invalid/Unknown packet!";
	}

	/**
	 * Default flagged players on server start-up.
	 */
	public static void defaultFlaggedPlayers() {
		String line = "";
		boolean EndOfFile = false;
		BufferedReader fileLocation = null;
		String fileLocationText = "backup/logs/packet abuse/flagged players.txt";
		try {
			fileLocation = new BufferedReader(new FileReader(fileLocationText));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Misc.print(fileLocationText + ": file not found.");
			return;
		}
		try {
			line = fileLocation.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			Misc.print(fileLocationText + ": error loading file.");
		}
		while (!EndOfFile && line != null) {
			if (line.isEmpty()) {
				continue;
			}
			PacketHandler.packetLogPlayerList.add(line);

			try {
				line = fileLocation.readLine();
			} catch (IOException e) {
				e.printStackTrace();
				EndOfFile = true;
			}
		}
		try {
			fileLocation.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void processPacket(Player player, int packetType, int packetSize) {
		PacketType p = packetId[packetType];
		if (p != null && packetType > 0 && packetType < 257 && packetType == player.packetType && packetSize == player.packetSize) {
			player.packetsSentThisTick++;
			if (player.packetsSentThisTick > ServerConfiguration.MAXIMUM_PACKETS_PER_TICK) {
				//Misc.print("Too many packets sent by: " + player.getPlayerName() + ", amount:" + player.packetsSentThisTick + ", last packet type: " + getPacketName(packetType));
				return;
			}
			if (ServerConfiguration.SHOW_PACKETS) {
				player.playerAssistant.sendMessage("PacketType: " + packetType + ". PacketSize: " + packetSize + ".");
			}
			// Uncomment to show packets sent.
			//Misc.print("Name: " + player.getPlayerName() + ", PacketType: " + packetType + ". PacketSize: " + packetSize + ".");
			if (showIndividualPackets) {
				Misc.print("Name: " + player.getPlayerName() + ", PacketType: " + packetType + ". PacketSize: " + packetSize + ".");
			}

			boolean trackPlayer = false;
			long uid = 0;
			for (int i = 0; i < packetLogPlayerList.size(); i++) {
				if (player.getPlayerName().toLowerCase().equals(packetLogPlayerList.get(i).toLowerCase())) {
					trackPlayer = true;
					String bankCheck = "";
					bankCheck = ", wealth: " + ItemAssistant.getAccountBankValue(player);
					// Generate a long number between x and y.
					long x = 0;
					long y = Long.MAX_VALUE;
					Random r = new Random();
					uid = x + ((long) (r.nextDouble() * (y - x)));
					saveData(player.getPlayerName(),
					         "Sending " + getPacketName(packetType) + ", Packet type: " + packetType + ". Packet size: " + packetSize + ", uid: " + uid + ", at: " + player.getX()
					         + ", " + player.getY() + ", " + player.getHeight() + bankCheck);
					break;
				}
			}

			try {
				long start = System.currentTimeMillis();

				p.processPacket(player, packetType, packetSize, trackPlayer);

				if (trackPlayer) {
					packetLogItemsData.add("-------------------------------------------------------------");
					packetLogItemsData.add("uid: " + uid);
					packetLogItemsData.add(player.getPlayerName() + " at " + Misc.getDateAndTime());
					packetLogItemsData.add("Inventory----");
					for (int index = 0; index < player.playerItems.length; index++) {
						int itemId = player.playerItems[index] - 1;
						if (itemId <= 0) {
							continue;
						}
						int amount = player.playerItemsN[index];
						String name = ItemAssistant.getItemName(itemId);
						packetLogItemsData.add(itemId + ", " + name + ", x" + Misc.formatNumber(amount));
					}
					packetLogItemsData.add("Bank0----");
					for (int index = 0; index < player.bankItems.length; index++) {
						int itemId = player.bankItems[index] - 1;
						if (itemId <= 0) {
							continue;
						}
						int amount = player.bankItemsN[index];
						String name = ItemAssistant.getItemName(itemId);
						packetLogItemsData.add(itemId + ", " + name + ", x" + Misc.formatNumber(amount));
					}
					packetLogItemsData.add("Bank1----");
					for (int index = 0; index < player.bankItems1.length; index++) {
						int itemId = player.bankItems1[index] - 1;
						if (itemId <= 0) {
							continue;
						}
						int amount = player.bankItems1N[index];
						String name = ItemAssistant.getItemName(itemId);
						packetLogItemsData.add(itemId + ", " + name + ", x" + Misc.formatNumber(amount));
					}
					packetLogItemsData.add("Bank2----");
					for (int index = 0; index < player.bankItems2.length; index++) {
						int itemId = player.bankItems2[index] - 1;
						if (itemId <= 0) {
							continue;
						}
						int amount = player.bankItems2N[index];
						String name = ItemAssistant.getItemName(itemId);
						packetLogItemsData.add(itemId + ", " + name + ", x" + Misc.formatNumber(amount));
					}
					packetLogItemsData.add("Bank3----");
					for (int index = 0; index < player.bankItems3.length; index++) {
						int itemId = player.bankItems3[index] - 1;
						if (itemId <= 0) {
							continue;
						}
						int amount = player.bankItems3N[index];
						String name = ItemAssistant.getItemName(itemId);
						packetLogItemsData.add(itemId + ", " + name + ", x" + Misc.formatNumber(amount));
					}
					packetLogItemsData.add("Bank4----");
					for (int index = 0; index < player.bankItems4.length; index++) {
						int itemId = player.bankItems4[index] - 1;
						if (itemId <= 0) {
							continue;
						}
						int amount = player.bankItems4N[index];
						String name = ItemAssistant.getItemName(itemId);
						packetLogItemsData.add(itemId + ", " + name + ", x" + Misc.formatNumber(amount));
					}
					packetLogItemsData.add("Bank5----");
					for (int index = 0; index < player.bankItems5.length; index++) {
						int itemId = player.bankItems5[index] - 1;
						if (itemId <= 0) {
							continue;
						}
						int amount = player.bankItems5N[index];
						String name = ItemAssistant.getItemName(itemId);
						packetLogItemsData.add(itemId + ", " + name + ", x" + Misc.formatNumber(amount));
					}
					packetLogItemsData.add("Bank6----");
					for (int index = 0; index < player.bankItems6.length; index++) {
						int itemId = player.bankItems6[index] - 1;
						if (itemId <= 0) {
							continue;
						}
						int amount = player.bankItems6N[index];
						String name = ItemAssistant.getItemName(itemId);
						packetLogItemsData.add(itemId + ", " + name + ", x" + Misc.formatNumber(amount));
					}
					packetLogItemsData.add("Bank7----");
					for (int index = 0; index < player.bankItems7.length; index++) {
						int itemId = player.bankItems7[index] - 1;
						if (itemId <= 0) {
							continue;
						}
						int amount = player.bankItems7N[index];
						String name = ItemAssistant.getItemName(itemId);
						packetLogItemsData.add(itemId + ", " + name + ", x" + Misc.formatNumber(amount));
					}
					packetLogItemsData.add("Bank8----");
					for (int index = 0; index < player.bankItems8.length; index++) {
						int itemId = player.bankItems8[index] - 1;
						if (itemId <= 0) {
							continue;
						}
						int amount = player.bankItems8N[index];
						String name = ItemAssistant.getItemName(itemId);
						packetLogItemsData.add(itemId + ", " + name + ", x" + Misc.formatNumber(amount));
					}
				}
				long delta = System.currentTimeMillis() - start;

				if (delta > 50) {
					if (getPacketName(packetType).contains("Walking")) {
						GameTickLog.singlePlayerPacketLog
								.add(Misc.getDateAndTime() + ", " + player.getPlayerName() + ", " + getPacketName(packetType) + " took " + (delta) + " ms");
						GameTickLog.singlePlayerPacketLog
								.add(Misc.getDateAndTime() + ", loops1: " + PathFinder.loops1 + ", " + ", loops2: " + PathFinder.loops2 + ", " + ", loops3: " + PathFinder.loops3
								     + ", " + ", loops4: " + PathFinder.loops4 + ", " + ", loops5: " + PathFinder.loops5 + ", " + ", loops6: " + PathFinder.loops6 + ", destX: "
								     + PathFinder.destX + ", destY: " + PathFinder.destY);
					} else {
						GameTickLog.singlePlayerPacketLog
								.add(Misc.getDateAndTime() + ", " + player.getPlayerName() + ", " + getPacketName(packetType) + " took " + (delta) + " ms");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			player.setDisconnected(true, "invalid packet");
			if (ServerConfiguration.DEBUG_MODE) {
				Misc.print(player.getPlayerName() + " has been disconnected at " + Misc.getDateAndTime() + " for sending invalid Packet type: " + packetType + ". Packet size: "
				           + packetSize);
			}
			invalidPacketLog
					.add(player.getPlayerName() + " has been disconnected at " + Misc.getDateAndTime() + " for sending invalid Packet type: " + packetType + ". Packet size: "
					     + packetSize);
		}
	}

}
