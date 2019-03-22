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
public class NpcDefinitionCombatJSON extends JSONLoader {

	public NpcDefinitionCombatJSON() {
		super(ServerConstants.getOsrsGlobalDataLocation() + "npc/npc_definition_combat.json");
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
		int attackAnimation = reader.get("attack_animation").getAsInt();
		int blockAnimation = reader.get("block_animation").getAsInt();
		int deathAnimation = reader.get("death_animation").getAsInt();
		int attackSpeed = reader.get("attack_speed").getAsInt();
		int attackDistance = reader.get("attack_distance").getAsInt();
		int hitPoints = reader.get("hitpoints").getAsInt();
		int attack = reader.get("attack").getAsInt();
		int meleeDefence = reader.get("melee_defence").getAsInt();
		int rangedDefence = reader.get("ranged_defence").getAsInt();
		int magicDefence = reader.get("magic_defence").getAsInt();
		int maximumDamage = reader.get("maximum_damage").getAsInt();
		int rangedAttackAnimation = !reader.has("ranged_attack_animation") ? 0 : reader.get("ranged_attack_animation").getAsInt();
		int magicAttackAnimation = !reader.has("magic_attack_animation") ? 0 : reader.get("magic_attack_animation").getAsInt();

		int respawnTicks = 70;
		NpcDefinition.DEFINITIONS[npcType] = new NpcDefinition(npcType, name, size, roamDistance, deathDeleteTicks, aggressive, attackAnimation, blockAnimation, deathAnimation,
		                                                       attackSpeed, attackDistance, hitPoints, attack, meleeDefence, rangedDefence, magicDefence, maximumDamage,
		                                                       maximumDamage, maximumDamage, respawnTicks, rangedAttackAnimation, magicAttackAnimation, 0, 0, 0, false);
	}
}
