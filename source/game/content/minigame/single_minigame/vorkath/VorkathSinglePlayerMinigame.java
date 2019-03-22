package game.content.minigame.single_minigame.vorkath;

import game.position.Position;
import game.content.minigame.MinigameArea;
import game.content.minigame.MinigameAreaKey;
import game.content.minigame.MinigameKey;
import game.content.minigame.MinigamePlayerParticipant;
import game.content.minigame.height_manager.MinigameKeyHeightManager;
import game.content.minigame.single_minigame.SinglePlayerMinigame;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.npc.impl.vorkath.Vorkath;
import game.player.Player;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Jason MacKeigan on 2018-06-14 at 10:14 AM
 */
public class VorkathSinglePlayerMinigame extends SinglePlayerMinigame {

	private final MinigameArea area;

	private final Vorkath vorkath;

	/**
	 *
	 * @param player
	 *            the player this minigame is for.
	 * @throws IllegalStateException
	 *            thrown only if no npc can be created for Vorkath.
	 */
	public VorkathSinglePlayerMinigame(Player player, int height) throws IllegalStateException {
		super(MinigameKey.VORKATH, player, MinigameAreaKey.VORKATH);

		area = new VorkathMinigameArea(height);

		addArea(MinigameAreaKey.VORKATH, area);

		addPlayer(player, MinigameAreaKey.VORKATH);

		Npc npc = NpcHandler.spawnNpc(8059, 2269, 4062, height);

		if (npc instanceof Vorkath) {
			vorkath = (Vorkath) npc;
		} else {
			throw new IllegalStateException("No NPC available for Vorkath.");
		}
	}

	/**
	 * Determines if an npc can be added to the minigame.
	 *
	 * @param npc the npc being added.
	 * @return whether or not the npc can be added.
	 */
	@Override
	public boolean isAddable(Npc npc) {
		return true;
	}

	/**
	 * Referenced when a player is successfully added to the minigame.
	 *
	 * @param player the player being added.
	 */
	@Override
	public void onAdd(Player player) {

	}

	/**
	 * Referenced when an npc is successfully added to the minigame.
	 *
	 * @param npc the npc being added
	 */
	@Override
	public void onAdd(Npc npc) {

	}

	/**
	 * Referenced when a player is removed from the minigame.
	 *
	 * @param player the player being removed.
	 */
	@Override
	public void onRemove(Player player) {
		end();
	}

	/**
	 * Referenced when a npc is removed from the minigame.
	 *
	 * @param npc the npc being removed.
	 */
	@Override
	public void onRemove(Npc npc) {

	}

	/**
	 * Referenced when a player logs into the game.
	 *
	 * @param player the player logging in.
	 */
	@Override
	public void onLogin(Player player) {

	}

	/**
	 * Referenced when a player logs out of the game.
	 *
	 * @param player the player logging out.
	 */
	@Override
	public void onLogout(Player player) {
		removePlayer(player);
	}

	/**
	 * Referenced when walkable interfaces should be updated.
	 *
	 * @param player the player having their walkable interfaces updated.
	 */
	@Override
	public void onUpdateWalkableInterface(Player player) {

	}

	/**
	 * Referenced to start or initiate the minigame.
	 */
	@Override
	public void start() {
		MinigamePlayerParticipant playerParticipant = area.getAnyPlayerParticipant();

		if (playerParticipant == null) {
			return;
		}
		Player player = playerParticipant.getEntity();

		if (player == null) {
			return;
		}
		player.move(new Position(2272, 4054, area.getBoundary().getHeight()));
	}

	@Override
	public void end() {
		MinigameKeyHeightManager.getSingleton().release(getKey(), area.getBoundary().getHeight());

		if (vorkath != null && !vorkath.isDead()) {
			vorkath.setItemsDroppable(false);
			vorkath.killIfAlive();
		}
	}

	/**
	 * The maximum level difference allowed between two players in combat.
	 *
	 * @param attacker the player starting the attack.
	 * @param defender the player defending the attack.
	 * @return the maximum allowed level difference between the two players to initiate combat.
	 */
	@Override
	public int maximumLevelDifference(Player attacker, Player defender) {
		return 0;
	}

	/**
	 * Determines whether or not a player can teleport in a minigame.
	 *
	 * @param player the player attempting to teleport.
	 * @return whether or not the player can teleport.
	 */
	@Override
	public boolean canTeleport(Player player) {
		return true;
	}

	/**
	 * Referenced when the player can sucessfully teleport during a minigame (not necessarily outside).
	 *
	 * @param player the player teleporting.
	 */
	@Override
	public void onTeleport(Player player) {
		super.onTeleport(player);

		removePlayer(player);
	}

	/**
	 * Referenced during the death step.
	 *
	 * @param player the player dying or have died.
	 */
	@Override
	public void onDeath(Player player) {
		super.onDeath(player);

		removePlayer(player);
		player.getPA().movePlayer(2272 + ThreadLocalRandom.current().nextInt(0, 2),
		                          4045 + ThreadLocalRandom.current().nextInt(0, 2), 0);
	}
}
