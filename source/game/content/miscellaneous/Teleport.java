package game.content.miscellaneous;

import core.GameType;
import core.Server;
import core.ServerConfiguration;
import core.ServerConstants;
import game.position.Position;
import game.content.clanchat.ClanChatHandler;
import game.content.combat.Combat;
import game.content.combat.EdgeAndWestsRule;
import game.content.interfaces.InterfaceAssistant;
import game.content.minigame.Minigame;
import game.content.minigame.RecipeForDisaster;
import game.content.minigame.barrows.Barrows;
import game.content.music.SoundSystem;
import game.content.skilling.agility.AgilityAssistant;
import game.content.staff.PrivateAdminArea;
import game.content.worldevent.Tournament;
import game.item.ItemAssistant;
import game.npc.impl.KrakenCombat;
import game.object.clip.Region;
import game.object.lever.Lever;
import game.player.Area;
import game.player.Player;
import game.player.PlayerHandler;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import game.player.movement.Follow;
import game.player.movement.Movement;
import game.player.punishment.RagBan;
import java.util.ArrayList;
import utility.Misc;

/**
 * Spellbook teleports for the Modern spellbook and Ancient magicks spellbook.
 *
 * @author MGT Madness, created on 29-11-2013.
 */

public class Teleport {


	public static ArrayList<String> debugPreEocTeleports = new ArrayList<String>();

	public static int index = 0;

