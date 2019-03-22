package game.npc.data;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import core.ServerConstants;
import game.npc.NpcHandler;
import utility.JSONLoader;

import java.util.Objects;


/**
 * Load NPCSpawnJSON data.
 *
 * @author MGT Madness, created on 03-11-2015.
 */
public class NpcSpawnBossJSON extends JSONLoader {

	public NpcSpawnBossJSON() {
		super(ServerConstants.getOsrsGlobalDataLocation() + "npc/npc_spawn_boss.json");
		load();
	}

	@Override
	public void load(JsonObject reader, Gson builder) {
		int npcType = reader.get("npc_type").getAsInt();
		int x = reader.get("x").getAsInt();
		int y = reader.get("y").getAsInt();
		int height = reader.get("height").getAsInt();
		String faceAction = Objects.requireNonNull(reader.get("face_action").getAsString());
		NpcHandler.spawnDefaultNpc(npcType, NpcDefinition.getDefinitions()[npcType].name, x, y, height, faceAction, 0, -1);
	}
}
