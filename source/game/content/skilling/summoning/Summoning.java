package game.content.skilling.summoning;

import java.text.DecimalFormat;
import core.GameType;
import core.ServerConstants;
import game.content.interfaces.InterfaceAssistant;
import game.content.item.impl.PreEocCompletionistCape;
import game.content.skilling.Skilling;
import game.content.skilling.summoning.familiar.SummoningFamiliar;
import game.content.skilling.summoning.familiar.SummoningFamiliarNpc;
import game.content.skilling.summoning.familiar.special.impl.BoBSummoningFamiliarSpecialAbility;
import game.content.skilling.summoning.pet.GrowingPet;
import game.content.skilling.summoning.pet.SummoningPetManager;
import game.entity.Entity;
import game.item.ItemAssistant;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.npc.data.NpcDefinition;
import game.npc.pet.Pet;
import game.object.clip.Region;
import game.player.Player;
import game.player.PlayerHandler;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;

/**
 * Handles summoning
 *
 * @author 2012
 */
public class Summoning {

	/**
	 * The decimal format for growth and hunger
	 */
	private static final DecimalFormat format = new DecimalFormat("#.#");

	/**
	 * The familiar interface id
	 */
	private static final int FAMILIAR_INTERFACE = 39_560;

	/**
	 * The pet interface id
	 */
	private static final int PET_INTERFACE = 38_560;

	/**
	 * The familiar head id
	 */
	private static final int FAMILIAR_HEAD_ID = 39_579;

	/**
	 * The pet head id
	 */
	private static final int PET_HEAD_ID = 38_571;

	/**
	 * The hunger line id
	 */
	private static final int HUNGER_LINE_ID = 38_568;

	/**
	 * The growth line id
	 */
	private static final int GROWTH_LINE_ID = 38_569;

	/**
	 * The name line id for familiars
	 */
	private static final int FAMILIAR_NAME_LINE_ID = 39_576;

	/**
	 * The name line id for pets
	 */
	private static final int PET_NAME_LINE_ID = 38_570;

	/**
	 * The minimum duration required to renew familiar (in minutes)
	 */
	private static final int MINIMUM_DURATION = 3;

	/**
	 * To deduct a point from summoning skill every # ticks
	 */
	private static final int DEDUCTION_TICKS = 100;

	/**
	 * Healing every 25 ticks, 15 seconds
	 */
	private static final int HEALING_TICKS = 25;

	/**
	 * The deduction from required special
	 */
	private static final double SPECIAL_DEDUCTION_PERCENTAGE = 0.80;

	/**
	 * Used for deducting special amount
	 */
	public static final int SPIRIT_CAPE = 19_893;

	/**
	 * The activate animation for special attacks
	 */
	public static final int ACTIVATE_ANIMATION = 7_660; // 10252

	/**
	 * The type of growth
	 */
	public enum PetGrowthType {
		/*
		 * The pet doesn't grow
		 */
		ONE_FORM,
		/*
		 * The pet transition from baby to adult
		 */
		TWO_FORM,
		/*
		 * The pet transitions from baby, "teen", adult
		 */
		THREE_FORM,
	}

	/**
	 * The stage of the growth
	 */
	public enum PetGrowthStage {
		BABY, TEEN, ADULT,
	}

	/**
	 * The special percentage
	 */
	private int special;

	/**
	 * The duration of the familiar
	 */
	private int duration;

	/**
	 * The summoning familiar
	 */
	private SummoningFamiliarNpc familiar;

