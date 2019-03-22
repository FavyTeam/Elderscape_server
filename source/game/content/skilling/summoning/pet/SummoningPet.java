package game.content.skilling.summoning.pet;

import core.ServerConstants;
import game.content.skilling.summoning.pet.impl.AbyssalMinion;
import game.content.skilling.summoning.pet.impl.BabyBasilisk;
import game.content.skilling.summoning.pet.impl.BabyKurask;
import game.content.skilling.summoning.pet.impl.Minitrice;

/**
 * Represents a pet
 * 
 * @author 2012
 *
 */
public enum SummoningPet {

	DOGS(new int[][] {{6958, 6959, 12512}, {6959, 12513}, {6960, 6961, 12514}, {6961, 12515},
			{6962, 6963, 12516}, {6963, 12517}, {6964, 6965, 12518}, {6965, 12519},
			{6966, 6967, 12520}, {6967, 12521}, {6968, 6969, 12522}, {6969, 12523},}, null,
			new int[] {1927}, null, null, null, new int[][] {},
			new String[] {"Woof!", "Woof Woof!"}),

	CATS(new int[][] {{761, 1555}, {762, 1556}, {763, 1557}, {764, 1558}, {765, 1559}, {766, 1560},
			{768, 1561}, {769, 1562}, {770, 1563}, {771, 1564}, {772, 1565}, {773, 1566}, {3505, 7583},
			{3506, 7584}, {3507, 7585},}, null, SummoningPetManager.DOG_FOOD, null, null, null,
			new int[][] {}, new String[] {"Meow"}),

	CREEPING_HAND(new int[][] {{8619, 14652,}}, null, new int[] {1059}, "Food!",
			"Finger food; my favourite.",
			"Ewaaa", new int[][] {{ServerConstants.SLAYER, 5}, {ServerConstants.SUMMONING, 4},},
			new String[] {"How handy am I?!", "I can't feel my arms!",
					"There's no wrist for the wicked."}),

	MINITRICE(new int[][] {{8620, 14653,}}, new Minitrice(), new int[] {225}, "Food!", "Yummy!",
			"Eww",
			new int[][] {{ServerConstants.SLAYER, 25},},
			new String[] {"Maybe not today, maybe not tomorrow, but me kill you",
					"Don't look into the light!", "I will blind you!",
					"My eyes feel like burning!...",}),

	BABY_BASILISK(new int[][] {{8621, 14654,}}, new BabyBasilisk(), new int[] {221}, "Me hungry!",
			"Yummy!", "Eww",
			new int[][] {{ServerConstants.SLAYER, 40}},
			new String[] {"I will blind you!", "My eyes feel like burning, My eyes feel like burning!",
					"Don't look into the light!"}),

	BABY_KURASK(new int[][] {{8622, 14655,}}, new BabyKurask(), new int[] {526}, "Foodegh", "Munch",
			"Ewwghh",
			new int[][] {{ServerConstants.SLAYER, 70}}, new String[] {"I kill you!", "Eurghh"}),

	ABYSSAL_MINION(new int[][] {{8624, 14656}}, new AbyssalMinion(), new int[] {}, "Food human!",
			"More power!",
			"Dislike!", new int[][] {{ServerConstants.SLAYER, 85}},
			new String[] {"To the Abyss!", "For the Abyss!"}),

	RUNE_GUARDIAN(new int[][] {}, new AbyssalMinion(), new int[] {}, "Food human!", "More power!",
			"Dislike!", new int[][] {{ServerConstants.SLAYER, 85}},
			new String[] {"You must lash out with every limb, like the octopus who plays the bongos.",
					"He who questions training only trains himself in asking questions",
					"Have you ever thought that, whenever you examine an item, that item is also examining you?",}),

	GECKO(new int[][] {{6915, 6916, 12488}, {6916, 12489},}, null, new int[] {12127, 12125},
			"Hungggerry", "Yaah", "Neeuhh",
			new int[][] {{ServerConstants.SUMMONING, 10}, {ServerConstants.HUNTER, 27}}, null),

	PLATYPUS(new int[][] {{9304, 12547}}, null, SummoningPetManager.RAW_FISH, "Chiiim-food!",
			"Chim!", "Cheeewww",
			new int[][] {{ServerConstants.SUMMONING, 10}, {ServerConstants.HUNTER, 27}},
			new String[] {"Chim chim chiree"}),

	BROAV(new int[][] {{8491, 14533}}, null, new int[] {2970}, "HUNGERery", "PHueeh", "Awehe",
			new int[][] {{ServerConstants.SUMMONING, 23}, {ServerConstants.HUNTER, 55}},
			new String[] {"I eat like a pig."}),

