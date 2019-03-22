package game.content.combat;

import java.util.ArrayList;

import core.ServerConstants;
import game.content.combat.vsplayer.range.RangedData;
import game.content.consumable.Food;
import game.content.interfaces.InterfaceAssistant;
import game.content.miscellaneous.OverlayTimer;
import game.content.miscellaneous.RunePouch;
import game.content.quicksetup.QuickSetUp;
import game.content.wildernessbonus.WildernessRisk;
import game.content.worldevent.BloodKey;
import game.item.ItemAssistant;
import game.item.ItemDefinition;
import game.player.Area;
import game.player.Player;
import game.player.PlayerHandler;
import game.player.punishment.RagBan;
import utility.Misc;

/**
 * Edgeville 1-5 Wilderness and West dragons wilderness rules.
 *
 * @author MGT Madness, created on 21-01-2017.
 */
public class EdgeAndWestsRule {

	/**
	 * Time in seconds since the player left a dangerous area and entered the Edgeville 1-5 wilderness or West dragons area where rules are applied.
	 */
	public final static int TIME_SINCE_LEAVING_EDGE_OR_WESTS_TO_BE_SAFE = 300;

	/**
	 * Time in seconds that the player will be immune from being attacked by other players after the player gets a kill while under protection.
	 */
	public final static int TIME_IMMUNE_FROM_BEING_ATTACKED = 15;

	public final static int MAXIMUM_EXCESS_BREW_DOSES = 24;



