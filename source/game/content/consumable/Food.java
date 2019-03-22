package game.content.consumable;

import java.util.HashMap;

import core.GameType;
import core.ServerConstants;
import game.bot.BotContent;
import game.content.combat.Combat;
import game.item.ItemAssistant;
import game.player.Player;

/**
 * @author Sanity
 */

public class Food {

	public static enum FoodToEat {
		//Fruit
		BANANA(1963, 2, "Banana", 0, "Food"),
		ORANGE(2108, 2, "Orange", 0, "Food"),
		PINEAPPLE_CHUNKS(2116, 2, "Pineapple chunks", 0, "Food"),
		PINEAPPLE_RING(2118, 2, "Pineapple ring", 0, "Food"),
		PAPAYA_FRUIT(5972, 2, "Papaya", 0, "Food"),
		PEACH(6883, 8, "Peach", 0, "Food"),
		LEMON(2102, 2, "Lemon", 0, "Food"),
		LIME(2120, 2, "Lime", 0, "Food"),
		SPINACH(1969, 2, "Spinach", 0, "Food"),
		EDIBLE_SEAWEED(403, 4, "Edible seaweed", 0, "Food"),

		//Vegetables
		CABBAGE(1965, 1, "Cabbage", 0, "Food"),
		POTATO(1942, 1, "Potato", 0, "Food"),

		//Cakes
		CAKE(1891, 4, "Cake", 1893, "Food"),
		CAKE_TWO_THIRDS(1893, 4, "2/3 cake", 1895, "Food"),
		SLICE_OF_CAKE(1895, 4, "Slice of cake", 0, "Food"),
		CHOCOLATE_CAKE(1897, 4, "Cake", 1899, "Food"),
		CHOCOLATE_CAKE_TWO_THIRDS(1899, 4, "2/3 chocolate cake", 1901, "Food"),
		SLICE_OF_CHOCOLATE_CAKE(1901, 4, "Slice of chocolate cake", 0, "Food"),

		//Misc
		PURPLE_SWEETS(10476, 3, "Purple sweets", 0, "Food"),
		PUMPKIN(1959, 23, "Pumpkin", 0, "Food"),
		EASTER_EGG(1961, 23, "Easter egg", 0, "Food"),
		CHOCOLATE_BAR(1973, 2, "Chocolate bar", 0, "Food"),
		CHEESE(1985, 2, "Cheese", 0, "Food"),
		STEW(2003, 11, "Stew", 1923, "Food"),
		CURRY(2011, 19, "Curry", 1923, "Food"),
		SEAWEED(403, 4, "Edible seaweed", 0, "Food"),
		BREAD(2309, 5, "Bread", 0, "Food"),

		//Meat
		COOKED_CHICKEN(2140, 3, "Cooked chicken", 0, "Food"),
		COOKED_MEAT(2142, 3, "Cooked meat", 0, "Food"),
		ROAST_BIRD_MEAT(9980, 6, "Roast bird meat", 0, "Food"),

		//Potatos
		BAKED_POTATO(6701, 4, "Baked potato", 0, "Food"),
		POTATO_WITH_CHEESE(6705, 16, "Potato with Cheese", 0, "Food"),
		EGG_POTATO(7056, 16, "Egg potato", 0, "Food"),
		CHILLI_POTATO(7054, 14, "Chilli potato", 0, "Food"),
		MUSHROOM_POTATO(7058, 20, "Mushroom potato", 0, "Food"),
		TUNA_POTATO(7060, 22, "Tuna potato", 0, "Food"),
		POTATO_WITH_BUTTER(6703, 14, "Potato with Butter", 0, "Food"),

		PLAIN_PIZZA(2289, 7, "Pizza", 2291, "Food"),
		HALF_PLAIN_PIZZA(2291, 7, "1/2 pizza", 0, "Food"),
		MEAT_PIZZA(2293, 8, "Meat pizza", 2295, "Food"),
		HALF_MEAT_PIZZA(2295, 8, "1/2 meat pizza", 0, "Food"),
		ANCHOVY_PIZZA(2297, 8, "Anchovy pizza", 2299, "Food"),
		HALF_ANCHOVY_PIZZA(2299, 8, "1/2 anchovy pizza", 0, "Food"),
		PINEAPPLE_PIZZA(2301, 11, "Pineapple pizza", 2303, "Food"),
		HALF_PINEAPPLE_PIZZA(2303, 11, "1/2 pineapple pizza", 0, "Food"),

