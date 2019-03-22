package game.content.item.impl;

import core.ServerConstants;
import core.GameType;
import game.content.item.ItemInteraction;
import game.content.item.ItemInteractionComponent;
import game.content.skilling.Skilling;
import game.entity.Entity;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.npc.pet.Pet;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.type.GameTypeIdentity;

/**
 * Handles the max cape
 * 
 * @author 2012
 *
 */
@ItemInteractionComponent(
		identities = {@GameTypeIdentity(type = GameType.PRE_EOC, identity = {20_767, 20_768}),})
public class MaxCape implements ItemInteraction {

	// anim 1179 - Max Cape Emote (Step One)
	// anim 1180 - Max Cape Emote (Step Two) (Inferno adze and Saw)
	// anim 1181 - Max Cape Emote (Step Three) (Crystal Pick)
	// anim 1182 - Max Cape Emote (Step Four) (Hatchet and Watering Can)
	// anim 1250 - Max Cape Emote (Step Five) (Smithing and Crafting)
	// animGFX 1251 4253 - Max Cape Emote (Step Six) (Prepare For Battle)
	// animGFX 1291 1505 1686 - Max Cape Emote (Step Seven) (Fighting)

	/**
	 * The npc
	 */
	private static final int NPC = 1224;

	/**
	 * Whether can equip the cape
	 * 
	 * @param player the player
	 * @return the cape
	 */
	public static boolean eligible(Player player) {
		/*
		 * Checks levels
		 */
		for (int i = 0; i < player.skillExperience.length; i++) {
			/*
			 * Below requirement
			 */
			if (Skilling.getLevelForExperience(player.skillExperience[i]) < 99) {
				player.getPA().sendMessage("You haven't reached the 99 in all skills. Your "
						+ ServerConstants.SKILL_NAME[i] + " is too low.");
				return false;
			}
		}
		return true;
	}


	private static void perform(Player player) {
		// TODO:
		final Npc npc = NpcHandler.spawnNpc(NPC, player.getX(), player.getY(), player.getHeight());

		player.getEventHandler().addEvent(player, new CycleEvent<Entity>() {

			int cycle = 0;

			@Override
			public void execute(CycleEventContainer<Entity> container) {
				switch (cycle) {
					case 0:
						npc.facePlayer(player.getPlayerId());
						player.faceUpdate(npc.npcIndex);
						break;
					case 2:
						NpcHandler.startAnimation(npc, 1434);
						npc.gfx0(1482);
						// player.startAnimation(1179);
						break;
					case 3:
						NpcHandler.startAnimation(npc, 1498);
						player.startAnimation(1181);
						break;
					case 4:
						NpcHandler.startAnimation(npc, 1498);
						player.startAnimation(1182);
						break;
					case 5:
						NpcHandler.startAnimation(npc, 1448);
						player.startAnimation(1250);
						break;
					case 6:
						player.startAnimation(1251);
						player.gfx0(1499);
						NpcHandler.startAnimation(npc, 1454);
						npc.gfx0(1504);
						break;
					case 11:
						player.startAnimation(1251);
						player.gfx0(1499);
						NpcHandler.startAnimation(npc, 1454);
						npc.gfx0(1504);
						break;

					case 15:
						Pet.deletePet(npc);
						container.stop();
						break;
				}
				cycle++;
				player.getPA().sendMessage("cycle: " + cycle);
			}

			@Override
			public void stop() {

			}
		}, 1);
	}

	@Override
	public void operate(Player player, int id) {
		if (id == 20_767 || id == 20_768) {
			perform(player);
		}
	}

	@Override
	public boolean canEquip(Player player, int id, int slot) {
		return true;
	}


	@Override
	public boolean sendItemAction(Player player, int id, int type) {
		return false;
	}


	@Override
	public boolean useItem(Player player, int id, int useWith) {
		return false;
	}

	@Override
	public boolean useItemOnObject(Player player, int id, int object) {
		return false;
	}

	@Override
	public boolean useItemOnNpc(Player player, int id, Npc npc) {
		return false;
	}

	@Override
	public boolean dropItem(Player player, int id) {
		return false;
	}
}
