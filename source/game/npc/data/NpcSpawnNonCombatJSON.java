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
public class NpcSpawnNonCombatJSON extends JSONLoader {

	private static String location = ServerConstants.getDataLocation() + "npc/npc_spawn_non-combat.json";

	public NpcSpawnNonCombatJSON() {
		super(location);
		load();
	}

	@Override
	public void load(JsonObject reader, Gson builder) {
		int npcType = reader.get("npc_type").getAsInt();
		int x = reader.get("x").getAsInt();
		int y = reader.get("y").getAsInt();
		int height = reader.get("height").getAsInt();
		String faceAction = Objects.requireNonNull(reader.get("face_action").getAsString());
		int behindWallDistance = 0;
		if (NpcDefinition.getDefinitions()[npcType] == null) {
			Misc.printDontSave(location + " Npc needs to be defined first: " + npcType);
			return;
		}
		try {
			behindWallDistance = reader.get("behind_wall_distance").getAsInt();
		} catch (Exception e) {
		}
		NpcHandler.spawnDefaultNpc(npcType, NpcDefinition.getDefinitions()[npcType].name, x, y, height, faceAction, behindWallDistance, -1);
	}
}
