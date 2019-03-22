package game.npc;

import core.GameType;
import core.Plugin;
import core.ServerConfiguration;
import core.ServerConstants;
import game.content.combat.Combat;
import game.content.combat.Poison;
import game.content.combat.Venom;
import game.content.combat.vsnpc.CombatNpc;
import game.content.combat.vsplayer.Effects;
import game.content.combat.vsplayer.magic.MagicFormula;
import game.content.combat.vsplayer.melee.MeleeFormula;
import game.content.combat.vsplayer.range.RangedFormula;
import game.content.degrading.DegradingManager;
import game.content.interfaces.NpcDoubleItemsInterface;
import game.content.minigame.RecipeForDisaster;
import game.content.minigame.WarriorsGuild;
import game.content.minigame.barrows.Barrows;
import game.content.minigame.zombie.Zombie;
import game.content.miscellaneous.BraceletOfEthereum;
import game.content.miscellaneous.FightCaves;
import game.content.miscellaneous.GameTimeSpent;
import game.content.miscellaneous.GodWarsDungeonInterface;
import game.content.miscellaneous.OverlayTimer;
import game.content.miscellaneous.SpecialAttackTracker;
import game.content.music.SoundSystem;
import game.content.profile.NpcKillTracker;
import game.content.skilling.Skilling;
import game.content.skilling.Slayer;
import game.content.skilling.thieving.PickPocket;
import game.entity.EntityType;
import game.entity.MovementState;
import game.entity.combat_strategy.EntityCombatStrategy;
import game.item.ItemAssistant;
import game.log.GameTickLog;
import game.npc.data.NpcDefinition;
import game.npc.data.NpcDefinitionCleverJSON;
import game.npc.data.NpcDefinitionCombatJSON;
import game.npc.data.NpcDefinitionNonCombatJSON;
import game.npc.data.NpcSpawnBossJSON;
import game.npc.data.NpcSpawnCombatJSON;
import game.npc.data.NpcSpawnNonCombatJSON;
import game.npc.impl.ChaosFanatic;
import game.npc.impl.KrakenCombat;
import game.npc.impl.VenenatisTest;
import game.npc.impl.VetionCombat;
import game.npc.impl.donator_boss.DonatorBossCombatStrategy;
import game.npc.pet.Pet;
import game.object.clip.Region;
import game.player.Area;
import game.player.Player;
import game.player.PlayerHandler;
import game.player.movement.Follow;
import game.player.movement.Movement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import utility.Misc;
import utility.WebsiteLogInDetails;

public class NpcHandler {

	/**
	 * Last checked, i have 862 npc spawned on server start up. I also need to
	 * take in account pets spawned and barrows brothers spawned, Jad minigame,
	 * Zombies minigame.
	 */
	public final static int NPC_INDEX_OPEN_MAXIMUM = 2500;

	public static Npc npcs[] = new Npc[NPC_INDEX_OPEN_MAXIMUM];

	public static int random;

	/**
	 * True, if the 'Teleporter' NPC is performing an emote/action.
	 */
	public boolean teleporterInAction;

	private int[] rarelyWalkNpcs = {3257};

	/**
	 * Npcs that are undamagable, such as Whirlpools of the Kraken.
	 */
	public static boolean isUnkillableNpc(Npc npc) {
		switch (npc.npcType) {
			case 493: // Whirlpool
			case 496: // Master whirlpool
				return true;
		}
		return false;
	}

	public static Npc getNpcByNpcId(int npcId) {
		for (int index = 0; index < NpcHandler.NPC_INDEX_OPEN_MAXIMUM; index++) {
			Npc npc = NpcHandler.npcs[index];
			if (npc == null) {
				continue;
			}
			if (npc.npcType == npcId) {
				return npc;
			}
		}
		return null;
	}

	public static Npc getNpcByNpcIndex(int npcIndex) {
		if (NpcHandler.npcs[npcIndex] != null) {
			return NpcHandler.npcs[npcIndex];
		} else {
			return null;
		}
	}

	private boolean rarelyWalkNpcs(int npcType) {
		for (int i = 0; i < rarelyWalkNpcs.length; i++) {
			if (npcType == rarelyWalkNpcs[i]) {
				return Misc.hasPercentageChance(5);
			}
		}
		for (PickPocket.PickPocketData data : PickPocket.PickPocketData.values()) {
			if (npcType == data.getNpcId()) {
				return Misc.hasPercentageChance(5);
			}
		}
		if (Misc.hasPercentageChance(30)) {
			return true;
		}
		return false;
	}

	// How long the delay is then the damage hitsplat appears
	public int getHitDelay(Npc npc) {

		switch (npc.attackType) {
			case ServerConstants.MAGIC_ICON:
				if (NpcDefinition.getDefinitions()[npc.npcType].magicHitsplatDelay == 0) {
					return 4;
				}
				return NpcDefinition.getDefinitions()[npc.npcType].magicHitsplatDelay;
			case ServerConstants.MELEE_ICON:

				if (NpcDefinition.getDefinitions()[npc.npcType].meleeHitsplatDelay == 0) {
					return 2;
				}
				return NpcDefinition.getDefinitions()[npc.npcType].meleeHitsplatDelay;
			case ServerConstants.RANGED_ICON:
				if (NpcDefinition.getDefinitions()[npc.npcType].rangedHitsplatDelay == 0) {
					return 4;
				}
				return NpcDefinition.getDefinitions()[npc.npcType].rangedHitsplatDelay;
		}
		return 2;
	}

	// / Fight caves Npc
	public static boolean isFightCaveNpc(Npc npc) {
		switch (npc.npcType) {
			case 6506:
				// TzTok-Jad
				return true;
		}
		return false;

	}

	/**
	 * Npc cannot attack with melee if added here to attack with melee . Npc
	 * will hit a max of 1 with melee unless i add it into maxhit
	 **/
	public static boolean multiAttacks(Npc npc) {
		switch (npc.npcType) {
			case 2054:
				// Chaos Elemental
			case 5666: // Barrelchest
			case 8133: // Corporeal beast.
			case 6504: // Venenatis.
			case 3162:// Kree'arra.
			case 2205: // Commander Zilyana.
				if (npc.attackType == ServerConstants.MAGIC_ICON) {
					return true;
				}
				break;

			case 2215: // General Graardor.
				if (npc.attackType == ServerConstants.RANGED_ICON) {
					return true;
				}
				break;

		}
		return false;

	}

	/**
	 * Attempts to retrieve the first non-playable character within distance of
	 * the given x and y coordinates that is within distance of the given
	 * parameter.
	 *
	 * @param id the id of the npc that must match.
	 * @param x the x position of the distance point.
	 * @param y the y position of hte distance point.
	 * @param distance the maximum inclusive distance from the
	 * @return the first npc with the same id within distance of the given
	 * point, or null if none can be found.
	 */
	public static Npc getFirstByIdInDistance(int id, int x, int y, int height, int distance) {
		for (Npc npc : npcs) {
			if (npc == null || npc.isDead() || npc.npcType != id || npc.getHeight() != height
			    || npc.distanceTo(x, y) > distance) {
				continue;
			}
			return npc;
		}
		return null;
	}

	public static Npc[] getNpcsById(int npcType) {
		List<Npc> npcList = new ArrayList<>();
		for (Npc npc : npcs) {
			if (npc == null) {
				continue;
			}
			if (npc.npcType != npcType) {
				continue;
			}
			npcList.add(npc);
		}
		return npcList.toArray(new Npc[npcList.size()]);
	}