	/**
	 * @return True if the rules of Edgeville wilderness applies to the player.<p>
	 * If the player has not entered a Wilderness area other than Edgeville 1-5 Wild and West dragons area in
	 * the past 3 minutes, then the rule applies to the player.<p>
	 * The rules are:<p>
	 * If the player is 20 defence or less, enemies with 21 defence or more cannot attack this player.
	 * Enemies cannot teleblock this player.
	 * Blacklisted enemies cannot attack the player.
	 * Enemies with Tribrid cannot attack a player using Hybrid.
	 * Enemies with 2 brews or more cannot attack a player using 1 brew.
	 * Enemies with magic gear cannot attack you if you have melee only.
	 * If i am risking 1k or more blood money worth, then players with less than 50% of my risk cannot attack me.
	 * No one can pj the fight at all.
	 * Cannot pickup or drop food.
	 * Cannot pickup a Ranged item.
	 * If the player gets a kill, the player has 30 seconds of immunity from enemies so he can teleport out.
	 */
	public static boolean isEdgeOrWestRule(Player attacker, Player victim, String action) {
		if (victim.getPlayerIdAttacking() == attacker.getPlayerId()) {
			return false;
		}
		if (victim.getUnderAttackBy() == attacker.getPlayerId()) {
			return false;
		}
		if (victim.bot) {
			return false;
		}

		// This is the fix for if i mage someone manually just once, they cannot melee me at all, it needs to be like this.
		if (victim.lastPlayerAttackedName.equals(attacker.getPlayerName()) && System.currentTimeMillis() - victim.timeInPlayerCombat <= 10000) {
			return false;
		}


		boolean attackerHasF2p = QuickSetUp.isUsingF2pOnly(attacker, false, true);
		boolean victimHasF2p = QuickSetUp.isUsingF2pOnly(victim, false, true);

		if (!attackerHasF2p && victimHasF2p) {
			attacker.getPA().sendMessage(victim.getPlayerName() + " is under f2p protection.");
			QuickSetUp.isUsingF2pOnly(attacker, true, true);
			return true;
		}

		if (attackerHasF2p && !victimHasF2p) {
			attacker.getDH().sendStatement(victim.getPlayerName() + " is not wearing full f2p");
			return true;
		}


		if (Area.inDangerousPvpArea(attacker) || Area.inDangerousPvpArea(victim)) {
			if (!attacker.bot) {
				WildernessRisk.hasWildernessActivityRisk(attacker, 0);
			}
			if (!victim.bot) {
				WildernessRisk.hasWildernessActivityRisk(victim, 0);
			}
			boolean victimHasBloodkey = BloodKey.hasAnyBloodKeyInInventory(victim);
			if (victimHasBloodkey) {
				attacker.timeAttackedBloodkeyHolder = System.currentTimeMillis();
			}
			if (attacker.wildernessRiskAmount < ServerConstants.getRiskRequiredToAttackAnyPlayer()) {
				if (victim.wildernessRiskAmount >= ServerConstants.getRiskRequiredForWildProtection() && attacker.wildernessRiskAmount < victim.wildernessRiskAmount / 5
				    && !Area.inMulti(victim.getX(), victim.getY())) {
					if (!victimHasBloodkey) {
						if (System.currentTimeMillis() - victim.timeAttackedBloodkeyHolder >= 120000) {
							long riskNeeded = (victim.wildernessRiskAmount / 5);
							if (riskNeeded > ServerConstants.getRiskRequiredToAttackAnyPlayer()) {
								riskNeeded = ServerConstants.getRiskRequiredToAttackAnyPlayer();
							}
							attacker.getPA().sendMessage("You need at least a risk of " + Misc.formatNumber(riskNeeded) + " blood money to attack this player.");
							return true;
						}
					}
				}
			}
		}
		if (!EdgeAndWestsRule.isUnderEdgeAndWestsProtectionRules(victim)) {
			return false;
		}
		String location = "Edgeville";
		if (victim.getHeight() == 4) {
			location = "Edge Pvp";
		}

		boolean warningSent = false;
		switch (action) {
			case "NORMAL ATTACK":

				// Cannot attack a player at all.
				for (int b = 0; b < victim.pvpBlacklist.size(); b++) {
					if (attacker.getPlayerName().toLowerCase().equals(victim.pvpBlacklist.get(b))) {
						if (!EdgeAndWestsRule.bothPlayersCanByPass(attacker, victim)) {
							attacker.getPA().sendMessage(victim.getPlayerName() + " is under " + location + " protection.");
							attacker.getPA().sendMessage("You are blacklisted from attacking this player.");
							attacker.playerTriedToAttack = victim.getPlayerName();
							attacker.timeTriedToAttackPlayer = System.currentTimeMillis();
							return true;
						}
					}
				}
				if (attacker.getBaseDefenceLevel() > 20 && victim.getBaseDefenceLevel() <= 1) {
					if (!EdgeAndWestsRule.bothPlayersCanByPass(attacker, victim)) {
						attacker.getPA().sendMessage(victim.getPlayerName() + " is under " + location + " protection.");
						attacker.getPA().sendMessage("You need 20 defence or less to attack this player.");
						attacker.playerTriedToAttack = victim.getPlayerName();
						attacker.timeTriedToAttackPlayer = System.currentTimeMillis();
						return true;
					}
				}
				boolean attackerTribrid = isTribrid(attacker);
				boolean victimTribrid = isTribrid(victim);

				if (attackerTribrid && attacker.getHeight() == 4 && !victimTribrid) {

					attacker.getPA().sendMessage(victim.getPlayerName() + " is under " + location + " protection.");
					attacker.getPA().sendMessage("You cannot use Ranged here.");
					attacker.playerTriedToAttack = victim.getPlayerName();
					attacker.timeTriedToAttackPlayer = System.currentTimeMillis();
					return true;
				}
				/*
				if (!attacker.isBot && !victim.isBot)
				{
						if (attackerTribrid)
						{
								if (!victimTribrid)
								{
										if (!EdgeAndWestsRule.bothPlayersCanByPass(attacker, victim))
										{
												attacker.getPA().sendMessage(victim.getPlayerName() + " is under " + location + " protection.");
												attacker.getPA().sendMessage("You need hybrid to attack this hybrid.");
												attacker.playerTriedToAttack = victim.getPlayerName();
												attacker.timeTriedToAttackPlayer = System.currentTimeMillis();
												return true;
										}
								}
						}
				}
				// Prevent a mager to attack a player with melee only.
				if (!victim.hasMagicEquipment && victim.combatStylesUsed == 1 && !victim.hasRangedEquipment && attacker.hasMagicEquipment)
				{
						if (!EdgeAndWestsRule.bothPlayersCanByPass(attacker, victim))
						{
								attacker.getPA().sendMessage(victim.getPlayerName() + " is under " + location + " protection.");
								attacker.getPA().sendMessage("You cannot have magic equipment to attack this player.");
								attacker.playerTriedToAttack = victim.getPlayerName();
								attacker.timeTriedToAttackPlayer = System.currentTimeMillis();
								return true;
						}
				}
				*/

				// Has to be kept here, because tribrid is only called for both players if the attacker is a tribrid.
				hasExcessBrews(attacker, 100);// Change both to 3.
				hasExcessBrews(victim, 100);
				/*
				int victimBrews = victim.brewCount / 2;
				if (victim.brewCount == 1)
				{
						victimBrews = 1;
				}
				int attackerBrews = attacker.brewCount / 2;
				if (attacker.brewCount == 1)
				{
						attackerBrews = 1;
				}
				
				String string = victimBrews > 1 ? "Saradomin brews." : "Saradomin brew.";
				if (!attacker.brewMessageSent.equals(victim.getPlayerName() + " has " + victimBrews + " " + string))
				{
						attacker.brewMessageSent = victim.getPlayerName() + " has " + victimBrews + " " + string;
						attacker.timeBrewMessageSent = System.currentTimeMillis();
						attacker.getPA().sendMessage(victim.getPlayerName() + " has " + victimBrews + " " + string);
				}
				
				String string1 = attackerBrews > 1 ? "Saradomin brews." : "Saradomin brew.";
				if (!victim.brewMessageSent.equals(attacker.getPlayerName() + " has " + attackerBrews + " " + string1))
				{
						victim.getPA().sendMessage(attacker.getPlayerName() + " has " + attackerBrews + " " + string1);
						victim.brewMessageSent = attacker.getPlayerName() + " has " + attackerBrews + " " + string1;
						victim.timeBrewMessageSent = System.currentTimeMillis();
				}
				*/
				/*
				if (attacker.excessBrews && !victim.excessBrews)
				{
						if (!EdgeAndWestsRule.bothPlayersCanByPass(attacker, victim))
						{
								attacker.getPA().sendMessage(victim.getPlayerName() + " is under " + location + " protection.");
								attacker.getPA().sendMessage("You need 1 Saradomin brew maximum to attack this player.");
								attacker.playerTriedToAttack = victim.getPlayerName();
								attacker.timeTriedToAttackPlayer = System.currentTimeMillis();
								return true;
						}
				}
				*/

				// Warnings before attacking a player.
				ArrayList<String> reasons = new ArrayList<String>();

				// Warnings
				for (int a = 0; a < attacker.pvpBlacklist.size(); a++) {
					if (victim.getPlayerName().toLowerCase().equals(attacker.pvpBlacklist.get(a))) {
						reasons.add("is blacklisted");
						break;
					}
				}
				if (victim.getBaseDefenceLevel() >= 21 && attacker.getBaseDefenceLevel() <= 1) {
					reasons.add("21 defence or more");
				}
				if (!attacker.bot && !victim.bot) {
					if (!attackerTribrid) {
						if (victimTribrid) {
							reasons.add("a tribrid");
						}
					}
				}

				// Prevent a mager to attack a player with melee only.
				if (!attacker.hasMagicEquipment && attacker.combatStylesUsed == 1 && !attacker.hasRangedEquipment && victim.hasMagicEquipment) {
					reasons.add("has magic equipment");
				}

				// Compile all warnings together.
				String reasonsJoined = "";
				for (int index = 0; index < reasons.size(); index++) {
					if (index + 1 == reasons.size() - 1) {
						reasonsJoined = reasonsJoined + reasons.get(index) + " & ";
					} else if (index + 1 == reasons.size()) {
						reasonsJoined = reasonsJoined + reasons.get(index) + ".";
					} else {
						reasonsJoined = reasonsJoined + reasons.get(index) + ", ";
					}
				}
				warningSent = warning(attacker, victim, reasonsJoined);
				return warningSent;
			case "TELEBLOCK":
				attacker.getPA().sendMessage(victim.getPlayerName() + " is under " + location + " protection.");
				attacker.getPA().sendMessage("You cannot teleblock this player.");
				return true;
		}
		return false;
	}

