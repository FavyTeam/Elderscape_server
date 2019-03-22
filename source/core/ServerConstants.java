package core;

import game.bot.BotManager;
import game.content.shop.ShopAssistant;
import game.item.BloodMoneyPrice;
import game.item.ItemAssistant;
import network.connection.VoteManager;
import utility.Misc;

/**
 * Constants regarding whole server.
 */
public class ServerConstants {
	/**
	 * All custom donator item requests.
	 */
	private final static int[] IMMORTAL_DONATOR_ITEMS_OSRS = {
					16052, // Rainbow Twisted bow for Locals
					16055, // Blue Abyssal tentacle for Verticle
					16056, // Black and orange Twisted bow for Thuggahhh
					16093, // Black Defender Icon for Oatrix
					16042, // Gold Armadyl godsword  for C0nz0le
					16047, // Pink Armadyl godsword for Trenbolone e
					16060, // Black Armadyl godsword for Bites
					16063, // White Armadyl godsword for Russman887
			16091, // Lava Armadyl godsword for Dameon
					16041, // Green Elysian spirit shield with white sigil for Austin W
					16090, // Black Elysian spirit shield with white sigilfor Shattered
					16097, // White Elysian spirit shield with black sigil for Verticle
					16098, // Blue and black Elder maul for Verticle
					16100, // Cyan Abyssal tentacle for Buying Gf69
					16103, // Black and white partyhat for Shattered
					16104, // Black and white Quest point cape for Shattered
					16109, // Cyan Partyhat for Draco
					16113, // Red Elysian spirit shield with black sigil for Thuggahhh
					16112, // Red Armadyl godsword for Thuggahhh
					16122, // Cyan Elder maul for Buying Gf69
					16124, // Black Primordial boots for Shattered
					16133, // Cyan h'ween mask for Buying Gf69
					16134, // Cyan Berserker ring (i) for Buying Gf69
					16136, // Black Abyssal tentacle for Shattered
					16138, // Golden Dragon claws for Vippy
					16150, // Cyan santa hat for Slice U Ko
					16151, // Golden Abyssal tentacle for Hvid
					16155, // Pink partyhat for Cat pewp
					16163, // Black Armadyl godsword for Slice u ko
					16176, // Orange h'ween mask for Real Staker
			16183, // Rainbow Armadyl godsword for Pouch
					16186, // Green Elysian spirit shield with black sigil for Chip
					16187, // Light blue Armadyl godsword for Bluezia
					16199, // Pink h'ween mask for Muppets
					16206, // Green Armadyl godsword for Dirty Specz
					16207, // Pink Armadyl godsword for Muppets
					16208, // Pink Abyssal tentacle for Muppets
					16209, // Cyan Toxic staff of the dead for (no one)
					16210, // Black Dragon claws for Sam gravano
					16224, // Gold and silver Elder maul for Enchiladas
					16225, // Lime green Elder maul for Wespoonedyou
					16226, // Lime green Abyssal tentacle for Wespoonedyou
					16227, // Green Berserker ring (i) for Dirty Specz
					16228, // Green Arcane spirit shield for Dirty Specz
					16229, // White h'ween mask for Askeladden
					16230, // Black granite maul for Askeladden
					16231, // Bandos chestplate rainbow for Locals
					16232, // Bandos plateskirt rainbow for Locals
					16255, // Orange Elder maul for Lowriders
					16256, // Orange Elysian spirit shield with white sigil for Lowriders
					16257, // Orange Abyssal tentacle for Lowriders
					16258, // Cyan Dragon claws for Youll Lose
					16259, // Green/Red/Yellow Dinh's bulwark for Huffjenkem
					16260, // Purple Armadyl godsword for Demian
					16263, // Black Elysian spirit shield with Golden Sigil for Mafia Don
					16267, // Black Elysian spirit shield with Purple sigil for Criboli
					16268, // Pink Elysian spirit shield with pink sigil for Chamo
					16269, // Red Elysian spirit shield with White sigil for New Username
					16270, // Purple Elysian spirit shield with pink sigil for Lazoh
					16271, // Blue/Red/White Armadyl godsword for Pannibal
					16272, // Rainbow Toxic staff of the dead for Meeko
					16273, // Wise old man's santa hat, Black santahat and White partyhat for Dan dices.
					16274, // Yellow Elysian spirit shield with black sigil for ThatKidCudi
					16275, // Black/White/Grey Ancestral top for ThatKidCudi
					16276, // Black/White/Grey Ancestral bottom for ThatKidCudi
					16277, // Purple Elysian spirit shield with Yellow sigil for Harry
					16278, // Black Elysian spirit shield with Pink sigil for No Honor Xvi
					16279, // White Elysian spirit shield with Red sigil for Bakura
					16280, // Dark golden Elysian spirit shield with pink sigil for 114 zeus 114
					16281, // White Dragon Claws for Bakura
					16282, // White Twisted Bow for Bakura
					16283, // Purple arcane spirit shield with red sigil for Beef
					16284, // Black Elysian spirit shield with cyan sigil for Peekaayy
					16285, // Cyan Abyssal tentacle for Youll Lose
					16286, // Black and white Mythical cape for Our story
					16287, // Black and cyan ancestral top  for Our Story
					16288, // Black and cyan ancestral bottom for Our Story
					16289, // Black and cyan ancestral hat for Our Story
					16290, // Cyan primordial boots for Our Story
					16291, // Blue/Cyan claws for Our story
					16292, // Rainbow bandos chestplate for killa4realz
					16293, // Rainbow bandos tassets for Killa4realz
					16294, // Dark Blue Elysian Spirit Shield with Red sigal for Killa4realz
					16295, // Red Elysian spirit shield with white sigil and yellow trim for Frederik
					16296, // Dark purple Elysian shield for Frank Cali
					16297, // Elder maul with green handle for Gene gotti
					16298, // Elysian spirit shield with red sigil, green trim & blue shield for 604
					16299, // Elysian spirit shield with purple sigil and white shield for Syntaxed
					16300, // Ancestral Top Purple / Green / Red for Lil  Uzi
					16301, // Ancestral Bottom Purple / Green / Red Lil  Uzi
					16302, // Bandos Tassets Purple / Black Lil  Uzi
					16303, // Abyssal Tentacle Purple Lil  Uzi
					16304, // Elder Maul Purple Pole / Yellow Hammer Lil  Uzi
					16305, // White Elysian spirit shield with purple sigil and purple trim for Tony
					16306, // White Elysian spirit shield with Blue sigil for Tankthee
			16307, // Pink Elysian with black sigil for Been Woke
					16308, // Dark purple Arcane spirit shield for Crip Walkin
					16309, // Bandos tassets for Red with red socks, red front flap, and the side/back to be dark red
					16310, // Black Berserker ring (i) for I Solo
					16311, // White and light blue Dragon claws for I Solo
					16312, // Dark Purple dragon claws for Crip Walkin
					16313, // Arcane spirit shield with white sigil and light blue shield for Trevino
					16315, // Armadyl godsword (or) with Black hilt for MrJustin
					16316, // Pink Armadyl godsword with white blade for Chamo
					16317, // Pink ancestral hat for Chamo
					16320, // Elysian spirit shield with blue sigil and lava coloured shield for Stallion
					16321, // Elysian spirit shield with black shield and orange sigil for A P P  L E
					16322, // Lime santa hat for Syntaxed(same lime as wespoonedyou elder maul)
					16323, // White & lime Abyssal tentacle for Syntaxed (same lime as wespoonedyou elder maul)
					16324, // Black and purple Dragon claws for 4hed
					16325, // White and purple Elder maul for 4hed
					16326, // White Kodai wand for 4hed
					16329, // Pink Elder maul for Tag
					16330, // Lime green dragon claws for Tag
					16331, // Lime green h'ween mask for Tag
					16333, // Elysian spirit shield, black shield, Cyan sigil, Cyan trim for Tom Bilotti
					16334, // Abyssal tentacle Cyan and pink for Tag
					16335, // Granite maul, white handle and lime top for Tag
					16336, // Berserker Ring i, lime orange and pink for Tag
					16337, // Armadyl Godsword, White Black and some lime for Tag
					16338, // H'ween mask, White mask with Red eyes Tag
					16339, // Elder maul Replace Black with red and orange with black for Thuggahhh
					16340, // Orange Dragon claws for Thuggahhh
					16341, // Abyssal tentacle red and black spikes for Thuggahhh
					16342, // Purple defender icon in Shield slot for Thuggahhh
					16343, // Purple H'ween for Thuggahhh
					16345, // Ancestral robe top for Tony
					16346, // Ancestral robe bottom for Tony
					16347, // Elysian spirit shield with pink shield and sigil and light blue trim for Calsta
					16348, // Ancestral robe top for Tom Bilotti
					16349, // Ancestral robe bottom for Tom Bilotti
					16351, // Black and white Bandos chestplate for Thuggahhh
					16352, // Black and white Bandos tassets for Thuggahhh
					16353, // Black and white primordials for Thuggahhh
					16354, // Abyssal tentacle, red with black spikes for King of Lumb
					16355, // Elysian spirit shield with light yellow shield, black sigil and trim for 11 P 1ill1 P
					16356, // Armadyl cross bow, white > black, yellow > cyan, black strings for Sam Gravano
					16357, // Armadyl chestplate, silver > black, yellow > black, grey > cyan for Sam Gravano
					16358, // Bandos tassets, grey > black, biege > black, dark biege to Cyan for Sam Gravano
					16359, // Attacker icon, black ring and cyan swords in gloves slot for Sam Gravano
					16360, // Attacker icon, all cyan, in shield slot for Youll Lose
					16361, // Elysian spirit shield, Cyan shield with pink sigil and pink trim for Foul
					16363, // Elysian spirit shield with Light purples shield, black sigil and white trim for Half Life
					16364, // Ancestral top, Blue > black, Gold > white, biege > light purple for Half Life
					16365, // Ancestral bottom, Blue > black, Gold > white, biege > light purple for Half Life
		16367, // Ancestral top, blue --> dark green for Dps
		16368, // Ancestral bottom, blue --> dark green for Dps
			16369, // Primordial boots, red --> black, white/grey --> dark orange for Tyr
		16370, // Elysian spirit, white shield, cyan sigil and red trim for Pouch
		16371, // Ancestral top, black and pink with cyan highlights to match Tanzanite helm for Dope E Grunt
		16372, // Ancestral bottom, black and pink with cyan highlights to match Tanzanite helm for Dope E Grunt
		16373, // Purple and white ancestral hat for Tony
		16374, // Orange elder maul for Tyr
		16375, // Elysian spirit, black shield, dark orange sigil and dark orange trim for Tyr
		16376, // Purple and white bandos tassets for Tony
		16377, // Purple and white bandos chestplate for Tony
		16378, // Purple and white dragon claws for Tony
			16379, // Elysian spirit shield, dark blue shield, with a Red sigil and a white trim for Rap
			16380, // Armadyl godsword being blue on top white in the middle and the bottom hilt area/handle being red for Rap
			16381, // Primordial boots, blue based with the little "Wings" on the side to be red instead of white for Rap
			16382, // Amulet of torture with a blue string and the Gem part of the amulet recolored to red for Rap
			16383, // Ghrazi rapier with blue blade, with a red handle for Rap
			16384, // Ghrazi Rapier purple handle where its grey black where its red and a white blade for Tony
			16385, // Ghrazi Rapier with Cyan blade and white guard for Pouch.
			16386, // Ghrazi Rapier with pink blade and cyan handle for Foul.
			16387, // Ghrazi Rapier, blade grey > black, handle grey > orange, handle red > black for Tyr
			16388, // Ghrazi Rapier with red blade for Red
			16389, // Ghrazi rapier with black handle and purple blade for Detroit
			16390, // Heavy ballista, black and white for Den Applez
			16391, // Amulet of torture with dark orange gem and black string for Tyr
			16392, // Defender icon Cyan in head slot for Foul.
			16393, // Justiciar helm with grey parts recoloured in black and yellow parts recoloured dark orange for Tyr
			16394, // Justiciar body with blue/grey parts recoloured in black and yellow parts recoloured dark orange for Tyr
			16395, // Justiciar legs with blue/grey parts recoloured in black and yellow parts recoloured dark orange for Tyr
			16396, // Armadyl godsword blood red for Zachery
			16397, // Elysian spirit shield with white shield and blue sigil for Kawaii desu
			16400, // Ghrazi rapier with green handle and pink blade for Smac it
		16401, // Armadyl godsword, dark orange blade and black hilt for Kourtney
			16402, // Elysian spirit shield, white shield, with a red sigil and a red trim for Got Downed
		16403, // Dragon claws, mainly purple with black for Half life
		16404, // Armadyl godsword, Purple hilt, blade half white and half black for Half life
		16405, // Bandos tassets, purple based with some black and white for Half life
		16406, // Bandos chestplate, purple based with some black and white for Half life
		16407, // Ghrazie rapier, purple handle and black blade for Half life
		16408, // Elysian spirit shield, green shield, with a green sigil and a black trim for Half life
		16409, // Armadyl godsword, Lava colour for hilt, right side of blade white, left side of blade golden for I Whale Hunt
		16410, // Elder maul, black for Missile
		16411, // Armadyl godsword, Bright red handle, White and light blue trim for Middle
		16412, // Abyssal tentacle, Red based, with white spikes for Middle
		16413, // Ghrazi rapier, Yellow blade and lime green handle for Btk
		16415, // Armadyl godsword, red hilt, black blade for Volbeat
		16416, // Armadyl crossbow, replace white with black and yellow with red for Volbeat
		16417, // Justiciar body, red and black for Volbeat
		16418, // Justiciar legs, red and black for Volbeat
		16419, // Elysian spirit shield, black shield, black trim, yellow sigil, for Skill3d
		16420, // Black and red partyhat for Volbeat
		16421, // Dragon claws, green and gold for Dameon
		16422, // Elder maul, white and purple for Dameon
		16423, // Elder maul, completely black for Wallah Bro
		16424, // Armadyl godsword, blue blade (like blue phat) and white hilt for Wallah Bro
		16425, // Dragon claws, blue (like blue phat) for Wallah Bro
		16426, // Armadyl godsword, pink blade and cyan hilt for Foul
		16427, // Armadyl crossbow, replace white pink and yellow with cyan for Foul
		16428, // Primordial boots, cyan and pink for Foul
		16430, // Amulet of torture, all pink for Foul
		16431, // Bandos tassets, pink and cyan for Foul
		16432, // Elder maul, black and white for Den Applez
		16433, // Dragon claws, black and white for Den Applez
		16434, // Santa hat, white replaced with gold for Thirst
		16435, // Armadyl godsword, red for Hac Coin2
	};