	public void loadSpell(Npc npc) {
		Player player = PlayerHandler.players[npc.getKillerId()];
		Npc n = npc;
		npc.useCleverBossMechanics = false;
		npc.cleverBossStopAttack = false;
		npc.cleverBossFixedDamage = -1;
		npc.bottomGfx = false;
		npc.projectileId = 0;
		npc.forceNormalNpcFixedDamage = -1;
		npc.hitThroughPrayerAmount = 0.0;
		npc.endGfx = -1;
		npc.startingGfx = -1;
		int npcX = npc.getVisualX();
		int npcY = npc.getVisualY();

		switch (npc.npcType) {

			// Karamel.
			case 6371:
				if (player.canBeFrozen()) {
					player.playerAssistant.sendFilterableMessage(ServerConstants.RED_COL + "You have been Frozen!");
					player.setFrozenLength(20000);
					OverlayTimer.sendOverlayTimer(player, ServerConstants.OVERLAY_TIMER_SPRITE_ICE_BARRAGE, 20);
					Movement.stopMovement(player);
					npc.endGfx = 281;
					npc.attackType = ServerConstants.MAGIC_ICON;
					player.gfx0(369);
					player.frozenBy = -1;
				}
				break;

			// Enormous Tentacle
			case 493:
				KrakenCombat.tentacleAttack(player, npc);
				break;
			// Skotizo
			case 7286:
				if (withInMeleeDistance(npcX, npcY, player.getX(), player.getY(), npc.npcType)) {
					random = Misc.random(1);
				} else {
					random = 0;
				}
				if (random == 1) {
					npc.attackType = ServerConstants.MELEE_ICON;
				} else {
					npc.attackType = ServerConstants.MAGIC_ICON;
					npc.projectileId = 1242;
				}
				break;
			// Verac
			case 1677:
				npc.hitThroughPrayerAmount = 1.0;
				break;

			// Infernal mage.
			case 443:
				npc.attackType = ServerConstants.MAGIC_ICON;
				npc.startingGfx = 129;
				npc.projectileId = 130;
				npc.endGfx = 131;
				break;

			// Elder chaos druid
			case 6607:
				npc.attackType = ServerConstants.MAGIC_ICON;
				npc.projectileId = 159;
				npc.endGfx = 160;
				npc.startingGfx = 158;
				elderChaosDruidAttack(player, npc);
				break;

			case 1443:
				if (withInMeleeDistance(npcX, npcY, player.getX(), player.getY(), npc.npcType)) {
					npc.attackType = ServerConstants.MELEE_ICON;
				} else {
					npc.attackType = ServerConstants.MAGIC_ICON;
					npc.projectileId = 139;
					npc.endGfx = 140;
				}
				break;

			// Dark wizards (level 7)
			case 5086:
			case 5087:
				npc.attackType = ServerConstants.MAGIC_ICON;
				npc.projectileId = 94;
				npc.endGfx = 95;
				npc.startingGfx = 93;
				break;

			// Dark wizards (level 20)
			case 5088:
			case 5089:
				npc.attackType = ServerConstants.MAGIC_ICON;
				npc.projectileId = 97;
				npc.endGfx = 98;
				npc.startingGfx = 96;
				break;

			// Dark wizards (level 7) (wild)
			case 2870:
				npc.attackType = ServerConstants.MAGIC_ICON;
				npc.projectileId = 94;
				npc.endGfx = 95;
				npc.startingGfx = 93;
				break;

			// Balfrug Kreeyath
			case 3132:
				npc.attackType = ServerConstants.MAGIC_ICON;
				npc.projectileId = 156;
				break;

			// Zakl'n Gritch
			case 3131:
				npc.attackType = ServerConstants.RANGED_ICON;
				break;

			case 1611:
				npc.endGfx = 76;
				npc.bottomGfx = true;
				npc.attackType = ServerConstants.MAGIC_ICON;
				if (Misc.hasPercentageChance(25)) {
					npc.forceChat("Hail Saradomin!");
				}
				break;

			case 1610:
				npc.endGfx = 78;
				npc.bottomGfx = true;
				npc.attackType = ServerConstants.MAGIC_ICON;
				if (Misc.hasPercentageChance(25)) {
					npc.forceChat("Hail Zamorak!");
				}
				break;

			case 1612:
				npc.endGfx = 77;
				npc.bottomGfx = true;
				npc.attackType = ServerConstants.MAGIC_ICON;
				if (Misc.hasPercentageChance(25)) {
					npc.forceChat("Hail Guthix!");
				}
				break;

			// Revenants
			case 11124:// dragon
			case 11125:// werewolf
				BraceletOfEthereum.reduceBracletCharges(player, npc);
				if (withInMeleeDistance(npcX, npcY, player.getX(), player.getY(), npc.npcType)) {
					random = Misc.random(2);
				} else {

					random = Misc.random(1);
				}
				if (random == 0) {
					npc.attackType = ServerConstants.MAGIC_ICON;
					npc.projectileId = 500;
				} else if (random == 1) {
					npc.attackType = ServerConstants.RANGED_ICON;
					npc.projectileId = 554;
					if (BraceletOfEthereum.hasChargedBracelet(player)) {
						npc.forceNormalNpcFixedDamage = 0;
					}
				} else if (random == 2) {
					npc.attackType = ServerConstants.MELEE_ICON;
					if (BraceletOfEthereum.hasChargedBracelet(player)) {
						npc.forceNormalNpcFixedDamage = 0;
					}
				}
				break;

			// Old revs
			case 11007:// dark beast
			case 11009:// hellhound
			case 11010:// ork
				if (withInMeleeDistance(npcX, npcY, player.getX(), player.getY(), npc.npcType)) {
					random = Misc.random(2);
				} else {

					random = Misc.random(1);
				}
				if (random == 0) {
					npc.attackType = ServerConstants.MAGIC_ICON;
					npc.projectileId = 500;
				} else if (random == 1) {
					npc.attackType = ServerConstants.RANGED_ICON;
					npc.projectileId = 554;
				} else if (random == 2) {
					npc.attackType = ServerConstants.MELEE_ICON;
				}
				break;

			// Tormented demon
			case 11011:

				if (withInMeleeDistance(npcX, npcY, player.getX(), player.getY(), npc.npcType)) {
					random = Misc.random(2);
				} else {

					random = Misc.random(1);
				}
				if (random == 0) {
					npc.attackType = ServerConstants.MAGIC_ICON;
					npc.projectileId = 2003;
				} else if (random == 1) {
					npc.attackType = ServerConstants.RANGED_ICON;
					npc.projectileId = 2004;
				} else if (random == 2) {
					npc.attackType = ServerConstants.MELEE_ICON;
					npc.gfx100(2001);
					npc.projectileId = -1;
				}
				break;
			// Skeletal wyvern
			case 465:

				if (withInMeleeDistance(npcX, npcY, player.getX(), player.getY(), npc.npcType)) {
					random = Misc.random(2);
				} else {

					random = Misc.random(1);
				}
				if (random == 0) {
					npc.attackType = ServerConstants.WYVERN_BREATH; // ice breath
					// attack (blue)
					npc.projectileId = 500;
					// npc.endGfx = 430;
					player.gfx100(501);
				} else if (random == 1) {
					npc.attackType = ServerConstants.RANGED_ICON;
					npc.gfx100(499);
				} else if (random == 2) {
					npc.attackType = ServerConstants.MELEE_ICON;
				}
				break;
			// Ice Strykewyrm
			case 11006:
				random = Misc.random(2);
				if (random <= 1) {
					npc.attackType = ServerConstants.MAGIC_ICON;
				} else if (random == 2) {
					npc.attackType = ServerConstants.RANGED_ICON;
				}
				break;

			// Venenatis.
			case 6504:
				VenenatisTest.venenatisAttack(npc, player);
				break;

			// Callisto Combat
			case 6503:
				if (ServerConfiguration.DEBUG_MODE) {
					if (withInMeleeDistance(npcX, npcY, player.getX(), player.getY(), npc.npcType)) {
						n.requestAnimation(4925);
						if (Misc.random(10) <= 5) {
							npc.attackType = ServerConstants.MELEE_ICON;
							callistoRoar(player, n.getX(), n.getY());
						} else {
							int offX = (n.getY() - player.getY()) * -1;
							int offY = (n.getX() - player.getX()) * -1;
							player.getPA().createPlayersProjectile(n.getX() + 1, n.getY() + 1, offX, offY, 50, 96, 395, 43,
							                                       31, 66, 5, 36);
						}
						npc.hitThroughPrayerAmount = 0.5;
					}
				} else {
					npc.hitThroughPrayerAmount = 0.5;
				}
				break;

			// Vet'ion Combat
			case 6611:
				VetionCombat.attackPlayer(player, npc);
				break;

			// Chaos Fanatic Combat
			case 6619:
				ChaosFanatic.attackPlayer(player, npc);
				break;

			// Kraken
			case 496:
				KrakenCombat.krakenAttack(player, npc);
				break;

			// Lizard Shaman
			case 6766:
				// LizardShamanCombat.attackPlayer(player, npc);
				break;

			// Kree'Arra
			case 3162:
				if (withInMeleeDistance(npcX, npcY, player.getX(), player.getY(), npc.npcType)) {
					random = Misc.random(2);
				} else {
					random = Misc.random(1);
				}
				if (random == 0) {
					npc.attackType = ServerConstants.MAGIC_ICON;
					npc.projectileId = 1196;
				} else if (random == 1) {
					npc.attackType = ServerConstants.RANGED_ICON;
					npc.projectileId = 1198;
				} else if (random == 2) {
					npc.attackType = ServerConstants.MELEE_ICON;
					npc.projectileId = 1198;
				}
				if (Misc.random(5) >= 3) {
					npc.forceChat(npc.Kree());
				}
				break;
			// Wingman Skree
			case 3163:
				npc.attackType = ServerConstants.MAGIC_ICON;
				break;

			// Commander Zilyana
			case 2205:
				if (withInMeleeDistance(npcX, npcY, player.getX(), player.getY(), npc.npcType)) {
					random = Misc.random(2);
				} else {
					random = Misc.random(1);
				}
				if (random <= 1) {
					npc.attackType = ServerConstants.MAGIC_ICON;
					npc.projectileId = -1;
				} else if (random == 2) {
					npc.attackType = ServerConstants.MELEE_ICON;
					npc.projectileId = -1;
				}
				if (Misc.random(5) >= 3) {
					npc.forceChat(npc.Zilyana());
				}
				break;
			case 2206:
				// Starlight
				npc.attackType = ServerConstants.MELEE_ICON;
				break;
			case 3164:
				// Flockleader Geerin
				npc.attackType = ServerConstants.RANGED_ICON;
				break;
			case 2207:
				// Growler
				npc.attackType = ServerConstants.MAGIC_ICON;
				npc.projectileId = 1203;
				break;
			case 2208:
				// Bree
				npc.attackType = ServerConstants.RANGED_ICON;
				npc.projectileId = 9;
				break;
			case 2216:
				// Sergeant Strongstack
				npc.attackType = ServerConstants.MELEE_ICON;
				break;
			case 2217:
				// Sergeant Steelwill
				npc.attackType = ServerConstants.MAGIC_ICON;
				npc.projectileId = 1217;
				break;
			case 2218:
				// Sergeant Grimspike
				npc.attackType = ServerConstants.RANGED_ICON;
				npc.projectileId = 1193;
				break;
			case 3129:
				// K'ril Tsutsaroth
				if (withInMeleeDistance(npcX, npcY, player.getX(), player.getY(), npc.npcType)) {
					random = Misc.random(2);
				} else {
					random = Misc.random(1);
				}
				if (random <= 1) {
					npc.attackType = ServerConstants.MAGIC_ICON;
					npc.projectileId = 1211;
				} else if (random == 2) {
					npc.attackType = ServerConstants.MELEE_ICON;
					npc.projectileId = -1;
				}
				if (Misc.random(5) >= 3) {
					npc.forceChat(npc.Tsutsaroth());
				}
				break;
			case 5947:
				// Spinolyp (mage)
				n.projectileId = 94;
				n.attackType = ServerConstants.MAGIC_ICON;
				n.endGfx = 95;
				break;
			case 5961:
				// Spinolyp (ranged)
				npc.projectileId = 298;
				npc.attackType = ServerConstants.RANGED_ICON;
				break;
			case 239:
				// King Black Dragon
				if (withInMeleeDistance(npcX, npcY, player.getX(), player.getY(), npc.npcType)) {
					random = Misc.random(4);
				} else {
					random = Misc.random(3);
				}
				if (random == 0) {
					npc.projectileId = 393; // red
					npc.endGfx = 430;
					npc.attackType = 3;
					npc.hitThroughPrayerAmount = 0.2;
				} else if (random == 1) {
					npc.projectileId = 394; // green
					npc.endGfx = 429;
					npc.attackType = 3;
					npc.hitThroughPrayerAmount = 0.2;
				} else if (random == 2) {
					npc.projectileId = 395; // white
					npc.endGfx = 431;
					npc.attackType = 3;
					npc.hitThroughPrayerAmount = 0.2;
				} else if (random == 3) {
					npc.projectileId = 396; // blue
					npc.endGfx = 428;
					npc.attackType = 3;
					npc.hitThroughPrayerAmount = 0.2;
				} else if (random == 4) {
					npc.projectileId = -1; // melee
					npc.attackType = ServerConstants.MELEE_ICON;
					npc.hitThroughPrayerAmount = 0.2;
				}
				break;
			case 1672:
				// Ahrim the Blighted
				npc.attackType = ServerConstants.MAGIC_ICON;
				int r = Misc.random(3);
				if (r == 0) {
					npc.gfx100(158);
					npc.projectileId = 159;
					npc.endGfx = 160;
				}
				if (r == 1) {
					npc.gfx100(161);
					npc.projectileId = 162;
					npc.endGfx = 163;
				}
				if (r == 2) {
					npc.gfx100(164);
					npc.projectileId = 165;
					npc.endGfx = 166;
				}
				if (r == 3) {
					npc.gfx100(155);
					npc.projectileId = 156;
				}
				break;

			case 2265:
				// Dagannoth Supreme
				npc.attackType = ServerConstants.RANGED_ICON;
				npc.projectileId = 298;
				break;
			case 2266:
				// Dagannoth Prime
				npc.attackType = ServerConstants.MAGIC_ICON;
				npc.projectileId = 162;
				npc.endGfx = 477;
				break;
			case 1675:
				// Karil the Tainted
				npc.attackType = ServerConstants.RANGED_ICON;
				npc.projectileId = 27;
				break;
			case 2054:
				// Chaos Elemental
				random = Misc.random(1);
				if (random == 0) {
					npc.attackType = ServerConstants.RANGED_ICON;
					npc.gfx100(550);
					npc.projectileId = 551;
					npc.endGfx = 552;
				} else {
					npc.attackType = ServerConstants.MAGIC_ICON;
					npc.gfx100(553);
					npc.projectileId = 554;
					npc.endGfx = 555;
				}
				break;
			case 2215:
				// General Graardor
				if (withInMeleeDistance(npcX, npcY, player.getX(), player.getY(), npc.npcType)) {
					random = Misc.random(2);
				} else {
					random = Misc.random(1);
				}
				if (random <= 1) {
					npc.attackType = ServerConstants.RANGED_ICON;
					npc.projectileId = 288;
				} else if (random == 2) {
					npc.attackType = ServerConstants.MELEE_ICON;
					npc.projectileId = -1;
				}
				if (Misc.random(5) >= 3) {
					npc.forceChat(npc.Graardor());
				}
				break;
			case 6506:
				// TzTok-Jad
				if (withInMeleeDistance(npcX, npcY, player.getX(), player.getY(), npc.npcType)) {
					random = Misc.random(2);
				} else {
					random = Misc.random(1);
				}
				if (random == 0) {
					npc.attackType = ServerConstants.MAGIC_ICON;
					npc.endGfx = 157;
					npc.projectileId = 448;
				} else if (random == 1) {
					npc.attackType = ServerConstants.RANGED_ICON;
					npc.endGfx = 451;
					npc.projectileId = -1;
				} else if (random == 2) {
					npc.attackType = ServerConstants.MELEE_ICON;
					npc.projectileId = -1;
				}
				break;

			case 5363 : // Mithril-Dragon
			case 247 : // Red dragon
			case 252 : // Black dragon
			case 265 : // Blue dragon
			case 260:// Green dragon
			case 261:// Green dragon
			case 262:// Green dragon
			case 263:// Green dragon
			case 264:// Green dragon
			case 7275: // Brutal black dragon
			case 7274: // Brutal red dragon
			case 7273: // Brutal blue dragon
			case 270 : // Bronze dragon
			case 272 : // Iron dragon
			case 274 : // Steel dragon
			case 6593: // Lava dragon.
				random = Misc.random(2);
				if (random <= 1) {
					npc.projectileId = -1;
					npc.attackType = ServerConstants.MELEE_ICON;
				} else {
					npc.projectileId = -1;
					npc.gfx100(1);
					npc.attackType = ServerConstants.DRAGONFIRE_ATTACK;
				}
				break;
		}
	}

	private static void callistoRoar(Player player, int startX, int startY) {
		// Where the player will end up
		int endY = ((player.getX() <= startX) ? 2 : -1);
		int endX = ((player.getY() <= startY) ? 1 : -2);

		// The attack
		player.doingActionEvent(3);
		player.startAnimation(3170);
		player.setForceMovement(3170, endX, endY, 1, 2, 5, 1);
		player.dealDamage(2);
		player.getPA().sendFilterableMessage("Callisto's roar sends you backwards.");
	}

	private void elderChaosDruidAttack(Player player, Npc npc) {
		player.lastDruidTele(player.getDruidDelay() - 1);
		if (player.getDruidDelay() <= 0) {
			player.lastDruidTele(Misc.random(30, 80));

			// Find the last npc attacked by the player that is a Elder druid
			// and is alive, teleport the player to this npc.
			// If not available, teleport the player to this Elder chaos druid
			// that attacked the player.
			Npc npcLastAttacked = NpcHandler.npcs[player.getNpcIdAttacking()];
			boolean npcLastAttackedUsed = false;
			int[] teleportCoords = new int[2];
			if (npcLastAttacked != null) {
				if (!npcLastAttacked.isDead() && npcLastAttacked.npcType == 6607) {
					teleportCoords = teleportPlayerNextToNpc(player.getX(), player.getY(), true, npcLastAttacked.getX(),
					                                         npcLastAttacked.getY(), npcLastAttacked.getHeight(), 1, 1);
					if (teleportCoords[0] > 0) {
						npcLastAttackedUsed = true;
						npcLastAttacked.forceChat("You dare run from us!");
					}
				}
			}
			if (!npcLastAttackedUsed) {
				teleportCoords = teleportPlayerNextToNpc(player.getX(), player.getY(), true, npc.getX(), npc.getY(),
				                                         npc.getHeight(), 1, 1);
				npc.forceChat("You dare run from us!");
			}

			player.getPA().movePlayer(teleportCoords[0], teleportCoords[1], npc.getHeight());
		}

	}

