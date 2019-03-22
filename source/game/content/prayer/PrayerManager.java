package game.content.prayer;

import java.util.ArrayList;
import java.util.Objects;
import core.GameType;
import core.ServerConstants;
import game.content.combat.Combat;
import game.content.interfaces.ItemsKeptOnDeath;
import game.content.music.SoundSystem;
import game.content.quicksetup.QuickSetUp;
import game.content.skilling.Skilling;
import game.content.starter.GameMode;
import game.content.worldevent.Tournament;
import game.player.Area;
import game.player.Player;

/**
 * Handles prayer
 * 
 * @author 2012 TODO: Combat effects for curses
 */
public class PrayerManager {

	/**
	 * The curse config id
	 */
	public static final int CURSE_CONFIG_START = 660;

	/**
	 * The regular config id
	 */
	public static final int REGULAR_CONFIG_START = 650;

	/**
	 * The size of the skills to check adjustment
	 */
	private static final int SKILL_STATS_SIZE = 5;

	/**
	 * The skill adjustment line
	 */
	private static final int SKILL_ADJUSTMENT_LINE = 28_788;

	/**
	 * The active prayers
	 */
	private ArrayList<Prayer> active = new ArrayList<Prayer>();

	/**
	 * The quick prayers
	 */
	private ArrayList<Prayer> quickPrayer = new ArrayList<Prayer>();

	/**
	 * Represents the prayer type
	 */
	public enum PrayerType {

		/*
		 * The curse prayer book
		 */
		CURSES(21_356),

		/*
		 * The regular prayer book
		 */
		REGULAR(35_800),

		;

		/**
		 * The interface id
		 */
		private int interfaceId;

		/**
		 * Represents a prayer type
		 * 
		 * @param interfaceId the interface id
		 */
		PrayerType(int interfaceId) {
			this.interfaceId = interfaceId;
		}

		/**
		 * Sets the interfaceId
		 *
		 * @return the interfaceId
		 */
		public int getInterfaceId() {
			return interfaceId;
		}
	}

	/**
	 * The defence prayers
	 */
	private static final Prayer[] DEFENCE_PRAYER = {Prayer.STEEL_SKIN, Prayer.ROCK_SKIN,
			Prayer.STEEL_SKIN, Prayer.CHIVALRY, Prayer.PIETY, Prayer.RIGOUR, Prayer.AUGURY};

	/**
	 * The strength prayers
	 */
	private static final Prayer[] STRENGTH_PRAYER = {Prayer.BURST_OF_STRENGTH,
			Prayer.SUPERHUMAN_STRENGTH, Prayer.ULTIMATE_STRENGTH, Prayer.CHIVALRY, Prayer.PIETY};

	/**
	 * The attack prayers
	 */
	private static final Prayer[] ATTACK_PRAYER = {Prayer.CLARITY_OF_THOUGHT,
			Prayer.IMPROVED_REFLEXES, Prayer.INCREDIBLE_REFLEXES, Prayer.CHIVALRY, Prayer.PIETY};

	/**
	 * The range prayer
	 */
	private static final Prayer[] RANGE_PRAYER =
			{Prayer.SHARP_EYE, Prayer.HAWK_EYE, Prayer.EAGLE_EYE, Prayer.RIGOUR};

	/**
	 * The magic prayer
	 */
	private static final Prayer[] MAGIC_PRAYER =
			{Prayer.MYSTIC_WILL, Prayer.MYSTIC_LORE, Prayer.MYSTIC_EYE, Prayer.AUGURY};

	/**
	 * The overhead prayers
	 */
	private static final Prayer[] OVERHEADS = {Prayer.PROTECT_FROM_MAGIC, Prayer.PROTECT_FROM_MELEE,
			Prayer.PROTECT_FROM_MISSILES, Prayer.RETRIBUTION, Prayer.REDEMPTION, Prayer.SMITE};

	/**
	 * Handles switching prayers
	 */
	public enum SwitchingPrayer {

		DEFENCE(DEFENCE_PRAYER, DEFENCE_PRAYER),

		ATTACK(ATTACK_PRAYER, ATTACK_PRAYER),

		STRENGTH(STRENGTH_PRAYER, STRENGTH_PRAYER),

		RANGE(RANGE_PRAYER, RANGE_PRAYER),

