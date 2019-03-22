package game.object.clip;

import core.ServerConstants;

public final class ObjectDefinitionServer {
	private static ByteStreamExt stream;

	public static int[] streamIndices;

	public static ObjectDefinitionServer objectDef;

	private static int objects = 0;

	public static int getObjects() {
		return objects;
	}

	public static ObjectDefinitionServer getObjectDef(int objectId) {
		if (objectId > streamIndices.length) {
			objectId = streamIndices.length - 1;
		}

		for (int j = 0; j < 20; j++) {
			if (cache[j].type == objectId) {
				return cache[j];
			}
		}

		cacheIndex = (cacheIndex + 1) % 20;
		objectDef = cache[cacheIndex];

		if (objectId > streamIndices.length - 1 || objectId < 0) {
			return null;
		}

		stream.currentOffset = streamIndices[objectId];

		objectDef.type = objectId;
		objectDef.setDefaults();
		objectDef.readValues(stream);

		switch (objectId) {

			// Snow piles
			case 19030:
			case 19031:
			case 19033:
			case 19034:
			case 19035:
				objectDef.objectSizeX = 3;
				objectDef.objectSizeY = 3;
				break;
		}

		return objectDef;
	}

	@SuppressWarnings("unused")
	private boolean castsShadow;

	@SuppressWarnings("unused")
	private int scaleX;

	@SuppressWarnings("unused")
	private int scaleY;

	@SuppressWarnings("unused")
	private int scaleZ;

	@SuppressWarnings("unused")
	private int offsetX;

	@SuppressWarnings("unused")
	private int offsetY;

	@SuppressWarnings("unused")
	private int offsetZ;

	@SuppressWarnings("unused")
	private int icon;


	private void setDefaults() {
		models = null;
		modelTypes = null;
		name = null;
		description = null;
		modifiedModelColors = null;
		originalModelColors = null;
		objectSizeX = 1;
		objectSizeY = 1;
		blocksWalk = true;
		blocksProjectiles = true;
		hasActions = false;
		aBoolean762 = false;
		aBoolean764 = false;
		anInt781 = -1;
		anInt775 = 16;
		actions = null;
		anInt746 = -1;
		mapscene = -1;
		aBoolean779 = true;
		rotationFlags = 0;
		isDecoration = false;
		ghost = false;
		holdsItemPiles = -1;
		varbit = -1;
		setting = -1;
		childrenIDs = null;
	}

	public static void loadConfig() {
		stream = new ByteStreamExt(getBuffer("loc.dat"));
		ByteStreamExt stream = new ByteStreamExt(getBuffer("loc.idx"));
		objects = stream.readUnsignedWord();
		streamIndices = new int[objects];
		int i = 2;
		for (int j = 0; j < objects; j++) {
			streamIndices[j] = i;
			i += stream.readUnsignedWord();
		}
		cache = new ObjectDefinitionServer[20];
		for (int k = 0; k < 20; k++) {
			cache[k] = new ObjectDefinitionServer();
		}
	}