	/**
	 * Teleport the player next to this npc. Used for Elder Chaos druid for
	 * example.
	 * <p>
	 * Find a spot next to the npc that the npc can walk to, then return these
	 * coordinates.
	 * <p>
	 * if usePlayer is false, then it will find a spot next to the npc that the
	 * npc can walk to and return those coordinates.
	 * @param xSize TODO
	 * @param ySize TODO
	 */
	public static int[] teleportPlayerNextToNpc(int playerX, int playerY, boolean usePlayer, int npcX, int npcY,
			int npcHeight, int xSize, int ySize) {
		int[] teleportCoords = new int[2];
		int targetX = 0;
		int targetY = 0;
		teleportCoords[0] = 0;
		teleportCoords[1] = 0;

		targetX = npcX + 1;
		targetY = npcY;
		if (((playerX != targetX || playerY != targetY)) && usePlayer
				&& Region.isStraightPathUnblocked(npcX, npcY, targetX, targetY, npcHeight, xSize, ySize, false) || Region.isStraightPathUnblocked(npcX, npcY, targetX, targetY, npcHeight, xSize, ySize, false) && !usePlayer) {
			teleportCoords[0] = targetX;
			teleportCoords[1] = targetY;
			return teleportCoords;
		}

		targetX = npcX + 1;
		targetY = npcY + 1;
		if (((playerX != targetX || playerY != targetY)) && usePlayer
				&& Region.isStraightPathUnblocked(npcX, npcY, targetX, targetY, npcHeight, xSize, ySize, false) || Region.isStraightPathUnblocked(npcX, npcY, targetX, targetY, npcHeight, xSize, ySize, false) && !usePlayer) {
			teleportCoords[0] = targetX;
			teleportCoords[1] = targetY;
			return teleportCoords;
		}

		targetX = npcX;
		targetY = npcY + 1;
		if (((playerX != targetX || playerY != targetY)) && usePlayer
				&& Region.isStraightPathUnblocked(npcX, npcY, targetX, targetY, npcHeight, xSize, ySize, false) || Region.isStraightPathUnblocked(npcX, npcY, targetX, targetY, npcHeight, xSize, ySize, false) && !usePlayer) {
			teleportCoords[0] = targetX;
			teleportCoords[1] = targetY;
			return teleportCoords;
		}

		targetX = npcX - 1;
		targetY = npcY;
		if (((playerX != targetX || playerY != targetY)) && usePlayer
				&& Region.isStraightPathUnblocked(npcX, npcY, targetX, targetY, npcHeight, xSize, ySize, false) || Region.isStraightPathUnblocked(npcX, npcY, targetX, targetY, npcHeight, xSize, ySize, false) && !usePlayer) {
			teleportCoords[0] = targetX;
			teleportCoords[1] = targetY;
			return teleportCoords;
		}

		targetX = npcX - 1;
		targetY = npcY - 1;
		if (((playerX != targetX || playerY != targetY)) && usePlayer
				&& Region.isStraightPathUnblocked(npcX, npcY, targetX, targetY, npcHeight, xSize, ySize, false) || Region.isStraightPathUnblocked(npcX, npcY, targetX, targetY, npcHeight, xSize, ySize, false) && !usePlayer) {
			teleportCoords[0] = targetX;
			teleportCoords[1] = targetY;
			return teleportCoords;
		}

		targetX = npcX;
		targetY = npcY - 1;
		if (((playerX != targetX || playerY != targetY)) && usePlayer
				&& Region.isStraightPathUnblocked(npcX, npcY, targetX, targetY, npcHeight, xSize, ySize, false) || Region.isStraightPathUnblocked(npcX, npcY, targetX, targetY, npcHeight, xSize, ySize, false) && !usePlayer) {
			teleportCoords[0] = targetX;
			teleportCoords[1] = targetY;
			return teleportCoords;
		}

		targetX = npcX + 1;
		targetY = npcY - 1;
		if (((playerX != targetX || playerY != targetY)) && usePlayer
				&& Region.isStraightPathUnblocked(npcX, npcY, targetX, targetY, npcHeight, xSize, ySize, false) || Region.isStraightPathUnblocked(npcX, npcY, targetX, targetY, npcHeight, xSize, ySize, false) && !usePlayer) {
			teleportCoords[0] = targetX;
			teleportCoords[1] = targetY;
			return teleportCoords;
		}

		targetX = npcX - 1;
		targetY = npcY + 1;
		if (((playerX != targetX || playerY != targetY)) && usePlayer
				&& Region.isStraightPathUnblocked(npcX, npcY, targetX, targetY, npcHeight, xSize, ySize, false) || Region.isStraightPathUnblocked(npcX, npcY, targetX, targetY, npcHeight, xSize, ySize, false) && !usePlayer) {
			teleportCoords[0] = targetX;
			teleportCoords[1] = targetY;
			return teleportCoords;
		}
		return teleportCoords;
	}

	/**
	 * Speed of gfx special attack of the Npc
	 **/
	public int getProjectileSpeed(Npc npc) {
		EntityCombatStrategy combatStrategy = npc.getCombatStrategyOrNull();

		if (combatStrategy != null) {
			return combatStrategy.getProjectileSpeed(npc);
		}
		switch (npc.npcType) {
			case 6506:
				// TzTok-Jad
				return 140;

			case 53:
				// Red dragon
			case 54:
				// Black dragon
			case 55:
				// Blue dragon
			case 941:
				// Green dragon
			case 1589:
				// Baby red dragon
			case 1590:
				// Bronze dragon
			case 1591:
				// Iron dragon
			case 1592:
				// Steel dragon
			case 6593: // Lava dragon.
			case 7275: // Brutal black dragon
			case 7274: // Brutal red dragon
			case 7273: // Brutal blue dragon
				return 120;
			default:
				return 100;
		}
	}

	public static void multiAttackDamage(Npc npc) {
		for (int j = 0; j < PlayerHandler.players.length; j++) {
			if (PlayerHandler.players[j] != null) {
				Player player = PlayerHandler.players[j];
				if (player.getDead() || player.getHeight() != npc.getHeight()) {
					continue;
				}
				if (PlayerHandler.players[j].playerAssistant.withInDistance(player.getX(), player.getY(), npc.getX(),
				                                                            npc.getY(), 15)) {
					int damage = 0;
					if (npc.attackType == ServerConstants.MAGIC_ICON) {
						damage = Misc.random(NpcDefinition.getDefinitions()[npc.npcType].magicMaximumDamage);
						damage = Effects.victimWearingElysianSpiritShield(player, damage, true);
						if (Misc.random(NpcDefinition.getDefinitions()[npc.npcType].attack) < Misc
								.random(MagicFormula.getMagicDefenceAdvantage(player))) {
							damage = 0;
						}

						if (player.prayerActive[ServerConstants.PROTECT_FROM_MAGIC]) {
							damage = (int) (damage * npc.hitThroughPrayerAmount);
						}
					} else if (npc.attackType == ServerConstants.RANGED_ICON) {
						damage = Misc.random(NpcDefinition.getDefinitions()[npc.npcType].rangedMaximumDamage);
						damage = Effects.victimWearingElysianSpiritShield(player, damage, true);
						if (Misc.random(NpcDefinition.getDefinitions()[npc.npcType].attack) < Misc
								.random(RangedFormula.getInvisibleRangedDefenceAdvantage(player))) {
							damage = 0;
						}

						if (player.prayerActive[ServerConstants.PROTECT_FROM_RANGED]) {
							damage = (int) (damage * npc.hitThroughPrayerAmount);
						}
					} else if (npc.attackType == ServerConstants.MELEE_ICON) {
						damage = Misc.random(NpcDefinition.getDefinitions()[npc.npcType].maximumDamage);
						damage = Effects.victimWearingElysianSpiritShield(player, damage, true);
						if (Misc.random(NpcDefinition.getDefinitions()[npc.npcType].attack) < Misc
								.random(MeleeFormula.getInvisibleMeleeDefenceAdvantage(player))) {
							damage = 0;
						}

						if (player.prayerActive[ServerConstants.PROTECT_FROM_MELEE]) {
							damage = (int) (damage * npc.hitThroughPrayerAmount);
						}
					}
					if ((player.getWieldedWeapon() == 21015 || player.getWieldedWeapon() == 16259)
					    && player.getCombatStyle(ServerConstants.DEFENSIVE)
					    && System.currentTimeMillis() - player.timeSwitchedToBulwark >= 4700) {
						damage *= 0.8;
					}
					Combat.createHitsplatOnPlayerNormal(player, damage, ServerConstants.NORMAL_HITSPLAT_COLOUR,
					                                    npc.attackType);
					playerHasVengeanceVsNpc(player, npc, damage);
					ringOfRecoilNpc(player, npc, damage);
					if (npc.endGfx > 0) {
						player.gfx0(npc.endGfx);
					}
				}
			}
		}
	}

	public List<Player> findPlayerToAttack(Npc npc) {
		ArrayList<Player> players = new ArrayList<>();

		for (Player player : npc.getLocalPlayers()) {
			if (player == null) {
				continue;
			}
			if (player.getHeight() != npc.getHeight()) {
				continue;
			}
			if (Combat.wasAttackedByNpc(player) && player.getNpcIndexAttackingPlayer() != npc.npcIndex
			    && !Area.npcInMulti(npc, player.getX(), player.getY())) {
				continue;
			}
			if (!canAggressOnPlayer(player, npc)) {
				continue;
			}
			if (!player.playerAssistant.withInDistance(player.getX(), player.getY(), npc.getSpawnPositionX(),
			                                           npc.getSpawnPositionY(), NpcDefinition.getDefinitions()[npc.getTransformOrId()].attackDistance
			                                                                    + NpcDefinition.getRoamDistance(npc.npcType))) {
				continue;
			}
			players.add(player);
		}

		return players;
	}

	public void multiAttackGfx(Npc npc) {
		if (npc.projectileId < 0) {
			return;
		}
		for (int j = 0; j < PlayerHandler.players.length; j++) {
			if (PlayerHandler.players[j] != null) {
				Player player = PlayerHandler.players[j];
				if (player.getHeight() != npc.getHeight())
					continue;
				if (PlayerHandler.players[j].playerAssistant.withInDistance(player.getX(), player.getY(), npc.getX(),
				                                                            npc.getY(), 15)) {
					int nX = npc.getVisualX();
					int nY = npc.getVisualY();
					int pX = player.getX();
					int pY = player.getY();
					int offX = (nY - pY) * -1;
					int offY = (nX - pX) * -1;
					player.getPA().createPlayersProjectile(nX, nY, offX, offY, 50, getProjectileSpeed(npc),
					                                       npc.projectileId, 43, 31, -player.getPlayerId() - 1, 65, Combat.getProjectileSlope(player));
				}
			}
		}
	}

	/**
	 * Attempts to retrieve an open index in the {@link #npcs} array. An open
	 * index is one that has a null reference.
	 *
	 * @return the next open index, or -1 if none can be found.
	 */
	private static int findIndexOrNone() {
		for (int index = 1; index < NPC_INDEX_OPEN_MAXIMUM; index++) {
			if (npcs[index] == null) {
				return index;
			}
		}
		return -1;
	}

	/**
	 * Spawns a non-playable character for no specific player.
	 *
	 * @param id the id of the non-playable character that makes it unique.
	 * @param x the x-position on the map that this character is located.
	 * @param y the y-position on the map that this character is located.
	 * @param height the height or plane that the character is on.
	 * @return the new spawned non-playable character or {@link null} if one
	 * cannot be created.
	 */
	public static Npc spawnNpc(int id, int x, int y, int height) {
		return spawnNpc(null, id, x, y, height, false, false);
	}

	/**
	 * Spawns a new non-playable character to the given x, y, and height
	 * coordinates.
	 *
	 * @param npc the npc being spawned.
	 * @param x the x position to be spawned at.
	 * @param y the y position to be spawned at.
	 * @param height the z position to be spawned at.
	 */
	public static void spawnNpc(Npc npc, int x, int y, int height) {
		int index = findIndexOrNone();

		if (index == -1) {
			throw new IllegalStateException("No open index can be found.");
		}

		npcs[index] = npc;
		npc.npcIndex = index;
		npc.name = NpcDefinition.getDefinitions()[npc.npcType].name;
		npc.setX(x);
		npc.doNotRespawn = true;
		npc.setY(y);
		npc.setSpawnPositionX(x);
		npc.setSpawnPositionY(y);
		npc.setHeight(height);
		npc.faceAction = "ROAM";
		npc.setCurrentHitPoints(NpcDefinition.getDefinitions()[npc.npcType].hitPoints);
		npc.maximumHitPoints = npc.getCurrentHitPoints();
		npc.setSpawnedBy(0);
		npc.underAttack = true;
		npc.setKillerId(0);
	}

