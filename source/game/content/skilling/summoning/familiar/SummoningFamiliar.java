package game.content.skilling.summoning.familiar;

import java.util.Arrays;
import game.content.skilling.summoning.familiar.special.SummoningFamiliarSpecialAbility;
import game.content.skilling.summoning.familiar.special.impl.BoBSummoningFamiliarSpecialAbility;
import game.content.skilling.summoning.familiar.special.impl.SteelTitanSummoningFamiliarSpecialAbility;
import game.content.skilling.summoning.familiar.special.impl.SummoningFamiliarHealerSpecialAbility;
import game.content.skilling.summoning.familiar.special.impl.SummoningFamiliarHealerSpecialAbility.HealingType;
import game.content.skilling.summoning.familiar.special.impl.WolpertingerSummoningFamiliarSpecialAbility;

/**
 * Handles the summoning familiars
 *
 * @author 2012
 */
public enum SummoningFamiliar {

	VOID_SPINNER(7333, 12780, 34, 27, 1, 4, 12443,
			new SummoningFamiliarHealerSpecialAbility(1, HealingType.CYCLE)),

	BUNYIP(6813, 12029, 68, 44, 1, 7, 12438,
			new SummoningFamiliarHealerSpecialAbility(2, HealingType.CYCLE)),

	UNICORN_STALLION(6822, 12039, 88, 54, 2, 9, 12434,
			new SummoningFamiliarHealerSpecialAbility(0, HealingType.PERCENTANGE)),

	STEEL_TITAN(7343, 12790, 99, 64, 10, 5, 12825, new SteelTitanSummoningFamiliarSpecialAbility()),

	PACK_YAK(6873, 12093, 96, 58, 10, 5, 12435, BoBSummoningFamiliarSpecialAbility.getPackYak()),

	WOLPERTINGER(6869, 12089, 92, 62, 10, 5, 12437,
			new WolpertingerSummoningFamiliarSpecialAbility()),

	;

	/**
	 * The npc id
	 */
	private int npc;

	/**
	 * The pouch
	 */
	private int pouch;

	/**
	 * The level required
	 */
	private int levelRequired;

	/**
	 * The duration
	 */
	private int duration;

	/**
	 * The summoning points cost
	 */
	private int cost;

	/**
	 * The experienced received for using the pouch
	 */
	private double useExperience;

	/**
	 * The scroll
	 */
	private int scroll;

	/**
	 * The special ability
	 */
	private SummoningFamiliarSpecialAbility summoningFamiliarSpecialAbility;

	/**
	 * Represents a summoning familiar
	 *
	 * @param npc the npc
	 * @param pouch the pouch
	 * @param leveRequired the level required
	 * @param duration the duration
	 * @param summoningFamiliarSpecialAbility the special ability
	 */
	SummoningFamiliar(int npc, int pouch, int leveRequired, int duration, int cost,
			double useExperience, int scroll,
			SummoningFamiliarSpecialAbility summoningFamiliarSpecialAbility) {
		this.npc = npc;
		this.pouch = pouch;
		this.levelRequired = leveRequired;
		this.duration = duration;
		this.cost = cost;
		this.useExperience = useExperience;
		this.scroll = scroll;
		this.summoningFamiliarSpecialAbility = summoningFamiliarSpecialAbility;
	}

	/**
	 * Gets the npc
	 *
	 * @return the npc
	 */
	public int getNpc() {
		return npc;
	}

	/**
	 * Gets the pouch
	 *
	 * @return the pouch
	 */
	public int getPouch() {
		return pouch;
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
	 * Gets the duration
	 *
	 * @return the duration
	 */
	public int getDuration() {
		return duration;
	}

	/**
	 * Gets the cost
	 *
	 * @return the cost
	 */
	public int getCost() {
		return cost;
	}

	/**
	 * Gets the useExperience
	 *
	 * @return the useExperience
	 */
	public double getUseExperience() {
		return useExperience;
	}

	/**
	 * Gets the scroll
	 *
	 * @return the scroll
	 */
	public int getScroll() {
		return scroll;
	}

	/**
	 * Gets the summoningFamiliarSpecialAbility
	 *
	 * @return the summoningFamiliarSpecialAbility
	 */
	public SummoningFamiliarSpecialAbility getSummoningFamiliarSpecialAbility() {
		return summoningFamiliarSpecialAbility;
	}

	/**
	 * Checking whether is bob
	 * 
	 * @return is bob
	 */
	public boolean isBoB() {
		return summoningFamiliarSpecialAbility instanceof BoBSummoningFamiliarSpecialAbility;
	}

	/**
	 * Gets the Bob storage
	 * 
	 * @return the storage
	 */
	public BoBSummoningFamiliarSpecialAbility getBoB() {
		return (BoBSummoningFamiliarSpecialAbility) getSummoningFamiliarSpecialAbility();
	}

	/**
	 * Gets the familiar by pouch
	 *
	 * @param pouch the pouch
	 * @return the familiar
	 */
	public static SummoningFamiliar forPouch(int pouch) {
		return Arrays.stream(values()).filter(c -> c.getPouch() == pouch).findFirst().orElse(null);
	}

	/**
	 * Gets the familiar by id
	 *
	 * @param id the id
	 * @return the familiar
	 */
	public static SummoningFamiliar forId(int id) {
		return Arrays.stream(values()).filter(c -> c.getNpc() == id).findFirst().orElse(null);
	}
}