	/**
	 * True if the player falls under the Edgeville protection rule.
	 */
	public static boolean isUnderEdgeAndWestsProtectionRules(Player player) {
		if (Area.inDuelArenaRing(player)) {
			return false;
		}
		if (Area.inClanWarsDangerousArea(player)) {
			return false;
		}
		if (player.getHeight() == 4) {
			return true;
		}
		if (player.inEdgeOrWestArea && System.currentTimeMillis() - player.timeEnteredEdgeOrWestArea > (EdgeAndWestsRule.TIME_SINCE_LEAVING_EDGE_OR_WESTS_TO_BE_SAFE * 1000)) {
			return true;
		}
		return false;
	}

	private static boolean warning(Player attacker, Player victim, String reasons) {
		if (reasons.isEmpty()) {
			return false;
		}
		reasons = victim.getPlayerName() + " " + reasons;
		if (attacker.nameWarnedOf.equals(reasons) && System.currentTimeMillis() - attacker.timeWarned <= 20000) {
			return false;
		}
		attacker.getDH().sendStatement(reasons);
		attacker.nameWarnedOf = reasons;
		attacker.timeWarned = System.currentTimeMillis();
		return true;
	}

	public static void edgeOrWestRuleCoordinateUpdate(Player victim, boolean logInTeleportCompleted) {
		if (Area.inEdgevilleWilderness(victim) || victim.getHeight() == 4) {
			if (logInTeleportCompleted) {
				// Player goes from dangerous area to safe.
				if (!victim.inEdgeOrWestArea && Combat.wasUnderAttackByAnotherPlayer(victim, 9600)) {
					victim.timeEnteredEdgeOrWestArea = System.currentTimeMillis();
					victim.getPA().sendMessage(ServerConstants.RED_COL + "You have entered the safe area from a dangerous zone, in combat. Protection rules will");
					victim.getPA().sendMessage(ServerConstants.RED_COL + "apply to you in " + TIME_SINCE_LEAVING_EDGE_OR_WESTS_TO_BE_SAFE + " seconds.");
				}
			}
			victim.inEdgeOrWestArea = true;
			victim.canTriggerCityTimer = true;
			resetCityTimer(victim);
			victim.timeExitedWilderness = 0;
		} else if (Area.inDangerousPvpArea(victim)) {
			// Player goes from safe area to dangerous.
			if (victim.inEdgeOrWestArea) {
				victim.getPA().sendMessage(ServerConstants.RED_COL + "You have entered a dangerous area, you are no longer under protection.");
			}
			victim.inEdgeOrWestArea = false;
			victim.canTriggerCityTimer = true;
			resetCityTimer(victim);
			victim.timeExitedWilderness = 0;
		}
		// Out of wilderness, so reset. This will be called everytime the player walks when already outside of wild.
		// If i want it to be only called once, go to Movement class, it's done there.
		else {
			if (System.currentTimeMillis() < victim.teleBlockEndTime && victim.getHeight() != 20) {
				victim.teleBlockEndTime = 0;
				victim.getPA().sendMessage("The teleport block fades as you leave the wilderness...");
				OverlayTimer.stopOverlayTimer(victim, ServerConstants.OVERLAY_TIMER_SPRITE_TELEBLOCK);
			}

			if (!victim.bot) {

				/*
					if (EdgeAndWestsRule.isUnderEdgeAndWestsProtectionRules(victim) && System.currentTimeMillis() - victim.getTimeUnderAttackByAnotherPlayer() <= 8000 && victim.canTriggerCityTimer)
					{
							Player attacker = PlayerHandler.players[victim.getLastAttackedBy()];
							if (attacker != null)
							{
									if (!attacker.isBot && attacker.lastPlayerAttackedName == victim.getPlayerName() && victim.lastPlayerAttackedName.equals(attacker.getPlayerName()) && EdgeAndWestsRule.isUnderEdgeAndWestsProtectionRules(attacker))
									{
											if (System.currentTimeMillis() - attacker.timeExitedWilderness >= 30000)
											{
													victim.canTriggerCityTimer = false;
													attacker.playerIdCanAttackInSafe = victim.getPlayerId();
													attacker.getPA().sendMessage(":packet:citytimer");
													attacker.timeVictimExitedWilderness = System.currentTimeMillis();
													victim.getPA().sendMessage(":packet:citytimer");
													victim.playerIdAttackingMeInSafe = attacker.getPlayerId();
													victim.timeExitedWilderness = System.currentTimeMillis();
													cityTimerEvent(victim, true);
													cityTimerEvent(attacker, false);
											}
									}
							}
					}
					*/
			}
			if (System.currentTimeMillis() - victim.timeExitedWilderness > 10000 && System.currentTimeMillis() - victim.timeVictimExitedWilderness > 10000) {
				if (!Combat.inCombat(victim)) {
					victim.lastPlayerAttackedName = "";
					victim.nameWarnedOf = "";
					victim.timeScannedForTribrid = 0;
					victim.timeEnteredEdgeOrWestArea = 0;
					victim.timeScannedForWildernessRisk = 0;
					victim.setTimeAttackedAnotherPlayer(0);
					victim.setTimeUnderAttackByAnotherPlayer(0);
					victim.setTimeNpcAttackedPlayer(0);
					victim.droppedFood.clear();
					victim.brewMessageSent = "";
					victim.resetDamageTaken();
					victim.killedPlayerImmuneTime = 0;
				}
			}
		}
	}

