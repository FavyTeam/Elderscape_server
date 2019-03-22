package game.content.skilling.prayer;

import core.GameType;
import core.ServerConstants;
import game.content.achievement.Achievements;
import game.content.combat.Combat;
import game.content.miscellaneous.RandomEvent;
import game.content.music.SoundSystem;
import game.content.skilling.Skilling;
import game.content.skilling.SkillingStatistics;
import game.item.ItemAssistant;
import game.player.Area;
import game.player.Player;
import game.player.movement.Follow;
import utility.Misc;

public class BuryBone {

	public static void buryBone(Player player, int id, int slot) {
		if (System.currentTimeMillis() - player.buryDelay > 900) {
			if (System.currentTimeMillis() - player.timeAttackedAnotherPlayer <= 8000) {
				player.getPA()
						.sendMessage("You cannot bury bones while under attack by another player.");
				return;
			}
			if (RandomEvent.isBannedFromSkilling(player)) {
				return;
			}
			// Dagannoth bone.
			if (id == 6729) {
				Achievements.checkCompletionSingle(player, 1025);
			}
			// Superior dragon bones
			if (id == 22124 && player.baseSkillLevel[ServerConstants.PRAYER] < 70) {
				player.getPA().sendMessage("You need a prayer level of at least 70 to bury these bones.");
				return;
			}
			ItemAssistant.deleteItemFromInventory(player, id, slot, 1);
			player.playerAssistant.sendFilterableMessage("You bury the bones.");
			player.skillingStatistics[SkillingStatistics.BONES_BURIED]++;
			for (Bones data : Bones.values()) {
				if (data.getItemId() == id) {
					Skilling.addSkillExperience(player, data.getXP(), ServerConstants.PRAYER, false);
					if (isWearingNecklace(player)) {
						dragonboneEffect(player, id);
					}
				}
			}

			// Normal bone in Monastery.
			if (id == 526 && Area.isWithInArea(player, 3044, 3059, 3481, 3500)) {
				Achievements.checkCompletionSingle(player, 1013);
			} else // Dagannoth bone in Monastery.
			if (id == 6729 && Area.isWithInArea(player, 3044, 3059, 3481, 3500)) {
				Achievements.checkCompletionMultiple(player, "1056");
			}
			Combat.resetPlayerAttack(player);
			Follow.resetFollow(player);
			player.buryDelay = System.currentTimeMillis();
			player.startAnimation(827);
			SoundSystem.sendSound(player, 380, 700);
			player.getPA().requestUpdates();
			player.getPA().stopAllActions();
		}
	}

	public enum Bones {
		REGULAR(526, 5, 1, 1),
		BAT(530, 6, 1, 1),
		BIG(532, 15, 2, 1),
		LONGBONE(10976, 15, 1, 1),
		CURVED(10977, 15, 1, 1),
		BABY_DRAGON(534, 30, 3, 1),
		WYVERN(6812, 72, 4, 1),
		DRAGON(536, 72, 4, 1),
		LAVA_DRAGON(11943, 85, 4, 1),
		DAGANNOTH(6729, 125, 4, 1),
		SUPERIOR(22124, 150, 5, 70);

		private int itemId, xp, restoreAmount, level;

		private Bones(int itemId, int xp, int restoreAmount, int level) {
			this.itemId = itemId;
			this.xp = xp;
			this.restoreAmount = restoreAmount;
			this.level = level;
		}

		public int getItemId() {
			return itemId;
		}

		public int getXP() {
			return xp;
		}

		public int getRestoreAmount() {
			return restoreAmount;
		}

		public int getLevelRequired() {
			return level;
		}
	}

	public static void dragonboneEffect(Player player, int id) {
		long timer = player.getAttributes().getOrDefault(Player.DRAGONBONE_NECKLACE_TIMER);
		if (System.currentTimeMillis() - timer > Misc.getSecondsToMilliseconds(7)) {
			for (Bones data : Bones.values()) {
				if (data.getItemId() == id) {
					player.currentCombatSkillLevel[ServerConstants.PRAYER] += data.getRestoreAmount();
					if (player.currentCombatSkillLevel[ServerConstants.PRAYER] > player.getBasePrayerLevel()) {
						player.currentCombatSkillLevel[ServerConstants.PRAYER] = player.getBasePrayerLevel();
					}
					player.skillTabMainToUpdate.add(ServerConstants.PRAYER);
				}
			}
		}

	}

	public static boolean isWearingNecklace(Player player) {
		if (!GameType.isOsrs()) {
			return false;
		}
		return (player.playerEquipment[ServerConstants.AMULET_SLOT] == 22111);
	}


	public static boolean useBoneCrusher(Player player, int id) {
		if (!ItemAssistant.hasItemInInventory(player, 18337)) {
			return false;
		}
		for (Bones data : Bones.values()) {
			if (data.getItemId() == id) {
				Skilling.addSkillExperience(player, data.getXP(), ServerConstants.PRAYER, false);
				return true;
			}
		}
		return false;
	}

	public static boolean isBone(int id) {
		for (Bones data : Bones.values()) {
			if (data.getItemId() == id) {
				return true;
			}
		}
		return false;
	}

}
