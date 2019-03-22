package game.content.music;

import game.player.Player;


/**
 * @author Jordy/mrclassic and MGT Madness.
 */
public class Music {

	public Music(String name, int swX, int swY, int neX, int neY, int music, int tabId, int array) {
		this.name = name;
		this.swX = swX;
		this.swY = swY;
		this.neX = neX;
		this.neY = neY;
		this.music = music;
		this.tabId = tabId;
		this.array = array;
	}

	public int music, swX, swY, neX, neY, tabId, array;

	public String name;

	/**
	 * Receive data from the client on weather the music is set to manual or auto.
	 */
	public static void receiveMusicState(Player player, String string) {
		if (string.toLowerCase().equals("musicauto")) {
			player.autoMusic = true;
			playRegionMusic(player);
		} else if (string.toLowerCase().equals("musicmanual")) {
			player.autoMusic = false;
		} else if (string.toLowerCase().equals("musicloopon")) {
			player.isLoopingMusic = true;
		} else if (string.toLowerCase().equals("musicloopoff")) {
			player.isLoopingMusic = false;
		} else if (string.toLowerCase().equals("musicnext")) {
			playNextSong(player);
		}
		player.musicPacketsReceived++;

	}

	public static void requestMusicStateFromClient(Player player) {
		player.playerAssistant.sendMessage(":musicstate:");
	}

	/**
	 * @param c
	 */
	public static void playRegionMusic(Player c) {
		Music song = getMusicIdForRegion(c);
		if (!c.autoMusic) {
			//c.getPA().sendFrame126("No music selected.", 4439);
			return;
		}
		if (song == null) {
			c.getOutStream().createFrame(74);
			c.getOutStream().writeWordBigEndian(324);
			c.getPA().sendFrame126("Somewhere", 4439);
			return;
		}
		c.getOutStream().createFrame(74);
		c.getOutStream().writeWordBigEndian(song.music);
		String musicName = "";
		for (int i = 0; i < songs.length; i++) {
			if (which == songs[i]) {
				c.getPA().sendFrame126(songs[i].name, 4439);
				musicName = songs[i].name;
			}
		}
		for (int i = 0; i < MusicTab.music.length; i++) {
			if (MusicTab.music[i].songName.toLowerCase().equalsIgnoreCase(musicName.toLowerCase())) {
				c.currentMusicOrder = MusicTab.music[i].array;
			}
		}
	}

	/**
	 * Play next song upon request from the client.
	 *
	 * @param player The associated player.
	 */
	public static void playNextSong(Player player) {
		if (player.currentMusicOrder + 1 > MusicTab.music.length - 1) {
			player.getPA().sendMessage("End of playlist reached.");
			return;
		}
		player.getPA().sendFrame126(MusicTab.music[player.currentMusicOrder + 1].songName, 4439);
		player.getOutStream().createFrame(74);
		player.getOutStream().writeWordBigEndian(MusicTab.music[player.currentMusicOrder + 1].songId);
		//player.playerAssistant.sendMessage("Song: " + MusicTab.music[player.currentMusicOrder + 1].songName + ", ID: " + MusicTab.music[player.currentMusicOrder + 1].songId);
		player.currentMusicOrder = MusicTab.music[player.currentMusicOrder + 1].array;

	}

	public static Music which;

	/**
	 * @param c
	 * @return
	 */
	private static Music getMusicIdForRegion(Player c) {
		int x = c.getX(), y = c.getY();
		for (int i = 0; i < songs.length; i++) {
			if (x >= songs[i].swX && x <= songs[i].neX && y >= songs[i].swY && y <= songs[i].neY) {
				which = songs[i];
				return songs[i];
			}
		}
		return null;
	}