	private static void resetCityTimer(Player player) {
		Player attacker = PlayerHandler.players[player.getLastAttackedBy()];
		if (attacker != null) {
			if (attacker.lastPlayerAttackedName == player.getPlayerName() && player.lastPlayerAttackedName.equals(attacker.getPlayerName())) {
				if (System.currentTimeMillis() - player.timeExitedWilderness < 10000) {
					InterfaceAssistant.offCityTimer(player);
					InterfaceAssistant.offCityTimer(attacker);
				}
			}
		}

	}
	/*
	private static void cityTimerEvent(Player player, boolean running)
	{
			CycleEventHandler.getSingleton().addEvent(player, new CycleEvent()
			{
					@Override
					public void execute(CycleEventContainer container)
					{
							long time = player.timeVictimExitedWilderness;
							if (running)
							{
									time = player.timeExitedWilderness;
							}
							if (System.currentTimeMillis() - time >= 10200)
							{
									AreaInterface.updateWalkableInterfaces(player);
									container.stop();
							}
					}
	
					@Override
					public void stop()
					{
					}
			}, 1);
	}
	*/

	public static boolean isTribrid(Player player) {
		if (System.currentTimeMillis() - player.timeScannedForTribrid <= 20000) {
			return player.combatStylesUsed == 3;
		}
		int combatStylesUsed = 0;
		player.timeScannedForTribrid = System.currentTimeMillis();

		player.combatStylesUsed = 0;
		player.hasRangedEquipment = false;
		player.hasMagicEquipment = false;

		if (player.spellBook.equals("ANCIENT")) {
			if (ItemAssistant.hasItemInInventory(player, 560) || ItemAssistant.hasItemInInventory(player, 12791) && RunePouch
					                                                                                                        .specificRuneInsideRunePouch(player, "CHECK", 560, 2)) {
				combatStylesUsed++;
				player.hasMagicEquipment = true;
			}
		}
		if (RangedData.hasRangedWeapon(player)) {
			combatStylesUsed++;
			player.hasRangedEquipment = true;
		}

		if (player.getBaseStrengthLevel() >= 90 && player.getBaseAttackLevel() >= 50) {
			combatStylesUsed++;
		}
		player.combatStylesUsed = combatStylesUsed;
		hasExcessBrews(player, 3);
		if (combatStylesUsed == 3) {
			return true;
		}
		return false;
	}