	/**
	 * Summon npc, barrows, etc
	 **/
	public static Npc spawnNpc(Player player, int npcType, int x, int y, int heightLevel, boolean attackPlayer,
	                           boolean headIcon) {
		if (NpcDefinition.getDefinitions()[npcType] == null) {
			Misc.print("Npc is null: " + npcType);
			return null;
		}
		int slot = -1;
		for (int i = 1; i < NPC_INDEX_OPEN_MAXIMUM; i++) {
			if (npcs[i] == null) {
				slot = i;
				break;
			}
		}
		if (slot == -1) {
			return null;
		}
		Npc newNpc = createCustomOrDefault(slot, npcType);

		npcs[slot] = newNpc;
		newNpc.name = NpcDefinition.getDefinitions()[npcType].name;
		newNpc.doNotRespawn = true;
		newNpc.setSpawnPositionX(x);
		newNpc.setSpawnPositionY(y);
		newNpc.setX(x);
		newNpc.setY(y);
		newNpc.setHeight(heightLevel);
		newNpc.faceAction = "ROAM";
		newNpc.setCurrentHitPoints(NpcDefinition.getDefinitions()[npcType].hitPoints);
		newNpc.maximumHitPoints = newNpc.getCurrentHitPoints();
		newNpc.setSpawnedBy(player == null ? 0 : player.getPlayerId());
		newNpc.onAdd();
		if (headIcon && player != null) {
			player.getPA().drawHeadicon(1, slot, 0, 0);
			newNpc.headIcon = true;
		}
		if (attackPlayer && player != null) {
			newNpc.underAttack = true;
			newNpc.setKillerId(player.getPlayerId());
		}
		// Jad
		if (newNpc.npcType == 6506) {
			newNpc.attackType = Misc.random(1, 2);
		}

		for (int index = 0; index < WarriorsGuild.ARMOUR_DATA.length; index++) {
			if (npcType == WarriorsGuild.ARMOUR_DATA[index][3]) {
				newNpc.forceChat("I'M ALIVE!!!!");
			}
		}

		for (int index = 0; index < Barrows.COFFIN_AND_BROTHERS.length; index++) {
			if (npcType == Barrows.COFFIN_AND_BROTHERS[index][1]) {
				newNpc.forceChat("You dare disturb my rest!");
			}
		}
		return newNpc;
	}

	public static void spawnNpcZombie(Player player, int npcType, int x, int y, int heightLevel) {
		int slot = -1;
		for (int i = 1; i < NPC_INDEX_OPEN_MAXIMUM; i++) {
			if (npcs[i] == null) {
				slot = i;
				break;
			}
		}
		if (slot == -1) {
			return;
		}
		Npc newNPC = createCustomOrDefault(slot, npcType);

		npcs[slot] = newNPC;
		newNPC.name = NpcDefinition.getDefinitions()[npcType].name;
		newNPC.setX(x);
		newNPC.zombieOwner = player.getPlayerName();
		newNPC.doNotRespawn = true;
		newNPC.setY(y);
		newNPC.setSpawnPositionX(x);
		newNPC.setSpawnPositionY(y);
		newNPC.setHeight(heightLevel);
		newNPC.faceAction = "ROAM";
		newNPC.setCurrentHitPoints(NpcDefinition.getDefinitions()[npcType].hitPoints);
		newNPC.maximumHitPoints = newNPC.getCurrentHitPoints();
		newNPC.setSpawnedBy(0);
		newNPC.underAttack = true;
		newNPC.setKillerId(player.getPlayerId());
		newNPC.onAdd();
	}

	/**
	 * Creates a custom non-playable character implementation if possible for
	 * the given type, or null if one cannot be created.
	 * <p>
	 *
	 * @param index the index in the list of the npc.
	 * @param type the unique type of the npc.
	 * @return the custom npc, or a default new npc.
	 */
	public static Npc createCustomOrDefault(int index, int type) {
		Npc custom = CustomNpcMap.getSingleton().createCopyOrNull(type, index);

		if (custom != null && custom.npcIndex != -1) {
			return custom;
		}
		return new Npc(index, type);
	}

	public static int getAttackEmote(Npc npc) {
		if (NpcDefinition.getDefinitions()[npc.npcType].name.toLowerCase().contains("dragon") && npc.attackType == ServerConstants.DRAGONFIRE_ATTACK) {
			return 81;
		}
		switch (npc.npcType) {
			case 465: // Skeletal wyvern
				if (npc.attackType == ServerConstants.WYVERN_BREATH) {
					return 2988;
				}
				if (npc.attackType == ServerConstants.RANGED_ICON) {
					return 2989;
				}
				break;
		}

		return -1;
	}

	public static Npc spawnDefaultNpc(int npcType, String name, int x, int y, int heightLevel, String faceAction,
	                                  int behindWallDistance, int forceNewIndex) {
		int slot = -1;
		for (int i = 1; i < NPC_INDEX_OPEN_MAXIMUM; i++) {
			if (npcs[i] == null) {
				slot = i;
				break;
			}
		}
		if (slot == -1) {
			return null;
		}
		if (forceNewIndex >= 0) {
			slot = forceNewIndex;
		}
		Npc newNpc = createCustomOrDefault(slot, npcType);
		npcs[slot] = newNpc;
		newNpc.name = name;
		newNpc.setX(x);
		newNpc.setY(y);
		newNpc.setSpawnPositionX(x);
		newNpc.setSpawnPositionY(y);
		newNpc.setHeight(heightLevel);
		newNpc.faceAction = faceAction;
		newNpc.setCurrentHitPoints(NpcDefinition.getDefinitions()[npcType].hitPoints);
		newNpc.maximumHitPoints = newNpc.getCurrentHitPoints();
		newNpc.behindWallDistance = behindWallDistance;
		newNpc.onAdd();
		return newNpc;
	}

	public static void spawnDefaultNpcSheep(int npcType, String name, int x, int y, int heightLevel, String faceAction,
	                                        int ticks, int newNpcId) {
		int slot = -1;
		for (int i = 1; i < NPC_INDEX_OPEN_MAXIMUM; i++) {
			if (npcs[i] == null) {
				slot = i;
				break;
			}
		}
		if (slot == -1) {
			return;
		}
		Npc newNpc = createCustomOrDefault(slot, npcType);
		npcs[slot] = newNpc;
		newNpc.name = name;
		newNpc.transformTimer = ticks;
		newNpc.transformIntoId = newNpcId;
		newNpc.setX(x);
		newNpc.setY(y);
		newNpc.setSpawnPositionX(x);
		newNpc.setSpawnPositionY(y);
		newNpc.setHeight(heightLevel);
		newNpc.faceAction = faceAction;
		newNpc.setCurrentHitPoints(6);
		newNpc.maximumHitPoints = newNpc.getCurrentHitPoints();
		newNpc.onAdd();
	}

	private static long openSlots() {
		return java.util.stream.Stream.of(npcs).filter(Objects::isNull).count();
	}

