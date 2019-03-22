package game.npc;

import java.util.ArrayList;
import java.util.List;

import core.GameType;
import core.Server;
import core.ServerConstants;
import game.content.miscellaneous.Announcement;
import game.content.profile.RareDropLog;
import game.content.shop.ShopAssistant;
import game.content.starter.GameMode;
import game.content.worldevent.WorldEvent;
import game.item.ItemAssistant;
import game.item.ItemDefinition;
import game.log.CoinEconomyTracker;
import game.npc.data.NpcDefinition;
import game.npc.pet.BossPetDrops;
import game.player.Area;
import game.player.Player;
import game.player.PlayerHandler;
import game.position.Position;
import utility.FileUtility;
import utility.Misc;
import utility.WebsiteLogInDetails;

/**
 * @author Sanity
 */

public class NpcDrops {

	private final static double DROP_RATE_EASYNESS_MULTIPLIER = 1.3;

	public static NpcDrops[] NPC_DROPS = new NpcDrops[NpcDefinition.NPC_MAXIMUM_ID];

	public static ArrayList<Integer> npcIdOrder = new ArrayList<>();

	/**
	 * Npc ids.
	 */
	public ArrayList<Integer> npcId = new ArrayList<>();

	/**
	 * Item drop ids.
	 */
	public ArrayList<Integer> itemId = new ArrayList<>();

	/**
	 * Droprate chance for item drop.
	 */
	public ArrayList<Integer> chance = new ArrayList<>();

	/**
	 * Item drop minimum amount.
	 */
	public ArrayList<Integer> minimumAmount = new ArrayList<>();

	/**
	 * Item drop maximum amount.
	 */
	public ArrayList<Integer> maximumAmount = new ArrayList<>();

	/**
	 * The npc id that is representing this npc. When there are multiple npcs sharing the same drop table, the secondary npcs also have their own
	 * definitions, so if they have a parent npc id, use that definition instead.
	 */
	public int parentNpcId;

	/**
	 * True to announce drop.
	 */
	public ArrayList<Boolean> announce = new ArrayList<>();

	/**
	 * True if the drop is a pet drop, so when the drop occurs, it can summon it to follow the player.
	 */
	public ArrayList<Boolean> petDrop = new ArrayList<>();

	public NpcDrops(ArrayList<Integer> npcId, ArrayList<Integer> itemId, ArrayList<Integer> minimumAmount, ArrayList<Integer> maximumAmount, ArrayList<Integer> chance,
	                ArrayList<Boolean> announce, int parentNpcId, ArrayList<Boolean> petDrop) {
		this.npcId = npcId;
		this.itemId = itemId;
		this.minimumAmount = minimumAmount;
		this.maximumAmount = maximumAmount;
		this.announce = announce;
		this.chance = chance;
		this.parentNpcId = parentNpcId;
		this.petDrop = petDrop;
	}

	public static class NpcIdList {
		public static List<NpcIdList> npcIdStoredList = new ArrayList<NpcIdList>();

		public ArrayList<Integer> npcId = new ArrayList<>();

		public NpcIdList(ArrayList<Integer> npcId) {
			this.npcId = npcId;
		}
	}

	private static int turnNpcDropToNotedForGladiator(Player player, int itemId) {
		if (GameMode.getDifficulty(player, "GLADIATOR")) {
			String itemName = ItemAssistant.getItemName(itemId).toLowerCase();
			if (itemName.contains("bone") || itemName.contains("hide") || itemName.contains("bar")) {
				return itemId = ItemAssistant.getNotedItem(itemId);
			}
		}
		return itemId;
	}