	/**
	 * Incase the player drops a ranged weapon, then starts a fight, then picks up the ranged weapon.
	 *
	 * @param player
	 * @param itemId
	 */
	public static boolean canPickUpItemEdgeOrWestsRule(Player player, int itemId, boolean pickup) {
		if (pickup) {
			/*
				if (System.currentTimeMillis() - player.getTimeAttackedAnotherPlayer() <= 8000 || System.currentTimeMillis() - player.getTimeUnderAttackByAnotherPlayer() <= 8000)
				{
						if (Food.isFood(itemId) || ItemDefinition.getDefinitions()[itemId].name.contains("Saradomin brew") && !ItemDefinition.getDefinitions()[itemId].note)
						{
								for (int index = 0; index < player.droppedFood.size(); index++)
								{
										int foodDropped = Integer.parseInt(player.droppedFood.get(index));
										if (itemId == foodDropped)
										{
												player.droppedFood.remove(index);
												return true;
										}
								}
								player.getPA().sendMessage("You cannot pick up extra food while in combat.");
								return false;
						}
				}
				*/
			if (!canPickUpOrReceiveBrew(player, itemId)) {
				return false;
			}
		} else {
			if (System.currentTimeMillis() - player.getTimeAttackedAnotherPlayer() <= 8000 || System.currentTimeMillis() - player.getTimeUnderAttackByAnotherPlayer() <= 8000) {
				if (Food.isFood(itemId) || ItemDefinition.getDefinitions()[itemId].name.contains("Saradomin brew") && !ItemDefinition.getDefinitions()[itemId].note) {
					player.droppedFood.add(itemId + "");
					return true;
				}
			}
		}
		if (EdgeAndWestsRule.isUnderEdgeAndWestsProtectionRules(player)) {
			if (!pickup) {
				if (Food.isFood(itemId) || ItemDefinition.getDefinitions()[itemId].name.contains("Saradomin brew") && !ItemDefinition.getDefinitions()[itemId].note) {
				} else {
					player.timeScannedForTribrid = 0;
				}
			}
		}
		return true;

	}

