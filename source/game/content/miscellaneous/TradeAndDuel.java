package game.content.miscellaneous;

import core.GameType;
import core.Server;
import core.ServerConstants;
import game.content.clanchat.ClanChatHandler;
import game.content.combat.Combat;
import game.content.combat.CombatInterface;
import game.content.combat.EdgeAndWestsRule;
import game.content.combat.Poison;
import game.content.interfaces.InterfaceAssistant;
import game.content.quicksetup.QuickSetUp;
import game.content.skilling.Skilling;
import game.content.starter.GameMode;
import game.content.worldevent.BloodKey;
import game.entity.EntityType;
import game.item.GameItem;
import game.item.ItemAssistant;
import game.item.ItemDefinition;
import game.object.clip.Region;
import game.player.Area;
import game.player.Player;
import game.player.PlayerHandler;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import game.player.movement.Follow;
import game.player.movement.Movement;
import game.player.pet.PlayerPet;
import game.player.punishment.DuelArenaBan;
import java.util.concurrent.CopyOnWriteArrayList;
import utility.Misc;


public class TradeAndDuel {

	public final static int MAXIMUM_BANK_PERCENTAGE_STAKE = 60;

	public final static int LOWEST_BANK_PERCENTAGE_STAKE = 5;

	public final static int NO_FF = 0;

	public final static int NO_MOVEMENT = 1;

	public final static int NO_RANGED = 2;

	public final static int NO_MELEE = 3;

	public final static int NO_MAGIC = 4;

	public final static int NO_DRINK = 5;

	public final static int NO_FOOD = 6;

	public final static int NO_PRAYER = 7;

	public final static int OBSTACLES = 8;

	public final static int ANTI_SCAM = 9;

	public final static int NO_SPECIAL_ATTACK = 10;

	public final static int NO_HELM = 11;

	public final static int NO_CAPE = 12;

	public final static int NO_AMULET = 13;

	public final static int NO_WEAPON = 14;

	public final static int NO_BODY = 15;

	public final static int NO_SHIELD = 16;

	public final static int NO_LEGS = 17;

	public final static int NO_GLOVES = 18;

	public final static int NO_BOOTS = 19;

	public final static int NO_RING = 20;

	public final static int NO_ARROWS = 21;

	//@formatter:off
	@SuppressWarnings("unused")
	private final static String[] ruleNames =
			{
					"NO FF", "NO MOVEMENT", "NO RANGED", "NO MELEE", "NO MAGIC", "NO DRINK", "NO FOOD", "NO PRAYER",
					"OBSTACLES", "NO FUN WEAPON", "NO SPECIAL ATTACK",
					"HELM", "CAPE", "AMULET", "WEAPON", "BODY", "LEGS", "GLOVES", "BOOTS", "RING", "ARROWS"
			};

	//@formatter:on
	private Player player;

	public TradeAndDuel(Player Player) {
		this.player = Player;
	}

	public CopyOnWriteArrayList<GameItem> offeredItems = new CopyOnWriteArrayList<GameItem>();

	/**
	 * Will reset to 0 once the claim interface has been closed.
	 */
	public final static int THE_INSTANT_ENEMY_DIED_IN_DUEL = 6;

	public Player getPartnerDuel() {
		Player partner = PlayerHandler.players[player.getDuelingWith()];
		if (partner != null) {
			if (!partner.getPlayerName().equals(player.getLastDueledWithName()) || !partner.getLastDueledWithName().equals(player.getPlayerName())) {
				partner = null;
			}
		}
		return partner;
	}

	public Player getPartnerTrade() {
		Player partner = PlayerHandler.players[player.getTradingWithId()];
		if (partner != null) {
			if (!partner.getPlayerName().equals(player.lastTradedWithName) || !partner.lastTradedWithName.equals(player.getPlayerName())) {
				partner = null;
			}
		}
		return partner;
	}

	public void tradeRequestChatbox(int id) {
		if (id <= 0) {
			return;
		}
		if (id > ServerConstants.MAXIMUM_PLAYERS) {
			return;
		}
		final Player other = PlayerHandler.players[id];
		if (other == null) {
			return;
		}
		player.hasLastAttackedAPlayer = false;

		if (player.findOtherPlayerId > 0) {
			return;
		}
		if (player.doingAnAction()) {
			return;
		}
		player.setPlayerIdToFollow(id);
		player.setMeleeFollow(true);
		player.findOtherPlayerId = 20;
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				if (player.findOtherPlayerId > 0) {
					player.findOtherPlayerId--;
					if (player.getPA().withinDistanceOfTargetPlayer(other, 1)) {
						requestTrade(other.getPlayerId());
						container.stop();
					}
				} else {
					container.stop();
				}
			}

