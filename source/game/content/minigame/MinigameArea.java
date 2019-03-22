package game.content.minigame;

import game.npc.Npc;
import game.player.Boundary;
import game.player.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Created by Jason MacKeigan on 2018-01-30 at 2:08 PM
 * <p>
 * A unique area within a minigame that holds some number of participants.
 */
public class MinigameArea {

	/**
	 * The unique key for this area.
	 */
	private final MinigameAreaKey key;

	/**
	 * The boundary that represents this area on the map.
	 */
	private final Boundary boundary;

	/**
	 * A set of players in this area.
	 */
	private final Set<MinigamePlayerParticipant> playerParticipants;

	/**
	 * A set of npcs in this area.
	 */
	private final Set<MinigameNpcParticipant> npcParticipants;

	/**
	 * Determines if pvp combat is safe or not.
	 */
	private final MinigameAreaCombatSafety combatSafety;

	/**
	 * Determines if items are dropped on death or not.
	 */
	private final MinigameAreaDeathSafety deathSafety;

	/**
	 * Creates a new area for the given key.
	 *
	 * @param key the key to the area.
	 * @param boundary the boundary for the area.
	 * @param combatSafety determines if players can be attacked by others.
	 * @param deathSafety determines if items are dropped on death.
	 * @param playerParticipants the set of players.
	 * @param npcParticipants the set of npcs.
	 */
	public MinigameArea(MinigameAreaKey key, Boundary boundary,
	                    MinigameAreaCombatSafety combatSafety, MinigameAreaDeathSafety deathSafety, Set<MinigamePlayerParticipant> playerParticipants,
	                    Set<MinigameNpcParticipant> npcParticipants) {
		this.key = key;
		this.boundary = boundary;
		this.combatSafety = combatSafety;
		this.deathSafety = deathSafety;
		this.playerParticipants = playerParticipants;
		this.npcParticipants = npcParticipants;
	}

	/**
	 * Creates a new area for the given key.
	 *
	 * @param key the key to the area.
	 * @param boundary the boundary for the area.
	 * @param combatSafety determines if players can be attacked by others.
	 * @param deathSafety determines if items are dropped on death.
	 */
	public MinigameArea(MinigameAreaKey key, MinigameAreaCombatSafety combatSafety, MinigameAreaDeathSafety deathSafety, Boundary boundary) {
		this(key, boundary, combatSafety, deathSafety, new HashSet<>(), new HashSet<>());
	}

	/**
	 * Adds a player to the area if the player does not already exist.
	 *
	 * @param playerParticipant the new participant being added.
	 * @return {@code true} if added, otherwise false.
	 */
	public boolean add(MinigamePlayerParticipant playerParticipant) {
		if (isAddable(playerParticipant) && playerParticipants.add(playerParticipant)) {
			onAdd(playerParticipant);

			return true;
		}

		return false;
	}

	/**
	 * Removes a player from the area if the player exists.
	 *
	 * @param playerParticipant the player to be removed.
	 * @return {@code true} if the player was removed.
	 */
	public boolean remove(MinigamePlayerParticipant playerParticipant) {
		if (playerParticipants.remove(playerParticipant)) {
			onRemove(playerParticipant);

			return true;
		}

		return false;
	}

	/**
	 * Performs a simple for-each on the player participants for the given consumer.
	 *
	 * @param playerParticipantConsumer the action being performed on all players.
	 */
	public void forEachPlayerParticipant(Consumer<MinigamePlayerParticipant> playerParticipantConsumer) {
		playerParticipants.forEach(playerParticipantConsumer);
	}

	/**
	 * Retrieves a participant based on the predicated provided.
	 *
	 * @param predicate the predicate that the player must match to be retrieved.
	 * @return the participant, or null if none.
	 */
	public MinigamePlayerParticipant getPlayerParticipant(Predicate<MinigamePlayerParticipant> predicate) {
		return playerParticipants.stream().filter(predicate).findAny().orElse(null);
	}

