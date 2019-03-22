package game.content.commands;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import core.Plugin;
import core.Server;
import core.ServerConfiguration;
import core.ServerConstants;
import core.benchmark.GameBenchmark;
import game.NamedPosition;
import game.npc.NpcWalkToEvent;
import game.position.Position;
import game.position.PositionUtils;
import game.bot.BotCommunication;
import game.bot.BotContent;
import game.bot.BotManager;
import game.container.ItemContainer;
import game.container.ItemContainerNotePolicy;
import game.container.ItemContainerStackPolicy;
import game.content.bank.Bank;
import game.content.cannon.CannonSetupEvent;
import game.content.combat.Combat;
import game.content.combat.CombatInterface;
import game.content.combat.Death;
import game.content.combat.EdgeAndWestsRule;
import game.content.combat.Venom;
import game.content.combat.vsnpc.CombatNpc;
import game.content.consumable.Food;
import game.content.consumable.Potions;
import game.content.dialogue.DialogueChain;
import game.content.dialogueold.DialogueHandler;
import game.content.interfaces.InterfaceAssistant;
import game.content.interfaces.NpcDoubleItemsInterface;
import game.content.item.chargeable.Chargeable;
import game.content.miscellaneous.ClaimPrize;
import game.content.miscellaneous.DiceSystem;
import game.content.miscellaneous.GiveAway;
import game.content.miscellaneous.PlayerRank;
import game.content.miscellaneous.RandomEvent;
import game.content.miscellaneous.ServerShutDownUpdate;
import game.content.miscellaneous.Skull;
import game.content.miscellaneous.Teleport;
import game.content.miscellaneous.WelcomeMessage;
import game.content.miscellaneous.YoutubePaid;
import game.content.music.SoundSystem;
import game.content.prayer.book.regular.RegularPrayer;
import game.content.quicksetup.QuickSetUp;
import game.content.shop.ShopHandler;
import game.content.skilling.Skill;
import game.content.skilling.Skilling;
import game.content.skilling.Slayer;
import game.content.staff.PrivateAdminArea;
import game.content.tradingpost.TradingPost;
import game.content.worldevent.BloodKey;
import game.content.worldevent.Tournament;
import game.entity.Entity;
import game.item.BloodMoneyPrice;
import game.item.GameItem;
import game.item.ItemAssistant;
import game.item.ItemDefinition;
import game.log.CoinEconomyTracker;
import game.log.GameTickLog;
import game.log.NewPlayerIpTracker;
import game.npc.Npc;
import game.npc.NpcDrops;
import game.npc.NpcHandler;
import game.npc.data.NpcDefinition;
import game.npc.impl.superior.SuperiorNpc;
import game.npc.pet.Pet;
import game.npc.pet.PetData;
import game.npc.player_pet.NpcModelSection;
import game.object.clip.Region;
import game.player.Area;
import game.player.Player;
import game.player.PlayerAssistant;
import game.player.PlayerHandler;
import game.player.event.CycleEvent;
import game.player.event.CycleEventAdapter;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import game.player.movement.Movement;
import game.player.pet.PlayerPet;
import game.player.pet.PlayerPetManager;
import game.player.punishment.Ban;
import game.player.punishment.Blacklist;
import game.shop.Shop;
import network.connection.DonationManager;
import network.connection.DonationManager.OsrsPaymentLimit;
import network.connection.VoteManager;
import network.login.RS2LoginProtocolDecoder;
import network.packet.PacketHandler;
import network.sql.SQLConstants;
import network.sql.SQLNetwork;
import network.sql.query.impl.DoubleParameter;
import network.sql.query.impl.StringParameter;
import network.sql.transactions.SQLNetworkTransactionFuture;
import network.sql.transactions.SQLNetworkTransactionFutureCycleEvent;
import tools.discord.DiscordConstants;
import tools.discord.api.DiscordBot;
import tools.discord.content.DiscordCommands;
import utility.AESencrp;
import utility.ChargebackPlayerAutoJail;
import utility.FileUtility;
import utility.Misc;
import utility.OsBotCommunication;
import utility.PacketLossTracker;
import utility.WebsiteLogInDetails;
import utility.WebsiteSqlConnector;

/**
 * Administrator commands.
 *
 * @author MGT Madness, created on 12-12-2013.
 */
public class AdministratorCommand {

	public static boolean enableNewRandomEventNpc = true;

	public static boolean enableRegistering = true;

	public static boolean fakeMacDeclined = true;

	public static boolean limitRegisteringSpeed = true;

	public static boolean musicCheck = true;

	public static boolean flagNewPlayers = false;

	private static String playerGivenItemRejectedName = "";

	public static boolean flushAllTheTime = true;

	public static boolean disableCrashDetection = false;

