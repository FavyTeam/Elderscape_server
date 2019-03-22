package game.player.movement;

import core.GameType;
import game.content.combat.Combat;
import game.content.combat.CombatConstants;
import game.content.combat.vsplayer.melee.MeleeData;
import game.content.combat.vsplayer.range.RangedData;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.npc.data.NpcDefinition;
import game.object.clip.Region;
import game.player.Player;
import game.player.PlayerHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import game.position.PositionUtils;
import game.position.distance.DistanceAlgorithms;
import utility.Misc;

/**
 * Handle following.
 *
 * @author MGT Madness, created on 27-03-2015.
 */
public class Follow {

	/**
	 * Follow the player.
	 *
	 * @param player The associated player.
	 * @param secondCalling True if this is the second time follow is called in the same game tick.
	 */
	public static void followPlayer(Player player, boolean secondCalling) {

		if (player.multiLoggingInWild) {
			player.resetPlayerIdToFollow();
			return;
		}
		Player target = PlayerHandler.players[player.getPlayerIdToFollow()];
		if (player.getTeleTimer() > 3) {
			player.resetPlayerIdToFollow();
			return;
		}
		if (target == null || target.getDead()) {
			player.resetPlayerIdToFollow();
			return;
		}
		if (player.getDead()) {
			player.resetPlayerIdToFollow();
			return;
		}
		if (player.isFrozen()) {
			player.turnPlayerTo(target.getX(), target.getY());
			player.resetPlayerIdToFollow();
			return;
		}
		if (player.doingAnAction()) {
			player.resetPlayerIdToFollow();
			return;
		}
		int otherX = target.getX();
		int otherY = target.getY();
		if (target.getPlayerIdToFollow() == player.getPlayerId() && player.playerAssistant.withInDistance(otherX, otherY, player.getX(), player.getY(), 1) && !Combat.inCombat(
				player) && !Combat.inCombat(target)) {
			if (secondCalling) {
				return;
			}
		}
		if (!player.playerAssistant.withinDistanceOfTargetPlayer(target, CombatConstants.OUT_OF_VIEW_DISTANCE)) {
			player.resetPlayerIdToFollow();
			return;
		}
		if (Combat.stopMovement(player, target, true)) {
			return;
		}
		player.faceUpdate(player.getPlayerIdToFollow() + 32768);
		if (player.timeFollowedTarget > target.timePlayerMoved && player.followTargetName.equals(target.getPlayerName()) && player.lastFollowType.equals(player.lastFollowTypeApplied)) {
				return;
		}
		player.timeFollowedTarget = System.currentTimeMillis();
		player.followTargetName = target.getPlayerName();
		Player other = target;
		if (target.getPlayerIdToFollow() == player.getPlayerId() && player.playerAssistant.withInDistance(otherX, otherY, player.getX(), player.getY(), 1) && !Combat.inCombat(player) && !Combat.inCombat(other) && player.getPlayerIdAttacking() == 0 && other.getPlayerIdAttacking() == 0) {
			if (!player.followLeader && !other.followLeader) {
				Movement.playerWalk(player, other.oldX, other.oldY);
			} else {
				Movement.stopMovementDifferent(player);
				if (player.getX() == other.getX() && player.getY() != other.getY()) {
					Movement.playerWalk(player, player.getX() + 1, player.getY());
				} else if (player.getX() != other.getX() && player.getY() == other.getY()) {
					Movement.playerWalk(player, player.getX(), player.getY() + 1);
				} else if (player.getX() != other.getX() && player.getY() != other.getY()) {
					if (player.getY() < other.getY()) {
						Movement.playerWalk(player, player.getX(), player.getY() + 1);
					} else {
						Movement.playerWalk(player, player.getX(), player.getY() - 1);
					}
				}
				player.followLeader = false;
			}
			player.lastFollowType = "FOLLOW EACH OTHER";
			player.lastFollowTypeApplied = player.lastFollowType;
			return;
		}
		// Following target with ranged/magic in combat.
		if (!player.isMeleeFollow() && player.getPlayerIdAttacking() > 0 && player.playerAssistant.withinDistanceOfTargetPlayer(target, CombatConstants.getAttackDistance(player))
		    && !Region.isStraightPathUnblockedProjectiles(player.getX(), player.getY(), target.getX(), target.getY(), target.getHeight(), 1, 1, true)) {
			projectileFollowing(otherX, otherY, player);
			player.lastFollowType = "PROJECTILE FOLLOW";
			player.lastFollowTypeApplied = player.lastFollowType;
			return;
		}
		if (player.isMeleeFollow()) {
			player.lastFollowType = "MELEE FOLLOW";
			player.lastFollowTypeApplied = player.lastFollowType;
			if (otherX == player.getX() && otherY > player.getY()) {
				meleeFollow(player, "SOUTH", otherX, otherY);
			} else if (otherX == player.getX() && otherY < player.getY()) {
				meleeFollow(player, "NORTH", otherX, otherY);
			} else if (otherX > player.getX() && otherY == player.getY()) {
				meleeFollow(player, "WEST", otherX, otherY);
			} else if (otherX < player.getX() && otherY == player.getY()) {
				meleeFollow(player, "EAST", otherX, otherY);
			} else if (otherX < player.getX() && otherY < player.getY()) {
				meleeFollow(player, "NORTH", otherX, otherY);
			} else if (otherX > player.getX() && otherY > player.getY()) {
				meleeFollow(player, "SOUTH", otherX, otherY);
			} else if (otherX < player.getX() && otherY > player.getY()) {
				meleeFollow(player, "SOUTH", otherX, otherY);
			} else if (otherX > player.getX() && otherY < player.getY()) {
				meleeFollow(player, "WEST", otherX, otherY);
			} else if (player.getPA().isOnTopOfTarget(target)) {
				Movement.movePlayerFromUnderEntity(player);
			}
		} else {
			player.lastFollowType = "OUT OF COMBAT FOLLOW";
			player.lastFollowTypeApplied = player.lastFollowType;

			// Position the player behind the player, as if i am following a human, so i am following their back.
			switch (target.directionFacingPath) {
				// North.
				case 0:
					pathFocusOrder(Region.SOUTH, Region.SOUTH_WEST, Region.SOUTH_EAST, Region.WEST, Region.EAST, Region.NORTH_WEST, Region.NORTH_EAST, Region.NORTH, player, otherX,
					               otherY);
					break;
				// North West.
				case 14:
					pathFocusOrder(Region.SOUTH_EAST, Region.SOUTH, Region.EAST, Region.SOUTH_WEST, Region.NORTH_EAST, Region.WEST, Region.NORTH, Region.NORTH_WEST, player, otherX,
					               otherY);
					break;
				// North East.
				case 2:
					pathFocusOrder(Region.SOUTH_WEST, Region.WEST, Region.SOUTH, Region.NORTH_WEST, Region.SOUTH_EAST, Region.NORTH, Region.EAST, Region.NORTH_EAST, player, otherX,
					               otherY);
					break;
				// West.
				case 12:
					pathFocusOrder(Region.EAST, Region.SOUTH_EAST, Region.NORTH_EAST, Region.SOUTH, Region.NORTH, Region.SOUTH_WEST, Region.NORTH_WEST, Region.WEST, player, otherX,
					               otherY);
					break;
				// East.
				case 4:
					pathFocusOrder(Region.WEST, Region.SOUTH_WEST, Region.NORTH_WEST, Region.SOUTH, Region.NORTH, Region.SOUTH_EAST, Region.NORTH_EAST, Region.EAST, player, otherX,
					               otherY);
					break;
				// South.
				case 8:
					pathFocusOrder(Region.NORTH, Region.NORTH_WEST, Region.NORTH_EAST, Region.WEST, Region.EAST, Region.SOUTH_WEST, Region.SOUTH_EAST, Region.SOUTH, player, otherX,
					               otherY);
					break;
				// South West.
				case 10:
					pathFocusOrder(Region.NORTH_EAST, Region.NORTH, Region.EAST, Region.NORTH_WEST, Region.SOUTH_EAST, Region.WEST, Region.SOUTH, Region.SOUTH_WEST, player, otherX,
					               otherY);
					break;
				// South East.
				case 6:
					pathFocusOrder(Region.NORTH_WEST, Region.WEST, Region.NORTH, Region.SOUTH_WEST, Region.NORTH_EAST, Region.SOUTH, Region.EAST, Region.SOUTH_EAST, player, otherX,
					               otherY);
					break;
			}
		}
		player.faceUpdate(player.getPlayerIdToFollow() + 32768);
	}

