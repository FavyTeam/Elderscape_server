package game.content.miscellaneous;

import core.GameType;
import core.Plugin;
import core.ServerConstants;
import game.content.combat.Combat;
import game.content.dialogueold.DialogueHandler.FacialAnimation;
import game.content.music.SoundSystem;
import game.content.quest.QuestHandler;
import game.content.skilling.Skilling;
import game.content.starter.GameMode;
import game.entity.Entity;
import game.entity.MovementState;
import game.item.ItemAssistant;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.npc.pet.Pet;
import game.object.clip.Region;
import game.object.custom.DoorEvent;
import game.object.custom.Object;
import game.player.Area;
import game.player.Player;
import game.player.PlayerHandler;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import game.player.movement.Movement;
import game.player.punishment.IpMute;
import utility.Misc;



/**
 * General methods related to the player. Unfinished with PlayerOther class...
 *
 * @author MGT Madness, created on 01-01-2015.
 */
public class PlayerMiscContent {

	/**
	 * Most commonly used domain name endings.
	 */
	private final static String[] flaggedDomainEndings =
			{".com", ".net", ".co.uk", ".io", ".org"};

	/**
	 * Websites an advertiser may use to tell players to go on them to type in a specific search term.
	 */
	private final static String[] flaggedSearchWebsites =
			{"googl", "youtube", "yt"};

	/**
	 * Alterinative synonyms to tell someone to type in something on a website.
	 */
	private final static String[] flaggedSearchWords =
			{"search", "type", "look"};

	/**
	 * Ip mute a new player for advertising.
	 */
	public static boolean isNewPlayerSpamming(Player player, String textSent) {
		if (player.secondsBeenOnline < 600 || ItemAssistant.getAccountBankValueLongWithDelay(player) <= 50) {
			int flagPoint = 0;
			for (int index = 0; index < player.newPlayerChat.size(); index++) {
				String oldText = player.newPlayerChat.get(index).toLowerCase();
				boolean flaggedThisText = false;

				// Give the player +1 flag point player for advertising a website, for example "moparscape.org"
				for (int a = 0; a < flaggedDomainEndings.length; a++) {
					String flaggedDomainText = flaggedDomainEndings[a];
					String flaggedDomainTextWithDot = flaggedDomainText.replace(".", "dot");
					flaggedDomainTextWithDot = flaggedDomainTextWithDot.replace(".", "dot");

					String flaggedDomainTextWithDotAndSpace = flaggedDomainText.replace(".", "dot ");
					if (oldText.contains(flaggedDomainText) || oldText.contains(flaggedDomainTextWithDot) || oldText.contains(flaggedDomainTextWithDotAndSpace)) {
						flagPoint++;
						flaggedThisText = true;
						break;
					}
				}
				if (flaggedThisText) {
					continue;
				}

				boolean oldTextHasSearchWebsite = false;
				boolean oldTextHasSearchWordText = false;
				// Give the player +1 flag point player for advertising in a search manor such as "search for autoswitcher on google"
				for (int a = 0; a < flaggedSearchWebsites.length; a++) {
					String flaggedSearchWebsiteText = flaggedSearchWebsites[a];
					if (oldText.contains(flaggedSearchWebsiteText)) {
						oldTextHasSearchWebsite = true;
						break;
					}
				}
				for (int a = 0; a < flaggedSearchWords.length; a++) {
					String flaggedSearchWordText = flaggedSearchWords[a];
					if (oldText.contains(flaggedSearchWordText)) {
						oldTextHasSearchWordText = true;
						break;
					}
				}
				if (oldTextHasSearchWebsite && oldTextHasSearchWordText) {
					flagPoint++;
				}

			}
			if (flagPoint >= 2) {
				IpMute.ipMute(null, "ipmute 2147000 " + player.getPlayerName());
				return true;
			}
			player.newPlayerChat.add(textSent.toLowerCase());
			if (player.newPlayerChat.size() > 5) {
				player.newPlayerChat.remove(0);
			}
		}
		return false;
	}

