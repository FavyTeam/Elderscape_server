package game.content.combat.vsnpc;

import core.GameType;
import core.ServerConstants;
import game.content.achievement.Achievements;
import game.content.combat.Combat;
import game.content.combat.CombatConstants;
import game.content.combat.CombatInterface;
import game.content.combat.Venom;
import game.content.combat.special.SpecialAttackBase;
import game.content.combat.vsplayer.AttackPlayer;
import game.content.combat.vsplayer.magic.AutoCast;
import game.content.combat.vsplayer.magic.MagicData;
import game.content.combat.vsplayer.magic.MagicFormula;
import game.content.combat.vsplayer.melee.MeleeAttack;
import game.content.combat.vsplayer.melee.MeleeData;
import game.content.combat.vsplayer.melee.MeleeFormula;
import game.content.combat.vsplayer.range.RangedAmmoUsed;
import game.content.combat.vsplayer.range.RangedData;
import game.content.combat.vsplayer.range.RangedFormula;
import game.content.degrading.DegradingManager;
import game.content.item.chargeable.Chargeable;
import game.content.item.chargeable.ChargeableCollection;
import game.content.minigame.TargetSystem;
import game.content.minigame.WarriorsGuild;
import game.content.minigame.barrows.Barrows;
import game.content.minigame.zombie.Zombie;
import game.content.miscellaneous.GameTimeSpent;
import game.content.miscellaneous.OverlayTimer;
import game.content.miscellaneous.RandomEvent;
import game.content.miscellaneous.SpecialAttackTracker;
import game.content.skilling.Skilling;
import game.content.skilling.Slayer;
import game.entity.Entity;
import game.entity.EntityType;
import game.entity.combat_strategy.EntityAttackType;
import game.entity.combat_strategy.EntityCombatStrategy;
import game.item.GameItem;
import game.item.ItemAssistant;
import game.item.ItemDefinition;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.npc.data.NpcDefinition;
import game.npc.impl.KrakenCombat;
import game.npc.impl.VetionCombat;
import game.object.clip.Region;
import game.player.Area;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import game.player.movement.Follow;
import game.player.movement.Movement;
import utility.Misc;

/**
 * Player vs Npc combat.
 *
 * @author MGT Madness, Edited heavily on 02-11-2015 to make the Xp drops appear before hitsplat.
 */
public class CombatNpc {

	public static void applyPoisonOnNpc(Player player, Npc npc, int poisonDamage) {
		if (npc.venomEvent) {
			return;
		}
		if (poisonDamage > npc.poisonDamage) {
			npc.poisonDamage = poisonDamage;
			npc.poisonHitsplatsLeft = 4;
		}
		if (npc.poisonEvent) {
			return;
		}
		npc.poisonDamage = poisonDamage;
		npc.poisonHitsplatsLeft = 3;
		npc.poisonTicksUntillDamage = 100;
		npc.getEventHandler().addEvent(npc, new CycleEvent<Entity>() {
			@Override
			public void execute(CycleEventContainer container) {
				container.stop();
			}

			@Override
			public void stop() {
				if (player == null) {
					CombatNpc.applyHitSplatOnNpc(null, npc, poisonDamage, ServerConstants.POISON_HITSPLAT_COLOUR, ServerConstants.NO_ICON, 1);
				} else {
					npc.underAttack = true;
					CombatNpc.applyHitSplatOnNpc(player, npc, poisonDamage, ServerConstants.POISON_HITSPLAT_COLOUR, ServerConstants.NO_ICON, 1);
				}
			}
		}, 1);
		npc.poisonEvent = true;

		CycleEventContainer<Entity> poisonEvent = npc.getPoisonCycleEvent();

		if (poisonEvent != null && poisonEvent.isRunning()) {
			poisonEvent.stop();
		}

		npc.setPoisonCycleEvent(npc.getEventHandler().addEvent(npc, new CycleEvent<Entity>() {
			@Override
			public void execute(CycleEventContainer container) {
				if (npc.isDead()) {
					container.stop();
					return;
				}
				if (npc.poisonDamage == 0) {
					container.stop();
					return;
				}
				int damage = npc.poisonDamage;

				npc.poisonTicksUntillDamage--;
				if (npc.poisonTicksUntillDamage == 0) {
					if (player == null) {
						CombatNpc.applyHitSplatOnNpc(null, npc, damage, ServerConstants.POISON_HITSPLAT_COLOUR, ServerConstants.NO_ICON, 1);
					} else {
						CombatNpc.applyHitSplatOnNpc(player, npc, damage, ServerConstants.POISON_HITSPLAT_COLOUR, ServerConstants.NO_ICON, 1);
					}
					npc.poisonHitsplatsLeft--;
					npc.poisonTicksUntillDamage = 100;

					if (npc.poisonHitsplatsLeft == 0) {
						if (npc.poisonDamage == 1) {
							container.stop();
							return;
						} else {

							npc.poisonDamage--;
							npc.poisonHitsplatsLeft = 4;
						}
					}
				}



			}

			@Override
			public void stop() {
				npc.poisonEvent = false;
				npc.poisonDamage = 0;
			}
		}, 1));

	}

