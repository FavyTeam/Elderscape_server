package game.object.areas;

import core.ServerConstants;
import game.content.skilling.agility.AgilityAssistant;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import game.player.movement.Movement;

/**
 * Handles the objects of Brimhaven dungeon
 *
 * @author MGT Madness 28-10-2013
 */
public class BrimhavenDungeon {

	/**
	 * Perform actions of the objects in Brimhaven dungeon.
	 */
	public static boolean isBrimhavenDungeonObject(final Player player, int objectType) {
		switch (objectType) {

			// Pipe.
			case 21728:

				if (player.getX() == 2655 && player.getY() == 9573) {
					if (player.baseSkillLevel[ServerConstants.AGILITY] < 22) {
						player.playerAssistant.sendMessage("You need 22 agility to use this shortcut.");
						return true;
					}

					if (System.currentTimeMillis() - player.agility7 < 3000) {
						return true;
					}
					player.agility7 = System.currentTimeMillis();
					AgilityAssistant.agilityAnimation(player, 844, false, 0, -7);
					CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
						@Override
						public void execute(CycleEventContainer container) {
							if (player.getY() == 9566) {
								container.stop();
							}
						}

						@Override
						public void stop() {
							AgilityAssistant.resetAgilityWalk(player);
							player.setDoingAgility(false);
						}
					}, 1);

				}

