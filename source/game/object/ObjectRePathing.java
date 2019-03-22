package game.object;

import game.content.miscellaneous.DiceSystem;
import game.player.Area;
import game.player.Player;
import game.player.movement.Follow;

/**
 * Re-path the walking path to a specific object.
 *
 * @author MGT Madness, created on 01-08-2015.
 */
public class ObjectRePathing {

	public static boolean collectObjectRePathingData(Player player, int object, int objectX, int objectY) {
		player.specialObjectActionPoint[0] = object;
		player.specialObjectActionPoint[1] = objectX;
		player.specialObjectActionPoint[2] = objectY;

		switch (object) {

			case 1996:
				if (objectX == 2547 && objectY == 2697) {
					player.specialObjectActionPoint[3] = 2553;
					player.specialObjectActionPoint[4] = 2697;
					return true;
				}
				break;

			case 16466:
				if (objectX == 2536 && objectY == 2704) {
					player.specialObjectActionPoint[3] = 2538;
					player.specialObjectActionPoint[4] = 2704;
					return true;
				}
				break;

			// Revenant caves shortcut
			case 31561 :
				if (objectX == 3202 && objectY == 10196) {
					if (player.getX() >= 3203) {
						player.specialObjectActionPoint[3] = 3204;
						player.specialObjectActionPoint[4] = 10196;
						return true;
					}
					else {
						player.specialObjectActionPoint[3] = 3200;
						player.specialObjectActionPoint[4] = 10196;
						return true;
					}
				}
				else if (objectX == 2860 && objectY == 10209) {
					if (player.getY() >= 10210) {
						player.specialObjectActionPoint[3] = 2860;
						player.specialObjectActionPoint[4] = 10211;
						return true;
					} else {
						player.specialObjectActionPoint[3] = 2860;
						player.specialObjectActionPoint[4] = 10207;
						return true;
					}
				}
				else if (objectX == 2882 && objectY == 10196) {
					if (player.getX() <= 2881) {
						player.specialObjectActionPoint[3] = 2880;
						player.specialObjectActionPoint[4] = 10196;
						return true;
					} else {
						player.specialObjectActionPoint[3] = 2884;
						player.specialObjectActionPoint[4] = 10196;
						return true;
					}
				}
				else if (objectX == 2880 && objectY == 10136) {
					if (player.getX() <= 2879) {
						player.specialObjectActionPoint[3] = 2878;
						player.specialObjectActionPoint[4] = 10136;
						return true;
					} else {
						player.specialObjectActionPoint[3] = 2882;
						player.specialObjectActionPoint[4] = 10136;
						return true;
					}
				}
				else if (objectX == 2921 && objectY == 10145) {
					if (player.getX() <= 2920) {
						player.specialObjectActionPoint[3] = 2919;
						player.specialObjectActionPoint[4] = 10145;
						return true;
					} else {
						player.specialObjectActionPoint[3] = 2923;
						player.specialObjectActionPoint[4] = 10145;
						return true;
					}
				}
				else if (objectX == 2900 && objectY == 10086) {
					if (player.getY() <= 10085) {
						player.specialObjectActionPoint[3] = 2900;
						player.specialObjectActionPoint[4] = 10084;
						return true;
					} else {
						player.specialObjectActionPoint[3] = 2900;
						player.specialObjectActionPoint[4] = 10088;
						return true;
					}
				}
				break;
			// Deposit vault at Dice zone.
			case 29111:
				Follow coordinate = Follow.getClosestPosition(player.getX(), player.getY(), DiceSystem.vaultCoorinates);
				player.specialObjectActionPoint[3] = coordinate.possibleX;
				player.specialObjectActionPoint[4] = coordinate.possibleY;
				return true;
			// Obstacle pipe, Edgeville dungeon
			case 16511:
				if (objectX == 3153 && objectY == 9906) {
					player.specialObjectActionPoint[3] = 3155;
					player.specialObjectActionPoint[4] = 9906;
					return true;
				}
				if (objectX == 3150 && objectY == 9906) {
					player.specialObjectActionPoint[3] = 3149;
					player.specialObjectActionPoint[4] = 9906;
					return true;
				}
				break;
			// Stepping stone, Brimhaven dungeon
			case 19040:
				if (objectX == 2684 && objectY == 9548) {
					player.specialObjectActionPoint[3] = 2682;
					player.specialObjectActionPoint[4] = 9548;
					return true;
				}
				if (objectX == 2688 && objectY == 9547) {
					player.specialObjectActionPoint[3] = 2690;
					player.specialObjectActionPoint[4] = 9547;
					return true;
				}
				if (objectX == 2695 && objectY == 9531) {
					player.specialObjectActionPoint[3] = 2695;
					player.specialObjectActionPoint[4] = 9533;
					return true;
				}
				if (objectX == 2696 && objectY == 9527) {
					player.specialObjectActionPoint[3] = 2697;
					player.specialObjectActionPoint[4] = 9525;
					return true;
				}
				break;

			// Log balance, Brimhaven dungeon
			case 20884:
				if (objectX == 2686 && objectY == 9506) {
					player.specialObjectActionPoint[3] = 2687;
					player.specialObjectActionPoint[4] = 9506;
					return true;
				}
				break;

			// Log balance, Brimhaven dungeon
			case 20882:
				if (objectX == 2683 && objectY == 9506) {
					player.specialObjectActionPoint[3] = 2682;
					player.specialObjectActionPoint[4] = 9506;
					return true;
				}
				break;

			// Pipe, Brimhaven dungeon
			case 21728:
				if (objectX == 2655 && objectY == 9567) {
					player.specialObjectActionPoint[3] = 2655;
					player.specialObjectActionPoint[4] = 9566;
					return true;
				}
				if (objectX == 2655 && objectY == 9571) {
					player.specialObjectActionPoint[3] = 2655;
					player.specialObjectActionPoint[4] = 9573;
					return true;
				}
				break;
			case 272:
				if (objectX == 3018 && objectY == 3957) {
					player.specialObjectActionPoint[3] = 3018;
					player.specialObjectActionPoint[4] = 3958;
					return true;
				}
				break;
			case 273:
				if (objectX == 3018 && objectY == 3957) {
					player.specialObjectActionPoint[3] = 3018;
					player.specialObjectActionPoint[4] = 3958;
					return true;
				}
				break;
			case 245:
				if (objectX == 3017 && objectY == 3959) {
					player.specialObjectActionPoint[3] = 3017;
					player.specialObjectActionPoint[4] = 3958;
					return true;
				}
				if (objectX == 3019 && objectY == 3959) {
					player.specialObjectActionPoint[3] = 3019;
					player.specialObjectActionPoint[4] = 3958;
					return true;
				}
				break;
			// Lever
			case 1816:
				if (objectX == 3067 && objectY == 10253) {
					player.specialObjectActionPoint[3] = 3067;
					player.specialObjectActionPoint[4] = 10253;
					return true;
				}
				break;
			// Lever
			case 1817:
				if (objectX == 3153 && objectY == 3923) {
					player.specialObjectActionPoint[3] = 3153;
					player.specialObjectActionPoint[4] = 3923;
					return true;
				}
				break;



			// Lever
			case 19043:
				if (objectX == 3048 && objectY == 10335) {
					player.specialObjectActionPoint[3] = 3048;
					player.specialObjectActionPoint[4] = 10336;
					return true;
				}
				break;
			// Lever
			case 9707:
				if (objectX == 3105 && objectY == 3952) {
					player.specialObjectActionPoint[3] = 3105;
					player.specialObjectActionPoint[4] = 3951;
					return true;
				}
				break;
			// Lever
			case 9706:
				if (objectX == 3104 && objectY == 3956) {
					player.specialObjectActionPoint[3] = 3105;
					player.specialObjectActionPoint[4] = 3956;
					return true;
				}
				break;
			// Lever
			case 5959:
				if (objectX == 3090 && objectY == 3956) {
					player.specialObjectActionPoint[3] = 3090;
					player.specialObjectActionPoint[4] = 3956;
					return true;
				}
				break;
			// Ladder at Kbd ontop at lesser.
			case 18987:
				if (objectX == 3017 && objectY == 3849) {
					player.specialObjectActionPoint[3] = 3017;
					player.specialObjectActionPoint[4] = 3850;
					return true;
				}
				break;
			// Crevice at Lava dragons level 28 wilderness
			case 26766:
				if (objectX == 3016 && objectY == 3738) {
					player.specialObjectActionPoint[3] = 3017;
					player.specialObjectActionPoint[4] = 3740;
					return true;
				}
				break;

			// Crevice at Lava dragons level 28 wilderness
			case 26767:
				if (objectX == 3065 && objectY == 10160) {
					player.specialObjectActionPoint[3] = 3065;
					player.specialObjectActionPoint[4] = 10159;
					return true;
				}
				break;

			// Web
			case 733:
				boolean firstCompartment = Area.isWithInArea(player, 3090, 3092, 3956, 3958) || Area.isWithInArea(player, 3090, 3091, 3955, 3955);
				boolean inside = Area.isWithInArea(player, 3105, 3107, 3955, 3957);
				// Mage bank web
				if (objectX == 3092 && objectY == 3957) {
					if (firstCompartment) {
						player.specialObjectActionPoint[3] = 3092;
						player.specialObjectActionPoint[4] = objectY;
						return true;
					}
				} else if (objectX == 3095 && objectY == 3957) {
					if (!firstCompartment && !Area.isWithInArea(player, 3093, 3094, 3956, 3958)) {
						player.specialObjectActionPoint[3] = 3095;
						player.specialObjectActionPoint[4] = 3957;
						return true;
					}
				} else if (objectX == 3105 && objectY == 3958) {
					if (!inside) {
						player.specialObjectActionPoint[3] = 3105;
						player.specialObjectActionPoint[4] = 3958;
						return true;
					}
				} else if (objectX == 3106 && objectY == 3958) {
					if (!inside) {
						player.specialObjectActionPoint[3] = 3106;
						player.specialObjectActionPoint[4] = 3958;
						return true;
					}
				}
				break;
			case 11726:

				// Inside the hut.
				if (player.getX() >= 3187 && player.getX() <= 3194 && player.getY() >= 3958 && player.getY() <= 3962) {
					// South gate.
					if (objectX == 3190 && objectY == 3957) {
						player.specialObjectActionPoint[3] = 3190;
						player.specialObjectActionPoint[4] = 3958;
						return true;
					} else {
						player.specialObjectActionPoint[3] = 3191;
						player.specialObjectActionPoint[4] = 3962;
						return true;
					}
				} else {
					// South gate.
					if (objectX == 3190 && objectY == 3957) {
						player.specialObjectActionPoint[3] = 3190;
						player.specialObjectActionPoint[4] = 3957;
						return true;
					} else {
						player.specialObjectActionPoint[3] = 3191;
						player.specialObjectActionPoint[4] = 3963;
						return true;
					}
				}
				// Gate of Resource wilderness area
			case 26760:
				player.specialObjectActionPoint[3] = 3184;
				player.specialObjectActionPoint[4] = player.getY() >= 3945 ? 3945 : 3944;
				return true;
			// Pipe at Edgeville dungeon that leads to Moss giants.
			case 9295:
				if (objectX == 3150 && objectY == 9906 || objectX == 3153 && objectY == 9906) {
					player.specialObjectActionPoint[3] = player.getX() >= 3153 ? 3155 : 3149;
					player.specialObjectActionPoint[4] = 9906;
					return true;
				}
				break;

			// Large door at Edgeville men area.
			case 26910:
				if (objectX == 3101 && objectY == 3509) {
					player.specialObjectActionPoint[3] = player.getX() >= 3101 ? 3101 : 3100;
					player.specialObjectActionPoint[4] = 3509;
					return true;
				}
				break;

			// Large door at Edgeville men area.
			case 26913:
				if (objectX == 3101 && objectY == 3510) {
					player.specialObjectActionPoint[3] = player.getX() >= 3101 ? 3101 : 3100;
					player.specialObjectActionPoint[4] = 3510;
					return true;
				}
				break;

			// Door at Entrana to access Tanner.
			case 1533:
				if (objectX == 2822 && objectY == 3354) {
					player.specialObjectActionPoint[3] = player.getX() >= 2823 ? 2823 : 2822;
					player.specialObjectActionPoint[4] = 3354;
					return true;
				}
				break;

			// Warrior's guild door.
			case 15644:
				if (objectX == 2855 && objectY == 3546) {
					player.specialObjectActionPoint[3] = 2855;
					player.specialObjectActionPoint[4] = player.getY() >= 3546 ? 3546 : 3545;
					return true;
				} else if (objectX == 2847 && objectY == 3541) {
					player.specialObjectActionPoint[3] = player.getX() >= 2847 ? 2847 : 2846;
					player.specialObjectActionPoint[4] = 3541;
					return true;
				}
				break;

			// Warrior's guild door.
			case 15641:
				if (objectX == 2854 && objectY == 3546) {
					player.specialObjectActionPoint[3] = 2854;
					player.specialObjectActionPoint[4] = player.getY() >= 3546 ? 3546 : 3545;
					return true;
				} else if (objectX == 2847 && objectY == 3540) {
					player.specialObjectActionPoint[3] = player.getX() >= 2847 ? 2847 : 2846;
					player.specialObjectActionPoint[4] = 3540;
					return true;
				}
				break;

			// Manhole.
			case 882:
				if (objectX == 3237 && objectY == 3458) {
					player.specialObjectActionPoint[3] = 3237;
					player.specialObjectActionPoint[4] = 3459;
					return true;
				}
				break;

			case 1557:
			case 1558:
				if (objectX == 3103 && objectY == 9910) {
					player.specialObjectActionPoint[3] = player.getX() >= 3104 ? 3104 : 3103;
					player.specialObjectActionPoint[4] = 9910;
					return true;
				} else if (objectX == 3103 && objectY == 9909) {
					player.specialObjectActionPoint[3] = player.getX() >= 3104 ? 3104 : 3103;
					player.specialObjectActionPoint[4] = 9909;
					return true;
				} else if ((objectX == 3105 || objectX == 3106) && objectY == 9944) {
					int y = player.getY() <= 9944 ? 9944 : 9945;
					if (objectX == 3105 && objectY == 9944) {
						player.specialObjectActionPoint[3] = 3105;
						player.specialObjectActionPoint[4] = y;
						return true;
					} else {
						player.specialObjectActionPoint[3] = 3106;
						player.specialObjectActionPoint[4] = y;
						return true;
					}
				} else if (objectX == 3145 && (objectY == 9870 || objectY == 9871)) {
					int x = player.getX() >= 3146 ? 3146 : 3145;
					if (objectY == 9870) {
						player.specialObjectActionPoint[3] = x;
						player.specialObjectActionPoint[4] = 9870;
						return true;
					} else {
						player.specialObjectActionPoint[3] = x;
						player.specialObjectActionPoint[4] = 9871;
						return true;
					}
				}
				break;

			// Magix axe hut in the wilderness.
			case 11834:
				if (objectX == 3190 && objectY == 3957) {
					player.specialObjectActionPoint[3] = 3190;
					player.specialObjectActionPoint[4] = player.getY() == 3958 ? 3958 : 3957;
					return true;
				} else if (objectX == 3191 && objectY == 3963) {
					player.specialObjectActionPoint[3] = 3191;
					player.specialObjectActionPoint[4] = player.getY() == 3962 ? 3962 : 3963;
					return true;
				}
				break;

			// Farming patch at falador.
			case 8389:
				if (objectX == 3003 && objectY == 3372) {
					player.specialObjectActionPoint[3] = 3006;
					player.specialObjectActionPoint[4] = 3373;
					return true;
				}
				break;

			// Door inside Rogue's den.
			case 7259:
				if (objectX == 3061 && objectY == 4984) {
					player.specialObjectActionPoint[3] = 3061;
					player.specialObjectActionPoint[4] = player.getY() >= 4984 ? 4984 : 4983;
					return true;
				}
				break;

			// Corporeal beast gate.
			case 6452:
				if (objectX == 3305 && objectY == 9376) {
					player.specialObjectActionPoint[3] = player.getX() >= 3305 ? 3305 : 3304;
					player.specialObjectActionPoint[4] = 9376;
					return true;
				}
				break;

			// Corporeal beast gate.
			case 6451:
				if (objectX == 3305 && objectY == 9375) {
					player.specialObjectActionPoint[3] = player.getX() >= 3305 ? 3305 : 3304;
					player.specialObjectActionPoint[4] = 9375;
					return true;
				}
				break;

			// A wooden log, Karamja.
			case 2332:
				player.specialObjectActionPoint[3] = player.getX() >= 2910 ? 2910 : 2906;
				player.specialObjectActionPoint[4] = 3049;
				return true;

			// Door, Mining guild.
			case 11822:
				if (objectX == 3015 && objectY == 3333) {
					player.specialObjectActionPoint[3] = player.getX() >= 3016 ? 3016 : 3015;
					player.specialObjectActionPoint[4] = 3333;
					return true;
				} else if (objectX == 3013 && objectY == 3335) {
					player.specialObjectActionPoint[3] = 3013;
					player.specialObjectActionPoint[4] = player.getY() <= 3334 ? 3334 : 3335;
					return true;
				} else if (objectX == 3014 && objectY == 3340) {
					player.specialObjectActionPoint[3] = player.getX() <= 3013 ? 3013 : 3014;
					player.specialObjectActionPoint[4] = 3340;
					return true;
				}
				break;

			// Taverly dungeon, Obstacle pipe.
			case 9293:
				if (objectX == 2887 && objectY == 9799) {
					player.specialObjectActionPoint[3] = 2886;
					player.specialObjectActionPoint[4] = 9799;
					return true;
				}
				break;

			case 23566:
				// Monkeybars at Edgeville dungeon.
				if ((objectX == 3120 || objectX == 3119) && objectY == 9964) {
					player.specialObjectActionPoint[3] = 3120;
					player.specialObjectActionPoint[4] = 9963;
					return true;
				} else if ((objectX == 3120 || objectX == 3119) && objectY == 9969) {
					player.specialObjectActionPoint[3] = 3120;
					player.specialObjectActionPoint[4] = 9970;
					return true;
				}
				break;

			case 23137:
				// Obstacle pipe at Wilderness agility course.
				if (objectX == 3004 && objectY == 3938) {
					player.specialObjectActionPoint[3] = 3004;
					player.specialObjectActionPoint[4] = 3937;
					return true;
				}
				break;

			case 23552:
				// Gate exit at Wilderness agility course.
				if (objectX == 2998 && objectY == 3931) {
					player.specialObjectActionPoint[3] = 2998;
					player.specialObjectActionPoint[4] = 3931;
					return true;
				}
				break;

			case 23554:
				// Gate exit at Wilderness agility course.
				if (objectX == 2997 && objectY == 3931) {
					player.specialObjectActionPoint[3] = 2998;
					player.specialObjectActionPoint[4] = 3931;
					return true;
				}
				break;

			case 2878:
				// Mage bank sparkling pool.
				if (objectX == 2541 && objectY == 4719) {
					player.specialObjectActionPoint[3] = 2542;
					player.specialObjectActionPoint[4] = 4718;
					return true;
				}
				break;

			case 23145:
				// Gnome course, log balance.
				if (objectX == 2474 && objectY == 3435) {
					player.specialObjectActionPoint[3] = 2474;
					player.specialObjectActionPoint[4] = 3436;
					return true;
				}
				break;

			case 23138:
				// Gnome course, Obstacle pipe, left one.
				player.specialObjectActionPoint[3] = 2484;
				player.specialObjectActionPoint[4] = 3430;
				return true;

			case 23139:
				// Gnome course, Obstacle pipe, right one.
				player.specialObjectActionPoint[3] = 2487;
				player.specialObjectActionPoint[4] = 3430;
				return true;

			case 23131:
				// Barbarian course, Ropeswing.
				if (objectX == 2551 && objectY == 3550) {
					player.specialObjectActionPoint[3] = 2551;
					player.specialObjectActionPoint[4] = 3554;
					return true;
				}
				break;

			case 23144:
				// Barbarian course, Log balance.
				if (objectX == 2550 && objectY == 3546) {
					player.specialObjectActionPoint[3] = 2551;
					player.specialObjectActionPoint[4] = 3546;
					return true;
				}
				break;

			case 1948:
				// Barbarian course, First crumbling wall.
				if (objectX == 2536 && objectY == 3553) {
					player.specialObjectActionPoint[3] = 2535;
					player.specialObjectActionPoint[4] = 3553;
					return true;
				} else if (objectX == 2539 && objectY == 3553) {
					player.specialObjectActionPoint[3] = 2538;
					player.specialObjectActionPoint[4] = 3553;
					return true;
				} else if (objectX == 2542 && objectY == 3553) {
					player.specialObjectActionPoint[3] = 2541;
					player.specialObjectActionPoint[4] = 3553;
					return true;
				}
				break;

			case 23132:
				// Wilderness agility course, Rope swing.
				if (objectX == 3005 && objectY == 3952) {
					player.specialObjectActionPoint[3] = 3005;
					player.specialObjectActionPoint[4] = 3953;
					return true;
				}
				break;

			case 23556:
				// Wilderness course, Stepping stone.
				if (objectX == 3001 && objectY == 3960) {
					player.specialObjectActionPoint[3] = 3002;
					player.specialObjectActionPoint[4] = 3960;
					return true;
				}
				break;

			case 23542:
				// Wilderness course, Log balance
				if (objectX == 3001 && objectY == 3945) {
					player.specialObjectActionPoint[3] = 3002;
					player.specialObjectActionPoint[4] = 3945;
					return true;
				}
				break;

			case 11727:
				// Door, Pirate hut in Wilderness
				if (objectX == 3041 && objectY == 3959) {
					if (player.getY() <= 3959) {
						player.specialObjectActionPoint[3] = 3041;
						player.specialObjectActionPoint[4] = 3959;
						return true;
					} else {
						player.specialObjectActionPoint[3] = 3041;
						player.specialObjectActionPoint[4] = 3960;
						return true;
					}
				}

				// Door, Pirate hut in Wilderness
				else if (objectX == 3038 && objectY == 3956) {
					if (player.getX() >= 3038) {
						player.specialObjectActionPoint[3] = 3038;
						player.specialObjectActionPoint[4] = 3956;
						return true;
					} else {
						player.specialObjectActionPoint[3] = 3037;
						player.specialObjectActionPoint[4] = 3956;
						return true;
					}
				}

				// Door, Pirate hut in Wilderness
				else if (objectX == 3044 && objectY == 3956) {
					if (player.getX() <= 3044) {
						player.specialObjectActionPoint[3] = 3044;
						player.specialObjectActionPoint[4] = 3956;
						return true;
					} else {
						player.specialObjectActionPoint[3] = 3045;
						player.specialObjectActionPoint[4] = 3956;
						return true;
					}
				}
				break;

			// Door, Cooking guild.
			case 2712:
				if (objectX == 3143 && objectY == 3443) {
					if (player.getY() <= 3443) {
						player.specialObjectActionPoint[3] = 3143;
						player.specialObjectActionPoint[4] = 3443;
						return true;
					} else {
						player.specialObjectActionPoint[3] = 3143;
						player.specialObjectActionPoint[4] = 3444;
						return true;
					}
				}
				break;

			// Door, Ranging guild.
			case 2514:
				if (objectX == 2658 && objectY == 3438) {
					if (player.getY() >= 3438) {
						player.specialObjectActionPoint[3] = 2657;
						player.specialObjectActionPoint[4] = 3439;
						return true;
					} else {
						player.specialObjectActionPoint[3] = 2659;
						player.specialObjectActionPoint[4] = 3437;
						return true;
					}
				}
				break;

			// Magic guild door, Wizard's guild.
			case 1600:
			case 1601:
				if (objectX == 2597 && objectY == 3088) {
					if (player.getX() >= 2597) {
						player.specialObjectActionPoint[3] = 2597;
						player.specialObjectActionPoint[4] = 3088;
						return true;
					} else {
						player.specialObjectActionPoint[3] = 2596;
						player.specialObjectActionPoint[4] = 3088;
						return true;
					}
				} else if (objectX == 2597 && objectY == 3087) {
					int y = player.getObjectId() == 1601 ? 3088 : 3087;
					if (player.getX() >= 2597) {
						player.specialObjectActionPoint[3] = 2597;
						player.specialObjectActionPoint[4] = y;
						return true;
					} else {
						player.specialObjectActionPoint[3] = 2596;
						player.specialObjectActionPoint[4] = y;
						return true;
					}
				}
				break;

			// Door at Fishing guild.
			case 1530:
				if (objectX == 2611 && objectY == 3398) {
					if (player.getY() <= 3398) {
						player.specialObjectActionPoint[3] = 2611;
						player.specialObjectActionPoint[4] = 3398;
						return true;
					} else {
						player.specialObjectActionPoint[3] = 2611;
						player.specialObjectActionPoint[4] = 3399;
						return true;
					}
				}
				break;

			// Door at Fishing guild.
			case 2025:
				if (objectX == 2611 && objectY == 3394) {
					if (player.getY() <= 3393) {
						player.specialObjectActionPoint[3] = 2611;
						player.specialObjectActionPoint[4] = 3393;
						return true;
					} else {
						player.specialObjectActionPoint[3] = 2611;
						player.specialObjectActionPoint[4] = 3394;
						return true;
					}
				}
				break;

			// Gate at south west Falador, close to Make-over mage.
			case 1596:
			case 1597:
				if ((objectX == 2933 || objectX == 2934) && objectY == 3320) {
					int y = player.getY() >= 3320 ? 3320 : 3319;
					if (objectX == 2933 && objectY == 3320) {
						player.specialObjectActionPoint[3] = 2933;
						player.specialObjectActionPoint[4] = y;
						return true;
					} else {
						player.specialObjectActionPoint[3] = 2934;
						player.specialObjectActionPoint[4] = y;
						return true;
					}
				} else if ((objectX == 3131 || objectX == 3132) && objectY == 9917) {
					int y = player.getY() >= 9918 ? 9918 : 9917;
					if (objectX == 3131 && objectY == 9917) {
						player.specialObjectActionPoint[3] = 3131;
						player.specialObjectActionPoint[4] = y;
						return true;
					} else {
						player.specialObjectActionPoint[3] = 3132;
						player.specialObjectActionPoint[4] = y;
						return true;
					}
				}
				break;

			case 2647: // Guild door, Crafting guild.
				if (player.getY() <= 3288) {
					player.specialObjectActionPoint[3] = 2933;
					player.specialObjectActionPoint[4] = 3288;
					return true;
				}
				break;
		}
		return false;
	}

	public static void applyObjectRepathing(Player player, int object, int objectX, int objectY) {
		if (player.cannotIssueMovement) {
			return;
		}
		if (collectObjectRePathingData(player, player.getObjectId(), player.getObjectX(), player.getObjectY())) {
			if (object == player.specialObjectActionPoint[0] && objectX == player.specialObjectActionPoint[1] && objectY == player.specialObjectActionPoint[2]) {
				player.setWalkingPacketQueue(player.specialObjectActionPoint[3], player.specialObjectActionPoint[4], ObjectEvent::stoppedMovingObjectAction);
			}
		}
	}

}