	private final static int[] IMMORTAL_DONATOR_ITEMS_PRE_EOC = {
			// Empty
	};

	public static int[] getImmortalDonatorItems() {
		return GameType.isOsrs() ? IMMORTAL_DONATOR_ITEMS_OSRS : IMMORTAL_DONATOR_ITEMS_PRE_EOC;
	}

	/**
	 * Armadyl godsword itemid, animation id
	 */
	public final static int[][] ARMADYL_GODSWORDS_OSRS =
			{
					{16042, 11032}, // Armadyl godsword (g) for C0nz0le
					{16047, 11033}, // Armadyl godsword (p) for Trenbolone e
					{16060, 11034}, // Armadyl godsword (Bites)
					{16063, 11035}, // Armadyl godsword (Russman887)
					{16091, 11036}, // Armadyl godsword (lava) for wespoonedyou
					{11802, 7644}, // Armadyl godsword
					{20368, 7645}, // Armadyl godsword (or)
					{16112, 11037}, // Armadyl godsword (blood) for Thuggahhh
					{16163, 11034}, // Armadyl godsword black for Slice u ko
					{16183, 11048}, // Rainbow Armadyl godsword for Pouch
					{16187, 11049}, // Light blue Armadyl godsword for Bluezia
					{16206, 11050}, // Green Armadyl godsword for Dirty Specz
					{16207, 11051}, // Pink Armadyl godsword for Muppets
					{16260, 11053}, // Purple Armadyl godsword for Demian
					{16271, 11054}, // Blue/Red/White Armadyl godsword for Pannibal
					{16315, 11056}, // Armadyl godsword (or) with Black hilt for MrJustin
					{16316, 11057}, // Pink Armadyl godsword with white blade for Chamo
					{16337, 11058}, // Armadyl Godsword, White Black and some lime for Tag
					{16380, 11059}, // Armadyl godsword being blue on top white in the middle and the bottom hilt area/handle being red for Rap
					{16396, 11060}, // Armadyl godsword blood red for Zachery
		{16401, 11061}, // Armadyl godsword blood red for Zachery
		{16404, 11062}, // Armadyl godsword, Purple hilt, blade half white and half black for Half life
		{16409, 11063}, // Armadyl godsword, Lava colour for hilt, right side of blade white, left side of blade golden for I Whale Hunt
		{16411, 11064}, // Armadyl godsword, Bright red handle, White and light blue trim for Middle
		{16415, 11065}, // Armadyl godsword, red hilt, black blade for Volbeat
		{16424, 11066}, // Armadyl godsword, blue blade (like blue phat) and white hilt for Wallah Bro
		{16426, 11067}, // Armadyl godsword, pink blade and cyan hilt for Foul
		{16435, 11068}, // Armadyl godsword, red for Hac Coin2
			};

