package game.content.minigame;

import game.npc.Npc;
import game.player.Boundary;
import game.player.Player;
import game.player.event.CycleEventHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * Created by Jason MacKeigan on 2018-01-24 at 9:44 AM
 */
public abstract class Minigame {

	/**
	 * The key that is unique to this minigame.
	 */
	private final MinigameKey key;

	/**
	 * The mapping of keys to minigame areas for this minigame.
	 */
	private final Map<MinigameAreaKey, MinigameArea> areas = new HashMap<>();

	/**
	 * The cycle event handler for cycle events.
	 */
	private final CycleEventHandler<Object> eventHandler = new CycleEventHandler<>();

	/**
	 * Creates a new minigame for the given key.
	 *
	 * @param key the key for this minigame.
	 */
	public Minigame(MinigameKey key) {
		this.key = key;
	}

	/**
	 * Determines if the player can be added to this minigame.
	 *
	 * @param player the player being added.
	 * @return whether or not the player can be added.
	 */
	public abstract boolean isAddable(Player player);

	/**
	 * Determines if an npc can be added to the minigame.
	 *
	 * @param npc the npc being added.
	 * @return whether or not the npc can be added.
	 */
	public abstract boolean isAddable(Npc npc);

	/**
	 * Referenced when a player is successfully added to the minigame.
	 *
	 * @param player the player being added.
	 */
	public abstract void onAdd(Player player);

	/**
	 * Referenced when an npc is successfully added to the minigame.
	 *
	 * @param npc the npc being added
	 */
	public abstract void onAdd(Npc npc);

	/**
	 * Referenced when a player is removed from the minigame.
	 *
	 * @param player the player being removed.
	 */
	public abstract void onRemove(Player player);

	/**
	 * Referenced when a npc is removed from the minigame.
	 *
	 * @param npc the npc being removed.
	 */
	public abstract void onRemove(Npc npc);

	/**
	 * Referenced when a player logs into the game.
	 *
	 * @param player the player logging in.
	 */
	public abstract void onLogin(Player player);

	/**
	 * Referenced when a player logs out of the game.
	 *
	 * @param player the player logging out.
	 */
	public abstract void onLogout(Player player);

	/**
	 * Referenced during the death step.
	 *
	 * @param player the player dying or have died.
	 */
	public void onDeath(Player player) {
		forPlayerInArea(player, (p, a) -> a.onDeath(p));
	}

	/**
	 * Referenced when a player is outside of the natural bounds setup by the minigame.
	 *
	 * @param player the player out of bounds.
	 */
	public void onOutsideBounds(Player player) {
		forPlayerInArea(player, (p, a) -> a.onOutsideBounds(p));
	}

	/**
	 * Referenced when the player can sucessfully teleport during a minigame (not necessarily outside).
	 *
	 * @param player the player teleporting.
	 */
	public void onTeleport(Player player) {

	}

	/**
	 * Referenced when walkable interfaces should be updated.
	 *
	 * @param player the player having their walkable interfaces updated.
	 */
	public abstract void onUpdateWalkableInterface(Player player);

	/**
	 * Referenced to start or initiate the minigame.
	 */
	public abstract void start();

	/**
	 * Referenced to effectively end the minigame.
	 */
	public abstract void end();

	/**
	 * The maximum level difference allowed between two players in combat.
	 *
	 * @param attacker the player starting the attack.
	 * @param defender the player defending the attack.
	 * @return the maximum allowed level difference between the two players to initiate combat.
	 */
	public abstract int maximumLevelDifference(Player attacker, Player defender);

	/**
	 * Determines whether or not a player can teleport in a minigame.
	 *
	 * @param player the player attempting to teleport.
	 * @return whether or not the player can teleport.
	 */
	public abstract boolean canTeleport(Player player);

	/**
	 * Determines if the player is in any of the areas by doing a simple boundary check on any of them.
	 *
	 * @param player the player being determined if they're on the inside of the boundaries or not.
	 * @return whether or not the player is inside any of the minigame.
	 */
	public boolean inside(Player player) {
		return areas.values().stream().anyMatch(area -> Boundary.isIn(player, area.getBoundary()));
	}

	/**
	 * Referenced when the player logs out of the game.
	 *
	 * @param player the player logging out.
	 */
	public void logout(Player player) {
		if (!inside(player)) {
			return;
		}
		removePlayer(player);

		onLogout(player);
	}

	/**
	 * Executes a biconsumer if a player participant exists for given player as well as an area.
	 *
	 * @param player the player we're doing this for.
	 * @param consumer the action performed if the player participant exists and the area.
	 */
	private void forPlayerInArea(Player player, BiConsumer<MinigamePlayerParticipant, MinigameArea> consumer) {
		MinigameArea area = getAreaOrNull(player);

		if (area == null) {
			return;
		}
		MinigamePlayerParticipant playerParticipant = area.getPlayerParticipant(player);

		if (playerParticipant == null) {
			return;
		}
		consumer.accept(playerParticipant, area);
	}

	/**
	 * Adds an area to the mapping of possible areas in this minigame.
	 *
	 * @param key the key for the unique area.
	 * @param area the area for the unique key.
	 * @return whether or not an area was added, false if it already exists based on key.
	 */
	protected boolean addArea(MinigameAreaKey key, MinigameArea area) {
		if (areas.containsKey(key)) {
			return false;
		}
		areas.put(key, area);

		return true;
	}