	PENGIUN(new int[][] {{6908, 6910, 12481}, {6910, 12762},}, null, SummoningPetManager.RAW_FISH,
			"Awk awk!!", "Awkwakwak", "Awwwwwkkk", new int[][] {{ServerConstants.SUMMONING, 30}},
			new String[] {"Slide!"}),

	TOOTH_CREATURE(new int[][] {{11408, 18668}}, null, new int[] {13571, 1977}, "Food!", "Yuumm",
			"Eww",
			new int[][] {{ServerConstants.SUMMONING, 37}}, null),

	GIANT_CRAB(new int[][] {{6947, 6948, 12500}, {6948, 12501},}, null, SummoningPetManager.RAW_FISH,
			"Food!", "Yuumm", "Eww",
			new int[][] {{ServerConstants.SUMMONING, 40}, {ServerConstants.SLAYER, 32}}, null),

	SQUIRREL(new int[][] {{6919, 6920, 12490}, {6920, 12491},}, null, SummoningPetManager.RAW_FISH,
			"Squeak Food Squeak!", "Yuumm", "Eww",
			new int[][] {{ServerConstants.SUMMONING, 60}, {ServerConstants.HUNTER, 29}}, null),

	SARADOMIN_OWL(new int[][] {{6949, 6950, 12503}, {6950, 6951, 12504}, {6951, 12505},}, null,
			new int[] {313}, "Tweet Food!", "Chirp Chirp", "Eww",
			new int[][] {{ServerConstants.SUMMONING, 70}},
			new String[] {"Lovely blue plumage", "Oh, really?"}),

	GUTHIX_RAPTOR(new int[][] {{6955, 6956, 12509}, {6956, 6957, 12510}, {6957, 12511},}, null,
			new int[] {313}, "Chirp Food!", "Tweet", "Eww",
			new int[][] {{ServerConstants.SUMMONING, 70}}, new String[] {"Are you my daddy?",}),

	ZAMORAK_HAWK(new int[][] {{6952, 6953, 12506}, {6953, 6954, 12507}, {6954, 12508},}, null,
			new int[] {313}, "Chirrup Food!", "Tweet", "Eww",
			new int[][] {{ServerConstants.SUMMONING, 70}},
			new String[] {"I can feel the anger welling within you!", "Excellent!"}),

	PHOENIX_EGGLING(new int[][] {{8550, 14626}}, null, new int[] {592}, "Food!", "Yuum!", "Eww",
			new int[][] {{ServerConstants.SUMMONING, 72}, {ServerConstants.SLAYER, 51}},
			new String[] {"Burn", "Bwaaark!"}),

	RACCOON(new int[][] {{6913, 6914, 12486}, {6914, 12487},}, null, SummoningPetManager.RAW_FISH,
			"Chitt Food!", "Chitter!", "Eww chit!",
			new int[][] {{ServerConstants.SUMMONING, 80}, {ServerConstants.HUNTER, 27}}, null),

	SNEAKERPEEPER(new int[][] {{13090, 19891}}, null, new int[] {221}, "Food!", "Delicious!", "Eww!",
			new int[][] {{ServerConstants.SUMMONING, 80}}, new String[] {"Look into my eyes..."}),

	VULTURE(new int[][] {{6945, 6946, 12498}, {6946, 12499},}, null, new int[] {313}, "Chirp Food!",
			"Tweet", "Eww", new int[][] {{ServerConstants.SUMMONING, 85}}, null),

	CHAMELEON(new int[][] {{6922, 6923, 12492}, {6923, 12493},}, null, new int[] {313},
			"Chirp Food!", "Tweet", "Eeeee", new int[][] {{ServerConstants.SUMMONING, 90}}, null),

	MONKEY(new int[][] {{6942, 12496}, {6943, 12497},}, null, new int[] {5408}, "Ooh aah Banana!",
			"Ah ah!!", "Ehh",
			new int[][] {{ServerConstants.SUMMONING, 95}, {ServerConstants.HUNTER, 27}},
			new String[] {"Ooh Ooh Ah Ah!"}),

	DRAGON(
			new int[][] {{6900, 6901, 12469}, {6901, 12470}, {6902, 6903, 12471}, {6903, 12472},
					{6904, 6905, 12473}, {6905, 6906, 12474}, {6906, 6907, 12475}, {6907, 12476},},
			null, SummoningPetManager.RAW_FISH, "Growl meat!", "Rawr, raaar!", "Roooooar!",
			new int[][] {{ServerConstants.SUMMONING, 72}, {ServerConstants.SLAYER, 51}},
			new String[] {"Rawr, raaar! Growl raaaaawraaaaw!", "Roooooar! Graaaaah roaaaaar!"}),

	TZREK_JAD(new int[][] {{3604, 21512}}, null, null, "", "", "",
			new int[][] {{ServerConstants.SUMMONING, 99}, {ServerConstants.SLAYER, 99}}, null),

