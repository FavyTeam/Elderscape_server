package game.content.prayer;

import java.util.Arrays;
import core.ServerConstants;
import game.content.achievement.Achievements;
import game.content.item.impl.RapidRenewalScroll;
import game.content.prayer.combat.CombatPrayer;
import game.content.prayer.combat.impl.DeflectCombatPrayer;
import game.content.prayer.combat.impl.LeechCombatPrayer;
import game.content.prayer.combat.impl.SapCombatPrayer;
import game.content.prayer.combat.impl.SoulSplitCombatPrayer;
import game.player.Player;
import utility.Misc;

/**
 * Handles all the prayers
 * 
 * @author 2012
 *
 */
public enum Prayer {
	THICK_SKIN(21233, 135_006, 1, -1, 0.05, 446, 83, new int[] {0, 0, 5, 0, 0}),

	BURST_OF_STRENGTH(21234, 135_008, 4, -1, 0.05, 449, 84, new int[] {0, 5, 0, 0, 0}),

	CLARITY_OF_THOUGHT(21235, 135_010, 7, -1, 0.05, 436, 85, new int[] {5, 0, 0, 0, 0}),

	SHARP_EYE(70080, 135_012, 8, -1, 0.05, 337, 601, new int[] {0, 0, 0, 5, 0}),

	MYSTIC_WILL(70082, 135_014, 9, -1, 0.05, 337, 602, new int[] {0, 0, 0, 0, 5}),

	ROCK_SKIN(21236, 135_016, 10, -1, 0.10, 441, 86, new int[] {0, 0, 10, 0, 0}),

	SUPERHUMAN_STRENGTH(21237, 135_018, 13, -1, 0.10, 434, 87, new int[] {0, 10, 0, 0, 0}),

	IMPROVED_REFLEXES(21238, 135_020, 16, -1, 0.10, 448, 88, new int[] {10, 0, 0, 0, 0}),

	RAPID_RESTORE(21239, 135_022, 19, -1, 0.016, 451, 89),

	RAPID_HEAL(21240, 135_024, 22, -1, 0.03, 453, 90),

	PROTECT_ITEM(21241, 135_026, 25, -1, 0.03, 337, 91) {
		@Override
		public boolean getEffects(Player player) {
			return PrayerManager.checkProtectItem(player);
		}
	},

	HAWK_EYE(70084, 135_028, 26, -26, 0.10, 337, 603, new int[] {0, 0, 0, 10, 0}),

	MYSTIC_LORE(70086, 135_030, 27, -1, 0.10, 337, 604, new int[] {0, 0, 0, 0, 10}),

	STEEL_SKIN(21242, 135_032, 28, -1, 0.20, 439, 92, new int[] {0, 0, 15, 0, 0}),

	ULTIMATE_STRENGTH(21243, 135_034, 31, -1, 0.20, 450, 93, new int[] {0, 15, 0, 0, 0}),

	INCREDIBLE_REFLEXES(21244, 135_036, 34, -1, 0.20, 440, 94, new int[] {15, 0, 0, 0, 0}),

	PROTECT_FROM_SUMMONING(150104, 135_038, 35, 18, 0.20, 337, 612) {
		@Override
		public boolean getEffects(Player player) {
			return PrayerManager.checkProtectionPrayers(player);
		}
	},

	PROTECT_FROM_MAGIC(21245, 135_040, 37, 2, 0.20, 337, 95) {
		@Override
		public boolean getEffects(Player player) {
			return PrayerManager.checkProtectionPrayers(player);
		}
	},

	PROTECT_FROM_MISSILES(21246, 135_042, 40, 1, 0.20, 337, 96) {
		@Override
		public boolean getEffects(Player player) {
			return PrayerManager.checkProtectionPrayers(player);
		}
	},

	PROTECT_FROM_MELEE(21247, 135_044, 43, 0, 0.20, 337, 97) {
		@Override
		public boolean getEffects(Player player) {
			return PrayerManager.checkProtectionPrayers(player);
		}
	},

	EAGLE_EYE(70088, 135_046, 44, -1, 0.20, 337, 605, new int[] {0, 0, 0, 15, 0}),