	public final static int[] REPAIRABLE_ITEMS_OSRS =
			{
					6570, // Fire cape
					13329, // Fire max cape
					21285, // Infernal max cape
					21295, // Infernal cape
					14011, // Comp cape
					14013, // Veteran cape
					8844, // Bronze defender
					8845, // Iron defender
					8846, // Steel defender
					8847, // Black defender
					8848, // Mithril defender
					8849, // Adamant defender
					8850, // Rune defender
					12954, // Dragon defender
					8839, // Void robe top
					8840, // Void knight robe
					13072, // Elite void top
					13073, // Elite void robe
					8842, // Void gloves
					11663, // Void mage helm
					11664, // Void ranger helm
					11665, // Void melee helm
					10548, // Fighter hat
					10550, // Ranger hat
					10547, // Healer hat
					10549, // Runner hat
					10551, // Fighter torso
					10555, // Penance skirt
					12637, // Saradomin halo
					12638, // Zamorak halo
					12639, // Guthix halo
			};

	private static final int[] SLAYER_HELMS_OSRS =
			{11864, 11865, 19639, 19641, 19643, 19645, 19647, 19649, 21264, 21266,

			};

	private static final int[] SLAYER_HELMS_PRE_EOC = {
			// Empty
	};

	public static int[] getSlayerHelms() {
		return GameType.isOsrs() ? SLAYER_HELMS_OSRS : SLAYER_HELMS_PRE_EOC;
	}