	public static void loadRareDrops() {
		NpcDrops.NPC_DROPS = new NpcDrops[NpcDefinition.NPC_MAXIMUM_ID];
		NpcIdList.npcIdStoredList.clear();
		npcIdOrder.clear();
		boolean lastLineWasDropRate = false;
		ArrayList<String> arraylist = FileUtility.readFileAndSaveEmptyLine(ServerConstants.getDataLocation() + "npc/drops.txt");

		ArrayList<Integer> npcId = new ArrayList<>();
		ArrayList<Integer> itemId = new ArrayList<>();
		ArrayList<Integer> chance = new ArrayList<>();
		ArrayList<Integer> minimumAmount = new ArrayList<>();
		ArrayList<Integer> maximumAmount = new ArrayList<>();
		ArrayList<Boolean> petDrop = new ArrayList<>();
		ArrayList<Boolean> announce = new ArrayList<>();
		int parentNpcId = 0;

		for (int index = 0; index < arraylist.size(); index++) {
			String line = arraylist.get(index);
			if (line.isEmpty()) {
				continue;
			}
			String[] parse = line.split(" ");
			if (line.contains("Npc:") && !line.startsWith("//")) {
				line = line.substring(5);
				String[] parseNpcs = line.split(" ");
				ArrayList<Integer> localNpcIds = new ArrayList<>();
				for (int b = 0; b < parseNpcs.length; b++) {
					localNpcIds.add(Integer.parseInt(parseNpcs[b]));
					npcId.add(Integer.parseInt(parseNpcs[b]));
				}
				NpcIdList.npcIdStoredList.add(new NpcIdList(localNpcIds));
				lastLineWasDropRate = false;

			} else if (!line.startsWith("//")) {
				String otherParse[] = line.substring(0, line.indexOf("//") - 1).split(" ");
				boolean petApplied = false;
				boolean announcedApplied = false;
				if (otherParse.length > 3) {
					if (otherParse[3].equals("pet")) {
						petDrop.add(true);
						petApplied = true;
					}
					if (otherParse[3].equals("announce")) {
						announce.add(true);
						announcedApplied = true;
					}
				}
				if (!announcedApplied) {
					announce.add(false);
				}
				if (!petApplied) {
					petDrop.add(false);
				}
				int finalChance = Integer.parseInt(parse[0]);
				if (GameType.isOsrsEco()) {
					finalChance /= DROP_RATE_EASYNESS_MULTIPLIER;
					if (finalChance == 0) {
						finalChance = 1;
					}
				}
				chance.add(finalChance);
				itemId.add(Integer.parseInt(parse[1]));
				if (parse[2].contains("-")) {
					String[] furtherSplit = parse[2].split("-");
					minimumAmount.add(Integer.parseInt(furtherSplit[0]));
					maximumAmount.add(Integer.parseInt(furtherSplit[1]));
				} else {
					minimumAmount.add(Integer.parseInt(parse[2]));
					maximumAmount.add(Integer.parseInt(parse[2]));
				}
				lastLineWasDropRate = true;
			}
			if (line.startsWith("//") && lastLineWasDropRate || index == arraylist.size() - 1) {
				NPC_DROPS[npcId.get(0)] = new NpcDrops(npcId, itemId, minimumAmount, maximumAmount, chance, announce, parentNpcId, petDrop);
				lastLineWasDropRate = false;
				npcIdOrder.add(npcId.get(0));
				if (npcId.size() > 1) {
					for (int i = 1; i < npcId.size(); i++) {
						ArrayList<Integer> npcIdLocal = new ArrayList<>();
						npcIdLocal.add(npcId.get(i));
						NPC_DROPS[npcId.get(i)] = new NpcDrops(npcIdLocal, itemId, minimumAmount, maximumAmount, chance, announce, npcId.get(0), petDrop);
					}
				}
				npcId = new ArrayList<>();
				itemId = new ArrayList<>();
				chance = new ArrayList<>();
				minimumAmount = new ArrayList<>();
				maximumAmount = new ArrayList<>();
				petDrop = new ArrayList<>();
				announce = new ArrayList<>();
				parentNpcId = 0;
			}
		}

		if (GameType.isOsrsPvp() && WebsiteLogInDetails.SPAWN_VERSION) {
			NpcDrops scorpion = NpcDrops.NPC_DROPS[3024];
			if (scorpion != null) {
				scorpion.itemId.add(13307);
				scorpion.chance.add(1);
				scorpion.minimumAmount.add(1_000);
				scorpion.maximumAmount.add(10_000);
				scorpion.petDrop.add(false);
				scorpion.announce.add(false);
			}
		}

		if (GameType.isOsrsPvp()) {
			return;
		}
		ItemDefinition.getDefinitions()[TINY_CASKET_ID].price = TINY_CASKET_LOOT;
		ItemDefinition.getDefinitions()[SMALL_CASKET_ID].price = SMALL_CASKET_LOOT;
		ItemDefinition.getDefinitions()[MEDIUM_CASKET_ID].price = MEDIUM_CASKET_LOOT;
		ItemDefinition.getDefinitions()[LARGE_CASKET_ID].price = LARGE_CASKET_LOOT;

		for (int index = 0; index < NpcIdList.npcIdStoredList.size(); index++) {
			int npc = NpcIdList.npcIdStoredList.get(index).npcId.get(0);
			int npcHitpoints = NpcDefinition.getDefinitions()[npc].hitPoints;
			if (npcHitpoints < MINIMUM_NPC_HP) {
				continue;
			}
			double dropRate = 0;
			int casketId = 0;
			int hpCapChosen = 0;
			int lowestHp = 0;
			if (npcHitpoints <= TINY_CASKET_NPC_HP) {
				hpCapChosen = TINY_CASKET_NPC_HP;
				casketId = TINY_CASKET_ID;
				lowestHp = MINIMUM_NPC_HP;
			} else if (npcHitpoints <= SMALL_CASKET_NPC_HP) {
				hpCapChosen = SMALL_CASKET_NPC_HP;
				casketId = SMALL_CASKET_ID;
				lowestHp = TINY_CASKET_NPC_HP;
			} else if (npcHitpoints <= MEDIUM_CASKET_NPC_HP) {
				hpCapChosen = MEDIUM_CASKET_NPC_HP;
				casketId = MEDIUM_CASKET_ID;
				lowestHp = SMALL_CASKET_NPC_HP;
			} else {
				hpCapChosen = LARGE_NPC_HP_CAP;
				casketId = LARGE_CASKET_ID;
				lowestHp = MEDIUM_CASKET_NPC_HP;
			}
			if (npcHitpoints > LARGE_NPC_HP_CAP) {
				npcHitpoints = LARGE_NPC_HP_CAP;
			}
			dropRate = npcHitpoints - lowestHp;
			dropRate /= ((double) hpCapChosen - (double) lowestHp);
			double difference = (double) CASKET_MAXIMUM_CHANCE - (double) CASKET_MINIMUM_CHANCE;
			dropRate = dropRate * difference;
			dropRate = difference - dropRate;
			dropRate += (double) CASKET_MINIMUM_CHANCE;
			if (dropRate < CASKET_MINIMUM_CHANCE) {
				dropRate = CASKET_MINIMUM_CHANCE;
			}

			NpcDrops instance = NPC_DROPS[npc];
			boolean added = false;
			for (int i = 0; i < instance.chance.size(); i++) {
				if (instance.chance.get(i) == (int) dropRate || instance.chance.get(i) < (int) dropRate) {
					int indexToAdd = i;
					instance.chance.add(indexToAdd, (int) dropRate);
					instance.itemId.add(indexToAdd, casketId);
					instance.minimumAmount.add(indexToAdd, 1);
					instance.maximumAmount.add(indexToAdd, 1);
					instance.announce.add(indexToAdd, false);
					instance.petDrop.add(indexToAdd, false);
					added = true;
					break;
				}
			}
			if (!added) {
				instance.chance.add((int) dropRate);
				instance.itemId.add(casketId);
				instance.minimumAmount.add(1);
				instance.maximumAmount.add(1);
				instance.announce.add(false);
				instance.petDrop.add(false);
			}
		}
	}

