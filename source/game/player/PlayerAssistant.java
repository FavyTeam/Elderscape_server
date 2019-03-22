package game.player;

import core.GameType;
import core.Server;
import core.ServerConfiguration;
import core.ServerConstants;
import game.position.Position;
import game.container.ItemContainer;
import game.content.combat.Combat;
import game.content.combat.CombatConstants;
import game.content.interfaces.AreaInterface;
import game.content.interfaces.InterfaceAssistant;
import game.content.minigame.AutoDice;
import game.content.minigame.TargetSystem;
import game.content.minigame.barrows.Barrows;
import game.content.miscellaneous.HackLog;
import game.content.miscellaneous.PmLog;
import game.content.packet.ClickNpcPacket;
import game.content.packet.PrivateMessagingPacket;
import game.content.skilling.Skilling;
import game.content.skilling.agility.AgilityAssistant;
import game.content.staff.PrivateAdminArea;
import game.content.starter.GameMode;
import game.content.worldevent.Tournament;
import game.entity.Entity;
import game.entity.EntityType;
import game.npc.Npc;
import game.npc.data.NpcDefinition;
import game.object.clip.Region;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import game.player.movement.Follow;
import game.player.movement.Movement;
import game.player.punishment.Mute;
import game.player.punishment.RagBan;
import java.util.HashMap;
import java.util.Map;

import game.position.PositionUtils;
import game.position.distance.DistanceAlgorithms;
import network.connection.DonationManager;
import network.connection.VoteManager;
import network.packet.PacketHandler;
import network.packet.Stream;
import utility.LoadTextWidth;
import utility.Misc;
import utility.WebsiteLogInDetails;

public class PlayerAssistant {
	private Player player;

	public PlayerAssistant(Player Player) {
		this.player = Player;
	}

	public boolean playerIsBusy() {
		if (player.isInTrade() || player.isUsingBankInterface() || player.getDuelStatus() != 0 || player.getInterfaceIdOpened() == AutoDice.GAMBLE_INTERFACE_ID
				|| player.isInGambleMatch() || player.isTeleporting() || player.doingAnAction()) {
			return true;
		}
		return false;
	}

	public void chatEffectUpdate() {
		if (player.chatEffects) {
			InterfaceAssistant.chatEffectOn(player);
		} else {
			InterfaceAssistant.chatEffectOff(player);
		}
	}


	/**
	 * Remove player who is in an instance, if the player is in height 24 or more, then it is a private instance.
	 */
	public void removedFromPrivateInstance() {
		if (player.getHeight() >= 24) {
			player.getPA().movePlayer(player.getX(), player.getY(), 0);
		}
	}

	public void talkToHalloweenZamorak(Player player) {
		if (!Area.inHalloweenQuestGraveArea(player)) {
			player.getDH().sendNpcChat("Tell him that i will be there, then", "meet in the Haunted woods,", "east of Canafis. It needs to be the small building",
			                           "with the gravestone to the north and the swamp to the south.", 605);
		} else {
			player.getDH().sendNpcChat("Talk to him now that we are here.", 605);
		}
	}

	/**
	 * Open a website.
	 *
	 * @param website
	 */
	public void openWebsite(String website, boolean closeInterfaces) {
		if (website.contains("www.dawntained.com")) {
			website = website.replace("www.dawntained.com", WebsiteLogInDetails.WEBSITE_URL);
		}
		player.getPA().sendMessage(":packet:website " + website);
		if (closeInterfaces) {
			player.getPA().closeInterfaces(true);
		}
	}

	public void objectAnimation(int X, int Y, int tileObjectType, int orientation, int animationId) {
		sendPosition(X, Y, false);
		player.getOutStream().createFrame(160);
		player.getOutStream().writeByteS(((X & 7) << 4) + (Y & 7));
		player.getOutStream().writeByteS((tileObjectType << 2) + (orientation & 3));
		player.getOutStream().writeWordA(animationId);
	}

	public void sendObjectAnimationForLocal(int x, int y, int tileObjectType, int orientation, int animationId) {
		objectAnimation(x, y, tileObjectType, orientation, animationId);

		for (Player local : player.getLocalPlayers()) {
			if (local == null) {
				continue;
			}
			if (local.getType() == EntityType.PLAYER) {
				local.getPA().objectAnimation(x, y, tileObjectType, orientation, animationId);
			}
		}
	}

	public boolean objectIsAt(int x, int y) {
		return player.getObjectX() == x && player.getObjectY() == y;
	}


	public boolean objectIsAt(int x, int y, int height) {
		return player.getObjectX() == x && player.getObjectY() == y && player.getHeight() == height;
	}

	/**
	 * Reset stats to base values, such as 99 strength instead of 118.
	 */
	public void resetStats() {
		for (int index = 0; index < 7; index++) {
			if (player.currentCombatSkillLevel[index] > player.baseSkillLevel[index]) {
				player.currentCombatSkillLevel[index] = player.baseSkillLevel[index];
				Skilling.updateSkillTabFrontTextMain(player, index);
			}
		}
	}

	public boolean playerOnNpc(Player player, Npc npc) {
		if (npc == null) {
			return false;
		}
		return player.getX() == npc.getX() && player.getY() == npc.getY();
	}

	public void toggleBots(boolean dialogue) {
		player.displayBots = !player.displayBots;
		updateDisplayBots();
		String option = player.displayBots ? "on" : "off";
		if (dialogue) {
			player.getDH().sendStatement("Bots have been turned: " + option + ".");
		} else {
			player.getPA().sendMessage("Bots have been turned: " + option + ".");
		}
	}

	public void updateDisplayBots() {
		String option = player.displayBots ? "on" : "off";
		player.getPA().sendMessage(":packet:displaybots:" + option);
	}

	public void setInterfaceClicked(int parentInterfaceId, int interfaceId, boolean clicked) {
		player.getPA().sendMessage(":packet:setclicked " + parentInterfaceId + " " + interfaceId + " " + clicked);
	}

	public void setTextClicked(int interfaceId, boolean clicked) {
		player.textClickedInterfaceId = interfaceId;
		player.getPA().sendMessage(":packet:settextclicked " + interfaceId + " " + clicked);
	}

	public void alertNotSameIp() {
		if (!player.isTutorialComplete()) {
			return;
		}
		if (player.originalIp.isEmpty() || player.originalUid.isEmpty()) {
			player.originalIp = player.addressIp;
			player.originalUid = player.addressUid;
		}

		boolean originalIpMatch = player.originalIp.equals(player.addressIp);
		boolean uidHasMatches = Misc.uidMatches(player.addressUid, player.originalUid);
		if (originalIpMatch || uidHasMatches) {
			player.originalIp = player.addressIp;
			player.originalUid = player.addressUid;
		}

		HackLog.addNewHackEntry(player);
	}

	/**
	 * Cancel specific animations from creating this "drag" effect while moving.
	 */
	public void animationDragCancel() {
		for (int value = 0; value < ServerConstants.animationCancel.length; value++) {

			if (player.getLastAnimation() == ServerConstants.animationCancel[value]) {
				player.startAnimation(65535);
				return;
			}
		}
	}

