package game.content.minigame.impl.fight_pits;

import core.Server;
import core.ServerConstants;
import game.position.Position;
import game.content.AbstractContentToggle;
import game.content.ContentState;
import game.content.ContentToggle;
import game.content.bank.Bank;
import game.content.combat.Combat;
import game.content.dialogueold.DialogueHandler;
import game.content.interfaces.AreaInterface;
import game.content.minigame.*;
import game.item.ItemAssistant;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.player.Boundary;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import org.apache.commons.lang3.time.DurationFormatUtils;
import utility.Misc;

import java.time.DateTimeException;
import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Jason MacKeigan on 2018-01-24 at 9:47 AM
 */
public class FightPitsMinigame extends Minigame {

	public static final int COIN_REWARD = 50_000_000;

	public static final Boundary BOUNDARY = new Boundary(2375, 5128, 2423, 5175); // was 68

	public static final Boundary WAITING_AREA = new Boundary(2393, 5169, 2404, 5175);

	private static final int PLAYERS_REQUIRED_TO_START = 1;

	private static final long WAITING_TIME = 50;

	private static final long DURATION_BEFORE_MONSTERS = Misc.minutesToTicks(5);

	private static final long DURATION_BEFORE_DAMAGE = Misc.minutesToTicks(10);

	private static final long DURATION_BEFORE_END = Misc.minutesToTicks(15);

	private static final int MAXIMUM_MONSTERS = 10;

	private final ContentToggle content = new AbstractContentToggle(ContentState.ENABLED);

	private final long cooldown;

	private FightPitsState state = FightPitsState.INACTIVE;

	private FightPitsDanger danger = FightPitsDanger.NONE;

	private final FightPitsCombatArea combatArea = new FightPitsCombatArea(BOUNDARY);

	private final FightPitsLobbyArea lobbyArea = new FightPitsLobbyArea(WAITING_AREA);

	private long lastWinner;

	private long currentWinner;

	private long elapsedInState;

	public FightPitsMinigame(long cooldown) {
		super(MinigameKey.FIGHT_PIT);
		this.cooldown = cooldown;
		addArea(MinigameAreaKey.FIGHT_PIT_LOBBY, lobbyArea);
		addArea(MinigameAreaKey.FIGHT_PIT_COMBAT_AREA, combatArea);
		getEventHandler().addEvent(this, new FightPitsMinigameCycle(this), 1);
	}

	@Override
	public boolean isAddable(Player player) {
		return true;
	}

	@Override
	public boolean isAddable(Npc npc) {
		return true;
	}

	@Override
	public void onAdd(Player player) {
		if (!Boundary.isIn(player, WAITING_AREA)) {
			player.getPA().movePlayer(2399, 5175, 0);
		}
	}

	@Override
	public void onAdd(Npc npc) {

	}

	@Override
	public void onRemove(Player player) {
		player.getPA().movePlayer(2399, 5177, 0);

		if ((state == FightPitsState.ACTIVE || state == FightPitsState.WAITING) && combatArea.amountOfPlayers() <= 1 && currentWinner == 0L) {
			MinigamePlayerParticipant playerParticipant = combatArea.getAnyPlayerParticipant();

			// was not able to find a participant
			if (playerParticipant == null) {
				end();
				return;
			}
			long selectedWinner = playerParticipant.getEntity().getNameAsLong();

			lastWinner = selectedWinner;
			currentWinner = selectedWinner;
			playerParticipant.getEntity().setNpcType(2181);
			playerParticipant.getEntity().getDH().sendNpcChat("Well done you were the last person in the pit and", "won the reward. It has been added to your",
			                                                  "inventory or bank.", DialogueHandler.FacialAnimation.HAPPY.getAnimationId());
			if (!ItemAssistant.addItem(playerParticipant.getEntity(), 995, COIN_REWARD)) {
				if (!Bank.addItemToBank(playerParticipant.getEntity(), 995, COIN_REWARD, false)) {
					Server.itemHandler.createGroundItem(playerParticipant.getEntity(), 995, playerParticipant.getEntity().getX(),
					                                    playerParticipant.getEntity().getY(), 0, COIN_REWARD, true, 0, true, playerParticipant.getEntity().getPlayerName(), "", "",
							"", "Fight pits minigame reward onRemove");
				}
			}
			end();
		}
	}

