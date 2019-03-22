package game.content.miscellaneous;


import core.GameType;
import core.Server;
import core.ServerConstants;
import game.content.profile.RareDropLog;
import game.content.starter.GameMode;
import game.content.wildernessbonus.KillReward;
import game.item.BloodMoneyPrice;
import game.item.ItemAssistant;
import game.log.CoinEconomyTracker;
import game.player.Area;
import game.player.Player;
import java.util.ArrayList;
import network.connection.DonationManager;
import utility.Misc;

/**
 * Artefacts.
 *
 * @author MGT Madness, created on 18-01-2014.
 */
public class Artefacts {


	public final static int DHAROK_PET_DROPRATE = 150;

	public final static int KARIL_PET_CHANCE = 350;

	public final static int BLOOD_MONEY_ID = 13307;


	public static enum ArtefactsData {
		AncientStatuette(14876),
		SerenStatuette(14877),
		ArmadylStatuette(14878),
		ZamorakStatuette(14879),
		SaradominStatuette(14880),
		BandosStatuette(14881),
		RubyChalice(14882),
		GuthixianBrazier(14883),
		ArmadylTotem(14884),
		ZamorakMedalion(14885),
		SaradominCarving(14886),
		BandosScrimshaw(14887),
		SaradominAmphora(14888),
		AncientPsaltaryBridge(14889),
		BronzedDragonClaw(14890),
		ThirdAgeCarafe(14891),
		BrokenStatueHeaddress(14892);

		private int id;

		private ArtefactsData(int id) {
			this.id = id;
		}

		public int getId() {
			return id;
		}
	}


	public static int above20WildBloodMoneyAmount;

	public static int pureBloodMoneyAmount;

	public static int berserkerOrRangedTankBloodMoneyAmount;

	public static int combat122PlusBloodMoneyAmount;

	public static int otherCombatBuildsBloodMoneyAmount;

	public static int hybridWithMagicBloodMoneyAmount;

	public static int tribridBloodMoneyAmount;

	public static int tier6SkullBloodMoneyAmount;

	public static int tier5SkullBloodMoneyAmount;

	public static int tier4SkullBloodMoneyAmount;

	public static int tier3SkullBloodMoneyAmount;

	public static int tier2SkullBloodMoneyAmount;

	public static int tier1SkullBloodMoneyAmount;

	public static int tier0SkullBloodMoneyAmount;

	public static int tier6SkullShutDownBloodMoneyAmount;

	public static int tier5SkullShutDownBloodMoneyAmount;

	public static int tier4SkullShutDownBloodMoneyAmount;

	public static int tier3SkullShutDownBloodMoneyAmount;

	public static int tier2SkullShutDownBloodMoneyAmount;

	public static int tier1SkullShutDownBloodMoneyAmount;

	public static int tier0SkullShutDownBloodMoneyAmount;

	public static int donatorBloodMoneyAmount;

	public static int superDonatorBloodMoneyAmount;

	public static int extremeDonatorBloodMoneyAmount;

	public static int legendaryDonatorBloodMoneyAmount;

	public static int ultimateDonatorBloodMoneyAmount;

	public static int uberDonatorBloodMoneyAmount;

	public static int immortalDonatorBloodMoneyAmount;

	public static int supremeDonatorBloodMoneyAmount;

	public static ArrayList<String> pkingLootData = new ArrayList<String>();

