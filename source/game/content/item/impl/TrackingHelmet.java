package game.content.item.impl;

import java.util.Arrays;
import core.GameType;
import game.content.dialogue.DialogueChain;
import game.content.item.ItemInteraction;
import game.content.item.ItemInteractionComponent;
import game.content.miscellaneous.Transform;
import game.entity.Entity;
import game.entity.attributes.AttributeKey;
import game.entity.attributes.PermanentAttributeKey;
import game.entity.attributes.PermanentAttributeKeyComponent;
import game.npc.Npc;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.type.GameTypeIdentity;

/**
 * Handles the tracking helmets
 * 
 * @author 2012
 *
 */
@ItemInteractionComponent(identities = {@GameTypeIdentity(type = GameType.PRE_EOC, identity = {
		20795, 20796, 20797, 20798, 20799, 20800, 20801, 20802, 20803, 20804, 20805, 20806}),})
public class TrackingHelmet implements ItemInteraction {

	@PermanentAttributeKeyComponent
	public static final AttributeKey<Integer> EDGE_KILLS =
			new PermanentAttributeKey<Integer>(0, "t-edge-kills");

	@PermanentAttributeKeyComponent
	public static final AttributeKey<Integer> EDGE_DEATHS =
			new PermanentAttributeKey<Integer>(0, "t-edge-deaths");

	@PermanentAttributeKeyComponent
	public static final AttributeKey<Integer> DUEL_KILLS =
			new PermanentAttributeKey<Integer>(0, "t-duel-kills");

	@PermanentAttributeKeyComponent
	public static final AttributeKey<Integer> DUEL_DEATHS =
			new PermanentAttributeKey<Integer>(0, "t-duel-deaths");

	// anim 317 - Duellist Cap Brag (Version One) (T1)
	// anim 318 - Duellist Cap Brag (Version Two) (T2)
	// animGFX 507 91 - Duellist Cap Brag (Version Two) (T1)
	// animGFX 508 91 - Duellist Cap Brag (Version Two) (T2)
	// animGFX 509 91 - Duellist Cap Brag (Version Two) (T3)
	// animGFX 510 91 - Duellist Cap Brag (Version Two) (T4)
	// animGFX 511 91 - Duellist Cap Brag (Version Two) (T5)
	// animGFX 512 92 - Duellist Cap Brag (Version Three) (T1)
	// animGFX 513 92 - Duellist Cap Brag (Version Three) (T2)
	// animGFX 530 92 - Duellist Cap Brag (Version Three) (T3)
	// animGFX 531 92 - Duellist Cap Brag (Version Three) (T4)
	// animGFX 532 92 - Duellist Cap Brag (Version Three) (T5)
	// animGFX 533 92 - Duellist Cap Brag (Version Three) (T6)



	// anim 534 - Wildstalker Helmet Brag (Version One)
	// animGFX 412 121 - Wildstalker Helmet Brag (Version Two)
	// animGFX 361 122 - Wildstalker Helmet Brag (Version Three)

	/**
	 * Represents the helmets
	 */
	public enum Helmet {

		DUEL_ARENA_TIER_1(20_795, 0, 2_719),

		DUEL_ARENA_TIER_2(20_796, 10, 2_720),

		DUEL_ARENA_TIER_3(20_797, 100, 2_721),

		DUEL_ARENA_TIER_4(20_798, 500, 3_092),

		DUEL_ARENA_TIER_5(20_799, 2_000, 3_131),

		DUEL_ARENA_TIER_6(20_800, 5_000, 3_224),

		EDGEVILLE_TIER_1(20_801, 0, 2_216),

		EDGEVILLE_TIER_2(20_802, 10, 2_239),

		EDGEVILLE_TIER_3(20_803, 100, 2_358),

		EDGEVILLE_TIER_4(20_804, 500, 2_432),

		EDGEVILLE_TIER_5(20_805, 2_000, 2_433),

		EDGEVILLE_TIER_6(20_806, 5_000, 2_434),

		;

		/**
		 * The id
		 */
		private final int id;