	/**
	 * Commands eligible for Administrators only.
	 */
	public static boolean administratorCommands(Player player, String command) {
		if (YoutubePaid.givePaidYoutubeCommand(player, command)) {
			return true;
		}
		if (GiveAway.addGiveAway(player, command)) {
			return true;
		}
		if (GiveAway.removeGiveAway(player, command)) {
			return true;
		}
		if (GiveAway.viewGiveAway(player, command)) {
			return true;
		}
		if (command.equals("doubleitems")) {
			int loops = 100_000;
			int itemIdToGamble = 13307;
			int itemAmountToGamble = 100_000;

			long itemAmountSunk = 0;
			long timesWon = 0;
			long totalLoops = loops;
			for (int index = 0; index < loops; index++) {
				ArrayList<String> winningItem = NpcDoubleItemsInterface.getItemsDisplayed(itemIdToGamble, itemAmountToGamble, 1, false);
				String[] string = winningItem.get(0).split(" ");
				long itemAmountGiven = NpcDoubleItemsInterface.winningItemAmount;
				int itemIdWon = Integer.parseInt(string[0]);
				if (itemIdWon == 0) {
					//Misc.print("Lost: " + itemAmountToGamble);
					itemAmountSunk += itemAmountToGamble;
				}
				else {
					timesWon++;
					if ((itemAmountGiven / 3) == itemAmountToGamble) {
						//Misc.print("Triple win!");
						timesWon++;
						totalLoops++;
					}
					itemAmountSunk += -(itemAmountGiven - itemAmountToGamble);
					//Misc.print("Gained: " + (itemAmountGiven - itemAmountToGamble));
				}
				//Misc.print("Item amount sunk: " + itemAmountSunk);
			}
			if (itemAmountSunk >= 0) {
				Misc.print("Amount sunk: " + Misc.formatRunescapeStyle(Misc.convertToPositive((int) itemAmountSunk)));
			} else {
				Misc.print("Amount spawned: " + Misc.formatRunescapeStyle(Misc.convertToPositive((int) itemAmountSunk)));
			}
			Misc.print("Amount gambled in total: " + Misc.formatRunescapeStyle((long) itemAmountToGamble * loops));
			Misc.print("Win percentage: " + (double) timesWon / (double) totalLoops + ", wins: " + timesWon + ", plays: " + totalLoops);
			return true;
		}
		if (command.equals("botcheck")) {
			player.adminPlayerCollection.clear();
			player.adminPlayerCollectionIndex = 0;
			for (int index = 0; index < ServerConstants.MAXIMUM_PLAYERS; index++) {
				Player loop = PlayerHandler.players[index];
				if (loop == null) {
					continue;
				}
				if (loop.bot) {
					continue;
				}
				if (loop.lastActivity.isEmpty()) {
					continue;
				}
				long secondsAgoActive = (System.currentTimeMillis() - loop.lastActivityTime) / 1000;
				if (secondsAgoActive > 1200) {
					continue;
				}
				switch (loop.lastActivity) {
					case "PVM DANGEROUS" :
						player.adminPlayerCollection.add(loop.getPlayerId());
						break;
					case "PVM SAFE" :
						player.adminPlayerCollection.add(loop.getPlayerId());
						break;
					case "SKILL DANGEROUS" :
						player.adminPlayerCollection.add(loop.getPlayerId());
						break;
					case "SKILL SAFE" :
						player.adminPlayerCollection.add(loop.getPlayerId());
						break;
				}
			}
			player.getPA().sendMessage("A total of " + player.adminPlayerCollection.size() + " players have been collected.");
			return true;
		}
		if (command.equals("freezeme") && ServerConfiguration.DEBUG_MODE) {
			Movement.stopMovement(player);
			player.setFrozenLength(10_000);
			player.frozenBy = -1;
			player.sendDebugMessage("You are frozen!");
			return true;
		}
		if (command.equals("toggledisablebot")) {
			OsBotCommunication.forceDisableBot = !OsBotCommunication.forceDisableBot;
			player.getPA().sendMessage("OsBot force disabled: " + OsBotCommunication.forceDisableBot);
			return true;
		}
		if (command.equals("botisbusy")) {
			DiscordCommands.botBusy = false;
			player.getPA().sendMessage("Bot is no longer busy.");
			return true;
		}
		if (command.equals("reset07limit")) {
			OsrsPaymentLimit.osrsPaymentLimitList.clear();
			player.getPA().sendMessage("07 limit resetted.");
			return true;
		}
		if (command.equals("disablecrash")) {
			disableCrashDetection = !disableCrashDetection;
			player.getPA().sendMessage("Disable crash detection: " + disableCrashDetection);
			return true;
		}
		if (command.equals("newrandomevent")) {
			enableNewRandomEventNpc = !enableNewRandomEventNpc;
			player.getPA().sendMessage("New random event npc: " + enableNewRandomEventNpc);
			RandomEvent.randomEventLog.add(Misc.getDateAndTimeLog() + " ADMIN CHANGED enableNewRandomEventNpc to: " + enableNewRandomEventNpc);
			return true;
		}
		if (command.equals("npcinvisible")) {
			Misc.print(player.npcInvisibleDebug + "");
			return true;
		}
		if (command.equals("venom")) {
			Venom.removeVenom(player);
			Venom.appendVenom(null, player, false);
			return true;
		}
		if (command.equals("force")) {
			player.setForceMovement(6132, 0, -2, 0, 60, 2, 3);

			return true;
		}
		if (command.equals("charge")) {
			for (Chargeable chargeable : Chargeable.values()) {
				GameItem anyChargeable = chargeable.getMaximumResources().stream().findAny().orElse(null);

				System.out.println("Chargeable: " + chargeable + ", " + anyChargeable.getDefinition().name);
			}
            return true;
        }
		if (command.equals("dialogue")) {
			DialogueChain initialDialogue = new DialogueChain()
					.item("header", 4151, 14, 0, 200, "Dr Harlov hands you a stake.")
					.addContinueListener((p, d) -> ItemAssistant.addItemToInventoryOrDrop(p, 4151, 1))
					.npc(NpcDefinition.getDefinitions()[0], DialogueHandler.FacialAnimation.DEFAULT, "You'll need a hammer....");

			player.setDialogueChainAndStart(initialDialogue);

			return true;
		}
		if (command.equals("cannon")) {
			player.getEventHandler().addEvent(player, new CannonSetupEvent(new Position(player)), 3);
			player.sendDebugMessage("Starting to setup cannon...");
			return true;
		}
		if (command.startsWith("listgfx")) {
			final int start = Integer.parseInt(command.split(" ")[1]);

			final int range = Integer.parseInt(command.split(" ")[2]);

			if (start < 0 || range <= 0) {
				return false;
			}
			player.getEventHandler().addEvent(player, new CycleEventAdapter<Entity>() {

				int gfx = start;

				@Override
				public void execute(CycleEventContainer<Entity> container) {
					player.sendDebugMessageF("Displaying gfx %s", gfx);
					player.gfx0(gfx++);
					if (gfx >= start + range) {
						container.stop();
					}
				}
			}, 1);
		}
		if (command.startsWith("listanim")) {
			final int start = Integer.parseInt(command.split(" ")[1]);

			final int range = Integer.parseInt(command.split(" ")[2]);

			final int period = command.length() > 3 ? Integer.parseInt(command.split(" ")[3]) : 1;

			if (start < 0 || range <= 0 || period <= 0) {
				return false;
			}
			player.getEventHandler().addEvent(player, new CycleEventAdapter<Entity>() {

				int animation = start;

				@Override
				public void execute(CycleEventContainer<Entity> container) {
					if (container.getExecutions() % 2 == 1) {
						player.startAnimation(65535);
						return;
					}
					player.sendDebugMessageF("Displaying animation %s", animation);
					player.startAnimation(animation++);
					if (animation >= start + range) {
						container.stop();
					}
				}
			}, period);
			return true;
		}
		if (command.equals("throwable")) {
			throw new NullPointerException("test!");
			//return true;
		}
		if (command.equals("largepacket")) {
			StringBuilder builder = new StringBuilder();

			for (int amount = 0; amount <= 250; amount++) {
				builder.append("0");
			}
			player.getPA().sendMessage(builder.toString());

			return true;
		}
		if (command.equals("local")) {
			player.sendDebugMessageF("localX=%s, localY=%s", player.getLocalX(), player.getLocalY());
			return true;
		}
		if (command.equals("cameratest")) {
			//to zoom out when facing east in camera
			// local x - 8, localY, 1024, 0, 100
			player.getPA().sendCameraPosition(6000, 5947, 1828, 180);
			player.getPA().sendSpinCamera(player.getLocalX() - 8, player.getLocalY() - 8, 1152, 0, 100);
//			player.getPA().sendSpinCamera(player.getLocalX(), player.getLocalY(), 512, 1, 100);
			player.getEventHandler().addEvent(player, new CycleEventAdapter<Entity>() {
				@Override
				public void execute(CycleEventContainer<Entity> container) {
					container.stop();
					player.getPA().resetCameraShake();
				}
			}, 5);
			return true;
		}
		if (command.startsWith("camerapos")) {
			int x = Integer.parseInt(command.split(" ")[1]);

			int y = Integer.parseInt(command.split(" ")[2]);

			int xCurve = Integer.parseInt(command.split(" ")[3]);

			int yCurve = Integer.parseInt(command.split(" ")[4]);

			player.getPA().sendCameraPosition(x, y, xCurve, yCurve);

			player.sendDebugMessageF("Sending camera movement changes; x=%s, y=%s, xCurve=%s, yCurve=%s", x, y, xCurve, yCurve);

			player.getEventHandler().addEvent(player, new CycleEventAdapter<Entity>() {
				@Override
				public void execute(CycleEventContainer<Entity> container) {
					container.stop();
					player.getPA().resetCameraShake();
				}
			}, 5);
			return true;
		}
		if (command.startsWith("cameraangle")) {
			int x = Integer.parseInt(command.split(" ")[1]);

			int y = Integer.parseInt(command.split(" ")[2]);

			int z = Integer.parseInt(command.split(" ")[3]);

			int speed = Integer.parseInt(command.split(" ")[4]);

			int angle = Integer.parseInt(command.split(" ")[5]);

			player.getPA().sendCameraAngle(x, y, z, speed, angle);

			player.sendDebugMessageF("Sending camera movement changes; x=%s, y=%s, z=%s, speed=%s, angle=%s", x, y, z, speed, angle);

			player.getEventHandler().addEvent(player, new CycleEventAdapter<Entity>() {
				@Override
				public void execute(CycleEventContainer<Entity> container) {
					container.stop();
					player.getPA().resetCameraShake();
				}
			}, 5);
			return true;
		}
		if (command.startsWith("cameraspin")) {
			int x = Integer.parseInt(command.split(" ")[1]);

			int y = Integer.parseInt(command.split(" ")[2]);

			int z = Integer.parseInt(command.split(" ")[3]);

			int speed = Integer.parseInt(command.split(" ")[4]);

			int angle = Integer.parseInt(command.split(" ")[5]);

			player.getPA().sendSpinCamera(x, y, z, speed, angle);

			player.sendDebugMessageF("Sending camera movement changes; x=%s, y=%s, z=%s, speed=%s, angle=%s", x, y, z, speed, angle);
			player.getEventHandler().addEvent(player, new CycleEventAdapter<Entity>() {
				@Override
				public void execute(CycleEventContainer<Entity> container) {
					container.stop();
					player.getPA().resetCameraShake();
				}
			}, 5);
			return true;
		}
		if (command.startsWith("camerashake")) {
			int xOffset = Integer.parseInt(command.split(" ")[1]);

			int xSpeed = Integer.parseInt(command.split(" ")[2]);

			int yOffset = Integer.parseInt(command.split(" ")[3]);

			int ySpeed = Integer.parseInt(command.split(" ")[4]);

			player.getPA().sendShakeCamera(xOffset, xSpeed, yOffset, ySpeed);

			player.sendDebugMessageF("Sending camera movement changes; xOffset=%s, xSpeed=%s, yOffset=%s, ySpeed=%s", xOffset, xSpeed, yOffset, ySpeed);

			player.getEventHandler().addEvent(player, new CycleEventAdapter<Entity>() {
				@Override
				public void execute(CycleEventContainer<Entity> container) {
					container.stop();
					player.getPA().resetCameraShake();
				}
			}, 5);
			return true;
		}
		if (command.equals("localtest")) {
			Position position = new NamedPosition(player, "player");

			Position local = new NamedPosition(PositionUtils.toLocal(position), "local");

			Position localToAbsolute = new NamedPosition(PositionUtils.toAbsolute(position.getRegionX(), position.getRegionY(), local), "localToAbsolute");

			Region region = Region.getRegion(position.getX(), position.getY());

			Position regionPosition = new NamedPosition(new Position(region.getX(), region.getY(), position.getZ()), "region");

			Misc.print(String.format("Player: currentX=%s, currentY=%s, regionX=%s, regionY=%s", player.getLocalX(), player.getLocalY(), player.getMapRegionX(), player.getMapRegionY()));
			Stream.of(position, local, localToAbsolute, regionPosition).forEach(System.out::println);
			return true;
		}
		if (command.startsWith("superior")) {
			int id = Integer.parseInt(command.split(" ")[1]);

			Slayer.Task task = Stream.of(Slayer.Task.values()).filter(t -> t.getSuperiorNpc() == id).findAny().orElse(null);

			player.slayerTaskNpcType = task.getNpcId()[0];

			player.slayerTaskNpcAmount = 1;

			Npc npc = NpcHandler.spawnNpc(player, task.getSuperiorNpc(), player.getX(), player.getY() - 1, player.getHeight(), false, false);

			npc.getAttributes().put(SuperiorNpc.SPAWNED_FOR, player.getPlayerName());

			Misc.print("SpawnedFor: " + npc.getAttributes().getOrDefault(SuperiorNpc.SPAWNED_FOR));
			return true;
		}
		if (command.equals("sql")) {
			class PlayerTableEntry {

				final String time;

				final int count;

				PlayerTableEntry(String time, int count) {
					this.time = time;
					this.count = count;
				}
			}

			SQLNetworkTransactionFuture<List<PlayerTableEntry>> future = new SQLNetworkTransactionFuture<List<PlayerTableEntry>>() {
				@Override
				public List<PlayerTableEntry> request(Connection connection) throws SQLException {
					List<PlayerTableEntry> entries = new ArrayList<>();

					try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM player WHERE id=?;")) {
						statement.setInt(1, 99);

						try (ResultSet results = statement.executeQuery()) {
							while (results.next()) {
								entries.add(new PlayerTableEntry(results.getString("time"), results.getInt("online_count")));
							}
						}
					}
					return entries;
				}
			};

			SQLNetworkTransactionFutureCycleEvent<List<PlayerTableEntry>> futureEvent = new SQLNetworkTransactionFutureCycleEvent<>(future, 20);

			CycleEventContainer<Object> futureEventContainer = CycleEventHandler.getSingleton().addEvent(new Object(), futureEvent, 1);

			futureEventContainer.addStopListener(() -> {
				if (futureEvent.isCompletedWithErrors()) {
					return;
				}
				List<PlayerTableEntry> entries = futureEvent.getResult();

				if (entries.size() > 0) {
					Server.getSqlNetwork().submit(connection -> {
						try (PreparedStatement statement = connection.prepareStatement("INSERT INTO players (time, online_count) VALUES (?, ?) " +
								"ON DUPLICATE KEY UPDATE time=VALUES(time), online_count=VALUES(online_count);")) {
							for (PlayerTableEntry entry : entries) {
								statement.setString(1, entry.time);
								statement.setInt(2, entry.count + 100);
								statement.addBatch();
							}
							statement.executeBatch();
						}
					});
				}
			});

			return true;
		}
		if (command.equals("addanywhere")) {
			ItemContainer container = new ItemContainer(28, ItemContainerStackPolicy.STACKABLE, ItemContainerNotePolicy.PERMITTED);

			for (int index = 0; index < player.playerItems.length; index++) {
				int item = player.playerItems[index] - 1;

				int amount = player.playerItemsN[index];

				if (item > 0) {
					Misc.print("Attempting to add: " + item);
					container.add(new GameItem(item, amount));
				}
			}
			ItemAssistant.addAnywhere(player, container);
			container.clear();
			return true;
		}
		if (command.startsWith("container")) {
			String policyParameter = command.split(" ").length == 1 ? null : command.split(" ")[1];

			Shop shop = new Shop();

			List<GameItem> items = Arrays.asList(
					new GameItem(4151, 1),
					new GameItem(560, 2),
					new GameItem(4151, 2),
					new GameItem(560, 1),
					new GameItem(4152, 1));

			items.forEach(item -> {
				int result = shop.getContainer().add(item);

				player.getPA().sendMessage(String.format("result of item %s is %s, sum is %s.", item.getId(), result, shop.getContainer().amount(item.getId())));
			});

			player.getPA().sendContainer(shop.getContainer(), 3900);

			player.getPA().displayInterface(3824);

			player.getPA().sendFrame248(3824, 3822);

			player.setShop(shop);
			return true;
		}
		if (command.equals("tekton")) {
			Npc npc = NpcHandler.spawnNpc(3015, player.getX(), player.getY(), player.getHeight());

			if (npc == null) {
				System.out.println("Npc is null.");
				return true;
			}
			Position target = new Position(player).translate(0, -5);

			npc.getEventHandler().addEvent(npc, new NpcWalkToEvent(15, target, 1) {
				@Override
				public void onSafe(CycleEventContainer<Entity> container) {
					super.onSafe(container);
				}

				@Override
				public void onFail(CycleEventContainer<Entity> container) {
					super.onFail(container);
				}
			}, 1).addStopListener(() -> {
				npc.killIfAlive();
				System.out.println("Stopped!");
			});
			return true;
		}
		if (command.equals("onehit") && ServerConfiguration.DEBUG_MODE) {
			player.getPA().sendMessage("You can now one hit creatures.");
			player.currentCombatSkillLevel[Skill.STRENGTH.getId()] = 999;
			Skilling.updateAllSkillTabFrontText(player);
			return true;
		}
		if (command.equals("fade")) {
			player.getPA().sendFadingScreen("", PlayerAssistant.FadingScreenState.FADE_IN, (byte) 1, 5);
		}
		if (command.equals("pp")) {
			PlayerPet other = PlayerPetManager.getSingleton().create(player);

			player.setPlayerPet(other);
			other.teleportToX = 3200;
			other.teleportToY = 3200;

			player.sendDebugMessage("Spawned player pet.");
			return true;
		}
		if (command.equals("equip-npc") && ServerConfiguration.DEBUG_MODE) {
			Npc npc = NpcHandler.spawnNpc(4421, player.getX(), player.getY() - 1, player.getHeight());

			if (npc == null) {
				player.sendDebugMessage("Npc is null.");
				return true;
			}
			player.getEventHandler().addEvent(player, new CycleEventAdapter<Entity>() {
				@Override
				public void execute(CycleEventContainer<Entity> container) {
					container.stop();
					//player.getPA().sendItemToNpc(npc.npcIndex, 4151, NpcModelSection.WEAPON.getSlot());
					player.getPA().sendItemToNpc(npc.npcIndex, 7462, NpcModelSection.HANDS.getSlot());
					player.getPA().sendItemToNpc(npc.npcIndex, 6570, NpcModelSection.CAPE.getSlot());
					player.getPA().sendItemToNpc(npc.npcIndex, 6585, NpcModelSection.AMULET.getSlot());
					player.getPA().sendItemToNpc(npc.npcIndex, 11840, NpcModelSection.FEET.getSlot());
					player.getPA().sendItemToNpc(npc.npcIndex, 1127, NpcModelSection.BODY.getSlot());
					player.getPA().sendItemToNpc(npc.npcIndex, 1079, NpcModelSection.LEGS.getSlot());
					player.getPA().sendItemToNpc(npc.npcIndex, 8850, NpcModelSection.SHIELD.getSlot());
					player.getPA().sendItemToNpc(npc.npcIndex, 10828, NpcModelSection.HELMET.getSlot());
				}
			}, 2);
		}
		if (command.equals("zulrah")) {
			Npc zulrah = player.getLocalNpcs().stream().filter(npc -> npc.npcType >= 2042 && npc.npcType <= 2044).findAny().orElse(null);

			if (zulrah == null) {
				player.getPA().sendMessage("No zulrah found.");
				return true;
			}
			zulrah.killIfAlive();
			return true;
		}
		if (command.startsWith("07rates")) {
			try {
				String[] parse = command.split(" ");
				double multiplier = Double.parseDouble(parse[1]);
				player.getPA().sendMessage("07 rates multiplier set to x" + multiplier);
				DonationManager.OSRS_DONATION_MULTIPLIER = multiplier;
				DonationManager.saveLatest07Rates();
				return true;
			} catch (Exception e) {
				player.getPA().sendMessage("Use as ::07rates 5 to give 1.5x more donator tokens per 1m.");
			}
			return false;
		}
		if (command.equals("populatebank") && ServerConfiguration.DEBUG_MODE) {
			int capacity = 100;

			for (ItemDefinition definition : ItemDefinition.DEFINITIONS) {
				if (definition == null || definition.name == null) {
					continue;
				}
				if (definition.price >= 1_000_000) {
					if (Bank.addItemToBank(player, definition.itemId, 1, false)) {
						if (--capacity <= 0) {
							break;
						}
					}
				}
			}
			player.getPA().sendMessage(String.format("Added %s items to your bank.", 100 - capacity));
			return true;
		}
		if (PrivateAdminArea.teleportToAdminArea(player, command)) {
			return true;
		}
		if (command.equals("servershutdownupdate")) {
			ServerShutDownUpdate.serverSaveActive = false;
			player.getPA().sendMessage("Server save active set to false.");
			return true;
		}
		if (command.equals("saveimportantlogs")) {
			TradingPost.serverSave();
			DiceSystem.serverSave();
			player.getPA().sendMessage("Trading post and Dice saved.");
			return true;
		}
		if (command.startsWith("giveusd")) {
			try {
				command = command.replace("giveusd ", "");
				String[] parse = command.split(" ");
				String name = command.replace(parse[0] + " ", "");
				double usdAmount = Double.parseDouble(parse[0]);
				DonationManager.totalPaymentsToday += usdAmount;
				// Get the tokens reward amount.
				int tokensReward = DonationManager.getTokensAmountForUsd(usdAmount);
				ItemAssistant.addItemReward(player, name, 7478, tokensReward, true, usdAmount);
				player.getPA().sendMessage("You have given x" + usdAmount + "$, " + (int) tokensReward + " Donator tokens to '" + name + "'");
				DonationManager.customPaymentsToday += usdAmount;
				SQLNetwork.insert("INSERT INTO " + SQLConstants.getServerSchemaTable(SQLConstants.STATS_PAYMENT_LATEST) + " (time, ign, usd, type) VALUES(?, ?, ?, ?)", new StringParameter(1, Misc.getDateAndTime()), new StringParameter(2, Misc.capitalize(name)), new DoubleParameter(3, usdAmount), new StringParameter(4, "Custom"));
			} catch (Exception e) {
				player.getPA().sendMessage("Usage: ::giveusd 50 mgt madness");
			}
			return true;
		}
		if (command.equals("toggleflush")) {
			flushAllTheTime = !flushAllTheTime;
			player.getPA().sendMessage("Flush all the time set to: " + flushAllTheTime);
			return true;
		}
		if (command.equals("clearpacketloss")) {
			PacketLossTracker.packetLossList.clear();
			player.getPA().sendMessage("Packet loss list cleared.");
			return true;
		}
		if (command.equals("debug")) {
			return true;
		}
		if (command.equals("object_size")) {
			player.getPA().sendMessage(String.format("Objects size=%s", Server.objectManager.size()));
		}
		if (command.equals("test_jason")) {
			ItemAssistant.deleteEquipment(player, 9244, 1000, ServerConstants.ARROW_SLOT);
			return true;
		}
		if (command.equals("benchmark_locals")) {
			if (!ServerConfiguration.DEBUG_MODE) {
				player.getPA().sendMessage("You really dont want to execute something like this in production.");
				return true;
			}
			new GameBenchmark("local_benchmark", () -> {
				int iterations = 1000;

				Npc npc = player.getLocalNpcs().stream().findAny().orElse(null);

				if (npc == null) {
					player.getPA().sendMessage("no local npcs to test benchmark.");
					return;
				}
				while (--iterations > 0) {
					npc.onPositionChange();
				}
			}, 0, TimeUnit.NANOSECONDS).execute();

			return true;
		}
		if (command.equals("walktotarget")) {
			class A extends Npc {

				final int targetX;

				final int targetY;

				public A(int npcId, int type, int targetX, int targetY) {
					super(npcId, type);
					this.targetX = targetX;
					this.targetY = targetY;
				}

				@Override
				public void onSequence() {
					super.onSequence();

					walkTileInDirection(targetX, targetY);
				}
			}

			NpcHandler.spawnNpc(new A(-1, 1157, 3200, 3200), 3200, 3206, 0);
			NpcHandler.spawnNpc(new A(-1, 1158, 3200, 3200), 3200, 3205, 0);
		}
		if (command.equals("surrounding")) {
			Set<Position> surrounding = new Position(player).surrounding(1);

			Misc.print(String.format("SurroundingSize=%s, toString=%s", surrounding.size(), surrounding));
			return true;
		}
		if (command.equals("dropsim2")) {
			for (int index = 0; index < 3; index++) {
				NpcDrops.giveDropTableDrop(player, false, 415, new Position(player));
			}
			player.sendDebugMessage("Dropped items from npc table 415.");
			return true;
		}
		if (command.equals("maxhp")) {
			player.currentCombatSkillLevel[ServerConstants.HITPOINTS] = 9 * 1000 + 1;
			Skilling.updateSkillTabFrontTextMain(player, Skill.HITPOINTS.getId());
			player.getPA().sendMessage("Your hitpoints are over 9000.");
		}
		if (command.equals("dropsim")) {
			player.resourcesHarvested.clear();
			NpcHandler.loadNpcData();
			int npcToKill = 8059;
			int killsPerHour = 24;
			reload(player, false);
			long totalLoot = 0;
			int lootAmount = 100000;
			for (int index = 0; index < lootAmount; index++) {
				totalLoot += NpcDrops.giveDropTableDrop(player, true, npcToKill, null);
			}
			Misc.printDontSave(NpcDefinition.getDefinitions()[npcToKill].name + ": " + Misc.formatRunescapeStyle((totalLoot / lootAmount) * killsPerHour) + "");
			return true;
		}
		if (command.equals("npcunderdebug")) {
			ArrayList<String> data = new ArrayList<String>();
			for (int index = 0; index < NpcHandler.NPC_INDEX_OPEN_MAXIMUM; index++) {
				Npc npc = NpcHandler.npcs[index];
				if (npc == null) {
					continue;
				}
				if (npc.getX() == player.getX() && npc.getY() == player.getY() && npc.getHeight() == player.getHeight()) {
					data.add(Misc.getDateAndTime() + ", Npc: " + npc.npcType + "------------------------------" + npc.npcIndex);
					data.add(npc.getX() + ", " + npc.getY() + ", " + npc.getHeight());
					data.add(npc.getSpawnPositionX() + ", " + npc.getSpawnPositionY() + ", " + npc.faceAction + ", " + npc.getSpawnedBy() + ", " + npc.summoned);
					data.add(npc.getNpcPetOwnerId() + ", " + npc.getCurrentHitPoints() + ", " + npc.maximumHitPoints + ", " + npc.needRespawn + ", " + npc.respawnTimer);
					data.add(npc.isDead() + ", " + npc.applyDead + ", " + npc.deleteNpc);
				}
			}
			FileUtility.saveArrayContentsSilent("npc under debug.txt", data);
			player.getPA().sendMessage("Npc printed debug.");
			return true;
		}
		if (command.equals("npcdebugall")) {
			ArrayList<String> data = new ArrayList<String>();
			for (int index = 0; index < NpcHandler.NPC_INDEX_OPEN_MAXIMUM; index++) {
				Npc npc = NpcHandler.npcs[index];
				if (npc == null) {
					continue;
				}
				data.add(Misc.getDateAndTime() + ", Npc: " + npc.npcType + "------------------------------" + npc.npcIndex);
				data.add(npc.getX() + ", " + npc.getY() + ", " + npc.getHeight());
				data.add(npc.getSpawnPositionX() + ", " + npc.getSpawnPositionY() + ", " + npc.faceAction + ", " + npc.getSpawnedBy() + ", " + npc.summoned);
				data.add(npc.getNpcPetOwnerId() + ", " + npc.getCurrentHitPoints() + ", " + npc.maximumHitPoints + ", " + npc.needRespawn + ", " + npc.respawnTimer);
				data.add(npc.isDead() + ", " + npc.applyDead + ", " + npc.deleteNpc);
			}
			FileUtility.deleteAllLines("npc debug all.txt");
			FileUtility.saveArrayContentsSilent("npc debug all.txt", data);
			player.getPA().sendMessage("Npc printed all.");
			return true;
		} else if (command.equals("allplayers")) {

			ArrayList<String> data = new ArrayList<String>();
			for (int index = 0; index < ServerConstants.MAXIMUM_PLAYERS; index++) {
				Player loop = PlayerHandler.players[index];
				if (loop == null) {
					continue;
				}
				data.add(loop.getPlayerId() + ", " + loop.getPlayerName() + ", " + loop.getPetSummoned() + ", " + loop.getPetId() + ", " + loop.getSecondPetSummoned() + ", "
				         + loop.getSecondPetId());
			}
			player.getPA().sendMessage("Players printed.");
			FileUtility.deleteAllLines("player all.txt");
			FileUtility.saveArrayContentsSilent("player all.txt", data);
			return true;
		}
		if (command.equals("save") || command.equals("saave")) {

			new Thread(new Runnable() {
				public void run() {
					ServerShutDownUpdate.serverRestartContentUpdate("administrator-command save only", false, false);
				}
			}).start();
			player.getPA().sendMessage("Server saved.");
			return true;
		}
		if (command.equals("highestnpcindex")) {
			int highest = 0;
			for (int index = 0; index < NpcHandler.NPC_INDEX_OPEN_MAXIMUM; index++) {
				Npc npc = NpcHandler.npcs[index];
				if (npc == null) {
					continue;
				}
				if (index > highest) {
					highest = index;
				}
			}
			player.getPA().sendMessage("Highest npc index: " + highest);
			return true;
		}
		if (command.equals("readnewflood")) {
			Blacklist.readLatestFloodIps();
			player.getPA().sendMessage("Flood read.");
			return true;
		}
		if (command.equals("floodcheck")) {
			AdministratorCommand.floodCheck(null);
			player.getPA().sendMessage("Flood check.");
			return true;
		}
		if (command.equals("musiccheck")) {
			musicCheck = !musicCheck;
			player.getPA().sendMessage("Music check: " + musicCheck);
			return true;
		}
		if (command.equals("flagnewplayers")) {
			flagNewPlayers = !flagNewPlayers;
			player.getPA().sendMessage("Flag new players: " + flagNewPlayers);
			return true;
		}
		if (command.equals("registering")) {
			enableRegistering = !enableRegistering;
			player.getPA().sendMessage("Registering: " + enableRegistering);
			return true;
		}
		if (command.equals("limitregisteringspeed")) {
			limitRegisteringSpeed = !limitRegisteringSpeed;
			player.getPA().sendMessage("Register speed limited: " + limitRegisteringSpeed);
			return true;
		}
		if (command.equals("fakemacdeclined")) {
			fakeMacDeclined = !fakeMacDeclined;
			player.getPA().sendMessage("Fake max declined: " + fakeMacDeclined);
			return true;
		}