	MYSTIC_EYE(70090, 135_048, 45, -1, 0.20, 337, 606, new int[] {0, 0, 0, 0, 15}),

	RETRIBUTION(2171, 135_050, 46, 3, 0.05, 337, 98),

	REDEMPTION(2172, 135_052, 49, 5, 0.10, 337, 99),

	SMITE(2173, 135_054, 52, 4, 0.3, 337, 100),

	CHIVALRY(70092, 135_056, 60, -1, 0.4, 337, 607, new int[] {15, 18, 20, 0, 0}) {
		@Override
		public boolean getEffects(Player player) {
			if (player.getCurrentCombatSkillLevel(ServerConstants.DEFENCE) < 65) {
				player.getDH().sendStatement("You need 65 defence to use this prayer.");
				return false;
			}
			return true;
		}
	},
	RAPID_RENEWAL(128162, 135_058, 65, -1, 0.2, 337, 1073) {
		@Override
		public boolean getEffects(Player player) {
			/*
			 * Hasn't unlocked rapid renewal
			 */
			if (player.getAttributes().getOrDefault(RapidRenewalScroll.SCROLL_UNLOCKED) == 0) {
				player.getDH().sendStatement("You do not have rapid renewal unlocked.");
				return false;
			}
			return true;
		}
	},
	PIETY(70094, 135_060, 70, -1, 0.4, 337, 608, new int[] {20, 23, 25, 0, 0}) {
		@Override
		public boolean getEffects(Player player) {
			/*
			 * Achievement completion
			 */
			Achievements.checkCompletionSingle(player, 1047);
			/*
			 * Low defence level
			 */
			if (player.getBaseDefenceLevel() < 70) {
				player.getDH().sendStatement("You need 70 defence to use this prayer.");
				return false;
			}
			return true;
		}
	},

	PRESERVE(0, 135_060, 55, -1, 0.05, 337, 609),

	RIGOUR(89178, 135_062, 74, -1, 0.4, 337, 610, new int[] {0, 0, 25, 20, 0}) {
		@Override
		public boolean getEffects(Player player) {
			/*
			 * Hasn't unlocked rigour
			 */
			if (!player.rigourUnlocked) {
				player.getDH().sendStatement("You do not have rigour unlocked.");
				return false;
			}
			/*
			 * Low defence level
			 */
			if (player.getBaseDefenceLevel() < 70) {
				player.getDH().sendStatement("You need 70 defence to use this prayer.");
				return false;
			}
			return true;
		}
	},

	AUGURY(89180, 135_064, 77, -1, 0.4, 337, 611, new int[] {25, 0, 0, 0, 25}) {
		@Override
		public boolean getEffects(Player player) {
			/*
			 * Hasn't unlocked augury
			 */
			if (!player.auguryUnlocked) {
				player.getDH().sendStatement("You do not have augury unlocked.");
				return false;
			}
			/*
			 * Low defence level
			 */
			if (player.getBaseDefenceLevel() < 70) {
				player.getDH().sendStatement("You need 70 defence to use this prayer.");
				return false;
			}
			return true;
		}
	},
	/**
	 * Curses start
	 */
	PROTECT_ITEM_CURSE(87231, 135_141, 50, -1, 0.3, 337, 624) {
		@Override
		public boolean getEffects(Player player) {
			if (PrayerManager.checkProtectItem(player)) {
				return false;
			}
			player.startAnimation(12_567);
			player.gfx0(2213);
			return true;
		}
	},

	SAP_WARRIOR(87233, 135_143, 50, -1, 2.5, 337, 625, new SapCombatPrayer(0), new int[] {}),

	SAP_RANGER(87235, 135_145, 52, -1, 2.5, 337, 626, new SapCombatPrayer(1), new int[] {}),

	SAP_MAGE(87237, 135_147, 54, -1, 2.5, 337, 627, new SapCombatPrayer(2), new int[] {}),

	SAP_SPIRIT(87239, 135_149, 56, -1, 2.5, 337, 628, new SapCombatPrayer(3), new int[] {}),

