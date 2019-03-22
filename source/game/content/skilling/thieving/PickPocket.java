package game.content.skilling.thieving;

import core.GameType;
import core.ServerConstants;
import game.content.combat.Combat;
import game.content.miscellaneous.RandomEvent;
import game.content.music.SoundSystem;
import game.content.skilling.Skilling;
import game.content.skilling.SkillingStatistics;
import game.item.ItemAssistant;
import game.log.CoinEconomyTracker;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import utility.Misc;

/**
 * Thieving system.
 *
 * @author MGT Madness, created on 24-01-2015.
 **/

public class PickPocket {

	public static enum PickPocketData {
		//@formatter:off
		HUMAN(3078, 1, 8, 5, 1, new int[][] {{995, 30}}, 40),
		FARMER(3086, 10, 15, 5, 1, new int[][] {{995, 90}}, 60),
		KHARID_WARRIOR(3103, 25, 26, 5, 2, new int[][] {{995, 180}}, 75),
		MASTER_FARMER(3257, 38, 43, 5, 3,
		              new int[][] {{5291, 1}, {5292, 1}, {5293, 1}, {5294, 1}, {5295, 1}, {5296, 1}, {5297, 1}, {5298, 1}, {5299, 1}, {5300, 1}, {5301, 1}, {5302, 1}, {5303, 1},
				              {5304, 1}
		              }, 95),
		GUARD(3094, 40, 46, 5, 2, new int[][] {{995, 500}}, 110),
		KNIGHT_OF_ARDOUGNE(3108, 55, 84, 5, 3, new int[][] {{995, 500}}, 125),
		PALADIN(3104, 70, 151, 5, 3, new int[][] {{995, 800}, {562, 2}}, 150),
		HERO(3106, 80, 275, 6, 4, new int[][] {{995, 2000}, {995, 3000}, {565, 1}, {1601, 1}, {560, 2}}, 175);

		//@formatter:on
		private int npcId;

		private int levelRequired;

		private int experience;

		private int stunDuration;

		private int stunDamage;

		private int[][] loot;

		private int currencyAmountLoot;

		private PickPocketData(int npcId, int levelRequired, int experience, int stunDuration, int stunDamage, int[][] loot, int currencyAmountLoot) {
			this.npcId = npcId;
			this.levelRequired = levelRequired;
			this.experience = experience;
			this.stunDuration = stunDuration;
			this.stunDamage = stunDamage;
			this.loot = loot;
			this.currencyAmountLoot = currencyAmountLoot;
		}

		public int getNpcId() {
			return npcId;
		}

		public int getLevelRequired() {
			return levelRequired;
		}

		public int getExperience() {
			return experience;
		}

		public int getStunDuration() {
			return stunDuration;
		}

		public int getStunDamage() {
			return stunDamage;
		}

		public int[][] getLoot() {
			return loot;
		}

		public int getCurrencyAmountLoot() {
			return currencyAmountLoot;
		}


	}


	/**
	 * Messages the man says when catching out player.
	 */
	private static String[] chat =
			{"Get off me thief!", "Somebody help me!", "You never learn!", "Take this and never come back!", "Leave me alone!", "How dare you!", "Stay away from me peasant!"};

	private final static int[] seedsChance =
			{100, 85, 75, 65, 55, 45, 35, 30, 25, 20, 15, 10, 6, 3};

	/**
	 * Start of pickpocketing system.
	 *
	 * @param player The associated player.
	 */
	public static void pickPocket(Player player, PickPocketData data) {
		if (player.getTransformed() != 0) {
			return;
		}
		if (System.currentTimeMillis() - player.lastThieve < 1200) {
			return;
		}
		player.lastThieve = System.currentTimeMillis();
		if (player.baseSkillLevel[ServerConstants.THIEVING] < data.getLevelRequired()) {
			player.getDH().sendStatement("You need " + data.getLevelRequired() + " thieving to pickpocket this npc.");
			return;
		}
		if (pickPocketFailure(player, data, "You fail to pickpocket.")) {
			return;
		}
		successfulPickPocket(player, data);
	}

