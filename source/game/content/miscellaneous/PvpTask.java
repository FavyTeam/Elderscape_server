package game.content.miscellaneous;

import core.GameType;
import core.ServerConstants;
import game.content.dialogueold.DialogueHandler;
import game.content.starter.GameMode;
import game.item.ItemAssistant;
import game.log.CoinEconomyTracker;
import game.player.Player;
import utility.Misc;

/**
 * Pvp task system.
 *
 * @author MGT Madness, created on 27-02-2017.
 */
public class PvpTask {

	public static int getMaximumKillsPerTaskType() {
		return GameType.isOsrsPvp() ? 4 : 2;
	}

	public static int getPvpTaskRewardPerKill() {
		return GameType.isOsrsPvp() ? 400 : 400000;
	}

	private final static String[] PVP_TYPE_NAME =
			{"Pure", "Berserker", "Ranged tank", "Maxed main"};

	public static void obtainTask(Player player) {
		if (hasPvpTask(player)) {
			player.getDH().sendNpcChat("You already have a Pvp task.", DialogueHandler.FacialAnimation.CALM_1.getAnimationId());
			return;
		}
		if (player.canClaimPvpTaskReward) {
			player.getDH().sendNpcChat("Claim your reward first.", DialogueHandler.FacialAnimation.CALM_1.getAnimationId());
			return;
		}
		int maximumTaskCancels = 2;
		int totalKills = 0;
		for (int index = 0; index < player.pvpTask.length; index++) {
			if (maximumTaskCancels > 0) {
				if (Misc.hasPercentageChance(50)) {
					maximumTaskCancels--;
					continue;
				}
			}
			int random = Misc.random(1, getMaximumKillsPerTaskType());
			totalKills += random;
			player.pvpTask[index] = random;
		}
		player.pvpTaskSize = totalKills;
		killsLeft(player);

	}

	public static void claimReward(Player player) {
		if (hasPvpTask(player)) {
			player.getDH().sendNpcChat("You need to complete your Pvp task first.", DialogueHandler.FacialAnimation.CALM_1.getAnimationId());
			return;
		}
		if (!player.canClaimPvpTaskReward) {
			player.getDH().sendNpcChat("You need to obtain a Pvp task first.", DialogueHandler.FacialAnimation.CALM_1.getAnimationId());
			return;
		}
		int amount = getPvpTaskRewardPerKill() * player.pvpTaskSize;
		if (ItemAssistant.addItem(player, ServerConstants.getMainCurrencyId(), amount)) {
			CoinEconomyTracker.addIncomeList(player, "PVP-TASK " + amount);
			player.canClaimPvpTaskReward = false;
			player.getDH().sendItemChat("Pvp task", "You are awarded x" + Misc.formatRunescapeStyle(amount) + " " + ServerConstants.getMainCurrencyName().toLowerCase() + "!",
			                            ServerConstants.getMainCurrencyId() + 9, 200, 40, 0);
			if (Misc.hasPercentageChance(10)) {
				Announcement.announce(ServerConstants.GREEN_COL + GameMode.getGameModeName(player) + " has receieved " + Misc.formatRunescapeStyle(amount) + " " + ServerConstants.getMainCurrencyName().toLowerCase() + " from a Pvp task at ::shops");
			}
			player.pvpTasksCompleted++;
		}
	}

	public static void whatAreRewards(Player player) {
		player.getDH().sendNpcChat("After your task is completed, you will",
		                           "be awarded " + Misc.formatRunescapeStyle(getPvpTaskRewardPerKill()) + " " + ServerConstants.getMainCurrencyName().toLowerCase() + " per kill.",
		                           DialogueHandler.FacialAnimation.CALM_1.getAnimationId());
	}

	public static void pvpKill(Player player, int pvpTypeIndex) {
		if (!hasPvpTask(player)) {
			return;
		}
		if (player.pvpTask[pvpTypeIndex] > 0) {
			player.getPA().sendMessage("This kill counts as a " + PVP_TYPE_NAME[pvpTypeIndex] + " kill to your Pvp task.");
			player.pvpTask[pvpTypeIndex]--;
			if (!hasPvpTask(player)) {
				player.getPA().sendMessage("You have completed: " + ServerConstants.RED_COL + (player.pvpTasksCompleted + 1) + ServerConstants.BLACK_COL + " Pvp tasks.");
				player.canClaimPvpTaskReward = true;
			}
		}
	}

	public static void killsLeft(Player player) {
		if (!hasPvpTask(player)) {
			player.getDH().sendNpcChat("You do not have a Pvp task.", DialogueHandler.FacialAnimation.CALM_1.getAnimationId());
			return;
		}
		String text = "Kill players as a: ";
		for (int index = 0; index < player.pvpTask.length; index++) {
			if (player.pvpTask[index] > 0) {
				String type = "x" + player.pvpTask[index] + " " + PVP_TYPE_NAME[index];
				if (player.pvpTask[index] > 1) {
					//type = type + "s";
				}
				if (index == 3) {
					type = type + ".";
				} else {
					type = type + ", ";
				}

				text = text + type;
			}
		}
		if (text.endsWith(", ")) {
			text = text.substring(0, text.length() - 2);
			text = text + ".";
		}
		player.getDH().sendNpcChat(text, DialogueHandler.FacialAnimation.CALM_1.getAnimationId());
	}

	public static boolean hasPvpTask(Player player) {
		for (int index = 0; index < player.pvpTask.length; index++) {
			if (player.pvpTask[index] > 0) {
				return true;
			}
		}
		return false;
	}

}
