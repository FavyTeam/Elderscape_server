package game.content.miscellaneous;

import core.ServerConstants;
import game.content.skilling.Skilling;
import game.item.ItemAssistant;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;

public class Ectofuntus {

	public static enum Bonemeal {
		REGULAR(526, 4255, 5),
		BIG(532, 4256, 15),
		BABY_DRAG(534, 4257, 30),
		DRAG(536, 4258, 72),
		DAG(6729, 4259, 125),
		BAT(530, 4260, 5),
		WOLF(2859, 4261, 5),
		MONKEY(3179, 4262, 5),
		JOGRE(3125, 4263, 15),
		RAURG(4832, 4264, 150),
		OURG(4834, 4265, 140),
		LAVA_DRAG(11943, 4266, 85),
		WYVERN(6812, 4267, 85),
		SUPERIOR(22124, 22116, 150);

		private int boneId;

		private int bonemealId;

		private int xp;


		Bonemeal(final int boneId, final int bonemealId, final int xp) {
			this.boneId = boneId;
			this.bonemealId = bonemealId;
			this.xp = xp;
		}

		public int getBoneId() {
			return boneId;
		}

		public int getXp() {
			return xp;
		}

		public int getBonemealId() {
			return bonemealId;
		}

	}

	public static void FillBucket(final Player player) {
		if (!ItemAssistant.hasItemInInventory(player, 1925)) {
			return;
		}
		if (Skilling.cannotActivateNewSkillingEvent(player)) {
			return;
		}
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				if (Skilling.forceStopSkillingEvent(player)) {
					container.stop();
					return;
				}
				if (ItemAssistant.hasItemInInventory(player, 1925)) {
					ItemAssistant.deleteItemFromInventory(player, 1925, 1);
					ItemAssistant.addItem(player, 4286, 1);
					player.startAnimation(4471);
				} else {
					container.stop();
				}
			}

			@Override
			public void stop() {
				Skilling.endSkillingEvent(player);
			}
		}, 1);
		return;
	}

	public static void WorshipFuntus(final Player player) {
		for (final Bonemeal b : Bonemeal.values()) {
			if (!ItemAssistant.hasItemInInventory(player, b.getBonemealId()) || !ItemAssistant.hasItemInInventory(player, 4286)) {
				continue;
			}
			if (Skilling.cannotActivateNewSkillingEvent(player)) {
				return;
			}
			CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
				@Override
				public void execute(CycleEventContainer container) {
					if (Skilling.forceStopSkillingEvent(player)) {
						container.stop();
						return;
					}
					if (ItemAssistant.hasItemInInventory(player, b.getBonemealId()) && ItemAssistant.hasItemInInventory(player, 4286)) {
						ItemAssistant.deleteItemFromInventory(player, b.getBonemealId(), 1);
						ItemAssistant.deleteItemFromInventory(player, 4286, 1);
						ItemAssistant.addItem(player, 1931, 1);
						ItemAssistant.addItem(player, 1925, 1);
						Skilling.addSkillExperience(player, b.getXp() * 4, ServerConstants.PRAYER, false);
						player.startAnimation(1651);
					} else {
						container.stop();
					}
				}

				@Override
				public void stop() {
					Skilling.endSkillingEvent(player);
				}
			}, 2);
			return;
		}
	}

	public static void BoneGrinding(Player player) {
		for (final Bonemeal b : Bonemeal.values()) {
			int amount = ItemAssistant.getItemAmount(player, b.getBoneId());
			if (!ItemAssistant.hasItemInInventory(player, b.getBoneId()) || !ItemAssistant.hasItemInInventory(player, 1931)) // Pot
			{
				continue;
			}
			if (Skilling.cannotActivateNewSkillingEvent(player)) {
				return;
			}
			if (ItemAssistant.hasItemAmountInInventory(player, b.getBoneId(), amount) && ItemAssistant.hasItemAmountInInventory(player, 1931, amount)) {
				player.cannotIssueMovement = true;
				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
					int i = 0;

					@Override
					public void execute(CycleEventContainer container) {
						i++;
						switch (i) {
							case 1:
								player.startAnimation(1649);
								player.getDH().sendStartInfo("", "", "You load the bones into the hopper...", "", "");
								ItemAssistant.deleteItemFromInventory(player, b.getBoneId(), amount);
								break;

							case 5:
								player.setForceMovement(821, -1, 0, 0, 40, 0, 3);
								break;

							case 8:
								player.startAnimation(1648);
								player.getDH().sendStartInfo("", "", "...crank the handle and crush the bones...", "", "");
								break;

							case 13:
								player.setForceMovement(821, -1, 0, 0, 40, 0, 3);
								break;

							case 16:
								player.startAnimation(1650);
								player.getDH().sendStartInfo("", "", "...and collect the remains using the empty pots.", "", "");
								ItemAssistant.deleteItemFromInventory(player, 1931, amount);
								ItemAssistant.addItem(player, b.getBonemealId(), amount);
								break;

							case 19:
								player.getPA().closeInterfaces(true);
								player.cannotIssueMovement = false;
								container.stop();
								break;
						}
					}

					@Override
					public void stop() {
						Skilling.endSkillingEvent(player);
					}
				}, 1);
			}
		}
	}
}