	/**
	 * The player already follows the target with normal findRoute method if the path is blocked. All this method does is see if there is a tile that i can move to
	 * and that tile i can use it to attack target straight away, then move to it.
	 */
	private static void projectileFollowing(int otherX, int otherY, Player player) {
		// If player can move 1 tile and can instantly attack target, then do that instead.
		if (otherX == player.getX() && otherY > player.getY()) {
			Follow.setPathFocusOrderForProjectiles(Region.WEST, Region.EAST, Region.NORTH_WEST, Region.NORTH_EAST, Region.SOUTH_WEST, Region.SOUTH_EAST, Region.SOUTH, player,
			                                       player.getX(), player.getY(), otherX, otherY);
		} else if (otherX == player.getX() && otherY < player.getY()) {
			Follow.setPathFocusOrderForProjectiles(Region.WEST, Region.EAST, Region.SOUTH_EAST, Region.SOUTH_WEST, Region.NORTH_WEST, Region.NORTH_EAST, Region.NORTH, player,
			                                       player.getX(), player.getY(), otherX, otherY);
		} else if (otherX > player.getX() && otherY == player.getY()) {
			Follow.setPathFocusOrderForProjectiles(Region.NORTH, Region.SOUTH, Region.SOUTH_EAST, Region.NORTH_EAST, Region.SOUTH_WEST, Region.NORTH_WEST, Region.WEST, player,
			                                       player.getX(), player.getY(), otherX, otherY);
		} else if (otherX < player.getX() && otherY == player.getY()) {
			Follow.setPathFocusOrderForProjectiles(Region.SOUTH, Region.NORTH, Region.NORTH_WEST, Region.SOUTH_WEST, Region.NORTH_EAST, Region.SOUTH_EAST, Region.EAST, player,
			                                       player.getX(), player.getY(), otherX, otherY);
		} else if (otherX < player.getX() && otherY < player.getY()) {
			Follow.setPathFocusOrderForProjectiles(Region.SOUTH, Region.WEST, Region.SOUTH_EAST, Region.NORTH_WEST, Region.EAST, Region.NORTH, Region.NORTH_EAST, player,
			                                       player.getX(), player.getY(), otherX, otherY);
		} else if (otherX > player.getX() && otherY > player.getY()) {
			Follow.setPathFocusOrderForProjectiles(Region.NORTH, Region.EAST, Region.NORTH_WEST, Region.SOUTH_EAST, Region.SOUTH, Region.SOUTH_WEST, Region.WEST, player,
			                                       player.getX(), player.getY(), otherX, otherY);
		} else if (otherX < player.getX() && otherY > player.getY()) {
			Follow.setPathFocusOrderForProjectiles(Region.NORTH, Region.WEST, Region.NORTH_EAST, Region.SOUTH_WEST, Region.SOUTH, Region.EAST, Region.SOUTH_EAST, player,
			                                       player.getX(), player.getY(), otherX, otherY);
		} else if (otherX > player.getX() && otherY < player.getY()) {
			Follow.setPathFocusOrderForProjectiles(Region.EAST, Region.SOUTH, Region.NORTH_EAST, Region.SOUTH_WEST, Region.NORTH, Region.WEST, Region.NORTH_WEST, player,
			                                       player.getX(), player.getY(), otherX, otherY);
		}

	}