	@Override
	public void onRemove(Npc npc) {

	}

	@Override
	public void onLogin(Player player) {
		if (!Boundary.isIn(player, lobbyArea.getBoundary())) {
			player.getPA().movePlayer(2399, 5175, 0);
			addPlayer(player, MinigameAreaKey.FIGHT_PIT_LOBBY);
			return;
		}
		addPlayer(player, MinigameAreaKey.FIGHT_PIT_LOBBY);
	}

	@Override
	public void onLogout(Player player) {

	}

	@Override
	public void start() {
		if (state != FightPitsState.INACTIVE) {
			throw new IllegalStateException("Minigame must be inactive to start.");
		}
		state = FightPitsState.WAITING;

		lobbyArea.forEachPlayerParticipant(player -> {
			combatArea.add(player);
			player.getEntity().move(Misc.random(FightPitsRegion.values()).getPosition().translate(
					ThreadLocalRandom.current().nextInt(-3, 3), ThreadLocalRandom.current().nextInt(-3, 3), 0));
			player.getEntity().setNpcType(2181);
			player.getEntity().getDH().sendNpcChat("Wait for my signal before fighting.", DialogueHandler.FacialAnimation.ANGER_1.getAnimationId());
		});
		lobbyArea.clearPlayerParticipants();
	}

	@Override
	public void end() {
		if (state == FightPitsState.INACTIVE) {
			throw new IllegalStateException("Minigame is not active.");
		}
		state = FightPitsState.INACTIVE;
		danger = FightPitsDanger.NONE;
		lastWinner = currentWinner;
		elapsedInState = 0;
		currentWinner = 0L;
		combatArea.forEachPlayerParticipant(playerParticipant -> addPlayer(playerParticipant.getEntity(), MinigameAreaKey.FIGHT_PIT_LOBBY));
		combatArea.clearPlayerParticipants();
		combatArea.forEachNpcParticipant(npcParticipant -> {
			Npc npc = npcParticipant.getEntity();

			if (!npc.isDead()) {
				npc.setDead(true);
			}
		});
		combatArea.clearNpcParticipants();
	}

	@Override
	public void onDeath(Player player) {
		super.onDeath(player);

		removePlayer(player);
		addPlayer(player, MinigameAreaKey.FIGHT_PIT_LOBBY);
	}

	@Override
	public void onOutsideBounds(Player player) {
		super.onOutsideBounds(player);

		removePlayer(player);
	}

	@Override
	public void onUpdateWalkableInterface(Player player) {
		player.getPA().walkableInterface(2804);
		player.getPA().sendFrame126(String.format("Current champion: %s", lastWinner == 0L ? "None" : Misc.capitalize(Misc.nameForLong(lastWinner))), 2805);

		if (state == FightPitsState.INACTIVE) {
			try {
				player.getPA().sendFrame126(String.format("Next game starts in: %s",
				                                          DurationFormatUtils
						                                          .formatDuration(Duration.ofSeconds(Misc.ticksToSeconds(Math.max(0, cooldown - elapsedInState))).toMillis(),
						                                                          "HH:mm:ss")), 2806);
			} catch (DateTimeException | ArithmeticException exception) {
				player.getPA().sendFrame126("Determining next game time...", 2806);
			}
			player.getPA().sendFrame36(560, 1, false);
			player.getPA().showOption(3, 0, "Null", 1);
		} else if (state == FightPitsState.WAITING) {
			player.getPA().showOption(3, 0, "Null", 1);
			player.getPA().sendFrame126(String.format("Fighting in: %s",
			                                          DurationFormatUtils
					                                          .formatDuration(Duration.ofSeconds(Misc.ticksToSeconds(Math.max(0, WAITING_TIME - elapsedInState))).toMillis(),
					                                                          "HH:mm:ss")), 2806);
		} else if (state == FightPitsState.ACTIVE) {
			player.getPA().showOption(3, 0, "Attack", 1);
			player.getPA().sendFrame126(String.format("Foes remaining: %s", combatArea.amountOfPlayers()), 2806);
		}
	}