		MAGIC(MAGIC_PRAYER, MAGIC_PRAYER),

		OVERHEAD(OVERHEADS, OVERHEADS),

		SAP_WARRIOR(Prayer.SAP_RANGER, new Prayer[] {Prayer.LEECH_ATTACK, Prayer.LEECH_DEFENCE,
				Prayer.LEECH_STRENGTH, Prayer.TURMOIL}),

		SAP_RANGE(Prayer.SAP_RANGER, new Prayer[] {Prayer.LEECH_RANGED, Prayer.TURMOIL}),

		SAP_MAGIC(Prayer.SAP_MAGE, new Prayer[] {Prayer.LEECH_MAGIC, Prayer.TURMOIL}),

		SAP_SPIRIT(Prayer.SAP_SPIRIT,
				new Prayer[] {Prayer.LEECH_ENERGY, Prayer.LEECH_SPECIAL, Prayer.TURMOIL}),

		DEFLECT_SUMMONING(Prayer.DEFLECT_SUMMONING, new Prayer[] {Prayer.WRATH, Prayer.SOUL_SPLIT}),

		DEFLECT_MAGIC(Prayer.DEFLECT_MAGIC, new Prayer[] {Prayer.DEFLECT_MISSILES,
				Prayer.DEFLECT_MELEE, Prayer.WRATH, Prayer.SOUL_SPLIT}),


		DEFLECT_MISSILES(Prayer.DEFLECT_MISSILES, new Prayer[] {Prayer.DEFLECT_MAGIC,
				Prayer.DEFLECT_MELEE, Prayer.WRATH, Prayer.SOUL_SPLIT}),

		DEFLECT_MELEE(Prayer.DEFLECT_MELEE, new Prayer[] {Prayer.DEFLECT_MISSILES,
				Prayer.DEFLECT_MAGIC, Prayer.WRATH, Prayer.SOUL_SPLIT}),

		LEECH_ATTACK(Prayer.LEECH_ATTACK, new Prayer[] {Prayer.SAP_WARRIOR, Prayer.TURMOIL}),

		LEECH_RANGER(Prayer.LEECH_RANGED, new Prayer[] {Prayer.SAP_RANGER, Prayer.TURMOIL}),

		LEECH_MAGIC(Prayer.LEECH_MAGIC, new Prayer[] {Prayer.SAP_MAGE, Prayer.TURMOIL}),

		LEECH_DEFENCE(Prayer.LEECH_DEFENCE, new Prayer[] {Prayer.SAP_MAGE, Prayer.TURMOIL}),

		LEECH_STRENGTH(Prayer.LEECH_STRENGTH,
				new Prayer[] {Prayer.SAP_MAGE, Prayer.TURMOIL}), LEECH_ENERGY(Prayer.LEECH_ENERGY,
						new Prayer[] {Prayer.SAP_MAGE, Prayer.TURMOIL}), LEECH_SPECIAL(
								Prayer.LEECH_SPECIAL, new Prayer[] {Prayer.SAP_MAGE, Prayer.TURMOIL}),

		WRATH(Prayer.WRATH, new Prayer[] {Prayer.SOUL_SPLIT, Prayer.DEFLECT_SUMMONING,
				Prayer.DEFLECT_MAGIC, Prayer.DEFLECT_MISSILES, Prayer.DEFLECT_MELEE}),

		SOUL_SPLIT(Prayer.SOUL_SPLIT, new Prayer[] {Prayer.DEFLECT_SUMMONING, Prayer.DEFLECT_MAGIC,
				Prayer.DEFLECT_MISSILES, Prayer.DEFLECT_MELEE, Prayer.WRATH}),

		TURMOIL(Prayer.SOUL_SPLIT,
				new Prayer[] {Prayer.SAP_WARRIOR, Prayer.SAP_RANGER, Prayer.SAP_MAGE, Prayer.SAP_SPIRIT,
						Prayer.LEECH_ATTACK, Prayer.LEECH_RANGED, Prayer.LEECH_MAGIC,
						Prayer.LEECH_DEFENCE, Prayer.LEECH_STRENGTH, Prayer.LEECH_ENERGY,
						Prayer.LEECH_SPECIAL}),;

		/**
		 * The prayer
		 */
		private Prayer[] prayer;