	/**
	 * Summoning a familiar
	 *
	 * @param player the player
	 * @param id the id
	 */
	public static boolean summonFamiliar(Player player, int id) {
		if (!GameType.isPreEoc()) {
			return false;
		}
		player.baseSkillLevel[ServerConstants.SUMMONING] = 99;
		player.skillExperience[ServerConstants.SUMMONING] = 14_000_000;
		/*
		 * The familiar
		 */
		SummoningFamiliar familiar = SummoningFamiliar.forPouch(id);
		/*
		 * No familiar
		 */
		if (familiar == null) {
			return false;
		}
		/*
		 * Already summoned
		 */
		if (player.getSummoning().getFamiliar() != null || player.getPetSummoned()) {
			player.getPA().sendMessage("You already have a familiar spawned.");
			return true;
		}
		/*
		 * Checking level
		 */
		if (Skilling.getLevelForExperience(
				player.skillExperience[ServerConstants.SUMMONING]) < familiar.getLevelRequired()) {
			player.getPA()
					.sendMessage("You don't have the required Summoning level to summon this familiar.");
			player.getPA().sendMessage(
					"You need the Summoning level of at least " + familiar.getLevelRequired() + ".");
			return true;
		}
		/*
		 * Checking level
		 */
		if (player.baseSkillLevel[ServerConstants.SUMMONING] <= familiar.getCost()) {
			player.getPA()
					.sendMessage("You don't have enough summoning points to summon this familiar");
			return true;
		}
		/*
		 * No pouch
		 */
		if (!ItemAssistant.hasItemAmountInInventory(player, familiar.getPouch(), 1)) {
			return true;
		}
		/*
		 * Delete pouch
		 */
		ItemAssistant.deleteItemFromInventory(player, familiar.getPouch(), 1);
		/*
		 * Deduct points
		 */
		player.baseSkillLevel[ServerConstants.SUMMONING] -= familiar.getCost();
		player.skillTabMainToUpdate.add(ServerConstants.SUMMONING);
		/*
		 * Sets the duration
		 */
		player.getSummoning().setDuration(familiar.getDuration());
		/*
		 * Adds experiencing for use
		 */
		// Skilling.addSkillExperience(player, (int)
		// familiar.getUseExperience(), ServerConstants.SUMMONING, false);
		/*
		 * Spawns familiar
		 */
		spawnFamiliar(player, familiar);
		return true;
	}

	/**
	 * Picking up pet
	 * 
	 * @param player the player
	 * @param npc the npc
	 * @param id the id
	 * @return picked up pet
	 */
	public static boolean pickupPet(Player player, Npc npc, int id) {
		/*
		 * The pet
		 */
		int pet = -1;
		/*
		 * No pet
		 */
		if (pet == -1) {
			return false;
		}
		/*
		 * No space
		 */
		if (ItemAssistant.getFreeInventorySlots(player) == 0) {
			player.playerAssistant.sendMessage("Not enough space in your inventory.");
			return true;
		}
		/*
		 * Not owned
		 */
		if (npc.getNpcPetOwnerId() != player.getPlayerId()) {
			player.playerAssistant.sendMessage("This is not your pet.");
			return true;
		}
		player.startAnimation(827);
		ItemAssistant.addItem(player, pet, 1);
		player.playerAssistant.sendMessage("You pick up your pet.");
		Pet.deletePet(npc);
		player.setPetSummoned(false);
		player.setPetId(0);
		return true;
	}