	public static void loadPkingLootData() {
		if (GameType.isOsrsPvp()) {
			above20WildBloodMoneyAmount = 300;
			pureBloodMoneyAmount = 150;
			berserkerOrRangedTankBloodMoneyAmount = 500;
			combat122PlusBloodMoneyAmount = 500;
			hybridWithMagicBloodMoneyAmount = 500;
			tribridBloodMoneyAmount = 1000;
			otherCombatBuildsBloodMoneyAmount = 250;
			tier6SkullBloodMoneyAmount = 25;
			tier5SkullBloodMoneyAmount = 50;
			tier4SkullBloodMoneyAmount = 100;
			tier3SkullBloodMoneyAmount = 200;
			tier2SkullBloodMoneyAmount = 400;
			tier1SkullBloodMoneyAmount = 700;
			tier0SkullBloodMoneyAmount = 1000;
			tier6SkullShutDownBloodMoneyAmount = 50;
			tier5SkullShutDownBloodMoneyAmount = 200;
			tier4SkullShutDownBloodMoneyAmount = 500;
			tier3SkullShutDownBloodMoneyAmount = 2000;
			tier2SkullShutDownBloodMoneyAmount = 6000;
			tier1SkullShutDownBloodMoneyAmount = 10000;
			tier0SkullShutDownBloodMoneyAmount = 18000;
			donatorBloodMoneyAmount = 400;
			superDonatorBloodMoneyAmount = 800;
			extremeDonatorBloodMoneyAmount = 1200;
			legendaryDonatorBloodMoneyAmount = 1600;
			ultimateDonatorBloodMoneyAmount = 2000;
			uberDonatorBloodMoneyAmount = 2400;
			immortalDonatorBloodMoneyAmount = 2800;
			supremeDonatorBloodMoneyAmount = 3200;
		}

		if (GameType.isOsrsEco()) {
			above20WildBloodMoneyAmount = 50;
			pureBloodMoneyAmount = 20;
			berserkerOrRangedTankBloodMoneyAmount = 40;
			combat122PlusBloodMoneyAmount = 40;
			hybridWithMagicBloodMoneyAmount = 40;
			tribridBloodMoneyAmount = 80;
			otherCombatBuildsBloodMoneyAmount = 30;
			tier6SkullBloodMoneyAmount = 5;
			tier5SkullBloodMoneyAmount = 10;
			tier4SkullBloodMoneyAmount = 20;
			tier3SkullBloodMoneyAmount = 40;
			tier2SkullBloodMoneyAmount = 60;
			tier1SkullBloodMoneyAmount = 100;
			tier0SkullBloodMoneyAmount = 180;
			tier6SkullShutDownBloodMoneyAmount = 5;
			tier5SkullShutDownBloodMoneyAmount = 20;
			tier4SkullShutDownBloodMoneyAmount = 50;
			tier3SkullShutDownBloodMoneyAmount = 100;
			tier2SkullShutDownBloodMoneyAmount = 200;
			tier1SkullShutDownBloodMoneyAmount = 400;
			tier0SkullShutDownBloodMoneyAmount = 800;
			donatorBloodMoneyAmount = 20;
			superDonatorBloodMoneyAmount = 40;
			extremeDonatorBloodMoneyAmount = 80;
			legendaryDonatorBloodMoneyAmount = 100;
			ultimateDonatorBloodMoneyAmount = 120;
			uberDonatorBloodMoneyAmount = 140;
			immortalDonatorBloodMoneyAmount = 180;
			supremeDonatorBloodMoneyAmount = 200;
		}

		pkingLootData.add("@dre@Artefact drops average:");
		pkingLootData.add("All the blood money rewards below stack if you meet the requirements");
		pkingLootData.add("On average you will get the blood money value below in artefacts");
		pkingLootData.add("Above 20 wild: +" + above20WildBloodMoneyAmount + " bm");
		pkingLootData.add("Getting a kill as a:");
		pkingLootData.add("Pure: +" + pureBloodMoneyAmount + " bm");
		pkingLootData.add("Berserker/Ranged tank: +" + berserkerOrRangedTankBloodMoneyAmount + " bm");
		pkingLootData.add("122+ combat: +" + combat122PlusBloodMoneyAmount + " bm");
		pkingLootData.add("Hybridder with magic: + " + hybridWithMagicBloodMoneyAmount + " bm");
		pkingLootData.add("Tribridder: +" + tribridBloodMoneyAmount + " bm");
		pkingLootData.add("");
		pkingLootData.add("@dre@Killstreak bonuses:");
		pkingLootData.add("The killstreak bonuses below do not stack with each other.");
		pkingLootData.add(Skull.SkullData.TIER_6_SKULL.killstreak + "+ killstreak = Pale red skull, +" + tier6SkullBloodMoneyAmount + " bm");
		pkingLootData.add(Skull.SkullData.TIER_5_SKULL.killstreak + "+ killstreak = Silver skull, +" + tier5SkullBloodMoneyAmount + " bm");
		pkingLootData.add(Skull.SkullData.TIER_4_SKULL.killstreak + "+ killstreak = Green skull, +" + tier4SkullBloodMoneyAmount + " bm");
		pkingLootData.add(Skull.SkullData.TIER_3_SKULL.killstreak + "+ killstreak = Blue skull, +" + tier3SkullBloodMoneyAmount + " bm");
		pkingLootData.add(Skull.SkullData.TIER_2_SKULL.killstreak + "+ killstreak = Red skull, +" + tier2SkullBloodMoneyAmount + " bm");
		pkingLootData.add(Skull.SkullData.TIER_1_SKULL.killstreak + "+ killstreak = Golden skull, +" + tier1SkullBloodMoneyAmount + " bm");
		pkingLootData.add(Skull.SkullData.TIER_0_SKULL.killstreak + "+ killstreak = Black skull, +" + tier0SkullBloodMoneyAmount + " bm");
		pkingLootData.add("");
		pkingLootData.add("@dre@Killstreak shutdown blood money:");
		pkingLootData.add("If you end a player's killstreak, you will receive");
		pkingLootData.add("bonus shut down money reward! The amount depends");
		pkingLootData.add("on the player's killstreak you shut down.");
		pkingLootData.add("The rewards below do not stack.");
		pkingLootData.add(Skull.SkullData.TIER_6_SKULL.killstreak + "+ killstreak = " + tier6SkullShutDownBloodMoneyAmount + " bm reward");
		pkingLootData.add(Skull.SkullData.TIER_5_SKULL.killstreak + "+ killstreak = " + tier5SkullShutDownBloodMoneyAmount + " bm reward");
		pkingLootData.add(Skull.SkullData.TIER_4_SKULL.killstreak + "+ killstreak = " + tier4SkullShutDownBloodMoneyAmount + " bm reward");
		pkingLootData.add(Skull.SkullData.TIER_3_SKULL.killstreak + "+ killstreak = " + tier3SkullShutDownBloodMoneyAmount + " bm reward");
		pkingLootData.add(Skull.SkullData.TIER_2_SKULL.killstreak + "+ killstreak = " + tier2SkullShutDownBloodMoneyAmount + " bm reward");
		pkingLootData.add(Skull.SkullData.TIER_1_SKULL.killstreak + "+ killstreak = " + tier1SkullShutDownBloodMoneyAmount + " bm reward");
		pkingLootData.add(Skull.SkullData.TIER_0_SKULL.killstreak + "+ killstreak = " + tier0SkullShutDownBloodMoneyAmount + " bm reward");
		pkingLootData.add("");
		pkingLootData.add("@dre@Donator bonuses:");
		pkingLootData.add("Donator bonus does not stack with other donator bonuses below");
		pkingLootData.add("Donators receive the rewards below in raw blood money instead!");
		pkingLootData.add("Normal Donator: +" + donatorBloodMoneyAmount + " bm");
		pkingLootData.add("Super Donator: +" + superDonatorBloodMoneyAmount + " bm");
		pkingLootData.add("Extreme Donator: +" + extremeDonatorBloodMoneyAmount + " bm");
		pkingLootData.add("Legendary Donator: +" + legendaryDonatorBloodMoneyAmount + " bm");
		pkingLootData.add("Ultimate Donator: +" + ultimateDonatorBloodMoneyAmount + " bm");
		pkingLootData.add("Uber Donator: +" + uberDonatorBloodMoneyAmount + " bm");
		pkingLootData.add("Immortal Donator: +" + immortalDonatorBloodMoneyAmount + " bm");
		pkingLootData.add("Supreme Donator: +" + supremeDonatorBloodMoneyAmount + " bm");
	}