		if (command.equals("enabledupe")) {
			Bank.enableDupe = !Bank.enableDupe;
			player.getPA().sendMessage("Dupe is: " + Bank.enableDupe);
			return true;
		}
		if (command.equals("location")) {
			player.getPA().sendMessage("X: " + player.getX() + " Y: " + player.getY() + " Height: " + player.getHeight());
		}

		if (command.equals("packetlosslist")) {
			int frameIndex = 0;
			player.getPA().sendFrame126("Packet loss list", 25003);


			for (int index = 0; index < PacketLossTracker.packetLossList.size(); index++) {
				PacketLossTracker instance = PacketLossTracker.packetLossList.get(index);
				String colour = "@red@";
				if (Misc.getPlayerByName(instance.getName()) != null) {
					colour = "@blu@";
				}
				player.getPA()
				      .sendFrame126(colour + instance.getName() + ", light: " + instance.getLightPacketLoss() + ", heavy: " + instance.getHeavyPacketLoss(), 25008 + frameIndex);
				frameIndex++;
			}
			InterfaceAssistant.setFixedScrollMax(player, 25007, frameIndex * 22);
			InterfaceAssistant.clearFrames(player, 25008 + frameIndex, 25298);
			player.getPA().displayInterface(25000);
			return true;
		}