	;

	/**
	 * The pet
	 */
	private int[][] pet;

	/**
	 * The dialogue
	 */
	private PetDialogue dialogue;

	/**
	 * The food
	 */
	private int[] food;

	/**
	 * The hungry line
	 */
	private String hungry;

	/**
	 * The accepting food line
	 */
	private String acceptFood;

	/**
	 * The rejecting food line
	 */
	private String rejectFood;

	/**
	 * The skills required
	 */
	private int[][] skillsRequired;

	/**
	 * The random shouts
	 */
	private String[] shouts;

	/**
	 * Represents a summoning pet
	 * 
	 * @param pet the pet
	 * @param dialogue the dialogue
	 * @param food the food
	 * @param hungry the hungry
	 * @param acceptFood the acceptance food
	 * @param rejectFood the rejection of food
	 * @param requireAttention whether requires attention
	 * @param hourlyGrowth the hourly growth rate
	 * @param skillsRequired the skills required
	 * @param shouts the shouts
	 */
	SummoningPet(int[][] pet, PetDialogue dialogue, int[] food, String hungry, String acceptFood,
			String rejectFood, int[][] skillsRequired, String[] shouts) {
		this.pet = pet;
		this.dialogue = dialogue;
		this.food = food;
		this.hungry = hungry;
		this.acceptFood = acceptFood;
		this.rejectFood = rejectFood;
		this.skillsRequired = skillsRequired;
		this.shouts = shouts;
	}

	/**
	 * Gets the pet
	 *
	 * @return the pet
	 */
	public int[][] getPet() {
		return pet;
	}

	/**
	 * Gets the dialogue
	 *
	 * @return the dialogue
	 */
	public PetDialogue getDialogue() {
		return dialogue;
	}

	/**
	 * Gets the food
	 *
	 * @return the food
	 */
	public int[] getFood() {
		return food;
	}

	/**
	 * Gets the hungry
	 *
	 * @return the hungry
	 */
	public String getHungry() {
		return hungry;
	}

	/**
	 * Gets the acceptFood
	 *
	 * @return the acceptFood
	 */
	public String getAcceptFood() {
		return acceptFood;
	}

	/**
	 * Gets the rejectFood
	 *
	 * @return the rejectFood
	 */
	public String getRejectFood() {
		return rejectFood;
	}

	/**
	 * Gets the skillsRequired
	 *
	 * @return the skillsRequired
	 */
	public int[][] getSkillsRequired() {
		return skillsRequired;
	}

	/**
	 * Gets the shouts
	 *
	 * @return the shouts
	 */
	public String[] getShouts() {
		return shouts;
	}

	/**
	 * Gets pet by item id
	 * 
	 * @param id the id
	 * @return the pet
	 */
	public static SummoningPet forItem(int id) {
		for (SummoningPet pet : SummoningPet.values()) {
			if (pet == null) {
				continue;
			}
			for (int i = 0; i < pet.getPet().length; i++) {
				if (pet.getPet()[i].length == 3) {
					if (pet.getPet()[i][2] == id) {
						return pet;
					}
				} else {
					if (pet.getPet()[i][1] == id) {
						return pet;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Gets pet by npc id
	 * 
	 * @param id the id
	 * @return the pet
	 */
	public static SummoningPet forNpc(int id) {
		for (SummoningPet pet : SummoningPet.values()) {
			if (pet == null) {
				continue;
			}
			for (int i = 0; i < pet.getPet().length; i++) {
				if (pet.getPet()[i][0] == id) {
					return pet;
				}
			}
		}
		return null;
	}

	/**
	 * Gets npc by item id
	 * 
	 * @param id the id
	 * @return the npc
	 */
	public static int getNpc(int id) {
		for (SummoningPet pet : SummoningPet.values()) {
			if (pet == null) {
				continue;
			}
			for (int i = 0; i < pet.getPet().length; i++) {
				if (pet.getPet()[i].length == 3) {
					if (pet.getPet()[i][2] == id) {
						return pet.getPet()[i][0];
					}
				} else {
					if (pet.getPet()[i][1] == id) {
						return pet.getPet()[i][0];
					}
				}
			}
		}
		return -1;
	}

	/**
	 * Gets the next stage
	 * 
	 * @param id the id
	 * @return the npc
	 */
	public static int getNextStage(int id) {
		for (SummoningPet pet : SummoningPet.values()) {
			if (pet == null) {
				continue;
			}
			for (int i = 0; i < pet.getPet().length; i++) {
				if (pet.getPet()[i].length == 3) {
					if (pet.getPet()[i][0] == id) {
						return pet.getPet()[i][1];
					}
				}
			}
		}
		return -1;
	}
}
