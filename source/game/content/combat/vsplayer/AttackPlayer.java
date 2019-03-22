package game.content.combat.vsplayer;

import core.GameType;
import core.ServerConstants;
import game.bot.BotContent;
import game.content.combat.Combat;
import game.content.combat.CombatConstants;
import game.content.combat.CombatInterface;
import game.content.combat.EdgeAndWestsRule;
import game.content.combat.HolidayItem;
import game.content.combat.special.SpecialAttackBase;
import game.content.combat.vsplayer.magic.AutoCast;
import game.content.combat.vsplayer.magic.MagicAttack;
import game.content.combat.vsplayer.melee.MeleeAttack;
import game.content.combat.vsplayer.range.RangedAttack;
import game.content.combat.vsplayer.range.RangedData;
import game.content.degrading.DegradingManager;
import game.content.interfaces.AreaInterface;
import game.content.minigame.AutoDice;
import game.content.minigame.Minigame;
import game.content.minigame.TargetSystem;
import game.content.minigame.zombie.Zombie;
import game.content.miscellaneous.GameTimeSpent;
import game.content.miscellaneous.Skull;
import game.content.miscellaneous.SpecialAttackTracker;
import game.content.skilling.Skilling;
import game.content.skilling.agility.AgilityAssistant;
import game.entity.EntityType;
import game.item.ItemAssistant;
import game.player.Area;
import game.player.Player;
import game.player.PlayerHandler;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import game.player.movement.Movement;

/**
 * Attack player.
 *
 * @author MGT Madness, created on 27-03-2015.
 */