	/**
	 * Sending the familiar to the world
	 *
	 * @param player the player
	 * @param familiar the familiar
	 */
	public static void spawnFamiliar(Player player, SummoningFamiliar familiar) {
		/*
		 * Spawns the npc
		 */
		SummoningFamiliarNpc summoned = summon(player, familiar);
		/*
		 * Not valid
		 */
		if (summoned == null) {
			return;
		}
		/*
		 * Sets familiar
		 */
		player.getSummoning().setFamiliar(summoned);
		/*
		 * Send the interface
		 */
		sendFamiliarInterface(player, familiar);
		/*
		 * Update combat
		 */
		player.setAppearanceUpdateRequired(true);
		InterfaceAssistant.updateCombatLevel(player);
		/*
		 * The task
		 */
		player.getEventHandler().addEvent(player, new CycleEvent<Entity>() {
			/*
			 * The cycle
			 */
			int cycle = 0;

			@Override
			public void execute(CycleEventContainer<Entity> container) {
				/*
				 * No familiar
				 */
				if (player.getSummoning().getFamiliar() == null) {
					container.stop();
					return;
				}
				/*
				 * Deduct summoning points every minute
				 */
				if (cycle % DEDUCTION_TICKS == 1) {
					/*
					 * Decrease duration
					 */
					if (player.getSummoning().getDuration() > 0) {
						player.getSummoning().setDuration(player.getSummoning().getDuration() - 1);
					}
					/*
					 * Last minute
					 */
					if (player.getSummoning().getDuration() == 1) {
						player.getPA()
								.sendMessage("You have 1 minute left before your familiar vanishes");
					} else if (player.getSummoning().getDuration() == 0) {
						player.getPA().sendMessage("Your familiar has vanished.");
						/*
						 * Special available
						 */
						if (familiar.getSummoningFamiliarSpecialAbility() != null) {
							/*
							 * Bob familiar
							 */
							if (familiar.isBoB()) {
								Pet.deletePet(summoned);
								familiar.getBoB().dropItems(player, summoned);
								reset(player);
								container.stop();
								return;
							}
						}
					}
					/*
					 * Decreasing skill
					 */
					if (player.baseSkillLevel[ServerConstants.SUMMONING] > 0) {
						player.baseSkillLevel[ServerConstants.SUMMONING] -= 1;
						player.skillTabMainToUpdate.add(ServerConstants.SUMMONING);
					}
					sendFamiliarInterfaceText(player);
				}
				/*
				 * Healing ability
				 */
				if (cycle % HEALING_TICKS == 1) {
					if (familiar.getSummoningFamiliarSpecialAbility() != null) {
						familiar.getSummoningFamiliarSpecialAbility().cycle(player);
					}
				}
				/*
				 * Reset from combat
				 */
				if (player.getPlayerIdAttacking() < 1 && player.getNpcIdAttacking() < 1) {
					/*
					 * Change to non combat npc
					 */
					if (summoned.transformId != familiar.getNpc()) {
						summoned.transform(familiar.getNpc());
					}
					/*
					 * Stop attacking
					 */
					summoned.underAttack = false;
					summoned.setKillerId(-1);
				} else
				/*
				 * Attacking player
				 */
				if (player.getPlayerIdAttacking() > 0) {
					/*
					 * The victim player
					 */
					Player victim = PlayerHandler.players[player.getPlayerIdAttacking()];
					/*
					 * Invalid victim
					 */
					if (victim == null) {
						return;
					}
					setTarget(summoned, familiar, victim.getX(), victim.getY(), victim.getPlayerId());
				} else if (player.getNpcIdAttacking() > 0) {
					/*
					 * The victim player
					 */
					Npc victim = NpcHandler.npcs[player.getNpcIdAttacking()];
					/*
					 * Invalid victim
					 */
					if (victim == null) {
						return;
					}
					setTarget(summoned, familiar, victim.getX(), victim.getY(), victim.npcIndex);
				}
				cycle++;
			}

			@Override
			public void stop() {

			}
		}, 1);
	}

	/**
	 * Renewing familiars
	 *
	 * @param player the player
	 */
	private static void renewFamiliar(Player player) {
		/*
		 * No familiar
		 */
		if (player.getSummoning().getFamiliar() == null) {
			player.getPA().sendMessage("You don't have a familiar summoned at the moment.");
			return;
		}
		/*
		 * Not ready
		 */
		if (player.getSummoning().getDuration() > MINIMUM_DURATION) {
			player.getPA().sendMessage("Familiars can only be renewed once they have gone below "
					+ MINIMUM_DURATION + " minutes left.");
			return;
		}
		/*
		 * The familiar
		 */
		SummoningFamiliar familiar = player.getSummoning().getFamiliar().getFamiliar();
		/*
		 * Doesn't have pouch to renew
		 */
		if (!ItemAssistant.hasItemAmountInInventory(player, familiar.getPouch(), 1)) {
			player.getPA()
					.sendMessage("You don't have the right summoning pouch to rewnew your familiar.");
			return;
		}
		/*
		 * Delete pouch
		 */
		ItemAssistant.deleteItemFromInventory(player, familiar.getPouch(), 1);
		/*
		 * Sets the duration
		 */
		player.getSummoning().setDuration(familiar.getDuration());
		/*
		 * Restores hitpoints
		 */
		player.getSummoning().getFamiliar().heal(
				NpcDefinition.getDefinitions()[player.getSummoning().getFamiliar().npcType].hitPoints);
		player.getPA().sendMessage("You renew your familiars duration and health.");
		sendFamiliarInterfaceText(player);
	}