	/**
	 * This event will save the player after the player has been online for 5 mins.
	 */
	public void saveGameEvent() {
		if (ServerConfiguration.DEBUG_MODE) {
			return;
		}
		if (player.isCombatBot()) {
			return;
		}
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {

				VoteManager.voteAlert(player);

				// Do not save account if in tournament.
				if (player.getHeight() != 20) {
					PlayerSave.saveGame(player);
				}
			}

			@Override
			public void stop() {

			}
		}, 100 * Server.saveTimer); // 20 minutes..

	}

	/**
	 * This event is called per game tick to save the game.
	 */
	public void loopedSave() {
		if (!ServerConfiguration.DEBUG_MODE) {
			return;
		}
		if (ServerConfiguration.STABILITY_TEST) {
			return;
		}
		if (player.isCombatBot()) {
			return;
		}
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				PlayerSave.saveGame(player);
			}

			@Override
			public void stop() {

			}
		}, 1);

	}

	public void sendClan(String name, String message, String clan, int rights) {
		if (player.bot) {
			return;
		}
		player.getOutStream().createFrameVarSizeWord(217);
		player.getOutStream().writeString(name);
		player.getOutStream().writeString(message);
		player.getOutStream().writeString(clan);
		player.getOutStream().writeWord(rights);
		player.getOutStream().endFrameVarSize();
	}

	public void sendFrame34Other(int item, int slot, int frame, int amount) {
		sendFrame34(frame, item, slot, amount);
	}

	public void sendFrame34(int frame, int item, int slot, int amount) {
		if (player.bot) {
			return;
		}
		if (alreadyHasSendFrame34a(frame, item, slot, amount)) {
			return;
		}
		player.getOutStream().createFrameVarSizeWord(34);
		player.getOutStream().writeWord(frame);
		player.getOutStream().writeByte(slot);
		player.getOutStream().writeWord(item + 1);
		if (amount > 254) {
			player.getOutStream().writeByte(255);
			player.getOutStream().writeDWord(amount);
		} else {
			player.getOutStream().writeByte(amount);
		}
		player.getOutStream().endFrameVarSizeWord();
	}

	public void multiWay(int i1) {
		if (player.bot) {
			return;
		}
		if (player.getOutStream() == null) {
			return;
		}
		player.getOutStream().createFrame(61);
		player.getOutStream().writeByte(i1);
		player.setUpdateRequired(true);
		player.setAppearanceUpdateRequired(true);
	}

	private Map<Integer, interfaceColourStore> interfaceColour = new HashMap<Integer, interfaceColourStore>();


	public class interfaceColourStore {
		public int colour;

		public int id;

		public interfaceColourStore(int colour, int id) {
			this.colour = colour;
			this.id = id;
		}

	}

	/**
	 * @return True, if the player already has the text in the interface id.
	 */
	public boolean alreadyHasColourInFrame(int colour, int id) {
		if (interfaceColour.containsKey(id)) {
			interfaceColourStore t = interfaceColour.get(id);
			if (colour == t.colour) {
				return true;
			}
			t.colour = colour;
		} else {
			if (colour == ServerConstants.RED_HEX) {
				return true;
			}
			interfaceColour.put(id, new interfaceColourStore(colour, id));
		}
		return false;
	}

	public Map<Integer, InterfaceStore> interfaceText = new HashMap<Integer, InterfaceStore>();


	public class InterfaceStore {
		public int id;

		public String text;

		public InterfaceStore(String s, int id) {
			this.text = s;
			this.id = id;
		}

	}

	/**
	 * @return True, if the player already has the text in the interface id.
	 */
	public boolean alreadyHasTextInFrame(String text, int id) {
		for (int i = 0; i < ServerConstants.interfaceFramesIgnoreRepeat.length; i++) {
			if (id == ServerConstants.interfaceFramesIgnoreRepeat[i]) {
				return false;
			}
		}

		// Dialogue option ids. It changes to Please wait client sided, so must update it here all the time.
		if (id >= 2461 && id <= 2498) {
			return false;
		}

		if (interfaceText.containsKey(id)) {
			InterfaceStore t = interfaceText.get(id);
			if (text.equals(t.text)) {
				return true;
			}
			t.text = text;
		} else {
			interfaceText.put(id, new InterfaceStore(text, id));
		}
		return false;
	}

	public void sendFrame126(String text, int id) {
		sendFrame126Specific(text, id, true);
	}

	public void sendFrame126Specific(String text, int id, boolean store) {
		if (player.bot) {
			return;
		}
		if (player.getOutStream() != null && player != null) {
			if (store) {
				if (alreadyHasTextInFrame(text, id)) {
					return;
				}
			}
			player.getOutStream().createFrameVarSizeWord(126);
			player.getOutStream().writeString(text);
			player.getOutStream().writeWordA(id);
			player.getOutStream().endFrameVarSizeWord();
			player.flushOutStream();
		}
	}

	public void setSkillLevel(int skillNum, int currentLevel, int XP) {
		if (player.bot) {
			return;
		}

		// So it updates correctly on log-in.
		if (skillNum == ServerConstants.MAGIC) {
			currentLevel = player.getCurrentCombatSkillLevel(ServerConstants.MAGIC);
		}
		if (player.getOutStream() != null && player != null) {
			player.getOutStream().createFrame(134);
			player.getOutStream().writeByte(skillNum);
			player.getOutStream().writeDWord_v1(XP);
			player.getOutStream().writeByte(currentLevel);
			player.flushOutStream();
		}
	}

	public void sendFrame106(int sideIcon) {
		if (player.bot) {
			return;
		}
		if (player.getOutStream() != null && player != null) {
			player.getOutStream().createFrame(106);
			player.getOutStream().writeByteC(sideIcon);
			player.flushOutStream();
			requestUpdates();
		}
	}

	public void resetCameraShake() {
		if (player.bot) {
			return;
		}
		if (player.getOutStream() != null && player != null) {
			player.getOutStream().createFrame(107);
			player.flushOutStream();
		}
	}


	public void sendFrame36(int id, int state, boolean isPrayerDisableGlow) {
		if (alreadyHasSendFrame36(id, state) && !isPrayerDisableGlow) {
			return;
		}
		if (player.bot) {
			return;
		}
		if (player.getOutStream() != null && player != null) {
			player.getOutStream().createFrame(36);
			player.getOutStream().writeWordBigEndian(id);
			player.getOutStream().writeByte(state);
			player.flushOutStream();
		}
	}

	public void sendFrame185(int Frame) {
		if (player.bot) {
			return;
		}
		if (player.getOutStream() != null && player != null) {
			player.getOutStream().createFrame(185);
			player.getOutStream().writeWordBigEndianA(Frame);
		}
	}

	public void displayInterface(int interfaceId) {
		if (player.bot) {
			return;
		}
		player.setInterfaceIdOpened(interfaceId);
		player.isUsingDeathInterface = false;
		if (player.getOutStream() != null && player != null) {
			player.getOutStream().createFrame(97);
			player.getOutStream().writeWord(interfaceId);
			player.flushOutStream();
		}
	}

	public void sendFrame248(int interfaceId, int frame) {
		if (player.bot) {
			return;
		}
		if (player.getOutStream() != null && player != null) {
			player.setInterfaceIdOpened(interfaceId);
			player.getOutStream().createFrame(248);
			player.getOutStream().writeWordA(interfaceId);
			player.getOutStream().writeWord(frame);
			player.flushOutStream();
		}
	}

	public void sendFrame246(int MainFrame, int SubFrame, int SubFrame2) {
		if (player.bot) {
			return;
		}
		if (player.getOutStream() != null && player != null) {
			player.getOutStream().createFrame(246);
			player.getOutStream().writeWordBigEndian(MainFrame);
			player.getOutStream().writeWord(SubFrame);
			player.getOutStream().writeWord(SubFrame2);
			player.flushOutStream();
		}
	}

	public void sendComponentVisibility(boolean hide, int component) {
		sendFrame171(hide ? 1 : 0, component);
	}

	public void sendFrame171(int MainFrame, int SubFrame) {
		if (player.bot) {
			return;
		}
		if (player.getOutStream() != null && player != null) {
			player.getOutStream().createFrame(171);
			player.getOutStream().writeByte(MainFrame);
			player.getOutStream().writeWord(SubFrame);
			player.flushOutStream();
		}
	}

	public void sendFrame200(int MainFrame, int SubFrame) {
		if (player.bot) {
			return;
		}
		if (player.getOutStream() != null && player != null) {
			player.playerAssistant.sendMessage(":npctype" + player.getNpcType());
			player.getOutStream().createFrame(200);
			player.getOutStream().writeWord(MainFrame);
			player.getOutStream().writeWord(SubFrame);
			player.flushOutStream();
		}
	}

	public void sendComponentOffset(int x, int y, int id) {
		if (player.bot) {
			return;
		}
		if (player.getOutStream() != null && player != null) {
			player.getOutStream().createFrame(70);
			player.getOutStream().writeWord(x);
			player.getOutStream().writeWordBigEndian(y);
			player.getOutStream().writeWordBigEndian(id);
			player.flushOutStream();
		}
	}

	public void sendFrame75(int MainFrame, int SubFrame) {
		if (player.bot) {
			return;
		}
		if (player.getOutStream() != null && player != null) {
			player.getOutStream().createFrame(75);
			player.getOutStream().writeWordBigEndianA(MainFrame);
			player.getOutStream().writeWordBigEndianA(SubFrame);
			player.flushOutStream();
		}
	}

	private boolean sendable() {
        if (player.bot) {
            return false;
        }
        if (player.getOutStream() == null) {
            return false;
        }
        return true;
    }

    public void sendCameraPosition(int x, int y, int xCurve, int yCurve) {
        if (!sendable()) {
            return;
        }
        Stream stream = player.getOutStream();

        stream.createFrame(19);
        stream.writeWord(x);
        stream.writeWord(xCurve);
        stream.writeWord(y);
        stream.writeWord(yCurve);
        player.flushOutStream();
    }

	public void sendCameraAngle(int localX, int localY, int z, int rotationSpeed, int angle) {
        if (!sendable()) {
            return;
        }
        Stream stream = player.getOutStream();

        stream.createFrame(177);
        stream.writeByte(localX);
        stream.writeByte(localY);
        stream.writeWord(z);
        stream.writeByte(rotationSpeed);
        stream.writeByte(angle);
        player.flushOutStream();
    }

	public void sendSpinCamera(int x, int y, int z, int speed, int angle) {
		if (player.bot) {
			return;
		}
		Stream stream = player.getOutStream();

		if (stream == null) {
			return;
		}
		stream.createFrame(166);
		stream.writeByte(x);
		stream.writeByte(y);
		stream.writeWord(z);
		stream.writeByte(speed);
		stream.writeByte(angle);
		player.flushOutStream();
	}

	public void sendFrame164(int Frame) {
		if (player.bot) {
			return;
		}
		if (player.getOutStream() != null && player != null) {
			player.getOutStream().createFrame(164);
			player.getOutStream().writeWordBigEndian_dup(Frame);
			player.flushOutStream();
		}
	}

	public void sendFrame214() {
		if (player.bot) {
			return;
		}
		if (player.getOutStream() != null && player != null) {
			player.getOutStream().createFrameVarSizeWord(214);

			for (long ignore : player.ignores) {
				if (ignore > 0) {
					player.getOutStream().writeQWord(ignore);
				}
			}

			player.getOutStream().endFrameVarSizeWord();
			player.flushOutStream();
		}
	}

	public void setPrivateMessaging(int i) { // friends and ignore list status
		if (player.bot) {
			return;
		}
		if (player.getOutStream() != null && player != null) {
			player.getOutStream().createFrame(221);
			player.getOutStream().writeByte(i);
			player.flushOutStream();
		}
	}

	public void setChatOptions(int publicChat, int privateChat, int tradeBlock) {
		if (player.bot) {
			return;
		}
		if (player.getOutStream() != null && player != null) {
			player.getOutStream().createFrame(206);
			player.getOutStream().writeByte(publicChat);
			player.getOutStream().writeByte(privateChat);
			player.getOutStream().writeByte(tradeBlock);
			player.flushOutStream();
		}
	}

	public void sendFrame87(int id, int state) {
		if (player.bot) {
			return;
		}
		if (player.getOutStream() != null && player != null) {
			player.getOutStream().createFrame(87);
			player.getOutStream().writeWordBigEndian_dup(id);
			player.getOutStream().writeDWord_v1(state);
			player.flushOutStream();
		}
	}

	public void sendPM(Player other, String pmSender, long name, int rights, byte[] chatmessage, int messagesize, boolean trackPlayer) {
		String message = Misc.textUnpack(chatmessage, messagesize);
		if (message.toLowerCase().contains("<img=") || message.toLowerCase().contains("<col=")) {
			return;
		} else if (Misc.containsPassword(player.playerPass, message)) {
			return;
		}
		if (player.bot) {
			return;
		}
		if (player.getOutStream() != null && player != null) {
			player.getOutStream().createFrameVarSize(196);
			player.getOutStream().writeQWord(name);
			player.getOutStream().writeDWord(player.lastChatId++);
			player.getOutStream().writeByte(rights);
			player.getOutStream().writeBytes(chatmessage, messagesize, 0);
			player.getOutStream().endFrameVarSize();
			player.flushOutStream();
			PmLog.addPmLog(other, player, pmSender, Misc.textUnpack(chatmessage, messagesize));
			if (trackPlayer) {
				PacketHandler.chatAndPmLog.add("At " + Misc.getDateAndTime());
				PacketHandler.chatAndPmLog.add("From: " + pmSender + ", to: " + player.getPlayerName() + ", " + Misc.textUnpack(chatmessage, messagesize));
			}
		}
	}

	public void createPlayerHints(int type, int id) {
		if (player.bot) {
			return;
		}

		// Tell client to prioritize the target.
		if (id >= 0) {
			player.getPA().sendMessage(":packet:prioritizetarget " + id);
		}
		if (player.getOutStream() != null && player != null) {
			if (id >= 0) {
				Player target = PlayerHandler.players[id];
				if (target != null) {
					player.getPA().sendMessage(":packet:targethint:" + target.getX() + ":" + target.getY() + ":" + id);
				}

			}
			player.getOutStream().createFrame(254);
			player.getOutStream().writeByte(type);
			player.getOutStream().writeWord(id);
			player.getOutStream().write3Byte(0);
			player.flushOutStream();
		}
	}

	public void createNpcProjectile(int projectileId, Npc npc, int offsetX, int offsetY, int delay) {
		int nX = npc.getX();
		int nY = npc.getY();
		if (player.getX() > npc.getX()) {
			nX += offsetX;
		}
		if (player.getY() > npc.getY()) {
			nY += offsetY;
		}
		int pX = player.getX();
		int pY = player.getY();
		int offX = (nY - pY) * -1;
		int offY = (nX - pX) * -1;
		int speed = 90 + (npc.distanceTo(player.getX(), player.getY()) * 5);
		player.getPA().createPlayersProjectile(nX, nY, offX, offY, 50, speed, projectileId, 83, 33, -player.getPlayerId() - 1, delay, 0);

	}

	public void loadPM(long playerName, int world) {
		if (player.bot) {
			return;
		}
		if (player.getOutStream() != null && player != null) {
			if (world != 0) {
				world += 9;
			}
			player.getOutStream().createFrame(50);
			player.getOutStream().writeQWord(playerName);
			player.getOutStream().writeByte(world);
			player.flushOutStream();
		}
	}

	public void removeAllItemsFromInventory() {
		for (int i = 0; i < player.playerItems.length; i++) {
			player.playerItems[i] = 0;
		}
		for (int i = 0; i < player.playerItemsN.length; i++) {
			player.playerItemsN[i] = 0;
		}
		player.setInventoryUpdate(true);
	}

	public void closeInterfaces(boolean sendCloseInterfacePacket) {
			player.shopSearchString = "";
		if (player.bot) {
			return;
		}
		if (player.notifyFlagged) {
			return;
		}
		if (player.getOutStream() != null && player != null) {
			player.setInterfaceIdOpened(0);
			player.resetActionIdUsed();
			player.hasDialogueOptionOpened = false;
			if (sendCloseInterfacePacket) {
				player.getOutStream().createFrame(219);
				player.flushOutStream();
			}
			player.getTradeAndDuel().declineTrade1(true);
			player.usingShop = false;
			player.setUsingBankInterface(false);
			player.usingEquipmentBankInterface = false;
			player.setShopId(0);
			player.setDialogueAction(0);
			player.setDialogueChain(null);
			player.popUpSearchTerm = "";
			if (!player.doNotClosePmInterface) {
				player.getPA().sendMessage(":packet:closepminterface");
			}
			player.doNotClosePmInterface = false;
		}
	}

	public void walkableInterface(int id) {
		if (player.walkableInterface == id) {
			return;
		}
		if (player.bot) {
			return;
		}
		if (player.getOutStream() != null && player != null) {
			player.getOutStream().createFrame(208);
			player.getOutStream().writeWordBigEndian_dup(id);
			player.flushOutStream();
			player.walkableInterface = id;
		}
	}

	public int mapStatus = 0;

	public void sendFrame99(int state) { // used for disabling map
		if (player.bot) {
			return;
		}
		if (player.getOutStream() != null && player != null) {
			if (mapStatus != state) {
				mapStatus = state;
				player.getOutStream().createFrame(99);
				player.getOutStream().writeByte(state);
				player.flushOutStream();
			}
		}
	}

	/**
	 * Indicates the position in the region for the entity.
	 *
	 * @param x the x position.
	 * @param y the y position.
	 */
	public void sendPosition(int x, int y, boolean subtract) {
		player.getOutStream().createFrame(85);
		y = (y - (player.getMapRegionY() * 8)) - (subtract ? 2 : 0);
		player.getOutStream().writeByteC(y);
		x = (x - (player.getMapRegionX() * 8)) - (subtract ? 3 : 0);
		player.getOutStream().writeByteC(x);
		player.flushOutStream();
	}

	/**
	 * Creating projectile
	 **/
	public void createProjectile(int x, int y, int offX, int offY, int angle, int speed, int gfxMoving, int startHeight, int endHeight, int lockon, int time, int slope) {
		if (player.bot) {
			return;
		}
		if (player.getOutStream() != null && player != null) {
			sendPosition(x, y, true);
			player.getOutStream().createFrame(117);
			player.getOutStream().writeByte(angle);
			player.getOutStream().writeByte(offY);
			player.getOutStream().writeByte(offX);
			player.getOutStream().writeWord(lockon);
			player.getOutStream().writeWord(gfxMoving);
			player.getOutStream().writeByte(startHeight);
			player.getOutStream().writeByte(endHeight);
			player.getOutStream().writeWord(time);
			player.getOutStream().writeWord(speed);
			player.getOutStream().writeByte(slope);
			player.getOutStream().writeByte(64);
			player.flushOutStream();
		}
	}

	public void createProjectile2(int x, int y, int offX, int offY, int angle, int speed, int gfxMoving, int startHeight, int endHeight, int lockon, int time, int slope) {
		if (player.bot) {
			return;
		}
		if (player.getOutStream() != null && player != null) {
			sendPosition(x, y, true);
			player.getOutStream().createFrame(117);
			player.getOutStream().writeByte(angle);
			player.getOutStream().writeByte(offY);
			player.getOutStream().writeByte(offX);
			player.getOutStream().writeWord(lockon);
			player.getOutStream().writeWord(gfxMoving);
			player.getOutStream().writeByte(startHeight);
			player.getOutStream().writeByte(endHeight);
			player.getOutStream().writeWord(time);
			player.getOutStream().writeWord(speed);
			player.getOutStream().writeByte(slope);
			player.getOutStream().writeByte(64);
			player.flushOutStream();
		}
	}

	public void createPlayersProjectile(Npc npc, Player player, int angle, int speed, int gfxMoving, int startHeight, int endHeight, int lockon, int time, int slope) {
		createPlayersProjectile(npc, new Position(player.getX(), player.getY(), player.getHeight()), angle, speed, gfxMoving, startHeight, endHeight, lockon, time, slope);
	}

	public void createPlayersProjectile(Npc npc, Position to, int angle, int speed, int gfxMoving, int startHeight, int endHeight, int lockon, int time, int slope) {
		int nX = npc.getX();
		int nY = npc.getY();

		NpcDefinition definition = NpcDefinition.getDefinitions()[npc.npcType];

		if (definition != null && definition.size > 2) {
			nX += Math.ceil(definition.size / 2D);
			nY += Math.ceil(definition.size / 2D);
		}
		int pX = to.getX();
		int pY = to.getY();
		int offX = (nY - pY) * -1;
		int offY = (nX - pX) * -1;

		createPlayersProjectile(nX, nY, offX, offY, angle, speed, gfxMoving, startHeight, endHeight, lockon, time, slope);
	}

	public void createPlayersProjectile(Position from, Position to, int angle, int speed, int gfxMoving, int startHeight, int endHeight, int lockon, int time, int slope) {
		int nX = from.getX();

		int nY = from.getY();

		int pX = to.getX();

		int pY = to.getY();

		int offX = (nY - pY) * -1;

		int offY = (nX - pX) * -1;

		createPlayersProjectile(nX, nY, offX, offY, angle, speed, gfxMoving, startHeight, endHeight, lockon, time, slope);
	}

	// projectiles for everyone within 25 squares
	public void createPlayersProjectile(int x, int y, int offX, int offY, int angle, int speed, int gfxMoving, int startHeight, int endHeight, int lockon, int time, int slope) {
		for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++) {
			Player p = PlayerHandler.players[i];
			if (p != null) {
				if (p.getOutStream() != null) {
					if (p.playerAssistant.distanceToPoint(x, y) <= 25) {
						if (p.getHeight() == player.getHeight()) {
							p.getPA().createProjectile(x, y, offX, offY, angle, speed, gfxMoving, startHeight, endHeight, lockon, time, slope);
						}
					}
				}
			}
		}
	}

	public void createPlayersProjectile2(int x, int y, int offX, int offY, int angle, int speed, int gfxMoving, int startHeight, int endHeight, int lockon, int time, int slope,
	                                     int playerHeight) {
		for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++) {
			Player p = PlayerHandler.players[i];
			if (p != null) {
				Player person = p;
				if (person != null) {
					if (person.getOutStream() != null) {
						if (person.playerAssistant.distanceToPoint(x, y) <= CombatConstants.OUT_OF_VIEW_DISTANCE && person.getHeight() == playerHeight) {
							person.getPA().createProjectile2(x, y, offX, offY, angle, speed, gfxMoving, startHeight, endHeight, lockon, time, slope);
						}
					}
				}
			}
		}
	}
	
	public void createPlayersProjectile2(Player player, Player victim, int angle, int speed, int gfxMoving, int startHeight, int endHeight, int lockon, int time, int slope,
	                                     int playerHeight) {
		int pX = player.getX();
		int pY = player.getY();
		int oX = victim.getX();
		int oY = victim.getY();
		int offX = (pY - oY) * -1;
		int offY = (pX - oX) * -1;
		int differenceY = player.getY() - victim.getY();
		int differenceX = player.getX() - victim.getX();
		pY -= differenceY / 1.5;
		pX -= differenceX / 1.5;
		int distance = 0;
		if (!player.playerAssistant.withinDistanceOfTargetPlayer(victim, 4)) {
			distance++;
		}
		if (!player.playerAssistant.withinDistanceOfTargetPlayer(victim, 6)) {
			distance++;
		}
		speed += distance * 25;
		for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++) {
			Player p = PlayerHandler.players[i];
			if (p != null) {
				Player person = p;
				if (person != null) {
					if (person.getOutStream() != null) {
						if (person.playerAssistant.distanceToPoint(pX, pY) <= CombatConstants.OUT_OF_VIEW_DISTANCE && person.getHeight() == playerHeight) {
							person.getPA().createProjectile2(pX, pY, offX, offY, angle, speed, gfxMoving, startHeight, endHeight, lockon, time, slope);
						}
					}
				}
			}
		}
	}
	/**
	 * * GFX
	 **/
	public void stillGfx(int id, int x, int y, int height, int time) {
		if (player.bot) {
			return;
		}
		if (player.getOutStream() != null && player != null) {
			sendPosition(x, y, false);
			player.getOutStream().createFrame(4);
			player.getOutStream().writeByte(0);
			player.getOutStream().writeWord(id);
			player.getOutStream().writeByte(height);
			player.getOutStream().writeWord(time);
			player.flushOutStream();
		}
	}

	// creates gfx for everyone
	public void createPlayersStillGfx(int id, int x, int y, int height, int time) {
		player.getPA().stillGfx(id, x, y, height, time);

		for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++) {
			Player person = PlayerHandler.players[i];

			if (person != null && person != player) {
				if (person.getOutStream() != null) {
					if (person.playerAssistant.distanceToPoint(x, y) <= 25) {
						person.getPA().stillGfx(id, x, y, height, time);
					}
				}
			}
		}
	}

	/**
	 * Objects, add and remove
	 **/
	public void spawnClientObject(int objectId, int objectX, int objectY, int face, int objectType) {
		if (player.bot) {
			return;
		}
		// Packet 151 on the client handleSecondaryPacket method.
		if (player.getOutStream() != null && player != null) {
			sendPosition(objectX, objectY, false);
			player.getOutStream().createFrame(101);
			player.getOutStream().writeByteC((objectType << 2) + (face & 3));
			player.getOutStream().writeByte(0);
			player.flushOutStream();
			if (objectId != -1) { // removing
				player.getOutStream().createFrame(151);
				player.getOutStream().writeByteS(0);
				player.getOutStream().writeWordBigEndian(objectId);
				player.getOutStream().writeByteS((objectType << 2) + (face & 3));
			}
			player.flushOutStream();
		}
	}

	public void showOption(int i, int l, String s, int a) {
		if (player.bot) {
			return;
		}
		if (player.getOutStream() != null && player != null) {
			if (!player.optionType.equalsIgnoreCase(s)) {
				player.optionType = s;
				player.getOutStream().createFrameVarSize(104);
				player.getOutStream().writeByteC(i);
				player.getOutStream().writeByteA(l);
				player.getOutStream().writeString(s);
				player.getOutStream().endFrameVarSize();
				player.flushOutStream();
			}
		}
	}


	/**
	 * Private Messaging
	 **/
	public void privateMessagingLogIn() {

		// Tell client about toggle of friends tab
		setPrivateMessaging(2);

		// Load friends list
		for (int i = 0; i < player.friends.length; i++) {
			if (player.friends[i][0] != 0) {
				player.getPA().updatePM(player.friends[i][0], false);
			}
		}

		PrivateMessagingPacket.setPlayerPrivateMessageStatusForWorld(player);
	}

	public void updatePM(long masterPlayerNameLong, boolean skipIgnore) {
		Player master = Misc.getPlayerByName(Misc.longToPlayerName(masterPlayerNameLong));
		if (master == null) {
			loadPM(masterPlayerNameLong, 0);
			return;
		}
		long masterNameLong = Misc.playerNameToInt64(master.getPlayerName());
		if (master.privateChat == ServerConstants.PRIVATE_ON) {
			if (PrivateMessagingPacket.isIgnored(master, player) && !player.isModeratorRank() || skipIgnore) {
				loadPM(masterNameLong, 0);
				return;
			}
			loadPM(masterNameLong, 1);
			return;
		} else if (master.privateChat == ServerConstants.PRIVATE_FRIENDS) {
			if (master.getPA().hasFriend(Misc.playerNameToInt64(player.getPlayerName())) && !skipIgnore || player.isModeratorRank() && !skipIgnore) {
				loadPM(masterNameLong, 1);
				return;
			} else {
				loadPM(masterNameLong, 0);
				return;
			}
		} else if (master.privateChat == ServerConstants.PRIVATE_OFF) {
			// Master will only appear offline if i am not a staff member.
			if (player.isModeratorRank() && !skipIgnore) {
				if (!PrivateAdminArea.playerIsInAdminArea(master)) {
					loadPM(masterNameLong, 1);
				} else {
					loadPM(masterNameLong, 0);
				}
			} else {
				loadPM(masterNameLong, 0);
			}
		}
	}

	public boolean hasFriend(long l) {
		for (int i = 0; i < player.friends.length; i++) {
			if (player.friends[i][0] != 0) {
				if (l == player.friends[i][0]) {
					return true;
				}
			}
		}
		return false;
	}

	public void resetFollowers() {
		for (int j = 0; j < PlayerHandler.players.length; j++) {
			if (PlayerHandler.players[j] != null) {
				if (PlayerHandler.players[j].getPlayerIdToFollow() == player.getPlayerId()) {
					Follow.resetFollow(player);
				}
			}
		}
	}

	public void processTeleport() {
		regionUpdateForce(player, player.teleportToHeight);
		player.teleportToX = player.teleX;
		player.teleportToY = player.teleY;

		if (!Area.inWilderness(player.teleportToX, player.teleportToY, player.teleportToHeight)) {
			TargetSystem.leftWild(player);
		}
		if (player.teleportToHeight != 20) {
			Tournament.removeFromTournamentLobby(player.getPlayerId());
		}

		player.setHeight(player.teleportToHeight);
		if (player.teleEndAnimation > 0) {
			player.startAnimation(player.teleEndAnimation);
			player.forceSendAnimation = true;
		}
		if (player.teleEndGfx > 0) {
			player.gfx0(player.teleEndGfx);
		}

		player.teleEndAnimation = 0;
		player.teleEndGfx = 0;
		AreaInterface.updateWalkableInterfaces(player);
	}

	public void movePlayer(int x, int y, int h) {

		if (player.isAdministratorRank() && ServerConfiguration.DEBUG_MODE) {
			Misc.print("Previous location: " + player.getX() + ", " + player.getY());
		}
		Movement.resetWalkingQueue(player);
		regionUpdateForce(player, h);
		player.teleportToX = x;
		player.teleportToY = y;
		player.setHeight(h);
		if (!Area.inDangerousPvpArea(player)) {
			if (Area.inWilderness(x, y, h)) {
				RagBan.wildDebug
						.add(Misc.getDateAndTime() + " Add3: " + player.getPlayerName() + ", " + player.addressIp + ", " + player.addressUid + ", " + x + ", " + y + ", " + h + ", "
						     + player.getX() + ", " + player.getY() + ", " + player.getHeight());
				RagBan.addToWilderness(player.addressIp, player.addressUid);
			}
		}
		if (!Area.inWilderness(x, y, h)) {
			if (Area.inDangerousPvpArea(player)) {
				RagBan.wildDebug
						.add(Misc.getDateAndTime() + " Remove3: " + player.getPlayerName() + ", " + player.addressIp + ", " + player.addressUid + ", " + player.getX() + ", "
						     + player.getY() + ", " + player.getHeight() + ", " + x + ", " + y);
				RagBan.removeFromWilderness(player.addressIp, player.addressUid);
			}
		}

		if (h != 20) {
			Tournament.removeFromTournamentLobby(player.getPlayerId());
		}
		requestUpdates();
		AreaInterface.updateWalkableInterfaces(player);
		Barrows.resetCoffinStatus(player);
	}

	private void regionUpdateForce(Player player, int nextHeight) {
		if (nextHeight != player.getHeight()) {
			// Change region packet is not called when teleporting to the same height in a closer area or different height and close area.
			Server.objectManager.changeRegionPacketClientObjectUpdate(player, true);

			CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
				@Override
				public void execute(CycleEventContainer container) {
					container.stop();
				}

				@Override
				public void stop() {
					if ((System.currentTimeMillis() - player.timeReloadedItems) <= 1300) {
						return;
					}
					Server.itemHandler.showGroundItemsToSpecificPlayerOnRegionChange(player);
				}
			}, 2);
		}

	}


	/**
	 * reseting animation
	 **/
	public void resetAnimation() {
		Combat.updatePlayerStance(player);
		player.startAnimation(player.playerStandIndex);
		requestUpdates();
	}

	/**
	 * Update player appearance.
	 */
	public void requestUpdates() {
		player.setUpdateRequired(true);
		player.setAppearanceUpdateRequired(true);
	}

	/**
	 * Show an arrow icon on the selected player.
	 */

	public void drawHeadicon(int i, int j, int k, int l) {
		player.getOutStream().createFrame(254);
		player.getOutStream().writeByte(i);
		if (i == 1 || i == 10) {
			player.getOutStream().writeWord(j);
			player.getOutStream().writeWord(k);
			player.getOutStream().writeByte(l);
		} else {
			player.getOutStream().writeWord(k);
			player.getOutStream().writeWord(l);
			player.getOutStream().writeByte(j);
		}
	}

	public void flashSelectedSidebar(int i1) {
		player.getOutStream().createFrame(24);
		player.getOutStream().writeByteA(i1);
	}

	/**
	 * Select the tab.
	 *
	 * @param tab The tab identity.
	 */
	public void changeToSidebar(int tab) {
		player.getOutStream().createFrame(106);
		player.getOutStream().writeByteC(tab);
	}

	public boolean confirmMessage;

	public void getSpeared(int otherX, int otherY) {
		int x = player.getX() - otherX;
		int y = player.getY() - otherY;
		//player.forceNoClip = true;
		if (x > 0 && Region.pathUnblocked(player.getX(), player.getY(), player.getHeight(), "EAST")) {
			//player.setForceMovement(player.playerWalkIndex, 1, 0, 0, 40, 1, 1);
			Movement.travelTo(player, 1, 0);
		} else if (x < 0 && Region.pathUnblocked(player.getX(), player.getY(), player.getHeight(), "WEST")) {
			//player.setForceMovement(player.playerWalkIndex, -1, 0, 0, 40, 3, 1);
			Movement.travelTo(player, -1, 0);
		} else if (y > 0 && Region.pathUnblocked(player.getX(), player.getY(), player.getHeight(), "NORTH")) {
			//player.setForceMovement(player.playerWalkIndex, 0, 1, 0, 40, 0, 1);
			Movement.travelTo(player, 0, 1);
		} else if (y < 0 && Region.pathUnblocked(player.getX(), player.getY(), player.getHeight(), "SOUTH")) {
			//player.setForceMovement(player.playerWalkIndex, 0, -1, 0, 40, 2, 1);
			Movement.travelTo(player, 0, -1);
		}
		player.dragonSpearTicksLeft += 5;
		player.timeDragonSpearEffected = System.currentTimeMillis();
		dragonSpearEvent(player);
	}

	public enum FadingScreenState {
		/**
		 * Fading in from transparent to black.
		 */
		FADE_IN(1),

		/**
		 * Stops the fading screen if there is any.
		 */
		STOP(0),

		/**
		 * Fading out from black to transparent
		 */
		FADE_OUT(-1);

		private final int value;

		FadingScreenState(int value) {
			this.value = value;
		}
	}

	/**
	 * Creates a black screen that covers the entire 3d screen for a certain amount of seconds.
	 *
	 * @param text
	 * 			  the text to be displayed if any.
	 * @param state
	 * 			  the type of fade, -1 for black to clear, 0 to stop the fading, 1 for clear to black.
	 * @param seconds
	 * 			  the amount of seconds for the duration fo the fade.
	 * @param elongation
	 * 			  the amount of seconds for the elongation of the fade after the duration.
	 */
	public void sendFadingScreen(String text, FadingScreenState state, byte seconds, int elongation) {
		if (player == null || player.getType() == EntityType.PLAYER_BOT || player.getOutStream() == null) {
			return;
		}
		Stream out = player.getOutStream();

		out.createFrameVarSizeWord(102);
		out.writeString(text);
		out.writeByte(state.value);
		out.writeByte(seconds);
		out.writeByte(elongation);
		out.endFrameVarSizeWord();
	}

	public void updateBankSeparators(boolean show) {
		player.getOutStream().createFrame(9);
		player.getOutStream().writeByte(show ? 1 : 0);
		player.flushOutStream();
	}

	private void dragonSpearEvent(Player player2) {
		if (player.dragonSpearEvent) {
			return;
		}
		player.dragonSpearEvent = true;
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent<Object>() {
			@Override
			public void execute(CycleEventContainer container) {
				player.dragonSpearTicksLeft--;
				if (player.dragonSpearTicksLeft == 1) {
					player.dragonSpearEvent = false;
					for (int index = 0; index < player.dragonSpearEffectStack.size(); index++) {
						String string = player.dragonSpearEffectStack.get(index);
						String parse[];
						if (string.startsWith("Damage:")) {
							string = string.replace("Damage:", "");
							parse = string.split(" ");
							int damage = Integer.parseInt(parse[0]);
							int hitSplatColour = Integer.parseInt(parse[1]);
							int icon = Integer.parseInt(parse[2]);
							boolean maxHit = parse[3].equals("trues");
							player.handleHitMask(damage, hitSplatColour, icon, 0, maxHit);
							player.dealDamage(damage);
							Skilling.updateSkillTabFrontTextMain(player, ServerConstants.HITPOINTS);
						} else if (string.startsWith("Prayer:")) {
							string = string.replace("Prayer:", "");
							int reducePrayerAmount = Integer.parseInt(string);
							player.currentCombatSkillLevel[ServerConstants.PRAYER] -= reducePrayerAmount;
							if (player.getCurrentCombatSkillLevel(ServerConstants.PRAYER) <= 0) {
								player.currentCombatSkillLevel[ServerConstants.PRAYER] = 0;
								Combat.resetPrayers(player);
							}
							Skilling.updateSkillTabFrontTextMain(player, ServerConstants.PRAYER);
						}
					}
					player.dragonSpearEffectStack.clear();
				} else if (player.dragonSpearTicksLeft == 0) {
					container.stop();
				}
			}

			@Override
			public void stop() {

			}
		}, 1);

	}

	/**
	 * Reset time attacked by another player, time attacked another player, time engaged npc.
	 */
	public void resetCombatTimer() {
		player.setTimeAttackedAnotherPlayer(0);
		player.setTimeUnderAttackByAnotherPlayer(0);
		player.setTimeNpcAttackedPlayer(0);
		player.timeNpcAttackedPlayerLogOutTimer = 0;
	}

	/**
	 * Check if player is within distance of the player.
	 *
	 * @param target The player to check the distance with.
	 * @param distance The maximum distance for this method to return true.
	 * @return True, if the player is within the distance of the target.
	 */
	public boolean withinDistanceOfTargetPlayer(Player target, int distance) {
		for (int i = 0; i <= distance; i++) {
			for (int j = 0; j <= distance; j++) {
				if ((target.getX() + i) == player.getX() && ((target.getY() + j) == player.getY() || (target.getY() - j) == player.getY() || target.getY() == player.getY())) {
					return true;
				} else if ((target.getX() - i) == player.getX() && ((target.getY() + j) == player.getY() || (target.getY() - j) == player.getY()
				                                                    || target.getY() == player.getY())) {
					return true;
				} else if (target.getX() == player.getX() && ((target.getY() + j) == player.getY() || (target.getY() - j) == player.getY() || target.getY() == player.getY())) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean withInDistance(int objectX, int objectY, int playerX, int playerY, int distance) {
		for (int i = 0; i <= distance; i++) {
			for (int j = 0; j <= distance; j++) {
				if ((objectX + i) == playerX && ((objectY + j) == playerY || (objectY - j) == playerY || objectY == playerY)) {
					return true;
				} else if ((objectX - i) == playerX && ((objectY + j) == playerY || (objectY - j) == playerY || objectY == playerY)) {
					return true;
				} else if (objectX == playerX && ((objectY + j) == playerY || (objectY - j) == playerY || objectY == playerY)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Check if player is within distance of the npc.
	 *
	 * @param target The player to check the distance with.
	 * @param distance The maximum distance for this method to return true.
	 * @return True, if the player is within the distance of the target.
	 */
	public boolean withinDistanceOfTargetNpc(Npc target, int distance) {
		for (int i = 0; i <= distance; i++) {
			for (int j = 0; j <= distance; j++) {
				if ((target.getX() + i) == player.getX() && ((target.getY() + j) == player.getY() || (target.getY() - j) == player.getY() || target.getY() == player.getY())) {
					return true;
				} else if ((target.getX() - i) == player.getX() && ((target.getY() + j) == player.getY() || (target.getY() - j) == player.getY()
				                                                    || target.getY() == player.getY())) {
					return true;
				} else if (target.getX() == player.getX() && ((target.getY() + j) == player.getY() || (target.getY() - j) == player.getY() || target.getY() == player.getY())) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean withinDistance(Npc npc) {
		if (npc == null || npc.needRespawn || player.getHeight() != npc.getHeight()) {
			return false;
		}
		int deltaX = npc.getX() - player.getX();

		int deltaY = npc.getY() - player.getY();

		return deltaX <= 15 && deltaX >= -16 && deltaY <= 15 && deltaY >= -16;
	}

	public int distanceToPoint(int pointX, int pointY) {
		return (int) Math.sqrt(Math.pow(player.getX() - pointX, 2) + Math.pow(player.getY() - pointY, 2));
	}


	public boolean withinDistance(Player otherPlr) {
		if (player.getHeight() != otherPlr.getHeight()) {
			return false;
		}
		int deltaX = otherPlr.getX() - player.getX(), deltaY = otherPlr.getY() - player.getY();
		return deltaX <= 15 && deltaX >= -16 && deltaY <= 15 && deltaY >= -16;
	}

	public boolean withinDistance(int x, int y) {
		int deltaX = x - player.getX(), deltaY = y - player.getY();

		return deltaX <= 15 && deltaX >= -16 && deltaY <= 15 && deltaY >= -16;
	}

	public void stopAllActions() {
		player.flowerX = 0;
		player.flowerY = 0;
		player.flowerHeight = 0;
		player.flower = 0;
		player.itemOnNpcEvent = false;
		player.itemDestroyedId = -1;
		player.itemDestroyedSlot = -1;
		player.resetActionIdUsed();
		player.canUseTeleportInterface = false;
		player.tradingPostBuyAmount = 0;
		player.tradingPostBuyPrice = 0;
		Skilling.stopAllSkilling(player);
		player.runecraftingBankEventRunning = false;
		AgilityAssistant.stopResting(player);
		player.walkingToItem = false;
		//Do not add player.setMeleeFollow(false); or if you spam click a player with melee, it would cancel the melee follow
		player.setSpellId(-1);
		player.itemOnObjectEvent = false;
	}

	/**
	 * Change the interface of a certain tab.
	 *
	 * @param menuId The tab identity.
	 * @param form The interface to spawn in the tab.
	 */
	public void setSidebarInterface(int menuId, int form) {
		if (player.bot) {
			return;
		}
		if (menuId == 0 && player.autoCasting) {
			// form = 328;
		}
		if (player.getOutStream() != null) {
			player.getOutStream().createFrame(71);
			player.getOutStream().writeWord(form);
			player.getOutStream().writeByteA(menuId);
		}
	}

	public void sendItemToNpc(int npcIndex, int item, int slot) {
		if (player.bot) {
			return;
		}
		Stream stream = player.getOutStream();

		stream.createFrame(29);
		stream.writeWord(npcIndex);
		stream.writeWord(item);
		stream.writeByte(slot);
	}

	/**
	 * Announce a message to all players.
	 *
	 * @param message The message to announce to all players.
	 */
	public void announce(String message) {
		String text = message;
		boolean extend = false;
		if (message.contains("NEWLINE")) {
			String[] split = message.split("-NEWLINE-");
			message = split[0];
			extend = true;
			text = split[1];
		}
		if (extend) {
			for (int j = 0; j < PlayerHandler.players.length; j++) {
				if (PlayerHandler.players[j] != null) {
					Player c3 = PlayerHandler.players[j];
					c3.playerAssistant.sendMessage(ServerConstants.DARK_RED_COL + message);
					c3.playerAssistant.sendMessage(ServerConstants.DARK_RED_COL + text);
				}
			}
		} else {
			for (int j = 0; j < PlayerHandler.players.length; j++) {
				if (PlayerHandler.players[j] != null) {
					Player c3 = PlayerHandler.players[j];
					c3.playerAssistant.sendMessage(ServerConstants.DARK_RED_COL + message);
				}
			}
		}
	}

	public void sendMessageF(String message, Object... parameters) {
		sendMessage(String.format(message, parameters));
	}

	/**
	 * Send a message to the chatbox.
	 *
	 * @param message The message to send.
	 */
	public void sendMessage(String message) {
		if (player.bot) {
			return;
		}
		if (player.doNotSendMessage) {
			player.doNotSendMessage = false;
			return;
		}
		if (player.getOutStream() != null) {
			player.getOutStream().createFrameVarSize(253);
			player.getOutStream().writeString(message);
			player.getOutStream().endFrameVarSize();
		}
	}

	public void sendDelayedMessage(String message, int delay) {
		player.getEventHandler().addEvent(player, new CycleEvent<Entity>() {

			@Override
			public void execute(CycleEventContainer<Entity> container) {
				container.stop();
			}

			@Override
			public void stop() {
				sendMessage(message);
			}
		}, delay);
	}

	public void sendContainer(ItemContainer container, int interfaceId) {
		player.getOutStream().createFrameVarSizeWord(53);
		player.getOutStream().writeWord(interfaceId);
		player.getOutStream().writeWord(container.getCapacity());

		container.forAll((index, item) -> {
			int itemId = item == null ? 0 : item.getId();

			int amount = item == null ? 0 : item.getAmount();

			if (amount < 1) {
				itemId = 0;
				amount = 0;
			}
			if (amount > 254) {
				player.getOutStream().writeByte(255);
				player.getOutStream().writeDWord_v2(amount);
			} else {
				player.getOutStream().writeByte(amount);
			}
			player.getOutStream().writeWordBigEndianA(itemId + 1);
		});

		player.getOutStream().endFrameVarSizeWord();
		player.flushOutStream();
	}

	/**
	 * Send a long message that will definitely need 2 lines.
	 */
	public void sendLongMessage(String message) {
		String originalMessage = message;
		message = LoadTextWidth.shortenText(message);
		if (!message.equals(originalMessage)) {
			sendMessage(message + "-");
			originalMessage = originalMessage.replace(message, "");
			if (originalMessage.startsWith(" ")) {
				originalMessage = originalMessage.replaceFirst(" ", "");
			}
			sendMessage(originalMessage);
		} else {
			sendMessage(message);
		}
	}

	public PlayerAssistant sendShakeCamera(int xOffset, int xSpeed, int yOffset, int ySpeed) {
		if (java.util.stream.Stream.of(xOffset, xSpeed, yOffset, ySpeed).anyMatch(i -> i < 0 || i > 255)) {
			throw new IllegalArgumentException("All parameters must be within the values 0 inclusive and 255 inclusive.");
		}
		Stream stream = player.getOutStream();

		if (player.bot || stream == null) {
			return this;
		}
		stream.createFrame(35);
		stream.writeByte(xOffset);
		stream.writeByte(xSpeed);
		stream.writeByte(yOffset);
		stream.writeByte(ySpeed);

		return this;
	}

	public void sendFilterableMessage(String message) {
		if (player.bot) {
			return;
		}
		if (player.doNotSendMessage) {
			player.doNotSendMessage = false;
			return;
		}
		if (player.messageFiltered.equals("ON")) {
			return;
		}
		if (player.getOutStream() != null) {
			player.getOutStream().createFrameVarSize(253);
			player.getOutStream().writeString(message);
			player.getOutStream().endFrameVarSize();
		}
	}

	public boolean isOnTopOfTarget(Player target) {
		if (player.getX() == target.getX() && player.getY() == target.getY()) {
			return true;
		}
		return false;
	}

	public boolean isDiagonalFromTarget(Player target) {
		if (player.getX() != target.getX() && player.getY() != target.getY()) {
			return true;
		}
		return false;
	}

	public void calculateCombatLevel() {
		int magic = (int) (player.baseSkillLevel[ServerConstants.MAGIC] * 1.5);
		int ranged = (int) (player.baseSkillLevel[ServerConstants.RANGED] * 1.5);
		int attstr = (int) ((double) (player.baseSkillLevel[ServerConstants.ATTACK])
				+ (double) (player.baseSkillLevel[ServerConstants.STRENGTH]));
		player.setCombatLevel(3);
		double defence = (player.baseSkillLevel[ServerConstants.DEFENCE] * 0.25);
		double hp = (player.baseSkillLevel[ServerConstants.HITPOINTS] * 0.25);
		double prayer = (player.baseSkillLevel[ServerConstants.PRAYER] * 0.125);
		double summoning = 0.0;
		if (GameType.isPreEoc()) {
			summoning = (int) Math.floor(
					Skilling.getLevelForExperience(player.skillExperience[ServerConstants.SUMMONING])
							* 0.125);
		}
		if (ranged > attstr && magic < ranged) {
			player.setCombatLevel((int) (defence + hp + prayer + summoning
					+ (player.baseSkillLevel[ServerConstants.RANGED] * 0.4875)));
		} else if (magic > attstr) {
			player.setCombatLevel((int) (defence + hp + prayer + summoning
					+ (player.baseSkillLevel[ServerConstants.MAGIC] * 0.4875)));
		} else {
			player.setCombatLevel(
					(int) (defence + hp + prayer + summoning + (player.baseSkillLevel[ServerConstants.ATTACK] * 0.325)
							+ (player.baseSkillLevel[ServerConstants.STRENGTH] * 0.325)));
		}
	}

	/**
	 * Change colour of the text of an interface id.
	 *
	 * @param colour Hex colour code.
	 */
	public void changeTextColour(int id, int colour) {
		if (colour == -1) {
			return;
		}
		if (alreadyHasColourInFrame(colour, id)) {
			return;
		}
		player.getPA().sendMessage(":packet:textcolour " + id + " " + colour);
	}

	public void sendKillScreenshot(Player attacker, Player victim, boolean instant) {
		if (!attacker.killScreenshots) {
			return;
		}
		String name = victim.getPlayerName().toLowerCase().replaceAll(" ", "_");
		if (instant) {
			player.getPA().sendMessage(":packet:screenshot: " + name + " kill_screenshots");
			return;
		}
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				container.stop();
			}

			@Override
			public void stop() {
				player.getPA().sendMessage(":packet:screenshot: " + name + " kill_screenshots");
			}
		}, 1);
	}

	public void sendScreenshot(final String screenshotName, int tickDelay) {
		if (tickDelay == 0) {
			String screenshotName1 = screenshotName.toLowerCase();
			screenshotName1 = screenshotName1.replaceAll(" ", "_");
			player.getPA().sendMessage(":packet:screenshot: " + screenshotName1 + " game_screenshots");
			return;
		}
		// Has to be on an event or when i get a rare drop, it won't show the drop for some reason.
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				container.stop();
			}

			@Override
			public void stop() {
				String screenshotName1 = screenshotName.toLowerCase();
				screenshotName1 = screenshotName1.replaceAll(" ", "_");
				player.getPA().sendMessage(":packet:screenshot: " + screenshotName1 + " game_screenshots");
			}
		}, tickDelay);
	}

	/**
	 * True if the player can walk under the npc.
	 * Use for interacting with npcs that are used to talk to, fishing spots etc..
	 */
	public boolean canInteractWithNpc(Npc npc) {
		if (GameType.isOsrs()) {
			switch (npc.npcType) {
				// Fishing spots.
				case 3913 :
				case 3914 :
				case 3915 :
				case 635 :
				case 1506 :
				case 4082 :
				case 8059 :
				case 4712 :
					return true;
			}
		}

		// If npc is stationary behind a wall, make sure player is in the correct location.
		int[] location = ClickNpcPacket.getNpcBehindWallInteractLocation(npc);
		if (location != null) {
			if (player.getX() == location[0] && player.getY() == location[1]) {
				return true;
			}
		}
		return Region.isStraightPathUnblocked(player.getX(), player.getY(), npc.getVisualX(), npc.getVisualY(), player.getHeight(), 1, 1, false);
	}

	public void quickChat(String string) {
		if (player.secondsBeenOnline < 30) {
			player.getPA().sendMessage("You cannot talk for 30 seconds after joining.");
			return;
		}
		if (Mute.isMuted(player)) {
			return;
		}
		String text = string;
		player.forcedChat(text, true, true);
	}

	/**
	 * When an Admin logs in, they will receive a special message.
	 */
	public void adminPrivateLogInNotification() {
		if (!player.getPlayerName().equals("Mgt Madness")) {
			return;
		}
		player.getPA()
				.sendMessage(ServerConstants.PURPLE_COL + "Osrs in inventory: " + DonationManager.getOsrsInInventory() + ", Osrs rates: " + DonationManager.OSRS_DONATION_MULTIPLIER + "$");
		if (player.tank) {
			player.getPA().sendMessage(ServerConstants.RED_COL + "Tank mode is on.");
		}

	}

	public boolean alreadyHasSendFrame36(int id, int state) {
		if (sendFrame36Config.containsKey(id)) {
			SendFrame36ConfigStore t = sendFrame36Config.get(id);
			if (state == t.state) {
				return true;
			}
			t.state = state;
		} else {
			sendFrame36Config.put(id, new SendFrame36ConfigStore(id, state));
		}
		return false;
	}

	private Map<Integer, SendFrame36ConfigStore> sendFrame36Config = new HashMap<Integer, SendFrame36ConfigStore>();


	public class SendFrame36ConfigStore {
		public int state;

		public int id;

		public SendFrame36ConfigStore(int id, int state) {
			this.id = id;
			this.state = state;
		}

	}

	public boolean alreadyHasSendFrame34a(int frame, int item, int slot, int amount) {
		// Pet mystery box item interface id.
		if (frame == 29216) {
			return false;
		}
		if (sendFrame34aConfig.containsKey(frame)) {
			SendFrame34aConfigStore t = sendFrame34aConfig.get(frame);
			if (frame == t.frame) {
				if (item == t.item && slot == t.slot && amount == t.amount) {
					return true;
				}
			}
			t.frame = frame;
			t.item = item;
			t.slot = slot;
			t.amount = amount;
		} else {
			sendFrame34aConfig.put(frame, new SendFrame34aConfigStore(frame, item, slot, amount));
		}
		return false;
	}



	private Map<Integer, SendFrame34aConfigStore> sendFrame34aConfig = new HashMap<Integer, SendFrame34aConfigStore>();


	public class SendFrame34aConfigStore {
		public int frame;

		public int item;

		public int slot;

		public int amount;

		public SendFrame34aConfigStore(int frame, int item, int slot, int amount) {
			this.frame = frame;
			this.item = item;
			this.slot = slot;
			this.amount = amount;
		}

	}

	public void forceToLogInScreen() {
		if (player.getOutStream() == null) {
			return;
		}
		if (player == null) {
			return;
		}
		player.getOutStream().createFrame(109);
		player.canFlush = true;
		player.flushOutStream();
		player.canFlush = false;
	}


	/**
	 * True if the player recently created his account.
	 */
	public boolean isNewPlayerEco() {
		if (player.isCombatBot()) {
			return false;
		}
		if (player.secondsBeenOnline < 360 && GameType.isOsrsEco()) {
			if (GameMode.getGameMode(player, "PKER")) {
				return false;
			}
			return true;
		}
		return false;
	}
}
