package game.npc;


import core.ServerConstants;
import game.position.Position;
import game.content.combat.damage.queue.impl.PlayerToNpcDamageQueue;
import game.content.minigame.barrows.Barrows;
import game.entity.Entity;
import game.entity.EntityType;
import game.entity.MovementState;
import game.entity.combat_strategy.EntityCombatStrategy;
import game.npc.combat.DamageQueue;
import game.npc.data.NpcDefinition;
import game.object.clip.Region;
import game.player.Player;
import game.player.PlayerHandler;
import game.player.event.CycleEventContainer;
import game.player.movement.Follow;
import java.util.ArrayList;
import java.util.List;
import network.packet.Stream;
import utility.Misc;

public class Npc extends Entity {

	private final PlayerToNpcDamageQueue incomingPlayerDamage = new PlayerToNpcDamageQueue();

	/**
	 * True if the npc is attackable.
	 */
	private boolean attackable = true;

	/**
	 * Whether a summoned familiar
	 */
	private boolean familiar;

	/**
	 * A flag that determines whether or not items should be dropped.
	 */
	private boolean itemsDroppable = true;

	/**
	 * Determines whether or not an update is required to
	 */
	private boolean focusPointUpdateRequired;

	/**
	 * Determines if the entity can attack even if the path is blocked
	 */
	private boolean attackWithPathBlocked;

	/**
	 * Determines if this entity ever faces another entity, by default, false.
	 */
	private boolean facingEntityDisabled;

	/**
	 * The idle animation to be updated for this npc.
	 */
	private int idleAnimation;

	/**
	 * Determines whether or not we require an update for our idle animation.
	 */
	private boolean idleAnimationUpdateRequired;

	/**
	 * Determines if this entity can attack while its the same position as another.
	 */
	private boolean attackableWhileSamePosition;

	/**
	 * True to use clever boss mechanics, this gives more freedom in perfecting boss mechanics to what Osrs has.
	 */
	public boolean useCleverBossMechanics;

	/**
	 * Stop the next normal attack hitsplat from coming up.
	 */
	public boolean cleverBossStopAttack;

	/**
	 * The fixed damage the boss will deal.
	 */
	public int cleverBossFixedDamage;

	/**
	 * How long till the hitsplat appears.
	 */
	public int cleverBossHitsplatDelay;

	/**
	 * The hitsplat type. Magic. Ranged or Melee.
	 */
	public int cleverBossHitsplatType;

	/**
	 * The attack animation this clever boss will use.
	 */
	public int cleverBossAttackAnimation;

	public int forceNormalNpcFixedDamage = -1;

	public long tormentedDemonTimeWeakened;

	public boolean tormentedDemonShield = true;

	public long tormentedDemonTimeChangedPrayer;

	public int tormentedDemonPrayerChangeRandom;

	/**
	 * Delete the npc next npc game tick.
	 */
	public boolean deleteNpc;

	/**
	 * The last damage style to damage the npc.
	 */
	public int attackStyleDamagedBy = -1;

	/**
	 * True, if the NPC has been summoned by a player.
	 */
	public boolean summoned;

	/**
	 * The identity of the player that summoned this NPC.
	 */
	private int npcPetOwnerId;

	public long timeFoundNewTarget;

	public long timeTurnedByPlayer;

	/**
	 * True if the npc shall be compeletely deleted when respawning, instead of actually respawning.
	 */
	public boolean doNotRespawn;

	public boolean instancedNpcForceRespawn;

	public String name = "";

	/**
	 * The npc index in the npc array.
	 */
	public int npcIndex;

	/**
	 * The npc identity in the client.
	 */
	public int npcType;

	private int x;

	private int y;

	private int height;

	private int spawnPositionX;

	private int spawnPositionY;

	/**
	 * True if the npc has moved during this tick.
	 */
	private boolean moved;

	private int moveX;

	private int moveY;

	public int direction;

	/**
	 * NPC attack type, Melee = 0, Ranged = 1, Magic = 2, Dragonfire = 3.
	 * //TODO Replace this with {@link game.entity.combat_strategy.EntityAttackType} in the near future.
	 */
	public int attackType;

	public int projectileId, endGfx, startingGfx;

	private boolean clippingIgnored;

