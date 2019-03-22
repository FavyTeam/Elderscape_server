package game.content.skilling.fishing;

import core.GameType;
import core.ServerConstants;
import game.position.Position;
import game.content.achievement.Achievements;
import game.content.donator.DonatorTokenUse;
import game.content.donator.DonatorTokenUse.DonatorRankSpentData;
import game.content.miscellaneous.RandomEvent;
import game.content.music.SoundSystem;
import game.content.skilling.Skilling;
import game.content.skilling.SkillingStatistics;
import game.content.skilling.summoning.pet.impl.FishBowl;
import game.entity.Entity;
import game.entity.attributes.AttributeKey;
import game.entity.attributes.TransientAttributeKey;
import game.item.ItemAssistant;
import game.item.ItemDefinition;
import game.npc.Npc;
import game.player.Area;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import utility.Misc;

/**
 * Handles the fishing skill
 * 
 * @author 2012
 *
 */
public class Fishing {

	/**
	 * The fishing spot
	 */
	public static final AttributeKey<Position> FISHING_SPOT =
			new TransientAttributeKey<Position>(new Position(0, 0, 0));

	/**
	 * Handles fishing
	 * 
	 * @param player the player
	 * @param npc the npc
	 * @param fish the fish
	 */
	public static boolean startFishing(Player player, Npc npc, Fish fish) {
		/*
		 * Requirements
		 */
		if (!checkRequirements(player, fish)) {
			stopFishing(player);
			return true;
		}
		/*
		 * Sound
		 */
		SoundSystem.sendSound(player, 289, 500);
		/*
		 * Set position
		 */
		player.getAttributes().put(FISHING_SPOT,
				new Position(npc.getX(), npc.getY(), npc.getHeight()));
		/*
		 * Fishing action
		 */
		player.fishTimerAmount = getTime(player, fish);
		player.getEventHandler().addEvent(player, new CycleEvent<Entity>() {

			@Override
			public void execute(CycleEventContainer<Entity> container) {
				/*
				 * Check requirements
				 */
				if (!checkRequirements(player, fish)) {
					container.stop();
					return;
				}
				/*
				 * Fishing
				 */
				if (player.fishTimerAmount-- > 0) {
					player.startAnimation(fish.getAnimation());
				} else {
					Skilling.endSkillingEvent(player);
					/*
					 * Caught fish
					 */
					if (player.fishTimerAmount == 0) {
						catchFish(player, fish);
					}
					container.stop();
				}
			}

			@Override
			public void stop() {
				Skilling.endSkillingEvent(player);
			}
		}, 1);
		return true;
	}

	/**
	 * Caught the fish
	 * 
	 * @param player the player
	 * @param fish the fish
	 */
	private static void catchFish(Player player, Fish fish) {
		/*
		 * Delete bait
		 */
		if (fish.getBait() > 0) {
			ItemAssistant.deleteItemFromInventory(player, fish.getBait(), 1);
		}
		/*
		 * Statistics
		 */
		player.skillingStatistics[SkillingStatistics.FISH_CAUGHT]++;
		/*
		 * Sound
		 */
		SoundSystem.sendSound(player, 378, 500);
		/*
		 * Trout or salmon
		 */
		if (fish == Fish.TROUT || fish == Fish.SALMON) {
			int chanceForSalmon = 0;
			int level = player.baseSkillLevel[ServerConstants.FISHING];
			if (level >= 30) {
				chanceForSalmon = 99 - level;
				chanceForSalmon += 15;
				chanceForSalmon = 100 - chanceForSalmon;
				if (Misc.hasPercentageChance(chanceForSalmon)) {
					fish = Fish.SALMON;
				} else {
					fish = Fish.TROUT;
				}
			}
		}
		/*
		 * Shrimp or anchovies
		 */
		if (fish == Fish.SHRIMP || fish == Fish.ANCHOVIES) {
			int chanceForAnchovies = 0;
			int level = player.baseSkillLevel[ServerConstants.FISHING];
			if (level >= 15) {
				chanceForAnchovies = 99 - level;
				chanceForAnchovies += 5;
				chanceForAnchovies = 100 - chanceForAnchovies;
				if (Misc.hasPercentageChance(chanceForAnchovies)) {
					fish = Fish.ANCHOVIES;
				} else {
					fish = Fish.SHRIMP;
				}
			}
		}
		/*
		 * Tuna or swordfish
		 */
		if (fish == Fish.TUNA || fish == Fish.SWORDFISH) {
			int chanceForSwordy = 0;
			int level = player.baseSkillLevel[ServerConstants.FISHING];
			if (level >= 50) {
				chanceForSwordy = 99 - level;
				chanceForSwordy += 15;
				chanceForSwordy = 100 - chanceForSwordy;
				if (Misc.hasPercentageChance(chanceForSwordy)) {
					fish = Fish.SWORDFISH;
				} else {
					fish = Fish.TUNA;
				}
			}
		}
		/*
		 * Add fish
		 */
		Skilling.addHarvestedResource(player, fish.getItemId(), 1);
		/*
		 * Cape feature
		 */
		if (Skilling.hasMasterCapeWorn(player, 9798) && Misc.hasPercentageChance(10)) {
			Skilling.addHarvestedResource(player, fish.getItemId(), 1);
			ItemAssistant.addItemToInventoryOrDrop(player, fish.getItemId(), 1);
			player.getPA().sendMessage("<col=a54704>Your cape allows you to catch an extra fish.");
		}
		/*
		 * The experience
		 */
		int experience = (int) (fish.getExperience() * 1.05);
		/*
		 * Add experience
		 */
		if (GameType.isOsrs()) {
			for (int index = 0; index < ServerConstants.ANGLER_PIECES.length; index++) {
				int itemId = ServerConstants.ANGLER_PIECES[index][0];
				if (ItemAssistant.hasItemEquippedSlot(player, itemId, ServerConstants.ANGLER_PIECES[index][1])) {
					experience *= ServerConstants.SKILLING_SETS_EXPERIENCE_BOOST_PER_PIECE;
				}
			}
		}
		Skilling.addSkillExperience(player, experience,
				ServerConstants.FISHING, false);
		/*
		 * The prefix
		 */
		String prefix = ItemAssistant.getItemName(fish.getItemId()).endsWith("s") ? "some" : "a";
		/*
		 * Message
		 */
		player.playerAssistant.sendFilterableMessage(
				"You catch " + prefix + " " + ItemAssistant.getItemName(fish.getItemId()) + ".");
		/*
		 * Pet chance
		 */
		Skilling.petChance(player, experience, 200, 4800, ServerConstants.FISHING, null);
		/*
		 * Fishbowl pet
		 */
		if (GameType.isPreEoc()) {
			FishBowl.reward(player);
		}
		/*
		 * Achievements
		 */
		if (fish == Fish.SHARK) {
			Achievements.checkCompletionMultiple(player, "1036");
		} else if (fish == Fish.DARK_CRAB) {
			Achievements.checkCompletionMultiple(player, "1062 1128");
		}
	}