		/**
		 * The required kills
		 */
		private final int requiredKills;

		/**
		 * The transforming id
		 */
		private final int transform;

		/**
		 * Represents a duellist helm
		 * 
		 * @param id the id
		 * @param requiredKills the kill required
		 * @param transform the transform id
		 */
		Helmet(int id, int requiredKills, int transform) {
			this.id = id;
			this.requiredKills = requiredKills;
			this.transform = transform;
		}

		/**
		 * Gets the id
		 *
		 * @return the id
		 */
		public int getId() {
			return id;
		}

		/**
		 * Gets the requiredKills
		 *
		 * @return the requiredKills
		 */
		public int getRequiredKills() {
			return requiredKills;
		}

		/**
		 * Gets the transform
		 *
		 * @return the transform
		 */
		public int getTransform() {
			return transform;
		}

		/**
		 * Checking whether is wildy stalker helm
		 * 
		 * @return the helm
		 */
		public boolean isWildStalker() {
			return ordinal() >= 7;
		}

		/**
		 * Gets the helm
		 * 
		 * @param id for id
		 * @return the helm
		 */
		public static Helmet forId(int id) {
			return Arrays.stream(values()).filter(c -> c.getId() == id).findFirst().orElse(null);
		}
	}

	@Override
	public boolean canEquip(Player player, int id, int slot) {
		/*
		 * Not pre eoc
		 */
		if (!GameType.isPreEoc()) {
			return false;
		}
		/*
		 * The helm
		 */
		Helmet helm = Helmet.forId(id);
		/*
		 * Invalid helm
		 */
		if (helm == null) {
			return false;
		}
		/*
		 * The kills
		 */
		int kills =
				player.getAttributes().getOrDefault(helm.isWildStalker() ? EDGE_KILLS : DUEL_KILLS);
		/*
		 * Not enough kills
		 */
		if (helm.getRequiredKills() > kills) {
			/*
			 * The location
			 */
			String loc = helm.isWildStalker() ? "Edgeville" : "Duel Arena";
			player.getPA().sendMessage("You don't have enough kills to equip this helm. You need "
					+ (helm.getRequiredKills() - kills) + " more " + loc + " kills.");
			return false;
		}
		return true;
	}

	/**
	 * Sending the options
	 * 
	 * @param player the player
	 * @param id the id
	 */
	private static void sendOptions(Player player, int id) {
		/*
		 * The helm
		 */
		Helmet helm = Helmet.forId(id);
		/*
		 * Invalid helm
		 */
		if (helm == null) {
			return;
		}
		/*
		 * The dialogue
		 */
		player.setDialogueChain(new DialogueChain().option((p, option) -> {
			/*
			 * Close interface
			 */
			player.getPA().closeInterfaces(true);
			/*
			 * View kdr
			 */
			if (option == 1) {
				/*
				 * The kills
				 */
				int kills = player.getAttributes()
						.getOrDefault(helm.isWildStalker() ? EDGE_KILLS : DUEL_KILLS);
				/*
				 * The deaths
				 */
				int deaths = player.getAttributes()
						.getOrDefault(helm.isWildStalker() ? EDGE_DEATHS : DUEL_DEATHS);
				player.getPA().sendMessage("Kills: " + kills + ". Deaths: " + deaths);
				/*
				 * Transform
				 */
			} else if (option == 2) {
				/*
				 * Transform
				 */
				Transform.npcTransform(player, helm.getTransform());

				player.getEventHandler().addEvent(player, new CycleEvent<Entity>() {

					@Override
					public void execute(CycleEventContainer<Entity> container) {
						container.stop();
					}

					@Override
					public void stop() {
						Transform.unTransform(player);
					}
				}, 5);
			}
		}, "Select an Option", "Check KDR", "Transform")).start(player);
	}

	@Override
	public boolean sendItemAction(Player player, int id, int type) {
		if (type == 2) {
			if (id >= 20795 && id <= 20806) {
				sendOptions(player, id);
				return true;
			}
		}
		return false;
	}

	@Override
	public void operate(Player player, int id) {

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
