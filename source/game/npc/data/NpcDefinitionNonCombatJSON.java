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
public class NpcDefinitionNonCombatJSON extends JSONLoader {

	public NpcDefinitionNonCombatJSON() {
		super(ServerConstants.getOsrsGlobalDataLocation() + "npc/npc_definition_non_combat.json");
		load();
	}

	@Override
	public void load(JsonObject reader, Gson builder) {
		String name = Objects.requireNonNull(reader.get("npc_name").getAsString());
		int npcType = reader.get("npc_type").getAsInt();
		int roamDistance = reader.get("roam_distance").getAsInt();
		int respawnTimer = 0;
		try {
			respawnTimer = reader.get("respawn_timer").getAsInt();
		} catch (Exception e) {
		}
		NpcDefinition.DEFINITIONS[npcType] = new NpcDefinition(npcType, name, 1, roamDistance, 0, false, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, respawnTimer, 0, 0, 0, 0, 0, false);
	}
}