	public static void loadPreEocTeleportsDebug() {
		if (!GameType.isPreEoc()) {
			return;
		}
		debugPreEocTeleports.add("Summoning Portal Room for QBD ; 1199 6498 0");
		debugPreEocTeleports.add("Queen Black Dragon lair ; 1440 6363 1");
		debugPreEocTeleports.add("Jadinko liar ; 3012 9275 0");
		debugPreEocTeleports.add("Polypore dungeon ground floor ; 4637 5438 0");
		debugPreEocTeleports.add("Polypore dungeon first floor ; 4626 5434 0");
		debugPreEocTeleports.add("Polypore dungeon second floor ; 4626 5434 0");
		debugPreEocTeleports.add("Dungeoneering lobby ; 3451 3726 0");
		debugPreEocTeleports.add("Dungeoneering tokens area ; 3446 3699 0");
		debugPreEocTeleports.add("Dungeoneering frozen floors chunk 1 ; 118 4300 0");
		debugPreEocTeleports.add("Dungeoneering frozen floors chunk 2 ; 359 4275 0");
		debugPreEocTeleports.add("Dungeoneering frozen floors chunk 3 ; 2350 5500 0");
		debugPreEocTeleports.add("Dungeoneering abandoned floors chunk 1 ; 118 5160 0");
		debugPreEocTeleports.add("Dungeoneering abandoned floors chunk 2 ; 118 4444 0");
		debugPreEocTeleports.add("Dungeoneering occult floors ; 118 4670 0");
		debugPreEocTeleports.add("Dungeoneering warped floors chunk 1 ; 118 5555 0");
		debugPreEocTeleports.add("Dungeoneering warped floors chunk 2 ; 980 1130 0");
		debugPreEocTeleports.add("Dungeoneering warped floors chunk 3 ; 455 4792 0");
		debugPreEocTeleports.add("Dungeoneering warped floors chunk 4 ; 567 4806 0");
		debugPreEocTeleports.add("Asgarnian ice dungeon ; 3048 9582 0");
		debugPreEocTeleports.add("Nomad ; 3361 5857 0");
		debugPreEocTeleports.add("Glacor cave ; 4188 5720 0");
		debugPreEocTeleports.add("Ice strykewyrm cave ; 3422 5668 0");
		debugPreEocTeleports.add("Fist of guthix arena ; 1661 5698 0");
		debugPreEocTeleports.add("Fist of guthix lobby ; 1632 5599 0");
		debugPreEocTeleports.add("Fist of guthix entrance with shop ; 1692 5600 0");
		debugPreEocTeleports.add("Light creatures ; 2526 5874 0");
		debugPreEocTeleports.add("Bh entrance ; 3188 3681 0");
		debugPreEocTeleports.add("Bh pking area ; 3156 3700 0");
		debugPreEocTeleports.add("Corporeal beast ; 2922 4384 0");
		debugPreEocTeleports.add("Lumbridge cows ; 3259 3267 0");
		debugPreEocTeleports.add("Yaks ; 2321 3793 0");
		debugPreEocTeleports.add("Rock crabs ; 2676 3715 0");
		debugPreEocTeleports.add("Slayer tower ; 3428 3537 0");
		debugPreEocTeleports.add("Slayer tower ; 3428 3537 1");
		debugPreEocTeleports.add("Slayer tower ; 3428 3537 2");
		debugPreEocTeleports.add("Brimhaven dungeon ; 2713 9564 0");
		debugPreEocTeleports.add("Bandit camp ; 3169 2990 0");
		debugPreEocTeleports.add("Taverley dungeon ; 2884 9798 0");
		debugPreEocTeleports.add("Edgeville dungeon ; 3096 9867 0");
		debugPreEocTeleports.add("Fremennik dungeon ; 2808 10002 0");
		debugPreEocTeleports.add("Tzhaar ; 2452 5167 0");
		debugPreEocTeleports.add("Entrana monster zone ; 2866 3337 0");
		debugPreEocTeleports.add("Dwarven mine ; 3023 9739 0");
		debugPreEocTeleports.add("Agility ; 2998 3906 0");
		debugPreEocTeleports.add("Edge Pvp @gr3@(Bank area) ; 3094 3496 4");
		debugPreEocTeleports.add("West dragons @red@(monster 10) ; 2979 3594 0");
		debugPreEocTeleports.add("East dragons @red@(monster 17) ; 3348 3647 0");
		debugPreEocTeleports.add("Elder chaos druids @red@(monster 15) ; 3235 3635 0");
		debugPreEocTeleports.add("Tormented demons @red@(monster 24) ; 3260 3705 0");
		debugPreEocTeleports.add("Crazy archaeologist @red@(monster 25) ; 2980 3713 0");
		debugPreEocTeleports.add("Revenants @red@(monster 27) ; 2978 3735 0");
		debugPreEocTeleports.add("Venenatis @red@(monster 28) ; 3308 3737 0");
		debugPreEocTeleports.add("Lava dragons @red@(monster 31) ; 3070 3760 0");
		debugPreEocTeleports.add("Chinchompa hill @red@(monster 34) ; 3138 3785 0");
		debugPreEocTeleports.add("Vet'ion @red@(monster 34) ; 3220 3789 0");
		debugPreEocTeleports.add("Chaos Fanatic @red@(monster 42) ; 2979 3848 0");
		debugPreEocTeleports.add("Callisto @red@(monster 44) ; 3202 3865 0");
		debugPreEocTeleports.add("Ice Strykewyrms @red@(monster 45) ; 2977 3873 0");
		debugPreEocTeleports.add("Lever to go to Kbd; 3067 10257 0");
		debugPreEocTeleports.add("Lava bridge; 3367 3935 0");
		debugPreEocTeleports.add("Demonic ruins @red@(monster 46) ; 3288 3886 0");
		debugPreEocTeleports.add("Chaos Elemental @red@(monster 50) ; 3307 3916 0");
		debugPreEocTeleports.add("Mage arena @red@(monster 52) ; 3105 3934 0");
		debugPreEocTeleports.add("Magebank @gr3@(Bank area) ; 2537 4714 0");
		debugPreEocTeleports.add("Kalphite Queen ; 3507 9494 0");
		debugPreEocTeleports.add("Giant mole ; 1760 5194 0");
		debugPreEocTeleports.add("Corporeal beast ; 2966 4382 2");
		debugPreEocTeleports.add("K'ril Tsutsaroth ; 2925 5331 2");
		debugPreEocTeleports.add("Commander Zilyana ; 2907 5265 0");
		debugPreEocTeleports.add("Kree'arra ; 2839 5296 2");
		debugPreEocTeleports.add("General Graardor ; 2864 5354 2");
		debugPreEocTeleports.add("Dagannoth Kings ; 1904 4366 0");
		debugPreEocTeleports.add("King Black Dragon ; 2271 4680 0");
		debugPreEocTeleports.add("TzTok-Jad ; 2452 5167 0");
		debugPreEocTeleports.add("Duel arena ; 3366 3266 0");
		debugPreEocTeleports.add("Barrows ; 3565 3315 0");
		debugPreEocTeleports.add("Barrows 1 ; 3578 9706 3");
		debugPreEocTeleports.add("Barrows 2 ; 3568 9683 3");
		debugPreEocTeleports.add("Barrows 3 ; 3557 9703 3");
		debugPreEocTeleports.add("Barrows 4 ; 3556 9718 3");
		debugPreEocTeleports.add("Barrows 5 ; 3534 9704 3");
		debugPreEocTeleports.add("Barrows 6 ; 3546 9684 3");
		debugPreEocTeleports.add("Barrows 7 ; 3552 9693 0");
		debugPreEocTeleports.add("Castle wars 1 ; 2440 3090 0");
		debugPreEocTeleports.add("Castle wars 2 ; 2400 9504 0");
		debugPreEocTeleports.add("Castle wars 3 ; 2381 9489 0");
		debugPreEocTeleports.add("Castle wars 4 ; 2421 9523 0");
		debugPreEocTeleports.add("Castle wars 5 ; 2425 3077 0");
		debugPreEocTeleports.add("Castle wars 6 ; 2425 3077 1");
		debugPreEocTeleports.add("Castle wars 7 ; 2425 3077 2");
		debugPreEocTeleports.add("Castle wars 8 ; 2425 3077 3");
		debugPreEocTeleports.add("Castle wars 9 ; 2399 3104 0");
		debugPreEocTeleports.add("Castle wars 10 ; 2381 3126 0");
		debugPreEocTeleports.add("Castle wars 11 ; 2416 3126 0");
		debugPreEocTeleports.add("Pest control ; 2658 2659 0");
		debugPreEocTeleports.add("Warrior's guild ; 2851 3548 0");
		debugPreEocTeleports.add("Recipe for disaster ; 1900 5346 2");
		debugPreEocTeleports.add("Fight pits ; 2401 5180 0");
		debugPreEocTeleports.add("Varrock ; 3213 3424 0");
		debugPreEocTeleports.add("Falador ; 2965 3378 0");
		debugPreEocTeleports.add("Lumbridge ; 3222 3218 0");
		debugPreEocTeleports.add("Al-kharid ; 3276 3167 0");
		debugPreEocTeleports.add("Karamja ; 2947 3147 0");
		debugPreEocTeleports.add("Draynor ; 3093 3248 0");
		debugPreEocTeleports.add("Ardougne ; 2661 3306 0");
		debugPreEocTeleports.add("Taverly ; 2934 3450 0");
		debugPreEocTeleports.add("Yanille ; 2606 3092 0");
		debugPreEocTeleports.add("Catherby ; 2827 3437 0");
		debugPreEocTeleports.add("Nardah ; 3420 2916 0");
		debugPreEocTeleports.add("Pollvineach ; 3357 2967 0");
		debugPreEocTeleports.add("Canifis ; 3494 3483 0");
		debugPreEocTeleports.add("Camelot ; 2756 3477 0");
		debugPreEocTeleports.add("Port Phasmatys ; 3684 3473 0");
		debugPreEocTeleports.add("Seers village ; 2719 3485 0");
		debugPreEocTeleports.add("Abyss ; 3039 4834 0");
		debugPreEocTeleports.add("Chaos tunnels 1 ; 3249 5488 0");
		debugPreEocTeleports.add("Chaos tunnels 2 ; 3230 5494 0");
	}

