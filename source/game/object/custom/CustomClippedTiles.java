package game.object.custom;

import core.GameType;
import game.content.skilling.Farming;
import game.object.clip.Region;
import network.connection.DonationManager;

public class CustomClippedTiles {


	private static void addSpecificDirectionClipping(int x, int y, int height, int direction) {
		Region.addClipping(x, y, height, direction, "different1", 0, 0);
	}

	public static void removeClippedTilePreEoc() {
		if (!GameType.isPreEoc()) {
			return;
		}
	}

	public static void removeClippedTileOsrs() {
		if (!GameType.isOsrs()) {
			return;
		}

		for (int index = 0; index < Region.removedObjectCoordinates.size(); index++) {
			String[] parse = Region.removedObjectCoordinates.get(index).split(" ");
			int parseX = Integer.parseInt(parse[0]);
			int parseY = Integer.parseInt(parse[1]);
			int parseHeight = Integer.parseInt(parse[2]);
			Region.removeClipping(parseX, parseY, parseHeight);
		}

		if (GameType.isOsrsEco()) {
			// Edgeville building north west of Edgeville bank.
			Region.removeClipping(3078, 3513, 0);
			Region.removeClipping(3078, 3514, 0);
			Region.removeClipping(3082, 3513, 0);
			Region.removeClipping(3082, 3514, 0);
			Region.removeClipping(3083, 3493, 0);
			Region.removeClipping(3082, 3493, 0);
		}

		Region.addObject(29867, 3331, 5327, 0, 10, 3, false);

		int CANNOT_ENTER_FROM_ANY_WEST_DIRECTION = 128;
		int CANNOT_ENTER_FROM_ANY_EAST_DIRECTION = 8;
		int CANNOT_ENTER_FROM_ANY_SOUTH_DIRECTION = 32;
		int CANNOT_ENTER_FROM_ANY_NORTH_DIRECTION = 2;

		//Clipping fix for donator zone
		addSpecificDirectionClipping(2198, 3241, 0, CANNOT_ENTER_FROM_ANY_SOUTH_DIRECTION);
		addSpecificDirectionClipping(2198, 3241, 0, CANNOT_ENTER_FROM_ANY_WEST_DIRECTION);

		// Clipping fixes for Edgeville general store.
		addSpecificDirectionClipping(3084, 3512, 0, CANNOT_ENTER_FROM_ANY_EAST_DIRECTION);
		addSpecificDirectionClipping(3077, 3507, 0, CANNOT_ENTER_FROM_ANY_SOUTH_DIRECTION);
		addSpecificDirectionClipping(3079, 3507, 0, CANNOT_ENTER_FROM_ANY_SOUTH_DIRECTION);
		addSpecificDirectionClipping(3083, 3507, 0, CANNOT_ENTER_FROM_ANY_SOUTH_DIRECTION);
		addSpecificDirectionClipping(3076, 3509, 0, CANNOT_ENTER_FROM_ANY_WEST_DIRECTION);
		addSpecificDirectionClipping(3076, 3510, 0, CANNOT_ENTER_FROM_ANY_WEST_DIRECTION);
		addSpecificDirectionClipping(3076, 3511, 0, CANNOT_ENTER_FROM_ANY_WEST_DIRECTION);
		addSpecificDirectionClipping(3076, 3512, 0, CANNOT_ENTER_FROM_ANY_WEST_DIRECTION);

		// Clipping fixes for Edgeville building, that is north of Edgeville bank.
		addSpecificDirectionClipping(3091, 3507, 0, CANNOT_ENTER_FROM_ANY_WEST_DIRECTION);
		addSpecificDirectionClipping(3091, 3511, 0, CANNOT_ENTER_FROM_ANY_WEST_DIRECTION);
		addSpecificDirectionClipping(3091, 3513, 0, CANNOT_ENTER_FROM_ANY_WEST_DIRECTION);
		addSpecificDirectionClipping(3091, 3508, 0, CANNOT_ENTER_FROM_ANY_WEST_DIRECTION);
		addSpecificDirectionClipping(3101, 3510, 0, CANNOT_ENTER_FROM_ANY_EAST_DIRECTION);
		addSpecificDirectionClipping(3101, 3509, 0, CANNOT_ENTER_FROM_ANY_EAST_DIRECTION);
		addSpecificDirectionClipping(3101, 3510, 0, CANNOT_ENTER_FROM_ANY_WEST_DIRECTION);
		addSpecificDirectionClipping(3101, 3509, 0, CANNOT_ENTER_FROM_ANY_WEST_DIRECTION);
		addSpecificDirectionClipping(3093, 3507, 0, CANNOT_ENTER_FROM_ANY_SOUTH_DIRECTION);
		addSpecificDirectionClipping(3096, 3507, 0, CANNOT_ENTER_FROM_ANY_SOUTH_DIRECTION);
		addSpecificDirectionClipping(3097, 3507, 0, CANNOT_ENTER_FROM_ANY_SOUTH_DIRECTION);
		addSpecificDirectionClipping(3098, 3507, 0, CANNOT_ENTER_FROM_ANY_SOUTH_DIRECTION);
		addSpecificDirectionClipping(3099, 3507, 0, CANNOT_ENTER_FROM_ANY_SOUTH_DIRECTION);
		addSpecificDirectionClipping(3100, 3507, 0, CANNOT_ENTER_FROM_ANY_SOUTH_DIRECTION);
		addSpecificDirectionClipping(3095, 3507, 0, CANNOT_ENTER_FROM_ANY_SOUTH_DIRECTION);
		addSpecificDirectionClipping(3094, 3507, 0, CANNOT_ENTER_FROM_ANY_SOUTH_DIRECTION);
		addSpecificDirectionClipping(3091, 3507, 0, CANNOT_ENTER_FROM_ANY_SOUTH_DIRECTION);
		addSpecificDirectionClipping(3100, 3510, 0, CANNOT_ENTER_FROM_ANY_EAST_DIRECTION);
		addSpecificDirectionClipping(3100, 3509, 0, CANNOT_ENTER_FROM_ANY_EAST_DIRECTION);
		addSpecificDirectionClipping(3100, 3507, 0, CANNOT_ENTER_FROM_ANY_EAST_DIRECTION);
		addSpecificDirectionClipping(3100, 3512, 0, CANNOT_ENTER_FROM_ANY_EAST_DIRECTION);
		addSpecificDirectionClipping(3100, 3513, 0, CANNOT_ENTER_FROM_ANY_EAST_DIRECTION);
		addSpecificDirectionClipping(3100, 3508, 0, CANNOT_ENTER_FROM_ANY_EAST_DIRECTION);
		addSpecificDirectionClipping(3090, 3508, 0, CANNOT_ENTER_FROM_ANY_EAST_DIRECTION);
		addSpecificDirectionClipping(3091, 3513, 0, CANNOT_ENTER_FROM_ANY_NORTH_DIRECTION);
		addSpecificDirectionClipping(3092, 3513, 0, CANNOT_ENTER_FROM_ANY_NORTH_DIRECTION);
		addSpecificDirectionClipping(3093, 3513, 0, CANNOT_ENTER_FROM_ANY_NORTH_DIRECTION);
		addSpecificDirectionClipping(3094, 3513, 0, CANNOT_ENTER_FROM_ANY_NORTH_DIRECTION);
		addSpecificDirectionClipping(3099, 3513, 0, CANNOT_ENTER_FROM_ANY_NORTH_DIRECTION);
		addSpecificDirectionClipping(3096, 3513, 0, CANNOT_ENTER_FROM_ANY_NORTH_DIRECTION);
		addSpecificDirectionClipping(3097, 3513, 0, CANNOT_ENTER_FROM_ANY_NORTH_DIRECTION);
		addSpecificDirectionClipping(3098, 3513, 0, CANNOT_ENTER_FROM_ANY_NORTH_DIRECTION);
		addSpecificDirectionClipping(3095, 3513, 0, CANNOT_ENTER_FROM_ANY_NORTH_DIRECTION);
		addSpecificDirectionClipping(3100, 3513, 0, CANNOT_ENTER_FROM_ANY_NORTH_DIRECTION);
		addSpecificDirectionClipping(2192, 3250, 0, CANNOT_ENTER_FROM_ANY_SOUTH_DIRECTION);
		addSpecificDirectionClipping(2192, 3250, 0, CANNOT_ENTER_FROM_ANY_EAST_DIRECTION);
		addSpecificDirectionClipping(2192, 3250, 0, CANNOT_ENTER_FROM_ANY_WEST_DIRECTION);

		// Blood money stall @ donator zone
		Region.addObjectActionTile(29165, 2196, 3250, 0, 2, 2);
		// Max dummy @ donator zone
		//Region.addObjectActionTile(18242, 2192, 3250, 0, 1, 1);

		//staffzone
		Region.addObjectActionTile(312, 2948, 2801, 0, 1, 1); //potato plant
		Region.addObjectActionTile(3195, 2955, 2806, 0, 1, 1); //rotten tomatoes

		// Wilderness ardougne lever
		Region.addObjectActionTile(1814, 3153, 3923, 0, 0, 0);

		//objects for orb charging
		Region.addObjectActionTile(15268, 3086, 3570, 0, 1, 1);
		Region.addObjectActionTile(15269, 3086, 3569, 0, 1, 1);
		Region.addObjectActionTile(15270, 3088, 3560, 0, 1, 1);
		Region.addObjectActionTile(15271, 3089, 9960, 0, 1, 1);
		Region.addObjectActionTile(15275, 3086, 3571, 0, 1, 1);

		// Warriors guild gates when you open it, then it closes and spawns new objects.
		Region.addObjectActionTile(24306, 2847, 3541, 2, 0, 0);
		Region.addObjectActionTile(24309, 2847, 3540, 2, 0, 0);

		// Box of health.
		Region.addObjectActionTile(23709, 3090, 3512, 0, 1, 1);
		Region.addObjectActionTile(23709, 3091, 3493, 0, 1, 1);

		// Deposit vault at Dice zone
		Region.addObjectActionTile(29111, 1692, 4257, 0, 2, 2);

		// varrock west bank fix
		Region.addObjectActionTile(7409, 3186, 3436, 0, 1, 1);

		// catherby bank fix
		Region.addObjectActionTile(6943, 2810, 3442, 0, 1, 1);

		// seers bank fix
		Region.addObjectActionTile(25808, 2728, 3494, 0, 1, 1);

		// warriors guild door fix
		Region.addObjectActionTile(818, 2853, 3546, 0, 1, 1);
		Region.addObjectActionTile(818, 2856, 3546, 0, 1, 1);

		Region.addObjectActionTile(17119, 3682, 9888, 0, 1, 1); //pool of slime for ecto

		if (DonationManager.EVENT_SALE.equals("Christmas")) {
			Region.addObjectActionTile(19038, 3083, 3502, 0, 3, 3); //Christmas tree
			Region.addObjectActionTile(29709, 3084, 3500, 0, 2, 2); //Present table
		}
		if (DonationManager.EVENT_SALE.equals("Easter")) {
			Region.addObjectActionTile(31878, 3101, 3502, 0, 2, 2); // Easter rabbit hole
			Region.addObjectActionTile(15301, 2520, 9327, 0, 2, 2); // Easter chocolate rock
			Region.addObjectActionTile(15301, 2519, 9317, 0, 2, 2); // Easter chocolate rock
			Region.addObjectActionTile(15299, 2516, 9324, 0, 1, 1); // Easter chocolate rock
			Region.addObjectActionTile(15299, 2518, 9320, 0, 1, 1); // Easter chocolate rock
			Region.addObjectActionTile(15300, 2513, 9323, 0, 2, 2); // Easter chocolate crater
		}

		// Resource area runite rocks
		Region.addObjectActionTile(7494, 3191, 3929, 0, 1, 1);
		Region.addObjectActionTile(7494, 3192, 3930, 0, 1, 1);

		if (GameType.isOsrsEco()) {
			// Home thieving stalls
			Region.addObjectActionTile(4875, 3093, 3506, 0, 1, 1);
			Region.addObjectActionTile(4876, 3094, 3506, 0, 1, 1);
			Region.addObjectActionTile(4874, 3095, 3506, 0, 1, 1);
			Region.addObjectActionTile(4877, 3096, 3506, 0, 1, 1);
			Region.addObjectActionTile(4878, 3097, 3506, 0, 1, 1); //stalls

			Region.addObjectActionTile(30253, 3092, 3513, 0, 2, 1); //altar
			Region.addObjectActionTile(13213, 3091, 3513, 0, 1, 1); //burners
			Region.addObjectActionTile(13213, 3094, 3513, 0, 1, 1); //burners

			//Region.addObjectActionTile(30251, 3083, 3502, 0, 3, 3); //Well of godwill

			Region.addObjectActionTile(172, 3081, 3495, 0, 1, 1); //Crystal key chest
			Region.addObjectActionTile(3194, 3081, 3491, 0, 1, 1); //Bank chest

			//Fossil island
			Region.addObjectActionTile(3194, 3742, 3805, 0, 1, 1); //Bank chest

			Region.addObjectActionTile(29211, 3076, 3508, 0, 2, 2); //Skillcape stand

			// Blood key chest.
			Region.addObjectActionTile(27277, 3099, 3513, 0, 1, 1);

			//Dark altar
			Region.addObjectActionTile(29149, 3096, 3512, 0, 2, 2); //altar

			Region.addObjectActionTile(27290, 3091, 3495, 0, 1, 3); //loot key object

			Region.addObjectActionTile(5249, 2895, 3117, 0, 1, 1); //Fire @ karamb fishing spot


			addSpecificDirectionClipping(3079, 3513, 0, CANNOT_ENTER_FROM_ANY_WEST_DIRECTION);
			addSpecificDirectionClipping(3079, 3512, 0, CANNOT_ENTER_FROM_ANY_WEST_DIRECTION);
			addSpecificDirectionClipping(3079, 3512, 0, CANNOT_ENTER_FROM_ANY_SOUTH_DIRECTION);
			addSpecificDirectionClipping(3080, 3513, 0, CANNOT_ENTER_FROM_ANY_SOUTH_DIRECTION);
			addSpecificDirectionClipping(3080, 3512, 0, CANNOT_ENTER_FROM_ANY_SOUTH_DIRECTION);
			addSpecificDirectionClipping(3081, 3513, 0, CANNOT_ENTER_FROM_ANY_EAST_DIRECTION);
			addSpecificDirectionClipping(3081, 3512, 0, CANNOT_ENTER_FROM_ANY_EAST_DIRECTION);
			addSpecificDirectionClipping(3081, 3512, 0, CANNOT_ENTER_FROM_ANY_SOUTH_DIRECTION);
		}
		if (GameType.isOsrsPvp()) {
			// Edgeville building objects
			// Altar of the occult.
			Region.addObjectActionTile(29150, 3099, 3507, 0, 2, 2);
			// Blood key chest.
			Region.addObjectActionTile(27277, 3097, 3513, 0, 1, 1);
			// Altar.
			Region.addObjectActionTile(2640, 3093, 3513, 0, 2, 1);
			// Box of health by edge jail
			Region.addObjectActionTile(23709, 3112, 3513, 0, 1, 1);
		}

		// Completionist cape stand.
		Region.addObjectActionTile(29170, 3095, 3507, 0, 1, 1);
		// Highscores stand.
		Region.addObjectActionTile(563, 3093, 3507, 0, 1, 1);
		// Ladder
		//Region.addObjectActionTile(11, 3097, 3507, 0, 2, 1);

		//Falador market area
		Region.addObjectActionTile(25808, 3004, 3384, 0, 1, 1);
		Region.addObjectActionTile(25809, 3004, 3383, 0, 1, 1);
		Region.addObjectActionTile(25808, 3004, 3382, 0, 1, 1);


		// Tournament objects
		Region.addObjectActionTile(23709, 3328, 4753, 0, 1, 1);
		Region.addObjectActionTile(76, 3327, 4753, 0, 1, 1);

		// Duel arena chest opened.
		Region.addObjectActionTile(3194, 3381, 3269, 0, 1, 1);
		Region.addObjectActionTile(3194, 3382, 3270, 0, 1, 1);
		Region.addObjectActionTile(23709, 3327, 4757, 0, 1, 1); // Box of health at clan wars
		Region.addObjectActionTile(3194, 3328, 4757, 0, 1, 1); // Opened chest at clan wars

		// Gate at resource wilderness.
		Region.addObjectActionTile(26760, 3184, 3944, 0, 0, 0);

		// Oak tree at Entrana.
		Region.addObjectActionTile(1751, 2852, 3332, 0, 3, 3);
		// Yew tree at Entrana.
		Region.addObjectActionTile(1753, 2848, 3332, 0, 3, 3);
		// Willow tree at Entrana.
		Region.addObjectActionTile(1760, 2850, 3338, 0, 2, 2);
		// Magic tree at Entrana.
		Region.addObjectActionTile(1761, 2853, 3338, 0, 1, 1);

		// Anvil at Entrana
		Region.addObjectActionTile(2031, 2833, 3349, 0, 1, 1);

		// Altar at Entrana for Runecrafting
		Region.addObjectActionTile(14897, 2857, 3380, 0, 3, 3);

		// Bank booths at Entrana bank.
		Region.addObjectActionTile(6943, 2860, 3338, 0, 1, 1);
		Region.addObjectActionTile(6943, 2861, 3338, 0, 1, 1);
		Region.addObjectActionTile(6943, 2862, 3338, 0, 1, 1);
		Region.addObjectActionTile(6943, 2863, 3338, 0, 1, 1);

		// Bank booth at Donator zone
		//Region.addObjectActionTile(6943, 2192, 3250, 0, 1, 1);

		// Bank booth at Zombie waiting room.
		Region.addObjectActionTile(6943, 3658, 3513, 0, 1, 1);

		// Deposit box at Rune essence mine.
		Region.addObjectActionTile(6948, 2911, 4832, 0, 1, 1);

		Region.addObjectActionTile(14841, 2148, 3864, 0, 1, 1); // Portal at Astral altar
		Region.addObjectActionTile(14841, 1727, 3825, 0, 1, 1); // Portal at Blood altar
		Region.addObjectActionTile(14841, 1820, 3862, 0, 1, 1); // Portal at Soul altar

		// Falador dwarf mine.
		Region.addObjectActionTile(2031, 3042, 9746, 0, 1, 1); // Anvil.
		Region.addObjectActionTile(24009, 3037, 9746, 0, 3, 3); // Furnace.
		Region.addObjectActionTile(6943, 3043, 9736, 0, 1, 1); // Bank booth.
		Region.addObjectActionTile(7494, 3044, 9753, 0, 1, 1); // Runite ore
		Region.addObjectActionTile(7494, 3048, 9754, 0, 1, 1); // Runite ore
		Region.addObjectActionTile(7494, 3048, 9747, 0, 1, 1); // Runite ore
		Region.addObjectActionTile(7494, 3045, 9744, 0, 1, 1); // Runite ore
		Region.addObjectActionTile(7494, 3048, 9743, 0, 1, 1); // Runite ore
		Region.addObjectActionTile(7493, 3046, 9735, 0, 1, 1); // Adamant ore
		Region.addObjectActionTile(7493, 3047, 9737, 0, 1, 1); // Adamant ore
		Region.addObjectActionTile(7493, 3049, 9735, 0, 1, 1); // Adamant ore
		Region.addObjectActionTile(7493, 3049, 9734, 0, 1, 1); // Adamant ore
		Region.addObjectActionTile(7493, 3049, 9740, 0, 1, 1); // Adamant ore
		Region.addObjectActionTile(7492, 3045, 9741, 0, 1, 1); // Mithril ore
		Region.addObjectActionTile(7492, 3045, 9738, 0, 1, 1); // Mithril ore
		Region.addObjectActionTile(7492, 3046, 9742, 0, 1, 1); // Mithril ore
		Region.addObjectActionTile(7492, 3043, 9743, 0, 1, 1); // Mithril ore
		Region.addObjectActionTile(7489, 3032, 9741, 0, 1, 1); // Coal ore
		Region.addObjectActionTile(7489, 3034, 9739, 0, 1, 1); // Coal ore
		Region.addObjectActionTile(7455, 3035, 9742, 0, 1, 1); // Iron ore
		Region.addObjectActionTile(7455, 3037, 9739, 0, 1, 1); // Iron ore
		Region.addObjectActionTile(7455, 3037, 9735, 0, 1, 1); // Iron ore
		Region.addObjectActionTile(7455, 3035, 9734, 0, 1, 1); // Iron ore
		Region.addObjectActionTile(7485, 3032, 9737, 0, 1, 1); // Tin ore
		Region.addObjectActionTile(7485, 3031, 9739, 0, 1, 1); // Tin ore
		Region.addObjectActionTile(7485, 3028, 9739, 0, 1, 1); // Tin ore
		Region.addObjectActionTile(7484, 3028, 9737, 0, 1, 1); // Copper ore
		Region.addObjectActionTile(7484, 3029, 9734, 0, 1, 1); // Copper ore
		Region.addObjectActionTile(7484, 3032, 9735, 0, 1, 1); // Copper ore

		for (int i = 0; i < Farming.patchLoopAmount; i++) {
			for (int a = 0; a < Farming.patchLoopAmount; a++) {
				Region.addObjectActionTile(Farming.PATCH_HERBS, Farming.patchX + i, Farming.patchY + a, 0, 1, 1);
			}
		}
		for (int i = 0; i < Farming.patchLoopAmount; i++) {
			for (int a = 0; a < Farming.patchLoopAmount; a++) {
				Region.addObjectActionTile(Farming.patchCleanObject, Farming.patchX + i, Farming.patchY + a, 0, 1, 1);
			}
		}

		Region.addObject(3194, 3080, 3514, 0, 10, 0, false); // Chest at the west of Edge wilderness
		if (GameType.isOsrsPvp()) {
			Region.addObject(3194, 3096, 3514, 0, 10, 0, false); // Chest.
			Region.addObject(3194, 3112, 3516, 0, 10, 0, false); // Chest.
		}

		// Staff zone
		Region.addObjectActionTile(3194, 2698, 3250, 0, 1, 1); // Chest
		Region.addObjectActionTile(24452, 2699, 3250, 0, 1, 1); // Staff activity booth
		Region.removeClipping(2956, 2799, 0); // Fairy ring clipping removed

		// Gates in the wilderness and double doors.
		Region.addObject(1728, 3008, 3849, 0, 0, 3, true);
		Region.addObject(1727, 3008, 3850, 0, 0, 1, true);
		Region.addObject(1727, 2947, 3904, 0, 0, 0, true);
		Region.addObject(1728, 2948, 3904, 0, 0, 2, true);
		Region.addObject(1727, 3224, 3904, 0, 0, 0, true);
		Region.addObject(1728, 3225, 3904, 0, 0, 2, true);
		Region.addObject(1727, 3336, 3896, 0, 0, 0, true);
		Region.addObject(1728, 3337, 3896, 0, 0, 2, true);
		Region.addObject(1524, 2957, 3821, 0, 0, 1, true);
		Region.addObject(1521, 2957, 3820, 0, 0, 3, true);
		Region.addObject(1727, 3201, 3856, 0, 0, 0, true);
		Region.addObject(1728, 3202, 3856, 0, 0, 2, true);
		Region.addObject(14752, 3022, 3631, 0, 0, 3, true);
		Region.addObject(14751, 3022, 3632, 0, 0, 1, true);

		Region.addObject(1728, 3040, 10308, 0, 0, 1, true);
		Region.addObject(1727, 3040, 10307, 0, 0, 3, true);

		Region.addObject(1728, 3044, 10342, 0, 0, 1, true);
		Region.addObject(1727, 3044, 10341, 0, 0, 3, true);

		Region.addObject(1728, 3022, 10312, 0, 0, 1, true);
		Region.addObject(1727, 3022, 10311, 0, 0, 3, true);

		// Slayer tower no clip fix.
		Region.addObjectActionTile(1, 3433, 3534, 0, 1, 1);
		Region.addObjectActionTile(1, 3432, 3535, 0, 1, 1);
		Region.addObjectActionTile(1, 3425, 3534, 0, 1, 1);
		Region.addObjectActionTile(1, 3433, 3534, 1, 1, 1);
		Region.addObjectActionTile(1, 3432, 3535, 1, 1, 1);
		Region.addObjectActionTile(1, 3425, 3534, 1, 1, 1);
		Region.addObjectActionTile(1, 3434, 3532, 1, 1, 1);
		Region.addObjectActionTile(1, 3435, 3532, 1, 1, 1);

		// Edgeville dungeon gate
		Region.addObjectActionTile(1727, 3104, 9909, 0, 0, 0);
		Region.addObjectActionTile(1728, 3104, 9910, 0, 0, 0);
		Region.addObjectActionTile(1728, 3131, 9918, 0, 0, 0);
		Region.addObjectActionTile(1727, 3132, 9918, 0, 0, 0);
		Region.addObjectActionTile(1728, 3105, 9945, 0, 0, 0);
		Region.addObjectActionTile(1727, 3106, 9945, 0, 0, 0);
		Region.addObjectActionTile(1728, 3146, 9871, 0, 0, 0);
		Region.addObjectActionTile(1727, 3146, 9870, 0, 0, 0);

		// Ladder that leads down to black demon area inside Edgeville dungeon.
		Region.addObjectActionTile(16680, 3088, 3571, 0, 1, 1);

		// Resource wilderness area fence
		addSpecificDirectionClipping(3183, 3945, 0, CANNOT_ENTER_FROM_ANY_SOUTH_DIRECTION);
		addSpecificDirectionClipping(3183, 3944, 0, CANNOT_ENTER_FROM_ANY_NORTH_DIRECTION);
		addSpecificDirectionClipping(3184, 3945, 0, CANNOT_ENTER_FROM_ANY_SOUTH_DIRECTION);
		addSpecificDirectionClipping(3184, 3944, 0, CANNOT_ENTER_FROM_ANY_NORTH_DIRECTION);

		// Jail clipping fixes
		addSpecificDirectionClipping(3018, 3187, 0, CANNOT_ENTER_FROM_ANY_NORTH_DIRECTION);
		addSpecificDirectionClipping(3014, 3187, 0, CANNOT_ENTER_FROM_ANY_NORTH_DIRECTION);

		// Fix for no-clipping out of Wilderness at level 49.
		for (int index = 0; index < 34; index++) {
			addSpecificDirectionClipping(2944, 3904 + index, 0, CANNOT_ENTER_FROM_ANY_EAST_DIRECTION);
		}

		// Fix for no-clipping out of Canafis PvP Zone
		for (int index = 0; index < 63; index++) {
			addSpecificDirectionClipping(2880, 4672 + index, 0, CANNOT_ENTER_FROM_ANY_EAST_DIRECTION);
		}
		for (int index = 0; index < 63; index++) {
			addSpecificDirectionClipping(2943, 4672 + index, 0, CANNOT_ENTER_FROM_ANY_WEST_DIRECTION);
		}
		for (int index = 0; index < 63; index++) {
			addSpecificDirectionClipping(2880 + index, 4735, 0, CANNOT_ENTER_FROM_ANY_SOUTH_DIRECTION);
		}

		removeGatesAndDoorClipping();
		Region.removeClipping(3095, 3523, 0); // Invisible object at Edgeville wilderness
		Region.removeClipping(3101, 3523, 0); // Invisible object at Edgeville wilderness
		Region.removeClipping(3098, 3522, 0); // Invisible object at Edgeville wilderness
		Region.removeClipping(3099, 3525, 0); // Invisible object at Edgeville wilderness
		Region.removeClipping(3097, 3525, 0); // Invisible object at Edgeville wilderness
		Region.removeClipping(3115, 3526, 0); // Invisible object at Edgeville wilderness
		Region.removeClipping(3081, 3527, 0); // Invisible object at Edgeville wilderness
		Region.removeClipping(3081, 3527, 0); // Invisible object north of Black knights castle
		Region.removeClipping(3017, 3527, 0); // Invisible object north of Black knights castle

		//1, cannot enter from north west
		//4, cannot enter from north east
		//16, cannot enter from south east
		//64, cannot enter from south west

		//130, cannot enter from all the north and west blocks.
		//10
		//40
		//160

		//65536
		//1024
		//4096
		//16384

		//512
		//2048
		//8192
		//32768
	}