		//Pies
		APPLE_PIE(2323, 7, "Apple pie", 2335, "Food"),
		HALF_APPLE_PIE(2335, 7, "Half apple pie", 2313, "Food"),
		REDBERRY_PIE(2325, 5, "Redberry pie", 2333, "Food"),
		HALF_REDBERRY_PIE(2333, 5, "Half redberry pie", 2313, "Food"),
		MEAT_PIE(2327, 6, "Meat pie", 2331, "Food"),
		HALF_MEAT_PIE(2331, 6, "Half meat pie", 2313, "Food"),
		SUMMER_PIE(7218, 11, "Summer pie", 7220, "Food"),
		HALF_SUMMER_PIE(7220, 11, "Half Summer Pie", 2313, "Food"),

		//Drinks
		Wine(1993, 11, "Wine", 1935, "Drink"),

		//Fish
		SHRIMPS(315, 3, "Shrimps", 0, "Food"),
		SARDINE(325, 4, "Sardine", 0, "Food"),
		ANCHOVIES(319, 1, "Anchovies", 0, "Food"),
		HERRING(347, 5, "Herring", 0, "Food"),
		MACKEREL(355, 6, "Mackerel", 0, "Food"),
		TROUT(333, 7, "Trout", 0, "Food"),
		COD(339, 7, "Cod", 0, "Food"),
		PIKE(351, 8, "Pike", 0, "Food"),
		SALMON(329, 9, "Salmon", 0, "Food"),
		TUNA(361, 10, "Tuna", 0, "Food"),
		LOBSTER(379, 12, "Lobster", 0, "Food"),
		BASS(365, 13, "Bass", 0, "Food"),
		SWORDFISH(373, 14, "Swordfish", 0, "Food"),
		MONKFISH(7946, 16, "Monkfish", 0, "Food"),
		SHARK(385, 20, "Shark", 0, "Food"),
		SEA_TURTLE(397, 21, "Sea turtle", 0, "Food"),
		DARK_CAB(11936, 22, "Dark crab", 0, "Food"),
		ANGLERFISH(13441, 22, "Anglerfish", 0, "Food"),
		MANTA_RAY(391, 22, "Manta ray", 0, "Food"),

		ROCKTAIL(15272, 23, "Rocktail", 0, "Food");


		private int id;

		private int heal;

		private String name;

		private int replace;

		private String type;

		private FoodToEat(int id, int heal, String name, int replaceWith, String type) {
			this.id = id;
			this.heal = heal;
			this.name = name;
			replace = replaceWith;
			this.type = type;
		}

		public int getId() {
			return id;
		}

		public int getHeal() {
			return heal;
		}

		public String getName() {
			return name;
		}

		public int replaceWith() {
			return replace;
		}

		private String getType() {
			return type;
		}

		public static HashMap<Integer, FoodToEat> food = new HashMap<Integer, FoodToEat>();

		static {
			for (FoodToEat f : FoodToEat.values())
				food.put(f.getId(), f);
		}
	}