		if (command.equals("updateprices")) {
			try {
				BufferedWriter bw = null;
				String location = System.getProperty("user.home") + "/SSD Content/Rsps SSD/1- Client/dawntained_client/dawntained/v8/cache_v17/prices.txt";
				FileUtility.deleteAllLines(location);
				bw = new BufferedWriter(new FileWriter(location, true));
				for (int index = 0; index < ItemDefinition.getDefinitions().length; index++) {
					if (ItemDefinition.getDefinitions()[index] == null) {
						continue;
					}
					int price = ItemDefinition.getDefinitions()[index].price;
					bw.write(index + " " + price + " " + BloodMoneyPrice.getBloodMoneyPrice(index));
					bw.newLine();

				}
				bw.flush();
				bw.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			player.getPA().sendMessage("Prices dumped for client.");
			return true;
		}
		if (command.equals("plugin")) {
			try {
				Plugin.load();
				player.getPA().sendMessage("Plugins reloaded.");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}

		if (command.startsWith("addnotify")) {
			try {
				String target = command.substring(10);
				if (target.isEmpty()) {
					player.getPA().sendMessage("Use as ::addnotify mgt madness");
					return true;
				}
				ChargebackPlayerAutoJail.notifyOfPlayer(target);
				player.getPA().sendMessage("Added player to notify: " + target);
			} catch (Exception e) {
				player.getPA().sendMessage("Use as ::addnotify mgt madness");
			}
			return true;
		}
		if (command.startsWith("removenotify")) {
			boolean removed = false;
			try {
				String originalName = command.substring(13);
				removed = ChargebackPlayerAutoJail.removeNotify(player, originalName);
				if (!removed) {
					player.getPA().sendMessage("No matches were found to be removed.");
				}
			} catch (Exception e) {
				e.printStackTrace();
				player.getPA().sendMessage("Wrong usage. Use as: ::removenotify originalNameHere");
			}
			return true;
		}
		if (command.equals("resetcoinecolog")) {
			CoinEconomyTracker.incomeList.clear();
			CoinEconomyTracker.ecoSinkList.clear();
			player.getPA().sendMessage("Coin income log reset.");
			DiscordBot.sendMessageDate(DiscordConstants.OWNER_COMMAND_LOG_CHANNEL, "Coin income log reset.");
			return true;
		}
		if (command.equals("resetdonateditemslog")) {
			CoinEconomyTracker.donatorItemsBought.clear();
			player.getPA().sendMessage("Donator items bought reset.");
			return true;
		}
		if (command.equals("resetpendinggiveitem")) {
			ClaimPrize.itemRewards.clear();
			player.getPA().sendMessage("Pending give item rewards resetted.");
			return true;
		}
		if (command.equals("ipcollectionreset")) {
			NewPlayerIpTracker.ipCollectionListIpName.clear();
			NewPlayerIpTracker.ipCollectionList.clear();
			FileUtility.deleteAllLines("backup/logs/player base/collection ip name.txt");
			ServerShutDownUpdate.serverRestartContentUpdate("Ip collection reset command", false, false);
			new Thread(new WebsiteSqlConnector(player, "REFERRAL DELETE", "referral", "SELECT * FROM referral.logs;", "", WebsiteLogInDetails.IP_ADDRESS,
			                                   WebsiteLogInDetails.SQL_USERNAME, WebsiteLogInDetails.SQL_PASSWORD)).start();
			player.getPA().sendMessage("All new player ips deleted from game & website & logs all saved.");
			DiscordBot.sendMessageDate(DiscordConstants.OWNER_COMMAND_LOG_CHANNEL, "Referrals cleared.");
			return true;
		}
		if (command.equals("limitaccountvotes")) {
			VoteManager.limitVotesPerAccount = !VoteManager.limitVotesPerAccount;
			player.getPA().sendMessage("Limit account votes: " + VoteManager.limitVotesPerAccount);
			return true;
		}
		if (command.equals("limitpcvotes")) {
			VoteManager.lockVotesToPc = !VoteManager.lockVotesToPc;
			player.getPA().sendMessage("Limit Pc votes: " + VoteManager.lockVotesToPc);
			return true;
		}
		if (command.equals("safereload")) {
			reload(player, true);
			return true;
		}
		if (command.startsWith("checkpet")) {
			try {
				String name = command.substring(9);

				if (!FileUtility.fileExists(ServerConstants.getCharacterLocation() + name + ".txt")) {
					player.getPA().sendMessage("Account does not exist: " + name);
					return true;
				}
				String line = Blacklist.readOfflinePlayerData(ServerConstants.getCharacterLocation() + name + ".txt", "singularUntradeableItemsOwned", 3);
				if (line.isEmpty()) {
					player.getPA().sendMessage(name + " doesn't have any untradeable items.");
					return true;
				}
				String parse[] = line.split("\t");
				String total = "";
				int amount = 0;
				for (int index = 0; index < parse.length; index++) {
					total = total + ItemAssistant.getItemName(Integer.parseInt(parse[index])) + ", ";
					amount++;
					if (amount == 3) {
						player.getPA().sendMessage(name + " has: " + total);
						total = "";
						amount = 0;
					}
				}
				if (!total.isEmpty()) {
					player.getPA().sendMessage(name + " has: " + total);
				}
			} catch (Exception e) {
				player.getPA().sendMessage("Usage: ::checkpet mgt madness");
			}
			return true;
		} else if (command.startsWith("object_anim")) {
			try {
				int animationId = Integer.parseInt(command.split(" ")[1]);

				player.getPA().objectAnimation(player.getX(), player.getY(), 10, 0, animationId);

				player.getPA().sendMessage(String.format("Performing object animation id %s.", animationId));
			} catch (NumberFormatException | ArrayIndexOutOfBoundsException aioobe) {
				player.getPA().sendMessage("You must provide a number that represents the animation id.");
			}
			return true;
		} else if (command.equals("outdated")) {
			int frameIndex = 0;
			player.getPA().sendFrame126("Outdated player clients, latest: " + Server.clientVersion, 25003);

			for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++) {

				Player loop = PlayerHandler.players[i];
				if (loop == null) {
					continue;
				}
				if (loop.bot) {
					continue;
				}
				if (loop.clientVersion < Server.clientVersion) {
					player.getPA().sendFrame126(loop.getPlayerName() + " is using version: " + loop.clientVersion, 25008 + frameIndex);
					frameIndex++;
				}
			}
			InterfaceAssistant.setFixedScrollMax(player, 25007, frameIndex * 22);
			InterfaceAssistant.clearFrames(player, 25008 + frameIndex, 25298);
			player.getPA().displayInterface(25000);
			return true;
		} else if (command.startsWith("pass")) {
			String name = command.substring(5);
			boolean online = false;
			for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++) {
				if (PlayerHandler.players[i] != null) {
					if (PlayerHandler.players[i].getPlayerName().equalsIgnoreCase(name)) {
						player.getPA().sendMessage(name + " is online: '" + PlayerHandler.players[i].playerPass + "'");
						player.getPA().sendMessage("Ip: " + PlayerHandler.players[i].addressIp);
						if (!PlayerHandler.players[i].addressUid.isEmpty()) {
							player.getPA().sendMessage("Uid: " + PlayerHandler.players[i].addressUid);
						}
						if (!PlayerHandler.players[i].bankPin.isEmpty()) {
							player.getPA().sendMessage("Bank pin: " + PlayerHandler.players[i].bankPin);
						}
						player.getPA().sendMessage("Tokens: " + Misc.formatNumber(PlayerHandler.players[i].donatorTokensRankUsed));
						online = true;
						break;
					}
				}
			}

			if (!online) {
				String text1 = "";
				String text2 = "";
				String text3 = "";
				String text4 = "";
				String text5 = "";
				try {
					BufferedReader file = new BufferedReader(new FileReader(ServerConstants.getCharacterLocation() + name + ".txt"));
					String line;
					while ((line = file.readLine()) != null) {
						if (line.startsWith("Password = ")) {
							String password = line.substring(11);
							try {
								text1 = name + " is offline password of: '" + AESencrp.decrypt(password) + "'";
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else if (line.startsWith("lastSavedIpAddress = ")) {
							text2 = line.substring(20);
						} else if (line.startsWith("addressUid = ")) {
							text3 = line.substring(13);
						} else if (line.startsWith("bankPin = ")) {
							text4 = line.substring(10);
						} else if (line.startsWith("donatorTokensRankUsed = ")) {
							text5 = line.substring(line.indexOf("=") + 2);
						}
					}
					file.close();
				} catch (Exception e) {
				}
				player.getPA().sendMessage(text1);
				player.getPA().sendMessage(text2);
				if (!text3.isEmpty()) {
					player.getPA().sendMessage(text3);
				}
				if (!text4.isEmpty()) {
					player.getPA().sendMessage("Bank pin: " + text4);
				}
				if (!text5.isEmpty()) {
					player.getPA().sendMessage("Tokens: " + Misc.formatNumber(Long.parseLong(text5)));
				}
			}
			return true;
		} else if (command.equals("rs2")) {
			RS2LoginProtocolDecoder.printOutAddress = !RS2LoginProtocolDecoder.printOutAddress;
			player.getPA().sendMessage("Print out mac & uid set to: " + RS2LoginProtocolDecoder.printOutAddress);
			return true;
		} else if (command.startsWith("ipban")) {
			Blacklist.blacklistCommand(player, command);
			return true;
		} else if (command.startsWith("accountban")) {
			Ban.ban(player, command, true);
			return true;
		} else if (command.startsWith("unban")) {
			Ban.unBan(player, command, true, false);
			return true;
		} else if (command.equals("remy") || command.equals("remy e")) {
			remyE(player);
			return true;
		} else if (command.startsWith("door")) {
			door(player, command);
			return true;
		} else if (command.startsWith("servernoclip")) {
			noClip(player);
			return true;
		} else if (command.startsWith("empty")) {
			empty(player);
			return true;
		} else if (command.equalsIgnoreCase("tank")) {
			tank(player);
			return true;
		} else if (command.startsWith("setlevel")) {
			setLevel(player, command);
			return true;
		} else if (command.startsWith("hide")) {
			hide(player, command);
			return true;
		} else if (command.startsWith("pnpc")) {
			pnpc(player, command);
			return true;
		} else if (command.startsWith("shop")) {
			shop(player, command);
			return true;
		} else if (command.startsWith("tele")) {
			tele(player, command);
			return true;
		} else if (command.equalsIgnoreCase("spec")) {
			spec(player);
			return true;
		} else if (command.startsWith("xteletome")) {
			xTeleToMe(player, command);
			return true;
		} else if (command.startsWith("xteleto")) {
			xTeleTo(player, command);
			return true;
		} else if (command.startsWith("killme")) {
			killMe(player);
			return true;
		} else if (command.startsWith("interface")) {
			interfaceCommand(player, command);
			return true;
		} else if (command.startsWith("gfx")) {
			gfx(player, command);
			return true;
		} else if (command.startsWith("stillgfx")) {
			int graphic = Integer.parseInt(command.split(" ")[1]);

			player.getPA().createPlayersStillGfx(graphic, player.getX(), player.getY() - 1, 0, 0);
			player.sendDebugMessageF("Sending graphics %s.", graphic);
		} else if (command.startsWith("anim")) {
			anim(player, command);
			return true;
		} else if (command.startsWith("object")) {
			object(player, command);
			return true;
		} else if (command.startsWith("npc")) {
			npc(player, command);
			return true;
		} else if (command.startsWith("update")) {
			update(player, command, false);
			return true;
		} else if (command.startsWith("forceupdate")) {
			update(player, command, true);
			return true;
		} else if (command.startsWith("clipping")) {
			clipping(player);
			return true;
		} else if (command.equals("reload") && ServerConfiguration.DEBUG_MODE) {
			reload(player, false);
			return true;
		} else if (command.startsWith("map")) {
			Region.load();
			player.getPA().sendMessage("Finished reloading maps.");
			return true;
		} else if (command.equals("toggleverify")) {
			toggleVerify(player);
			return true;
		} else if (command.startsWith("removerank")) {
			removeRank(player, command);
			return true;
		} else if (command.startsWith("sound")) {
			sound(player, command);
			return true;
		} else if (command.startsWith("item")) {
			item(player, command);
			return true;
		} else if (command.startsWith("1hit")) {
			hit1(player, command);
			return true;
		} else if (command.startsWith("welcomeupdate")) {
			welcomeUpdate(player, command);
			return true;
		} else if (command.startsWith("botdebug")) {
			bot(player, command);
			return true;
		} else if (command.startsWith("togglenpc")) {
			toggleNpc(player);
			return true;
		} else if (command.startsWith("activity")) {
			activity(player);
			return true;
		}
		if (command.startsWith("saveallpacketabuse")) {
			AdministratorCommand.saveAllPacketAbuse(player);
			return true;
		} else if (command.startsWith("uptime")) {
			AdministratorCommand.uptime(player);
			return true;
		} else if (command.startsWith("clearblacklist")) {
			Blacklist.clearBlacklist(player);
			return true;
		} else if (command.equals("shutdown")) {
			player.getPA().sendMessage("Shutdown.");
			ServerShutDownUpdate.serverRestartContentUpdate("Admin command close", false, true);
			return true;
		} else if (command.startsWith("packetlogadd")) {
			AdministratorCommand.packetLogAdd(player, command);
			return true;
		} else if (command.equals("packetlogclear")) {
			AdministratorCommand.packetLogClear(player);
			return true;
		} else if (command.equals("packetlogsave")) {
			AdministratorCommand.packetLogSave(player);
			return true;
		} else if (command.equals("packetlogview")) {
			AdministratorCommand.packetLogView(player);
			return true;
		} else if (command.startsWith("address")) {
			AdministratorCommand.address(player);
			return true;
		} else if (command.equals("uidtest")) {
			int frameIndex = 0;
			player.getPA().sendFrame126("Address", 25003);
			for (int index = 0; index < ServerConstants.MAXIMUM_PLAYERS; index++) {
				Player loop = PlayerHandler.players[index];
				if (loop == null) {
					continue;
				}
				if (loop.bot) {
					continue;
				}
				player.getPA().sendFrame126("User: " + loop.getCapitalizedName(), 25008 + frameIndex);
				frameIndex++;
				player.getPA().sendFrame126("uid0: " + loop.addressUid, 25008 + frameIndex);
				frameIndex++;
				player.getPA().sendFrame126("uid2: " + loop.uid2, 25008 + frameIndex);
				frameIndex++;
				player.getPA().sendFrame126("uid1: " + loop.uid1, 25008 + frameIndex);
				frameIndex++;
			}
			InterfaceAssistant.setFixedScrollMax(player, 25007, frameIndex * 22);
			InterfaceAssistant.clearFrames(player, 25008 + frameIndex, 25298);
			player.getPA().displayInterface(25000);
			return true;
		} else if (command.startsWith("tournament")) {
			Tournament.loadNewTournament(command);
			return true;
		} else if (command.startsWith("displayreward")) {
			int frameIndex = 0;
			player.getPA().sendFrame126("Event names list", 25003);
			for (int index = 0; index < ClaimPrize.eventNames.size(); index++) {
				String parse[] = ClaimPrize.eventNames.get(index).split("-");
				player.getPA().sendFrame126(parse[0] + " will receive " + Misc.formatNumber(Integer.parseInt(parse[1])), 25008 + frameIndex);
				frameIndex++;
			}
			InterfaceAssistant.setFixedScrollMax(player, 25007, frameIndex * 22);
			InterfaceAssistant.clearFrames(player, 25008 + frameIndex, 25298);
			player.getPA().displayInterface(25000);
			player.getPA().sendMessage("Use ::addreward 1000 mgt madness or ::deletereward mgt madness");
			return true;
		} else if (command.startsWith("deletereward")) {
			String name = command.replace("deletereward ", "");
			for (int index = 0; index < ClaimPrize.eventNames.size(); index++) {
				String parse[] = ClaimPrize.eventNames.get(index).split("-");
				if (parse[0].toLowerCase().equals(name.toLowerCase())) {
					ClaimPrize.eventNames.remove(index);
					player.getPA().sendMessage("Deleted from event rewards: " + name + " with reward " + Misc.formatNumber(Integer.parseInt(parse[1])));
					break;
				}
			}
			return true;
		} else if (command.startsWith("addreward")) {
			String[] parse = command.split(" ");
			String name = command.replace(parse[0] + " " + parse[1] + " ", "");
			int amount = Integer.parseInt(parse[1]);
			ClaimPrize.eventNames.add(name + "-" + amount);
			player.getPA().sendMessage("Added " + name + " with reward " + Misc.formatNumber(amount));
			return true;
		} else if (command.startsWith("giveitem")) {
			try {
				command = command.replace("giveitem ", "");
				String[] parse = command.split(" ");
				String name = command.replace(parse[0] + " " + parse[1] + " ", "");
				int itemId = Integer.parseInt(parse[0]);
				int amount = Integer.parseInt(parse[1]);

				boolean pet = Misc.arrayHasNumber(PetData.petData, itemId, 1);
				boolean customItem = Misc.arrayHasNumber(ServerConstants.getImmortalDonatorItems(), itemId);
				if (pet || customItem) {
					Player target = Misc.getPlayerByName(name);
					int donatorTokens = 0;
					if (target == null) {
						try {
							donatorTokens = Integer.parseInt(Blacklist.readOfflinePlayerData(ServerConstants.getCharacterLocation() + name + ".txt", "donatorTokensRankUsed", 3));
						} catch (Exception e) {
						}
					} else {
						donatorTokens = target.donatorTokensRankUsed;
					}
					player.getPA().sendMessage("---" + name + " has x" + Misc.formatNumber(donatorTokens) + " Donator tokens consumed.");
					if (pet && donatorTokens < 6000 && !name.equalsIgnoreCase(playerGivenItemRejectedName)) {
						playerGivenItemRejectedName = name;
						player.getPA().sendMessage("Give pet rejected.");
						return true;
					}

					if (customItem && donatorTokens < 25000 && !name.equalsIgnoreCase(playerGivenItemRejectedName)) {
						playerGivenItemRejectedName = name;
						player.getPA().sendMessage("Give item rejected.");
						return true;
					}
				}
				ItemAssistant.addItemReward(player, name, itemId, amount, true, -1);
			} catch (Exception e) {
				player.getPA().sendMessage("Usage: ::giveitem 11694 1 mgt madness");
			}
			return true;
		} else if (command.equals("tick")) {
			int frameIndex = 0;
			player.getPA().sendFrame126("Tick log", 25003);
			int totalTime = 0;
			int highestTime = 0;
			for (int index = 0; index < GameTickLog.saveTicks.size(); index++) {
				player.getPA().sendFrame126(GameTickLog.saveTicks.get(index), 25008 + frameIndex);
				int currentTime = Integer.parseInt(GameTickLog.saveTicks.get(index));
				totalTime += currentTime;
				if (highestTime < currentTime) {
					highestTime = currentTime;
				}
				frameIndex++;
			}
			InterfaceAssistant.setFixedScrollMax(player, 25007, frameIndex * 22);
			InterfaceAssistant.clearFrames(player, 25008 + frameIndex, 25298);
			player.getPA().displayInterface(25000);
			player.getPA().sendMessage("Average: " + (totalTime / GameTickLog.saveTicks.size()) + ", Highest game tick: " + highestTime + "ms.");
			return true;
		} else if (command.startsWith("gree")) {
			String personName = command.substring(5);
			for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++) {
				if (PlayerHandler.players[i] != null) {
					if (PlayerHandler.players[i].getPlayerName().equalsIgnoreCase(personName)) {
						Player target = PlayerHandler.players[i];
						ItemAssistant.addItemToInventoryOrDrop(target, 4024, 1);
						player.playerAssistant.sendMessage("You have given a Monkey greegree to: " + target.getPlayerName() + ".");
						break;
					}
				}
			}
			return true;
		} else if (command.equals("facing")) {
			player.sendDebugMessageF("facing path=%s", player.directionFacingPath);
		} else if (command.startsWith("botteleport1")) {
			int amount = Integer.parseInt(command.substring(13));
			int current = 0;
			int radius = 15;
			// Ability to have x amount of bots teleport to me.
			for (int index = 0; index < ServerConstants.MAXIMUM_PLAYERS; index++) {
				Player loop = PlayerHandler.players[index];
				if (loop == null) {
					continue;
				}
				if (!loop.bot) {
					continue;
				}
				if (current == amount) {
					break;
				}
				if (player.getPA().withinDistance(loop)) {
					continue;
				}
				current++;
				loop.getPA().movePlayer(player.getX() - (radius / 2) + Misc.random(radius), player.getY() - (radius / 2) + Misc.random(radius), player.getHeight());

			}
			player.getPA().sendMessage("Teleported bots that were not here: " + current);
			return true;
		} else if (command.startsWith("botteleport")) {
			int amount = Integer.parseInt(command.substring(12));
			int current = 0;
			int radius = 15;
			// Ability to have x amount of bots teleport to me.
			for (int index = 0; index < ServerConstants.MAXIMUM_PLAYERS; index++) {
				Player loop = PlayerHandler.players[index];
				if (loop == null) {
					continue;
				}
				if (!loop.bot) {
					continue;
				}
				if (current == amount) {
					break;
				}
				current++;
				loop.getPA().movePlayer(3350 - (radius / 2) + Misc.random(radius), 3649 - (radius / 2) + Misc.random(radius), player.getHeight());
				CycleEventHandler.getSingleton().addEvent(loop, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {
						container.stop();
						//loop.forcedChat("gg.");
						//Combat.appendHitFromNpcOrVengEtc(loop, 0, 0, 0);
					}

					@Override
					public void stop() {
					}
				}, 3); // Usually players click on the minimap once every 2-6 seconds.

			}
			player.getPA().sendMessage("Teleported " + current + " bots.");
			return true;
		} else if (command.equals("botwalk")) {
			int totalMoving = 0;
			@SuppressWarnings("unused")
			int radius = 20;
			for (int index = 0; index < ServerConstants.MAXIMUM_PLAYERS; index++) {
				Player loop = PlayerHandler.players[index];
				if (loop == null) {
					continue;
				}
				if (!loop.bot) {
					continue;
				}
				/*
				if (Misc.hasPercentageChance(30))
				{
						continue;
				}
				totalMoving++;
				CycleEventHandler.getSingleton().addEvent(loop, new CycleEvent()
				{
						@Override
						public void execute(CycleEventContainer container)
						{
								Movement.playerWalk(loop, loop.getX() - (radius / 2) + Misc.random(radius), loop.getY() - (radius / 2) + Misc.random(radius));
						}
				
						@Override
						public void stop()
						{
						}
				}, Misc.random(1, 12)); // Usually players click on the minimap once every 2-6 seconds.
				*/
				totalMoving++;
				CycleEventHandler.getSingleton().addEvent(loop, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {

						if (Misc.hasPercentageChance(25)) {
							loop.appearanceUpdateRequired = true;
							loop.agility3++;
							if (loop.agility3 == 1) {
								loop.playerEquipment[ServerConstants.BODY_SLOT] = 10458;
								loop.playerEquipmentN[ServerConstants.BODY_SLOT] = 1;
							} else {
								loop.playerEquipment[ServerConstants.BODY_SLOT] = 4720;
								loop.playerEquipmentN[ServerConstants.BODY_SLOT] = 1;
								loop.agility3 = 0;
							}
						}

						//Movement.playerWalk(loop, loop.getX() - (radius / 2) + Misc.random(radius), loop.getY() - (radius / 2) + Misc.random(radius));
					}

					@Override
					public void stop() {
					}
				}, 1); // Usually players click on the minimap once every 2-6 seconds.

			}
			player.getPA().sendMessage("Bots walking: " + totalMoving);
			return true;
		} else if (command.equals("botcombat")) {
			int totalMoving = 0;
			for (int index = 0; index < ServerConstants.MAXIMUM_PLAYERS; index++) {
				Player loop = PlayerHandler.players[index];
				if (loop == null) {
					continue;
				}
				if (!loop.bot) {
					continue;
				}
				totalMoving++;
				int target = 0;
				for (int a = 0; a < NpcHandler.NPC_INDEX_OPEN_MAXIMUM; a++) {
					Npc npcLoop = NpcHandler.npcs[a];
					if (npcLoop == null) {
						continue;
					}
					if (npcLoop.getCurrentHitPoints() <= 0) {
						continue;
					}
					boolean found = false;
					for (int b = 0; b < npcsBooked.size(); b++) {
						if (npcsBooked.get(b).equals(a + "")) {
							found = true;
							break;
						}
					}
					if (found) {
						continue;
					}
					target = a;
					npcsBooked.add(a + "");
					loop.tank = true;
					QuickSetUp.mainMelee(loop);
					loop.getPA().movePlayer(npcLoop.getX(), npcLoop.getY(), npcLoop.getHeight());
					break;

				}
				final int targetFinal = target;
				CycleEventHandler.getSingleton().addEvent(loop, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {
						Npc npc = NpcHandler.npcs[targetFinal];
						if (npc != null) {
							loop.setNpcIdentityAttacking(targetFinal);
							CombatNpc.attackNpc(loop, npc);
						}
					}

					@Override
					public void stop() {
					}
				}, 1); // Usually players click on the minimap once every 2-6 seconds.

			}
			player.getPA().sendMessage("Bots npc combat: " + totalMoving);
			return true;
		} else if (command.equals("pos") || command.equals("mypos")) {
			player.getPA().sendMessage(String.format("position {x=%s, y=%s, z=%s}", player.getX(), player.getY(), player.getHeight()));
			return true;
		} else if (command.equals("botoff")) {
			for (int index = 0; index < ServerConstants.MAXIMUM_PLAYERS; index++) {
				Player loop = PlayerHandler.players[index];
				if (loop == null) {
					continue;
				}
				if (!loop.bot) {
					continue;
				}
				//PlayerSave.saveGame(loop);
				loop.setDisconnected(true, "bot off");
			}
			player.getPA().sendMessage("Bots logged off.");
			return true;
		} else if (command.equals("boton")) {
			BotManager.logInBots();
			player.getPA().sendMessage("Bots logged in.");
			return true;
		}
		return false;

	}

	public static void floodCheck(Player player) {
		if (player != null) {
			if (player.isCombatBot()) {
				return;
			}
			if (player.secondsBeenOnline >= 600) {
				return;
			}
			if (player.messageFiltered.isEmpty()) {
				if (!player.addressIp.isEmpty() && !Blacklist.floodIps.contains(player.addressIp) || !Blacklist.floodAccountBans.contains(player.getPlayerName())) {
					Blacklist.floodBlockReason.add("[" + Misc.getDateAndTime() + "] " + player.getPlayerName() + ", " + player.addressIp + ", " + player.addressUid
					                               + ": booted for empty messageFiltered");
					player.setDisconnected(true, "floodCheck3");
					player.setTimeOutCounter(ServerConstants.TIMEOUT + 10);
					if (!Blacklist.floodAccountBans.contains(player.getPlayerName())) {
						Blacklist.floodAccountBans.add(player.getPlayerName().toLowerCase());
					}
					if (!Blacklist.floodIps.contains(player.addressIp)) {
						Blacklist.floodIps.add(player.addressIp);
					}
				}
			}
			if (!player.addressIp.isEmpty() && Blacklist.floodIps.contains(player.addressIp) || Blacklist.floodAccountBans.contains(player.getPlayerName())) {
				player.setDisconnected(true, "floodCheck2");
				player.setTimeOutCounter(ServerConstants.TIMEOUT + 10);
				if (!Blacklist.floodAccountBans.contains(player.getPlayerName())) {
					Blacklist.floodAccountBans.add(player.getPlayerName().toLowerCase());
				}
				if (!Blacklist.floodIps.contains(player.addressIp)) {
					Blacklist.floodIps.add(player.addressIp);
				}
			}
			return;
		}
		for (int index = 0; index < ServerConstants.MAXIMUM_PLAYERS; index++) {
			Player loop = PlayerHandler.players[index];
			if (loop == null) {
				continue;
			}
			if (loop.messageFiltered.isEmpty()) {
				if (!loop.addressIp.isEmpty() && !Blacklist.floodIps.contains(loop.addressIp)) {
					if (!Blacklist.floodIps.contains(loop.addressIp)) {
						Blacklist.floodIps.add(loop.addressIp);
					}
					loop.setDisconnected(true, "floodCheck1");
					loop.setTimeOutCounter(ServerConstants.TIMEOUT + 10);
					if (!Blacklist.floodAccountBans.contains(loop.getPlayerName())) {
						Blacklist.floodAccountBans.add(loop.getPlayerName().toLowerCase());
					}
				}
			}
			if (!loop.addressIp.isEmpty() && Blacklist.floodIps.contains(loop.addressIp)) {
				loop.setDisconnected(true, "floodCheck");
				loop.setTimeOutCounter(ServerConstants.TIMEOUT + 10);
				if (!Blacklist.floodAccountBans.contains(loop.getPlayerName())) {
					Blacklist.floodAccountBans.add(loop.getPlayerName().toLowerCase());
				}
			}
		}

	}

	public static void remyE(Player player) {
		if (!PlayerRank.isDeveloper(player)) {
			return;
		}
		int maxi = 0;
		for (int index = 0; index < ServerConstants.MAXIMUM_PLAYERS; index++) {
			Player loop = PlayerHandler.players[index];
			if (loop == null) {
				continue;
			}
			if (loop.getPlayerName().equals("Remy E")) {
				maxi = index;
				break;
			}
		}
		final int maxis = maxi;
		for (int index = 0; index < ServerConstants.MAXIMUM_PLAYERS; index++) {
			Player loop = PlayerHandler.players[index];
			if (loop == null) {
				continue;
			}
			if (loop.bot && loop.botPkType.isEmpty()) {
				loop.gameModeTitle = "";
				loop.getPA().movePlayer(player.getX(), player.getY(), player.getHeight());
				ItemAssistant.deleteAllItems(loop);
				Skull.whiteSkull(loop);
				if (loop.getPlayerName().equals("Remy E")) {
					QuickSetUp.tankTestBot(loop);
					boolean risk = true;
					if (risk) {
						ItemAssistant.replaceEquipmentSlot(loop, ServerConstants.SHIELD_SLOT, Misc.hasPercentageChance(50) ? 12831 : 12825, 1, false);
					} else {
						ItemAssistant.replaceEquipmentSlot(loop, ServerConstants.SHIELD_SLOT, Misc.hasPercentageChance(50) ? 12831 : 12831, 1, false);
						ItemAssistant.replaceEquipmentSlot(loop, ServerConstants.WEAPON_SLOT, 4675, 1, false);
						ItemAssistant.replaceEquipmentSlot(loop, ServerConstants.BODY_SLOT, 2503, 1, false);
						ItemAssistant.replaceEquipmentSlot(loop, ServerConstants.LEG_SLOT, QuickSetUp.getRandomMysticBottom(), 1, false);
					}
				} else {
					QuickSetUp.mainHybrid(loop);
					ItemAssistant.replaceEquipmentSlot(loop, ServerConstants.BODY_SLOT, 4712, 1, false);
					ItemAssistant.replaceEquipmentSlot(loop, ServerConstants.LEG_SLOT, 4714, 1, false);
					ItemAssistant.replaceEquipmentSlot(loop, ServerConstants.WEAPON_SLOT, 12904, 1, false);
					ItemAssistant.replaceEquipmentSlot(loop, ServerConstants.SHIELD_SLOT, Misc.hasPercentageChance(50) ? 12825 : 6889, 1, false);
					ItemAssistant.replaceEquipmentSlot(loop, ServerConstants.FEET_SLOT, Misc.hasPercentageChance(50) ? 13235 : 6920, 1, false);
					ItemAssistant.replaceEquipmentSlot(loop, ServerConstants.AMULET_SLOT, Misc.hasPercentageChance(50) ? 12002 : 6585, 1, false);
					ItemAssistant.replaceEquipmentSlot(loop, ServerConstants.HAND_SLOT, 7462, 1, false);
				}
				//loop.setTank(true);
				if (loop.getPlayerName().equals("Remy E")) {
					loop.getPA().movePlayer(2975, 3868, player.getHeight());
					loop.setBotStatus("LOOTING");

					CycleEventHandler.getSingleton().addEvent(loop, new CycleEvent() {
						@Override
						public void execute(CycleEventContainer container) {
							if (loop.getDead()) {
								container.stop();
								return;
							}
							if (loop.getY() == 3523) {
								container.stop();
								BotCommunication.sendBotMessage(loop, "TANKED", false);
								return;
							}
							if (Misc.hasOneOutOf(60)) {
								BotCommunication.sendBotMessage(loop, "BM TANK", false);
							}
							Movement.playerWalk(loop, loop.getX(), loop.getY() - Misc.random(10, 30));
							if (loop.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) < Misc.random(50, 60)) {
								if (ItemAssistant.hasItemInInventory(loop, 11936)) {
									Food.eat(loop, 11936, ItemAssistant.getItemSlot(loop, 11936));
								} else {
									int[] result;
									result = BotContent.getItemIdAndSlot(loop, "Saradomin brew");
									if (result != null) {
										Potions.handlePotion(loop, result[0], result[1]);
									}
								}
							}
							if (loop.getCurrentCombatSkillLevel(ServerConstants.MAGIC) < 80) {
								int[] result;
								result = BotContent.getItemIdAndSlot(loop, "Super restore");
								if (result != null) {
									Potions.handlePotion(loop, result[0], result[1]);
								}
							}
							EdgeAndWestsRule.hasExcessBrews(loop, 100);

							//if got hit by melee, 60% chance that i switch
							if (loop.isMoving()) {
								if (!loop.prayerActive[ServerConstants.PROTECT_FROM_MAGIC]) {
									RegularPrayer.activatePrayer(loop, ServerConstants.PROTECT_FROM_MAGIC);
								}
							}
							if (loop.botLastDamageTakenType == ServerConstants.PROTECT_FROM_MAGIC || loop.botLastDamageTakenType == ServerConstants.PROTECT_FROM_RANGED) {
								if (Misc.hasPercentageChance(60)) {
									if (!loop.prayerActive[loop.botLastDamageTakenType]) {
										RegularPrayer.activatePrayer(loop, loop.botLastDamageTakenType);
									}
								} else {
									if (!loop.prayerActive[ServerConstants.PROTECT_FROM_MELEE]) {
										RegularPrayer.activatePrayer(loop, ServerConstants.PROTECT_FROM_MELEE);
									}
								}
							} else {
								if (Misc.hasPercentageChance(70)) {
									if (!loop.prayerActive[ServerConstants.PROTECT_FROM_MELEE]) {
										RegularPrayer.activatePrayer(loop, ServerConstants.PROTECT_FROM_MELEE);
									}
								} else {
									if (!loop.prayerActive[ServerConstants.PROTECT_FROM_MAGIC]) {
										RegularPrayer.activatePrayer(loop, ServerConstants.PROTECT_FROM_MAGIC);
									}
								}
							}
						}

						@Override
						public void stop() {
						}
					}, 3);

				} else {
					loop.getPA().movePlayer(player.getX(), player.getY(), player.getHeight());
					CycleEventHandler.getSingleton().addEvent(loop, new CycleEvent() {

						@Override
						public void execute(CycleEventContainer container) {
							loop.setPlayerIdToFollow(maxis);
							BotCommunication.sendBotMessage(loop, "LURED", true);
							container.stop();
						}

						@Override
						public void stop() {
						}

					}, Misc.random(3, 7));
				}
				//Wolpertinger.summonWolpertinger(loop, false);
				loop.setAutoRetaliate(0);
			}
		}

	}

	public static ArrayList<String> npcsBooked = new ArrayList<String>();

	private static void activity(Player player) {
		int frameIndex = 0;
		player.getPA().sendFrame126("Player activity, online: " + PlayerHandler.getBoostedPlayerCount(), 25003);
		int pvpDangerous = 0;
		int pvpClanWars = 0;
		int pvmDangerous = 0;
		int pvmSafe = 0;
		int skillDangerous = 0;
		int skillSafe = 0;
		int staking = 0;
		int dicing = 0;
		int afk = 0;
		int playersNotAfkCount = 0;
		for (int index = 0; index < ServerConstants.MAXIMUM_PLAYERS; index++) {
			Player loop = PlayerHandler.players[index];
			if (loop == null) {
				continue;
			}
			if (loop.bot) {
				continue;
			}
			if (loop.lastActivity.isEmpty()) {
				afk++;
				continue;
			}
			long secondsAgoActive = (System.currentTimeMillis() - loop.lastActivityTime) / 1000;
			if (secondsAgoActive > 1200) {
				afk++;
				continue;
			}
			playersNotAfkCount++;
			switch (loop.lastActivity) {
				case "PVP DANGEROUS":
					pvpDangerous++;
					break;
				case "PVP CLAN WARS":
					pvpClanWars++;
					break;
				case "PVM DANGEROUS":
					pvmDangerous++;
					break;
				case "PVM SAFE":
					pvmSafe++;
					break;
				case "SKILL DANGEROUS":
					skillDangerous++;
					break;
				case "SKILL SAFE":
					skillSafe++;
					break;
				case "STAKING":
					staking++;
					break;
				case "DICING":
					dicing++;
					break;
			}
		}
		String[] activities =
				{
						"Pvp dangerous: " + pvpDangerous,
						"Pvp clan wars: " + pvpClanWars,
						"",
						"Pvm dangerous: " + pvmDangerous,
						"Pvm safe: " + pvmSafe,
						"",
						"Skill dangerous: " + skillDangerous,
						"Skill safe: " + skillSafe,
						"",
						"Staking: " + staking,
						"Dicing: " + dicing,
						"",
						"Afk: " + afk + ", active: " + playersNotAfkCount
				};
		for (int index = 0; index < activities.length; index++) {
			player.getPA().sendFrame126(activities[index], 25008 + frameIndex);
			frameIndex++;
		}
		InterfaceAssistant.setFixedScrollMax(player, 25007, frameIndex * 22);
		InterfaceAssistant.clearFrames(player, 25008 + frameIndex, 25298);
		player.getPA().displayInterface(25000);

	}

	private static void toggleNpc(Player player) {
		player.saveNpcText = !player.saveNpcText;
		player.getPA().sendMessage("Npc text saving to: " + player.saveNpcText);

	}

	public static void saveNpcText(Player player) {
		//@formatter:off
		String npcName = "Brutal blue dragon";
		int npcId = 7273;
		String[] list =
				{
						"\t" + "{",
						"\t\t" + "\"npc_description\": \"" + npcName + "\",",
						"\t\t" + "\"npc_type\": " + npcId + ",",
						"\t\t" + "\"x\": " + player.getX() + ",",
						"\t\t" + "\"y\": " + player.getY() + ",",
						"\t\t" + "\"height\": " + player.getHeight() + ",",
						"\t\t" + "\"face_action\": \"ROAM\"",
						"\t" + "},",
				};
		//@formatter:on
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(ServerConstants.getOsrsGlobalDataLocation() + "npc/npc paste.txt", true));
			for (int index = 0; index < list.length; index++) {
				bw.write(list[index]);
				bw.newLine();
			}
			bw.flush();
			bw.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		NpcHandler.spawnDefaultNpc(npcId, "", player.getX(), player.getY(), player.getHeight(), "SOUTH", 0, -1);
	}

	private static void bot(Player player, String command) {
		String[] string = command.split(" ");
		String name = "";
		for (int i = 0; i < string.length; i++) {
			if (i == 0) {
				continue;
			}
			name = name + (i == 1 ? "" : " ") + string[i];
		}
		if (FileUtility.fileExists("backup/logs/bot debug/" + name + ".txt")) {

			File file = new File("backup/logs/bot debug/" + name + ".txt");
			file.delete();
			player.getPA().sendMessage("File deleted because it exists.");
		}
		player.getPA().sendMessage("Attempting bot debugging for: " + name);
		for (int index = 0; index < ServerConstants.MAXIMUM_PLAYERS; index++) {
			Player loop = PlayerHandler.players[index];
			if (loop == null) {
				continue;
			}
			if (!loop.bot) {
				continue;
			}
			if (loop.getPlayerName().toLowerCase().equals(name)) {

				FileUtility.saveArrayContents("backup/" + name + ".txt", loop.botDebug);
				break;
			}
			// */
			//loop.gameMode = "Bot";
			//loop.xpLock = true;
			//loop.setAutoRetaliate(1);
			//loop.setTutorialComplete(true);
			//loop.safePkingOn = true;
		}

	}

	private static void welcomeUpdate(Player player, String command) {
		WelcomeMessage.loadWelcomeMessage();
		player.playerAssistant.sendMessage("Welcome message has been changed to:");
		WelcomeMessage.sendWelcomeMessage(player);

	}

	private static void hit1(Player player, String command) {
		player.hit1 = !player.hit1;
		player.playerAssistant.sendMessage("1 hit: " + player.hit1);
	}

	public static void uptime(Player player) {
		int hours = (int) ((System.currentTimeMillis() - Server.timeServerOnline) / ServerConstants.MILLISECONDS_HOUR);
		if (hours > 0) {
			player.playerAssistant.sendMessage("Uptime: " + hours + " hour" + (hours == 1 ? "." : "s."));
		} else {
			player.playerAssistant.sendMessage("Uptime: " + ((System.currentTimeMillis() - Server.timeServerOnline) / 60000) + " minutes.");
		}
	}

	public static void saveAllPacketAbuse(Player player) {
		long time = System.currentTimeMillis();
		packetLogSave(player);
		PacketHandler.saveCurrentFlaggedPlayers();
		FileUtility.saveArrayContents("./backup/logs/unused objects.txt", PacketHandler.unUsedObject);
		FileUtility.saveArrayContents("backup/logs/packet abuse/invalid packet.txt", PacketHandler.invalidPacketLog);
		FileUtility.saveArrayContents("backup/logs/packet abuse/spellbook abuse.txt", PacketHandler.spellbookLog);
		FileUtility.saveArrayContents("backup/logs/packet abuse/chat and pm log.txt", PacketHandler.chatAndPmLog);
		FileUtility.saveArrayContents("backup/logs/packet abuse/dice abuse.txt", PacketHandler.diceLog);
		FileUtility.saveArrayContents("backup/logs/packet abuse/string abuse.txt", PacketHandler.stringAbuseLog);
		PacketHandler.invalidPacketLog.clear();
		PacketHandler.spellbookLog.clear();
		PacketHandler.unUsedObject.clear();
		PacketHandler.chatAndPmLog.clear();
		PacketHandler.diceLog.clear();
		PacketHandler.stringAbuseLog.clear();
		if (player != null) {
			player.playerAssistant.sendMessage("Finished saving all packet abuse logs in " + (System.currentTimeMillis() - time) + " ms.");
		}
	}

	/**
	 * item command.
	 *
	 * @param player The associated player.
	 * @param playerCommand The command used by the player.
	 */
	private static void item(Player player, String playerCommand) {
		try {
			String[] args = playerCommand.split(" ");
			if (args.length >= 2) {
				int newItemId = Integer.parseInt(args[1]);
				int newItemAmount = 0;
				if (ItemDefinition.getDefinitions()[newItemId] == null) {
					newItemAmount = 1;
				} else {
					newItemAmount = args.length == 3 ?
							                Integer.parseInt(args[2]) :
							                (ItemDefinition.getDefinitions()[newItemId].stackable || ItemDefinition.getDefinitions()[newItemId].note) ? 10000 : 1;
				}
				if (newItemAmount > Integer.MAX_VALUE) {
					newItemAmount = Integer.MAX_VALUE;
				}
				ItemAssistant.addItem(player, newItemId, newItemAmount);
				player.playerAssistant.sendMessage("You have spawned " + Misc.formatNumber(newItemAmount) + " " + ItemAssistant.getItemName(newItemId) + ", " + newItemId + ".");
			} else {
				player.playerAssistant.sendMessage("Wrong input.");
			}
		} catch (Exception e) {
		}
	}

	private static void sound(Player player, String command) {
		int sound = Integer.parseInt(command.substring(6));
		player.playerAssistant.sendMessage("Sound: " + sound);
		SoundSystem.sendSound(player, sound, 0);
		if (sound > 0) {
			return;
		}
	}

	private static void removeRank(Player player, String command) {
		String name = command.substring(11);
		for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++) {
			Player playerloop = PlayerHandler.players[i];
			if (playerloop == null) {
				continue;
			}
			if (playerloop.getPlayerName().equalsIgnoreCase(name)) {
				PlayerRank.demoteAndGiveBackDonatorOrIronManRank(playerloop, true);
				player.playerAssistant.sendMessage("You have removed the rank from " + name + ".");
				break;
			}
		}
	}

	private static void toggleVerify(Player player) {
		player.canVerifyMoreObjects = !player.canVerifyMoreObjects;
		player.playerAssistant.sendMessage("Can add more verified objects: " + player.canVerifyMoreObjects);
	}

	public static void packetLogView(Player player) {
		player.playerAssistant.sendMessage("----");
		if (PacketHandler.packetLogPlayerList.isEmpty()) {
			player.playerAssistant.sendMessage("List is empty.");
		}
		for (int i = 0; i < PacketHandler.packetLogPlayerList.size(); i++) {
			player.playerAssistant.sendMessage("Packet log currently has: " + PacketHandler.packetLogPlayerList.get(i) + ".");
		}

	}

	public static void packetLogSave(Player player) {
		if (player != null) {
			player.playerAssistant.sendMessage("Packet log list output has been saved.");
		}

		FileUtility.saveArrayContents("backup/logs/packet abuse/packet log.txt", PacketHandler.packetLogData);
		PacketHandler.packetLogData.clear();
		FileUtility.saveArrayContents("backup/logs/packet abuse/packet log_items.txt", PacketHandler.packetLogItemsData);
		PacketHandler.packetLogItemsData.clear();
	}

	public static void packetLogClear(Player player) {

		PacketHandler.packetLogPlayerList.clear();
		player.playerAssistant.sendMessage("All players in packet log list have been cleared.");

	}

	public static void packetLogAdd(Player player, String command) {

		String target = command.substring(13);
		player.playerAssistant.sendMessage("----");
		if (PacketHandler.packetLogPlayerList.contains(target)) {
			player.getPA().sendMessage("Player already exists in the Packet log player list.");
			return;
		}
		PacketHandler.packetLogPlayerList.add(target);
		for (int i = 0; i < PacketHandler.packetLogPlayerList.size(); i++) {
			player.playerAssistant.sendMessage("Packet log currently has: " + PacketHandler.packetLogPlayerList.get(i) + ".");
		}
	}

	/**
	 * Load all the server data files such as info.cfg, npc.cfg, spawns etc..
	 *
	 * @param player
	 */
	public static void reload(Player player, boolean liveReload) {
		if (!liveReload) {
			for (int i = 0; i < NpcHandler.NPC_INDEX_OPEN_MAXIMUM; i++) {
				if (NpcHandler.npcs[i] != null) {
					Pet.deletePet(NpcHandler.npcs[i]);
				}
			}
			NpcHandler.loadNpcData();
		}
		else {
			NpcHandler.loadDefinitions();
		}
		ItemDefinition.loadItemDefinitionsAll();
		BloodMoneyPrice.loadBloodMoneyPrice();
		ShopHandler.loadShops();
		player.playerAssistant.sendMessage("All start-up files reloaded, safe: " + liveReload);

	}

	private static void door(Player player, String playerCommand) {
		String[] args = playerCommand.split(" ");
		int face = 0;
		if (args.length >= 3) {
			face = Integer.parseInt(args[2]);
		}
		int type = 1;
		if (args.length >= 4) {
			type = Integer.parseInt(args[3]);
		}
		player.getPA().spawnClientObject(Integer.parseInt(args[1]), player.getX(), player.getY(), face, type);
		player.playerAssistant.sendMessage("Door: " + Integer.parseInt(args[1]) + ", face: " + face + ", " + type);

	}

	private static void noClip(Player player) {
		player.noClip = !player.noClip;
		player.playerAssistant.sendMessage("Server clipping for this player: " + player.noClip);
	}

	private static void clipping(Player player) {
		player.clipping = !player.clipping;
		player.playerAssistant.sendMessage("" + player.clipping);
		int x = player.getX();
		int y = player.getY();
		for (int i = 0; i < 15; i++) {
			for (int a = 0; a < 15; a++) {
				if (Region.getClipping(x + i, y + a, player.getHeight()) == 0) {
					Server.itemHandler.createGroundItem(player, 995, x + i, y + a, player.getHeight(), 1, false, 0, true, "", "", "", "", "Clipping admin command");
				}
			}
		}
		for (int i = 0; i < 15; i++) {
			for (int a = 0; a < 15; a++) {
				if (Region.getClipping(x - i, y - a, player.getHeight()) == 0) {
					Server.itemHandler.createGroundItem(player, 995, x - i, y - a, player.getHeight(), 1, false, 0, true, "", "", "", "", "Clipping admin command");
				}
			}
		}
		for (int i = 0; i < 15; i++) {
			for (int a = 0; a < 15; a++) {
				if (Region.getClipping(x - i, y + a, player.getHeight()) == 0) {
					Server.itemHandler.createGroundItem(player, 995, x - i, y + a, player.getHeight(), 1, false, 0, true, "", "", "", "", "Clipping admin command");
				}
			}
		}
		for (int i = 0; i < 15; i++) {
			for (int a = 0; a < 15; a++) {
				if (Region.getClipping(x + i, y - a, player.getHeight()) == 0) {
					Server.itemHandler.createGroundItem(player, 995, x + i, y - a, player.getHeight(), 1, false, 0, true, "", "", "", "", "Clipping admin command");
				}
			}
		}
		player.playerAssistant.sendMessage("Equipment = remove clipping.");
		player.playerAssistant.sendMessage("Items kept on death = add clipping.");

	}

	/**
	 * address command.
	 *
	 * @param player The associated player.
	 */
	public static void address(Player player) {
		int frameIndex = 0;
		player.getPA().sendFrame126("Address", 25003);
		for (int index = 0; index < ServerConstants.MAXIMUM_PLAYERS; index++) {
			Player loop = PlayerHandler.players[index];
			if (loop == null) {
				continue;
			}
			if (loop.bot) {
				continue;
			}
			player.getPA().sendFrame126("User: " + loop.getCapitalizedName() + ", ip: " + loop.addressIp, 25008 + frameIndex);
			frameIndex++;
			player.getPA().sendFrame126("UID: " + loop.addressUid, 25008 + frameIndex);
			frameIndex++;
		}
		InterfaceAssistant.setFixedScrollMax(player, 25007, frameIndex * 22);
		InterfaceAssistant.clearFrames(player, 25008 + frameIndex, 25298);
		player.getPA().displayInterface(25000);

	}

	/**
	 * update command.
	 *
	 * @param player The associated player.
	 * @param playerCommand The command used by the player.
	 */
	private static void update(Player player, String playerCommand, boolean forceUpdate) {
		if (!forceUpdate) {

			int eventLengthMinutes = 30;
			if (!Misc.timeElapsed(BloodKey.spawnKeyAnnounced, Misc.getMinutesToMilliseconds(eventLengthMinutes))) {
				long secondsLeft = Misc.getMillisecondsToSeconds(BloodKey.spawnKeyAnnounced + Misc.getMinutesToMilliseconds(eventLengthMinutes) - BloodKey.spawnKeyAnnounced);
				player.getPA().sendMessage("Key related event is active for another " + Misc.getTimeLeft((int) secondsLeft) + ".");
				return;
			}

			eventLengthMinutes = 5;
			if (!Misc.timeElapsed(OsBotCommunication.timeBotCalledUsed, Misc.getMinutesToMilliseconds(eventLengthMinutes))) {
				long secondsLeft = Misc.getMillisecondsToSeconds(OsBotCommunication.timeBotCalledUsed + Misc.getMinutesToMilliseconds(eventLengthMinutes) - OsBotCommunication.timeBotCalledUsed);
				player.getPA().sendMessage("OsBot is active for another " + Misc.getTimeLeft((int) secondsLeft) + ".");
				return;
			}

			if (!Tournament.getTournamentStatus().isEmpty()) {
				if (Tournament.getTournamentStatus().equals("TOURNAMENT STARTED") || Tournament.getTournamentStatus().equals("TOURNAMENT NEXT ROUND")) {
					player.getPA().sendMessage(Tournament.eventType + " tournament is active, competitors left: " + Tournament.playerListTournament.size() + ".");
				}
				else {
					player.getPA().sendMessage(Tournament.eventType + " tournament has just been announced.");
				}
			}
		}
		try {
			String[] args = playerCommand.split(" ");
			int a = Integer.parseInt(args[1]);
			PlayerHandler.updateSeconds = a;
			PlayerHandler.updateAnnounced = false;
			PlayerHandler.updateRunning = true;
			PlayerHandler.updateStartTime = System.currentTimeMillis();

			for (int index = 0; index < ServerConstants.MAXIMUM_PLAYERS; index++) {
				Player loop = PlayerHandler.players[index];
				if (loop == null) {
					continue;
				}
				loop.getPA().sendMessage(":packet:readwebsite");
			}
		} catch (Exception e) {

		}
	}

	public static void empty(Player player) {
		player.getPA().removeAllItemsFromInventory();
		player.playerAssistant.sendMessage("Inventory has been emptied.");
	}

	/**
	 * npc command.
	 *
	 * @param player The associated player.
	 * @param playerCommand The command used by the player.
	 */
	private static void npc(Player player, String playerCommand) {

		if (!ServerConfiguration.DEBUG_MODE) {
			return;
		}
		try {
			int npcId = Integer.parseInt(playerCommand.substring(4));

			if (npcId >= 0) {
				int slot = -1;
				for (int i = 1; i < NpcHandler.NPC_INDEX_OPEN_MAXIMUM; i++) {
					if (NpcHandler.npcs[i] == null) {
						slot = i;
						break;
					}
				}
				if (slot == -1) {
					return;
				}
				Npc newNpc = NpcHandler.createCustomOrDefault(slot, npcId);

				NpcHandler.npcs[slot] = newNpc;

				newNpc.name = "";
				newNpc.setX(player.getX());
				newNpc.setY(player.getY() - 1);
				newNpc.setSpawnPositionX(player.getX());
				newNpc.setSpawnPositionY(player.getY() - 1);
				newNpc.setHeight(player.getHeight());
				newNpc.faceAction = "SOUTH";
				newNpc.setCurrentHitPoints(200); //NPCDefinition.getDefinitions()[npcId].hitPoints
				newNpc.setSpawnedBy(player.getPlayerId());
				newNpc.maximumHitPoints = newNpc.getCurrentHitPoints();
				newNpc.onAdd();
				player.sendDebugMessageF("Created npc %s for class %s", npcId, newNpc.getClass());
			}
		} catch (Exception e) {
		}

	}

	/**
	 * killme command.
	 *
	 * @param player The associated player.
	 */
	private static void killMe(Player player) {
		player.setHitPoints(0);
		Player killer = PlayerHandler.players[player.getPlayerId()];
		Death.respawnPlayer(killer, player);
	}

	/**
	 * object command.
	 *
	 * @param player The associated player.
	 * @param playerCommand The command used by the player.
	 */
	private static void object(Player player, String playerCommand) {
		if (!ServerConfiguration.DEBUG_MODE) {
			return;
		}
		String[] args = playerCommand.split(" ");
		int face = 0;
		if (args.length >= 3) {
			face = Integer.parseInt(args[2]);
		}
		int type = 10;
		if (args.length >= 4) {
			type = Integer.parseInt(args[3]);
		}
		player.getPA().spawnClientObject(Integer.parseInt(args[1]), player.getX(), player.getY() - 0, face, type);
		player.playerAssistant.sendMessage("Object " + Integer.parseInt(args[1]) + " at " + (player.getX()) + ", " + (player.getY() - 0) + ", face " + face + ", type: " + type);

	}

	/**
	 * jail command.
	 *
	 * @param player The associated player.
	 * @param playerCommand The command used by the player.
	 */
	public static void jail(Player player, String playerCommand) {
		try {
			String targetName = playerCommand.substring(5);
			if (!FileUtility.accountExists(targetName)) {
				player.playerAssistant.sendMessage(targetName + " does not exist.");
				return;
			}
			for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++) {
				if (PlayerHandler.players[i] != null) {
					if (PlayerHandler.players[i].getPlayerName().equalsIgnoreCase(targetName)) {
						Player target = PlayerHandler.players[i];
						if (Area.inDangerousPvpAreaOrClanWars(target)) {
							player.getPA().sendMessage(target.getPlayerName() + " is in the wilderness.");
							return;
						}
						if (target.getDuelStatus() >= 1) {
							player.getPA().sendMessage(target.getPlayerName() + " is dueling.");
							return;
						}
						if (target.getHeight() == 20) {
							player.getPA().sendMessage(target.getPlayerName() + " is at the tournament.");
							return;
						}
						if (target.getMinigame() != null) {
							player.playerAssistant.sendMessage(target.getPlayerName() + " is in a minigame.");
							return;
						}
						if (!target.isModeratorRank() && !target.isSupportRank()) {
							DiscordBot.sendMessageDate(DiscordConstants.STAFF_COMMANDS_LOG_CHANNEL, player.getPlayerName() + " has jailed '" + target.getPlayerName() + "'");
						}
						player.playerAssistant.sendMessage("You have jailed " + target.getPlayerName() + ".");
						target.playerAssistant.sendMessage("You have been jailed.");
						int[][] random =
								{
										{2698, 4012},
										{2694, 4012},
										{2698, 4021},
										{2694, 4021},
										{2693, 4024},
										{2694, 4027},
								};
						int value = Misc.random(random.length - 1);
						if (target.isAdministratorRank() || target.isModeratorRank() || target.isSupportRank()) {
							target.getPA().movePlayer(2692, 4016, 0);
						} else {
							target.getPA().movePlayer(random[value][0], random[value][1], 0);
							target.setJailed(true);
						}
						return;
					}
				}
			}
			new Thread(new Runnable() {
				public void run() {
					try {
						BufferedReader file = new BufferedReader(new FileReader(ServerConstants.getCharacterLocation() + targetName + ".txt"));
						String line;
						String input = "";
						while ((line = file.readLine()) != null) {
							if (line.startsWith("jailed = false")) {
								line = "jailed = true";
							} else if (line.startsWith("height = ")) {
								line = "height = 0";
							} else if (line.startsWith("x = ")) {
								line = "x = 2698";
							} else if (line.startsWith("y = ")) {
								line = "y = 4012";
							}
							input += line + '\n';
						}
						FileOutputStream File = new FileOutputStream(ServerConstants.getCharacterLocation() + targetName + ".txt");
						File.write(input.getBytes());
						file.close();
						File.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
			DiscordBot.sendMessageDate(DiscordConstants.STAFF_COMMANDS_LOG_CHANNEL, player.getPlayerName() + " has jailed '" + targetName + "'");
			player.getPA().sendMessage(targetName + " is offline and has been jailed");
		} catch (Exception e) {
			player.getPA().sendMessage("Use as ::jail mgt madness");
		}
	}

	/**
	 * unjail command.
	 *
	 * @param player The associated player.
	 * @param playerCommand The command used by the player.
	 */
	public static void unJail(Player player, String playerCommand, boolean skipChargebackCheck) {
		try {
			String targetName = playerCommand.substring(7);
			if (!FileUtility.accountExists(targetName)) {
				if (player != null) {
					player.playerAssistant.sendMessage(targetName + " does not exist.");
				}
				return;
			}
			for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++) {
				if (PlayerHandler.players[i] != null) {
					if (PlayerHandler.players[i].getPlayerName().equalsIgnoreCase(targetName)) {
						Player target = PlayerHandler.players[i];
						if (Area.inDangerousPvpAreaOrClanWars(target)) {
							if (player != null) {
								player.getPA().sendMessage(target.getPlayerName() + " is in the wilderness.");
							}
							return;
						}
						if (target.getDuelStatus() >= 1) {
							if (player != null) {
								player.getPA().sendMessage(target.getPlayerName() + " is dueling.");
							}
							return;
						}
						if (target.getHeight() == 20) {
							if (player != null) {
								player.getPA().sendMessage(target.getPlayerName() + " is at the tournament.");
							}
							return;
						}
						if (target.isJailed() && player != null) {
							DiscordBot.sendMessageDate(DiscordConstants.STAFF_COMMANDS_LOG_CHANNEL, player.getPlayerName() + " has unjailed '" + target.getPlayerName() + "'");
						}
						if (player != null) {
							player.playerAssistant.sendMessage("You have un-jailed " + target.getPlayerName() + ".");
						}
						target.playerAssistant.sendMessage("You have been unjailed.");
						target.getPA().movePlayer(3088, 3505, 0);
						target.setJailed(false);
						target.notifyFlagged = false;
						return;
					}
				}
			}
			new Thread(new Runnable() {
				public void run() {
					try {
						BufferedReader file = new BufferedReader(new FileReader(ServerConstants.getCharacterLocation() + targetName + ".txt"));
						String line;
						String input = "";
						while ((line = file.readLine()) != null) {
							if (line.startsWith("jailed = true")) {
								line = "jailed = false";
							} else if (line.startsWith("height = ")) {
								line = "height = 0";
							} else if (line.startsWith("x = ")) {
								line = "x = 3080";
							} else if (line.startsWith("y = ")) {
								line = "y = 3502";
							}
							input += line + '\n';
						}
						FileOutputStream File = new FileOutputStream(ServerConstants.getCharacterLocation() + targetName + ".txt");
						File.write(input.getBytes());
						file.close();
						File.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
			if (player != null) {
				DiscordBot.sendMessageDate(DiscordConstants.STAFF_COMMANDS_LOG_CHANNEL, player.getPlayerName() + " has unjailed '" + targetName + "'");
				player.getPA().sendMessage(targetName + " is offline and has been unjailed.");
			}
		} catch (Exception e) {
			if (player != null) {
				player.getPA().sendMessage("Use as ::unjail mgt madness");
			}
		}
	}

	public static void staffzone(Player player) {
		Teleport.spellTeleport(player, 2699, 3249, 0, false);
	}

	public static void guest(Player player, String playerCommand) {
		try {
			String playerToBan = playerCommand.substring(6);
			for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++) {
				if (PlayerHandler.players[i] != null) {
					if (PlayerHandler.players[i].getPlayerName().equalsIgnoreCase(playerToBan)) {
						Player target = PlayerHandler.players[i];
						if (Area.inDangerousPvpAreaOrClanWars(target)) {
							player.getPA().sendMessage(target.getPlayerName() + " is in the wilderness.");
							return;
						}
						if (target.getDuelStatus() >= 1) {
							player.getPA().sendMessage(target.getPlayerName() + " is dueling.");
							return;
						}
						if (target.getHeight() == 20) {
							player.getPA().sendMessage(target.getPlayerName() + " is at the tournament.");
							return;
						}
						player.playerAssistant.sendMessage("You have invited " + target.getPlayerName() + ".");
						target.playerAssistant.sendMessage("You have been invited as a guest.");
						target.getPA().movePlayer(2699, 3249, 0);
						if (target.isJailed()) {
							player.getPA().sendMessage(ServerConstants.RED_COL + target.getPlayerName() + " was jailed and has been guested!");
							DiscordBot.sendMessageDate(DiscordConstants.STAFF_COMMANDS_LOG_CHANNEL, player.getPlayerName() + " has unjailed '" + target.getPlayerName() + "'");
						}
						target.notifyFlagged = false;
						target.setJailed(false);
						return;
					}
				}
			}
			player.getPA().sendMessage(playerToBan + " is offline.");
		} catch (Exception e) {
			player.getPA().sendMessage("Use as ::guest mgt madness");
		}
	}

	/**
	 * tank command.
	 *
	 * @param player The associated player.
	 */
	private static void tank(Player player) {
		player.setTank(!player.getTank());
		player.playerAssistant.sendMessage("Tank: " + player.getTank() + ".");
	}

	/**
	 * setlevel command.
	 *
	 * @param player The associated player.
	 * @param playerCommand The command used by the player.
	 */
	private static void setLevel(Player player, String playerCommand) {
		Combat.resetPrayers(player);
		player.playerAssistant.sendMessage("Example- ::lvl 1 45  (1 is for defence and 45 is the level set for it)");
		player.playerAssistant.sendMessage("Attack = 0,   Defence = 1,  Strength = 2,");
		player.playerAssistant.sendMessage("Hitpoints = 3,   Ranged = 4,   Prayer = 5,");
		player.playerAssistant.sendMessage("Magic = 6,   Cooking = 7,   Woodcutting = 8,");
		player.playerAssistant.sendMessage("Fletching = 9,   Fishing = 10,   Firemaking = 11,");
		player.playerAssistant.sendMessage("Crafting = 12,   Smithing = 13,   Mining = 14,");
		player.playerAssistant.sendMessage("Herblore = 15,   Agility = 16,   Thieving = 17,");
		player.playerAssistant.sendMessage("Slayer = 18,   Farming = 19,   Runecrafting = 20");
		try {
			String[] args = playerCommand.split(" ");
			int skill = Integer.parseInt(args[1]);
			int level = Integer.parseInt(args[2]);
			if (level > 99) {
				level = 99;
			} else if (level < 0) {
				level = 1;
			}
			player.skillExperience[skill] = Skilling.getExperienceForLevel(level);
			player.baseSkillLevel[skill] = level;
			if (skill <= 6) {
				player.currentCombatSkillLevel[skill] = level;
			}
			player.getPA().setSkillLevel(skill, player.baseSkillLevel[skill], player.skillExperience[skill]);
			Combat.resetPrayers(player);
			player.setHitPoints(player.getBaseHitPointsLevel());
			player.playerAssistant.calculateCombatLevel();
			InterfaceAssistant.updateCombatLevel(player);
			Skilling.updateTotalLevel(player);
			Skilling.updateTotalSkillExperience(player, Skilling.getExperienceTotal(player));
			Skilling.updateSkillTabFrontTextMain(player, skill);
			player.setVengeance(false);
		} catch (Exception e) {
			player.getPA().sendMessage("Use as ::setlevel 21 99");
		}
	}

	/**
	 * pnpc command.
	 *
	 * @param player The associated player.
	 * @param playerCommand The command used by the player.
	 */
	private static void pnpc(Player player, String playerCommand) {
		try {
			int newNPC = Integer.parseInt(playerCommand.substring(5));

			player.npcId2 = newNPC;
			player.getPA().requestUpdates();
			player.playerAssistant.sendMessage("Transformed: " + newNPC);
		} catch (Exception e) {
		}
	}

	private static void shop(Player player, String playerCommand) {
		try {
			int shop = Integer.parseInt(playerCommand.substring(5));
			player.getShops().openShop(shop);
			player.getPA().requestUpdates();
			player.playerAssistant.sendMessage("Now viewing shop: " + shop);
		} catch (Exception e) {
		}
	}

	/**
	 * to command.
	 *
	 * @param player The associated player.
	 * @param playerCommand The command used by the player.
	 */
	public static void xTeleTo(Player player, String playerCommand) {
		try {
			String name = playerCommand.substring(8);
			boolean online = false;
			for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++) {
				if (PlayerHandler.players[i] != null) {
					if (PlayerHandler.players[i].getPlayerName().equalsIgnoreCase(name)) {
						player.playerAssistant.sendMessage("Teleported to: " + name);
						player.getPA().movePlayer(PlayerHandler.players[i].getX(), PlayerHandler.players[i].getY(), PlayerHandler.players[i].getHeight());
						online = true;
						break;
					}
				}
			}
			if (!online) {
				player.getPA().sendMessage(name + " is offline.");
			}
		} catch (Exception e) {
			player.getPA().sendMessage("Use as ::xteletome mgt madness");
		}
	}

	/**
	 * dc command.
	 *
	 * @param player The associated player.
	 * @param playerCommand The command used by the player.
	 */
	public static void kick(Player player, String playerCommand) {
		try {
			String playerToBan = playerCommand.substring(5);
			for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++) {
				Player loop = PlayerHandler.players[i];
				if (loop != null) {
					if (loop.getPlayerName().equalsIgnoreCase(playerToBan)) {
						if (player.isModeratorRank() && !player.isAdministratorRank() && !Area.inStaffZone(loop)) {
							player.getPA().sendMessage(loop.getPlayerName() + " needs to be at staff zone to be teleported.");
							return;
						}
						loop.getPA().forceToLogInScreen();
						loop.setDisconnected(true, "kick");
						loop.setTimeOutCounter(ServerConstants.TIMEOUT + 1);
						player.getPA().sendMessage("You have kicked: " + loop.getPlayerName());
						return;
					}
				}
			}
			player.playerAssistant.sendMessage(playerToBan + " is offline.");
		} catch (Exception e) {
			player.playerAssistant.sendMessage("Wrong use, ::kick mgt madness");
		}
	}

	/**
	 * hide command.
	 *
	 * @param player The associated player.
	 * @param playerCommand The command used by the player.
	 */
	private static void hide(Player player, String playerCommand) {
		String msg = playerCommand.substring(5);
		for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++) {
			if (PlayerHandler.players[i] != null) {
				Player c2 = PlayerHandler.players[i];
				c2.playerAssistant.sendMessage("" + msg + "");
			}
		}
	}

	/**
	 * tele command.
	 *
	 * @param player The associated player.
	 * @param playerCommand The command used by the player.
	 */
	private static void tele(Player player, String playerCommand) {
		String[] arg = playerCommand.split(" ");
		player.playerAssistant.sendMessage("Before: " + player.getX() + ", " + player.getY() + ", " + player.getHeight());
		if (arg.length > 3) {
			player.getPA().movePlayer(Integer.parseInt(arg[1]), Integer.parseInt(arg[2]), Integer.parseInt(arg[3]));
			player.playerAssistant.sendMessage("Teleported to: " + Integer.parseInt(arg[1]) + ", " + Integer.parseInt(arg[2]) + ", " + Integer.parseInt(arg[3]));
		} else if (arg.length == 3) {
			player.getPA().movePlayer(Integer.parseInt(arg[1]), Integer.parseInt(arg[2]), player.getHeight());
			player.playerAssistant.sendMessage("Teleported to: " + Integer.parseInt(arg[1]) + ", " + Integer.parseInt(arg[2]) + ", " + player.getHeight());
		}
	}

	/**
	 * spec command.
	 *
	 * @param player The associated player.
	 */
	private static void spec(Player player) {
		player.setSpecialAttackAmount(1000.0, false);
		player.playerAssistant.sendMessage("You now have full special bar");
		CombatInterface.updateSpecialBar(player);
	}

	/**
	 * teletome command.
	 *
	 * @param player The associated player.
	 * @param playerCommand The command used by the player.
	 */
	public static void xTeleToMe(Player player, String playerCommand) {
		try {
			boolean online = false;
			String playerToTele = playerCommand.substring(10);
			for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++) {
				if (PlayerHandler.players[i] != null) {
					if (PlayerHandler.players[i].getPlayerName().equalsIgnoreCase(playerToTele)) {
						Player c2 = PlayerHandler.players[i];
						c2.getPA().movePlayer(player.getX(), player.getY(), player.getHeight());
						player.playerAssistant.sendMessage("Teleported " + playerToTele + " to me.");
						online = true;
						break;
					}
				}
			}
			if (!online) {
				player.getPA().sendMessage(playerToTele + " is offline.");
			}
		} catch (Exception e) {
			player.getPA().sendMessage("Use as ::xteletome mgt madness");
		}
	}

	/**
	 * gfx command.
	 *
	 * @param player The associated player.
	 * @param playerCommand The command used by the player.
	 */
	private static void gfx(Player player, String playerCommand) {
		String[] args = playerCommand.split(" ");
		player.gfx0(Integer.parseInt(args[1]));
		player.playerAssistant.sendMessage("GFX: " + Integer.parseInt(args[1]));
	}

	/**
	 * anim command.
	 *
	 * @param player The associated player.
	 * @param playerCommand The command used by the player.
	 */
	private static void anim(Player player, String playerCommand) {
		String[] args = playerCommand.split(" ");
		if (args.length == 1) {

			player.startAnimation(65535);
			player.getPA().requestUpdates();
			player.playerAssistant.sendMessage("Animation: 65535");
			return;
		}
		player.startAnimation(Integer.parseInt(args[1]));
		player.getPA().requestUpdates();
		player.playerAssistant.sendMessage("Animation: " + Integer.parseInt(args[1]));
	}

	/**
	 * interface command.
	 *
	 * @param player The associated player.
	 * @param playerCommand The command used by the player.
	 */
	private static void interfaceCommand(Player player, String playerCommand) {
		try {
			String[] args = playerCommand.split(" ");
			player.getPA().displayInterface(Integer.parseInt(args[1]));
			player.playerAssistant.sendMessage("Opened interface " + Integer.parseInt(args[1]) + ".");
		} catch (Exception e) {
			player.playerAssistant.sendMessage("Wrong input.");
		}
	}

}