	private static void removeGatesAndDoorClipping() {

		// Door, Taverly dungeon
		Region.removeClipping(2924, 9803, 0);
		Region.removeClipping(2923, 9803, 0);

		// Gates, Taverly dungeon
		Region.removeClipping(2898, 9832, 0);
		Region.removeClipping(2897, 9832, 0);
		Region.removeClipping(2898, 9831, 0);
		Region.removeClipping(2897, 9831, 0);

		// Entrana wooden gate, north east of Entrana after the bridge.
		Region.removeClipping(2854, 3370, 0);
		Region.removeClipping(2854, 3371, 0);
		Region.removeClipping(2855, 3370, 0);
		Region.removeClipping(2855, 3371, 0);

		// King black dragon gates
		Region.removeClipping(3007, 3850, 0);
		Region.removeClipping(3007, 3849, 0);
		Region.removeClipping(3008, 3850, 0);
		Region.removeClipping(3008, 3849, 0);

		// Yak gates
		Region.removeClipping(2326, 3802, 0);
		Region.removeClipping(2326, 3801, 0);

		// Gates at Chaos altar at 38 wilderness nearly near the snow
		Region.removeClipping(2958, 3820, 0);
		Region.removeClipping(2958, 3821, 0);
		Region.removeClipping(2957, 3820, 0);
		Region.removeClipping(2957, 3821, 0);

		// Gates at 49 wilderness. North west of the wilderness
		Region.removeClipping(2948, 3904, 0);
		Region.removeClipping(2947, 3904, 0);
		Region.removeClipping(2948, 3903, 0);
		Region.removeClipping(2947, 3903, 0);

		// Gates at Black knights fortress in multi at level 15
		Region.removeClipping(3021, 3632, 0);
		Region.removeClipping(3021, 3631, 0);
		Region.removeClipping(3022, 3632, 0);
		Region.removeClipping(3022, 3631, 0);

		// Unused place in the wilderness at level 43 near King black dragon gates.
		Region.removeClipping(3071, 3857, 0);
		Region.removeClipping(3071, 3856, 0);
		Region.removeClipping(3072, 3857, 0);
		Region.removeClipping(3072, 3856, 0);

		// Unused place in the wilderness at level 44 near King black dragon gates.
		Region.removeClipping(3075, 3867, 0);
		Region.removeClipping(3076, 3867, 0);
		Region.removeClipping(3075, 3868, 0);
		Region.removeClipping(3076, 3868, 0);

		// Gates south of Callisto at level 43
		Region.removeClipping(3202, 3856, 0);
		Region.removeClipping(3201, 3856, 0);
		Region.removeClipping(3202, 3855, 0);
		Region.removeClipping(3201, 3855, 0);

		// Gates near hellhounds at level 49 wilderness
		Region.removeClipping(3225, 3904, 0);
		Region.removeClipping(3224, 3904, 0);
		Region.removeClipping(3225, 3903, 0);
		Region.removeClipping(3224, 3903, 0);

		// Gates near Chaos elemental at level 48 wilderness
		Region.removeClipping(3337, 3896, 0);
		Region.removeClipping(3336, 3896, 0);
		Region.removeClipping(3337, 3895, 0);
		Region.removeClipping(3336, 3895, 0);

	}
}
