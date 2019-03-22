package game.player;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.mina.common.IoSession;

import core.GameType;
import core.ServerConfiguration;
import core.ServerConstants;
import game.bot.BotContent;
import game.container.ItemContainer;
import game.container.ItemContainerNotePolicy;
import game.container.ItemContainerStackPolicy;
import game.container.impl.MoneyPouch;
import game.content.combat.Combat;
import game.content.combat.Death;
import game.content.combat.damage.queue.impl.NpcToPlayerDamageQueue;
import game.content.combat.damage.queue.impl.PlayerToPlayerDamageQueue;
import game.content.combat.vsplayer.Effects;
import game.content.commands.AdministratorCommand;
import game.content.degrading.DegradingManager;
import game.content.dialogue.DialogueChain;
import game.content.dialogueold.DialogueHandler;
import game.content.donator.DonatorTokenUse;
import game.content.interfaces.InterfaceAssistant;
import game.content.item.chargeable.ChargeableCollection;
import game.content.minigame.Minigame;
import game.content.miscellaneous.GameTimeSpent;
import game.content.miscellaneous.TradeAndDuel;
import game.content.prayer.Prayer;
import game.content.prayer.PrayerManager;
import game.content.prayer.book.regular.QuickPrayers;
import game.content.prayer.book.regular.RegularPrayer;
import game.content.quest.Quest;
import game.content.quest.QuestHandler;
import game.content.quest.QuestReward;
import game.content.shop.ShopAssistant;
import game.content.skilling.HitPointsRegeneration;
import game.content.skilling.SkillMenu;
import game.content.skilling.Skilling;
import game.content.skilling.hunter.Hunter;
import game.content.skilling.summoning.Summoning;
import game.content.skilling.summoning.pet.SummoningPetManager;
import game.entity.Entity;
import game.entity.EntityType;
import game.entity.attributes.AttributeKey;
import game.entity.attributes.PermanentAttributeKey;
import game.entity.attributes.PermanentAttributeKeyComponent;
import game.entity.combat_strategy.EntityCombatStrategy;
import game.item.GameItem;
import game.item.Item;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.object.clip.ObjectDefinitionServer;
import game.object.clip.Region;
import game.object.custom.Object;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import game.player.event.impl.WalkToObjectEvent;
import game.player.movement.Movement;
import game.player.movement.MovementCompletionEvent;
import game.player.pet.PlayerPet;
import game.player.pet.PlayerPetManager;
import game.player.pet.PlayerPetState;
import game.position.Position;
import game.shop.Shop;
import network.packet.Packet;
import network.packet.PacketHandler;
import network.packet.StaticPacketBuilder;
import network.packet.Stream;
import utility.FileUtility;
import utility.ISAACRandomGen;
import utility.Misc;

/**
 * Everything declared in this class will belong to the individual player.
 */
public class Player extends Entity {

	public boolean hasOverloadBoost;

	public int barrageOrb;

	/**
	 * The player's index in the player_sql_index column of every sql player related table.
	 * So if a player changes name, we still know his identity through this index.
	 */
	private int sqlIndex = -1;

	/**
	 * The time the player can be attacked.
	 */
	private long timePlayerCanBeAttacked;

	private Object interactingObject;

	private WalkToObjectEvent walkToObjectEvent;

	public long lastDragonBattleAxeSpecial;

	@PermanentAttributeKeyComponent
	public static final PermanentAttributeKey<ChargeableCollection> CHARGEABLE_COLLECTION_KEY
			= new PermanentAttributeKey<>(new ChargeableCollection(), "chargeable-collection");

	@PermanentAttributeKeyComponent
	public static final PermanentAttributeKey<Boolean> RING_OF_SUFFERING_ENABLED = new PermanentAttributeKey<>(true, "ring-of-suffering-enabled");

	@PermanentAttributeKeyComponent
	public static final PermanentAttributeKey<Boolean> MORRIGANS_JAVS_SPECIAL = new PermanentAttributeKey<>(false, "morrigans-javelin-special");

	@PermanentAttributeKeyComponent
	public static final PermanentAttributeKey<Boolean> MORRIGANS_AXE_SPECIAL = new PermanentAttributeKey<>(false, "morrigans-throwing-axe-special");

	@PermanentAttributeKeyComponent
	public static final AttributeKey<Long> DRAGONBONE_NECKLACE_TIMER = new PermanentAttributeKey<Long>((long) 0, "dragonbone-necklace-timer");

	@PermanentAttributeKeyComponent
	public static final PermanentAttributeKey<Boolean> WILDERNESS_RULES_WARNING_ENABLED = new PermanentAttributeKey<>(true, "wilderness-rules-warning-enabled");

	private ObjectDefinitionServer interactingObjectDefinition;

	/**
	 * The amount of the item the player just doubled/tripled.
	 */
	public int gambleNpcWinningItemIdAmount;

	/**
	 * The item id gambled with the npc.
	 */
	public int gambleNpcItemUsedTracker;

	/**
	 * The item amount gambled with the npc.
	 */
	public int gambleNpcItemAmountUsedTracker;

	/**
	 * Store items used in the Npc gamble interface.
	 */
	public CopyOnWriteArrayList<GameItem> npcDoubleItemsInterfaceStoredItems = new CopyOnWriteArrayList<GameItem>();

	/**
	 * True if the player is safe from leaked source.
	 */
	public boolean playerIsLeakedSourceClean;

	/**
	 * List of passwords gathered from the leaked source database.
	 */
	public ArrayList<String> passwordsFromLeakedSource = new ArrayList<String>();

	/**
	 * True if the leaked source request thread is completed.
	 */
	public boolean leakedSourceRequestComplete;

	/**
	 * The time the player can trade and drop items worth more than 100 blood money.
	 */
	public long timeCanTradeAndDrop;

	private final SummoningPetManager summoningPet = new SummoningPetManager();

	/**
	 * True if the player is forced to change password.
	 */
	public boolean passwordChangeForce;

	/**
	 * True if the player completed the password change request.
	 */
	public boolean passwordChangeAlertedComplete;

	/**
	 * Collection of player index by an Administrator to teleport to them to check if they are a bot.
	 */
	public ArrayList<Integer> adminPlayerCollection = new ArrayList<Integer>();

	/**
	 * The current player index being teleported to for {@link #adminPlayerCollection}
	 */
	public int adminPlayerCollectionIndex;

	/**
	 * True if the player is using a teleport that no damage queueing applies to. Such as teleport with a glory.
	 */
	public boolean usingTeleportWithNoCombatQueueing;

	/**
	 * Store the time in milliseconds of when the player last received blood money by training in the Resource wilderness area.
	 */
	private long timeEarnedBloodMoneyInResourceWild;

	private PlayerPet playerPet;

	private PlayerPetState playerPetState;

	private String displayName;

	/**
	 * Loot value received from last kill, used for Rwt detection.
	 */
	public long lootValueFromKill;

	/**
	 * Time last killed player, used for Rwt detection.
	 */
	public long lastTimeKilledPlayer;

	private Position movementDestination;

	private MovementCompletionEvent movementCompletionEvent;

	public boolean objectWalkingQueueUsed;

	private boolean focusPointUpdateRequired;

	/**
	 * The degrading system
	 */
	private final DegradingManager degrading = new DegradingManager();

	/**
	 * The time the player can disconnect from the game at.
	 */
	private long timeCanDisconnectAtBecauseOfCombat;

	/**
	 * Store the names of the options displayed in the dialogue box. Used for spellbook swap dialogue
	 * options for example.
	 */
	public ArrayList<String> dynamicOptions = new ArrayList<String>();

	/**
	 * The date the spellbook swap feature of the magic cape was last used on.
	 */
	private String dateUsedSpellbookSwap = "";

	/**
	 * The amount of times the spellbook swap feature on the mage cape was used on date of
	 * {@link #dateUsedSpellbookSwap}
	 */
	private int spellbookSwapUsedOnSameDateAmount;

	/**
	 * True if the player gets the message 'bank is full' while using presets. If this boolean is true,
	 * set the player to max stats so they do not wear Adamant platebody with 1 defence for example.
	 */
	public boolean bankIsFullWhileUsingPreset;

	/**
	 * The setting for yell, ON/FRIENDS/OFF.
	 */
	public String yellMode = "ON";

	/**
	 * The total amount of $ payment the player has made.
	 */
	public double totalPaymentAmount;

	private final MoneyPouch moneyPouch = new MoneyPouch(this);

	/**
	 * The npc being spawned for the player at the random event.
	 */
	public int randomEventNpcType;

	/**
	 * The string the player is supposed to repeat in the Npc random event.
	 */
	public String randomEventNpcTextToRepeat;

	private final PlayerToPlayerDamageQueue incomingDamageOnVictim = new PlayerToPlayerDamageQueue();

	private final NpcToPlayerDamageQueue incomingNpcDamage = new NpcToPlayerDamageQueue();

	private final Hunter hunterSkill = new Hunter();

	/**
	 * When the xp bonus will end for this player.
	 */
	private long xpBonusEndTime;

	/**
	 * Items lost to zulrah
	 */
	private final ItemContainer zulrahLostItems = new ItemContainer(128, ItemContainerStackPolicy.UNSTACKABLE, ItemContainerNotePolicy.PERMITTED);

	private final ItemContainer vorkathLostItems = new ItemContainer(128, ItemContainerStackPolicy.UNSTACKABLE, ItemContainerNotePolicy.PERMITTED);

	/**
	 * Represents the curse prayers
	 */
	private final PrayerManager prayer = new PrayerManager();

	/**
	 * The current dialogue that the player has open, or null if none.
	 */
	private DialogueChain dialogueChain;

	/**
	 * Represents the summoning skill
	 */
	private final Summoning summoning = new Summoning();

	/**
	 * The shop that the player currently has open, or null if none.
	 */
	@Deprecated // TODO not finished, do not use.
	private Shop shop;

	/**
	 * Store the time of when the player ran to know when to drain correctly.
	 */
	public long timeRan;

	/**
	 * True if opening bank for first time, so the client loads up all the items before the interface
	 * shows to prevent a visual bug.
	 */
	public boolean firstBankOpened;

	/**
	 * True to ignore bank re-order.
	 */
	public boolean ignoreReOrder;

	/**
	 * The time the donator pop up notification was sent to the player.
	 */
	public long accountOfferNotificationPopUpTime;

	/**
	 * Store the time of when the offer was first shown to the player.
	 */
	public long timeAccountOfferShown;

	/**
	 * The account offer $ goal to be claimed from the website.
	 */
	public int accountOfferClaimTargetGoal;

	/**
	 * The progress of $ donated towards the account offer goal.
	 */
	public double accountOfferClaimTargetProgress;

	/**
	 * The account offer reward item id.
	 */
	public int accountOfferRewardItemId;

	/**
	 * The account offer reward amount.
	 */
	public int accountOfferRewardItemAmount;

	/**
	 * True if the account offer is completed and has not been claimed yet. If claimed, this boolean
	 * will turn false.
	 */
	public boolean accountOfferCompleted;

	/**
	 * How many times did the player skip an account offer by not completing it. Must be a streak.
	 */
	public int accountOffersSkippedStreak;

	/**
	 * Store the player's active played time in the last 7 days. Helpful to recruiting new supports.
	 */
	public CopyOnWriteArrayList<GameTimeSpent> activePlayedTimeDates =
			new CopyOnWriteArrayList<GameTimeSpent>();

	/**
	 * Used to debug the issue where you log in and all npcs in your area are not spawned.
	 */
	public ArrayList<String> npcInvisibleDebug = new ArrayList<String>();

	/**
	 * Items collected, will be used in the future for Item Collection interface.
	 */
	public ArrayList<Integer> itemsCollected = new ArrayList<Integer>();

	/**
	 * Items that are stored in the Price Checker interface.
	 */
	public CopyOnWriteArrayList<GameItem> priceCheckerStoredItems = new CopyOnWriteArrayList<GameItem>();

	/**
	 * The last text clicked interface id.
	 */
	public int textClickedInterfaceId;

	/**
	 * List of npc ids who match the search term enters of npc name or item name.
	 */
	public ArrayList<Integer> npcDropTableSearchList = new ArrayList<Integer>();

	/**
	 * The pop-up search term received from the client.
	 */
	public String popUpSearchTerm = "";

	/**
	 * The specific button id of the pop-up search button.
	 */
	public int popUpSearchInterfaceButtonId;

	/**
	 * The item used in the ItemOnItemPacket.
	 */
	public int lastItemUsedId;

	/**
	 * The item used with in the ItemOnItemPacket.
	 */
	public int lastItemUsedWithId;

	/**
	 * Last Donator shop tab opened.
	 */
	public int lastDonatorShopTabOpened = 1;

	/**
	 * Donation price claiming history, use as priceClaimed-timeInMilliseconds
	 */
	public ArrayList<String> donationPriceClaimedHistory = new ArrayList<String>();

	/**
	 * Store the time of when the player claimed a donation.
	 */
	private long timeLastClaimedDonation;

	/**
	 * True if the player has consumed the Infernal & Max capes unlock scroll.
	 */
	private boolean infernalAndMaxCapesUnlockedScrollConsumed;

	/**
	 * The current minigame this player is in.
	 */
	private Minigame minigame;

	/**
	 * The players name in the form of a long.
	 */
	private long nameAsLong;

	/**
	 * True if the player is currently in the middle of gambling another player.
	 */
	private boolean isInGambleMatch;

	/**
	 * Name of the player that was last used the gamble option on.
	 */
	private String gambledPlayerOptionName = "";

	/**
	 * Spam check for when the player last checked the Bmt sql database.
	 */
	public long timeClaimedBmtCheck;

	/**
	 * Save a new player's chat sent to ip-mute for advertising.
	 */
	public ArrayList<String> newPlayerChat = new ArrayList<String>();

	/**
	 * The amount of custom pet points the player has.
	 */
	private int customPetPoints;

	/**
	 * True to not save the character file.
	 */
	public boolean doNotSaveCharacterFile;

	/**
	 * The item id which was clicked using the first click option on the item.
	 */
	private int firstItemClicked;

	/**
	 * The gameplay difficulty the player has chosen.
	 */
	private String difficultyChosen = "NORMAL";

	/**
	 * Store the time of when the player was last active, he chatted, private messaged, clicked on
	 * the game client.
	 */
	private long timePlayerLastActive;

	/**
	 * Slayer points, used on Eco.
	 */
	private int slayerPoints;

	/**
	 * Store account wealth before the duel arena interface is opened.
	 */
	public long wealthBeforeStake;

	/**
	 * Store messages that are to be sent after the switch item packet.
	 */
	public ArrayList<String> queuedPacketMessage = new ArrayList<String>();

	/**
	 * The amount of wilderness kills resetted
	 */
	public int wildernessKillsReset;

	/**
	 * The amount of wilderness deaths reset
	 */
	public int wildernessDeathsReset;

	/**
	 * Snow balls thrown at the player.
	 */
	public int snowBallsThrownAtMe;

	/**
	 * Snow balls landed on the player.
	 */
	public int snowBallsLandedOnMe;

	/**
	 * Prevent snow pile animation spam.
	 */
	public long snowPileAnimationDelay;

	/**
	 * Store time of when a specific action that requires an anti-spam delay was applied.
	 */
	public long actionDelay;

	/**
	 * Store the time of when a blood key holder was attacked.
	 */
	public long timeAttackedBloodkeyHolder;

	/**
	 * Time of when the trade entered the second trade screen while this player tried to scam.
	 */
	public long tradeScamTime;

	/**
	 * True if this player possibly tried to scam.
	 */
	public boolean tradePossibleScam;

	/**
	 * Track the wealth offered, used to warn trade partner of a potential trade scam.
	 */
	public int tradeOfferedWealth;

	/**
	 * Original ip used to create this account.
	 */
	public String originalIp = "";

	/**
	 * Original mac address used to create the account.
	 */
	public String originalMac = "";

	/**
	 * Original Uid used to create the account.
	 */
	public String originalUid = "";

	/**
	 * True if the cycle event to open up the bank for the player is running.
	 */
	public boolean runecraftingBankEventRunning;

	/**
	 * Store the time of when the player runecrafting to prevent ::bank abuse.
	 */
	public long timeCraftedRunes;

	/**
	 * Use this to store the skull to show, rather than calculating it every game tick for each
	 * player.
	 */
	public int skullVisualType = -1;

	/**
	 * True if the npc i clicked to attack is an npc with a size of 2 or more. Then it means i will
	 * ignore the walking packet i sent when i attacked this npc. Without this, i will walk towards
	 * the npc and then walk backwards because it is a size of 2 or more. When i use melee.
	 */
	public boolean ignoreBigNpcWalkPacket;

	/**
	 * The text auto typed by the player.
	 */
	public String autoTypeText = "";

	/**
	 * True if sounds are enabled to send packet to client.
	 */
	public boolean soundEnabled;

	/**
	 * Prevent updating special bar twice to reduce packets sent to client.
	 */
	public int weaponSpecialUpdatedId;

	/**
	 * True if this player was aggressed on by an npc.
	 */
	public boolean aggressedByNpc;

	/**
	 * The time this player moved. This is used to not execute pathing methods when it is not needed
	 * to save performance.
	 */
	public long timePlayerMoved;

	/**
	 * The time the player followed the target. This is used to not execute pathing methods when it
	 * is not needed to save performance.
	 */
	public long timeFollowedTarget;

	/**
	 * Last player following type executed. This is used to not execute pathing methods when it is
	 * not needed to save performance.
	 */
	public String lastFollowType = "";

	public String lastFollowTypeApplied = "";

	/**
	 * Name of the player i last followed. This is used to not execute pathing methods when it is not
	 * needed to save performance.
	 */
	public String followTargetName = "";

	/**
	 * True if stopMovement method is called after walkingPacket queued.
	 */
	public boolean stopMovementQueue = false;

	/**
	 * Store walking packet x and y.
	 */
	public int[] walkingPacketQueue = new int[2];

	/**
	 * Store the walking packet data here and only send once to reduce lag.
	 */
	public void setWalkingPacketQueue(int x, int y) {
		setWalkingPacketQueue(x, y, null);
	}

	public void setWalkingPacketQueue(int x, int y, MovementCompletionEvent event) {
		walkingPacketQueue[0] = x;
		walkingPacketQueue[1] = y;
		stopMovementQueue = false;
		movementDestination = new Position(x, y, getHeight());
		movementCompletionEvent = event;
	}

	/**
	 * setSkillLevel queued untill after the item switch update is sent.
	 */
	public int[] queuedSetSkillLevel = new int[3];

	/**
	 * Which walkable interface was last sent to the player.
	 */
	public int walkableInterface = -1;

	/**
	 * How many music packets were received from the client, this is used to differentiate flood bots
	 * from real players.
	 */
	public int musicPacketsReceived;

	/**
	 * Public chat settings.
	 */
	public int publicChatMode = 0;

	/**
	 * Trade chat settings.
	 */
	public int tradeChatMode = 0;