	BERSERKER(87241, 135_151, 59, -1, 0.3, 337, 629),

	DEFLECT_SUMMONING(87243, 135_153, 62, 12, 2, 337, 630, new DeflectCombatPrayer(0),
			new int[] {}) {
		@Override
		public boolean getEffects(Player player) {
			return PrayerManager.checkProtectionPrayers(player);
		}
	},

	DEFLECT_MAGIC(87245, 135_155, 65, 10, 2, 337, 631, new DeflectCombatPrayer(1), new int[] {}) {
		@Override
		public boolean getEffects(Player player) {
			return PrayerManager.checkProtectionPrayers(player);
		}
	},

	DEFLECT_MISSILES(87247, 135_157, 68, 11, 2, 337, 632, new DeflectCombatPrayer(2), new int[] {}) {
		@Override
		public boolean getEffects(Player player) {
			return PrayerManager.checkProtectionPrayers(player);
		}
	},


	DEFLECT_MELEE(87249, 135_159, 71, 9, 2, 337, 633, new DeflectCombatPrayer(3), new int[] {}) {
		@Override
		public boolean getEffects(Player player) {
			return PrayerManager.checkProtectionPrayers(player);
		}
	},

	LEECH_ATTACK(87251, 135_161, 74, -1, 1.6, 337, 634, new LeechCombatPrayer(0),
			new int[] {5, 0, 0, 0, 0}),

	LEECH_RANGED(87253, 135_163, 76, -1, 1.6, 337, 635, new LeechCombatPrayer(1),
			new int[] {0, 5, 0, 0, 0}),

	LEECH_MAGIC(87255, 135_165, 78, -1, 1.6, 337, 636, new LeechCombatPrayer(2),
			new int[] {0, 0, 5, 0, 0}),

	LEECH_DEFENCE(88001, 135_167, 80, -1, 1.6, 337, 637, new LeechCombatPrayer(3),
			new int[] {0, 0, 0, 5, 0}),

	LEECH_STRENGTH(88003, 135_169, 82, -1, 1.6, 337, 638, new LeechCombatPrayer(4),
			new int[] {0, 0, 0, 0, 5}),

	LEECH_ENERGY(88005, 135_171, 84, -1, 1.6, 337, 639, new LeechCombatPrayer(5), new int[] {}),

	LEECH_SPECIAL(88007, 135_173, 86, -1, 1.6, 337, 640, new LeechCombatPrayer(6), new int[] {}),

	WRATH(88009, 135_175, 89, 16, 0.5, 337, 641) {
		@Override
		public boolean getEffects(Player player) {
			player.startAnimation(12_583);
			return true;
		}
	},

	SOUL_SPLIT(88011, 135_177, 92, 17, 3, 337, 642, new SoulSplitCombatPrayer(), new int[] {}),

	TURMOIL(88013, 135_179, 95, -1, 3, 337, 643, new int[] {15, 23, 15, 0, 0}) {
		@Override
		public boolean getEffects(Player player) {
			player.startAnimation(12_565);
			player.gfx0(2226);
			return true;
		}
	},

	;

	/**
	 * The button
	 */
	private final int button;

	/**
	 * The button for quick prayer
	 */
	private final int quickButton;

	/**
	 * The level required
	 */
	private final int levelRequired;

	/**
	 * The headicon
	 */
	private final int headIcon;

	/**
	 * The drain
	 */
	private final double drain;

	/**
	 * The sound
	 */
	private final int sound;

	/**
	 * The config
	 */
	private final int config;

	/**
	 * The combat prayer
	 */
	private final CombatPrayer combatPrayer;

	/**
	 * Displaying the stat percentage
	 */
	private final int[] statDisplay;

	/**
	 * Represents a prayer
	 * 
	 * @param button the button
	 * @param quickButton the quick button
	 * @param levelRequired the level required
	 * @param headIcon the headicon
	 * @param drain the drain
	 * @param sound the sound
	 * @param config the config
	 * @param combatPrayer the combat prayer
	 * @param statDisplay the stat display
	 */
	Prayer(int button, int quickButton, int levelRequired, int headIcon, double drain, int sound,
			int config, CombatPrayer combatPrayer, int[] statDisplay) {
		this.quickButton = quickButton;
		this.button = button;
		this.levelRequired = levelRequired;
		this.headIcon = headIcon;
		this.drain = drain;
		this.sound = sound;
		this.config = config;
		this.combatPrayer = combatPrayer;
		this.statDisplay = statDisplay;
	}

