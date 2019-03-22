package game.content.prayer.book.regular;

import core.ServerConstants;
import game.content.achievement.Achievements;
import game.content.combat.Combat;
import game.content.interfaces.InterfaceAssistant;
import game.content.interfaces.ItemsKeptOnDeath;
import game.content.music.SoundSystem;
import game.content.quicksetup.QuickSetUp;
import game.content.starter.GameMode;
import game.content.worldevent.Tournament;
import game.player.Area;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;

/**
 * Normal prayer book.
 *
 * @author MGT Madness, created on 12-01-2013.
 * 
 * TODO: Convert and remove
 */
public class RegularPrayer {

	/**
	 * Turn off all prayer glows.
	 *
	 * @param player The associated player.
	 */
	public static void resetAllPrayerGlows(Player player) {
		for (int p = 0; p < player.prayerActive.length; p++) {
			player.setPrayerActive(p, false);
			player.getPA().sendFrame36(ServerConstants.PRAYER_GLOW[p], 0, false);
		}
	}

	/**
	 * Inform the client weather rigour and augury are unlocked.
	 *
	 * @param player
	 */
	public static void updateRigourAndAugury(Player player) {
		player.getPA().sendMessage(":packet:raidprayer " + player.rigourUnlocked + " " + player.auguryUnlocked);
	}