	/**
	 * Melee following the entity.
	 *
	 * @param focus The direction to focus. The direction of the player to be with the entity. So south means 1 tile south of entity.
	 * @param entityX
	 * @param entityY
	 */
	private static void meleeFollow(Player player, String focus, int entityX, int entityY) {
		switch (focus) {
			case "SOUTH":
				pathFocusOrder(Region.SOUTH, Region.WEST, Region.EAST, Region.NORTH, -1, -1, -1, -1, player, entityX, entityY);
				break;
			case "WEST":
				pathFocusOrder(Region.WEST, Region.SOUTH, Region.NORTH, Region.EAST, -1, -1, -1, -1, player, entityX, entityY);
				break;
			case "EAST":
				pathFocusOrder(Region.EAST, Region.SOUTH, Region.NORTH, Region.WEST, -1, -1, -1, -1, player, entityX, entityY);
				break;
			case "NORTH":
				pathFocusOrder(Region.NORTH, Region.WEST, Region.EAST, Region.SOUTH, -1, -1, -1, -1, player, entityX, entityY);
				break;
		}

	}

	/**
	 * @param focusOrder If focusOrder is south, walk player to south of entity if south of entity position is walkable to entity position.
	 * @param projectile True, to find the path where i can move once to it, then i can attack the entity from it.
	 */
	public static void pathFocusOrder(int one, int two, int three, int four, int five, int six, int seven, int eight, Player player, int entityX, int entityY) {
		if (getPath(one, entityX, entityY, player)) {
			return;
		}
		if (getPath(two, entityX, entityY, player)) {
			return;
		}
		if (getPath(three, entityX, entityY, player)) {
			return;
		}
		if (getPath(four, entityX, entityY, player)) {
			return;
		}
		if (getPath(five, entityX, entityY, player)) {
			return;
		}
		if (getPath(six, entityX, entityY, player)) {
			return;
		}
		if (getPath(seven, entityX, entityY, player)) {
			return;
		}
		if (getPath(eight, entityX, entityY, player)) {
			return;
		}
	}