public class AttackPlayer {
	/**
	 * Handle the player attack.
	 *
	 * @param attacker The player initiating the attack.
	 * @param victim The player receiving the attack.
	 */
	public static void handlePlayerAttack(final Player attacker, final Player victim, boolean specialWeapon) {
		if (victim == null) {
			return;
		}

		if (attacker.doingAnAction()) {
			return;
		}
		long time = System.currentTimeMillis();

		attacker.faceUpdate(victim.getPlayerId() + 32768);

		// Snowball.
		if (ItemAssistant.hasItemEquippedSlot(attacker, 10501, ServerConstants.WEAPON_SLOT)) {
			if (attacker.playerAssistant.withinDistanceOfTargetPlayer(victim, 8)) {
				if (time - attacker.getLastSnowBallThrowTime() >= 2400) {
					//ItemAssistant.deleteEquipment(attacker, 10501, ServerConstants.WEAPON_SLOT);
					attacker.cannotIssueMovement = true;
					attacker.startAnimation(5063);
					victim.snowBallsThrownAtMe++;
					attacker.gfx0(860);
					if (attacker.playerEquipmentN[ServerConstants.WEAPON_SLOT] == 1) {
						ItemAssistant.deleteEquipment(attacker, attacker.playerEquipment[ServerConstants.WEAPON_SLOT], ServerConstants.WEAPON_SLOT);
					}
					if (attacker.playerEquipmentN[ServerConstants.WEAPON_SLOT] != 0) {
						attacker.playerEquipmentN[ServerConstants.WEAPON_SLOT] -= 1;
						attacker.getPA().sendFrame34(1688, attacker.playerEquipment[ServerConstants.WEAPON_SLOT], ServerConstants.WEAPON_SLOT,
						                             attacker.playerEquipmentN[ServerConstants.WEAPON_SLOT]);
					}
					attacker.setUpdateRequired(true);
					attacker.setAppearanceUpdateRequired(true);
					CycleEventHandler.getSingleton().addEvent(attacker, new CycleEvent() {
						int i = 0;

						int enemyX = victim.getX();

						int enemyY = victim.getY();

						@Override
						public void execute(CycleEventContainer container) {
							i++;
							switch (i) {
								case 1:
									attacker.setLastSnowBallThrowTime(time);
									final int oX = attacker.getX();
									final int oY = attacker.getY();
									final int pX = victim.getX();
									final int pY = victim.getY();
									final int offX = (oY - pY) * -1;
									final int offY = (oX - pX) * -1;
									attacker.turnPlayerTo(victim.getX(), victim.getY());
									attacker.getPA()
									        .createPlayersProjectile(oX, oY, offX, offY, 50, 100, 861, 21, 21, victim.getPlayerId(), 65, Combat.getProjectileSlope(attacker));
									break;
								case 2:
									attacker.cannotIssueMovement = false;
									break;
								case 4:
									if (enemyX == victim.getX() && enemyY == victim.getY()) {
										victim.gfx0(858);
										victim.startAnimation(Combat.getBlockAnimation(victim));
										victim.turnPlayerTo(attacker.getX(), attacker.getY());
										victim.snowBallsLandedOnMe++;
									}
									break;
								case 5:
									container.stop();
									break;
							}
						}

						public void stop() {
						}
					}, 1);
				}
				Combat.resetPlayerAttack(attacker);
				Movement.stopMovement(attacker);
				return;
			}
		}

		// Rubber chicken & Stale baguette
		if (ItemAssistant.hasItemEquippedSlot(attacker, 4566, ServerConstants.WEAPON_SLOT) || ItemAssistant.hasItemEquippedSlot(attacker, 20590, ServerConstants.WEAPON_SLOT) && GameType.isOsrs()) {
			if (attacker.getPA().withinDistanceOfTargetPlayer(victim, 1)) {
				attacker.turnPlayerTo(victim.getX(), victim.getY());
				attacker.startAnimation(1833);
				victim.startAnimation(Combat.getBlockAnimation(victim));
				Movement.stopMovement(attacker);
				Combat.resetPlayerAttack(attacker);
				return;
			}
		}

		if (ItemAssistant.hasItemEquippedSlot(victim, 11014, ServerConstants.RING_SLOT)) {
			attacker.getPA().sendMessage("You can't do this right now.");
			Combat.resetPlayerAttack(attacker);
			return;
		}

		// Boxing gloves
		if (ItemAssistant.hasItemEquippedSlot(attacker, 7671, ServerConstants.WEAPON_SLOT) || ItemAssistant.hasItemEquippedSlot(attacker, 7673, ServerConstants.WEAPON_SLOT)) {
			if (attacker.playerAssistant.withinDistanceOfTargetPlayer(victim, 1)) {
				attacker.turnPlayerTo(victim.getX(), victim.getY());
				attacker.startAnimation(3678);
				victim.startAnimation(Combat.getBlockAnimation(victim));
				Movement.stopMovement(attacker);
				Combat.resetPlayerAttack(attacker);
				return;
			}
		}
		resetAttackData(attacker);
		MagicAttack.reEngageWithMagic(attacker);
		RangedAttack.reEngageWithRanged(attacker);
		// If using trident of the swamp and autocasting the trident of the swamp spell.
		if (attacker.getWieldedWeapon() == 12899 && attacker.getAutocastId() == 52 && attacker.getAutoCasting() && attacker.getSpellId() == -1) {
			AutoCast.resetAutocast(attacker, true);
			attacker.setPlayerIdAttacking(victim.getPlayerId());
		}
		if (!hasAttackRequirements(attacker, victim, true, true)) {
			if (!attacker.ignorePlayerTurn) {
				attacker.turnPlayerTo(victim.getX(), victim.getY());
			}
			return;
		}
		if (Area.inZombieWaitingRoom(victim)) {
			Combat.resetPlayerAttack(attacker);
			if (victim.getPA().playerIsBusy()) {
				attacker.turnPlayerTo(victim.getX(), victim.getY());
				attacker.getPA().sendMessage(victim.getPlayerName() + " is busy.");
				return;
			}
			Zombie.requestDuo(attacker, victim);
			return;
		}
		successfulAttackInitiated(attacker, victim);
		landNormalAttack(attacker, victim, specialWeapon);
	}

