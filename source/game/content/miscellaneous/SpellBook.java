package game.content.miscellaneous;

import game.content.combat.vsplayer.magic.AutoCast;
import game.content.music.SoundSystem;
import game.player.Player;

/**
 * Change prayer book/magic book.
 *
 * @author MGT Madness, created on 16-03-2015.
 */
public class SpellBook {

	public static void switchToLunar(Player player) {
		player.getPA().closeInterfaces(true);
		SpellBook.lunarSpellBook(player, true);
		player.getPA().sendMessage("Your mind is filled with a lunar wisdom.");
		player.startAnimation(645);
		player.gfx0(436);
		SoundSystem.sendSound(player, 442, 200);
		player.getPA().sendMessage(":packet:lunarSpellbook:");
	}

	public static void switchToAncients(Player player) {
		player.getPA().closeInterfaces(true);
		SpellBook.ancientMagicksSpellBook(player, true);
		player.getPA().sendMessage("Your mind is filled with an ancient wisdom.");
		player.startAnimation(645);
		player.gfx0(436);
		SoundSystem.sendSound(player, 442, 200);
		player.getPA().sendMessage(":packet:ancientSpellbook:");
	}

	public static void switchToModern(Player player) {
		player.getPA().closeInterfaces(true);
		SpellBook.modernSpellBook(player, true);
		player.getPA().sendMessage("You feel a drain on your memory.");
		player.startAnimation(645);
		player.gfx0(436);
		SoundSystem.sendSound(player, 442, 200);
		player.getPA().sendMessage(":packet:regularSpellbook:");
	}

	public static void lunarSpellBook(Player player, boolean updateSpecialBar) {
		player.spellBook = "LUNAR";
		player.getPA().setSidebarInterface(6, 29999);
		AutoCast.resetAutocast(player, updateSpecialBar);
		player.getPA().sendMessage(":packet:lunarSpellbook:");
	}

	public static void modernSpellBook(Player player, boolean updateSpecialBar) {
		player.spellBook = "MODERN";
		player.getPA().setSidebarInterface(6, 1151);
		AutoCast.resetAutocast(player, updateSpecialBar);
		player.getPA().sendMessage(":packet:regularSpellbook:");
	}

	public static void ancientMagicksSpellBook(Player player, boolean updateSpecialBar) {
		player.spellBook = "ANCIENT";
		player.getPA().setSidebarInterface(6, PlayerMiscContent.getAncientMagicksInterface(player));
		AutoCast.resetAutocast(player, updateSpecialBar);
		player.getPA().sendMessage(":packet:ancientSpellbook:");
	}

}
