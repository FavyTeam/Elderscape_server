package game.content.skilling.summoning.pet.impl;

import java.util.Arrays;
import core.GameType;
import game.content.dialogue.DialogueChain;
import game.content.dialogueold.DialogueHandler.FacialAnimation;
import game.content.item.ItemInteraction;
import game.content.item.ItemInteractionComponent;
import game.entity.attributes.AttributeKey;
import game.entity.attributes.PermanentAttributeKey;
import game.entity.attributes.PermanentAttributeKeyComponent;
import game.item.ItemAssistant;
import game.item.ItemDefinition;
import game.npc.Npc;
import game.npc.data.NpcDefinition;
import game.npc.pet.Pet;
import game.player.Player;
import game.type.GameTypeIdentity;
import utility.Misc;

/**
 * Handles the baby troll pet
 * 
 * @author 2012
 *
 */
@ItemInteractionComponent(
		identities = {@GameTypeIdentity(type = GameType.PRE_EOC, identity = 23030),})
public class BabyTroll implements ItemInteraction {

	/**
	 * The baby troll
	 */
	private static final int BABY_TROLL = 14846;

	@PermanentAttributeKeyComponent
	/**
	 * The trolls name
	 */
	public static final AttributeKey<String> TROLL_NAME =
			new PermanentAttributeKey<String>("unnamed", "troll-name");

	/**
	 * The alternative possible names
	 */
	public enum AlternativeName {

		TROLL_GENERAL_BONES(0, new String[] {"My own dad"}),

		TROLL_LEUTENANT_BONES(0, new String[] {"My dad's friend"}),

		VALENTINE_HEART(0, new String[] {"Angel", "Baby", "Boo", "Cutie", "Darling", "Gorgeous",
				"Honeybunch", "Lovely", "Pet", "Snookums", " Sugar", " Sweetheart", "Sweetie Treacle"}),

		VALENTINE_NOTE(0, new String[] {"Angel", "Babydoll", "Blossom", "Bubbles", "Cuddlebug",
				"Dearest", "Ducky", "Flower", "Honeybear", "Jellybean", "Pooky", "Tootsie"}),

		DOUBLOONS(0, new String[] {"Avast", "Cabin Boy", "Davey", "Doubloons", "Kraken", "Yarrr"}),

		ROSE_PETALS(0, new String[] {"baby-bugga-boo", "bae", "big hippo", "chunky monkey", "cupid",
				"hater", "hot stuff", "lover", "muffin", "sugar", "tootsie wootsy", "winky dink"}),

		;

		/**
		 * The id
		 */
		private int id;

		/**
		 * The names
		 */
		private String[] names;

		/**
		 * Represents a name
		 * 
		 * @param id the id
		 * @param names the names
		 */
		AlternativeName(int id, String[] names) {
			this.id = id;
			this.names = names;
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
		 * Gets the names
		 *
		 * @return the names
		 */
		public String[] getNames() {
			return names;
		}

		/**
		 * Gets the alternative name by id
		 * 
		 * @param id the id
		 * @return the name
		 */
		public static AlternativeName forId(int id) {
			return Arrays.stream(values()).filter(c -> c.getId() == id).findFirst().orElse(null);
		}
	}

	/**
	 * The troll gene dialogue
	 * 
	 * @param player the player
	 * @param npc the npc troll
	 */
	private static void sendTrollGeneralDialogue(Player player, Npc npc) {
		/*
		 * The troll
		 */
		NpcDefinition babyTroll = NpcDefinition.DEFINITIONS[BABY_TROLL];
		/*
		 * The dialogue
		 */
		player.setDialogueChain(new DialogueChain()
				.player(FacialAnimation.DISTRESSED, "What am I doing? I got these bones",
						"from this poor baby's father!")
				.npc(babyTroll, FacialAnimation.SAD, "Da...da?").player(FacialAnimation.CALM_1,
						"This is terrible, and I am a monster", "for thinking of doing it!")
				.option((p, option) -> {
					if (option == 1) {
						player.setDialogueChain(new DialogueChain()
								.player(FacialAnimation.BOWS_HEAD_WHILE_SAD,
										"Although, having said that...")
								.statement("The player feeds the bones to the baby troll.")).start(player);
						setTrollName(player, AlternativeName.TROLL_GENERAL_BONES.getId(),
								AlternativeName.TROLL_GENERAL_BONES.getNames()[0], npc);
					}
				}, "Select an Option", "Do it anyway")).start(player);
	}