	public static final int[][] GRACEFUL_PIECES_OSRS =
			{
		{11850, ServerConstants.HEAD_SLOT}, // Graceful hood
		{11852, ServerConstants.CAPE_SLOT}, // Graceful cape
		{11854, ServerConstants.BODY_SLOT}, // Graceful top
		{11856, ServerConstants.LEG_SLOT}, // Graceful legs
		{11858, ServerConstants.HAND_SLOT}, // Graceful gloves
		{11860, ServerConstants.FEET_SLOT}, // Graceful boots
		{13579, ServerConstants.HEAD_SLOT}, // Graceful hood
		{13581, ServerConstants.CAPE_SLOT}, // Graceful cape
		{13583, ServerConstants.BODY_SLOT}, // Graceful top
		{13585, ServerConstants.LEG_SLOT}, // Graceful legs
		{13587, ServerConstants.HAND_SLOT}, // Graceful gloves
		{13589, ServerConstants.FEET_SLOT}, // Graceful boots
		{13591, ServerConstants.HEAD_SLOT}, // Graceful hood
		{13593, ServerConstants.CAPE_SLOT}, // Graceful cape
		{13595, ServerConstants.BODY_SLOT}, // Graceful top
		{13597, ServerConstants.LEG_SLOT}, // Graceful legs
		{13599, ServerConstants.HAND_SLOT}, // Graceful gloves
		{13601, ServerConstants.FEET_SLOT}, // Graceful boots
		{13603, ServerConstants.HEAD_SLOT}, // Graceful hood
		{13605, ServerConstants.CAPE_SLOT}, // Graceful cape
		{13607, ServerConstants.BODY_SLOT}, // Graceful top
		{13609, ServerConstants.LEG_SLOT}, // Graceful legs
		{13611, ServerConstants.HAND_SLOT}, // Graceful gloves
		{13613, ServerConstants.FEET_SLOT}, // Graceful boots
		{13615, ServerConstants.HEAD_SLOT}, // Graceful hood
		{13617, ServerConstants.CAPE_SLOT}, // Graceful cape
		{13619, ServerConstants.BODY_SLOT}, // Graceful top
		{13621, ServerConstants.LEG_SLOT}, // Graceful legs
		{13623, ServerConstants.HAND_SLOT}, // Graceful gloves
		{13625, ServerConstants.FEET_SLOT}, // Graceful boots
		{13627, ServerConstants.HEAD_SLOT}, // Graceful hood
		{13629, ServerConstants.CAPE_SLOT}, // Graceful cape
		{13631, ServerConstants.BODY_SLOT}, // Graceful top
		{13633, ServerConstants.LEG_SLOT}, // Graceful legs
		{13635, ServerConstants.HAND_SLOT}, // Graceful gloves
		{13637, ServerConstants.FEET_SLOT}, // Graceful boots
		{13667, ServerConstants.HEAD_SLOT}, // Graceful hood
		{13669, ServerConstants.CAPE_SLOT}, // Graceful cape
		{13671, ServerConstants.BODY_SLOT}, // Graceful top
		{13673, ServerConstants.LEG_SLOT}, // Graceful legs
		{13675, ServerConstants.HAND_SLOT}, // Graceful gloves
		{13677, ServerConstants.FEET_SLOT}, // Graceful boots
		{21061, ServerConstants.HEAD_SLOT}, // Graceful hood
		{21064, ServerConstants.CAPE_SLOT}, // Graceful cape
		{21067, ServerConstants.BODY_SLOT}, // Graceful top
		{21070, ServerConstants.LEG_SLOT}, // Graceful legs
		{21073, ServerConstants.HAND_SLOT}, // Graceful gloves
		{21076, ServerConstants.FEET_SLOT}, // Graceful boots
			};

	/**
	 * Woodcutting.
	 */
	public static final int[][] LUMBERJACK_PIECES =
	{
		{10933, ServerConstants.FEET_SLOT},
		{10939, ServerConstants.BODY_SLOT},
		{10940, ServerConstants.LEG_SLOT},
		{10941, ServerConstants.HEAD_SLOT},};

	/**
	 * Fishing.
	 */
	public static final int[][] ANGLER_PIECES =
	{
		{13258, ServerConstants.HEAD_SLOT},
		{13259, ServerConstants.BODY_SLOT},
		{13260, ServerConstants.LEG_SLOT},
		{13261, ServerConstants.FEET_SLOT},};

	/**
	 * Mining.
	 */
	public static final int[][] PROSPECTOR_PIECES =
	{
		{12013, ServerConstants.HEAD_SLOT},
		{12014, ServerConstants.BODY_SLOT},
		{12015, ServerConstants.LEG_SLOT},
		{12016, ServerConstants.FEET_SLOT},};

	public final static double SKILLING_SETS_EXPERIENCE_BOOST_PER_PIECE = 1.02;

	/**
	 * Unused.
	 */
	public static final int[][] PYROMANCER_PIECES =
	{
		{20704, ServerConstants.BODY_SLOT},
		{20706, ServerConstants.LEG_SLOT},
		{20708, ServerConstants.HEAD_SLOT},
		{20710, ServerConstants.FEET_SLOT},
		{20712, ServerConstants.HAND_SLOT},};


	/**
	 * Items sent to shop on death and drop blood money for killer.
	 */
	public static final int[] ITEMS_DROP_BLOOD_MONEY_OSRS_PVP = {
			12791, // Rune pouch.
			};
	//@formatter:on

	public final static String[] flaggedTradeWords =
			{"buy", "sell", "[b]", "[s]", "swap", "sale"};

	/**
	 * Amount of blood money received when buying the blood money stack in the Donator shop.
	 */
	public static int getDonatorShopBloodMoneyRewardAmount() {
		return GameType.isOsrsEco() ? 5000000 : 10000;
	}

	/**
	 * Amount of blood money received when buying the blood money stack in the Vote shop.
	 */
	public static int getVoteShopBloodMoneyRewardAmount() {
		return VoteManager.voteReward;
	}


	public final static int VOTE_SITES_AMOUNT = 4;

	public final static int VOTE_BASIC_TICKETS_TOTAL = 25;

	/**
	 * Email me if players online dropped more than x amount in 1 minute. Used for suspected Ddos attacks.
	 */
	public final static int PLAYER_DROP_ALERT = 20;

	public final static int NOT_DUELING = 0;

	public final static int IN_DUEL_INTERFACE = 1;

	public final static int ON_FIRST_SCREEN_ACCEPTED = 2;

	public final static int ON_SECOND_SCREEN = 3;

	public final static int ON_SECOND_SCREEN_ACCEPTED = 4;

	public final static int DUEL_STARTED = 5;

	public final static int HIGHSCORES_PLAYERS_AMOUNT = 30;

	public final static int DUELING = 5;

	public final static int[] animationCancel =
	{2763, 2756, 2761, 2764};

	/**
	 * Announce to all players.
	 */
	public static final String DARK_RED_COL = "<img=13><col=800000>";

	/**
	 * Announce rare drops.
	 */
	public static final String GREEN_COL = "<img=11><col=005f00>";

	/**
	 * Green colour.
	 */
	public static final String GREEN_COL_PLAIN = "<col=005f00>";

	/**
	 * Gold colour.
	 */
	public static final String GOLD_COL = "<col=a36718>";