	public static void eat(Player player, int id, int slot) {
		FoodToEat food = FoodToEat.food.get(id);
		BotContent.addBotDebug(player, "Here31.5");
		if (food.getName().contains("pizza")) {
			Potions.consumePizza(player, food.getId(), slot);
			return;
		}

		if (player.duelRule[6]) {
			player.getPA().sendMessage("You may not eat in this duel.");
			return;
		}
		BotContent.addBotDebug(player, "Here31.6");
		if (player.getDead()) {
			player.getPA().sendMessage("You are unable to eat whilst dead.");
			return;
		}
		if (System.currentTimeMillis() - player.cannotEatDelay < 1700) {
			return;
		}

		boolean skipFoodDelayBecauseLasteAtePizza = false;
		if (System.currentTimeMillis() - player.pizzaDelay <= 700) {
			skipFoodDelayBecauseLasteAtePizza = true;
			player.pizzaDelay = 0;
		}
		if (System.currentTimeMillis() - player.foodDelay < 1700 && !skipFoodDelayBecauseLasteAtePizza) {
			return;
		}
		BotContent.addBotDebug(player, "Here31.7");
		if (player.doingAnAction()) {
			return;
		}
		if (food.replaceWith() > 0) {
			ItemAssistant.addItemToInventory(player, food.replaceWith(), 1, slot, false);
		}
		BotContent.addBotDebug(player, "Here31.8: " + (System.currentTimeMillis() - player.foodDelay));
		BotContent.addBotDebug(player, "Here31.9");
		BotContent.handleSafing(player);
		player.getPA().stopAllActions();
		Combat.resetPlayerAttack(player);
		player.setAttackTimer(player.getAttackTimer() + 2);
		player.startAnimation(829);
		ItemAssistant.deleteItemFromInventory(player, id, slot, 1);
		boolean healed = false;
		if (GameType.isPreEoc()) {
			int health = getPreEocHealth(player, id);
			if (player.currentCombatSkillLevel[ServerConstants.HITPOINTS] < health) {
				if (player.currentCombatSkillLevel[ServerConstants.HITPOINTS] < health) {
					healed = true;
				}
				player.currentCombatSkillLevel[ServerConstants.HITPOINTS] += food.getHeal();
				if (player.currentCombatSkillLevel[ServerConstants.HITPOINTS] > health) {
					player.currentCombatSkillLevel[ServerConstants.HITPOINTS] = health;
				}
			}
		} else {
			if (player.currentCombatSkillLevel[ServerConstants.HITPOINTS] < (food.getId() == 13441
					? (player.getBaseHitPointsLevel() * 1.23) : player.getBaseHitPointsLevel())) {
				if (player.currentCombatSkillLevel[ServerConstants.HITPOINTS] < player
						.getBaseHitPointsLevel()) {
					healed = true;
				}
				player.currentCombatSkillLevel[ServerConstants.HITPOINTS] +=
						getHealAmount(player.baseSkillLevel[ServerConstants.HITPOINTS], food);
				if (player.currentCombatSkillLevel[ServerConstants.HITPOINTS] > (food.getId() == 13441
						? (player.getBaseHitPointsLevel() * 1.23) : player.getBaseHitPointsLevel())) {
					player.currentCombatSkillLevel[ServerConstants.HITPOINTS] =
							(int) (food.getId() == 13441 ? (player.getBaseHitPointsLevel() * 1.23)
									: player.getBaseHitPointsLevel());
				}
			}
		}
		player.skillTabMainToUpdate.add(ServerConstants.HITPOINTS);
		RegenerateSkill.storeBoostedTime(player, ServerConstants.HITPOINTS);

		if (skipFoodDelayBecauseLasteAtePizza) {
			player.pizzaDelayOther = System.currentTimeMillis();
		}
		player.foodDelay = System.currentTimeMillis();
		player.soundToSend = 317;
		player.soundDelayToSend = 400;
		player.foodAte++;
		player.setInventoryUpdate(true);
		if (food.getType().equalsIgnoreCase("Food")) {
			player.queuedPacketMessage.add("You eat the " + food.getName() + ".");
		} else if (food.getType().equalsIgnoreCase("Drink")) {
			player.queuedPacketMessage.add("You drink the " + food.getName() + ".");
		}
		if (healed) {
			player.queuedPacketMessage.add("It heals some health.");
		}
		if (food.getId() == FoodToEat.CABBAGE.getId()) {
			player.queuedPacketMessage.add("Yuck!");
		}
	}


	private static int getHealAmount(int hp, FoodToEat food) {
		if (food.getName().equals("Anglerfish")) {
			if (hp >= 93) {
				return 22;
			}
			if (hp >= 90) {
				return 17;
			}
			if (hp >= 80) {
				return 16;
			}
			if (hp >= 75) {
				return 15;
			}
			if (hp >= 70) {
				return 13;
			}
			if (hp >= 60) {
				return 12;
			}
			if (hp >= 50) {
				return 11;
			}
			if (hp >= 40) {
				return 8;
			}
			if (hp >= 30) {
				return 7;
			}
			if (hp >= 25) {
				return 6;
			}
			if (hp >= 20) {
				return 4;
			}
			return 3;
		}
		return food.getHeal();
	}

	private static int getPreEocHealth(Player player, int id) {
		int extra = 0;
		if (player.playerEquipment[ServerConstants.LEG_SLOT] == 20_143
				|| player.playerEquipment[ServerConstants.LEG_SLOT] == 20_167
				|| player.playerEquipment[ServerConstants.LEG_SLOT] == 20_155) {
			extra += 13;
		} else if (player.playerEquipment[ServerConstants.BODY_SLOT] == 20_139
				|| player.playerEquipment[ServerConstants.BODY_SLOT] == 20_163
				|| player.playerEquipment[ServerConstants.BODY_SLOT] == 20_151) {
			extra += 20;
		} else if (player.playerEquipment[ServerConstants.HEAD_SLOT] == 20_135
				|| player.playerEquipment[ServerConstants.HEAD_SLOT] == 20_159
				|| player.playerEquipment[ServerConstants.HEAD_SLOT] == 20_147) {
			extra += 7;
		}
		if (id == 15272) {
			extra += 10;
		}
		return player.getBaseHitPointsLevel() + extra;
	}

	public static boolean isFood(int id) {
		return FoodToEat.food.containsKey(id);
	}

}
