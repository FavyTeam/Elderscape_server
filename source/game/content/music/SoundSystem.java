package game.content.music;

import game.player.Player;

public class SoundSystem {

	public static void sendSound(Player player, Player victim, int id, int delay) {
		if (player.soundEnabled) {
			sendSound(player, id, 0, delay, 8);
		}
		if (victim != null) {
			if (victim.soundEnabled) {
				sendSound(victim, id, 0, delay, 8);
			}
		}
	}

	static int[] nonSpammableSounds =
			{
					// Magical force stops you from moving while you try and walk
					221,
					// Crafting gems
					464,
					
					447, // prayer off
			};

	public static void sendSound(Player player, int id, int delay) {
		if (!player.soundEnabled) {
			return;
		}
		for (int i = 0; i < nonSpammableSounds.length; i++) {
			if (id == nonSpammableSounds[i] && System.currentTimeMillis() - player.lastSpammedSoundTime < 2000) {
				return;
			} else if (id == nonSpammableSounds[i]) {
				player.lastSpammedSoundTime = System.currentTimeMillis();
			}
		}
		sendSound(player, id, 0, delay, 8);
	}

	public static void sendSound(Player player, int id, int type, int delay, int volume) {
		if (player == null || player.bot) {
			return;
		}
		if (player.getOutStream() != null && id != -1) {
			player.getOutStream().createFrame(174);
			player.getOutStream().writeWord(id);
			player.getOutStream().writeByte(type);
			player.getOutStream().writeWord(delay);
			player.getOutStream().writeWord(volume);
			player.flushOutStream();
		}
	}
}