		/**
		 * The prayers to turn off
		 */
		private Prayer[] turnOff;

		/**
		 * Represents switching prayer
		 * 
		 * @param prayer the prayer
		 * @param turnOff the turn off
		 */
		SwitchingPrayer(Prayer[] prayer, Prayer[] turnOff) {
			this.prayer = prayer;
			this.turnOff = turnOff;
		}

		/**
		 * Represents switching prayer
		 * 
		 * @param prayer the prayer
		 * @param turnOff the turn off
		 */
		SwitchingPrayer(Prayer prayer, Prayer[] turnOff) {
			this(new Prayer[] {prayer}, turnOff);
		}

		/**
		 * Gets the prayer
		 *
		 * @return the prayer
		 */
		public Prayer[] getPrayer() {
			return prayer;
		}

		/**
		 * Gets the turnOff
		 *
		 * @return the turnOff
		 */
		public Prayer[] getTurnOff() {
			return turnOff;
		}

		/**
		 * Gets the switching prayers by prayer
		 * 
		 * @param prayer the prayer
		 * @return the prayers
		 */
		public static SwitchingPrayer forPrayer(Prayer prayer) {
			for (SwitchingPrayer pray : SwitchingPrayer.values()) {
				for (Prayer p : pray.getPrayer()) {
					if (p.equals(prayer)) {
						return pray;
					}
				}
			}
			return null;
		}
	}

	/**
	 * The prayer type
	 */
	private PrayerType prayerType = PrayerType.REGULAR;

	/**
	 * Switching book
	 * 
	 * @param player the player
	 */
	public static void switchBook(Player player) {
		/*
		 * Non pre eoc
		 */
		if (!GameType.isPreEoc()) {
			return;
		}
		/*
		 * Switching
		 */
		if (player.getPrayer().getPrayerType().equals(PrayerType.REGULAR)) {
			player.getDH().sendStatement(
					"The altar fills your head with dark thoughts, purging the prayers",
					"from your memory and leaving only curses in their place.");
			player.getPrayer().setPrayerType(PrayerType.CURSES);
			player.getPA().setSidebarInterface(5, PrayerType.CURSES.getInterfaceId());
		} else {
			player.getDH().sendStatement(
					"The altar clears your head from dark thoughts, reviving the prayers",
					"from your memory and bringing the prayers in their place.");
			player.getPrayer().setPrayerType(PrayerType.REGULAR);
			player.getPA().setSidebarInterface(5, PrayerType.REGULAR.getInterfaceId());
		}
	}

	/**
	 * Handling the buttons
	 * 
	 * @param player the player
	 * @param buttonId the button id
	 * @return using prayer
	 */
	public static boolean handleButton(Player player, int buttonId) {
		if (GameType.isOsrs()) {
			return false;
		}
		/*
		 * The prayer
		 */
		Prayer prayer = Prayer.forButton(buttonId);
		/*
		 * Found prayer
		 */
		if (prayer != null) {
			active(player, prayer, false);
			return true;
		}
		/*
		 * The quick prayer
		 */
		Prayer quickPrayer = Prayer.forQuickButton(buttonId);
		/*
		 * Found prayer
		 */
		if (quickPrayer != null) {
			active(player, quickPrayer, true);
			return true;
		}
		return false;
	}

