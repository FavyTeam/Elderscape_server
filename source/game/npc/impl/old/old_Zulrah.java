package game.npc.impl.old;

/**
 * Created by Owain on 18/08/2017.
 */
public class old_Zulrah {

	/**
	 * The minion snake npc id
	 */
	/*public static final int SNAKELING = 2045;
	
	private static int[] DROP_LIST_RARE = {12936, 12922, 12932, 12927, 6571
	
	};
	
	public static int RARE_DROP() {
	return DROP_LIST_RARE[(int)(Math.random()*DROP_LIST_RARE.length - 1)];
	}
	
	public final static void dropLoot(Player c) {
	final int scales = Misc.random(3500);
	final int logs = Misc.random(200);
	final int coins = Misc.random(5000);
	final int food = Misc.random(20);
	if (Misc.random(100) >= 0 && Misc.random(100) <= 25) {
	    ItemAssistant.createGroundItem(c, 1514, 2267, 3069, logs);
	    ItemAssistant.createGroundItem(c, 12934, 2267, 3069, scales);
	} else
	if (Misc.random(100) >= 26 && Misc.random(100) <= 36) {
	    ItemAssistant.createGroundItem(c, 1516, 2267, 3069, logs);
	    ItemAssistant.createGroundItem(c, 12934, 2267, 3069, scales);
	} else
	if (Misc.random(100) >= 37 && Misc.random(100) <= 49) {
	    ItemAssistant.createGroundItem(c, 13307, 2267, 3069, coins);
	    ItemAssistant.createGroundItem(c, 12934, 2267, 3069, scales);
	} else
	if (Misc.random(100) >= 50 && Misc.random(100) <= 97) {
	    ItemAssistant.createGroundItem(c, 11937, 2267, 3069, food);
	    ItemAssistant.createGroundItem(c, 12934, 2267, 3069, scales);
	} else
	if(Misc.random(100) >= 98 && Misc.random(100) <= 100) {
	    ItemAssistant.createGroundItem(c, RARE_DROP(), 2267, 3069, 1);
	    ItemAssistant.createGroundItem(c, 12934, 2267, 3069, scales);
	}
	}
	
	public static void yell(String msg) {
	for (int j = 0; j < PlayerHandler.players.length; j++) {
	    if (PlayerHandler.players[j] != null) {
		Player c2 = (Player) PlayerHandler.players[j];
		c2.playerAssistant.sendMessage(msg);
	    }
	}
	}
	
	public final static void RARE_MESSAGE(Player c, final int itemId) {
	//yell("@cr10@[@blu@DROP@bla@] @bla@" + c.playerName + " received a @blu@RARE @bla@drop: @blu@1x "
	//+ ItemDefinition.forId(itemId).getName());
	}
	
	
	
	/**
	 * The relative lock for this event
	 */
	//private final Object EVENT_LOCK = new Object();

	/**
	 * The player associated with this event
	 */
	//private final Player player;

	/**
	 * The single instance of oldZulrah
	 */
	//private SingleInstancedArea zulrahInstance;

	/**
	 * The boundary of oldZulrah's location
	 */
	//public static final Boundary BOUNDARY = new Boundary(2248, 3059, 2283, 3084);

	/**
	 * The oldZulrah npc
	 */
	//private Npc npc;

	/**
	 * The current stage of oldZulrah
	 */
	//private int stage;

	/**
	 * Determines if the npc is transforming or not.
	 */
	//private boolean transforming;

	/**
	 * The stopwatch for tracking when the oldZulrah npc fight starts.
	 */
	//private Stopwatch stopwatch = Stopwatch.createUnstarted();

	/**
	 * A mapping of all the stages
	 */
	//private Map<Integer, old_ZulrahStage> stages = new HashMap<>();

	/**
	 * Creates a new old_Zulrah event for the player
	 * @param player    the player
	 */
	/*public old_Zulrah(Player player) {
	this.player = player;
	stages.put(0, new SpawnZulrahStageZero(this, player));
	stages.put(1, new CreateToxicStageOne(this, player));
	stages.put(2, new MeleeStageTwo(this, player));
	stages.put(3, new MageStageThree(this, player));
	stages.put(4, new RangeStageFour(this, player));
	stages.put(5, new MageStageFive(this, player));
	stages.put(6, new MeleeStageSix(this, player));
	stages.put(7, new RangeStageSeven(this, player));
	stages.put(8, new MageStageEight(this, player));
	stages.put(9, new RangeStageNine(this, player));
	stages.put(10, new MeleeStageTen(this, player));
	stages.put(11, new RangeStageEleven(this, player));
	}
	
	public void initialize() {
	if (zulrahInstance != null) {
	    InstancedAreaManager.getSingleton().disposeOf(zulrahInstance);
	}
	int height = InstancedAreaManager.getSingleton().getNextOpenHeight(BOUNDARY);
	zulrahInstance = new SingleInstancedZulrah(player, BOUNDARY, height);
	InstancedAreaManager.getSingleton().add(height, zulrahInstance);
	if (zulrahInstance == null) {
	    player.playerAssistant.sendMessage("An error occured while trying to enter old_Zulrah's shrine. Please try again.");
	    return;
	}
	//player.sendMessage("DEBUG: (instance height = " + zulrahInstance.getHeight() + ")");
	stage = 0;
	stopwatch = Stopwatch.createStarted();
	player.getPA().closeInterfaces();
	player.getPA().sendScreenFade("Welcome to old_Zulrah's shrine", 1, 5);
	CycleEventHandler.getSingleton().addEvent(player, stages.get(0), 1);
	}
	
	/**
	 * Determines if the player is standing in a toxic location
	 * @return	true of the player is in a toxic location
	 */
	/*public boolean isInToxicLocation() {
	for (int x = player.getX() - 1; x < player.getX() + 1; x++) {
	    for (int y = player.getY() - 1; y < player.getY() + 1; y++) {
		if (ObjectManagerServer.exists(11700, x, y, player.getHeight())) {
		    return true;
		}
	    }
	}
	return false;
	}
	
	/**
	 * Dispose event
	 */