	private final static int[] npcsWhoCanBow =
			{1, 2, 3, 4, 5, 6, 7, 8, 9, 6970, 548, 519, 883, 306, 535, 6540, 4397, 494, 495, 496, 497, 902, 6538, 176, 3073, 196, 195, 202, 172, 174, 7110, 187, 124, 922};

	private final static int[] hailEmoteList =
			{858, 862, 863, 2109, 861, 866, 2106, 2107, 2108, 865, 3543, 6111, 7531, 1331};

	/**
	 * All players to perform an emote and text.
	 *
	 * @param text The text for the players to shout.
	 */
	public static void allPlayersHail(String text[]) {
		for (int j = 0; j < PlayerHandler.players.length; j++) {
			Player player = PlayerHandler.players[j];
			if (player != null) {
				Player p = player;
				p.forcedChat(text[Misc.random(text.length - 1)], false, false);
				if (!Combat.inCombat(player)) {
					int index = Misc.random(hailEmoteList.length - 1);
					p.startAnimation(hailEmoteList[index]);
				}
			}
		}

		for (int i = 0; i < NpcHandler.NPC_INDEX_OPEN_MAXIMUM; i++) {
			if (NpcHandler.npcs[i] == null) {
				continue;
			}
			NpcHandler.npcs[i].forceChat(text[Misc.random(text.length - 1)]);
			for (int a = 0; a < npcsWhoCanBow.length; a++) {

				if (NpcHandler.npcs[i].npcType == npcsWhoCanBow[a]) {
					int index = Misc.random(hailEmoteList.length - 1);
					NpcHandler.npcs[i].requestAnimation(hailEmoteList[index]);
				}
			}
		}
	}

	/**
	 * Pray at the altar.
	 *
	 * @param player The associated player.
	 */
	public static void prayAtAltar(Player player) {
		player.getPA().closeInterfaces(true);
		if (player.getCurrentCombatSkillLevel(ServerConstants.PRAYER) >= player.getBasePrayerLevel()) {
			player.playerAssistant.sendMessage("Your prayer points are already full.");
			return;
		}
		player.playerAssistant.sendMessage("Your recharge your prayer points.");
		player.currentCombatSkillLevel[ServerConstants.PRAYER] = player.getBasePrayerLevel();
		Skilling.updateSkillTabFrontTextMain(player, ServerConstants.PRAYER);
		player.startAnimation(645);
		SoundSystem.sendSound(player, 442, 200);
	}

	/**
	 * @return The ancient magick interface.
	 */
	public static int getAncientMagicksInterface(Player player) {
		if(GameType.isPreEoc()) {
			return 37493;
		}
		if (player.ancientsInterfaceType == 1) {
			return 24836;
		} else if (player.ancientsInterfaceType == 2) {
			return 24818;
		} else {
			return 24800;
		}
	}

