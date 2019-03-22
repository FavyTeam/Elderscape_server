package game.npc.combat;

import game.npc.Npc;
import game.player.Player;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class Damage {

	private final int fixedDamage;

	private final int damageType;

	private final int npcIndex;

	private final int playerIndex;

	private final int maximumDamage;

	private final String playerName;

	private final Predicate<Player> requirementOrNull;

	private final BiConsumer<Integer, Player> onSuccessfulHit;

	private final BiConsumer<Integer, Player> onHitRegardless;

	private int hitsplatDelay;

	public Damage(Player player, Npc npc, int damageType, int hitsplatDelay, int maximumDamage) {
		this(player, npc, damageType, hitsplatDelay, maximumDamage, -1, null);
	}

	public Damage(Player player, Npc npc, int damageType, int hitsplatDelay, int maximumDamage, int fixedDamage) {
		this(player, npc, damageType, hitsplatDelay, maximumDamage, fixedDamage, null);
	}

	public Damage(Player player, Npc npc, int damageType, int hitsplatDelay, int maximumDamage, int fixedDamage, Predicate<Player> requirementOrNull) {
		this(player, npc, damageType, hitsplatDelay, maximumDamage, fixedDamage, requirementOrNull, null);
	}

	public Damage(Player player, Npc npc, int damageType, int hitsplatDelay, int maximumDamage, int fixedDamage, Predicate<Player> requirementOrNull,
	              BiConsumer<Integer, Player> onSuccessfulHit) {
		this(player, npc, damageType, hitsplatDelay, maximumDamage, fixedDamage, requirementOrNull, onSuccessfulHit, null);
	}

	public Damage(Player player, Npc npc, int damageType, int hitsplatDelay, int maximumDamage, int fixedDamage, Predicate<Player> requirementOrNull,
				  BiConsumer<Integer, Player> onSuccessfulHit, BiConsumer<Integer, Player> onHitRegardless) {
		this.playerIndex = player.getPlayerId();
		this.playerName = player.getPlayerName();
		this.npcIndex = npc.npcIndex;
		this.fixedDamage = fixedDamage;
		this.damageType = damageType;
		this.hitsplatDelay = hitsplatDelay;
		this.maximumDamage = maximumDamage;
		this.requirementOrNull = requirementOrNull;
		this.onSuccessfulHit = onSuccessfulHit;
		this.onHitRegardless = onHitRegardless;
	}

	public int getPlayerIndex() {
		return playerIndex;
	}

	public String getPlayerName() {
		return playerName;
	}

	public int getFixedDamage() {
		return fixedDamage;
	}

	public int getDamageType() {
		return damageType;
	}

	public int getHitsplatDelay() {
		return hitsplatDelay;
	}

	public int getTarget() {
		return npcIndex;
	}

	public void decreaseHitsplatDelay() {
		hitsplatDelay--;
	}

	public int getMaximumDamage() {
		return maximumDamage;
	}

	public Predicate<Player> getRequirementOrNull() {
		return requirementOrNull;
	}

	public BiConsumer<Integer, Player> getOnSuccessfulHit() {
		return onSuccessfulHit;
	}

	public BiConsumer<Integer, Player> getOnHitRegardless() {
		return onHitRegardless;
	}
}