	/**
	 * Store bank pin numbers order, it is mixed up.
	 */
	public int bankPins[] = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1};

	/**
	 * Store the time of when the weekly game time spent tracker was used.
	 */
	public long timeWeeklyGameTimeUsed;

	/**
	 * True if the player is flagged for charging back.
	 */
	public boolean notifyFlagged;

	/**
	 * Save the player's chat and export to file if the player gets flagged for rwting.
	 */
	public ArrayList<String> rwtChat = new ArrayList<String>();

	/**
	 * True if the player is flagged for Rwt.
	 */
	private boolean flaggedForRwt;

	/**
	 * True if the Rwt cycle event is active.
	 */
	private boolean flaggedForRwtEventActive;

	/**
	 * When using knife of log, it will bring up 3 options. Same thing with needle on leather.
	 */
	public String threeOptionType = "";

	/**
	 * Store the time of when Nurse Tifani at Pvp was used to heal the player. If it has been 15
	 * seconds or less sicne its been used, then the player cannot trigger a special attack. What the
	 * player would do is infinite recharge and back to Pvp area and spear the victim and tb them and
	 * refreeze and slowly kill them.
	 */
	public long edgePvpNurseUsedTime;

	/**
	 * True if Mystery box is currently spinning for the player.
	 */
	private boolean usedMysteryBox;

	/**
	 * True if the Mystery box disconnection cycle event is running.
	 */
	public boolean mysteryBoxEventForDc;

	/**
	 * The Mystery box prize to award the player with.
	 */
	public int mysteryBoxWinningItemId;

	/**
	 * The item id of the Mystery box used.
	 */
	public int mysteryBoxItemIdUsed;

	/**
	 * True to announce the item won from the Mystery box.
	 */
	public boolean announceMysteryBoxWinningItem;

	/**
	 * The last npc the player interacted with, this is never reset.
	 */
	public int lastNpcClickedIndex;

	/**
	 * True to send kill screenshots in wilderness.
	 */
	public boolean killScreenshots = true;

	public int nextChatWell;

	/**
	 * Time vote notification was received.
	 */
	public long timeVoteNotificationAlerted;

	/**
	 * Used to send vote pop-up for a new player after they finished setting their appearance.
	 */
	public long timeFinishedTutorial;

	/**
	 * True if claimed an item from website.
	 */
	public boolean websiteMessaged;

	/**
	 * Total vote tickets claimed from website.
	 */
	public int totalWebsiteClaimed;

	/**
	 * Store the time the player used an SQL connection to prevent spam.
	 */
	public long sqlConnectionDelay;

	/**
	 * Quest points.
	 */
	public int qp;

	public int getQuestPoints() {
		return qp;
	}

	public void setQuestPoints(int qp) {
		this.qp = qp;
	}

	/**
	 * Delay for druid teleport
	 */
	public int druidTeleportDelay = 80;

	/**
	 * Testing Different uid.
	 */
	public String uid1 = "";

	/**
	 * Testing another different uid.
	 */
	public String uid2 = "";

	/**
	 * True to not add experience to the player. Used for Max hit dummy
	 */
	public boolean usingMaxHitDummy;

	/**
	 * True if player is multilogging in wild.
	 */
	public boolean multiLoggingInWild;

	/**
	 * Used for bank pin interface to show which step the player is on.
	 */
	public int bankPinInterfaceStep = 1;

	/**
	 * The x of the Immortal donator that requested me to teleport to him.
	 */
	public int teleToXPermission;

	/**
	 * The y of the Immortal donator that requested me to teleport to him.
	 */
	public int teleToYPermission;

	/**
	 * The height of the Immortal donator that requested me to teleport to him.
	 */
	public int teleToHeightPermission;

	/**
	 * The name of the Immortal donator that requested me to teleport to him.
	 */
	public String teleToNamePermission = "";

	/**
	 * True if using chair.
	 */
	public boolean usingChair;

	private Player itemOnPlayer;

	public void setItemOnPlayer(Player player) {
		this.itemOnPlayer = player;
	}

	public Player getItemOnPlayer() {
		return itemOnPlayer;
	}

	/**
	 * Amount of times Granite maul special clicked.
	 */
	public int graniteMaulSpecialAttackClicks;

	/**
	 * Amount of Granite maul special attacks applied this game tick.
	 */
	public int graniteMaulSpecialsUsedThisTick;

	/**
	 * True if catching imp
	 */
	public boolean catchingImp;

	/**
	 * True to stop the skilling cycle event.
	 */
	public boolean forceStopSkillingEvent;

	/**
	 * Set a custom yell tag for Uber donators.
	 */
	public String yellTag = "Uber";

	/**
	 * True if the player is using Pet Mystery box Cycle event.
	 */
	public boolean usingPetMysteryBoxEvent;

	/**
	 * The time the player was affected by Claws of guthix spell effect.
	 */
	public long timeClawsOfGuthixAffected;

	/**
	 * The time the player was affected by Zamorak flames spell effect.
	 */
	public long timeZamorakFlamesAffected;

	/**
	 * Prevent player for logging off and in very fast. This will dupe summoned pets.
	 */
	public long timeLoggedOff;

	public ArrayList<String> tradingPostHistory = new ArrayList<String>();

	public int tradingPostBuyAmount;

	public int tradingPostSellAmount;

	public int tradingPostBuyPrice;

	public int tradingPostSellPrice;

	/**
	 * Store the player's vote times to prevent claiming too many on 1 account within 12 hours.
	 */
	public ArrayList<String> voteTimes = new ArrayList<String>();

	/**
	 * Amount of yell mutes used on the player.
	 */
	public int yellMutes;

	/**
	 * When does yell mute expire/
	 */
	public long yellMuteExpireTime;

	/**
	 * This is used to know the difference between two aggressive styles on the combat interface. Use
	 * as: type weaponId frameId
	 */
	public String aggressiveType = "DEFAULT";

	/**
	 * Save time Charge spell used.
	 */
	public long chargeSpellTime;

	/**
	 * Saves the time since the imbued heart's effect has been activated.
	 */
	public long imbuedHeartEndTime;

	/**
	 * Save time of when special attack was restored via box of health.
	 */
	public long timeUsedHealthBoxSpecial;

	/**
	 * Player's client version.
	 */
	public int clientVersion;

	/**
	 * Time player got attacked while golden skulled.
	 */
	public long timeGoldenSkullAttacked;

	/**
	 * Used to stop the player from withdrawing from Deposit vault after accepting a trade at Dice
	 * zone.
	 */
	public long timeAcceptedTradeInDiceZone;

	/**
	 * True if the player hasn't accepted the dice rules interface.
	 */
	public boolean diceRulesForce;

	/**
	 * Store the last 15 attacks against the player along with the damage. Used to 1 hit the player
	 * if they have golden skull and are getting boxed.
	 */
	public ArrayList<String> antiBoxingData = new ArrayList<String>();

	/**
	 * Store id of weapon used for attack animation.
	 */
	public int weaponUsedOnAnimation;

	/**
	 * Used to calculate account wealth on packet logged players.
	 */
	public long packetLogBankWealthTime;

	/**
	 * Used to check account value wealth on bank interface.
	 */
	public long bankWealthCheckedTime;

	public long bankWealthCheckedAmount;

	/**
	 * To prevent yell spam.
	 */
	public long yellTimeUsed;

	/**
	 * Used for dropping bonus artefacts.
	 */
	public String lastKillType = "";

	/**
	 * Store ip-time i killed a player and got the reward. This is used to prevent farming.
	 */
	public ArrayList<String> killTimes = new ArrayList<String>();

	/**
	 * Store time staff of the dead special attack was started.
	 */
	public long timeStaffOfTheDeadSpecialUsed;

	/**
	 * So the player does not get unfrozen when they use ladder.
	 */
	public long timeUsedLadder;

	public int teleportWarningX;

	public int teleportWarningY;

	public int teleportWarningHeight;

	public boolean teleportWarning = true;

	public boolean wildCrevice;

	public int itemInventoryOptionId;

	private SkillMenu skillMenu = new SkillMenu(this);

	public SkillMenu getSkillMenu() {
		return skillMenu;
	}

	/**
	 * Player heigh at the beginning of the game tick.
	 */
	public int startHeight;

	/**
	 * Save the time the player used a preset, so when they give rune essence/pure essence to
	 * someone, it will decline.
	 */
	public long timeUsedPreset;

	/**
	 * Amount of loot dropped this game tick.
	 */
	public long lootThisTick;

	/**
	 * Random event incorrect tries amount.
	 */
	public int randomEventIncorrectTries;

	/**
	 * Random event skill index answer.
	 */
	public int randomEventSkillIndex;

	/**
	 * Random event used to stop botters.
	 */
	private String randomEvent = "";

	/**
	 * True if dragon thrownaxe is active.
	 */
	public boolean dragonThrownaxeSpecialUsed;

	/**
	 * The time the dragon thrownaxe special was used.
	 */
	public long dragonThrownAxeTimeUsed;

	/**
	 * Dragon thrownaxe damage.
	 */
	public int dragonThrownAxeSpecialDamage;

	/**
	 * Time switched to bulwark, effect against npcs only occurs 6 seconds or so after equipping it.
	 */
	public long timeSwitchedToBulwark;

	/**
	 * True if using dragon sword special.
	 */
	public boolean dragonSwordSpecial;

	/**
	 * Skill ids to update the skilling tab ids of, this is queued till after send item switch update
	 * is sent. So the item update packet is reaching the client first.
	 */
	public ArrayList<Integer> skillTabMainToUpdate = new ArrayList<Integer>();

	/**
	 * The sound to send at the end of the game tick, so the item inventory update packet is sent
	 * first to the client.
	 */
	public int soundToSend;

	/**
	 * The sound delay to send at the end of the game tick, so the item inventory update packet is
	 * sent first to the client.
	 */
	public int soundDelayToSend;

	/**
	 * Ignore poison/recoil damage in combat timer.
	 */
	public boolean ignoreInCombat;

	/**
	 * Log the total amount of attacks used in a stake.
	 */
	public int stakeAttacks;

	/**
	 * Log the total amount of special attacks used in a stake.
	 */
	public int stakeSpecialAttacks;

	/**
	 * Prevent flush spam.
	 */
	public boolean canFlush;

	/**
	 * The time the player will be un rag banned.
	 */
	public long timeRagUnbanned;

	/**
	 * Save the time the player used the ::risk command.
	 */
	public long timeUsedRiskCommand;

	/**
	 * To prevent players from using presets while skilling.
	 */
	public long timeSkilled;

	/**
	 * True to auto-buy back untradeables from shop.
	 */
	public boolean autoBuyBack;

	/**
	 * Time followed a person into wilderness and was warned.
	 */
	public long timeWildernessFollowWarned;

	/**
	 * Last time random event activated.
	 */
	public long lastRandomEvent;

	/**
	 * True if the player cannot issue a walking packet. So the player does not go through a door
	 * then walk which cancels it.
	 */
	public boolean cannotIssueMovement;

	/**
	 * This string is updated on log in after reading the character file and before grabbing the last
	 * uid address from the client.
	 */
	public String lastUidAddress = "";

	/**
	 * Amount of ticks player is speared for.
	 */
	public int dragonSpearTicksLeft;

	/**
	 * Store the time the player was affected by the Dragon spear special attack.
	 */
	public long timeDragonSpearEffected;

	/**
	 * True if dragon spear event is active.
	 */
	public boolean dragonSpearEvent;

	/**
	 * Store on each line: Damage: 5 Smite: 5 Freeze: 15000 (as in 15 seconds extra freeze) Dragon
	 * scimitar special (as in apply dragon scimitar successful special attack)
	 */
	public ArrayList<String> dragonSpearEffectStack = new ArrayList<String>();

	public int flower;

	public int flowerX;

	public int flowerY;

	public int flowerHeight;

	private String lastDueledWithName = "";

	public String lastTradedWithName = "";

	public ArrayList<String> sendGroundItemPacket = new ArrayList<String>();

	public ArrayList<String> sendGroundItemPacketRemove = new ArrayList<String>();

	public int potionCombineLoops;

	/**
	 * True if player is ip-muted, updates on log-in
	 */
	public boolean ipMuted;

	/**
	 * Store the original account name that caused this account to be ip muted.
	 */
	public String ipMutedOriginalName = "";

	/**
	 * Double death duel arena moving fix.
	 */
	public long timeMovedFromDoubleDuelDeath;

	/**
	 * Used to block the player from entering the wilderness if they recently died. It takes an
	 * average of 45 seconds to gear up in tribrid 4 ways.
	 */
	public long timeDiedInWilderness;

	/**
	 * True if rigour is unlocked.
	 */
	public boolean rigourUnlocked;

	/**
	 * True if augury is unlocked.
	 */
	public boolean auguryUnlocked;

	/**
	 * Time used god wars dungeon altar.
	 */
	public long timeUsedGodWarsDungeonAltar;

	/**
	 * True if item on npc event is being used.
	 */
	public boolean itemOnNpcEvent;

	/**
	 * Save list of resources harvested, item id and amount, used to sell back to shop.
	 */
	public ArrayList<String> resourcesHarvested = new ArrayList<String>();

	/**
	 * True if player is using old school autocast through combat tab.
	 */
	public boolean usingOldAutocast;

	/**
	 * Brew message sent string.
	 */
	public String brewMessageSent = "";

	/**
	 * To prevent brew message spam.
	 */
	public long timeBrewMessageSent;

	/**
	 * Used with timeTriedToAttackPlayer.
	 */
	public String playerTriedToAttack = "";

	/**
	 * Used so both players can attack each other if they do not meet edge and wests requirements.
	 */
	public long timeTriedToAttackPlayer;

	/**
	 * Used to disconnect a player who has dced in combat and then died, so when they log in, they do
	 * not log in where they died. This is to give the player time to respawn then disconnect.
	 */
	public long timeDied;

	/**
	 * Pure tribrid tournament wins.
	 */
	public int hybridTournamentsWon1;

	/**
	 * Zerk hybrid tournament wins.
	 */
	public int tribridTournamentsWon1;

	/**
	 * Main hybrid tournament wins.
	 */
	public int meleeTournamentsWon1;

	/**
	 * Used to know if player will be moving, used for when the player is not moving, then interact
	 * with object. To prevent players from interacting with the object when they are on the other
	 * side of the wall. Player can still interact with object if there is no open path to it.
	 */
	public boolean tempMoving;

	/**
	 * Belongs to tempMoving.
	 */
	public int tempDir1;

	/**
	 * Belongs to tempMoving.
	 */
	public int tempDir2;

	/**
	 * Belongs to tempMoving.
	 */
	public boolean tempRunning;

	/**
	 * Tournament target id.
	 */
	public int tournamentTarget = -1;

	/**
	 * Used to update the bank
	 */
	public boolean bankUpdated;

	/**
	 * Force movement update mask boolean, used for cut scenes only because multiplayer does not work
	 * for it.
	 */
	public boolean forceMovementUpdate;

	/**
	 * True to not open shop interface, used when updating shop interface.
	 */
	public boolean doNotOpenShopInterface;

	/**
	 * Shop search string.
	 */
	public String shopSearchString = "";

	/**
	 * Total blood keys taken and made out alive.
	 */
	public int bloodKeysCollected;

	/**
	 * Kills in multi wilderness.
	 */
	public int killsInMulti;

	/**
	 * True if the player can claim the pvp task reward.
	 */
	public boolean canClaimPvpTaskReward;

	/**
	 * How many kills in total for the task, used for receiving blood money reward.
	 */
	public int pvpTaskSize;

	/**
	 * Total amount of pvp tasks completed.
	 */
	public int pvpTasksCompleted;

	/**
	 * Store the amount of kills to do for each type. 0 is pure, 1 is zerker, 2 is ranged tank, 3 is
	 * maxed.
	 */
	public int[] pvpTask = new int[4];

	public int[] questStages = new int[Quest.totalQuests + 1];

	private final QuestHandler questHandler = new QuestHandler(this);

	public void completeQuest(String questName, QuestReward questReward, int itemId) {
		getQuestFunction().completeQuest(questName, questReward, itemId);
	}

	public void ShowQuestInfo(String questName, String startInfo) {
		getQuestFunction().ShowQuestInfo(questName, startInfo);
	}

	public Quest getQuest(int id) {
		if (quests[id] == null) {
		}
		return quests[id];
	}

	public Quest[] quests = new Quest[Quest.totalQuests + 1];

	public void loadQuests() {
		for (int i = 0; i < Quest.totalQuests; i++) {
			quests[i] = new Quest(i, this);
		}
	}

	public QuestHandler getQuestFunction() {
		return questHandler;
	}

	/**
	 * Store time the player left the wilderness while having a target.
	 */
	public long timeExitedWildFromTarget;

	/**
	 * Used to avoid spamming the player with how many seconds they have left to return to wilderness
	 * to not lose target.
	 */
	public long targetLeftTime;

	/**
	 * To prevent the target activity gaining to be spammed.
	 */
	public long targetActivityTime;

	/**
	 * At a certain value, the player is eligible for a target.
	 */
	public int targetActivityPoints;

	/**
	 * The player id of my target.
	 */
	public int targetPlayerId = -1;

	/**
	 * True if Toxic blowpipe special attack is active.
	 */
	public boolean blowpipeSpecialAttack;

	/**
	 * The Toxic blowpipe dart type loaded into it.
	 */
	public int blowpipeDartItemId;

	/**
	 * The amount of charges in a player's Toxic blowpipe.
	 */
	private int blowpipeCharges;

	/**
	 * Count how many blowpipe shots fired, for every 3 shots fired, 1 scale is reduced from the
	 * Toxic blowpipe.
	 */
	public int blowpipeShotsFired;

	/**
	 * @return The amount of blowpipeCharges integer.
	 */
	public int getBlowpipeCharges() {
		return blowpipeCharges;
	}

	public void setBlowpipeCharges(int blowpipeCharges) {
		this.blowpipeCharges = blowpipeCharges;
	}

	/**
	 * The amount of darts loaded into the Toxic blowpipe.
	 */
	public int blowpipeDartItemAmount;

	/**
	 * Rune pouch item id data.
	 */
	public int[] runePouchItemId = new int[3];

	/**
	 * Rune pouch item amount data.
	 */
	public int[] runePouchItemAmount = new int[3];

	/**
	 * Food dropped while in the Wilderness.
	 */
	public ArrayList<String> droppedFood = new ArrayList<String>();

	/**
	 * Boss kill counts messages.
	 */
	public boolean bossKillCountMessage = true;

	/**
	 * Loot worth to notify to the player.
	 */
	public int valuableLoot;

	/**
	 * Save time of when the valuable loot "more loot" was sent.
	 */
	public long timeValuableLootNotifiedAgain;

	/**
	 * Used to record what time the player exited the player while in-combat, for the City timer at
	 * Edgeville.
	 */
	public long timeExitedWilderness;

	/**
	 * Save the time the victim exited wilderness while in combat with the player.
	 */
	public long timeVictimExitedWilderness;

	/**
	 * True if city timer can be triggered.
	 */
	public boolean canTriggerCityTimer;

	/**
	 * Save player id i can attack in safe area.
	 */
	public int playerIdCanAttackInSafe;

	/**
	 * Save player id attacking me in safe area.
	 */
	public int playerIdAttackingMeInSafe;

	/**
	 * Preset index set.
	 */
	public int presetIndex;

	/**
	 * Store preset data in here.
	 */
	public ArrayList<String> preset1 = new ArrayList<String>();

	/**
	 * Store preset data in here.
	 */
	public ArrayList<String> preset2 = new ArrayList<String>();

	/**
	 * Store preset data in here.
	 */
	public ArrayList<String> preset3 = new ArrayList<String>();

	/**
	 * Store preset data in here.
	 */
	public ArrayList<String> preset4 = new ArrayList<String>();

	/**
	 * Store preset data in here.
	 */
	public ArrayList<String> preset5 = new ArrayList<String>();

	/**
	 * Store preset data in here.
	 */
	public ArrayList<String> preset6 = new ArrayList<String>();

	/**
	 * Store preset data in here.
	 */
	public ArrayList<String> preset7 = new ArrayList<String>();

	/**
	 * Store preset data in here.
	 */
	public ArrayList<String> preset8 = new ArrayList<String>();

	/**
	 * Store preset data in here.
	 */
	public ArrayList<String> preset9 = new ArrayList<String>();

	/**
	 * True if the player has the required gear to cast an ice barrage.
	 */
	public boolean hasMagicEquipment;

	/**
	 * True if the player has the required gear to use ranged.
	 */
	public boolean hasRangedEquipment;

	/**
	 * The amount of risk the player is currently risking, force protect item on.
	 */
	public long wildernessRiskAmount;

	/**
	 * Current carried wealth total.
	 */
	public long carriedWealth;

	/**
	 * Current risked wealth total.
	 */
	public long riskedWealth;

	/**
	 * True if the first log-in teleport update has been completed
	 */
	public boolean logInTeleportCompleted;

	/**
	 * Amount of times the player has tabbed.
	 */
	public int myTabs;

	/**
	 * Amount of times the enemy has tabbed from the player in singles.
	 */
	public int enemyTabs;

	/**
	 * Store the time of when the player got a kill while being under the Edge and Wests protection
	 * rule.
	 */
	public long killedPlayerImmuneTime;

	/**
	 * True if the player has excess brews.
	 */
	public boolean excessBrews;

	public int brewCount;

	/**
	 * If the player is using 2 styles, then he is a Hybrid, if 3 styles, then is a Tribrid.
	 */
	public int combatStylesUsed;

	/**
	 * Time scanned for Tribrid gear.
	 */
	public long timeScannedForTribrid;

	/**
	 * List of Pvp blacklisted players.
	 */
	public ArrayList<String> pvpBlacklist = new ArrayList<String>();

	/**
	 * The time the player was warned about fighting another player.
	 */
	public long timeWarned;

	/**
	 * Ghosts spawned in Cerberus
	 */
	public boolean alreadySpawned = false;

	/**
	 * The name of the player and reason to be warned of.
	 */
	public String nameWarnedOf = "";

	/**
	 * True if player is in Edgeville 1-5 wilderness or West dragons wilderness.
	 */
	public boolean inEdgeOrWestArea;

	/**
	 * The time the player went from a dangerous area to the Edgeville 1-5 wilderness or West dragons
	 * wilderness.
	 */
	public long timeEnteredEdgeOrWestArea;

	/**
	 * Store time i attacked or was attacked by a player. This is reset when i get a kill, it is used
	 * specifically for me to attack a player right after i get a kill.
	 */
	public long timeInPlayerCombat;

	/**
	 * Time was in combat, this is never reset. Used to know if player was in combat.
	 */
	public long timeInCombat;

	/**
	 * If player sends more than 12 packets per tick, decline.
	 */
	public int packetsSentThisTick;

	/**
	 * Used to alert the player of when he is eligible to vote.
	 */
	public long timeVoted;

	/**
	 * Total votes claimed to avoid spam.
	 */
	public int votesClaimed;

	/**
	 * Time used xp lamp, can only use Xp lamp every 12 hours.
	 */
	public long xpLampUsedTime;

	/**
	 * True if the player is viewing max hits on npcs instead of max hits on player.s
	 */
	public boolean viewingNpcMaxHits;

	/**
	 * Store the time of when either player a or b changed the rule.
	 */
	public long timeDuelRuleChanged;

	/**
	 * Bars to smelt.
	 */
	public int barsToMake;

	/**
	 * Store action id, this is reset when doing any other action like walk etc.. Store a unique id
	 * of a specific action, such as opening an interface, this is used to verify that the player is
	 * still using the interfac,e to prevent packet exploits.
	 */
	private int actionIdUsed;

	/**
	 * True if using gnome glider interface event.
	 */
	public boolean gnomeGliderEvent;

	/**
	 * True if the player i searched for in the profile system is online.
	 */
	public boolean isProfileSearchOnline;

	/**
	 * The the online player's id.
	 */
	private int profileSearchOnlinePlayerId;

	/**
	 * Save the online player's name.
	 */
	public String profileSearchOnlineName;

	/**
	 * Used to gain merit points.
	 */
	public int runeEssenceCrafted;

	/**
	 * True to not send the packet that closes the pm interface.
	 */
	public boolean doNotClosePmInterface;

	public boolean closePmInterfaceOnWalk;

	/**
	 * Save total zombie damage in the minigame, this can show who played alot and who didn't.
	 */
	public int totalZombieDamage;

	/**
	 * To prevent ::claim abuse.
	 */
	public long timeClaimedEvent;

	/**
	 * To prevent ::claim abuse.
	 */
	public long timeClaimedDonation;

	/**
	 * This is a tick timer used for finding the other player when i request trade, challenege, duo
	 * zombies etc..
	 */
	public int findOtherPlayerId;

	/**
	 * Last teleport used on the teleport interface
	 */
	public String lastTeleport = "";

	/**
	 * True if the player is in the zombies minigame and has not died or logged off.
	 */
	private boolean inZombiesMinigame;

	/**
	 * Save the highest zombie wave the player has reached.
	 */
	public int highestZombieWave;

	/**
	 * Save the partner name, this is used along the highest zombie wave reached.
	 */
	public String zombiePartner = "";

	/**
	 * Zombie wave points to spend at shop.
	 */
	public int zombieWavePoints;

	/**
	 * True if player has clicked ready and is ready.
	 */
	public boolean isReadyForNextZombieWave;

	/**
	 * True to show the interface that has the option to click ready.
	 */
	public boolean waitingForWave;

	/**
	 * Player index of duo partner.
	 */
	private int zombiePartnerId = -1;

	/**
	 * Store name of the player i request a duo from.
	 */
	public String requestDuoName = "";

	/**
	 * Search time of last profile search to prevent abuse to try and lagg the server.
	 */
	public long timeSearchedProfile;

	/**
	 * Time clicked profile button, to prevent lagg abuse.
	 */
	public long timeClickedProfileButton;

	/**
	 * Time stats restored using Donator npc.
	 */
	public long restoreStatsTime;

	/**
	 * This si the throne id, depending on what the donator chose.
	 */
	public int throneId = 1097;

	/**
	 * Save the wilderness risk outcome of the last scan, this is used if the scan occured before it
	 * is time for the next scan.
	 */
	public boolean hasWildernessRisk;

	/**
	 * Used to scan the player for wilderness risk every 20 seconds, rather than every npc auto
	 * attack.
	 */
	public long timeScannedForWildernessRisk;

	/**
	 * Save items kept on death.
	 */
	public ArrayList<String> itemsKeptOnDeathList = new ArrayList<String>();

	/**
	 * Wilderness Risk items kept on death temporary.
	 */
	public ArrayList<String> wildernessRiskItemsKeptOnDeath = new ArrayList<String>();

	/**
	 * Save displayed hall of fame, to be used for when the player clicks on player name to view
	 * profile.
	 */
	public ArrayList<String> currentHallOfFame = new ArrayList<String>();

	/**
	 * Throne chair to remove.
	 */
	public ArrayList<Object> toRemove = new ArrayList<Object>();

	/**
	 * True if player was wearing whip/spear on aggressive style. So if player is using aggressive
	 * (controlled) whip and switches to Msb, it doesn't go to long ranged. Same thing with Dragon
	 * spear to Rune crossbow for example.
	 */
	public boolean wasWearingAggressiveSharedXpWeapon;

	/**
	 * True if the player can use teleport interface, to prevent packet abuse.
	 */
	public boolean canUseTeleportInterface;

	/**
	 * Store coordinates of displayed teleport, to be later used to execute the teleport action.
	 */
	public ArrayList<String> currentTeleports = new ArrayList<String>();

	/**
	 * Teleport tab clicked.
	 */
	public int teleportInterfaceIndex;

	/**
	 * Enter the displayed banned list here, to be used for when a player does the actual unbanning,
	 * because another moderator can do a change then instead of me unbanning Toxic, i unban spammer.
	 * Same thing with moderatorList
	 */
	public ArrayList<String> clanChatBannedList = new ArrayList<String>();

	public ArrayList<String> clanChatModeratorList = new ArrayList<String>();

	/**
	 * Hatched used for woodcutting.
	 */
	public int hatchetUsed;

	/**
	 * Pickaxe used for mining.
	 */
	public int pickAxeUsed;

	/**
	 * Save last activity.
	 */
	public String lastActivity = "";

	/**
	 * Last time the activity was done.
	 */
	public long lastActivityTime;

	/**
	 * True if using Poison event.
	 */
	public boolean poisonEvent;

	/**
	 * True if using Venom event.
	 */
	public boolean venomEvent;

	/**
	 * Used to prevent achievementSaveName to be added twice when it comes to adding to achievements
	 * like "1089 1090".
	 */
	public boolean achievementAddedOnce;

	/**
	 * True if using Yew log.
	 */
	public boolean yewLog;

	/**
	 * True if Raw beef is chosen.
	 */
	public boolean rawBeefChosen;

	/**
	 * True if making raw beef.
	 */
	public boolean rawBeef;

	/**
	 * Flax delay.
	 */
	public long flaxDelay;

	/**
	 * Amount of smithing items to make.
	 */
	public int smithingAmountToMake;

	/**
	 * Enchant delay.
	 */
	public long enchantDelay;

	/**
	 * True to save npc text. If true, when i click on equipment button, it will save the npc on a
	 * text file and the cooridinates will be where i am standing.
	 */
	public boolean saveNpcText;

	/**
	 * True if using withdraw all but one.
	 */
	public boolean withdrawAllButOne;

	/**
	 * Name of skilling action being performed by a cycle event.
	 */
	public boolean isUsingSkillingEvent;


	/**
	 * True if a potion has been decanted.
	 */
	public boolean potionDecanted;

	/**
	 * In seconds.
	 */
	public int barrowsPersonalRecord;

	/**
	 * Save barrows lap timer.
	 */
	public long barrowsTimer;

	/**
	 * 1 to make title appear after name.
	 */
	public int titleSwap;

	/**
	 * Save the index of the last title clicked, the index of it in the TitleDefinitions.
	 */
	public int titleIndexClicked;

	/**
	 * Titles unlocked, pking, skilling & misc.
	 */
	public int[] titleTotal = new int[3];

	/**
	 * Title ids unlocked.
	 */
	public ArrayList<String> titlesUnlocked = new ArrayList<String>();

	/**
	 * Title tab being used.
	 */
	public int titleTab = -1;

	/**
	 * True to display bots.
	 */
	public boolean displayBots = true;

	/**
	 * Last x amount withdrawn from bank.
	 */
	public int lastXAmount = 100;

	/**
	 * Boss score capped result, used only for calculating adventurer rank.
	 */
	public int bossScoreCapped;

	/**
	 * Boss score uncapped result, used for boss score highscores.
	 */
	public int bossScoreUnCapped;

	/**
	 * Death runes crafted.
	 */
	public int deathRunesCrafted;

	/**
	 * Blood money spent history. Needed for acheivement.
	 */
	public int bloodMoneySpent;

	/**
	 * Untradeable items owned by the player that the player can only have 1 quantity of such as Max
	 * capes, Clue scroll, God capes, Boss pets etc. Rather than searching the whole account for the
	 * item, just look at this array instead.
	 */
	public ArrayList<String> singularUntradeableItemsOwned = new ArrayList<String>();

	/**
	 * 0 is easy, 4 is elite. Used to check if an achievement difficulty reward has been claimed.
	 */
	public boolean achievementRewardClaimed[] = new boolean[4];

	/**
	 * 0 is easy, 4 is elite. Used to check if an achievement difficulty completion has been
	 * announced.
	 */
	public boolean achievementDifficultyCompleted[] = new boolean[4];

	/**
	 * Used to know if an achievement text is clicked on.
	 */
	public int lastAchievementClicked = -1;

	/**
	 * Achievements completed, easy, medium, hard & elite.
	 */
	public int[] achievementTotal = new int[4];

	/**
	 * Achievement progress.
	 */
	public ArrayList<String> achievementProgress = new ArrayList<String>();

	/**
	 * Achievements Ids completed.
	 */
	public ArrayList<String> achievementsCompleted = new ArrayList<String>();

	/**
	 * Store bot total wealth dropped to player.
	 */
	public int victimBotWealth;

	/**
	 * Potion decanting data
	 */
	public ArrayList<String> potions = new ArrayList<String>();

	/**
	 * True if the players messages are filtered. If true, spam messages such as You get some ores
	 * will be not sent.
	 */
	public String messageFiltered = "";

	/**
	 * Farming stage delays between cleaning patch and watering, planting.
	 */
	public long farmingStageDelay;

	/**
	 * Save x coordinate of raked spot.
	 */
	public int farmingXCoordinate;

	/**
	 * Save y coordinate of raked spot.
	 */
	public int farmingYCoordinate;

	/**
	 * True if player is farming.
	 */
	public boolean isFarming;

	/**
	 * True if using a skilling cycle event.
	 */
	public boolean farmingEvent;

	/**
	 * Save strung bow id.
	 */
	public int strungBowId;

	/**
	 * Amount of bots killed.
	 */
	public int playerBotKills;

	/**
	 * Amount of deaths to bots.
	 */
	public int playerBotDeaths;

	/**
	 * Highest killstreak against bots.
	 */
	public int playerBotHighestKillstreak;

	/**
	 * Current killstreak against bots.
	 */
	public int playerBotCurrentKillstreak;

	/**
	 * Looting bag slot item.
	 */
	public int[] lootingBagStorageItemId = new int[28];

	/**
	 * Looting bag slot amount.
	 */
	public int[] lootingBagStorageItemAmount = new int[28];

	/**
	 * Loot key slot item.
	 */
	public int[] lootKey1ItemId = new int[28];

	public int[] lootKey2ItemId = new int[28];

	public int[] lootKey3ItemId = new int[28];

	public int[] lootKey4ItemId = new int[28];

	public int[] lootKey5ItemId = new int[28];

	/**
	 * Loot key slot amount.
	 */
	public int[] lootKey1ItemAmount = new int[28];

	public int[] lootKey2ItemAmount = new int[28];

	public int[] lootKey3ItemAmount = new int[28];

	public int[] lootKey4ItemAmount = new int[28];

	public int[] lootKey5ItemAmount = new int[28];

	/**
	 * Save the leather item to produce.
	 */
	public int leatherUsed;

	/**
	 * Save the leather item to produce type. Weather it is chaps, body or vambraces.
	 */
	public int leatherItemToProduceType;

	/**
	 * Save the amount of pending achievement popups.
	 */
	public int pendingAchievementPopUps;

	public String selectedGameMode = "";

	public String selectedDifficulty = "";

	/**
	 * X coordinate of the fire used when cooking. Used for checking if the fire is alive before each
	 * cook.
	 */
	public int fireX;

	/**
	 * Y coordinate of the fire used when cooking. Used for checking if the fire is alive before each
	 * cook.
	 */
	public int fireY;

	/**
	 * Store npc kills, NAME AMOUNT.
	 */
	public ArrayList<String> npcKills = new ArrayList<String>();

	/**
	 * True if the player has the searched items displayed.
	 */
	private boolean usingBankSearch;

	/**
	 * The string being searched for.
	 */
	public String bankSearchString;

	/**
	 * The slot from where the item is being withdrawn from when using the search feature.
	 */
	public int itemInBankSlot;

	/**
	 * Save the array of searched items and the amount.
	 */
	public ArrayList<GameItem> bankSearchedItems = new ArrayList<GameItem>();

	/**
	 * Items to show in the untradeable shop.
	 */
	public ArrayList<String> itemsToShop = new ArrayList<String>();

	/**
	 * Items to show in the untradeable shop.
	 */
	public ArrayList<String> itemsToInventory = new ArrayList<String>();

	/**
	 * True if the player has too many connections from his address.
	 */
	public boolean hasTooManyConnections;

	/**
	 * True if the player is using the preaching event.
	 */
	public boolean usingPreachingEvent;

	/**
	 * Current rfd wave.
	 */
	public int rfdWave = 0;

	/**
	 * Highest Recipe for disaster wave reached
	 */
	public int highestRfdWave = -1;

	/**
	 * True if the max total level has been announce.
	 */
	public boolean announceMaxLevel;

	public int pcPoints = 0;

	public int pestControlDamage;

	public long buyPestControlTimer;

	/**
	 * Warrior's guild cycle event timer.
	 */
	public int warriorsGuildEventTimer;

	public int warriorsGuildCyclopsTimer;

	/**
	 * Save the index of the Warrior's guild armour data list.
	 */
	public int warriorsGuildArmourIndex;

	/**
	 * True if the player is using the cyclops drain tokens event.
	 */
	public boolean usingCyclopsEvent;

	/**
	 * True if the player summoned the Warrior's guild animator.
	 */
	public boolean summonedAnimator;

	/**
	 * True to 1 hit the npcs.
	 */
	public boolean hit1;

	/**
	 * Used for checking if player is a bot for combat related situations.
	 *
	 * @return
	 */
	public boolean isCombatBot() {
		return bot;
	}


	public ArrayList<String> botDebug = new ArrayList<String>();

	public ArrayList<String> botItemsToWear = new ArrayList<String>();

	private String botStatus = "";

	/**
	 * The last combat type the bot was attacked in, same value as the overhead prayer.
	 */
	public int botLastDamageTakenType = 18;

	public long botEnemyPrayedTime;

	public int botDiagonalTicks;

	public String botPkType = "";

	public boolean botWornItemThisTick;

	public boolean botRegearEvent;

	public boolean botEarlyRetreat;

	public long botTimeInCombat;

	public int botEatingEventTimer;

	public long botTimeSwitchedItem;

	public boolean botEatingEvent;

	public int[] botEnemyDeathPosition = new int[2];

	/**
	 * The bot may have 1 action per tick.
	 */
	private boolean botActionApplied;


	/**
	 * The bot will have an idea of the enemy's special attack bar percentage, to react accordingly.
	 */
	public int botEnemySpecialAttack;

	public boolean botUsedSpecialAttack;

	public boolean botReAttack = true;

	public boolean botWearingPrimaryWeapon = true;

	public int botSpecialAttackWeapon;

	public int botPrimaryWeapon;

	public int botShield;

	public int botSpecialAttackWeaponShield;

	public int botArrowSpecial;

	public int botArrowPrimary;

	public long botTimePrayerToggled;

	public int botPureWeaponSet;

	/**
	 * The type of experience to show in the xp bar, weather it is COMBAT/SESSION/TOTAL
	 */
	public String xpBarShowType = "COMBAT";

	/**
	 * Save experince gained that belongs to the current log-in session.
	 */
	public int currentSessionExperience;

	/**
	 * Barrows brothers killed progress.
	 */
	public boolean[] barrowsBrothersKilled = new boolean[6];

	/**
	 * The part of the Completionist cape being edited.
	 */
	public String completionistCapePartEdited = "TOP";

	/**
	 * Save all the xp gained this tick and send at the end of the tick to the client.
	 */
	public int xpDropAmount;

	/**
	 * List of skills displayed in order for the xp orb.
	 */
	public String xpDropSkills = "";

	/**
	 * True, to stop gaining experience.
	 */
	public boolean xpLock;

	/**
	 * Save all names on current viewed highscore, to be later used when clicking on highscore name
	 * to display the relevant profile.
	 */
	public ArrayList<String> currentHighscoresNameList = new ArrayList<String>();

	/**
	 * Save current adventurer rank, so if it has increased, show achievement popup.
	 */
	public int currentAdventurerRank;

	/**
	 * Save current pker rank, so if it has increased, show achievement popup.
	 */
	public int currentPkerRank;

	/**
	 * The mac address. current mac
	 */
	public String addressMac = "";

	/**
	 * The unique UID address. Current uid
	 */
	public String addressUid = "";

	/**
	 * True, if it has been already announced for the player.
	 */
	public boolean[] skillMilestone100mAnnounced = new boolean[25];

	/**
	 * True, if it has been already announced for the player.
	 */
	public boolean[] skillMilestone200mAnnounced = new boolean[25];

	/**
	 * True if the player can verify new objects.
	 */
	public boolean canVerifyMoreObjects;

	/**
	 * Store last non-verified object used, to prevent spam.
	 */
	public String lastNonVerifiedObjectUsed = "";

	/**
	 * True if player is using bank interface, to prevent packet interface abuse.
	 */
	private boolean usingBankInterface;

	/**
	 * True if player is using equipment bank interface, to prevent packet interface abuse.
	 */
	public boolean usingEquipmentBankInterface;

	/**
	 * The highscores main tab clicked by the player.
	 */
	public int highscoresTabClicked = -1;

	/**
	 * Gwd kills.
	 */
	public int gwdKills[] = new int[4];

	/**
	 * Total donator tokens donated for.
	 */
	public int donatorTokensReceived;

	/**
	 * Total amount of Donator tokens used for a rank.
	 */
	public int donatorTokensRankUsed;

	/**
	 * @return True if the player is a Donator.
	 */
	public boolean isDonator() {
		return donatorTokensRankUsed >= DonatorTokenUse.DonatorRankSpentData.DONATOR
				.getTokensRequired();
	}

	/**
	 * @return True if the player is a Super Donator.
	 */
	public boolean isSuperDonator() {
		return donatorTokensRankUsed >= DonatorTokenUse.DonatorRankSpentData.SUPER_DONATOR
				.getTokensRequired();
	}

	/**
	 * @return True if the player is an Extreme Donator.
	 */
	public boolean isExtremeDonator() {
		return donatorTokensRankUsed >= DonatorTokenUse.DonatorRankSpentData.EXTREME_DONATOR
				.getTokensRequired();
	}

	/**
	 * @return True if the player is a Legendary Donator.
	 */
	public boolean isLegendaryDonator() {
		return donatorTokensRankUsed >= DonatorTokenUse.DonatorRankSpentData.LEGENDARY_DONATOR
				.getTokensRequired();
	}

	/**
	 * @return True if the player is an Ultimate Donator.
	 */
	public boolean isUltimateDonator() {
		return donatorTokensRankUsed >= DonatorTokenUse.DonatorRankSpentData.ULTIMATE_DONATOR
				.getTokensRequired();
	}

	/**
	 * @return True if the player is an Uber Donator.
	 */
	public boolean isUberDonator() {
		return donatorTokensRankUsed >= DonatorTokenUse.DonatorRankSpentData.UBER_DONATOR
				.getTokensRequired();
	}

	/**
	 * @return True if the player is an Immortal Donator.
	 */
	public boolean isImmortalDonator() {
		return donatorTokensRankUsed >= DonatorTokenUse.DonatorRankSpentData.IMMORTAL_DONATOR
				.getTokensRequired();
	}

	/**
	 * @return True if the player is a Supreme Donator.
	 */
	public boolean isSupremeDonator() {
		return donatorTokensRankUsed >= DonatorTokenUse.DonatorRankSpentData.SUPREME_DONATOR
				.getTokensRequired();
	}

	/**
	 * @return True if the player is a Lucifer Donator.
	 */
	public boolean isLuciferDonator() {
		return donatorTokensRankUsed >= DonatorTokenUse.DonatorRankSpentData.LUCIFER_DONATOR
				.getTokensRequired();
	}

	/**
	 * @return True if the player is an Omega Donator.
	 */
	public boolean isOmegaDonator() {
		return donatorTokensRankUsed >= DonatorTokenUse.DonatorRankSpentData.OMEGA_DONATOR.getTokensRequired();
	}

	/**
	 * @return True if the player is any type of ironman
	 */
	public boolean hasIronRank() {
		return isIronman() || isUltiIronman() || isHardcoreIronman();
	}

	/**
	 * @return True if the player is an Ironman
	 */
	public boolean isIronman() {
		return playerRights == 9;
	}

	/**
	 * @return True if the player is an Ultimate Ironman
	 */
	public boolean isUltiIronman() {
		return playerRights == 25;
	}

	/**
	 * @return True if the player is a Hardcore Ironman
	 */
	public boolean isHardcoreIronman() {
		return playerRights == 26;
	}


	/**
	 * Save time of an item picked up from floor.
	 */
	public long timePickedUpItem;

	/**
	 * Save last item id picked up, so i can add a delay to it if it is the same item, so it is not
	 * too fast.
	 */
	public int lastItemIdPickedUp;

	/**
	 * True if shop interface is opened.
	 */
	public boolean usingShop;

	/**
	 * Text of the first line of profile biography.
	 */
	public String biographyLine1 = "";

	/**
	 * Text of the second line of profile biography.
	 */
	public String biographyLine2 = "";

	/**
	 * Text of the third line of profile biography.
	 */
	public String biographyLine3 = "";

	/**
	 * Text of the fourth line of profile biography.
	 */
	public String biographyLine4 = "";

	/**
	 * Text of the fifth line of profile biography.
	 */
	public String biographyLine5 = "";

	public int deathsToNpc;

	/**
	 * True, if the player has profile privacy on, to hide Pvm stats and to hide rare drop
	 * announcements.
	 */
	public boolean profilePrivacyOn;

	/**
	 * Amount of clue scrolls completed.
	 */
	private int clueScrollsCompleted;

	/**
	 * Amount of barrows chests opened.
	 */
	private int barrowsRunCompleted;

	/**
	 * String of the player's name searched.
	 */
	private String profileNameSearched = "";

	public boolean profilePrivacySearched;

	/**
	 * Store the item id that the player has in his equipment slots, if it has changed in the next
	 * game tick, then update that specific slot.
	 * <p>
	 * This is done to specifically send the equipment update packet at the end of the player packet
	 * tick to speed up switching.
	 */
	public long profileSearchDelay;

	/**
	 * Store the amount of an item the player has in his equipment slots, if it has changed in the
	 * next game tick, then update that specific slot.
	 * <p>
	 * This is done to specifically send the equipment update packet at the end of the player packet
	 * tick to speed up switching.
	 */
	public int[] playerEquipmentAfterLastTick = new int[14];

	/**
	 * Store
	 */
	public int[] playerEquipmentAmountAfterLastTick = new int[14];

	/**
	 * Save the opened tab before pressing bank inventory/equipment button. Used to deposit new items
	 * into this tab number.
	 */
	public int originalTab;

	/**
	 * Used for the God cape claiming system.
	 */
	public int godCapeClaimingTimer;

	/**
	 * Used to store, Pking, Skilling, Pvm activities. Increase each by +1 if activity used, cannot
	 * increase for another 10 seconds for all indexes.
	 */
	public int[] timeSpent = new int[3];

	/**
	 * Save time of when timeSpent[] has been last increased.
	 */
	public long lastTimeSpentUsed;

	/**
	 * Store skilling statistics such as bones buried etc..
	 */
	public int[] skillingStatistics = new int[25];

	/**
	 * Store dragon claws damage, used for calculating max dragon claws damage.
	 */
	public int[] storeDragonClawsDamage = new int[4];

	/**
	 * Save time of a dragon claw kill, to stop it from saving 4 times.
	 */
	public long timeDragonClawKill;

	/**
	 * Which difficulty tab was last clicked on the achievement interface.
	 */
	public String lastProfileTabText = "INFO";

	/**
	 * Amount of barrages casted.
	 */
	public int barragesCasted;

	/**
	 * True, if the player has the same dialogue opened before opening the same dialogue again.
	 */
	public boolean hasDialogueOptionOpened;

	/**
	 * True, if the player has last used a manual combat spell. This is to determine weather the last
	 * magic spell was an autocast or a manual cast.
	 */
	public boolean lastUsedManualSpell;

	/**
	 * Slayer tower doors, they do not update when going up a height level, so have to manually
	 * update it when using the door.
	 */
	public long doorAntiSpam;

	/**
	 * X coordinate of the fishing spot before it got moved, this is used to check if the fishing
	 * spot still exists in the spot that i'm fishing in.
	 */
	public int lastFishingSpotX;

	/**
	 * Y coordinate of the fishing spot before it got moved, this is used to check if the fishing
	 * spot still exists in the spot that i'm fishing in.
	 */
	public int lastFishingSpotY;

	/**
	 * True, to not send a message when using an achievement item.
	 */
	public boolean doNotSendMessage;

	/**
	 * Time used stall.
	 */
	public long stoleFromStallTime;

	/**
	 * True, if anti-fire event is being used.
	 */
	public boolean antiFireEvent;

	/**
	 * True, if stamina potion event is being used.
	 */
	public boolean staminaEvent;

	/**
	 * Save the last dialogue sent, used for shop 'back' button.
	 */
	public int lastDialogueSelected;

	/**
	 * Save npc damage mask time.
	 */
	public long npcDamageMaskTime;

	/**
	 * True, if the special attack used is a ranged weapon. Used for npcs.
	 */
	public boolean rangedSpecialAttackOnNpc;

	/**
	 * The x-coordinate of the player when the agility action ends. Also used for teleporting player to
	 * this location if server forces log out all players.
	 */
	public int agilityEndX;

	/**
	 * The y-coordinate of the player when the agility action ends. Also used for teleporting player to
	 * this location if server forces log out all players.
	 */
	public int agilityEndY;

	/**
	 * True to not call the playerTurn method.
	 */
	public boolean ignorePlayerTurn;

	/**
	 * The skill being used for the Skill cape master.
	 */
	public int skillCapeMasterSkill;

	/**
	 * The expression of the Skill cape master.
	 */
	public int skillCapeMasterExpression = 9850;

	/**
	 * Show option, attack, trade, follow etc
	 **/
	public String optionType = "null";

	/**
	 * True, to enable no-clipping for the player, for certain movements such as through doors.
	 */
	public boolean forceNoClip;

	/**
	 * Used to know what action the player is doing before summoning the amount interface, that is
	 * used for x amount for the bank and skill editing.
	 */
	private String amountInterface = "";

	/**
	 * Store the name of the skill that is being used for this interface.
	 */
	public String skillingInterface = "";

	/**
	 * Store the skilling data, used with skillingInterface String.
	 */
	public int[] skillingData = new int[10];

	/**
	 * Store the experience gained in a skill after reaching maxing it out.
	 * <p>
	 * Used for saving the extra experience after 99 for when quick set-up is used.
	 */
	public int[] combatExperienceGainedAfterMaxed = new int[7];

	/**
	 * Used for rapid heal.
	 */
	public int hitPointsRegenerationCount;

	/**
	 * True, if the Hit points regeneration event is running.
	 */
	public boolean hitPointsRegenerationEvent;

	/**
	 * Time left for the anti fire potion effect.
	 */
	private int antiFirePotionTimer;

	public int getAntiFirePotionTimer() {
		return antiFirePotionTimer;
	}

	public void setAntiFirePotionTimer(int antiFirePotionTimer) {
		this.antiFirePotionTimer = antiFirePotionTimer;
	}

	/**
	 * Time left for the stamina potion effect.
	 */
	private int staminaPotionTimer;

	public int getStaminaPotionTimer() {
		return staminaPotionTimer;
	}

	public void setStaminaPotionTimer(int staminaPotionTimer) {
		this.staminaPotionTimer = staminaPotionTimer;
	}

	/**
	 * True, if the anti fire potion effect is on.
	 */
	public boolean antiFirePotion;

	/**
	 * True, if the player is currently travelling to the item on object action.
	 */
	public boolean itemOnObjectEvent;

	/**
	 * True, if the player has the OSRS xp orb bar opened.
	 */
	public boolean useBottomRightWildInterface;

	/**
	 * Amount of agility points.
	 */
	public int agilityPoints;


	/**
	 * @return The amount of agility points.
	 */
	public int getAgilityPoints() {
		return agilityPoints;
	}

	/**
	 * Change te amount of agilityPoints variable.
	 *
	 * @param value The amount to set agilityPoints to.
	 */
	public void setAgilityPoints(int value) {
		agilityPoints = value;
	}

	/**
	 * Amount of merit points.
	 */
	public int meritPoints;

	/**
	 * @return The amount of merit points.
	 */
	public int getMeritPoints() {
		return meritPoints;
	}

	/**
	 * Set the amount of merit points.
	 *
	 * @param value The amount to change merit points to.
	 */
	public void setMeritPoints(int value) {
		meritPoints = value;
	}

	/**
	 * These are to be used for creating delays, using System.currentTimeMillis(), If there is a few
	 * objects close to the player, use a different delay variable for each one. The different
	 * variables are objectDelay1/2/3/4/5
	 */
	public long objectDelay1, objectDelay2, objectDelay3, objectDelay4, objectDelay5;

	/**
	 * Stepping stone at Brimhaven Dungeon cycle event integer.
	 */
	public byte stoneTimer;


	/**
	 * The amount of game ticks, the crumbling wall cycle event will last for.
	 */
	public int firstCrumblingWallActionEvent;

	/**
	 * The amount of game ticks, the log balance cycle event will last for.
	 */
	public int logBalanceActionEvent;

	public long agility1;

	public long agility2;

	public long agility3;

	public long agility4;

	public long agility5;

	public long agility6;

	public long agility7;


	public int smithingItem;

	public int smithingExperience;

	public int smithingBarToRemove;

	public int smithingRemoveamount;

	public int amountToSmith;

	public long meleeOutOfDistanceTime;

	public long wildernessAgilityCourseImmunity;

	public int[] pouchesPure = new int[4];

	public int[] pouchesRune = new int[4];

	public int[] specialObjectActionPoint = new int[6];

	public String currentAgilityArea = "";

	private int agilityCourseCompletedMessage = -1;

	/**
	 * True, if Wilderness course obstacle pipe has been used.
	 */
	public boolean wildernessCourseObstaclePipe;

	/**
	 * True, if Wilderness course log balance has been used.
	 */
	public boolean wildernessCourseLogBalance;

	/**
	 * True, if Wilderness course stepping stone has been used.
	 */
	public boolean wildernessCourseSteppingStone;

	/**
	 * True, if Wilderness course rope swing has been used.
	 */
	public boolean wildernessCourseRopeSwing;

	/**
	 * The NPC type of the slayer task.
	 */
	public int slayerTaskNpcType;

	/**
	 * The amount of slayer monsters to kill.
	 */
	public int slayerTaskNpcAmount;

	/**
	 * 0: ore identity.
	 * <p>
	 * 1: level required to mine the ore.
	 * <p>
	 * 2: experience to gain from the ore.
	 */
	public int[] oreInformation = new int[3];

	/**
	 * Amount of game ticks untill the ore is mined.
	 */
	public int miningTimer = 0;

	/**
	 * The amount of game ticks, the wood cutting event will last for.
	 */
	public int woodCuttingEventTimer;

	/**
	 * The amount of game ticks, the fishing event will last for.
	 */
	public int fishTimerAmount = 0;

	public boolean smeltInterface;

	public int[] farm = new int[2];

	public int[][] playerSkillProp = new int[20][15];

	public boolean seedPlanted;

	public boolean seedWatered;

	public boolean patchRaked;

	public boolean patchCleaned;

	public boolean logBalance;

	public boolean obstacleNetUp;

	public boolean treeBranchUp;

	public boolean balanceRope;

	public boolean treeBranchDown;

	public boolean obstacleNetOver;

	public boolean ropeSwing;

	public boolean logBalance1;

	public boolean obstacleNet;

	public boolean balancingLedge;

	public boolean Ladder;

	public boolean[] combatSkillsAnnounced = new boolean[7];

	public boolean smashVials;

	public boolean toggleSeedPod;

	public boolean toggleLootKey;

	/**
	 * Used to count how many players in Wilderness in the past 10 minutes on server print debug.
	 */
	public long wildernessEnteredTime;

	private boolean usingFightCaves;

	/**
	 * True, if the player can change edit combat stats.
	 */
	private boolean ableToEditCombat;

	private int meleeMainKills;

	private int hybridKills;

	private int berserkerPureKills;

	private int pureKills;

	private int rangedTankKills;

	private int f2pKills;

	/**
	 * Melee, hybrid, berserker, pure, ranged, f2p.
	 */
	public int[] deathTypes = new int[6];

	public String lastPlayerKilled = "";

	public long timeFightStarted;

	public String lastPlayerAttackedName = "";

	public long timeMeleeUsed;

	public long timeRangedUsed;

	public long timeMagicUsed;

	/**
	 * The game mode chosen.
	 */
	private String gameMode = "NONE";

	public String gameModeTitle = "";

	public String playerTitle = "";

	public String titleColour = "<col=ED700E>";

	/**
	 * Used to restore special attack by 5%.
	 */
	public int specialAttackRestoreTimer;

	/**
	 * True, if music is being looped.
	 */
	public boolean isLoopingMusic;

	/**
	 * True, if auto music is turned on.
	 */
	public boolean autoMusic;

	/**
	 * Save the current song playing, to use when next song is to be played.
	 */
	public int currentMusicOrder = -1;


	/**
	 * True, if the Ranged ammo used will be dropped on the floor.
	 */
	public boolean ammoDropped;

	public boolean getAmmoDropped() {
		return ammoDropped;
	}

	public void setAmmoDropped(boolean state) {
		ammoDropped = state;
	}

	/**
	 * True, if the player is casting magic. This is used to confirm that the player is magic
	 * following.
	 * <p>
	 * We cannot use isUsingMagic() because isUsingMagic() turns to false after the hitsplat appears
	 * to stop the player from re-attacking.
	 */
	private boolean lastCastedMagic;

	/**
	 * True, to reset face update at the end of the tick.
	 * <p>
	 * Used to stop the player from permanently facing the target after being used once.
	 */
	private boolean faceResetAtEndOfTick;

	public boolean getFaceResetAtEndOfTick() {
		return faceResetAtEndOfTick;
	}

	public void setFaceResetAtEndOfTick(boolean state) {
		faceResetAtEndOfTick = state;
	}

	public boolean getInitiateCombatEvent() {
		return initiateCombatEvent;
	}

	public void setInitiateCombatEvent(boolean initiateCombatEvent) {
		this.initiateCombatEvent = initiateCombatEvent;
	}

	private boolean initiateCombatEvent;

	/**
	 * The NPC identity being attacked by the player.
	 */
	public int npcIdentityAttacking;

	/**
	 * @return The NPC identity being attacked by the player.
	 */
	public int getNpcIdAttacking() {
		return npcIdentityAttacking;
	}

	/**
	 * @param value The value to change npcIdentityAttacking into.
	 */
	public void setNpcIdentityAttacking(int value) {
		npcIdentityAttacking = value;
		setLastNpcAttackedIndex(value);
	}

	/**
	 * Reset npcIdentityAttacking to 0.
	 */
	public void resetNpcIdentityAttacking() {
		npcIdentityAttacking = 0;
	}

	/**
	 * The player identity to follow.
	 */
	public int playerIdToFollow;

	/**
	 * @return The player identity to follow.
	 */
	public int getPlayerIdToFollow() {
		return playerIdToFollow;
	}

	/**
	 * @param value The value to change the playerIdToFollow into.
	 */
	public void setPlayerIdToFollow(int value) {
		playerIdToFollow = value;
	}

	public void resetPlayerIdToFollow() {
		playerIdToFollow = 0;
	}

	/**
	 * The NPC identity to follow.
	 */
	public int npcIdToFollow;

	/**
	 * @return The NPC identity to follow.
	 */
	public int getNpcIdToFollow() {
		return npcIdToFollow;
	}

	/**
	 * @param value The value to change the npcIdToFollow into.
	 */
	public void setNpcIdToFollow(int value) {
		npcIdToFollow = value;
	}

	public void resetNpcIdToFollow() {
		npcIdToFollow = 0;
	}

	/**
	 * True, if vengeance is casted.
	 */
	public boolean vengeance;

	/**
	 * @return The toggle of vengeance.
	 */
	public boolean getVengeance() {
		return vengeance;
	}

	/**
	 * @param state The toggle of vengeance to change to.
	 */
	public void setVengeance(boolean state) {
		vengeance = state;
	}

	/**
	 * True, if administrator tank mode.
	 */
	public boolean tank;

	public boolean getTank() {
		return tank;
	}

	public void setTank(boolean state) {
		tank = state;
	}

	/**
	 * Last animation identity used.
	 */
	private int lastAnimation;

	/**
	 * @return Last animation identity used.
	 */
	public int getLastAnimation() {
		return lastAnimation;
	}

	/**
	 * @param lastAnimation The value to change lastAnimation into.
	 */
	public void setLastAnimation(int lastAnimation) {
		this.lastAnimation = lastAnimation;
	}


	/**
	 * Store the time of when a skill was last boosted/drained.
	 */
	public long[] boostedTime = new long[7];

	/**
	 * True, if the drain skill event is being used.
	 */
	private boolean drainEvent;

	/**
	 * @return True, if the drain skill event is being used.
	 */
	public boolean isDrainEvent() {
		return drainEvent;
	}

	/**
	 * @param drainEvent The toggle to change drainEvent into.
	 */
	public void setDrainEvent(boolean drainEvent) {
		this.drainEvent = drainEvent;
	}

	/**
	 * True, to show tutorial arrows when player moves/accepts clothes.
	 */
	public boolean showTutorialArrows;

	/**
	 * Last time a quick set-up button was used.
	 */
	public long lastQuickSetUpClicked;


	/**
	 * Last time a quick set-up button was used.
	 */
	public long lastQuickchatButtonClicked;

	/**
	 * Store the time of when magic was used.
	 */
	private long lastUsedMagic;

	/**
	 * @return The time of when magic was last used.
	 */
	public long getLastUsedMagic() {
		return lastUsedMagic;
	}

	/**
	 * @param lastUsedMagic The time of when magic was used.
	 */
	public void setLastUsedMagic(long lastUsedMagic) {
		this.lastUsedMagic = lastUsedMagic;
	}

	/**
	 * Which difficulty tab was last clicked on the achievement interface.
	 */
	public String lastAchievementDifficulty = "EASY";

	/**
	 * Which difficulty tab was last clicked on the rewardsachievement interface.
	 */
	public String lastAchievementRewardsDifficulty = "EASY";

	/**
	 * Which column on the achievement interface was last clicked.
	 */
	public int lastAchievementColumn;

	/**
	 * Name of the last achievement that was clicked on.
	 */
	public String lastAchievementName;

	/**
	 * Amount of food ate.
	 */
	public int foodAte;

	/**
	 * @return Get the amount of food eaten.
	 */
	public int getFoodAte() {
		return foodAte;
	}

	/**
	 * Set the amount of foodAte.
	 *
	 * @param value The value of foodAte to change to.
	 */
	public void setFoodAte(int value) {
		foodAte = value;
	}

	/**
	 * Current kill streak.
	 */
	public int currentKillStreak;

	/**
	 * The record kill streak.
	 */
	public int killStreaksRecord;

	/**
	 * Save time of when account was created.
	 */
	public long timeOfAccountCreation;

	/**
	 * Store time of when leech animation was last used.
	 */
	public long lastLeechAnimation;

	/**
	 * Last time the player drank a potion.
	 */
	public long lastPotionSip = System.currentTimeMillis();

	/**
	 * Amount of tiles player has ran across.
	 */
	public int tilesWalked;

	/**
	 * True, if redemption or wrath has been activated.
	 */
	public boolean redemptionOrWrathActivated;

	/**
	 * Used to determine the last player who started the duo following for Runescape follow dancing.
	 */
	public boolean followLeader;

	/**
	 * Last time the player throwed a snowball.
	 */
	private long lastSnowBallThrowTime = System.currentTimeMillis();

	/**
	 * True, if the player is using the overload event
	 */
	public boolean overloadEvent;

	/**
	 * Used for timing the overload damage hitsplat.
	 */
	public int overloadTicks;

	/**
	 * Used for timing the overload reboosting time frame.
	 */
	public int overloadReboostTicks;

	/**
	 * Old x position of the player i'm following.
	 */
	public int oldX;

	/**
	 * Old y position of the player i'm following.
	 */
	public int oldY;

	/**
	 * True, if the player successfully drank a potion.
	 */
	public boolean showPotionMessage = true;

	/**
	 * True, if player is drinking an Extreme magic potion.
	 */
	public boolean extremeMagic;

	/**
	 * Store the time of when the dialgueAction integer was last changed.
	 */
	public long lastDialogueAction;

	/**
	 * 1 is normal interface, 2 is combat spells first, 3 is teleports spells first.
	 */
	public int ancientsInterfaceType = 1;

	/**
	 * True, if the player is following with melee.
	 */
	private boolean meleeFollow;

	/**
	 * Amount of Ring of recoil charges left.
	 */
	public int recoilCharges = 40;

	/**
	 * @return The amount of recoilCharges integer.
	 */
	public int getRecoilCharges() {
		return recoilCharges;
	}

	/**
	 * set the recoilCharges integer to the specified amount
	 *
	 * @param amount The amount to change the recoilCharges integer to.
	 */
	public void setRecoilCharges(int amount) {
		recoilCharges = amount;
	}

	/**
	 * Amount of Bracelet of ethereum charges left.
	 */
	public int braceletCharges;

	/**
	 * @return The amount of braceletCharges integer.
	 */
	public int getBraceletCharges() {
		return braceletCharges;
	}

	/**
	 * set the braceletCharges integer to the specified amount
	 *
	 * @param amount The amount to change the braceletCharges integer to.
	 */
	public void setBraceletCharges(int amount) {
		braceletCharges = amount;
	}

	// Holiday tool

	public int toolUses = 5;

	public int getToolUses() {
		return toolUses;
	}

	public void setToolUses(int amount) {
		toolUses = amount;
	}

	/**
	 * True, if the player is using bone on altar cycle event.
	 */
	public boolean usingBoneOnAltarEvent;

	/**
	 * Record the time of when the bone on altar animation was last used.
	 */
	public long boneOnAltarAnimation;

	/**
	 * True, to change what equipment and death icon on equipment tab does.
	 */
	public boolean clipping;

	/**
	 * True, if the player has auto-casting turned on.
	 */
	public boolean autoCasting;

	/**
	 * Get the autocasting toggle.
	 *
	 * @return The toggle of autocasting.
	 */
	public boolean getAutoCasting() {
		return autoCasting;
	}

	/**
	 * Change the toggle of autoCasting.
	 *
	 * @param state The toggle of autoCasting.
	 */
	public void setAutoCasting(Boolean state) {
		autoCasting = state;
	}

	/**
	 * Each special attack weapon has its own slot, if slot is 1 means it's a single hitsplat weapon,
	 * 2 means double hitsplat. 29 and above is free. When using double damage weapon, leave the next
	 * index after it free.
	 */
	public int[] specialAttackWeaponUsed = new int[35];

	/**
	 * Store the maximum hit of a special attack weapon, same order as specialAttackWeaponUsed array.
	 */
	public int[] maximumSpecialAttack = new int[35];

	public int[] maximumSpecialAttackNpc = new int[35];

	/**
	 * Store the amount of times a special attack weapon was used, same order as
	 * specialAttackWeaponUsed array.
	 */
	public int[] weaponAmountUsed = new int[35];

	public int[] weaponAmountUsedNpc = new int[35];

	/**
	 * Add to the amount a weapon has been used.
	 *
	 * @param index The index of the weapon.
	 */
	public void setWeaponAmountUsed(int index) {
		if (againstPlayer) {
			weaponAmountUsed[index]++;
		} else {
			weaponAmountUsedNpc[index]++;
		}
	}

	/**
	 * True if player is against player, used to save special attack amount used.
	 */
	public boolean againstPlayer;

	/**
	 * Melee, ranged, magic, vengeance, recoil, dfs. Pvp only, it is in the Pking tab on the Profile.
	 */
	public int[] totalDamage = new int[6];

	/**
	 * Store the time of when the last saveMaximumDamage method was used.
	 */
	public long lastSpecialAttackSaved;

	/**
	 * Store the single hitsplat damage of a special attack weapon.
	 */
	public int firstHitSplatDamage;

	/**
	 * The name of the last clan chat joined.
	 */
	public String lastClanChatJoined = "" + ServerConstants.getServerName() + "";

	/**
	 * Amount of targets killed.
	 */
	public int targetsKilled;

	/**
	 * Amount of times died from target.
	 */
	public int targetDeaths;

	/**
	 * Amount of Barrows brothers killed.
	 */
	public int barrowsKillCount;

	/**
	 * 0 if the player is not transformed.
	 */
	private int transformed;

	/**
	 * 5 is easter egg only, 1 is monkey.
	 */
	public int getTransformed() {
		return transformed;
	}

	/**
	 * True, if the player has transformed into an Egg.
	 */
	public boolean isAnEgg;

	/**
	 * The total amount of votes the player has accumulated.
	 */
	public int voteTotalPoints;

	/**
	 * This is used to extinguish different Clue scroll quests.
	 */
	public int clueScrollType = -1;

	/**
	 * True, if the player is using the summon pet cycle event.
	 */
	public boolean isUsingSummonPetEvent;

	/**
	 * Used to stop block emote being used an instant after the attack emote is called. Which ends up
	 * cancelling the attack emote.
	 */
	public long lastAttackAnimationTimer;

	/**
	 * Potion sip timer.
	 */
	public byte timer;

	/**
	 * The amount of ticks the startInterfaceEvent will last for.
	 */
	public int extraTime;

	/**
	 * True, if the startSkullTimerEvent is active.
	 */
	public boolean isUsingSkullTimerEvent;

	/**
	 * True, if the player is logging out manually.
	 */
	public boolean manualLogOut;

	/**
	 * The last bank tab the player had opened.
	 */
	public byte lastBankTabOpened;

	/**
	 * Change the amount of lastBankTabOpened
	 *
	 * @param value The amount of lastbankTabOpened
	 */
	public void setLastBankTabOpened(byte value) {
		lastBankTabOpened = value;
	}

	/**
	 * @return The amount of lastBankTabOpened.
	 */
	public byte getLastBankTabOpened() {
		return lastBankTabOpened;
	}

	/**
	 * Set the toggle of inventoryUpdate.
	 *
	 * @param state State of inventoryUpdate.
	 */
	public void setInventoryUpdate(boolean state) {
		inventoryUpdate = state;
	}

	/**
	 * @return The toggle of inventoryUpdate.
	 */
	public boolean getInventoryUpdate() {
		return inventoryUpdate;
	}

	/**
	 * True, if the inventory needs visual updating.
	 */
	private boolean inventoryUpdate;

	/**
	 * Store the time of when the casket was last opened.
	 */
	public long casketTime;

	/**
	 * The total amount of the item scanned in the bank and inventory.
	 */
	public int quantityOfItem;

	/**
	 * The damage of the Morrigan's javelin special attack to deal, either 5 or less, depending on
	 * the victim's hitpoints.
	 */
	public int morrigansJavelinDamageToDeal;

	/**
	 * Amount of Morrigan's javelin special attack damages to apply.
	 */
	public int amountOfDamages;

	/**
	 * True, if Morrigan's javelin special attack is being used.
	 */
	public boolean morrigansJavelinSpecialAttack;

	/**
	 * The time of when the last anti-poison potion was taken.
	 */
	public long lastPoisonSip;

	/**
	 * The amount of time of immunity to poison.
	 */
	public long poisonImmune;

	/**
	 * Store the time of when the venom immunity will expire.
	 */
	public long venomImmunityExpireTime;

	/**
	 * The amount of times a venom damage cycle has taken place
	 */
	public int venomHits;

	/**
	 * The ip that connected to this account before this session.
	 */
	public String lastSavedIpAddress = "";

	/**
	 * The time of when the player logged out.
	 */
	public long logOutTime = System.currentTimeMillis();

	/**
	 * True if Saradomin special attack is activated.
	 */
	public boolean saradominSwordSpecialAttack;

	/**
	 * True, if the player is using a special attack that causes multiple damage. e.g: Dragon dagger,
	 * Dragon claws and Dragon halberd.
	 */
	public boolean multipleDamageSpecialAttack;

	/**
	 * Get the toggle of multipleDamageSpecialAttack.
	 *
	 * @return The toggle of multipleDamageSpecialAttack.
	 */
	public boolean getMultipleDamageSpecialAttack() {
		return multipleDamageSpecialAttack;
	}

	/**
	 * Change the toggle of multipleDamageSpecialAttack.
	 *
	 * @param state The toggle of multipleDamageSpecialAttack.
	 */
	public void setMultipleDamageSpecialAttack(boolean state) {
		multipleDamageSpecialAttack = state;
	}

	/**
	 * True, if the player is using Dragon claws special attack.
	 */
	public boolean dragonClawsSpecialAttack;

	/**
	 * Change the toggle of dragonClawsSpecialAttack.
	 *
	 * @param state The toggle of dragonClawsSpecialAttack.
	 */
	public void setDragonClawsSpecialAttack(boolean state) {
		dragonClawsSpecialAttack = state;
	}

	/**
	 * Get the toggle of dragonClawsSpecialAttack.
	 *
	 * @return The toggle of dragonClawsSpecialAttack.
	 */
	public boolean getDragonClawsSpecialAttack() {
		return dragonClawsSpecialAttack;
	}

	/**
	 * True, if the player is using the dragon claws special attack event.
	 */
	public boolean usingDragonClawsSpecialAttackEvent;

	/**
	 * Get the toggle of usingDragonClawsSpecialAttackEvent.
	 *
	 * @return The toggle of usingDragonClawsSpecialAttackEvent.
	 */
	public boolean getUsingDragonClawsSpecialAttackEvent() {
		return usingDragonClawsSpecialAttackEvent;
	}

	/**
	 * Set the toggle of usingDragonClawsSpecialAttackEvent.
	 *
	 * @param state The toggle of usingDragonClawsSpecialAttackEvent.
	 */
	public void setUsingDragonClawsSpecialAttackEvent(boolean state) {
		usingDragonClawsSpecialAttackEvent = state;
	}

	/**
	 * True, if the player has a red skull.
	 */
	public boolean redSkull;

	/**
	 * Change the toggle of redSkull.
	 *
	 * @param state The toggle of redSkull.
	 */
	public void setRedSkull(boolean state) {
		redSkull = state;
	}

	/**
	 * Get the toggle of redSkull.
	 *
	 * @return The toggle of redSkull.
	 */
	public boolean getRedSkull() {
		return redSkull;
	}

	/**
	 * True, if the player has a white skull.
	 */
	public boolean whiteSkull;

	/**
	 * Change the toggle of whiteSkull.
	 *
	 * @param state The toggle of whiteSkull.
	 */
	public void setWhiteSkull(boolean state) {
		whiteSkull = state;
	}

	/**
	 * Get the toggle of whiteSkull.
	 *
	 * @return The toggle of whiteSkull.
	 */
	public boolean getWhiteSkull() {
		return whiteSkull;
	}

	/**
	 * Amount of milliseconds, the player has been online for, in this session.
	 */
	public long millisecondsOnline;

	/**
	 * The amount of seconds, the player has been online for.
	 */
	public int secondsBeenOnline;

	public int gameTicksOnline;

	/**
	 * The date of when the account was created.
	 */
	public String accountDateCreated = "";

	/**
	 * True, if the special attack event is being used.
	 */
	public boolean specialAttackEvent;

	/**
	 * Store the time of the last Wolpertinger special attack used.
	 */
	public long lastWolpertingerSpecialAttack;

	/**
	 * @param amount Change the player's Hitpoints to this.
	 */
	public void setHitPoints(int amount) {
		if (this.getDead()) {
			return;
		}
		currentCombatSkillLevel[ServerConstants.HITPOINTS] = amount;
		Skilling.updateSkillTabFrontTextMain(this, ServerConstants.HITPOINTS);
	}

	/**
	 * Duel arena stakes done.
	 */
	public int duelArenaStakes;

	/**
	 * Trades completed.
	 */
	public int tradesCompleted;

	/**
	 * Amount of times used teleport.
	 */
	public int teleportsUsed;

	/**
	 * Amount of times the player has died in a safe area.
	 */
	public int safeDeaths;

	/**
	 * Amount of times the player has killed another player in a safe area.
	 */
	public int safeKills;

	/**
	 * Amount of potion doses drank.
	 */
	public int potionDrank;

	/**
	 * KDR of the player, used for highscores.
	 */
	public int kdr;

	/**
	 * True, if the resting event is active.
	 */
	public boolean restingEvent;

	/**
	 * True, if the player is resting.
	 */
	public boolean resting;

	/**
	 * The delay in milliseconds to gain energy.
	 */
	public int agilityRestoreDelay = 3000;

	/**
	 * Run energy remaining.
	 */
	public double runEnergy = 100;

	public boolean energyGainEvent;

	/**
	 * Store the time of the last time the player had their run energy restored.
	 */
	public long lastRunRecovery;

	/**
	 * True, if the player is running.
	 */
	public boolean isRunning() {
		return isNewWalkCmdIsRunning() || (runModeOn && isMoving());
	}

	/**
	 * Store the time of when a clan chat message was last sent by the player.
	 */
	public long clanChatMessageTime;

	/**
	 * True, if the player is dead.
	 */
	public boolean dead;

	public boolean getDead() {
		return dead;
	}

	/**
	 * Change the toggle of isDead.
	 */
	public void setDead(boolean state) {
		dead = state;
	}

	/**
	 * True, to call the familiar.
	 */
	public boolean forceCallFamiliar;

	/**
	 * Last time familiar called.
	 */
	public long callFamiliarTimer = System.currentTimeMillis();

	/**
	 * True, if the player has summoned a pet.
	 */
	private boolean petSummoned;

	/**
	 * True if the second pet is summoned.
	 */
	private boolean secondPetSummoned;

	/**
	 * The toggle of petSummoned.
	 *
	 * @return The toggle of petSummoned.
	 */
	public boolean getPetSummoned() {
		return petSummoned;
	}

	/**
	 * Change the toggle of petSummoned.
	 *
	 * @param state The toggle of petSummoned.
	 */
	public void setPetSummoned(boolean state) {
		petSummoned = state;
	}

	/**
	 * The NPC type of the pet that the player currently has summoned.
	 */
	private int petId;

	/**
	 * The second pet id summoned.
	 */
	private int secondPetId;

	/**
	 * True if the ladder event is being used.
	 */
	public boolean ladderEvent;

	/**
	 * True, if the idle Event is in use.
	 */
	public boolean idleEventUsed;

	/**
	 * This will keep increasing by +1 when the player is not sending any packets to the server. If
	 * this is 3 or more, then the player is not sending any connections to the server.
	 * <p>
	 * This is used to disconnect the player, if in-combat, after 40 seconds.
	 */
	private int timeOutCounter = 0;

	/**
	 * Is the clickNpcTypeEvent1 being used?
	 */
	public boolean usingClickNpcType1Event;

	/**
	 * Is the clickNpcTypeEvent2 being used?
	 */
	public boolean usingClickNpcType2Event;

	/**
	 * Is the clickNpcTypeEvent3 being used?
	 */
	public boolean usingClickNpcType3Event;

	/**
	 * Is the clickNpcTypeEvent4 being used?
	 */
	public boolean usingClickNpcType4Event;

	/**
	 * Is the clickObject1Event active?
	 */
	public boolean doingClickObjectType1Event;

	/**
	 * Is the clickObject2Event active?
	 */
	public boolean doingClickObjectType2Event;

	/**
	 * Is the clickObject3Event active?
	 */
	public boolean doingClickObjectType3Event;

	/**
	 * if true, the player cannot perform any action and is performing agility.
	 */
	private boolean doingAgility;

	/**
	 * The toggle of doingAgility.
	 *
	 * @return The toggle of doingAgility.
	 */
	public boolean getDoingAgility() {
		return doingAgility;
	}

	/**
	 * Change the toggle of doingAgility.
	 *
	 * @param state The toggle of doingAgility.
	 */
	public void setDoingAgility(boolean state) {
		doingAgility = state;
	}

	/**
	 * True, if the interface Cycle Event is being used.
	 */
	public boolean isUsingInterfaceEvent;

	/**
	 * The range damage for a single hit.
	 */
	public int rangedFirstDamage;

	/**
	 * The range damage for a double hit.
	 */
	public int rangedSecondDamage;

	/**
	 * True, to show the Diamond bolts (e) GFX during the hitsplat.
	 */
	public boolean showDiamondBoltGFX;

	/**
	 * True, to show the Onyx bolts (e) GFX during the hitsplat.
	 */
	public boolean showOnyxBoltGfx;

	/**
	 * True, to show the Dragon bolts (e) GFX during the hitsplat.
	 */
	public boolean showDragonBoltGFX;

	/**
	 * True, to show the Ruby bolts (e) GFX during the hitsplat.
	 */
	public boolean showRubyBoltGFX;

	/**
	 * True, to show the Opal bolts (e) GFX during the hitsplat.
	 */
	public boolean showOpalBoltGFX;

	/**
	 * Store the maximum damage of the player.
	 */
	public int maximumDamageRanged;

	/**
	 * * Store the normal single hit damage of a melee weapon..
	 */
	public int meleeFirstDamage;

	public int graniteMaulSpecialDamage;

	public int graniteMaulSpecialCriticalDamage;

	public boolean isGraniteMaulSpecial;

	/**
	 * * Store the second hit of a Dragon dagger or Halbred special attack.
	 */
	public int meleeSecondDamage;

	/**
	 * Store the third hit of a Dragon claw special attack.
	 */
	public int meleeThirdDamage;

	/**
	 * Store the fourth hit of a Dragon claw special attack.
	 */
	public int meleeFourthDamage;

	/**
	 * Maximum damage of Melee.
	 */
	public int maximumDamageMelee;

	/**
	 * Magic damage.
	 */
	private int magicDamage;

	/**
	 * Maximum damage of Magic.
	 */
	private int maximumDamageMagic;

	/**
	 * Amount of 600ms cycles untill the teleport finishes.
	 */
	private int teleportCycle;

	/**
	 * True, if the player is teleporting. Use this to check if a player is teleporting.
	 */
	public boolean isTeleporting() {
		return teleporting;
	}

	/**
	 * True, if Magic bow special attack is being used.
	 */
	private boolean magicBowSpecialAttack;

	/**
	 * True, if using Dark bow to start a normal attack.
	 */
	private boolean usingDarkBowNormalAttack;

	/**
	 * True if magic will splash.
	 */
	private boolean magicSplash;

	public int[] baseSkillLevel = new int[23];

	public int[] currentCombatSkillLevel = new int[7];

	public int getCurrentCombatSkillLevel(int combatSkill) {
		return currentCombatSkillLevel[combatSkill];
	}

	/**
	 * Experience in a skill.
	 */
	public int[] skillExperience = new int[23];


	/**
	 * Store the player's experience before they enter the tournament on the Economy server.
	 */
	public int[] skillExperienceStoredBeforeTournament = new int[7];

	/**
	 * Store the player's base skill level before they enter the tournament on the Economy server.
	 */
	public int[] baseSkillLevelStoredBeforeTournament = new int[7];

	public int getBaseDefenceLevel() {
		return baseSkillLevel[ServerConstants.DEFENCE];
	}

	public int getBaseAttackLevel() {
		return baseSkillLevel[ServerConstants.ATTACK];
	}

	public int getBaseStrengthLevel() {
		return baseSkillLevel[ServerConstants.STRENGTH];
	}

	public int getBaseRangedLevel() {
		return baseSkillLevel[ServerConstants.RANGED];
	}

	public int getBasePrayerLevel() {
		return baseSkillLevel[ServerConstants.PRAYER];
	}

	public int getBaseMagicLevel() {
		return baseSkillLevel[ServerConstants.MAGIC];
	}

	/**
	 * @return The player's maximum hitpoints.
	 */
	public int getBaseHitPointsLevel() {
		return baseSkillLevel[ServerConstants.HITPOINTS];
	}

	/**
	 * The other player that is being attacked by this player.
	 */
	public int playerIdAttacking;

	public void setPlayerIdAttacking(int value) {
		playerIdAttacking = value;
		hasLastAttackedAPlayer = true;
	}

	/**
	 * True if the player has last interacted by attacking a player.
	 */
	public boolean hasLastAttackedAPlayer;

	public void resetPlayerIdAttacking() {
		playerIdAttacking = 0;
	}

	public int getPlayerIdAttacking() {
		return playerIdAttacking;
	}

	/**
	 * True, if the player is teleporting.
	 */
	private boolean teleporting;

	/**
	 * True, if the player is using range.
	 */
	public boolean usingRanged;

	/**
	 * The toggle of usingRange.
	 *
	 * @return The toggle of usingRange.
	 */
	public boolean getUsingRanged() {
		return usingRanged;
	}

	/**
	 * Change the toggle of usingRange.
	 *
	 * @param state The toggle of usingRange.
	 */
	public void setUsingRanged(boolean state) {
		usingRanged = state;
	}

	/**
	 * True if the player is a Normal player.
	 */
	public boolean isNormalRank() {
		return playerRights == 0;
	}

	/**
	 * True if the player is a Moderator.
	 */
	public boolean isModeratorRank() {
		return playerRights == 1 || playerRights == 2 || playerRights == 31 || isDeveloperRank();
	}

	/**
	 * 
	 * True if the player is a Developer.
	 * 
	 */
	public boolean isDeveloperRank() {
		return playerRights == 33;
	}

	/**
	 * True if the player is a head moderator rank.
	 */
	public boolean isHeadModeratorRank() {
		return playerRights == 31 || playerRights == 2;
	}

	/**
	 * True if the player is a Support.
	 */
	public boolean isSupportRank() {
		return playerRights == 10;
	}

	/**
	 * True if the player is a Youtuber rank.
	 */
	public boolean isYoutubeRank() {
		return playerRights == 21;
	}

	/**
	 * True if the player is an Administrator.
	 */
	public boolean isAdministratorRank() {
		return playerRights == 2;
	}

	/**
	 * Has the player finished logging in?
	 */
	public boolean loggingInFinished;

	/**
	 * True if the player is doing an action.
	 */
	public boolean doingAction() {
		if (doingActionTimer > 0) {
			return true;
		}
		return false;
	}

	/**
	 * if 1 or more, the player cannot do anything..
	 */
	public int doingActionTimer;

	/**
	 * Is the doingActionEvent being used?
	 */
	public boolean isUsingDoingActionEvent;

	/**
	 * True, if the player has finished the new player tutorial.
	 */
	private boolean tutorialComplete;

	/**
	 * Store the time of when this player has attacked another player.
	 */
	public long timeAttackedAnotherPlayer;

	public long getTimeAttackedAnotherPlayer() {
		return timeAttackedAnotherPlayer;
	}

	public void setTimeAttackedAnotherPlayer(long value) {
		timeAttackedAnotherPlayer = value;
	}

	/**
	 * Store the time of when this player has been under attack by another player.
	 */
	public long timeUnderAttackByAnotherPlayer;

	public long getTimeUnderAttackByAnotherPlayer() {
		return timeUnderAttackByAnotherPlayer;
	}

	public void setTimeUnderAttackByAnotherPlayer(long value) {
		timeUnderAttackByAnotherPlayer = value;
		timeUnderAttackByAnotherPlayerOther = value;
	}

	/**
	 * Exactly same as timeUnderAttackByAnotherPlayerOther except that this variable is also called when
	 * a recoil hits the player or venom etc..
	 */
	public long timeUnderAttackByAnotherPlayerOther;

	/**
	 * Used for log out timer, this only resets upon death.
	 */
	public long timeNpcAttackedPlayerLogOutTimer;

	/**
	 * Store the time of when the NPC last attacked the player.
	 */
	public long timeNpcAttackedPlayer;

	public long getTimeNpcAttackedPlayer() {
		return timeNpcAttackedPlayer;
	}

	public void setTimeNpcAttackedPlayer(long value) {
		timeNpcAttackedPlayer = value;
	}

	/**
	 * The players i have recently attacked. Used for skulling.
	 */
	public ArrayList<String> attackedPlayers = new ArrayList<String>();

	/**
	 * The poison damage to appear.
	 */
	public int poisonDamage;

	/**
	 * The amount of poison hitsplats left of the current poisonDamage.
	 */
	public int poisonHitsplatsLeft;

	/**
	 * Amount of game ticks left untill the next poison hitsplat damage.
	 */
	public int poisonTicksUntillDamage;

	/**
	 * The venom damage to appear.
	 */
	public int venomDamage = 6;

	/**
	 * The amount of poison hitsplats left of the current venomDamage.
	 */
	public int venomHitsplatsLeft;

	/**
	 * Amount of game ticks left until the next venom hitsplat damage.
	 */
	public int venomTicksUntillDamage;

	/**
	 * Amount of achievement points.
	 */
	public int achievementPoint;

	/**
	 * Amount of achievement points history.
	 */
	public int achievementPointHistory;

	/**
	 * Amount of times the player died in a dangerous area.
	 */
	public int wildernessDeaths;

	/**
	 * Change wildernessDeaths integer.
	 *
	 * @param value The value to set the wildernessDeaths integer to.
	 */
	public void setWildernessDeaths(int value) {
		wildernessDeaths = value;
	}

	/**
	 * @param countWildernessDeathsReset TODO
	 * @return The wildernessDeaths integer value.
	 */
	public int getWildernessDeaths(boolean countWildernessDeathsReset) {
		if (countWildernessDeathsReset) {
			return wildernessDeaths + wildernessDeathsReset;
		}
		return wildernessDeaths;
	}

	/**
	 * Amount of players killed in a dangeorus area.
	 */
	private int wildernessKills;

	/**
	 * Change wildernessKills integer.
	 *
	 * @param value The value to set the wildernessKills integer to.
	 */
	public void setWildernessKills(int value) {
		wildernessKills = value;
	}

	/**
	 * @param countResettedWildernessKills TODO
	 * @return The wildernessKills integer value.
	 */
	public int getWildernessKills(boolean countResettedWildernessKills) {
		if (countResettedWildernessKills) {
			return wildernessKills + wildernessKillsReset;
		}
		return wildernessKills;
	}

	/**
	 * Total level.
	 */
	private int totalLevel;

	/**
	 * Total experience in all skills.
	 */
	private long xpTotal;

	/**
	 * Quick prayers of normal prayers.
	 */
	public boolean[] quickPrayers = new boolean[QuickPrayers.MAX_PRAYERS];

	/**
	 * True if quick prayers of normal prayers are active.
	 */
	public boolean quickPray;

	/**
	 * The part of the Completionist cape that is currently being changed.
	 */
	public String partOfCape = "";

	/**
	 * The top detail colour of the Completionist cape.
	 */
	public int compColor1 = 685;

	/**
	 * The top colour of the Completionist cape.
	 */
	public int compColor2 = 685;

	/**
	 * The bottom detail colour of the Completionist cape.
	 */
	public int compColor3 = 685;

	/**
	 * The bottom colour of the Completionist cape.
	 */
	public int compColor4 = 685;

	/**
	 * The identity of the latest attacker (other player) that attacked this player. This is only
	 * reset when the player dies.
	 */
	private int lastAttackedBy;

	/**
	 * The identity of an NPC, in order of which NPC was spawned first. So, the first NPC spawned in
	 * spawns.cfg gets the value +1. When an NPC is created by summoning a pet for example, it gets
	 * the next unoccupied lowest number. This integer is used to identify the exact NPC the player
	 * clicked on and once all the data is put to use by knowing the exact NPC the player clicked on,
	 * the value is returned to 0.
	 */
	private int npcClickIndex;

	private int animationRequest = -1;

	public int getAnimationRequest() {
		return animationRequest;
	}

	public void setAnimationRequest(int animationRequest) {
		this.animationRequest = animationRequest;
	}

	private int FocusPointX = 0;

	public int getFocusPointX() {
		return FocusPointX;
	}

	public void setFocusPointX(int focusPointX) {
		FocusPointX = focusPointX;
	}

	private int FocusPointY = 0;

	public int getFocusPointY() {
		return FocusPointY;
	}

	public void setFocusPointY(int focusPointY) {
		FocusPointY = focusPointY;
	}

	private boolean faceUpdateRequired;

	protected boolean isFaceUpdateRequired() {
		return faceUpdateRequired;
	}

	protected void setFaceUpdateRequired(boolean faceUpdateRequired) {
		this.faceUpdateRequired = faceUpdateRequired;
	}

	private int face = -1;

	public int getFace() {
		return face;
	}

	public void setFace(int face) {
		this.face = face;
	}

	private int duelStatus;

	/**
	 * public final static int NOT_DUELING = 0; <br>
	 * public final static int IN_DUEL_INTERFACE = 1; <br>
	 * public final static int ON_FIRST_SCREEN_ACCEPTED = 2; <br>
	 * public final static int ON_SECOND_SCREEN = 3; <br>
	 * public final static int ON_SECOND_SCREEN_ACCEPTED = 4; <br>
	 * public final static int DUEL_STARTED = 5; <br>
	 * public final static int DUEL_WON = 6;
	 *
	 * @return
	 */
	public int getDuelStatus() {
		return duelStatus;
	}

	/**
	 * public final static int NOT_DUELING = 0; public final static int IN_DUEL_INTERFACE = 1; public
	 * final static int ON_FIRST_SCREEN_ACCEPTED = 2; public final static int ON_SECOND_SCREEN = 3;
	 * public final static int ON_SECOND_SCREEN_ACCEPTED = 4; public final static int DUEL_STARTED =
	 * 5;
	 *
	 * @return
	 */
	public void setDuelStatus(int duelStatus) {
		this.duelStatus = duelStatus;
	}

	public int getDuelCount() {
		return duelCount;
	}

	public void setDuelCount(int duelCount) {
		this.duelCount = duelCount;
	}

	/**
	 * True, if the player is casting magic. This is used to confirm that the player is magic
	 * following.
	 * <p>
	 * We cannot use isUsingMagic() because it turns to false after the hitsplat appears to stop the
	 * player from re-attacking.
	 */
	public boolean hasLastCastedMagic() {
		return lastCastedMagic;
	}

	/**
	 * True, if the player is casting magic. This is used to confirm that the player is magic
	 * following.
	 * <p>
	 * We cannot use isUsingMagic() because isUsingMagic() turns to false after the hitsplat appears
	 * to stop the player from re-attacking.
	 */
	public void setLastCastedMagic(boolean lastCastedMagic) {
		this.lastCastedMagic = lastCastedMagic;
	}

	public boolean isUsingMediumRangeRangedWeapon() {
		return usingMediumRangeRangedWeapon;
	}

	public void setUsingMediumRangeRangedWeapon(boolean state) {
		this.usingMediumRangeRangedWeapon = state;
	}

	public boolean isWieldingRangedWeaponWithNoArrowSlotRequirement() {
		return isWieldingRangedWeaponWithNoArrowSlotRequirement;
	}

	public void setIsWieldingRangedWeaponWithNoArrowSlotRequirement(boolean state) {
		this.isWieldingRangedWeaponWithNoArrowSlotRequirement = state;
	}

	/**
	 * @return True, if the player is following with melee.
	 */
	public boolean isMeleeFollow() {
		return meleeFollow;
	}

	/**
	 * True, if the player is following with melee.
	 */
	public void setMeleeFollow(boolean meleeFollow) {
		this.meleeFollow = meleeFollow;
	}

	public void setTransformed(int transformed) {
		this.transformed = transformed;
	}

	public void setTeleporting(boolean teleporting) {
		this.teleporting = teleporting;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public long getLastSnowBallThrowTime() {
		return lastSnowBallThrowTime;
	}

	public void setLastSnowBallThrowTime(long lastSnowBallThrowTime) {
		this.lastSnowBallThrowTime = lastSnowBallThrowTime;
	}
	
	public boolean getMiasmicTimer() {
		if (!GameType.isPreEoc()) {
			return false;
		}
		if (miasmicSpeedTime < 1) {
			return false;
		}
		if (getSpellId() > 0) {
			return false;
		}
		long elapsed = System.currentTimeMillis() - miasmicSpeedTimeElapsed / 1000;
		if (elapsed < miasmicSpeedTime) {
			return true;
		}
		miasmicSpeedTime = 0;
		return false;
	}

	public int getAttackTimer() {
		if (getMiasmicTimer()) {
			return attackTimer * 2;
		}
		return attackTimer;
	}

	public void setAttackTimer(int attackTimer) {
		this.attackTimer = attackTimer;
	}

	public int getSpecEffect() {
		return specEffect;
	}

	public void setSpecEffect(int specEffect) {
		this.specEffect = specEffect;
	}

	public int getDroppedRangedWeaponUsed() {
		return droppedRangedWeaponUsed;
	}

	public void setDroppedRangedItemUsed(int rangeItemUsed) {
		this.droppedRangedWeaponUsed = rangeItemUsed;
	}

	public int getProjectileStage() {
		return projectileStage;
	}

	public void setProjectileStage(int projectileStage) {
		this.projectileStage = projectileStage;
	}

	public void setX(int playerX) {
		this.playerX = playerX;
	}

	public int setY(int playerY) {
		this.playerY = playerY;
		return playerY;
	}

	/**
	 * The time the player was frozen.
	 */
	public long timeFrozen = 0;

	/**
	 * The amount of time the player won't be able to move for. 8000 means 8 seconds.
	 */
	private long frozenLength = 0;

	public boolean isFrozen() {
		if (System.currentTimeMillis() - timeFrozen >= getFrozenLength()) {
			return false;
		}
		return true;
	}

	/**
	 * True if the player can be frozen.
	 */
	public boolean canBeFrozen() {
		if (System.currentTimeMillis() - timeFrozen >= (getFrozenLength() + 2900)) {
			return true;
		}
		return false;
	}

	public int getAutocastId() {
		return autocastId;
	}

	public void setAutocastId(int autocastId) {
		this.autocastId = autocastId;
	}

	public boolean isUsingSpecial() {
		return usingSpecial;
	}

	public void setUsingSpecialAttack(boolean usingSpecial) {
		this.usingSpecial = usingSpecial;
	}

	public boolean isMagicBowSpecialAttack() {
		return magicBowSpecialAttack;
	}

	public void setMagicBowSpecialAttack(boolean magicBowSpecialAttack) {
		this.magicBowSpecialAttack = magicBowSpecialAttack;
	}

	public boolean isUsingDarkBowSpecialAttack() {
		return usingDarkBowSpecialAttack;
	}

	public void setUsingDarkBowSpecialAttack(boolean usingDarkBowSpecialAttack) {
		this.usingDarkBowSpecialAttack = usingDarkBowSpecialAttack;
	}

	public boolean isUsingDarkBowNormalAttack() {
		return usingDarkBowNormalAttack;
	}

	public void setUsingDarkBowNormalAttack(boolean usingDarkBowNormalAttack) {
		this.usingDarkBowNormalAttack = usingDarkBowNormalAttack;
	}

	public boolean isMagicSplash() {
		return magicSplash;
	}

	public void setMagicSplash(boolean magicSplash) {
		this.magicSplash = magicSplash;
	}

	public int getMagicDamage() {
		return magicDamage;
	}

	public int setMagicDamage(int magicDamage) {
		this.magicDamage = magicDamage;
		return magicDamage;
	}

	public int getUnderAttackBy() {
		return underAttackBy;
	}

	public void setUnderAttackBy(int underAttackBy) {
		this.underAttackBy = underAttackBy;
	}

	public int getLastAttackedBy() {
		return lastAttackedBy;
	}

	public void setLastAttackedBy(int lastAttackedBy) {
		this.lastAttackedBy = lastAttackedBy;
	}

	public int getHitDelay() {
		return hitDelay;
	}

	public void setHitDelay(int hitDelay) {
		this.hitDelay = hitDelay;
	}

	public int getCombatStyle() {
		return combatStyle;
	}

	public void setCombatStyle(int combatStyle) {
		this.combatStyle = combatStyle;
	}

	public int getOldSpellId() {
		return oldSpellId;
	}

	public void setOldSpellId(int oldSpellId) {
		this.oldSpellId = oldSpellId;
	}

	public boolean isMoving() {
		return isMoving;
	}

	public void setMoving(boolean isMoving) {
		this.isMoving = isMoving;
	}

	public int getDuelingWith() {
		return duelingWith;
	}

	public void setDuelingWith(int duelingWith) {
		this.duelingWith = duelingWith;
	}

	public int getTradingWithId() {
		return tradeWith;
	}

	public void setTradeWith(int tradeWith) {
		this.tradeWith = tradeWith;
	}

	/**
	 * As long as this is true, the next hitsplat will be a magic one.
	 */
	private boolean usingMagic;

	public boolean isUsingMagic() {
		return usingMagic;
	}

	public void setUsingMagic(boolean state) {
		usingMagic = state;
	}

	public int getSpellId() {
		return spellId;
	}

	public void setSpellId(int spellId) {
		this.spellId = spellId;
	}

	public int doAmount;

	public long lastFire;

	public long lastLockPick;

	public boolean playerIsFiremaking;

	public int privateChat;

	private int specEffect;

	public int specBarId;

	public int[] itemRequirement = new int[22];

	public int switches;

	public int skullTimer;

	public int votingPoints;

	public int nextDialogue;

	private int dialogueAction;

	public int randomCoffin;

	private int autocastId;

	public int barrageCount;

	private int autoRetaliate;

	public int getAutoRetaliate() {
		return autoRetaliate;
	}

	public void setAutoRetaliate(int autoRetaliate) {
		this.autoRetaliate = autoRetaliate;
	}

	private int xInterfaceId;

	public int xRemoveId;

	public int xRemoveSlot;

	public int coinsPile;

	public int magicAltar;

	public int bonusAttack;

	/**
	 * The last npc index i attacked. This is never reset.
	 */
	private int lastNpcAttackedIndex = -1;

	public int bankWithdraw;

	public int destroyItem;

	public int npcId2;

	public int lastChatId = 1;

	private int clanId = -1;

	public int itemDestroyedId = -1;

	public boolean splitChat;

	private boolean jailed;

	public boolean initialized;

	private boolean disconnected;

	public boolean rebuildNpcList;

	private boolean isActive;

	public boolean hasMultiSign;

	public boolean saveCharacter;

	public boolean mouseButton;

	public boolean chatEffects = true;

	public boolean adminAttack;

	public boolean acceptAid;

	private boolean usingDarkBowSpecialAttack;

	public boolean isUsingDeathInterface;

	private int interfaceIdOpened;

	public String bankPin = "";

	public int attempts = 3;

	public boolean hasEnteredPin;

	public String enteredPin = "";

	public boolean setPin;

	public String fullPin = "";

	public int getObjectX() {
		return objectX;
	}

	public int getObjectY() {
		return objectY;
	}


	private int projectileStage;

	/**
	 * MODERN/LUNAR/ANCIENT
	 */
	public String spellBook = "MODERN";

	public int teleGfx;

	public int teleEndGfx;

	public int teleEndAnimation;

	public int teleportToHeight;

	public int teleX;

	public int teleY;

	public int teleHeight;

	/**
	 * The last ranged weapon used that will be dropped on floor, such as knifes/arrows etc..
	 */
	private int droppedRangedWeaponUsed;

	public int killingNpcIndex;

	private int oldNpcIndex;

	private int attackTimer;

	private int npcType;

	public int castingSpellId;

	public int oldSpellId;

	private int spellId;

	private int hitDelay;

	public int hitDelay2;

	private int bowSpecShot;

	private int clickNpcType;

	public int clickObjectType;

	private int objectId;

	private int objectX;

	private int objectY;

	public int objectXOffset;

	public int objectYOffset;

	public int objectDistance;

	public int itemPickedUpX, itemPickedUpY, itemPickedUpId;

	private boolean isMoving;

	public boolean walkingToItem;

	public boolean walkingToItemEvent;

	public boolean magicOnFloor;

	private int shopId;

	private int tradeStatus;

	private int tradeWith;

	public boolean ignoreTradeMessage;

	public boolean forcedChatUpdateRequired, inDuel, tradeAccepted, goodTrade;

	private boolean inTrade;

	public boolean tradeRequested;

	public boolean tradeResetNeeded;

	public boolean tradeConfirmed;

	public boolean tradeConfirmed2;

	public boolean canOffer;

	public boolean acceptTrade;

	public boolean acceptedTrade;

	public int attackAnim;

	public int animationWaitCycles;

	public int[] playerBonus = new int[12];

	public boolean runModeOn = true;

	public boolean takeAsNote;

	private int combatLevel;

	public boolean saveFile;

	public int playerAppearance[] = new int[13];

	public int tempItems[] = new int[ServerConstants.BANK_SIZE];

	public int tempItemsN[] = new int[ServerConstants.BANK_SIZE];

	public int tempItemsT[] = new int[ServerConstants.BANK_SIZE];

	public int tempItemsS[] = new int[ServerConstants.BANK_SIZE];

	public boolean[] invSlot = new boolean[28], equipSlot = new boolean[14];

	public long[][] friends = new long[200][2];

	public long ignores[] = new long[200];

	private double specialAttackAmount = 10;

	private double specialAttackAccuracyMultiplier = 1;

	public double specDamage = 1.0;

	public double prayerPoint = 1.0;

	public int teleGrabItem;

	public int teleGrabX;

	public int teleGrabY;

	private int duelCount;

	/**
	 * The attacker(other player) identity that is attacking this player.
	 */
	private int underAttackBy;

	private int npcIndexAttackingPlayer;

	private int wildernessLevel;

	private int teleTimer;

	public int getTeleTimer() {
		return teleTimer;
	}

	public void setTeleTimer(int teleTimer) {
		this.teleTimer = teleTimer;
	}

	public long drainRunEnergyFaster;
	public long immuneToMeleeAttacks;

	public long miasmicSpeedTimeElapsed;
	public int miasmicSpeedTime;

	public long teleBlockEndTime;

	public int poisonDelay;

	public int venomDelay;

	public int vengTimer;

	public long lastPlayerMove;

	public long dfsDelay;

	public long lastVeng;

	public long teleGrabDelay;

	public long lastWebCut;

	public long alchDelay;

	public long reduceStat;

	public long buryDelay;

	public long foodDelay;

	public long potDelay;

	public long karambwanDelay;

	public long pizzaDelay;

	public long diceDelay;

	public long tradeDelay;

	public long clickDelay;

	private int trapsLaid;

	public int getTrapsLaid() {
		return trapsLaid;
	}

	public void setTrapsLaid(int trapsLaid) {
		this.trapsLaid = trapsLaid;
	}


	public boolean canChangeAppearance;

	public byte duelForceChatCount = 4;

	public int reduceSpellId;

	public int headIcon = -1;

	public int duelTimer, duelTeleX, duelTeleY;

	private int duelItemSlot;

	public int duelSpaceReq;

	public int duelOptionFrameId;

	private int duelingWith;

	public int headIconPk = -1, headIconHints;

	public boolean duelRequested;

	private boolean usingSpecial;

	private boolean isWieldingRangedWeaponWithNoArrowSlotRequirement;

	private boolean usingMediumRangeRangedWeapon;

	private int combatStyle = ServerConstants.AGGRESSIVE;

	public boolean[] duelRule = new boolean[22];

	public final int[] ARROWS = {882, 884, 886, 888, 890, 892, 21326, 4740, 11212, 9140, 9141, 4142,
			9143, 9144, 9240, 9241, 9242, 9243, 9244, 9245, 15243, 9337, 9338, 9339, 9340, 9341, 9342,
			21905, 21924, 21926, 21928, 21932, 21934, 21936, 21938, 21940, 21942, 21944, 21946, 21948,
			21950};

	public long[] reduceSpellDelay = new long[6];

	public boolean[] canUseReducingSpell = {true, true, true, true, true, true};

	public long stopPrayerDelay;

	public boolean[] prayerActive = {false, false, false, false, false, false, false, false, false,
			false, false, false, false, false, false, false, false, false, false, false, false, false,
			false, false, false, false, false, false, false};

	public long[] timePrayerActivated = new long[Prayer.values().length];

	public void setPrayerActive(int index, boolean state) {
		prayerActive[index] = state;
		RegularPrayer.handlePrayerDrain(this);
	}

	public boolean prayerEvent;

	public boolean getCombatStyle(int type) {
		return getCombatStyle() == type;
	}

	private Stream outStream = null;

	public Stream getOutStream() {
		return outStream;
	}

	/**
	 * Current ip address.
	 */
	public String addressIp = "";

	/**
	 * The player's unique identity number.
	 */
	private int playerId = -1;

	public String getPlayerName() {
		return playerName;
	}

	/**
	 * The name in the form of a base 64 long.
	 *
	 * @return the name of the player.
	 */
	public long getNameAsLong() {
		return nameAsLong;
	}

	public String getLowercaseName() {
		return playerName.toLowerCase();
	}

	public void setPlayerName(String string) {
		playerName = string;
		nameAsLong = Misc.stringToLong(string);
	}

	private String playerName = null;

	public String playerPass = null;

	public int playerRights;

	public boolean forceX1ExperienceRate;

	public PlayerHandler handler = null;

	/**
	 * Item id stored here are +1, so 4151 whip becomes 4152
	 */
	public int playerItems[] = new int[28];

	public int playerItemsN[] = new int[28];

	public int playerStandIndex = 0x328;

	public int playerTurnIndex = 0x337;

	public int playerWalkIndex = 0x333;

	public int playerTurn180Index = 0x334;

	public int playerTurn90CWIndex = 0x335;

	public int playerTurn90CCWIndex = 0x336;

	public int playerRunIndex = 0x338;

	public int[] playerEquipment = new int[14];

	public int[] playerEquipmentN = new int[14];

	public int getWieldedWeapon() {
		return playerEquipment[ServerConstants.WEAPON_SLOT];
	}

	public boolean getEquippedWeapon(int itemId) {
		return playerEquipment[ServerConstants.WEAPON_SLOT] == itemId;
	}

	public boolean getEquippedHelm(int itemId) {
		return playerEquipment[ServerConstants.HEAD_SLOT] == itemId;
	}

	public boolean getEquippedBody(int itemId) {
		return playerEquipment[ServerConstants.BODY_SLOT] == itemId;
	}

	public boolean getEquippedLeg(int itemId) {
		return playerEquipment[ServerConstants.LEG_SLOT] == itemId;
	}

	public boolean getEquippedBoots(int itemId) {
		return playerEquipment[ServerConstants.FEET_SLOT] == itemId;
	}

	public boolean getEquippedAmulet(int itemId) {
		return playerEquipment[ServerConstants.AMULET_SLOT] == itemId;
	}

	public boolean getEquippedArrows(int itemId) {
		return playerEquipment[ServerConstants.ARROW_SLOT] == itemId;
	}

	public boolean getEquippedGloves(int itemId) {
		return playerEquipment[ServerConstants.HAND_SLOT] == itemId;
	}

	public boolean getEquippedRing(int itemId) {
		return playerEquipment[ServerConstants.RING_SLOT] == itemId;
	}

	public boolean getEquippedShield(int itemId) {
		return playerEquipment[ServerConstants.SHIELD_SLOT] == itemId;
	}

	public boolean getEquippedCape(int itemId) {
		return playerEquipment[ServerConstants.CAPE_SLOT] == itemId;
	}

	/**
	 * This array will store the items of the current bank tab being viewed into this array.
	 */
	public int bankingItems[] = new int[ServerConstants.BANK_SIZE];

	public int bankingItemsN[] = new int[ServerConstants.BANK_SIZE];

	public int bankingTab = 0; // -1 = bank closed

	public boolean doNotSendTabs;

	public int bankItems[] = new int[ServerConstants.BANK_SIZE];

	public int bankItemsN[] = new int[ServerConstants.BANK_SIZE];

	public int bankItems1[] = new int[ServerConstants.BANK_SIZE];

	public int bankItems1N[] = new int[ServerConstants.BANK_SIZE];

	public int bankItems2[] = new int[ServerConstants.BANK_SIZE];

	public int bankItems2N[] = new int[ServerConstants.BANK_SIZE];

	public int bankItems3[] = new int[ServerConstants.BANK_SIZE];

	public int bankItems3N[] = new int[ServerConstants.BANK_SIZE];

	public int bankItems4[] = new int[ServerConstants.BANK_SIZE];

	public int bankItems4N[] = new int[ServerConstants.BANK_SIZE];

	public int bankItems5[] = new int[ServerConstants.BANK_SIZE];

	public int bankItems5N[] = new int[ServerConstants.BANK_SIZE];

	public int bankItems6[] = new int[ServerConstants.BANK_SIZE];

	public int bankItems6N[] = new int[ServerConstants.BANK_SIZE];

	public int bankItems7[] = new int[ServerConstants.BANK_SIZE];

	public int bankItems7N[] = new int[ServerConstants.BANK_SIZE];

	public int bankItems8[] = new int[ServerConstants.BANK_SIZE];

	public int bankItems8N[] = new int[ServerConstants.BANK_SIZE];

	/**
	 * Instances of players that i have seen before.
	 */
	public Player playerList[] = new Player[ServerConstants.MAXIMUM_PLAYERS];

	public int playerListSize = 0;

	public byte playerInListBitmap[] = new byte[(ServerConstants.MAXIMUM_PLAYERS + 7) >> 3];

	public static final int maxNPCListSize = NpcHandler.NPC_INDEX_OPEN_MAXIMUM;

	public Npc npcList[] = new Npc[maxNPCListSize];

	public int npcListSize = 0;

	public byte npcInListBitmap[] = new byte[(NpcHandler.NPC_INDEX_OPEN_MAXIMUM + 7) >> 3];

	/**
	 * True if the specific index is an Npc that requires an npc transform update. So the
	 * transformation is sent to the client once.
	 */
	public boolean npcTransformRequiresUpdate[] = new boolean[NpcHandler.NPC_INDEX_OPEN_MAXIMUM];

	private int mapRegionX, mapRegionY;

	private int playerX;

	private int playerY;

	public int walkingQueueCurrentSize;

	public int currentX, currentY;

	private boolean updateRequired = true;

	public final int walkingQueueSize = 50;

	public int walkingQueueX[] = new int[walkingQueueSize], walkingQueueY[] = new int[walkingQueueSize];

	public int wQueueReadPtr = 0;

	public int wQueueWritePtr = 0;

	public boolean isRunning = true;

	public int teleportToX = -1, teleportToY = -1;

	public boolean didTeleport;

	public boolean mapRegionDidChange;

	public int dir1 = -1, dir2 = -1;

	/**
	 * The timer used for Completionist cape emote.
	 */
	public int dungTime = 16;

	public int DirectionCount = 0;

	public boolean appearanceUpdateRequired = true;

	public boolean justTransformed;

	protected int hitDiff2;

	private int hitDiff = 0;

	public boolean cycleEventDamageRunning;

	protected boolean hitUpdateRequired2;

	private boolean hitUpdateRequired;

	protected static Stream playerProps;

	static {
		playerProps = new Stream(new byte[100]);
	}

	public int[][] barrowsNpcs = {{1677, 0}, // verac
			{1676, 0}, // toarg
			{1675, 0}, // karil
			{1674, 0}, // guthan
			{1673, 0}, // dharok
			{1672, 0} // ahrim
	};

	public int soakDamage, soakDamage2 = 0;

	public int[] damageTaken = new int[ServerConstants.MAXIMUM_PLAYERS];

	public String[] damageTakenName = new String[ServerConstants.MAXIMUM_PLAYERS];

	public int hitMask;

	public int hitIcon;

	public int hitMask2;

	public int hitIcon2;

	private boolean chatTextUpdateRequired;

	private byte chatText[] = new byte[4096];

	private byte chatTextSize = 0;

	private int chatTextColor = 0;

	private int chatTextEffects = 0;

	private String forcedText = "null";

	public int mask100var1 = 0;

	public int mask100var2 = 0;

	protected boolean mask100update;

	public int newWalkCmdX[] = new int[walkingQueueSize];

	public int newWalkCmdY[] = new int[walkingQueueSize];

	private int newWalkCmdSteps = 0;

	public boolean newWalkCmdIsRunning;

	public int travelBackX[] = new int[walkingQueueSize];

	public int travelBackY[] = new int[walkingQueueSize];

	public int numTravelBackSteps = 0;

	public void preProcessing() {
		setNewWalkCmdSteps(0);
	}

	public int getMapRegionX() {
		return mapRegionX;
	}

	public int getMapRegionY() {
		return mapRegionY;
	}

	public int getLocalX() {
		return getX() - 8 * getMapRegionX();
	}

	public int getLocalY() {
		return getY() - 8 * getMapRegionY();
	}

	public void forcedChat(String message, boolean sendToAllVisiblePlayersChatBox, boolean showQuickChatIconInChatBox) {
		setForcedText(message);
		forcedChatUpdateRequired = true;
		setUpdateRequired(true);
		setAppearanceUpdateRequired(true);
		InterfaceAssistant.sendChatBoxMessageOfForcedChat(this, this.getPlayerName(), message, showQuickChatIconInChatBox);
		if (sendToAllVisiblePlayersChatBox) {
			for (Player loop : getLocalPlayers()) {
				InterfaceAssistant.sendChatBoxMessageOfForcedChat(loop, this.getPlayerName(), message, showQuickChatIconInChatBox);
			}
		}
	}

	/**
	 * The player's x-coordinate.
	 *
	 * @return Player's x-cooridnate.
	 */
	public int getX() {
		return playerX;
	}

	/**
	 * The player's y-coordinate.
	 *
	 * @return Player's y-cooridnate.
	 */
	public int getY() {
		return playerY;
	}

	/**
	 * Get the player's height level.
	 *
	 * @return The height level.
	 */
	public int getHeight() {
		return height;
	}

	private int height;

	public int getHitDiff() {
		return hitDiff;
	}

	public void setHitUpdateRequired(boolean hitUpdateRequired) {
		this.hitUpdateRequired = hitUpdateRequired;
	}

	public boolean isHitUpdateRequired() {
		return hitUpdateRequired;
	}

	public void setAppearanceUpdateRequired(boolean appearanceUpdateRequired) {
		this.appearanceUpdateRequired = appearanceUpdateRequired;
	}

	public boolean isAppearanceUpdateRequired() {
		return appearanceUpdateRequired;
	}

	public void setChatTextEffects(int chatTextEffects) {
		this.chatTextEffects = chatTextEffects;
	}

	public int getChatTextEffects() {
		return chatTextEffects;
	}

	public void setChatTextSize(byte chatTextSize) {
		this.chatTextSize = chatTextSize;
	}

	public byte getChatTextSize() {
		return chatTextSize;
	}

	public void setChatTextUpdateRequired(boolean chatTextUpdateRequired) {
		this.chatTextUpdateRequired = chatTextUpdateRequired;
	}

	public boolean isChatTextUpdateRequired() {
		return chatTextUpdateRequired;
	}

	public byte[] getChatText() {
		return chatText;
	}

	public void setChatTextColor(int chatTextColor) {
		this.chatTextColor = chatTextColor;
	}

	public int getChatTextColor() {
		return chatTextColor;
	}

	public int[] getNewWalkCmdX() {
		return newWalkCmdX;
	}

	public void setNewWalkCmdY(int newWalkCmdY[]) {
		this.newWalkCmdY = newWalkCmdY;
	}

	public int[] getNewWalkCmdY() {
		return newWalkCmdY;
	}

	public void setNewWalkCmdIsRunning(boolean newWalkCmdIsRunning) {
		this.newWalkCmdIsRunning = newWalkCmdIsRunning;
	}

	public boolean isNewWalkCmdIsRunning() {
		return newWalkCmdIsRunning;
	}

	public void setInStreamDecryption(ISAACRandomGen inStreamDecryption) {}

	public void setOutStreamDecryption(ISAACRandomGen outStreamDecryption) {}

	public byte buffer[] = null;

	public int packetSize = 0, packetType = -1;

	public IoSession session;

	public IoSession getSession() {
		return session;
	}

	public Queue<Packet> queuedPackets = new LinkedList<Packet>();

	public Stream inStream = null;

	public Stream getInStream() {
		return inStream;
	}

	private ShopAssistant shopAssistant = new ShopAssistant(this);

	public ShopAssistant getShops() {
		return shopAssistant;
	}

	private TradeAndDuel tradeAndDuel = new TradeAndDuel(this);

	public TradeAndDuel getTradeAndDuel() {
		return tradeAndDuel;
	}

	public PlayerAssistant playerAssistant = new PlayerAssistant(this);

	public PlayerAssistant getPA() {
		return playerAssistant;
	}

	private DialogueHandler dialogueHandler = new DialogueHandler(this);

	public long timeUnderAttackByAnotherPlayerAchievement;

	public long timeAttackedAnotherPlayerAchievement;

	public boolean noClip;

	public int itemDestroyedSlot;

	public String lastDialogueOptionString = "";

	public boolean soundSent;

	public long lastSpammedSoundTime;

	public long timeSentFoodSound;

	public long timeSentDrinkSound;

	public boolean itemWorn;

	public long lastThieve;

	public long timePlayerAttackedNpc;

	public long timeReloadedItems;

	public boolean armadylCrossbowSpecial;

	public boolean dragonCrossbowSpecial;

	public boolean doingClickObjectType4Event;

	public DialogueHandler getDH() {
		return dialogueHandler;
	}

	public boolean isJailed() {
		return jailed;
	}

	public void setJailed(boolean jailed) {
		this.jailed = jailed;
	}

	public int getClickNpcType() {
		return clickNpcType;
	}

	public void setClickNpcType(int clickNpcType) {
		this.clickNpcType = clickNpcType;
	}

	public int getOldNpcIndex() {
		return oldNpcIndex;
	}

	public void setOldNpcIndex(int oldNpcIndex) {
		this.oldNpcIndex = oldNpcIndex;
	}

	public boolean isDisconnected() {
		return disconnected;
	}

	public void setDisconnected(boolean disconnected, String reason) {
		PlayerHandler.disconnectReason.add("[" + Misc.getDateAndTimeAndSeconds() + "] "
				+ getPlayerName() + ", disconnect boolean:" + reason);
		this.disconnected = disconnected;
	}

	public int getTimeOutCounter() {
		return timeOutCounter;
	}

	public void setTimeOutCounter(int timeOutCounter) {
		this.timeOutCounter = timeOutCounter;
	}

	public boolean isUpdateRequired() {
		return updateRequired;
	}

	public void setUpdateRequired(boolean updateRequired) {
		this.updateRequired = updateRequired;
	}

	public double getSpecialAttackAccuracyMultiplier() {
		return specialAttackAccuracyMultiplier;
	}

	public void setSpecialAttackAccuracyMultiplier(double specialAttackAccuracyMultiplier) {
		this.specialAttackAccuracyMultiplier = specialAttackAccuracyMultiplier;
	}

	public boolean isTutorialComplete() {
		return tutorialComplete;
	}

	public void setTutorialComplete(boolean tutorialComplete) {
		this.tutorialComplete = tutorialComplete;
	}

	public int getNewWalkCmdSteps() {
		return newWalkCmdSteps;
	}

	public int setNewWalkCmdSteps(int newWalkCmdSteps) {
		this.newWalkCmdSteps = newWalkCmdSteps;
		return newWalkCmdSteps;
	}

	public int getMeleeMainKills() {
		return meleeMainKills;
	}

	public void setMeleeMainKills(int meleeKills) {
		this.meleeMainKills = meleeKills;
	}

	public int getHybridKills() {
		return hybridKills;
	}

	public void setHybridKills(int hybridKills) {
		this.hybridKills = hybridKills;
	}

	public int getBerserkerPureKills() {
		return berserkerPureKills;
	}

	public void setBerserkerPureKills(int berserkerPureKills) {
		this.berserkerPureKills = berserkerPureKills;
	}

	public int getPureKills() {
		return pureKills;
	}

	public void setPureKills(int pureKills) {
		this.pureKills = pureKills;
	}

	public int getRangedTankKills() {
		return rangedTankKills;
	}

	public void setRangedTankKills(int rangedTankKills) {
		this.rangedTankKills = rangedTankKills;
	}

	public int getCombatLevel() {
		return combatLevel;
	}

	public void setCombatLevel(int combatLevel) {
		this.combatLevel = combatLevel;
	}

	public int getNpcType() {
		return npcType;
	}

	public void setNpcType(int npcType) {
		this.npcType = npcType;
	}

	public boolean isUsingFightCaves() {
		return usingFightCaves;
	}

	public void setUsingFightCaves(boolean isPreparingForFightCaves) {
		this.usingFightCaves = isPreparingForFightCaves;
	}

	public long getXpTotal() {
		return xpTotal;
	}

	public void setXpTotal(long xpTotal) {
		this.xpTotal = xpTotal;
	}

	public int getObjectId() {
		return objectId;
	}

	public void setObjectId(int id) {
		objectId = id;
	}

	public int getAgilityCourseCompletedMessage() {
		return agilityCourseCompletedMessage;
	}

	public void setAgilityCourseCompletedMessage(int agilityCourseCompletedMessage) {
		this.agilityCourseCompletedMessage = agilityCourseCompletedMessage;
	}

	public String getAmountInterface() {
		return amountInterface;
	}

	public void setAmountInterface(String amountInterface) {
		this.amountInterface = amountInterface;
		if (!amountInterface.isEmpty()) {
			this.setxInterfaceId(0); // Resetting it to stop it from also withdrawing from other
												// interfaces like the bank.
		}
	}

	public int getNpcClickIndex() {
		return npcClickIndex;
	}

	public void setNpcClickIndex(int npcClickIndex) {
		if (npcClickIndex > 0) {
			lastNpcClickedIndex = npcClickIndex;
		}
		this.npcClickIndex = npcClickIndex;
	}

	public int setObjectX(int objectX) {
		this.objectX = objectX;
		return objectX;
	}

	public int getMaximumDamageMagic() {
		return maximumDamageMagic;
	}

	public void setMaximumDamageMagic(int maximumDamageMagic) {
		this.maximumDamageMagic = maximumDamageMagic;
	}

	public boolean getAbleToEditCombat() {
		return ableToEditCombat;
	}

	public void setAbleToEditCombat(boolean canEditCombatStats) {
		this.ableToEditCombat = canEditCombatStats;
	}

	public boolean isUsingBankInterface() {
		return usingBankInterface;
	}

	public void setUsingBankInterface(boolean usingBankInterface) {
		this.usingBankInterface = usingBankInterface;
	}

	public int getTotalLevel() {
		return totalLevel;
	}

	public void setTotalLevel(int totalLevel) {
		this.totalLevel = totalLevel;
	}

	public int getBarrowsRunCompleted() {
		return barrowsRunCompleted;
	}

	public void setBarrowsRunCompleted(int barrowsRunCompleted) {
		this.barrowsRunCompleted = barrowsRunCompleted;
	}

	public int getClueScrollsCompleted() {
		return clueScrollsCompleted;
	}

	public void setClueScrollsCompleted(int clueScrollsCompleted) {
		this.clueScrollsCompleted = clueScrollsCompleted;
	}

	public void setOutStream(Stream outStream) {
		this.outStream = outStream;
	}

	public boolean isUsingBankSearch() {
		return usingBankSearch;
	}

	public void setUsingBankSearch(boolean usingBankSearch) {
		if (!usingBankSearch) {

			this.bankSearchString = "";
		}
		this.usingBankSearch = usingBankSearch;
	}



	public boolean processQueuedPackets() {
		Packet p = null;

		synchronized (queuedPackets) {
			p = queuedPackets.poll();
		}
		if (p == null) {
			return false;
		}

		inStream.currentOffset = 0;
		packetType = p.getId();
		packetSize = p.getLength();
		inStream.buffer = p.getData();
		setTimeOutCounter(0);
		// Uncomment if player is stuck logged in.
		// Misc.print("Player is active: " + getPlayerName() + ", Packet type: " + packetType);
		if (PacketHandler.showIndividualPackets) {
			Misc.print("Player is active: " + getPlayerName() + ", Packet type: " + packetType);
		}
		if (packetType > 0) {
			PacketHandler.processPacket(this, packetType, packetSize);
		}
		return true;
	}

	public void flushOutStream() {
		if (!this.canFlush && !AdministratorCommand.flushAllTheTime) {
			return;
		}
		if (this.bot) {
			return;
		}
		if (isDisconnected() || getOutStream().currentOffset == 0) {
			return;
		}
		StaticPacketBuilder out = new StaticPacketBuilder().setBare(true);
		byte[] temp = new byte[getOutStream().currentOffset];
		System.arraycopy(getOutStream().buffer, 0, temp, 0, temp.length);
		out.addBytes(temp);
		session.write(out.toPacket());
		getOutStream().currentOffset = 0;
	}

	public void update() {
		handler.updatePlayer(this, getOutStream());
		handler.updateNpc(this, getOutStream());

		canFlush = true;
		flushOutStream();
		canFlush = false;
	}

	public void queueMessage(Packet arg1) {
		synchronized (queuedPackets) {
			queuedPackets.add(arg1);
		}
	}

	public void clearUpdateFlags() {
		setUpdateRequired(false);
		setChatTextUpdateRequired(false);
		setAppearanceUpdateRequired(false);
		setHitUpdateRequired(false);
		hitUpdateRequired2 = false;
		forcedChatUpdateRequired = false;
		mask100update = false;
		forceMovementUpdate = false;
		focusPointUpdateRequired = false;
		setAnimationRequest(-1);
		forceSendAnimation = false;
		resetPlayerTurn();
		setFaceUpdateRequired(false);
		setFace(65535);
		if (getFaceResetAtEndOfTick()) {
			resetFaceUpdate();
			setFaceResetAtEndOfTick(false);
		}
	}

	public void appendMask100Update(Stream str) {
		str.writeWordBigEndian(mask100var1);
		str.writeDWord(mask100var2);
	}

	/**
	 * Gfx will be launched from the middle of the body.
	 */
	public void gfx100(int gfx) {
		if (playerPet != null && playerPet.isSummoned() && playerPet.isMimicking() && !Combat.inCombat(this)) {
			playerPet.gfx100(gfx);
		}
		mask100var1 = gfx;
		mask100var2 = 6553600;
		mask100update = true;
		setUpdateRequired(true);
	}

	/**
	 * Gfx will be launched from bottom of the body.
	 *
	 * @param gfx
	 */
	public void gfx0(int gfx) {
		if (playerPet != null && playerPet.isSummoned() && playerPet.isMimicking() && !Combat.inCombat(this)) {
			playerPet.gfx0(gfx);
		}
		mask100var1 = gfx;
		mask100var2 = 65536;
		mask100update = true;
		setUpdateRequired(true);
	}

	public void gfx(int gfx, int height) {
		if (playerPet != null && playerPet.isSummoned() && playerPet.isMimicking() && !Combat.inCombat(this)) {
			playerPet.gfx(gfx, height);
		}
		mask100var1 = gfx;
		mask100var2 = 65536 * height;
		mask100update = true;
		setUpdateRequired(true);
	}

	public void gfxDelay(int id, int delay, int height) {
		mask100var1 = id;
		mask100var2 = delay + (65536 * height);
		mask100update = true;
		updateRequired = true;
	}

	/**
	 * Perform an animation.
	 *
	 * @param animId The animation identity number.
	 */
	public void startAnimation(int animId) {
		if (this.forceSendAnimation) {
			return;
		}
		if (animId < 0) {
			return;
		}
		if (this.getTransformed() > 0) {
			return;
		}
		if (playerPet != null && playerPet.isSummoned() && playerPet.isMimicking() && !Combat.inCombat(this)) {
			playerPet.startAnimation(animId);
		}
		setLastAnimation(animId);
		setAnimationRequest(animId);
		animationWaitCycles = 0;
		setUpdateRequired(true);
	}

	public void appendAnimationRequest(Stream str) {
		str.writeWordBigEndian((getAnimationRequest() == -1) ? 65535 : getAnimationRequest());
		str.writeByteC(animationWaitCycles);

		forceSendAnimation = false;
	}

	/**
	 * Face Update
	 **/
	public void faceUpdate(int index) {
		if (this.dead) {
			resetFaceUpdate();
			return;
		}
		setFace(index);
		setFaceUpdateRequired(true);
		setUpdateRequired(true);
	}

	public void appendFaceUpdate(Stream str) {
		str.writeWordBigEndian(getFace());
	}

	public void resetPlayerTurn() {
		setUpdateRequired(true);
	}

	public void resetFaceUpdate() {
		setFace(-1);
		setFaceUpdateRequired(true);
		setUpdateRequired(true);
	}

	/**
	 * Turn the player's characterto face the given coordinates.
	 */
	public void turnPlayerTo(int pointX, int pointY) {
		if (this.getDead()) {
			resetPlayerTurn();
			return;
		}
		setFocusPointX(2 * pointX + 1);
		setFocusPointY(2 * pointY + 1);
		focusPointUpdateRequired = true;
		setUpdateRequired(true);
		int x = getX() - pointX;
		int y = getY() - pointY;
		this.directionFacingPath = Misc.direction(getX(), getY(), x, y);
	}

	private void appendSetFocusDestination(Stream str) {
		str.writeWordBigEndianA(getFocusPointX());
		str.writeWordBigEndian(getFocusPointY());
	}

	protected void appendHitUpdate(Stream str) {
		str.writeWordA(getHitDiff());
		str.writeByte(hitMask);
		str.writeByte(hitIcon);
		str.writeWordA(soakDamage);
		str.writeWordA(currentCombatSkillLevel[ServerConstants.HITPOINTS]);
		str.writeWordA(getBaseHitPointsLevel());
	}

	protected void appendHitUpdate2(Stream str) {
		str.writeWordA(hitDiff2);
		str.writeByte(hitMask2);
		str.writeByte(hitIcon2);
		str.writeWordA(soakDamage2);
		str.writeWordA(currentCombatSkillLevel[ServerConstants.HITPOINTS]);
		str.writeWordA(getBaseHitPointsLevel());
	}

	public boolean forceSendAnimation;

	public boolean appendPlayerUpdateBlock(Stream str, boolean forceStop) {
		boolean appearanceUpdated = false;
		if (!isUpdateRequired() && !chatTextUpdateRequired) {
			return false;
		}
		int updateMask = 0;
		if (forceMovementUpdate) {
			updateMask |= 0x400;
		}
		if (mask100update) {
			updateMask |= 0x100;
		}
		if (getAnimationRequest() != -1) {
			updateMask |= 8;
		}
		if (forcedChatUpdateRequired) {
			updateMask |= 4;
		}
		if (isChatTextUpdateRequired()) {
			updateMask |= 0x80;
		}
		if (isAppearanceUpdateRequired() && !forceStop) {
			updateMask |= 0x10;
		}
		if (isFaceUpdateRequired()) {
			updateMask |= 1;
		}
		if (focusPointUpdateRequired) {
			updateMask |= 2;
		}
		if (isHitUpdateRequired()) {
			updateMask |= 0x20;
		}

		if (hitUpdateRequired2) {
			updateMask |= 0x200;
		}

		if (updateMask >= 0x100) {
			updateMask |= 0x40;
			str.writeByte(updateMask & 0xFF);
			str.writeByte(updateMask >> 8);
		} else {
			str.writeByte(updateMask);
		}

		// now writing the various update blocks itself - note that their order crucial.

		if (forceMovementUpdate) {
			appendForceMovement(str);
		}
		if (mask100update) {
			appendMask100Update(str);
		}
		if (getAnimationRequest() != -1) {
			appendAnimationRequest(str);
		}
		if (forcedChatUpdateRequired) {
			appendForcedChat(str);
		}
		if (isChatTextUpdateRequired()) {
			appendPlayerChatText(str);
		}
		if (isFaceUpdateRequired()) {
			appendFaceUpdate(str);
		}
		if (isAppearanceUpdateRequired() && !forceStop) {
			appendPlayerAppearance(str);
			appearanceUpdated = true;
		}
		if (focusPointUpdateRequired) {
			appendSetFocusDestination(str);
		}
		if (isHitUpdateRequired()) {
			appendHitUpdate(str);
		}
		if (hitUpdateRequired2) {
			appendHitUpdate2(str);
		}
		return appearanceUpdated;
	}

	public int forceMovementLocalXStart = -1;

	public int forceMovementLocalYStart = -1;

	public int forceMovementLocalXEnd = -1;

	public int forceMovementLocalYEnd = -1;

	public int forceMovementSpeedFromCurrentToStart = -1;

	public int forceMovementSpeedFromStartToEnd = -1;

	public int forceMovementPlayerFace = -1;

	/**
	 * The direction the player is facing depending on the last path walked.
	 */
	public int directionFacingPath = 8;

	public boolean teleportUpdateNeeded;

	public int frozenBy;

	public long cannotEatDelay;

	public long pizzaDelayOther;

	/**
	 * The time the player applied a hitsplat on another player using magic/ranged or melee.
	 */
	public long hitsplatApplied;

	/**
	 * @param x x tiles to move.
	 * @param y y tiles to move
	 * @param updateCoordinateTicks How many ticks later to update the player's coordinate.
	 */
	public void setForceMovement(int animation, int x, int y,
			int forceMovementSpeedFromCurrentToStart, int forceMovementSpeedFromStartToEnd,
			int forceMovementPlayerFace, int updateCoordinateTicks) {
		this.startAnimation(animation);
		this.forceMovementLocalXStart = getLocalX();
		this.forceMovementLocalYStart = getLocalY();
		this.forceMovementLocalXEnd = getLocalX() + x;
		this.forceMovementLocalYEnd = getLocalY() + y;
		this.forceMovementSpeedFromCurrentToStart = forceMovementSpeedFromCurrentToStart;
		this.forceMovementSpeedFromStartToEnd = forceMovementSpeedFromStartToEnd;
		this.forceMovementPlayerFace = forceMovementPlayerFace;
		this.forceMovementUpdate = true;
		this.updateRequired = true;
		this.forceNoClip = true;
		this.agilityEndX = getX() + x;
		this.agilityEndY = getY() + y;

		CycleEventHandler.getSingleton().addEvent(this, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				container.stop();
			}

			@Override
			public void stop() {
				getPA().movePlayer(getX() + x, getY() + y, getHeight());
				setDoingAgility(false);
			}
		}, updateCoordinateTicks);
	}

	public void appendForceMovement(Stream str) {
		str.writeByteS(forceMovementLocalXStart);
		str.writeByteS(forceMovementLocalYStart);
		str.writeByteS(forceMovementLocalXEnd);
		str.writeByteS(forceMovementLocalYEnd);
		str.writeWordBigEndianA(forceMovementSpeedFromCurrentToStart);
		str.writeWordA(forceMovementSpeedFromStartToEnd);
		str.writeByteS(forceMovementPlayerFace);
	}

	public int distanceToPoint(int pointX, int pointY) {
		return (int) Math.sqrt(Math.pow(getX() - pointX, 2) + Math.pow(getY() - pointY, 2));
	}

	protected void appendPlayerChatText(Stream str) {
		str.writeWordBigEndian(((getChatTextColor() & 0xFF) << 8) + (getChatTextEffects() & 0xFF));
		str.writeByte(playerRights);
		str.writeByteC(getChatTextSize());
		str.writeBytes_reverse(getChatText(), getChatTextSize(), 0);
	}

	public void setFocusRelativeToFace() {
		PlayerFaceDirection facingDirection = PlayerFaceDirection.DIRECTION_MAP.get(directionFacingPath);

		if (facingDirection == null) {
			return;
		}
		turnPlayerTo(getX() + facingDirection.getXOffset(), getY() + facingDirection.getYOffset());
	}

	public void appendForcedChat(Stream str) {
		str.writeString(getForcedText());
	}

	public void dealDamage(int damage) {
		if (playerEquipment[ServerConstants.SHIELD_SLOT] == 13740 && GameType.isPreEoc()) {
			if (prayerPoint > 0) {
				double damageRecieved = damage * 0.7;
				int prayerLost = (int) Math.ceil((damage * 0.3) / 20);
				if (prayerPoint >= prayerLost) {
					damage = (int) damageRecieved;
					prayerPoint -= prayerLost;
					if (prayerPoint < 0)
						prayerPoint = 0;
				}
			}
		}
		if (getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) - damage < 0) {
			damage = currentCombatSkillLevel[ServerConstants.HITPOINTS];
		}
		if (!this.getTank()) {
			this.subtractFromHitPoints(damage);
		}
		BotContent.damaged(this);
		if (getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) == 0 && !this.getDead()) {
			Death.deathStage(this);
			return;
		}
		Combat.appendRedemption(this, damage);
		Effects.phoenixNecklace(this, damage);
		HitPointsRegeneration.startHitPointsRegeneration(this);
	}

	/**
	 * @param damage The damage
	 * @param hitSplatColour The colour of the hitsplat.
	 * @param icon The icon to show next to the hitsplat.
	 */
	public void handleHitMask(int damage, int hitSplatColour, int icon, int soak, boolean maxHit) {
		if (!hitUpdateRequired) {
			// If this event is not added, then the msb to dds spec to gmaul bug will appear.
			if (this.cycleEventDamageRunning && !hitUpdateRequired2) {
				hitDiff2 = damage;
				hitMask2 = maxHit ? 1 : hitSplatColour;
				hitIcon2 = icon;
				soakDamage2 = soak;
				hitUpdateRequired2 = true;
				setUpdateRequired(true);
			} else {
				hitDiff = damage;
				hitMask = maxHit ? 1 : hitSplatColour;
				hitIcon = icon;
				soakDamage = soak;
				hitUpdateRequired = true;
			}
		} else if (!hitUpdateRequired2) {

			hitDiff2 = damage;
			hitMask2 = maxHit ? 1 : hitSplatColour;
			hitIcon2 = icon;
			soakDamage2 = soak;
			hitUpdateRequired2 = true;
		} else {
			if (cycleEventDamageRunning) {
				CycleEventHandler.getSingleton().addEvent(this, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {
						container.stop();
					}

					@Override
					public void stop() {
						hitDiff2 = damage;
						hitMask2 = maxHit ? 1 : hitSplatColour;
						hitIcon2 = icon;
						soakDamage2 = soak;
						hitUpdateRequired2 = true;
						setUpdateRequired(true);
					}
				}, 1);
			} else {
				cycleEventDamageRunning = true;
				CycleEventHandler.getSingleton().addEvent(this, new CycleEvent() {

					@Override
					public void execute(CycleEventContainer container) {
						container.stop();
					}

					@Override
					public void stop() {
						// Changed to hitDiff2 etc from hitDiff1 to fix DclawstoGmaul to maket it show all
						// hitsplats.
						hitDiff2 = damage;
						hitMask2 = maxHit ? 1 : hitSplatColour;
						hitIcon2 = icon;
						soakDamage2 = soak;
						hitUpdateRequired2 = true;
						setUpdateRequired(true);
						cycleEventDamageRunning = false;
					}

				}, 1);
			}
		}
		setUpdateRequired(true);
	}

	protected void appendPlayerAppearance(Stream str) {
		playerProps.currentOffset = 0;
		playerProps.writeByte(playerAppearance[0]);
		playerProps.writeByte(headIcon);
		playerProps.writeByte(skullVisualType);
		playerProps.writeWord(compColor1);
		playerProps.writeWord(compColor2);
		playerProps.writeWord(compColor3);
		playerProps.writeWord(compColor4);
		if (npcId2 <= 0) {
			int number = GameType.isPreEoc() ? 32768 : 0x100;
			if (playerEquipment[ServerConstants.HEAD_SLOT] > 1) {
				playerProps.writeWord(0x200 + playerEquipment[ServerConstants.HEAD_SLOT]);
			} else {
				playerProps.writeByte(0);
			}

			if (playerEquipment[ServerConstants.CAPE_SLOT] > 1) {
				playerProps.writeWord(0x200 + playerEquipment[ServerConstants.CAPE_SLOT]);
			} else {
				playerProps.writeByte(0);
			}

			if (playerEquipment[ServerConstants.AMULET_SLOT] > 1) {
				playerProps.writeWord(0x200 + playerEquipment[ServerConstants.AMULET_SLOT]);
			} else {
				playerProps.writeByte(0);
			}

			if (getWieldedWeapon() > 1) {
				playerProps.writeWord(0x200 + getWieldedWeapon());
			} else {
				playerProps.writeByte(0);
			}

			if (playerEquipment[ServerConstants.BODY_SLOT] > 1) {
				playerProps.writeWord(0x200 + playerEquipment[ServerConstants.BODY_SLOT]);
			} else {
				playerProps.writeWord(number + playerAppearance[2]);
			}

			if (playerEquipment[ServerConstants.SHIELD_SLOT] > 1) {
				playerProps.writeWord(0x200 + playerEquipment[ServerConstants.SHIELD_SLOT]);
			} else {
				playerProps.writeByte(0);
			}

			if (!Item.isFullBody(playerEquipment[ServerConstants.BODY_SLOT])) {
				playerProps.writeWord(number + playerAppearance[3]);
			} else {
				playerProps.writeByte(0);
			}

			if (playerEquipment[ServerConstants.LEG_SLOT] > 1) {
				playerProps.writeWord(0x200 + playerEquipment[ServerConstants.LEG_SLOT]);
			} else {
				playerProps.writeWord(number + playerAppearance[5]);
			}

			if (!Item.isNormalHelm(playerEquipment[ServerConstants.HEAD_SLOT])
					&& !Item.isFullMask(playerEquipment[ServerConstants.HEAD_SLOT])) {
				playerProps.writeWord(number + playerAppearance[1]);
			} else {
				playerProps.writeByte(0);
			}
			if (playerEquipment[ServerConstants.HAND_SLOT] > 1) {
				playerProps.writeWord(0x200 + playerEquipment[ServerConstants.HAND_SLOT]);
			} else {
				playerProps.writeWord(number + playerAppearance[4]);
			}
			if (playerEquipment[ServerConstants.FEET_SLOT] > 1) {
				playerProps.writeWord(0x200 + playerEquipment[ServerConstants.FEET_SLOT]);
			} else {
				playerProps.writeWord(number + playerAppearance[6]);
			}
			if (playerAppearance[0] != 1
					&& !Item.isFullMask(playerEquipment[ServerConstants.HEAD_SLOT])) {
				playerProps.writeWord(number + playerAppearance[7]);
			} else {
				playerProps.writeByte(0);
			}
		} else {
			playerProps.writeWord(-1);
			playerProps.writeWord(npcId2);
		}
		playerProps.writeByte(playerAppearance[8]);
		playerProps.writeByte(playerAppearance[9]);
		playerProps.writeByte(playerAppearance[10]);
		playerProps.writeByte(playerAppearance[11]);
		playerProps.writeByte(playerAppearance[12]);
		playerProps.writeWord(playerStandIndex); // standAnimIndex
		playerProps.writeWord(playerTurnIndex); // standTurnAnimIndex
		playerProps.writeWord(playerWalkIndex); // walkAnimIndex
		playerProps.writeWord(playerTurn180Index); // turn180AnimIndex
		playerProps.writeWord(playerTurn90CWIndex); // turn90CWAnimIndex
		playerProps.writeWord(playerTurn90CCWIndex); // turn90CCWAnimIndex
		playerProps.writeWord(playerRunIndex); // runAnimIndex
		playerProps.writeWord(currentCombatSkillLevel[ServerConstants.HITPOINTS]);
		playerProps.writeWord(getBaseHitPointsLevel());
		playerProps.writeString(displayName == null ? getPlayerName() : getDisplayName());
		this.playerAssistant.calculateCombatLevel();
		playerProps.writeByte(getCombatLevel());
		playerProps.writeString(this.gameModeTitle);
		playerProps.writeString(this.playerTitle);
		playerProps.writeString(this.titleColour);
		if (GameType.isPreEoc()) {
			if (Area.inWilderness(getX(), getY(), getHeight())
					&& getSummoning().getFamiliar() != null) {
				playerProps.writeWord(
						Skilling.getLevelForExperience(skillExperience[ServerConstants.SUMMONING]));
			} else {
				playerProps.writeWord(0);
			}
		}
		playerProps.writeByte(this.titleSwap);
		playerProps.writeByte(this.playerRights);
		playerProps.writeByte(getType() == EntityType.PLAYER_PET ? PlayerPetManager.PET_SIZE : 0);
		playerProps.writeWord(playerPet == null ? -1 : playerPet.getPlayerId());
		str.writeByteC(playerProps.currentOffset);
		str.writeBytes(playerProps.buffer, playerProps.currentOffset, 0);
	}

	public void updateThisPlayerMovement(Stream str) {
		if (this.bot) {
			if (didTeleport) {
				return;
			}
			if (dir1 == -1) {

				setMoving(false);
			} else {
				if (dir2 == -1) {
					setMoving(true);
				} else {
					setMoving(true);
				}
			}
			return;
		}
		if (mapRegionDidChange) {
			str.createFrame(73);
			str.writeWordA(getMapRegionX() + 6);
			str.writeWord(getMapRegionY() + 6);
		}
		if (didTeleport) {
			str.createFrameVarSizeWord(81);
			str.initBitAccess();
			str.writeBits(1, 1);
			str.writeBits(2, 3);
			str.writeBits(2, getHeight());
			str.writeBits(1, 1);
			str.writeBits(1, (isUpdateRequired()) ? 1 : 0);
			str.writeBits(7, currentY);
			str.writeBits(7, currentX);
			return;
		}
		if (dir1 == -1) {
			// don't have to update the character position, because we're
			// just standing
			str.createFrameVarSizeWord(81);
			str.initBitAccess();
			setMoving(false);
			tempMoving = false;
			if (isUpdateRequired()) {
				// tell client there's an update block appended at the end
				str.writeBits(1, 1);
				str.writeBits(2, 0);
			} else {
				str.writeBits(1, 0);
			}
		} else {
			str.createFrameVarSizeWord(81);
			str.initBitAccess();
			str.writeBits(1, 1);
			if (dir2 == -1) {
				setMoving(true);
				tempMoving = true;
				str.writeBits(2, 1);
				str.writeBits(3, Misc.xlateDirectionToClient[dir1]);
				if (isUpdateRequired())
					str.writeBits(1, 1);
				else
					str.writeBits(1, 0);
			} else {
				setMoving(true);
				tempMoving = true;
				str.writeBits(2, 2);
				str.writeBits(3, Misc.xlateDirectionToClient[dir1]);
				str.writeBits(3, Misc.xlateDirectionToClient[dir2]);
				if (isUpdateRequired())
					str.writeBits(1, 1);
				else
					str.writeBits(1, 0);
			}
		}
	}

	/**
	 * Adds all of the items from the players inventory and equipment to the
	 * container.
	 *
	 * @param container
	 *            the container we're adding the items to.
	 */
	public void itemsToContainer(ItemContainer container) {
		for (int index = 0; index < playerItems.length; index++) {
			int item = playerItems[index] - 1;

			int amount = playerItemsN[index];

			if (item <= 0 || amount <= 0) {
				continue;
			}
			container.add(new GameItem(item, amount));
		}

		for (int index = 0; index < playerEquipment.length; index++) {
			int item = playerEquipment[index];

			int amount = playerEquipmentN[index];

			if (item <= 0 || amount <= 0) {
				continue;
			}
			container.add(new GameItem(item, amount));
		}
	}

	public void updatePlayerMovement(Stream str) {
		if (dir1 == -1) {
			if (isUpdateRequired() || isChatTextUpdateRequired()) {
				str.writeBits(1, 1);
				str.writeBits(2, 0);
			} else
				str.writeBits(1, 0);
		} else if (dir2 == -1) {
			str.writeBits(1, 1);
			str.writeBits(2, 1);
			str.writeBits(3, Misc.xlateDirectionToClient[dir1]);
			str.writeBits(1, (isUpdateRequired() || isChatTextUpdateRequired()) ? 1 : 0);
		} else {
			str.writeBits(1, 1);
			str.writeBits(2, 2);
			str.writeBits(3, Misc.xlateDirectionToClient[dir1]);
			str.writeBits(3, Misc.xlateDirectionToClient[dir2]);
			str.writeBits(1, (isUpdateRequired() || isChatTextUpdateRequired()) ? 1 : 0);
		}
	}

	/**
	 * Called when the position of the entity has changed.
	 */
	@Override
	public void onPositionChange() {
		super.onPositionChange();

		if (minigame != null) {
			if (!minigame.inside(this)) {
				minigame.onOutsideBounds(this);
			}
		}
	}

	/**
	 * Called when the region of the entity changes
	 */
	@Override
	public void onRegionChange() {
		Region lastRegion = super.getRegionOrNull();

		Region next = Region.getRegion(getX(), getY());

		if (next != null) {
			if (lastRegion != null) {
				if (lastRegion != next) {
					lastRegion.removePlayerIfPresent(this);
				}
			}
			next.addPlayerIfAbsent(this);
			setRegion(next);
		}
	}

	/**
	 * Moves the entity to a new position.
	 *
	 * @param position the new position.
	 */
	@Override
	public void move(Position position) {
		super.move(position);

		playerAssistant.movePlayer(position.getX(), position.getY(), position.getZ());
	}

	public void addNewNpc(Npc npc, Stream str, Stream updateBlock) {
		int id = npc.npcIndex;
		npcInListBitmap[id >> 3] |= 1 << (id & 7);
		npcList[npcListSize++] = npc;
		str.writeBits(14, id);
		int z = npc.getY() - getY();
		if (z < 0)
			z += 32;
		str.writeBits(5, z);
		z = npc.getX() - getX();
		if (z < 0)
			z += 32;
		str.writeBits(5, z);
		str.writeBits(1, 0);
		str.writeBits(14, npc.npcType);// npc bits.
		boolean savedUpdateRequired = npc.updateRequired;
		npc.updateRequired = true;
		npc.appendNpcUpdateBlock(updateBlock, this, true);
		npc.updateRequired = savedUpdateRequired;
		str.writeBits(1, 1);
	}

	public void addNewPlayer(Player plr, Stream str, Stream updateBlock) {
		if (playerListSize >= 255) {
			return;
		}
		int id = plr.getPlayerId();
		playerInListBitmap[id >> 3] |= 1 << (id & 7);
		playerList[playerListSize] = plr;
		playerListSize++;
		str.writeBits(11, id);
		str.writeBits(1, 1);
		boolean savedFlag = plr.isAppearanceUpdateRequired();
		boolean savedUpdateRequired = plr.isUpdateRequired();
		plr.setAppearanceUpdateRequired(true);
		plr.setUpdateRequired(true);
		plr.appendPlayerUpdateBlock(updateBlock, false);
		plr.setAppearanceUpdateRequired(savedFlag);
		plr.setUpdateRequired(savedUpdateRequired);
		str.writeBits(1, 1);
		int z = plr.getY() - getY();
		if (z < 0)
			z += 32;
		str.writeBits(5, z);
		z = plr.getX() - getX();
		if (z < 0)
			z += 32;
		str.writeBits(5, z);
		str.writeBits(5, plr.directionFacingPath);
	}

	public boolean bot;

	public Player(IoSession ioSession, int playerId, boolean isBot, EntityType type) {
		super(type);

		this.bot = isBot;

		setPlayerId(playerId);

		this.session = ioSession;
		setOutStream(new Stream(new byte[ServerConstants.BUFFER_SIZE]));
		getOutStream().currentOffset = 0;
		inStream = new Stream(new byte[ServerConstants.BUFFER_SIZE]);
		inStream.currentOffset = 0;
		buffer = new byte[ServerConstants.BUFFER_SIZE];

		playerRights = 0;
		for (int i = 0; i < playerItems.length; i++) {
			playerItems[i] = 0;
		}
		for (int i = 0; i < playerItemsN.length; i++) {
			playerItemsN[i] = 0;
		}
		for (int i = 0; i < ServerConstants.getTotalSkillsAmount(); i++) {
			baseSkillLevel[i] = 1;
		}
		for (int i = 0; i < currentCombatSkillLevel.length; i++) {
			currentCombatSkillLevel[i] = 1;
		}
		for (int i = 0; i < skillExperience.length; i++) {
			skillExperience[i] = 0;
		}
		for (int i = 0; i < ServerConstants.BANK_SIZE; i++) {
			bankItems[i] = 0;
		}
		for (int i = 0; i < ServerConstants.BANK_SIZE; i++) {
			bankItemsN[i] = 0;
		}
		skillExperience[ServerConstants.HITPOINTS] = 1154;
		baseSkillLevel[ServerConstants.HITPOINTS] = 10;
		currentCombatSkillLevel[ServerConstants.HITPOINTS] = 10;
		playerAppearance[0] = 0; // gender
		playerAppearance[1] = 0; // head
		playerAppearance[2] = 18; // Torso
		playerAppearance[3] = 26; // arms
		playerAppearance[4] = 33; // hands
		playerAppearance[5] = 36; // legs
		playerAppearance[6] = 42; // feet
		playerAppearance[7] = 10; // beard
		playerAppearance[8] = 0; // hair colour
		playerAppearance[9] = 0; // torso colour
		playerAppearance[10] = 0; // legs colour
		playerAppearance[11] = 0; // feet colour
		playerAppearance[12] = 0; // skin colour
		playerEquipment[ServerConstants.HEAD_SLOT] = -1;
		playerEquipment[ServerConstants.CAPE_SLOT] = -1;
		playerEquipment[ServerConstants.AMULET_SLOT] = -1;
		playerEquipment[ServerConstants.BODY_SLOT] = -1;
		playerEquipment[ServerConstants.SHIELD_SLOT] = -1;
		playerEquipment[ServerConstants.LEG_SLOT] = -1;
		playerEquipment[ServerConstants.HAND_SLOT] = -1;
		playerEquipment[ServerConstants.FEET_SLOT] = -1;
		playerEquipment[ServerConstants.RING_SLOT] = -1;
		playerEquipment[ServerConstants.ARROW_SLOT] = -1;
		playerEquipment[ServerConstants.WEAPON_SLOT] = -1;
		setHeight(0);
		teleportToX = GameType.isOsrsEco() ? 3095 : 3087; // Starter co-ord for new players
		teleportToY = GameType.isOsrsEco() ? 3502 : 3517;
		setX(setY(-1));
		mapRegionX = mapRegionY = -1;
		currentX = currentY = 0;
		Movement.resetWalkingQueue(this);
		valuableLoot = GameType.isOsrsPvp() ? 10 : 5000;
		setAbleToEditCombat(GameType.isOsrsPvp() ? true : false);
	}

	/**
	 * Called when the entity is removed from the world.
	 */
	@Override
	public void onRemove() {
		super.onRemove();

		getEventHandler().stopAll();

		if (playerPet != null) {
			playerPet.setDisconnected(true, "player-pet");
		}

		Region region = getRegionOrNull();

		if (region == null) {
			region = Region.getRegion(playerX, playerY);
		}
		if (region != null) {
			region.removePlayerIfPresent(this);
		}

		getLocalNpcs().forEach(npc -> npc.getLocalPlayers().remove(this));
		getLocalPlayers().forEach(player -> player.getLocalPlayers().remove(this));
		getLocalNpcs().clear();
		getLocalPlayers().clear();
	}

	/**
	 * Called when the entity is added to the world.
	 */
	@Override
	public void onAdd() {
		super.onAdd();

		onRegionChange();
	}

	public boolean isFocusPointUpdateRequired() {
		return focusPointUpdateRequired;
	}

	public void setFocusPointUpdateRequired(boolean focusPointUpdateRequired) {
		this.focusPointUpdateRequired = focusPointUpdateRequired;
	}

	/**
	 * Determines if a specific position matches the players current position.
	 * <p>
	 * TODO (jason) replace playerX, playerY and height with a Position object and make a equals
	 * reference to determine equality.
	 *
	 * @param position the position that this player must match.
	 * @return true if the position x, y, and z match this players x, y, and z.
	 */
	public boolean samePosition(Position position) {
		return playerX == position.getX() && playerY == position.getY() && height == position.getZ();
	}

	/**
	 * The combat strategy for the entity, or null if there is no combat strategy to be used.
	 *
	 * @return the strategy used against other entities.
	 */
	@Override
	public EntityCombatStrategy getCombatStrategyOrNull() {
		return null;
	}

	/**
	 * True, if the player is doing an action.
	 */
	public boolean doingAnAction() {
		if (this.bot) {
			return false;
		}
		if (this.playerIsFiremaking || this.doingAction() || this.getDoingAgility()
				|| !this.isTutorialComplete() || this.isTeleporting() || this.isAnEgg
				|| this.usingPreachingEvent) {
			return true;
		}
		if (this.dragonSpearEvent) {
			return true;
		}
		return false;
	}

	public void sendDebugMessage(String message) {
		if (ServerConfiguration.DEBUG_MODE) {
			playerAssistant.sendMessage(message);
		}
	}

	public void sendDebugMessageF(String message, java.lang.Object... varargs) {
		sendDebugMessage(String.format(message, varargs));
	}

	public String getCapitalizedName() {
		return Misc.capitalize(getPlayerName());
	}

	/**
	 * Decrease the doingAction variable untill it reaches 0.
	 *
	 * @param time The amount of cycles the player will be doing an action.
	 */
	public void doingActionEvent(
			int time) { /* Check if this event is being used, if it is, then stop */
		if (isUsingDoingActionEvent) {
			return;
		}
		playerAssistant.stopAllActions();
		isUsingDoingActionEvent = true;
		doingActionTimer = time;
		/*
		 * The event is continious untill doingAction reaches 0.
		 */
		CycleEventHandler.getSingleton().addEvent(this, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				if (doingActionTimer > 0) {
					doingActionTimer--;
				}
				if (doingActionTimer == 0) {
					container.stop();
				}
			}

			@Override
			public void stop() {
				isUsingDoingActionEvent = false;
			}
		}, 1);
	}

	public void clearEquipmentSlot(Player player, int slot) {
		if (slot < 0 || slot > ServerConstants.ARROW_SLOT) {
			return;
		}
		playerEquipment[slot] = -1;
		playerEquipmentN[slot] = 0;
		player.setUpdateRequired(true);
		player.setAppearanceUpdateRequired(true);
	}

	/**
	 * @param amount The amount to add to the Hitpoints of the player.
	 */
	public void addToHitPoints(int amount) {
		if (this.getDead()) {
			return;
		}
		if (getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) > getBaseHitPointsLevel()) {
			return;
		}
		if (getCurrentCombatSkillLevel(ServerConstants.HITPOINTS)
				+ amount > getBaseHitPointsLevel()) {
			int extraAmount = (getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) + amount)
					- getBaseHitPointsLevel();
			amount -= extraAmount;
		}
		currentCombatSkillLevel[ServerConstants.HITPOINTS] += amount;
		Skilling.updateSkillTabFrontTextMain(this, ServerConstants.HITPOINTS);
	}

	/**
	 * @param amount The amount to subtract from the Hitpoints of the player.
	 */
	public void subtractFromHitPoints(int amount) {
		if (this.getDead()) {
			return;
		}
		if (getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) - amount < 0) {
			amount = getCurrentCombatSkillLevel(ServerConstants.HITPOINTS);
		} else {
			currentCombatSkillLevel[ServerConstants.HITPOINTS] -= amount;
		}
	}

	public int getNpcIndexAttackingPlayer() {
		return npcIndexAttackingPlayer;
	}

	public void setNpcIndexAttackingPlayer(int npcIndexAttackingPlayer) {
		this.npcIndexAttackingPlayer = npcIndexAttackingPlayer;
	}

	public String getBotStatus() {
		return botStatus;
	}

	public void setBotStatus(String botStatus) {
		this.botStatus = botStatus;
	}

	public int setObjectY(int objectY) {
		this.objectY = objectY;
		return objectY;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public double getSpecialAttackAmount() {
		return specialAttackAmount;
	}

	public void setSpecialAttackAmount(double specialAttackAmount, boolean startEvent) {
		this.specialAttackAmount = specialAttackAmount;
		if (startEvent) {
			Combat.restoreSpecialAttackEvent(this);
		}
	}

	public int getClanId() {
		return clanId;
	}

	public void setClanId(int clanId) {
		this.clanId = clanId;
	}

	public int getZombiePartnerId() {
		return zombiePartnerId;
	}

	public void setZombiePartnerId(int zombiePartnerId) {
		this.zombiePartnerId = zombiePartnerId;
	}

	public boolean isInZombiesMinigame() {
		return inZombiesMinigame;
	}

	public void setInZombiesMinigame(boolean inZombiesMinigame) {
		this.inZombiesMinigame = inZombiesMinigame;
	}

	public String getProfileNameSearched() {
		return profileNameSearched;
	}

	public void setProfileNameSearched(String profileNameSearched) {
		profileNameSearched = Misc.capitalize(profileNameSearched);
		this.profileNameSearched = profileNameSearched;
	}

	public int getProfileSearchOnlinePlayerId() {
		return profileSearchOnlinePlayerId;
	}

	public void setProfileSearchOnlinePlayerId(int profileSearchOnlinePlayerId) {
		this.profileSearchOnlinePlayerId = profileSearchOnlinePlayerId;
	}

	public boolean isBotActionApplied() {
		return botActionApplied;
	}

	public void setBotActionApplied(boolean botActionApplied) {
		this.botActionApplied = botActionApplied;
	}

	public long getFrozenLength() {
		return frozenLength;
	}

	public void setFrozenLength(long frozenLength) {
		if (frozenLength > 0) {
			timeFrozen = System.currentTimeMillis();
		}
		this.frozenLength = frozenLength;
	}

	public int getActionIdUsed() {
		return actionIdUsed;
	}

	public void setActionIdUsed(int teleportNpcId) {
		this.actionIdUsed = teleportNpcId;
	}

	public void resetActionIdUsed() {
		this.actionIdUsed = 0;
	}

	public boolean isInTrade() {
		return inTrade;
	}

	public void setInTrade(boolean inTrade) {
		this.inTrade = inTrade;
	}

	public int getPetId() {
		return petId;
	}

	public void setPetId(int petId) {
		this.petId = petId;
	}

	public int getTradeStatus() {
		return tradeStatus;
	}

	public void setTradeStatus(int tradeStatus) {
		this.tradeStatus = tradeStatus;
	}

	public int getDialogueAction() {
		return dialogueAction;
	}

	public void setDialogueAction(int dialogueAction) {
		this.lastDialogueSelected = dialogueAction;
		this.dialogueAction = dialogueAction;
	}

	public int getSecondPetId() {
		return secondPetId;
	}

	public void setSecondPetId(int secondPetId) {
		this.secondPetId = secondPetId;
	}

	public boolean getSecondPetSummoned() {
		return secondPetSummoned;
	}

	public void setSecondPetSummoned(boolean secondPetSummoned) {
		this.secondPetSummoned = secondPetSummoned;
	}

	public boolean isInRandomEvent() {
		return !randomEvent.isEmpty();
	}

	public boolean isInRandomEventType(String type) {
		return randomEvent.equals(type);
	}

	public void setRandomEvent(String type) {
		this.randomEvent = type;
	}

	public int getShopId() {
		return shopId;
	}

	public void setShopId(int shopId) {
		this.shopId = shopId;
	}

	public int getF2pKills() {
		return f2pKills;
	}

	public void setF2pKills(int f2pKills) {
		this.f2pKills = f2pKills;
	}

	/**
	 * @return The delay for druid teleporting
	 */
	public int getDruidDelay() {
		return druidTeleportDelay;
	}

	/**
	 * Used to calculate the time since an elder chaos druid teleports a player
	 */
	public void lastDruidTele(int amount) {
		druidTeleportDelay = amount;
	}

	public int getLastNpcAttackedIndex() {
		return lastNpcAttackedIndex;
	}

	public void setLastNpcAttackedIndex(int lastNpcAttackedIndex) {
		this.lastNpcAttackedIndex = lastNpcAttackedIndex;
	}

	public int getDuelItemSlot() {
		return duelItemSlot;
	}

	public void setDuelItemSlot(int duelSlot) {
		this.duelItemSlot = duelSlot;
	}

	public boolean isFlaggedForRwt() {
		return flaggedForRwt;
	}

	public void setFlaggedForRwt(boolean flaggedForRwt) {
		this.flaggedForRwt = flaggedForRwt;
		if (!this.flaggedForRwtEventActive) {
			this.flaggedForRwtEventActive = true;
			CycleEventHandler.getSingleton().addEvent(this, new CycleEvent() {
				@Override
				public void execute(CycleEventContainer container) {
					container.stop();
					if (ServerConfiguration.DEBUG_MODE) {
						return;
					}
					FileUtility.saveArrayContentsSilent(
							"backup/logs/rwt/chat/" + getPlayerName() + ".txt", rwtChat);
					rwtChat.clear();
				}

				@Override
				public void stop() {
					flaggedForRwtEventActive = false;
				}
			}, 200);
		}
	}

	public ItemContainer getZulrahLostItems() {
		return zulrahLostItems;
	}

	public Minigame getMinigame() {
		return minigame;
	}

	public void setMinigame(Minigame minigame) {
		this.minigame = minigame;
	}

	public int getSlayerPoints() {
		return slayerPoints;
	}

	public void setSlayerPoints(int slayerPoints) {
		this.slayerPoints = slayerPoints;
	}

	public long getTimePlayerLastActive() {
		return timePlayerLastActive;
	}

	public void setTimePlayerLastActive(long timePlayerLastActive) {
		this.timePlayerLastActive = timePlayerLastActive;
	}

	public int getTeleportCycle() {
		return teleportCycle;
	}

	public void setTeleportCycle(int teleportCycle) {
		this.teleportCycle = teleportCycle;
	}

	public String getGameMode() {
		return gameMode;
	}

	public void setGameMode(String gameMode) {
		this.gameMode = gameMode;
	}

	public String getDifficultyChosen() {
		return difficultyChosen;
	}

	public void setDifficultyChosen(String difficultyChosen) {
		this.difficultyChosen = difficultyChosen;
	}

	public int getFirstItemClicked() {
		return firstItemClicked;
	}

	public void setFirstItemClicked(int firstItemClicked) {
		this.firstItemClicked = firstItemClicked;
	}

	public int getCustomPetPoints() {
		return customPetPoints;
	}

	public void setCustomPetPoints(int customPetPoints) {
		this.customPetPoints = customPetPoints;
	}


	public String getGambledPlayerOptionName() {
		return gambledPlayerOptionName;
	}

	public void setGambledPlayerOptionName(String gambledPlayerOptionName) {
		this.gambledPlayerOptionName = gambledPlayerOptionName;
	}

	public boolean isInGambleMatch() {
		return isInGambleMatch;
	}

	public void setInGambleMatch(boolean isInGambleMatch) {
		this.isInGambleMatch = isInGambleMatch;
	}

	public int getInterfaceIdOpened() {
		return interfaceIdOpened;
	}

	public void setInterfaceIdOpened(int interfaceIdOpened) {
		InterfaceAssistant.interfaceClosed(this);
		this.interfaceIdOpened = interfaceIdOpened;
	}

	public boolean isInfernalAndMaxCapesUnlockedScrollConsumed() {
		return infernalAndMaxCapesUnlockedScrollConsumed;
	}

	public void setInfernalAndMaxCapesUnlockedScrollConsumed(
			boolean infernalAndMaxCapesUnlockedScrollConsumed) {
		this.infernalAndMaxCapesUnlockedScrollConsumed = infernalAndMaxCapesUnlockedScrollConsumed;
	}

	@Override
	public String toString() {
		return String.format("username=%s, usernameAsLong=%s", playerName, nameAsLong);
	}

	public long getTimeLastClaimedDonation() {
		return timeLastClaimedDonation;
	}

	public void setTimeLastClaimedDonation(long timeLastClaimedDonation) {
		this.timeLastClaimedDonation = timeLastClaimedDonation;
	}

	public int getWildernessLevel() {
		return wildernessLevel;
	}

	public void setWildernessLevel(int wildernessLevel) {
		this.wildernessLevel = wildernessLevel;
	}

	public Shop getShop() {
		return shop;
	}

	public void setShop(Shop shop) {
		this.shop = shop;
	}

	/**
	 * Sets the summoning
	 *
	 * @return the summoning
	 */
	public Summoning getSummoning() {
		return summoning;
	}

	public DialogueChain getDialogueChain() {
		return dialogueChain;
	}

	public void setDialogueChainAndStart(DialogueChain chain) {
		setDialogueChain(chain).start(this);
	}

	public DialogueChain setDialogueChain(DialogueChain dialogueChain) {
		this.dialogueChain = dialogueChain;

		return dialogueChain;
	}

	/**
	 * Gets the prayer
	 *
	 * @return the prayer
	 */
	public PrayerManager getPrayer() {
		return prayer;
	}

	public Hunter getHunterSkill() {
		return hunterSkill;
	}

	public int getxInterfaceId() {
		return xInterfaceId;
	}

	public void setxInterfaceId(int xInterfaceId) {
		if (xInterfaceId > 0) {
			this.setAmountInterface("");
		}
		this.xInterfaceId = xInterfaceId;
	}

	public NpcToPlayerDamageQueue getIncomingNpcDamage() {
		return incomingNpcDamage;
	}

	public PlayerToPlayerDamageQueue getIncomingDamageOnVictim() {
		return incomingDamageOnVictim;
	}

	public int getBowSpecShot() {
		return bowSpecShot;
	}

	public void setBowSpecShot(int bowSpecShot) {
		this.bowSpecShot = bowSpecShot;
	}

	public long getXpBonusEndTime() {
		return xpBonusEndTime;
	}

	public void setXpBonusEndTime(long xpBonusEndTime) {
		this.xpBonusEndTime = xpBonusEndTime;
	}

	/**
	 * Gets the moneyPouch
	 *
	 * @return the moneyPouch
	 */
	public MoneyPouch getMoneyPouch() {
		return moneyPouch;
	}

	public String getLastDueledWithName() {
		return lastDueledWithName;
	}

	public void setLastDueledWithName(String lastDueledWithName) {
		this.lastDueledWithName = lastDueledWithName;
	}

	public void resetAnimation() {
		this.startAnimation(65535);
	}

	public String getDateUsedSpellbookSwap() {
		return dateUsedSpellbookSwap;
	}

	public void setDateUsedSpellbookSwap(String dateUsedSpellbookSwap) {
		this.dateUsedSpellbookSwap = dateUsedSpellbookSwap;
	}

	public int getSpellbookSwapUsedOnSameDateAmount() {
		return spellbookSwapUsedOnSameDateAmount;
	}

	public void setSpellbookSwapUsedOnSameDateAmount(int spellbookSwapUsedOnSameDateAmount) {
		this.spellbookSwapUsedOnSameDateAmount = spellbookSwapUsedOnSameDateAmount;
	}

	public ItemContainer getVorkathLostItems() {
		return vorkathLostItems;
	}
	/**
	 * Gets the degrading
	 *
	 * @return the degrading
	 */
	public DegradingManager getDegrading() {
		return degrading;
	}

	public Position getMovementDestination() {
		return movementDestination;
	}

	public MovementCompletionEvent getMovementCompletionEvent() {
		return movementCompletionEvent;
	}

	public Position getPosition() {
		return new Position(getX(), getY(), getHeight());
	}

	public void setMapRegionX(int mapRegionX) {
		this.mapRegionX = mapRegionX;
	}

	public void setMapRegionY(int mapRegionY) {
		this.mapRegionY = mapRegionY;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setPlayerPet(PlayerPet playerPet) {
		this.playerPet = playerPet;
	}

	public PlayerPet getPlayerPet() {
		return playerPet;
	}

	public long getTimeEarnedBloodMoneyInResourceWild() {
		return timeEarnedBloodMoneyInResourceWild;
	}

	public void setTimeEarnedBloodMoneyInResourceWild(long timeEarnedBloodMoneyInResourceWild) {
		this.timeEarnedBloodMoneyInResourceWild = timeEarnedBloodMoneyInResourceWild;
	}

	public PlayerPetState getPlayerPetState() {
		return playerPetState;
	}

	public void setPlayerPetState(PlayerPetState playerPetState) {
		this.playerPetState = playerPetState;
	}

	public void resetDamageTaken() {
		damageTaken = new int[ServerConstants.MAXIMUM_PLAYERS];
		damageTakenName = new String[ServerConstants.MAXIMUM_PLAYERS];
	}

	public void setInteractingObjectDefinition(ObjectDefinitionServer definition) {
		this.interactingObjectDefinition = definition;
	}

	public ObjectDefinitionServer getInteractingObjectDefinition() {
		return interactingObjectDefinition;
	}

	public void setWalkToObjectEvent(WalkToObjectEvent walkToObjectEvent) {
		this.walkToObjectEvent = walkToObjectEvent;
	}

	public void setInteractingObject(Object object) {
		this.interactingObject = object;
	}

	public Object getInteractingObject() {
		return interactingObject;
	}

	public WalkToObjectEvent getWalkToObjectEvent() {
		return walkToObjectEvent;
	}
	/**
	 * Gets the summoningPet
	 *
	 * @return the summoningPet
	 */
	public SummoningPetManager getSummoningPet() {
		return summoningPet;
	}

	public long getTimeCanDisconnectAtBecauseOfCombat() {
		return timeCanDisconnectAtBecauseOfCombat;
	}

	public void setTimeCanDisconnectAtBecauseOfCombat(long timeCanDisconnectAtBecauseOfCombat) {
		this.timeCanDisconnectAtBecauseOfCombat = timeCanDisconnectAtBecauseOfCombat;
	}

	public boolean isUsedMysteryBox() {
		return usedMysteryBox;
	}

	public void setUsedMysteryBox(boolean usedMysteryBox) {
		this.usedMysteryBox = usedMysteryBox;
	}

	public long getTimePlayerCanBeAttacked() {
		return timePlayerCanBeAttacked;
	}

	public void setTimePlayerCanBeAttacked(long timePlayerCanBeAttacked) {
		this.timePlayerCanBeAttacked = timePlayerCanBeAttacked;
	}

	public int getSqlIndex() {
		return sqlIndex;
	}

	public void setSqlIndex(int getSqlIndex) {
		this.sqlIndex = getSqlIndex;
	}

	public String getForcedText() {
		return forcedText;
	}

	public void setForcedText(String forcedText) {
		this.forcedText = forcedText;
	}
}