	/**
	 * Represents a non combat prayer
	 * 
	 * @param button the button
	 * @param quickButton the quick button
	 * @param levelRequired the level required
	 * @param headIcon the headicon
	 * @param drain the drain
	 * @param sound the sound
	 * @param config the config
	 */
	Prayer(int button, int quickButton, int levelRequired, int headIcon, double drain, int sound,
			int config) {
		this(button, quickButton, levelRequired, headIcon, drain, sound, config, null, null);
	}

	/**
	 * Represents a non combat prayer
	 * 
	 * @param button the button
	 * @param quickButton the quick button
	 * @param levelRequired the level required
	 * @param headIcon the headicon
	 * @param drain the drain
	 * @param sound the sound
	 * @param config the config
	 * @param turnOff the turn off
	 * @param statDisplay the stat display
	 */
	Prayer(int button, int quickButton, int levelRequired, int headIcon, double drain, int sound,
			int config, int[] statDisplay) {
		this(button, quickButton, levelRequired, headIcon, drain, sound, config, null, statDisplay);
	}

	/**
	 * Gets the button
	 *
	 * @return the button
	 */
	public int getButton() {
		return button;
	}

	/**
	 * Gets the quickButton
	 *
	 * @return the quickButton
	 */
	public int getQuickButton() {
		return quickButton;
	}

	/**
	 * Gets the levelRequired
	 *
	 * @return the levelRequired
	 */
	public int getLevelRequired() {
		return levelRequired;
	}

	/**
	 * Gets the headIcon
	 *
	 * @return the headIcon
	 */
	public int getHeadIcon() {
		return headIcon;
	}

	/**
	 * Gets the drain
	 *
	 * @return the drain
	 */
	public double getDrain() {
		return drain;
	}

	/**
	 * Sets the sound
	 *
	 * @return the sound
	 */
	public int getSound() {
		return sound;
	}

	/**
	 * Sets the config
	 *
	 * @return the config
	 */
	public int getConfig() {
		return config;
	}

	/**
	 * Sets the combatPrayer
	 *
	 * @return the combatPrayer
	 */
	public CombatPrayer getCombatPrayer() {
		return combatPrayer;
	}

	/**
	 * Sets the statDisplay
	 *
	 * @return the statDisplay
	 */
	public int[] getStatDisplay() {
		return statDisplay;
	}

	/**
	 * Gets the turn on effects
	 * 
	 * @param player the player
	 */
	public boolean getEffects(Player player) {
		return true;
	}

	/**
	 * Checks whether curse prayer
	 * 
	 * @return the name
	 */
	public boolean isCurse() {
		return ordinal() >= PROTECT_ITEM_CURSE.ordinal();
	}

	/**
	 * Gets the quick prayer config
	 * 
	 * @return the config
	 */
	public int getQuickPrayerConfig() {
		return (isCurse() ? PrayerManager.CURSE_CONFIG_START : PrayerManager.REGULAR_CONFIG_START)
				+ ordinal();
	}

	/**
	 * Gets the name
	 * 
	 * @return the name
	 */
	public String getName() {
		return Misc.ucFirst(name().toLowerCase().replaceAll("_", " "));
	}

	/**
	 * Gets the prayer by button
	 * 
	 * @param button the button
	 * @return the prayer
	 */
	public static Prayer forButton(int button) {
		return Arrays.stream(values()).filter(c -> c.getButton() == button).findFirst().orElse(null);
	}

	/**
	 * Gets the prayer by quick button
	 * 
	 * @param button the quick button
	 * @return the prayer
	 */
	public static Prayer forQuickButton(int button) {
		return Arrays.stream(values()).filter(c -> c.getQuickButton() == button).findFirst()
				.orElse(null);
	}
}