	/**
	 * Activating prayer
	 * 
	 * @param player the player
	 * @param prayer the prayer
	 */
	private static void active(Player player, Prayer prayer, boolean quickPrayer) {
		/*
		 * Player is dead
		 */
		if (player.getDead()) {
			reset(player, prayer, quickPrayer);
			return;
		}
		/*
		 * Check prayer book
		 */
		if (prayer.isCurse() && !player.getPrayer().getPrayerType().equals(PrayerType.CURSES)) {
			return;
		}
		/*
		 * Check prayer book
		 */
		if (!prayer.isCurse() && player.getPrayer().getPrayerType().equals(PrayerType.CURSES)) {
			return;
		}
		/*
		 * Spear event
		 */
		if (player.dragonSpearEvent) {
			reset(player, prayer, quickPrayer);
			return;
		}
		/*
		 * Prevent player from using p2p prayers while in f2p combat.
		 */
		if (System.currentTimeMillis() - player.getTimeUnderAttackByAnotherPlayer() <= 9000
				|| System.currentTimeMillis() - player.getTimeAttackedAnotherPlayer() <= 9000) {
			if (prayer.ordinal() >= 21 && QuickSetUp.isUsingF2pOnly(player, false, false)) {
				player.getPA()
						.sendMessage("You cannot use p2p prayers while wearing full f2p in combat.");
				reset(player, prayer, quickPrayer);
				return;
			}
		}
		/*
		 * Cant use prayers in tournament
		 */
		if (prayer.ordinal() == ServerConstants.RETRIBUTION && player.getHeight() == 20
				|| player.getHeight() == 20 && prayer.ordinal() >= 16 && prayer.ordinal() <= 18
						&& !Tournament.eventType.equals("Pure tribrid")
						&& !Tournament.eventType.equals("Main Nh")) {
			player.getPA().sendMessage("You cannot use these prayers in this tournament.");
			reset(player, prayer, quickPrayer);
			return;
		}
		/*
		 * Cant use prayer here
		 */
		if (Area.inRfdArea(player.getX(), player.getY())) {
			Combat.resetPrayers(player);
			player.playerAssistant.sendMessage("A strong force prevents you from using prayers.");
			reset(player, prayer, quickPrayer);
			return;
		}
		/*
		 * Can't duel
		 */
		if (player.duelRule[7]) {
			player.getPA().sendMessage("You cannot use prayer during this duel!");
			resetAll(player, quickPrayer);
			return;
		}
		/*
		 * Checks for points
		 */
		if (player.baseSkillLevel[ServerConstants.PRAYER] < 1) {
			player.getPA().sendMessage("You don't have any Prayer points!");
			resetAll(player, quickPrayer);
			SoundSystem.sendSound(player, 437, 0);
			return;
		}
		/*
		 * Check for level
		 */
		if (Skilling.getLevelForExperience(player.skillExperience[ServerConstants.PRAYER]) < prayer
				.getLevelRequired()) {
			player.getPA().sendFrame126("You need a Prayer level of " + prayer.getLevelRequired()
					+ " to use " + prayer.name().toLowerCase().replaceAll("_", " ") + ".", 357);
			player.getPA().sendFrame126("Click here to continue", 358);
			player.getPA().sendFrame164(356);
			reset(player, prayer, quickPrayer);
			return;
		}
		/*
		 * Effects
		 */
		if (!prayer.getEffects(player)) {
			reset(player, prayer, quickPrayer);
			return;
		}
		/*
		 * Activating
		 */
		if (quickPrayer ? !player.getPrayer().getQuickPrayer().contains(prayer)
				: !player.getPrayer().getActive().contains(prayer)) {
			/*
			 * The head icon
			 */
			if (!quickPrayer) {
				if (prayer.getHeadIcon() > 0) {
					player.headIcon = prayer.getHeadIcon();
				}
			}
			/*
			 * Turn off
			 */
			SwitchingPrayer switching = SwitchingPrayer.forPrayer(prayer);
			/*
			 * Switches
			 */
			if (switching != null) {
				/*
				 * Turn off others
				 */
				for (Prayer prayers : switching.getTurnOff()) {
					reset(player, prayers, quickPrayer);
				}
			}
		} else {
			/*
			 * Turn off headicon
			 */
			if (!quickPrayer) {
				if (prayer.getHeadIcon() > 0 && player.headIcon == prayer.getHeadIcon()) {
					player.headIcon = -1;
				}
				/*
				 * Summoning, multiple prayer
				 */
				if (prayer == Prayer.DEFLECT_SUMMONING) {
					if (player.getPrayer().getActive().contains(Prayer.DEFLECT_MAGIC)) {
						player.headIcon = Prayer.DEFLECT_MAGIC.getHeadIcon();
					} else if (player.getPrayer().getActive().contains(Prayer.DEFLECT_MISSILES)) {
						player.headIcon = Prayer.DEFLECT_MISSILES.getHeadIcon();
					} else if (player.getPrayer().getActive().contains(Prayer.DEFLECT_MELEE)) {
						player.headIcon = Prayer.DEFLECT_MELEE.getHeadIcon();
					}
				} else if (prayer == Prayer.PROTECT_FROM_SUMMONING) {
					if (player.getPrayer().getActive().contains(Prayer.PROTECT_FROM_MAGIC)) {
						player.headIcon = Prayer.PROTECT_FROM_MAGIC.getHeadIcon();
					} else if (player.getPrayer().getActive().contains(Prayer.PROTECT_FROM_MISSILES)) {
						player.headIcon = Prayer.PROTECT_FROM_MISSILES.getHeadIcon();
					} else if (player.getPrayer().getActive().contains(Prayer.PROTECT_FROM_MELEE)) {
						player.headIcon = Prayer.PROTECT_FROM_MELEE.getHeadIcon();
					}
				}
			}
			/*
			 * Off sound
			 */
			SoundSystem.sendSound(player, 435, 0);
		}
		/*
		 * Set active state
		 */
		if (quickPrayer) {
			/*
			 * Contains so remove
			 */
			if (player.getPrayer().getQuickPrayer().contains(prayer)) {
				player.getPrayer().getQuickPrayer().remove(prayer);
			} else {
				player.getPrayer().getQuickPrayer().add(prayer);
			}
			/*
			 * Set config
			 */
			player.getPA().sendFrame36(prayer.getQuickPrayerConfig(),
					player.getPrayer().getQuickPrayer().contains(prayer) ? 1 : 0, false);
		} else {
			/*
			 * Contains so remove
			 */
			if (player.getPrayer().getActive().contains(prayer)) {
				player.getPrayer().getActive().remove(prayer);
			} else {
				if (!prayer.isCurse()) {
					player.timePrayerActivated[prayer.ordinal()] = System.currentTimeMillis();
				}
				player.getPrayer().getActive().add(prayer);
			}
			/*
			 * Set config
			 */
			player.getPA().sendFrame36(prayer.getConfig(),
					player.getPrayer().getActive().contains(prayer) ? 1 : 0, false);
			/*
			 * Multi icon for curses
			 */
			if (GameType.isPreEoc()) {
				if (player.getPrayer().getPrayerType().equals(PrayerType.CURSES)) {
					/*
					 * Deflecting summoning
					 */
					if (player.getPrayer().getActive().contains(Prayer.DEFLECT_SUMMONING)) {
						/*
						 * Deflect magic
						 */
						if (player.getPrayer().getActive().contains(Prayer.DEFLECT_MAGIC)) {
							player.headIcon = 15;
						} else if (player.getPrayer().getActive().contains(Prayer.DEFLECT_MISSILES)) {
							player.headIcon = 14;
						} else if (player.getPrayer().getActive().contains(Prayer.DEFLECT_MELEE)) {
							player.headIcon = 13;
						} else {
							player.headIcon = 12;
						}
					}
				} else {
					/*
					 * Protect from summoning
					 */
					if (player.getPrayer().getActive().contains(Prayer.PROTECT_FROM_SUMMONING)) {
						if (player.getPrayer().getActive().contains(Prayer.PROTECT_FROM_MAGIC)) {
							player.headIcon = 21;
						} else if (player.getPrayer().getActive()
								.contains(Prayer.PROTECT_FROM_MISSILES)) {
							player.headIcon = 20;
						} else if (player.getPrayer().getActive().contains(Prayer.PROTECT_FROM_MELEE)) {
							player.headIcon = 19;
						} else {
							player.headIcon = 18;
						}
					}
				}
			}
		}
		/*
		 * Send stat adjustment
		 */
		sendStatAdjustment(player);
		/*
		 * Request updates such as headicon
		 */
		player.getPA().requestUpdates();
	}

