package game.item;

import core.GameType;
import core.ServerConstants;
import game.content.skilling.summoning.Summoning;
import game.player.Player;

/**
 * Item requirements.
 *
 * @author MGT Madness, created on 12-10-2015.
 */
public class ItemRequirement {

	/**
	 * @param player The associated player.
	 * @param itemEquipmentSlot The equipment slot used by the item.
	 * @return True, if the player has the level requirements to wear the item.
	 */
	public static boolean hasItemRequirement(Player player, int itemEquipmentSlot) {
		for (int i = 0; i < player.itemRequirement.length; i++) {
			if (player.baseSkillLevel[i] < player.itemRequirement[i]) {
				String verb = itemEquipmentSlot == ServerConstants.WEAPON_SLOT ? "wield" : "wear";
				player.playerAssistant.sendMessage("You need a " + ServerConstants.SKILL_NAME[i] + " level of " + player.itemRequirement[i] + " to " + verb + " this item.");
				return false;
			}
		}

		return true;
	}

	/**
	 * Set the item requirement variables.
	 *
	 * @param player The associated player.
	 * @param itemName The name of the item that will have it's item requirement variables set.
	 * @param itemId The identity of the item that will have it's item requirement variables set.
	 **/
	public static void setItemRequirements(Player player, String itemName, int itemId) {
		for (int i = 0; i < player.itemRequirement.length; i++) {
			player.itemRequirement[i] = 1;
		}
		if (setOsrsItemRequirements(player, itemName, itemId)) {
			return;
		}
		if (setPreEocItemRequirements(player, itemName, itemId)) {
			return;
		}
		switch (itemId) {
			case 10010: // Butterfly net
				player.itemRequirement[ServerConstants.HUNTER] = 15;
				return;
			case 22616: // Vesta's chainbody
			case 22619: // Vesta's plateskirt
			case 22628: // Statius's platebody
			case 22631: // Statius's platelegs
			case 22625: // Statius's full helm

			case 22650: // Zuriel's hood
			case 22653: // Zuriel's robe top
			case 22656: // Zuriel's robe bottom

			case 22638: // Morrigan's coif
			case 22641: // Morrigan's leather body
			case 22644: // Morrigan's leather chaps
				player.itemRequirement[ServerConstants.DEFENCE] = 78;
				return;

			case 22610: // Vesta's spear
			case 22647: // Zuriel's staff
			case 22613: // Vesta's longsword
			case 22622: // Statius's warhammer
				player.itemRequirement[ServerConstants.ATTACK] = 78;
				return;

			case 22636: // Morrigan's javelin
			case 22634: // Morrigan's throwing axe
				player.itemRequirement[ServerConstants.RANGED] = 78;
				return;


			case 21742: // Granite hammer
			case 21646: // Granite longsword
				player.itemRequirement[ServerConstants.ATTACK] = 50;
				player.itemRequirement[ServerConstants.STRENGTH] = 50;
				return;

			case 4214: // Crystal bow
				player.itemRequirement[ServerConstants.RANGED] = 70;
				return;

			case 22550: // Craw's bow
			case 22547: // Craw's bow (u)
				player.itemRequirement[ServerConstants.RANGED] = 60;
				return;

			case 6562: // Mud battlestaff
			case 3053: // Lava battlestaff
			case 21198: // Lava battlestaff (or)
				player.itemRequirement[ServerConstants.ATTACK] = 30;
				player.itemRequirement[ServerConstants.MAGIC] = 30;
				return;
			case 10446: // Saradomin cloak
			case 10448: // Guthix cloak
			case 10450: // Zamorak cloak
				player.itemRequirement[ServerConstants.PRAYER] = 40;
				return;

			case 11061: // Ancient mace
				player.itemRequirement[ServerConstants.ATTACK] = 15;
				player.itemRequirement[ServerConstants.PRAYER] = 25;
				return;
			case 6526: // Toktz-mej-tal
				player.itemRequirement[ServerConstants.MAGIC] = 60;
				player.itemRequirement[ServerConstants.ATTACK] = 60;
				return;
			case 6523: // Toktz-xil-ak
				player.itemRequirement[ServerConstants.ATTACK] = 60;
				return;
			case 4125: // Black boots
				player.itemRequirement[ServerConstants.DEFENCE] = 10;
				return;
			case 1097: // Studded chaps
			case 1169: // Coif
				player.itemRequirement[ServerConstants.RANGED] = 20;
				return;

			case 2417: // Zamorak staff
			case 2415: // Saradomin staff
			case 2416: // Guthix staff
				player.itemRequirement[ServerConstants.MAGIC] = 60;
				return;
			case 4225: // Crystal shield full
				player.itemRequirement[ServerConstants.DEFENCE] = 70;
				return;

			case 12437: // 3rd age cloak
				player.itemRequirement[ServerConstants.PRAYER] = 65;
				return;
			// Black santahat.
			case 10402: // Black ele legs
				return;

			case 8847: // Black defender
			case 1131: // Hard leatherbody
				player.itemRequirement[ServerConstants.DEFENCE] = 10;
				return;

			case 4170: //slayer staff
				player.itemRequirement[ServerConstants.MAGIC] = 50;
				player.itemRequirement[ServerConstants.SLAYER] = 55;
				return;

			case 7456: // Steel gloves
			case 7457: // Black gloves
			case 7458: // Mithril gloves
				player.itemRequirement[ServerConstants.DEFENCE] = 1;
				return;

			case 7462: // Barrows gloves.
			case 7461: // Dragon gloves.
				player.itemRequirement[ServerConstants.DEFENCE] = 41;
				return;

			case 7460: // Rune gloves.
				player.itemRequirement[ServerConstants.DEFENCE] = 34;
				return;

			case 7459: // Addy gloves
				player.itemRequirement[ServerConstants.DEFENCE] = 13;
				return;
		}

		if (itemName.equals("kodai wand")) {
			player.itemRequirement[ServerConstants.MAGIC] = 75;
			return;
		}
		if (itemName.contains("ancestral")) {
			player.itemRequirement[ServerConstants.MAGIC] = 75;
			player.itemRequirement[ServerConstants.DEFENCE] = 65;
			return;
		}
		if (itemName.contains("cane") && !itemName.contains("arcane")) {
			return;
		}
		if (itemId >= 2653 && itemId <= 2676 || itemName.contains("gilded") || itemId >= 3476 && itemId <= 3489
		    || itemId >= 12460 && itemId <= 12488) // All God rune armour such as Zamorak platebody.
		{
			player.itemRequirement[ServerConstants.DEFENCE] = 40;
			return;
		}
		// All god hide armour
		if (itemId >= 10368 && itemId <= 10391 || itemId >= 12490 && itemId <= 12512) {
				player.itemRequirement[ServerConstants.DEFENCE] = 40;
			player.itemRequirement[ServerConstants.RANGED] = 70;
			return;
		}
		if (itemId >= 10547 && itemId <= 10555) // All penance queen minigame 40 defence items.
		{
			player.itemRequirement[ServerConstants.DEFENCE] = 40;
			return;
		} else if (itemName.equals("ava's accumulator")) {
			player.itemRequirement[ServerConstants.RANGED] = 50;
		} else if (itemName.equals("heavy ballista")) {
			player.itemRequirement[ServerConstants.RANGED] = 75;
		} else if (itemName.contains("studded body")) {
			player.itemRequirement[ServerConstants.DEFENCE] = 20;
		} else if (itemName.equals("light ballista")) {
			player.itemRequirement[ServerConstants.RANGED] = 65;
		} else if (itemName.contains(" stole")) {
			player.itemRequirement[ServerConstants.PRAYER] = 60;
		} else if (itemName.contains("crozier")) {
			player.itemRequirement[ServerConstants.PRAYER] = 60;
		} else if (itemName.equals("dragon warhammer")) {
			player.itemRequirement[ServerConstants.ATTACK] = 60;
		} else if (itemName.contains("granite") && !itemName.contains("maul")) {
			player.itemRequirement[ServerConstants.DEFENCE] = 50;
			player.itemRequirement[ServerConstants.STRENGTH] = 50;
		} else if (itemName.contains("twisted bow")) {
			player.itemRequirement[ServerConstants.RANGED] = 75;
		} else if (itemName.contains("snakeskin")) {
			player.itemRequirement[ServerConstants.DEFENCE] = 30;
			player.itemRequirement[ServerConstants.RANGED] = 30;
		} else if (itemName.equals("elder maul")) {
			player.itemRequirement[ServerConstants.ATTACK] = 75;
			player.itemRequirement[ServerConstants.STRENGTH] = 75;
		} else if (itemName.equals("eternal boots")) {
			player.itemRequirement[ServerConstants.MAGIC] = 75;
			player.itemRequirement[ServerConstants.DEFENCE] = 75;
		} else if (itemName.equals("primordial boots")) {
			player.itemRequirement[ServerConstants.STRENGTH] = 75;
			player.itemRequirement[ServerConstants.DEFENCE] = 75;
		} else if (itemName.equals("pegasian boots")) {
			player.itemRequirement[ServerConstants.RANGED] = 75;
			player.itemRequirement[ServerConstants.DEFENCE] = 75;
		} else if (itemName.equals("dragon dart")) {
			player.itemRequirement[ServerConstants.RANGED] = 60;
		} else if (itemName.equals("trident of the swamp")) {
			player.itemRequirement[ServerConstants.MAGIC] = 75;
		} else if (itemName.equals("dragon hunter crossbow")) {
			player.itemRequirement[ServerConstants.RANGED] = 65;
		} else if (itemName.equals("hunters' crossbow")) {
			player.itemRequirement[ServerConstants.RANGED] = 50;
		} else if (itemName.contains("abyssal dagger")) {
			player.itemRequirement[ServerConstants.ATTACK] = 70;
		} else if (itemName.equals("twisted buckler")) {
			player.itemRequirement[ServerConstants.RANGED] = 75;
			player.itemRequirement[ServerConstants.DEFENCE] = 75;
		} else if (itemName.equals("abyssal bludgeon")) {
			player.itemRequirement[ServerConstants.ATTACK] = 70;
			player.itemRequirement[ServerConstants.STRENGTH] = 70;
		} else if (itemName.equals("necklace of anguish") || itemName.contains("amulet of torture")) {
			player.itemRequirement[ServerConstants.HITPOINTS] = 75;
		} else if (itemName.contains("occult necklace")) {
			player.itemRequirement[ServerConstants.MAGIC] = 70;
		} else if (itemName.equals("trident of the swamp")) {
			player.itemRequirement[ServerConstants.MAGIC] = 75;
		} else if (itemName.equals("toxic blowpipe")) {
			player.itemRequirement[ServerConstants.RANGED] = 75;
		} else if (itemName.equals("dragon javelin")) {
		} else if (itemName.equals("serpentine helm") || itemName.equals("magma helm") || itemName.equals("tanzanite helm")) {
			player.itemRequirement[ServerConstants.DEFENCE] = 75;
		} else if (itemName.contains("staff of the dead")) {
			player.itemRequirement[ServerConstants.MAGIC] = 75;
			player.itemRequirement[ServerConstants.ATTACK] = 75;
		} else if (itemName.startsWith("dragonstone")) {
			player.itemRequirement[ServerConstants.DEFENCE] = 1;
		} else if (itemName.startsWith("proselyte")) {
			player.itemRequirement[ServerConstants.DEFENCE] = 30;
			player.itemRequirement[ServerConstants.PRAYER] = 20;
		} else if (itemName.startsWith("toktz-xil-ek")) {
			player.itemRequirement[ServerConstants.ATTACK] = 60;
		} else if (itemName.startsWith("rock-shell")) {
			player.itemRequirement[ServerConstants.DEFENCE] = 40;
		} else if (itemName.startsWith("spined")) {
			player.itemRequirement[ServerConstants.DEFENCE] = 50;
		} else if (itemName.startsWith("rune berserker shield")) {
			player.itemRequirement[ServerConstants.DEFENCE] = 45;
		} else if (itemName.startsWith("slayer helmet")) {
			player.itemRequirement[ServerConstants.DEFENCE] = 20;
		} else if (itemName.contains("mystic") || itemName.contains("enchanted")) {
			if (itemName.contains("staff")) {
				player.itemRequirement[ServerConstants.MAGIC] = 20;
				player.itemRequirement[ServerConstants.ATTACK] = 40;
			} else {
				player.itemRequirement[ServerConstants.MAGIC] = 20;
				player.itemRequirement[ServerConstants.DEFENCE] = 20;
			}
		} else if (itemName.contains("infinity")) {
			player.itemRequirement[ServerConstants.MAGIC] = 50;
			player.itemRequirement[ServerConstants.DEFENCE] = 25;
		} else if (itemName.contains("rune crossbow")) {
			player.itemRequirement[ServerConstants.RANGED] = 61;
		} else if (itemName.contains("oak") && itemName.contains("bow")) {
			player.itemRequirement[ServerConstants.RANGED] = 5;
		} else if (itemName.contains("willow") && itemName.contains("bow")) {
			player.itemRequirement[ServerConstants.RANGED] = 20;
		} else if (itemName.contains("maple") && itemName.contains("bow")) {
			player.itemRequirement[ServerConstants.RANGED] = 30;
		} else if (itemName.contains("yew") && itemName.contains("bow")) {
			player.itemRequirement[ServerConstants.RANGED] = 40;
		} else if (itemName.contains("magic") && itemName.contains("bow")) {
			player.itemRequirement[ServerConstants.RANGED] = 50;
		} else if (itemName.contains("steel knife")) {
			player.itemRequirement[ServerConstants.RANGED] = 5;
		} else if (itemName.contains("black knife")) {
			player.itemRequirement[ServerConstants.RANGED] = 10;
		} else if (itemName.contains("mithril knife")) {
			player.itemRequirement[ServerConstants.RANGED] = 20;
		} else if (itemName.contains("adamant knife")) {
			player.itemRequirement[ServerConstants.RANGED] = 30;
		} else if (itemName.contains("rune knife")) {
			player.itemRequirement[ServerConstants.RANGED] = 40;
		} else if (itemName.contains("splitbark")) {
			player.itemRequirement[ServerConstants.MAGIC] = 40;
			player.itemRequirement[ServerConstants.DEFENCE] = 40;
		} else if (itemName.contains("green")) {
			if (itemName.contains("hide")) {
				player.itemRequirement[ServerConstants.RANGED] = 40;
				if (itemName.contains("body")) {
					player.itemRequirement[ServerConstants.DEFENCE] = 40;
				}

			}
		} else if (itemName.contains("blue")) {
			if (itemName.contains("hide")) {
				player.itemRequirement[ServerConstants.RANGED] = 50;
				if (itemName.contains("body")) {
					player.itemRequirement[ServerConstants.DEFENCE] = 40;
				}

			}
		} else if (itemName.contains("red")) {
			if (itemName.contains("hide")) {
				player.itemRequirement[ServerConstants.RANGED] = 60;
				if (itemName.contains("body")) {
					player.itemRequirement[ServerConstants.DEFENCE] = 40;
				}

			}
		} else if (itemName.contains("black") && (itemName.contains("body") || itemName.contains("leg") || itemName.contains("shield") || itemName.contains("helm"))
		           && !itemName.contains("hide")) {
			player.itemRequirement[ServerConstants.DEFENCE] = 10;
		} else if (itemName.contains("black")) {
			if (itemName.contains("scimitar") || itemName.contains("dagger")) {
				player.itemRequirement[ServerConstants.ATTACK] = 10;
			} else if (itemName.contains("hide")) {
				player.itemRequirement[ServerConstants.RANGED] = 70;
				if (itemName.contains("body")) {
					player.itemRequirement[ServerConstants.DEFENCE] = 40;
				}

			}
		} else if (itemName.contains("bronze")) {
			if (!itemName.contains("knife") && !itemName.contains("dart") && !itemName.contains("javelin") && !itemName.contains("thrownaxe")) {
				player.itemRequirement[ServerConstants.ATTACK] = player.itemRequirement[ServerConstants.DEFENCE] = 1;
			}

		} else if (itemName.contains("iron")) {
			if (!itemName.contains("knife") && !itemName.contains("dart") && !itemName.contains("javelin") && !itemName.contains("thrownaxe")) {
				player.itemRequirement[ServerConstants.ATTACK] = player.itemRequirement[ServerConstants.DEFENCE] = 1;
			}

		} else if (itemName.contains("steel") && !itemName.contains("bolt")) {
			if (itemName.contains("sword") || itemName.contains("dagger") || itemName.contains("pickaxe") || itemName.contains("scimitar") || itemName.contains("axe")
			    || itemName.contains("hatchet")) {
				player.itemRequirement[ServerConstants.ATTACK] = 5;
			} else if (!itemName.contains("knife") && !itemName.contains("dart") && !itemName.contains("javelin") && !itemName.contains("thrownaxe") && !itemName.contains("'bow")
			           && !itemName.contains("arrow")) {
				player.itemRequirement[ServerConstants.DEFENCE] = 5;
			}

		} else if (itemName.contains("mithril") && !itemName.contains("bolt")) {
			if (itemName.contains("sword") || itemName.contains("dagger") || itemName.contains("pickaxe") || itemName.contains("scimitar") || itemName.contains("axe")
			    || itemName.contains("hatchet")) {
				player.itemRequirement[ServerConstants.ATTACK] = 20;
			} else if (!itemName.contains("knife") && !itemName.contains("dart") && !itemName.contains("javelin") && !itemName.contains("thrownaxe") && !itemName.contains("'bow")
			           && !itemName.contains("arrow")) {
				player.itemRequirement[ServerConstants.DEFENCE] = 20;
			}

		}

		// Adam because the trimmed adamant items is called "adam".
		else if (itemName.contains("adam") && !itemName.contains("bolt")) {
			if (itemName.contains("sword") || itemName.contains("dagger") || itemName.contains("pickaxe") || itemName.contains("scimitar") || itemName.contains("axe")
			    || itemName.contains("hatchet")) {
				player.itemRequirement[ServerConstants.ATTACK] = 30;
			} else if (!itemName.contains("knife") && !itemName.contains("dart") && !itemName.contains("javelin") && !itemName.contains("thrownaxe") && !itemName.contains("'bow")
			           && !itemName.contains("arrow")) {
				player.itemRequirement[ServerConstants.DEFENCE] = 30;
			}

		} else if (itemName.contains("rune") && !itemName.contains("bolt") && !itemName.contains("runecraf")) {
			if (itemName.contains("sword") || itemName.contains("dagger") || itemName.contains("pickaxe") || itemName.contains("scimitar") || itemName.contains("axe")
			    || itemName.contains("hatchet")) {
				player.itemRequirement[ServerConstants.ATTACK] = 40;
			} else if (!itemName.contains("knife") && !itemName.contains("dart") && !itemName.contains("javelin") && !itemName.contains("thrownaxe") && !itemName.contains("'bow")
			           && !itemName.contains("arrow")) {
				player.itemRequirement[ServerConstants.DEFENCE] = 40;
			}

		} else if (itemName.contains("dragon") && !itemName.contains("bolt") && !itemName.contains("arrow")) {
			if (itemName.contains("sword") || itemName.contains("scimitar") || itemName.contains("dagger") || itemName.contains("mace") || itemName.contains("axe")
			    || itemName.contains("halberd") || itemName.contains("claw") || itemName.contains("spear") || itemName.contains("hatchet")) {
				player.itemRequirement[ServerConstants.ATTACK] = 60;

			} else if (!itemName.contains("nti-") && !itemName.contains("fire")) {
				player.itemRequirement[ServerConstants.DEFENCE] = 60;

			} else if (itemName.contains("dragonfire shield")) {
				player.itemRequirement[ServerConstants.DEFENCE] = 75;
			}
		} else if (itemName.contains("ahrim")) {
			if (itemName.contains("staff")) {
				player.itemRequirement[ServerConstants.ATTACK] = 70;
			} else {
				player.itemRequirement[ServerConstants.DEFENCE] = 70;
			}
			player.itemRequirement[ServerConstants.MAGIC] = 70;
		} else if (itemName.contains("dagon")) {
			player.itemRequirement[ServerConstants.MAGIC] = 40;
			player.itemRequirement[ServerConstants.DEFENCE] = 20;
		} else if (itemName.contains("arcane stream necklace")) {
			player.itemRequirement[ServerConstants.MAGIC] = 70;
		} else if (itemName.contains("initiate")) {
			player.itemRequirement[ServerConstants.DEFENCE] = 20;
			player.itemRequirement[ServerConstants.PRAYER] = 10;
		} else if (itemName.contains("armadyl") && !itemName.contains("godsword")) {
			if (itemName.contains("plate") || itemName.contains("helmet") || itemName.contains("chainskirt")) {
				player.itemRequirement[ServerConstants.RANGED] = 70;
				player.itemRequirement[ServerConstants.DEFENCE] = 70;
			} else if (itemName.contains("crossbow")) {
				player.itemRequirement[ServerConstants.RANGED] = 70;
			}
		} else if (itemName.contains("karil")) {
			if (itemName.contains("crossbow")) {
				player.itemRequirement[ServerConstants.RANGED] = 70;
			} else {
				player.itemRequirement[ServerConstants.RANGED] = 70;
				player.itemRequirement[ServerConstants.DEFENCE] = 70;
			}
		} else if (itemName.contains("godsword")) {
			player.itemRequirement[ServerConstants.ATTACK] = 75;
		} else if (itemName.contains("3rd age mage hat") || itemName.contains("3rd age robe top") || itemName.contains("3rd age robe bottom") || itemName.contains(
				"3rd age amulet")) {
			player.itemRequirement[ServerConstants.DEFENCE] = 30;
			player.itemRequirement[ServerConstants.MAGIC] = 65;

		} else if (itemName.contains("3rd age range top") || itemName.contains("3rd age range legs") || itemName.contains("3rd age range coif") || itemName.contains(
				"3rd age vambraces")) {
			player.itemRequirement[ServerConstants.DEFENCE] = 45;
			player.itemRequirement[ServerConstants.RANGED] = 65;

		} else if (itemName.contains("3rd age") && !itemName.contains("sword") && !itemName.contains("wand") && !itemName.contains("bow") && !itemName.contains("axe")) {
			player.itemRequirement[ServerConstants.DEFENCE] = 65;
		} else if (itemName.contains("Initiate")) {
			player.itemRequirement[ServerConstants.DEFENCE] = 20;
		} else if (itemName.contains("verac") || itemName.contains("guthan") || itemName.contains("dharok") || itemName.contains("torag")) {
			if (itemName.contains("hammers")) {
				player.itemRequirement[ServerConstants.ATTACK] = 70;
				player.itemRequirement[ServerConstants.STRENGTH] = 70;
			} else if (itemName.contains("axe")) {
				player.itemRequirement[ServerConstants.ATTACK] = 70;
				player.itemRequirement[ServerConstants.STRENGTH] = 70;
			} else if (itemName.contains("warspear")) {
				player.itemRequirement[ServerConstants.ATTACK] = 70;
				player.itemRequirement[ServerConstants.STRENGTH] = 70;
			} else if (itemName.contains("flail")) {
				player.itemRequirement[ServerConstants.ATTACK] = 70;
				player.itemRequirement[ServerConstants.STRENGTH] = 70;
			} else {
				player.itemRequirement[ServerConstants.DEFENCE] = 70;
			}
		} else if (itemName.contains("void")) {
			player.itemRequirement[ServerConstants.ATTACK] = 42;
			player.itemRequirement[ServerConstants.RANGED] = 42;
			player.itemRequirement[ServerConstants.STRENGTH] = 42;
			player.itemRequirement[ServerConstants.MAGIC] = 42;
			player.itemRequirement[ServerConstants.DEFENCE] = 42;
		} else if (itemName.contains("ancient staff")) {
			player.itemRequirement[ServerConstants.ATTACK] = 50;
			player.itemRequirement[ServerConstants.MAGIC] = 50;
		} else if (itemName.contains("staff of light")) {
			player.itemRequirement[ServerConstants.ATTACK] = 75;
			player.itemRequirement[ServerConstants.MAGIC] = 75;
		} else if (itemName.contains("saradomin sword") || itemName.contains("blessed sword") || itemName.contains("zamorakian spear")) {
			player.itemRequirement[ServerConstants.ATTACK] = 70;
		} else if (itemName.contains("elysian spirit shield") || itemName.contains("divine spirit shield") || itemName.contains("arcane spirit shield") || itemName.contains(
				"spectral spirit shield")) {
			player.itemRequirement[ServerConstants.DEFENCE] = 75;
			player.itemRequirement[ServerConstants.PRAYER] = 75;
		} else if (itemName.contains("blessed spirit shield")) {
			player.itemRequirement[ServerConstants.DEFENCE] = 70;
			player.itemRequirement[ServerConstants.PRAYER] = 60;
		} else if (itemName.contains("spirit shield")) {
			player.itemRequirement[ServerConstants.DEFENCE] = 40;
			player.itemRequirement[ServerConstants.PRAYER] = 55;
		} else if (itemName.contains("fighter")) {
			player.itemRequirement[ServerConstants.DEFENCE] = 40;
		} else if (itemName.equals("dark bow") || itemName.contains("toktz-xil-ul")) {
			player.itemRequirement[ServerConstants.RANGED] = 60;
		} else if (itemName.contains("toktz-ket-xil")) {
			player.itemRequirement[ServerConstants.DEFENCE] = 60;
		} else if (itemName.contains("master wand") || itemName.contains("mages' book")) {
			player.itemRequirement[ServerConstants.MAGIC] = 60;
		} else if (itemName.contains("helm of neitiznot")) {
			player.itemRequirement[ServerConstants.DEFENCE] = 55;
		} else if (itemName.contains("bandos chestplate") || itemName.contains("bandos tassets") || itemName.contains("bandos boots")) {
			player.itemRequirement[ServerConstants.DEFENCE] = 65;
		} else if (itemName.contains("berserker helm") || itemName.contains("archer helm") || itemName.contains("farseer helm") || itemName.contains("warrior helm")) {
			player.itemRequirement[ServerConstants.DEFENCE] = 45;
		} else if (itemName.contains("zamorak cape") || itemName.contains("saradomin cape") || itemName.contains("guthix cape")) {
			player.itemRequirement[ServerConstants.MAGIC] = 60;
		} else if (itemName.contains("steel defender")) {
			player.itemRequirement[ServerConstants.DEFENCE] = 5;
		} else if (itemName.contains("black defender")) {
			player.itemRequirement[ServerConstants.DEFENCE] = 10;
		} else if (itemName.contains("mithril defender")) {
			player.itemRequirement[ServerConstants.DEFENCE] = 20;
		} else if (itemName.contains("adamant defender")) {
			player.itemRequirement[ServerConstants.DEFENCE] = 30;
		} else if (itemName.contains("rune defender")) {
			player.itemRequirement[ServerConstants.DEFENCE] = 40;
		} else if (itemName.contains("barrelchest anchor")) {
			player.itemRequirement[ServerConstants.STRENGTH] = 40;
			player.itemRequirement[ServerConstants.ATTACK] = 60;
		} else if (itemName.contains("abyssal whip")) {
			player.itemRequirement[ServerConstants.ATTACK] = 70;
		} else if (itemName.contains("abyssal tentacle")) {
			player.itemRequirement[ServerConstants.ATTACK] = 75;
		} else if (itemName.contains("granite maul")) {
			player.itemRequirement[ServerConstants.ATTACK] = 50;
			player.itemRequirement[ServerConstants.STRENGTH] = 50;
		} else if (itemName.contains("tzhaar-ket-om")) {
			player.itemRequirement[ServerConstants.STRENGTH] = 60;
		}
	}

