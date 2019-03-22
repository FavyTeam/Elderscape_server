package game.content.skilling.prayer;

import core.ServerConstants;
import game.content.interfaces.InterfaceAssistant;
import game.content.miscellaneous.RandomEvent;
import game.content.music.SoundSystem;
import game.content.skilling.Skilling;
import game.content.skilling.SkillingStatistics;
import game.content.skilling.prayer.BuryBone.Bones;
import game.entity.Entity;
import game.item.ItemAssistant;
import game.player.Area;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;

/**
 * Bones on altar.
 *
 * @author MGT Madness, created on 29-01-2015.
 */
public class BoneOnAltar {

	/**
	 * @param player The associated player.
	 * @param itemUsedID The item identity used on the altar.
	 * @return True, if the item identity used on the altar is a bone.
	 */
	public static boolean isABone(Player player, int itemUsedID) {
		for (Bones data : Bones.values()) {
			if (itemUsedID == data.getItemId()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param player The associated player.
	 * @param itemUsedID The item identity used on the altar.
	 * @return True, if the player has a bone in his inventory.
	 */
	private static boolean hasBone(Player player, int itemUsedID) {
		return ItemAssistant.hasItemInInventory(player, itemUsedID) ? true : false;
	}

	/**
	 * Start the bone on altar system.
	 *
	 * @param player The associated player.
	 * @param itemUsedId The item identity used on the altar.
	 */
	public static void useBoneOnAltar(Player player, int itemUsedId) {
		if (!isABone(player, itemUsedId)) {
			player.getPA().sendMessage("You may only use bones on the altar.");
			return;
		}
		if (player.baseSkillLevel[ServerConstants.PRAYER] < 75) {
			player.playerAssistant.sendMessage("You need a Prayer level of 75 to use this feature.");
			return;
		}
		if (player.usingBoneOnAltarEvent) {
			return;
		}
		player.skillingInterface = ServerConstants.SKILL_NAME[ServerConstants.PRAYER];
		InterfaceAssistant.showSkillingInterface(player, "How many would you like to use?", 200, itemUsedId, 35, 0);
		player.skillingData[0] = itemUsedId;

	}

	/**
	 * Cycle event of bones on altar.
	 *
	 * @param player The associated player.
	 * @param itemUsedId The item identity used on the altar.
	 */
	private static void boneOnAltarCycleEvent(final Player player, final int itemUsedId) {
		if (RandomEvent.isBannedFromSkilling(player)) {
			return;
		}
		player.usingBoneOnAltarEvent = true;

		if (Skilling.cannotActivateNewSkillingEvent(player)) {
			return;
		}
		player.getEventHandler().addEvent(player, new CycleEvent<Entity>() {
			@Override
			public void execute(CycleEventContainer<Entity> container) {
				if (Skilling.forceStopSkillingEvent(player)) {
					container.stop();
					return;
				}

				if (!canUseBoneOnAltarAction(player, itemUsedId)) {
					container.stop();
				}
			}

			@Override
			public void stop() {
				player.usingBoneOnAltarEvent = false;
				Skilling.endSkillingEvent(player);
			}
		}, 2);
	}

	private static boolean canUseBoneOnAltarAction(Player player, int itemUsedId) {
		if (hasBone(player, itemUsedId) && player.skillingData[1] > 0) {
			for (Bones data : Bones.values()) {
				if (itemUsedId == data.getItemId()) {
					successfulBoneOnAltar(player, itemUsedId);
					player.skillingData[1]--;
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Reward the player for a successful bone on altar action.
	 *
	 * @param player The associated player.
	 * @param itemUsedId The item identity used on the altar.
	 */
	private static void successfulBoneOnAltar(Player player, int itemUsedId) {
		for (final Bones data : Bones.values()) {
			if (!ItemAssistant.hasItemInInventory(player, data.getItemId())) {
				continue;
			}
			boolean inArea = Area.inChaosAltarZone(player);
			Skilling.addSkillExperience(player, (int) (data.getXP() * (inArea ? 4 : 3.5)), ServerConstants.PRAYER, false);
			if (System.currentTimeMillis() - player.boneOnAltarAnimation > 2000) {
				player.startAnimation(3705);
				player.boneOnAltarAnimation = System.currentTimeMillis();
				player.gfx100(624);
			}
			ItemAssistant.deleteItemFromInventory(player, itemUsedId, 1);
			player.skillingStatistics[SkillingStatistics.BONES_ON_ALTAR]++;
			SoundSystem.sendSound(player, 442, 650);
		}
	}

	public static void prayerInterfaceAction(Player player, int amount) {
		player.getPA().closeInterfaces(true);
		player.skillingData[1] = amount;
		if (amount == 100) {
			InterfaceAssistant.showAmountInterface(player, ServerConstants.SKILL_NAME[ServerConstants.PRAYER], "Enter amount");
			return;
		}
		boneOnAltarCycleEvent(player, player.skillingData[0]);
	}

	public static void xAmountPrayerAction(Player player, int amount) {
		player.skillingData[1] = amount;
		boneOnAltarCycleEvent(player, player.skillingData[0]);
	}

}