	public static byte[] getBuffer(String s) {
		try {
			java.io.File f = new java.io.File(ServerConstants.getOsrsGlobalDataLocation() + "world osrs/object/" + s);
			if (!f.exists()) {
				return null;
			}
			byte[] buffer = new byte[(int) f.length()];
			java.io.DataInputStream dis = new java.io.DataInputStream(new java.io.FileInputStream(f));
			dis.readFully(buffer);
			dis.close();
			return buffer;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void readValues(ByteStreamExt stream) {

		int flag = -1;
		do {
			int type = stream.readUnsignedByte();
			if (type == 0)
				break;
			if (type == 1) {
				int len = stream.readUnsignedByte();
				if (len > 0) {
					if (models == null || lowMem) {
						modelTypes = new int[len];
						models = new int[len];
						for (int k1 = 0; k1 < len; k1++) {
							models[k1] = stream.readUnsignedWord();
							modelTypes[k1] = stream.readUnsignedByte();
						}
					} else {
						stream.currentOffset += len * 3;
					}
				}
			} else if (type == 2)
				name = stream.readString();
			else if (type == 3)
				description = stream.readBytes();
			else if (type == 5) {
				int len = stream.readUnsignedByte();
				if (len > 0) {
					if (models == null || lowMem) {
						modelTypes = null;
						models = new int[len];
						for (int l1 = 0; l1 < len; l1++)
							models[l1] = stream.readUnsignedWord();
					} else {
						stream.currentOffset += len * 2;
					}
				}
			} else if (type == 14)
				objectSizeX = stream.readUnsignedByte();
			else if (type == 15)
				objectSizeY = stream.readUnsignedByte();
			else if (type == 17)
				blocksWalk = false;
			else if (type == 18)
				blocksProjectiles = false;
			else if (type == 19)
				hasActions = (stream.readUnsignedByte() == 1);
			else if (type == 21)
				aBoolean762 = true;
			else if (type == 22) {
			} else if (type == 23)
				aBoolean764 = true;
			else if (type == 24) {
				anInt781 = stream.readUnsignedWord();
				if (anInt781 == 65535)
					anInt781 = -1;
			} else if (type == 28)
				anInt775 = stream.readUnsignedByte();
			else if (type == 29)
				stream.readSignedByte();
			else if (type == 39)
				stream.readSignedByte();
			else if (type >= 30 && type < 39) {
				if (actions == null)
					actions = new String[5];
				actions[type - 30] = stream.readString();
				if (actions[type - 30].equalsIgnoreCase("hidden"))
					actions[type - 30] = null;
			} else if (type == 40) {
				int i1 = stream.readUnsignedByte();
				modifiedModelColors = new int[i1];
				originalModelColors = new int[i1];
				for (int i2 = 0; i2 < i1; i2++) {
					modifiedModelColors[i2] = stream.readUnsignedWord();
					originalModelColors[i2] = stream.readUnsignedWord();
				}

			} else if (type == 41) {
				int j2 = stream.readUnsignedByte();
				modifiedTexture = new short[j2];
				originalTexture = new short[j2];
				for (int k = 0; k < j2; k++) {
					modifiedTexture[k] = (short) stream.readUnsignedWord();
					originalTexture[k] = (short) stream.readUnsignedWord();
				}

			} else if (type == 82) {
				icon = stream.readUnsignedWord();
			} else if (type == 62) {
				rotateLeft = true;
			} else if (type == 64) {
				castsShadow = true;
			} else if (type == 65)
				scaleX = stream.readUnsignedWord();
			else if (type == 66)
				scaleY = stream.readUnsignedWord();
			else if (type == 67)
				scaleZ = stream.readUnsignedWord();
			else if (type == 68)
				mapscene = stream.readUnsignedWord();
			else if (type == 69)
				rotationFlags = stream.readUnsignedByte();
			else if (type == 70)
				offsetX = stream.readSignedWord();
			else if (type == 71)
				offsetY = stream.readSignedWord();
			else if (type == 72)
				offsetZ = stream.readSignedWord();
			else if (type == 73)
				isDecoration = true;
			else if (type == 74)
				ghost = true;
			else if (type == 75)
				holdsItemPiles = stream.readUnsignedByte();
			else if (type == 77) {
				varbit = stream.readUnsignedWord();
				if (varbit == 65535)
					varbit = -1;
				setting = stream.readUnsignedWord();
				if (setting == 65535)
					setting = -1;
				int j1 = stream.readUnsignedByte();
				childrenIDs = new int[j1 + 1];
				for (int j2 = 0; j2 <= j1; j2++) {
					childrenIDs[j2] = stream.readUnsignedWord();
					if (childrenIDs[j2] == 65535)
						childrenIDs[j2] = -1;
				}
			}
		}
		while (true);
		if (flag == -1 && name != "null" && name != null) {
			hasActions = models != null && (modelTypes == null || modelTypes[0] == 10);
			if (actions != null)
				hasActions = true;
		}
		if (ghost) {
			blocksWalk = false;
			blocksProjectiles = false;
		}
		if (holdsItemPiles == -1)
			holdsItemPiles = blocksWalk ? 1 : 0;
	}

	private ObjectDefinitionServer() {
		type = -1;
	}

	public boolean hasActions() {
		return hasActions || actions != null;
	}

	public boolean hasName() {
		return name != null && name.length() > 1;
	}

	public int xLength() {
		return objectSizeX;
	}

	public int yLength() {
		return objectSizeY;
	}


	public boolean blocksWalk() {
		return blocksWalk;
	}

	public boolean solid() {
		return aBoolean779;
	}

	public boolean isDecoration;

	public String name;

	public int objectSizeX;

	public int anInt746;

	int[] originalModelColors;

	public int setting;

	public static boolean lowMem;

	public int type;

	public boolean blocksProjectiles;

	public int mapscene;

	public int childrenIDs[];

	private int holdsItemPiles;

	public int objectSizeY;

	public boolean aBoolean762;

	public boolean aBoolean764;

	private boolean ghost;

	public boolean blocksWalk;

	public int rotationFlags;

	private static int cacheIndex;

	@SuppressWarnings("unused")
	private boolean rotateLeft;

	int[] models;

	public int varbit;

	public int anInt775;

	int[] modelTypes;

	public byte description[];

	public boolean hasActions;

	public boolean aBoolean779;

	public int anInt781;

	private static ObjectDefinitionServer[] cache;

	int[] modifiedModelColors;

	public String actions[];

	private short[] originalTexture;

	private short[] modifiedTexture;
}