	/**
	 * Teleport a player and perform the correct emote and gfx depending on their spellbook type.
	 *
	 * @param x x-axis coordinate of the player.
	 * @param y y-axis coordinate of the player.
	 * @param height height level of the player.
	 * @param teleporter True, if the player is being teleported by the 'Teleporter' NPC.
	 */
	public static boolean spellTeleport(Player player, int x, int y, int height, boolean teleporter) {
		if (!player.isTutorialComplete()) {
			return false;
		}
		if (teleporter) {
			return startTeleport(player, x, y, height, "TELEPORTER");
		} else {
			return startTeleport(player, x, y, height, player.spellBook);
		}
	}

	public static void startTeleportAndDeleteItem(final Player player, int x, int y, int height, final String teleportType, int itemDelete) {
		if (startTeleport(player, x, y, height, teleportType)) {
			ItemAssistant.deleteItemFromInventory(player, itemDelete, 1);
		}
	}

	/**
	 * Initiate the spellbook teleport.
	 */
	public static boolean startTeleport(final Player player, int x, int y, int height, final String teleportType) {

		if (!canTeleport(player, teleportType, x, y, height)) {
			return false;
		}
		if (!Area.inWilderness(x, y, height)) {
			player.usingTeleportWithNoCombatQueueing = true;
		}
		else {
			player.usingTeleportWithNoCombatQueueing = false;
		}
		player.getTradeAndDuel().claimStakedItems();
		Tournament.logOutUpdate(player, false);
		player.clickObjectType = 0;
		player.setClickNpcType(0);
		player.resetNpcIdentityAttacking();
		player.resetPlayerIdAttacking();
		Player o = player.getTradeAndDuel().getPartnerDuel();
		if (o != null) {
			o.getTradeAndDuel().declineDuel(false);
		}
		player.doNotClosePmInterface = true;
		player.getTradeAndDuel().declineDuel(true);
		player.requestDuoName = "";
		player.doNotClosePmInterface = true;
		player.getTradeAndDuel().declineTrade1(true);
		player.resetFaceUpdate();
		Follow.resetFollow(player);
		GodWarsDungeonInterface.resetGwdData(player);
		player.playerAssistant.stopAllActions();
		if (teleportType.contains("GLORY")) {
			String[] split = teleportType.split(" ");
			int itemId = Integer.parseInt(split[1]);
			if (teleportType.contains("EQUIPMENT")) {
				ItemAssistant.replaceEquipmentSlot(player, ServerConstants.AMULET_SLOT, itemId - 2, 1, true);
			} else {
				ItemAssistant.deleteItemFromInventory(player, itemId, 1);
				ItemAssistant.addItemToInventoryOrDrop(player, itemId - 2, 1);
			}
		}
		if (teleportType.contains("BURNING_AMULET")) {
			String[] split = teleportType.split(" ");
			int itemId = Integer.parseInt(split[1]);
			if (teleportType.contains("EQUIPMENT")) {
				if (itemId == 21166) {
					ItemAssistant.replaceEquipmentSlot(player, ServerConstants.AMULET_SLOT, 21169, 1, true);
				} else if (itemId == 21175) {
					ItemAssistant.replaceEquipmentSlot(player, ServerConstants.AMULET_SLOT, -1, 1, true);
					player.getPA().sendFilterableMessage("<col=ba05a8>Your amulet has run out of charges and is destroyed in the process.");
				} else {
					ItemAssistant.replaceEquipmentSlot(player, ServerConstants.AMULET_SLOT, itemId + 2, 1, true);
				}
			} else {
				if (itemId == 21166) {
					ItemAssistant.deleteItemFromInventory(player, itemId, 1);
					ItemAssistant.addItemToInventoryOrDrop(player, itemId + 3, 1);
				} else if (itemId == 21175) {
					ItemAssistant.deleteItemFromInventory(player, itemId, 1);
					player.getPA().sendFilterableMessage("<col=ba05a8>Your amulet has run out of charges and is destroyed in the process.");
				} else {
					ItemAssistant.deleteItemFromInventory(player, itemId, 1);
					ItemAssistant.addItemToInventoryOrDrop(player, itemId + 2, 1);
				}
			}
		}

		player.teleportsUsed++;
		AgilityAssistant.stopResting(player);
		if (player.getPlayerIdAttacking() > 0 || player.getNpcIdAttacking() > 0) {
			Combat.resetPlayerAttack(player);
		}

		if (System.currentTimeMillis() - player.getTimeUnderAttackByAnotherPlayer() <= 10000) {
			if (player.getUnderAttackBy() != player.getPlayerId()) {
				Player attacker = PlayerHandler.players[player.getUnderAttackBy()];
				if (attacker != null) {
					Combat.resetPlayerAttack(attacker);
					attacker.setHitDelay(0);
					player.setTimeUnderAttackByAnotherPlayer(0);
					player.setTimeAttackedAnotherPlayer(0);
				}
			}
		}
		player.setTeleporting(true);
		Movement.stopMovement(player);
		player.getTradeAndDuel().declineTrade1(true);
		player.teleX = x;
		player.teleY = y;
		player.resetNpcIdentityAttacking();
		player.resetPlayerIdAttacking();
		player.resetFaceUpdate();
		player.teleportToHeight = height;

		if (teleportType.equalsIgnoreCase("MODERN") || teleportType.contains("GLORY") || teleportType.contains("BURNING_AMULET")) {
			player.startAnimation(714);
			player.setTeleportCycle(4);
			player.teleEndAnimation = 65535;
		}
		else if (teleportType.equalsIgnoreCase("LUNAR"))
		{
			player.startAnimation(1816);
			player.setTeleportCycle(4);
			player.teleEndAnimation = 65535;
			player.gfx0(747);
			SoundSystem.sendSound(player, 202, 100);
		}
		else if (teleportType.equalsIgnoreCase("ANCIENT")) {
			player.startAnimation(1979);
			player.setTeleportCycle(4);
			player.teleEndAnimation = 65535;
			player.gfx0(392);
		} else if (teleportType.equalsIgnoreCase("ARCEUUS")) {
			player.startAnimation(3865);
			player.setTeleportCycle(4);
			player.teleEndAnimation = 65535;
			player.gfx0(1296);
		} else if (teleportType.equalsIgnoreCase("SEED_POD")) {
			player.startAnimation(4544);
			player.setTeleportCycle(5);
			player.teleEndAnimation = 65535;
			player.gfx0(767);
		} else if (teleportType.equalsIgnoreCase("FAIRY_RING")) {
			player.setTeleportCycle(4);
			player.teleEndAnimation = 3266;
			player.gfx0(569);
		} else if (teleportType.equalsIgnoreCase("ZANARIS")) {
			player.startAnimation(3265);
			player.setTeleportCycle(2);
			player.teleEndAnimation = 3266;
		} else if (teleportType.equalsIgnoreCase("PURO")) {
			player.startAnimation(6601);
			player.setTeleportCycle(10);
			player.teleEndAnimation = 65535;
			player.gfx0(1118);
		} else if (teleportType.equalsIgnoreCase("ECTO")) {
			player.startAnimation(878);
			player.setTeleportCycle(5);
			player.teleEndAnimation = 65535;
			player.gfx0(1273);
		} else if (teleportType.equalsIgnoreCase("TAB")) {
			player.startAnimation(4069);
			player.setTeleportCycle(4);
			ItemAssistant.deleteItemFromInventory(player, 8013, ItemAssistant.getItemSlot(player, 8013), 1);
			player.getPA().sendFilterableMessage("You break the tablet.");
			EdgeAndWestsRule.teleTab(player);
		} else if (teleportType.equalsIgnoreCase("BARROWS_TAB")) {
			player.startAnimation(4069);
			player.setTeleportCycle(4);
			ItemAssistant.deleteItemFromInventory(player, 19629, ItemAssistant.getItemSlot(player, 19629), 1);
			player.getPA().sendFilterableMessage("You break the tablet.");
		} else if (teleportType.contains("LEVER")) {
			player.setTeleportCycle(5);
			player.startAnimation(2140);
		} else if (teleportType.contains("OBELISK")) {
			player.setTeleportCycle(3);
			player.startAnimation(3945);
			player.teleEndAnimation = 65535;
			player.playerAssistant.sendFilterableMessage("Ancient magic teleports you somewhere in the wilderness.");
		} else if (teleportType.equalsIgnoreCase("TELEPORTER")) {
			player.setTeleportCycle(4);
			player.teleEndAnimation = 715;
			Server.npcHandler.teleporterInAction = true;
			SoundSystem.sendSound(player, 202, 600);
		}
		if (!Area.inDangerousPvpArea(player)) {
			if (Area.inWilderness(player.teleX, player.teleY, player.teleportToHeight)) {
				RagBan.wildDebug.add(Misc.getDateAndTime() + " Add4: " + player.getPlayerName() + ", " + player.addressIp + ", " + player.addressUid + ", " + player.getX() + ", "
				                     + player.getY() + ", " + player.getHeight() + ", " + player.teleX + ", " + player.teleY);
				RagBan.addToWilderness(player.addressIp, player.addressUid);
			}
		}
		if (!Area.inWilderness(player.teleX, player.teleY, player.teleportToHeight)) {
			if (Area.inDangerousPvpArea(player)) {
				RagBan.wildDebug
						.add(Misc.getDateAndTime() + " Remove4: " + player.getPlayerName() + ", " + player.addressIp + ", " + player.addressUid + ", " + player.getX() + ", "
						     + player.getY() + ", " + player.getHeight() + ", " + player.teleX + ", " + player.teleY);
				RagBan.removeFromWilderness(player.addressIp, player.addressUid);
			}
		}

		teleportEvent(player, teleportType);
		return true;
	}