				if (player.getX() == 2655 && player.getY() == 9566) {
					if (player.baseSkillLevel[ServerConstants.AGILITY] < 22) {
						player.playerAssistant.sendMessage("You need 22 agility to use this shortcut.");
						return true;
					}

					if (System.currentTimeMillis() - player.agility7 < 3000) {
						return true;
					}
					player.agility7 = System.currentTimeMillis();
					AgilityAssistant.agilityAnimation(player, 844, false, 0, 7);
					CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
						@Override
						public void execute(CycleEventContainer container) {
							if (player.getY() == 9573) {
								container.stop();
							}
						}

						@Override
						public void stop() {
							AgilityAssistant.resetAgilityWalk(player);
							player.setDoingAgility(false);
						}
					}, 1);

				}

				return true;

			case 5097:
				if (System.currentTimeMillis() - player.agility1 < 2000) {
					return true;
				}
				player.agility1 = System.currentTimeMillis();
				player.playerAssistant.sendMessage("The stairs seem to be too slippery to walk on.");
				return true;
			// Stepping stone
			case 19040:

				if (player.getX() == 2682 && player.getY() == 9548 && player.getObjectX() == 2684 && player.getObjectY() == 9548) {
					if (player.baseSkillLevel[ServerConstants.AGILITY] < 83) {
						player.playerAssistant.sendMessage("You need 83 agility to use this shortcut.");
						return true;
					}
					if (System.currentTimeMillis() - player.agility1 < 3000) {
						return true;
					}
					player.agility1 = System.currentTimeMillis();
					player.setDoingAgility(true);
					player.agilityEndX = 2690;
					player.agilityEndY = 9547;
					CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
						@Override
						public void execute(CycleEventContainer container) {
							player.stoneTimer++;
							if (player.stoneTimer == 1) {

								player.turnPlayerTo(player.getX() + 1, player.getY());
								player.startAnimation(769);

							} else if (player.stoneTimer == 3) {
								player.getPA().movePlayer(2684, 9548, 0);

							} else if (player.stoneTimer == 4) {
								player.startAnimation(769);

							} else if (player.stoneTimer == 6) {
								player.getPA().movePlayer(2686, 9548, 0);

							} else if (player.stoneTimer == 7) {
								player.startAnimation(769);

							} else if (player.stoneTimer == 9) {
								player.getPA().movePlayer(2688, 9547, 0);
							} else if (player.stoneTimer == 10) {
								player.startAnimation(769);

							} else if (player.stoneTimer == 12) {
								player.getPA().movePlayer(2690, 9547, 0);
								container.stop();
							}
						}

						@Override
						public void stop() {
							AgilityAssistant.resetAgilityWalk(player);
							player.setDoingAgility(false);
							player.stoneTimer = 0;
						}
					}, 1);
				} else if (player.getX() >= 2688 && player.getObjectX() == 2688 && player.getObjectY() == 9547) {
					if (player.baseSkillLevel[ServerConstants.AGILITY] < 83) {
						player.playerAssistant.sendMessage("You need 83 agility to use this shortcut.");
						return true;
					}
					if (System.currentTimeMillis() - player.agility1 < 3000) {
						return true;
					}
					player.agility1 = System.currentTimeMillis();
					player.setDoingAgility(true);
					player.agilityEndX = 2682;
					player.agilityEndY = 9548;
					CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
						@Override
						public void execute(CycleEventContainer container) {
							player.stoneTimer++;
							if (player.stoneTimer == 1) {

								player.turnPlayerTo(player.getX() - 1, player.getY());
								player.startAnimation(769);

							} else if (player.stoneTimer == 3) {
								player.getPA().movePlayer(2688, 9547, 0);

							} else if (player.stoneTimer == 4) {
								player.startAnimation(769);

							} else if (player.stoneTimer == 6) {
								player.getPA().movePlayer(2686, 9548, 0);
							} else if (player.stoneTimer == 7) {
								player.startAnimation(769);

							} else if (player.stoneTimer == 8) {
								player.getPA().movePlayer(2684, 9548, 0);
							} else if (player.stoneTimer == 9) {
								player.startAnimation(769);

							} else if (player.stoneTimer == 11) {
								player.getPA().movePlayer(2682, 9548, 0);
								container.stop();
							}
						}

						@Override
						public void stop() {
							AgilityAssistant.resetAgilityWalk(player);
							player.setDoingAgility(false);
							player.stoneTimer = 0;
						}
					}, 1);
				} else if (player.getX() == 2695 && player.getY() == 9533 && player.getObjectX() == 2695 && player.getObjectY() == 9531) {
					if (player.baseSkillLevel[ServerConstants.AGILITY] < 83) {
						player.playerAssistant.sendMessage("You need 83 agility to use this shortcut.");
						return true;
					}
					if (System.currentTimeMillis() - player.agility1 < 3000) {
						return true;
					}
					player.agility1 = System.currentTimeMillis();
					player.setDoingAgility(true);
					player.agilityEndX = 2697;
					player.agilityEndY = 9525;
					CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
						@Override
						public void execute(CycleEventContainer container) {
							player.stoneTimer++;
							if (player.stoneTimer == 1) {

								player.turnPlayerTo(player.getX(), player.getY() - 1);
								player.startAnimation(769);

							} else if (player.stoneTimer == 3) {
								player.getPA().movePlayer(2695, 9531, 0);

							} else if (player.stoneTimer == 4) {
								player.startAnimation(769);

							} else if (player.stoneTimer == 6) {
								player.getPA().movePlayer(2695, 9529, 0);
							} else if (player.stoneTimer == 7) {
								player.startAnimation(769);

							} else if (player.stoneTimer == 8) {
								player.getPA().movePlayer(2696, 9527, 0);
							} else if (player.stoneTimer == 9) {
								player.startAnimation(769);

							} else if (player.stoneTimer == 11) {
								player.getPA().movePlayer(2697, 9525, 0);
								container.stop();
							}
						}

						@Override
						public void stop() {
							AgilityAssistant.resetAgilityWalk(player);
							player.setDoingAgility(false);
							player.stoneTimer = 0;
						}
					}, 1);
				} else if (player.getX() == 2697 && player.getY() == 9525 && player.getObjectX() == 2696 && player.getObjectY() == 9527) {
					if (player.baseSkillLevel[ServerConstants.AGILITY] < 83) {
						player.playerAssistant.sendMessage("You need 83 agility to use this shortcut.");
						return true;
					}
					if (System.currentTimeMillis() - player.agility1 < 3000) {
						return true;
					}
					player.agility1 = System.currentTimeMillis();
					player.setDoingAgility(true);
					player.agilityEndX = 2695;
					player.agilityEndY = 9533;
					CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
						@Override
						public void execute(CycleEventContainer container) {
							switch (container.getExecutions()) {
								case 0:
									player.startAnimation(769);
									break;
								case 1:
									player.getPA().movePlayer(2696, 9527, 0);
									break;
								case 2:
									player.startAnimation(769);
									break;
								case 3:
									player.getPA().movePlayer(2695, 9529, 0);
									break;
								case 4:
									player.startAnimation(769);
									break;
								case 5:
									player.getPA().movePlayer(2695, 9531, 0);
									break;
								case 6:
									player.startAnimation(769);
									break;
								case 7:
									player.getPA().movePlayer(2695, 9533, 0);
									container.stop();
									break;
							}
						}

						@Override
						public void stop() {
							AgilityAssistant.resetAgilityWalk(player);
							player.setDoingAgility(false);
						}
					}, 1);
				}
				return true;

			// Stepping stone
			case 21738:

				if (player.getX() == 2649 && player.getY() == 9562) {
					if (player.baseSkillLevel[ServerConstants.AGILITY] < 12) {
						player.playerAssistant.sendMessage("You need 12 agility to use this shortcut.");
						return true;
					}
					if (System.currentTimeMillis() - player.agility1 < 3000) {
						return true;
					}
					player.agility1 = System.currentTimeMillis();
					player.setDoingAgility(true);
					player.agilityEndX = 2647;
					player.agilityEndY = 9558;
					CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
						@Override
						public void execute(CycleEventContainer container) {
							player.stoneTimer++;
							if (player.stoneTimer == 1) {

								player.turnPlayerTo(player.getX(), player.getY() - 1);
								player.startAnimation(769);

							} else if (player.stoneTimer == 3) {
								player.getPA().movePlayer(2649, 9561, 0);

							} else if (player.stoneTimer == 4) {
								player.startAnimation(769);

							} else if (player.stoneTimer == 6) {
								player.getPA().movePlayer(2649, 9560, 0);

							} else if (player.stoneTimer == 7) {
								player.turnPlayerTo(player.getX() - 1, player.getY());
								player.startAnimation(769);

							} else if (player.stoneTimer == 9) {
								player.getPA().movePlayer(2648, 9560, 0);

							} else if (player.stoneTimer == 10) {
								player.startAnimation(769);

							} else if (player.stoneTimer == 12) {
								player.getPA().movePlayer(2647, 9560, 0);
							} else if (player.stoneTimer == 13) {
								player.turnPlayerTo(player.getX(), player.getY() - 1);
								player.startAnimation(769);
							} else if (player.stoneTimer == 15) {
								player.getPA().movePlayer(2647, 9559, 0);

							} else if (player.stoneTimer == 16) {
								player.startAnimation(769);
							} else if (player.stoneTimer == 18) {
								player.getPA().movePlayer(2647, 9558, 0);
								container.stop();

							}
						}

						@Override
						public void stop() {
							AgilityAssistant.resetAgilityWalk(player);
							player.setDoingAgility(false);
							player.stoneTimer = 0;
						}
					}, 1);
				}
				return true;
			case 21739:

				if (player.getX() == 2647 && player.getY() == 9557) {
					if (System.currentTimeMillis() - player.agility1 < 3000) {
						return true;
					}
					player.agility1 = System.currentTimeMillis();
					player.setDoingAgility(true);
					player.agilityEndX = 2649;
					player.agilityEndY = 9561;
					CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
						@Override
						public void execute(CycleEventContainer container) {
							player.stoneTimer++;
							if (player.stoneTimer == 1) {
								player.startAnimation(769);
								player.turnPlayerTo(player.getX(), player.getY() + 1);

							} else if (player.stoneTimer == 3) {
								player.getPA().movePlayer(2647, 9558, 0);

							} else if (player.stoneTimer == 4) {
								player.startAnimation(769);

							} else if (player.stoneTimer == 6) {
								player.getPA().movePlayer(2647, 9559, 0);

							} else if (player.stoneTimer == 7) {
								player.startAnimation(769);

							} else if (player.stoneTimer == 9) {
								player.getPA().movePlayer(2647, 9560, 0);
							} else if (player.stoneTimer == 10) {
								player.turnPlayerTo(player.getX() + 1, player.getY());
								player.startAnimation(769);

							} else if (player.stoneTimer == 12) {
								player.getPA().movePlayer(2648, 9560, 0);
							} else if (player.stoneTimer == 13) {
								player.startAnimation(769);
							} else if (player.stoneTimer == 15) {
								player.getPA().movePlayer(2649, 9560, 0);

							} else if (player.stoneTimer == 16) {
								player.turnPlayerTo(player.getX(), player.getY() + 1);
								player.startAnimation(769);
							} else if (player.stoneTimer == 18) {
								player.getPA().movePlayer(2649, 9561, 0);
								container.stop();

							}
						}

						@Override
						public void stop() {
							AgilityAssistant.resetAgilityWalk(player);
							player.setDoingAgility(false);
							player.stoneTimer = 0;
						}
					}, 1);
				}
				return true;

			// Stairs
			case 21725:

				if (System.currentTimeMillis() - player.agility1 < 2000) {
					return true;
				}
				player.agility1 = System.currentTimeMillis();
				player.playerAssistant.sendMessage("The stairs seem to be too slippery to walk on.");
				return true;

			case 21733:
				player.forceNoClip = true;
				if (player.getX() == 2672 && player.getY() == 9499) {
					Movement.travelTo(player, 2, 0);
				} else if (player.getX() == 2674 && player.getY() == 9499) {
					Movement.travelTo(player, -2, 0);
				}
				return true;

			// Log balance
			case 20882:
				if (player.getX() != 2682 && player.getY() != 9506) {
					return true;
				}
				if (System.currentTimeMillis() - player.agility1 < 3000) {
					return true;
				}
				player.agility1 = System.currentTimeMillis();
				AgilityAssistant.agilityAnimation(player, 762, false, 5, 0);
				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {
						if (player.getX() == 2687 && player.getY() == 9506) {
							container.stop();
						}
					}

					@Override
					public void stop() {
						AgilityAssistant.resetAgilityWalk(player);
						player.setDoingAgility(false);
					}
				}, 1);

				return true;

			// Log balance
			case 20884:

				if (player.getX() != 2687 && player.getY() != 9506) {
					return true;
				}
				if (System.currentTimeMillis() - player.agility1 < 3000) {
					return true;
				}
				player.agility1 = System.currentTimeMillis();
				AgilityAssistant.agilityAnimation(player, 762, false, -5, 0);

				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {
						if (player.getX() == 2682 && player.getY() == 9506) {
							container.stop();
						}
					}

					@Override
					public void stop() {
						AgilityAssistant.resetAgilityWalk(player);
						player.setDoingAgility(false);
					}
				}, 1);

				return true;

			// Vines
			case 21731:

				player.forceNoClip = true;
				if (player.getX() == 2691 && player.getY() == 9564) {
					Movement.travelTo(player, -2, 0);
				} else if (player.getX() == 2689 && player.getY() == 9564) {
					Movement.travelTo(player, 2, 0);
				}

				return true;

			case 21735:
				player.forceNoClip = true;
				if (player.getX() == 2695 && player.getY() == 9482) {
					Movement.travelTo(player, -2, 0);
				} else if (player.getX() == 2693 && player.getY() == 9482) {
					Movement.travelTo(player, 2, 0);
				}
				return true;
			case 21734:
				player.forceNoClip = true;
				if (player.getX() == 2676 && player.getY() == 9479) {
					Movement.travelTo(player, -2, 0);
				} else if (player.getX() == 2674 && player.getY() == 9479) {
					Movement.travelTo(player, 2, 0);
				}
				return true;
			case 21732:
				player.forceNoClip = true;
				if (player.getX() == 2683 && player.getY() == 9568) {
					Movement.travelTo(player, 0, 2);
				} else if (player.getX() == 2683 && player.getY() == 9570) {
					Movement.travelTo(player, 0, -2);
				}
				return true;

			// Pipe
			case 21727:

				if (player.getX() == 2698 && player.getY() == 9500) {

					if (player.baseSkillLevel[ServerConstants.AGILITY] < 34) {
						player.playerAssistant.sendMessage("You need 34 agility to use this shortcut.");
						return true;
					}
					if (System.currentTimeMillis() - player.agility7 < 3000) {
						return true;
					}
					player.agility7 = System.currentTimeMillis();
					AgilityAssistant.agilityAnimation(player, 844, false, 0, -8);
					CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
						@Override
						public void execute(CycleEventContainer container) {
							if (player.getY() == 9492) {
								container.stop();
							}
						}

						@Override
						public void stop() {
							AgilityAssistant.resetAgilityWalk(player);
							player.setDoingAgility(false);
						}
					}, 1);

				}

				if (player.getX() == 2698 && player.getY() == 9492) {
					if (player.baseSkillLevel[ServerConstants.AGILITY] < 34) {
						player.playerAssistant.sendMessage("You need 34 agility to use this shortcut.");
						return true;
					}

					if (System.currentTimeMillis() - player.agility7 < 3000) {
						return true;
					}
					player.agility7 = System.currentTimeMillis();
					AgilityAssistant.agilityAnimation(player, 844, false, 0, 8);
					CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
						@Override
						public void execute(CycleEventContainer container) {
							if (player.getY() == 9500) {
								container.stop();
							}
						}

						@Override
						public void stop() {
							AgilityAssistant.resetAgilityWalk(player);
							player.setDoingAgility(false);
						}
					}, 1);

				}
				return true;
		}
		return false;

	}

}
