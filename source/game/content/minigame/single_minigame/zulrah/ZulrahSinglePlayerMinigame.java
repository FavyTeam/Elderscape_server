package game.content.minigame.single_minigame.zulrah;

import game.position.Position;
import game.content.minigame.MinigameAreaKey;
import game.content.minigame.MinigameKey;
import game.content.minigame.MinigamePlayerParticipant;
import game.content.minigame.height_manager.MinigameKeyHeightManager;
import game.content.minigame.single_minigame.SinglePlayerMinigame;
import game.entity.Entity;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.npc.impl.zulrah.Zulrah;
import game.npc.impl.zulrah.ZulrahLocation;
import game.player.Boundary;
import game.player.Player;
import game.player.PlayerAssistant;
import game.player.event.CycleEventAdapter;
import game.player.event.CycleEventContainer;
import utility.Misc;

/**
 * Created by Jason MacKeigan on 2018-04-04 at 12:54 PM
 */
public class ZulrahSinglePlayerMinigame extends SinglePlayerMinigame {

	private final ZulrahMinigameArea minigameArea;

	private Zulrah zulrah;

	public ZulrahSinglePlayerMinigame(MinigameKey key, Player player, int height) {
		super(key, player, MinigameAreaKey.ZULRAH);
		this.minigameArea = new ZulrahMinigameArea(new Boundary(2250, 3060, 2290, 3090, height));
		addArea(minigameArea.getKey(), minigameArea);
		addPlayer(player, minigameArea.getKey());
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
	 * Referenced during the death step.
	 *
	 * @param player the player dying or have died.
	 */
	@Override
	public void onDeath(Player player) {
		super.onDeath(player);

		player.getPA().movePlayer(3101 + Misc.random(3), 3492 + Misc.random(6), 0);
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
		MinigamePlayerParticipant playerParticipant = minigameArea.getAnyPlayerParticipant();

		if (playerParticipant == null) {
			throw new IllegalStateException("Unable to find a player for the minigame.");
		}
		playerParticipant.getEntity().move(new Position(2268, 3069, minigameArea.getBoundary().getHeight()));

		Npc result = NpcHandler.spawnNpc(playerParticipant.getEntity(), 2042, ZulrahLocation.NORTH.getLocation().getX(),
		                                 ZulrahLocation.SOUTH.getLocation().getY(), playerParticipant.getEntity().getHeight(), true, false);

		if (!(result instanceof Zulrah)) {
			throw new IllegalStateException("Zulrah entity is null or is not an instance of Zulrah class.");
		}
		zulrah = (Zulrah) result;

		zulrah.spawn(playerParticipant.getEntity());

		playerParticipant.getEntity().getPA().sendFadingScreen("", PlayerAssistant.FadingScreenState.FADE_OUT, (byte) 2, 0);
		playerParticipant.getEntity().getEventHandler().addEvent(playerParticipant.getEntity(), new CycleEventAdapter<Entity>() {
			@Override
			public void execute(CycleEventContainer<Entity> container) {
				container.stop();

				playerParticipant.getEntity().getPA().sendCameraPosition(6000, 5947, 1828, 180);
				playerParticipant.getEntity().getPA().sendSpinCamera(52 - 8, 53 - 8, 1024, 0, 100);

				playerParticipant.getEntity().getEventHandler().addEvent(playerParticipant.getEntity(), new CycleEventAdapter<Entity>() {
					@Override
					public void execute(CycleEventContainer<Entity> container) {
						container.stop();
						playerParticipant.getEntity().getPA().resetCameraShake();
					}
				}, 10);
			}
		}, 1);
	}

	/**
	 * Referenced to effectively end the minigame.
	 */
	@Override
	public void end() {
		MinigameKeyHeightManager.getSingleton().release(getKey(), minigameArea.getBoundary().getHeight());

		if (!zulrah.isDead()) {
			zulrah.setItemsDroppable(false);
			zulrah.killIfAlive();
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
}