	/**
	 * Only if this npc is spawned by a player to be killed. Such as Barrows npc, Warriors guild npc.
	 * Once the player does not exist or walks too far away, this npc will be deleted.
	 */
	private int spawnedBy;

	public int hitDelayTimer;

	private int currentHitPoints;

	public int maximumHitPoints;

	public int animNumber;

	public int respawnTimer;

	public int enemyX;

	public int enemyY;

	public boolean bottomGfx;

	public boolean applyDead;

	private boolean isDead;

	public boolean needRespawn;

	private boolean walkingHome;

	public boolean underAttack;

	public long timeAttackedAPlayer;

	public String faceAction = "";

	public int attackTimer;

	private boolean neverRandomWalks;

	private boolean walkingHomeDisabled;

	/**
	 * The time the npc was frozen.
	 */
	public long timeFrozen = 0;

	private DamageQueue damageQueue = new DamageQueue();

	/**
	 * Creates a new instance of this npc with the given index.
	 *
	 * @param index
	 *            the new index of this npc.
	 * @return the new instance.
	 */
	public Npc copy(int index) {
		throw new UnsupportedOperationException("This function is not supported.");
	}

	public DamageQueue getDamage() {
		return damageQueue;
	}

	/**
	 * The amount of time the npc won't be able to move for.
	 * 8000 means 8 seconds.
	 */
	private long frozenLength = 0;

	public boolean isFrozen() {
		if (System.currentTimeMillis() - timeFrozen >= getFrozenLength()) {
			return false;
		}
		return true;
	}

	/**
	 * True if the npc can be frozen.
	 */
	public boolean canBeFrozen() {
		if (System.currentTimeMillis() - timeFrozen >= (getFrozenLength() + 3500)) {
			return true;
		}
		return false;
	}

	/**
	 * Called when the region of the entity changes
	 */
	@Override
	public void onRegionChange() {
		super.onRegionChange();

		Region lastRegion = super.getRegionOrNull();

		Region next = Region.getRegion(getX(), getY());

		if (next != null) {
			if (lastRegion != null) {
				if (lastRegion.id() != next.id()) {
					lastRegion.removeNpcIfPresent(this);
				}
			}
			if (!next.contains(this)) {
				next.addNpcIfAbsent(this);
				setRegion(next);
			}
		}
	}

	public Position getDropPosition() {
		// Cave kraken/Master whirlpool
		int npcX = getVisualX();
		int npcY = getVisualY();

		// Kraken
		if (this.npcType == 496) {
			npcX = 2280;
			npcY = 10031;
		}
		return new Position(npcX, npcY, height);
	}

	private int killerId;

	private int killedBy;

	public int oldIndex;

	public int underAttackBy;

	public long lastDamageTaken;

	public boolean randomWalk;

	public boolean dirUpdateRequired;

	public boolean animUpdateRequired;

	public boolean hitUpdateRequired;

	public boolean updateRequired;

	public boolean forcedChatRequired;

	private String forcedText;

	private EntityType followingType;

	private int followIndex;

	public Npc(int npcId, int npcType) {
		super(EntityType.NPC);
		npcIndex = npcId;
		this.npcType = npcType;
		direction = -1;
		setDead(false);
		applyDead = false;
		respawnTimer = 0;
		randomWalk = true;
	}

	/**
	 * A listener function that is referenced every sequence of the non-playable character update.
	 */
	public void onSequence() {
		super.onSequence();
	}

	/**
	 * Referenced when the npc is added to the world.
	 */
	@Override
	public void onAdd() {
		super.onAdd();

		onRegionChange();
	}

	/**
	 * Referenced when the npc is removed from the world.
	 */
	@Override
	public void onRemove() {
		super.onRemove();

		getEventHandler().stopAll();

		Region region = getRegionOrNull();

		if (region == null) {
			region = Region.getRegion(x, y);
		}
		if (region != null) {
			region.removeNpcIfPresent(this);
		}

		getLocalNpcs().forEach(npc -> npc.getLocalNpcs().remove(this));
		getLocalNpcs().clear();
	}

	/**
	 * Referenced when a player is added to this entities local list.
	 *
	 * @param player the player that was being added to the npcs local list.
	 */
	public void onAddToLocalList(Player player) {

	}


	/**
	 * A listener function that is referenced when the non-playable character first dies, at the start of the
	 * animation.
	 */
	public void onDeath() {

	}