	/**
	 * Used for World events.
	 */
	public static final String DARK_BLUE = "<img=12><col=d51212>";

	/**
	 * Alert player not same ip, preset missing from bank.
	 */
	public static final String RED_COL = "<col=ef1020>";

	/**
	 * Player personal alerts, eligible to vote, password changed.
	 */
	public static final String BLUE_COL = "<col=3f3fff>";

	public static final String BLACK_COL = "<col=000000>";

	/**
	 * Special attack tracker.
	 */
	public static final String PALE_DARK_BLUE_COL = "<col=186098>";

	/**
	 * Used for anti-fire potion, teleblock.
	 */
	public static final String PURPLE_COL = "<col=804080>";

	public static final int NORMAL_HITSPLAT_COLOUR = 0;

	public static final int CRITICAL_HITSPLAT_COLOUR = 1;

	public static final int POISON_HITSPLAT_COLOUR = 2;

	public static final int YELLOW_HITSPLAT_COLOUR = 3;

	public static final int PURPLE_HITSPLAT_COLOUR = 4;

	public static final int DARK_RED_HITSPLAT_COLOUR = 5;

	public static final int VENOM_HITSPLAT_COLOUR = 6;


	public static final int NO_ICON = -1;

	public static final int MELEE_ICON = 0;

	public static final int RANGED_ICON = 1;

	public static final int MAGIC_ICON = 2;

	public static final int DRAGONFIRE_ATTACK = 3;

	public static final int WYVERN_BREATH = 4;

	public static final int ACCURATE = 0;

	public static final int AGGRESSIVE = 1;

	public static final int DEFENSIVE = 2;

	public static final int CONTROLLED = 3;


	public static final int RAPID = 1;

	public static final int LONG_RANGED = 3;

	private static final String CHARACTER_LOCATION_OSRS = "backup/characters/players/";

	public static String getCharacterLocation() {
		return GameType.isPreEoc() ? CHARACTER_LOCATION_PRE_EOC : CHARACTER_LOCATION_OSRS;
	}

	private static final String CHARACTER_LOCATION_WITHOUT_LAST_SLASH_OSRS = "backup/characters/players";

	public static String getCharacterLocationWithoutLastSlash() {
		return GameType.isPreEoc() ? CHARACTER_LOCATION_WITHOUT_LAST_SLASH_PRE_EOC : CHARACTER_LOCATION_WITHOUT_LAST_SLASH_OSRS;
	}

	private static final String CHARACTER_LOCATION_PRE_EOC = "backup/characters/players_pre-eoc/";

	private static final String CHARACTER_LOCATION_WITHOUT_LAST_SLASH_PRE_EOC = "backup/characters/players_pre-eoc";


	public static final String QUEST_DATA_DIR = "data/plugins/quests/";

	public static final String SCRIPT_DIRECTORY = "data/plugins/";

	public static final String SCRIPT_FILE_EXTENSION = ".py";

	public static final boolean PRINT_PLUGIN_DIRECTORIES = false;

	public static boolean DOUBLE_EXP = false;

	/**
	 * Blacklisted words cannot be used as a username or player title.
	 */
	public static final String FLAGGED_NAMES[] =
			{
					"mod",
					"admin",
					"moderator",
					"nigg",
					"administrator",
					"owner",
					"m0d",
					"adm1n",
					"0wner",
					"retard",
					"n1gger",
					"n1gg3r",
					"n1gga",
					"cock",
					"faggot",
					"fag",
					"anus",
					"arse",
					"fuck",
					"bastard",
					"bitch",
					"cunt",
					"chode",
					"damn",
					"dick",
					"faggit",
					"gay",
					"homo",
					"jizz",
					"lesbian",
					"pussy",
					"penis",
					"queef",
					"twat",
					"titty",
					"whore",
					"b1tch",
					"jew"
			};

	/**
	 * Offensive words.
	 */
	public static final String offensiveLanguage[] =
			{
					"sex",
					"idiot",
					"fuck",
					"kanker",
					"cancer",
					"faggot",
					"fk u",
					"bitch",
					"kys",
					"k y s",
					"k ys",
					"ky s",
					"k.y.s",
					"kill urself",
					"kill ys",
					"kill yourself",
					"cunt",
					"asshole",
					"nigger",
					"gay",
					"lesbian",
					"bastard",
					"blowjob",
					"blow job",
					"bukakke",
					"bukake",
					"cum",
					"homosexual",
					"vagina",
					"penis",
					"dick",
					"shit",
					"tits",
					"cock",
					"nigga",
					"retard",
					"stfu",
					"fag",
					"fgt",
					"cnt"
			};

	/**
	 * Maximum amount of invalid password log-in attempts allowed.
	 */
	public static int getMaximumInvalidAttempts() {
		return ServerConfiguration.DEBUG_MODE ? 100 : 2;
	}

	/**
	 * The delay in milliseconds between continuous connections from the same IP.
	 */
	public static final int CONNECTION_DELAY = 1000;

	/**
	 * The amount of simultaneous connections from the same IP.
	 */
	public static final int IPS_ALLOWED = 2;

	/**
	 * Equivelant to 40 seconds.
	 * <p>
	 * This is used to force disconnect the player if in combat.
	 */
	public static final int TIMEOUT = 67;

	/**
	 * The maximum item identity.
	 */
	public static final int MAX_ITEM_ID = 25000;

	/**
	 * The maximum amount of a single item.
	 */
	public static final int MAX_ITEM_AMOUNT = Integer.MAX_VALUE;

	/**
	 * Remove player from Duel arena to this X coordinate.
	 */
	public static final int DUEL_ARENA_X = 3362;

	/**
	 * Remove player from Duel arena to this Y coordinate.
	 */
	public static final int DUEL_ARENA_Y = 3263;

	/**
	 * The random distance from the Duel arena X and Y coordinate spawn.
	 */
	public static final int RANDOM_DISTANCE = 5;

	//@formatter:off

	/**
	 * Undead NPCs identities.
	 */
	public static final int[] UNDEAD_NPCS_OSRS = {90, 91, 92, 93, 94, 103, 104,
			70, 73, 74, 75, 76, 77, // Skeletons
			11144, 11145, 11146, 11124, 11125, // new revs
			6611, 8059, 414, 448, // Vet'ion, vorkath, banshee, crawling hand
			6815, 26, // ghost, zombie
			11007, 11009, 11010, 8062,// old revs
			1337 // max hit dummy
	};
	//@formatter:on