	/**
	 * Drop the artefact as a loot for the player.
	 *
	 * @param victim The associated player
	 */
	public static void dropArtefacts(Player killer, Player victim) {
		if (killer == null) {
			return;
		}
		if (victim != null) {
			if (victim.bot && Misc.hasPercentageChance(75)) {
				return;
			}
		}
		int donatorBloodMoneyBonus = 0;
		if (killer.isSupremeDonator()) {
			donatorBloodMoneyBonus += supremeDonatorBloodMoneyAmount;
			if (GameType.isOsrsPvp()) {
				CoinEconomyTracker.addIncomeList(killer, "pking(supreme) " + donatorBloodMoneyBonus);
			}
		} else if (killer.isImmortalDonator()) {
			donatorBloodMoneyBonus += immortalDonatorBloodMoneyAmount;
			if (GameType.isOsrsPvp()) {
				CoinEconomyTracker.addIncomeList(killer, "pking(immortal) " + donatorBloodMoneyBonus);
			}
		} else if (killer.isUberDonator()) {
			donatorBloodMoneyBonus += uberDonatorBloodMoneyAmount;
			if (GameType.isOsrsPvp()) {
				CoinEconomyTracker.addIncomeList(killer, "pking(uber) " + donatorBloodMoneyBonus);
			}
		} else if (killer.isUltimateDonator()) {
			donatorBloodMoneyBonus += ultimateDonatorBloodMoneyAmount;
			if (GameType.isOsrsPvp()) {
				CoinEconomyTracker.addIncomeList(killer, "pking(ultimate) " + donatorBloodMoneyBonus);
			}
		} else if (killer.isLegendaryDonator()) {
			donatorBloodMoneyBonus += legendaryDonatorBloodMoneyAmount;
			if (GameType.isOsrsPvp()) {
				CoinEconomyTracker.addIncomeList(killer, "pking(legendary) " + donatorBloodMoneyBonus);
			}
		} else if (killer.isExtremeDonator()) {
			donatorBloodMoneyBonus += extremeDonatorBloodMoneyAmount;
			if (GameType.isOsrsPvp()) {
				CoinEconomyTracker.addIncomeList(killer, "pking(extreme) " + donatorBloodMoneyBonus);
			}
		} else if (killer.isSuperDonator()) {
			donatorBloodMoneyBonus += superDonatorBloodMoneyAmount;
			if (GameType.isOsrsPvp()) {
				CoinEconomyTracker.addIncomeList(killer, "pking(super) " + donatorBloodMoneyBonus);
			}
		} else if (killer.isDonator()) {
			donatorBloodMoneyBonus += donatorBloodMoneyAmount;
			if (GameType.isOsrsPvp()) {
				CoinEconomyTracker.addIncomeList(killer, "pking(donator) " + donatorBloodMoneyBonus);
			}
		}


		int boneShardsAmount = 0;
		int artefactsBloodMoneyLoot = 0;

		if (killer.getWildernessLevel() >= 20) {
			artefactsBloodMoneyLoot += above20WildBloodMoneyAmount;
			if (GameType.isOsrsPvp()) {
				CoinEconomyTracker.addIncomeList(killer, "pking(20wild) " + above20WildBloodMoneyAmount);
			}
			boneShardsAmount += 2;
		}

		if (killer.getCombatLevel() >= 122) {
			artefactsBloodMoneyLoot += combat122PlusBloodMoneyAmount;
			if (GameType.isOsrsPvp()) {
				CoinEconomyTracker.addIncomeList(killer, "pking(maxed) " + combat122PlusBloodMoneyAmount);
			}
			KillReward.killTypes.add("main " + 1);
			boneShardsAmount += 1;
		} else {
			switch (killer.lastKillType) {
				case "PURE":
					artefactsBloodMoneyLoot += pureBloodMoneyAmount;
					if (GameType.isOsrsPvp()) {
						CoinEconomyTracker.addIncomeList(killer, "pking(pure) " + pureBloodMoneyAmount);
					}
					KillReward.killTypes.add("pure " + 1);
					boneShardsAmount += 1;
					break;
				case "BERSERKER":
					artefactsBloodMoneyLoot += berserkerOrRangedTankBloodMoneyAmount;
					if (GameType.isOsrsPvp()) {
						CoinEconomyTracker.addIncomeList(killer, "pking(zerk) " + berserkerOrRangedTankBloodMoneyAmount);
					}
					KillReward.killTypes.add("zerk " + 1);
					boneShardsAmount += 2;
					break;
				case "RANGED":
					artefactsBloodMoneyLoot += berserkerOrRangedTankBloodMoneyAmount;
					if (GameType.isOsrsPvp()) {
						CoinEconomyTracker.addIncomeList(killer, "pking(ranged) " + berserkerOrRangedTankBloodMoneyAmount);
					}
					KillReward.killTypes.add("ranged " + 1);
					boneShardsAmount += 2;
					break;

				// Other builds
				default:
					artefactsBloodMoneyLoot += otherCombatBuildsBloodMoneyAmount;
					if (GameType.isOsrsPvp()) {
						CoinEconomyTracker.addIncomeList(killer, "pking(other) " + otherCombatBuildsBloodMoneyAmount);
					}
					boneShardsAmount += 1;
					break;
			}
		}

		// if player is a magic hybrid.
		if (killer.combatStylesUsed == 2 && killer.hasMagicEquipment && System.currentTimeMillis() - killer.timeMagicUsed <= 25000) {
			artefactsBloodMoneyLoot += hybridWithMagicBloodMoneyAmount;
			if (GameType.isOsrsPvp()) {
				CoinEconomyTracker.addIncomeList(killer, "pking(hybrid) " + hybridWithMagicBloodMoneyAmount);
			}
			KillReward.killTypes.add("hybrid " + 1);
			boneShardsAmount += 2;
		}

		// if player is a tribrid.
		if (killer.combatStylesUsed == 3 && System.currentTimeMillis() - killer.timeRangedUsed <= 25000 && System.currentTimeMillis() - killer.timeMagicUsed <= 25000) {
			artefactsBloodMoneyLoot += tribridBloodMoneyAmount;
			if (GameType.isOsrsPvp()) {
				CoinEconomyTracker.addIncomeList(killer, "pking(tribrid) " + tribridBloodMoneyAmount);
			}
			KillReward.killTypes.add("tribrid " + 1);
			boneShardsAmount += 3;
		}

		int artefactsDropped500Bm = 0;
		for (int index = 0; index < 5; index++) {
			if (artefactsBloodMoneyLoot >= 500) {
				artefactsBloodMoneyLoot -= 500;
				artefactsDropped500Bm++;
			}
		}
		for (int index = 0; index < 5; index++) {
			if (artefactsBloodMoneyLoot == 0) {
				break;
			}
			switch (artefactsBloodMoneyLoot) {
				case 300:
					drop300BmArtefact(killer, victim);
					artefactsBloodMoneyLoot -= 300;
					break;
				case 200:
					drop200BmArtefact(killer, victim);
					artefactsBloodMoneyLoot -= 200;
					break;
				case 150:
					drop150BmArtefact(killer, victim);
					artefactsBloodMoneyLoot -= 150;
					break;
				case 100:
					drop100BmArtefact(killer, victim);
					artefactsBloodMoneyLoot -= 100;
					break;
				case 50:
					drop50BmArtefact(killer, victim);
					artefactsBloodMoneyLoot -= 50;
					break;
			}
		}

		int bloodMoneyTotalDrop = 0;
		// More loot can be left over on the Economy server because we are working with tiny amounts.
		if (artefactsBloodMoneyLoot > 0) {
			bloodMoneyTotalDrop += artefactsBloodMoneyLoot;
		}

		for (int index = 0; index < artefactsDropped500Bm; index++) {
			int artefact = artefactDrop();
			Server.itemHandler.createGroundItem(killer, artefact, victim.getX(), victim.getY(), victim.getHeight(), 1, false, 0, true, victim.getPlayerName(), "", "", "", "Artefacts from pking");
			if (artefact == ArtefactsData.AncientStatuette.getId()) {
				if (!killer.profilePrivacyOn) {
					Announcement.announce(ServerConstants.GREEN_COL + GameMode.getGameModeName(killer) + " received an Ancient statuette from Pking!");
				}
			}
		}

		if (boneShardsAmount > 0 && DonationManager.EVENT_SALE.equals("Halloween")) {
			Server.itemHandler
					.createGroundItem(killer, 604, victim.getX(), victim.getY(), victim.getHeight(), boneShardsAmount, false, 0, true, victim.getPlayerName(), "", "", "", "Halloween bone shards from pking");
		}
		bloodMoneyTotalDrop += donatorBloodMoneyBonus;
		if (bloodMoneyTotalDrop > 0 && GameMode.getDifficulty(killer, "GLADIATOR")) {
			bloodMoneyTotalDrop *= 0.2;
		}

		if (bloodMoneyTotalDrop > 0) {
			Server.itemHandler
					.createGroundItem(killer, 13307, victim.getX(), victim.getY(), victim.getHeight(), bloodMoneyTotalDrop, false, 0, true, victim.getPlayerName(), "", "", "", "bloodMoneyTotalDrop loot from pking");
		}
	}

