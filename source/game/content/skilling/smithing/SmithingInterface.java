package game.content.skilling.smithing;

import core.ServerConstants;
import game.item.ItemAssistant;
import game.player.Player;


public class SmithingInterface {

	public static void showSmithInterface(Player player, int itemId) {
		if (!ItemAssistant.hasItemInInventory(player, 2347) && !player.isInZombiesMinigame()) {
			player.playerAssistant.sendMessage("You need a hammer.");
			return;
		}

		player.setActionIdUsed(6);

		if (itemId == 2349)
			makeBronzeInterface(player);
		else if (itemId == 2351)
			makeIronInterface(player);
		else if (itemId == 2353)
			makeSteelInterface(player);
		else if (itemId == 2359)
			makeMithInterface(player);
		else if (itemId == 2361)
			makeAddyInterface(player);
		else if (itemId == 2363)
			makeRuneInterface(player);


	}

	private static void makeRuneInterface(Player player) {
		String fiveb = GetForBars(2363, 5, player);
		String threeb = GetForBars(2363, 3, player);
		String twob = GetForBars(2363, 2, player);
		String oneb = GetForBars(2363, 1, player);
		player.getPA().sendFrame126(fiveb + "5 Bars" + fiveb, 1112);
		player.getPA().sendFrame126(threeb + "3 Bars" + threeb, 1109);
		player.getPA().sendFrame126(threeb + "3 Bars" + threeb, 1110);
		player.getPA().sendFrame126(threeb + "3 Bars" + threeb, 1118);
		player.getPA().sendFrame126(threeb + "3 Bars" + threeb, 1111);
		player.getPA().sendFrame126(threeb + "3 Bars" + threeb, 1095);
		player.getPA().sendFrame126(threeb + "3 Bars" + threeb, 1115);
		player.getPA().sendFrame126(threeb + "3 Bars" + threeb, 1090);
		player.getPA().sendFrame126(twob + "2 Bars" + twob, 1113);
		player.getPA().sendFrame126(twob + "2 Bars" + twob, 1116);
		player.getPA().sendFrame126(twob + "2 Bars" + twob, 1114);
		player.getPA().sendFrame126(twob + "2 Bars" + twob, 1089);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 1124);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 1125);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 1126);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 1127);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 1128);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 1129);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 1130);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 1131);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 13357);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 1132);
		player.getPA().sendFrame126(GetForlvl(88, player) + "Plate Body" + GetForlvl(18, player), 1101);
		player.getPA().sendFrame126(GetForlvl(99, player) + "Plate Legs" + GetForlvl(16, player), 1099);
		player.getPA().sendFrame126(GetForlvl(99, player) + "Plate Skirt" + GetForlvl(16, player), 1100);
		player.getPA().sendFrame126(GetForlvl(99, player) + "2 Hand Sword" + GetForlvl(14, player), 1088);
		player.getPA().sendFrame126(GetForlvl(97, player) + "Kite Shield" + GetForlvl(12, player), 1105);
		player.getPA().sendFrame126(GetForlvl(96, player) + "Chain Body" + GetForlvl(11, player), 1098);
		player.getPA().sendFrame126(GetForlvl(95, player) + "Battle Axe" + GetForlvl(10, player), 1092);
		player.getPA().sendFrame126(GetForlvl(94, player) + "Warhammer" + GetForlvl(9, player), 1083);
		player.getPA().sendFrame126(GetForlvl(93, player) + "Square Shield" + GetForlvl(8, player), 1104);
		player.getPA().sendFrame126(GetForlvl(92, player) + "Full Helm" + GetForlvl(7, player), 1103);
		player.getPA().sendFrame126(GetForlvl(92, player) + "Throwing Knives" + GetForlvl(7, player), 1106);
		player.getPA().sendFrame126(GetForlvl(91, player) + "Long Sword" + GetForlvl(6, player), 1086);
		player.getPA().sendFrame126(GetForlvl(90, player) + "Scimitar" + GetForlvl(5, player), 1087);
		player.getPA().sendFrame126(GetForlvl(90, player) + "Dart Tips" + GetForlvl(5, player), 1108);
		player.getPA().sendFrame126(GetForlvl(89, player) + "Sword" + GetForlvl(4, player), 1085);
		player.getPA().sendFrame126(GetForlvl(88, player) + "Bolts" + GetForlvl(4, player), 1107);
		player.getPA().sendFrame126(GetForlvl(89, player) + "Nails" + GetForlvl(4, player), 13358);
		player.getPA().sendFrame126(GetForlvl(88, player) + "Medium Helm" + GetForlvl(3, player), 1102);
		player.getPA().sendFrame126(GetForlvl(87, player) + "Mace" + GetForlvl(2, player), 1093);
		player.getPA().sendFrame126(GetForlvl(85, player) + "Dagger" + GetForlvl(1, player), 1094);
		player.getPA().sendFrame126(GetForlvl(86, player) + "Axe" + GetForlvl(1, player), 1091);
		player.getPA().sendFrame126(GetForlvl(91, player) + "Runite limbs" + GetForlvl(1, player), 1096);
		//1107 is the dart tips text
		player.getPA().sendFrame34Other(1213, 0, 1119, 1); //dagger
		player.getPA().sendFrame34Other(1359, 0, 1120, 1); //axe
		player.getPA().sendFrame34Other(1113, 0, 1121, 1); //chain body
		player.getPA().sendFrame34Other(1147, 0, 1122, 1); //med helm
		player.getPA().sendFrame34Other(9381, 0, 1123, 10); //Bolts

		player.getPA().sendFrame34Other(1289, 1, 1119, 1); //s-sword
		player.getPA().sendFrame34Other(1432, 1, 1120, 1); //mace
		player.getPA().sendFrame34Other(1079, 1, 1121, 1); //platelegs
		player.getPA().sendFrame34Other(1163, 1, 1122, 1); //full helm
		player.getPA().sendFrame34Other(824, 1, 1123, 10); //darttip

		player.getPA().sendFrame34Other(1333, 2, 1119, 1); //scimmy
		player.getPA().sendFrame34Other(1347, 2, 1120, 1); //warhammer
		player.getPA().sendFrame34Other(1093, 2, 1121, 1); //plateskirt
		player.getPA().sendFrame34Other(1185, 2, 1122, 1); //Sq. Shield
		player.getPA().sendFrame34Other(868, 2, 1123, 5); //throwing-knives

		player.getPA().sendFrame34Other(1303, 3, 1119, 1); //longsword
		player.getPA().sendFrame34Other(1373, 3, 1120, 1); //battleaxe
		player.getPA().sendFrame34Other(1127, 3, 1121, 1); //platebody
		player.getPA().sendFrame34Other(1201, 3, 1122, 1); //kiteshield
		player.getPA().sendFrame34Other(9431, 3, 1123, 1); // Runite limbs.

		player.getPA().sendFrame34Other(1319, 4, 1119, 1); //2h sword

		player.getPA().sendFrame34Other(-1, 4, 1120, 1);
		player.getPA().sendFrame34Other(-1, 4, 1121, 1);
		player.getPA().sendFrame34Other(-1, 4, 1123, 1);
		player.getPA().sendFrame126(" ", 8428);

		player.getPA().sendFrame34Other(4824, 4, 1122, 15); //nails
		player.getPA().sendFrame126(" ", 1135);
		player.getPA().sendFrame126(" ", 1134);
		player.getPA().sendFrame126(" ", 11461);
		player.getPA().sendFrame126(" ", 11459);
		player.getPA().sendFrame126(" ", 1132);
		player.getPA().sendFrame126(" ", 8429);
		player.getPA().displayInterface(994);
	}

	private static void makeAddyInterface(Player player) {
		String fiveb = GetForBars(2361, 5, player);
		String threeb = GetForBars(2361, 3, player);
		String twob = GetForBars(2361, 2, player);
		String oneb = GetForBars(2361, 1, player);
		player.getPA().sendFrame126(fiveb + "5 Bars" + fiveb, 1112);
		player.getPA().sendFrame126(threeb + "3 Bars" + threeb, 1109);
		player.getPA().sendFrame126(threeb + "3 Bars" + threeb, 1110);
		player.getPA().sendFrame126(threeb + "3 Bars" + threeb, 1118);
		player.getPA().sendFrame126(threeb + "3 Bars" + threeb, 1111);
		player.getPA().sendFrame126(threeb + "3 Bars" + threeb, 1095);
		player.getPA().sendFrame126(threeb + "3 Bars" + threeb, 1115);
		player.getPA().sendFrame126(threeb + "3 Bars" + threeb, 1090);
		player.getPA().sendFrame126(twob + "2 Bars" + twob, 1113);
		player.getPA().sendFrame126(twob + "2 Bars" + twob, 1116);
		player.getPA().sendFrame126(twob + "2 Bars" + twob, 1114);
		player.getPA().sendFrame126(twob + "2 Bars" + twob, 1089);
		player.getPA().sendFrame126(twob + "2 Bars" + twob, 8428);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 1124);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 1125);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 1126);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 1127);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 1128);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 1129);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 1130);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 1131);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 13357);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 11459);
		player.getPA().sendFrame126(GetForlvl(88, player) + "Plate Body" + GetForlvl(18, player), 1101);
		player.getPA().sendFrame126(GetForlvl(86, player) + "Plate Legs" + GetForlvl(16, player), 1099);
		player.getPA().sendFrame126(GetForlvl(86, player) + "Plate Skirt" + GetForlvl(16, player), 1100);
		player.getPA().sendFrame126(GetForlvl(84, player) + "2 Hand Sword" + GetForlvl(14, player), 1088);
		player.getPA().sendFrame126(GetForlvl(82, player) + "Kite Shield" + GetForlvl(12, player), 1105);
		player.getPA().sendFrame126(GetForlvl(81, player) + "Chain Body" + GetForlvl(11, player), 1098);
		player.getPA().sendFrame126(GetForlvl(80, player) + "Battle Axe" + GetForlvl(10, player), 1092);
		player.getPA().sendFrame126(GetForlvl(79, player) + "Warhammer" + GetForlvl(9, player), 1083);
		player.getPA().sendFrame126(GetForlvl(78, player) + "Square Shield" + GetForlvl(8, player), 1104);
		player.getPA().sendFrame126(GetForlvl(77, player) + "Full Helm" + GetForlvl(7, player), 1103);
		player.getPA().sendFrame126(GetForlvl(77, player) + "Throwing Knives" + GetForlvl(7, player), 1106);
		player.getPA().sendFrame126(GetForlvl(76, player) + "Long Sword" + GetForlvl(6, player), 1086);
		player.getPA().sendFrame126(GetForlvl(75, player) + "Scimitar" + GetForlvl(5, player), 1087);
		player.getPA().sendFrame126(GetForlvl(75, player) + "Dart tips" + GetForlvl(5, player), 1108);
		player.getPA().sendFrame126(GetForlvl(74, player) + "Sword" + GetForlvl(4, player), 1085);
		player.getPA().sendFrame126(GetForlvl(74, player) + "Bolts" + GetForlvl(4, player), 1107);
		player.getPA().sendFrame126(GetForlvl(74, player) + "Nails" + GetForlvl(4, player), 13358);
		player.getPA().sendFrame126(GetForlvl(73, player) + "Medium Helm" + GetForlvl(3, player), 1102);
		player.getPA().sendFrame126(GetForlvl(72, player) + "Mace" + GetForlvl(2, player), 1093);
		player.getPA().sendFrame126(GetForlvl(70, player) + "Dagger" + GetForlvl(1, player), 1094);
		player.getPA().sendFrame126(GetForlvl(71, player) + "Axe" + GetForlvl(1, player), 1091);
		player.getPA().sendFrame34Other(1211, 0, 1119, 1); //dagger
		player.getPA().sendFrame34Other(1357, 0, 1120, 1); //axe
		player.getPA().sendFrame34Other(1111, 0, 1121, 1); //chain body
		player.getPA().sendFrame34Other(1145, 0, 1122, 1); //med helm
		player.getPA().sendFrame34Other(9380, 0, 1123, 10); //Bolts
		player.getPA().sendFrame34Other(1287, 1, 1119, 1); //s-sword
		player.getPA().sendFrame34Other(1430, 1, 1120, 1); //mace
		player.getPA().sendFrame34Other(1073, 1, 1121, 1); //platelegs
		player.getPA().sendFrame34Other(1161, 1, 1122, 1); //full helm
		player.getPA().sendFrame34Other(823, 1, 1123, 10); //dart tips
		player.getPA().sendFrame34Other(1331, 2, 1119, 1); //scimmy
		player.getPA().sendFrame34Other(1345, 2, 1120, 1); //warhammer
		player.getPA().sendFrame34Other(1091, 2, 1121, 1); //plateskirt
		player.getPA().sendFrame34Other(1183, 2, 1122, 1); //Sq. Shield
		player.getPA().sendFrame34Other(867, 2, 1123, 5); //throwing-knives
		player.getPA().sendFrame34Other(1301, 3, 1119, 1); //longsword
		player.getPA().sendFrame34Other(1371, 3, 1120, 1); //battleaxe
		player.getPA().sendFrame34Other(1123, 3, 1121, 1); //platebody
		player.getPA().sendFrame34Other(1199, 3, 1122, 1); //kiteshield
		player.getPA().sendFrame34Other(1317, 4, 1119, 1); //2h sword
		player.getPA().sendFrame34Other(4823, 4, 1122, 15); //nails
		player.getPA().sendFrame34Other(-1, 3, 1123, 1);
		player.getPA().sendFrame126(" ", 1135);
		player.getPA().sendFrame126(" ", 1134);
		player.getPA().sendFrame126(" ", 11461);
		player.getPA().sendFrame126(" ", 11459);
		player.getPA().sendFrame126(" ", 1132);
		player.getPA().sendFrame126(" ", 1096);


		player.getPA().sendFrame34Other(-1, 4, 1120, 1);
		player.getPA().sendFrame34Other(-1, 4, 1121, 1);
		player.getPA().sendFrame34Other(-1, 4, 1123, 1);
		player.getPA().sendFrame126(" ", 8428);
		player.getPA().sendFrame126(" ", 8429);
		player.getPA().displayInterface(994);
	}

	private static void makeMithInterface(Player player) {
		String fiveb = GetForBars(2359, 5, player);
		String threeb = GetForBars(2359, 3, player);
		String twob = GetForBars(2359, 2, player);
		String oneb = GetForBars(2359, 1, player);
		player.getPA().sendFrame126(fiveb + "5 Bars" + fiveb, 1112);
		player.getPA().sendFrame126(threeb + "3 Bars" + threeb, 1109);
		player.getPA().sendFrame126(threeb + "3 Bars" + threeb, 1110);
		player.getPA().sendFrame126(threeb + "3 Bars" + threeb, 1118);
		player.getPA().sendFrame126(threeb + "3 Bars" + threeb, 1111);
		player.getPA().sendFrame126(threeb + "3 Bars" + threeb, 1095);
		player.getPA().sendFrame126(threeb + "3 Bars" + threeb, 1115);
		player.getPA().sendFrame126(threeb + "3 Bars" + threeb, 1090);
		player.getPA().sendFrame126(twob + "2 Bars" + twob, 1113);
		player.getPA().sendFrame126(twob + "2 Bars" + twob, 1116);
		player.getPA().sendFrame126(twob + "2 Bars" + twob, 1114);
		player.getPA().sendFrame126(twob + "2 Bars" + twob, 1089);
		player.getPA().sendFrame126(twob + "2 Bars" + twob, 8428);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 1124);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 1125);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 1126);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 1127);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 1128);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 1129);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 1130);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 1131);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 13357);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 11459);
		player.getPA().sendFrame126(GetForlvl(68, player) + "Plate Body" + GetForlvl(18, player), 1101);
		player.getPA().sendFrame126(GetForlvl(66, player) + "Plate Legs" + GetForlvl(16, player), 1099);
		player.getPA().sendFrame126(GetForlvl(66, player) + "Plate Skirt" + GetForlvl(16, player), 1100);
		player.getPA().sendFrame126(GetForlvl(64, player) + "2 Hand Sword" + GetForlvl(14, player), 1088);
		player.getPA().sendFrame126(GetForlvl(62, player) + "Kite Shield" + GetForlvl(12, player), 1105);
		player.getPA().sendFrame126(GetForlvl(61, player) + "Chain Body" + GetForlvl(11, player), 1098);
		player.getPA().sendFrame126(GetForlvl(60, player) + "Battle Axe" + GetForlvl(10, player), 1092);
		player.getPA().sendFrame126(GetForlvl(59, player) + "Warhammer" + GetForlvl(9, player), 1083);
		player.getPA().sendFrame126(GetForlvl(58, player) + "Square Shield" + GetForlvl(8, player), 1104);
		player.getPA().sendFrame126(GetForlvl(57, player) + "Full Helm" + GetForlvl(7, player), 1103);
		player.getPA().sendFrame126(GetForlvl(57, player) + "Throwing Knives" + GetForlvl(7, player), 1106);
		player.getPA().sendFrame126(GetForlvl(56, player) + "Long Sword" + GetForlvl(6, player), 1086);
		player.getPA().sendFrame126(GetForlvl(55, player) + "Scimitar" + GetForlvl(5, player), 1087);
		player.getPA().sendFrame126(GetForlvl(55, player) + "Dart Tips" + GetForlvl(5, player), 1108);
		player.getPA().sendFrame126(GetForlvl(54, player) + "Sword" + GetForlvl(4, player), 1085);
		player.getPA().sendFrame126(GetForlvl(54, player) + "Bolts" + GetForlvl(4, player), 1107);
		player.getPA().sendFrame126(GetForlvl(54, player) + "Nails" + GetForlvl(4, player), 13358);
		player.getPA().sendFrame126(GetForlvl(53, player) + "Medium Helm" + GetForlvl(3, player), 1102);
		player.getPA().sendFrame126(GetForlvl(52, player) + "Mace" + GetForlvl(2, player), 1093);
		player.getPA().sendFrame126(GetForlvl(50, player) + "Dagger" + GetForlvl(1, player), 1094);
		player.getPA().sendFrame126(GetForlvl(51, player) + "Axe" + GetForlvl(1, player), 1091);
		player.getPA().sendFrame34Other(1209, 0, 1119, 1); //dagger
		player.getPA().sendFrame34Other(1355, 0, 1120, 1); //axe
		player.getPA().sendFrame34Other(1109, 0, 1121, 1); //chain body
		player.getPA().sendFrame34Other(1143, 0, 1122, 1); //med helm
		player.getPA().sendFrame34Other(9379, 0, 1123, 10); //Bolts
		player.getPA().sendFrame34Other(1285, 1, 1119, 1); //s-sword
		player.getPA().sendFrame34Other(1428, 1, 1120, 1); //mace
		player.getPA().sendFrame34Other(1071, 1, 1121, 1); //platelegs
		player.getPA().sendFrame34Other(1159, 1, 1122, 1); //full helm
		player.getPA().sendFrame34Other(822, 1, 1123, 10); //Dart tips
		player.getPA().sendFrame34Other(1329, 2, 1119, 1); //scimmy
		player.getPA().sendFrame34Other(1343, 2, 1120, 1); //warhammer
		player.getPA().sendFrame34Other(1085, 2, 1121, 1); //plateskirt
		player.getPA().sendFrame34Other(1181, 2, 1122, 1); //Sq. Shield
		player.getPA().sendFrame34Other(866, 2, 1123, 5); //throwing-knives
		player.getPA().sendFrame34Other(1299, 3, 1119, 1); //longsword
		player.getPA().sendFrame34Other(1369, 3, 1120, 1); //battleaxe
		player.getPA().sendFrame34Other(1121, 3, 1121, 1); //platebody
		player.getPA().sendFrame34Other(1197, 3, 1122, 1); //kiteshield
		player.getPA().sendFrame34Other(1315, 4, 1119, 1); //2h sword
		player.getPA().sendFrame34Other(4822, 4, 1122, 15); //nails
		player.getPA().sendFrame34Other(-1, 3, 1123, 1);
		player.getPA().sendFrame126(" ", 1135);
		player.getPA().sendFrame126(" ", 1134);
		player.getPA().sendFrame126(" ", 11461);
		player.getPA().sendFrame126(" ", 11459);
		player.getPA().sendFrame126(" ", 1132);
		player.getPA().sendFrame126(" ", 1096);

		player.getPA().sendFrame34Other(-1, 4, 1120, 1);
		player.getPA().sendFrame34Other(-1, 4, 1121, 1);
		player.getPA().sendFrame34Other(-1, 4, 1123, 1);
		player.getPA().sendFrame126(" ", 8428);
		player.getPA().sendFrame126(" ", 8429);
		player.getPA().displayInterface(994);
	}

	private static void makeSteelInterface(Player player) {
		String fiveb = GetForBars(2353, 5, player);
		String threeb = GetForBars(2353, 3, player);
		String twob = GetForBars(2353, 2, player);
		String oneb = GetForBars(2353, 1, player);
		player.getPA().sendFrame126(fiveb + "5 Bars" + fiveb, 1112);
		player.getPA().sendFrame126(threeb + "3 Bars" + threeb, 1109);
		player.getPA().sendFrame126(threeb + "3 Bars" + threeb, 1110);
		player.getPA().sendFrame126(threeb + "3 Bars" + threeb, 1118);
		player.getPA().sendFrame126(threeb + "3 Bars" + threeb, 1111);
		player.getPA().sendFrame126(threeb + "3 Bars" + threeb, 1095);
		player.getPA().sendFrame126(threeb + "3 Bars" + threeb, 1115);
		player.getPA().sendFrame126(threeb + "3 Bars" + threeb, 1090);
		player.getPA().sendFrame126(twob + "2 Bars" + twob, 1113);
		player.getPA().sendFrame126(twob + "2 Bars" + twob, 1116);
		player.getPA().sendFrame126(twob + "2 Bars" + twob, 1114);
		player.getPA().sendFrame126(twob + "2 Bars" + twob, 1089);
		player.getPA().sendFrame126(twob + "2 Bars" + twob, 8428);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 1124);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 1125);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 1126);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 1127);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 1128);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 1129);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 1130);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 1131);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 13357);
		player.getPA().sendFrame126(" ", 1132);
		player.getPA().sendFrame126(" ", 1135);
		player.getPA().sendFrame126(" ", 11459);
		player.getPA().sendFrame126(GetForlvl(48, player) + "Plate Body" + GetForlvl(18, player), 1101);
		player.getPA().sendFrame126(GetForlvl(46, player) + "Plate Legs" + GetForlvl(16, player), 1099);
		player.getPA().sendFrame126(GetForlvl(46, player) + "Plate Skirt" + GetForlvl(16, player), 1100);
		player.getPA().sendFrame126(GetForlvl(44, player) + "2 Hand Sword" + GetForlvl(14, player), 1088);
		player.getPA().sendFrame126(GetForlvl(42, player) + "Kite Shield" + GetForlvl(12, player), 1105);
		player.getPA().sendFrame126(GetForlvl(41, player) + "Chain Body" + GetForlvl(11, player), 1098);
		player.getPA().sendFrame126(" ", 11461);
		player.getPA().sendFrame126(GetForlvl(40, player) + "Battle Axe" + GetForlvl(10, player), 1092);
		player.getPA().sendFrame126(GetForlvl(39, player) + "Warhammer" + GetForlvl(9, player), 1083);
		player.getPA().sendFrame126(GetForlvl(38, player) + "Square Shield" + GetForlvl(8, player), 1104);
		player.getPA().sendFrame126(GetForlvl(37, player) + "Full Helm" + GetForlvl(7, player), 1103);
		player.getPA().sendFrame126(GetForlvl(37, player) + "Throwing Knives" + GetForlvl(7, player), 1106);
		player.getPA().sendFrame126(GetForlvl(36, player) + "Long Sword" + GetForlvl(6, player), 1086);
		player.getPA().sendFrame126(GetForlvl(35, player) + "Scimitar" + GetForlvl(5, player), 1087);
		player.getPA().sendFrame126(GetForlvl(35, player) + "Dart Tips" + GetForlvl(5, player), 1108);
		player.getPA().sendFrame126(GetForlvl(34, player) + "Sword" + GetForlvl(4, player), 1085);
		player.getPA().sendFrame126(GetForlvl(34, player) + "Bolts" + GetForlvl(4, player), 1107);
		player.getPA().sendFrame126(GetForlvl(34, player) + "Nails" + GetForlvl(4, player), 13358);
		player.getPA().sendFrame126(GetForlvl(33, player) + "Medium Helm" + GetForlvl(3, player), 1102);
		player.getPA().sendFrame126(GetForlvl(32, player) + "Mace" + GetForlvl(2, player), 1093);
		player.getPA().sendFrame126(GetForlvl(30, player) + "Dagger" + GetForlvl(1, player), 1094);
		player.getPA().sendFrame126(GetForlvl(31, player) + "Axe" + GetForlvl(1, player), 1091);
		player.getPA().sendFrame126(" ", 1096);
		player.getPA().sendFrame126(" ", 1134);
		player.getPA().sendFrame34Other(1207, 0, 1119, 1);
		player.getPA().sendFrame34Other(1353, 0, 1120, 1);
		player.getPA().sendFrame34Other(1105, 0, 1121, 1);
		player.getPA().sendFrame34Other(1141, 0, 1122, 1);
		player.getPA().sendFrame34Other(9378, 0, 1123, 10);
		player.getPA().sendFrame34Other(1281, 1, 1119, 1);
		player.getPA().sendFrame34Other(1424, 1, 1120, 1);
		player.getPA().sendFrame34Other(1069, 1, 1121, 1);
		player.getPA().sendFrame34Other(1157, 1, 1122, 1);
		player.getPA().sendFrame34Other(821, 1, 1123, 10);
		player.getPA().sendFrame34Other(1325, 2, 1119, 1);
		player.getPA().sendFrame34Other(1339, 2, 1120, 1);
		player.getPA().sendFrame34Other(1083, 2, 1121, 1);
		player.getPA().sendFrame34Other(1177, 2, 1122, 1);
		player.getPA().sendFrame34Other(865, 2, 1123, 5);
		player.getPA().sendFrame34Other(1295, 3, 1119, 1);
		player.getPA().sendFrame34Other(1365, 3, 1120, 1);
		player.getPA().sendFrame34Other(1119, 3, 1121, 1);
		player.getPA().sendFrame34Other(1193, 3, 1122, 1);
		player.getPA().sendFrame34Other(1311, 4, 1119, 1);
		player.getPA().sendFrame34Other(1539, 4, 1122, 15);
		player.getPA().sendFrame34Other(-1, 3, 1123, 4);
		player.getPA().sendFrame34Other(-1, 4, 1123, 1);


		player.getPA().sendFrame34Other(-1, 4, 1120, 1);
		player.getPA().sendFrame34Other(-1, 4, 1121, 1);
		player.getPA().sendFrame34Other(-1, 4, 1123, 1);
		player.getPA().sendFrame126(" ", 8428);
		player.getPA().sendFrame126(" ", 8429);
		player.getPA().displayInterface(994);
	}

	private static void makeIronInterface(Player player) {
		String fiveb = GetForBars(2351, 5, player);
		String threeb = GetForBars(2351, 3, player);
		String twob = GetForBars(2351, 2, player);
		String oneb = GetForBars(2351, 1, player);
		player.getPA().sendFrame126(fiveb + "5 Bars" + fiveb, 1112);
		player.getPA().sendFrame126(threeb + "3 Bars" + threeb, 1109);
		player.getPA().sendFrame126(threeb + "3 Bars" + threeb, 1110);
		player.getPA().sendFrame126(threeb + "3 Bars" + threeb, 1118);
		player.getPA().sendFrame126(threeb + "3 Bars" + threeb, 1111);
		player.getPA().sendFrame126(threeb + "3 Bars" + threeb, 1095);
		player.getPA().sendFrame126(threeb + "3 Bars" + threeb, 1115);
		player.getPA().sendFrame126(threeb + "3 Bars" + threeb, 1090);
		player.getPA().sendFrame126(twob + "2 Bars" + twob, 1113);
		player.getPA().sendFrame126(twob + "2 Bars" + twob, 1116);
		player.getPA().sendFrame126(twob + "2 Bars" + twob, 1114);
		player.getPA().sendFrame126(twob + "2 Bars" + twob, 1089);
		player.getPA().sendFrame126(twob + "2 Bars" + twob, 8428);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 1124);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 1125);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 1126);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 1127);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 1128);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 1129);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 1130);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 1131);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 13357);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 11459);
		player.getPA().sendFrame126(GetForlvl(33, player) + "Plate Body" + GetForlvl(18, player), 1101);
		player.getPA().sendFrame126(GetForlvl(31, player) + "Plate Legs" + GetForlvl(16, player), 1099);
		player.getPA().sendFrame126(GetForlvl(31, player) + "Plate Skirt" + GetForlvl(16, player), 1100);
		player.getPA().sendFrame126(GetForlvl(29, player) + "2 Hand Sword" + GetForlvl(14, player), 1088);
		player.getPA().sendFrame126(GetForlvl(27, player) + "Kite Shield" + GetForlvl(12, player), 1105);
		player.getPA().sendFrame126(GetForlvl(26, player) + "Chain Body" + GetForlvl(11, player), 1098);
		player.getPA().sendFrame126(GetForlvl(26, player) + "Oil Lantern Frame" + GetForlvl(11, player), 11461);
		player.getPA().sendFrame126(GetForlvl(25, player) + "Battle Axe" + GetForlvl(10, player), 1092);
		player.getPA().sendFrame126(GetForlvl(24, player) + "Warhammer" + GetForlvl(9, player), 1083);
		player.getPA().sendFrame126(GetForlvl(23, player) + "Square Shield" + GetForlvl(8, player), 1104);
		player.getPA().sendFrame126(GetForlvl(22, player) + "Full Helm" + GetForlvl(7, player), 1103);
		player.getPA().sendFrame126(GetForlvl(21, player) + "Throwing Knives" + GetForlvl(7, player), 1106);
		player.getPA().sendFrame126(GetForlvl(21, player) + "Long Sword" + GetForlvl(6, player), 1086);
		player.getPA().sendFrame126(GetForlvl(20, player) + "Scimitar" + GetForlvl(5, player), 1087);
		player.getPA().sendFrame126(GetForlvl(20, player) + "Dart Tips" + GetForlvl(5, player), 1108);
		player.getPA().sendFrame126(GetForlvl(19, player) + "Sword" + GetForlvl(4, player), 1085);
		player.getPA().sendFrame126(GetForlvl(19, player) + "Bolts" + GetForlvl(4, player), 1107);
		player.getPA().sendFrame126(GetForlvl(19, player) + "Nails" + GetForlvl(4, player), 13358);
		player.getPA().sendFrame126(GetForlvl(18, player) + "Medium Helm" + GetForlvl(3, player), 1102);
		player.getPA().sendFrame126(GetForlvl(17, player) + "Mace" + GetForlvl(2, player), 1093);
		player.getPA().sendFrame126(GetForlvl(15, player) + "Dagger" + GetForlvl(1, player), 1094);
		player.getPA().sendFrame126(GetForlvl(16, player) + "Axe" + GetForlvl(1, player), 1091);
		player.getPA().sendFrame34Other(1203, 0, 1119, 1);
		player.getPA().sendFrame34Other(1349, 0, 1120, 1);
		player.getPA().sendFrame34Other(1101, 0, 1121, 1);
		player.getPA().sendFrame34Other(1137, 0, 1122, 1);
		player.getPA().sendFrame34Other(9377, 0, 1123, 10);
		player.getPA().sendFrame34Other(1279, 1, 1119, 1);
		player.getPA().sendFrame34Other(1420, 1, 1120, 1);
		player.getPA().sendFrame34Other(1067, 1, 1121, 1);
		player.getPA().sendFrame34Other(1153, 1, 1122, 1);
		player.getPA().sendFrame34Other(820, 1, 1123, 10);
		player.getPA().sendFrame34Other(1323, 2, 1119, 1);
		player.getPA().sendFrame34Other(1335, 2, 1120, 1);
		player.getPA().sendFrame34Other(1081, 2, 1121, 1);
		player.getPA().sendFrame34Other(1175, 2, 1122, 1);
		player.getPA().sendFrame34Other(863, 2, 1123, 5);
		player.getPA().sendFrame34Other(1293, 3, 1119, 1);
		player.getPA().sendFrame34Other(1363, 3, 1120, 1);
		player.getPA().sendFrame34Other(1115, 3, 1121, 1);
		player.getPA().sendFrame34Other(1191, 3, 1122, 1);
		player.getPA().sendFrame34Other(1309, 4, 1119, 1);
		player.getPA().sendFrame34Other(4820, 4, 1122, 15);
		player.getPA().sendFrame34Other(4540, 4, 1121, 1);
		player.getPA().sendFrame34Other(-1, 3, 1123, 1);
		player.getPA().sendFrame34Other(-1, 4, 1120, 1);
		player.getPA().sendFrame34Other(-1, 4, 1121, 1);
		player.getPA().sendFrame34Other(-1, 4, 1123, 1);
		player.getPA().sendFrame126(" ", 1135);
		player.getPA().sendFrame126(" ", 1134);
		player.getPA().sendFrame126(" ", 1132);
		player.getPA().sendFrame126(" ", 1096);
		player.getPA().sendFrame126(" ", 8428);
		player.getPA().sendFrame126(" ", 8429);
		player.getPA().sendFrame126(" ", 11461);
		player.getPA().sendFrame126(" ", 11459);
		player.getPA().displayInterface(994);
	}

	private static void makeBronzeInterface(Player player) {
		String fiveb = GetForBars(2349, 5, player);
		String threeb = GetForBars(2349, 3, player);
		String twob = GetForBars(2349, 2, player);
		String oneb = GetForBars(2349, 1, player);
		player.getPA().sendFrame126(fiveb + "5 Bars" + fiveb, 1112);
		player.getPA().sendFrame126(threeb + "3 Bars" + threeb, 1109);
		player.getPA().sendFrame126(threeb + "3 Bars" + threeb, 1110);
		player.getPA().sendFrame126(threeb + "3 Bars" + threeb, 1118);
		player.getPA().sendFrame126(threeb + "3 Bars" + threeb, 1111);
		player.getPA().sendFrame126(threeb + "3 Bars" + threeb, 1095);
		player.getPA().sendFrame126(threeb + "3 Bars" + threeb, 1115);
		player.getPA().sendFrame126(threeb + "3 Bars" + threeb, 1090);
		player.getPA().sendFrame126(twob + "2 Bars" + twob, 1113);
		player.getPA().sendFrame126(twob + "2 Bars" + twob, 1116);
		player.getPA().sendFrame126(twob + "2 Bars" + twob, 1114);
		player.getPA().sendFrame126(twob + "2 Bars" + twob, 1089);
		player.getPA().sendFrame126(twob + "2 Bars" + twob, 8428);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 1124);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 1125);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 1126);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 1127);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 1128);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 1129);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 1130);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 1131);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 13357);
		player.getPA().sendFrame126(oneb + "1 Bar" + oneb, 11459);
		player.getPA().sendFrame126(GetForlvl(18, player) + "Plate Body" + GetForlvl(18, player), 1101);
		player.getPA().sendFrame126(GetForlvl(16, player) + "Plate Legs" + GetForlvl(16, player), 1099);
		player.getPA().sendFrame126(GetForlvl(16, player) + "Plate Skirt" + GetForlvl(16, player), 1100);
		player.getPA().sendFrame126(GetForlvl(14, player) + "2 Hand Sword" + GetForlvl(14, player), 1088);
		player.getPA().sendFrame126(GetForlvl(12, player) + "Kite Shield" + GetForlvl(12, player), 1105);
		player.getPA().sendFrame126(GetForlvl(11, player) + "Chain Body" + GetForlvl(11, player), 1098);
		player.getPA().sendFrame126(GetForlvl(10, player) + "Battle Axe" + GetForlvl(10, player), 1092);
		player.getPA().sendFrame126(GetForlvl(9, player) + "Warhammer" + GetForlvl(9, player), 1083);
		player.getPA().sendFrame126(GetForlvl(8, player) + "Square Shield" + GetForlvl(8, player), 1104);
		player.getPA().sendFrame126(GetForlvl(7, player) + "Full Helm" + GetForlvl(7, player), 1103);
		player.getPA().sendFrame126(GetForlvl(7, player) + "Throwing Knives" + GetForlvl(7, player), 1106);
		player.getPA().sendFrame126(GetForlvl(6, player) + "Long Sword" + GetForlvl(6, player), 1086);
		player.getPA().sendFrame126(GetForlvl(5, player) + "Scimitar" + GetForlvl(5, player), 1087);
		player.getPA().sendFrame126(GetForlvl(5, player) + "Dart Tips" + GetForlvl(5, player), 1108);
		player.getPA().sendFrame126(GetForlvl(4, player) + "Sword" + GetForlvl(4, player), 1085);
		player.getPA().sendFrame126(GetForlvl(4, player) + "Bolts" + GetForlvl(4, player), 1107);
		player.getPA().sendFrame126(GetForlvl(4, player) + "Nails" + GetForlvl(4, player), 13358);
		player.getPA().sendFrame126(GetForlvl(3, player) + "Medium Helm" + GetForlvl(3, player), 1102);
		player.getPA().sendFrame126(GetForlvl(2, player) + "Mace" + GetForlvl(2, player), 1093);
		player.getPA().sendFrame126(GetForlvl(1, player) + "Dagger" + GetForlvl(1, player), 1094);
		player.getPA().sendFrame126(GetForlvl(1, player) + "Axe" + GetForlvl(1, player), 1091);
		player.getPA().sendFrame34Other(1205, 0, 1119, 1);
		player.getPA().sendFrame34Other(1351, 0, 1120, 1);
		player.getPA().sendFrame34Other(1103, 0, 1121, 1);
		player.getPA().sendFrame34Other(1139, 0, 1122, 1);
		player.getPA().sendFrame34Other(9375, 0, 1123, 10);
		player.getPA().sendFrame34Other(1277, 1, 1119, 1);
		player.getPA().sendFrame34Other(1422, 1, 1120, 1);
		player.getPA().sendFrame34Other(1075, 1, 1121, 1);
		player.getPA().sendFrame34Other(1155, 1, 1122, 1);
		player.getPA().sendFrame34Other(819, 1, 1123, 10);
		player.getPA().sendFrame34Other(1321, 2, 1119, 1);
		player.getPA().sendFrame34Other(1337, 2, 1120, 1);
		player.getPA().sendFrame34Other(1087, 2, 1121, 1);
		player.getPA().sendFrame34Other(1173, 2, 1122, 1);
		player.getPA().sendFrame34Other(864, 2, 1123, 5);
		player.getPA().sendFrame34Other(1291, 3, 1119, 1);
		player.getPA().sendFrame34Other(1375, 3, 1120, 1);
		player.getPA().sendFrame34Other(1117, 3, 1121, 1);
		player.getPA().sendFrame34Other(1189, 3, 1122, 1);
		player.getPA().sendFrame34Other(1307, 4, 1119, 1);
		player.getPA().sendFrame34Other(4819, 4, 1122, 15);
		player.getPA().sendFrame34Other(-1, 3, 1123, 1);
		player.getPA().sendFrame126(" ", 1135);
		player.getPA().sendFrame126(" ", 1134);
		player.getPA().sendFrame126(" ", 11461);
		player.getPA().sendFrame126(" ", 11459);
		player.getPA().sendFrame126(" ", 1132);
		player.getPA().sendFrame126(" ", 1096);


		player.getPA().sendFrame34Other(-1, 4, 1120, 1);
		player.getPA().sendFrame34Other(-1, 4, 1121, 1);
		player.getPA().sendFrame34Other(-1, 4, 1123, 1);
		player.getPA().sendFrame126(" ", 8428);
		/*
		player.getPA().sendFrame126(" ", 1135);
		player.getPA().sendFrame126(" ", 1134);
		player.getPA().sendFrame126(" ", 11461);
		player.getPA().sendFrame126(" ", 11459);
		player.getPA().sendFrame126(" ", 1132);
		*/
		player.getPA().sendFrame126(" ", 8429); // Claws.
		player.getPA().displayInterface(994);
	}

	private static String GetForlvl(int i, Player player) {
		if (player.baseSkillLevel[ServerConstants.SMITHING] >= i)
			return "@whi@";

		return "@bla@";
	}

	private static String GetForBars(int i, int j, Player player) {
		if (ItemAssistant.hasItemAmountInInventory(player, i, j))
			return "@gre@";

		return "@red@";
	}

}