	public static void rockCake(Player player) {
		if (System.currentTimeMillis() - player.buryDelay > 300) {
			int hp = player.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS);
			int amount = hp / 10 + 3;
			if (hp - amount < 1) {
				amount = hp - 1;
			}
			if (amount > 0) {
				Combat.createHitsplatOnPlayerNormal(player, amount, ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.NO_ICON);
			}
			else {
				Combat.createHitsplatOnPlayerNormal(player, amount, 0, ServerConstants.NO_ICON);
			}
			player.buryDelay = System.currentTimeMillis();
			player.startAnimation(829);
			player.forcedChat("Ow! I nearly broke a tooth!", false, false);
			player.forcedChatUpdateRequired = true;
			Skilling.updateSkillTabFrontTextMain(player, ServerConstants.HITPOINTS);
		}
	}

	/**
	 * Calculate and change wildernessLevel integer.
	 *
	 * @param player The associated player.
	 */
	public static void calculateWildernessLevel(Player player) {
		int modY = player.getY() > 6400 ? player.getY() - 6400 : player.getY();
		int level = (((modY - 3520) / 8) + 1);
		if (level >= 1 && level <= ServerConstants.getMinimumWildernessLevel()) {
			level = ServerConstants.getMinimumWildernessLevel();
		}
		player.setWildernessLevel(level);
	}

	public static void resetKdr(Player player) {
		player.wildernessKillsReset += player.getWildernessKills(false);
		player.wildernessDeathsReset += player.getWildernessDeaths(false);
		player.setWildernessKills(0);
		player.setWildernessDeaths(0);
	}

	public static void PickNuts(final Player player) {
		if (ItemAssistant.getFreeInventorySlots(player) == 0) {
			player.getDH().sendStatement("You need at least 1 free inventory space to do this.");
			return;
		}
		if (Skilling.cannotActivateNewSkillingEvent(player)) {
			return;
		}
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				if (Skilling.forceStopSkillingEvent(player)) {
					container.stop();
					return;
				}
				if (ItemAssistant.getFreeInventorySlots(player) > 0) {
					if (Misc.random(150) == 150) {

						player.startAnimation(832);
						ItemAssistant.addItem(player, 7573, 1);
						player.getDH().sendItemChat("", "@dre@You find a rare Tchiki nut!", 7573, 300, 18, 0);
						player.getPA().sendMessage("You find a rare Tchiki nut!");
					} else {
						player.startAnimation(832);
						ItemAssistant.addItem(player, 4012, 1);
					}
				} else {
					container.stop();
				}
			}

			@Override
			public void stop() {
				Skilling.endSkillingEvent(player);
			}
		}, 1);
		return;
	}

	public static void CombineKeyHalfs(Player player) {
		int tooth = 985;
		int loop = 987;
		int full = 989;
		if (ItemAssistant.hasItemInInventory(player, tooth) && ItemAssistant.hasItemInInventory(player, loop)) {
			ItemAssistant.deleteItemFromInventory(player, tooth, 1);
			ItemAssistant.deleteItemFromInventory(player, loop, 1);
			ItemAssistant.addItem(player, full, 1);
			player.getDH().sendItemChat("", "You combine the key halves to make a Crystal key.", full, 250, 0, 0);
		}
	}

	public static enum EggData {
		RED(5076, 100, 1308),
		BLUE(5077, 125, 1307),
		GREEN(5078, 150, 1309);


		private int itemId;

		private int prayerXp;

		private int gfxId;


		EggData(final int itemId, final int prayerXp, final int gfxId) {
			this.itemId = itemId;
			this.prayerXp = prayerXp;
			this.gfxId = gfxId;
		}

		public int getItemId() {
			return itemId;
		}

		public int getPrayerXp() {
			return prayerXp;
		}

		public int getGfxId() {
			return gfxId;
		}

	}

	public static void EggShrine(Player player) {
		for (final EggData egg : EggData.values()) {
			if (RandomEvent.isBannedFromSkilling(player)) {
				return;
			}
			if (!ItemAssistant.hasItemInInventory(player, egg.getItemId())) {
				continue;
			}
			if (Skilling.cannotActivateNewSkillingEvent(player)) {
				return;
			}
			if (ItemAssistant.hasItemAmountInInventory(player, egg.getItemId(), 1)) {
				PlayerMiscContent.startEggCycle(player);
				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {
						if (Skilling.forceStopSkillingEvent(player)) {
							container.stop();
							return;
						}
						if (ItemAssistant.hasItemInInventory(player, egg.getItemId())) {
							PlayerMiscContent.startEggCycle(player);
						} else {
							container.stop();
						}
					}

					@Override
					public void stop() {
						Skilling.endSkillingEvent(player);
					}
				}, 3);
				return;
			}
		}
	}

	public static void startEggCycle(Player player) {
		for (final EggData egg : EggData.values()) {
			if (ItemAssistant.hasItemAmountInInventory(player, egg.getItemId(), 1)) {
				player.turnPlayerTo(1613, 3514);
				ItemAssistant.deleteItemFromInventory(player, egg.getItemId(), 1);
				ItemAssistant.addItem(player, 5074, 1);
				//player.getPA().createPlayersProjectile(x, y, offX, offY, angle, speed, gfxMoving, startHeight, endHeight, lockon, time, slope);
				player.getPA().createPlayersProjectile(1613, 3514, 1, 1, 50, 90, egg.getGfxId(), 10, 90, player.getObjectId(), 10, -100);
				Skilling.addSkillExperience(player, egg.getPrayerXp(), ServerConstants.PRAYER, false);
				player.startAnimation(3705);
				if (Misc.random(300) == 1) { //handle random chance for evil chicken outfit
					Announcement.announce(ServerConstants.GREEN_COL + GameMode.getGameModeName(player) + " has received an Evil chicken outfit piece from the Egg shrine!");
					final int OutfitItems[] =
							{20433, 20436, 20439, 20442};
					ItemAssistant.addItemToInventoryOrDrop(player, OutfitItems[(int) (Math.random() * OutfitItems.length)], 1);
				}
			}
		}
	}

	public static void HalloweenInterface(Player player) {
		player.getDH().sendStartInfo("", "", "You find it hard to read the font, and lean closer...", "", "");
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			int i = 0;

			@Override
			public void execute(CycleEventContainer container) {
				i++;
				switch (i) {
					case 6:
						player.getPA().displayInterface(18681);
						player.getDH().sendPlayerChat("ARGH!", FacialAnimation.ANGER_4.getAnimationId());
						player.forcedChat("ARGH!", false, false);
						break;

					case 10:
						player.getPA().closeInterfaces(true);
						Movement.stopMovement(player);
						player.startAnimation(2836);
						if (player.getX() == 4245)
							;
						break;

					case 12:
						player.getDH().sendPlayerChat("Oh my goodness! I nearly had a heart attack!", "What on earth was that?!", FacialAnimation.NEARLY_CRYING.getAnimationId());
						player.nextDialogue = 6183;
						container.stop();
						break;
				}
			}

			@Override
			public void stop() {
			}
		}, 1);
	}

	public static void HalloweenDig(Player player) {
		player.getDH().sendStartInfo("", "", "You start digging near the grave...", "", "");
		player.startAnimation(831);
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			int i = 0;

			@Override
			public void execute(CycleEventContainer container) {
				i++;
				switch (i) {
					case 6:
						Movement.stopMovement(player);
						player.forcedChat("There must be a key here somewhere...", false, false);
						player.getDH().sendStartInfo("", "", "There's nothing near the surface, so you keep digging...", "", "");
						break;

					case 11:
						Movement.stopMovement(player);
						player.getDH().sendStartInfo("", "", "You think you see something in the dirt...", "", "");
						break;

					case 16:
						Movement.stopMovement(player);
						player.forcedChat("Aha! I've found something.", false, false);
						player.startAnimation(65535);
						player.getDH().sendItemChat("", "You find an extremely muddy key.", 991, 200, 14, 0);
						ItemAssistant.addItemToInventoryOrDrop(player, 991, 1);
						player.getQuest(1).setStage(9);
						QuestHandler.updateAllQuestTab(player);
						player.nextDialogue = 6186;
						container.stop();
						break;
				}
			}

			@Override
			public void stop() {
			}
		}, 1);
	}

	public static void entEvent(Player player, Npc npc) {
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			int i = 0;

			@Override
			public void execute(CycleEventContainer container) {
				i++;
				switch (i) {
					case 4:
						Npc EntTrunk = NpcHandler.spawnNpc(player, 6595, npc.getX(), npc.getY(), 0, false, false);
						EntTrunk.setFrozenLength(999999999); // Stop the npc from moving.
						container.stop();
						break;
				}
			}

			@Override
			public void stop() {
			}
		}, 1);
	}

	public static boolean isUpdatesInterfaceButton(Player player, int buttonId) {
		if (buttonId >= 114124 && buttonId <= 114250) {
			//handles the opening of the website
			return true;
		}
		return false;
	}

	public static void sledgingSanta(Player player) {
		String[] Phrases =
				{"WAHOOOOOOOOOOO!", "What fun!", "You're all so slow, hahaha!", "Can't catch me!", "Can you keep up?", "Onwards and upwards!"};
		Npc Santa = NpcHandler.spawnNpc(player, 11113, 3087, 3485, 0, false, false);
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			int i = 0;

			@Override
			public void execute(CycleEventContainer container) {
				i++;
				switch (i) {
					case 1:
						Santa.turnNpc(3087, 3487);
						Santa.forceChat("And off we go!");
						//Santa.setFrozenLength(50000); // Stop the npc from moving.
						break;

					case 3:
						Santa.setMoveY(5);
						Santa.setMoved(true);
						Santa.getMoveY();
						Santa.updateRequired = true;
						Santa.forceChat(Phrases[Misc.random(Phrases.length - 1)]);
						break;

					case 40:
						Santa.forceChat("Phew! I'm worn out. Better be off!");
						player.getPA().createPlayersStillGfx(86, Santa.getX(), Santa.getY(), Santa.getHeight(), 130);
						break;

					case 45:
						Pet.deletePet(Santa);
						container.stop();
						break;
				}
			}

			@Override
			public void stop() {
			}
		}, 1);
	}

	public static void zanarisTeleport(Player player) {
		if (player.getObjectX() == 3202)
			DoorEvent.canUseAutomaticDoor(player, 1, false, 2406, 3202, 3169, 3, 0);
		player.forceNoClip = true;
		Movement.travelTo(player, player.getX() == 3201 ? 1 : -1, 0);
		player.resetPlayerTurn();
		if (player.getX() == 3201) {
			player.gfx0(569);
			CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
				int i = 0;

				@Override
				public void execute(CycleEventContainer container) {
					i++;
					switch (i) {
						case 2:
							player.getPA().sendMessage("The world starts to shimmer...");
							break;

						case 3:
							Teleport.startTeleport(player, 2452, 4473, player.getHeight(), "ZANARIS");
							break;

						case 6:
							if (player.getQuest(3).getStage() == 3) {
								Plugin.execute("finish_quest", player);
							}
							;
							container.stop();
							break;
					}
				}

				@Override
				public void stop() {
				}
			}, 1);
		}
	}

	public static void dramenTree(Player player) {
		player.startAnimation(879);
		if (Skilling.cannotActivateNewSkillingEvent(player)) {
			return;
		}
		if (ItemAssistant.getFreeInventorySlots(player) == 0) {
			player.playerAssistant.sendMessage("You don't have enough inventory space.");
			return;
		}
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			int i = 0;

			@Override
			public void execute(CycleEventContainer container) {
				if (Skilling.forceStopSkillingEvent(player)) {
					container.stop();
					return;
				}
				i++;
				switch (i) {
					case 5:
						player.getPA().sendMessage("You cut a branch from the Dramen tree.");
						ItemAssistant.addItem(player, 771, 1);
						player.startAnimation(65535);
						Skilling.endSkillingEvent(player);
						container.stop();
						break;
				}
			}

			@Override
			public void stop() {
			}
		}, 1);
	}

	public static enum Baubles {
		GOLD(6822),
		YELLOW(6823),
		RED(6824),
		BLUE(6825),
		GREEN(6826),
		PURPLE(6827);

		private int id;

		private Baubles(int id) {
			this.id = id;
		}

		public int getId() {
			return id;
		}
	}

	public static void handleApeAtollDoor(Player player) {
		DoorEvent.canUseAutomaticDoor(player, 1, false, 4788, 2719, 2766, 1, 0);
		DoorEvent.canUseAutomaticDoor(player, 1, false, 4787, 2721, 2766, 3, 0);
		player.forceNoClip = true;
		Movement.travelTo(player, 0, player.getY() <= 2765 ? 2 : -2);
		player.resetPlayerTurn();
	}

	public static boolean isBauble(Player player, int itemId) {
		for (Baubles b : Baubles.values()) {
			if (itemId == b.getId()) {
				if (Area.inDangerousPvpArea(player)) {
					player.getPA().sendMessage("You cannot do this in the wilderness.");
					return true;
				}
				int amount = GameType.isOsrsPvp() ? Misc.random(750, 1250) : Misc.random(560000, 950000);
				ItemAssistant.deleteItemFromInventory(player, itemId, 1);
				ItemAssistant.addItem(player, ServerConstants.getMainCurrencyId(), amount);
				player.getPA()
				      .sendFilterableMessage("You smash the bauble and find " + Misc.formatNumber(amount) + " " + ServerConstants.getMainCurrencyName().toLowerCase() + " inside!");
				return true;
			}
		}
		return false;
	}

	public static void chocolateCycle(Player player) {
		if (!ItemAssistant.hasItemInInventory(player, 1925)) {
			player.getPA().sendMessage("You need at least one bucket to put the melted chocolate into.");
			return;
		}
		if (Skilling.cannotActivateNewSkillingEvent(player)) {
			return;
		}
		if (ItemAssistant.hasItemAmountInInventory(player, 22345, 1) && ItemAssistant.hasItemAmountInInventory(player, 1925, 1)) {
			player.cannotIssueMovement = true;
			CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
				int i = 0;

				@Override
				public void execute(CycleEventContainer container) {
					i++;
					switch (i) {
						case 1:
							player.getDH().sendStartInfo("", "", "You carefully place the chocolate chunks into the crater...", "", "");
							player.startAnimation(899);
							ItemAssistant.deleteItemFromInventory(player, 22345, 1);
							break;

						case 5:
							player.getDH().sendStartInfo("", "", "The chocolate begins to melt...", "", "");
							break;

						case 9:
							ItemAssistant.deleteItemFromInventory(player, 1925, 1);
							ItemAssistant.addItem(player, 4687, 1);
							player.startAnimation(899);
							player.getDH().sendItemChat("", "You scoop some melted chocolate into a bucket.", 4687, 200, 20, 0);
							Skilling.endSkillingEvent(player);
							player.cannotIssueMovement = false;
							break;
					}
				}

				@Override
				public void stop() {
					player.cannotIssueMovement = false;
					Skilling.endSkillingEvent(player);
				}
			}, 1);
		}
	}

	public static boolean isAStaffMember(Player player) {
		if (player.isModeratorRank() || player.isHeadModeratorRank() || player.isSupportRank() || player.isAdministratorRank()) {
			return true;
		}
		return false;
	}

	public static void tournamentAntiDeathDot(Player player) {
		if (Region.pathUnblocked(player.getX(), player.getY(), player.getHeight(), "WEST")) {
			Movement.travelTo(player, -1, 0);
		}
		else if (Region.pathUnblocked(player.getX(), player.getY(), player.getHeight(), "EAST")) {
			Movement.travelTo(player, 1, 0);
		}
		else if (Region.pathUnblocked(player.getX(), player.getY(), player.getHeight(), "SOUTH")) {
			Movement.travelTo(player, 0, -1);
		}
		else if (Region.pathUnblocked(player.getX(), player.getY(), player.getHeight(), "NORTH")) {
			Movement.travelTo(player, 0, 1);
		}
	}

	public static void takeGarlic(Player player) {
		player.cannotIssueMovement = true;
		player.doingActionEvent(5);
		player.setMovementState(MovementState.DISABLED);
		player.getEventHandler().addEvent(player, new CycleEvent<Entity>() {
			int i = 0;

			@Override
			public void execute(CycleEventContainer<Entity> container) {
				i++;
				switch (i) {
					case 1:
						player.startAnimation(832);
						new Object(2613, 3096, 3269, 1, 0, 10, -1, 3);
						break;

					case 3:
						ItemAssistant.addItemToInventoryOrDrop(player, 1550, 1);
						player.getPA().sendMessage("You take a clove of garlic");
						player.cannotIssueMovement = false;
						player.setMovementState(MovementState.WALKABLE);
						container.stop();
						break;
				}
			}
			@Override
			public void stop() {
			}
		}, 1);
	}
}