	/**
	 * Performing special
	 *
	 * @param player the player
	 */
	public static void performSpecial(Player player) {
		/*
		 * Dead
		 */
		if (player.getDead()) {
			return;
		}
		/*
		 * The familiar
		 */
		SummoningFamiliar familiar = player.getSummoning().getFamiliar().getFamiliar();
		/*
		 * No familiar
		 */
		if (familiar == null) {
			return;
		}
		/*
		 * Doesn't have scroll
		 */
		if (!ItemAssistant.hasItemAmountInInventory(player, familiar.getScroll(), 1)) {
			player.getPA().sendMessage(
					"You don't have the right summoning scroll to perform the special ability.");
			return;
		}
		/*
		 * Special amount
		 */
		int specRequired = familiar.getSummoningFamiliarSpecialAbility().getPercentageRequired();
		/*
		 * The special to remove
		 */
		int special = (int) ((player.playerEquipment[ServerConstants.CAPE_SLOT] == SPIRIT_CAPE
				|| PreEocCompletionistCape.capeEquipped(player))
						? specRequired * SPECIAL_DEDUCTION_PERCENTAGE : specRequired);
		/*
		 * Not enough special
		 */
		if (player.getSummoning().getSpecial() < special
				|| player.getSummoning().getSpecial() - special < 0) {
			player.getPA()
					.sendMessage("You don't have enough special energy to perform a special attack.");
			return;
		}
		/*
		 * Stops all action
		 */
		player.playerAssistant.stopAllActions();
		/*
		 * Performs special
		 */
		if (familiar.getSummoningFamiliarSpecialAbility().sendScroll(player)) {
			/*
			 * Deletes the scroll
			 */
			ItemAssistant.deleteItemFromInventory(player, familiar.getScroll(), 1);
			/*
			 * Delegates special
			 */
			player.getSummoning().setSpecial(player.getSummoning().getSpecial() - special);
		}
	}

	/**
	 * Opening the storage for BoB
	 * 
	 * @param player the player
	 */
	public static void openBobStorage(Player player) {
		/*
		 * The familiar
		 */
		SummoningFamiliar familiar = player.getSummoning().getFamiliar().getFamiliar();
		/*
		 * No familiar
		 */
		if (familiar == null) {
			return;
		}
		/*
		 * Not BoB
		 */
		if (!familiar.isBoB()) {
			return;
		}
		/*
		 * Opens the storage
		 */
		familiar.getBoB().openBoBStorage(player);
	}

	/**
	 * Sets the target for summoning
	 *
	 * @param summoned the sumonned familiar
	 * @param familiar the familiar data
	 * @param x the victim x
	 * @param y the victim y
	 * @param id the id
	 */
	private static void setTarget(Npc summoned, SummoningFamiliar familiar, int x, int y, int id) {
		/*
		 * In multi area
		 */
		// if (Area.inMulti(x, y)) {
		/*
		 * Transform to combat npc
		 */
		if (summoned.transformId != familiar.getNpc() + 1) {
			summoned.transform(familiar.getNpc() + 1);
		}
		/*
		 * Start attacking
		 */
		summoned.underAttack = true;
		summoned.setKillerId(id);
		// }
	}