	private static boolean getPath(int direction, int entityX, int entityY, Player player) {
		if (direction == Region.SOUTH) {
			if (Region.isStraightPathUnblocked(entityX, entityY, entityX, entityY - 1, player.getHeight(), 1, 1, false)) {
				Movement.playerWalk(player, entityX, entityY - 1);
				return true;
			}
		} else if (direction == Region.WEST) {
			if (Region.isStraightPathUnblocked(entityX, entityY, entityX - 1, entityY, player.getHeight(), 1, 1, false)) {
				Movement.playerWalk(player, entityX - 1, entityY);
				return true;
			}
		} else if (direction == Region.EAST) {
			if (Region.isStraightPathUnblocked(entityX, entityY, entityX + 1, entityY, player.getHeight(), 1, 1, false)) {
				Movement.playerWalk(player, entityX + 1, entityY);
				return true;
			}
		} else if (direction == Region.NORTH) {
			if (Region.isStraightPathUnblocked(entityX, entityY, entityX, entityY + 1, player.getHeight(), 1, 1, false)) {
				Movement.playerWalk(player, entityX, entityY + 1);
				return true;
			}
		} else if (direction == Region.NORTH_WEST) {
			if (Region.isStraightPathUnblocked(entityX, entityY, entityX - 1, entityY + 1, player.getHeight(), 1, 1, false)) {
				Movement.playerWalk(player, entityX - 1, entityY + 1);
				return true;
			}
		} else if (direction == Region.NORTH_EAST) {
			if (Region.isStraightPathUnblocked(entityX, entityY, entityX + 1, entityY + 1, player.getHeight(), 1, 1, false)) {
				Movement.playerWalk(player, entityX + 1, entityY + 1);
				return true;
			}
		} else if (direction == Region.SOUTH_WEST) {
			if (Region.isStraightPathUnblocked(entityX, entityY, entityX - 1, entityY - 1, player.getHeight(), 1, 1, false)) {
				Movement.playerWalk(player, entityX - 1, entityY - 1);
				return true;
			}
		} else if (direction == Region.SOUTH_EAST) {
			if (Region.isStraightPathUnblocked(entityX, entityY, entityX + 1, entityY - 1, player.getHeight(), 1, 1, false)) {
				Movement.playerWalk(player, entityX + 1, entityY - 1);
				return true;
			}
		}
		return false;
	}

	/**
	 * This is same as pathFocusOrder, except on this one, it will find if any tile can be moved to and on that tile moved to, i can attack the entity position instantly.
	 * If so, move to that tile.
	 *
	 * @param focusOrder If focusOrder is south, walk player to south of entity if south of entity position is walkable to entity position.
	 */
	public static void setPathFocusOrderForProjectiles(int one, int two, int three, int four, int five, int six, int seven, Player player, int entityX, int entityY,
	                                                   int entityXPosition, int entityYPosition) {
		if (getProjectilePath(one, player, entityX, entityY, entityXPosition, entityYPosition)) {
			return;
		}
		if (getProjectilePath(two, player, entityX, entityY, entityXPosition, entityYPosition)) {
			return;
		}
		if (getProjectilePath(three, player, entityX, entityY, entityXPosition, entityYPosition)) {
			return;
		}
		if (getProjectilePath(four, player, entityX, entityY, entityXPosition, entityYPosition)) {
			return;
		}
		if (getProjectilePath(five, player, entityX, entityY, entityXPosition, entityYPosition)) {
			return;
		}
		if (getProjectilePath(six, player, entityX, entityY, entityXPosition, entityYPosition)) {
			return;
		}
		if (getProjectilePath(seven, player, entityX, entityY, entityXPosition, entityYPosition)) {
			return;
		}
		// findRoute. Player movement will be stopped automatically in other methods once player is in distance and is in straight path to target.
		Movement.playerWalk(player, entityXPosition, entityYPosition);

	}