	public static void activatePrayer(Player player, int prayerIndex) {
		int sound = 0;
		if (player.getDead()) {
			player.getPA().sendFrame36(ServerConstants.PRAYER_GLOW[prayerIndex], 0, true);
			sound = 447;
			SoundSystem.sendSound(player, sound, 0);
			return;
		}

		if (player.dragonSpearEvent) {
			player.getPA().sendFrame36(ServerConstants.PRAYER_GLOW[prayerIndex], player.prayerActive[prayerIndex] ? 1 : 0, true);
			sound = 447;
			SoundSystem.sendSound(player, sound, 0);
			return;
		}
		// Prevent player from using p2p prayers while in f2p combat.
		if (System.currentTimeMillis() - player.getTimeUnderAttackByAnotherPlayer() <= 9000 || System.currentTimeMillis() - player.getTimeAttackedAnotherPlayer() <= 9000) {
			if (prayerIndex >= 21 && QuickSetUp.isUsingF2pOnly(player, false, false) && !player.prayerActive[prayerIndex]) {
				player.getPA().sendMessage("You cannot use p2p prayers while wearing full f2p in combat.");
				player.getPA().sendFrame36(ServerConstants.PRAYER_GLOW[prayerIndex], 0, true);
				sound = 447;
				SoundSystem.sendSound(player, sound, 0);
				return;
			}
		}
		if (prayerIndex == ServerConstants.RETRIBUTION && player.getHeight() == 20 || player.getHeight() == 20 && prayerIndex >= 16 && prayerIndex <= 18 && !Tournament.eventType
				                                                                                                                                                     .equals("Pure tribrid")
		                                                                              && !Tournament.eventType.equals("Main Nh")) {
			player.getPA().sendMessage("You cannot use these prayers in this tournament.");
			player.getPA().sendFrame36(ServerConstants.PRAYER_GLOW[prayerIndex], 0, true);
			sound = 447;
			SoundSystem.sendSound(player, sound, 0);
			return;
		}
		if (Area.inRfdArea(player.getX(), player.getY())) {
			Combat.resetPrayers(player);
			player.playerAssistant.sendMessage("A strong force prevents you from using prayers.");
			sound = 447;
			SoundSystem.sendSound(player, sound, 0);
			player.getPA().sendFrame36(ServerConstants.PRAYER_GLOW[prayerIndex], 0, true);
			return;
		}
		if (player.duelRule[7]) {
			Combat.resetPrayers(player);
			player.playerAssistant.sendMessage("Prayer has been disabled in this duel!");
			sound = 447;
			SoundSystem.sendSound(player, sound, 0);
			player.getPA().sendFrame36(ServerConstants.PRAYER_GLOW[prayerIndex], 0, true);
			return;
		}
		if (prayerIndex == ServerConstants.CHIVALRY && player.getCurrentCombatSkillLevel(ServerConstants.DEFENCE) < 65) {
			player.getPA().sendFrame36(ServerConstants.PRAYER_GLOW[prayerIndex], 0, true);
			player.getDH().sendStatement("You need 65 defence to use this prayer.");
			sound = 447;
			SoundSystem.sendSound(player, sound, 0);
			return;
		}
		if ((prayerIndex == ServerConstants.PIETY || prayerIndex == ServerConstants.AUGURY || prayerIndex == ServerConstants.RIGOUR) && player.getBaseDefenceLevel() < 70) {
			player.getPA().sendFrame36(ServerConstants.PRAYER_GLOW[prayerIndex], 0, true);
			player.getDH().sendStatement("You need 70 defence to use this prayer.");
			sound = 447;
			SoundSystem.sendSound(player, sound, 0);
			return;
		}

		if (prayerIndex == ServerConstants.RIGOUR && !player.rigourUnlocked) {
			player.getPA().sendFrame36(ServerConstants.PRAYER_GLOW[prayerIndex], 0, true);
			player.getDH().sendStatement("You do not have rigour unlocked.");
			sound = 447;
			SoundSystem.sendSound(player, sound, 0);
			return;
		}

		if (prayerIndex == ServerConstants.AUGURY && !player.auguryUnlocked) {
			player.getPA().sendFrame36(ServerConstants.PRAYER_GLOW[prayerIndex], 0, true);
			player.getDH().sendStatement("You do not have augury unlocked.");
			sound = 447;
			SoundSystem.sendSound(player, sound, 0);
			return;
		}
		if (prayerIndex == ServerConstants.PROTECT_ITEM && !player.prayerActive[ServerConstants.PROTECT_ITEM] && player.getRedSkull()) {
			player.getPA().sendMessage(ServerConstants.RED_COL + "You cannot use protect item with red skull!");
			player.getPA().sendFrame36(ServerConstants.PRAYER_GLOW[prayerIndex], 0, true);
			sound = 447;
			SoundSystem.sendSound(player, sound, 0);
			return;
		}

		if (prayerIndex == ServerConstants.PROTECT_ITEM && GameMode.getGameMode(player, "ULTIMATE IRON MAN")) {
			player.getPA().sendFrame36(ServerConstants.PRAYER_GLOW[prayerIndex], 0, true);
			player.getDH().sendStatement("As an Ultimate Ironman, you cannot protect item.");
			sound = 447;
			SoundSystem.sendSound(player, sound, 0);
			return;
		}

		int[] defPray =
				{0, 5, 13, 24, 25, ServerConstants.RIGOUR, ServerConstants.AUGURY};
		int[] strPray =
				{1, 6, 14, 24, 25};
		int[] atkPray =
				{2, 7, 15, 24, 25};
		int[] rangePray =
				{3, 11, 19, ServerConstants.RIGOUR};
		int[] magePray =
				{4, 12, 20, ServerConstants.AUGURY};
		if (player.getCurrentCombatSkillLevel(ServerConstants.PRAYER) > 0) {
			if (player.baseSkillLevel[ServerConstants.PRAYER] >= ServerConstants.PRAYER_LEVEL_REQUIRED[prayerIndex]) {
				boolean headIcon = false;
				switch (prayerIndex) {
					case ServerConstants.THICK_SKIN:
					case ServerConstants.ROCK_SKIN:
					case ServerConstants.STEEL_SKIN:
						if (player.prayerActive[prayerIndex] == false) {
							for (int j = 0; j < defPray.length; j++) {
								if (defPray[j] != prayerIndex) {
									player.setPrayerActive(defPray[j], false);
									player.getPA().sendFrame36(ServerConstants.PRAYER_GLOW[defPray[j]], 0, true);
								}
							}
						}
						break;
					case ServerConstants.BURST_OF_STRENGTH:
					case ServerConstants.SUPERHUMAN_STRENGTH:
					case ServerConstants.ULTIMATE_STRENGTH:
						if (player.prayerActive[prayerIndex] == false) {
							for (int j = 0; j < strPray.length; j++) {
								if (strPray[j] != prayerIndex) {
									player.setPrayerActive(strPray[j], false);
									player.getPA().sendFrame36(ServerConstants.PRAYER_GLOW[strPray[j]], 0, true);
								}
							}
							for (int j = 0; j < rangePray.length; j++) {
								if (rangePray[j] != prayerIndex) {
									player.setPrayerActive(rangePray[j], false);
									player.getPA().sendFrame36(ServerConstants.PRAYER_GLOW[rangePray[j]], 0, true);
								}
							}
							for (int j = 0; j < magePray.length; j++) {
								if (magePray[j] != prayerIndex) {
									player.setPrayerActive(magePray[j], false);
									player.getPA().sendFrame36(ServerConstants.PRAYER_GLOW[magePray[j]], 0, true);
								}
							}
						}
						break;
					case ServerConstants.CLARITY_OF_THOUGHT:
					case ServerConstants.IMPROVED_REFLEXES:
					case ServerConstants.INCREDIBLE_REFLEXES:
						if (player.prayerActive[prayerIndex] == false) {
							for (int j = 0; j < atkPray.length; j++) {
								if (atkPray[j] != prayerIndex) {
									player.setPrayerActive(atkPray[j], false);
									player.getPA().sendFrame36(ServerConstants.PRAYER_GLOW[atkPray[j]], 0, true);
								}
							}
							for (int j = 0; j < rangePray.length; j++) {
								if (rangePray[j] != prayerIndex) {
									player.setPrayerActive(rangePray[j], false);
									player.getPA().sendFrame36(ServerConstants.PRAYER_GLOW[rangePray[j]], 0, true);
								}
							}
							for (int j = 0; j < magePray.length; j++) {
								if (magePray[j] != prayerIndex) {
									player.setPrayerActive(magePray[j], false);
									player.getPA().sendFrame36(ServerConstants.PRAYER_GLOW[magePray[j]], 0, true);
								}
							}
						}
						break;
					case ServerConstants.SHARP_EYE:
						// range prays
					case ServerConstants.HAWK_EYE:
					case ServerConstants.EAGLE_EYE:
					case ServerConstants.RIGOUR:

						if (prayerIndex == ServerConstants.RIGOUR) {
							if (player.prayerActive[prayerIndex] == false) {
								for (int j = 0; j < defPray.length; j++) {
									if (defPray[j] != prayerIndex) {
										player.setPrayerActive(defPray[j], false);
										player.getPA().sendFrame36(ServerConstants.PRAYER_GLOW[defPray[j]], 0, true);
									}
								}
							}
						}
						if (player.prayerActive[prayerIndex] == false) {
							for (int j = 0; j < atkPray.length; j++) {
								if (atkPray[j] != prayerIndex) {
									player.setPrayerActive(atkPray[j], false);
									player.getPA().sendFrame36(ServerConstants.PRAYER_GLOW[atkPray[j]], 0, true);
								}
							}
							for (int j = 0; j < strPray.length; j++) {
								if (strPray[j] != prayerIndex) {
									player.setPrayerActive(strPray[j], false);
									player.getPA().sendFrame36(ServerConstants.PRAYER_GLOW[strPray[j]], 0, true);
								}
							}
							for (int j = 0; j < rangePray.length; j++) {
								if (rangePray[j] != prayerIndex) {
									player.setPrayerActive(rangePray[j], false);
									player.getPA().sendFrame36(ServerConstants.PRAYER_GLOW[rangePray[j]], 0, true);
								}
							}
							for (int j = 0; j < magePray.length; j++) {
								if (magePray[j] != prayerIndex) {
									player.setPrayerActive(magePray[j], false);
									player.getPA().sendFrame36(ServerConstants.PRAYER_GLOW[magePray[j]], 0, true);
								}
							}
						}
						break;
					case 4:
					case 12:
					case ServerConstants.MYSTIC_MIGHT:
					case ServerConstants.AUGURY:
						if (prayerIndex == ServerConstants.AUGURY) {
							if (player.prayerActive[prayerIndex] == false) {
								for (int j = 0; j < defPray.length; j++) {
									if (defPray[j] != prayerIndex) {
										player.setPrayerActive(defPray[j], false);
										player.getPA().sendFrame36(ServerConstants.PRAYER_GLOW[defPray[j]], 0, true);
									}
								}
							}
						}
						if (player.prayerActive[prayerIndex] == false) {
							for (int j = 0; j < atkPray.length; j++) {
								if (atkPray[j] != prayerIndex) {
									player.setPrayerActive(atkPray[j], false);
									player.getPA().sendFrame36(ServerConstants.PRAYER_GLOW[atkPray[j]], 0, true);
								}
							}
							for (int j = 0; j < strPray.length; j++) {
								if (strPray[j] != prayerIndex) {
									player.setPrayerActive(strPray[j], false);
									player.getPA().sendFrame36(ServerConstants.PRAYER_GLOW[strPray[j]], 0, true);
								}
							}
							for (int j = 0; j < rangePray.length; j++) {
								if (rangePray[j] != prayerIndex) {
									player.setPrayerActive(rangePray[j], false);
									player.getPA().sendFrame36(ServerConstants.PRAYER_GLOW[rangePray[j]], 0, true);
								}
							}
							for (int j = 0; j < magePray.length; j++) {
								if (magePray[j] != prayerIndex) {
									player.setPrayerActive(magePray[j], false);
									player.getPA().sendFrame36(ServerConstants.PRAYER_GLOW[magePray[j]], 0, true);
								}
							}
						}
						break;
					case 16:
					case 17:
					case 18:
						if (System.currentTimeMillis() - player.stopPrayerDelay < 4700) {
							player.playerAssistant.sendMessage("You have been injured and can't use this prayer!");
							player.getPA().sendFrame36(ServerConstants.PRAYER_GLOW[16], 0, true);
							player.getPA().sendFrame36(ServerConstants.PRAYER_GLOW[17], 0, true);
							player.getPA().sendFrame36(ServerConstants.PRAYER_GLOW[18], 0, true);
							sound = 447;
							SoundSystem.sendSound(player, sound, 0);
							return;
						}
					case 21:
					case 22:
					case 23:
						headIcon = true;
						for (int p = 16; p < 24; p++) {
							if (prayerIndex != p && p != 19 && p != 20) {
								player.setPrayerActive(p, false);
								player.getPA().sendFrame36(ServerConstants.PRAYER_GLOW[p], 0, true);
							}
						}
						break;
					case 24:
					case 25:
						if (prayerIndex == ServerConstants.PIETY) {
							Achievements.checkCompletionSingle(player, 1047);
						}
						if (player.prayerActive[prayerIndex] == false) {
							for (int j = 0; j < atkPray.length; j++) {
								if (atkPray[j] != prayerIndex) {
									player.setPrayerActive(atkPray[j], false);
									player.getPA().sendFrame36(ServerConstants.PRAYER_GLOW[atkPray[j]], 0, true);
								}
							}
							for (int j = 0; j < strPray.length; j++) {
								if (strPray[j] != prayerIndex) {
									player.setPrayerActive(strPray[j], false);
									player.getPA().sendFrame36(ServerConstants.PRAYER_GLOW[strPray[j]], 0, true);
								}
							}
							for (int j = 0; j < rangePray.length; j++) {
								if (rangePray[j] != prayerIndex) {
									player.setPrayerActive(rangePray[j], false);
									player.getPA().sendFrame36(ServerConstants.PRAYER_GLOW[rangePray[j]], 0, true);
								}
							}
							for (int j = 0; j < magePray.length; j++) {
								if (magePray[j] != prayerIndex) {
									player.setPrayerActive(magePray[j], false);
									player.getPA().sendFrame36(ServerConstants.PRAYER_GLOW[magePray[j]], 0, true);
								}
							}
							for (int j = 0; j < defPray.length; j++) {
								if (defPray[j] != prayerIndex) {
									player.setPrayerActive(defPray[j], false);
									player.getPA().sendFrame36(ServerConstants.PRAYER_GLOW[defPray[j]], 0, true);
								}
							}
						}
						break;
				}
				if (!headIcon) {
					if (player.prayerActive[prayerIndex] == false) {
						player.setPrayerActive(prayerIndex, true);
						player.timePrayerActivated[prayerIndex] = System.currentTimeMillis();
						switch (prayerIndex) {
							case ServerConstants.THICK_SKIN:
								sound = 446;
								break;
							case ServerConstants.BURST_OF_STRENGTH:
								sound = 449;
								break;
							case ServerConstants.CLARITY_OF_THOUGHT:
								sound = 436;
								break;
							case ServerConstants.ROCK_SKIN:
								sound = 441;
								break;
							case ServerConstants.SUPERHUMAN_STRENGTH:
								sound = 434;
								break;
							case ServerConstants.IMPROVED_REFLEXES:
								sound = 448;
								break;
							case ServerConstants.RAPID_RESTORE:
								sound = 451;
								break;
							case ServerConstants.RAPID_HEAL:
								sound = 443;
								break;
							case ServerConstants.STEEL_SKIN:
								sound = 439;
								break;
							case ServerConstants.ULTIMATE_STRENGTH:
								sound = 450;
								break;
							case ServerConstants.INCREDIBLE_REFLEXES:
								sound = 440;
								break;
						}
					} else {
						player.setPrayerActive(prayerIndex, false);
						sound = 435;
					}
				} else {
					if (player.prayerActive[prayerIndex] == false) {
						player.setPrayerActive(prayerIndex, true);
						player.timePrayerActivated[prayerIndex] = System.currentTimeMillis();
						player.headIcon = ServerConstants.PRAYER_HEAD_ICONS[prayerIndex];
						player.getPA().requestUpdates();
						switch (prayerIndex) {
							case ServerConstants.PROTECT_FROM_MAGIC:
								sound = 438;
								break;
							case ServerConstants.PROTECT_FROM_RANGED:
								sound = 444;
								break;
							case ServerConstants.PROTECT_FROM_MELEE:
								sound = 433;
								break;
						}
					} else {
						player.setPrayerActive(prayerIndex, false);
						player.headIcon = -1;
						player.getPA().requestUpdates();
						sound = 435;
					}
				}
			} else {
				player.getPA().sendFrame126(
						"You need a Prayer level of " + ServerConstants.PRAYER_LEVEL_REQUIRED[prayerIndex] + " to use " + ServerConstants.PRAYER_NAME[prayerIndex] + ".", 357);
				player.getPA().sendFrame126("Click here to continue", 358);
				player.getPA().sendFrame164(356);
			}
		} else {
			player.playerAssistant.sendMessage("You have run out of prayer points!");
			sound = 437;
		}
		boolean active = false;
		for (int p = 0; p < player.prayerActive.length; p++) {
			if (player.prayerActive[p]) {
				active = true;
			}
		}
		if (!active) {
			InterfaceAssistant.quickPrayersOff(player);
			player.quickPray = false;
		}
		if (sound > 0) {
			SoundSystem.sendSound(player, sound, 0);
		} else {
			SoundSystem.sendSound(player, 337, 0);
		}
		if (prayerIndex == ServerConstants.PROTECT_ITEM) {
			ItemsKeptOnDeath.updateInterface(player);
		}
		player.getPA().sendFrame36(ServerConstants.PRAYER_GLOW[prayerIndex], player.prayerActive[prayerIndex] ? 1 : 0, true);
	}