	/**
	 * Summon the pet.
	 *
	 * @param player The player who summoned the pet.
	 * @param npcType The pet being summoned.
	 * @param x The x coord of the pet.
	 * @param y The x coord of the pet.
	 * @param heightLevel The height of the pet.
	 */
	public static SummoningFamiliarNpc summon(Player player, SummoningFamiliar familiar) {
		int slot = -1;
		for (int i = 1; i < NpcHandler.NPC_INDEX_OPEN_MAXIMUM; i++) {
			if (NpcHandler.npcs[i] == null) {
				slot = i;
				break;
			}
		}
		if (slot == -1) {
			return null;
		}
		SummoningFamiliarNpc npc = new SummoningFamiliarNpc(slot, familiar);
		int x = player.getX();
		int y = player.getY();
		if (Region.pathUnblocked(x, y, player.getHeight(), "SOUTH")) {
			y--;
		} else if (Region.pathUnblocked(x, y, player.getHeight(), "WEST")) {
			x--;
		} else if (Region.pathUnblocked(x, y, player.getHeight(), "EAST")) {
			x++;
		} else if (Region.pathUnblocked(x, y, player.getHeight(), "NORTH")) {
			y++;
		}
		npc.setX(x);
		npc.setY(y);
		npc.setSpawnPositionX(x);
		npc.setSpawnPositionY(y);
		npc.setHeight(player.getHeight());
		npc.setNpcPetOwnerId(player.getPlayerId());
		npc.setSpawnedBy(player.getPlayerId());
		npc.facePlayer(player.getPlayerId());
		npc.setFamiliar(true);
		npc.faceAction = "";
		npc.summoned = true;
		NpcHandler.npcs[slot] = npc;
		player.setPetId(familiar.getNpc());
		player.setPetSummoned(true);
		return npc;
	}

	/**
	 * Sends the interface
	 *
	 * @param player the player
	 * @param familiar the familiar
	 */
	private static void sendFamiliarInterface(Player player, SummoningFamiliar familiar) {
		/*
		 * Set side bar
		 */
		player.getPA().setSidebarInterface(13, FAMILIAR_INTERFACE);
		/*
		 * Display head
		 */
		player.getPA().sendFrame75(familiar.getNpc(), FAMILIAR_HEAD_ID);
		/*
		 * Check for name
		 */
		if (NpcDefinition.getDefinitions()[familiar.getNpc()] != null) {
			/*
			 * The name
			 */
			String npcName = NpcDefinition.getDefinitions()[familiar.getNpc()].name;
			/*
			 * Display name
			 */
			player.getPA().sendFrame126(npcName, FAMILIAR_NAME_LINE_ID);
		}
		/*
		 * Send text
		 */
		sendFamiliarInterfaceText(player);
	}

	/**
	 * Sends the interface
	 *
	 * @param player the player
	 * @param pet the pet
	 */
	public static void sendPetInterface(Player player, int id) {
		/*
		 * Set side bar
		 */
		player.getPA().setSidebarInterface(13, PET_INTERFACE);
		/*
		 * Display head
		 */
		player.getPA().sendFrame75(id, PET_HEAD_ID);
		/*
		 * Check for name
		 */
		if (NpcDefinition.getDefinitions()[id] != null) {
			/*
			 * The name
			 */
			String npcName = NpcDefinition.getDefinitions()[id].name;
			/*
			 * Display name
			 */
			player.getPA().sendFrame126(npcName, PET_NAME_LINE_ID);
		}
		/*
		 * Send text
		 */
		sendPetInterfaceText(player);
	}

	/**
	 * Sends the interface text
	 *
	 * @param player the player
	 */
	public static void sendFamiliarInterfaceText(Player player) {
		player.getPA().sendFrame126(
				player.baseSkillLevel[ServerConstants.SUMMONING] + "/"
						+ Skilling
								.getLevelForExperience(player.skillExperience[ServerConstants.SUMMONING]),
				39_574);
		player.getPA().sendFrame126(player.getSummoning().getDuration() + " min", 39_575);
	}