	/**
	 * Lists NPCs that aren't affected by venom
	 */
	public static final int[] NON_VENOMABLE_NPCS =
			{
					6611, // Vet'ion
					2054, // Chaos elemental
					2266, // Dagannoth Prime
					2265, // Dagannoth Supreme
					2267, // Dagannoth Rex
					496, // Kraken
		1337, // Max hit dummy
			};

	/**
	 * The slot identity of the head in the equipment tab.
	 */
	public static final int HEAD_SLOT = 0;

	/**
	 * The slot identity of the cape in the equipment tab.
	 */
	public static final int CAPE_SLOT = 1;

	/**
	 * The slot identity of the amulet in the equipment tab.
	 */
	public static final int AMULET_SLOT = 2;

	/**
	 * The slot identity of the weapon in the equipment tab.
	 */
	public static final int WEAPON_SLOT = 3;

	/**
	 * The slot identity of the torso in the equipment tab.
	 */
	public static final int BODY_SLOT = 4;

	/**
	 * The slot identity of the shield in the equipment tab.
	 */
	public static final int SHIELD_SLOT = 5;

	/**
	 * The slot identity of the leg in the equipment tab.
	 */
	public static final int LEG_SLOT = 7;

	/**
	 * The slot identity of the hand in the equipment tab.
	 */
	public static final int HAND_SLOT = 9;

	/**
	 * The slot identity of the feet in the equipment tab.
	 */
	public static final int FEET_SLOT = 10;

	/**
	 * The slot identity of the ring in the equipment tab.
	 */
	public static final int RING_SLOT = 12;

	/**
	 * The slot identity of the arrow in the equipment tab.
	 */
	public static final int ARROW_SLOT = 13;

	public final static int SARADOMIN_BLESSED_SWORD_CHARGES = 6000;

	public static final int ATTACK = 0;

	public static final int DEFENCE = 1;

	public static final int STRENGTH = 2;

	public static final int HITPOINTS = 3;

	public static final int RANGED = 4;

	public static final int PRAYER = 5;

	public static final int MAGIC = 6;

	public static final int COOKING = 7;

	public static final int WOODCUTTING = 8;

	public static final int FLETCHING = 9;

	public static final int FISHING = 10;

	public static final int FIREMAKING = 11;

	public static final int CRAFTING = 12;

	public static final int SMITHING = 13;

	public static final int MINING = 14;

	public static final int HERBLORE = 15;

	public static final int AGILITY = 16;

	public static final int THIEVING = 17;

	public static final int SLAYER = 18;

	public static final int FARMING = 19;

	public static final int RUNECRAFTING = 20;

	public static final int HUNTER = 21;

	public static final int SUMMONING = 22;

	public static final int getTotalSkillsAmount() {
		return GameType.isPreEoc() ? 23 : 22;
	}

	public static final int THICK_SKIN = 0;

	public static final int BURST_OF_STRENGTH = 1;

	public static final int CLARITY_OF_THOUGHT = 2;

	public static final int SHARP_EYE = 3;

	public static final int MYSTIC_WILL = 4;

	public static final int ROCK_SKIN = 5;

	public static final int SUPERHUMAN_STRENGTH = 6;

	public static final int IMPROVED_REFLEXES = 7;

	public static final int RAPID_RESTORE = 8;

	public static final int RAPID_HEAL = 9;

	public static final int PROTECT_ITEM = 10;

	public static final int HAWK_EYE = 11;

	public static final int MYSTIC_LORE = 12;

	public static final int STEEL_SKIN = 13;

	public static final int ULTIMATE_STRENGTH = 14;

	public static final int INCREDIBLE_REFLEXES = 15;

	public static final int PROTECT_FROM_MAGIC = 16;

	public static final int PROTECT_FROM_RANGED = 17;

	public static final int PROTECT_FROM_MELEE = 18;

	public static final int EAGLE_EYE = 19;

	public static final int MYSTIC_MIGHT = 20;

	public static final int RETRIBUTION = 21;

	public static final int REDEMPTION = 22;

	public static final int SMITE = 23;

	public static final int CHIVALRY = 24;

	public static final int PIETY = 25;

	public static final int PRESERVE = 26;

	public static final int RIGOUR = 27;

	public static final int AUGURY = 28;

	public final static String[] SKILL_NAME =
			{
					"Attack",
					"Defence",
					"Strength",
					"Hitpoints",
					"Ranged",
					"Prayer",
					"Magic",
					"Cooking",
					"Woodcutting",
					"Fletching",
					"Fishing",
					"Firemaking",
					"Crafting",
					"Smithing",
					"Mining",
					"Herblore",
					"Agility",
					"Thieving",
					"Slayer",
					"Farming",
					"Runecrafting",
					"Hunter",
					"Summoning",

			};

	/**
	 * The name of the equipment bonuses.
	 */
	public final static String[] EQUIPMENT_BONUS =
			{"Stab", "Slash", "Crush", "Magic", "Ranged", "Stab", "Slash", "Crush", "Magic", "Range", "Strength", "Prayer"};

	public final static int STAB_ATTACK_BONUS = 0;

	public final static int SLASH_ATTACK_BONUS = 1;

	public final static int CRUSH_ATTACK_BONUS = 2;

	public final static int MAGIC_ATTACK_BONUS = 3;

	public final static int RANGED_ATTACK_BONUS = 4;

	public final static int STAB_DEFENCE_BONUS = 5;

	public final static int SLASH_DEFENCE_BONUS = 6;

	public final static int CRUSH_DEFENCE_BONUS = 7;

	public final static int MAGIC_DEFENCE_BONUS = 8;

	public final static int RANGED_DEFENCE_BONUS = 9;

	public final static int STRENGTH_BONUS = 10;

	public final static int PRAYER_BONUS = 11;

	public static final int BUFFER_SIZE = 10000;