	public void npcGameTick() {
		GameTickLog.npcTickDuration = System.currentTimeMillis();

		if (ServerConfiguration.DEBUG_MODE) {
			boolean debugNpcArray = false;
			if (debugNpcArray) {
				long openSlots = openSlots();
				double percentageOpen = ((double) openSlots / (double) NPC_INDEX_OPEN_MAXIMUM) * 100D;
				if (percentageOpen >= 50) {
					Misc.print(String.format("NPC List Overload: percentage=%s, open slots=%s, max=%s", percentageOpen,
					                         openSlots, NPC_INDEX_OPEN_MAXIMUM));
				}
			}
		}
		for (int i = 0; i < NPC_INDEX_OPEN_MAXIMUM; i++) {
			Npc npc = npcs[i];

			if (npc == null) {
				continue;
			}
			npc.onSequence();

			npc.getEventHandler().cycle();
			npc.getIncomingPlayerDamage().process(true);
			/*
			 * Start of Pet. Leave this in process because this for loop can
			 * cause a problem when using events for 10 players for example.
			 * It's best to have this 'for loop' once every game tick than have
			 * 10 players doing it 10 times per game tick.
			 */
			Player petOwner = PlayerHandler.players[npc.getNpcPetOwnerId()];
			if (petOwner != null) {
				if (npc.summoned) {
					boolean firstPet = petOwner.getPetSummoned() && petOwner.getPetId() == npc.npcType;
					boolean secondPet = petOwner.getSecondPetSummoned() && petOwner.getSecondPetId() == npc.npcType;
					if (secondPet || firstPet) {
						if (petOwner.forceCallFamiliar) {
							Pet.deletePet(npc);
							Pet.summonNpcOnValidTile(petOwner, npc.npcType, secondPet);
							petOwner.forceCallFamiliar = false;
						} else {
							if (petOwner.playerAssistant.withInDistance(npc.getX(), npc.getY(), petOwner.getX(),
							                                            petOwner.getY(), 10) && petOwner.getHeight() == npc.getHeight()) {
								followPlayer(i, petOwner.getPlayerId());
							} else {
								Pet.deletePet(npc);
								Pet.summonNpcOnValidTile(petOwner, npc.npcType, secondPet);
							}
						}
					}
				}
			} else if (npc.summoned) {
				Pet.deletePet(npc);
			}

			/* End of Pet. */

			if (npc.transformIntoId > 0) {
				if (npc.transformTimer > 0) {
					npc.transformTimer--;
				} else if (npc.transformTimer == 0) {
					int oldX = npc.getX();
					int oldY = npc.getY();
					Pet.deletePet(npc);
					NpcHandler.spawnDefaultNpc(2800, "Sheep", oldX, oldY, 0, "ROAM", 0, -1);
				}
			}

			if (npc.npcType == 4397 && teleporterInAction) {
				startAnimation(npc, 722);
				npc.forceChat("Senventior Disthine Molenko!");
				teleporterInAction = false;
			} else if (npc.npcType == 4420) {
				if (Misc.hasOneOutOf(10)) {
					npc.forceChat("Talk to me to join the tournament!");
				}
			}

			if (npc.respawnTimer > 0) {
				npc.respawnTimer--;
			}
			if (npc.hitDelayTimer > 0) {
				npc.hitDelayTimer--;
				if (npc.isDead()) {
					npc.setKillerId(0);
					npc.hitDelayTimer = 0;
				}

				Player player = PlayerHandler.players[npc.oldIndex];
				if (player == null) {
					npc.setKillerId(0);
					npc.hitDelayTimer = 0;
				} else {

					if (player.isTeleporting()) {
						npc.setKillerId(0);
						npc.hitDelayTimer = 0;
					}
					if (player.getDoingAgility()) {
						npc.setKillerId(0);
						npc.hitDelayTimer = 0;
					}
				}

			}
			if (npc.hitDelayTimer == 1) {
				npc.hitDelayTimer = 0;
				applyDamageOnPlayerFromNPC(npc.oldIndex, npc, -1, false, -1, -1);
			}
			if (npc.attackTimer > 0) {
				npc.attackTimer--;

				EntityCombatStrategy strategy = npc.getCombatStrategyOrNull();

				if (strategy != null) {
					strategy.onAttackCooldown(npc, npc.attackTimer);
				}
			}

			if (npc.getSpawnedBy() > 0 && npc.getNpcPetOwnerId() <= 0 || npc.deleteNpc) {
				if (PlayerHandler.players[npc.getSpawnedBy()] == null
				    || PlayerHandler.players[npc.getSpawnedBy()].getHeight() != npc.getHeight()
				       && npc.getNpcPetOwnerId() <= 0
				    || PlayerHandler.players[npc.getSpawnedBy()].getDead()
				    || !PlayerHandler.players[npc.getSpawnedBy()].playerAssistant.withInDistance(npc.getX(),
				                                                                                 npc.getY(), PlayerHandler.players[npc.getSpawnedBy()].getX(),
				                                                                                 PlayerHandler.players[npc.getSpawnedBy()].getY(), 20)) {
					npc.setX(0);
					npc.setY(0);
					if (PlayerHandler.players[npc.getSpawnedBy()] != null) {
						for (int index = 0; index < WarriorsGuild.ARMOUR_DATA.length; index++) {
							if (WarriorsGuild.ARMOUR_DATA[index][3] == npc.npcType) {
								PlayerHandler.players[npc.getSpawnedBy()].summonedAnimator = false;
							}
						}
					}
					npc.onRemove();
					npcs[i] = null;
					continue;
				}
			}

			/**
			 * Attacking player
			 **/
			if (!npc.getLocalPlayers().isEmpty()) {
				EntityCombatStrategy combatStrategy = npc.getCombatStrategyOrNull();

				if (combatStrategy == null || combatStrategy.canFindTarget(npc)) {
					if (npc.getSpawnedBy() <= 0 && NpcDefinition.getAggressive(npc.getTransformOrId()) && !npc.isDead()
							&& (npc.getKillerId() <= 0 || switchTargets(npc))) {
						List<Player> possibleTargets = findPlayerToAttack(npc);

						if (!possibleTargets.isEmpty()) {
							Player randomTarget = Misc.random(possibleTargets);

							if (randomTarget != null) {
								npc.setKillerId(randomTarget.getPlayerId());
							}
						}
					}
				}
			}
			if (System.currentTimeMillis() - npc.lastDamageTaken > 5000) {
				npc.underAttackBy = 0;
			}
			if ((npc.getKillerId() > 0 || npc.underAttack || npc.getFollowingType() != null) && !npc.isWalkingHome() && npc.getNpcPetOwnerId() == 0) {
				if (!npc.isDead()) {
					int p = npc.getKillerId();

					EntityType followingType = npc.getFollowingType();

					if (followingType == EntityType.NPC && npc.getFollowIndex() != -1
					    && npcs[npc.getFollowIndex()] != null) {
						followNpc(npc.npcIndex, npc.getFollowIndex());

						// the follow index can change after followNpc has been
						// referenced.
						if (npc.getFollowIndex() != -1) {
							stepAway(npc, npcs[npc.getFollowIndex()]);

							npc.faceNpc(npc.getFollowIndex());
						}
						// should the npc step away?
					} else if (PlayerHandler.players[p] != null) {
						Player player = PlayerHandler.players[p];

						followPlayer(i, player.getPlayerId());

						stepAway(npc, player);
						if (npc.attackTimer == 0) {
							attackPlayer(player, i);
						}
					} else {
						npc.setKillerId(0);
						npc.underAttack = false;
						npc.facePlayer(0);
					}
				}
			}

			if (npc.npcType == 6080 && Misc.random(20) == 1) {
				int random = Misc.random(5);
				if (random == 0) {
					npc.forceChat("You are too slow, keep moving!");
				} else if (random == 1) {
					npc.forceChat("My grand mother can move faster than this!");
				} else if (random == 2) {
					npc.forceChat("I said no breaks, don't stop!");
				} else if (random == 3) {
					npc.forceChat("Faster, faster, faster!");
				} else if (random == 4) {
					int value = Misc.random(500);
					npc.forceChat((value + 80) + " more laps to go!");
				} else if (random == 5) {
					npc.forceChat("Come on! Move your legs!");
				}
			}

			if (npc.npcType == 11201 && Misc.random(20) == 1) {
				int random = Misc.random(5);
				if (random == 0) {
					npc.forceChat("WOO! I'm gonna be rich!");
				} else if (random == 1) {
					npc.forceChat("I... just... can't... stop... dancing...");
				} else if (random == 2) {
					npc.forceChat("Right, left, right, right, left, right, left, left...");
				} else if (random == 3) {
					npc.forceChat("Speak to me if you need any gambling help!");
				} else if (random == 4) {
					npc.forceChat("My arm's getting tired...");
				}
			}
			if (npc.getKillerId() == 0) {
				npc.randomWalk = true;
			}
			/**
			 * Random walking and walking home
			 **/
			if ((!npc.underAttack || npc.isWalkingHome()) && npc.randomWalk && !npc.isDead()
					&& System.currentTimeMillis() - npc.timeAttackedAPlayer > 10000 && !npc.summoned && !npc.neverRandomWalks()) {
				npc.facePlayer(0);
				npc.setKillerId(0);
				if (npc.getSpawnedBy() == 0 && !npc.isWalkingHomeDisabled()) {
					if ((npc.getX() > npc.getSpawnPositionX() + NpcDefinition.getRoamDistance(npc.npcType))
					    || (npc.getX() < npc.getSpawnPositionX() - NpcDefinition.getRoamDistance(npc.npcType))
					    || (npc.getY() > npc.getSpawnPositionY() + NpcDefinition.getRoamDistance(npc.npcType))
					    || (npc.getY() < npc.getSpawnPositionY() - NpcDefinition.getRoamDistance(npc.npcType))) {
						npc.setWalkingHome(true);
					}
				}
				if (npc.isWalkingHome() && npc.getX() == npc.getSpawnPositionX()
				    && npc.getY() == npc.getSpawnPositionY()) {
					npc.setWalkingHome(false);
				} else if (npc.isWalkingHome() && npc.getKillerId() == 0 && !npc.isMoved()
				           && npc.getMovementState() == MovementState.WALKABLE) {
					npc.setMoveX(compareMovement(npc.getX(), npc.getSpawnPositionX()));
					npc.setMoveY(compareMovement(npc.getY(), npc.getSpawnPositionY()));
					npc.getNextNPCMovement(npc.npcIndex);
					npc.updateRequired = true;
					npc.setMoved(true);
				}
				if (!npc.faceAction.isEmpty()) {
					switch (npc.faceAction) {

						case "ROAM":
							boolean roam = true;
							if (npc.getKillerId() > 0) {
								roam = false;
							}
							if (npc.isMoved()) {
								roam = false;
							}
							if (npc.neverRandomWalks()) {
								roam = false;
							}
							if (npc.getMovementState() == MovementState.DISABLED) {
								roam = false;
							}
							if (roam) {
								if (System.currentTimeMillis() - npc.timeTurnedByPlayer < 10000) {
									break;
								}
								if (rarelyWalkNpcs(npc.npcType) && !npc.isWalkingHome()) {
									int possibleX = 0;
									int possibleY = 0;
									ArrayList<Byte> useableX = new ArrayList<Byte>();
									ArrayList<Byte> useableY = new ArrayList<Byte>();
									for (int index = 0; index < 8; index++) {
										possibleX = npc.getX() + Misc.directionDeltaX[index];
										possibleY = npc.getY() + Misc.directionDeltaY[index];
										if (Misc.distanceToPoint(npc.getSpawnPositionX(), npc.getSpawnPositionY(),
										                         possibleX, possibleY) <= NpcDefinition.getRoamDistance(npc.npcType)) {
											if (Region.isStraightPathUnblocked(npc.getX(), npc.getY(), possibleX, possibleY,
											                                   npc.getHeight(), NpcDefinition.getDefinitions()[npc.npcType].size,
											                                   NpcDefinition.getDefinitions()[npc.npcType].size, false)) {
												useableX.add(Misc.directionDeltaX[index]);
												useableY.add(Misc.directionDeltaY[index]);
											}
										}
									}
									int randomPath = Misc.random(useableX.size() - 1);
									if (useableX.size() > 0) {
										npc.setMoveX(useableX.get(randomPath));
										npc.setMoveY(useableY.get(randomPath));
										npc.setMoved(true);
										npc.getNextNPCMovement(npc.npcIndex);
										npc.updateRequired = true;
									}
								}
							}
							break;

						case "WEST":
							npc.turnNpc(npc.getX() - 1, npc.getY());
							break;

						case "EAST":
							npc.turnNpc(npc.getX() + 1, npc.getY());
							break;

						case "SOUTH":
							npc.turnNpc(npc.getX(), npc.getY() - 1);
							break;
						case "NORTH":
							npc.turnNpc(npc.getX(), npc.getY() + 1);
							break;
					}
				}
			}

			if (npc.isDead()) {
				if (npc.respawnTimer == 0 && !npc.applyDead && !npc.needRespawn) {
					npc.facePlayer(0);
					if (NpcDefinition.getDefinitions()[npc.npcType] == null) {
						Misc.print("Npc is null: " + npc.npcType);
					}
					npc.respawnTimer = NpcDefinition.getDefinitions()[npc.npcType].deathDeleteTicks;
					if (npc.npcType == 7884) {
						DonatorBossCombatStrategy.handleDeathTransformation(npc);
					}
					npc.setKilledBy(getNpcKillerId(i, false));
					Player player = PlayerHandler.players[npc.getKilledBy()];
					Plugin.execute("kill_npc_" + npcs[i].npcType, player);
					int deathAnimation = 0;
					if (NpcDefinition.getDefinitions()[npc.getTransformOrId()] != null) {
						deathAnimation = NpcDefinition.getDefinitions()[npc.getTransformOrId()].deathAnimation;
					} else {
						deathAnimation = NpcDefinition.getDefinitions()[npc.npcType].deathAnimation;
					}
					startAnimation(npc, deathAnimation);
					Slayer.slayerTaskNPCKilled(player, npcs[i],
					                           NpcDefinition.getDefinitions()[npcs[i].npcType].hitPoints);
					FightCaves.fightCavesReward(player, npc.npcType);
					Barrows.killedBarrows(player, npc);
					npc.setFrozenLength(0);
					npc.applyDead = true;
					npc.onDeath();
					resetPlayersInCombat(i);
					VetionCombat.vetionRelatedNpcsDeath(npc.npcType);
					if (npc.headIcon) {
						npc.headIcon = false;
						player.getPA().drawHeadicon(0, npc.npcIndex, 0, 0);
					}
				} else if (npc.respawnTimer == 0 && npc.applyDead && !npc.needRespawn) {
					npc.needRespawn = true;
					int respawnTimer = NpcDefinition.getDefinitions()[npc.npcType].respawnTicks;
					if (NpcDefinition.getDefinitions()[npc.npcType].name.toLowerCase().contains("revenant")) {
						respawnTimer = 30;
					}
					npc.respawnTimer = respawnTimer;
					dropLoot(npc);
					npc.afterDeath();
					if (!npc.instancedNpcForceRespawn) {
						npc.setX(0);
						npc.setY(0);
					}
					npc.animNumber = 0x328;
					npc.updateRequired = true;
					npc.animUpdateRequired = true;
				} else if (npc.respawnTimer == 0 && npc.needRespawn) {
					if (!npc.instancedNpcForceRespawn && (npc.getSpawnedBy() > 0 || npc.doNotRespawn)) {
						npc.onRemove();
						npcs[i] = null;
					} else {
						KrakenCombat.krakenRespawn(npc);
						int old1 = npc.npcType;
						String old2 = npc.name;
						int old3 = npc.getSpawnPositionX();
						int old4 = npc.getSpawnPositionY();
						int old5 = npc.getHeight();
						String old6 = npc.faceAction;
						npc.onRemove();
						npcs[i] = null;
						spawnDefaultNpc(old1, old2, old3, old4, old5, old6, 0, -1);
					}
				}
			}
			npc.setMoved(false);
		}
		GameTickLog.npcTickDuration = System.currentTimeMillis() - GameTickLog.npcTickDuration;
	}

	public void fastForEach(Consumer<Npc> consumer) {
		int consecutiveNulls = 0;

		int consecutiveLimitInclusive = 3;

		for (int index = 0; index < npcs.length; index++) {
			Npc npc = npcs[index];

			if (npc == null) {
				consecutiveNulls++;

				if (consecutiveNulls >= consecutiveLimitInclusive) {
					break;
				}
				continue;
			}
			consumer.accept(npc);
			consecutiveNulls = 0;
		}
	}

	/**
	 * @return True, if the npc can switch targets when player is in a
	 * multi-zone.
	 */
	private boolean switchTargets(Npc npc) {
		if (NpcDefinition.getDefinitions()[npc.npcType].clever
		    && System.currentTimeMillis() - npc.timeFoundNewTarget > 10000) {
			npc.timeFoundNewTarget = System.currentTimeMillis();
			return true;
		}
		return false;
	}

	/**
	 * Npc killer id?
	 **/
	public int getNpcKillerId(int npcId, Boolean secondHighest) {
		Npc npc = NpcHandler.npcs[npcId];
		if (npc == null) {
			return 0;
		}
		int highestDamageDealerIndex = 0;
		int highestDamage = 0;
		for (int index = 0; index < npc.playerTotalDamage.length; index++) {
			if (npc.playerTotalDamage[index] <= 0) {
				continue;
			}
			if (npc.playerTotalDamage[index] > highestDamage) {
				// Skip the #1 damage dealer if looking for second highest.
				if (secondHighest && npc.getKilledBy() == index) {
					continue;
				}
				highestDamage = npc.playerTotalDamage[index];
				highestDamageDealerIndex = index;
			}
		}

		// Check if name matches incase the player logs off and another player
		// logs in and takes index.
		Player highestDamageDealerPlayer = PlayerHandler.players[highestDamageDealerIndex];
		if (highestDamageDealerPlayer != null) {
			if (highestDamageDealerPlayer.getPlayerName().equals(npc.playerNameTotalDamage[highestDamageDealerIndex])) {
				return highestDamageDealerIndex;
			} else {
				return 0;
			}
		}

		for (int p = 1; p < ServerConstants.MAXIMUM_PLAYERS; p++) {
			Player loop = PlayerHandler.players[p];
			if (loop != null) {
				if (loop.getLastNpcAttackedIndex() == npcId) {
					loop.setTimeNpcAttackedPlayer(0);

					// Only reset if the last npc i attacked is the one i just
					// killed.
					if (loop.getNpcIdAttacking() == npcId) {
						loop.resetNpcIdentityAttacking();
					}
				}
			}
		}
		return 0;
	}

	public int getSecondHighestDamageDealer(Npc npc) {
		return getNpcKillerId(npc.npcIndex, true);
	}

	public static boolean isBattleMageNpc(int npcType) {
		switch (npcType) {
			case 912:
			case 913:
			case 914:
				return true;
		}
		return false;
	}

	public void dropLoot(Npc npc) {
		Player player = PlayerHandler.players[npc.getKilledBy()];
		if (player == null) {
			return;
		}
		player.resetFaceUpdate();
		if (Zombie.zombieKilled(player, npc)) {
			return;
		}
		if (!npc.isItemsDroppable()) {
			return;
		}
		GodWarsDungeonInterface.addGwdKillCount(player, npc);
		NpcKillTracker.addNpcKill(player, npc.npcType);
		NpcDrops.giveDropTableDrop(player, false, npc.npcType, npc.getDropPosition());
		NpcDrops.otherDrops(npc, player);
		WarriorsGuild.dropAnimatedTokens(player, npc);
		WarriorsGuild.dropDefender(player, npc);
		RecipeForDisaster.isRfdNpc(player, npc);
		RecipeForDisaster.spawnNextWave(player, false, npc.npcType);
		KrakenCombat.krakenDeath(player, npc);
	}

	/**
	 * Resets players in combat
	 */
	public void resetPlayersInCombat(int npcIndex) {
		for (int j = 0; j < PlayerHandler.players.length; j++) {
			if (PlayerHandler.players[j] != null) {
				if (PlayerHandler.players[j].getNpcIndexAttackingPlayer() == npcIndex) {
					PlayerHandler.players[j].setTimeNpcAttackedPlayer(0);
				}
			}
		}
	}