			@Override
			public void stop() {
				player.findOtherPlayerId = 0;
			}
		}, 1);
	}

	public void requestTrade(int id) {

		Player partner = PlayerHandler.players[id];
		if (partner == null) {
			return;
		}
		if (id == player.getPlayerId()) {
			return;
		}
		if (System.currentTimeMillis() < player.timeCanTradeAndDrop) {
			player.getPA().sendMessage("You are unable to trade.");
			return;
		}
		if (!player.playerAssistant.withinDistance(partner)) {
			return;
		}
		if (partner.getPA().playerIsBusy()) {
			player.turnPlayerTo(partner.getX(), partner.getY());
			player.getPA().sendMessage(partner.getPlayerName() + " is busy.");
			Combat.resetPlayerAttack(player);
			return;
		}
		if (partner.getHeight() != player.getHeight()) {
			return;
		}
		if (player.getType() == EntityType.PLAYER_PET) {
			return;
		}
		if (player.getHeight() == 20) {
			return;
		}
		if (!Region.isStraightPathUnblocked(player.getX(), player.getY(), partner.getX(), partner.getY(), player.getHeight(), 1, 1, false)) {
			return;
		}
		if (Area.inDiceZone(player) && !ClanChatHandler.inDiceCc(player, false, false)) {
			player.getPA().sendMessage("You must join the 'Dice' clan chat.");
			return;
		}
		if (player.isJailed()) {
			player.getPA().sendMessage("You cannot do this while jailed.");
			return;
		}
		if (partner.getType() == EntityType.PLAYER_PET) {
			if (Area.inDangerousPvpArea(player)) {
				return;
			}
			if (Area.inAreaWhereItemsGetDeletedUponExit(player)) {
				return;
			}
			PlayerPet pet = (PlayerPet) partner;
			if (player.getPlayerPet() != pet || player.getNameAsLong() != pet.getOwner().getNameAsLong()) {
				player.getPA().sendMessageF("%s is not your pet to talk to.", pet.getDisplayName());
				return;
			}
			Follow.resetFollow(player);
			player.turnPlayerTo(pet.getX(), pet.getY());
			player.setDialogueChainAndStart(pet.talk());
			return;
		}
		if (ClanChatHandler.inDiceCc(player, false, false) && ClanChatHandler.inDiceCc(partner, false, false)) {
			int wealth = 0;
			for (int index = 0; index < partner.playerItems.length; index++) {
				int itemId = partner.playerItems[index] - 1;
				if (itemId <= 0) {
					continue;
				}
				wealth += ServerConstants.getItemValue(itemId) * partner.playerItemsN[index];
			}
			ClanChatHandler.sendDiceClanMessage(partner.getPlayerName(), partner.getClanId(),
			                                    "I have " + Misc.formatRunescapeStyle(wealth) + " in my inventory & " + Misc.formatRunescapeStyle(
					                                    DiceSystem.getPlayerVaultAmount(partner.getPlayerName())) + " in my deposit vault.");
		}
		player.resetPlayerIdToFollow();
		player.turnPlayerTo(partner.getX(), partner.getY());
		player.setTradeWith(id);
		player.lastTradedWithName = partner.getPlayerName();
		if (!player.isInTrade() && partner.tradeRequested && partner.getTradingWithId() == player.getPlayerId() && partner.lastTradedWithName.equals(player.getPlayerName())) {
			player.getTradeAndDuel().openTrade();
			partner.getTradeAndDuel().openTrade();
		} else if (!player.isInTrade()) {
			player.tradeRequested = true;
			player.playerAssistant.sendMessage("Sending trade request to " + partner.getCapitalizedName() + ".");
			player.faceUpdate(partner.getPlayerId() + 32768);
			player.setFaceResetAtEndOfTick(true);
			partner.playerAssistant.sendMessage(player.getCapitalizedName() + ":tradereq:");
		}
	}

	public void openTrade() {
		Player o = getPartnerTrade();

		if (o == null) {
			return;
		}
		player.setInTrade(true);
		player.canOffer = true;
		player.setTradeStatus(1);
		player.tradeRequested = false;
		ItemAssistant.resetItems(player, 3322);
		resetTItems(3415);
		resetOTItems(3416);
		String out = o.getPlayerName();
		if (o.isAdministratorRank()) {
			out = "@cr2@" + out;
		} else if (o.isModeratorRank()) {
			out = "@cr1@" + out;
		}
		player.getPA().sendFrame126(o.getPlayerName(), 21350);
		player.getPA().sendFrame126("has " + ItemAssistant.getFreeInventorySlots(o) + " free", 21351);
		player.getPA().sendFrame126("Trading with: " + o.getCapitalizedName(), 3417);
		player.getPA().sendFrame126("", 3431);
		player.getPA().sendFrame126("Are you sure you want to make this trade with " + o.getCapitalizedName() + "?", 3535);
		player.getPA().sendFrame248(3323, 3321);
		player.getPA().sendFrame126("0 " + ServerConstants.getMainCurrencyName().toLowerCase() + ".", 21353);
		player.getPA().sendFrame126("0 " + ServerConstants.getMainCurrencyName().toLowerCase() + ".", 21354);
		player.tradeOfferedWealth = 0;
		player.tradePossibleScam = false;
		player.tradeScamTime = 0;
	}



	public void resetTItems(int WriteFrame) {
		player.getOutStream().createFrameVarSizeWord(53);
		player.getOutStream().writeWord(WriteFrame);
		int len = offeredItems.toArray().length;
		int current = 0;
		player.getOutStream().writeWord(len);
		for (GameItem item : offeredItems) {
			if (item.getAmount() > 254) {
				player.getOutStream().writeByte(255);
				player.getOutStream().writeDWord_v2(item.getAmount());
			} else {
				player.getOutStream().writeByte(item.getAmount());
			}
			player.getOutStream().writeWordBigEndianA(item.getId() + 1);
			current++;
		}
		if (current < 27) {
			for (int i = current; i < 28; i++) {
				player.getOutStream().writeByte(1);
				player.getOutStream().writeWordBigEndianA(-1);
			}
		}
		player.getOutStream().endFrameVarSizeWord();
		player.flushOutStream();
	}

	public boolean fromTrade(int itemID, int fromSlot, int amount) {
		Player other = getPartnerTrade();
		if (other == null) {
			return false;
		}
		try {
			if (!player.isInTrade() || !player.canOffer) {
				declineTrade1(true);
				return false;
			}
			if (amount <= 0) {
				return false;
			}
			if (other.getTradingWithId() != player.getPlayerId()) {
				player.getTradeAndDuel().declineTrade1(true);
				return false;
			}
			player.tradeConfirmed = false;
			other.tradeConfirmed = false;
			if (!ItemDefinition.getDefinitions()[itemID].stackable) {
				if (amount > 28) {
					amount = 28;
				}
				for (int a = 0; a < amount; a++) {
					for (GameItem item : offeredItems) {
						if (item.getId() == itemID) {
							if (!item.isStackable()) {
								offeredItems.remove(item);
								ItemAssistant.addItem(player, itemID, 1);
							} else if (item.getAmount() > amount) {
								offeredItems.set(offeredItems.indexOf(item), new GameItem(item.getId(), item.getAmount() - amount));
								//item.setAmount(item.getAmount() - amount);
								ItemAssistant.addItem(player, itemID, amount);
							} else {
								amount = item.getAmount();
								offeredItems.remove(item);
								ItemAssistant.addItem(player, itemID, amount);
							}
							break;
						}
					}
				}
			}
			for (GameItem item : offeredItems) {
				if (item.getId() == itemID) {
					if (!item.isStackable()) {
					} else if (item.getAmount() > amount) {
						offeredItems.set(offeredItems.indexOf(item), new GameItem(item.getId(), item.getAmount() - amount));
						//item.setAmount(item.getAmount() - amount);
						ItemAssistant.addItem(player, itemID, amount);
					} else {
						amount = item.getAmount();
						offeredItems.remove(item);
						ItemAssistant.addItem(player, itemID, amount);

					}
					break;
				}
			}

			bloodMoneyWorth(player, other);

			player.tradeConfirmed = false;
			other.tradeConfirmed = false;
			ItemAssistant.resetItems(player, 3322);
			resetTItems(3415);
			other.getTradeAndDuel().resetOTItems(3416);
			player.getPA().sendFrame126("", 3431);
			other.getPA().sendFrame126("", 3431);

			other.getPA().sendFrame126(player.getPlayerName(), 21350);
			other.getPA().sendFrame126("has " + ItemAssistant.getFreeInventorySlots(player) + " free", 21351);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	private void bloodMoneyWorth(Player player, Player o) {
		int totalBloodMoneyWorth = 0;
		for (GameItem item : offeredItems) {
			totalBloodMoneyWorth += ServerConstants.getItemValue(item.getId()) * item.getAmount();
		}
		if (totalBloodMoneyWorth < player.tradeOfferedWealth) {
			player.tradePossibleScam = true;
		}
		player.tradeOfferedWealth = totalBloodMoneyWorth;
		player.getPA().sendFrame126(Misc.formatNumber(totalBloodMoneyWorth) + " " + ServerConstants.getMainCurrencyName().toLowerCase() + ".", 21353); // my amount
		o.getPA().sendFrame126(Misc.formatNumber(totalBloodMoneyWorth) + " " + ServerConstants.getMainCurrencyName().toLowerCase() + ".", 21354); // send my amount to other player

	}

	public boolean tradeItem(int itemId, int fromSlot, int amount) {
		if (ItemAssistant.isNulledSlot(fromSlot)) {
			return false;
		}
		if (!ItemAssistant.playerHasItem(player, itemId, 1, fromSlot)) {
			return false;
		}
		Player other = getPartnerTrade();
		if (other == null) {
			return false;
		}

		if (other.getTradingWithId() != player.getPlayerId()) {
			player.getTradeAndDuel().declineTrade1(true);
			return false;
		}
		if (other.getType() == EntityType.PLAYER_PET || player.getType() == EntityType.PLAYER_PET) {
			return false;
		}
		int baseAmount = ItemDefinition.getDefinitions()[itemId].stackable ? ItemAssistant.getItemAmount(player, itemId, fromSlot) : ItemAssistant.getItemAmount(player, itemId);
		if (amount > baseAmount) {
			amount = baseAmount;
		}
		if (amount <= 0) {
			return false;
		}


		// Hand cannon and hand cannon noted.
		if (itemId == 15241 || itemId == 15242) {
			player.getPA().sendMessage("This item is useless.");
			return false;
		}
		// Blood key.
		if (BloodKey.isAnyBloodKey(itemId)) {
			if (Area.inDangerousPvpArea(player)) {
				player.getPA().sendMessage("You are unable to trade this key in the wilderness.");
				return false;
			}
		}

		if (ItemAssistant.cannotTradeAndStakeItemItem(itemId)) {
			player.getPA().sendMessage("This item is untradeable.");
			return false;
		}
		if (itemId == 21816) //Bracelet of ethereum
		{
			player.getPA().sendMessage("This item is untradeable.");
			return false;
		}
		// Skill capes and hoods.
		if (itemId >= 9747 && itemId <= 9812) {
			player.getPA().sendMessage("This item is untradeable.");
			return false;
		}
		if (DiceSystem.preventTradeScam(player, other, itemId, amount)) {
			return false;
		}
		if (player.getPA().isNewPlayerEco()) {
			player.getPA().sendMessage("You cannot trade items as a new player.");
			return false;
		}

		if (itemId == 995 && GameType.isOsrsPvp()) {
			return false;
		}
		player.tradeConfirmed = false;
		other.tradeConfirmed = false;
		if (!ItemDefinition.getDefinitions()[itemId].stackable && !ItemDefinition.getDefinitions()[itemId].note) {
			for (int a = 0; a < amount; a++) {
				if (ItemAssistant.hasItemAmountInInventory(player, itemId, 1)) {
					offeredItems.add(new GameItem(itemId, 1));
					ItemAssistant.deleteItemFromInventory(player, itemId, ItemAssistant.getItemSlot(player, itemId), 1);
					other.getPA().sendFrame126(player.getPlayerName(), 21350);
					other.getPA().sendFrame126("has " + ItemAssistant.getFreeInventorySlots(player) + " free", 21351);
				}
			}


			bloodMoneyWorth(player, other);

			other.getPA().sendFrame126(player.getPlayerName(), 21350);
			other.getPA().sendFrame126("has " + ItemAssistant.getFreeInventorySlots(player) + " free", 21351);
			ItemAssistant.resetItems(player, 3322);
			resetTItems(3415);
			other.getTradeAndDuel().resetOTItems(3416);
			player.getPA().sendFrame126("", 3431);
			other.getPA().sendFrame126("", 3431);
		}
		if (ItemAssistant.getItemAmount(player, itemId) < amount) {
			amount = ItemAssistant.getItemAmount(player, itemId);
			if (amount == 0) {
				return false;
			}
		}
		if (!player.isInTrade() || !player.canOffer) {
			declineTrade1(true);
			return false;
		}
		if (!ItemAssistant.hasItemAmountInInventory(player, itemId, amount)) {
			return false;
		}

		if (ItemDefinition.getDefinitions()[itemId].stackable || ItemDefinition.getDefinitions()[itemId].note) {
			boolean inTrade = false;
			for (GameItem item : offeredItems) {
				if (item.getId() == itemId) {
					inTrade = true;
					offeredItems.set(offeredItems.indexOf(item), new GameItem(item.getId(), item.getAmount() + amount));
					ItemAssistant.deleteItemFromInventory(player, itemId, fromSlot, amount);
					other.getPA().sendFrame126(player.getPlayerName(), 21350);
					other.getPA().sendFrame126("has " + ItemAssistant.getFreeInventorySlots(player) + " free", 21351);
					break;
				}
			}

			if (!inTrade) {
				offeredItems.add(new GameItem(itemId, amount));
				ItemAssistant.deleteItemFromInventory(player, itemId, fromSlot, amount);
				other.getPA().sendFrame126(player.getPlayerName(), 21350);
				other.getPA().sendFrame126("has " + ItemAssistant.getFreeInventorySlots(player) + " free", 21351);
			}
		}


		bloodMoneyWorth(player, other);

		other.getPA().sendFrame126(player.getPlayerName(), 21350);
		other.getPA().sendFrame126("has " + ItemAssistant.getFreeInventorySlots(player) + " free", 21351);
		ItemAssistant.resetItems(player, 3322);
		resetTItems(3415);
		other.getTradeAndDuel().resetOTItems(3416);
		player.getPA().sendFrame126("", 3431);
		other.getPA().sendFrame126("", 3431);
		return true;
	}

	public void tradeResetRequired() {
		Player o = getPartnerTrade();
		if (o != null) {
			if (o.tradeResetNeeded) {
				player.getTradeAndDuel().resetTrade();
				o.getTradeAndDuel().resetTrade();
			}
		}
	}

	public void resetTrade() {
		offeredItems.clear();
		player.setInTrade(false);
		player.setTradeStatus(0);
		player.setTradeWith(0);
		player.lastTradedWithName = "";
		player.canOffer = true;
		player.tradeConfirmed = false;
		player.tradeConfirmed2 = false;
		player.acceptedTrade = false;
		player.getPA().closeInterfaces(true);
		player.tradeResetNeeded = false;
		player.getPA().sendFrame126("Are you sure you want to make this trade?", 3535);
	}

	public void declineTrade1(boolean alert) {
		if (!player.isInTrade()) {
			return;
		}
		player.setTradeStatus(0);
		declineTrade(alert);
	}


	public void declineTrade(boolean tellOther) {
		// Close interface. Cannot use player.getPA().closeInterfaces.
		if (!player.bot) {
			if (player.getOutStream() != null && player != null) {
				player.hasDialogueOptionOpened = false;
				player.getOutStream().createFrame(219);
				player.flushOutStream();
			}
		}
		Player o = getPartnerTrade();
		if (o == null) {
			return;
		}

		o.resetFaceUpdate();
		player.resetFaceUpdate();

		if (tellOther) {
			if (!o.ignoreTradeMessage && !player.ignoreTradeMessage) {
				if (o.isInTrade()) {
					o.getPA().sendMessage(player.getPlayerName() + " has declined the trade.");
					player.getPA().sendMessage("You have declined the trade.");
				}
			}
			o.getTradeAndDuel().declineTrade(false);
			o.getTradeAndDuel().player.getPA().closeInterfaces(true);
		}

		for (GameItem item : offeredItems) {
			if (item.getAmount() < 1) {
				continue;
			}
			if (item.isStackable()) {
				ItemAssistant.addItem(player, item.getId(), item.getAmount());
			} else {
				for (int i = 0; i < item.getAmount(); i++) {
					ItemAssistant.addItem(player, item.getId(), 1);
				}
			}
		}
		player.canOffer = true;
		player.tradeConfirmed = false;
		player.tradeConfirmed2 = false;
		offeredItems.clear();
		player.setTradeStatus(0);
		player.setInTrade(false);
		player.setTradeWith(0);
		player.lastTradedWithName = "";
	}


	public void resetOTItems(int WriteFrame) {
		Player o = getPartnerTrade();
		if (o == null) {
			return;
		}
		player.getOutStream().createFrameVarSizeWord(53);
		player.getOutStream().writeWord(WriteFrame);
		int len = o.getTradeAndDuel().offeredItems.toArray().length;
		int current = 0;
		player.getOutStream().writeWord(len);
		for (GameItem item : o.getTradeAndDuel().offeredItems) {
			if (item.getAmount() > 254) {
				player.getOutStream().writeByte(255); // item's stack count. if over 254, write byte 255
				player.getOutStream().writeDWord_v2(item.getAmount());
			} else {
				player.getOutStream().writeByte(item.getAmount());
			}
			player.getOutStream().writeWordBigEndianA(item.getId() + 1); // item id
			current++;
		}
		if (current < 27) {
			for (int i = current; i < 28; i++) {
				player.getOutStream().writeByte(1);
				player.getOutStream().writeWordBigEndianA(-1);
			}
		}
		player.getOutStream().endFrameVarSizeWord();
		player.flushOutStream();
	}


	public void confirmScreen() {
		Player o = getPartnerTrade();
		if (o == null) {
			return;
		}
		player.canOffer = false;
		player.setInventoryUpdate(true);
		String SendTrade = "@red@Absolutely nothing!";
		String SendAmount = "";
		int Count = 0;
		for (GameItem item : offeredItems) {
			if (item.getId() > 0) {
				if (item.getAmount() >= 1000 && item.getAmount() < 1000000) {
					SendAmount = "@cya@" + (item.getAmount() / 1000) + "K @whi@(" + Misc.formatNumber(item.getAmount()) + ")";
				} else if (item.getAmount() >= 1000000) {
					SendAmount = "@gre@" + (item.getAmount() / 1000000) + " million @whi@(" + Misc.formatNumber(item.getAmount()) + ")";
				} else {
					SendAmount = "" + Misc.formatNumber(item.getAmount());
				}

				if (Count == 0) {
					SendTrade = ItemAssistant.getItemName(item.getId());
				} else {
					SendTrade = SendTrade + "\\n" + ItemAssistant.getItemName(item.getId());
				}

				if (item.isStackable()) {
					SendTrade = SendTrade + " x " + SendAmount;
				}
				Count++;
			}
		}

		player.getPA().sendFrame126(SendTrade, 3557);
		SendTrade = "@red@Absolutely nothing!";
		SendAmount = "";
		Count = 0;

		for (GameItem item : o.getTradeAndDuel().offeredItems) {
			if (item.getId() > 0) {
				if (item.getAmount() >= 1000 && item.getAmount() < 1000000) {
					SendAmount = "@cya@" + (item.getAmount() / 1000) + "K @whi@(" + Misc.formatNumber(item.getAmount()) + ")";
				} else if (item.getAmount() >= 1000000) {
					SendAmount = "@gre@" + (item.getAmount() / 1000000) + " million @whi@(" + Misc.formatNumber(item.getAmount()) + ")";
				} else {
					SendAmount = "" + Misc.formatNumber(item.getAmount());
				}
				if (Count == 0) {
					SendTrade = ItemAssistant.getItemName(item.getId());
				} else {
					SendTrade = SendTrade + "\\n" + ItemAssistant.getItemName(item.getId());
				}
				if (item.isStackable()) {
					SendTrade = SendTrade + " x " + SendAmount;
				}
				Count++;
			}
		}
		player.getPA().sendFrame126(SendTrade, 3558);
		player.getPA().sendFrame248(3443, 197);
		o.getPA().sendFrame126(player.getPlayerName(), 3451);
		player.getPA().sendFrame126(o.getPlayerName(), 3451);
		if (player.tradePossibleScam) {
			player.tradeScamTime = System.currentTimeMillis();
		}
	}


	public void giveItems(Player other) {
		if (other == null) {
			return;
		}
		try {
			if (System.currentTimeMillis() - player.tradeDelay < 1300) {
				return;
			}
			player.tradeDelay = System.currentTimeMillis();
			for (GameItem item : other.getTradeAndDuel().offeredItems) {
				if (item.getId() > 0) {
					ItemAssistant.addItem(player, item.getId(), item.getAmount());
				}
			}
			player.tradesCompleted++;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public CopyOnWriteArrayList<GameItem> otherStakedItems = new CopyOnWriteArrayList<GameItem>();

	public CopyOnWriteArrayList<GameItem> myStakedItems = new CopyOnWriteArrayList<GameItem>();

	public void requestDuel(int id) {
		try {
			if (id == player.getPlayerId()) {
				return;
			}
			if (player.getDuelStatus() >= 1) {
				return;
			}
			if (player.isJailed()) {
				player.getPA().sendMessage("You cannot do this while jailed.");
				return;
			}
			player.resetPlayerIdToFollow();
			resetDuel();
			resetDuelItems();
			Player partner = PlayerHandler.players[id];
			if (partner == null) {
				return;
			}

			if (partner.getHeight() != player.getHeight()) {
				return;
			}

			if (player.getType() == EntityType.PLAYER_PET || partner.getType() == EntityType.PLAYER_PET) {
				return;
			}
			if (player.getHeight() == 20) {
				return;
			}
			if (!player.playerAssistant.withinDistance(partner)) {
				return;
			}
			if (!Region.isStraightPathUnblocked(player.getX(), player.getY(), partner.getX(), partner.getY(), player.getHeight(), 1, 1, false)) {
				return;
			}
			if (partner.getPA().playerIsBusy()) {
				player.turnPlayerTo(partner.getX(), partner.getY());
				player.getPA().sendMessage(partner.getPlayerName() + " is busy.");
				Combat.resetPlayerAttack(player);
				return;
			}
			if (player.getPA().isOnTopOfTarget(partner)) {
				Movement.movePlayerFromUnderEntity(player);
			}
			player.setDuelingWith(id);
			player.setLastDueledWithName(partner.getPlayerName());
			player.duelRequested = true;
			if (player.getDuelStatus() == 0 && partner.getDuelStatus() == 0 && player.duelRequested && partner.duelRequested && player.getDuelingWith() == partner.getPlayerId()
			    && partner.getDuelingWith() == player.getPlayerId()) {
				if (player.playerAssistant.withInDistance(player.getX(), player.getY(), partner.getX(), partner.getY(), 1)) {
					player.getTradeAndDuel().openDuel();
					partner.getTradeAndDuel().openDuel();
				} else {
					player.playerAssistant.sendMessage("You need to get closer to your opponent to start the duel.");
				}

			} else {
				player.playerAssistant.sendMessage("Sending duel request...");
				player.turnPlayerTo(partner.getX(), partner.getY());
				partner.playerAssistant.sendMessage(player.getCapitalizedName() + ":duelreq:");
			}
		} catch (Exception e) {
			e.printStackTrace();
			Misc.print("Error requesting duel.");
		}
	}

	public void openDuel() {
		Player o = player.getTradeAndDuel().getPartnerDuel();
		if (o == null) {
			return;
		}
		player.setDuelStatus(1);
		refreshDuelRules();
		refreshDuelScreen();
		player.canOffer = true;
		for (int i = 0; i < player.playerEquipment.length; i++) {
			sendDuelEquipment(player.playerEquipment[i], player.playerEquipmentN[i], i);
		}
		player.getPA().sendFrame126("Dueling with: " + o.getCapitalizedName() + " (level-" + o.getCombatLevel() + ")", 6671);
		player.getPA().sendFrame126("", 6684);
		player.getPA().sendFrame248(6575, 3321);
		ItemAssistant.resetItems(player, 3322);
		player.wealthBeforeStake = ItemAssistant.getAccountBankValueLong(player);
	}

	public void sendDuelEquipment(int itemId, int amount, int slot) {
		if (itemId != 0) {
			player.getPA().sendFrame34(13824, itemId, slot, amount);
		}
	}

	public void refreshDuelRules() {
		for (int i = 0; i < player.duelRule.length; i++) {
			player.duelRule[i] = false;
		}
		player.getPA().sendFrame87(286, 0);
		player.duelOptionFrameId = 0;
	}

	public void refreshDuelScreen() {
		Player o = player.getTradeAndDuel().getPartnerDuel();
		if (o == null) {
			return;
		}
		player.getOutStream().createFrameVarSizeWord(53);
		player.getOutStream().writeWord(6669);
		player.getOutStream().writeWord(myStakedItems.toArray().length);
		int current = 0;
		for (GameItem item : myStakedItems) {
			int id = item.getId();

			int amount = item.getAmount();

			if (item.getAmount() > 254) {
				player.getOutStream().writeByte(255);
				player.getOutStream().writeDWord_v2(amount);
			} else {
				player.getOutStream().writeByte(amount);
			}
			if (item.getId() > ServerConstants.MAX_ITEM_ID || item.getId() < 0) {
				id = ServerConstants.MAX_ITEM_ID; // this might need to be 0
			}
			player.getOutStream().writeWordBigEndianA(id + 1);

			current++;
		}

		if (current < 27) {
			for (int i = current; i < 28; i++) {
				player.getOutStream().writeByte(1);
				player.getOutStream().writeWordBigEndianA(-1);
			}
		}
		player.getOutStream().endFrameVarSizeWord();
		player.flushOutStream();

		player.getOutStream().createFrameVarSizeWord(53);
		player.getOutStream().writeWord(6670);
		player.getOutStream().writeWord(o.getTradeAndDuel().myStakedItems.toArray().length);
		current = 0;
		for (GameItem item : o.getTradeAndDuel().myStakedItems) {
			int id = item.getId();

			int amount = item.getAmount();

			if (item.getAmount() > 254) {
				player.getOutStream().writeByte(255);
				player.getOutStream().writeDWord_v2(item.getAmount());
			} else {
				player.getOutStream().writeByte(item.getAmount());
			}
			if (item.getId() > ServerConstants.MAX_ITEM_ID || item.getId() < 0) {
				id = ServerConstants.MAX_ITEM_ID; // should this be 0?
			}
			player.getOutStream().writeWordBigEndianA(item.getId() + 1);
			current++;
		}

		if (current < 27) {
			for (int i = current; i < 28; i++) {
				player.getOutStream().writeByte(1);
				player.getOutStream().writeWordBigEndianA(-1);
			}
		}
		player.getOutStream().endFrameVarSizeWord();
		player.flushOutStream();
	}


	public boolean stakeItem(int itemId, int fromSlot, int amount) {
		if (ItemAssistant.isNulledSlot(fromSlot)) {
			return false;
		}
		int amountStock = ItemDefinition.getDefinitions()[itemId].stackable ? ItemAssistant.getItemAmount(player, itemId, fromSlot) : ItemAssistant.getItemAmount(player, itemId);

		if (amountStock < amount) {
			amount = amountStock;
		}
		if (amount <= 0) {
			return false;
		}

		if (!ItemAssistant.playerHasItem(player, itemId, amount, fromSlot)) {
			return false;
		}
		if (amount <= 0) {
			return false;
		}
		Player partner = player.getTradeAndDuel().getPartnerDuel();
		if (partner == null) {
			declineDuel(false);
			return false;
		}
		if (player.getType() == EntityType.PLAYER_PET || partner.getType() == EntityType.PLAYER_PET) {
			declineDuel(false);
			partner.getTradeAndDuel().declineDuel(false);
			return false;
		}
		if (partner.getDuelStatus() <= 0 || player.getDuelStatus() <= 0) {
			declineDuel(false);
			partner.getTradeAndDuel().declineDuel(false);
			return false;
		}
		// Skill capes and hoods.
		if (itemId >= 9747 && itemId <= 9812) {
			player.getPA().sendMessage("This item is untradeable.");
			return false;
		}
		if (!player.canOffer) {
			return false;
		}

		if (ItemAssistant.cannotTradeAndStakeItemItem(itemId)) {
			player.getPA().sendMessage("This item is unstakeable.");
			return false;
		}

		if (DuelArenaBan.isDuelBanned(player, false)) {
			return false;
		}

		if (GameMode.getGameModeContains(player, "IRON MAN")) {
			player.getPA().sendMessage("As an Ironman, you stand alone! No staking allowed.");
			return false;
		}

		if (GameMode.getGameModeContains(partner, "IRON MAN")) {
			player.getPA().sendMessage(partner.getPlayerName() + " is an Ironman. You can't stake Ironmen!");
			return false;
		}
		if (player.getPA().isNewPlayerEco()) {
			player.getPA().sendMessage("You cannot stake items as a new player.");
			return false;
		}
		if (itemId == 995 && GameType.isOsrsPvp()) {
			return false;
		}

		long myStakedItemsWealth = 0;
		for (int index = 0; index < myStakedItems.size(); index++) {
			myStakedItemsWealth += ServerConstants.getItemValue(myStakedItems.get(index).getId()) * myStakedItems.get(index).getAmount();
		}

		double number = (double) MAXIMUM_BANK_PERCENTAGE_STAKE / 100.0;
		long stakeTotalAfterAddition = myStakedItemsWealth + (ServerConstants.getItemValue(itemId) * amount);
		long amountLeft = (long) ((player.wealthBeforeStake * number) - myStakedItemsWealth);
		if (stakeTotalAfterAddition > (player.wealthBeforeStake * number)) {
			player.getPA().sendMessage("You cannot stake more than " + MAXIMUM_BANK_PERCENTAGE_STAKE + "% of your bank.");
			player.getPA().sendMessage("Dawntained is trying to make you not lose your bank staking, please stake wisely.");
			player.getPA().sendMessage("You can add another " + Misc.formatNumber(amountLeft) + " " + ServerConstants.getMainCurrencyName().toLowerCase() + ".");
			player.getPA().sendMessage("Talk to the duel arena npc west of the bank at duel to ban yourself for a week!");
			return false;
		}

		partner.timeDuelRuleChanged = System.currentTimeMillis();
		player.timeDuelRuleChanged = System.currentTimeMillis();
		GameTimeSpent.setLastActivity(player, "STAKING");
		changeDuelStuff();

		if (!ItemDefinition.getDefinitions()[itemId].stackable) {
			for (int a = 0; a < amount; a++) {
				if (ItemAssistant.hasItemAmountInInventory(player, itemId, 1)) {
					myStakedItems.add(new GameItem(itemId, 1));
					ItemAssistant.deleteItemFromInventory(player, itemId, ItemAssistant.getItemSlot(player, itemId), 1);
				}
			}
			player.setInventoryUpdate(true);
			ItemAssistant.resetItems(player, 3322);
			partner.setInventoryUpdate(true);
			ItemAssistant.resetItems(partner, 3322);
			refreshDuelScreen();
			partner.getTradeAndDuel().refreshDuelScreen();
			player.getPA().sendFrame126("", 6684);
			partner.getPA().sendFrame126("", 6684);
		}

		if (!ItemAssistant.hasItemAmountInInventory(player, itemId, amount)) {
			return false;
		}
		if (ItemDefinition.getDefinitions()[itemId].stackable || ItemDefinition.getDefinitions()[itemId].note) {
			boolean found = false;
			for (GameItem item : myStakedItems) {
				if (item.getId() == itemId) {
					found = true;
					myStakedItems.set(myStakedItems.indexOf(item), new GameItem(item.getId(), item.getAmount() + amount));
					ItemAssistant.deleteItemFromInventory(player, itemId, fromSlot, amount);
					break;
				}
			}
			if (!found) {
				ItemAssistant.deleteItemFromInventory(player, itemId, fromSlot, amount);
				myStakedItems.add(new GameItem(itemId, amount));
			}
		}

		player.setInventoryUpdate(true);
		ItemAssistant.resetItems(player, 3322);
		partner.setInventoryUpdate(true);
		ItemAssistant.resetItems(partner, 3322);
		refreshDuelScreen();
		partner.getTradeAndDuel().refreshDuelScreen();
		player.getPA().sendFrame126("", 6684);
		partner.getPA().sendFrame126("", 6684);
		return true;
	}


	public boolean fromDuel(int itemID, int fromSlot, int amount) {
		if (player.getDuelStatus() >= 3) {
			return false;
		}
		Player o = player.getTradeAndDuel().getPartnerDuel();
		if (o == null) {
			declineDuel(false);
			return false;
		}
		if (o.getDuelStatus() <= 0 || player.getDuelStatus() <= 0) {
			declineDuel(false);
			o.getTradeAndDuel().declineDuel(false);
			return false;
		}
		if (!player.canOffer) {
			return false;
		}
		if (amount <= 0) {
			return false;
		}
		if (player.getType() == EntityType.PLAYER_PET || o.getType() == EntityType.PLAYER_PET) {
			return false;
		}
		if (ItemDefinition.getDefinitions()[itemID].stackable) {
			if (ItemAssistant.getFreeInventorySlots(player) - 1 < (player.duelSpaceReq)) {
				player.playerAssistant.sendMessage("You have too many rules set to remove that item.");
				return false;
			}
		}

		o.timeDuelRuleChanged = System.currentTimeMillis();
		player.timeDuelRuleChanged = System.currentTimeMillis();
		changeDuelStuff();
		boolean goodSpace = true;
		if (!ItemDefinition.getDefinitions()[itemID].stackable) {
			for (int a = 0; a < amount; a++) {
				for (GameItem item : myStakedItems) {
					if (item.getId() == itemID) {
						if (!item.isStackable()) {
							if (ItemAssistant.getFreeInventorySlots(player) - 1 < (player.duelSpaceReq)) {
								goodSpace = false;
								break;
							}
							myStakedItems.remove(item);
							ItemAssistant.addItem(player, itemID, 1);
						} else {
							if (ItemAssistant.getFreeInventorySlots(player) - 1 < (player.duelSpaceReq)) {
								goodSpace = false;
								break;
							}
							if (item.getAmount() > amount) {
								myStakedItems.set(myStakedItems.indexOf(item), new GameItem(item.getId(), item.getAmount() - amount));
								ItemAssistant.addItem(player, itemID, amount);
							} else {
								if (ItemAssistant.getFreeInventorySlots(player) - 1 < (player.duelSpaceReq)) {
									goodSpace = false;
									break;
								}
								amount = item.getAmount();
								myStakedItems.remove(item);
								ItemAssistant.addItem(player, itemID, amount);
							}
						}
						break;
					}
					o.setDuelStatus(1);
					player.setDuelStatus(1);
					player.setInventoryUpdate(true);
					ItemAssistant.resetItems(player, 3322);
					o.setInventoryUpdate(true);
					ItemAssistant.resetItems(o, 3322);
					player.getTradeAndDuel().refreshDuelScreen();
					o.getTradeAndDuel().refreshDuelScreen();
					o.getPA().sendFrame126("", 6684);
				}
			}
		}

		for (GameItem item : myStakedItems) {
			if (item.getId() == itemID) {
				if (!item.isStackable()) {
				} else {
					if (item.getAmount() > amount) {
						myStakedItems.set(myStakedItems.indexOf(item), new GameItem(item.getId(), item.getAmount() - amount));
						ItemAssistant.addItem(player, itemID, amount);
					} else {
						amount = item.getAmount();
						myStakedItems.remove(item);
						ItemAssistant.addItem(player, itemID, amount);
					}
				}
				break;
			}
		}
		o.setDuelStatus(1);
		player.setDuelStatus(1);
		player.setInventoryUpdate(true);
		ItemAssistant.resetItems(player, 3322);
		o.setInventoryUpdate(true);
		ItemAssistant.resetItems(o, 3322);
		player.getTradeAndDuel().refreshDuelScreen();
		o.getTradeAndDuel().refreshDuelScreen();
		o.getPA().sendFrame126("", 6684);
		if (!goodSpace) {
			player.playerAssistant.sendMessage("You have too many rules set to remove that item.");
			return true;
		}
		return true;
	}

	public void confirmDuel() {
		Player o = player.getTradeAndDuel().getPartnerDuel();
		if (o == null) {
			declineDuel(false);
			return;
		}
		String itemId = "";
		for (GameItem item : myStakedItems) {
			if (ItemDefinition.getDefinitions()[item.getId()].stackable || ItemDefinition.getDefinitions()[item.getId()].note) {
				itemId += ItemAssistant.getItemName(item.getId()) + " x " + Misc.formatNumber(item.getAmount()) + "\\n";
			} else {
				itemId += ItemAssistant.getItemName(item.getId()) + "\\n";
			}
		}
		player.getPA().sendFrame126(itemId, 6516);
		itemId = "";
		for (GameItem item : o.getTradeAndDuel().myStakedItems) {
			if (ItemDefinition.getDefinitions()[item.getId()].stackable || ItemDefinition.getDefinitions()[item.getId()].note) {
				itemId += ItemAssistant.getItemName(item.getId()) + " x " + Misc.formatNumber(item.getAmount()) + "\\n";
			} else {
				itemId += ItemAssistant.getItemName(item.getId()) + "\\n";
			}
		}
		player.getPA().sendFrame126(itemId, 6517);
		player.getPA().sendFrame126("", 8242);
		for (int i = 8238; i <= 8253; i++) {
			player.getPA().sendFrame126("", i);
		}
		player.getPA().sendFrame126("Hitpoints will be restored.", 8250);
		player.getPA().sendFrame126("Boosted stats will be restored.", 8238);
		if (player.duelRule[8]) {
			player.getPA().sendFrame126("There will be obstacles in the arena.", 8239);
		}
		player.getPA().sendFrame126("", 8240);
		player.getPA().sendFrame126("", 8241);

		String[] rulesOption =
				{"Players cannot forfeit!", "Players cannot move.", "Players cannot use range.", "Players cannot use melee.", "Players cannot use magic.",
						"Players cannot drink pots.", "Players cannot eat food.", "Players cannot use prayer."
				};

		int lineNumber = 8242;
		for (int i = 0; i < 8; i++) {
			if (player.duelRule[i]) {
				player.getPA().sendFrame126("" + rulesOption[i], lineNumber);
				lineNumber++;
			}
		}
		player.getPA().sendFrame126("", 6571);
		player.getPA().sendFrame248(6412, 197);
		//c.getPA().showInterface(6412);
	}


	public void startDuel() {
		player.stakeAttacks = 0;
		player.stakeSpecialAttacks = 0;
		player.getPA().resetStats();
		Combat.resetPrayers(player);
		Follow.resetFollow(player);
		Player o = player.getTradeAndDuel().getPartnerDuel();
		if (o == null) {
			duelVictory();
		}
		if (o.isDisconnected()) {
			duelVictory();
		}
		QuickSetUp.heal(player, false, true);
		player.headIconHints = 2;
		player.setVengeance(false);

		if (player.duelRule[7]) {
			Combat.resetPrayers(player);
		}
		if (player.duelRule[11]) {
			ItemAssistant.removeItem(player, player.playerEquipment[0], 0, false);
		}
		if (player.duelRule[12]) {
			ItemAssistant.removeItem(player, player.playerEquipment[1], 1, false);
		}
		if (player.duelRule[13]) {
			ItemAssistant.removeItem(player, player.playerEquipment[2], 2, false);
		}
		if (player.duelRule[14]) {
			ItemAssistant.removeItem(player, player.playerEquipment[3], 3, false);
		}
		if (player.duelRule[15]) {
			ItemAssistant.removeItem(player, player.playerEquipment[4], 4, false);
		}
		if (player.duelRule[16]) {
			ItemAssistant.removeItem(player, player.playerEquipment[5], 5, false);
		}
		if (player.duelRule[17]) {
			ItemAssistant.removeItem(player, player.playerEquipment[7], 7, false);
		}
		if (player.duelRule[18]) {
			ItemAssistant.removeItem(player, player.playerEquipment[9], 9, false);
		}
		if (player.duelRule[19]) {
			ItemAssistant.removeItem(player, player.playerEquipment[10], 10, false);
		}
		if (player.duelRule[20]) {
			ItemAssistant.removeItem(player, player.playerEquipment[12], 12, false);
		}
		if (player.duelRule[21]) {
			ItemAssistant.removeItem(player, player.playerEquipment[13], 13, false);
		}

		//14 means weapon rule is on
		if (!player.duelRule[14] && ItemAssistant.hasItemInEquipmentSlot(player, ServerConstants.WEAPON_SLOT)) {
			// If 2h weapon is wielded and shields is ticked off, then wapon must be removed. 16 means shield rule is on.
			if (ItemAssistant.hasItemInEquipmentSlot(player, ServerConstants.WEAPON_SLOT) && player.duelRule[16]) {
				if (ItemAssistant.is2handed(player.getWieldedWeapon())) {
					ItemAssistant.removeItem(player, player.playerEquipment[ServerConstants.WEAPON_SLOT], ServerConstants.WEAPON_SLOT, false);
				}
			}
		}
		player.setDuelStatus(5);
		player.getPA().closeInterfaces(true);
		player.setSpecialAttackAmount(10, false);
		CombatInterface.addSpecialBar(player, player.getWieldedWeapon());

		if (player.duelRule[8]) {
			if (player.duelRule[1]) {
				player.getPA().movePlayer(player.duelTeleX, player.duelTeleY, 0);
			} else {
				player.getPA().movePlayer(3366 + Misc.random(12), 3246 + Misc.random(6), 0);
			}
		} else {
			if (player.duelRule[1]) {
				player.getPA().movePlayer(player.duelTeleX, player.duelTeleY, 0);
			} else {
				player.getPA().movePlayer(3336 + Misc.random(19), 3246 + Misc.random(10), 0);
			}
		}

		player.getPA().createPlayerHints(10, o.getPlayerId());
		player.getPA().showOption(3, 0, "Attack", 1);
		Skilling.updateAllSkillTabFrontText(player);
		boolean staked = false;
		for (GameItem item : o.getTradeAndDuel().myStakedItems) {
			otherStakedItems.add(new GameItem(item.getId(), item.getAmount()));
			staked = true;
		}
		if (staked) {
			o.duelArenaStakes++;
		}
		player.getPA().requestUpdates();
	}


	public void duelVictory() {
		Player o = player.getTradeAndDuel().getPartnerDuel();
		if (o != null) {
			player.getPA().sendFrame126("" + o.getCombatLevel(), 6839);
			player.getPA().sendFrame126(o.getPlayerName(), 6840);
			o.setDuelStatus(0);
			o.getTradeAndDuel().resetDuel();
			QuickSetUp.heal(player, false, true);
		} else {
			player.getPA().sendFrame126("", 6839);
			player.getPA().sendFrame126("", 6840);
		}
		player.setDuelStatus(6);
		Combat.resetPrayers(player);
		Skilling.updateAllSkillTabFrontText(player);
		player.setSpecialAttackAmount(10, false);
		CombatInterface.addSpecialBar(player, player.getWieldedWeapon());
		duelRewardInterface();
		player.getPA().displayInterface(6733);
		player.getPA().movePlayer(3362, 3263, 0);
		player.getPA().requestUpdates();
		player.getPA().showOption(3, 0, "Challenge", 3);
		player.getPA().resetStats();
		player.setSpecialAttackAmount(10.0, false);
		CombatInterface.addSpecialBar(player, player.getWieldedWeapon());
		player.setHitPoints(player.getBaseHitPointsLevel());
		player.runEnergy = 100;
		Poison.removePoison(player);
		player.currentCombatSkillLevel[ServerConstants.PRAYER] = player.getBasePrayerLevel();
		Skilling.updateSkillTabFrontTextMain(player, ServerConstants.PRAYER);
		player.getPA().createPlayerHints(10, -1);
		player.canOffer = true;
		player.duelSpaceReq = 0;
		player.setDuelingWith(0);
		player.setFrozenLength(0);
		Follow.resetFollow(player);
		Combat.resetPlayerAttack(player);
		player.duelRequested = false;
		for (int i = 0; i < player.duelRule.length; i++) {
			player.duelRule[i] = false;
		}
	}


	public void duelRewardInterface() {
		player.getOutStream().createFrameVarSizeWord(53);
		player.getOutStream().writeWord(6822);
		player.getOutStream().writeWord(otherStakedItems.toArray().length);
		for (GameItem item : otherStakedItems) {
			int id = item.getId() + 1;
			if (item.getAmount() > 254) {
				player.getOutStream().writeByte(255);
				player.getOutStream().writeDWord_v2(item.getAmount());
			} else {
				player.getOutStream().writeByte(item.getAmount());
			}
			if (item.getId() > ServerConstants.MAX_ITEM_ID || item.getId() < 0) {
				id = ServerConstants.MAX_ITEM_ID;
			}
			player.getOutStream().writeWordBigEndianA(id);
		}
		player.getOutStream().endFrameVarSizeWord();
		player.flushOutStream();
	}

	public void claimStakedItems() {
		if (player.getDuelStatus() != 6) {
			return;
		}
		if (Area.inDuelArenaRing(player)) {
			return;
		}

		Player lastDueledWithPlayer = Misc.getPlayerByName(player.getLastDueledWithName());
		String originalOwnerIp = "";
		String originalOwnerUid = "";
		if (lastDueledWithPlayer != null) {
			originalOwnerIp = lastDueledWithPlayer.addressIp;
			originalOwnerUid = lastDueledWithPlayer.addressUid;
		}
		for (GameItem item : otherStakedItems) {

			if (item.getId() > 0 && item.getAmount() > 0) {
				if (ItemDefinition.getDefinitions()[item.getId()].stackable) {
					if (!ItemAssistant.addItem(player, item.getId(), item.getAmount())) {
						player.getPA().sendMessage(ServerConstants.RED_COL + "The item has been dropped on the ground!");
						Server.itemHandler.createGroundItem(player, item.getId(), player.getX(), player.getY(), player.getHeight(), item.getAmount(), false, 0, true,
								player.getLastDueledWithName(), "", originalOwnerIp, originalOwnerUid, "claimStakedItems first " + lastDueledWithPlayer);
					}
				} else {
					int amount = item.getAmount();
					for (int a = 1; a <= amount; a++) {
						if (!ItemAssistant.addItem(player, item.getId(), 1)) {
							player.getPA().sendMessage(ServerConstants.RED_COL + "The item has been dropped on the ground!");
							Server.itemHandler
									.createGroundItem(player, item.getId(), player.getX(), player.getY(), player.getHeight(), 1, false, 0, true, player.getLastDueledWithName(), "",
											originalOwnerIp, originalOwnerUid, "claimStakedItems second " + lastDueledWithPlayer);
						}
					}
				}
			}
		}
		for (GameItem item : myStakedItems) {
			if (item.getId() > 0 && item.getAmount() > 0) {
				if (ItemDefinition.getDefinitions()[item.getId()].stackable) {
					if (!ItemAssistant.addItem(player, item.getId(), item.getAmount())) {
						player.getPA().sendMessage(ServerConstants.RED_COL + "The item has been dropped on the ground!");
						Server.itemHandler.createGroundItem(player, item.getId(), player.getX(), player.getY(), player.getHeight(), item.getAmount(), false, 0, true,
								player.getLastDueledWithName(), "", originalOwnerIp, originalOwnerUid, "claimStakedItems third " + lastDueledWithPlayer);
					}
				} else {
					int amount = item.getAmount();
					for (int a = 1; a <= amount; a++) {
						if (!ItemAssistant.addItem(player, item.getId(), 1)) {
							player.getPA().sendMessage(ServerConstants.RED_COL + "The item has been dropped on the ground!");
							Server.itemHandler
									.createGroundItem(player, item.getId(), player.getX(), player.getY(), player.getHeight(), 1, false, 0, true, player.getLastDueledWithName(), "",
											originalOwnerIp, originalOwnerUid, "claimStakedItems fourth " + lastDueledWithPlayer);
						}
					}
				}
			}
		}
		resetDuelItems();
		player.setDuelStatus(0);
	}

	public void declineDuel(boolean tellOther) {
		if (tellOther) {

			if (player.getDuelingWith() > 0) {
				player.getPA().sendMessage("You have declined the duel.");
			}
			Player o = player.getTradeAndDuel().getPartnerDuel();
			if (o != null) {
				o.getPA().sendMessage(player.getPlayerName() + " has declined the duel.");
			}
		}
		player.getPA().closeInterfaces(true);
		player.canOffer = true;
		player.setDuelStatus(0);
		Player o = player.getTradeAndDuel().getPartnerDuel();
		if (o != null) {
			o.getTradeAndDuel().otherStakedItems.clear();
		}
		player.setDuelingWith(0);
		player.duelSpaceReq = 0;
		player.duelRequested = false;
		for (GameItem item : myStakedItems) {
			if (item.getAmount() < 1)
				continue;
			if (ItemDefinition.getDefinitions()[item.getId()].stackable || ItemDefinition.getDefinitions()[item.getId()].note) {
				ItemAssistant.addItemToInventoryOrDrop(player, item.getId(), item.getAmount());
			} else {
				ItemAssistant.addItemToInventoryOrDrop(player, item.getId(), 1);
			}
		}
		myStakedItems.clear();
		for (int i = 0; i < player.duelRule.length; i++) {
			player.duelRule[i] = false;
		}
	}

	public void resetDuel() {
		player.getPA().showOption(3, 0, "Challenge", 3);
		player.headIconHints = 0;
		for (int i = 0; i < player.duelRule.length; i++) {
			player.duelRule[i] = false;
		}
		player.getPA().createPlayerHints(10, -1);
		player.setDuelStatus(0);
		player.canOffer = true;
		player.duelSpaceReq = 0;
		player.setDuelingWith(0);
		player.setFrozenLength(0);
		Follow.resetFollow(player);
		player.getPA().requestUpdates();
		Combat.resetPlayerAttack(player);
		player.duelRequested = false;
	}

	public void resetDuelItems() {
		myStakedItems.clear();
		otherStakedItems.clear();
	}

	public void changeDuelStuff() {
		Player o = player.getTradeAndDuel().getPartnerDuel();
		if (o == null) {
			return;
		}
		o.setDuelStatus(1);
		player.setDuelStatus(1);
		o.getPA().sendFrame126("", 6684);
		player.getPA().sendFrame126("", 6684);
	}

	/**
	 * Remove player from the arena
	 */
	public void removeFromArena() {
		if (Area.inDuelArenaRing(player) && player.getDuelStatus() != 5) {
			player.getPA().movePlayer(ServerConstants.DUEL_ARENA_X + (Misc.random(ServerConstants.RANDOM_DISTANCE)),
			                          ServerConstants.DUEL_ARENA_Y + (Misc.random(ServerConstants.RANDOM_DISTANCE)), 0);
		}
	}


	//@formatter:off

	/**
	 * These rules below must be enabled for Anti-scam to be active.
	 */
	private final static int[] ANTI_SCAM_SETUP_RULES_TO_ENABLE = {NO_FF, NO_MOVEMENT, NO_RANGED, NO_MAGIC, NO_DRINK, NO_FOOD, NO_PRAYER, NO_HELM, NO_CAPE, NO_AMULET,
			NO_BODY, NO_SHIELD, NO_LEGS, NO_GLOVES, NO_BOOTS, NO_RING, NO_ARROWS
	};

	/**
	 * These rules below must be not activated for Anti-scam to be active.
	 */
	private final static int[] ANTI_SCAM_SETUP_RULES_TO_DISABLE = {NO_MELEE, OBSTACLES, NO_WEAPON};
	//@formatter:on


	public void toggleAntiScanSetUp() {
		Player o = player.getTradeAndDuel().getPartnerDuel();
		if (o == null) {
			return;
		}
		if (!player.canOffer) {
			return;
		}
		for (int index = 0; index < ANTI_SCAM_SETUP_RULES_TO_ENABLE.length; index++) {
			if (!player.duelRule[ANTI_SCAM_SETUP_RULES_TO_ENABLE[index]]) {
				if (player.duelRule[ANTI_SCAM]) {
					toggleRule(ANTI_SCAM_SETUP_RULES_TO_ENABLE[index], false);
				}
			}
		}
		for (int index = 0; index < ANTI_SCAM_SETUP_RULES_TO_DISABLE.length; index++) {
			if (player.duelRule[ANTI_SCAM_SETUP_RULES_TO_DISABLE[index]]) {
				if (player.duelRule[ANTI_SCAM]) {
					toggleRule(ANTI_SCAM_SETUP_RULES_TO_DISABLE[index], false);
				}
			}
		}
	}

	/**
	 * @return True if Anti-scam rules are all enabled.
	 */
	public boolean antiScanSetupRulesActive() {
		for (int index = 0; index < ANTI_SCAM_SETUP_RULES_TO_ENABLE.length; index++) {
			if (!player.duelRule[ANTI_SCAM_SETUP_RULES_TO_ENABLE[index]]) {
				return false;
			}
		}
		for (int index = 0; index < ANTI_SCAM_SETUP_RULES_TO_DISABLE.length; index++) {
			if (player.duelRule[ANTI_SCAM_SETUP_RULES_TO_DISABLE[index]]) {
				return false;
			}
		}
		return true;
	}

	// Toggle a duel arena interface rule, weather it is a rule or an item on/off.
	public void toggleRule(int ruleId, boolean manualAction) {
		Player o = player.getTradeAndDuel().getPartnerDuel();
		if (o == null) {
			return;
		}
		if (!player.canOffer) {
			return;
		}
		if (o.getDuelStatus() <= 0 || player.getDuelStatus() <= 0) {
			declineDuel(false);
			o.getTradeAndDuel().declineDuel(false);
			return;
		}
		boolean antiScamSetupTicked = player.duelRule[ANTI_SCAM];
		if (ruleId >= 11) {
			player.setDuelItemSlot(ruleId - 11);
		} else {
			player.setDuelItemSlot(-1);
		}
		o.timeDuelRuleChanged = System.currentTimeMillis();
		player.timeDuelRuleChanged = System.currentTimeMillis();
		changeDuelStuff();
		o.setDuelItemSlot(player.getDuelItemSlot());
		if (ruleId >= 11 && player.getDuelItemSlot() > -1) {
			if (player.playerEquipment[player.getDuelItemSlot()] > 0) {
				if (!player.duelRule[ruleId]) {
					player.duelSpaceReq++;
				} else {
					player.duelSpaceReq--;
				}
			}
			if (o.playerEquipment[o.getDuelItemSlot()] > 0) {
				if (!o.duelRule[ruleId]) {
					o.duelSpaceReq++;
				} else {
					o.duelSpaceReq--;
				}
			}
		}

		if (ruleId >= 11) {
			if (ItemAssistant.getFreeInventorySlots(player) < (player.duelSpaceReq) || ItemAssistant.getFreeInventorySlots(o) < (o.duelSpaceReq)) {
				player.playerAssistant.sendMessage("You or your opponent don't have the required space to set this rule.");
				if (player.playerEquipment[player.getDuelItemSlot()] > 0) {
					player.duelSpaceReq--;
				}
				if (o.playerEquipment[o.getDuelItemSlot()] > 0) {
					o.duelSpaceReq--;
				}
				return;
			}
		}

		if (!player.duelRule[ruleId]) {
			player.duelRule[ruleId] = true;
			player.duelOptionFrameId += ServerConstants.DUEL_RULE_FRAME_ID[ruleId];
		} else {
			player.duelRule[ruleId] = false;
			player.duelOptionFrameId -= ServerConstants.DUEL_RULE_FRAME_ID[ruleId];
		}

		player.getPA().sendFrame87(286, player.duelOptionFrameId);
		o.duelOptionFrameId = player.duelOptionFrameId;
		o.duelRule[ruleId] = player.duelRule[ruleId];
		o.getPA().sendFrame87(286, o.duelOptionFrameId);

		if (player.duelRule[OBSTACLES]) {
			if (player.duelRule[NO_MOVEMENT]) {
				player.duelTeleX = 3366 + Misc.random(12);
				o.duelTeleX = player.duelTeleX - 1;
				player.duelTeleY = 3246 + Misc.random(6);
				o.duelTeleY = player.duelTeleY;
			}
		} else {
			if (player.duelRule[NO_MOVEMENT]) {
				player.duelTeleX = 3336 + Misc.random(19);
				o.duelTeleX = player.duelTeleX - 1;
				player.duelTeleY = 3246 + Misc.random(10);
				o.duelTeleY = player.duelTeleY;
			}
		}
		if (manualAction) {
			if (player.duelRule[ANTI_SCAM] && antiScamSetupTicked) {
				if (!antiScanSetupRulesActive()) {
					toggleRule(ANTI_SCAM, manualAction);
				}
			}
		}

	}

	public boolean hasRequiredSpaceForDuel() {
		int amountOfItemsToRemove = 0;
		if (player.duelRule[11] && ItemAssistant.hasItemInEquipmentSlot(player, ServerConstants.HEAD_SLOT)) {
			amountOfItemsToRemove++;
		}
		if (player.duelRule[12] && ItemAssistant.hasItemInEquipmentSlot(player, ServerConstants.CAPE_SLOT)) {
			amountOfItemsToRemove++;
		}
		if (player.duelRule[13] && ItemAssistant.hasItemInEquipmentSlot(player, ServerConstants.AMULET_SLOT)) {
			amountOfItemsToRemove++;
		}
		if (player.duelRule[14] && ItemAssistant.hasItemInEquipmentSlot(player, ServerConstants.WEAPON_SLOT)) {
			amountOfItemsToRemove++;
		} else {
			// If 2h weapon is wielded and shields is ticked off, then wapon must be removed.
			if (ItemAssistant.hasItemInEquipmentSlot(player, ServerConstants.WEAPON_SLOT) && player.duelRule[16]) {
				if (ItemAssistant.is2handed(player.getWieldedWeapon())) {
					amountOfItemsToRemove++;
				}
			}
		}
		if (player.duelRule[15] && ItemAssistant.hasItemInEquipmentSlot(player, ServerConstants.BODY_SLOT)) {
			amountOfItemsToRemove++;
		}

		// Shield slot.
		if (player.duelRule[16] && ItemAssistant.hasItemInEquipmentSlot(player, ServerConstants.SHIELD_SLOT)) {
			amountOfItemsToRemove++;
		}
		if (player.duelRule[17] && ItemAssistant.hasItemInEquipmentSlot(player, ServerConstants.LEG_SLOT)) {
			amountOfItemsToRemove++;
		}
		if (player.duelRule[18] && ItemAssistant.hasItemInEquipmentSlot(player, ServerConstants.HAND_SLOT)) {
			amountOfItemsToRemove++;
		}
		if (player.duelRule[19] && ItemAssistant.hasItemInEquipmentSlot(player, ServerConstants.FEET_SLOT)) {
			amountOfItemsToRemove++;
		}
		if (player.duelRule[20] && ItemAssistant.hasItemInEquipmentSlot(player, ServerConstants.RING_SLOT)) {
			amountOfItemsToRemove++;
		}
		if (player.duelRule[21] && ItemAssistant.hasItemInEquipmentSlot(player, ServerConstants.ARROW_SLOT)) {
			amountOfItemsToRemove++;
		}
		int slotsLeft = ItemAssistant.getFreeInventorySlots(player);
		if (slotsLeft < amountOfItemsToRemove) {
			player.getPA().sendMessage("You need " + (amountOfItemsToRemove - slotsLeft) + " more inventory spaces.");
			return false;
		}
		return true;
	}

	public void acceptFirstTradeScreen() {
		Player other = getPartnerTrade();
		if (other == null) {
			player.getTradeAndDuel().declineTrade1(true);
			return;
		}
		if (other.getTradingWithId() != player.getPlayerId()) {
			player.getTradeAndDuel().declineTrade1(true);
			return;
		}


		if (GameMode.getGameModeContains(player, "IRON MAN")) {
			player.getPA().sendMessage("As an Ironman, you stand alone! No trading allowed.");
			return;
		}

		if (GameMode.getGameModeContains(other, "IRON MAN")) {
			player.getPA().sendMessage(other.getPlayerName() + " is an Ironman. You can't trade Ironmen!");
			return;
		}
		player.getPA().sendFrame126("Waiting for other player...", 3431);
		other.getPA().sendFrame126("Other player has accepted", 3431);
		player.goodTrade = true;
		other.goodTrade = true;
		for (GameItem item : other.getTradeAndDuel().offeredItems) {
			if (item.getId() == 7936 || item.getId() == 1436) {
				if (System.currentTimeMillis() - player.timeSkilled <= 60000 && System.currentTimeMillis() - other.timeUsedPreset <= 60000) {
					player.goodTrade = false;
					other.goodTrade = false;
					player.getPA().sendMessage("You cannot receive presetted essence! Cheater! Let him run instead.");
					return;
				}
			}
		}
		for (GameItem item : player.getTradeAndDuel().offeredItems) {
			if (item.getId() > 0) {
				if (ItemAssistant.getFreeInventorySlots(other) < player.getTradeAndDuel().offeredItems.size()) {
					player.playerAssistant.sendMessage(other.getCapitalizedName() + " only has " + ItemAssistant.getFreeInventorySlots(other) + " free slots, please remove " + (
							player.getTradeAndDuel().offeredItems.size() - ItemAssistant.getFreeInventorySlots(other)) + " items.");
					other.playerAssistant.sendMessage(
							player.getCapitalizedName() + " has to remove " + (player.getTradeAndDuel().offeredItems.size() - ItemAssistant.getFreeInventorySlots(other))
							+ " items or you could offer them " + (player.getTradeAndDuel().offeredItems.size() - ItemAssistant.getFreeInventorySlots(other)) + " items.");
					player.goodTrade = false;
					other.goodTrade = false;
					player.getPA().sendFrame126("Not enough inventory space...", 3431);
					other.getPA().sendFrame126("Not enough inventory space...", 3431);
					break;
				} else {
					if (!EdgeAndWestsRule.canPickUpOrReceiveBrew(other, item.getId())) {
						player.goodTrade = false;
						other.goodTrade = false;
						player.getPA().sendFrame126("Cannot give excess brews", 3431);
						other.getPA().sendFrame126("Cannot give excess brews", 3431);
						break;
					} else {
						player.getPA().sendFrame126("Waiting for other player...", 3431);
						other.getPA().sendFrame126("Other player has accepted", 3431);
						player.goodTrade = true;
						other.goodTrade = true;
					}
				}
			}
		}
		if (player.isInTrade() && !player.tradeConfirmed && other.goodTrade && player.goodTrade) {
			player.tradeConfirmed = true;
			if (other.tradeConfirmed) {
				player.getTradeAndDuel().confirmScreen();
				other.getTradeAndDuel().confirmScreen();
				return;
			}
		}
	}

	public void acceptSecondTradeScreen() {
		Player partner = getPartnerTrade();
		if (partner == null) {
			player.getTradeAndDuel().declineTrade1(true);
			return;
		}
		if (partner.getTradingWithId() != player.getPlayerId()) {
			player.getTradeAndDuel().declineTrade1(true);
			return;
		}

		if (partner.tradePossibleScam && System.currentTimeMillis() - partner.tradeScamTime <= 5000) {
			player.getPA().sendMessage(ServerConstants.RED_COL + partner.getPlayerName() + " has recently reduced the items in the trade, please check the items!");
			long seconds = 5 - ((System.currentTimeMillis() - partner.tradeScamTime) / 1000);
			player.getPA().sendMessage(ServerConstants.RED_COL + seconds + " second" + Misc.getPluralS(seconds) + " left.");
			return;
		}
		player.tradeAccepted = true;
		if (player.isInTrade() && player.tradeConfirmed && partner.tradeConfirmed && !player.tradeConfirmed2) {
			player.tradeConfirmed2 = true;
			if (partner.tradeConfirmed2) {
				player.ignoreTradeMessage = true;
				player.acceptedTrade = true;
				partner.acceptedTrade = true;
				ItemTransferLog.tradeCompleted(player, partner);
				player.getTradeAndDuel().giveItems(partner);
				partner.getTradeAndDuel().giveItems(player);
				player.getTradeAndDuel().offeredItems.clear();
				partner.getTradeAndDuel().offeredItems.clear();

				player.getPA().closeInterfaces(true);
				player.tradeResetNeeded = true;
				player.getTradeAndDuel().tradeResetRequired();
				partner.getPA().closeInterfaces(true);
				partner.tradeResetNeeded = true;
				partner.getTradeAndDuel().tradeResetRequired();
				partner.playerAssistant.sendMessage("You have finished a trade with " + player.getCapitalizedName() + ".");
				player.getPA().sendMessage("You have finished a trade with " + partner.getCapitalizedName() + ".");
				player.ignoreTradeMessage = false;
				if (Area.inDiceZone(player) && Area.inDiceZone(partner) && ClanChatHandler.inDiceCc(player, false, false) && ClanChatHandler.inDiceCc(partner, false, false)) {
					//Do not add check for dice bag in inv or amount taken.
					//The scammer can easily bank the dice and then trade etc.
					player.timeAcceptedTradeInDiceZone = System.currentTimeMillis();
					partner.timeAcceptedTradeInDiceZone = System.currentTimeMillis();
				}
				return;
			}
			partner.getPA().sendFrame126("Other player has accepted.", 3535);
			player.getPA().sendFrame126("Waiting for other player...", 3535);
		}

	}

	public void duelArenaAcceptFirstScreen(boolean skipAntiScamRequirement) {
		if (player.getDuelStatus() != 1) {
			return;
		}
		if (Area.inDuelArena(player)) {
			Player other = player.getTradeAndDuel().getPartnerDuel();
			if (other == null) {
				player.getTradeAndDuel().declineDuel(false);
				return;
			}


			if (other.getDuelingWith() != player.getPlayerId()) {
				player.getTradeAndDuel().declineTrade1(true);
				return;
			}

			if (player.duelRule[2] && player.duelRule[3] && player.duelRule[4]) {
				player.playerAssistant.sendMessage("You won't be able to attack the player with the rules you have set.");
				return;
			}

			if (player.duelRule[TradeAndDuel.ANTI_SCAM]) {
				if (player.getWieldedWeapon() > 0) {
					if (!ItemDefinition.getDefinitions()[player.getWieldedWeapon()].name.contains("Abyssal tentacle") && player.duelRule[TradeAndDuel.NO_SPECIAL_ATTACK]) {
						player.getPA().sendMessage("You need an Abyssal tentacle worn to start this duel.");
						return;
					}

					if ((!ItemAssistant.hasItemEquippedSlot(player, 5698, ServerConstants.WEAPON_SLOT) && !ItemDefinition.getDefinitions()[player.getWieldedWeapon()].name
							                                                                                       .contains("Abyssal tentacle"))
					    && !player.duelRule[TradeAndDuel.NO_SPECIAL_ATTACK]) {
						player.getPA().sendMessage("You need a Dragon dagger p++ or Abyssal tentacle worn to start this duel.");
						return;
					}
				} else {
					player.getPA().sendMessage("You need an Abyssal tentacle or Dragon dagger p++ worn to start this duel.");
					return;
				}
				if (!player.duelRule[TradeAndDuel.NO_SPECIAL_ATTACK]) {
					if (!ItemAssistant.hasItemEquippedSlot(player, 5698, ServerConstants.WEAPON_SLOT)) {
						if (!ItemAssistant.hasItemInInventory(player, 5698)) {
							player.getPA().sendMessage("You need a Dragon dagger p++ to start this duel.");
							return;
						}
					} else {
						boolean hasTentacleInInventory = false;
						for (int index = 0; index < player.playerItems.length; index++) {
							if (player.playerItemsN[index] == 1) {
								if (Combat.hasAbyssalTentacle(player, player.playerItems[index] - 1)) {
									hasTentacleInInventory = true;
									break;
								}
							}
						}
						if (!hasTentacleInInventory) {
							player.getPA().sendMessage("You need an Abyssal tentacle in your inventory to start this duel.");
							return;
						}
					}
				} else {
					if (ItemAssistant.hasItemEquippedSlot(player, 5698, ServerConstants.WEAPON_SLOT)) {
						player.getPA().sendMessage("Special attacks are turned off, you cannot start duel with Dds worn.");
						return;
					}
				}
			}

			if (!player.getTradeAndDuel().hasRequiredSpaceForDuel()) {
				return;
			}


			long myStakedItemsWealth = 0;
			for (int index = 0; index < myStakedItems.size(); index++) {
				myStakedItemsWealth += ServerConstants.getItemValue(myStakedItems.get(index).getId()) * myStakedItems.get(index).getAmount();
			}

			double number = (TradeAndDuel.LOWEST_BANK_PERCENTAGE_STAKE / 100.0);
			long minimumStake = (long) (player.wealthBeforeStake * number);
			long toAdd = minimumStake - myStakedItemsWealth;
			if (myStakedItemsWealth < minimumStake && myStakedItemsWealth > 0) {
				player.getPA().sendMessage("Your lowest stake possible is " + Misc.formatNumber(minimumStake) + " " + ServerConstants.getMainCurrencyName().toLowerCase() + ". "
				                           + TradeAndDuel.LOWEST_BANK_PERCENTAGE_STAKE + "% of your bank.");
				player.getPA().sendMessage("Please add " + Misc.formatNumber(toAdd) + " " + ServerConstants.getMainCurrencyName().toLowerCase() + " to your stake to continue.");
				player.getPA().sendMessage("This is a strict rule to prevent rich players from cleaning poorer");
				player.getPA().sendMessage("players which leads to them quitting and the eventual death of Dawntained.");
				player.getPA().sendMessage(ServerConstants.RED_COL + "Evading this rule in any way will result it an ip ban and accounts wiped.");
				return;
			}
			if (player.getTradeAndDuel().myStakedItems.size() > 0) {
				if (player.getCombatLevel() != other.getCombatLevel()) {
					player.getPA().sendMessage("Your combat level is not the same as your opponent.");
					return;
				}
			}

			if (System.currentTimeMillis() - player.timeDuelRuleChanged <= 5000) {
				int time = 5 - (int) ((System.currentTimeMillis() - player.timeDuelRuleChanged) / 1000);
				if (time < 0) {
					time = 0;
				}
				player.getPA().sendMessage(ServerConstants.RED_COL + "Please check the rules and items staked, last modification: " + time + " seconds ago.");
				return;
			}
			if (System.currentTimeMillis() - other.timeDuelRuleChanged <= 5000) {
				int time = 5 - (int) ((System.currentTimeMillis() - other.timeDuelRuleChanged) / 1000);
				if (time < 0) {
					time = 0;
				}
				player.getPA().sendMessage(ServerConstants.RED_COL + "Please check the rules and items staked, last modification: " + time + " seconds ago.");
				return;
			}
			if (player.getTradeAndDuel().myStakedItems.size() > 0) {
				if (!player.duelRule[TradeAndDuel.ANTI_SCAM] && !skipAntiScamRequirement) {
					player.getDH().sendStatement("@red@Anti-scam is turned off! Turn it on to prevent being scammed!");
					player.nextDialogue = 653;
					return;
				}
			}

			player.setDuelStatus(2);
			if (player.getDuelStatus() == 2) {
				player.getPA().sendFrame126("Waiting for other player...", 6684);
				other.getPA().sendFrame126("Other player has accepted.", 6684);
			}
			if (other.getDuelStatus() == 2) {
				other.getPA().sendFrame126("Waiting for other player...", 6684);
				player.getPA().sendFrame126("Other player has accepted.", 6684);
			}

			if (player.getDuelStatus() == 2 && other.getDuelStatus() == 2) {
				player.canOffer = false;
				other.canOffer = false;
				player.setDuelStatus(3);
				other.setDuelStatus(3);
				player.getTradeAndDuel().confirmDuel();
				other.getTradeAndDuel().confirmDuel();
			}
		} else {
			Player o = player.getTradeAndDuel().getPartnerDuel();
			player.getTradeAndDuel().declineDuel(false);
			if (o != null) {
				o.getTradeAndDuel().declineDuel(false);
			}
		}

	}

	public void toggleAntiScamManually() {
		InterfaceAssistant.closeDialogueOnly(player);
		player.getTradeAndDuel().toggleRule(TradeAndDuel.ANTI_SCAM, true);
		player.getTradeAndDuel().toggleAntiScanSetUp();

	}
}