	public static void successfulAttackInitiated(Player attacker, Player victim) {
		long time = System.currentTimeMillis();

		victim.doNotClosePmInterface = true;
		victim.getPA().closeInterfaces(true);
		Skilling.stopAllSkilling(victim);
		AgilityAssistant.stopResting(victim);
		TargetSystem.doingWildActivity(attacker);
		attacker.killedPlayerImmuneTime = 0;
		Skull.combatSkull(attacker, victim); // Must be kept above timeInCombat variable.
		attacker.timeInCombat = time;
		attacker.setTimeAttackedAnotherPlayer(time);
		attacker.timeInPlayerCombat = time;
		victim.setTimeUnderAttackByAnotherPlayer(time);
		victim.timeInCombat = time;
		victim.timeInPlayerCombat = time;
		boolean enable = false;
		if (enable) {
			// Do not enable because in multi barrage situations, it will degrade the equipment very fast.
			DegradingManager.degrade(attacker, true);
			DegradingManager.degrade(victim, false);
		}
		GameTimeSpent.increaseGameTime(attacker, GameTimeSpent.PKING);
		GameTimeSpent.increaseGameTime(victim, GameTimeSpent.PKING);
		if (victim.isCombatBot()) {
			// To count pking time spent.
			if (time - attacker.lastTimeSpentUsed > 5000) {
				attacker.lastTimeSpentUsed = time;
				attacker.timeSpent[0]++;
			}
		}
		AreaInterface.updateWalkableInterfaces(attacker);
		AreaInterface.updateWalkableInterfaces(victim);
		victim.setUnderAttackBy(attacker.getPlayerId());
		victim.setLastAttackedBy(attacker.getPlayerId());
		Combat.resetSpecialAttackData(attacker);
		attacker.againstPlayer = true;
		BotContent.damaged(attacker);
		BotContent.goodLuckMessage(attacker, victim);
		attacker.botTimeInCombat = time;
		victim.botTimeInCombat = time;

	}