	private static void drop50BmArtefact(Player killer, Player victim) {
		int artefact = 14890; // Bronzed dragon claw
		Server.itemHandler.createGroundItem(killer, artefact, victim.getX(), victim.getY(), victim.getHeight(), 1, false, 0, true, victim.getPlayerName(), "", "", "", "Artefacts 50bm loot from pking");

	}

	private static void drop100BmArtefact(Player killer, Player victim) {
		int artefact = get100BmArtefact();
		Server.itemHandler.createGroundItem(killer, artefact, victim.getX(), victim.getY(), victim.getHeight(), 1, false, 0, true, victim.getPlayerName(), "", "", "", "Artefacts 100bm loot from pking");

	}

	private static void drop150BmArtefact(Player killer, Player victim) {
		int artefact = get150BmArtefact();
		Server.itemHandler.createGroundItem(killer, artefact, victim.getX(), victim.getY(), victim.getHeight(), 1, false, 0, true, victim.getPlayerName(), "", "", "", "Artefacts 150bm loot from pking");

	}

	private static void drop200BmArtefact(Player killer, Player victim) {
		int artefact = get200BmArtefact();
		Server.itemHandler.createGroundItem(killer, artefact, victim.getX(), victim.getY(), victim.getHeight(), 1, false, 0, true, victim.getPlayerName(), "", "", "", "Artefacts 200bm loot from pking");

	}