	/**
	 * Sends the interface text
	 *
	 * @param player the player
	 */
	public static void sendPetInterfaceText(Player player) {
		/*
		 * No pet
		 */
		if (player.getSummoningPet().getPet() == null) {
			player.getPA().sendFrame126("1@whi@0%", HUNGER_LINE_ID);
			player.getPA().sendFrame126("@whi@N/A", GROWTH_LINE_ID);
		} else {
			/*
			 * The pet
			 */
			GrowingPet pet =
					SummoningPetManager.getPet(player, player.getSummoningPet().getPet().npcType);
			/*
			 * Invalid pet
			 */
			if (pet == null) {
				player.getPA().sendFrame126("@whi@0%", HUNGER_LINE_ID);
				player.getPA().sendFrame126("@whi@N/A", GROWTH_LINE_ID);
			} else {
				player.getPA().sendFrame126("@whi@" + format.format(pet.getHunger()) + "%",
						HUNGER_LINE_ID);
				player.getPA().sendFrame126("@whi@" + format.format(pet.getGrowth()), GROWTH_LINE_ID);
			}
		}
	}

	/**
	 * Gets the skill bonus
	 * 
	 * @param player the player
	 * @param skill the skill
	 * @return the bonus
	 */
	public static double getSkillBonus(Player player, int skill) {
		/*
		 * Not pre eoc
		 */
		if (!GameType.isPreEoc()) {
			return 1.0;
		}
		/*
		 * The familiar
		 */
		SummoningFamiliarNpc npc = player.getSummoning().getFamiliar();
		/*
		 * Invalid familiar
		 */
		if (npc == null) {
			return 1.0;
		}
		/*
		 * The skill bonus
		 */
		return npc.getFamiliar().getSummoningFamiliarSpecialAbility().getSkillBonus(player, skill);
	}

	/**
	 * Reseting summoning
	 *
	 * @param player the player
	 */
	public static void reset(Player player) {
		player.getSummoningPet().setPet(null);
		player.getSummoning().setFamiliar(null);
		player.getPA().setSidebarInterface(13, -1);
		player.getPA().sendFrame75(-1, FAMILIAR_HEAD_ID);
		player.getPA().sendFrame75(-1, PET_HEAD_ID);
		player.getPA().sendFrame126("", 39_576);
		player.getPA().sendFrame126("", 39_574);
		player.getPA().sendFrame126("", 39_575);
		InterfaceAssistant.updateCombatLevel(player);
		player.setAppearanceUpdateRequired(true);
	}

	/**
	 * Handles the button interaction
	 *
	 * @param player the player
	 * @param button the button
	 * @return the interaction
	 */
	public static boolean handleButton(Player player, int button) {
		if (GameType.isOsrs()) {
			return false;
		}
		switch (button) {
			case 154_137:
			case 150_161:
				Pet.callFamiliar(player);
				return true;
			case 154_146:
			case 150_164:
				Pet.dismissFamiliar(player);
				return true;
			case 154_143:
				renewFamiliar(player);
				return true;
			case 154_156:
				performSpecial(player);
				return true;
			case 130_223:
				BoBSummoningFamiliarSpecialAbility.withdrawAll(player);
				return true;
		}
		return false;
	}

	/**
	 * Sets the special
	 *
	 * @return the special
	 */
	public int getSpecial() {
		return special;
	}

	/**
	 * Sets the special
	 *
	 * @param special the special
	 */
	public void setSpecial(int special) {
		this.special = special;
	}

	/**
	 * Sets the duration
	 *
	 * @return the duration
	 */
	public int getDuration() {
		return duration;
	}

	/**
	 * Sets the duration
	 *
	 * @param duration the duration
	 */
	public void setDuration(int duration) {
		this.duration = duration;
	}

	/**
	 * Sets the familiar
	 *
	 * @return the familiar
	 */
	public SummoningFamiliarNpc getFamiliar() {
		return familiar;
	}

	/**
	 * Sets the familiar
	 *
	 * @param familiar the familiar
	 */
	public void setFamiliar(SummoningFamiliarNpc familiar) {
		this.familiar = familiar;
	}
}
