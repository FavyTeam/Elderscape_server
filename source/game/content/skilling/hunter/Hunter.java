package game.content.skilling.hunter;

import core.Server;
import core.ServerConstants;
import game.content.skilling.Skill;
import game.content.skilling.hunter.technique.HunterTechnique;
import game.content.skilling.hunter.trap.HunterTrap;
import game.content.skilling.hunter.trap.impl.ButterflyNetTrap;
import game.entity.Entity;
import game.item.ItemAssistant;
import game.item.ItemDefinition;
import game.npc.Npc;
import game.player.Area;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.movement.Movement;

/**
 * Created by Jason MacKeigan on 2018-01-22 at 11:15 AM
 */
public class Hunter {

	public void lay(Player player, HunterStyle style, HunterTrapCreationMethod method) {
		final int hunterLevel = player.baseSkillLevel[Skill.HUNTER.getId()];

		if (hunterLevel < style.getEquipment().getLevelRequired()) {
			player.getPA().sendMessage(String.format("You need a hunter level of %s to lay this trap.", style.getEquipment().getLevelRequired()));
			return;
		}

		if (!Area.inAHuntingArea(player.getX(), player.getY(), player.getHeight())) {
			player.getPA().sendMessage("You need to be in a hunting area to lay a trap.");
			return;
		}

		if (HunterTrapObjectManager.getSingleton().getObjects().stream().anyMatch(trap -> trap.objectX == player.getX()
		                                                                                  && trap.objectY == player.getY()
		                                                                                  && trap.height == player.getHeight())) {
			player.getPA().sendMessage("A trap already exists here.");
			return;
		}

		if (Server.objectManager.exists(player.getX(), player.getY(), player.getHeight())) {
			player.getPA().sendMessage("Your path is blocked.");
			return;
		}

		if (method == HunterTrapCreationMethod.INVENTORY && !ItemAssistant.hasItemInInventory(player, style.getEquipment().getItemId())) {
			player.getPA().sendMessage("You need a " + ItemDefinition.getDefinitions()[style.getEquipment().getItemId()].name + " to do this.");
			return;
		}

		if (method == HunterTrapCreationMethod.GROUND && !Server.itemHandler.itemExists(style.getEquipment().getItemId(), player.getX(), player.getY())) {
			player.getPA().sendMessage("There is no trap underneath you to lay.");
			return;
		}

		if (player.getLocalNpcs().stream().anyMatch(npc -> npc != null
		                                                   && npc.distanceTo(player.getX(), player.getY()) <= 1)) {
			player.getPA().sendMessage("You cannot setup a trap this close to a creature.");
			return;
		}
		int maximumNumberOfTrap = hunterLevel >= 80 ? 5 : hunterLevel >= 60 ? 4 : hunterLevel >= 40 ? 3 : hunterLevel >= 20 ? 2 : 1;

		if (player.getWildernessLevel() > 0) {
			maximumNumberOfTrap++;
		}

		if (HunterTrapObjectManager.getSingleton().getObjects().stream().filter(trap ->
		player.getPlayerName().equalsIgnoreCase(trap.getOwner())).count() >= maximumNumberOfTrap) {
			player.getPA().sendMessage("You have already placed the maximum number of traps that you can use at your level.");
			return;
		}
		player.doingActionEvent(4);
		player.getEventHandler().addEvent(player, new CycleEvent<Entity>() {
			int i = 0;

			@Override
			public void execute(CycleEventContainer<Entity> container) {
				i++;
				switch (i) {
					case 1:
						if (method == HunterTrapCreationMethod.INVENTORY) {
							if (!ItemAssistant.hasItemInInventory(player, style.getEquipment().getItemId())) {
								player.getPA().sendMessage("You need a " + ItemDefinition.getDefinitions()[style.getEquipment().getItemId()].name + " to do this.");
								container.stop();
								return;
							}
							Server.itemHandler.createGroundItem(player, style.getEquipment().getItemId(), player.getX(), player.getY(), player.getHeight(), 1,
									false, 0, true, "", "", "", "", "Hunter trap " + style.getEquipment().getItemId());
							ItemAssistant.deleteItemFromInventory(player, style.getEquipment().getItemId(), 1);
						} else if (!Server.itemHandler.itemExists(style.getEquipment().getItemId(), player.getX(), player.getY())) {
							player.getPA().sendMessage("There is no trap underneath you to lay.");
							container.stop();
							return;
						}
						break;
					case 2:
						player.startAnimation(5208);
						break;
					case 5:
						player.resetAnimation();
						Movement.movePlayerFromUnderEntity(player);
						HunterTechnique technique = style.getTechnique();
						HunterTrap trap = technique.createTrap(player);
						Server.itemHandler.removeGroundItem(player, style.getEquipment().getItemId(), player.getX(), player.getY(), 1, false);
						HunterTrapObjectManager.getSingleton().add(trap);
						Server.objectManager.addObject(trap);
						container.stop();
						break;
				}
			}

			@Override
			public void stop() {
			}
		}, 1);
	}

	public void net(Player player, Npc npc, HunterStyle style) {
		if (!(npc instanceof HunterCreature)) {
			return;
		}
		final int hunterLevel = player.baseSkillLevel[Skill.HUNTER.getId()];

		if (hunterLevel < style.getEquipment().getLevelRequired()) {
			player.getPA().sendMessage(String.format("You need a hunter level of %s to use this net.", style.getEquipment().getLevelRequired()));
			return;
		}

		if (!ItemAssistant.hasItemInInventory(player, style.getEquipment().getItemId())
				&& !ItemAssistant.hasItemEquippedSlot(player, style.getEquipment().getItemId(), ServerConstants.WEAPON_SLOT)) {
			player.getPA().sendMessage("You need a " + ItemDefinition.getDefinitions()[style.getEquipment().getItemId()].name + " to do this.");
			return;
		}
		if (!ItemAssistant.hasItemInInventory(player, 11260)) {
			player.getPA().sendMessage("You need an impling jar to do this.");
			return;
		}
		HunterCreature creature = (HunterCreature) npc;

		if (hunterLevel < creature.levelRequired()) {
			player.getPA().sendMessage(String.format("You need a hunter level of %s to catch this.", creature.levelRequired()));
			return;
		}

		ButterflyNetTrap trap = new ButterflyNetTrap();

		boolean trapped = trap.trap(player, creature);

		if (trapped) {
			creature.killIfAlive();
		}
		player.startAnimation(6606);
	}

}