	private static boolean getProjectilePath(int direction, Player player, int entityX, int entityY, int entityXPosition, int entityYPosition) {
		if (direction == Region.SOUTH) {
			if (Region.isStraightPathUnblocked(entityX, entityY, entityX, entityY - 1, player.getHeight(), 1, 1, false) && Region.isStraightPathUnblockedProjectiles(entityX,
			                                                                                                                                                         entityY - 1,
			                                                                                                                                                         entityXPosition,
			                                                                                                                                                         entityYPosition,
			                                                                                                                                                         player.getHeight(),
			                                                                                                                                                         1, 1, true)) {
				if (player.playerAssistant.withInDistance(entityX, entityY - 1, entityXPosition, entityYPosition, CombatConstants.getAttackDistance(player))) {
					Movement.playerWalk(player, entityX, entityY - 1);
					return true;
				}
			}
		} else if (direction == Region.WEST) {
			if (Region.isStraightPathUnblocked(entityX, entityY, entityX - 1, entityY, player.getHeight(), 1, 1, false) && Region.isStraightPathUnblockedProjectiles(entityX - 1,
			                                                                                                                                                         entityY,
			                                                                                                                                                         entityXPosition,
			                                                                                                                                                         entityYPosition,
			                                                                                                                                                         player.getHeight(),
			                                                                                                                                                         1, 1, true)) {
				if (player.playerAssistant.withInDistance(entityX - 1, entityY, entityXPosition, entityYPosition, CombatConstants.getAttackDistance(player))) {
					Movement.playerWalk(player, entityX - 1, entityY);
					return true;
				}
			}
		} else if (direction == Region.EAST) {
			if (Region.isStraightPathUnblocked(entityX, entityY, entityX + 1, entityY, player.getHeight(), 1, 1, false) && Region.isStraightPathUnblockedProjectiles(entityX + 1,
			                                                                                                                                                         entityY,
			                                                                                                                                                         entityXPosition,
			                                                                                                                                                         entityYPosition,
			                                                                                                                                                         player.getHeight(),
			                                                                                                                                                         1, 1, true)) {
				if (player.playerAssistant.withInDistance(entityX + 1, entityY, entityXPosition, entityYPosition, CombatConstants.getAttackDistance(player))) {
					Movement.playerWalk(player, entityX + 1, entityY);
					return true;
				}
			}
		} else if (direction == Region.NORTH) {
			if (Region.isStraightPathUnblocked(entityX, entityY, entityX, entityY + 1, player.getHeight(), 1, 1, false) && Region.isStraightPathUnblockedProjectiles(entityX,
			                                                                                                                                                         entityY + 1,
			                                                                                                                                                         entityXPosition,
			                                                                                                                                                         entityYPosition,
			                                                                                                                                                         player.getHeight(),
			                                                                                                                                                         1, 1, true)) {
				if (player.playerAssistant.withInDistance(entityX, entityY + 1, entityXPosition, entityYPosition, CombatConstants.getAttackDistance(player))) {
					Movement.playerWalk(player, entityX, entityY + 1);
					return true;
				}
			}
		} else if (direction == Region.NORTH_WEST) {
			if (Region.isStraightPathUnblocked(entityX, entityY, entityX - 1, entityY + 1, player.getHeight(), 1, 1, false) && Region.isStraightPathUnblockedProjectiles(
					entityX - 1, entityY + 1, entityXPosition, entityYPosition, player.getHeight(), 1, 1, true)) {
				if (player.playerAssistant.withInDistance(entityX - 1, entityY + 1, entityXPosition, entityYPosition, CombatConstants.getAttackDistance(player))) {
					Movement.playerWalk(player, entityX - 1, entityY + 1);
					return true;
				}
			}
		} else if (direction == Region.NORTH_EAST) {
			if (Region.isStraightPathUnblocked(entityX, entityY, entityX + 1, entityY + 1, player.getHeight(), 1, 1, false) && Region.isStraightPathUnblockedProjectiles(
					entityX + 1, entityY + 1, entityXPosition, entityYPosition, player.getHeight(), 1, 1, true)) {
				if (player.playerAssistant.withInDistance(entityX + 1, entityY + 1, entityXPosition, entityYPosition, CombatConstants.getAttackDistance(player))) {
					Movement.playerWalk(player, entityX + 1, entityY + 1);
					return true;
				}
			}
		} else if (direction == Region.SOUTH_WEST) {
			if (Region.isStraightPathUnblocked(entityX, entityY, entityX - 1, entityY - 1, player.getHeight(), 1, 1, false) && Region.isStraightPathUnblockedProjectiles(
					entityX - 1, entityY - 1, entityXPosition, entityYPosition, player.getHeight(), 1, 1, true)) {
				if (player.playerAssistant.withInDistance(entityX - 1, entityY - 1, entityXPosition, entityYPosition, CombatConstants.getAttackDistance(player))) {
					Movement.playerWalk(player, entityX - 1, entityY - 1);
					return true;
				}
			}
		} else if (direction == Region.SOUTH_EAST) {
			if (Region.isStraightPathUnblocked(entityX, entityY, entityX + 1, entityY - 1, player.getHeight(), 1, 1, false) && Region.isStraightPathUnblockedProjectiles(
					entityX + 1, entityY - 1, entityXPosition, entityYPosition, player.getHeight(), 1, 1, true)) {
				if (player.playerAssistant.withInDistance(entityX + 1, entityY - 1, entityXPosition, entityYPosition, CombatConstants.getAttackDistance(player))) {
					Movement.playerWalk(player, entityX + 1, entityY - 1);
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * On specific large npcs, they are visually on a different tile.
	 *
	 * @param npcId
	 * @return
	 */
	public static int isBigNpc(int npcId) {
		if (NpcDefinition.getDefinitions()[npcId] == null) {
			return 0;
		}
		return (NpcDefinition.getDefinitions()[npcId].size - 1);
	}

	public static void followNpc(Player player) {
		int npcIdToFollow = player.getNpcIdToFollow();
		Npc npc = NpcHandler.npcs[npcIdToFollow];
		if (npc == null || npc.isDead()) {
			player.resetNpcIdToFollow();
			return;
		}
		if (player.isFrozen()) {
			return;
		}
		if (player.getDead()) {
			return;
		}
		if (player.doingAnAction()) {
			player.resetNpcIdToFollow();
			return;
		}
		int npcX = npc.getVisualX();
		int npcY = npc.getVisualY();
		boolean isWieldingRangedWeaponWithNoArrowSlotRequirement = false;
		int size = 1;
		if (NpcDefinition.getDefinitions()[npc.npcType] != null) {
			size = NpcDefinition.getDefinitions()[npc.npcType].size;
		}
		boolean withinDistance = player.playerAssistant.withInDistance(player.getX(), player.getY(), npcX, npcY, size);
		boolean hallyDistance = player.playerAssistant.withInDistance(npcX, npcY, player.getX(), player.getY(), 2);
		boolean bowDistance = player.playerAssistant.withInDistance(npcX, npcY, player.getX(), player.getY(), CombatConstants.getAttackDistance(player));
		boolean magicDistance = player.playerAssistant.withInDistance(npcX, npcY, player.getX(), player.getY(), 10);
		boolean rangeWeaponDistance = player.playerAssistant.withInDistance(npcX, npcY, player.getX(), player.getY(), CombatConstants.getAttackDistance(player));
		boolean sameSpot = player.getX() == npcX && player.getY() == npcY;
		boolean inside = false;
		if (NpcDefinition.getDefinitions()[npc.npcType] != null) {
			boolean within = npc.getDefinition().npcType != 7540 ?
					player.playerAssistant.withInDistance(npcX, npcY, player.getX(), player.getY(),
					NpcDefinition.getDefinitions()[npc.npcType].size - 1) :
					PositionUtils.withinDistance(player.getX(), player.getY(), npcX, npcY, size, size, 0, DistanceAlgorithms.EUCLIDEAN);

			if (NpcDefinition.getDefinitions()[npc.npcType].size > 1 && within) {
				inside = true;
			}
		}

		if (!player.playerAssistant.withInDistance(npcX, npcY, player.getX(), player.getY(), 25)) {
			player.resetNpcIdToFollow();
			return;
		}
		if (RangedData.isWieldingRangedWeaponWithNoArrowSlotRequirement(player) && player.getNpcIdAttacking() > 0) {
			isWieldingRangedWeaponWithNoArrowSlotRequirement = true;
		}

		if (isWieldingRangedWeaponWithNoArrowSlotRequirement && rangeWeaponDistance && !sameSpot && !player.hasLastCastedMagic() && player.getNpcIdAttacking() > 0) {
			if (!inside) {
				boolean isStraightFromNpc = followNpcClippedProjectile(player, npc);
				if (isStraightFromNpc) {
					Movement.stopMovement(player);
				}
				return;
			}
		}
		if (RangedData.isWieldingMediumRangeRangedWeapon(player) && player.getNpcIdAttacking() > 0 && bowDistance && !sameSpot) {
			if (!inside) {
				boolean isStraightFromNpc = followNpcClippedProjectile(player, npc);
				if (isStraightFromNpc) {
					Movement.stopMovement(player);
				}
				return;
			}
		}
		if ((player.hasLastCastedMagic() || player.getAutoCasting()) && magicDistance && (npcX != player.getX() || npcY != player.getY()) && player.getNpcIdAttacking() > 0) {
			if (!inside) {
				boolean isStraightFromNpc = followNpcClippedProjectile(player, npc);
				if (isStraightFromNpc) {
					Movement.stopMovement(player);
				}
				return;
			}
		}
		if (MeleeData.usingHalberd(player) && hallyDistance && !sameSpot && player.getNpcIdAttacking() > 0) {
			if (!inside) {
				followNpcClippedProjectile(player, npc);
				return;
			}
		}
		if (player.isUsingMediumRangeRangedWeapon() && rangeWeaponDistance && !sameSpot && player.getNpcIdAttacking() > 0) {
			if (!inside) {
				followNpcClippedProjectile(player, npc);
				return;
			}
		}

		if (npcX == player.getX() && npcY == player.getY()) {
			Movement.movePlayerFromUnderEntity(player);

		} else if (size == 1) {
			if (!GameType.isPreEoc() && isSpecialNpcPathing(npc.npcType)) {
				Movement.playerWalk(player, npcX, npcY);
			} else {
				if (npcX == player.getX() && npcY > player.getY()) {
					meleeFollow(player, "SOUTH", npcX, npcY);
				} else if (npcX == player.getX() && npcY < player.getY()) {
					meleeFollow(player, "NORTH", npcX, npcY);
				} else if (npcX > player.getX() && npcY == player.getY()) {
					meleeFollow(player, "WEST", npcX, npcY);
				} else if (npcX < player.getX() && npcY == player.getY()) {
					meleeFollow(player, "EAST", npcX, npcY);
				} else if (npcX < player.getX() && npcY < player.getY()) {
					meleeFollow(player, "NORTH", npcX, npcY);
				} else if (npcX > player.getX() && npcY > player.getY()) {
					meleeFollow(player, "SOUTH", npcX, npcY);
				} else if (npcX < player.getX() && npcY > player.getY()) {
					meleeFollow(player, "SOUTH", npcX, npcY);
				} else if (npcX > player.getX() && npcY < player.getY()) {
					meleeFollow(player, "WEST", npcX, npcY);
				}
			}
		}

		// If withIn distance and big npc, this is to reposition player, if player is inside Npc.
		else if (!withinDistance || inside) {
			if (!GameType.isPreEoc() && isSpecialNpcPathing(npc.npcType)) {
				Movement.playerWalk(player, npcX, npcY);
			} else {
				walkToPathCloseToBigNpc(player, npcX, npcY, NpcDefinition.getDefinitions()[npc.npcType].size);
			}
		}
		player.faceUpdate(npcIdToFollow);
	}

	public static boolean isSpecialNpcPathing(int npc) {
		if (GameType.isPreEoc()) {
			return false;
		}
		switch (npc) {
			// Zulrah
			case 2042:
			case 2043:
			case 2044:

			case 5886: // Abyssal sire
			case 5914: // Respiratory system
			case 5915: // Respiratory system
			case 493: // Whirlpool
			case 496: // Master whirlpool
			case 5961: // Spinolyp
			case 5947: // Spinolyp
			case 8059: // Vorkath
				return true;
		}
		return false;
	}

	public int possibleX;

	public int possibleY;

	private int disanceOfPlayerToPossibleCoords;

	public Follow(int possibleX, int possibleY, int disanceOfPlayerToPossibleCoords) {
		this.possibleX = possibleX;
		this.possibleY = possibleY;
		this.disanceOfPlayerToPossibleCoords = disanceOfPlayerToPossibleCoords;
	}

	/**
	 * @return Return the coordinate that the player is closest to.
	 */
	public static Follow getClosestPosition(int playerX, int playerY, ArrayList<Follow> coords) {
		for (int index = 0; index < coords.size(); index++) {
			coords.get(index).disanceOfPlayerToPossibleCoords = Misc.distanceToPoint(playerX, playerY, coords.get(index).possibleX, coords.get(index).possibleY);
		}
		Collections.sort(coords, new Comparator<Follow>() {
			public int compare(Follow o1, Follow o2) {
				// Sort ascending.
				if (o1.disanceOfPlayerToPossibleCoords < o2.disanceOfPlayerToPossibleCoords) {
					return -1;
				}
				if (o1.disanceOfPlayerToPossibleCoords == o2.disanceOfPlayerToPossibleCoords) {
					return 0;
				}
				return 1;
			}
		});
		return coords.get(0);
	}

	public static void walkToPathCloseToBigNpc(Player player, int npcX, int npcY, int npcSize) {
		ArrayList<Follow> coords = new ArrayList<Follow>();
		int i = 0;
		int originalX = npcX;
		int originalY = npcY;
		Follow entry;
		for (int index = 0; index < 4; index++) {
			npcX = originalX;
			npcY = originalY;
			switch (index) {
				case 0:
					npcX += npcSize;
					if (Region.isStraightPathUnblocked(originalX, originalY, npcX, npcY, player.getHeight(), 1, 1, false)) {
						coords.add(new Follow(npcX, npcY, player.distanceToPoint(npcX, npcY)));
					}
					for (i = 1; i <= npcSize; i++) {
						if (Region.isStraightPathUnblocked(originalX, originalY, npcX, npcY + i, player.getHeight(), 1, 1, false)) {
							coords.add(new Follow(npcX, (npcY + i), player.distanceToPoint(npcX, npcY + i)));
						}
					}
					for (i = 1; i <= npcSize; i++) {
						if (Region.isStraightPathUnblocked(originalX, originalY, npcX, npcY - i, player.getHeight(), 1, 1, false)) {
							coords.add(new Follow(npcX, (npcY - i), player.distanceToPoint(npcX, npcY - i)));
						}
					}
					break;
				case 1:
					npcX -= npcSize;
					if (Region.isStraightPathUnblocked(originalX, originalY, npcX, npcY, player.getHeight(), 1, 1, false)) {
						coords.add(new Follow(npcX, npcY, player.distanceToPoint(npcX, npcY)));
					}
					for (i = 1; i <= npcSize; i++) {
						if (Region.isStraightPathUnblocked(originalX, originalY, npcX, npcY + i, player.getHeight(), 1, 1, false)) {
							coords.add(new Follow(npcX, (npcY + i), player.distanceToPoint(npcX, npcY + i)));
						}
					}
					for (i = 1; i <= npcSize; i++) {
						if (Region.isStraightPathUnblocked(originalX, originalY, npcX, npcY - i, player.getHeight(), 1, 1, false)) {
							coords.add(new Follow(npcX, (npcY - i), player.distanceToPoint(npcX, npcY - i)));
						}
					}
					break;
				case 2:
					npcY += npcSize;
					if (Region.isStraightPathUnblocked(originalX, originalY, npcX, npcY, player.getHeight(), 1, 1, false)) {
						coords.add(new Follow(npcX, npcY, player.distanceToPoint(npcX, npcY)));
					}
					for (i = 1; i <= npcSize; i++) {
						entry = new Follow((npcX + i), npcY, player.distanceToPoint(npcX + i, npcY));
						if (!coords.contains(entry)) {
							if (Region.isStraightPathUnblocked(originalX, originalY, npcX + i, npcY, player.getHeight(), 1, 1, false)) {
								coords.add(entry);
							}
						}
					}
					for (i = 1; i <= npcSize; i++) {
						entry = new Follow((npcX - i), npcY, player.distanceToPoint(npcX - i, npcY));
						if (!coords.contains(entry)) {
							if (Region.isStraightPathUnblocked(originalX, originalY, npcX - i, npcY, player.getHeight(), 1, 1, false)) {
								coords.add(entry);
							}
						}
					}
					break;
				case 3:
					npcY -= npcSize;
					if (Region.isStraightPathUnblocked(originalX, originalY, npcX, npcY, player.getHeight(), 1, 1, false)) {
						coords.add(new Follow(npcX, npcY, player.distanceToPoint(npcX, npcY)));
					}
					for (i = 1; i <= npcSize; i++) {
						entry = new Follow((npcX + i), npcY, player.distanceToPoint(npcX + i, npcY));
						if (!coords.contains(entry)) {
							if (Region.isStraightPathUnblocked(originalX, originalY, npcX + i, npcY, player.getHeight(), 1, 1, false)) {
								coords.add(entry);
							}
						}
					}
					for (i = 1; i <= npcSize; i++) {
						entry = new Follow((npcX - i), npcY, player.distanceToPoint(npcX - i, npcY));
						if (!coords.contains(entry)) {
							if (Region.isStraightPathUnblocked(originalX, originalY, npcX - i, npcY, player.getHeight(), 1, 1, false)) {
								coords.add(entry);
							}
						}
					}
					break;
			}
		}

		if (coords.isEmpty()) {
			return;
		}
		Collections.sort(coords, new Comparator<Follow>() {
			public int compare(Follow o1, Follow o2) {
				// Sort ascending.
				if (o1.disanceOfPlayerToPossibleCoords < o2.disanceOfPlayerToPossibleCoords) {
					return -1;
				}
				return 1;
			}
		});
		Movement.playerWalk(player, coords.get(0).possibleX, coords.get(0).possibleY);
	}

	private static boolean followNpcClippedProjectile(Player player, Npc npc) {
		int otherX = npc.getVisualX();
		int otherY = npc.getVisualY();
		if (!GameType.isPreEoc() && isSpecialNpcPathing(npc.npcType)) {
				return true;
		}
		boolean isStraightFromNpc = Region.isStraightPathUnblockedProjectiles(player.getX(), player.getY(), otherX, otherY, npc.getHeight(), 1, 1, true);
		if (!player.isMeleeFollow() && player.getNpcIdAttacking() > 0 && !isStraightFromNpc) {
			projectileFollowing(otherX, otherY, player);
		}
		return isStraightFromNpc;

	}

	public static void resetFollow(Player player) {
		player.followTargetName = "";
		player.resetPlayerIdToFollow();
		player.resetNpcIdToFollow();
		player.setLastCastedMagic(false);
	}

}