	private static void drop300BmArtefact(Player killer, Player victim) {
		int artefact = get300BmArtefact();
		Server.itemHandler.createGroundItem(killer, artefact, victim.getX(), victim.getY(), victim.getHeight(), 1, false, 0, true, victim.getPlayerName(), "", "", "", "Artefacts 300bm loot from pking");

	}

	/**
	 * Used only for dropping 2 extra artefacts if world event is active for pking.
	 */
	public static void dropArtefactsAmountWorldEvent(Player killer, Player victim, int amount) {
		for (int index = 0; index < amount; index++) {
			int artefact = artefactDrop();
			if (GameType.isOsrsPvp()) {
				CoinEconomyTracker.addIncomeList(killer, "PKEVENT " + BloodMoneyPrice.getBloodMoneyPrice(artefact));
			}
			Server.itemHandler.createGroundItem(killer, artefact, victim.getX(), victim.getY(), victim.getHeight(), 1, false, 0, true, victim.getPlayerName(), "", "", "", "Artefacts from world event");
			if (artefact == ArtefactsData.AncientStatuette.getId()) {
				if (!killer.profilePrivacyOn) {
					Announcement.announce(ServerConstants.GREEN_COL + GameMode.getGameModeName(killer) + " received an Ancient statuette from Pking!");
				}
				RareDropLog.appendRareDrop(killer, "Pking: Ancient statuette");
				killer.getPA().sendScreenshot(ItemAssistant.getItemName(artefact), 2);
			}
		}
	}