	public static void applyVenomOnNpc(Player player, Npc npc) {
		for (int npcs : ServerConstants.NON_VENOMABLE_NPCS) {
			if (npc.npcType == npcs) {
				return;
			}
		}
		if (npc.venomEvent) {
			return;
		}
		npc.venomHitsplatsLeft = 50000;
		npc.venomTicksUntillDamage = Venom.TICKS_UNTIL_VENOM;
		npc.poisonDamage = 0; // Reset poison.
		CycleEventHandler.getSingleton().addEvent(npc, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				container.stop();
			}

			@Override
			public void stop() {
				if (player == null) {
					if (npc.venomDamage == 6) {
						CombatNpc.applyHitSplatOnNpc(null, npc, npc.venomDamage, ServerConstants.VENOM_HITSPLAT_COLOUR, ServerConstants.NO_ICON, 1);
					} else if (npc.venomDamage >= 20) {
						CombatNpc.applyHitSplatOnNpc(null, npc, 20, ServerConstants.VENOM_HITSPLAT_COLOUR, ServerConstants.NO_ICON, 1);
					} else {
						CombatNpc.applyHitSplatOnNpc(null, npc, npc.venomDamage, ServerConstants.VENOM_HITSPLAT_COLOUR, ServerConstants.NO_ICON, 1);
					}
					npc.venomDamage += 2;
					npc.venomHits += 1;
				} else {
					npc.underAttack = true;
					if (npc.venomDamage == 6) {
						CombatNpc.applyHitSplatOnNpc(null, npc, npc.venomDamage, ServerConstants.VENOM_HITSPLAT_COLOUR, ServerConstants.NO_ICON, 1);
					} else if (npc.venomDamage >= 20) {
						CombatNpc.applyHitSplatOnNpc(null, npc, 20, ServerConstants.VENOM_HITSPLAT_COLOUR, ServerConstants.NO_ICON, 1);
					} else {
						CombatNpc.applyHitSplatOnNpc(null, npc, npc.venomDamage, ServerConstants.VENOM_HITSPLAT_COLOUR, ServerConstants.NO_ICON, 1);
					}
					npc.venomDamage += 2;
					npc.venomHits += 1;
				}
			}
		}, 1);
		npc.venomEvent = true;

		CycleEventHandler.getSingleton().addEvent(npc, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				if (npc.isDead()) {
					container.stop();
					return;
				}
				if (npc.venomHits == 0) {
					container.stop();
					return;
				}

				npc.venomTicksUntillDamage--;
				if (npc.venomTicksUntillDamage == 0) {
					if (player == null) {
						if (npc.venomDamage == 6) {
							CombatNpc.applyHitSplatOnNpc(null, npc, npc.venomDamage, ServerConstants.VENOM_HITSPLAT_COLOUR, ServerConstants.NO_ICON, 1);
						} else if (npc.venomDamage >= 20) {
							CombatNpc.applyHitSplatOnNpc(null, npc, 20, ServerConstants.VENOM_HITSPLAT_COLOUR, ServerConstants.NO_ICON, 1);
						} else {
							CombatNpc.applyHitSplatOnNpc(null, npc, npc.venomDamage, ServerConstants.VENOM_HITSPLAT_COLOUR, ServerConstants.NO_ICON, 1);
						}
						npc.venomDamage += 2;
						npc.venomHits += 1;
					} else {
						if (npc.venomDamage == 6) {
							CombatNpc.applyHitSplatOnNpc(player, npc, npc.venomDamage, ServerConstants.VENOM_HITSPLAT_COLOUR, ServerConstants.NO_ICON, 1);
						} else if (npc.venomDamage >= 20) {
							CombatNpc.applyHitSplatOnNpc(player, npc, 20, ServerConstants.VENOM_HITSPLAT_COLOUR, ServerConstants.NO_ICON, 1);
						} else {
							CombatNpc.applyHitSplatOnNpc(player, npc, npc.venomDamage, ServerConstants.VENOM_HITSPLAT_COLOUR, ServerConstants.NO_ICON, 1);
						}
						npc.venomDamage += 2;
						npc.venomHits += 1;
					}
					npc.venomHitsplatsLeft--;
					npc.venomTicksUntillDamage = Venom.TICKS_UNTIL_VENOM;
				}
			}

			@Override
			public void stop() {
				npc.venomEvent = false;
				npc.venomDamage = 6;
				npc.venomHits = 0;
			}
		}, 1);

	}

	public static void calculateMagicDamageOnNpc(Player attacker, Npc npc) {
		// Ice Blitz on Battle mage.
		if (NpcHandler.isBattleMageNpc(npc.npcType) && CombatConstants.MAGIC_SPELLS[attacker.getSpellId()][0] == 12871) {
			Achievements.checkCompletionMultiple(attacker, "1027");
		}
		int maximumDamage = MagicFormula.getMagicMaximumDamage(attacker);
		int accuracy = MagicFormula.getMagicAttackAdvantage(attacker);
		if (attacker.slayerTaskNpcType == npc.npcType) {
			if (Slayer.isSlayerNpc(npc.npcType)) {
				// Slayer helm (i)
				if (ItemAssistant.hasItemEquippedSlot(attacker, 11865, ServerConstants.HEAD_SLOT)) {
					maximumDamage *= 1.15;
				}
			}
		}
		if (GameType.isOsrs()) { // Thammaron's sceptre, 100% boosted accuracy & 25% bonus damage vs wilderness npcs (or max hit dummy in this case)
			if (attacker.getWieldedWeapon() == 22555 && Area.inDangerousPvpArea(attacker) || attacker.getWieldedWeapon() == 22555 && npc.npcType == 1337) {
				accuracy *= 2;
				maximumDamage *= 1.25;
				//attacker.getPA().sendMessage("" + maximumDamage + ", " + accuracy);
			}
		}
		int damage = Misc.random(maximumDamage);

		// Max hit dummy.
		if (npc.npcType == 1337) {
			damage = maximumDamage;
			attacker.usingMaxHitDummy = true;
		}
		attacker.setMaximumDamageMagic(maximumDamage);
		boolean magicFailed = false;
		int bonusAttack = CombatNpc.getBonusAttack(attacker, npc);
		if (Misc.random(npc.npcType == 2267 ? (NpcDefinition.getDefinitions()[npc.npcType].meleeDefence / 2) : NpcDefinition.getMagicDefence(npc.npcType)) > 10 + Misc.random(
				MagicFormula.getMagicAttackAdvantage(attacker)) + bonusAttack && npc.npcType != 11006) {
			damage = 0;
			magicFailed = true;
		} else if (npc.npcType == 2265 || npc.npcType == 2266) {
			damage = 0;
			magicFailed = true;
		}
		if (Misc.random((NpcDefinition.getDefinitions()[npc.npcType].meleeDefence / 6)) > 10 + Misc.random(MagicFormula.getMagicAttackAdvantage(attacker)) + bonusAttack
		    && npc.npcType == 11006) {
			damage = 0;
			magicFailed = true;
		}
		attacker.setOldSpellId(attacker.getSpellId());
		damage = tormentedDemonShield(attacker, damage, npc.npcType, npc.tormentedDemonShield);


		if (npc.npcType == 8350) {
			damage *= 0.4;
		}
		if (!magicFailed) {
			long freezeDelay = Combat.getFreezeTime(attacker); // freeze
			if (freezeDelay > 0 && npc.canBeFrozen()) {
				npc.setFrozenLength(freezeDelay);
			}
			switch (CombatConstants.MAGIC_SPELLS[attacker.getOldSpellId()][0]) {
				case 12901:
				case 12919:
					// blood spells
				case 12911:
				case 12929:
					int heal = Misc.random(damage / 4);
					attacker.addToHitPoints(heal);
					break;
			}
		}

		attacker.setMagicDamage(damage);
		attacker.setMagicSplash(magicFailed);


		Combat.addCombatExperience(attacker, ServerConstants.MAGIC_ICON, damage);
	}

	private static void appendMultiBarrageNPC(Player attacker, int npcId, boolean splashed) {
		if (NpcHandler.npcs[npcId] != null) {
			Npc n = NpcHandler.npcs[npcId];
			if (n.isDead() || n.getCurrentHitPoints() <= 0) {
				return;
			}
			if (checkMultiBarrageReqsNPC(npcId)) {
				attacker.barrageCount++;
				if (Misc.random(NpcDefinition.getMagicDefence(NpcHandler.npcs[npcId].npcType)) < (10 + Misc.random(MagicFormula.getMagicAttackAdvantage(attacker)))) {
					if (Combat.getEndGfxHeight(attacker, CombatConstants.MAGIC_SPELLS[attacker.getOldSpellId()][0]) == 100) {
						n.gfx100(CombatConstants.MAGIC_SPELLS[attacker.oldSpellId][5]);
					} else {
						n.gfx0(CombatConstants.MAGIC_SPELLS[attacker.oldSpellId][5]);
					}
					int damage = Misc.random(attacker.getMagicDamage());
					if (n.getCurrentHitPoints() - damage < 0) {
						damage = n.getCurrentHitPoints();
					}
					CombatNpc.applyHitSplatOnNpc(attacker, n, damage, ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.MAGIC_ICON, 1);
					attacker.setSpellId(attacker.oldSpellId);
					attacker.setLastCastedMagic(true);
					Combat.addCombatExperience(attacker, ServerConstants.MAGIC_ICON, damage);
					n.underAttackBy = attacker.getPlayerId();
					n.underAttack = true;
					multiSpellEffectNPC(attacker, npcId, damage);
				} else
					n.gfx100(85);
			}
		}

	}

	public static boolean multiMagicSpell(Player player) {
		switch (CombatConstants.MAGIC_SPELLS[player.oldSpellId][0]) {
			case 12891:
			case 12881:
			case 13011:
			case 13023:
			case 12919:
				// blood spells
			case 12929:
			case 12963:
			case 12975:
				return true;
		}
		return false;

	}

	public static void calculateRangedDamageOnNpc(Player attacker, Npc npc, int damageType) {
		int rangedAttack = RangedFormula.getInvisibleRangedAttackAdvantage(attacker);
		int maximum = RangedFormula.getRangedMaximumDamage(attacker);

		if (attacker.slayerTaskNpcType == npc.npcType) {
			if (Slayer.isSlayerNpc(npc.npcType)) {
				// Slayer helm (i)
				if (ItemAssistant.hasItemEquippedSlot(attacker, 11865, ServerConstants.HEAD_SLOT)) {
					maximum *= 1.15;
				}
			}
		}
		// Dragon hunter crossbow.
		if (attacker.getWieldedWeapon() == 21012 && NpcDefinition.getDefinitions()[npc.npcType].name.toLowerCase().contains("dragon")) {
			rangedAttack *= 1.3;
			maximum *= 1.3;
		}

		if (GameType.isOsrs()) { // Craw's bow, 50% boosted accuracy & damage vs wilderness npcs (or max hit dummy in this case)
			if (attacker.getWieldedWeapon() == 22550 && Area.inDangerousPvpArea(attacker) || attacker.getWieldedWeapon() == 22550 && npc.npcType == 1337) {
				rangedAttack *= 1.5;
				maximum *= 1.5;
				//attacker.getPA().sendMessage("" + maximum + ", " + rangedAttack);
			}
		}

		// Twisted bow.
		if (attacker.getWieldedWeapon() == 20997 || attacker.getWieldedWeapon() == 16052 || attacker.getWieldedWeapon() == 16056 || attacker.getWieldedWeapon() == 16282) {
			double damageMultiplier = 0.0;
			double attackMultiplier = 0.0;
			switch (NpcDefinition.getDefinitions()[npc.npcType].name) {
				case "Cerberus":
					damageMultiplier = 2.0;
					break;
				case "Infernal Mage":
					damageMultiplier = 1.2;
					break;
				case "Balfrug Kreeyath":
					damageMultiplier = 1.6;
					break;
				case "Tormented demon":
					damageMultiplier = 1.7;
					break;
				case "General Graardor":
					damageMultiplier = 1.4;
					break;
				case "Lizardman Shaman":
					damageMultiplier = 1.5;
					break;
				case "Corporeal Beast":
					damageMultiplier = 1.4;
					break;
				case "Venenatis":
					damageMultiplier = 1.6;
					break;
				case "Kree'arra":
					damageMultiplier = 1.8;
					break;
				case "Wingman Skree":
					damageMultiplier = 1.6;
					break;
				case "Commander Zilyana":
					damageMultiplier = 2.3;
					break;
				case "Growler":
					damageMultiplier = 1.6;
					break;
				case "Sergeant Steelwill":
					damageMultiplier = 1.6;
					break;
				case "K'ril Tsutsaroth":
					damageMultiplier = 1.9;
					break;
				case "Ahrim":
					damageMultiplier = 1.3;
					break;
				case "Dagannoth Prime":
					damageMultiplier = 2.1;
					break;
				case "Chaos Fanatic":
					damageMultiplier = 1.9;
					break;
				case "Chaos elemental":
					damageMultiplier = 2.2;
					break;
				case "TzTok-Jad":
					damageMultiplier = 2.5;
					break;
			}
			if (damageMultiplier > 1.0) {
				damageMultiplier += 0.2;
				maximum *= damageMultiplier;
				attackMultiplier = damageMultiplier;
				if (maximum > 250) {
					maximum = 250;
				}
			}
			if (attackMultiplier > 1.0) {
				rangedAttack *= (attackMultiplier + 0.50);
			}
		}
		int damage = Misc.random(1, maximum);

		// Max hit dummy.
		if (npc.npcType == 1337) {
			damage = maximum;
			attacker.usingMaxHitDummy = true;
		}

		// Cave kraken
		if (npc.npcType == 496) {
			damage *= 0.5;
		}
		attacker.maximumDamageRanged = maximum;
		boolean ignoreDef = false;

		if (Misc.random(NpcDefinition.getRangedDefence(npc.npcType)) > Misc.random(rangedAttack) && !ignoreDef) {
			damage = 0;
		} else if (npc.npcType == 2265 || npc.npcType == 2267 && !ignoreDef) {
			damage = 0;
		}
		if (npc.npcType == 8133) {
			damage /= 2;
		}
		if (attacker.hit1) {
			damage = 999;
		}
		damage = tormentedDemonShield(attacker, damage, npc.npcType, npc.tormentedDemonShield);
		if (npc.npcType == 8351) {
			damage *= 0.4;
		}

		//Opal bolts (e)
		if (Misc.hasPercentageChance(5) && (attacker.playerEquipment[ServerConstants.ARROW_SLOT] == 9236 || attacker.playerEquipment[ServerConstants.ARROW_SLOT] == 21932)) {

			if (RangedData.hasCrossBowEquipped(attacker)) {
				if (attacker.playerEquipment[ServerConstants.ARROW_SLOT] <= RangedData.getHighestBolt(attacker, attacker.getWieldedWeapon())) {
					int level = attacker.getCurrentCombatSkillLevel(4);
					damage += level / 10;
					npc.gfx0(749);
				}
			}
		}

		//Diamond bolts (e)
		if (Misc.random(9) == 1 && (attacker.playerEquipment[ServerConstants.ARROW_SLOT] == 9243 || attacker.playerEquipment[ServerConstants.ARROW_SLOT] == 21946)) {

			if (RangedData.hasCrossBowEquipped(attacker)) {
				if (attacker.playerEquipment[ServerConstants.ARROW_SLOT] <= RangedData.getHighestBolt(attacker, attacker.getWieldedWeapon())) {
					ignoreDef = true;
					npc.gfx0(758);
				}
			}
		}
		//Ruby bolts (e)
		if (Misc.hasPercentageChance(6) && (attacker.playerEquipment[ServerConstants.ARROW_SLOT] == 9242 || attacker.playerEquipment[ServerConstants.ARROW_SLOT] == 21944)
		    && damage > 0) {
			if (RangedData.hasCrossBowEquipped(attacker)) {
				if (attacker.playerEquipment[ServerConstants.ARROW_SLOT] <= RangedData.getHighestBolt(attacker, attacker.getWieldedWeapon())) {
					if (attacker.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) > (attacker.getBaseHitPointsLevel() / 10)) {
						npc.gfx0(754);
						damage = npc.getCurrentHitPoints() / 5;
						if (damage > 100) {
							damage = 100;
						}
						attacker.dealDamage(attacker.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) / 10);
						attacker.gfx0(754);
						attacker.subtractFromHitPoints(attacker.getBaseHitPointsLevel() / 10);
						Skilling.updateSkillTabFrontTextMain(attacker, ServerConstants.HITPOINTS);
					}
				}
			}
		}
		if (attacker.isUsingDarkBowSpecialAttack()) {
			if (damage < 8) {
				damage = 8;
			}
		}
		//Dragon bolts (e)
		if (damage > 0 && Misc.random(9) == 1 && (attacker.playerEquipment[ServerConstants.ARROW_SLOT] == 9244 || attacker.playerEquipment[ServerConstants.ARROW_SLOT] == 21948)) {

			if (RangedData.hasCrossBowEquipped(attacker)) {
				if (attacker.playerEquipment[ServerConstants.ARROW_SLOT] <= RangedData.getHighestBolt(attacker, attacker.getWieldedWeapon())) {
					damage *= 1.55;
					npc.gfx0(756);
					attacker.specialAttackWeaponUsed[29] = 1;
					attacker.setWeaponAmountUsed(29);
				}
			}
		}

		// Onyx bolt (e)
		else if ((attacker.playerEquipment[ServerConstants.ARROW_SLOT] == 9245 || attacker.playerEquipment[ServerConstants.ARROW_SLOT] == 21950) && Misc.hasPercentageChance(11)) {
			if (RangedData.hasCrossBowEquipped(attacker)) {
				if (attacker.playerEquipment[ServerConstants.ARROW_SLOT] <= RangedData.getHighestBolt(attacker, attacker.getWieldedWeapon())) {
					damage *= 1.2;
					int heal = damage / 4;
					attacker.addToHitPoints(heal);
					npc.gfx0(753);
				}
			}
		}
		if (attacker.blowpipeSpecialAttack) {
			attacker.addToHitPoints(damage / 2);
		}
		if (damageType == 1) {
			attacker.rangedFirstDamage = damage;
		} else {
			attacker.rangedSecondDamage = damage;
		}
		Combat.addCombatExperience(attacker, ServerConstants.RANGED_ICON, damage);
	}


	private static void applyDragonClawsDamageOnNpc(final Player attacker, final Npc npc) {
		if (!attacker.getDragonClawsSpecialAttack()) {
			return;
		}
		if (attacker.getUsingDragonClawsSpecialAttackEvent()) {
			return;
		}
		attacker.setUsingDragonClawsSpecialAttackEvent(true);
		CycleEventHandler.getSingleton().addEvent(attacker, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				container.stop();
			}

			@Override
			public void stop() {
				if (npc.getCurrentHitPoints() - attacker.meleeThirdDamage < 0) {
					attacker.meleeThirdDamage = npc.getCurrentHitPoints();
				}

				CombatNpc.applyMeleeDamageOnNpc(attacker, npc, 3, attacker.meleeThirdDamage);

				if (npc.getCurrentHitPoints() - attacker.meleeFourthDamage < 0) {
					attacker.meleeFourthDamage = npc.getCurrentHitPoints();
				}
				CombatNpc.applyMeleeDamageOnNpc(attacker, npc, 4, attacker.meleeFourthDamage);
				attacker.setUsingDragonClawsSpecialAttackEvent(false);
			}
		}, 1);
	}

	public static void calculateMeleeDamageOnNpc(Player attacker, Npc npc, int damageType, boolean clawsFormula) {
		int damage = 0;
		MeleeAttack.saveCriticalDamage(attacker);
		int maximumDamage = MeleeFormula.getMaximumMeleeDamage(attacker);
		int accuracy = MeleeFormula.getInvisibleMeleeAttackAdvantage(attacker);
		boolean task = false;
		if (attacker.slayerTaskNpcType == npc.npcType) {
			task = Slayer.isSlayerNpc(npc.npcType);
			if (task) {
				for (int index = 0; index < ServerConstants.getSlayerHelms().length; index++) {
					int helmId = ServerConstants.getSlayerHelms()[index];
					if (ItemAssistant.hasItemEquipped(attacker, helmId)) {
						maximumDamage *= 1.15;
					}
				}
			}
		}
		if (GameType.isOsrs()) { // Viggora's chainmace, 50% boosted accuracy & damage vs wilderness npcs (or max hit dummy in this case)
			if (attacker.getWieldedWeapon() == 22545 && Area.inDangerousPvpArea(attacker) || attacker.getWieldedWeapon() == 22545 && npc.npcType == 1337) {
				maximumDamage *= 1.5;
				accuracy *= 1.5;
				//attacker.getPA().sendMessage("" + maximumDamage + ", " + accuracy);
			}
		}

		String npcName = NpcDefinition.getDefinitions()[npc.npcType].name.toLowerCase();


		// Leaf-bladed battleaxe
		if (attacker.getWieldedWeapon() == 20727) {
			if (npcName.contains("turoth") || npcName.contains("kurask")) {
				maximumDamage *= 1.175;
			}
		}
		boolean salveAmuletEBoost = false;
		if (Combat.hasSalveAmuletE(attacker)) {
			for (int undead : ServerConstants.UNDEAD_NPCS_OSRS) {
				if (npc.npcType == undead) {
					maximumDamage *= 1.20;
					accuracy *= 1.20;
					salveAmuletEBoost = true;
					break;
				}
			}
		}

		damage = Misc.random(1, maximumDamage);

		// Max hit dummy.
		if (npc.npcType == 1337) {
			damage = maximumDamage;
			attacker.usingMaxHitDummy = true;
		}

		int attackAdvantage = MeleeFormula.getInvisibleMeleeAttackAdvantage(attacker);
		if (task) {
			if (ItemAssistant.hasItemEquippedSlot(attacker, 11864, ServerConstants.HEAD_SLOT)) {
				attackAdvantage *= 1.15;
			} else if (ItemAssistant.hasItemEquippedSlot(attacker, 11865, ServerConstants.HEAD_SLOT)) {
				attackAdvantage *= 1.15;
			}
		}
		int defence = NpcDefinition.getDefinitions()[npc.getTransformOrId()].meleeDefence;

		EntityCombatStrategy combatStrategy = npc.getCombatStrategyOrNull();

		if (combatStrategy != null) {
			defence = combatStrategy.calculateCustomCombatDefence(attacker, npc, defence, ServerConstants.MELEE_ICON);
		}
		if (Misc.random(defence) > Misc.random(attackAdvantage)) {
			damage = 0;
		}

		if (npc.npcType == 2266 || npc.npcType == 2267) {
			damage = 0;
		}

		if (attacker.hit1) {
			damage = 999;
		}

		damage = tormentedDemonShield(attacker, damage, npc.npcType, npc.tormentedDemonShield);
		if (npc.npcType == 11011 && !Combat.wearingFullVerac(attacker)) {
			damage *= 0.4;
		}
		if (npc.tormentedDemonShield && (npc.npcType == 11011)) {
			if (ItemAssistant.hasItemEquipped(attacker, 6746)) {
				npc.tormentedDemonShield = false;
				npc.tormentedDemonTimeWeakened = System.currentTimeMillis();
				attacker.getPA().sendMessage("The shield of the demon has been demolished.");
			}
		}
		if (damageType == 2) {
			if (attacker.getMultipleDamageSpecialAttack()) {
				if (attacker.saradominSwordSpecialAttack) {
					damage = Misc.random(damage);
					if (damage > 18) {
						damage = 18;
					}
				}
				attacker.meleeSecondDamage = damage;
				if (!clawsFormula) {
					Combat.addCombatExperience(attacker, ServerConstants.MELEE_ICON, damage);
				}
			}
		} else if (damageType == 1) {
			if (attacker.isGraniteMaulSpecial) {
				attacker.graniteMaulSpecialDamage = damage;
				attacker.isGraniteMaulSpecial = false;
			} else {
				attacker.meleeFirstDamage = damage;
			}
			if (!clawsFormula) {
				Combat.addCombatExperience(attacker, ServerConstants.MELEE_ICON, damage);
			}
		} else if (damageType == 3) {
			attacker.meleeThirdDamage = damage;
			if (!clawsFormula) {
				Combat.addCombatExperience(attacker, ServerConstants.MELEE_ICON, damage);
			}
		}
	}

	public static void setEngageWithMagicVariables(Player attacker) {

		if (GameType.isPreEoc()) {
			if (attacker.getWieldedWeapon() == 22_494 && attacker.getSpellId() == -1
					&& (attacker.getAutocastId() == -1
					|| attacker.getAutocastId() == AutoCast.POLYPORE_AUTOCAST)) {
				attacker.setSpellId(AutoCast.POLYPORE_AUTOCAST);
				attacker.setAutocastId(AutoCast.POLYPORE_AUTOCAST);
				attacker.setAutoCasting(true);
				attacker.setLastCastedMagic(true);
				attacker.getPA().sendFrame36(43, -1, false);
				attacker.getPA().sendFrame36(108, 1, false);
			}
		}

		if (attacker.getAutoCasting()) {
			if (attacker.getAutocastId() != 52) {
				if (Combat.spellbookPacketAbuse(attacker, attacker.getAutocastId())) {
					Combat.resetPlayerAttack(attacker);
					return;
				}
			}
			attacker.setSpellId(attacker.getAutocastId());
			attacker.setLastCastedMagic(true);
		}
		// Trident of the swamp.
		else if (attacker.getWieldedWeapon() == 12899 && attacker.getSpellId() == -1) {
			attacker.setSpellId(52);
			attacker.setAutocastId(52);
			attacker.setAutoCasting(true);
			attacker.setLastCastedMagic(true);
			attacker.getPA().sendFrame36(43, -1, false);
			attacker.getPA().sendFrame36(108, 1, false);
		}


	}

	private static int tormentedDemonShield(Player attacker, int damage, int npcType, boolean shield) {
		if (npcType != 11011) {
			return damage;
		}
		/*
		if (shield)
		{
				attacker.getPA().sendMessage("Your damage has been reduced due to the Tormented Demon's shield.");
				damage *= 0.7;
		}
		*/
		return damage;
	}

	private static void calculateDragonClawsOnNpc(Player attacker, Npc npc) {
		if (!attacker.getDragonClawsSpecialAttack()) {
			return;
		}
		calculateMeleeDamageOnNpc(attacker, npc, 1, true);
		int damage1 = attacker.meleeFirstDamage;
		int damage2 = 0;
		int damage3 = 0;
		int damage4 = 0;

		damage1 = tormentedDemonShield(attacker, damage1, npc.npcType, npc.tormentedDemonShield);
		if (npc.npcType == 11011) {
			damage1 *= 0.4;
		}

		/* Start of First result. */
		if (damage1 > 0) {
			damage2 = damage1 / 2;
			damage3 = damage2 / 2;
			damage4 = damage3;
		} else {
			calculateMeleeDamageOnNpc(attacker, npc, 1, true);
			damage1 = attacker.meleeFirstDamage;
			damage2 = damage1 / 2;
			damage3 = damage2 / 2;
			damage4 = damage3;
		} /* End of First result. */
		if (damage1 == 0) {
			calculateMeleeDamageOnNpc(attacker, npc, 3, true);
			damage3 = attacker.meleeThirdDamage;
			damage4 = damage3;
		}
		Combat.addCombatExperience(attacker, ServerConstants.MELEE_ICON, (damage1 + damage2 + damage3 + damage4));
		attacker.meleeSecondDamage = damage2;
		attacker.meleeThirdDamage = damage3;
		attacker.meleeFourthDamage = damage4;
	}

	public static void attackNpc(Player attacker, Npc npc) {
		if (attacker.multiLoggingInWild) {
			Combat.resetPlayerAttack(attacker);
			return;
		}
		if (npc == null) {
			Combat.resetPlayerAttack(attacker);
			return;
		}
		if (attacker.getType() == EntityType.PLAYER_PET) {
			Combat.resetPlayerAttack(attacker);
			return;
		}
		if (attacker.doingAnAction()) {
			Combat.resetPlayerAttack(attacker);
			return;
		}

		if (attacker.getHeight() != npc.getHeight()) {
			Combat.resetPlayerAttack(attacker);
			return;
		}

		// Toxic blowpipe (empty)
		if (attacker.getWieldedWeapon() == 12924) {
			Combat.resetPlayerAttack(attacker);
			return;
		}
		Combat.resetSpecialAttackData(attacker);
		AttackPlayer.resetAttackData(attacker);

		if (npc.isDead() || npc.maximumHitPoints <= 0) {
			Movement.stopMovement(attacker);

			// This will stop the player from turning to npc after it died when i just killed it.
			if (attacker.getNpcIdAttacking() != npc.npcIndex) {
				attacker.turnPlayerTo(npc.getX(), npc.getY());
			}
			Combat.resetPlayerAttack(attacker);
			return;
		}
		if (attacker.getDead()) {
			Combat.resetPlayerAttack(attacker);
			return;
		}

		if (!WarriorsGuild.canAttackCyclops(attacker, npc.npcType)) {
			Movement.stopMovement(attacker);
			attacker.turnPlayerTo(npc.getX(), npc.getY());
			Combat.resetPlayerAttack(attacker);
			return;
		}

		int npcX = npc.getVisualX();
		int npcY = npc.getVisualY();
		if (!npc.isAttackable()) {
			Movement.stopMovement(attacker);
			attacker.turnPlayerTo(npc.getX(), npc.getY());
			Combat.resetPlayerAttack(attacker);
			return;
		}

		// Max hit dummy
		if (npc.npcType == 1337) {
			RandomEvent.randomEvent(attacker, 300);
			if (RandomEvent.isBannedFromSkilling(attacker)) {
				return;
			}
		}

		if (npc.underAttackBy > 0 && npc.underAttackBy != attacker.getPlayerId() && !Area.inMulti(npcX, npcY) && npc.npcType != 7286 && npc.npcType != 1337) {
			Movement.stopMovement(attacker);
			attacker.turnPlayerTo(npc.getX(), npc.getY());
			attacker.playerAssistant.sendMessage("This monster is already in combat.");
			Combat.resetPlayerAttack(attacker);
			return;
		}
		if (!Area.npcInMulti(npc, attacker.getX(), attacker.getY()) && Combat.wasUnderAttackByAnotherPlayer(attacker, 5000) && npc.npcType != 7286) {
			Movement.stopMovement(attacker);
			attacker.turnPlayerTo(npc.getX(), npc.getY());
			attacker.playerAssistant.sendMessage("I am already under attack.");
			Combat.resetPlayerAttack(attacker);
			return;
		}

		if ((attacker.getUnderAttackBy() > 0 || attacker.getNpcIndexAttackingPlayer() > 0 && Combat.wasAttackedByNpc(attacker))
		    && attacker.getNpcIndexAttackingPlayer() != npc.npcIndex && !Area.npcInMulti(npc, attacker.getX(), attacker.getY())) {
			if (npc.npcType != 7286) {
				Movement.stopMovement(attacker);
				attacker.turnPlayerTo(npc.getX(), npc.getY());
				attacker.playerAssistant.sendMessage("I am already under attack.");
				Combat.resetPlayerAttack(attacker);
				return;
			}
		}

		Slayer.Task taskData = Slayer.getTask(npc.npcType);
		if (taskData != null) {
			if (attacker.baseSkillLevel[ServerConstants.SLAYER] < taskData.getLevelReq() && taskData.isLevelRequired()) {
				attacker.playerAssistant.sendMessage("You need " + taskData.getLevelReq() + " slayer to attack this monster.");
				Movement.stopMovement(attacker);
				attacker.turnPlayerTo(npc.getX(), npc.getY());
				Combat.resetPlayerAttack(attacker);
				return;
			}
		}
		if (npc.npcType == 1 && npcX >= 2985 && npcX <= 3000 && npcY >= 3360 && npcY <= 3380) {
			attacker.playerAssistant.sendMessage("You cannot attack this npc.");
			Combat.resetPlayerAttack(attacker);
			Movement.stopMovement(attacker);
			attacker.turnPlayerTo(npc.getX(), npc.getY());
			return;
		}
		if (npc.npcType == 7273 && !attacker.isSuperDonator()) {
			attacker.playerAssistant.sendMessage("<img=4>You need to be a Super donator to attack Brutal blue dragons.");
			Combat.resetPlayerAttack(attacker);
			Movement.stopMovement(attacker);
			attacker.turnPlayerTo(npc.getX(), npc.getY());
			return;
		}
		if (npc.npcType == 7274 && !attacker.isExtremeDonator()) {
			attacker.playerAssistant.sendMessage("<img=5>You need to be an Extreme donator to attack Brutal red dragons.");
			Combat.resetPlayerAttack(attacker);
			Movement.stopMovement(attacker);
			attacker.turnPlayerTo(npc.getX(), npc.getY());
			return;
		}
		if (npc.npcType == 7275 && !attacker.isLegendaryDonator()) {
			attacker.playerAssistant.sendMessage("<img=6>You need to be a Legendary donator to attack Brutal black dragons.");
			Combat.resetPlayerAttack(attacker);
			Movement.stopMovement(attacker);
			attacker.turnPlayerTo(npc.getX(), npc.getY());
			return;
		}
		if (npc.npcType == 7286 && attacker.getCombatLevel() != 126) {
			attacker.playerAssistant.sendMessage("You may only attack Skotizo with 126 combat.");
			Combat.resetPlayerAttack(attacker);
			Movement.stopMovement(attacker);
			attacker.turnPlayerTo(npc.getX(), npc.getY());
			return;
		}

		if (attacker.getSpellId() == 31) {
			attacker.getPA().sendMessage("You cannot use tele block on npcs.");
			Combat.resetPlayerAttack(attacker);
			Movement.stopMovement(attacker);
			attacker.turnPlayerTo(npc.getX(), npc.getY());
			return;
		}
		// Trident of the swap
		if (attacker.getAutocastId() == 52 && attacker.getWieldedWeapon() != 12899) {
			attacker.setAutocastId(-1);
			attacker.setAutoCasting(false);
			Combat.resetPlayerAttack(attacker);
			Movement.stopMovement(attacker);
			attacker.turnPlayerTo(npc.getX(), npc.getY());
			return;
		}
		EntityCombatStrategy strategy = npc.getCombatStrategyOrNull();

		if (strategy != null) {
			if (!strategy.canBeAttacked(attacker, npc)) {
				Combat.resetPlayerAttack(attacker);
				return;
			}
		}

		if (Combat.hasGraniteMaulEquipped(attacker) && attacker.graniteMaulSpecialAttackClicks > 0 && attacker.getSpecialAttackAmount() >= 5) {
			return;
		}
		attacker.resetPlayerIdToFollow();
		attacker.setNpcIdToFollow(npc.npcIndex);
		// Dinh's bulwark
		if ((attacker.getWieldedWeapon() == 21015 || attacker.getWieldedWeapon() == 16259) && attacker.getCombatStyle(ServerConstants.DEFENSIVE)) {
			return;
		}
		ChargeableCollection collection = attacker.getAttributes().getOrDefault(Player.CHARGEABLE_COLLECTION_KEY);

		Chargeable chargeable = Chargeable.valueOfCharged(attacker.getWieldedWeapon());

		if (chargeable != null) {
			if (collection.isEmpty(chargeable)) {
				collection.switchToUncharged(attacker, chargeable);
			} else {
				collection.decreaseAll(chargeable, 1);
			}
		}
		if (attacker.getAttackTimer() <= 0) {
			boolean usingBow = false;
			boolean isWieldingRangedWeaponWithNoArrowSlotRequirement = false;
			boolean usingCross = false;
			boolean usingMagic = attacker.hasLastCastedMagic();
			if (!usingMagic) {
				usingCross = Combat.getUsingCrossBow(attacker);
			}
			attacker.bonusAttack = 0;
			attacker.setDroppedRangedItemUsed(0);
			attacker.setProjectileStage(0);

			setEngageWithMagicVariables(attacker);
			attacker.setSpecialAttackAccuracyMultiplier(1.0);
			attacker.specDamage = 1.0;
			if (!usingMagic) {
				if (RangedData.isWieldingRangedWeaponWithNoArrowSlotRequirement(attacker)) {
					isWieldingRangedWeaponWithNoArrowSlotRequirement = true;
					attacker.setMeleeFollow(false);
				}
			} else {
				attacker.setMeleeFollow(false);
			}
			attacker.setUsingMediumRangeRangedWeapon(false);
			RangedData.isWieldingMediumRangeRangedWeapon(attacker);
			if (!usingMagic) {
				if (attacker.isUsingMediumRangeRangedWeapon()) {
					usingBow = true;
				}
			}
			if (attacker.playerAssistant.withInDistance(attacker.getX(), attacker.getY(), npcX, npcY, CombatConstants.getAttackDistance(attacker)) && (usingBow || usingMagic)) {
				Movement.stopMovement(attacker);
			}
			if (CombatNpc.isArmadylNpc(npc.npcIndex) && !usingCross && !usingBow && !usingMagic && !Combat.usingCrystalBow(attacker) && !isWieldingRangedWeaponWithNoArrowSlotRequirement) {
				attacker.playerAssistant.sendMessage("You can only use range against this.");
				Combat.resetPlayerAttack(attacker);
				return;
			}

			if (!attacker.playerAssistant.withInDistance(attacker.getX(), attacker.getY(), npcX, npcY, 2) && MeleeData.usingHalberd(attacker) && !isWieldingRangedWeaponWithNoArrowSlotRequirement
			    && !usingBow && !usingMagic
			    || !attacker.playerAssistant.withInDistance(attacker.getX(), attacker.getY(), npcX, npcY, CombatConstants.getAttackDistance(attacker)) && isWieldingRangedWeaponWithNoArrowSlotRequirement
			       && !usingBow && !usingMagic
			    || !attacker.playerAssistant.withInDistance(attacker.getX(), attacker.getY(), npcX, npcY, CombatConstants.getAttackDistance(attacker)) && usingBow) {
				return;
			}

			if (!attacker.playerAssistant.withInDistance(attacker.getX(), attacker.getY(), npcX, npcY, CombatConstants.MAGIC_FOLLOW_DISTANCE) && usingMagic) {
				return;
			}

			if (NpcDefinition.getDefinitions()[npc.npcType] == null) {
				Misc.print("Npc is not defined: " + npc.npcType);
				return;
			}

			if (!attacker.playerAssistant.withInDistance(attacker.getX(), attacker.getY(), npcX, npcY, NpcDefinition.getDefinitions()[npc.npcType].size) && (!isWieldingRangedWeaponWithNoArrowSlotRequirement
			                                                                                                                                                 && !MeleeData
					                                                                                                                                                     .usingHalberd(
							                                                                                                                                                     attacker)
			                                                                                                                                                 && !usingBow
			                                                                                                                                                 && !usingMagic)) {
				return;
			}

			boolean hasArrowEquipped = attacker.playerEquipment[ServerConstants.ARROW_SLOT] <= 0 ? false : true;
			if (usingBow || isWieldingRangedWeaponWithNoArrowSlotRequirement) {
				if (!RangedData.hasRequiredAmmo(attacker, hasArrowEquipped)) {
					Movement.stopMovement(attacker);
					Combat.resetPlayerAttack(attacker);
					attacker.turnPlayerTo(npc.getX(), npc.getY());
					return;
				}
				if (npc.npcType >= 1610 && npc.npcType <= 1612) {
					attacker.getPA().sendMessage("You can only use magic spells on this npc.");
					Movement.stopMovement(attacker);
					attacker.resetNpcIdentityAttacking();
					attacker.turnPlayerTo(npc.getX(), npc.getY());
					Combat.resetPlayerAttack(attacker);
					return;
				}
			} else if (!usingMagic) {
				if (npc.npcType >= 1610 && npc.npcType <= 1612) {
					attacker.getPA().sendMessage("You can only use magic spells on this npc.");
					Movement.stopMovement(attacker);
					attacker.resetNpcIdentityAttacking();
					attacker.turnPlayerTo(npc.getX(), npc.getY());
					Combat.resetPlayerAttack(attacker);
					return;
				}
			}
			if (usingBow || usingMagic || isWieldingRangedWeaponWithNoArrowSlotRequirement || (attacker.playerAssistant.withInDistance(attacker.getX(), attacker.getY(), npcX, npcY, 2) && MeleeData
					                                                                                                                                                     .usingHalberd(
							                                                                                                                                                     attacker))) {
				Movement.stopMovement(attacker);
			}
			if (attacker.getSpellId() >= 0) {
				if (!Combat.checkMagicRequirementsForNpcCombatAndMagicOnFloorItemPacket(attacker, attacker.getSpellId())) {
					Movement.stopMovement(attacker);
					attacker.resetNpcIdentityAttacking();
					attacker.turnPlayerTo(npc.getX(), npc.getY());
					return;
				}
			}
			if (attacker.isMeleeFollow() && attacker.getX() != npc.getX() && attacker.getY() != npc.getY() && Follow.isBigNpc(npc.npcType) == 0) {
				if (attacker.isFrozen()) {
					Combat.resetPlayerAttack(attacker);
					attacker.turnPlayerTo(npc.getX(), npc.getY());
				}
				return;
			}

			if (!Follow.isSpecialNpcPathing(npc.npcType)) {
				// Max hit dummy
				if (npc.npcType != 1337) {
					if (attacker.isMeleeFollow()) {
						if (!Region.isStraightPathUnblocked(attacker.getX(), attacker.getY(), npc.getVisualX(), npc.getVisualY(), attacker.getHeight(), 1, 1, false)) {
							return;
						}
					} else {
						if (!Region.isStraightPathUnblockedProjectiles(attacker.getX(), attacker.getY(), npc.getVisualX(), npc.getVisualY(), attacker.getHeight(), 1, 1, true)) {
							return;
						}
					}
				}
			}
			EntityAttackType attackType = usingBow || usingCross || isWieldingRangedWeaponWithNoArrowSlotRequirement ? EntityAttackType.RANGED
					                              : usingMagic && attacker.getSpellId() > 0 ? EntityAttackType.MAGIC
							                                : EntityAttackType.MELEE;

			if (strategy != null) {
				if (!strategy.canBeAttackedByType(attacker, npc, attackType)) {
					attacker.getPA().sendMessage(String.format("This npc cannot be attacked by %s attacks.", attackType.name().toLowerCase().replace("_", " ")));
					Combat.resetPlayerAttack(attacker);
					return;
				}
			}
			TargetSystem.doingWildActivity(attacker);
			attacker.againstPlayer = false;
			attacker.rangedSpecialAttackOnNpc = false;
			SpecialAttackTracker.resetSpecialAttackWeaponUsed(attacker);
			// Attacking begins.
			attacker.setAttackTimer(Combat.getAttackTimerCount(attacker, ItemAssistant.getItemName(attacker.getWieldedWeapon()).toLowerCase()));
			attacker.timePlayerAttackedNpc = System.currentTimeMillis();
			Barrows.startBarrowsTimer(attacker, npc);
			attacker.faceUpdate(npc.npcIndex);
			npc.underAttackBy = attacker.getPlayerId();
			npc.lastDamageTaken = System.currentTimeMillis();
			GameTimeSpent.increaseGameTime(attacker, GameTimeSpent.PVM);
			if (attacker.isUsingSpecial() && !usingMagic) {
				if (Combat.checkSpecAmount(attacker, attacker.getWieldedWeapon())) {
					SpecialAttackBase.activateSpecial(attacker, attacker.getWieldedWeapon(), npc.npcIndex);
					attacker.setUsingSpecialAttack(false);
					attacker.botUsedSpecialAttack = false;
					CombatInterface.updateSpecialBar(attacker);
					if (attacker.rangedSpecialAttackOnNpc) {
						calculateRangedDamageOnNpc(attacker, npc, 1);
						if (ItemDefinition.getDefinitions()[attacker.getWieldedWeapon()].name.toLowerCase().contains("dark bow") || ItemDefinition
								                                                                                                            .getDefinitions()[attacker.getWieldedWeapon()].name
								                                                                                                            .toLowerCase()
								                                                                                                            .contains("magic short")) {
							calculateRangedDamageOnNpc(attacker, npc, 2);
						}
					} else {
						if (attacker.getDragonClawsSpecialAttack()) {
							calculateDragonClawsOnNpc(attacker, npc);
						} else {
							calculateMeleeDamageOnNpc(attacker, npc, 1, false);
							calculateMeleeDamageOnNpc(attacker, npc, 2, false);
						}
					}
					Combat.saradominGodswordSpecialAttack(attacker);
					return;
				} else {
					Combat.notEnoughSpecialLeft(attacker);
					attacker.resetNpcIdentityAttacking();
					return;
				}
			}
			if (usingMagic && attacker.getSpellId() >= 0) {
				// Dinh's bulwark
				if ((attacker.getWieldedWeapon() == 21015 || attacker.getWieldedWeapon() == 16259)) {
					attacker.getPA().sendMessage("Your bulwark gets in the way.");
					return;
				}
				attacker.startAnimation(CombatConstants.MAGIC_SPELLS[attacker.getSpellId()][2]);
				Combat.addBarragesCasted(attacker);
				attacker.setUsingMagic(true);
				calculateMagicDamageOnNpc(attacker, npc);
				MagicData.requiredRunes(attacker, attacker.getSpellId(), "DELETE RUNES");
				npc.gfx0(65535);
			}

			if (!usingBow && !usingMagic && !isWieldingRangedWeaponWithNoArrowSlotRequirement) {
				attacker.setHitDelay(Combat.getHitDelay(attacker, ItemAssistant.getItemName(attacker.getWieldedWeapon()).toLowerCase()));
				attacker.setProjectileStage(0);
				attacker.setOldNpcIndex(npc.npcIndex);
				calculateMeleeDamageOnNpc(attacker, npc, 1, false);

				attacker.startAnimation(MeleeData.getWeaponAnimation(attacker, ItemAssistant.getItemName(attacker.getWieldedWeapon()).toLowerCase()));
			}
			if (usingBow && !isWieldingRangedWeaponWithNoArrowSlotRequirement && !usingMagic || usingCross) { // range hit delay
				if (usingCross) {
					attacker.setUsingMediumRangeRangedWeapon(true);
				}
				attacker.gfx100(Combat.getRangeStartGFX(attacker));
				attacker.setHitDelay(Combat.getHitDelay(attacker, ItemAssistant.getItemName(attacker.getWieldedWeapon()).toLowerCase()));
				attacker.setProjectileStage(1);
				attacker.setOldNpcIndex(npc.npcIndex);
				if (attacker.getWieldedWeapon() >= 4212 && attacker.getWieldedWeapon() <= 4223) {
					attacker.setDroppedRangedItemUsed(attacker.getWieldedWeapon());
				} else {
					attacker.setDroppedRangedItemUsed(attacker.playerEquipment[ServerConstants.ARROW_SLOT]);
					RangedAmmoUsed.deleteAmmo(attacker);
					if (attacker.getWieldedWeapon() == 11235) {
						RangedAmmoUsed.deleteAmmo(attacker);
					}
				}
				CombatNpc.fireProjectileNpc(attacker);
				calculateRangedDamageOnNpc(attacker, npc, 1);
				if (Combat.usingDbow(attacker)) {
					attacker.setUsingDarkBowNormalAttack(true);
					CombatNpc.fireProjectileNpc(attacker);
					calculateRangedDamageOnNpc(attacker, npc, 2);
				}
				attacker.startAnimation(RangedData.getRangedAttackEmote(attacker));
			} else if (isWieldingRangedWeaponWithNoArrowSlotRequirement && !usingMagic && !usingBow) { // knives, darts, etc hit delay
				attacker.setDroppedRangedItemUsed(attacker.getWieldedWeapon());
				RangedAmmoUsed.deleteAmmo(attacker);
				attacker.gfx100(Combat.getRangeStartGFX(attacker));
				attacker.setHitDelay(Combat.getHitDelay(attacker, ItemAssistant.getItemName(attacker.getWieldedWeapon()).toLowerCase()));
				attacker.setProjectileStage(1);
				attacker.setOldNpcIndex(npc.npcIndex);
				CombatNpc.fireProjectileNpc(attacker);
				calculateRangedDamageOnNpc(attacker, npc, 1);
				attacker.startAnimation(RangedData.getRangedAttackEmote(attacker));
			}
			if (usingMagic && attacker.getSpellId() >= 0) { // magic hit delay
				int pX = attacker.getX();
				int pY = attacker.getY();
				int nX = npc.getVisualX();
				int nY = npc.getVisualY();
				int offX = (pY - nY) * -1;
				int offY = (pX - nX) * -1;
				int distance = 0;
				if (!attacker.getPA().withinDistanceOfTargetNpc(npc, 4)) {
					distance++;
				}
				if (!attacker.getPA().withinDistanceOfTargetNpc(npc, 6)) {
					distance++;
				}
				attacker.setLastCastedMagic(true);
				attacker.setProjectileStage(2);
				if (CombatConstants.MAGIC_SPELLS[attacker.getSpellId()][3] > 0) {
					if (Combat.getStartGfxHeight(attacker) == 100) {
						attacker.gfx100(CombatConstants.MAGIC_SPELLS[attacker.getSpellId()][3]);
					} else {
						attacker.gfx0(CombatConstants.MAGIC_SPELLS[attacker.getSpellId()][3]);
					}
				}
				if (CombatConstants.MAGIC_SPELLS[attacker.getSpellId()][4] > 0) {
					attacker.getPA().createPlayersProjectile(pX, pY, offX, offY, 50, 78 + (distance * 25), CombatConstants.MAGIC_SPELLS[attacker.getSpellId()][4], Combat.getStartHeight(attacker),
					                                         Combat.getEndHeight(attacker), npc.npcIndex + 1, 50, Combat.getProjectileSlope(attacker));
				}
				attacker.setHitDelay(Combat.getHitDelay(attacker, ItemAssistant.getItemName(attacker.getWieldedWeapon()).toLowerCase()));
				attacker.setOldNpcIndex(npc.npcIndex);
				attacker.setOldSpellId(attacker.getSpellId());
				attacker.setSpellId(-1);
				if (!attacker.getAutoCasting()) {
					attacker.resetNpcIdentityAttacking();
					attacker.resetNpcIdToFollow();
				}
			}
		}
	}

	public static void applyHitSplatOnNPC(final Player attacker, final Npc npc) { // npc hit delay
		if (npc != null) {
			if (npc.isDead()) {
				if (npc.npcIndex == attacker.getNpcIdAttacking()) {
					attacker.resetNpcIdentityAttacking();
				}
				attacker.setHitDelay(0);
				attacker.resetFaceUpdate();
				attacker.setUsingDarkBowSpecialAttack(false);
				return;
			}

			// Max hit dummy, Master whirlpool
			if (npc.npcType != 1337 && npc.npcType != 496) {
				npc.facePlayer(attacker.getPlayerId());
			}
			if (npc.underAttackBy > 0) {
				npc.setKillerId(attacker.getPlayerId());
			}
			NpcHandler.blockAnimation(npc);
			DegradingManager.degrade(attacker, true);
			EntityCombatStrategy defenderStrategy = npc.getCombatStrategyOrNull();

			/*
			 * Melee damage
			 */
			if (attacker.getProjectileStage() == 0) { // melee hit damage

				// Dragon dagger p++ and Abyssal dagger p++.
				if ((attacker.getWieldedWeapon() == 5698 || attacker.getWieldedWeapon() == 13271) && Misc.hasPercentageChance(30)) {
					CombatNpc.applyPoisonOnNpc(attacker, npc, 6);
				}

				// Abyssal tentacle.
				if (Combat.hasAbyssalTentacle(attacker, attacker.getWieldedWeapon()) && Misc.hasPercentageChance(25)) {
					CombatNpc.applyPoisonOnNpc(attacker, npc, 4);
				}
				CombatNpc.applyMeleeDamageOnNpc(attacker, npc, 1, attacker.meleeFirstDamage);

				if (attacker.getMultipleDamageSpecialAttack()) {
					CombatNpc.applyMeleeDamageOnNpc(attacker, npc, 2, attacker.meleeSecondDamage);
					applyDragonClawsDamageOnNpc(attacker, npc);
				}

				if (defenderStrategy != null) {
					defenderStrategy.onDamageTaken(attacker, npc, attacker.meleeFirstDamage, ServerConstants.MELEE_ICON);

					if (attacker.getMultipleDamageSpecialAttack()) {
						defenderStrategy.onDamageTaken(attacker, npc, attacker.meleeSecondDamage, ServerConstants.MELEE_ICON);
					}
				}
			}
			/*
			 * Range damage
			 */
			if (!attacker.isUsingMagic() && attacker.getProjectileStage() > 0) { // range hit damage
				int damage = attacker.rangedFirstDamage;
				int damage2 = attacker.rangedSecondDamage;
				if (npc.getCurrentHitPoints() - damage < 0) {
					damage = npc.getCurrentHitPoints();
				}
				if (npc.getCurrentHitPoints() - damage <= 0 && damage2 > 0) {
					damage2 = 0;
				}
				// chinchompa
				boolean chinchompa = attacker.getWieldedWeapon() == 10033 || attacker.getWieldedWeapon() == 10034 || attacker.getWieldedWeapon() == 11959;

				if (chinchompa) {
					npc.gfx100(157);
				}

				// Blowpipe
				if (attacker.getWieldedWeapon() == 12926 && Misc.hasOneOutOf(4)) {
					if (Venom.ENABLE_VENOM) {
						applyVenomOnNpc(attacker, npc);
					} else {
						applyPoisonOnNpc(attacker, npc, 10);
					}
				}
				RangedAmmoUsed.dropAmmo(attacker, npc.getX(), npc.getY(), npc.getHeight());
				npc.underAttack = true;
				CombatNpc.applyHitSplatOnNpc(attacker, npc, damage, ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.RANGED_ICON, 1);
				if (defenderStrategy != null) {
					defenderStrategy.onDamageTaken(attacker, npc, damage, ServerConstants.RANGED_ICON);
				}
				if (attacker.isMagicBowSpecialAttack() || attacker.isUsingDarkBowSpecialAttack() || attacker.isUsingDarkBowNormalAttack()) {
					CombatNpc.applyHitSplatOnNpc(attacker, npc, damage2, ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.RANGED_ICON, 2);
					if (defenderStrategy != null) {
						defenderStrategy.onDamageTaken(attacker, npc, damage, ServerConstants.RANGED_ICON);
					}
				} else if (chinchompa) {
					int affected = 0;

					for (Npc local : attacker.getLocalNpcs()) {
						if (local == null) {
							continue;
						}
						if (local == npc) {
							continue;
						}
						if (local.distanceTo(npc.getX(), npc.getY()) <= 1) {
							calculateRangedDamageOnNpc(attacker, local, 1);

							CombatNpc.applyHitSplatOnNpc(attacker, local, attacker.rangedFirstDamage, ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.RANGED_ICON, 1);

							local.gfx0(157);
						}
						if (++affected > 9) {
							break;
						}
					}
				}
				attacker.killingNpcIndex = attacker.getOldNpcIndex();
				
				
			} 
			/*
			 * Magic damage
			 */
			else if (attacker.getProjectileStage() > 0) { // magic hit damage
				// Toxic staff of the dead.
				if ((attacker.getWieldedWeapon() == 12904 || attacker.getWieldedWeapon() == 16209 || attacker.getWieldedWeapon() == 16272) && Misc.hasPercentageChance(25)) {
					if (Venom.ENABLE_VENOM) {
						applyVenomOnNpc(attacker, npc);
					} else {
						applyPoisonOnNpc(attacker, npc, 10);
					}
				}
				int damage = attacker.getMagicDamage();
				if (Area.inMulti(npc.getX(), npc.getY()) && multiMagicSpell(attacker) && damage > 0) {
					attacker.barrageCount = 0;
					for (int j = 0; j < NpcHandler.npcs.length; j++) {
						if (NpcHandler.npcs[j] != null) {
							if (attacker.barrageCount >= 9) {
								break;
							}
							int nX = NpcHandler.npcs[j].getX(), nY = NpcHandler.npcs[j].getY(), pX = npc.getX(), pY = npc.getY();
							if ((nX - pX == -1 || nX - pX == 0 || nX - pX == 1) && (nY - pY == -1 || nY - pY == 0 || nY - pY == 1) && npc.npcIndex != j) {
								appendMultiBarrageNPC(attacker, j, false);
							}
						}
					}
				}
				if (npc.getCurrentHitPoints() - damage < 0) {
					damage = npc.getCurrentHitPoints();
				}
				boolean magicFailed = attacker.isMagicSplash();
				if (Combat.getEndGfxHeight(attacker, CombatConstants.MAGIC_SPELLS[attacker.getOldSpellId()][0]) == 100 && !magicFailed) { // end GFX
					npc.gfx100(CombatConstants.MAGIC_SPELLS[attacker.getOldSpellId()][5]);
				} else if (!magicFailed) {
					npc.gfx0(CombatConstants.MAGIC_SPELLS[attacker.getOldSpellId()][5]);
				}
				if (magicFailed) {
					npc.gfx100(85);
				}
				npc.underAttack = true;
				if (CombatConstants.MAGIC_SPELLS[attacker.getOldSpellId()][6] != 0) {
					CombatNpc.applyHitSplatOnNpc(attacker, npc, damage, ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.MAGIC_ICON, 1);
					if (defenderStrategy != null) {
						defenderStrategy.onDamageTaken(attacker, npc, damage, ServerConstants.MAGIC_ICON);
					}
				} else {
					Combat.addCombatExperience(attacker, ServerConstants.RANGED_ICON, 0);
				}
				attacker.killingNpcIndex = attacker.getOldNpcIndex();
				npc.updateRequired = true;
				attacker.setUsingMagic(false);
				attacker.setOldSpellId(0);
			}

		}
		if (attacker.getBowSpecShot() <= 0) {
			attacker.setOldNpcIndex(0);
			attacker.setProjectileStage(0);
			attacker.setBowSpecShot(0);
		}
		if (attacker.getBowSpecShot() >= 2) {
			attacker.setBowSpecShot(0);
		}
		if (attacker.getBowSpecShot() == 1) {
			attacker.setBowSpecShot(0);
		}
	}


	public static void applyMeleeDamageOnNpc(Player player, Npc npc, int damageMask, int damage) {
		if (npc.getCurrentHitPoints() - damage < 0) {
			damage = npc.getCurrentHitPoints();
		}
		boolean guthansEffect = false;
		if (Combat.wearingFullGuthan(player)) {
			if (Misc.random(3) == 1) {
				guthansEffect = true;
			}
		}
		if (damage > 0 && guthansEffect) {
			player.addToHitPoints(damage);
			npc.gfx0(398);
		}
		npc.underAttack = true;
		player.killingNpcIndex = player.getNpcIdAttacking();
		switch (player.getSpecEffect()) {

			// Zamorak Godsword.
			case 2:
				if (damage > 0 && npc.canBeFrozen()) {
					npc.setFrozenLength(20000);
					npc.gfx0(369);
					player.playerAssistant.sendMessage("You have frozen your target.");
				}
				break;
		}
		CombatNpc.applyHitSplatOnNpc(player, npc, damage, ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.MELEE_ICON, damageMask);
		player.setSpecEffect(0);
	}

	public static void fireProjectileNpc(Player player) {
		if (player.getOldNpcIndex() > 0) {
			if (NpcHandler.npcs[player.getOldNpcIndex()] != null) {
				player.setProjectileStage(2);
				int pX = player.getX();
				int pY = player.getY();
				int nX = NpcHandler.npcs[player.getOldNpcIndex()].getVisualX();
				int nY = NpcHandler.npcs[player.getOldNpcIndex()].getVisualY();
				int offX = (pY - nY) * -1;
				int offY = (pX - nX) * -1;
				if (GameType.isPreEoc()) {
					// Hand cannon
					if (player.getEquippedWeapon(15241) && !player.isUsingSpecial()) {
						player.gfx0(2138);
					}
				}
				player.getPA().createPlayersProjectile(pX, pY, offX, offY, 50, Combat.getProjectileSpeed(player), Combat.getRangeProjectileGFX(player),
				                                       Combat.getProjectileStartHeight(player), 31, player.getOldNpcIndex() + 1, Combat.getStartDelay(player),
				                                       Combat.getProjectileSlope(player));
				if (Combat.usingDbow(player)) {
					player.getPA().createPlayersProjectile2(pX, pY, offX, offY, 50, Combat.getProjectileSpeed(player) - 20, Combat.getRangeProjectileGFX(player), 43, 31,
					                                        player.getOldNpcIndex() + 1, Combat.getStartDelay(player), Combat.getProjectileSlope(player), player.getHeight());
				}
			}
		}
	}

	/**
	 * Create hitsplat on NPC.
	 */
	public static void applyHitSplatOnNpc(Player player, Npc npc, int damage, int mask, int icon, int hitsplatIndex) {
		if (npc.getCurrentHitPoints() == 0) {
			return;
		}

		npc = KrakenCombat.disturbWhirpool(npc);
		npc = KrakenCombat.disturbMasterWhirlpool(player, npc);
		if (npc.npcType == 496 && npc.transformId == -1) {
			damage = 0;
		}
		if (VetionCombat.isInvincibleVetion(npc)) {
			damage = 0;
		}

		npc.attackStyleDamagedBy = icon;

		EntityCombatStrategy defenderStrategy = npc.getCombatStrategyOrNull();

		if (defenderStrategy != null && player != null) {
			int customDamage = defenderStrategy.calculateCustomDamageTaken(player, npc, damage, icon);

			if (customDamage != -1) {
				damage = customDamage;
			}
		}

		if (damage > npc.getCurrentHitPoints()) {
			damage = npc.getCurrentHitPoints();
		}
		// Max hit dummy
		if (npc.npcType != 1337) {
			npc.setCurrentHitPoints(npc.getCurrentHitPoints() - damage);
		}
		if (player != null) {
			npc.playerTotalDamage[player.getPlayerId()] += damage;
			npc.playerNameTotalDamage[player.getPlayerId()] = player.getPlayerName();
			Zombie.zombieDamaged(player, npc, damage);
			if (player.getDragonClawsSpecialAttack()) {
				// Max hit dummy
				if (npc.npcType != 1337) {
					SpecialAttackTracker.storeDragonClawsDamage(player, hitsplatIndex == 1 ? damage : -1, hitsplatIndex == 2 ? damage : -1, hitsplatIndex == 3 ? damage : -1,
					                                            hitsplatIndex == 4 ? damage : -1);
					if (hitsplatIndex == 4) {
						SpecialAttackTracker.saveDragonClawsMaximumDamage(player, true);
					}
				}
			} else {
				// Max hit dummy
				if (npc.npcType != 1337) {
					if (hitsplatIndex == 1) {
						SpecialAttackTracker.saveMaximumDamage(player, damage, "FIRST", true);
					} else {
						SpecialAttackTracker.saveMaximumDamage(player, damage, "SECOND", true);
					}
				}
			}

			boolean maxHit = false;
			if (player.maximumDamageMelee < 4) {
				player.maximumDamageMelee = 4;
			}
			if (player.maximumDamageRanged < 4) {
				player.maximumDamageRanged = 4;
			}
			if (player.getMaximumDamageMagic() < 4) {
				player.setMaximumDamageMagic(4);
			}
			switch (icon) {
				case 0:
					int damageMelee = player.maximumDamageMelee;
					if (player.isGraniteMaulSpecial) {
						damageMelee = player.graniteMaulSpecialCriticalDamage;
					}
					maxHit = damage >= damageMelee * 0.96;
					break;
				case 1:
					maxHit = damage >= player.maximumDamageRanged * 0.96;
					break;
				case 2:
					maxHit = damage >= player.getMaximumDamageMagic() * 0.96;
					break;
			}
			if (maxHit) {
				mask = 1;
			}
		}
		if (npc.getCurrentHitPoints() == 0) {
			npc.setDead(true);
			VetionCombat.vetionDeath(npc);
		}
		if (!npc.hitUpdateRequired) {
			npc.hitDiff = damage;
			npc.hitUpdateRequired = true;
			npc.updateRequired = true;
			npc.hitIcon = icon;
			npc.hitMask = mask;
		} else {
			npc.hitDiff2 = damage;
			npc.hitUpdateRequired2 = true;
			npc.updateRequired = true;
			npc.hitIcon2 = icon;
			npc.hitMask2 = mask;
		}
	}

	public static void multiSpellEffectNPC(Player player, int npcId, int damage) {
		switch (CombatConstants.MAGIC_SPELLS[player.getOldSpellId()][0]) {
			case 12919:
				// blood spells
			case 12929:
				int heal = damage / 4;
				player.addToHitPoints(heal);
				break;
			case 12891:
			case 12881:
				if (NpcHandler.npcs[npcId].canBeFrozen()) {
					NpcHandler.npcs[npcId].setFrozenLength(Combat.getFreezeTime(player));
				}
				break;
		}
	}

	public static boolean checkMultiBarrageReqsNPC(int i) {
		if (NpcHandler.npcs[i] == null)
			return false;
		return true;
	}

	public static void handleDfsNPC(Player player) {
		if (player.getNpcIdAttacking() > 0) {
			Npc npc = NpcHandler.npcs[player.getNpcIdAttacking()];
			if (npc == null) {
				return;
			}

			if (!player.playerAssistant.withInDistance(player.getX(), player.getY(), npc.getX(), npc.getY(), NpcDefinition.getDefinitions()[npc.npcType].size)) {
				return;
			}
			if (System.currentTimeMillis() - player.dfsDelay > 120000) {
				if (player.getNpcIdAttacking() > 0 && npc != null) {
					OverlayTimer.sendOverlayTimer(player, ServerConstants.OVERLAY_TIMER_SPRITE_DRAGON_FIRE_SHIELD_SPECIAL, 120);
					final int damage = Misc.random(25);
					player.startAnimation(6696);
					player.gfx0(1165);
					applyHitSplatOnNpc(player, npc, damage, ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.NO_ICON, 1);
					final int pX = player.getX();
					final int pY = player.getY();
					int oX = npc.getX();
					int oY = npc.getY();
					final int offX = (pY - oY) * -1;
					final int offY = (pX - oX) * -1;
					player.getPA().createPlayersProjectile2(pX, pY, offX, offY, 50, 75, 1166, 37, 37, -player.getOldNpcIndex() - 1, 53, 16, player.getHeight());
					if (npc.isDead() == true) {
						return;
					}
					player.dfsDelay = System.currentTimeMillis();
				} else {
					player.playerAssistant.sendMessage("I should be in combat before using this.");
				}
			} else {
				player.playerAssistant.sendMessage("My shield hasn't finished recharging yet.");
			}
		}
	}

	public static int getBonusAttack(Player player, Npc npc) {
		switch (npc.npcType) {
			case 2267:
				return Misc.random(50) + 30;
			case 2026:
			case 2027:
			case 2029:
			case 2030:
				return Misc.random(50) + 30;
		}
		return 0;
	}

	public static boolean isArmadylNpc(int i) {
		switch (NpcHandler.npcs[i].npcType) {
			case 3162:
			case 3163:
			case 3164:
			case 3165:
				return true;
		}
		return false;
	}
}
