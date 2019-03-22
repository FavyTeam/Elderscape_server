package game.content.skilling.smithing;

import core.GameType;
import core.ServerConstants;
import game.content.achievement.Achievements;
import game.content.miscellaneous.RandomEvent;
import game.content.music.SoundSystem;
import game.content.skilling.Skilling;
import game.content.skilling.SkillingStatistics;
import game.item.ItemAssistant;
import game.item.ItemDefinition;
import game.log.CoinEconomyTracker;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import utility.Misc;

/**
 * Smithing.java
 *
 * @author Sanity
 **/

public class Smithing {

	public static boolean isFurnace(Player player, int objectId) {
		switch (objectId) {

			case 24009:
			case 16469:
			case 2030:
			case 26300:
			case 21303:
			case 12100:
				return true;
		}
		return false;
	}

	private static final int[] SMELT_BARS =
			{2349, 2351, 2355, 2353, 2357, 2359, 2361, 2363};

	private static final int[] SMELT_FRAME =
			{2405, 2406, 2407, 2409, 2410, 2411, 2412, 2413};

	public static final int[] ORES_SMELT =
			{436, 438, 440, 442, 447, 449, 451};

	public static final String[] ORES_SMELT_NAME =
			{"BRONZE", "BRONZE", "IRON", "STEEL", "MITHRIL", "ADAMANT", "RUNE"};