	@Override
	public int maximumLevelDifference(Player attacker, Player defender) {
		return 126;
	}

	final class FightPitsMinigameCycle extends CycleEvent<Object> {

		private final FightPitsMinigame minigame;

		FightPitsMinigameCycle(FightPitsMinigame minigame) {
			this.minigame = minigame;
		}

		@Override
		public void execute(CycleEventContainer<Object> container) {
			elapsedInState++;
			if (state == FightPitsState.INACTIVE) {
				if (elapsedInState >= cooldown) {
					if (lobbyArea.amountOfPlayers() < PLAYERS_REQUIRED_TO_START) {
						elapsedInState = (int) ((double) cooldown * .95);
						return;
					}
					start();
					elapsedInState = 0;
				}
			} else if (state == FightPitsState.WAITING) {
				if (elapsedInState >= WAITING_TIME) {
					state = FightPitsState.ACTIVE;
					elapsedInState = 0;
					combatArea.forEachPlayerParticipant(playerParticipant -> {
						AreaInterface.updateWalkableInterfaces(playerParticipant.getEntity());
						playerParticipant.getEntity().setNpcType(2181);
						playerParticipant.getEntity().getDH().sendNpcChat("FIGHT!", DialogueHandler.FacialAnimation.ANGER_2.getAnimationId());
					});
				}
			} else if (state == FightPitsState.ACTIVE) {
				if (elapsedInState >= DURATION_BEFORE_END) {
					end();
					return;
				}

				if (danger == FightPitsDanger.NONE) {
					if (elapsedInState >= DURATION_BEFORE_MONSTERS) {
						danger = FightPitsDanger.CREATE_MONSTERS;
					}
				} else if (danger == FightPitsDanger.CREATE_MONSTERS) {
					danger = FightPitsDanger.MONSTERS;

					for (int i = 0; i < MAXIMUM_MONSTERS; i++) {
						Position regionPosition = Misc.random(FightPitsRegion.values()).getPosition().translate(
								ThreadLocalRandom.current().nextInt(-3, 3), ThreadLocalRandom.current().nextInt(-3, 3), 0);

						addNpc(new MinigameNpcParticipant(NpcHandler.spawnNpc(Misc.random(FightPitsMonster.values()).getId(),
						                                                      regionPosition.getX(), regionPosition.getY(), 0)), MinigameAreaKey.FIGHT_PIT_COMBAT_AREA);
					}
				} else if (danger == FightPitsDanger.MONSTERS) {
					if (elapsedInState >= DURATION_BEFORE_DAMAGE) {
						danger = FightPitsDanger.CREATE_DAMAGE;
					}
				} else if (danger == FightPitsDanger.CREATE_DAMAGE) {
					danger = FightPitsDanger.DAMAGE;
				} else if (danger == FightPitsDanger.DAMAGE) {
					combatArea.forEachPlayerParticipant(player -> {
						Combat.createHitsplatOnPlayerNormal(player.getEntity(), 1, ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.NO_ICON);
					});
				}
			}
		}

		@Override
		public void stop() {

		}
	}

	@Override
	public boolean canTeleport(Player player) {
		MinigameArea area = getAreaOrNull(player);

		if (area == null) {
			return true;
		}
		return area.getKey() == lobbyArea.getKey();
	}
}