	private static boolean wearingFullRogue(Player player) {
		if (ItemAssistant.hasItemEquippedSlot(player, 5554, ServerConstants.HEAD_SLOT) && ItemAssistant.hasItemEquippedSlot(player, 5553, ServerConstants.BODY_SLOT)
		    && ItemAssistant.hasItemEquippedSlot(player, 5555, ServerConstants.LEG_SLOT) && ItemAssistant.hasItemEquippedSlot(player, 5557, ServerConstants.FEET_SLOT)
		    && ItemAssistant.hasItemEquippedSlot(player, 5556, ServerConstants.HAND_SLOT)) {
			return true;
		}
		return false;
	}

	/**
	 * @param player The associated player.
	 * @return True, if the pickpocket is a failure.
	 */
	public static boolean pickPocketFailure(final Player player, final PickPocketData data, String failMessage) {
		int level = player.baseSkillLevel[ServerConstants.THIEVING];
		if (level <= 6) {
			level = 6;
		}
		int random = Misc.random(level / 2);
		Npc npc = null;
		if (data != null) {
			npc = NpcHandler.npcs[player.getNpcClickIndex()];
			if (npc.isDead()) {
				return true;
			}
		}

		final int damage = data == null ? 2 : data.getStunDamage();
		final int stunDuration = data == null ? 5 : data.getStunDuration();
		if (random == 0) {
			if (data != null) {
				npc.facePlayer(player.getPlayerId());
				npc.forceChat(chat[Misc.random(4)]);
				NpcHandler.startAnimation(npc, 401);
			}
			player.doingActionEvent((int) (stunDuration / 0.6));
			CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
				@Override
				public void execute(CycleEventContainer container) {
					container.stop();
				}

				@Override
				public void stop() {
					player.startAnimation(404);
					player.gfx(254, 110);
					player.forcedChat("Ow!", false, false);
					SoundSystem.sendSound(player, 69, 100);
					Combat.createHitsplatOnPlayerNormal(player, damage, ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.NO_ICON);
				}
			}, 1);

			player.getPA().sendFilterableMessage(failMessage);
			return true;
		}
		return false;
	}

	/**
	 * A successful pickpoket.
	 *
	 * @param player The associated player.
	 */
	private static void successfulPickPocket(Player player, PickPocketData data) {
		if (RandomEvent.isBannedFromSkilling(player)) {
			return;
		}
		int random = Misc.random(data.getLoot().length - 1);
		int item = data.getLoot()[random][0];
		if (data.equals(PickPocketData.MASTER_FARMER)) {

			int random1 = Misc.random(1, 100);
			for (int index = seedsChance.length - 1; index >= 0; index--) {
				if (random1 <= seedsChance[index]) {
					item = data.getLoot()[index][0];
					break;
				}
			}
		}
		int farmer = 1;
		if (Misc.hasOneOutOf(3)) {
			farmer = 2;
		}
		int itemAmount = data.equals(PickPocketData.MASTER_FARMER) ? farmer : data.getLoot()[random][1];
		double chance = 20.0;

		boolean roguePerkApplied = false;

		if (Misc.hasPercentageChance(15)) {
			if (wearingFullRogue(player)) {
				roguePerkApplied = true;
			}
		}

		if (roguePerkApplied) {
			itemAmount *= 2;
		}
		if (Skilling.hasMasterCapeWorn(player, 9777) && Misc.hasPercentageChance(10)) {
			player.getPA().sendMessage("<col=a54704>Your cape allows you to gain double the amount of loot.");
			itemAmount *= 2;
		}
		if (ItemAssistant.addItem(player, item, itemAmount)) {
			player.startAnimation(881);
			Skilling.addSkillExperience(player, data.getExperience(), ServerConstants.THIEVING, false);
			Skilling.petChance(player, data.getExperience(), 275, 13000, ServerConstants.THIEVING, null);
			player.getPA().sendFilterableMessage("You pickpocket some loot.");

		}
		if (Misc.hasOneOutOf(chance)) {
			int lootAmount = data.getCurrencyAmountLoot() * (GameType.isOsrsPvp() ? 1 : 300);
			if (roguePerkApplied) {
				lootAmount *= 2;
			}
			ItemAssistant.addItem(player, ServerConstants.getMainCurrencyId(), lootAmount);
			CoinEconomyTracker.addIncomeList(player, "SKILLING " + lootAmount);
			player.getPA().sendFilterableMessage("You find some " + ServerConstants.getMainCurrencyName().toLowerCase() + "!");
		}
		player.skillingStatistics[SkillingStatistics.PICKPOCKETS]++;
	}

}
