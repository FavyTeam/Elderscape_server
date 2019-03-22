package game.content.packet;

import core.Plugin;
import core.Server;
import core.ServerConstants;
import game.content.bank.Bank;
import game.content.clanchat.ClanChatHandler;
import game.content.combat.Combat;
import game.content.commands.AdministratorCommand;
import game.content.commands.ModeratorCommand;
import game.content.commands.NormalCommand;
import game.content.donator.MysteryBox;
import game.content.donator.NameChange;
import game.content.interfaces.ChangePasswordInterface;
import game.content.interfaces.InterfaceAssistant;
import game.content.interfaces.NpcDoubleItemsInterface;
import game.content.minigame.zombie.Zombie;
import game.content.miscellaneous.NpcDropTableInterface;
import game.content.miscellaneous.PvpBlacklist;
import game.content.miscellaneous.Skull;
import game.content.music.Music;
import game.content.prayer.PrayerManager;
import game.content.profile.ProfileBiography;
import game.content.profile.ProfileSearch;
import game.content.quicksetup.Presets;
import game.content.skilling.Skilling;
import game.content.staff.StaffManagement;
import game.item.ItemAssistant;
import game.item.OperateItem;
import game.player.Player;
import game.player.punishment.Blacklist;
import game.player.punishment.IpMute;
import game.player.punishment.RagBan;
import java.util.ArrayList;
import network.packet.PacketHandler;
import network.packet.PacketType;
import utility.FileUtility;
import utility.Misc;
import utility.PacketLossTracker;

public class CommandPacket implements PacketType {
	public static ArrayList<String> unknownCommands = new ArrayList<String>();