	private static boolean setPreEocItemRequirements(Player player, String itemName, int itemId) {
		if (!GameType.isPreEoc()) {
			return false;
		}
		switch (itemId) {
			case 15_241: // hand cannon
				player.itemRequirement[ServerConstants.RANGED] = 75;
				player.itemRequirement[ServerConstants.FIREMAKING] = 61;
				return true;
			case 18_359:
			case 18_361:
			case 18_363: // chaotic shields
				player.itemRequirement[ServerConstants.DEFENCE] = 80;
				return true;
			case 18_349:
			case 18_351:
			case 18_353: // chaotic weapons
				player.itemRequirement[ServerConstants.ATTACK] = 80;
				return true;
			case 18_355: // chaotic staff
				player.itemRequirement[ServerConstants.MAGIC] = 80;
				return true;
			case 18_357: // chaotic crossbow
				player.itemRequirement[ServerConstants.RANGED] = 80;
				return true;
			case 19_669: // ring of rigour
				player.itemRequirement[ServerConstants.ATTACK] = 62;
				return true;
			case 18_347: // Mercenary's gloves
				player.itemRequirement[ServerConstants.RANGED] = 73;
				return true;
			case Summoning.SPIRIT_CAPE:
				player.itemRequirement[ServerConstants.DEFENCE] = 50;
				player.itemRequirement[ServerConstants.SUMMONING] = 50;
				return true;
			case 18_340: // anti poison totem
				player.itemRequirement[ServerConstants.DEFENCE] = 60;
				player.itemRequirement[ServerConstants.HERBLORE] = 70;
				return true;
			case 18_346: // tome of frost
				player.itemRequirement[ServerConstants.MAGIC] = 48;
				return true;
			case 18_365:
			case 18_367:
			case 18_369: // gravite weapons
				player.itemRequirement[ServerConstants.ATTACK] = 45;
				return true;
			case 18_371: // gravite staff
				player.itemRequirement[ServerConstants.MAGIC] = 45;
				return true;
			case 18_373: // gravite bow
				player.itemRequirement[ServerConstants.RANGED] = 45;
				return true;
			case 14_490:
			case 14_492:
			case 14_494: // black elite
				player.itemRequirement[ServerConstants.DEFENCE] = 40;
				return true;
			case 14_497:
			case 14_499:
			case 14_501: // dagon'hai
				player.itemRequirement[ServerConstants.MAGIC] = 40;
				player.itemRequirement[ServerConstants.DEFENCE] = 40;
				return true;
			case 21_462:
			case 21_463:
			case 21_464:
			case 21_465:
			case 21_466: // battlemage
				player.itemRequirement[ServerConstants.MAGIC] = 85;
				player.itemRequirement[ServerConstants.STRENGTH] = 85;
				player.itemRequirement[ServerConstants.DEFENCE] = 85;
				return true;
			case 21_467:
			case 21_468:
			case 21_469:
			case 21_470:
			case 21_471: // trickster
				player.itemRequirement[ServerConstants.MAGIC] = 85;
				player.itemRequirement[ServerConstants.RANGED] = 85;
				player.itemRequirement[ServerConstants.DEFENCE] = 85;
				return true;
			case 21_736:
			case 21_744:
			case 21_752:
			case 21_760: // akrisae's set
				player.itemRequirement[ServerConstants.MAGIC] = 70;
				player.itemRequirement[ServerConstants.PRAYER] = 70;
				player.itemRequirement[ServerConstants.ATTACK] = 70;
				player.itemRequirement[ServerConstants.DEFENCE] = 70;
				return true;
			case 21_777:
				player.itemRequirement[ServerConstants.ATTACK] = 40;
				player.itemRequirement[ServerConstants.MAGIC] = 77;
				return true;
			case 21_793: // ragefire boots
				player.itemRequirement[ServerConstants.DEFENCE] = 75;
				player.itemRequirement[ServerConstants.MAGIC] = 75;
				return true;
			case 21_790: // glaiven boots
				player.itemRequirement[ServerConstants.DEFENCE] = 75;
				player.itemRequirement[ServerConstants.RANGED] = 75;
				return true;
			case 21_787: // steadfast boots
				player.itemRequirement[ServerConstants.DEFENCE] = 75;
				player.itemRequirement[ServerConstants.ATTACK] = 75;
				return true;
			case 22_366:
			case 22_367:
			case 22_368:
			case 22_369: // spellcaster gloves
				player.itemRequirement[ServerConstants.MAGIC] = 80;
				player.itemRequirement[ServerConstants.DEFENCE] = 80;
				return true;
			case 22_362:
			case 22_363:
			case 22_364:
			case 22_365: // swift gloves
				player.itemRequirement[ServerConstants.RANGED] = 80;
				player.itemRequirement[ServerConstants.DEFENCE] = 80;
				return true;
			case 22_358:
			case 22_359:
			case 22_360:
			case 22_361: // goliath gloves
				player.itemRequirement[ServerConstants.ATTACK] = 80;
				player.itemRequirement[ServerConstants.DEFENCE] = 80;
				return true;
			case 15_486:
				player.itemRequirement[ServerConstants.MAGIC] = 75;
				player.itemRequirement[ServerConstants.ATTACK] = 75;
				return true;
			case 24_379:
			case 24_376:
				player.itemRequirement[ServerConstants.RANGED] = 80;
				return true;
			case 24_382:
				player.itemRequirement[ServerConstants.RANGED] = 80;
				player.itemRequirement[ServerConstants.DEFENCE] = 40;
				return true;
			case 24_354:
			case 24_355:
			case 24_356:
			case 24_357:
			case 24_358: // dragonbone mage armour
				player.itemRequirement[ServerConstants.MAGIC] = 80;
				player.itemRequirement[ServerConstants.DEFENCE] = 80;
				return true;
			case 24_359:
			case 24_360:
			case 24_361:
			case 24_362:
			case 24_363:
			case 24_364: // dragonbone armour
			case 24_365: // dragon kiteshield
				player.itemRequirement[ServerConstants.DEFENCE] = 80;
				return true;
			case 24_338: // royal crossbow
			case 24_336: // royal bolts
				player.itemRequirement[ServerConstants.RANGED] = 80;
				return true;
			case 22_494: // polypore staff
				player.itemRequirement[ServerConstants.MAGIC] = 75;
				return true;
			case 22_482:
			case 22_486:
			case 22_490: // ganodermic
				player.itemRequirement[ServerConstants.DEFENCE] = 75;
				return true;
			case 13_867: // zuriel staff
				player.itemRequirement[ServerConstants.MAGIC] = 78;
				return true;
			case 13_899: // vesta long
			case 13_902: // statius hammer
			case 13_905: // vesta spear
				player.itemRequirement[ServerConstants.ATTACK] = 78;
				return true;
			case 13_864:
			case 13_861:
			case 13_858: // zuriel armour
			case 13_896:
			case 13_884:
			case 13_890: // statius
			case 13_887:
			case 13_893: // vesta
				player.itemRequirement[ServerConstants.DEFENCE] = 78;
				return true;
			case 13_870:
			case 13_873:
			case 13_876: // morrigans
				player.itemRequirement[ServerConstants.DEFENCE] = 78;
				return true;
			case 13_883:
			case 13_879: // morrigans axe and javelin
				player.itemRequirement[ServerConstants.RANGED] = 78;
				return true;
			case 20_135:
			case 20_139:
			case 20_143: // torva
				player.itemRequirement[ServerConstants.DEFENCE] = 80;
				player.itemRequirement[ServerConstants.STRENGTH] = 80;
				player.itemRequirement[ServerConstants.HITPOINTS] = 80;
				return true;
			case 20_147:
			case 20_151:
			case 20_155: // pernix
				player.itemRequirement[ServerConstants.DEFENCE] = 80;
				player.itemRequirement[ServerConstants.RANGED] = 80;
				player.itemRequirement[ServerConstants.HITPOINTS] = 80;
				return true;
			case 20_159:
			case 20_163:
			case 20_167: // virtus
				player.itemRequirement[ServerConstants.DEFENCE] = 80;
				player.itemRequirement[ServerConstants.MAGIC] = 80;
				player.itemRequirement[ServerConstants.HITPOINTS] = 80;
				return true;
			case 21_371: // abyssal vine
				player.itemRequirement[ServerConstants.ATTACK] = 85;
				player.itemRequirement[ServerConstants.SLAYER] = 80;
				return true;
		}
		return false;
	}