	/**
	 * Randomly choose one of the artefactDrop[].
	 *
	 * @return One of the artefactDrop[].
	 */
	public static int artefactDrop() {
		int random = Misc.random(1, 50);
		if (random >= 50) {
			return ArtefactsData.AncientStatuette.getId();
		} else if (random >= 43) {
			int misc = Misc.random(1, 4);
			if (misc == 1) {
				return ArtefactsData.SerenStatuette.getId();
			} else if (misc == 2) {
				return ArtefactsData.ArmadylStatuette.getId();
			} else if (misc == 3) {
				return ArtefactsData.ZamorakStatuette.getId();
			} else if (misc == 4) {
				return ArtefactsData.SaradominStatuette.getId();
			}
		} else if (random >= 38) {
			int misc = Misc.random(1, 4);
			if (misc == 1) {
				return ArtefactsData.BandosStatuette.getId();
			} else if (misc == 2) {
				return ArtefactsData.RubyChalice.getId();
			} else if (misc == 3) {
				return ArtefactsData.GuthixianBrazier.getId();
			} else if (misc == 4) {
				return ArtefactsData.ArmadylTotem.getId();
			}
		} else if (random >= 31) {
			int misc = Misc.random(1, 4);
			if (misc == 1) {
				return ArtefactsData.ZamorakMedalion.getId();
			} else if (misc == 2) {
				return ArtefactsData.SaradominCarving.getId();
			} else if (misc == 3) {
				return ArtefactsData.BandosScrimshaw.getId();
			} else if (misc == 4) {
				return ArtefactsData.SaradominAmphora.getId();
			}
		} else {
			int misc = Misc.random(1, 4);
			if (misc == 1) {
				return ArtefactsData.AncientPsaltaryBridge.getId();
			} else if (misc == 2) {
				return ArtefactsData.BronzedDragonClaw.getId();
			} else if (misc == 3) {
				return ArtefactsData.ThirdAgeCarafe.getId();
			} else if (misc == 4) {
				return ArtefactsData.BrokenStatueHeaddress.getId();
			}
		}
		return 995;


	}