	// name, swX, swY, neX, neY, song id, childId, arraySlot
	public static final Music[] songs =
			{
					new Music("Harmony", 3200, 3199, 3273, 3302, 76, 4344, 0), // Harmony
					new Music("Harmony", 1856, 5312, 1874, 5334, 76, 4344, 0),
					new Music("Autumn Voyage", 3200, 3303, 3273, 3353, 2, 4304, 1), // Autumn Voyage
					new Music("7th Realm", 2624, 9472, 2687, 9599, 363, 11941, 2),
					new Music("Adventure", 3200, 3456, 3263, 3519, 177, 4287, 3),
					new Music("Al Kharid", 3264, 3136, 3391, 3199, 50, 4288, 4),
					new Music("Alone", 3008, 3456, 3071, 3519, 102, 4289, 5),
					new Music("Alone", 2496, 9600, 2623, 9663, 102, 4289, 5),
					new Music("Ambient Jungle", 2816, 2944, 2879, 3007, 90, 4290, 6),
					new Music("Anywhere", 2688, 2752, 2751, 2815, 305, 11134, 7),
					new Music("Arabian", 3264, 3200, 3327, 3263, 36, 4291, 8),
					new Music("Arabian", 3392, 3136, 3455, 3199, 36, 4291, 8), //
					new Music("Arabian 2", 3264, 3264, 3327, 3327, 123, 4292, 9),
					new Music("Arabian 3", 3200, 3072, 3263, 3135, 124, 4293, 10),
					new Music("Arabique", 2816, 9792, 2879, 9855, 19, 4294, 11),
					new Music("Army of Darkness", 3008, 3584, 3071, 3647, 160, 4295, 12),
					new Music("Arrival", 2880, 3328, 2943, 3391, 186, 4296, 13),
					new Music("Artistry", 1984, 4736, 2047, 4799, 247, 8935, 14),
					new Music("Attack 1", 2496, 3200, 2559, 3263, 24, 4297, 15),
					new Music("Attack 2", 2816, 9600, 2879, 9663, 25, 4298, 16),
					new Music("Attack 3", 3008, 10240, 3071, 10303, 26, 4299, 17),
					new Music("Attack 4", 2560, 3136, 2623, 3199, 27, 4300, 18),
					new Music("Attack 4", 2560, 9536, 2623, 9599, 27, 4300, 18),
					new Music("Attack 5", 2240, 4672, 2303, 4735, 28, 4301, 19),
					new Music("Attack 6", 2560, 9408, 2623, 9471, 29, 4302, 20),
					new Music("Attention", 2944, 3136, 3007, 3199, 180, 4303, 21),
					new Music("Aye Car Rum Ba", 2112, 5056, 2175, 5119, 497, 14872, 22),
					new Music("Aztec", 2752, 9536, 2815, 9599, 248, 664, 23),
					new Music("Background", 2752, 3328, 2879, 3391, 324, 8971, 24),
					new Music("Background", 1953, 4992, 1983, 5055, 324, 8971, 24),
					new Music("Ballad of Enchantment", 2560, 3200, 2623, 3263, 152, 4306, 25),
					new Music("Bandit Camp", 3136, 2944, 3199, 3007, 263, 7454, 26),
					new Music("Barbarianism", 3072, 3392, 3135, 3455, 141, 4864, 27), // 4868
					new Music("Barbarianism", 3072, 9792, 3135, 9855, 141, 4864, 27),
					new Music("Barking Mad", 3520, 9856, 3583, 9919, 345, 12128, 28),
					new Music("Baroque", 2624, 3264, 2687, 3327, 99, 4307, 29),
					new Music("Beyond", 2816, 9856, 2879, 9919, 100, 4308, 30),
					new Music("Beyond", 2816, 9920, 2943, 9983, 100, 4308, 30),
					new Music("Big Chords", 2496, 3072, 2559, 3135, 83, 4309, 31),
					new Music("Blistering Barnacles", 2112, 5120, 2175, 5183, 498, 14871, 32),
					new Music("Body Parts", 3456, 9920, 3583, 9983, 342, 12127, 33),
					new Music("Bone Dance", 3392, 3264, 3455, 3327, 154, 8968, 34),
					new Music("Bone Dry", 3200, 9344, 3327, 9407, 266, 12844, 35),
					new Music("Book of Spells", 3136, 3136, 3199, 3199, 64, 4310, 36),
					new Music("Borderland", 2688, 3648, 2751, 3775, 291, 10111, 37),
					new Music("Breeze", 2240, 3200, 2303, 3263, 132, 8565, 38),
					new Music("Brew Hoo Hoo!", 3648, 9920, 3711, 9983, 471, 14242, 39),
					new Music("Bubble and Squeak", 1920, 4672, 1983, 4735, 489, 15494, 40),
					new Music("Cabin Fever", 1792, 4800, 1855, 4863, 545, 17508, 41),
					new Music("Camelot", 2752, 3456, 2815, 3583, 104, 4311, 42),
					new Music("Castlewars", 2368, 3072, 2431, 3135, 314, 11476, 43),
					new Music("Catch me if you can", 2624, 9600, 2687, 9663, 481, 15493, 44),
					new Music("Cave Background", 3008, 9728, 3071, 9791, 325, 8972, 45),
					new Music("Cave Background", 2944, 9792, 3071, 9855, 325, 8972, 45),
					new Music("Cave of Beasts", 2752, 10048, 2815, 10111, 357, 12390, 46),
					new Music("Cave of the Goblins", 3136, 9536, 3263, 9599, 389, 12584, 47),
					new Music("Cavern", 3008, 10304, 3071, 10367, 68, 4313, 48),
					new Music("Cavern", 2560, 9472, 2623, 9535, 68, 4313, 48),
					new Music("Cellar Song", 3136, 9792, 3199, 9855, 330, 8980, 49),
					new Music("Chain of Command", 2624, 9728, 2687, 9920, 63, 4314, 50),
					new Music("Chain of Command", 2688, 9792, 2751, 9855, 63, 4314, 50),
					new Music("Chamber", 2688, 4416, 2751, 4479, 282, 11484, 51),
					new Music("Chamber", 2752, 4480, 2815, 4543, 282, 11484, 51),
					new Music("Chef Surprise", 1875, 5312, 1919, 5375, 583, 246, 52),
					new Music("Chickened Out", 2432, 4352, 2495, 4415, 575, 18302, 53),
					new Music("Chompy Hunt", 2624, 2944, 2687, 3007, 71, 674, 54),
					new Music("Chompy Hunt", 2624, 9344, 2687, 9407, 71, 674, 54),
					new Music("City of the Dead", 3200, 2752, 3263, 2879, 383, 12849, 55),
					new Music("City of the Dead", 3264, 2752, 3327, 2815, 383, 12849, 55),
					new Music("Claustrophobia", 2304, 4928, 2431, 4991, 373, 12810, 56),
					new Music("Close Quarters", 3136, 3712, 3199, 3775, 67, 6944, 57),
					new Music("Competition", 2176, 4928, 2239, 4991, 269, 10983, 58),
					new Music("Complication", 2240, 4800, 2303, 4863, 142, 4870, 59),
					new Music("Contest", 2880, 3584, 2943, 3647, 258, 8436, 60),
					new Music("Corporal Punishment", 3136, 4800, 3199, 4863, 418, 6151, 61),
					new Music("Courage", 2880, 9792, 2943, 9919, 178, 4880, 62),
					new Music("Crystal Castle", 2240, 3264, 2303, 3391, 259, 8567, 63),
					new Music("Crystal Cave", 2432, 4416, 2495, 4479, 181, 4315, 64),
					new Music("Crystal Sword", 3200, 3520, 3263, 3583, 169, 4316, 65),
					new Music("Crystal Sword", 2624, 9664, 2687, 9727, 169, 4316, 65),
					new Music("Cursed", 2368, 9664, 2495, 9727, 59, 677, 66),
					new Music("Dagannoth Dawn", 1792, 4352, 1855, 4415, 198, 13385, 67),
					new Music("Dagannoth Dawn", 1920, 4352, 1983, 4415, 198, 13385, 67),
					new Music("Dance of the Undead", 3520, 3264, 3583, 3327, 380, 13352, 68),
					new Music("Dangerous Road", 2816, 9536, 2879, 9599, 336, 10128, 69),
					new Music("Dangerous Way", 3520, 9664, 3583, 9727, 381, 13353, 70),
					new Music("Dangerous", 3072, 3520, 3135, 3583, 182, 4317, 71),
					new Music("Dangerous", 3264, 3776, 3391, 3839, 182, 4317, 71),
					new Music("Dark", 3264, 3648, 3391, 3711, 326, 8973, 72),
					new Music("Davy Jones' Locker", 2944, 9472, 3007, 9535, 576, 18275, 73),
					new Music("Dead Can Dance", 3136, 3648, 3199, 3711, 476, 14453, 74),
					new Music("Dead Quiet", 3392, 3392, 3455, 3455, 84, 8142, 75),
					new Music("Dead Quiet", 2304, 4992, 2431, 5055, 84, 8142, 75),
					new Music("Deadlands", 3520, 3456, 3647, 3519, 288, 8936, 76),
					new Music("Deep Down", 2688, 4544, 2815, 4607, 278, 11483, 77),
					new Music("Deep Down", 2688, 4480, 2751, 4543, 278, 11483, 77),
					new Music("Deep Wildy", 2944, 3776, 3007, 3903, 37, 4319, 78),
					new Music("Desert Heat", 3392, 2944, 3455, 3071, 465, 5996, 79),
					new Music("Desert Voyage", 3264, 2944, 3327, 3071, 174, 4320, 80),
					new Music("Desert Voyage", 3328, 3008, 3391, 3071, 174, 4320, 80),
					new Music("Diango's Little Helpers", 1984, 4416, 2047, 4479, 532, 15842, 81),
					new Music("Distant Land", 3456, 3161, 3583, 3263, 501, 3320, 351), //
					new Music("Doorways", 3136, 3456, 3199, 3519, 56, 4321, 82),
					new Music("Down Below", 3072, 9600, 3135, 9727, 361, 12848, 83),
					new Music("Down To Earth", 2624, 4800, 2687, 4863, 143, 4879, 84),
					new Music("Dragontooth Island", 3776, 3520, 3839, 3583, 358, 12289, 85),
					new Music("Dream", 3136, 3200, 3199, 3263, 327, 8974, 86),
					new Music("Duel Arena", 3328, 3200, 3419, 3263, 47, 6298, 87),
					new Music("Dunjun", 2880, 9728, 3007, 9791, 173, 4323, 88),
					new Music("Dynasty", 3328, 2944, 3391, 3007, 351, 12835, 89),
					new Music("Egypt", 3264, 3072, 3391, 3135, 69, 4324, 90),
					new Music("Elven Mist", 2304, 3200, 2367, 3263, 252, 8575, 91),
					new Music("Emotion", 2496, 3136, 2559, 3199, 148, 4325, 92),
					new Music("Emotion", 2560, 4416, 2623, 4479, 148, 4325, 92),
					new Music("Emotion", 2496, 9536, 2559, 9599, 148, 4325, 92),
					new Music("The Enchanter", -1, -1, -1, -1, 541, 15887, 352), //
					new Music("Emperor", 2880, 3200, 2943, 3263, 138, 4441, 93),
					new Music("Emperor", 2880, 9600, 2943, 9663, 138, 4441, 93),
					new Music("Escape", 2688, 9664, 2751, 9727, 17, 6943, 94),
					new Music("Etceteria", 2560, 3840, 2623, 3903, 285, 11108, 95),
					new Music("Everlasting Fire", 3328, 3904, 3391, 3967, 586, 18784, 353), //
					new Music("Everywhere", 2112, 3264, 2239, 3327, 268, 8572, 96),
					new Music("Evil Bob's Island", 2496, 4736, 2559, 4799, 411, 14243, 97),
					new Music("Expanse", 3136, 3904, 3199, 3967, 106, 4326, 98),
					new Music("Expanse", 3200, 3328, 3263, 3391, 106, 4326, 98),
					new Music("Expanse", 3200, 9728, 3263, 9791, 106, 4326, 98),
					new Music("Expecting", 2432, 3200, 2495, 3263, 41, 4327, 99),
					new Music("Expecting", 2432, 9600, 2495, 9663, 41, 4327, 99),
					new Music("Expedition", 2880, 9984, 2943, 10047, 153, 4437, 100),
					new Music("Exposed", 2176, 3072, 2239, 3135, 270, 8568, 101),
					new Music("Faerie", 2368, 4352, 2431, 4479, 118, 4328, 102),
					new Music("Faithless", 3200, 3584, 3327, 3647, 337, 10129, 103),
					new Music("Fanfare", 2944, 3328, 3007, 3391, 72, 4329, 104),
					new Music("Fanfare 2", 2944, 3008, 3007, 3071, 166, 4330, 105),
					new Music("Fanfare 3", 2624, 3136, 2687, 3199, 167, 4331, 106),
					new Music("Far Away", 2304, 3136, 2367, 3199, 372, 7030, 107),
					new Music("Fear and Loathing", -1, -1, -1, -1, 602, 15070, 354), //
					new Music("Fenkenstrain's Refrain", 3456, 3520, 3583, 3583, 344, 12126, 108),
					new Music("Fight or Flight", 1920, 4608, 2047, 4671, 375, 7044, 109),
					new Music("Find My Way", 2688, 9088, 2815, 9151, 312, 11142, 110),
					new Music("Fire and Brimstone", 2368, 5120, 2431, 5183, 463, 2802, 111),
					new Music("Fishing", 2816, 3392, 2879, 3455, 119, 4332, 112),
					new Music("Flute Salad", 3136, 3264, 3199, 3327, 163, 4333, 113),
					new Music("Forbidden", 3264, 3520, 3391, 3583, 121, 676, 114),
					new Music("Forest", 2240, 3136, 2303, 3199, 251, 8574, 115),
					new Music("Forever", 3072, 3456, 3135, 3519, 98, 4334, 116),
					new Music("Forever", 3072, 9856, 3135, 10047, 98, 4334, 116),
					new Music("Forgettable Melody", -1, -1, -1, -1, 436, 14167, 355), //
					new Music("Forgotten", -1, -1, -1, -1, 378, 12842, 356), //
					new Music("Frogland", 2432, 4736, 2495, 4799, 409, 14189, 117),
					new Music("Frostbite", 2816, 3776, 2879, 3839, 294, 12847, 118),
					new Music("Fruits de Mer", 2752, 3264, 2815, 3327, 347, 11107, 119),
					new Music("Funny Bunnies", 2432, 5248, 2559, 5311, 603, 12371, 357), //
					new Music("Gaol", 3008, 3712, 3071, 3775, 159, 4335, 120),
					new Music("Gaol", 2496, 3008, 2559, 3071, 159, 4335, 120),
					new Music("Gaol", 2496, 9408, 2559, 9471, 159, 4335, 120),
					new Music("Garden", 3200, 3392, 3263, 3455, 125, 4336, 121),
					new Music("Gnome King", 2432, 3456, 2495, 3583, 22, 4337, 122),
					new Music("Gnome Theme", 3008, 3392, 3071, 3455, 33, 4338, 123),
					new Music("Gnome Village", 2432, 3392, 2495, 3455, 33, 4339, 124),
					new Music("Gnome Village 2", 2304, 3392, 2431, 3455, 101, 4340, 125),
					new Music("Gnome", 2944, 3456, 3007, 3519, 112, 4341, 126),
					new Music("Gnomeball", 2304, 3456, 2431, 3583, 112, 4342, 127),
					new Music("Goblin Game", 2560, 9792, 2623, 9855, 346, 11106, 128),
					new Music("Golden Touch", -1, -1, -1, -1, 535, 15888, 358), //
					new Music("Greatness", 3136, 3328, 3199, 3391, 116, 4343, 129),
					new Music("Grip of the Talon", -1, -1, -1, -1, 520, 15350, 359), //
					new Music("Grotto", 3392, 9728, 3455, 9791, 246, 8138, 130),
					new Music("Grumpy", 2560, 2944, 2623, 3007, 128, 675, 131),
					new Music("Harmony 2", 3200, 9600, 3220, 9663, 46, 6843, 132),
					new Music("Haunted Mine", 2752, 4416, 2815, 4479, 277, 11481, 133),
					new Music("Have a Blast", 1920, 4928, 1983, 4991, 434, 14602, 134),
					new Music("Heart and Mind", 2496, 4800, 2559, 4863, 190, 6945, 135),
					new Music("Hells Bells", 2752, 3712, 2815, 3839, 4, 11881, 136),
					new Music("Hermit", 2240, 4736, 2303, 4799, 97, 8434, 137),
					new Music("High Seas", 2752, 3136, 2815, 3199, 55, 4345, 138),
					new Music("Horizon", 2880, 3392, 2943, 3455, 18, 4346, 139),
					new Music("Hypnotized", -1, -1, -1, -1, 384, 4506, 360), //
					new Music("Iban", 2112, 4544, 2175, 4735, 1, 4347, 140),
					new Music("Ice Melody", 2816, 3456, 2879, 3519, 87, 961, 141),
					new Music("In Between", 2496, 4928, 2623, 4991, 370, 12809, 142),
					new Music("In the Brine", 3648, 2944, 3711, 3007, 530, 17509, 143),
					new Music("In the Clink", 2048, 4416, 2111, 4479, 511, 16146, 144),
					new Music("In The Manor", 2560, 3008, 2623, 3071, 188, 4348, 145),
					new Music("In The Pits", 2432, 5120, 2495, 5183, 469, 2803, 146),
					new Music("Incantation", -1, -1, -1, -1, 519, 15355, 361), //
					new Music("Insect Queen", 3456, 9472, 3583, 9535, 260, 1739, 147),
					new Music("Inspiration", 3008, 3520, 3071, 3583, 96, 4349, 148),
					new Music("Into the Abyss", 3008, 4800, 3071, 4927, 412, 13712, 149),
					new Music("Intrepid", 2304, 9792, 2367, 9919, 95, 4350, 150),
					new Music("Island Life", 2688, 2688, 2815, 2751, 306, 11138, 151),
					new Music("Jolly-R", 2752, 3200, 2815, 3263, 6, 4351, 152),
					new Music("Island", 2816, 3136, 2879, 3199, 306, 4352, 153),
					new Music("Island", 2816, 2880, 2879, 2943, 306, 4352, 153),
					new Music("Jungle Troubles", 2880, 3072, 2943, 3135, 479, 14603, 154),
					new Music("Jungly 1", 2752, 2944, 2815, 3007, 114, 4353, 155),
					new Music("Jungly 1", 2752, 9344, 2815, 9407, 114, 4353, 155),
					new Music("Jungly 2", 2688, 3200, 2751, 3263, 115, 4354, 156),
					new Music("Jungly 3", 2752, 3008, 2815, 3071, 117, 4355, 157),
					new Music("Karamja Jam", 2688, 9408, 2751, 9535, 362, 11939, 158),
					new Music("Kingdom", 2816, 3520, 2879, 3583, 9, 8435, 159),
					new Music("Knightly", 2560, 3264, 2623, 3327, 191, 4356, 160),
					new Music("La Mort", 2176, 4800, 2239, 4863, 134, 6026, 161),
					new Music("Lair", 3456, 9664, 3519, 9727, 287, 8967, 162),
					new Music("Lament", 3072, 9280, 3135, 9343, 542, 16148, 163),
					new Music("Land of the Dwarves", 2816, 10176, 2879, 10239, 396, 13576, 164),
					new Music("Landlubber", 2688, 3136, 2751, 3199, 164, 1883, 165),
					new Music("Last Stand", -1, -1, -1, -1, 546, 247, 362), //
					new Music("Lasting", 2624, 3392, 2687, 3455, 60, 4357, 166),
					new Music("Legend", 2688, 3584, 2815, 3647, 293, 7452, 167),
					new Music("Legion", 3008, 3648, 3071, 3711, 66, 4358, 168),
					new Music("Legion", 2496, 3520, 2559, 3583, 66, 4358, 168),
					new Music("Lighthouse", 2496, 3584, 2559, 3647, 320, 10131, 169),
					new Music("Lightness", 3136, 3520, 3199, 3583, 113, 4359, 170),
					new Music("Lightwalk", 2752, 3392, 2815, 3455, 74, 4360, 171),
					new Music("Lonesome", 3264, 9408, 3327, 9471, 168, 6185, 172),
					new Music("Long Ago", 2624, 3072, 2687, 3135, 161, 4361, 173),
					new Music("Long Way Home", 2944, 3200, 3007, 3263, 12, 4362, 174),
					new Music("Lost Soul", 2240, 3072, 2367, 3135, 253, 8569, 175),
					new Music("Lullaby", 3328, 3392, 3391, 3455, 20, 4364, 176),
					new Music("Lullaby", 2624, 3520, 2687, 3583, 20, 4364, 176),
					new Music("Mad Eadgar", 2880, 10048, 2943, 10111, 264, 189, 177),
					new Music("Mage Arena", 3072, 3904, 3135, 3967, 13, 4365, 178),
					new Music("Mage Arena", 2496, 4672, 2559, 4735, 13, 4365, 178),
					new Music("Magic Dance", 2560, 3072, 2623, 3135, 185, 4366, 179),
					new Music("Magical Journey", 2688, 3392, 2751, 3455, 184, 4367, 180),
					new Music("Making Waves", 2304, 3648, 2367, 3711, 544, 248, 363), //
					new Music("Making Waves", 2304, 3584, 2431, 3647, 544, 248, 363), //
					new Music("March", 2496, 3328, 2559, 3391, 328, 8975, 181),
					new Music("Marooned", 2880, 2688, 2943, 2751, 304, 11137, 182),
					new Music("Marooned", 3008, 5440, 3071, 5503, 304, 11137, 182),
					new Music("Marzipan", 2752, 10112, 2815, 10239, 261, 190, 183),
					new Music("Marzipan", 2816, 10048, 2879, 10111, 261, 190, 183),
					new Music("Masquerade", 2688, 9984, 2751, 10047, 340, 12047, 184),
					new Music("Mausoleum", 3392, 9856, 3455, 9919, 156, 8116, 185),
					new Music("Medieval", 3264, 3392, 3327, 3455, 157, 4368, 186),
					new Music("Mellow", 2560, 3392, 2623, 3455, 193, 4369, 187),
					new Music("Melodrama", 2432, 3072, 2495, 3135, 317, 11477, 188),
					new Music("Meridian", 2112, 3136, 2239, 3199, 254, 8570, 189),
					new Music("Method of Madness", -1, -1, -1, -1, 600, 15074, 364), //
					new Music("Miles Away", 2880, 3264, 2943, 3327, 107, 4370, 190),
					new Music("Miles Away", 2624, 4672, 2687, 4735, 107, 4370, 190),
					new Music("Mind Over Matter", -1, -1, -1, -1, 534, 15889, 365), //
					new Music("Miracle Dance", 2752, 4800, 2815, 4863, 65, 4371, 191),
					new Music("Mirage", 3264, 9152, 3327, 9215, 388, 4507, 192),
					new Music("Miscellania", 2496, 3840, 2559, 3903, 284, 11109, 193),
					new Music("Monarch Waltz", 2688, 3520, 2751, 3583, 21, 4372, 194),
					new Music("Monkey Madness", 2752, 2752, 2815, 2815, 303, 11136, 195),
					new Music("Monster Melee", 3136, 9600, 3199, 9663, 343, 12336, 196),
					new Music("Moody", 3136, 3584, 3199, 3647, 10, 4373, 197),
					new Music("Moody", 2368, 3264, 2495, 3327, 10, 4373, 197),
					new Music("Morytania", 3392, 3456, 3455, 3519, 48, 8117, 198),
					new Music("Mudskipper Melody", 2944, 3072, 3007, 3135, 515, 15293, 199),
					new Music("Narnode's Theme", 2432, 9856, 2495, 9919, 348, 11133, 200),
					new Music("Natural", 3392, 3328, 3455, 3391, 245, 8139, 201),
					new Music("Natural", 2240, 4992, 2303, 5055, 245, 8139, 201),
					new Music("Newbie Melody", 3008, 3008, 3135, 3135, 62, 4375, 202),
					new Music("Newbie Melody", 3136, 3072, 3199, 3135, 62, 4375, 202),
					new Music("Neverland", 2432, 3328, 2495, 3391, 155, 4374, 203),
					new Music("Nightfall", 3200, 3904, 3263, 3967, 127, 4376, 204),
					new Music("Nightfall", 2944, 3264, 3007, 3327, 127, 4376, 204),
					new Music("The Noble Rodent", -1, -1, -1, -1, 485, 15495, 366), //
					new Music("No Way Out", 3264, 9792, 3327, 9855, 594, 15077, 367), //
					new Music("No Way Out", 3072, 5184, 3135, 5247, 594, 15077, 367), //
					new Music("No Way Out", 3008, 5184, 3040, 5214, 594, 15077, 367), //
					new Music("Nomad", 2752, 3072, 2815, 3135, 58, 1893, 205),
					new Music("Null and Void", 2624, 2624, 2687, 2687, 587, 18466, 381),
					new Music("Oriental", 2880, 9344, 2943, 9407, 103, 4377, 206),
					new Music("Out of the Deep", 2496, 9984, 2559, 10047, 322, 10132, 207),
					new Music("Over to Nardah", 3392, 2880, 3455, 2943, 447, 15590, 208),
					new Music("Overpass", 2304, 3264, 2367, 3327, 256, 8573, 209),
					new Music("Overture", 2688, 3456, 2751, 3519, 7, 4378, 210),
					new Music("Parade", 3264, 3456, 3391, 3519, 93, 4379, 211),
					new Music("Path of Peril", 2624, 5056, 2751, 5119, 393, 12845, 212),
					new Music("Pathways", 2688, 9536, 2751, 9599, 364, 11940, 213),
					new Music("Pest Control", 2624, 2560, 2687, 2623, 584, 18690, 368), //
					new Music("Pharaoh's Tomb", 3328, 2816, 3391, 2879, 505, 16147, 214),
					new Music("Pharaoh's Tomb", 3008, 4672, 3071, 4735, 505, 16147, 214),
					new Music("Phasmatys", 3648, 9856, 3711, 9919, 354, 12287, 215),
					new Music("Pheasant Peasant", 2560, 4736, 2623, 4800, 419, 14188, 216),
					new Music("Pirates of Peril", 3008, 3904, 3071, 3967, 334, 10127, 217),
					new Music("The Power of Tears", -1, -1, -1, -1, 398, 3277, 369), //
					new Music("Principality", 2880, 3520, 2943, 3583, 149, 8433, 218),
					new Music("Quest", 2560, 4800, 2623, 4863, 158, 4380, 219),
					new Music("Rat a tat tat", 2880, 5056, 2943, 5119, 482, 15496, 220),
					new Music("Rat Hunt", 2816, 5056, 2879, 5119, 491, 11865, 221),
					new Music("Ready for Battle", 2368, 9472, 2431, 9535, 318, 11478, 222),
					new Music("Regal", 3264, 3904, 3327, 3967, 329, 8976, 223),
					new Music("Reggae", 2880, 2880, 3007, 2943, 78, 4382, 224),
					new Music("Reggae 2", 2880, 3008, 2943, 3071, 89, 4383, 225),
					new Music("Rellekka", 2560, 3648, 2687, 3775, 289, 10112, 226),
					new Music("Righteousness", 2432, 4800, 2495, 4863, 262, 192, 227),
					new Music("Right on Track", -1, -1, -1, -1, 44, 14168, 370), //
					new Music("Riverside", 2688, 3264, 2751, 3327, 91, 4384, 228),
					new Music("Riverside", 2112, 3072, 2175, 3135, 91, 4384, 228),
					new Music("Romancing the Crone", 2752, 3840, 2815, 3903, 335, 11882, 229),
					new Music("Roll the Bones", -1, -1, -1, -1, 533, 15890, 371), //
					new Music("Romper Chomper", 2304, 3008, 2431, 3071, 390, 13359, 230),
					new Music("Royale", 2880, 9664, 2943, 9727, 53, 4385, 231),
					new Music("Rune Essence", 2880, 4800, 2943, 4863, 57, 4386, 232),
					new Music("Sad Meadow", 2496, 3264, 2559, 3327, 5, 4387, 233),
					new Music("Sad Meadow", 2752, 4672, 2815, 4735, 5, 4387, 233),
					new Music("Saga", 2560, 3584, 2687, 3647, 290, 7451, 234),
					new Music("Sarcophagus", 3200, 9280, 3263, 9343, 359, 12846, 235),
					new Music("Sarim's Vermin", 2944, 9600, 3007, 9663, 490, 15498, 236),
					new Music("Scape Cave", 3136, 9856, 3327, 9919, 144, 4388, 237),
					new Music("Scape Cave", 3072, 9572, 3135, 9535, 144, 4388, 237),
					new Music("Scape Sad", 3264, 3840, 3391, 3903, 331, 8977, 238),
					new Music("Scape Soft", 2944, 3392, 3007, 3455, 54, 5988, 239),
					new Music("Scape Wild", 3200, 3648, 3263, 3711, 332, 8978, 240),
					new Music("Scape Wild", 3136, 3840, 3263, 3903, 332, 8978, 240),
					new Music("Scarab", 3136, 2880, 3327, 2943, 352, 12850, 241),
					new Music("Sea Shanty", 2880, 3136, 2943, 3199, 92, 4392, 242),
					new Music("Sea Shanty 2", 3008, 3200, 3071, 3263, 35, 4393, 243),
					new Music("Serenade", 2368, 3136, 2495, 3199, 110, 4394, 244),
					new Music("Serene", 2944, 3904, 3007, 3967, 52, 4395, 245),
					new Music("Serene", 2944, 10303, 3007, 10367, 52, 4395, 245),
					new Music("Serene", 2816, 4800, 2879, 4863, 52, 4395, 245),
					new Music("Settlement", 2752, 3648, 2815, 3711, 356, 12391, 246),
					new Music("Shadowland", 3420, 3200, 3455, 3263, 286, 8970, 247),
					new Music("Shadowland", 3456, 3264, 3519, 3327, 286, 8970, 247),
					new Music("Shadowland", 2112, 4992, 2175, 5055, 286, 8970, 247),
					new Music("Shine", 3328, 3264, 3391, 3327, 122, 4396, 248),
					new Music("Shining", 3200, 3712, 3263, 3775, 120, 5990, 249),
					new Music("Shipwrecked", 3584, 3520, 3647, 3583, 353, 12286, 250),
					new Music("Showdown", 2688, 9152, 2751, 9215, 311, 11141, 251),
					new Music("Sojourn", 2816, 3648, 2943, 3711, 257, 188, 252),
					new Music("Soundscape", 2432, 2944, 2559, 3007, 80, 4397, 253),
					new Music("Sphinx", 3264, 2816, 3327, 2879, 387, 12851, 254),
					new Music("Spirit", 3136, 3392, 3199, 3455, 175, 4398, 255),
					new Music("Spirits of the Elid", 3328, 9536, 3391, 9599, 462, 15591, 256),
					new Music("Splendour", 2880, 3456, 2943, 3519, 77, 4399, 257),
					new Music("Spooky Jungle", 2752, 2880, 2815, 2943, 129, 4401, 258),
					new Music("Spooky Jungle", 2880, 9472, 2943, 9535, 129, 4401, 258),
					new Music("Spooky", 3072, 3328, 3135, 3391, 333, 8979, 259),
					new Music("Spooky", 1920, 4992, 1952, 5055, 333, 8979, 259),
					new Music("Spooky 2", 3392, 9600, 3519, 9663, 11, 11482, 260),
					new Music("Stagnant", 3456, 3328, 3519, 3391, 241, 8141, 261),
					new Music("Stagnant", 2176, 4992, 2239, 5055, 241, 8141, 261),
					new Music("Starlight", 2944, 9536, 3020, 9599, 108, 4402, 262),
					new Music("Starlight", 3221, 9560, 3071, 9599, 108, 4402, 262),
					new Music("Start", 3072, 3264, 3135, 3327, 151, 4434, 263),
					new Music("Still Night", 3264, 3328, 3327, 3391, 111, 4403, 264),
					new Music("Stillness", 3456, 9792, 3519, 9855, 319, 11906, 265),
					new Music("Storm Brew", 2624, 5184, 2687, 5247, 568, 17524, 266),
					new Music("Stranded", 2816, 3712, 2943, 3775, 292, 11883, 267),
					new Music("Strange Place", -1, -1, -1, -1, 470, 15234, 372), //
					new Music("Stratosphere", 2112, 4800, 2175, 4863, 243, 7460, 268),
					new Music("Subterranea", 2496, 10112, 2560, 10175, 517, 15180, 269),
					new Music("Sunburn", 3200, 2944, 3263, 3007, 267, 7453, 270),
					new Music("Sunburn", 3328, 2880, 3391, 2943, 267, 7453, 270),
					new Music("Superstition", 2752, 9280, 2815, 9343, 265, 4881, 271),
					new Music("Suspicious", -1, -1, -1, -1, 308, 11140, 373), //
					new Music("Tale of Keldagrim", 2880, 10112, 2943, 10239, 395, 13575, 272),
					new Music("Talking Forest", 2624, 3456, 2687, 3519, 140, 4435, 273),
					new Music("Tears of Guthix", 3200, 9472, 3263, 9535, 397, 3276, 274),
					new Music("Technology", 2560, 4480, 2687, 4543, 296, 11135, 275),
					new Music("Temple of Light", 1856, 4608, 1919, 4671, 376, 6025, 276),
					new Music("Temple", 2752, 9152, 2815, 9215, 307, 11139, 277),
					new Music("The Cellar Dwellers", 2496, 9664, 2623, 9727, 478, 14455, 278),
					new Music("The Chosen", 2432, 4928, 2495, 4991, 425, 13900, 279),
					new Music("The Desert", 3136, 3008, 3263, 3071, 79, 4404, 280),
					new Music("The Desolate Isle", 2496, 3712, 2559, 3775, 461, 11095, 281),
					new Music("The Far Side", 3008, 5056, 3071, 5119, 403, 13780, 282),
					new Music("The Genie", 3328, 9280, 3391, 9343, 464, 5997, 283),
					new Music("The Golem", 3392, 3072, 3455, 3135, 377, 12841, 284),
					new Music("The Golem", 3456, 3072, 3519, 3160, 377, 12841, 284),
					new Music("The Lost Melody", 3264, 9600, 3327, 9663, 407, 13360, 285),
					new Music("The Lost Tribe", 3221, 9600, 3263, 9663, 420, 13361, 286),
					new Music("The Mad Mole", 1728, 5120, 1791, 5247, 573, 18175, 287),
					new Music("The Monsters Below", 2432, 10112, 2495, 10175, 448, 14186, 288),
					new Music("The Navigator", 2624, 9984, 2687, 10047, 316, 10114, 289),
					new Music("The Other Side", 3648, 3456, 3711, 3583, 355, 12288, 290),
					new Music("The Quizmaster", 1920, 4736, 1983, 4799, 413, 14190, 291),
					new Music("The Rogues Den", 2944, 4928, 3007, 5119, 402, 13779, 292),
					new Music("The Rogues Den", 3008, 4928, 3071, 5055, 402, 13779, 292),
					new Music("The Shadow", 2816, 3200, 2879, 3327, 170, 4405, 293),
					new Music("The Slayer", 2752, 9984, 2815, 10047, 341, 12049, 294),
					new Music("The Terrible Tower", 3392, 3520, 3455, 3583, 339, 12048, 295),
					new Music("The Tower", 2560, 3328, 2623, 3391, 133, 4406, 296),
					new Music("The Tower", 2496, 9728, 2623, 9791, 133, 4406, 296),
					new Music("Theme", 2560, 3456, 2623, 3519, 109, 4436, 297),
					new Music("Theme", 2496, 9856, 2623, 9919, 109, 4436, 297),
					new Music("Throne of the Demon", -1, -1, -1, -1, 379, 12843, 374), //
					new Music("Time Out", 2880, 4544, 2943, 4607, 242, 8432, 298),
					new Music("Time to Mine", 2816, 10112, 2879, 10175, 369, 12808, 299),
					new Music("Tiptoe", 3072, 9728, 3135, 9791, 338, 10133, 300),
					new Music("Title Fight", 3182, 9728, 3199, 9791, 525, 15826, 301),
					new Music("Title Fight", 3136, 9728, 3157, 9791, 525, 15826, 301),
					new Music("Title Fight", 3158, 9728, 3181, 9751, 525, 15826, 301),
					new Music("Title Fight", 3158, 9765, 3181, 9791, 525, 15826, 301),
					new Music("Tomorrow", 3008, 3136, 3071, 3199, 105, 6297, 302),
					new Music("Too Many Cooks...", 2944, 9856, 3007, 9919, 582, 18305, 303),
					new Music("Trawler Minor", 1920, 4800, 2047, 4863, 51, 4408, 304),
					new Music("Trawler", 1856, 4800, 1919, 4863, 38, 4407, 305),
					new Music("Tree Spirits", 2304, 3328, 2431, 3391, 130, 4409, 306),
					new Music("Tremble", 2816, 3584, 2879, 3647, 187, 8437, 307),
					new Music("Tribal Background", 2816, 3072, 2879, 3135, 162, 4410, 308),
					new Music("Tribal Background", 2816, 9472, 2879, 9535, 162, 4410, 308),
					new Music("Tribal", 2816, 3008, 2879, 3071, 165, 4411, 309),
					new Music("Tribal 2", 2880, 2944, 3007, 3007, 94, 4412, 310),
					new Music("Trinity", 2688, 3328, 2751, 3391, 192, 4413, 311),
					new Music("Trinity", 2688, 9728, 2751, 9791, 192, 4413, 312),
					new Music("Troubled", 2944, 3648, 3007, 3711, 183, 4414, 313),
					new Music("Twilight", 2688, 9856, 2751, 9919, 88, 7461, 314),
					new Music("TzHaar!", 2368, 5056, 2431, 5119, 473, 6013, 315),
					new Music("Undercurrent", 3072, 3648, 3135, 3711, 176, 1890, 316),
					new Music("Underground Pass", 2368, 9536, 2431, 9663, 323, 7382, 317),
					new Music("Underground", 3328, 3584, 3391, 3647, 179, 4415, 318),
					new Music("Underground", 2816, 9728, 2879, 9791, 179, 4415, 318),
					new Music("Understanding", 2368, 4800, 2431, 4863, 131, 678, 319),
					new Music("Unknown Land", 3072, 3200, 3135, 3263, 3, 4418, 320),
					new Music("Upcoming", 2624, 3200, 2687, 3263, 70, 4420, 321),
					new Music("Venture", 3328, 3328, 3391, 3391, 75, 4421, 322),
					new Music("Venture 2", 3328, 9728, 3391, 9855, 45, 6867, 323),
					new Music("Victory is Mine", 3158, 9755, 3181, 9761, 528, 15829, 324),
					new Music("Village", 3456, 3456, 3519, 3519, 61, 8118, 325),
					new Music("Vision", 3072, 3136, 3135, 3199, 85, 4422, 326),
					new Music("Vision", 3072, 9535, 3135, 9603, 85, 4422, 326),
					new Music("Voodoo Cult", 2368, 4672, 2431, 4735, 30, 4423, 327),
					new Music("Voodoo Cult", 2880, 9280, 2943, 9343, 30, 4423, 327),
					new Music("Voyage", 2496, 3456, 2559, 3519, 32, 4424, 328),
					new Music("Wander", 3008, 3264, 3071, 3327, 49, 4425, 329),
					new Music("Warrior", 2624, 10048, 2687, 10111, 295, 10113, 330),
					new Music("Waterfall", 2496, 3391, 2559, 3455, 82, 4426, 331),
					new Music("Waterfall", 2496, 9792, 2559, 9855, 82, 4426, 331),
					new Music("Waterlogged", 3456, 3392, 3583, 3455, 244, 8140, 332),
					new Music("Waterlogged", 1984, 4992, 2111, 5055, 244, 8140, 332),
					new Music("Wayward", 2432, 9408, 2495, 9471, 394, 13354, 333),
					new Music("Well Of Voyage", 2304, 9600, 2367, 9663, 271, 8566, 334),
					new Music("Wild Side", 3008, 3840, 3135, 3903, 475, 14454, 335),
					new Music("Wilderness", 2944, 3584, 3007, 3647, 435, 13713, 336),
					new Music("Wilderness", 3072, 3712, 3135, 3775, 435, 13713, 336),
					new Music("Wilderness 2", 3008, 3776, 3135, 3839, 42, 4427, 337),
					new Music("Wilderness 3", 2944, 3712, 3007, 3775, 43, 4428, 338),
					new Music("Wildwood", 3072, 3584, 3135, 3647, 8, 6983, 339),
					new Music("Witching", 3264, 3712, 3391, 3775, 14, 4430, 340),
					new Music("Woe of the Wyvern", 3021, 9536, 3071, 9559, 529, 15830, 341),
					new Music("Wolf Mountain", 3136, 3776, 3263, 3839, 422, 6842, 342),
					new Music("Wonder", 2944, 3520, 3007, 3583, 34, 4431, 343),
					new Music("Wonderous", 2624, 3328, 2687, 3391, 81, 4432, 344),
					new Music("Woodland", 2112, 3200, 2239, 3263, 255, 8571, 345),
					new Music("Workshop", 3008, 3328, 3071, 3391, 15, 4433, 346),
					new Music("Wrath and Ruin", -1, -1, -1, -1, 565, 15081, 375), //
					new Music("Xenophobe", 1856, 4352, 1919, 4415, 524, 15346, 347),
					new Music("Xenophobe", 2880, 4416, 2943, 4479, 524, 15346, 347),
					new Music("Yesteryear", 3200, 3136, 3263, 3199, 145, 5989, 348),
					new Music("Zealot", 2688, 4800, 2751, 4863, 146, 1898, 349),
					new Music("Zogre Dance", 2432, 3008, 2495, 3071, 392, 13355, 350),
					new Music("Scape Main", -1, -1, -1, -1, 0, 4389, 376), //
					new Music("Scape Original", -1, -1, -1, -1, 400, 12840, 377), //
					new Music("Scape Santa", -1, -1, -1, -1, 547, 11880, 378), //
					new Music("Scape Scared", -1, -1, -1, -1, 321, 10126, 379), //
					new Music("Ground Scape", -1, -1, -1, -1, 466, 13972, 380), //
					new Music("MastermindLess", -1, -1, -1, -1, 577, 18304, 382),
					new Music("Fangs for the memory", -1, -1, -1, -1, 504, 18516, 383),
			};
}
