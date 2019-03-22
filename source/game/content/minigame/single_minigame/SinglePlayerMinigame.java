package game.content.minigame.single_minigame;

import game.content.minigame.Minigame;
import game.content.minigame.MinigameAreaKey;
import game.content.minigame.MinigameKey;
import game.player.Player;

/**
 * Created by Jason MacKeigan on 2018-04-04 at 12:40 PM
 */
public abstract class SinglePlayerMinigame extends Minigame {

	/**
	 * The first and only player allowed in this minigame.
	 */
	private final long player;

	/**
	 * Creates a new minigame for the given key.
	 *
	 * @param key the key for this minigame.
	 */
	public SinglePlayerMinigame(MinigameKey key, Player player, MinigameAreaKey areaKey) {
		super(key);
		this.player = player.getNameAsLong();
	}

	/**
	 * Adds a player to the minigame and assigns the players minigame to this one.
	 *
	 * @param player the player being added.
	 * @param key the area the players being added to.
	 * @return whether or not the player was added.
	 */
	@Override
	public boolean addPlayer(Player player, MinigameAreaKey key) {
		if (this.player == player.getNameAsLong()) {
			return super.addPlayer(player, key);
		}
		return false;
	}

	/**
	 * Attempts to remove a player from the minigame, setting the value of minigame for the player to null.
	 *
	 * @param player the player being removed.
	 * @return {@code true} if the player could be removed.
	 */
	@Override
	public boolean removePlayer(Player player) {
		if (this.player == player.getNameAsLong()) {
			return super.removePlayer(player);
		}
		return false;
	}

	/**
	 * Determines if the player can be added to this minigame.
	 *
	 * @param player the player being added.
	 * @return whether or not the player can be added.
	 */
	@Override
	public boolean isAddable(Player player) {
		return this.player == player.getNameAsLong();
	}
}
