package game.content.skilling.summoning.pet.impl;

import core.GameType;
import game.content.dialogue.DialogueChain;
import game.content.dialogueold.DialogueHandler.FacialAnimation;
import game.content.item.ItemInteraction;
import game.content.item.ItemInteractionComponent;
import game.npc.Npc;
import game.npc.data.NpcDefinition;
import game.player.Player;
import game.type.GameTypeIdentity;
import utility.Misc;

@ItemInteractionComponent(
		identities = {@GameTypeIdentity(type = GameType.PRE_EOC, identity = {15353})})
/**
 * Handles the Eek pet
 * 
 * @author 2012
 *
 */
public class Eek implements ItemInteraction {

	/**
	 * Sending random conversation
	 * 
	 * @param player the player
	 */
	private void sendRandomConversation(Player player) {
		/*
		 * The eek npc definition
		 */
		NpcDefinition EEK = NpcDefinition.DEFINITIONS[8985];
		/*
		 * The random dialogue
		 */
		int dialogue = Misc.random(6);
		/*
		 * The first dialogue
		 */
		if (dialogue == 1) {
			player.setDialogueChain(
					new DialogueChain().npc(EEK, FacialAnimation.CALM_1, "I bet you're a mighty hero!")
							.npc(EEK, FacialAnimation.HAPPY, "Hero of the humans!")
							.npc(EEK, FacialAnimation.HAPPY, "You are a might hero, aren't you?",
									"I bet you've slain dragons and all sorts of stuff")
							.option((p, option) -> {
								if (option == 1) {
									player.setDialogueChain(new DialogueChain()
											.player(FacialAnimation.LAUGH_3, "That's right. I'm awesome!")
											.npc(EEK, FacialAnimation.HAPPY,
													"I could be your sidekick! We could have all",
													"sorts of adventures together!")
											.option((p1, option1) -> {
												if (option1 == 1) {
													player.setDialogueChain(new DialogueChain()
															.player(FacialAnimation.DELIGHTED_EVIL, "Yes!")
															.npc(EEK, FacialAnimation.HAPPY,
																	"Yay! We'll travel all over the world fighting for justice",
																	"and freedom! And things like that!")
															.npc(EEK, FacialAnimation.HAPPY,
																	"Come on, let's have an adventure!"))
															.start(player);
												} else if (option1 == 2) {
													player.setDialogueChain(new DialogueChain()
															.player(FacialAnimation.ANGER_1,
																	"No. It's too dangerous.")
															.npc(EEK, FacialAnimation.HAPPY, "Aww")
															.npc(EEK, FacialAnimation.HAPPY,
																	"You know best, though. You're so BRAVE putting",
																	"yourse'f in danger all by yourself!"))
															.start(player);
												}
											}, "Select an Option", "Yes!", "No. It's too dangerous."))
											.start(player);
								} else if (option == 2) {
									player.setDialogueChain(new DialogueChain()
											.player(FacialAnimation.BOWS_HEAD_WHILE_SAD,
													"I'm not a great hero. I'm just an ordinary person.")
											.npc(EEK, FacialAnimation.HAPPY,
													"Aww. I bet you'll be a mighty hero someday, though")
											.option((p1, option1) -> {
												if (option1 == 1) {
													player.setDialogueChain(
															new DialogueChain()
																	.player(FacialAnimation.DELIGHTED_EVIL,
																			"Yes, I'm trying to advance myself.")
																	.npc(EEK, FacialAnimation.HAPPY,
																			"Brilliant! You can do it! Come on, let's have an adventure!"))
															.start(player);
												} else if (option1 == 2) {
													player.setDialogueChain(
															new DialogueChain()
																	.player(FacialAnimation.ANGER_1,
																			"I don't know. I'm not very ambitious.")
																	.npc(EEK, FacialAnimation.HAPPY,
																			"Aww...well..never mind."))
															.start(player);
												}
											}, "Select an Option", "Yes, I'm trying to advance myself",
													"I'm not very ambitious."))
											.start(player);
								}
							}, "Select an Option", "That's right. I'm awesome!",
									"I'm not a great hero. I'm just an ordinary person."))
					.start(player);
		} else if (dialogue == 2) {
			player.setDialogueChain(new DialogueChain()
					.npc(EEK, FacialAnimation.CALM_1,
							"Hey, you've only got four legs. How do you manage?", "Don't you fall over?")
					.option((p, option) -> {
						if (option == 1) {
							player.setDialogueChain(new DialogueChain()
									.player(FacialAnimation.LAUGH_3, "Actually, I've only got two legs.")
									.npc(EEK, FacialAnimation.DISTRESSED,
											"Someone has stolen your legs! This is a DISASTER!")
									.npc(EEK, FacialAnimation.DISTRESSED,
											"We've got to catch the leg thief!")
									.option((p1, option1) -> {
										if (option1 == 1) {
											player.setDialogueChain(new DialogueChain()
													.player(FacialAnimation.CALM_2, "No one stole my legs.")
													.npc(EEK, FacialAnimation.SLEEPY, "You gave your legs away?")
													.npc(EEK, FacialAnimation.HAPPY,
															"That is so heroic.. giving your legs away",
															"to someone without legs.")
													.npc(EEK, FacialAnimation.HAPPY, "You're my hero!"))
													.start(player);
										} else if (option1 == 2) {
											player.setDialogueChain(
													new DialogueChain().player(FacialAnimation.ALMOST_CRYING,
															"They're probably long gone by now.").npc(EEK,
																	FacialAnimation.ANNOYED,
																	"You're right. They probably used teh machine to give",
																	"themselves really long long running-away type legs",
																	"We'll never catch them now."))
													.start(player);
										}
									}, "Select an Option", "No one stole my legs.",
											"They're probably long gone by now."))
									.start(player);
						} else if (option == 2) {
							player.setDialogueChain(new DialogueChain()
									.player(FacialAnimation.BOWS_HEAD_WHILE_SAD, "Oh, it's not so bad.")
									.npc(EEK, FacialAnimation.DEFAULT,
											"Then again, your legs are much longer than mine.")
									.npc(EEK, FacialAnimation.DRUNK_TO_LEFT,
											"How's you get such long legs? Do you have",
											"some kind of leg-extending machine?")
									.npc(EEK, FacialAnimation.DRUNK_TO_RIGHT,
											"Where is the leg-extending machine? I want to use it!")
									.option((p1, option1) -> {
										if (option1 == 1) {
											player.setDialogueChain(new DialogueChain()
													.player(FacialAnimation.DELIGHTED_EVIL,
															"There is no leg-extending machine.")
													.npc(EEK, FacialAnimation.HAPPY,
															"You would say that! You want to keep it for yourself!")
													.npc(EEK, FacialAnimation.HAPPY,
															"Well that's okay. Maybe I don't even need your",
															"leg-extending machine!")
													.npc(EEK, FacialAnimation.HAPPY,
															"I can do special leg-stretching exercises! Make my legs longer",
															"naturally! No artificial legs for me!")
													.npc(EEK, FacialAnimation.HAPPY,
															"You just wait! Soon I'll have the best legs ever!"))
													.start(player);
										} else if (option1 == 2) {
											player.setDialogueChain(new DialogueChain()
													.player(FacialAnimation.CALM_2,
															"You can't use it. It's broken")
													.npc(EEK, FacialAnimation.ANGER_2,
															"Oh! I wouldn't want to use it if it's broken.")
													.npc(EEK, FacialAnimation.ANGER_2,
															"It might malfunction and give me tiny, tiny legs!",
															"Like a slug, only with tiny legs!")
													.npc(EEK, FacialAnimation.ANGER_2,
															"Or it might make some legs longer than others so I",
															"wouldn't be able to walk and I'd fall over!")
													.npc(EEK, FacialAnimation.ANGER_2, "That would be TERRIBLE!")
													.npc(EEK, FacialAnimation.ANGER_2,
															"I'd better keep away from that machine!"))
													.start(player);
										}
									}, "Select an Option", "There is no leg-extending machine",
											"You can't use it. It's broken"))
									.start(player);
						}
					}, "Select an Option", "Actually, I've only got two legs.", "Oh, it's not so bad."))
					.start(player);
		} else if (dialogue == 3) {
			player.setDialogueChain(new DialogueChain()
					.npc(EEK, FacialAnimation.CALM_1, "Hey, are you scared of spiders?")
					.option((p, option) -> {
						if (option == 1) {
							player.setDialogueChain(new DialogueChain()
									.player(FacialAnimation.LAUGH_3, "Yes, I'm scared of spiders.")
									.npc(EEK, FacialAnimation.DISTRESSED, "I've got to try not to be scary.")
									.npc(EEK, FacialAnimation.DISTRESSED,
											"You should just pretend I'm not a spider!",
											"Like, maybe I'm an eight-legged mouse!")
									.npc(EEK, FacialAnimation.HAPPY, "Hello, I'm an eight legged mouse!",
											"Squeak squeak squeak, I like cheese,",
											"cats are bad, I'm definitely not a spider.")
									.npc(EEK, FacialAnimation.HAPPY, "Is that better?")
									.option((p1, option1) -> {
										if (option1 == 1) {
											player.setDialogueChain(new DialogueChain().player(
													FacialAnimation.CALM_2, "You still look like a spider.")
													.npc(EEK, FacialAnimation.SLEEPY,
															"A spider? Oh no! I'm scared of spiders!"))
													.start(player);
										} else if (option1 == 2) {
											player.setDialogueChain(new DialogueChain()
													.player(FacialAnimation.CALM_1, "That's much better.")
													.npc(EEK, FacialAnimation.ANNOYED,
															"Yay! I'll be an eight-legged mouse forever until I forget.")
													.npc(EEK, FacialAnimation.ANNOYED, "...").npc(EEK,
															FacialAnimation.DELIGHTED_EVIL,
															"You know what? It's great being a spider."))
													.start(player);
										}
									}, "Select an Option", "You still look like a spider.",
											"A spider? Oh no! I'm scared of spiders!"))
									.start(player);
						} else if (option == 2) {
							player.setDialogueChain(new DialogueChain()
									.player(FacialAnimation.BOWS_HEAD_WHILE_SAD,
											"No, I'm not scared of spiders.")
									.npc(EEK, FacialAnimation.DEFAULT, "Not even a bit?")
									.npc(EEK, FacialAnimation.DRUNK_TO_LEFT,
											"I bet I can make you scared of spiders!")
									.npc(EEK, FacialAnimation.DRUNK_TO_RIGHT,
											"Boo! Raaar! I'm a scary spider!")
									.npc(EEK, FacialAnimation.DRUNK_TO_RIGHT,
											"Next time you go to sleep I'm going to crawl",
											"on your face and EAT YOUR EYEBALLS!")
									.option((p1, option1) -> {
										if (option1 == 1) {
											player.setDialogueChain(new DialogueChain()
													.player(FacialAnimation.DELIGHTED_EVIL,
															"I'm still not scared.")
													.npc(EEK, FacialAnimation.HAPPY,
															"Oh, wow! Even I was scared of me when I said that!")
													.npc(EEK, FacialAnimation.HAPPY,
															"You're so brave! I bet you're not scared of anything!"))
													.start(player);
										} else if (option1 == 2) {
											player.setDialogueChain(new DialogueChain()
													.player(FacialAnimation.CALM_2, "That was a bit scary.")
													.npc(EEK, FacialAnimation.DEFAULT,
															"Ha ha ha ha ha ha ha! I made you scared of me!",
															"I made you scared of me!")
													.npc(EEK, FacialAnimation.ANGER_2,
															"It's okay, I'm not really going to eat your",
															"eyeballs. Spiders don't do that.")
													.npc(EEK, FacialAnimation.ANGER_2,
															"Well...I don't think they do."))
													.start(player);
										}
									}, "Select an Option", "I'm still not scared.", "That was a bit scary."))
									.start(player);
						}
					}, "Select an Option", "Yes, I'm scared of spiders.",
							"No, I'm not scared of spiders."))
					.start(player);
		} else if (dialogue == 4) {
			player.setDialogueChain(new DialogueChain().npc(EEK, FacialAnimation.CALM_1,
					"I'm going to learn how to fly!", "I'm going to be a flying spider!")
					.option((p, option) -> {
						if (option == 1) {
							player.setDialogueChain(new DialogueChain()
									.player(FacialAnimation.LAUGH_3, "How are you going to fly?")
									.npc(EEK, FacialAnimation.DISTRESSED,
											"I'm going to spin webs between my legs",
											"to make wings! Then I can fly!")
									.npc(EEK, FacialAnimation.HAPPY, "Is that better?")
									.option((p1, option1) -> {
										if (option1 == 1) {
											player.setDialogueChain(new DialogueChain()
													.player(FacialAnimation.CALM_2, "Let's try it now!")
													.npc(EEK, FacialAnimation.SLEEPY,
															"Um...okay! I'll spin the webs...and",
															"then you throw me in the air.")
													.npc(EEK, FacialAnimation.SLEEPY, "Okay. Three...")
													.npc(EEK, FacialAnimation.SLEEPY, "Two...")
													.npc(EEK, FacialAnimation.SLEEPY, "One...")
													.npc(EEK, FacialAnimation.SLEEPY, "Wait! I'm not ready!")
													.npc(EEK, FacialAnimation.SLEEPY,
															"I don't think I've got the webs right.",
															"I'm not ready yet. We'll do it another time."))
													.start(player);
										} else if (option1 == 2) {
											player.setDialogueChain(new DialogueChain()
													.player(FacialAnimation.CALM_1, "Sounds dangerous.")
													.npc(EEK, FacialAnimation.ANNOYED,
															"It's not dangerous! What's the worst that could happen?")
													.npc(EEK, FacialAnimation.ANNOYED,
															"The worst that can happen is, like,",
															" the wings fail and I fall to my death.")
													.npc(EEK, FacialAnimation.DELIGHTED_EVIL,
															"Or I get eaten by a bird.")
													.npc(EEK, FacialAnimation.DELIGHTED_EVIL,
															"I'm going to rethink this."))
													.start(player);
										}
									}, "Select an Option", "Let's try it now!", "Sounds dangerous."))
									.start(player);
						} else if (option == 2) {
							player.setDialogueChain(new DialogueChain()
									.player(FacialAnimation.BOWS_HEAD_WHILE_SAD, "Spiders can't fly!")
									.npc(EEK, FacialAnimation.DEFAULT, "Some spiders can fly! They use web",
											"strands to float in the air!")
									.npc(EEK, FacialAnimation.DRUNK_TO_LEFT,
											"I think I know more about spiders",
											" Than you, seeing how I am one!")
									.option((p1, option1) -> {
										if (option1 == 1) {
											player.setDialogueChain(new DialogueChain()
													.player(FacialAnimation.DELIGHTED_EVIL, "I'm sorry, Eek.")
													.npc(EEK, FacialAnimation.HAPPY, "That's okay.").npc(EEK,
															FacialAnimation.HAPPY,
															"I don't want to fly now. I'm not in the mood."))
													.start(player);
										} else if (option1 == 2) {
											player.setDialogueChain(new DialogueChain()
													.player(FacialAnimation.CALM_2, "I don't believe you.")
													.npc(EEK, FacialAnimation.DEFAULT,
															"I don't care. I'm not talking to you any more."))
													.start(player);
										}
									}, "Select an Option", "I'm sorry, Eek.", "I don't believe you."))
									.start(player);
						}
					}, "Select an Option", "How are you going to fly?", "Spiders can't fly!"))
					.start(player);
		} else {
			player.setDialogueChain(new DialogueChain()
					.npc(EEK, FacialAnimation.CALM_1, "Hey...there are so many humans in your world.")
					.npc(EEK, FacialAnimation.CALM_1, "Back in the Spider Realm there are only spiders.")
					.npc(EEK, FacialAnimation.CALM_1,
							"I guess that's why they call it the Spider Realm.")
					.npc(EEK, FacialAnimation.CALM_1,
							"Suppose I was bitten by a magically-irradiated man?")
					.npc(EEK, FacialAnimation.CALM_1, "I might become the Man-Spider! I'd gain",
							"all the powers of a man! Like um...")
					.npc(EEK, FacialAnimation.CALM_1, "Um...what can humans do that's special?")
					.option((p, option) -> {
						if (option == 1) {
							player.setDialogueChain(new DialogueChain()
									.player(FacialAnimation.LAUGH_3, "Walk on two legs.").npc(EEK,
											FacialAnimation.DISTRESSED, "Walk on two legs! Yes! Then I could",
											"use my other six legs to FIGHT CRIME!"))
									.start(player);
						} else if (option == 2) {
							player.setDialogueChain(new DialogueChain()
									.player(FacialAnimation.BOWS_HEAD_WHILE_SAD, "Use tools.")
									.npc(EEK, FacialAnimation.DEFAULT,
											"Yes! I could have all sorts of gadgets!",
											"Like a gadget that spins webs!")
									.npc(EEK, FacialAnimation.DRUNK_TO_LEFT,
											"Wait...I can already spin webs.")
									.npc(EEK, FacialAnimation.DRUNK_TO_LEFT, "Never mind!")).start(player);
						} else if (option == 3) {
							player.setDialogueChain(new DialogueChain()
									.player(FacialAnimation.BOWS_HEAD_WHILE_SAD,
											"Project heat-rays from our eyes.")
									.npc(EEK, FacialAnimation.DEFAULT,
											"Oh! I didn't know humans could do that!")
									.npc(EEK, FacialAnimation.DRUNK_TO_LEFT,
											"Please don't fry me with your heat rays!")
									.npc(EEK, FacialAnimation.DRUNK_TO_LEFT, "I'm too young to fry!"))
									.start(player);
						} else if (option == 4) {
							player.setDialogueChain(
									new DialogueChain()
											.player(FacialAnimation.BOWS_HEAD_WHILE_SAD,
													"Nothing, really. Humans are pretty ordinary.")
											.npc(EEK, FacialAnimation.DEFAULT,
													"Aww, don't say that. Everyone is special!"))
									.start(player);
						}
					}, "Select an Option", "Walk on two legs.", "Use tools.",
							"Project heat-rays from our eyes.",
							"Nothing, really. Humans are pretty ordinary."))
					.start(player);
		}
	}

	@Override
	public boolean canEquip(Player player, int id, int slot) {
		return true;
	}

	@Override
	public void operate(Player player, int id) {

	}

	@Override
	public boolean sendItemAction(Player player, int id, int type) {
		if (id == 15_353) {
			if (type == 2) {
				// player.startAnimation(12_490);
				player.gfx0(2178);
				return true;
			} else if (type == 1) {
				sendRandomConversation(player);
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
		return false;
	}

	@Override
	public boolean dropItem(Player player, int id) {
		return false;
	}
}