	/**
	 * Npc Follow Player
	 **/
	public static int compareMovement(int Place1, int Place2) {
		if ((Place1 - Place2) == 0) {
			return 0;
		} else if ((Place1 - Place2) < 0) {
			return 1;
		} else if ((Place1 - Place2) > 0) {
			return -1;
		}
		return 0;
	}

	/**
	 * NPC will not move if added here.
	 */
	public boolean doNotfollowPlayer(Npc npc) // Npc will never move if added
	// here
	{
		switch (npc.npcType) {
			case 5947: // Spinolyp
			case 5961: // Spinolyp
			case 2266: // Dagannoth Prime
			case 2265: // Dagannoth Supreme
			case 11006: // Ice Strykewyrm
			case 496: // Master whirlpool
			case 1337: // Max hit dummy
				return true;
		}
		return false;
	}

	/**
	 * NPC will follow the player.
	 *
	 * @param npcIndex The NPC following the player
	 * @param playerId The player the NPC will follow.
	 */
	public void followPlayer(int npcIndex, int playerId) {
		Player player = PlayerHandler.players[playerId];
		Npc npc = npcs[npcIndex];

		if (npc == null) {
			return;
		}
		if (npc.isMoved()) {
			return;
		}
		if (player == null) {
			return;
		}
		if (player.getDead()) {
			npc.facePlayer(0);
			npc.randomWalk = true;
			npc.underAttack = false;
			return;
		}
		if (npc.isFrozen()) {
			return;
		}
		if (npc.getMovementState() == MovementState.DISABLED) {
			return;
		}
		if (npc.isRequiringReplacement()) {
			return;
		}
		if (!npc.isVisible()) {
			return;
		}
		if (doNotfollowPlayer(npc)) {
			// Max hit dummy
			if (npc.npcType == 1337) {
				return;
			}
			if (npc.npcType == 496 && npc.transformId == -1) {

			} else {
				npc.facePlayer(playerId);
			}
			return;
		}
		int playerX = player.getX();
		int playerY = player.getY();
		int npcX = npc.getVisualX();
		int npcY = npc.getVisualY();
		if (!npc.summoned) {
			if (goodDistance(npcX, npcY, player.getX(), player.getY(),
			                 NpcDefinition.getDefinitions()[npc.getTransformOrId()].attackDistance
			                 + NpcDefinition.getDefinitions()[npc.npcType].size - 1)) {
				return;
			}
		}
		npc.setMoved(true);
		npc.randomWalk = false;
		npc.setWalkingHome(false);
		if (npcX == playerX && npc.getY() == playerY) {
			int o = Misc.random(3);
			switch (o) {
				case 0:
					npc.setMoveX(compareMovement(npc.getX(), playerX));
					npc.setMoveY(compareMovement(npc.getY(), playerY + 1));
					break;
				case 1:
					npc.setMoveX(compareMovement(npc.getX(), playerX));
					npc.setMoveY(compareMovement(npc.getY(), playerY - 1));
					break;
				case 2:
					npc.setMoveX(compareMovement(npc.getX(), playerX + 1));
					npc.setMoveY(compareMovement(npc.getY(), playerY));
					break;
				case 3:
					npc.setMoveX(compareMovement(npc.getX(), playerX - 1));
					npc.setMoveY(compareMovement(npc.getY(), playerY));
					break;
			}
			npc.getNextNPCMovement(npc.npcIndex);
			npc.facePlayer(playerId);
			npc.updateRequired = true;
		}
		if (goodDistance(npcX, npcY, playerX, playerY,
		                 npc.getSpawnedBy() > 0 ? 1 : NpcDefinition.getDefinitions()[npc.npcType].size)) {
			return;
		}
		if (npc.getSpawnedBy() > 0
		    || ((npc.getX() < npc.getSpawnPositionX() + NpcDefinition.getRoamDistance(npc.npcType))
		        && (npc.getX() > npc.getSpawnPositionX() - NpcDefinition.getRoamDistance(npc.npcType))
		        && (npc.getY() < npc.getSpawnPositionY() + NpcDefinition.getRoamDistance(npc.npcType))
		        && (npc.getY() > npc.getSpawnPositionY() - NpcDefinition.getRoamDistance(npc.npcType)))) {

			if (npc.getHeight() == player.getHeight()) {
				if (player != null && npc != null) {
					int xMovement = compareMovement(npc.getX(), playerX);

					int yMovement = compareMovement(npc.getY(), playerY);

					if (playerY < npc.getY() || playerY > npc.getY() || playerX < npc.getX() || playerX > npc.getX()) {
						npc.setMoveX(xMovement);
						npc.setMoveY(yMovement);
					} else if (playerX == npc.getX() || playerY == npc.getY()) {
						int o = Misc.random(3);
						switch (o) {
							case 0:
								npc.setMoveX(compareMovement(npc.getX(), playerX));
								npc.setMoveY(compareMovement(npc.getY(), playerY + 1));
								break;
							case 1:
								npc.setMoveX(compareMovement(npc.getX(), playerX));
								npc.setMoveY(compareMovement(npc.getY(), playerY - 1));
								break;
							case 2:
								npc.setMoveX(compareMovement(npc.getX(), playerX + 1));
								npc.setMoveY(compareMovement(npc.getY(), playerY));
								break;
							case 3:
								npc.setMoveX(compareMovement(npc.getX(), playerX - 1));
								npc.setMoveY(compareMovement(npc.getY(), playerY));
								break;
						}
					}
					npc.getNextNPCMovement(npc.npcIndex);
					npc.facePlayer(playerId);
					npc.updateRequired = true;
					npc.setFollowingType(EntityType.PLAYER);
					npc.setFollowIndex(playerId);
				}
			}
		} else {
			npc.setKillerId(0);
			npc.facePlayer(0);
			npc.randomWalk = true;
			npc.underAttack = false;
			npc.setFollowingType(null);
			npc.setFollowIndex(-1);
		}
	}

	/**
	 * Attempts to have one npc follow another.
	 *
	 * @param npcIndex the index of the npc we're trying to have follow the second.
	 * @param secondNpcIndex the index of the npc that is going to be followed.
	 */
	public void followNpc(int npcIndex, int secondNpcIndex) {
		if (npcIndex < 0 || secondNpcIndex < 0 || npcIndex == secondNpcIndex || npcIndex >= npcs.length
		    || secondNpcIndex >= npcs.length) {
			return;
		}
		Npc followed = npcs[secondNpcIndex];

		Npc npc = npcs[npcIndex];

		if (npc == null) {
			return;
		}
		if (npc.isMoved()) {
			npc.resetFollowing();
			return;
		}
		if (followed == null) {
			npc.resetFollowing();
			return;
		}
		if (followed.isDead()) {
			npc.resetFollowing();
			npc.resetFace();
			npc.randomWalk = true;
			npc.underAttack = false;
			return;
		}
		if (npc.isFrozen()) {
			npc.resetFollowing();
			return;
		}
		if (npc.getMovementState() == MovementState.DISABLED) {
			npc.resetFollowing();
			return;
		}
		if (npc.isRequiringReplacement()) {
			npc.resetFollowing();
			return;
		}
		if (doNotfollowPlayer(npc)) {
			// Max hit dummy
			if (npc.npcType == 1337) {
				return;
			}
			if (npc.npcType == 496 && npc.transformId == -1) {

			} else {
				npc.faceNpc(npc.npcIndex);
			}
			return;
		}
		NpcDefinition definition = NpcDefinition.getDefinitions()[npc.getTransformOrId()];

		if (definition != null) {
			if (npc.distanceTo(followed.getX(), followed.getY()) >= Math
					                                                        .min(definition.roamDistance + definition.attackDistance, 14)) {
				npc.resetFollowing();
				return;
			}
		}
		int playerX = followed.getX();
		int playerY = followed.getY();
		int npcX = npc.getVisualX();
		int npcY = npc.getVisualY();
		if (!npc.summoned) {
			if (goodDistance(npcX, npcY, followed.getX(), followed.getY(),
			                 NpcDefinition.getDefinitions()[npc.getTransformOrId()].attackDistance
			                 + NpcDefinition.getDefinitions()[npc.npcType].size - 1)) {
				npc.resetFollowing();
				return;
			}
		}
		npc.setMoved(true);
		npc.randomWalk = false;
		npc.setWalkingHome(false);
		if (goodDistance(npcX, npcY, playerX, playerY,
		                 npc.getSpawnedBy() > 0 ? 1 : NpcDefinition.getDefinitions()[npc.npcType].size)) {
			npc.resetFollowing();
			return;
		}
		if (npc.getHeight() == followed.getHeight()) {
			if (playerY < npc.getY()) {
				npc.setMoveX(compareMovement(npc.getX(), playerX));
				npc.setMoveY(compareMovement(npc.getY(), playerY));
			} else if (playerY > npc.getY()) {
				npc.setMoveX(compareMovement(npc.getX(), playerX));
				npc.setMoveY(compareMovement(npc.getY(), playerY));
			} else if (playerX < npc.getX()) {
				npc.setMoveX(compareMovement(npc.getX(), playerX));
				npc.setMoveY(compareMovement(npc.getY(), playerY));
			} else if (playerX > npc.getX()) {
				npc.setMoveX(compareMovement(npc.getX(), playerX));
				npc.setMoveY(compareMovement(npc.getY(), playerY));
			}
			npc.getNextNPCMovement(npc.npcIndex);
			npc.faceNpc(npc.npcIndex);
			npc.updateRequired = true;
			npc.setFollowingType(EntityType.NPC);
			npc.setFollowIndex(secondNpcIndex);
		} else {
			npc.setKillerId(0);
			npc.resetFace();
			npc.randomWalk = true;
			npc.underAttack = false;
			npc.resetFollowing();
		}
	}

	/**
	 * NPC Attacking Player
	 **/
	public void attackPlayer(final Player player, int i) {
		Npc npc = npcs[i];
		if (npc == null) {
			return;
		}

		if (npc.isDead()) {
			return;
		}
		if (!npc.isVisible()) {
			return;
		}
		// Cave kraken/Master whirlpool
		if (npc.npcType == 496 && npc.transformId == -1) {
			return;
		}
		// Max hit dummy
		if (npc.npcType == 1337) {
			return;
		}
		if (player.isTeleporting()) {
			npc.setKillerId(0);
			return;
		}
		if (player.getDoingAgility()) {
			npc.setKillerId(0);
			return;
		}
		if (!Area.npcInMulti(npc, npc.getX(), npc.getY()) && npc.underAttackBy > 0
		    && npc.underAttackBy != player.getPlayerId()) {
			npc.setKillerId(0);
			return;
		}
		if (!Area.npcInMulti(npc, npc.getX(), npc.getY()) && player.getNpcIndexAttackingPlayer() > 0
		    && player.getNpcIndexAttackingPlayer() != npc.npcIndex && Combat.wasAttackedByNpc(player)) {
			npc.setKillerId(0);
			return;
		}
		if (!Area.npcInMulti(npc, npc.getX(), npc.getY()) && Combat.wasUnderAttackByAnotherPlayer(player, 5000)) {
			npc.setKillerId(0);
			return;
		}
		if (npc.getHeight() != player.getHeight()) {
			npc.setKillerId(0);
			return;
		}
		if (!player.playerAssistant.withInDistance(player.getX(), player.getY(), npc.getSpawnPositionX(),
		                                           npc.getSpawnPositionY(), NpcDefinition.getDefinitions()[npc.getTransformOrId()].attackDistance
		                                                                    + NpcDefinition.getRoamDistance(npc.getTransformOrId()))) {
			if (player.killingNpcIndex == npc.npcIndex) {
				if (!npc.isWalkingHomeDisabled()) {
					npc.setWalkingHome(true);
				}
			}
			npc.setKillerId(0);
			return;
		}
		int playerX = player.getX();
		int playerY = player.getY();
		int npcX = npc.getVisualX();
		int npcY = npc.getVisualY();

		if (npcX == playerX && npcY == playerY && !npc.isAttackableWhileSamePosition()) {
			stepAway(npc, player);
			return;
		}
		if (!goodDistance(npcX, npcY, playerX, playerY, NpcDefinition.getDefinitions()[npc.npcType].attackDistance
		                                                + (NpcDefinition.getDefinitions()[npc.npcType].size - 1))) {
			return;
		}

		if (player.getDead()) {
			return;
		}
		EntityCombatStrategy strategy = npc.getCombatStrategyOrNull();

		if (strategy != null) {
			if (!strategy.canAttack(npc, player)) {
				if (strategy.resetsTargetOnCannotAttack()) {
					npc.setKillerId(0);
				}
				return;
			}
		}
		int nextXtile = player.getX();
		int nextYtile = player.getY();

		npc.facePlayer(player.getPlayerId());
		npc.attackType = ServerConstants.MELEE_ICON;

		// Keep load spell before the projectile clipping check. Read below
		loadSpell(npc);

		if (strategy != null) {
			int attackType = strategy.calculateAttackType(npc, player);

			if (attackType != -1) {
				npc.attackType = attackType;
			}

			// this is necessary if we dont want to trigger certain affects
			// like multi attacks, and npc projectiles

			if (strategy.isCustomAttack()) {
				strategy.onCustomAttack(npc, player);
				afterCombatStrategyAttack(npc, player);
				strategy.afterCustomAttack(npc, player);
				return;
			}
		}

		if (npc.useCleverBossMechanics) {
			npcAttack(npc, player, npc.cleverBossHitsplatDelay, npc.cleverBossHitsplatType,
			          npc.cleverBossAttackAnimation, true);
			return;
		}

		// This clipping part has to be after loadSpell(). because
		// npc.attackType changes within loadSpell.
		// Spinolyps at Dagannoth kings can shoot through anthing.
		if (npc.npcType != 5947) {
			if (npc.attackType == ServerConstants.MELEE_ICON) {
				if (!Region.isStraightPathUnblocked(nextXtile, nextYtile, npc.getVisualX(), npc.getVisualY(),
				                                    npc.getHeight(), 1, 1, false) && !npc.canAttackWithPathBlocked()) {
					return;
				}
			} else {
				if (!Follow.isSpecialNpcPathing(npc.npcType)) {
					// Has to be 1 size or sometimes when attacking bosses, you
					// can safespot them beside rocks etc.
					if (!Region.isStraightPathUnblockedProjectiles(nextXtile, nextYtile, npc.getVisualX(),
					                                               npc.getVisualY(), npc.getHeight(), 1, 1, true)) {
						return;
					}
				}
			}
		}
		npcAttack(npc, player, getHitDelay(npc), -1, -1, false);
		if (multiAttacks(npc)) {
			multiAttackGfx(npc);
			return;
		}
		if (npc.projectileId > 0) {
			int nX = npc.getVisualX();
			int nY = npc.getVisualY();
			int pX = player.getX();
			int pY = player.getY();
			int offX = (nY - pY) * -1;
			int offY = (nX - pX) * -1;
			player.getPA().createPlayersProjectile(nX, nY, offX, offY, 50, getProjectileSpeed(npc), npc.projectileId,
			                                       getGfxStartHeight(npc), 25, -player.getPlayerId() - 1, 70, 16);
		}
	}

