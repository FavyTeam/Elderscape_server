package game.npc.data;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import core.ServerConstants;
import utility.JSONLoader;

import java.util.Objects;



/**
 * Load non-combat NPCs definitions though the JSON file.
 *
 * @author MGT Madness, created on 03-11-2015.
 */
public class NpcDefinitionCleverJSON extends JSONLoader {

	public static int amount = 0;

	public NpcDefinitionCleverJSON() {
		super(ServerConstants.getOsrsGlobalDataLocation() + "npc/npc_definition_clever.json");
		load();
	}

	@Override
	public void load(JsonObject reader, Gson builder) {
		String name = Objects.requireNonNull(reader.get("npc_name").getAsString());
		int npcType = reader.get("npc_type").getAsInt();
		int roamDistance = reader.get("roam_distance").getAsInt();
		int size = reader.get("size").getAsInt();
		int deathDeleteTicks = reader.get("death_delete_ticks").getAsInt();
		boolean aggressive = reader.get("aggressive").getAsBoolean();
		int blockAnimation = reader.get("block_animation").getAsInt();
		int deathAnimation = reader.get("death_animation").getAsInt();
		int attackSpeed = reader.get("attack_speed").getAsInt();
		int attackDistance = reader.get("attack_distance").getAsInt();
		int hitPoints = reader.get("hitpoints").getAsInt();
		int attack = reader.get("attack").getAsInt();
		int meleeDefence = reader.get("melee_defence").getAsInt();
		int rangedDefence = reader.get("ranged_defence").getAsInt();
		int magicDefence = reader.get("magic_defence").getAsInt();
		int meleeMaximumDamage = reader.get("melee_maximum_damage").getAsInt();
		int rangedMaximumDamage = reader.get("ranged_maximum_damage").getAsInt();
		int magicMaximumDamage = reader.get("magic_maximum_damage").getAsInt();
		int respawnTicks = reader.get("respawn_ticks").getAsInt();
		int meleeAttackAnimation = reader.get("melee_attack_animation").getAsInt();
		int rangedAttackAnimation = reader.get("ranged_attack_animation").getAsInt();
		int magicAttackAnimation = reader.get("magic_attack_animation").getAsInt();
		int meleeHitsplatDelay = reader.get("melee_hitsplat_delay").getAsInt();
		int rangedHitsplatDelay = reader.get("ranged_hitsplat_delay").getAsInt();
		int magicHitsplatDelay = reader.get("magic_hitsplat_delay").getAsInt();
		NpcDefinition.DEFINITIONS[npcType] = new NpcDefinition(npcType, name, size, roamDistance, deathDeleteTicks, aggressive, meleeAttackAnimation, blockAnimation,
		                                                       deathAnimation, attackSpeed, attackDistance, hitPoints, attack, meleeDefence, rangedDefence, magicDefence,
		                                                       meleeMaximumDamage, rangedMaximumDamage, magicMaximumDamage, respawnTicks, rangedAttackAnimation,
		                                                       magicAttackAnimation, meleeHitsplatDelay, rangedHitsplatDelay, magicHitsplatDelay, true);
	}
}