	/**
	 * The teleport cycle event.
	 */
	private static void teleportEvent(final Player player, final String teleportType) {
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent<Object>() {
			@Override
			public void execute(CycleEventContainer container) {
				if (player.isJailed()) {
					container.stop();
					return;
				}
				player.setTeleportCycle(player.getTeleportCycle() - 1);
				if ((teleportType.equalsIgnoreCase("tab") || teleportType.equalsIgnoreCase("barrows_tab")) && player.getTeleportCycle() == 2) {
					player.startAnimation(4731);
					player.gfx0(678);
					SoundSystem.sendSound(player, 202, 100);
				} else if (teleportType.contains("LEVER") && player.getTeleportCycle() == 3) {
					player.startAnimation(714);
					SoundSystem.sendSound(player, 272, 0);
				} else if (teleportType.contains("LEVER") && player.getTeleportCycle() == 2) {
					player.gfx100(308);
					SoundSystem.sendSound(player, 202, 25);
				}
				else if ((teleportType.equalsIgnoreCase("MODERN") || teleportType.contains("GLORY")) && player.getTeleportCycle() == 2) {
					player.gfx100(308);
					SoundSystem.sendSound(player, 202, 0);
				} else if (teleportType.equalsIgnoreCase("teleporter") && player.getTeleportCycle() == 3) {
					player.startAnimation(714);
					player.gfx0(342);
					SoundSystem.sendSound(player, 202, 0);
				}
				switch (teleportType) {
					case "SEED_POD":
						if (player.getTeleportCycle() == 1) {
							player.startAnimation(2590);
						}
						break;
				}

				if (player.getTeleportCycle() == 0) {
					container.stop();
				}
			}

			@Override
			public void stop() {
				player.usingTeleportWithNoCombatQueueing = false;
				if (teleportType.equals("LEVER")) {
					player.resetAnimation();
					player.forceSendAnimation = true;
				}
				if (player.isJailed()) {
					return;
				}
				if (player.isAdministratorRank() && ServerConfiguration.DEBUG_MODE) {
					Misc.print("Previous location: " + player.getX() + ", " + player.getY() + ", " + player.getHeight());
				}
				player.getPA().processTeleport();
				if ((teleportType.equalsIgnoreCase("tab") || teleportType.equalsIgnoreCase("barrows_tab")) && player.getTeleportCycle() == 0) {
					tabletReturn(player);
				}
				if (player.getPlayerPet() != null && player.getPlayerPet().isSummoned()) {
					Position nextOpen = Region.nextOpenTileOrNull(player.teleportToX, player.teleportToY, player.teleportToHeight);
					player.getPlayerPet().getPA().movePlayer(nextOpen.getX(), nextOpen.getY(), nextOpen.getZ());
				}
				if (!Area.inWilderness(player.teleportToX, player.teleportToY, player.teleportToHeight)) {
					player.setFrozenLength(0);
					InterfaceAssistant.turnOffAllFreezeOverlays(player);
				}
				Barrows.resetCoffinStatus(player);
				// Recipe for disaster teleport
				if (player.teleportToX == 1900 && player.teleportToY == 5346) {
					player.getPA().sendMessage("If you die, you will have to start the waves from scratch.");
					player.getPA().sendMessage("Your items are safe on death.");
					RecipeForDisaster.spawnNextWave(player, true, RecipeForDisaster.NPC_WAVE_LIST[0]);
				} else {
					player.rfdWave = 0;
				}

				if (player.teleportToX == 3659 && player.teleportToY == 3516) {
					player.getPA().sendMessage("Right click duo a player to start the zombie survival.");
				} else if (player.teleportToX == 1690 && player.teleportToY == 4250) {
					ClanChatHandler.displayDiceRulesInterface(player);
				}
				if (player.teleportToX == 2280 && player.teleportToY == 10022 && player.teleportToHeight >= 24) {
					KrakenCombat.spawnInstancedRoom(player);
				}

				PrivateAdminArea.spotLanded(player);
			}
		}, 1);
	}

	/**
	 * Reset player's animation so the player can move after doing the last animation of tablet.
	 *
	 * @param player The associated player.
	 */
	private static void tabletReturn(final Player player) {
		player.startAnimation(65535);
	}

	/**
	 * True, if the player is allowed to teleport.
	 *
	 * @param player The player.
	 * @param teleportType The type of teleport the player is using.
	 */
	private static boolean canTeleport(Player player, String teleportType, int x, int y, int height) {
		if (player.isJailed()) {
			player.playerAssistant.sendMessage("You can't teleport out of jail.");
			return false;
		}

		if (player.getDuelStatus() >= 5) {
			player.playerAssistant.sendMessage("You can't teleport during a duel!");
			return false;
		}

		if (teleportType.contains("GLORY") || teleportType.contains("SEED_POD")) {
			if (!Area.inCityPvpArea(player) && Area.inDangerousPvpArea(player) && player.getWildernessLevel() > 30 && !player.isAdministratorRank()
			    && !player.isInZombiesMinigame()) {
				player.playerAssistant.sendMessage("You can't teleport above level 30 in the wilderness.");
				return false;
			}
		} else {
			if (!Area.inCityPvpArea(player) && Area.inDangerousPvpArea(player) && player.getWildernessLevel() > 20 && !teleportType.contains("OBELISK") && !teleportType.contains(
					"LEVER") && !player.isAdministratorRank() && !player.isInZombiesMinigame()) {
				player.playerAssistant.sendMessage("You can't teleport above level 20 in the wilderness.");
				return false;
			}
		}

		if ((Area.inDangerousPvpArea(player) || player.getHeight() == 20) && System.currentTimeMillis() < player.teleBlockEndTime) {

			long time = (player.teleBlockEndTime - System.currentTimeMillis()) / 1000;
			player.playerAssistant.sendMessage("You are teleblocked and cannot teleport for " + Misc.getTimeLeft((int) time) + ".");
			return false;
		}

		if (player.getDead() || player.isTeleporting() || player.isAnEgg) {
			return false;
		}
		if (!teleportType.equals("TAB") && !teleportType.contains("GLORY") && !teleportType.contains("SEED_POD")) {
			if (!player.isAdministratorRank() && (Combat.wasAttackingAnotherPlayer(player, 9600) || Combat.wasUnderAttackByAnotherPlayer(player, 9600)) && !teleportType.contains(
					"OBELISK") && !teleportType.contains("LEVER") && !teleportType.equalsIgnoreCase("tab")) {
				Combat.inCombatAlert(player);
				return false;
			}

			if (!player.isAdministratorRank() && System.currentTimeMillis() - player.timeNpcAttackedPlayer < 9600 && Area.inDangerousPvpArea(player) && !teleportType.contains(
					"OBELISK") && !teleportType.contains("LEVER") && !teleportType.equalsIgnoreCase("tab")) {
				Combat.inCombatAlert(player);
				return false;
			}
		}
		if (player.isUsingFightCaves()) {
			player.playerAssistant.sendMessage("A strong force keeps you from teleporting.");
			return false;
		}
		if (!EdgeAndWestsRule.canProcessToDestinationWithBrews(player, x, y, height)) {
			return false;
		}
		if (teleportType.equals("LEVER")) {
			if (!Lever.canActivateLever(player, player.getObjectX(), player.getObjectY())) {
				return false;
			}
		}
		Minigame minigame = player.getMinigame();

		if (minigame != null) {
			if (!minigame.canTeleport(player)) {
				player.getPA().sendMessage("You cannot teleport out of this minigame.");
				return false;
			}
		}
		return true;
	}

}