	//@formatter:off
	public static final int PACKET_SIZES_OSRS[] =
			{
					0, 0, 0, 1, -1, 0, 0, 0, 0, 0, // 0
					0, 0, 0, 0, 8, 0, 6, 2, 2, 0, // 10
					0, 2, 0, 6, 0, 12, 0, 0, 0, 0, // 20
					0, 0, 0, 0, 0, 8, 4, 0, 0, 2, // 30
					2, 6, 0, 6, 0, -1, 0, 0, 0, 0, // 40
					0, 0, 0, 12, 0, 0, 0, 8, 8, 12, // 50
					8, 8, 0, 0, 0, 0, 0, 0, 0, 0, // 60
					6, 0, 2, 2, 8, 6, 0, -1, 0, 6, // 70
					0, 0, 0, 0, 0, 1, 4, 6, 0, 0, // 80
					0, 0, 0, 0, 0, 3, 0, 0, -1, 0, // 90
					0, 13, 0, -1, 0, 0, 0, 0, 0, 0, // 100
					0, 0, 0, 0, 0, 0, 0, 6, 0, 0, // 110
					1, 0, 6, 0, 0, 0, -1, 0, 2, 6, // 120
					0, 4, 6, 8, 0, 6, 0, 0, 0, 2, // 130
					0, 0, 0, 0, 0, 6, 0, 0, 0, 0, // 140
					0, 0, 1, 2, 0, 2, 6, 0, 0, 0, // 150
					0, 0, 0, 0, -1, -1, 0, 0, 0, 0, // 160
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, // 170
					0, 8, 0, 3, 0, 2, 0, 0, 8, 1, // 180
					0, 0, 12, 0, 0, 0, 0, 0, 0, 0, // 190
					2, 0, 0, 0, 0, 0, 0, 0, 4, 0, // 200
					4, 0, 0, 9, 7, 8, 0, 0, 10, 0, // 210
					8, 0, 0, 0, 0, 0, -1, 0, 6, 0, // 220
					1, 0, 0, 0, 6, 0, 10, 8, 1, 0, // 230
					0, 4, 0, 0, 0, 0, -1, 0, -1, 4, // 240
					0, 0, 6, 6, 0, 0, 0 // 250
			};
	//@formatter:on

	//@formatter:off
	public static final int PACKET_SIZES_PRE_EOC[] =
			{
					0, 0, 0, 1, -1, 0, 0, 0, 0, 0, // 0
					0, 0, 0, 0, 8, 0, 6, 2, 2, 0, // 10
					0, 2, 0, 6, 0, 12, 0, 0, 0, 0, // 20
					0, 0, 0, 0, 0, 8, 4, 0, 0, 2, // 30
					2, 6, 0, 6, 0, -1, 0, 0, 0, 0, // 40
					0, 0, 0, 12, 0, 0, 0, 8, 8, 12, // 50
					8, 8, 0, 0, 0, 0, 0, 0, 0, 0, // 60
					6, 0, 2, 2, 8, 6, 0, -1, 0, 6, // 70
					0, 0, 0, 0, 0, 1, 4, 6, 0, 0, // 80
					0, 0, 0, 0, 0, 3, 0, 0, -1, 0, // 90
					0, 13 * 4, 0, -1, 0, 0, 0, 0, 0, 0, // 100
					0, 0, 0, 0, 0, 0, 0, 6, 0, 0, // 110
					1, 0, 6, 0, 0, 0, -1, 0, 2, 6, // 120
					0, 4, 6, 8, 0, 6, 0, 0, 0, 2, // 130
					0, 0, 0, 0, 0, 6, 0, 0, 0, 0, // 140
					0, 0, 1, 2, 0, 2, 6, 0, 0, 0, // 150
					0, 0, 0, 0, -1, -1, 0, 0, 0, 0, // 160
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, // 170
					0, 8, 0, 3, 0, 2, 0, 0, 8, 1, // 180
					0, 0, 12, 0, 0, 0, 0, 0, 0, 0, // 190
					2, 0, 0, 0, 0, 0, 0, 0, 4, 0, // 200
					4, 0, 0, 9, 7, 8, 0, 0, 10, 0, // 210
					8, 0, 0, 0, 0, 0, -1, 0, 6, 0, // 220
					1, 0, 0, 0, 6, 0, 10, 8, 1, 0, // 230
					0, 4, 0, 0, 0, 0, -1, 0, -1, 4, // 240
					0, 0, 6, 6, 0, 0, 0 // 250
			};
	//@formatter:on

	public final static int[] PRAYER_LEVEL_REQUIRED =
			{1, 4, 7, 8, 9, 10, 13, 16, 19, 22, 25, 26, 27, 28, 31, 34, 37, 40, 43, 44, 45, 46, 49, 52, 60, 70, 55, 74, 77};

	public final static int[] PRAYER_GLOW =
			{83, 84, 85, 601, 602, 86, 87, 88, 89, 90, 91, 603, 604, 92, 93, 94, 95, 96, 97, 605, 606, 98, 99, 100, 607, 608, 609, 610, 611};

	public final static String[] PRAYER_NAME =
			{
					"Thick Skin",
					"Burst of Strength",
					"Clarity of Thought",
					"Sharp Eye",
					"Mystic Will",
					"Rock Skin",
					"Superhuman Strength",
					"Improved Reflexes",
					"Rapid Restore",
					"Rapid Heal",
					"Protect Item",
					"Hawk Eye",
					"Mystic Lore",
					"Steel Skin",
					"Ultimate Strength",
					"Incredible Reflexes",
					"Protect from Magic",
					"Protect from Missiles",
					"Protect from Melee",
					"Eagle Eye",
					"Mystic Might",
					"Retribution",
					"Redemption",
					"Smite",
					"Chivalry",
					"Piety",
					"Preserve",
					"Rigour",
					"Augury"
			};

	public final static int[] PRAYER_HEAD_ICONS =
			{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 2, 1, 0, -1, -1, 3, 5, 4, -1, -1, -1, -1, -1};

	public final static int[] DUEL_RULE_FRAME_ID =
			{1, 2, 16, 32, 64, 128, 256, 512, 1024, 4096, 8192, 16384, 32768, 65536, 131072, 262144, 524288, 2097152, 8388608, 16777216, 67108864, 134217728};

	/**
	 * Maximum amount of players online simultaneously.
	 */
	public final static int MAXIMUM_PLAYERS = ServerConfiguration.STABILITY_TEST ? BotManager.BOTS_AMOUNT + 5 : 550;

	public final static int[] interfaceFramesIgnoreRepeat =
			{
					25359, // Profile interface biography text
					25360, // Profile interface biography text
					25361, // Profile interface biography text
					25362, // Profile interface biography text
					25373, // Profile interface biography text
					// Bank tabs clicked on
					27001,
					27002,
					27000,
					22033,
					22034,
					33063, // Donator interface, main tab, the account offer timer.
			};

	public static final int BANK_SIZE = 352;

	public final static int YELLOW_HEX = 0xF6FF00;

	public final static int ORANGE_HEX = 0xffb000;

	public final static int GREEN_HEX = 0x09FF00;

	public final static int RED_HEX = 0xFF0000;