	/**
	 * Attempts to remove an area from the mapping based on the key.
	 *
	 * @param key the key that is to be removed.
	 * @return {@code true} if an area could be removed.
	 */
	public boolean removeArea(MinigameAreaKey key) {
		if (!areas.containsKey(key)) {
			return false;
		}
		areas.remove(key);

		return true;
	}

	/**
	 * Attempts to get any area from the mapping if the player reference exists in the map using a simple
	 * {@link MinigameArea#getPlayerParticipant(Player)} call.
	 *
	 * @param player the player we're using to get the area for.
	 * @return the area with the player in it, or null if none can be found.
	 */
	public MinigameArea getAreaOrNull(Player player) {
		return areas.entrySet().stream().filter(entry -> entry.getValue().getPlayerParticipant(player) != null).map(
				Map.Entry::getValue).findAny().orElse(null);
	}

	/**
	 * Attempts to retrieve an area for the given key.
	 *
	 * @param key the key to the minigame area.
	 * @return the area for the key, or null if none can be found.
	 */
	private MinigameArea getAreaOrNull(MinigameAreaKey key) {
		return areas.get(key);
	}

	/**
	 * Adds a player to the minigame and assigns the players minigame to this one.
	 *
	 * @param player the player being added.
	 * @param key the area the players being added to.
	 * @return whether or not the player was added.
	 */
	public boolean addPlayer(Player player, MinigameAreaKey key) {
		MinigameArea area = getAreaOrNull(key);

		if (area == null) {
			return false;
		}
		Minigame currentMinigame = player.getMinigame();

		if (currentMinigame != null) {
			if (currentMinigame.removePlayer(player)) {

			}
		}
		MinigamePlayerParticipant playerParticipant = new MinigamePlayerParticipant(player);

		if (isAddable(player) && area.add(playerParticipant)) {
			player.setMinigame(this);
			onAdd(player);

			return true;
		}
		return false;
	}

	/**
	 * Retrieves a player participant for the given player.
	 *
	 * @param player the player that exists in the minigame.
	 * @return the participant, or null if none can be found.
	 */
	public MinigamePlayerParticipant getPlayerParticipant(Player player) {
		return areas.values().stream().map(area -> area.getPlayerParticipant(player)).filter(Objects::nonNull).findAny().orElse(null);
	}

	/**
	 * Retrieves an npc participant for the given npc.
	 *
	 * @param npc the npc that exists in the minigame.
	 * @return the participant, or null if none can be found.
	 */
	public MinigameNpcParticipant getNpcParticipant(Npc npc) {
		return areas.values().stream().map(area -> area.getNpcParticipant(npc)).filter(Objects::nonNull).findAny().orElse(null);
	}

	/**
	 * Attempts to remove a player from the minigame, setting the value of minigame for the player to null.
	 *
	 * @param player the player being removed.
	 * @return {@code true} if the player could be removed.
	 */
	public boolean removePlayer(Player player) {
		MinigamePlayerParticipant playerParticipant = getPlayerParticipant(player);

		if (playerParticipant == null) {
			return false;
		}
		MinigameArea area = areas.values().stream().filter(a -> a.contains(playerParticipant)).findAny().orElse(null);

		if (area == null) {
			return false;
		}
		if (area.remove(playerParticipant)) {
			if (player.getMinigame() == this) {
				player.setMinigame(null);
			}
			onRemove(player);

			return true;
		}
		return false;
	}

	/**
	 * Attempts to add an npc to the minigame.
	 *
	 * @param npcParticipant the npc being added.
	 * @param key the area the npc is being added to.
	 * @return {@code true} if an npc could be added.
	 */
	public boolean addNpc(MinigameNpcParticipant npcParticipant, MinigameAreaKey key) {
		MinigameArea area = getAreaOrNull(key);

		if (area == null) {
			return false;
		}
		Npc npc = npcParticipant.getEntity();

		if (isAddable(npc) && area.add(npcParticipant)) {
			onAdd(npc);

			return true;
		}
		return false;
	}

	/**
	 * Attempts to remove an npc.
	 *
	 * @param npc the npc we're attempting to remove.
	 * @param key the key of the area the npc is in.
	 * @return {@code true} if the npc was removed.
	 */
	public boolean removeNpc(Npc npc, MinigameAreaKey key) {
		MinigameArea area = getAreaOrNull(key);

		if (area == null) {
			return false;
		}
		MinigameNpcParticipant npcParticipant = area.getNpcParticipant(npc);

		if (npcParticipant == null) {
			return false;
		}
		if (area.remove(npcParticipant)) {
			onRemove(npc);

			return true;
		}
		return false;
	}

	/**
	 * Clears the underlying mapping of all players.
	 */
	public void clearPlayers() {
		areas.values().forEach(MinigameArea::clearPlayerParticipants);
	}

	/**
	 * Clears the underlying mapping of all npcs.
	 */
	public void clearNpcs() {
		areas.values().forEach(MinigameArea::clearNpcParticipants);
	}

	/**
	 * Retrieves the specific key for this minigame.
	 *
	 * @return the unique key for this minigame.
	 */
	public MinigameKey getKey() {
		return key;
	}

	/**
	 * Retrieves the cycle event handler that manages cycle events for this minigame.
	 *
	 * @return the event handler.
	 */
	public CycleEventHandler<Object> getEventHandler() {
		return eventHandler;
	}

	@Override
	public String toString() {
		return "Minigame{" +
		       "key=" + key +
		       ", areas=" + areas +
		       '}';
	}
}