	/*	public void DISPOSE_EVENT() {
			CycleEventHandler.getSingleton().stopEvents(EVENT_LOCK);
			zulrahInstance.onDispose();
			InstancedAreaManager.getSingleton().disposeOf(zulrahInstance);
			stopwatch.stop();
			zulrahInstance = null;
		}*/

	/*public void DISPOSE_EVENT() {
	CycleEventHandler.getSingleton().stopEvents(EVENT_LOCK);
	stopwatch.stop();
	zulrahInstance.onDispose();
	InstancedAreaManager.getSingleton().disposeOf(zulrahInstance);
	zulrahInstance = null;
	}
	
	/**
	 * Stops the oldZulrah instance and concludes the events
	 */
	/*public void stop() {
	CycleEventHandler.getSingleton().stopEvents(EVENT_LOCK);
	if (stage < 1) {
	    return;
	}
	stopwatch.stop();
	long time = stopwatch.elapsed(TimeUnit.MILLISECONDS);
	long best = player.getBestZulrahTime();
	String duration = best < (60_000 * 60) ? Misc.toFormattedMS(time) : Misc.toFormattedHMS(time);
	player.sendMessage("Time elapsed: " + duration + "</col> "+(time < player.getBestZulrahTime() ? "(New personal best)" : "")+".");
	if (time < player.getBestZulrahTime()) {
	    player.setBestZulrahTime(time);
	}
	SerializablePair<String, Long> globalBest = Server.getServerData().getZulrahTime();
	if (globalBest.getFirst() == null || globalBest.getSecond() == null || time < globalBest.getSecond() && globalBest.getSecond() != 0) {
	    PlayerHandler.executeGlobalMessage("<img=10>[<col=255>old_Zulrah</col>] A new record has been set! <col=255>" + Misc.capitalize(player.playerName)
			    + "</col> just killed old_Zulrah in <col=255> "+duration+"</col>.");
	    if (globalBest.getFirst() != null && globalBest.getSecond() != null) {
		PlayerHandler.executeGlobalMessage("<img=10></img>[<col=255>old_Zulrah</col>] The old record was set by: <col=255>" + globalBest.getFirst()
				+ "</col>, with a time of: <col=255>"+Misc.toFormattedMS(globalBest.getSecond())+"</col>.");
	    }
	    Server.getServerData().setSerializablePair(new SerializablePair<>(player.playerName, time));
	}
	zulrahInstance.onDispose();
	InstancedAreaManager.getSingleton().disposeOf(zulrahInstance);
	zulrahInstance = null;
	}
	
	public void changeStage(int stage, CombatType combatType, ZulrahLocation location) {
	this.stage = stage;
	CycleEventHandler.getSingleton().stopEvents(EVENT_LOCK);
	CycleEventHandler.getSingleton().addEvent(EVENT_LOCK, stages.get(stage), 1);
	if (stage == 1) {
	    return;
	}
	int type = combatType == CombatType.MELEE ? 2043 : combatType == CombatType.MAGE ? 2044 : 2042;
	npc.animation(5072);
	npc.attackTimer = 8;
	transforming = true;
	player.getCombat().resetPlayerAttack();
	CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
	
	    @Override
	    public void execute(CycleEventContainer container) {
		if (container.getTotalTicks() == 2) {
		    npc.requestTransform(6720);
		} else if (container.getTotalTicks() == 3) {
		    npc.absX = location.getLocation().x;
		    npc.absY = location.getLocation().y;
		    player.rebuildNPCList = true;
		} else if (container.getTotalTicks() == 5) {
		    npc.requestTransform(type);
		    npc.animation(5071);
		    npc.face(player);
		    transforming = false;
		    container.stop();
		}
	    }
	
	}, 1);
	}
	
	/**
	 * The {@link SingleInstancedArea} object for this class
	 * @return	the oldZulrah instance
	 */
	/*public InstancedArea getInstancedZulrah() {
	return zulrahInstance;
	}
	
	/**
	 * The reference to oldZulrah, the npc
	 * @return	the reference to oldZulrah
	 */
	/*public Npc getNpc() {
	return npc;
	}
	
	/**
	 * The instance of the old_Zulrah {@link Npc}
	 * @param npc	the oldZulrah npc
	 */
	/*public void setNpc(Npc npc) {
	this.npc = npc;
	}
	
	/**
	 * The stage of the oldZulrah event
	 * @return	the stage
	 */
	/*public int getStage() {
	return stage;
	}
	
	/**
	 * Determines if the NPC is transforming or not
	 * @return	{@code true} if the npc is in a transformation stage
	 */
	/*public boolean isTransforming() {
	return transforming;
	}*/

}
