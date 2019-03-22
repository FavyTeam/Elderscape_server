package game.content.skilling.summoning.pet.impl;

import core.GameType;
import game.content.dialogue.DialogueChain;
import game.content.dialogueold.DialogueHandler.FacialAnimation;
import game.content.item.ItemInteraction;
import game.content.item.ItemInteractionComponent;
import game.npc.Npc;
import game.npc.data.NpcDefinition;
import game.npc.pet.Pet;
import game.player.Player;
import game.type.GameTypeIdentity;
import utility.Misc;

@ItemInteractionComponent(
		identities = {@GameTypeIdentity(type = GameType.PRE_EOC, identity = {22_973}),})
/**
 * Handles the sparkles pet
 * 
 * @author 2012
 *
 */
public class Sparkles implements ItemInteraction {

	/**
	 * The sparkles pet item id
	 */
	private static final int SPARKLES = 22_973;

	/**
	 * The sparkles pet npc id
	 */
	public static final int SPARKLES_PET = 2267;

	/**
	 * Sending random conversation
	 * 
	 * @param player the player
	 */
	public static void sendRandomConversation(Player player) {
		/*
		 * The eek npc definition
		 */
		NpcDefinition SPARKLES = NpcDefinition.DEFINITIONS[SPARKLES_PET];
		/*
		 * The random dialogue
		 */
		int dialogue = Misc.random(6);
		/*
		 * The first dialogue
		 */
		if (dialogue == 1) {
			player.setDialogueChain(
					new DialogueChain()
							.npc(SPARKLES, FacialAnimation.CALM_1,
									"Ssssssalutations Player, how can I help?")
							.option((p, option) -> {
								if (option == 1) {
									player.setDialogueChain(new DialogueChain()
											.player(FacialAnimation.CALM_1, "Where did you get your hat?")
											.npc(SPARKLES, FacialAnimation.CALM_1,
													"Sssstole it from an old man.")
											.player(FacialAnimation.CALM_1, "You stole from Santa!")
											.npc(SPARKLES, FacialAnimation.CALM_1, "Who's Sssssssssanta?")
											.player(FacialAnimation.CALM_1,
													"Santa is a kind old man who gives presents",
													"to everyone all over the world.")
											.npc(SPARKLES, FacialAnimation.CALM_1, "He ssssssssssounds nice.")
											.player(FacialAnimation.CALM_1,
													"Santa is a kind old man who gives presents",
													"He is, and you stole from him.")
											.npc(SPARKLES, FacialAnimation.CALM_1,
													"Thissssss can be my pressssssent!")
											.player(FacialAnimation.CALM_1,
													"It doesn't work that way, you don't get",
													"to choose your present.")
											.npc(SPARKLES, FacialAnimation.CALM_1,
													"Sssssssso, I ssssssshould give this hat back?")
											.option((p1, option1) -> {
												if (option1 == 1) {
													player.setDialogueChain(new DialogueChain()
															.player(FacialAnimation.CALM_1,
																	"Yes, it doesn't belong to you.")
															.npc(SPARKLES, FacialAnimation.CALM_1,
																	"Okay, if I give it back will I get a pressssssent.")
															.player(FacialAnimation.CALM_1,
																	"Well that depends on whether you have been",
																	"naughty or nice.")
															.npc(SPARKLES, FacialAnimation.CALM_1,
																	"Isssss ssssstealing Ssssssanta's hat naughty or nice")
															.player(FacialAnimation.CALM_1,
																	"I'm pretty sure that is naughty...")
															.npc(SPARKLES, FacialAnimation.CALM_1,
																	"So what will I get for Christmassssss then?")
															.player(FacialAnimation.CALM_1, "Coal.").npc(SPARKLES,
																	FacialAnimation.CALM_1,
																	"I don't want coal for Christmassssss, I think I will keep the hat."))
															.start(player);
												} else if (option1 == 2) {
													player.setDialogueChain(new DialogueChain()
															.player(FacialAnimation.CALM_1,
																	"No, it makes you look very dashing.")
															.npc(SPARKLES, FacialAnimation.CALM_1,
																	"I will keep it then!"))
															.start(player);
												}
											}, "Select an Option", "Yes", "No")).start(player);
								}


							}, "Select an Option", "Where did you get your hat?", "What do you eat?",
									"Hey snakey!", "Do you want to play a game?", "Nothing"))
					.start(player);
		} else if (dialogue == 2) {
			player.setDialogueChain(
					new DialogueChain().player(FacialAnimation.CALM_1, "What do you eat?")
							.npc(SPARKLES, FacialAnimation.CALM_1,
									"Eat? I am made out of tinsssel, I do not have a stomach!")
							.player(FacialAnimation.CALM_1, "If you do not eat then why were you",
									"stealing the wizards' Christmas puddings?")
							.npc(SPARKLES, FacialAnimation.CALM_1, "Becaussssssse, it annoysssss them.")
							.player(FacialAnimation.CALM_1, "Well that is not very nice is it?")
							.npc(SPARKLES, FacialAnimation.CALM_1,
									"No, but it'sssss fun to sssee them",
									"all run around worried about pudding."))
					.start(player);
		} else if (dialogue == 3) {
			player.setDialogueChain(new DialogueChain()
					.player(FacialAnimation.CALM_1, "Do you want to play a game?")
					.npc(SPARKLES, FacialAnimation.CALM_1, "Ssssure, do you want to play knock",
							"doors in Varrock and run away?")
					.player(FacialAnimation.CALM_1, "No, do you play any games that",
							"don't inconvenience people?")
					.npc(SPARKLES, FacialAnimation.CALM_1, "What about throwing cabbages at sssssheep?")
					.player(FacialAnimation.CALM_1, "...or inconvenience animals.")
					.npc(SPARKLES, FacialAnimation.CALM_1,
							"Can we go and teassssse Timmy the lessser demon ssssome more?")
					.player(FacialAnimation.CALM_1, "I think he has had enough teasing.")
					.npc(SPARKLES, FacialAnimation.CALM_1, "What do you want to play?")
					.player(FacialAnimation.CALM_1, "How about a game of gnomeball?")
					.npc(SPARKLES, FacialAnimation.CALM_1,
							"I cannot catch.")
					.player(FacialAnimation.CALM_1, "Ok, think of a number between 1 and 10.")
					.npc(SPARKLES, FacialAnimation.CALM_1, "Okay... 7").player(FacialAnimation.CALM_1,
							"No, you are not supposed to tell me,",
							"think of another one and don't tell me.")
					.npc(SPARKLES, FacialAnimation.CALM_1, "Okay, I have thought of a number.....")
					.player(FacialAnimation.CALM_1, "Now let me guess it and you tell me",
							"if your number is higher or lower."))
					.start(player);
		}
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
		if (id == SPARKLES) {
			Pet.summonNpcOnValidTile(player, SPARKLES_PET, false);
			return true;
		}
		return false;
	}
}