	private final static int CASKET_MINIMUM_CHANCE = 6;

	private final static int CASKET_MAXIMUM_CHANCE = 12;

	private final static int MINIMUM_NPC_HP = 20;

	private final static int TINY_CASKET_NPC_HP = 40;

	private final static int SMALL_CASKET_NPC_HP = 80;

	private final static int MEDIUM_CASKET_NPC_HP = 160;

	private final static int LARGE_NPC_HP_CAP = 320;

	public final static int TINY_CASKET_LOOT = 30_000;

	public final static int SMALL_CASKET_LOOT = 60_000;

	public final static int MEDIUM_CASKET_LOOT = 120_000;

	public final static int LARGE_CASKET_LOOT = 240_000;

	private final static int TINY_CASKET_ID = 2724;

	private final static int SMALL_CASKET_ID = 2720;

	private final static int MEDIUM_CASKET_ID = 2717;

	private final static int LARGE_CASKET_ID = 2714;

	public static boolean exists(int npcId) {
		if (npcId < 0 || npcId > NPC_DROPS.length - 1) {
			return false;
		}
		return NPC_DROPS[npcId] != null;
	}

	public static long giveDropTableDrop(Player player, boolean dropSimulator, int npcToKill, Position dropPosition) {
		long dropLootTotal = 0;
		NpcDrops instance = NPC_DROPS[npcToKill];
		if (instance == null) {
			return 0;
		}
		if (instance.parentNpcId > 0) {
			npcToKill = instance.parentNpcId;
			instance = NPC_DROPS[npcToKill];
		}
		int dropsLength = instance.itemId.size();

		int dropAbove2ChanceReceived = 0;
		for (int index = 0; index < dropsLength; index++) {
			int itemId = instance.itemId.get(index);
			double chance = instance.chance.get(index);
			int originalChance = (int) chance;
			int minimumAmount = instance.minimumAmount.get(index);
			int maximumAmount = instance.maximumAmount.get(index);
			boolean announce = instance.announce.get(index);
			boolean petDrop = instance.petDrop.get(index);
			int itemAmount = Misc.random(minimumAmount, maximumAmount);

			if (!dropSimulator) {
				chance = GameMode.getDropRate(player, chance);
				if (WorldEvent.getCurrentEvent().contains(NpcDefinition.getDefinitions()[npcToKill].name)) {
					chance /= 3.0;
					if (chance <= 1) {
						chance = 1;
					}
				}
			}

			if (chance >= 2 && dropAbove2ChanceReceived >= 1) {
				boolean skip = false;
				if (GameType.isOsrsEco()) {
					if (dropAbove2ChanceReceived == 1 && Misc.hasOneOutOf(15) && chance <= 50) {
						skip = true;
					}
					if (itemId == TINY_CASKET_ID || itemId == SMALL_CASKET_ID || itemId == MEDIUM_CASKET_ID || itemId == LARGE_CASKET_ID) {
						skip = true;
					}
				}
				if (!skip) {
					continue;
				}
			}
			if (Misc.hasOneOutOf(chance)) {
				if (chance >= 2) {
					dropAbove2ChanceReceived++;
				}

				long itemValue = 0;
				itemValue = ShopAssistant.getSellToShopPrice(null, itemId, false) * itemAmount;
				if (itemId == ServerConstants.getMainCurrencyId()) {
					itemValue = 1 * itemAmount;
				}
				dropLootTotal += itemValue;
				if (!dropSimulator) {
					if (itemValue > 0) {
						if (Area.inDangerousPvpArea(player)) {
							CoinEconomyTracker.addIncomeList(player, "WILD-PVM " + itemValue);
						} else {
							CoinEconomyTracker.addIncomeList(player, "SAFE-PVM " + itemValue);
						}
					}
					if (petDrop) {
						BossPetDrops.awardBoss(player, itemId, itemId, npcToKill, "BOSS");
					} else {
						if (originalChance >= 20) {
							if (!player.itemsCollected.contains(itemId)) {
								player.itemsCollected.add(itemId);
							}
						}
						if (GameType.isOsrs() && ItemAssistant.getItemName(itemId).toLowerCase().contains("ancient") && (npcToKill == 7936 || npcToKill == 7939 || npcToKill == 11124)) {
							player.getPA().sendMessage("Exchange this drop at the Emblem trader who is at lvl 35 wild, in the Revenant caves.");
						}
						itemId = turnNpcDropToNotedForGladiator(player, itemId);
						if (GameType.isOsrsPvp() && itemId == 13307 && ItemAssistant.hasItemEquippedSlot(player, 12785, ServerConstants.RING_SLOT)) {
							player.getPA().sendFilterableMessage("<col=00a000>x" + itemAmount + " blood money has been added to your inventory.");
							ItemAssistant.addItemToInventoryOrDrop(player, itemId, itemAmount);
						}
						else {
							Server.itemHandler.createGroundItem(player, itemId, dropPosition.getX(), dropPosition.getY(), dropPosition.getZ(), itemAmount, false, 0, true, "", "", "", "", "giveDropTableDrop " + npcToKill);
						}
						if (announce) {
							String itemName = ItemAssistant.getItemName(itemId);
							if (!player.profilePrivacyOn) {
								Announcement.announce(ServerConstants.GREEN_COL + GameMode.getGameModeName(player) + " has received one " + itemName + " from " + NpcDefinition
										                                                                                                                                  .getDefinitions()[npcToKill].name
								                      + "!");
							}
							RareDropLog.appendRareDrop(player, NpcDefinition.getDefinitions()[npcToKill].name + ": " + ItemAssistant.getItemName(itemId));
							player.getPA().sendScreenshot(ItemAssistant.getItemName(itemId), 2);
						}
					}
				}
			}
		}
		return dropLootTotal;
	}

	public static void otherDrops(Npc npc, Player player) {
		int item = 0;
		int amount = 1;

		Position dropPosition = npc.getDropPosition();

		switch (npc.npcType) {
			// Skotizo
			case 7286:
				item = 13302;
				amount = 1;
				Player secondHighest = PlayerHandler.players[Server.npcHandler.getSecondHighestDamageDealer(npc)];
				if (secondHighest != null) {
					Server.itemHandler.createGroundItem(secondHighest, 13302, npc.getVisualX(), npc.getVisualY(), npc.getHeight(), 1, false, 0, true, "", "", "", "", "otherDrops Skotizo, " + npc.name);
				}
				break;
			// Oversized skeleton
			case 680:
				item = 16095; // Oversized bones
				amount = 1;
				break;

			// Donator boss (tempory loot to test)
			case 7884:
				item = 13307;
				amount = 10000;
				break;
		}

		if (item != 0) {
			item = turnNpcDropToNotedForGladiator(player, item);
			Server.itemHandler.createGroundItem(player, item, dropPosition.getX(), dropPosition.getY(), dropPosition.getZ(), amount, false, 0, true, "", "", "", "", "otherDrops " + npc.name);
		}
	}


}