	public void setHitDelay(Npc npc, int delay) {
		npc.hitDelayTimer = Math.max(1, delay);
	}

	private void afterCombatStrategyAttack(Npc npc, Player player) {
		npc.attackTimer = NpcDefinition.getDefinitions()[npc.getTransformOrId()].attackSpeed;
		npc.timeAttackedAPlayer = System.currentTimeMillis();

		performAttackAnimation(npc);

		NpcAggression.startNewAggression(player, npc);

		if (Combat.hasSerpentineHelm(player) && Misc.hasOneOutOf(6)) {
			if (Venom.ENABLE_VENOM) {
				CombatNpc.applyVenomOnNpc(player, npc);
			} else {
				CombatNpc.applyPoisonOnNpc(player, npc, 10);
			}
		}
		player.setNpcIndexAttackingPlayer(npc.npcIndex);
		npc.oldIndex = player.getPlayerId();
		player.setTimeNpcAttackedPlayer(System.currentTimeMillis());
		player.timeNpcAttackedPlayerLogOutTimer = System.currentTimeMillis();
		player.doNotClosePmInterface = true;
		player.getPA().closeInterfaces(true);
		if (npc.getCombatStrategyOrNull() != null) {
			npc.getCombatStrategyOrNull().onAttack(npc, player);
		}
	}

	public void npcAttack(Npc npc, Player player, int hitsplatDelay, int hitsplatType, int attackAnimation,
	                      boolean cleverBoss) {
		npc.attackTimer = NpcDefinition.getDefinitions()[npc.npcType].attackSpeed;
		npc.timeAttackedAPlayer = System.currentTimeMillis();
		NpcAggression.startNewAggression(player, npc);
		if (!npc.cleverBossStopAttack) {
			npc.hitDelayTimer = hitsplatDelay;
		}
		usingSpecial = false;
		if (cleverBoss) {
			npc.attackType = hitsplatType;
			startAnimation(npc, attackAnimation);
		} else {
			if (npc.attackType == 3) {
				npc.hitDelayTimer += 2;
			}
			performAttackAnimation(npc);
		}
		player.setNpcIndexAttackingPlayer(npc.npcIndex);
		npc.oldIndex = player.getPlayerId();
		player.setTimeNpcAttackedPlayer(System.currentTimeMillis());
		player.timeNpcAttackedPlayerLogOutTimer = System.currentTimeMillis();
		player.doNotClosePmInterface = true;
		player.getPA().closeInterfaces(true);

		if (Combat.hasSerpentineHelm(player) && Misc.hasOneOutOf(6)) {

			if (Venom.ENABLE_VENOM) {
				CombatNpc.applyVenomOnNpc(player, npc);
			} else {
				CombatNpc.applyPoisonOnNpc(player, npc, 10);
			}
		}
		EntityCombatStrategy strategy = npc.getCombatStrategyOrNull();

		if (strategy != null) {
			strategy.onAttack(npc, player);
		}
	}

	private int getGfxStartHeight(Npc npc) {
		EntityCombatStrategy strategy = npc.getCombatStrategyOrNull();

		if (strategy != null) {
			return strategy.getGfxStartHeight(npc);
		}
		switch (npc.npcType) {
			case 443:
			case 172:
				return 30;
		}
		return 43;
	}

	private boolean withInMeleeDistance(int npcX, int npcY, int x, int y, int npcType) {
		if (goodDistance(npcX, npcY, x, y, NpcDefinition.getDefinitions()[npcType].size)) {
			return true;
		}
		return false;
	}

	public void performAttackAnimation(Npc npc) {
		EntityCombatStrategy strategy = npc.getCombatStrategyOrNull();

		if (strategy != null) {
			if (!strategy.performsAttackAnimation()) {
				return;
			}
			int customAttackAnimation = strategy.getCustomAttackAnimation(npc);

			if (customAttackAnimation != -1) {
				startAnimation(npc, customAttackAnimation);
				return;
			}
		}

		switch (npc.attackType) {
			case ServerConstants.MELEE_ICON:
				startAnimation(npc, NpcDefinition.getDefinitions()[npc.getTransformOrId()].attackAnimation);
				break;
			case ServerConstants.RANGED_ICON:
				int animation = NpcDefinition.getDefinitions()[npc.getTransformOrId()].rangedAttackAnimation;
				if (animation == 0) {
					animation = NpcDefinition.getDefinitions()[npc.getTransformOrId()].attackAnimation;
				}
				startAnimation(npc, animation);
				break;
			case ServerConstants.MAGIC_ICON:
				int animation1 = NpcDefinition.getDefinitions()[npc.getTransformOrId()].magicAttackAnimation;
				if (animation1 == 0) {
					animation1 = NpcDefinition.getDefinitions()[npc.getTransformOrId()].attackAnimation;
				}
				if (npc.startingGfx > 0) {
					npc.gfx100(npc.startingGfx);
				}
				startAnimation(npc, animation1);
				break;
			case ServerConstants.DRAGONFIRE_ATTACK:
				startAnimation(npc, getAttackEmote(npc));
				break;
		}
	}

	public boolean usingSpecial;

	/**
	 * Apply the damage on the player.
	 */
	public static int applyDamageOnPlayerFromNPC(int playerIndex, Npc npc, int attackType, boolean damageQueue,
	                                             int fixedDamage, int maximumDamage) {
		if (npc == null) {
			return 0;
		}
		Player player = PlayerHandler.players[playerIndex];
		if (player == null) {
			npc.setKillerId(0);
			return 0;
		}
		if (player.getHeight() != npc.getHeight()) {
			return 0;
		}
		if (player.isTeleporting()) {
			npc.setKillerId(0);
			return 0;
		}
		if (player.getDoingAgility()) {
			npc.setKillerId(0);
			return 0;
		}
		if (npc.isDead()) {
			return 0;
		}

		if (!damageQueue) {
			attackType = npc.attackType;

			fixedDamage = npc.cleverBossFixedDamage;
		}
		if (player.getPlayerIdAttacking() <= 0 && player.getNpcIdAttacking() <= 0) {
			if (player.getAutoRetaliate() == 1 && !player.isMoving() && !player.doingAnAction()) {
				player.setNpcIdentityAttacking(npc.npcIndex);
				player.setNpcIdToFollow(npc.npcIndex);
			}
		}
		if (player.getAttackTimer() <= 3 || player.getAttackTimer() == 0 && player.getNpcIdAttacking() == 0
		                                    && player.getOldNpcIndex() == 0 && !player.getDoingAgility()) {
			player.startAnimation(Combat.getBlockAnimation(player));
			if (!player.soundSent) {
				SoundSystem.sendSound(player, 511, 450);
			}
		}
		if (multiAttacks(npc) && !npc.useCleverBossMechanics && !damageQueue) {
			multiAttackDamage(npc);
			return 0;
		}
		if (!player.getDead()) {
			int damage = 0;

			int maximumDamageFinal = NpcDefinition.getDefinitions()[npc.npcType].maximumDamage;

			if (damageQueue && maximumDamage >= 1) {
				maximumDamageFinal = maximumDamage;
			}
			EntityCombatStrategy strategy = npc.getCombatStrategyOrNull();

			int strategyDamage = strategy != null ? strategy.calculateCustomDamage(npc, player, attackType) : -1;
			DegradingManager.degrade(player, false);
			if (attackType == ServerConstants.MELEE_ICON) {
				damage = strategyDamage != -1 ? strategyDamage
						         : calculateNpcMeleeDamage(npc, player, fixedDamage, maximumDamageFinal);
			} else if (attackType == ServerConstants.RANGED_ICON) {
				maximumDamageFinal = NpcDefinition.getDefinitions()[npc.npcType].rangedMaximumDamage;
				if (damageQueue && maximumDamage >= 1) {
					maximumDamageFinal = maximumDamage;
				}
				damage = strategyDamage != -1 ? strategyDamage
						         : calculateNpcRangedData(npc, player, fixedDamage, maximumDamageFinal);

				if (npc.endGfx > 0) {
					if (npc.bottomGfx) {
						player.gfx0(npc.endGfx);
					} else {
						player.gfx100(npc.endGfx);
					}
				}
			} else if (attackType == ServerConstants.MAGIC_ICON) {
				maximumDamageFinal = NpcDefinition.getDefinitions()[npc.npcType].magicMaximumDamage;
				if (damageQueue && maximumDamage >= 1) {
					maximumDamageFinal = maximumDamage;
				}
				damage = strategyDamage != -1 ? strategyDamage
						         : calculateNpcMagicDamage(npc, player, fixedDamage, maximumDamageFinal);
				boolean magicFailed = false;
				if (damage == 0) {
					magicFailed = true;
				}
				if (npc.endGfx > 0 && (!magicFailed || isFightCaveNpc(npc))) {
					if (npc.bottomGfx) {
						player.gfx0(npc.endGfx);
					} else {
						player.gfx100(npc.endGfx);
					}
				} else if (magicFailed) {
					player.gfx100(85);
				}
			} else if (attackType == ServerConstants.DRAGONFIRE_ATTACK) {
				if (strategyDamage == -1) {
					int anti = Combat.antiFire(player, false, true);

					switch (anti) {
						case 0:
							damage = ThreadLocalRandom.current().nextInt(2, 61);
							player.playerAssistant.sendMessage("You are badly burnt by the dragon fire!");
							break;
						case 1:
							damage = ThreadLocalRandom.current().nextInt(0, 9);
							break;
						case 2:
							damage = 0;
							break;
					}

					// King black dragon.
					if (anti >= 1 && npc.npcType == 50) {
						damage = Misc.random(10);
					}
				} else {
					damage = strategyDamage;
				}

				if (npc.endGfx > 0) {
					if (npc.bottomGfx) {
						player.gfx0(npc.endGfx);
					} else {
						player.gfx100(npc.endGfx);
					}
				}

			} else if (attackType == ServerConstants.WYVERN_BREATH) {
				int protection = Combat.wyvernProtection(player, true);

				switch (protection) {
					case 0:
						damage = Misc.random(37) + 2;
						player.playerAssistant.sendMessage("The wyvern's ice breath chills you to the bone!");
						player.playerAssistant.sendMessage("You should equip an elemental, mind or dragonfire shield.");
						player.forcedChat("Ow!", false, false);
						break;
					case 1:
						damage = Misc.random(10);
						player.getPA().sendFilterableMessage("Your shield absorbs most of the wyvern's icy breath.");
						break;
				}
				if (npc.endGfx > 0) {
					if (npc.bottomGfx) {
						player.gfx0(npc.endGfx);
					} else {
						player.gfx100(npc.endGfx);
					}
				}
			}
			GameTimeSpent.increaseGameTime(player, GameTimeSpent.PVM);
			damage = Effects.victimWearingElysianSpiritShield(player, damage, true);
			if (player.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) - damage < 0) {
				damage = player.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS);
			}

			if (strategy != null) {
				strategy.onDamageDealt(npc, player, damage, attackType);
			}
			
			handleSpecialEffects(player, npc, damage);
			
//			PrayerManager.getBook(player).getCombatEffects(player, npc);
//			damage = PrayerManager.getBook(player).getDamageEffects(player, damage, attackType);
			
