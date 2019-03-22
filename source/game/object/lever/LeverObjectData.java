package game.object.lever;

import java.util.ArrayList;
import java.util.List;

public class LeverObjectData {
	public static List<LeverObjectData> leverObjectData = new ArrayList<LeverObjectData>();


	public int id;

	public int x;

	public int y;

	public int face;

	public int type;



	public LeverObjectData(int id, int x, int y, int face, int type) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.face = face;
		this.type = type;
	}
}