	/**
	 * Checking the requirements to fish
	 * 
	 * @param player the player
	 * @param fish the fish
	 * @return fishing
	 */
	private static boolean checkRequirements(Player player, Fish fish) {
		/*
		 * Stop skilling
		 */
		if (Skilling.forceStopSkillingEvent(player)) {
			return false;
		}
		/*
		 * Banned from skilling
		 */
		if (RandomEvent.isBannedFromSkilling(player)) {
			return false;
		}
		/*
		 * No skilling
		 */
		if (Skilling.cannotActivateNewSkillingEvent(player)) {
			return false;
		}
		/*
		 * Donator feature
		 */
		if (Area.inDonatorZone(player.getX(), player.getY())) {
			if (!player.isExtremeDonator()) {
				player.getPA().sendMessage("You need to be at least an "
						+ DonatorTokenUse.getDonatorRankIcon(DonatorRankSpentData.EXTREME_DONATOR) + "Extreme Donator to do this!");
				return false;
			}
		}
		/*
		 * No space
		 */
		if (ItemAssistant.getFreeInventorySlots(player) == 0) {
			player.playerAssistant.sendMessage("You do not have free inventory space.");
			return false;
		}
		/*
		 * Wrong fishing level
		 */
		if (player.baseSkillLevel[ServerConstants.FISHING] < fish.getRequiredLevel()) {
			player.getDH().sendStatement(
					"You need a Fishing level of " + fish.getRequiredLevel() + " to catch this fish.");
			return false;
		}
		/*
		 * No equipment
		 */
		if (!ItemAssistant.hasItemInInventory(player, fish.getEquipment())) {
			player.getPA().sendMessage("You need a "
					+ ItemDefinition.DEFINITIONS[fish.getEquipment()].name + " to fish here.");
			return false;
		}
		/*
		 * No bait
		 */
		if (!ItemAssistant.hasItemInInventory(player, fish.getBait())) {
			player.getPA().sendMessage("You need some "
					+ ItemDefinition.DEFINITIONS[fish.getBait()].name + " to fish here.");
			return false;
		}
		return true;
	}

	/**
	 * Gets the time
	 * 
	 * @param player the player
	 * @param fish the fish
	 * @return the time
	 */
	private static int getTime(Player player, Fish fish) {
		int value = 30;
		int maximum =
				(int) (value - (player.baseSkillLevel[ServerConstants.FISHING] * (value / 99.0)))
						+ fish.getTimer();
		int baseMinimum = maximum / 2;
		int timer = Misc.random(baseMinimum, maximum);
		return timer;
	}

	/**
	 * Stops fishing
	 * 
	 * @param player the player
	 */
	public static void stopFishing(Player player) {
		if (player.fishTimerAmount >= 0) {
			player.startAnimation(65535);
			player.fishTimerAmount = -1;
			player.getAttributes().put(FISHING_SPOT, new Position(0, 0, 0));
		}
	}
}