	/**
	 * A listener function that is referenced when the non-playable character is removed after dying.
	 */
	public void afterDeath() {

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

	public int transformId = -1;

	public void appendTransformUpdate(Stream str) {
		str.writeWordBigEndianA(transformId);
	}

	public void transform(int Id) {
		transformId = Id;
		updateRequired = true;
		for (int index = 0; index < ServerConstants.MAXIMUM_PLAYERS; index++) {
			Player loop = PlayerHandler.players[index];
			if (loop == null) {
				continue;
			}
			loop.npcTransformRequiresUpdate[npcIndex] = true;
		}
	}

	public void updateNpcMovement(Stream str) {
		if (direction == -1) {
			if (updateRequired) {
				str.writeBits(1, 1);
				str.writeBits(2, 0);
			} else {
				str.writeBits(1, 0);
			}
		} else {
			str.writeBits(1, 1);
			str.writeBits(2, 1);
			str.writeBits(3, Misc.xlateDirectionToClient[direction]);
			if (updateRequired) {
				str.writeBits(1, 1);
			} else {
				str.writeBits(1, 0);
			}
		}
	}

	/**
	 * Text update
	 **/

	public void forceChat(String text) {
		forcedText = text;
		forcedChatRequired = true;
		updateRequired = true;
	}

	/**
	 * Graphics
	 **/

	public int mask80var1 = 0;

	public int mask80var2 = 0;

	protected boolean mask80update = false;

	public void appendMask80Update(Stream str) {
		str.writeWord(mask80var1);
		str.writeDWord(mask80var2);
	}

	public void gfx100(int gfx) {
		mask80var1 = gfx;
		mask80var2 = 6553600;
		mask80update = true;
		updateRequired = true;
	}

	public int teleportDelay = -1, teleX, teleY, teleHeight;

	/**
	 * Attempts to heal the non-playable character by the given amount of hitpoints provided.
	 * It cannot exceed the value specified by {@link #maximumHitPoints}.
	 *
	 * @param amount the amount to increase the health by.
	 */
	public void heal(int amount) {
		if (currentHitPoints >= maximumHitPoints) {
			return;
		}
		if (amount + currentHitPoints > maximumHitPoints) {
			amount = maximumHitPoints - currentHitPoints;
		}
		currentHitPoints += amount;
	}

	public void setMaximumHealth(int maximumHealth) {
		if (maximumHealth < 0) {
			throw new IllegalArgumentException("The maximum health cannot be less than zero.");
		}
		this.maximumHitPoints = maximumHealth;

		if (currentHitPoints > maximumHitPoints) {
			currentHitPoints = maximumHitPoints;
		}
	}

	public void setMaximumHealthAndHeal(int maximumHitPoints) {
		setMaximumHealth(maximumHitPoints);
		heal(maximumHitPoints);
	}

	public void npcTeleport(int x, int y, int h) {
		needRespawn = true;
		teleX = x;
		teleY = y;
		teleHeight = h;
		updateRequired = true;
		teleportDelay = 0;
	}

	public void gfx0(int gfx) {
		mask80var1 = gfx;
		mask80var2 = 65536;
		mask80update = true;
		updateRequired = true;
	}

	public void appendAnimUpdate(Stream str) {
		str.writeWordBigEndian(animNumber);
		str.writeByte(1);
	}

	public void requestAnimation(int animId) {
		animNumber = animId;
		animUpdateRequired = true;
		updateRequired = true;
	}

	@Override
	public void move(Position position) {
		if (super.isRequiringReplacement()) {
			return;
		}
		super.move(position);

		x = position.getX();
		y = position.getY();
		height = position.getZ();
		updateRequired = true;
		killerId = 0;
		resetFollowing();
		moveX = 0;
		moveY = 0;
	}

	/**
	 * Called when the position of the entity has changed.
	 */
	@Override
	public void onPositionChange() {
		super.onPositionChange();

		List<Npc> localToRemove = new ArrayList<>();

		for (Npc local : getLocalNpcs()) {
			if (local == null || local.isDead || local.needRespawn) {
				localToRemove.add(local);
				continue;
			}
			if (Misc.withinLocalViewport(x, y, local.x, local.y)) {
				continue;
			}
			localToRemove.add(local);
		}

		if (!localToRemove.isEmpty()) {
			for (Npc npc : localToRemove) {
				if (npc == null) {
					continue;
				}
				npc.getLocalNpcs().remove(this);
			}
			getLocalNpcs().removeAll(localToRemove);
		}
		List<Region> surroundingRegions = Region.getSurrounding(x, y);

		for (Region region : surroundingRegions) {
			region.forEachNpc(npc -> {
				if (npc == null || npc == this) {
					return;
				}
				if (npc.height != height || npc.needRespawn || !Misc.withinLocalViewport(x, y, npc.x, npc.y)) {
					return;
				}
				if (getLocalNpcs().size() >= 255) {
					return;
				}
				if (getLocalNpcs().contains(npc)) {
					return;
				}
				getLocalNpcs().add(npc);
				npc.getLocalNpcs().add(this);
			});
		}
	}

	/**
	 * Face
	 **/

	public int FocusPointX = -1, FocusPointY = -1;

	public int face = 0;

	/**
	 * True for the npc to face the player just once.
	 */
	public boolean facePlayerOnce;

	public int hitDiff;

	/**
	 * 1.0 means the npc damage will completely go through to the player. it's damage * hitThroughPrayerAmount.
	 * So 0.5 means only 50% of Npc damage will occur on prayer.
	 */
	public double hitThroughPrayerAmount = 0.0;

	/**
	 * Attempts to kill the npc but only if it is alive.
	 */
	public void killIfAlive() {
		if (isDead || applyDead) {
			return;
		}
		isDead = true;
	}

	private void appendSetFocusDestination(Stream str) {
		str.writeWordBigEndian(FocusPointX);
		str.writeWordBigEndian(FocusPointY);
	}

	public void turnNpc(int i, int j) {
		if (System.currentTimeMillis() - timeTurnedByPlayer < 5000) {
			return;
		}
		FocusPointX = 2 * i + 1;
		FocusPointY = 2 * j + 1;
		updateRequired = true;
		focusPointUpdateRequired = true;
	}

	public void appendFaceEntity(Stream str) {
		str.writeWord(face);
	}

	public void facePlayer(int player) {
		if (player == 0 && facePlayerOnce) {
			return;
		}
		if (facingEntityDisabled) {
			return;
		}
		face = player + 32768;
		dirUpdateRequired = true;
		updateRequired = true;
	}

	public void faceNpc(int index) {
		if (facingEntityDisabled) {
			return;
		}
		face = index;
		dirUpdateRequired = true;
		updateRequired = true;
	}

	public void resetFace() {
		face = 65535;
		dirUpdateRequired = true;
		updateRequired = true;
	}

	public void resetFollowing() {
		followIndex = -1;
		followingType = null;
	}

	public void appendNpcUpdateBlock(Stream str, Player c, boolean addNewNpc) {
		if (!updateRequired) {
			return;
		}
		boolean npcTransformMaskUpdateRequired = c.npcTransformRequiresUpdate[npcIndex] && transformId >= 0;

		if (addNewNpc && transformId >= 0) {
			npcTransformMaskUpdateRequired = true;
		}

		int updateMask = 0;
		if (animUpdateRequired)
			updateMask |= 0x10;
		if (hitUpdateRequired2)
			updateMask |= 8;
		if (mask80update)
			updateMask |= 0x80;
		if (dirUpdateRequired)
			updateMask |= 0x20;
		if (forcedChatRequired)
			updateMask |= 1;
		if (hitUpdateRequired)
			updateMask |= 0x40;
		if (npcTransformMaskUpdateRequired)
			updateMask |= 2;
		if (focusPointUpdateRequired)
			updateMask |= 0x200;
		if (idleAnimationUpdateRequired) {
			updateMask |= 0x100;
		}

		if (updateMask >= 0x100) {
			updateMask |= 4;
			str.writeByte(updateMask & 0xFF);
			str.writeByte(updateMask >> 8);
		} else {
			str.writeByte(updateMask);
		}

		if (animUpdateRequired)
			appendAnimUpdate(str);
		if (hitUpdateRequired2)
			appendHitUpdate2(str, c);
		if (mask80update)
			appendMask80Update(str);
		if (dirUpdateRequired)
			appendFaceEntity(str);
		if (forcedChatRequired) {
			str.writeString(forcedText);
		}
		if (hitUpdateRequired) {
			appendHitUpdate(str, c);
		}
		if (npcTransformMaskUpdateRequired) {
			appendTransformUpdate(str);
		}
		if (focusPointUpdateRequired) {
			appendSetFocusDestination(str);
		}
		if (idleAnimationUpdateRequired) {
			appendIdleAnimationUpdate(str);
		}
		c.npcTransformRequiresUpdate[npcIndex] = false;
	}

	private void appendIdleAnimationUpdate(Stream stream) {
		stream.writeDWord(idleAnimation);
	}

	public void clearUpdateFlags() {
		idleAnimationUpdateRequired = false;
		updateRequired = false;
		forcedChatRequired = false;
		hitUpdateRequired = false;
		hitUpdateRequired2 = false;
		animUpdateRequired = false;
		dirUpdateRequired = false;
		facePlayerOnce = false;
		mask80update = false;
		focusPointUpdateRequired = false;
		forcedText = null;
		setMoveX(0);
		setMoveY(0);
		direction = -1;
		FocusPointX = -1;
		FocusPointY = -1;
		setRequiresReplacement(false);
	}

	public boolean walkTileInDirection(int x, int y) {
		if (moved) {
			return false;
		}
		int xMovement = NpcHandler.compareMovement(this.x, x);

		int yMovement = NpcHandler.compareMovement(this.y, y);

		if (xMovement == 0 && yMovement == 0) {
			return false;
		}
		moveX = xMovement;
		moveY = yMovement;
		getNextNPCMovement(npcIndex);
		updateRequired = true;
		moved = true;
		return true;
	}

	public boolean isAttackableWhileSamePosition() {
		return attackableWhileSamePosition;
	}

	public void setAttackableWhileSamePosition(boolean attackableWhileSamePosition) {
		this.attackableWhileSamePosition = attackableWhileSamePosition;
	}

	public CycleEventContainer<Entity> getPoisonCycleEvent() {
		return poisonCycleEvent;
	}

	public void setPoisonCycleEvent(CycleEventContainer<Entity> poisonCycleEvent) {
		this.poisonCycleEvent = poisonCycleEvent;
	}

	public int getIdleAnimation() {
		return idleAnimation;
	}

	public void setIdleAnimation(int idleAnimation) {
		this.idleAnimation = idleAnimation;
		this.idleAnimationUpdateRequired = true;
		this.updateRequired = true;
	}

	public boolean isFacingEntityDisabled() {
		return facingEntityDisabled;
	}

	public void setFacingEntityDisabled(boolean facingEntityDisabled) {
		this.facingEntityDisabled = facingEntityDisabled;
	}

	public boolean canAttackWithPathBlocked() {
		return attackWithPathBlocked;
	}

	public void setAttackWithPathBlocked(boolean attackWithPathBlocked) {
		this.attackWithPathBlocked = attackWithPathBlocked;
	}

	public boolean isItemsDroppable() {
		return itemsDroppable;
	}

	public void setItemsDroppable(boolean itemsDroppable) {
		this.itemsDroppable = itemsDroppable;
	}

	public PlayerToNpcDamageQueue getIncomingPlayerDamage() {
		return incomingPlayerDamage;
	}

	enum NextWalkingDirecitonTileSearch {
		LINEAR_FOR_LOOP,

		REGION,

		LOCAL
	}


	NextWalkingDirecitonTileSearch nextWalkingDirecitonTileSearch = NextWalkingDirecitonTileSearch.LOCAL;

	public int getNextNpcWalkingDirection() {
		int dir;

		int nextXtile = getX() + getMoveX();

		int nextYtile = getY() + getMoveY();

		dir = Misc.direction(getX(), getY(), (nextXtile), (nextYtile));

		int size = 1;

		if (NpcDefinition.getDefinitions()[this.npcType] != null) {
			size = NpcDefinition.getDefinitions()[this.npcType].size;
		}

		if (dir == -1) {
			return -1;
		}

		if (barrowsNpcClipping(nextXtile, nextYtile)) {
			return -1;
		}

		if (!clippingIgnored && !Region.isStraightPathUnblocked(getX(), getY(), nextXtile, nextYtile, getHeight(), size, size, false)) {
			return -1;
		}

		//TODO remove the if-else once a final decision on what to use has been enstablished
		if (nextWalkingDirecitonTileSearch == NextWalkingDirecitonTileSearch.REGION) {
			Region region = getRegionOrNull();

			if (region != null) {
				if (region.anyMatch(n -> n != null && n != this && !n.isDead() && n.getX() == getX() && n.getY() == getY())) {
					return -1;
				}
			}

		} else if (nextWalkingDirecitonTileSearch == NextWalkingDirecitonTileSearch.LOCAL) {
			if (getLocalNpcs().stream().anyMatch(n -> n != null && n != this && !n.isDead() && n.getX() == nextXtile && n.getY() == nextYtile)) {
				return -1;
			}
		} else if (nextWalkingDirecitonTileSearch == NextWalkingDirecitonTileSearch.LINEAR_FOR_LOOP) {
			// Do not let Npc move onto a tile that has another npc on it.
			for (int i = 0; i < NpcHandler.NPC_INDEX_OPEN_MAXIMUM; i++) {
				Npc npc = NpcHandler.npcs[i];
				if (npc != null && !npc.isDead()) {

					if (nextXtile == npc.getX() && nextYtile == npc.getY()) {
						return -1;
					}
				}
			}
		}
		Region currentRegion = getRegionOrNull();

		Region nextRegion = Region.getRegion(nextXtile, nextYtile);

		if (nextRegion != null && currentRegion != nextRegion) {
			onRegionChange();
		}
		dir >>= 1;
		setX(nextXtile);
		setY(nextYtile);
		return dir;
	}

	private final static int[][] barrowsClipping =
			{

					{3554, 9714},
					{3555, 9714},
					{3556, 9714},
					{3554, 9715},
					{3555, 9715},
					{3556, 9715},

					{3538, 9703},
					{3539, 9703},
					{3540, 9703},
					{3538, 9704},
					{3539, 9704},
					{3540, 9704},

					{3550, 9682},
					{3550, 9683},
					{3550, 9684},
					{3551, 9682},
					{3551, 9683},
					{3551, 9684},

					{3569, 9685},
					{3569, 9686},
					{3569, 9687},
					{3570, 9685},
					{3570, 9686},
					{3570, 9687},

					{3573, 9705},
					{3573, 9706},
					{3573, 9707},
					{3574, 9705},
					{3574, 9706},
					{3574, 9707},
			};

	private boolean barrowsNpcClipping(int x, int y) {

		for (int index = 0; index < Barrows.COFFIN_AND_BROTHERS.length; index++) {
			if (npcType == Barrows.COFFIN_AND_BROTHERS[index][1]) {
				for (int a = 0; a < barrowsClipping.length; a++) {
					if (x == barrowsClipping[a][0] && y == barrowsClipping[a][1]) {
						return true;
					}
				}
				break;
			}
		}
		return false;
	}

	public void getNextNPCMovement(int i) {
		direction = -1;
		if (!NpcHandler.npcs[i].isFrozen() && getMovementState() == MovementState.WALKABLE) {
			direction = getNextNpcWalkingDirection();

			if (direction != -1) {
				onPositionChange();
			}
		}
	}

	public void appendHitUpdate(Stream str, Player c) {
		str.writeWordA(hitDiff);
		str.writeByteS(hitMask);
		str.writeByte(hitIcon);
		str.writeWordA(getCurrentHitPoints());
		str.writeWordA(maximumHitPoints);
	}

	public int hitDiff2 = 0;

	public boolean hitUpdateRequired2 = false;

	public int hitIcon, hitMask, hitIcon2, hitMask2;

	public int transformTimer;

	public int transformIntoId;

	public void appendHitUpdate2(Stream str, Player c) {
		str.writeWordA(hitDiff2);
		str.writeByteC(hitMask2);
		str.writeByte(hitIcon2);
		str.writeWordA(getCurrentHitPoints());
		str.writeWordA(maximumHitPoints);
	}

	public void curePoison() {
		poisonDamage = 0;
		poisonHitsplatsLeft = 0;
		poisonEvent = false;

		if (poisonCycleEvent != null && poisonCycleEvent.isRunning()) {
			poisonCycleEvent.stop();
		}
	}

	private CycleEventContainer<Entity> poisonCycleEvent;

	public String zombieOwner = "";

	public int poisonDamage;

	public int poisonHitsplatsLeft;

	public boolean poisonEvent;

	public int poisonTicksUntillDamage;

	public int venomDamage = 6;

	public int venomHitsplatsLeft;

	public boolean venomEvent;

	public int venomTicksUntillDamage;

	/**
	 * The amount of times a venom damage cycle has taken place
	 */
	public long venomHits;

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getPosition() {
		return x & y;
	}

	/**
	 * Npcs with size of 2 or more have a different x and y visually and another x and y on the server.
	 * This visual x and y is used for dropping items on the correct spot, following the npc etc..
	 */
	public int getVisualX() {
		return Follow.isBigNpc(this.npcType) + this.getX();
	}

	/**
	 * Npcs with size of 2 or more have a different x and y visually and another x and y on the server.
	 * This visual x and y is used for dropping items on the correct spot, following the npc etc..
	 */
	public int getVisualY() {
		return Follow.isBigNpc(this.npcType) + this.getY();
	}

	public boolean inBarbDef() {
		return (coordsCheck(3147, 3193, 9737, 9778));
	}

	public boolean coordsCheck(int X1, int X2, int Y1, int Y2) {
		return getX() >= X1 && getX() <= X2 && getY() >= Y1 && getY() <= Y2;
	}

	public boolean inWild() {
		if (getX() > 2941 && getX() < 3392 && getY() > 3518 && getY() < 3966 || getX() > 2941 && getX() < 3392 && getY() > 9918 && getY() < 10366) {
			return true;
		}
		return false;
	}

	public int getSpawnPositionX() {
		return spawnPositionX;
	}

	public void setSpawnPositionX(int makeX) {
		this.spawnPositionX = makeX;
	}

	public int getKillerId() {
		return killerId;
	}

	public void setKillerId(int killerId) {
		if (killerId == 0) {
			this.timeAttackedAPlayer = 0;
		}
		this.killerId = killerId;
	}

	public int distanceTo(int otherX, int otherY) {
		int minDistance = (int) Math.hypot(otherX - x, otherY - y);
		for (int x1 = x; x1 < x + 2 - 1; x1++) {
			for (int y1 = y; y1 < y + 2 - 1; y1++) {
				int distance = (int) Math.hypot(otherX - x1, otherY - y1);
				if (distance < minDistance) {
					minDistance = distance;
				}
			}
		}
		return minDistance;
	}

	public int getSpawnPositionY() {
		return spawnPositionY;
	}

	public void setSpawnPositionY(int spawnPositionY) {
		this.spawnPositionY = spawnPositionY;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public boolean isWalkingHome() {
		return walkingHome;
	}

	public void setWalkingHome(boolean walkingHome) {
		this.walkingHome = walkingHome;
	}

	public int getMoveX() {
		return moveX;
	}

	public void setMoveX(int moveX) {
		this.moveX = moveX;
	}

	public int getMoveY() {
		return moveY;
	}

	public void setMoveY(int moveY) {
		this.moveY = moveY;
	}

	public boolean isMoved() {
		return moved;
	}

	public void setMoved(boolean moved) {
		this.moved = moved;
	}

	public int getSpawnedBy() {
		return spawnedBy;
	}

	public void setSpawnedBy(int spawnedBy) {
		this.spawnedBy = spawnedBy;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int heightLevel) {
		this.height = heightLevel;
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

	public int getKilledBy() {
		return killedBy;
	}

	public void setKilledBy(int killedBy) {
		this.killedBy = killedBy;
	}

	public boolean isDead() {
		return isDead;
	}

	public void setDead(boolean isDead) {
		this.isDead = isDead;
	}

	public int getCurrentHitPoints() {
		return currentHitPoints;
	}

	public void setCurrentHitPoints(int currentHitPoints) {
		this.currentHitPoints = currentHitPoints;
	}

	/**
	 * Store the total damage of a player to the npc. The index is the player's index in the player array.
	 */
	public int[] playerTotalDamage = new int[ServerConstants.MAXIMUM_PLAYERS];

	/**
	 * Store the player's name who did damage to this npc, The index is the player's index in the player array.
	 */
	public String[] playerNameTotalDamage = new String[ServerConstants.MAXIMUM_PLAYERS];

	/**
	 * True if the npc has a head icon for the player to target.
	 */
	public boolean headIcon;

	/**
	 * 1 or more if the npc is behind a wall. 1 will mean the player can interact with this npc within a distance of 1 and ignoring any walls between them.
	 */
	public int behindWallDistance;

	public String Graardor() {
		int quote = Misc.random(9);
		switch (quote) {
			case 0:
				return "Death to our enemies!";
			case 1:
				return "Brargh!";
			case 2:
				return "Break their bones!";
			case 3:
				return "For the glory of the Big High War God!";
			case 4:
				return "Split their skulls!";
			case 5:
				return "We feast on the bones of our enemies tonight!";
			case 6:
				return "CHAAARGE!";
			case 7:
				return "Crush them underfoot!";
			case 8:
				return "All glory to Bandos!";
			case 9:
				return "GRAAAAAAAAAR!";
		}
		return "";
	}

	public String Tsutsaroth() {
		int quote = Misc.random(8);
		switch (quote) {
			case 0:
				return "Attack them!";
			case 1:
				return "Forward!";
			case 2:
				return "Death to Saradomin's dogs!";
			case 3:
				return "Kill them you cowards!";
			case 4:
				return "The Dark One will have their souls!";
			case 5:
				return "Zamorak curse them!";
			case 6:
				return "Rend them limb from limb!";
			case 7:
				return "No retreat!";
			case 8:
				return "Slay them all!!";
		}
		return "";
	}

	public String Zilyana() {
		int quote = Misc.random(9);
		switch (quote) {
			case 0:
				return "Death to the enemies of the light!";
			case 1:
				return "Slay the evil ones!";
			case 2:
				return "Saradomin lend me strength!";
			case 3:
				return "By the power of Saradomin!";
			case 4:
				return "May Saradomin be my sword!";
			case 5:
				return "Good will always triumph!";
			case 6:
				return "Forward! Our allies are with us!";
			case 7:
				return "Saradomin is with us!";
			case 8:
				return "In the name of Saradomin!";
			case 9:
				return "Attack! Find the Godsword!";
		}
		return "";
	}

	public String Kree() {
		int quote = Misc.random(6);
		switch (quote) {
			case 0:
				return "Attack with your talons!";
			case 1:
				return "Face the wratch of Armadyl";
			case 2:
				return "SCCCRREEEEEEEEEECHHHHH";
			case 3:
				return "KA KAW KA KAW";
			case 4:
				return "Fight my minions!";
			case 5:
				return "Good will always triumph!";
			case 6:
				return "Attack! Find the Godsword!";
		}
		return "";
	}

	@Override
	public String toString() {
		return String.format("Npc{ name=%s, index=%s }", name, npcIndex);
	}

	public int getTransformOrId() {
		return transformId == -1 ? npcType : transformId;
	}

	public int getNpcPetOwnerId() {
		return npcPetOwnerId;
	}

	public void setNpcPetOwnerId(int summonedBy) {
		this.npcPetOwnerId = summonedBy;
	}

	public boolean neverRandomWalks() {
		return neverRandomWalks;
	}

	public void setNeverRandomWalks(boolean neverRandomWalks) {
		this.neverRandomWalks = neverRandomWalks;
	}

	public EntityType getFollowingType() {
		return followingType;
	}

	public void setFollowingType(EntityType followingType) {
		this.followingType = followingType;
	}

	public int getFollowIndex() {
		return followIndex;
	}

	public void setFollowIndex(int followIndex) {
		this.followIndex = followIndex;
	}

	public boolean isWalkingHomeDisabled() {
		return walkingHomeDisabled;
	}

	public void setWalkingHomeDisabled(boolean walkingHomeDisabled) {
		this.walkingHomeDisabled = walkingHomeDisabled;
	}

	public boolean isClippingIgnored() {
		return clippingIgnored;
	}

	public void setClippingIgnored(boolean clippingIgnored) {
		this.clippingIgnored = clippingIgnored;
	}

	/**
	 * Sets the familiar
	 *
	 * @return the familiar
	 */
	public boolean isFamiliar() {
		return familiar;
	}

	/**
	 * Sets the familiar
	 *
	 * @param familiar the familiar
	 */
	public void setFamiliar(boolean familiar) {
		this.familiar = familiar;
	}

	public boolean isAttackable() {
		return attackable;
	}

	public void setAttackable(boolean attackable) {
		this.attackable = attackable;
	}

	public NpcDefinition getDefinition() {
		return NpcDefinition.DEFINITIONS[npcType];
	}
}