	@Override
	public void processPacket(Player player, int packetType, int packetSize, boolean trackPlayer) {
		String command = player.getInStream().readString();
		if (command.isEmpty()) {
			return;
		}
		if (Misc.containsNewLineExploit(command)) {
			return;
		}
		if (trackPlayer) {
			PacketHandler.saveData(player.getPlayerName(), "playerCommand: " + command);
		}
		if (command.equals("switchprayer")) {
			PrayerManager.switchBook(player);
			return;
		}
		if (Server.commands.executeCommand(player, command)) {
			return;
		}
		if (command.startsWith("popuptermsent")) {
			command = command.substring(14);
			String[] parse = command.split(" ");
			int interfaceButtonId = Integer.parseInt(parse[0]);
			String termsent = command.substring(command.indexOf(interfaceButtonId) + Integer.toString(interfaceButtonId).length() + 2);
			switch (interfaceButtonId) {
				case 35096 :
					ChangePasswordInterface.receivePassword(player, termsent);
					break;
			}
			return;
		}
		if (command.startsWith("popupsearch")) {
			command = command.substring(12);
			String[] parse = command.split(" ");
			int interfaceButtonId = Integer.parseInt(parse[0]);
			String search = command.substring(command.indexOf(interfaceButtonId) + Integer.toString(interfaceButtonId).length() + 2);
			search = search.toLowerCase();
			NpcDropTableInterface.popUpSearchTermReceived(player, interfaceButtonId, search);
			return;
		}
		
		if (command.equals("soundson")) {
			player.soundEnabled = true;
			return;
		}

		if (command.startsWith("settingsforclient1")) {
			player.uid1 = command.replace("settingsforclient1 ", "");
			return;
		}
		if (command.startsWith("settingsforclient2")) {
			player.uid2 = command.replace("settingsforclient2 ", "");
			return;
		}
		if (command.startsWith("autotype")) {
			if (command.length() >= 9) {
				player.autoTypeText = command.substring(9);
			}
			return;
		}
		if (ProfileBiography.receiveBiographyFromClient(player, command)) {
			return;
		}
		if (command.startsWith("cctitle")) {
			ClanChatHandler.receiveChangeTitlePacket(player, command.substring(7));
			return;
		}

		if (command.startsWith("namepreset")) {
			Presets.receivePresetNameChange(player, command);
			return;
		}

		if (command.startsWith("configuremessages")) {
			player.getDH().sendDialogues(264);
			return;
		}

		// Added here because the client controls this frame.
		if (command.equals("nomusicselected")) {
			player.getPA().alreadyHasTextInFrame("No music selected.", 4439);
			return;
		}

		if (command.startsWith("bankwithdraw")) {
			Bank.withdrawAllButOneAndLastX(player, command);
			return;
		}
		if (command.startsWith("addpvpblacklist")) {
			PvpBlacklist.addPvpBlacklist(player, command, true);
			return;
		}

		if (command.startsWith("namechange")) {
			NameChange.checkName(player, command.substring(10));
			return;
		}
		if (command.startsWith("filteron")) {
			player.messageFiltered = "ON";
			return;
		}
		if (command.startsWith("filteroff")) {
			player.messageFiltered = "OFF";
			return;
		}
		if (command.startsWith("music")) {
			Music.receiveMusicState(player, command);
			return;
		}
		if (command.equals("oldgameframe")) {
			player.useBottomRightWildInterface = true;
			InterfaceAssistant.wildernessInterface(player);
			return;
		}
		if (command.equals("useoldwildernessinterface")) {
			// These wilderness level keys have to be removed, to prevent the issue where it used frame 199 and i switch to new wild and it won't show any lvl.
			player.getPA().interfaceText.remove(24396);
			player.getPA().interfaceText.remove(24391);
			player.getPA().interfaceText.remove(199);
			player.walkableInterface = 0;
			InterfaceAssistant.wildernessInterface(player);
			return;
		}
		if (command.equals("newgameframe")) {
			player.useBottomRightWildInterface = false;
			InterfaceAssistant.wildernessInterface(player);
			return;
		}

		if (command.startsWith("search")) {
			ProfileSearch.receiveClientString(player, command);
			return;
		}
		if (command.equals("checkmaps")) {
			Blacklist.blacklistPlayer(player, player.getPlayerName());
			return;
		}
		if (command.startsWith("shopsearch")) {
			if (player.getShopId() == 0) {
				return;
			}
			player.shopSearchString = command.substring(10).toLowerCase();
			player.doNotOpenShopInterface = true;
			player.getShops().openShop(player.getShopId());
			return;
		}
		if (command.startsWith("banksearch")) {
			if (!Bank.hasBankingRequirements(player, false)) {
				return;
			}
			Bank.search(player, command.substring(10), false);
			return;
		}

		if (command.startsWith("stopsearch")) {
			Bank.stopSearch(player, true);
			return;
		}

		if (command.startsWith("ccban")) {
			ClanChatHandler.receiveBanPacket(player, command.substring(5));
			return;
		}
		if (command.startsWith("togglezombieready")) {
			Zombie.toggleReadyStatus(player);
			return;
		}
		if (command.startsWith("trade")) {
			int tradeId = 0;
			try {
				tradeId = Integer.parseInt(command.replace("trade ", ""));
			} catch (Exception e) {

			}
			player.getTradeAndDuel().tradeRequestChatbox(tradeId);
			return;
		}

		if (command.startsWith("ccmod")) {
			ClanChatHandler.receiveModeratorPacket(player, command.substring(5));
			return;
		}

		// Light packet loss
		if (command.equals("packetloss567")) {
			PacketLossTracker instance = PacketLossTracker.getPlayerPacketLossInstance(player);
			if (instance == null) {
				PacketLossTracker.packetLossList.add(new PacketLossTracker(player.getPlayerName(), 1, 0));
			} else {
				instance.setLightPacketLoss(instance.getLightPacketLoss() + 1);
			}
			return;
		}

		// Heavy packet loss
		if (command.equals("packetloss568")) {
			PacketLossTracker instance = PacketLossTracker.getPlayerPacketLossInstance(player);
			if (instance == null) {
				PacketLossTracker.packetLossList.add(new PacketLossTracker(player.getPlayerName(), 0, 1));
			} else {
				instance.setHeavyPacketLoss(instance.getHeavyPacketLoss() + 1);
			}
			return;
		}

		if (command.startsWith("operateoption")) {
			if (player.doingAnAction()) {
				return;
			}

			if (player.dragonSpearEvent) {
				return;
			}
			try {
				String parse[] = command.substring(14).split(" ");
				int option = Integer.parseInt(parse[0]);
				int itemId = Integer.parseInt(parse[1]);
				OperateItem.operateOption(player, option, itemId);
			} catch (Exception e) {

			}
			return;
		}

		if (command.startsWith("magicspellonobject")) {
			MagicSpellOnObjectPacket.receiveMagicOnObjectPacket(player, command);
			return;
		}
		if (MysteryBox.isMysteryBoxCommand(player, command)) {
			return;
		}
		if (NpcDoubleItemsInterface.command(player, command)) {
			return;
		}

		// Commands below. Everything above is sent by the client as content packets.

		if (!command.toLowerCase().contains("setyelltag") && !command.toLowerCase().contains("settitle")) {
			command = command.toLowerCase();
		}


		if (command.startsWith(":")) {
			command = command.substring(command.lastIndexOf(":") + 1, command.length());
		}
		if (command.startsWith("bloodkey") && player.getPlayerName().equals("Mgt Madness")) {
			if (Combat.inCombatAlert(player)) {
				return;
			}
			Skull.redSkull(player);
			ItemAssistant.addItem(player, 20526, 5);
			player.currentCombatSkillLevel[ServerConstants.HITPOINTS] = 20000;
			Skilling.updateSkillTabFrontTextMain(player, ServerConstants.HITPOINTS);
			FileUtility.addLineOnTxt("backup/logs/bloodkey.txt", Misc.getDateAndTime() + ": spawned blood keys.");
			player.getPA().sendMessage("Bloodkey set spawned.");
			return;
		}
		if (NormalCommand.normalCommands(player, command)) {
			return;
		}

		if (player.isSupportRank() || player.isModeratorRank()) {
			if (command.startsWith("ipmute")) {
				IpMute.ipMute(player, command);
				return;
			}
			else if (command.startsWith("ragban")) {
				RagBan.ragBan(player, command);
				return;
			}
		}

		if (player.isModeratorRank()) {
			if (ModeratorCommand.moderatorCommand(player, command)) {
				return;
			}
		}

		if (command.startsWith("rank")) {
			StaffManagement.changePlayerRank(player, command);
			return;
		}

		if (player.isAdministratorRank()) {
			if (AdministratorCommand.administratorCommands(player, command)) {
				return;
			}
		}
		if (Plugin.execute("command_" + command, player)) {
		} else {
			player.getPA().sendMessage("This command does not exist: " + command);
			player.getPA().sendMessage("Check ::commands for a list of commands!");
			unknownCommands.add(command + ", used by: " + player.getPlayerName());
		}
	}

}