	public static boolean smithingButtons(final Player player, int button, final String superheatType) {
		boolean state = false;
		String bar = superheatType;

		switch (button) {
			case 15147:
				bar = "BRONZE 1";
				break;
			case 15146:
				bar = "BRONZE 5";
				break;
			case 10247:
				bar = "BRONZE 10";
				break;
			case 9110:
				bar = "BRONZE 28";
				break;

			case 15151:
				bar = "IRON 1";
				break;
			case 15150:
				bar = "IRON 5";
				break;
			case 15149:
				bar = "IRON 10";
				break;
			case 15148:
				bar = "IRON 28";
				break;

			case 15159:
				bar = "STEEL 1";
				break;
			case 15158:
				bar = "STEEL 5";
				break;
			case 15157:
				bar = "STEEL 10";
				break;
			case 15156:
				bar = "STEEL 28";
				break;

			case 29017:
				bar = "MITHRIL 1";
				break;
			case 29016:
				bar = "MITHRIL 5";
				break;
			case 24253:
				bar = "MITHRIL 10";
				break;
			case 16062:
				bar = "MITHRIL 28";
				break;

			case 29022:
				bar = "ADAMANT 1";
				break;
			case 29020:
				bar = "ADAMANT 5";
				break;
			case 29019:
				bar = "ADAMANT 10";
				break;
			case 29018:
				bar = "ADAMANT 28";
				break;

			case 15163:
				bar = "GOLD 1";
				break;

			case 15162:
				bar = "GOLD 5";
				break;

			case 15161:
				bar = "GOLD 10";
				break;

			case 15160:
				bar = "GOLD 28";
				break;

			case 29026:
				bar = "RUNE 1";
				break;
			case 29025:
				bar = "RUNE 5";
				break;
			case 29024:
				bar = "RUNE 10";
				break;
			case 29023:
				bar = "RUNE 28";
				break;
		}
		if (bar.isEmpty()) {
			return false;
		} else {
			state = true;
		}
		if (player.getActionIdUsed() != 5) {
			return true;
		}
		if (RandomEvent.isBannedFromSkilling(player)) {
			return true;
		}
		if (bar.contains("BRONZE")) {

			if (!superheatType.isEmpty()) {
				player.barsToMake = 1;
			} else {
				String[] parse = bar.split(" ");
				player.barsToMake = Integer.parseInt(parse[1]);
			}
			player.getPA().closeInterfaces(true);
			CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
				@Override
				public void execute(CycleEventContainer container) {

					if (player.barsToMake <= 0) {
						container.stop();
						return;
					}
					if (ItemAssistant.hasItemAmountInInventory(player, 436, 1)) {
						if (ItemAssistant.hasItemAmountInInventory(player, 438, 1)) {
							Achievements.checkCompletionSingle(player, 1011);
							if (superheatType.isEmpty()) {
								player.playerAssistant.sendFilterableMessage("You begin to smelt...");
							}
							player.skillingStatistics[SkillingStatistics.BARS_SMELTED]++;
							ItemAssistant.deleteItemFromInventory(player, 436, 1);
							ItemAssistant.deleteItemFromInventory(player, 438, 1);
							ItemAssistant.addItem(player, 2349, 1);
							Skilling.addSkillExperience(player, 26, ServerConstants.SMITHING, false);
							if (!superheatType.isEmpty()) {
								ItemAssistant.deleteItemFromInventory(player, 561, 1);
								if (!ItemAssistant.hasItemEquipped(player, 1387)) {
									ItemAssistant.deleteItemFromInventory(player, 554, 4);
								}
								Skilling.addSkillExperience(player, 53, ServerConstants.MAGIC, false);
								Skilling.updateSkillTabFrontTextMain(player, ServerConstants.MAGIC);
							} else {
								SoundSystem.sendSound(player, 469, 500);
								player.startAnimation(899);
							}
							player.barsToMake--;
						} else {
							player.playerAssistant.sendMessage("You have run out of Tin ores.");
							player.getPA().closeInterfaces(true);
							player.smeltInterface = false;
							container.stop();
						}
					} else {
						player.playerAssistant.sendMessage("You have run out of Copper ores.");
						player.getPA().closeInterfaces(true);
						player.smeltInterface = false;
						container.stop();
					}
					if (player.barsToMake <= 0) {
						container.stop();
					}
				}

				@Override
				public void stop() {
					player.barsToMake = 0;
				}
			}, 2);
		}

		if (bar.contains("IRON")) {
			if ((player.baseSkillLevel[13] < 15)) {
				player.getDH().sendStatement("You need at least 15 Smithing to smelt an Iron bar.");
				return true;
			}
			if (!superheatType.isEmpty()) {
				player.barsToMake = 1;
			} else {
				String[] parse = bar.split(" ");
				player.barsToMake = Integer.parseInt(parse[1]);
			}
			player.getPA().closeInterfaces(true);
			CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
				@Override
				public void execute(CycleEventContainer container) {

					if (ItemAssistant.hasItemAmountInInventory(player, 440, 1)) {
						if (superheatType.isEmpty()) {
							player.playerAssistant.sendFilterableMessage("You begin to smelt...");
						}
						player.skillingStatistics[SkillingStatistics.BARS_SMELTED]++;
						ItemAssistant.deleteItemFromInventory(player, 440, 1);
						ItemAssistant.addItem(player, 2351, 1);
						Skilling.addSkillExperience(player, 13, ServerConstants.SMITHING, false);

						if (!superheatType.isEmpty()) {
							ItemAssistant.deleteItemFromInventory(player, 561, 1);
							if (!ItemAssistant.hasItemEquipped(player, 1387)) {
								ItemAssistant.deleteItemFromInventory(player, 554, 4);
							}
							Skilling.addSkillExperience(player, 53, ServerConstants.MAGIC, false);
							Skilling.updateSkillTabFrontTextMain(player, ServerConstants.MAGIC);
						} else {
							player.startAnimation(899);
							SoundSystem.sendSound(player, 469, 500);
						}
						player.barsToMake--;
					} else {
						player.playerAssistant.sendMessage("You have run out of iron ores.");
						player.getPA().closeInterfaces(true);
						player.smeltInterface = false;
						container.stop();
					}
					if (player.barsToMake <= 0) {
						container.stop();
					}
				}

				@Override
				public void stop() {
					player.barsToMake = 0;
				}
			}, 2);
		}

		if (bar.contains("STEEL")) {
			if ((player.baseSkillLevel[13] < 30)) {
				player.getDH().sendStatement("You need at least 30 Smithing to smelt a Steel bar.");
				return true;
			}
			if (!superheatType.isEmpty()) {
				player.barsToMake = 1;
			} else {
				String[] parse = bar.split(" ");
				player.barsToMake = Integer.parseInt(parse[1]);
			}
			player.getPA().closeInterfaces(true);
			if (superheatType.isEmpty()) {
				player.playerAssistant.sendFilterableMessage("You begin to smelt...");
			}
			player.skillingStatistics[SkillingStatistics.BARS_SMELTED]++;
			CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
				@Override
				public void execute(CycleEventContainer container) {
					if (ItemAssistant.hasItemAmountInInventory(player, 440, 1)) {
						if (ItemAssistant.hasItemAmountInInventory(player, 453, 1)) {
							ItemAssistant.deleteItemFromInventory(player, 440, 1);
							ItemAssistant.deleteItemFromInventory(player, 453, 1);
							ItemAssistant.addItem(player, 2353, 1);
							Skilling.addSkillExperience(player, 18, ServerConstants.SMITHING, false);

							if (!superheatType.isEmpty()) {
								ItemAssistant.deleteItemFromInventory(player, 561, 1);
								if (!ItemAssistant.hasItemEquipped(player, 1387)) {
									ItemAssistant.deleteItemFromInventory(player, 554, 4);
								}
								Skilling.addSkillExperience(player, 53, ServerConstants.MAGIC, false);
								Skilling.updateSkillTabFrontTextMain(player, ServerConstants.MAGIC);
							} else {
								player.startAnimation(899);
								SoundSystem.sendSound(player, 469, 500);
							}
							player.barsToMake--;
						} else {
							player.playerAssistant.sendMessage("You have run out of Coal ores.");
							player.getPA().closeInterfaces(true);
							player.smeltInterface = false;
							container.stop();
						}
					} else {
						player.playerAssistant.sendMessage("You have run out of Iron ores.");
						player.getPA().closeInterfaces(true);
						player.smeltInterface = false;
						container.stop();
					}
					if (player.barsToMake <= 0) {
						container.stop();
					}
				}

				@Override
				public void stop() {
					player.barsToMake = 0;
				}
			}, 2);
		}

		if (bar.contains("GOLD")) {
			if ((player.baseSkillLevel[13] < 40)) {
				player.getDH().sendStatement("You need at least 40 Smithing to smelt a Gold bar.");
				return true;
			}

			if (!superheatType.isEmpty()) {
				player.barsToMake = 1;
			} else {
				String[] parse = bar.split(" ");
				player.barsToMake = Integer.parseInt(parse[1]);
			}
			player.getPA().closeInterfaces(true);
			CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
				@Override
				public void execute(CycleEventContainer container) {
					if (ItemAssistant.hasItemAmountInInventory(player, 444, 1)) {
						if (superheatType.isEmpty()) {
							player.playerAssistant.sendFilterableMessage("You begin to smelt...");
						}
						player.skillingStatistics[SkillingStatistics.BARS_SMELTED]++;
						ItemAssistant.deleteItemFromInventory(player, 444, 1);
						ItemAssistant.addItem(player, 2357, 1);
						Skilling.addSkillExperience(player, 23, ServerConstants.SMITHING, false);

						if (!superheatType.isEmpty()) {
							ItemAssistant.deleteItemFromInventory(player, 561, 1);
							if (!ItemAssistant.hasItemEquipped(player, 1387)) {
								ItemAssistant.deleteItemFromInventory(player, 554, 4);
							}
							Skilling.addSkillExperience(player, 53, ServerConstants.MAGIC, false);
							Skilling.updateSkillTabFrontTextMain(player, ServerConstants.MAGIC);
						} else {
							player.startAnimation(899);
							SoundSystem.sendSound(player, 469, 500);
						}
						player.barsToMake--;
					} else {
						player.playerAssistant.sendMessage("You have run out of gold ores.");
						player.getPA().closeInterfaces(true);
						player.smeltInterface = false;
						container.stop();
					}
					if (player.barsToMake <= 0) {
						container.stop();
					}
				}

				@Override
				public void stop() {
					player.barsToMake = 0;
				}
			}, 2);
		}

		if (bar.contains("MITHRIL")) {
			if ((player.baseSkillLevel[13] < 50)) {
				player.getDH().sendStatement("You need at least 50 Smithing to smelt a Mithril bar.");
				return true;
			}
			if (!superheatType.isEmpty()) {
				player.barsToMake = 1;
			} else {
				String[] parse = bar.split(" ");
				player.barsToMake = Integer.parseInt(parse[1]);
			}
			player.getPA().closeInterfaces(true);
			CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
				@Override
				public void execute(CycleEventContainer container) {
					if (ItemAssistant.hasItemAmountInInventory(player, 453, 2)) {
						if (ItemAssistant.hasItemAmountInInventory(player, 447, 1)) {
							if (superheatType.isEmpty()) {
								player.playerAssistant.sendFilterableMessage("You begin to smelt...");
							}
							player.skillingStatistics[SkillingStatistics.BARS_SMELTED]++;
							ItemAssistant.deleteItemFromInventory(player, 447, 1);
							ItemAssistant.deleteItemFromInventory(player, 453, 2);
							ItemAssistant.addItem(player, 2359, 1);
							Skilling.addSkillExperience(player, 30, ServerConstants.SMITHING, false);

							if (!superheatType.isEmpty()) {
								ItemAssistant.deleteItemFromInventory(player, 561, 1);
								if (!ItemAssistant.hasItemEquipped(player, 1387)) {
									ItemAssistant.deleteItemFromInventory(player, 554, 4);
								}
								Skilling.addSkillExperience(player, 53, ServerConstants.MAGIC, false);
								Skilling.updateSkillTabFrontTextMain(player, ServerConstants.MAGIC);
							} else {
								player.startAnimation(899);
								SoundSystem.sendSound(player, 469, 500);
							}
							player.barsToMake--;
						} else {
							player.playerAssistant.sendMessage("You have run out of mithril ores.");
							player.getPA().closeInterfaces(true);
							player.smeltInterface = false;
							container.stop();
						}
					} else {
						player.playerAssistant.sendMessage("You have run out of Coal ores.");
						player.getPA().closeInterfaces(true);
						player.smeltInterface = false;
						container.stop();
					}
					if (player.barsToMake <= 0) {
						container.stop();
					}
				}

				@Override
				public void stop() {
					player.barsToMake = 0;
				}
			}, 2);
		}

		if (bar.contains("ADAMANT")) {
			if ((player.baseSkillLevel[13] < 70)) {
				player.getDH().sendStatement("You need at least 70 Smithing to smelt an Adamantite bar.");
				return true;
			}
			if (!superheatType.isEmpty()) {
				player.barsToMake = 1;
			} else {
				String[] parse = bar.split(" ");
				player.barsToMake = Integer.parseInt(parse[1]);
			}
			player.getPA().closeInterfaces(true);
			CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
				@Override
				public void execute(CycleEventContainer container) {
					if (ItemAssistant.hasItemAmountInInventory(player, 453, 3)) {
						if (ItemAssistant.hasItemAmountInInventory(player, 449, 1)) {
							if (superheatType.isEmpty()) {
								player.playerAssistant.sendFilterableMessage("You begin to smelt...");
							}
							player.skillingStatistics[SkillingStatistics.BARS_SMELTED]++;
							ItemAssistant.deleteItemFromInventory(player, 449, 1);
							ItemAssistant.deleteItemFromInventory(player, 453, 3);
							ItemAssistant.addItem(player, 2361, 1);
							Skilling.addSkillExperience(player, 38, ServerConstants.SMITHING, false);

							if (!superheatType.isEmpty()) {
								ItemAssistant.deleteItemFromInventory(player, 561, 1);
								if (!ItemAssistant.hasItemEquipped(player, 1387)) {
									ItemAssistant.deleteItemFromInventory(player, 554, 4);
								}
								Skilling.addSkillExperience(player, 53, ServerConstants.MAGIC, false);
								Skilling.updateSkillTabFrontTextMain(player, ServerConstants.MAGIC);
							} else {
								player.startAnimation(899);
								SoundSystem.sendSound(player, 469, 500);
							}
							player.barsToMake--;
						} else {
							player.playerAssistant.sendMessage("You have run out of adamantite ores.");
							player.getPA().closeInterfaces(true);
							player.smeltInterface = false;
							container.stop();
						}

					} else {
						player.playerAssistant.sendMessage("You have run out of Coal ores.");
						player.getPA().closeInterfaces(true);
						player.smeltInterface = false;
						container.stop();
					}
					if (player.barsToMake <= 0) {
						container.stop();
					}
				}

				@Override
				public void stop() {
					player.barsToMake = 0;
				}
			}, 2);
		}


		if (bar.contains("RUNE")) {
			if ((player.baseSkillLevel[13] < 85)) {
				player.getDH().sendStatement("You need at least 85 Smithing to smelt a Runite bar.");
				return true;
			}
			if (!superheatType.isEmpty()) {
				player.barsToMake = 1;
			} else {
				String[] parse = bar.split(" ");
				player.barsToMake = Integer.parseInt(parse[1]);
			}
			player.getPA().closeInterfaces(true);
			CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
				@Override
				public void execute(CycleEventContainer container) {
					if (ItemAssistant.hasItemAmountInInventory(player, 453, 4)) {
						if (ItemAssistant.hasItemAmountInInventory(player, 451, 1)) {
							if (superheatType.isEmpty()) {
								player.playerAssistant.sendFilterableMessage("You begin to smelt...");
							}
							player.skillingStatistics[SkillingStatistics.BARS_SMELTED]++;
							ItemAssistant.deleteItemFromInventory(player, 451, 1);
							ItemAssistant.deleteItemFromInventory(player, 453, 4);
							ItemAssistant.addItem(player, 2363, 1);
							Skilling.addSkillExperience(player, 50, ServerConstants.SMITHING, false);

							if (!superheatType.isEmpty()) {
								ItemAssistant.deleteItemFromInventory(player, 561, 1);
								if (!ItemAssistant.hasItemEquipped(player, 1387)) {
									ItemAssistant.deleteItemFromInventory(player, 554, 4);
								}
								Skilling.addSkillExperience(player, 53, ServerConstants.MAGIC, false);
								Skilling.updateSkillTabFrontTextMain(player, ServerConstants.MAGIC);
							} else {
								player.startAnimation(899);
								SoundSystem.sendSound(player, 469, 500);
							}
							player.barsToMake--;
							Achievements.checkCompletionMultiple(player, "1072 1130");
						} else {
							player.playerAssistant.sendMessage("You have run out of rune ores.");
							player.getPA().closeInterfaces(true);
							player.smeltInterface = false;
							container.stop();
						}
					} else {
						player.playerAssistant.sendMessage("You have run out of Coal ores.");
						player.getPA().closeInterfaces(true);
						player.smeltInterface = false;
						container.stop();
					}
					if (player.barsToMake <= 0) {
						container.stop();
					}
				}

				@Override
				public void stop() {
					player.barsToMake = 0;
				}
			}, 2);
		}
		return state;

	}


	public static void sendSmelting(Player c) {
		for (int j = 0; j < SMELT_FRAME.length; j++) {
			c.getPA().sendFrame246(SMELT_FRAME[j], 150, SMELT_BARS[j]);
		}
		c.getPA().sendFrame164(2400);
		c.smeltInterface = true;
	}

	public static void readInput(int level, String type, Player player, int amounttomake) {
		if (player.getActionIdUsed() != 6) {
			return;
		}
		if (ItemAssistant.getItemName(Integer.parseInt(type)).contains("Bronze")) {
			CheckBronze(player, level, amounttomake, type);
		} else if (ItemAssistant.getItemName(Integer.parseInt(type)).contains("Iron")) {
			CheckIron(player, level, amounttomake, type);
		} else if (ItemAssistant.getItemName(Integer.parseInt(type)).contains("Steel")) {
			CheckSteel(player, level, amounttomake, type);
		} else if (ItemAssistant.getItemName(Integer.parseInt(type)).contains("Mith")) {
			CheckMith(player, level, amounttomake, type);
		} else if (ItemAssistant.getItemName(Integer.parseInt(type)).contains("Adam") || ItemAssistant.getItemName(Integer.parseInt(type)).contains("Addy")) {
			CheckAddy(player, level, amounttomake, type);
		} else if (ItemAssistant.getItemName(Integer.parseInt(type)).contains("Rune") || ItemAssistant.getItemName(Integer.parseInt(type)).contains("Runite")) {
			CheckRune(player, level, amounttomake, type);
		}
	}

	private static void CheckIron(Player c, int level, int amounttomake, String type) {
		c.smithingBarToRemove = 2351;

		if (type.equalsIgnoreCase("1349") && level >= 16) //Axe
		{
			c.smithingExperience = 25;
			c.smithingItem = 1349;
			c.smithingRemoveamount = 1;
			c.amountToSmith = amounttomake;
		} else if (type.equalsIgnoreCase("1203") && level >= 15) //Dagger
		{
			c.smithingExperience = 25;
			c.smithingItem = 1203;
			c.smithingRemoveamount = 1;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1420") && level >= 17) //Mace
		{
			c.smithingExperience = 25;
			c.smithingItem = 1420;
			c.smithingRemoveamount = 1;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1137") && level >= 18) //Med helm
		{
			c.smithingExperience = 25;
			c.smithingItem = 1137;
			c.smithingRemoveamount = 1;
			c.amountToSmith = amounttomake;
		} else if (type.equals("9377") && level >= 19) //bolts
		{
			c.smithingExperience = 25;
			c.smithingItem = 9377;
			c.smithingRemoveamount = 1;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1279") && level >= 19) //Sword (s)
		{
			c.smithingExperience = 25;
			c.smithingItem = 1279;
			c.smithingRemoveamount = 1;
			c.amountToSmith = amounttomake;
		} else if (type.equals("4820") && level >= 19) //Nails
		{
			c.smithingExperience = 25;
			c.smithingItem = 4820;
			c.smithingRemoveamount = 1;
			c.amountToSmith = amounttomake;
		} else if (type.equals("820") && level >= 19) //Dart tips
		{
			c.smithingExperience = 25;
			c.smithingItem = 820;
			c.smithingRemoveamount = 1;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1323") && level >= 20) //Scim
		{
			c.smithingExperience = 50;
			c.smithingItem = 1323;
			c.smithingRemoveamount = 2;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1293") && level >= 21) //Longsword
		{
			c.smithingExperience = 50;
			c.smithingItem = 1293;
			c.smithingRemoveamount = 2;
			c.amountToSmith = amounttomake;
		} else if (type.equals("863") && level >= 22) //Knives
		{
			c.smithingExperience = 25;
			c.smithingItem = 863;
			c.smithingRemoveamount = 1;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1153") && level >= 22) //Full Helm
		{
			c.smithingExperience = 50;
			c.smithingItem = 1153;
			c.smithingRemoveamount = 2;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1175") && level >= 23) //Square shield
		{
			c.smithingExperience = 50;
			c.smithingItem = 1175;
			c.smithingRemoveamount = 2;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1335") && level >= 24) //Warhammer
		{
			c.smithingExperience = 38;
			c.smithingItem = 1335;
			c.smithingRemoveamount = 3;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1363") && level >= 25) //Battle axe
		{
			c.smithingExperience = 75;
			c.smithingItem = 1363;
			c.smithingRemoveamount = 3;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1101") && level >= 26) //Chain
		{
			c.smithingExperience = 75;
			c.smithingItem = 1101;
			c.smithingRemoveamount = 3;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1191") && level >= 27) //Kite
		{
			c.smithingExperience = 75;
			c.smithingItem = 1191;
			c.smithingRemoveamount = 3;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1309") && level >= 29) //2h Sword
		{
			c.smithingExperience = 75;
			c.smithingItem = 1309;
			c.smithingRemoveamount = 3;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1067") && level >= 31) //Platelegs
		{
			c.smithingExperience = 75;
			c.smithingItem = 1067;
			c.smithingRemoveamount = 3;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1081") && level >= 31) //PlateSkirt
		{
			c.smithingExperience = 75;
			c.smithingItem = 1081;
			c.smithingRemoveamount = 3;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1115") && level >= 33) //Platebody
		{
			c.smithingExperience = 100;
			c.smithingItem = 1115;
			c.smithingRemoveamount = 5;
			c.amountToSmith = amounttomake;
		} else {
			c.playerAssistant.sendMessage("You don't have a high enough level to make this Item!");
			return;
		}

		doaction(c, c.smithingItem, c.smithingBarToRemove, c.smithingRemoveamount, c.amountToSmith, -1, -1, c.smithingExperience);

	}

	private static void CheckSteel(Player c, int level, int amounttomake, String type) {
		c.smithingBarToRemove = 2353;

		if (type.equalsIgnoreCase("1353") && level >= 31) //Axe
		{
			c.smithingExperience = 38;
			c.smithingItem = 1353;
			c.smithingRemoveamount = 1;
			c.amountToSmith = amounttomake;
		} else if (type.equalsIgnoreCase("1207") && level >= 30) //Dagger
		{
			c.smithingExperience = 38;
			c.smithingItem = 1207;
			c.smithingRemoveamount = 1;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1424") && level >= 32) //Mace
		{
			c.smithingExperience = 38;
			c.smithingItem = 1424;
			c.smithingRemoveamount = 1;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1141") && level >= 33) //Med helm
		{
			c.smithingExperience = 38;
			c.smithingItem = 1141;
			c.smithingRemoveamount = 1;
			c.amountToSmith = amounttomake;
		} else if (type.equals("9378") && level >= 34) //bolts
		{
			c.smithingExperience = 38;
			c.smithingItem = 9378;
			c.smithingRemoveamount = 1;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1281") && level >= 34) //Sword (s)
		{
			c.smithingExperience = 38;
			c.smithingItem = 1281;
			c.smithingRemoveamount = 1;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1539") && level >= 34) //Nails
		{
			c.smithingExperience = 38;
			c.smithingItem = 1539;
			c.smithingRemoveamount = 1;
			c.amountToSmith = amounttomake;
		} else if (type.equals("821") && level >= 34) //Dart tips
		{
			c.smithingExperience = 38;
			c.smithingItem = 821;
			c.smithingRemoveamount = 1;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1325") && level >= 35) //Scim
		{
			c.smithingExperience = 75;
			c.smithingItem = 1325;
			c.smithingRemoveamount = 2;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1295") && level >= 36) //Longsword
		{
			c.smithingExperience = 75;
			c.smithingItem = 1295;
			c.smithingRemoveamount = 2;
			c.amountToSmith = amounttomake;
		} else if (type.equals("865") && level >= 37) //Knives
		{
			c.smithingExperience = 38;
			c.smithingItem = 865;
			c.smithingRemoveamount = 1;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1157") && level >= 37) //Full Helm
		{
			c.smithingExperience = 75;
			c.smithingItem = 1157;
			c.smithingRemoveamount = 2;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1177") && level >= 38) //Square shield
		{
			c.smithingExperience = 75;
			c.smithingItem = 1177;
			c.smithingRemoveamount = 2;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1339") && level >= 39) //Warhammer
		{
			c.smithingExperience = 113;
			c.smithingItem = 1339;
			c.smithingRemoveamount = 3;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1365") && level >= 40) //Battle axe
		{
			c.smithingExperience = 113;
			c.smithingItem = 1365;
			c.smithingRemoveamount = 3;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1105") && level >= 41) //Chain
		{
			c.smithingExperience = 113;
			c.smithingItem = 1105;
			c.smithingRemoveamount = 3;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1193") && level >= 42) //Kite
		{
			c.smithingExperience = 113;
			c.smithingItem = 1193;
			c.smithingRemoveamount = 3;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1311") && level >= 44) //2h Sword
		{
			c.smithingExperience = 113;
			c.smithingItem = 1311;
			c.smithingRemoveamount = 3;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1069") && level >= 46) //Platelegs
		{
			c.smithingExperience = 113;
			c.smithingItem = 1069;
			c.smithingRemoveamount = 3;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1083") && level >= 46) //PlateSkirt
		{
			c.smithingExperience = 113;
			c.smithingItem = 1083;
			c.smithingRemoveamount = 3;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1119") && level >= 48) //Platebody
		{
			c.smithingExperience = 188;
			c.smithingItem = 1119;
			c.smithingRemoveamount = 5;
			c.amountToSmith = amounttomake;
		} else {
			c.playerAssistant.sendMessage("You don't have a high enough level to make this Item!");
			return;
		}

		doaction(c, c.smithingItem, c.smithingBarToRemove, c.smithingRemoveamount, c.amountToSmith, -1, -1, c.smithingExperience);

	}

	private static void CheckMith(Player player, int level, int amounttomake, String type) {
		player.smithingBarToRemove = 2359;

		if (type.equalsIgnoreCase("1355") && level >= 51) //Axe
		{
			player.smithingExperience = 50;
			player.smithingItem = 1355;
			player.smithingRemoveamount = 1;
			player.amountToSmith = amounttomake;
		} else if (type.equalsIgnoreCase("1209") && level >= 50) //Dagger
		{
			player.smithingExperience = 50;
			player.smithingItem = 1209;
			player.smithingRemoveamount = 1;
			player.amountToSmith = amounttomake;
		} else if (type.equals("1428") && level >= 52) //Mace
		{
			player.smithingExperience = 50;
			player.smithingItem = 1428;
			player.smithingRemoveamount = 1;
			player.amountToSmith = amounttomake;
		} else if (type.equals("1143") && level >= 53) //Med helm
		{
			player.smithingExperience = 50;
			player.smithingItem = 1143;
			player.smithingRemoveamount = 1;
			player.amountToSmith = amounttomake;
		} else if (type.equals("9379") && level >= 54) //bolts
		{
			player.smithingExperience = 50;
			player.smithingItem = 9379;
			player.smithingRemoveamount = 1;
			player.amountToSmith = amounttomake;
		} else if (type.equals("1285") && level >= 54) //Sword (s)
		{
			player.smithingExperience = 50;
			player.smithingItem = 1285;
			player.smithingRemoveamount = 1;
			player.amountToSmith = amounttomake;
		} else if (type.equals("4822") && level >= 54) //Nails
		{
			player.smithingExperience = 50;
			player.smithingItem = 4822;
			player.smithingRemoveamount = 1;
			player.amountToSmith = amounttomake;
		} else if (type.equals("822") && level >= 54) //Dart tips
		{
			player.smithingExperience = 50;
			player.smithingItem = 822;
			player.smithingRemoveamount = 1;
			player.amountToSmith = amounttomake;
		} else if (type.equals("1329") && level >= 55) //Scim
		{
			player.smithingExperience = 100;
			player.smithingItem = 1329;
			player.smithingRemoveamount = 2;
			player.amountToSmith = amounttomake;
		} else if (type.equals("1299") && level >= 56) //Longsword
		{
			player.smithingExperience = 100;
			player.smithingItem = 1299;
			player.smithingRemoveamount = 2;
			player.amountToSmith = amounttomake;
		} else if (type.equals("866") && level >= 57) //Knives
		{
			player.smithingExperience = 50;
			player.smithingItem = 866;
			player.smithingRemoveamount = 1;
			player.amountToSmith = amounttomake;
		} else if (type.equals("1159") && level >= 57) //Full Helm
		{
			player.smithingExperience = 100;
			player.smithingItem = 1159;
			player.smithingRemoveamount = 2;
			player.amountToSmith = amounttomake;
		} else if (type.equals("1181") && level >= 58) //Square shield
		{
			player.smithingExperience = 100;
			player.smithingItem = 1181;
			player.smithingRemoveamount = 2;
			player.amountToSmith = amounttomake;
		} else if (type.equals("1343") && level >= 59) //Warhammer
		{
			player.smithingExperience = 150;
			player.smithingItem = 1343;
			player.smithingRemoveamount = 3;
			player.amountToSmith = amounttomake;
		} else if (type.equals("1369") && level >= 60) //Battle axe
		{
			player.smithingExperience = 150;
			player.smithingItem = 1369;
			player.smithingRemoveamount = 3;
			player.amountToSmith = amounttomake;
		} else if (type.equals("1109") && level >= 61) //Chain
		{
			player.smithingExperience = 150;
			player.smithingItem = 1109;
			player.smithingRemoveamount = 3;
			player.amountToSmith = amounttomake;
		} else if (type.equals("1197") && level >= 62) //Kite
		{
			player.smithingExperience = 150;
			player.smithingItem = 1197;
			player.smithingRemoveamount = 3;
			player.amountToSmith = amounttomake;
		} else if (type.equals("1315") && level >= 64) //2h Sword
		{
			player.smithingExperience = 150;
			player.smithingItem = 1315;
			player.smithingRemoveamount = 3;
			player.amountToSmith = amounttomake;
		} else if (type.equals("1071") && level >= 66) //Platelegs
		{
			player.smithingExperience = 150;
			player.smithingItem = 1071;
			player.smithingRemoveamount = 3;
			player.amountToSmith = amounttomake;
		} else if (type.equals("1085") && level >= 66) //PlateSkirt
		{
			player.smithingExperience = 150;
			player.smithingItem = 1085;
			player.smithingRemoveamount = 3;
			player.amountToSmith = amounttomake;
		} else if (type.equals("1121") && level >= 68) //Platebody
		{
			player.smithingExperience = 250;
			player.smithingItem = 1121;
			player.smithingRemoveamount = 5;
			player.amountToSmith = amounttomake;
		} else {
			player.playerAssistant.sendMessage("You don't have a high enough level to make this Item!");
			return;
		}

		doaction(player, player.smithingItem, player.smithingBarToRemove, player.smithingRemoveamount, player.amountToSmith, -1, -1, player.smithingExperience);


	}

	private static void CheckRune(Player c, int level, int amounttomake, String type) {
		c.smithingBarToRemove = 2363;

		if (type.equalsIgnoreCase("1359") && level >= 86) //Axe
		{
			c.smithingExperience = 75;
			c.smithingItem = 1359;
			c.smithingRemoveamount = 1;
			c.amountToSmith = amounttomake;
		} else if (type.equalsIgnoreCase("1213") && level >= 85) //Dagger
		{
			c.smithingExperience = 75;
			c.smithingItem = 1213;
			c.smithingRemoveamount = 1;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1432") && level >= 87) //Mace
		{
			c.smithingExperience = 75;
			c.smithingItem = 1432;
			c.smithingRemoveamount = 1;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1147") && level >= 88) //Med helm
		{
			c.smithingExperience = 75;
			c.smithingItem = 1147;
			c.smithingRemoveamount = 1;
			c.amountToSmith = amounttomake;
		} else if (type.equals("9381") && level >= 89) //bolts
		{

			c.smithingExperience = 75;
			c.smithingItem = 9381;
			c.smithingRemoveamount = 1;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1289") && level >= 89) //Sword (s)
		{
			c.smithingExperience = 75;
			c.smithingItem = 1289;
			c.smithingRemoveamount = 1;
			c.amountToSmith = amounttomake;
		} else if (type.equals("4824") && level >= 89) //Nails
		{
			c.smithingExperience = 75;
			c.smithingItem = 4824;
			c.smithingRemoveamount = 1;
			c.amountToSmith = amounttomake;
		}
		else if (type.equals("824") && level >= 89) //Dart tips
		{
			c.smithingExperience = 75;
			c.smithingItem = 824;
			c.smithingRemoveamount = 1;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1333") && level >= 90) //Scim
		{
			c.smithingExperience = 150;
			c.smithingItem = 1333;
			c.smithingRemoveamount = 2;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1303") && level >= 91) //Longsword
		{
			c.smithingExperience = 150;
			c.smithingItem = 1303;
			c.smithingRemoveamount = 2;
			c.amountToSmith = amounttomake;
		} else if (type.equals("9431") && level >= 91) //Runite limbs
		{
			c.smithingExperience = 75;
			c.smithingItem = 9431;
			c.smithingRemoveamount = 1;
			c.amountToSmith = amounttomake;
		} else if (type.equals("868") && level >= 92) //Knives
		{
			c.smithingExperience = 75;
			c.smithingItem = 868;
			c.smithingRemoveamount = 1;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1163") && level >= 92) //Full Helm
		{
			c.smithingExperience = 150;
			c.smithingItem = 1163;
			c.smithingRemoveamount = 2;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1185") && level >= 93) //Square shield
		{
			c.smithingExperience = 150;
			c.smithingItem = 1185;
			c.smithingRemoveamount = 2;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1347") && level >= 94) //Warhammer
		{
			c.smithingExperience = 225;
			c.smithingItem = 1347;
			c.smithingRemoveamount = 3;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1373") && level >= 95) //Battle axe
		{
			c.smithingExperience = 225;
			c.smithingItem = 1373;
			c.smithingRemoveamount = 3;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1113") && level >= 96) //Chain
		{
			c.smithingExperience = 225;
			c.smithingItem = 1113;
			c.smithingRemoveamount = 3;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1201") && level >= 97) //Kite
		{
			c.smithingExperience = 225;
			c.smithingItem = 1201;
			c.smithingRemoveamount = 3;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1319") && level >= 99) //2h Sword
		{
			c.smithingExperience = 225;
			c.smithingItem = 1319;
			c.smithingRemoveamount = 3;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1079") && level >= 99) //Platelegs
		{
			c.smithingExperience = 225;
			c.smithingItem = 1079;
			c.smithingRemoveamount = 3;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1093") && level >= 99) //PlateSkirt
		{
			c.smithingExperience = 225;
			c.smithingItem = 1093;
			c.smithingRemoveamount = 3;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1127") && level >= 99) //Platebody
		{
			c.smithingExperience = 375;
			c.smithingItem = 1127;
			c.smithingRemoveamount = 5;
			c.amountToSmith = amounttomake;
		} else {
			c.playerAssistant.sendMessage("You don't have a high enough level to make this Item!");
			return;
		}

		doaction(c, c.smithingItem, c.smithingBarToRemove, c.smithingRemoveamount, c.amountToSmith, -1, -1, c.smithingExperience);

	}

	private static void CheckAddy(Player c, int level, int amounttomake, String type) {
		c.smithingBarToRemove = 2361;

		if (type.equalsIgnoreCase("1357") && level >= 71) //Axe
		{
			c.smithingExperience = 63;
			c.smithingItem = 1357;
			c.smithingRemoveamount = 1;
			c.amountToSmith = amounttomake;
		} else if (type.equalsIgnoreCase("1211") && level >= 70) //Dagger
		{
			c.smithingExperience = 63;
			c.smithingItem = 1211;
			c.smithingRemoveamount = 1;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1430") && level >= 72) //Mace
		{
			c.smithingExperience = 63;
			c.smithingItem = 1430;
			c.smithingRemoveamount = 1;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1145") && level >= 73) //Med helm
		{
			c.smithingExperience = 63;
			c.smithingItem = 1145;
			c.smithingRemoveamount = 1;
			c.amountToSmith = amounttomake;
		} else if (type.equals("9380") && level >= 74) //bolts
		{
			c.smithingExperience = 63;
			c.smithingItem = 9380;
			c.smithingRemoveamount = 1;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1287") && level >= 74) //Sword (s)
		{
			c.smithingExperience = 63;
			c.smithingItem = 1287;
			c.smithingRemoveamount = 1;
			c.amountToSmith = amounttomake;
		} else if (type.equals("4823") && level >= 74) //Nails
		{
			c.smithingExperience = 63;
			c.smithingItem = 4823;
			c.smithingRemoveamount = 1;
			c.amountToSmith = amounttomake;
		} else if (type.equals("823") && level >= 75) //Dart tips
		{
			c.smithingExperience = 63;
			c.smithingItem = 823;
			c.smithingRemoveamount = 1;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1331") && level >= 75) //Scim
		{
			c.smithingExperience = 125;
			c.smithingItem = 1331;
			c.smithingRemoveamount = 2;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1301") && level >= 76) //Longsword
		{
			c.smithingExperience = 125;
			c.smithingItem = 1301;
			c.smithingRemoveamount = 2;
			c.amountToSmith = amounttomake;
		} else if (type.equals("867") && level >= 77) //Knives
		{
			c.smithingExperience = 63;
			c.smithingItem = 867;
			c.smithingRemoveamount = 1;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1161") && level >= 77) //Full Helm
		{
			c.smithingExperience = 125;
			c.smithingItem = 1161;
			c.smithingRemoveamount = 2;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1183") && level >= 78) //Square shield
		{
			c.smithingExperience = 125;
			c.smithingItem = 1183;
			c.smithingRemoveamount = 2;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1345") && level >= 79) //Warhammer
		{
			c.smithingExperience = 188;
			c.smithingItem = 1345;
			c.smithingRemoveamount = 3;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1371") && level >= 80) //Battle axe
		{
			c.smithingExperience = 188;
			c.smithingItem = 1371;
			c.smithingRemoveamount = 3;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1111") && level >= 81) //Chain
		{
			c.smithingExperience = 188;
			c.smithingItem = 1111;
			c.smithingRemoveamount = 3;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1199") && level >= 82) //Kite
		{
			c.smithingExperience = 188;
			c.smithingItem = 1199;
			c.smithingRemoveamount = 3;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1317") && level >= 84) //2h Sword
		{
			c.smithingExperience = 188;
			c.smithingItem = 1317;
			c.smithingRemoveamount = 3;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1073") && level >= 86) //Platelegs
		{
			c.smithingExperience = 188;
			c.smithingItem = 1073;
			c.smithingRemoveamount = 3;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1091") && level >= 86) //PlateSkirt
		{
			c.smithingExperience = 188;
			c.smithingItem = 1091;
			c.smithingRemoveamount = 3;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1123") && level >= 88) //Platebody
		{
			c.smithingExperience = 313;
			c.smithingItem = 1123;
			c.smithingRemoveamount = 5;
			c.amountToSmith = amounttomake;
		} else {
			c.playerAssistant.sendMessage("You don't have a high enough level to make this Item!");
			return;
		}

		doaction(c, c.smithingItem, c.smithingBarToRemove, c.smithingRemoveamount, c.amountToSmith, -1, -1, c.smithingExperience);

	}

	private static void CheckBronze(Player c, int level, int amounttomake, String type) {
		if (type.equalsIgnoreCase("1351") && level >= 1) {
			c.smithingExperience = 13;
			c.smithingItem = 1351;
			c.smithingBarToRemove = 2349;
			c.smithingRemoveamount = 1;
			c.amountToSmith = amounttomake;
		} else if (type.equalsIgnoreCase("9375") && level >= 1) {
			c.smithingExperience = 13;
			c.smithingItem = 9375;
			c.smithingRemoveamount = 1;
			c.amountToSmith = amounttomake;
		} else if (type.equalsIgnoreCase("1205") && level >= 1) {
			c.smithingExperience = 13;
			c.smithingItem = 1205;
			c.smithingBarToRemove = 2349;
			c.smithingRemoveamount = 1;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1422") && level >= 2) {
			c.smithingExperience = 13;
			c.smithingItem = 1422;
			c.smithingBarToRemove = 2349;
			c.smithingRemoveamount = 1;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1139") && level >= 3) {
			c.smithingExperience = 13;
			c.smithingItem = 1139;
			c.smithingBarToRemove = 2349;
			c.smithingRemoveamount = 1;
			c.amountToSmith = amounttomake;
		} else if (type.equals("819") && level >= 4) {
			c.smithingExperience = 13;
			c.smithingItem = 819;
			c.smithingBarToRemove = 2349;
			c.smithingRemoveamount = 1;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1277") && level >= 4) {
			c.smithingExperience = 13;
			c.smithingItem = 1277;
			c.smithingBarToRemove = 2349;
			c.smithingRemoveamount = 1;
			c.amountToSmith = amounttomake;
		} else if (type.equals("4819") && level >= 4) {
			c.smithingExperience = 13;
			c.smithingItem = 4819;
			c.smithingBarToRemove = 2349;
			c.smithingRemoveamount = 1;
			c.amountToSmith = amounttomake;
		} else if (type.equals("39") && level >= 5) {
			c.smithingExperience = 13;
			c.smithingItem = 39;
			c.smithingBarToRemove = 2349;
			c.smithingRemoveamount = 1;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1321") && level >= 5) {
			c.smithingExperience = 25;
			c.smithingItem = 1321;
			c.smithingBarToRemove = 2349;
			c.smithingRemoveamount = 2;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1291") && level >= 6) {
			c.smithingExperience = 25;
			c.smithingItem = 1291;
			c.smithingBarToRemove = 2349;
			c.smithingRemoveamount = 2;
			c.amountToSmith = amounttomake;
		} else if (type.equals("864") && level >= 7) {
			c.smithingExperience = 25;
			c.smithingItem = 864;
			c.smithingBarToRemove = 2349;
			c.smithingRemoveamount = 1;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1155") && level >= 7) {
			c.smithingExperience = 25;
			c.smithingItem = 1155;
			c.smithingBarToRemove = 2349;
			c.smithingRemoveamount = 2;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1173") && level >= 8) {
			c.smithingExperience = 25;
			c.smithingItem = 1173;
			c.smithingBarToRemove = 2349;
			c.smithingRemoveamount = 2;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1337") && level >= 9) {
			c.smithingExperience = 38;
			c.smithingItem = 1337;
			c.smithingBarToRemove = 2349;
			c.smithingRemoveamount = 3;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1375") && level >= 10) {
			c.smithingExperience = 38;
			c.smithingItem = 1375;
			c.smithingBarToRemove = 2349;
			c.smithingRemoveamount = 3;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1103") && level >= 11) {
			c.smithingExperience = 38;
			c.smithingItem = 1103;
			c.smithingBarToRemove = 2349;
			c.smithingRemoveamount = 3;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1189") && level >= 12) {
			c.smithingExperience = 38;
			c.smithingItem = 1189;
			c.smithingBarToRemove = 2349;
			c.smithingRemoveamount = 3;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1307") && level >= 14) {
			c.smithingExperience = 38;
			c.smithingItem = 1307;
			c.smithingBarToRemove = 2349;
			c.smithingRemoveamount = 3;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1075") && level >= 16) {
			c.smithingExperience = 38;
			c.smithingItem = 1075;
			c.smithingBarToRemove = 2349;
			c.smithingRemoveamount = 3;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1087") && level >= 16) {
			c.smithingExperience = 38;
			c.smithingItem = 1087;
			c.smithingBarToRemove = 2349;
			c.smithingRemoveamount = 3;
			c.amountToSmith = amounttomake;
		} else if (type.equals("1117") && level >= 18) {
			c.smithingExperience = 63;
			c.smithingItem = 1117;
			c.smithingBarToRemove = 2349;
			c.smithingRemoveamount = 5;
			c.amountToSmith = amounttomake;
		} else {
			c.playerAssistant.sendMessage("You don't have a high enough level to make this Item!");
			return;
		}

		doaction(c, c.smithingItem, c.smithingBarToRemove, c.smithingRemoveamount, c.amountToSmith, -1, -1, c.smithingExperience);

	}

	public static boolean doaction(final Player player, final int toProduce, final int toremove, final int toRemoveAmount, int timestomake, int NOTUSED, int NOTUSED2,
	                               final int xp) {
		if (RandomEvent.isBannedFromSkilling(player)) {
			return false;
		}
		player.smithingAmountToMake = timestomake;
		player.getPA().closeInterfaces(true);
		if (ItemAssistant.hasItemAmountInInventory(player, toremove, toRemoveAmount)) {
			if (Skilling.cannotActivateNewSkillingEvent(player)) {
				return false;
			}
			player.startAnimation(898);
			SoundSystem.sendSound(player, 468, 0);
			if (player.smithingAmountToMake > 1 && ItemAssistant.hasItemAmountInInventory(player, toremove, toRemoveAmount * 2)) {
				player.playerAssistant
						.sendFilterableMessage("You hammer the " + ItemAssistant.getItemName(toremove) + " and make some " + ItemAssistant.getItemName(toProduce) + ".");
			} else {
				String text = ItemDefinition.getDefinitions()[toProduce].stackable ? "some" : "a";
				player.playerAssistant
						.sendFilterableMessage("You hammer the " + ItemAssistant.getItemName(toremove) + " and make " + text + " " + ItemAssistant.getItemName(toProduce) + ".");
			}
			CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
				@Override
				public void execute(CycleEventContainer container) {
					if (Skilling.forceStopSkillingEvent(player)) {
						container.stop();
						return;
					}
					if (player.smithingAmountToMake == 0) {
						container.stop();
						return;
					}
					if (ItemAssistant.hasItemAmountInInventory(player, toremove, toRemoveAmount)) {
						player.startAnimation(898);
						// Adamant dagger.
						if (toProduce == 1211) {
							Achievements.checkCompletionMultiple(player, "1035");
						}
						// Rune platebody.
						else if (toProduce == 1127) {
							Achievements.checkCompletionMultiple(player, "1061");
						}
						player.skillingStatistics[SkillingStatistics.ITEMS_SMITHED]++;
						if (Skilling.hasMasterCapeWorn(player, 9795) && Misc.hasPercentageChance(10)) {
							player.getPA().sendMessage("<col=a54704>Your cape allows you to save " + toRemoveAmount + " bars from being used.");
						} else {
							ItemAssistant.deleteItemFromInventory(player, toremove, toRemoveAmount);
						}
						int chance = 0;


						if (player.isInZombiesMinigame()) {
							chance = 0;
						}

						if (ItemAssistant.getItemName(toProduce).contains("bolt")) {
							ItemAssistant.addItemToInventoryOrDrop(player, toProduce, (10 * (player.isInZombiesMinigame() ? 2 : 1)) * (Misc.hasPercentageChance(chance) ? 2 : 1));
						} else if (ItemAssistant.getItemName(toProduce).contains("nail")) {
							ItemAssistant.addItemToInventoryOrDrop(player, toProduce, 15 * (Misc.hasPercentageChance(chance) ? 2 : 1));
						} else if (ItemAssistant.getItemName(toProduce).contains("dart")) {
							ItemAssistant.addItemToInventoryOrDrop(player, toProduce, 10 * (Misc.hasPercentageChance(chance) ? 2 : 1));
						} else if (ItemAssistant.getItemName(toProduce).contains("arrow")) {
							ItemAssistant.addItemToInventoryOrDrop(player, toProduce, (15 * (player.isInZombiesMinigame() ? 2 : 1)) * (Misc.hasPercentageChance(chance) ? 2 : 1));
						} else if (ItemAssistant.getItemName(toProduce).contains("knife")) {
							ItemAssistant.addItemToInventoryOrDrop(player, toProduce, (5 * (player.isInZombiesMinigame() ? 2 : 1)) * (Misc.hasPercentageChance(chance) ? 2 : 1));
						} else if (ItemAssistant.getItemName(toProduce).contains("cannon")) {
							ItemAssistant.addItemToInventoryOrDrop(player, toProduce, 4 * (Misc.hasPercentageChance(chance) ? 2 : 1));
						} else {
							ItemAssistant.addItemToInventoryOrDrop(player, toProduce, 1 * (Misc.hasPercentageChance(chance) ? 2 : 1));
						}
						Skilling.addSkillExperience(player, xp, ServerConstants.SMITHING, false);
						int bloodMoney = (int) (0.67 * xp);
						if (!GameType.isOsrsEco()) {
							if (bloodMoney > 0) {
								ItemAssistant.addItemToInventoryOrDrop(player, 13307, bloodMoney);
								CoinEconomyTracker.addIncomeList(player, "SKILLING " + bloodMoney);
							}
						}
						player.smithingAmountToMake--;
					} else {
						container.stop();
					}


				}

				@Override
				public void stop() {
					Skilling.endSkillingEvent(player);
				}
			}, 3);
		} else {
			player.playerAssistant.sendMessage("You don't have enough bars to make this item!");
			return false;
		}
		return true;
	}
}