			if (attackType != ServerConstants.DRAGONFIRE_ATTACK) {
				Combat.createHitsplatOnPlayerNormal(player, damage, ServerConstants.NORMAL_HITSPLAT_COLOUR, attackType);
			} else {
				Combat.createHitsplatOnPlayerNormal(player, damage, ServerConstants.DARK_RED_HITSPLAT_COLOUR,
				                                    ServerConstants.NO_ICON);
			}
			if (player.getDead()) {
				player.deathsToNpc++;
				npc.setKillerId(0);
			}
			return damage;
		}
		return 0;
	}

	public static int calculateNpcMagicDamage(Npc npc, Player player, int fixedDamage, int maximumDamage) {
		if (npc.forceNormalNpcFixedDamage >= 0) {
			return npc.forceNormalNpcFixedDamage;
		}
		if (fixedDamage >= 0) {
			return fixedDamage;
		}
		int damage = Misc.random(maximumDamage);

		// Bulwark
		if ((player.getWieldedWeapon() == 21015 || player.getWieldedWeapon() == 16259)
		    && player.getCombatStyle(ServerConstants.DEFENSIVE)
		    && System.currentTimeMillis() - player.timeSwitchedToBulwark >= 4700) {
			damage *= 0.8;
		}
		if (10 + Misc.random(MagicFormula.getMagicDefenceAdvantage(player)) > Misc
				                                                                      .random(NpcDefinition.getDefinitions()[npc.npcType].attack)) {
			damage = 0;
		}
		if (player.prayerActive[ServerConstants.PROTECT_FROM_MAGIC]) {
			EntityCombatStrategy strategy = npc.getCombatStrategyOrNull();

			if (strategy != null && strategy.hitsThroughPrayer(npc, player, damage, ServerConstants.MAGIC_ICON)) {
				return damage;
			}
			damage = (int) (damage * npc.hitThroughPrayerAmount);
		}
		return damage;
	}

	public static int calculateNpcRangedData(Npc npc, Player player, int fixedDamage, int maximumDamage) {
		if (npc.forceNormalNpcFixedDamage >= 0) {
			return npc.forceNormalNpcFixedDamage;
		}
		if (fixedDamage >= 0) {
			return fixedDamage;
		}
		int damage = Misc.random(maximumDamage);

		// Bulwark
		if ((player.getWieldedWeapon() == 21015 || player.getWieldedWeapon() == 16259)
		    && player.getCombatStyle(ServerConstants.DEFENSIVE)
		    && System.currentTimeMillis() - player.timeSwitchedToBulwark >= 4700) {
			damage *= 0.8;
		}
		if (10 + Misc.random(RangedFormula.getInvisibleRangedDefenceAdvantage(player)) > Misc
				                                                                                 .random(NpcDefinition.getDefinitions()[npc.npcType].attack)) {
			damage = 0;
		}
		if (player.prayerActive[ServerConstants.PROTECT_FROM_RANGED]) {
			EntityCombatStrategy strategy = npc.getCombatStrategyOrNull();

			if (strategy != null && strategy.hitsThroughPrayer(npc, player, damage, ServerConstants.RANGED_ICON)) {
				return damage;
			}
			damage = (int) (damage * npc.hitThroughPrayerAmount);
		}
		return damage;
	}

	public static int calculateNpcMeleeDamage(Npc npc, Player player, int fixedDamage, int maximumDamage) {
		if (npc.forceNormalNpcFixedDamage >= 0) {
			return npc.forceNormalNpcFixedDamage;
		}
		if (fixedDamage >= 0) {
			return fixedDamage;
		}
		int damage = Misc.random(maximumDamage);

		// Bulwark
		if ((player.getWieldedWeapon() == 21015 || player.getWieldedWeapon() == 16259)
		    && player.getCombatStyle(ServerConstants.DEFENSIVE)
		    && System.currentTimeMillis() - player.timeSwitchedToBulwark >= 4700) {
			damage *= 0.8;
		}
		// Dharok formula.
		if (npc.npcType == 1673) {
			damage = 30;
			damage += (npc.maximumHitPoints - npc.getCurrentHitPoints()) / 2;
			damage = Misc.random(damage);
		}
		if (10 + Misc.random(MeleeFormula.getInvisibleMeleeDefenceAdvantage(player)) > Misc.random(NpcDefinition.getDefinitions()[npc.npcType].attack)) {
			damage = 0;
		}
		if (player.prayerActive[ServerConstants.PROTECT_FROM_MELEE]) {
			EntityCombatStrategy strategy = npc.getCombatStrategyOrNull();

			if (strategy != null && strategy.hitsThroughPrayer(npc, player, damage, ServerConstants.MELEE_ICON)) {
				return damage;
			}
			damage = (int) (damage * npc.hitThroughPrayerAmount);
		}
		return damage;
	}

	private static void playerHasVengeanceVsNpc(Player player, Npc npc, int damage) {
		if (player.getVengeance()) {
			if (damage <= 0) {
				return;
			}

			player.specialAttackWeaponUsed[31] = 1;
			player.setWeaponAmountUsed(31);
			player.forcedChat("Taste vengeance!", false, false);
			player.forcedChatUpdateRequired = true;
			player.setUpdateRequired(true);
			player.setVengeance(false);
			int vengDamage = (int) (damage * 0.75);
			if (vengDamage > npc.getCurrentHitPoints()) {
				vengDamage = npc.getCurrentHitPoints();
			}

			SpecialAttackTracker.saveMaximumDamage(player, vengDamage, "VENGEANCE", true);
			CombatNpc.applyHitSplatOnNpc(player, npc, vengDamage, ServerConstants.NORMAL_HITSPLAT_COLOUR,
			                             ServerConstants.NO_ICON, 1);
		}
	}

	public static void handleSpecialEffects(Player player, Npc npc, int damage) {
		playerHasVengeanceVsNpc(player, npc, damage);
		ringOfRecoilNpc(player, npc, damage);

		// Spinolyps
		if (npc.npcType == 5947 || npc.npcType == 5961) {
			if (damage > 0) {
				if (player != null) {
					if (player.getCurrentCombatSkillLevel(ServerConstants.PRAYER) > 0) {
						player.currentCombatSkillLevel[ServerConstants.PRAYER]--;
						Skilling.updateSkillTabFrontTextMain(player, ServerConstants.PRAYER);
						Poison.appendPoison(null, player, false, 2);
					}
				}
			}
		}
	}

	private static void ringOfRecoilNpc(Player player, Npc npc, int damage) {
		if (damage > 0 && player.getRecoilCharges() > 0
		    && (ItemAssistant.hasItemEquipped(player, 2550) || ItemAssistant.hasItemEquipped(player, 20657)
				&& player.getAttributes().getOrDefault(Player.RING_OF_SUFFERING_ENABLED))) {
			int recoilDamage = damage / 10;
			if (recoilDamage < 1) {
				recoilDamage = 1;
			}
			player.setRecoilCharges(player.getRecoilCharges() - recoilDamage);
			if (player.getRecoilCharges() < 0) {
				recoilDamage += player.getRecoilCharges();
			}
			if (recoilDamage > 0) {
				if (recoilDamage > npc.getCurrentHitPoints()) {
					recoilDamage = npc.getCurrentHitPoints();
				}
				CombatNpc.applyHitSplatOnNpc(player, npc, recoilDamage, ServerConstants.NORMAL_HITSPLAT_COLOUR,
				                             ServerConstants.NO_ICON, 1);
			}
			if (player.getRecoilCharges() <= 0) {
				player.setRecoilCharges(40);
				if (!ItemAssistant.hasItemEquipped(player, 20657)) {
					ItemAssistant.deleteEquipment(player, 2550, ServerConstants.RING_SLOT);
					player.playerAssistant.sendMessage("Your ring of recoil turns to dust.");
				}
			}
		}

	}

	public static void startAnimation(Npc npc, int animId) {
		if (npc == null) {
			return;
		}
		npc.animNumber = animId;
		npc.animUpdateRequired = true;
		npc.updateRequired = true;
	}

	public boolean goodDistance(int objectX, int objectY, int playerX, int playerY, int distance) {
		for (int i = 0; i <= distance; i++) {
			for (int j = 0; j <= distance; j++) {
				if ((objectX + i) == playerX
				    && ((objectY + j) == playerY || (objectY - j) == playerY || objectY == playerY)) {
					return true;
				} else if ((objectX - i) == playerX
				           && ((objectY + j) == playerY || (objectY - j) == playerY || objectY == playerY)) {
					return true;
				} else if (objectX == playerX
				           && ((objectY + j) == playerY || (objectY - j) == playerY || objectY == playerY)) {
					return true;
				}
			}
		}
		return false;
	}

	public NpcHandler() {
	}

	public static void loadNpcData() {
		for (int i = 0; i < NPC_INDEX_OPEN_MAXIMUM; i++) {
			npcs[i] = null;
		}
		try {
			loadDefinitions();
			new NpcSpawnNonCombatJSON();
			new NpcSpawnBossJSON();
			new NpcSpawnCombatJSON();
			NpcDrops.loadRareDrops();
			startNpcEvents();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void startNpcEvents() {
		if (GameType.isOsrs()) {
			NpcDoubleItemsInterface.npcEvent();
		}
	}

	public static void loadDefinitions() {
		new NpcDefinitionNonCombatJSON();
		new NpcDefinitionCombatJSON();
		new NpcDefinitionCleverJSON();

		if (GameType.isOsrsPvp() && WebsiteLogInDetails.SPAWN_VERSION) {
			NpcDefinition.getDefinitions()[3024].setHitpoints(99);
		}

	}

	private void stepAway(Npc npc, Player player) {
		if (npc.isMoved()) {
			return;
		}
		if (npc.getMovementState() == MovementState.DISABLED) {
			return;
		}
		int npcX = npc.getVisualX();
		int npcY = npc.getVisualY();
		if (npcX == player.getX() && npcY == player.getY()) {
			if (Region.pathUnblocked(player.getX(), player.getY(), player.getHeight(), "WEST")) {
				npc.setMoveX(-1);
			} else if (Region.pathUnblocked(player.getX(), player.getY(), player.getHeight(), "EAST")) {
				npc.setMoveX(1);
			} else if (Region.pathUnblocked(player.getX(), player.getY(), player.getHeight(), "SOUTH")) {
				npc.setMoveY(-1);
			} else if (Region.pathUnblocked(player.getX(), player.getY(), player.getHeight(), "NORTH")) {
				npc.setMoveY(1);
			}
			npc.setMoved(true);
			npc.getNextNPCMovement(npc.npcIndex);
			npc.updateRequired = true;
		}
	}

	public static void stepAway(Npc npc, Npc from) {
		if (npc.isMoved()) {
			return;
		}
		if (npc.getMovementState() == MovementState.DISABLED) {
			return;
		}
		int npcX = npc.getVisualX();
		int npcY = npc.getVisualY();
		if (npcX == from.getX() && npcY == from.getY()) {
			if (Region.pathUnblocked(from.getX(), from.getY(), from.getHeight(), "WEST")) {
				npc.setMoveX(-1);
			} else if (Region.pathUnblocked(from.getX(), from.getY(), from.getHeight(), "EAST")) {
				npc.setMoveX(1);
			} else if (Region.pathUnblocked(from.getX(), from.getY(), from.getHeight(), "SOUTH")) {
				npc.setMoveY(-1);
			} else if (Region.pathUnblocked(from.getX(), from.getY(), from.getHeight(), "NORTH")) {
				npc.setMoveY(1);
			}
			npc.setMoved(true);
			npc.getNextNPCMovement(npc.npcIndex);
			npc.updateRequired = true;
			npc.faceNpc(from.npcIndex);
		}
	}

	public static void facePlayer(Player player, Npc npc) {
		npc.facePlayer(player.getPlayerId());
		npc.timeTurnedByPlayer = System.currentTimeMillis();
		npc.facePlayerOnce = true;
	}

	private boolean canAggressOnPlayer(Player player, Npc npc) {
		if (NpcDefinition.getDefinitions()[npc.npcType].clever) {
			return true;
		}
		switch (npc.npcType) {
			// Wilderness Bosses
			case 6503: // Callisto
				// Wingman Skree
			case 3163:
			case 3165: // Flight kilisa
			case 2206:
				// Starlight
			case 3164:
				// Flockleader Geerin
			case 2207:
				// Growler
			case 2208:
				// Bree
			case 2216:
				// Sergeant Strongstack
			case 2217:
				// Sergeant Steelwill
			case 2218:
				// Sergeant Grimspike
				return true;
		}
		if (NpcAggression.npcCannotAggress(player, npc)) {
			return false;
		}
		return true;
	}

	public static void blockAnimation(Npc npc) {
		if (npc == null) {
			return;
		}

		// Cave kraken/Master whirlpool
		if (npc.npcType == 496 && npc.transformId == -1) {
			return;
		}

		EntityCombatStrategy combatStrategy = npc.getCombatStrategyOrNull();

		if (combatStrategy != null) {
			if (!combatStrategy.performsBlockAnimation()) {
				return;
			}
		}

		// TzTok-Jad
		if (npc.npcType != 6506 &&
		    // Corporeal beast
		    npc.npcType != 8133) {
			if (NpcDefinition.getDefinitions()[npc.npcType] == null) {
				return;
			}
			startAnimation(npc, NpcDefinition.getDefinitions()[npc.npcType].blockAnimation);
		}
	}

	/**
	 * Has to be called before Cycle event, because cycle event is basically an
	 * action that can have variable effects etc.. Also has to be before packet
	 * cycle.
	 */
	public void clearNpcFlags() {
		for (int i = 0; i < NPC_INDEX_OPEN_MAXIMUM; i++) {
			Npc npc = npcs[i];
			if (npc == null) {
				continue;
			}
			npcs[i].clearUpdateFlags();
		}
	}
}