	/**
	 * Retrieves the participant if the underlying entity matches the given parameter.
	 *
	 * @param player the player that the entity must match.
	 * @return the participant for the player, or null if none is found.
	 */
	public MinigamePlayerParticipant getPlayerParticipant(Player player) {
		return playerParticipants.stream().filter(playerParticipant -> playerParticipant.getEntity() == player).findAny().orElse(null);
	}

	/**
	 * Attempts to retrieve any player participant from the set of participants.
	 *
	 * @return any participant, or null.
	 */
	public MinigamePlayerParticipant getAnyPlayerParticipant() {
		return playerParticipants.stream().limit(1).findAny().orElse(null);
	}

	/**
	 * Determines if the set of player participants contains the provided participant.
	 *
	 * @param playerParticipant the player participant.
	 * @return {@code true} if the participant exists.
	 */
	public boolean contains(MinigamePlayerParticipant playerParticipant) {
		return playerParticipants.contains(playerParticipant);
	}

	/**
	 * The number of players in this area.
	 *
	 * @return the number of players.
	 */
	public int amountOfPlayers() {
		return playerParticipants.size();
	}

	/**
	 * Performs an action on each of the npc participants.
	 *
	 * @param npcParticipantConsumer the action being performed.
	 */
	public void forEachNpcParticipant(Consumer<MinigameNpcParticipant> npcParticipantConsumer) {
		npcParticipants.forEach(npcParticipantConsumer);
	}

	/**
	 * Retrieves the npc for the given predicate.
	 *
	 * @param predicate the predicate for the npc.
	 * @return the npc or null if none can be found.
	 */
	public MinigameNpcParticipant getNpcParticipant(Predicate<MinigameNpcParticipant> predicate) {
		return npcParticipants.stream().filter(predicate).findAny().orElse(null);
	}

	/**
	 * Retrieves the npc participant for any entity that matches the entity provided.
	 *
	 * @param npc the entity provided.
	 * @return a participant that matches the parameter, or null.
	 */
	public MinigameNpcParticipant getNpcParticipant(Npc npc) {
		return npcParticipants.stream().filter(npcParticipant -> npcParticipant.getEntity() == npc).findAny().orElse(null);
	}

	/**
	 * Attempts to retrieve any npc participant.
	 *
	 * @return any npc, or null if none exist.
	 */
	public MinigameNpcParticipant getAnyNpcParticipant() {
		return npcParticipants.stream().limit(1).findAny().orElse(null);
	}

	/**
	 * Attempts to add an npc participant if it does not already exist.
	 *
	 * @param minigameNpcParticipant the participant being added.
	 * @return {@code true} if the participant could be added.
	 */
	public boolean add(MinigameNpcParticipant minigameNpcParticipant) {
		if (isAddable(minigameNpcParticipant) && npcParticipants.add(minigameNpcParticipant)) {
			onAdd(minigameNpcParticipant);

			return true;
		}

		return false;
	}

	/**
	 * Attempts to remove an npc participant if it exists.
	 *
	 * @param minigameNpcParticipant the participant to be removed.
	 * @return {@code true} if the participant existed and could be removed.
	 */
	public boolean remove(MinigameNpcParticipant minigameNpcParticipant) {
		if (npcParticipants.remove(minigameNpcParticipant)) {
			onRemove(minigameNpcParticipant);

			return true;
		}

		return false;
	}

	/**
	 * Determines if the participant exists.
	 *
	 * @param npcParticipant the participant
	 * @return {@code true} if the participant exists.
	 */
	public boolean contains(MinigameNpcParticipant npcParticipant) {
		return npcParticipants.contains(npcParticipant);
	}

	/**
	 * The total number of npc participants.
	 *
	 * @return the total number of npcs.
	 */
	public int amountOfNpcs() {
		return npcParticipants.size();
	}

	/**
	 * Determines if any or a specific player can be added, by default true.
	 *
	 * @param playerParticipant the player attempted to be added.
	 * @return {@code true} by default.
	 */
	public boolean isAddable(MinigamePlayerParticipant playerParticipant) {
		return true;
	}

