package game.npc.data;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import core.ServerConstants;
import game.npc.NpcHandler;
import utility.JSONLoader;
import utility.Misc;

import java.util.Objects;


/**
 * Load NPCSpawnJSON data.
 *
 * @author MGT Madness, created on 03-11-2015.
 */
public class NpcSpawnCombatJSON extends JSONLoader {

	public NpcSpawnCombatJSON() {
		super(ServerConstants.getOsrsGlobalDataLocation() + "npc/npc_spawn_combat.json");
		load();
	}

	@Override
	public void load(JsonObject reader, Gson builder) {
		int npcType = reader.get("npc_type").getAsInt();
		int x = reader.get("x").getAsInt();
		int y = reader.get("y").getAsInt();
		int height = reader.get("height").getAsInt();
		String faceAction = Objects.requireNonNull(reader.get("face_action").getAsString());
		if (NpcDefinition.getDefinitions()[npcType] == null) {
			Misc.print("Npc is not defined2: " + npcType);
			return;
		}
		NpcHandler.spawnDefaultNpc(npcType, NpcDefinition.getDefinitions()[npcType].name, x, y, height, faceAction, 0, -1);
	}
}