	public final static String[] SPECIAL_ATTACK_SAVE_NAMES =
			{
					"Bandos godsword",
					"Saradomin godsword",
					"Dragon warhammer",
					"Armadyl godsword",
					"Zamorak godsword",
					"Dragon longsword",
					"",
					"Dragon halberd",
					"",
					"Dragon mace",
					"Barrelchest anchor",
					"Dragon dagger",
					"",
					"Saradomin sword",
					"",
					"",
					"",
					"",
					"",
					"Vesta's spear",
					"Vesta's longsword",
					"Hand cannon",
					"",
					"Morrigan's throwing axe",
					"Morrigan's javelin",
					"Magic shortbow",
					"",
					"Dark bow",
					"",
					"Dragon bolts (e)",
					"Dharok's axe",
					"Vengeance",
					"Heavy ballista",
					"Abyssal dagger (p++)",
					""
			};

	public final static int MILLISECONDS_HOUR = 3600000;

	public final static int MILLISECONDS_MINUTE = 60000;

	public final static String TEXT_SEPERATOR = "#=#";

	public final static String UUID_SEPERATOR = "&!#!";

	public static final String[] RANK_NAMES =
			{"", "Moderator", "Administrator"};

	public final static int ADMINISTRATOR = 2;

	public final static int MODERATOR = 1;

	public final static int PRIVATE_ON = 0;

	public final static int PRIVATE_FRIENDS = 1;

	public final static int PRIVATE_OFF = 2;

	public final static int OVERLAY_TIMER_SPRITE_BIND = 738;

	public final static int OVERLAY_TIMER_SPRITE_ICE_RUSH = 735;

	public final static int OVERLAY_TIMER_SPRITE_SNARE = 737;

	public final static int OVERLAY_TIMER_SPRITE_ICE_BURST = 734;

	public final static int OVERLAY_TIMER_SPRITE_ENTANGLE = 736;

	public final static int OVERLAY_TIMER_SPRITE_ICE_BLITZ = 733;

	public final static int OVERLAY_TIMER_SPRITE_ICE_BARRAGE = 711;

	public final static int OVERLAY_TIMER_SPRITE_STAMINA = 942;

	public final static int OVERLAY_TIMER_SPRITE_VENGEANCE = 694;

	public final static int OVERLAY_TIMER_SPRITE_ANTI_VENOM = 946;

	public final static int OVERLAY_TIMER_SPRITE_ANTI_FIRE = 943;

	public final static int OVERLAY_TIMER_SPRITE_TELEBLOCK = 709;

	public final static int OVERLAY_TIMER_SPRITE_DRAGON_FIRE_SHIELD_SPECIAL = 945;

	public final static int OVERLAY_TIMER_SPRITE_CHARGE = 947;

	public final static int OVERLAY_TIMER_SPRITE_IMBUED_HEART = 1222;

	public static int getMaxCapeCost() {
		return GameType.isOsrsEco() ? 15000000 : 20000;
	}

	public static int getInfernalCapeCost() {
		return GameType.isOsrsEco() ? 37500000 : 50000;
	}

	public static int getImbuedCapeCost() {
		return GameType.isOsrsEco() ? 37500000 : 50000;
	}

	public static int getResetKdrCost() {
		return GameType.isOsrsEco() ? 185000000 : 100000;
	}

	public static int getDropItemValueInCombatFlag() {
		return GameType.isOsrsPvp() ? 100 : 75000;
	}

	public static int getStackableItemGroundIgnoreValue() {
		return GameType.isOsrsPvp() ? 25 : 19000;
	}

	public static int getRiskingWealthCannotPickUpFoodRisk() {
		return GameType.isOsrsPvp() ? 5000 : 3750000;
	}

	/**
	 * Eco version of data folder location.
	 */
	public static String getEcoDataLocation() {
		return getOsrsDataLocation() + "eco/";
	}

	/**
	 * Pvp version of data folder location.
	 */
	public static String getPvpDataLocation() {
		return getOsrsDataLocation() + "pvp/";
	}

	/**
	 * Pre-eoc version of data folder location.
	 */
	public static String getPreEocDataLocation() {
		return "data/pre-eoc/";
	}

	/**
	 * Global version of data folder location.
	 */
	public static String getOsrsGlobalDataLocation() {
		if (GameType.isPreEoc()) {
			return getPreEocDataLocation();
		}
		return "data/osrs/global/";
	}

	/**
	 * data/osrs/
	 */
	public static String getOsrsDataLocation() {
		return "data/osrs/";
	}

	//check getDataLocation and getOsrsGlobalDataLocation

	/**
	 * Get data folder location depending on weather server is on pvp or eco mode.
	 * <p>
	 * Such as data/osrs/pvp/ or data/osrs/eco/ or data/pre-eoc/
	 */
	public static String getDataLocation() {
		if (GameType.isPreEoc()) {
			return getPreEocDataLocation();
		}
		if (GameType.isOsrsPvp()) {
			return getOsrsDataLocation() + "pvp/";
		} else {
			return getOsrsDataLocation() + "eco/";
		}
	}

	/**
	 * Location of dedicated_server_config folder depending on the server mode.
	 */
	public static String getDedicatedServerConfigLocation() {
		return "dedicated_server_config/";
	}

	/**
	 * @return The main currency item id for the server.
	 */
	public static int getMainCurrencyId() {
		return GameType.isOsrsEco() ? 995 : 13307;
	}

	public static String getMainCurrencyName() {
		return ItemAssistant.getItemName(getMainCurrencyId());
	}

	/**
	 * Get the item price. Blood money price if Pvp and coin price if Eco.
	 */
	public static int getItemValue(int itemId) {
		return GameType.isOsrsEco() ? ShopAssistant.getItemCoinPrice(itemId) : BloodMoneyPrice.getBloodMoneyPrice(itemId);
	}

	/**
	 * @return The server name.
	 */
	public static String getServerName() {
		return GameType.isOsrsEco() ? "Runefate" : "Dawntained";
	}

	/**
	 * Return the number in a formatted manor, for PVP it will return a formatted comma version and for ECO it will return a very short runescape way.
	 */
	public static String getFormattedNumberString(long number) {
		return GameType.isOsrsEco() ? Misc.formatRunescapeStyle(number) : Misc.formatNumber(number);
	}

	/**
	 * The amount of risk required to be under wilderness attack protection.
	 */
	public static int getRiskRequiredForWildProtection() {
		return GameType.isOsrsPvp() ? 10000 : 7_500_000;
	}

	public static int getRiskRequiredToAttackAnyPlayer() {
		return GameType.isOsrsPvp() ? 20000 : 750_000;
	}

	public static int getMinimumWildernessLevel() {
		return GameType.isOsrsEco() ? 7 : 1;
	}

	public static int getBotMaximumWealthItemDrop() {
		return GameType.isOsrsEco() ? 50_000 : 0;
	}

	public static int getItemsLostReclaimCost() {
		return GameType.isOsrsEco() ? 1_500_000 : 2_000;
	}
}