	/**
	 * Handles the combat prayer
	 * 
	 * @param player the player
	 * @param victim the victim
	 * @param damage the damage
	 */
	public static void handleCombatPrayer(Player player, Player victim, int damage) {
		player.getPrayer().getActive().stream().filter(Objects::nonNull)
				.filter(prayer -> prayer.getCombatPrayer() != null)
				.forEach(prayer -> prayer.getCombatPrayer().execute(player, victim, damage));
	}

	/**
	 * Checking protect item
	 * 
	 * @param player the player
	 * @return protect item
	 */
	public static boolean checkProtectItem(Player player) {
		/*
		 * Update item on death interface
		 */
		ItemsKeptOnDeath.updateInterface(player);
		/*
		 * Can't use with red skull
		 */
		if (player.getRedSkull()) {
			player.getPA().sendMessage(
					ServerConstants.RED_COL + "You cannot use protect item with red skull!");
			return false;
		}
		/*
		 * Can't use as ultimate iron man
		 */
		if (GameMode.getGameMode(player, "ULTIMATE IRON MAN")) {
			player.getDH().sendStatement("As an Ultimate Ironman, you cannot protect item.");
			return false;
		}
		return true;
	}

	/**
	 * Checking protection prayer availability
	 * 
	 * @param player the player
	 * @return protection prayer
	 */
	public static boolean checkProtectionPrayers(Player player) {
		/*
		 * Cannot use protection
		 */
		if (System.currentTimeMillis() - player.stopPrayerDelay < 4700) {
			player.playerAssistant.sendMessage("You have been injured and can't use this prayer!");
			reset(player, Prayer.PROTECT_FROM_MAGIC, false);
			reset(player, Prayer.PROTECT_FROM_MELEE, false);
			reset(player, Prayer.PROTECT_FROM_MISSILES, false);
			reset(player, Prayer.DEFLECT_MAGIC, false);
			reset(player, Prayer.DEFLECT_MELEE, false);
			reset(player, Prayer.DEFLECT_MISSILES, false);
			reset(player, Prayer.DEFLECT_SUMMONING, false);
			SoundSystem.sendSound(player, 447, 0);
			return false;
		}
		return true;
	}