	public static void handlePrayerDrain(final Player player) {
		if (player.prayerEvent) {
			return;
		}
		if (player.getDead()) {
			return;
		}
		player.prayerEvent = true;
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				if (player.getDead()) {
					container.stop();
					return;
				}
				double currentPrayerAmountToRemove = 0.0;
				double prayerAmountToRemoveTotal = 0.0;
				for (int index = 0; index < player.timePrayerActivated.length; index++) {
					if (player.timePrayerActivated[index] > 0 && player.prayerActive[index]) {
						prayerAmountToRemoveTotal += Combat.prayerDrainData[index];
						if (System.currentTimeMillis() - player.timePrayerActivated[index] >= 590) {
							currentPrayerAmountToRemove += Combat.prayerDrainData[index];
						}
					}
				}
				if (prayerAmountToRemoveTotal <= 0.014) // Keep it at this
														// variable or the event
														// won't stop.
				{
					container.stop();
					return;
				}
				double toRemove = currentPrayerAmountToRemove;
				if (toRemove > 0 && player.playerBonus[11] > 0) {
					double percentageSlowerRate = player.playerBonus[11] * 3.3;
					percentageSlowerRate /= 100;
					percentageSlowerRate = percentageSlowerRate + 1.0;
					toRemove = toRemove / percentageSlowerRate;
				}
				if (!player.getTank()) {
					player.prayerPoint -= toRemove;
				}
				if (player.prayerPoint <= 0) {
					player.prayerPoint = 1;
					Combat.reducePrayerLevel(player);
				}

				if (player.getCurrentCombatSkillLevel(ServerConstants.PRAYER) < 0) {
					player.currentCombatSkillLevel[ServerConstants.PRAYER] = 0;
				}
			}

			@Override
			public void stop() {
				player.timePrayerActivated = new long[50];
				player.prayerEvent = false;
			}
		}, 1);

	}
}