	private static boolean setOsrsItemRequirements(Player player, String itemName, int itemId) {
		if (!GameType.isOsrs()) {
			return false;
		}

		if (itemName.startsWith("justiciar")) {
			player.itemRequirement[ServerConstants.DEFENCE] = 75;
			return true;
		}

		if (itemName.equals("ghrazi rapier")) {
			player.itemRequirement[ServerConstants.ATTACK] = 75;
			return true;
		}

		switch (itemId) {
			case 22111: // Dragonbone necklace
				player.itemRequirement[ServerConstants.PRAYER] = 80;
				return true;
			case 12357: // Katana
				player.itemRequirement[ServerConstants.ATTACK] = 40;
				return true;
			case 12598: // Holy sandals
				player.itemRequirement[ServerConstants.PRAYER] = 31;
				return true;
			// Tome of fire
			case 20714:
				player.itemRequirement[ServerConstants.MAGIC] = 50;
				return true;
			case 20727: // Leaf-bladed battleaxe
				player.itemRequirement[ServerConstants.ATTACK] = 65;
				if (GameType.isOsrsEco()) {
					player.itemRequirement[ServerConstants.SLAYER] = 55;
				}
				return true;
			case 11889: // Zamorakian hasta
				player.itemRequirement[ServerConstants.ATTACK] = 70;
				return true;
			case 19921: // Ancient d'hide boots
			case 19924: // Bandos d'hide boots
			case 19927: // Guthix d'hide boots
			case 19930: // Armadyl d'hide boots
			case 19933: // Saradomin d'hide boots
			case 19936: //  Zamorak d'hide boots
				player.itemRequirement[ServerConstants.DEFENCE] = 40;
				player.itemRequirement[ServerConstants.RANGED] = 70;
				return true;
			case 22002: // Dragonfire ward
			case 22003: // Dragonfire ward
				player.itemRequirement[ServerConstants.RANGED] = 70;
				player.itemRequirement[ServerConstants.DEFENCE] = 75;
				return true;
			case 21633: // Ancient wyvern shield
				player.itemRequirement[ServerConstants.MAGIC] = 70;
				player.itemRequirement[ServerConstants.DEFENCE] = 75;
				return true;
			case 22109: // Ava's assembler
				player.itemRequirement[ServerConstants.RANGED] = 70;
				return true;

			case 20128: // Hood of darkness
			case 20131: // Robe top of darkness
			case 20134: // Gloves of darkness
			case 20137: // Robe bottom of darkness
				player.itemRequirement[ServerConstants.DEFENCE] = 20;
				player.itemRequirement[ServerConstants.MAGIC] = 40;
				return true;
			case 12197: // Ancient cloak
			case 12261: // Armadyl cloak
			case 12273: // Bandos cloak
				player.itemRequirement[ServerConstants.PRAYER] = 40;
				return true;

			// Obsidian armour set.
			case 21298:
			case 21301:
			case 21304:
				player.itemRequirement[ServerConstants.DEFENCE] = 60;
				return true;
			// Black santahat.
			case 13343:
			case 12371: // Lava dragon mask
			case 19988: // Blacksmith's helm
				return true;

			case 20155: // Gilded 2h
			case 12389: // Gilded scimitar
				player.itemRequirement[ServerConstants.ATTACK] = 40;
				return true;

			case 12596: // Ranger's tunic
			case 19994: // Ranger gloves
				player.itemRequirement[ServerConstants.RANGED] = 40;
				return true;
			case 21902: // Dragon c'bow
				player.itemRequirement[ServerConstants.RANGED] = 64;
				return true;

			// Malediction ward & Odium ward
			case 11924:
			case 11926:
			case 12806:
			case 12807:
				player.itemRequirement[ServerConstants.DEFENCE] = 60;
				return true;

			// Dinh's bulwark
			case 21015:
			case 16259:
				player.itemRequirement[ServerConstants.ATTACK] = 75;
				player.itemRequirement[ServerConstants.DEFENCE] = 75;
				return true;

			case 19544: // Tormented bracelet
			case 19550: // Ring of suffering
			case 19710: // Ring of suffering (i)
			case 20655: // Ring of suffering (r)
			case 20657: // Ring of suffering (ri)
				player.itemRequirement[ServerConstants.HITPOINTS] = 75;
				return true;


			case 13243: // Infernal pickaxe
			case 13241: // Infernal axe
				player.itemRequirement[ServerConstants.ATTACK] = 60;
				return true;
			// Dragon thrownaxe
			case 20849:
				player.itemRequirement[ServerConstants.RANGED] = 61;
				return true;

			case 20014: // 3rd age pickaxe
			case 20011: // 3rd age axe
			case 12426: // 3rd age longsword
				player.itemRequirement[ServerConstants.ATTACK] = 65;
				return true;

			case 12422: // 3rd age wand
				player.itemRequirement[ServerConstants.MAGIC] = 65;
				return true;

			case 12424: // 3rd age bow
				player.itemRequirement[ServerConstants.RANGED] = 65;
				return true;

			case 12373: // dragon cane
				player.itemRequirement[ServerConstants.ATTACK] = 60;
				return true;
			case 22322: // Avernic defender
				player.itemRequirement[ServerConstants.ATTACK] = 70;
				player.itemRequirement[ServerConstants.DEFENCE] = 70;
				return true;

			case 22323: // Sanguinesti staff
				player.itemRequirement[ServerConstants.MAGIC] = 75;
				return true;

			case 22325: // Scythe of vitur
				player.itemRequirement[ServerConstants.ATTACK] = 75;
				player.itemRequirement[ServerConstants.STRENGTH] = 75;
				return true;

			case 20730: // Mist battlestaff
			case 20736: // Dust battlestaff
			case 11998: // Smoke battlestaff
			case 11787: // Steam battlestaff
				player.itemRequirement[ServerConstants.ATTACK] = 30;
				player.itemRequirement[ServerConstants.MAGIC] = 30;
				return true;

			case 22_494: // polypore staff
				player.itemRequirement[ServerConstants.MAGIC] = 75;
				return true;
			case 22482:
			case 22486:
			case 22490: // ganodermic
				player.itemRequirement[ServerConstants.DEFENCE] = 75;
				return true;

			case 13_867: // zuriel staff
				player.itemRequirement[ServerConstants.MAGIC] = 78;
				return true;
			case 13_864:
			case 13_861:
			case 13_858: // zuriel armour
				player.itemRequirement[ServerConstants.DEFENCE] = 78;
				return true;
		}
	return false;
	}

}