	/**
	 * When the player picks up or receive a brew from a trade.
	 */
	public static boolean canPickUpOrReceiveBrew(Player player, int itemId) {
		if (player.getHeight() == 20) {
			if (ItemDefinition.getDefinitions()[itemId].name.contains("Saradomin brew")) {
				player.getPA().sendMessage("You cannot pickup brews in the tournament.");
				return false;
			}
		}
		if (Area.inDangerousPvpArea(player)) {
			if (ItemDefinition.getDefinitions()[itemId].name.contains("Saradomin brew") && !ItemDefinition.getDefinitions()[itemId].note) {
				hasExcessBrews(player, 100);
				int doses = Integer.parseInt(ItemDefinition.getDefinitions()[itemId].name.replace("Saradomin brew(", "").replace(")", "")) - 2;
				if (doses < 0) {
					doses = 0;
				}
				if (player.brewCount + doses > MAXIMUM_EXCESS_BREW_DOSES) {
					player.getPA().sendMessage("You cannot have more than 12 brews at a time.");
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Search the player for more than 4 doses of brews.
	 * Scan for 3 dose potions and 4 dose potions.
	 * 2 Dose of brews per inventory slot is fine. 3 or 4 dose of brew per inventory slot is not fine if it adds up to 5 doses of brews or more.
	 *
	 * @param player
	 * @return
	 */
	public static boolean hasExcessBrews(Player player, int excessDosesAmount) {
		// If excess doses reaches 5 or more, then flag.
		int excessDoses = 0;
		player.excessBrews = false;
		player.brewCount = 0;
		for (int index = 0; index < player.playerItems.length; index++) {
			int itemId = player.playerItems[index] - 1;

			// 4 Dose Saradomin brew.
			if (itemId == 6685) {
				excessDoses += 2;
			}

			// 3 Dose Saradomin brew.
			else if (itemId == 6687) {
				excessDoses += 1;
			}
			player.brewCount = excessDoses;
			if (excessDoses >= excessDosesAmount) {
				player.excessBrews = true;
				return true;
			}
		}
		return false;
	}

	public static void teleTab(Player victim) {
		if (System.currentTimeMillis() - victim.getTimeUnderAttackByAnotherPlayer() <= 8000) {
			if (victim.getUnderAttackBy() == victim.getPlayerId()) {
				return;
			}
			Player attacker = null;
			if (victim.getUnderAttackBy() > 0 && !Area.inMulti(victim.getX(), victim.getY())) {
				attacker = PlayerHandler.players[victim.getUnderAttackBy()];
			} else {
				attacker = PlayerHandler.players[Death.findPlayerWhoDidMostDamage(victim)];
			}
			if (attacker != null) {
				String[] text =
						{
								victim.getPlayerName() + " tabbed, you are clearly superior",
								victim.getPlayerName() + " tabbed from your superiority",
								victim.getPlayerName() + " tabbed from your might",
								victim.getPlayerName() + " tabbed, you clearly destroyed him",
								victim.getPlayerName() + " tabbed in fear",
								victim.getPlayerName() + " tabbed in cowardice",
								"Only a wasteman like " + victim.getPlayerName() + " tabs out",
								"Bad pkers like " + victim.getPlayerName() + " tab out",
								"Your greatness made " + victim.getPlayerName() + " tab out",
								"Your skills made " + victim.getPlayerName() + " tab out",
								"Noobs like " + victim.getPlayerName() + " tab out",
								"Noobs like " + victim.getPlayerName() + " panic tab"
						};
				attacker.getPA().sendMessage(text[Misc.random(text.length - 1)] + ".");
				attacker.enemyTabs++;
				victim.myTabs++;
			}
		}

	}

	/**
	 * @return True if the player can process to next destination with the amount of brews the player has.
	 */
	public static boolean canProcessToDestinationWithBrews(Player player, int destinationX, int destinationY, int height) {
		if (!Area.inDangerousPvpArea(player)) {
			if (Area.inWilderness(destinationX, destinationY, height)) {
				if (RagBan.isRagBanned(player)) {
					return false;
				}
				if (player.getAttributes().getOrDefault(Player.WILDERNESS_RULES_WARNING_ENABLED, true)) {
					player.getPA().displayInterface(42050);
					return false;

				}
				if (hasExcessBrews(player, MAXIMUM_EXCESS_BREW_DOSES + 1)) {
					player.getPA().sendMessage("Your may only carry a maximum of 12 brews to the wilderness.");
					return false;
				}
				if (destinationX >= 3025 && destinationX <= 3137 && destinationY >= 3520 && destinationY <= 3559) {
					// it means they are going to edgeville.
				} else {
					if (System.currentTimeMillis() - player.timeDiedInWilderness <= 45000) {
						int seconds = (int) ((System.currentTimeMillis() - player.timeDiedInWilderness) / 1000);
						seconds = 45 - seconds;
						if (seconds == 0) {
							seconds = 1;
						}
						String s = seconds > 1 ? "s" : "";
						player.getPA().sendMessage("You need to wait " + seconds + " second" + s + " before entering deep wild.");
						return false;
					}
				}
			}
		} else {
			if (destinationX >= 3025 && destinationX <= 3137 && destinationY >= 3520 && destinationY <= 3559) {
				// it means they are going to edgeville.
			} else {
				if (Area.inWilderness(destinationX, destinationY, height)) {
					if (System.currentTimeMillis() - player.timeDiedInWilderness <= 45000) {
						int seconds = (int) ((System.currentTimeMillis() - player.timeDiedInWilderness) / 1000);
						seconds = 45 - seconds;
						if (seconds == 0) {
							seconds = 1;
						}
						String s = seconds > 1 ? "s" : "";
						player.getPA().sendMessage("You need to wait " + seconds + " second" + s + " before entering deep wild.");
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * True if both players cannot attack each other and they keep trying to attack each other, so let it go through and let them fight.
	 */
	public static boolean bothPlayersCanByPass(Player attacker, Player victim) {
		if (attacker.playerTriedToAttack.equals(victim.getPlayerName()) && victim.playerTriedToAttack.equals(attacker.getPlayerName())) {
			if (System.currentTimeMillis() - attacker.timeTriedToAttackPlayer <= 10000 && System.currentTimeMillis() - victim.timeTriedToAttackPlayer <= 10000) {
				return true;
			}
		}
		return false;
	}
}