	private static int[] artefacts100bmList =
			{
					14890, // Bronzed dragon claw
					14891, // Third age carafe
					14892, // Broken statue headdress
			};

	private static int[] artefacts150bmList =
			{
					14888, // Saradomin amphora
					14889, // Ancient psaltery bridge
					14890, // Bronzed dragon claw
					14891, // Third age carafe
					14892, // Broken statue headdress
			};

	private static int[] artefacts200bmList =
			{
					14886, // Saradomin carving
					14887, // Bandos scrimshaw
					14888, // Saradomin amphora
					14889, // Ancient psaltery bridge
					14890, // Bronzed dragon claw
					14891, // Third age carafe
					14892, // Broken statue headdress
			};

	private static int[] artefacts300bmList =
			{
					14885, // Zamorak medallion
					14886, // Saradomin carving
					14887, // Bandos scrimshaw
					14888, // Saradomin amphora
					14889,// Ancient psaltery bridge
			};


	private static int get100BmArtefact() {
		return artefacts100bmList[Misc.random(artefacts100bmList.length - 1)];
	}

	private static int get150BmArtefact() {
		return artefacts150bmList[Misc.random(artefacts150bmList.length - 1)];
	}


	private static int get200BmArtefact() {
		return artefacts200bmList[Misc.random(artefacts200bmList.length - 1)];
	}

	private static int get300BmArtefact() {
		return artefacts300bmList[Misc.random(artefacts300bmList.length - 1)];
	}

	public static boolean isArtefact(Player player, int itemId) {
		for (ArtefactsData data : ArtefactsData.values()) {
			if (itemId == data.getId()) {
				if (Area.inDangerousPvpArea(player)) {
					player.getPA().sendMessage("You cannot use it in the wilderness.");
					return true;
				}
				int bloodMoney = BloodMoneyPrice.getBloodMoneyPrice(data.getId());
				ItemAssistant.deleteItemFromInventory(player, itemId, 1);
				ItemAssistant.addItem(player, BLOOD_MONEY_ID, bloodMoney);
				player.getPA().sendFilterableMessage("You redeem the artefact for " + Misc.formatNumber(bloodMoney) + " blood money.");
				return true;
			}
		}
		return false;
	}
}
