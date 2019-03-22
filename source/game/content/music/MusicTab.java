package game.content.music;

import game.player.Player;


/**
 * @author Jordy/mrclassic
 */

public class MusicTab {

	/**
	 * @param songId
	 * @param buttonId
	 * @param songName
	 * @param array
	 */
	public MusicTab(String songName, int songId, int buttonId, int array) {
		this.songName = songName;
		this.songId = songId;
		this.buttonId = buttonId;
		this.array = array;
	}

	public int songId, buttonId, array, x, y, a, b;

	public String songName;

	public static final MusicTab[] music =
			{
					new MusicTab("Harmony", 76, 16248, 0), // Harmony
					new MusicTab("Autumn Voyage", 2, 16208, 1), // Autumn Voyage
					new MusicTab("7th Realm", 363, 46165, 2),
					new MusicTab("Adventure", 177, 16191, 3),
					new MusicTab("Al Kharid", 50, 16192, 4),
					new MusicTab("Alone", 102, 16193, 5),
					new MusicTab("Ambient Jungle", 90, 16194, 6),
					new MusicTab("Anywhere", 305, 43126, 7),
					new MusicTab("Arabian", 36, 16195, 8),
					new MusicTab("Arabian 2", 123, 16196, 9),
					new MusicTab("Arabian 3", 124, 16197, 10),
					new MusicTab("Arabique", 19, 16198, 11),
					new MusicTab("Army of Darkness", 160, 16199, 12),
					new MusicTab("Arrival", 186, 16200, 13),
					new MusicTab("Artistry", 247, 34231, 14),
					new MusicTab("Attack 1", 24, 16201, 15),
					new MusicTab("Attack 2", 25, 16202, 16),
					new MusicTab("Attack 3", 26, 16203, 17),
					new MusicTab("Attack 4", 27, 16204, 18),
					new MusicTab("Attack 5", 28, 16205, 19),
					new MusicTab("Attack 6", 29, 16206, 20),
					new MusicTab("Attention", 180, 16207, 21),
					new MusicTab("Aye Car Rum Ba", 497, 58024, 22),
					new MusicTab("Aztec", 248, 2152, 23),
					new MusicTab("Background", 324, 35011, 24),
					new MusicTab("Ballad of Enchantment", 152, 16210, 25),
					new MusicTab("Bandit Camp", 263, 29030, 26),
					new MusicTab("Barbarianism", 141, 19000, 27),
					new MusicTab("Barking Mad", 345, 47096, 28),
					new MusicTab("Baroque", 99, 16211, 29),
					new MusicTab("Beyond", 100, 16212, 30),
					new MusicTab("Big Chords", 83, 16213, 31),
					new MusicTab("Blistering Barnacles", 498, 58023, 32),
					new MusicTab("Body Parts", 342, 47095, 33),
					new MusicTab("Bone Dance", 154, 35008, 34),
					new MusicTab("Bone Dry", 266, 50044, 35),
					new MusicTab("Book of Spells", 64, 16214, 36),
					new MusicTab("Borderland", 291, 39127, 37),
					new MusicTab("Breeze", 132, 33117, 38),
					new MusicTab("Brew Hoo Hoo!", 471, 55162, 39),
					new MusicTab("Bubble and Squeak", 489, 60134, 40),
					new MusicTab("Cabin Fever", 330, 68100, 41), //545
					new MusicTab("Camelot", 104, 16215, 42),
					new MusicTab("Castlewars", 314, 44212, 43),
					new MusicTab("Catch me if you can", 481, 60133, 44),
					new MusicTab("Cave Background", 325, 35012, 45),
					new MusicTab("Cave of Beasts", 357, 48102, 46),
					new MusicTab("Cave of the Goblins", 389, 49040, 47),
					new MusicTab("Cavern", 68, 16217, 48),
					new MusicTab("Cellar Song", 330, 35020, 49),
					new MusicTab("Chain of Command", 63, 16218, 50),
					new MusicTab("Chamber", 282, 44220, 51),
					new MusicTab("Chef Surprise", 583, 246, 52),
					new MusicTab("Chickened Out", 575, 18302, 53),
					new MusicTab("Chompy Hunt", 71, 2162, 54),
					new MusicTab("City of the Dead", 383, 50049, 55),
					new MusicTab("Claustrophobia", 373, 50010, 56),
					new MusicTab("Close Quarters", 67, 27032, 57),
					new MusicTab("Competition", 269, 42231, 58),
					new MusicTab("Complication", 142, 19006, 59),
					new MusicTab("Contest", 258, 32244, 60),
					new MusicTab("Corporal Punishment", 418, 24007, 61),
					new MusicTab("Courage", 178, 19016, 62),
					new MusicTab("Crystal Castle", 259, 33119, 63),
					new MusicTab("Crystal Cave", 181, 16219, 64),
					new MusicTab("Crystal Sword", 169, 16220, 65),
					new MusicTab("Cursed", 59, 2165, 66),
					new MusicTab("Dagannoth Dawn", 198, 52073, 67),
					new MusicTab("Dance of the Undead", 380, 52040, 68),
					new MusicTab("Dangerous Road", 336, 39144, 69),
					new MusicTab("Dangerous Way", 381, 52041, 70),
					new MusicTab("Dangerous", 182, 16221, 71),
					new MusicTab("Dark", 326, 35013, 72),
					new MusicTab("Davy Jones' Locker", 576, 71099, 73),
					new MusicTab("Dead Can Dance", 476, 56117, 74),
					new MusicTab("Dead Quiet", 84, 31206, 75),
					new MusicTab("Deadlands", 288, 34232, 76),
					new MusicTab("Deep Down", 278, 44219, 77),
					new MusicTab("Deep Wildy", 37, 16223, 78),
					new MusicTab("Desert Heat", 465, 23108, 79),
					new MusicTab("Desert Voyage", 174, 16224, 80),
					new MusicTab("Diango's Little Helpers", 532, 61226, 81),
					new MusicTab("Distant Land", 501, 12248, 351), //
					new MusicTab("Doorways", 56, 16225, 82),
					new MusicTab("Down Below", 361, 50048, 83),
					new MusicTab("Down To Earth", 143, 19015, 84),
					new MusicTab("Dragontooth Island", 358, 48001, 85),
					new MusicTab("Dream", 327, 35014, 86),
					new MusicTab("Duel Arena", 47, 24154, 87),
					new MusicTab("Dunjun", 173, 16227, 88),
					new MusicTab("Dynasty", 351, 50035, 89),
					new MusicTab("Egypt", 69, 16228, 90),
					new MusicTab("Elven Mist", 252, 33127, 91),
					new MusicTab("Emotion", 148, 16229, 92),
					new MusicTab("The Enchanter", 541, 62015, 352), //
					new MusicTab("Emperor", 138, 17089, 93),
					new MusicTab("Escape", 17, 27031, 94),
					new MusicTab("Etceteria", 285, 43100, 95),
					new MusicTab("Everlasting Fire", 586, 73096, 353), //
					new MusicTab("Everywhere", 268, 33124, 96),
					new MusicTab("Evil Bob's Island", 411, 55163, 97),
					new MusicTab("Expanse", 106, 16230, 98),
					new MusicTab("Expecting", 41, 16231, 99),
					new MusicTab("Expedition", 153, 17085, 100),
					new MusicTab("Exposed", 270, 33120, 101),
					new MusicTab("Faerie", 118, 16232, 102),
					new MusicTab("Faithless", 337, 39145, 103),
					new MusicTab("Fanfare", 72, 16233, 104),
					new MusicTab("Fanfare 2", 166, 16234, 105),
					new MusicTab("Fanfare 3", 167, 16235, 106),
					new MusicTab("Far Away", 372, 27118, 107),
					new MusicTab("Fear and Loathing", 602, 58222, 354),
					new MusicTab("Fenkenstrain's Refrain", 344, 47094, 108),
					new MusicTab("Fight or Flight", 375, 27132, 109),
					new MusicTab("Find My Way", 312, 43134, 110),
					new MusicTab("Fire and Brimstone", 463, 10242, 111),
					new MusicTab("Fishing", 119, 16236, 112),
					new MusicTab("Flute Salad", 163, 16237, 113),
					new MusicTab("Forbidden", 121, 2164, 114),
					new MusicTab("Forest", 251, 33126, 115),
					new MusicTab("Forever", 98, 16238, 116),
					new MusicTab("Forgettable Melody", 436, 55087, 355),
					new MusicTab("Forgotten", 378, 50042, 356),
					new MusicTab("Frogland", 409, 55109, 117),
					new MusicTab("Frostbite", 294, 50047, 118),
					new MusicTab("Fruits de Mer", 347, 43099, 119),
					new MusicTab("Funny Bunnies", 603, 72253, 357),
					new MusicTab("Gaol", 159, 16239, 120),
					new MusicTab("Garden", 125, 16240, 121),
					new MusicTab("Gnome King", 22, 16241, 122),
					new MusicTab("Gnome Theme", 33, 16242, 123),
					new MusicTab("Gnome Village", 33, 16243, 124),
					new MusicTab("Gnome Village 2", 101, 16244, 125),
					new MusicTab("Gnome", 56, 16245, 126),
					new MusicTab("Gnomeball", 56, 16246, 127),
					new MusicTab("Goblin Game", 346, 43098, 128),
					new MusicTab("Golden Touch", 535, 62016, 358),
					new MusicTab("Greatness", 116, 16247, 129),
					new MusicTab("Grip of the Talon", 520, 59246, 359),
					new MusicTab("Grotto", 246, 31202, 130),
					new MusicTab("Grumpy", 128, 2163, 131),
					new MusicTab("Harmony 2", 46, 26187, 132),
					new MusicTab("Haunted Mine", 277, 44217, 133),
					new MusicTab("Have a Blast", 434, 57010, 134),
					new MusicTab("Heart and Mind", 190, 27033, 135),
					new MusicTab("Hells Bells", 4, 46105, 136),
					new MusicTab("Hermit", 97, 32242, 137),
					new MusicTab("High Seas", 55, 16249, 138),
					new MusicTab("Horizon", 18, 16250, 139),
					new MusicTab("Hypnotized", 384, 17154, 360),
					new MusicTab("Iban", 1, 16251, 140),
					new MusicTab("Ice Melody", 87, 3193, 141),
					new MusicTab("In Between", 370, 50009, 142),
					new MusicTab("In the Brine", 530, 68101, 143),
					new MusicTab("In the Clink", 511, 63018, 144),
					new MusicTab("In The Manor", 188, 16252, 145),
					new MusicTab("In The Pits", 469, 10243, 146),
					new MusicTab("Incantation", 519, 59251, 361),
					new MusicTab("Insect Queen", 260, 6203, 147),
					new MusicTab("Inspiration", 96, 16253, 148),
					new MusicTab("Into the Abyss", 412, 53144, 149),
					new MusicTab("Intrepid", 95, 16254, 150),
					new MusicTab("Island Life", 306, 43130, 151),
					new MusicTab("Jolly-R", 6, 16255, 152),
					new MusicTab("Jungle Island", 172, 17000, 153),
					new MusicTab("Jungle Troubles", 479, 57011, 154),
					new MusicTab("Jungly 1", 114, 17001, 155),
					new MusicTab("Jungly 2", 115, 17002, 156),
					new MusicTab("Jungly 3", 117, 17003, 157),
					new MusicTab("Karamja Jam", 362, 46163, 158),
					new MusicTab("Kingdom", 9, 32243, 159),
					new MusicTab("Knightly", 191, 17004, 160),
					new MusicTab("La Mort", 134, 23138, 161),
					new MusicTab("Lair", 287, 35007, 162),
					new MusicTab("Lament", 542, 63020, 163),
					new MusicTab("Land of the Dwarves", 396, 53008, 164),
					new MusicTab("Landlubber", 164, 7091, 165),
					new MusicTab("Last Stand", 546, 247, 362), //
					new MusicTab("Lasting", 60, 17005, 166),
					new MusicTab("Legend", 293, 29028, 167),
					new MusicTab("Legion", 66, 17006, 168),
					new MusicTab("Lighthouse", 320, 39147, 169),
					new MusicTab("Lightness", 113, 17007, 170),
					new MusicTab("Lightwalk", 74, 17008, 171),
					new MusicTab("Lonesome", 168, 24041, 172),
					new MusicTab("Long Ago", 161, 17009, 173),
					new MusicTab("Long Way Home", 12, 17010, 174),
					new MusicTab("Lost Soul", 253, 33121, 175),
					new MusicTab("Lullaby", 20, 17012, 176),
					new MusicTab("Mad Eadgar", 264, 189, 177),
					new MusicTab("Mage Arena", 13, 17013, 178),
					new MusicTab("Magic Dance", 185, 17014, 179),
					new MusicTab("Magical Journey", 184, 17015, 180),
					new MusicTab("Making Waves", 544, 248, 363), //
					new MusicTab("March", 328, 35015, 181),
					new MusicTab("Marooned", 304, 43129, 182),
					new MusicTab("Marzipan", 261, 190, 183),
					new MusicTab("Masquerade", 340, 47015, 184),
					new MusicTab("Mausoleum", 156, 31180, 185), //
					new MusicTab("Medieval", 157, 17016, 186),
					new MusicTab("Mellow", 193, 17017, 187),
					new MusicTab("Melodrama", 317, 44213, 188),
					new MusicTab("Meridian", 254, 33122, 189),
					new MusicTab("Method of Madness", 600, 58226, 364), //
					new MusicTab("Miles Away", 107, 17018, 190),
					new MusicTab("Mind Over Matter", 534, 62017, 365), //
					new MusicTab("Miracle Dance", 65, 17019, 191),
					new MusicTab("Mirage", 388, 17155, 192),
					new MusicTab("Miscellania", 284, 43101, 193),
					new MusicTab("Monarch Waltz", 21, 17020, 194),
					new MusicTab("Monkey Madness", 303, 43128, 195),
					new MusicTab("Monster Melee", 343, 48048, 196),
					new MusicTab("Moody", 10, 17021, 197),
					new MusicTab("Morytania", 48, 31181, 198),
					new MusicTab("Mudskipper Melody", 515, 59189, 199),
					new MusicTab("Narnode's Theme", 348, 43125, 200),
					new MusicTab("Natural", 245, 31203, 201),
					new MusicTab("Newbie Melody", 62, 17023, 202),
					new MusicTab("Neverland", 155, 17022, 203),
					new MusicTab("Nightfall", 127, 17024, 204),
					new MusicTab("The Noble Rodent", 485, 60135, 366), //
					new MusicTab("No Way Out", 594, 58229, 367), //
					new MusicTab("Nomad", 58, 7101, 205),
					new MusicTab("Null and Void", 587, 18466, 381),
					new MusicTab("Oriental", 103, 17025, 206),
					new MusicTab("Out of the Deep", 322, 39148, 207),
					new MusicTab("Over to Nardah", 447, 60230, 208),
					new MusicTab("Overpass", 256, 33125, 209),
					new MusicTab("Overture", 7, 17026, 210),
					new MusicTab("Parade", 93, 17027, 211),
					new MusicTab("Path of Peril", 393, 50045, 212),
					new MusicTab("Pathways", 364, 46164, 213),
					new MusicTab("Pest Control", 584, 73002, 368), //
					new MusicTab("Pharaoh's Tomb", 505, 63019, 214),
					new MusicTab("Phasmatys", 354, 47255, 215),
					new MusicTab("Pheasant Peasant", 419, 55108, 216),
					new MusicTab("Pirates of Peril", 334, 39143, 217),
					new MusicTab("The Power of Tears", 398, 12205, 369), //
					new MusicTab("Principality", 149, 32241, 218),
					new MusicTab("Quest", 158, 17028, 219),
					new MusicTab("Rat a tat tat", 482, 60136, 220),
					new MusicTab("Rat Hunt", 491, 60137, 221),
					new MusicTab("Ready for Battle", 318, 44214, 222),
					new MusicTab("Regal", 329, 35016, 223),
					new MusicTab("Reggae", 78, 17030, 224),
					new MusicTab("Reggae 2", 89, 17031, 225),
					new MusicTab("Rellekka", 289, 39128, 226),
					new MusicTab("Righteousness", 262, 192, 227),
					new MusicTab("Right on Track", 44, 55088, 370), //
					new MusicTab("Riverside", 91, 17032, 228),
					new MusicTab("Romancing the Crone", 335, 46106, 229),
					new MusicTab("Roll the Bones", 533, 62018, 371), //
					new MusicTab("Romper Chomper", 390, 52047, 230),
					new MusicTab("Royale", 53, 17033, 231),
					new MusicTab("Rune Essence", 57, 17034, 232),
					new MusicTab("Sad Meadow", 5, 17035, 233),
					new MusicTab("Saga", 290, 29027, 234),
					new MusicTab("Sarcophagus", 359, 50046, 235),
					new MusicTab("Sarim's Vermin", 490, 60138, 236),
					new MusicTab("Scape Cave", 144, 17036, 237),
					new MusicTab("Scape Sad", 331, 35017, 238),
					new MusicTab("Scape Soft", 54, 23100, 239),
					new MusicTab("Scape Wild", 332, 35018, 240),
					new MusicTab("Scarab", 352, 50050, 241),
					new MusicTab("Sea Shanty", 92, 17040, 242),
					new MusicTab("Sea Shanty 2", 35, 17041, 243),
					new MusicTab("Serenade", 110, 17042, 244),
					new MusicTab("Serene", 52, 17043, 245),
					new MusicTab("Settlement", 356, 48103, 246),
					new MusicTab("Shadowland", 286, 35010, 247),
					new MusicTab("Shine", 122, 17044, 248),
					new MusicTab("Shining", 120, 23102, 249),
					new MusicTab("Shipwrecked", 353, 47254, 250),
					new MusicTab("Showdown", 311, 43133, 251),
					new MusicTab("Sojourn", 257, 188, 252),
					new MusicTab("Soundscape", 80, 17045, 253),
					new MusicTab("Sphinx", 387, 50051, 254),
					new MusicTab("Spirit", 175, 17046, 255),
					new MusicTab("Spirits of the Elid", 462, 60231, 256),
					new MusicTab("Splendour", 77, 17047, 257),
					new MusicTab("Spooky", 333, 35019, 259),
					new MusicTab("Spooky 2", 11, 44218, 260),
					new MusicTab("Spooky Jungle", 129, 17049, 258),
					new MusicTab("Stagnant", 241, 31205, 261),
					new MusicTab("Starlight", 108, 17050, 262),
					new MusicTab("Start", 151, 17082, 263),
					new MusicTab("Still Night", 111, 17051, 264),
					new MusicTab("Stillness", 319, 46130, 265),
					new MusicTab("Storm Brew", 568, 68116, 266),
					new MusicTab("Stranded", 292, 46107, 267),
					new MusicTab("Strange Place", 470, 59130, 372), //
					new MusicTab("Stratosphere", 243, 29036, 268),
					new MusicTab("Subterranea", 517, 59076, 269),
					new MusicTab("Sunburn", 267, 29029, 270),
					new MusicTab("Superstition", 265, 19017, 271),
					new MusicTab("Suspicious", 308, 43132, 373), //
					new MusicTab("Tale of Keldagrim", 395, 53007, 272),
					new MusicTab("Talking Forest", 140, 17083, 273),
					new MusicTab("Tears of Guthix", 397, 12204, 274),
					new MusicTab("Technology", 296, 43127, 275),
					new MusicTab("Temple of Light", 376, 23137, 276),
					new MusicTab("Temple", 307, 43131, 277),
					new MusicTab("The Cellar Dwellers", 478, 56119, 278), //
					new MusicTab("The Chosen", 425, 54076, 279), //
					new MusicTab("The Desert", 79, 17052, 280), //
					new MusicTab("The Desolate Isle", 461, 43087, 281), //
					new MusicTab("The Far Side", 403, 53212, 282), //
					new MusicTab("The Genie", 464, 23109, 283), //
					new MusicTab("The Golem", 377, 50041, 284), //
					new MusicTab("The Lost Melody", 407, 52048, 285), //
					new MusicTab("The Lost Tribe", 420, 52049, 286), //
					new MusicTab("The Mad Mole", 573, 70255, 287),
					new MusicTab("The Monsters Below", 448, 55106, 288), //
					new MusicTab("The Navigator", 316, 39130, 289), //
					new MusicTab("The Other Side", 355, 48000, 290), //
					new MusicTab("The Quizmaster", 413, 55110, 291), //
					new MusicTab("The Rogues Den", 402, 53211, 292), //
					new MusicTab("The Shadow", 170, 17053, 293), //
					new MusicTab("The Slayer", 341, 47017, 294), //
					new MusicTab("The Terrible Tower", 339, 47016, 295), //
					new MusicTab("The Tower", 133, 17054, 296), //
					new MusicTab("Theme", 109, 17084, 297),
					new MusicTab("Throne of the Demon", 379, 50043, 374), //
					new MusicTab("Time Out", 242, 32240, 298),
					new MusicTab("Time to Mine", 369, 50008, 299),
					new MusicTab("Tiptoe", 338, 39149, 300),
					new MusicTab("Title Fight", 525, 61210, 301),
					new MusicTab("Tomorrow", 105, 24153, 302),
					new MusicTab("Too Many Cooks...", 582, 71129, 303),
					new MusicTab("Trawler Minor", 51, 17056, 304),
					new MusicTab("Trawler", 38, 17055, 305),
					new MusicTab("Tree Spirits", 130, 17057, 306),
					new MusicTab("Tremble", 187, 32245, 307),
					new MusicTab("Tribal Background", 162, 17058, 308),
					new MusicTab("Tribal", 165, 17059, 309),
					new MusicTab("Tribal 2", 94, 17060, 310),
					new MusicTab("Trinity", 192, 17061, 311),
					new MusicTab("Troubled", 183, 17062, 313),
					new MusicTab("Twilight", 88, 29037, 314),
					new MusicTab("TzHaar!", 473, 23125, 315),
					new MusicTab("Undercurrent", 176, 7098, 316),
					new MusicTab("Underground Pass", 323, 28214, 317),
					new MusicTab("Underground", 179, 17063, 318),
					new MusicTab("Understanding", 131, 2166, 319),
					new MusicTab("Unknown Land", 3, 17066, 320),
					new MusicTab("Upcoming", 70, 17068, 321),
					new MusicTab("Venture", 75, 17069, 322),
					new MusicTab("Venture 2", 45, 26211, 323),
					new MusicTab("Victory is Mine", 528, 61213, 324),
					new MusicTab("Village", 61, 31182, 325),
					new MusicTab("Vision", 85, 17070, 326),
					new MusicTab("Voodoo Cult", 30, 17071, 327),
					new MusicTab("Voyage", 32, 17072, 328),
					new MusicTab("Wander", 49, 17073, 329),
					new MusicTab("Warrior", 295, 39129, 330),
					new MusicTab("Waterfall", 82, 17074, 331),
					new MusicTab("Waterlogged", 244, 31204, 332),
					new MusicTab("Wayward", 394, 52042, 333),
					new MusicTab("Well Of Voyage", 271, 33118, 334),
					new MusicTab("Wilderness", 435, 53145, 336),
					new MusicTab("Wilderness 2", 42, 17075, 337),
					new MusicTab("Wilderness 3", 43, 17076, 338),
					new MusicTab("Wild Side", 475, 56118, 335),
					new MusicTab("Wildwood", 8, 27071, 339),
					new MusicTab("Witching", 14, 17078, 340),
					new MusicTab("Woe of the Wyvern", 529, 61214, 341),
					new MusicTab("Wolf Mountain", 422, 26186, 342),
					new MusicTab("Wonder", 34, 17079, 343),
					new MusicTab("Wonderous", 81, 17080, 344),
					new MusicTab("Woodland", 255, 33123, 345),
					new MusicTab("Workshop", 15, 17081, 346),
					new MusicTab("Wrath and Ruin", 565, 58233, 375), //
					new MusicTab("Xenophobe", 524, 59242, 347),
					new MusicTab("Yesteryear", 145, 23101, 348),
					new MusicTab("Zealot", 146, 7106, 349),
					new MusicTab("Zogre Dance", 392, 52043, 350),
					new MusicTab("Scape Main", 0, 17037, 376), //
					new MusicTab("Scape Original", 400, 50040, 377), //
					new MusicTab("Scape Santa", 547, 46104, 378), //
					new MusicTab("Scape Scared", 321, 39142, 379), //
					new MusicTab("Ground Scape", 466, 54148, 380), //
					new MusicTab("MastermindLess", 577, 71128, 382),
					new MusicTab("Fangs for the memory", 504, 72084, 383),

			};

	public static void setToManual(Player c) {
		c.autoMusic = false;
		c.getPA().sendFrame36(18, 0, false); //1 = AUTO 0 = MANUAL.
	}

	/**
	 * @param c
	 * @param Id
	 */
	public static boolean handleClick(Player c, int Id) {
		switch (Id) {
			//AUTO
			case 24125:
				c.autoMusic = true;
				Music.playRegionMusic(c);
				return true;
			//MANUAL
			case 24126:
				c.autoMusic = false;
				return true;
			case 38197:
				c.isLoopingMusic = !c.isLoopingMusic;
				return true;
		}
		for (int i = 0; i < music.length; i++) {
			if (Id == music[i].buttonId) {
				c.getPA().sendFrame126(music[i].songName, 4439);
				c.getOutStream().createFrame(74);
				c.getOutStream().writeWordBigEndian(music[i].songId);
				//c.playerAssistant.sendMessage("Song: " + music[i].songName + ", ID: " + music[i].songId);
				setToManual(c);
				c.currentMusicOrder = music[i].array;
				return true;
			}
		}
		return false;
	}

}