	/**
	 * True, if the player has all the requirements to land an attack.
	 *
	 * @param attacker The player attacking.
	 * @param victim The player receiving the attack.
	 * @param notify
	 * @param reset
	 * @return True, if the player has all the requirements to land an attack.
	 */
	public static boolean hasAttackRequirements(Player attacker, Player victim, boolean notify, boolean reset) {
		if (!hasSubAttackRequirements(attacker, victim, true, true)) {
			if (reset) {
				Combat.resetPlayerAttack(attacker);
				Movement.stopMovement(attacker);
			}
			return false;
		}
		if (!attacker.hasLastCastedMagic() && !attacker.getUsingRanged()) {
			if (attacker.getAutoRetaliate() == 1) {
				attacker.setMeleeFollow(true);
			}
			if (!MeleeAttack.hasMeleeRequirements(attacker, victim)) {
				return false;
			}
		} else if (attacker.hasLastCastedMagic()) {
			if (!MagicAttack.hasMagicRequirements(attacker, victim)) {
				return false;
			}
			if (!attacker.playerAssistant.withinDistanceOfTargetPlayer(victim, CombatConstants.MAGIC_FOLLOW_DISTANCE)) {
				return false;
			}

		} else if (attacker.getUsingRanged()) {
			if (!RangedAttack.hasRangedRequirements(attacker, victim)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Initiate a normal attack.
	 *
	 * @param attacker The player initiating the normal attack.
	 * @param victim The player receiving the normal attack.
	 */
	private static void landNormalAttack(Player attacker, Player victim, boolean specialWeapon) {
		if (RangedAttack.landRangedAttack(attacker, victim, specialWeapon)) {
			return;
		}
		if (MeleeAttack.normalMeleeAttack(attacker, victim, specialWeapon)) {
			return;
		}
		if (specialWeapon) {
			return;
		}
		Combat.setAttackTimer(attacker);
		MagicAttack.landMagicAttack(attacker, victim);
	}

	/**
	 * Initiate the special attack.
	 *
	 * @param attacker The player initiating the special attack.
	 * @param victim The player receiving the special attack.
	 */
	public static boolean landSpecialAttack(Player attacker, Player victim) {
		if (!attacker.isUsingSpecial()) {
			return false;
		}

		if (attacker.duelRule[10] && attacker.getDuelStatus() == 5) {
			attacker.playerAssistant.sendMessage("Special attacks have been disabled during this duel!");
			attacker.setUsingSpecialAttack(false);
			CombatInterface.updateSpecialBar(attacker);
			Combat.resetPlayerAttack(attacker);
			return true;
		}

		// Edgepvp special attack refil nerf
		if (System.currentTimeMillis() - attacker.edgePvpNurseUsedTime <= 15000) {
			attacker.playerAssistant.sendMessage("You must wait a few more seconds before using the special attack.");
			attacker.setUsingSpecialAttack(false);
			CombatInterface.updateSpecialBar(attacker);
			Combat.resetPlayerAttack(attacker);
			return false;
		}

		// Dragon spear related weapons with special attack
		if (attacker.getWieldedWeapon() == 1249 || attacker.getWieldedWeapon() == 11824 || attacker.getWieldedWeapon() == 11889) {
			if (attacker.getPlayerIdAttacking() > 0) {
				if (victim != null) {
					if (System.currentTimeMillis() - victim.timeDragonSpearEffected <= 2900) {
						attacker.getPA().sendMessage("Your victim is immune to the dragon spear effect.");
						attacker.setAttackTimer(1);
						return true;
					}
				}
			}
		}

		if (Combat.checkSpecAmount(attacker, attacker.getWieldedWeapon())) {
			SpecialAttackBase.activateSpecial(attacker, attacker.getWieldedWeapon(), victim.getPlayerId());
			attacker.setUsingSpecialAttack(false);
			attacker.botUsedSpecialAttack = false;
			CombatInterface.updateSpecialBar(attacker);

			// Dragon spears related special attack weapons.
			if (attacker.getWieldedWeapon() == 1249 || attacker.getWieldedWeapon() == 11824 || attacker.getWieldedWeapon() == 11889) {
				return true;
			}
			return true;
		} else {
			Combat.notEnoughSpecialLeft(attacker);
			return true;
		}
	}

	/**
	 * Stop the player from attacking, if this returns true.
	 *
	 * @param attacker The player attacking.
	 * @param notify
	 * @param reset
	 * @return True, if the player has the requirements to attack the victim.
	 */
	public static boolean hasSubAttackRequirements(Player attacker, Player victim, boolean notify, boolean reset) {
		if (attacker.getType() == EntityType.PLAYER_PET || victim.getType() == EntityType.PLAYER_PET) {
			return false;
		}
		if (attacker.getDead()) {
			return false;
		}
		if (attacker.getPlayerIdAttacking() == attacker.getPlayerId()) {
			return false;
		}
		if (attacker.getTransformed() != 0) {
			return false;
		}
		if (attacker.isTeleporting()) {
			return false;
		}
		if (attacker.dragonSpearEvent) {
			return false;
		}
		if (!attacker.isTutorialComplete() || !victim.isTutorialComplete()) {
			if (notify) {
				attacker.sendDebugMessage("Didnt complete tutorial.");
			}
			return false;
		}
		if (!attacker.playerAssistant.withinDistanceOfTargetPlayer(victim, CombatConstants.OUT_OF_VIEW_DISTANCE)) {
			attacker.ignorePlayerTurn = true;
			return false;
		}
		if (attacker.getHeight() != victim.getHeight()) {
			return false;
		}

		if (attacker.playerAssistant.isOnTopOfTarget(victim) && attacker.isFrozen()) {
			return false;
		}

		if (victim.getDead()) {
			return false;
		}
		if (victim.isTeleporting()) {
			return false;
		}

		if (victim.getPA().isNewPlayerEco()) {
			if (notify) {
				attacker.getPA().sendMessage("You cannot attack new players.");
			}
			return false;
		}
		if (attacker.getPA().isNewPlayerEco()) {
			if (notify) {
				attacker.getPA().sendMessage("You cannot attack as a new player.");
			}
			return false;
		}
		long time = System.currentTimeMillis();
		if (Area.inWildernessAgilityCourse(victim) && time - victim.wildernessAgilityCourseImmunity <= 1800000) {
			if (notify) {
				attacker.playerAssistant.sendMessage("This player is protected by the wizards!");
			}
			return false;
		}
		if (Area.inDuelArena(victim) && attacker.getDuelStatus() != 5 && !attacker.hasLastCastedMagic()) {
			if (Area.inDuelArenaRing(attacker) || attacker.getDuelStatus() == 5) {
				if (notify) {
					attacker.playerAssistant.sendMessage("You can't challenge inside the arena!");
				}
				return false;
			}
			return true;
		}
		if (attacker.getDuelStatus() == 5 && victim.getDuelStatus() == 5) {
			if (victim.getDuelingWith() == attacker.getPlayerId() && victim.getLastDueledWithName().equals(attacker.getPlayerName())) {
				if (attacker.getDuelCount() > 0) {
					if (notify) {
						attacker.playerAssistant.sendMessage("The duel hasn't started yet!");
					}
					return false;
				}

				return true;
			} else {
				if (notify) {
					attacker.playerAssistant.sendMessage("This isn't your opponent!");
				}
				return false;
			}
		}
		if (Area.inZombieWaitingRoom(victim)) {
			return true;
		}


		if (HolidayItem.isHolidayItem(attacker, victim) || attacker.getWieldedWeapon() == 7671 || attacker.getWieldedWeapon() == 7673) {
			return true;
		}
		if (!Area.inDangerousPvpAreaOrClanWars(victim) || !Area.inDangerousPvpAreaOrClanWars(attacker) || attacker.getHeight() == 20) {
			if (attacker.isCombatBot()) {
				BotContent.cannotAttackPlayer(attacker);
				return false;
			}
			if (attacker.playerIdAttackingMeInSafe == victim.getPlayerId() && victim.playerIdCanAttackInSafe == attacker.getPlayerId()
			    && time - attacker.timeExitedWilderness < 10000
			    //
			    || victim.playerIdAttackingMeInSafe == attacker.getPlayerId() && attacker.playerIdCanAttackInSafe == victim.getPlayerId()
			       && time - victim.timeExitedWilderness < 10000) {
			} else {
				if (attacker.tournamentTarget >= 0) {
					if (victim.tournamentTarget != attacker.getPlayerId() || attacker.tournamentTarget != victim.getPlayerId()) {
						if (notify) {
							attacker.playerAssistant.sendMessage("This is not your challenger!");
						}
						return false;
					}

					if (attacker.getDuelCount() > 0) {
						if (notify) {
							attacker.playerAssistant.sendMessage("The tournament hasn't started yet!");
						}
						return false;
					}
				} else {
					if (!Area.inDangerousPvpAreaOrClanWars(victim)) {
						String string = "Wilderness";
						if (Area.inEdgevilleBankPvpInstance(victim.getX(), victim.getY(), victim.getHeight()) && victim.getHeight() == 4) {
							string = "Pvp area";
						}
						if (notify) {
							attacker.playerAssistant.sendMessage("That player is not in the " + string + ".");
						}
					} else {
						String string = "Wilderness";
						if (Area.inEdgevilleBankPvpInstance(victim.getX(), victim.getY(), victim.getHeight()) && victim.getHeight() == 4) {
							string = "Pvp area";
						}
						if (notify) {
							attacker.playerAssistant.sendMessage("You are not in the " + string + ".");
						}
					}
					return false;
				}
			}
		}

		if (Combat.wasAttackedByNpc(victim) && !Area.inMulti(victim.getX(), victim.getY())) {
			BotContent.cannotAttackPlayer(attacker);
			if (notify) {
				attacker.playerAssistant.sendMessage("That player is already in combat.");
			}
			return false;
		}
		if (Combat.wasAttackedByNpc(attacker) && !Area.inMulti(victim.getX(), victim.getY())) {
			if (notify) {
				attacker.playerAssistant.sendMessage("You are already in combat.");
			}
			return false;
		}

		if (attacker.multiLoggingInWild) {
			if (notify) {
				attacker.playerAssistant.sendMessage("You cannot attack while multi-logging!");
			}
			return false;
		}

		if (!Area.inSafePkFightZone(attacker) && attacker.getHeight() != 20) {
			if (Area.inCityPvpArea(attacker)) {
				int minimumLevel = attacker.getCombatLevel() - 17;

				int maximumLevel = attacker.getCombatLevel() + 17;
				if (minimumLevel < 3) {
					minimumLevel = 3;
				}
				if (maximumLevel > 126) {
					maximumLevel = 126;
				}
				if (victim.getCombatLevel() < minimumLevel || victim.getCombatLevel() > maximumLevel) {
					if (notify) {
						attacker.playerAssistant.sendMessage("Your combat level difference is too great to attack that player here.");
					}
					return false;
				}
			} else {
				int combatDif1 = Combat.getCombatDifference(attacker.getCombatLevel(), victim.getCombatLevel());

				Minigame minigame = attacker.getMinigame();

				if ((combatDif1 > attacker.getWildernessLevel() || combatDif1 > victim.getWildernessLevel()) && minigame == null
				    || minigame != null && combatDif1 > minigame.maximumLevelDifference(attacker, victim)) {
					if (notify) {
						attacker.playerAssistant.sendMessage("Your combat level difference is too great to attack that player here.");
					}
					return false;
				}
			}
		}
		if (!Area.inMulti(victim.getX(), victim.getY())) {

			// These two numbers 9000 and 9000 to be the same as each other.
			if (victim.getUnderAttackBy() > 0) {
				if (victim.getUnderAttackBy() != attacker.getPlayerId() && Combat.wasUnderAttackByAnotherPlayer(victim, 3500)) {
					if (notify) {
						attacker.playerAssistant.sendMessage("That player is already in combat.");
					}
					return false;
				}
			}
			if (EdgeAndWestsRule.isUnderEdgeAndWestsProtectionRules(victim)) {
				Player third = PlayerHandler.players[victim.getPlayerIdAttacking()];
				boolean thirdPersonNotAttackingBackAtAll = false;
				if (third != null) {
					if (!third.lastPlayerAttackedName.equals(victim.getPlayerName())) {
						thirdPersonNotAttackingBackAtAll = true;
					}
				}
				String location = "Edgeville";
				if (victim.getHeight() == 4) {
					location = "Edge Pvp";
				}
				if (victim.getPlayerIdAttacking() > 0 && victim.getPlayerIdAttacking() != attacker.getPlayerId() && !thirdPersonNotAttackingBackAtAll) {
					if (notify) {
						attacker.getPA().sendMessage(victim.getPlayerName() + " is under " + location + " protection.");
						attacker.getPA().sendMessage("You cannot Pj this fight.");
					}
					return false;
				}
				if (victim.getUnderAttackBy() > 0) {
					if (victim.getUnderAttackBy() != attacker.getPlayerId() && Combat.wasUnderAttackByAnotherPlayer(victim, 30000)) {
						if (notify) {
							attacker.getPA().sendMessage(victim.getPlayerName() + " is under " + location + " protection.");
							attacker.getPA().sendMessage("You cannot Pj this fight.");
						}
						return false;
					}
				}
				if (time - victim.killedPlayerImmuneTime <= (EdgeAndWestsRule.TIME_IMMUNE_FROM_BEING_ATTACKED * 1000)) {
					if (!EdgeAndWestsRule.bothPlayersCanByPass(attacker, victim)) {
						if (notify) {
							attacker.getPA().sendMessage(victim.getPlayerName() + " is under " + location + " protection.");
							attacker.getPA().sendMessage("This player just dropped another player, give him a chance to teleport.");
						}
						attacker.playerTriedToAttack = victim.getPlayerName();
						attacker.timeTriedToAttackPlayer = time;
						return false;
					}
				}
			}
			if (attacker.getUnderAttackBy() > 0) {
				if (victim.getPlayerId() != attacker.getUnderAttackBy()) {
					if (time - attacker.timeInPlayerCombat <= 3500) {
						if (notify) {
							attacker.playerAssistant.sendMessage("You are already in combat.");
						}
						return false;
					} else {
						if (victim.getPlayerId() != attacker.getUnderAttackBy()) {
							attacker.setUnderAttackBy(0);
						}
					}
				}
			}
		}

		if (attacker.isInZombiesMinigame() || victim.isInZombiesMinigame()) {
			return false;
		}

		if (EdgeAndWestsRule.isEdgeOrWestRule(attacker, victim, "NORMAL ATTACK")) {
			return false;
		}

		// Toxic blowpipe (empty)
		if (attacker.getWieldedWeapon() == 12924) {
			return false;
		}

		if (System.currentTimeMillis() < victim.getTimePlayerCanBeAttacked()) {
			long timeLeft = victim.getTimePlayerCanBeAttacked() - System.currentTimeMillis();
			timeLeft /= 1_000;
			if (timeLeft == 0) {
				timeLeft = 1;
			}
			if (notify) {
				attacker.getPA().sendMessage("You may attack this player in " + timeLeft + " seconds.");
			}
			return false;
		}
		return true;
	}

	/**
	 * Apply the normal attack packet, the attack could have ranged or melee.
	 *
	 * @param attacker The player sending the packet.
	 * @param victimID The player id being clicked on by the attacker.
	 */
	public static void normalAttackPacket(Player attacker, int victimID) {
		attacker.setUsingRanged(false);
		attacker.setMeleeFollow(false);
		attacker.setLastCastedMagic(false);
		AttackPlayer.resetAttackData(attacker);
		Player victim = PlayerHandler.players[victimID];
		if (victim == null) {
			return;
		}
		if (!attacker.getPA().withinDistance(victim)) {
			return;
		}
		attacker.setPlayerIdToFollow(victim.getPlayerId());
		attacker.faceUpdate(victim.getPlayerId() + 32768);

		if (AutoDice.gambleOptionClicked(attacker, victim)) {
			return;
		}

		if (Area.inDuelArena(victim) && attacker.getDuelStatus() != 5 && !attacker.hasLastCastedMagic()) {
			attacker.setMeleeFollow(true);
			if (!Area.inDuelArena(attacker)) {
				return;
			}

			if (Area.inDuelArenaRing(attacker)) {
				return;
			}

			if (attacker.findOtherPlayerId > 0) {
				return;
			}
			attacker.findOtherPlayerId = 20;
			CycleEventHandler.getSingleton().addEvent(attacker, new CycleEvent() {
				@Override
				public void execute(CycleEventContainer container) {
					if (attacker.findOtherPlayerId > 0) {
						attacker.findOtherPlayerId--;
						if (attacker.getPA().withinDistanceOfTargetPlayer(victim, 1)) {
							attacker.getTradeAndDuel().requestDuel(victim.getPlayerId());
							container.stop();
						}
					} else {
						container.stop();
					}
				}

				@Override
				public void stop() {
					attacker.findOtherPlayerId = 0;
				}
			}, 1);
			return;
		}

		if (attacker.getAutoCasting()) {
			attacker.setLastCastedMagic(true);
		} else {
			if (RangedData.isWieldingMediumRangeRangedWeapon(attacker) || RangedData.isWieldingRangedWeaponWithNoArrowSlotRequirement(attacker)) {
				attacker.setUsingRanged(true);
			} else {
				attacker.setMeleeFollow(true);
			}
		}

		attacker.setPlayerIdAttacking(victimID);
		Combat.stopMovement(attacker, victim, false);

		// Has to be kept here so the player doesn't run 2 extra tiles before realising he cannot even attack.
		if (!hasSubAttackRequirements(attacker, victim, true, true)) {
			attacker.turnPlayerTo(victim.getX(), victim.getY());
			Combat.resetPlayerAttack(attacker);
			Movement.stopMovement(attacker);
		}
	}

	/**
	 * Reset specific player attack fields because we are starting a new attack round.
	 *
	 * @param attacker The player attacking
	 */
	public static void resetAttackData(Player attacker) {
		attacker.armadylCrossbowSpecial = false;
		attacker.getAttributes().put(Player.MORRIGANS_JAVS_SPECIAL, false);
		attacker.getAttributes().put(Player.MORRIGANS_AXE_SPECIAL, false);
		attacker.setUsingMediumRangeRangedWeapon(false);
		attacker.againstPlayer = false;
		attacker.setIsWieldingRangedWeaponWithNoArrowSlotRequirement(false);
		SpecialAttackTracker.resetSpecialAttackWeaponUsed(attacker);
		attacker.setDroppedRangedItemUsed(0);
		attacker.setAmmoDropped(false);
		attacker.ignorePlayerTurn = false;
	}
}