	/**
	 * Sends random conversation
	 * 
	 * @param player the player
	 */
	public static void sendRandomConversation(Player player) {
		/*
		 * The troll
		 */
		NpcDefinition babyTroll = NpcDefinition.DEFINITIONS[BABY_TROLL];
		/*
		 * The random dialogue
		 */
		int random = Misc.random(5);
		/*
		 * The dialogues
		 */
		if (random == 1) {
			player.setDialogueChain(
					new DialogueChain().npc(babyTroll, FacialAnimation.CALM_1, "Dadda?")
							.player(FacialAnimation.CALM_1, "No, I'm not your dad.")
							.npc(babyTroll, FacialAnimation.CALM_1, "Momma?")
							.player(FacialAnimation.CALM_1, "No, I'm not your mother either!")
							.npc(babyTroll, FacialAnimation.CALM_1, "Food?")
							.player(FacialAnimation.CALM_1, "I am not food!")
							.npc(babyTroll, FacialAnimation.CALM_1, "I hungry.")
							.player(FacialAnimation.CALM_1, "Aren't you always?"))
					.start(player);
		} else if (random == 2) {
			player.setDialogueChain(
					new DialogueChain().npc(babyTroll, FacialAnimation.CALM_1, "We fight?")
							.player(FacialAnimation.CALM_1, "You're too young to fight!")
							.npc(babyTroll, FacialAnimation.CALM_1, "I big and scary!")
							.player(FacialAnimation.CALM_1, "You have to eat a lot more vegetables first.")
							.npc(babyTroll, FacialAnimation.CALM_1, "I no want to be named vegetable."))
					.start(player);
		} else if (random == 3) {
			player.setDialogueChain(
					new DialogueChain().npc(babyTroll, FacialAnimation.CALM_1, "Sleepy?")
							.player(FacialAnimation.CALM_1, "Aww, little baby is sleepy?")
							.npc(babyTroll, FacialAnimation.CALM_1, "*Grrr*")
							.player(FacialAnimation.CALM_1, "Uuu, I'm sooo scared.")
							.npc(babyTroll, FacialAnimation.CALM_1, "I eat you!"))
					.start(player);
		} else {
			player.setDialogueChain(new DialogueChain().npc(babyTroll, FacialAnimation.CALM_1, "Food?")
					.player(FacialAnimation.CALM_1, "Is that all you think about? Food?")
					.npc(babyTroll, FacialAnimation.CALM_1, "Food!")
					.player(FacialAnimation.CALM_1, "I'll see what I can find..")).start(player);
		}
	}

	/**
	 * Sets the trolls name
	 * 
	 * @param player the player
	 * @param id the id
	 * @param trollName the troll name
	 * @param npc the npc
	 */
	private static void setTrollName(Player player, int id, String trollName, Npc npc) {
		ItemAssistant.deleteItemFromInventory(player, id, 1);
		player.getAttributes().put(TROLL_NAME, trollName);
		player.getPA().sendMessage("You have named your pet troll: " + trollName);
		npc.forceChat("YUM! Me Like " + trollName + "!");
	}

	/**
	 * Naming the troll
	 * 
	 * @param player the player
	 * @param id the id
	 * @param npc the npc
	 * @return named the troll
	 */
	public static boolean nameTroll(Player player, int id, Npc npc) {
		/*
		 * The pet troll
		 */
		if (npc.npcType == BABY_TROLL) {
			/*
			 * The trolls name
			 */
			String name = player.getAttributes().getOrDefault(TROLL_NAME);
			/*
			 * Not named
			 */
			if (!name.equalsIgnoreCase("unnamed")) {
				player.getPA().sendMessage("You have already named your pet");
				// return true;
			}
			/*
			 * Untradeables
			 */
			if (ItemAssistant.cannotTradeAndStakeItemItem(id)) {
				player.getPA().sendMessage("Your pet troll refuses to eat that..");
				npc.forceChat("Eww..");
				return true;
			}
			/*
			 * The item definition
			 */
			ItemDefinition def = ItemDefinition.DEFINITIONS[id];
			/*
			 * Invalid item
			 */
			if (def == null) {
				player.getPA().sendMessage("You can't seem to use this item to feed your troll..");
				return true;
			}
			/*
			 * The new name
			 */
			String newName = def.name;
			/*
			 * Checks for alternative name
			 */
			AlternativeName altName = AlternativeName.forId(id);
			/*
			 * Found
			 */
			if (altName != null) {
				newName = Misc.random(altName.getNames());
			}
			/*
			 * General bones
			 */
			if (altName != null) {
				if (altName.equals(AlternativeName.TROLL_GENERAL_BONES)) {
					sendTrollGeneralDialogue(player, npc);
				}
			} else {
				/*
				 * The trolls name
				 */
				final String trollName = newName;
				/*
				 * Dialogue option
				 */
				player.setDialogueChain(
						new DialogueChain().statement("Using your item on your pet will name the troll",
								"the correspending item name. By this action, you will",
								"lose the item and will @red@NOT@bla@ be able to get it back.",
								"Are you sure you want to do this?").option((p, option) -> {
									player.getPA().closeInterfaces(true);
									if (option == 1) {
										setTrollName(player, id, trollName, npc);
									}
								}, "Are you sure?", "Yes, feed the troll.", "No, I want to keep my item."))
						.start(player);
			}
			return true;
		}
		return false;
	}


	@Override
	public boolean canEquip(Player player, int id, int slot) {
		return false;
	}

	@Override
	public void operate(Player player, int id) {

	}

	@Override
	public boolean sendItemAction(Player player, int id, int type) {
		if (id == 23030) {
			if (type == 1) {
				ItemAssistant.deleteItemFromInventory(player, id, 1);
				Pet.summonNpcOnValidTile(player, BABY_TROLL, false);
				return true;
			}
		}
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
		return nameTroll(player, id, npc);
	}

	@Override
	public boolean dropItem(Player player, int id) {
		return false;
	}
}