	/**
	 * Resets all prayers
	 * 
	 * @param player the player
	 */
	public static void resetAll(Player player, boolean quickPrayer) {
		/*
		 * Loops through all prayers
		 */
		for (Prayer prayer : player.getPrayer().getActive()) {
			reset(player, prayer, quickPrayer);
		}
	}

	/**
	 * Reseting prayer
	 * 
	 * @param player the player
	 * @param prayer the prayer
	 */
	public static void reset(Player player, Prayer prayer, boolean quickPrayer) {
		/*
		 * Reset quick prayer
		 */
		if (quickPrayer) {
			player.getPrayer().getQuickPrayer().remove(prayer);
			player.getPA().sendFrame36(prayer.getConfig(), 0, false);
		} else {
			/*
			 * Reset prayer
			 */
			player.getPrayer().getActive().remove(prayer);
			player.getPA().sendFrame36(prayer.getConfig(), 0, false);
		}
		/*
		 * Deactivate sound
		 */
		SoundSystem.sendSound(player, 447, 0);
		/*
		 * Send stats
		 */
		sendStatAdjustment(player);
	}

	/**
	 * Sends stat adjustment
	 * 
	 * @param player the player
	 */
	public static void sendStatAdjustment(Player player) {
		/*
		 * The stats
		 */
		int[] stats = new int[SKILL_STATS_SIZE];
		/*
		 * Loops through active prayers
		 */
		for (Prayer prayer : player.getPrayer().getActive()) {
			/*
			 * No stats to display
			 */
			if (prayer.getStatDisplay() == null) {
				continue;
			}
			/*
			 * Gathers the stats
			 */
			for (int i = 0; i < prayer.getStatDisplay().length; i++) {
				stats[i] += prayer.getStatDisplay()[i];
			}
		}
		/*
		 * Display stats
		 */
		for (int i = 0; i < stats.length; i++) {
			String colour = stats[i] > 0 ? "@gre@" : "@rfk@";
			player.getPA().sendFrame126(colour + stats[i] + "%", SKILL_ADJUSTMENT_LINE + i);
		}
	}

	/**
	 * Sets the prayerType
	 *
	 * @return the prayerType
	 */
	public PrayerType getPrayerType() {
		return prayerType;
	}

	/**
	 * Sets the prayerType
	 * 
	 * @param prayerType the prayerType
	 */
	public void setPrayerType(PrayerType prayerType) {
		this.prayerType = prayerType;
	}

	/**
	 * Gets the active
	 *
	 * @return the active
	 */
	public ArrayList<Prayer> getActive() {
		return active;
	}

	/**
	 * Gets the quickPrayer
	 *
	 * @return the quickPrayer
	 */
	public ArrayList<Prayer> getQuickPrayer() {
		return quickPrayer;
	}
}
