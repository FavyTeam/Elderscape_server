package game.content.combat.vsplayer.range;

import core.Server;
import core.ServerConstants;
import game.content.miscellaneous.Blowpipe;
import game.item.ItemAssistant;
import game.player.Player;
import utility.Misc;

/**
 * Drop the Ranged ammo on the floor.
 *
 * @author MGT Madness, created on 28-05-2015.
 */
public class RangedAmmoUsed {

	/**
	 * Drop the used ammo on the floor if requirements are met.
	 *
	 * @param attacker The player using the ammo.
	 */
	public static void dropAmmo(Player attacker, int targetX, int targetY, int targetHeight) {
		if (!attacker.getAmmoDropped()) {
			return;
		}
		if (Misc.hasPercentageChance(20)) {
				return;
			}
		// Kraken area
		if (targetX >= 3688 && targetX <= 3703 && targetY >= 5808 && targetY <= 5817) {
			targetX = 3696;
			targetY = 5807;
		}

		if (Server.itemHandler.itemAmount(attacker, attacker.getDroppedRangedWeaponUsed(), targetX, targetY) == 0) {
			Server.itemHandler.createGroundItem(attacker, attacker.getDroppedRangedWeaponUsed(), targetX, targetY, targetHeight, 1, false, 0, false, "", "", "", "", "dropAmmo first");
		} else if (Server.itemHandler.itemAmount(attacker, attacker.getDroppedRangedWeaponUsed(), targetX, targetY) != 0) {
			int amount = Server.itemHandler.itemAmount(attacker, attacker.getDroppedRangedWeaponUsed(), targetX, targetY);
			Server.itemHandler.removeGroundItem(attacker, attacker.getDroppedRangedWeaponUsed(), targetX, targetY, amount, false);
			Server.itemHandler.createGroundItem(attacker, attacker.getDroppedRangedWeaponUsed(), targetX, targetY, targetHeight, amount + 1, false, 0, false, "", "", "", "", "dropAmmo second");
		}
	}

	/**
	 * Delete the used ammo if requirements are met.
	 *
	 * @param player The associated player.
	 */
	public static void deleteAmmo(Player player) {

		// Toxic blowpipe.
		Blowpipe.reduceBlowpipeScales(player);
		Blowpipe.reduceBlowpipeDarts(player);
		if (RangedData.hasAvaRelatedItem(player) && Misc.hasPercentageChance(85)) {
			return;
		}

		// Toxic blowpipe, Crystal bow full, Craw's bow
		if (player.getDroppedRangedWeaponUsed() == 12926 || player.getDroppedRangedWeaponUsed() == 4214 || player.getDroppedRangedWeaponUsed() == 22550 || player.getDroppedRangedWeaponUsed() == 22547) {
			return;
		}
		if (ItemAssistant.hasItemEquippedSlot(player, player.getDroppedRangedWeaponUsed(), ServerConstants.WEAPON_SLOT)) {
			ItemAssistant.deleteEquipment(player, player.getDroppedRangedWeaponUsed(), 1, ServerConstants.WEAPON_SLOT);
		} else {
			ItemAssistant.deleteEquipment(player, player.getDroppedRangedWeaponUsed(), 1, ServerConstants.ARROW_SLOT);
		}
		// Bolt racks and hand cannon shots are an exception and Dragon javelin
		if (player.getDroppedRangedWeaponUsed() == 4740 || player.getDroppedRangedWeaponUsed() == 15243 || player.getDroppedRangedWeaponUsed() == 19484) {
			return;
		}
		player.setAmmoDropped(true);
	}
}