	/**
	 * Determines if any or a specific npc can be added, by default true.
	 *
	 * @param npcParticipant the npc attempted to be added.
	 * @return {@code true} by default.
	 */
	public boolean isAddable(MinigameNpcParticipant npcParticipant) {
		return true;
	}

	/**
	 * Referenced when a player is added to an area.
	 *
	 * @param playerParticipant the player being added.
	 */
	public void onAdd(MinigamePlayerParticipant playerParticipant) {

	}

	/**
	 * Referenced when an npc is added to an area.
	 *
	 * @param npcParticipant the npc being added.
	 */
	public void onAdd(MinigameNpcParticipant npcParticipant) {

	}

	/**
	 * Referenced when a player is removed.
	 *
	 * @param playerParticipant the player being removed.
	 */
	public void onRemove(MinigamePlayerParticipant playerParticipant) {

	}

	/**
	 * Referenced when an npc is removed.
	 *
	 * @param npcParticipant the npc being removed.
	 */
	public void onRemove(MinigameNpcParticipant npcParticipant) {

	}

	/**
	 * Referenced when the player is cleared from the list.
	 *
	 * @param playerParticipant the player being cleared.
	 */
	public void onClear(MinigamePlayerParticipant playerParticipant) {

	}

	/**
	 * Referenced when the npc is being cleared from the list.
	 *
	 * @param npcParticipant the npc being cleared.
	 */
	public void onClear(MinigameNpcParticipant npcParticipant) {

	}

	/**
	 * Clears the set of npc participants.
	 */
	public final void clearNpcParticipants() {
		npcParticipants.forEach(this::onClear);
		npcParticipants.clear();
	}

	/**
	 * Clears the set of player participants.
	 */
	public final void clearPlayerParticipants() {
		playerParticipants.forEach(this::onClear);
		playerParticipants.clear();
	}

	/**
	 * Referenced when a player participant dies.
	 *
	 * @param playerParticipant the player that died.
	 */
	public void onDeath(MinigamePlayerParticipant playerParticipant) {

	}

	/**
	 * Referenced when a player enters the area.
	 *
	 * @param playerParticipant the player entering the area.
	 */
	public void onEnter(MinigamePlayerParticipant playerParticipant) {

	}

	/**
	 * Referenced when a player leaves the area.
	 *
	 * @param playerParticipant the player leaving the area.
	 */
	public void onLeave(MinigamePlayerParticipant playerParticipant) {

	}

	/**
	 * Referenced when the player is outside of bounds from the area.
	 *
	 * @param playerParticipant the player going out of bounds.
	 */
	public void onOutsideBounds(MinigamePlayerParticipant playerParticipant) {

	}

	/**
	 * Referenced only when {@link #managesItemsForUnsafeDeath()} returns true.
	 *
	 * @param playerParticipant
	 *            the participant.
	 */
	public void onManageItemsForUnsafeDeath(MinigamePlayerParticipant playerParticipant) {

	}

	/**
	 * Determines whether or not the items removal portion of an unsafe death is managed by
	 * this minigame area.
	 *
	 * @return by default false.
	 */
	public boolean managesItemsForUnsafeDeath() {
		return false;
	}

	/**
	 * The key for this area.
	 *
	 * @return the key for the area.
	 */
	public MinigameAreaKey getKey() {
		return key;
	}

	/**
	 * Determines whether or not players can attack each other.
	 *
	 * @return the safety of the minigame.
	 */
	public MinigameAreaCombatSafety getCombatSafety() {
		return combatSafety;
	}

	/**
	 * Determines whether or not players lose items on death.
	 *
	 * @return the death safety of the minigame.
	 */
	public MinigameAreaDeathSafety getDeathSafety() {
		return deathSafety;
	}

	/**
	 * The boundary for the area.
	 *
	 * @return the boundary.
	 */
	public Boundary getBoundary() {
		return boundary;
	}

	@Override
	public String toString() {
		return "MinigameArea{" +
		       "playerParticipants=" + playerParticipants +
		       ", npcParticipants=" + npcParticipants +
		       '}';
	}
}
