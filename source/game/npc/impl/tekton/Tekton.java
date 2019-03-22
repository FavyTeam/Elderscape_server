package game.npc.impl.tekton;

import com.google.common.collect.ImmutableList;
import core.GameType;
import core.ServerConfiguration;
import game.entity.Entity;
import game.entity.attributes.AttributeKey;
import game.entity.attributes.TransientAttributeKey;
import game.entity.combat_strategy.EntityCombatStrategy;
import game.npc.CustomNpcComponent;
import game.npc.Npc;
import game.npc.NpcWalkToEvent;
import game.player.Player;
import game.player.event.CycleEventContainer;
import game.position.Position;
import game.position.PositionUtils;
import game.position.distance.DistanceAlgorithms;
import game.type.GameTypeIdentity;
import utility.Misc;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * Created by Jason MK on 2018-07-27 at 3:22 PM
 */
@CustomNpcComponent(identities = @GameTypeIdentity(type = GameType.OSRS, identity = 7540))
public class Tekton extends Npc {

    public static final List<Position> FIRE_STARTING_POSITIONS = ImmutableList.copyOf(Arrays.asList(
            new Position(3340, 5319),
            new Position(3339, 5319),
            new Position(3338, 5319),
            new Position(3336, 5320),
            new Position(3335, 5320),
            new Position(3332, 5325),
            new Position(3334, 5334),
            new Position(3340, 5339),
            new Position(3342, 5339),
            new Position(3345, 5337)
    ));

    public static final int HAMMINER_ANIMATION = 7473;

    public static final int STOP_HAMMERING_ANIMATION = 7474;

    public static final int TRANSFORM_TO_ORANGE_ANIMATION = 7475;

    public static final int WALKING_IDLE_ANIMATION = 7476;

    public static final int WALKING_ANIMATION = 7477;

    public static final int TRANSFORM_TO_ORANGE_ANIMATION_2 = 7478;

    public static final int TRANSFORM_FROM_ORANGE_ANIMATION = 7479;

    public static final int UN_ENRAGED_IDLE_ANIMATION = 7480;

    public static final int POKE_ANIMATION = 7482;

    public static final int SLASH_ANIMATION = 7483;

    public static final int SMASH_ANIMATION = 7484;

    public static final int ENRAGED_WALKING_ANIMATION = 7485;

    public static final int ENRAGED_WALKING_ANIMATION_2 = 7486;

    public static final int ENRAGED_STOP_HAMMERING_ANIMATION = 7487;

    public static final int ENRAGED_TO_ATTACK_ANIMATION = 7488;

    public static final int UNKNOWN_1 = 7489;

    public static final int ENRAGED_ATTACKING_IDLE_ANIMATION = 7490;

    public static final int ENRAGED_SMASH_ANIMATION = 7492;

    public static final int ENRAGED_POKE_ANIMATION = 7493;

    public static final int ENRAGED_SLASH_ANIMATION = 7494;

    public static final int UN_ENRAGED_DYING_ANIMATION = 7495;

    public static final AttributeKey<TektonTransformation> TRANSFORMATION = new TransientAttributeKey<>(TektonTransformation.HAMMERING);

    public static final AttributeKey<TektonState> STATE = new TransientAttributeKey<>(TektonState.HAMMERING);

    public static final AttributeKey<Integer> CONSECUTIVE_ATTACKS = new TransientAttributeKey<>(0);

    public static final AttributeKey<Boolean> TRIGGERED = new TransientAttributeKey<>(false);

    public static final AttributeKey<Boolean> ENRAGED = new TransientAttributeKey<>(false);

    public static final AttributeKey<Long> TIME_OF_ENRAGE = new TransientAttributeKey<>(0L);

    public static final long ENRAGED_COOL_DOWN = TimeUnit.SECONDS.toMillis(45);

    public static final long ENRAGED_DURATION = TimeUnit.SECONDS.toMillis(15);

    private final EntityCombatStrategy strategy = new TektonCombatStrategy();

    private NpcWalkToEvent walkingEvent;

    private TektonFireAttackEvent fireAttackEvent;

    public Tekton(int npcId, int npcType) {
        super(npcId, npcType);

        setNeverRandomWalks(true);
        setWalkingHomeDisabled(true);

        getAttributes().put(TRANSFORMATION);
        getAttributes().put(STATE);
    }

    @Override
    public void onSequence() {
        super.onSequence();

        if (isDead() || getCurrentHitPoints() <= 0) {
            return;
        }

        if (getAttributes().isNot(TRIGGERED)) {
            if (!getLocalPlayers().isEmpty()) {
                trigger();
            }
        }

        TektonState state = getAttributes().getOrDefault(STATE);

        if (ServerConfiguration.DEBUG_MODE) {
            System.out.println("State: " + state + ", Enraged: " + getAttributes().is(ENRAGED));
        }
        if (state == TektonState.HAMMERING) {
            if (getTransformOrId() != TektonTransformation.HAMMERING.getType()) {
                transform(TektonTransformation.HAMMERING);
            }
            turnNpc(3330, 5332);

            if (getKillerId() > 0) {
                setKillerId(0);
                resetFollowing();
                resetFace();
            }

            if (getAttributes().is(TRIGGERED)) {
                if (fireAttackEvent != null) {
                    if (fireAttackEvent.isFailed()) {
                        getEventHandler().stopIfEventEquals(fireAttackEvent);
                        fireAttackEvent = null;
                        trigger();
                    }
                } else {
                    getEventHandler().addEvent(this, fireAttackEvent = new TektonFireAttackEvent(3 + Misc.random(2)), 4);
                }
            }
        } else if (state == TektonState.WALKING_TO_TARGET) {
            if (walkingEvent == null) {
                Player local = getLocalPlayers().stream().findAny().orElse(null);

                if (local == null) {
                    getAttributes().put(STATE, TektonState.WALKING_TO_ANVIL);
                    return;
                }
                if (getCurrentHitPoints() < maximumHitPoints && enrageIsAvailable()) {
                    getAttributes().put(ENRAGED, true);
                    retransform();
                    getAttributes().put(TIME_OF_ENRAGE, System.currentTimeMillis());
                }
                walkingEvent = new NpcWalkToEvent(20, new Position(local), 1);
                getEventHandler().addEvent(this, walkingEvent, 1);
                setClippingIgnored(true);
                setKillerId(local.getPlayerId());
                setFacingEntityDisabled(true);
                resetFace();
                resetFollowing();
            } else {
                if (walkingEvent.isDestinationReached()) {
                    getEventHandler().stopIfEventEquals(walkingEvent);
                    walkingEvent = null;
                    lastDamageTaken = System.currentTimeMillis();
                    getAttributes().put(STATE, TektonState.ATTACKING);
                    getAttributes().put(CONSECUTIVE_ATTACKS, 0);
                    transform(TektonTransformation.DEFENSIVE);
                } else if (walkingEvent.isFailed()) {
                    reset();
                }
            }
        } else if (state == TektonState.WALKING_TO_ANVIL) {
            if (walkingEvent == null) {
                walkingEvent = new NpcWalkToEvent(20, new Position(3334, 5328, getHeight()), 0);
                getEventHandler().addEvent(this, walkingEvent, 1);
                setClippingIgnored(true);
                setKillerId(0);
                setFacingEntityDisabled(true);
                resetFace();
                resetFollowing();
            } else {
                if (walkingEvent.isDestinationReached()) {
                    getEventHandler().stopIfEventEquals(walkingEvent);
                    walkingEvent = null;
                    getAttributes().put(ENRAGED, false);
                    getAttributes().put(STATE, TektonState.HAMMERING);
                    transform(TektonTransformation.HAMMERING);
                    setFacingEntityDisabled(false);
                    setClippingIgnored(false);
                } else if (walkingEvent.isFailed()) {
                    getEventHandler().stopIfEventEquals(walkingEvent);
                    walkingEvent = null;
                    getAttributes().put(STATE, TektonState.HAMMERING);
                    transform(TektonTransformation.HAMMERING);
                    move(new Position(3336, 5328, getHeight()));
                    setFacingEntityDisabled(false);
                    setClippingIgnored(false);
                }
            }
        } else if (state == TektonState.ATTACKING) {
            if (getAttributes().getOrDefault(Tekton.CONSECUTIVE_ATTACKS) >= 16) {
                getAttributes().put(Tekton.CONSECUTIVE_ATTACKS, 0);
                getAttributes().put(Tekton.STATE, TektonState.WALKING_TO_ANVIL);
                transform(TektonTransformation.WALKING);
                return;
            }
            if (getLocalPlayers().stream().noneMatch(local ->
                    PositionUtils.withinDistance(this, new Position(local), 1, DistanceAlgorithms.EUCLIDEAN))) {
                getAttributes().put(Tekton.STATE, TektonState.WALKING_TO_ANVIL);
                getAttributes().put(Tekton.CONSECUTIVE_ATTACKS, 0);
                transform(TektonTransformation.WALKING);
                resetFollowing();
                resetFace();
                setKillerId(0);
                return;
            }
        }

        if (state == TektonState.ATTACKING && System.currentTimeMillis() - lastDamageTaken > 120_000
                || getAttributes().is(TRIGGERED) && getLocalPlayers().isEmpty()) {
            reset();
            getAttributes().put(TRIGGERED, false);
        }

        if (getAttributes().is(ENRAGED) && System.currentTimeMillis() - getAttributes().getOrDefault(TIME_OF_ENRAGE)
                >= TimeUnit.SECONDS.toMillis(ENRAGED_DURATION)) {
            getAttributes().put(ENRAGED, false);
            retransform();
        }
    }

    private boolean enrageIsAvailable() {
        return getAttributes().isNot(ENRAGED) && System.currentTimeMillis() -
                getAttributes().getOrDefault(TIME_OF_ENRAGE) >= ENRAGED_COOL_DOWN;
    }

    private void reset() {
        getEventHandler().stopIfEventEquals(walkingEvent);
        walkingEvent = null;
        getAttributes().put(STATE, TektonState.WALKING_TO_ANVIL);
        setFacingEntityDisabled(true);
        transform(TektonTransformation.WALKING);
        resetFollowing();
        resetFace();
        setKillerId(0);
    }

    private void trigger() {
        if (getAttributes().getOrDefault(STATE) == TektonState.HAMMERING && fireAttackEvent == null) {
            getAttributes().put(STATE, TektonState.WALKING_TO_TARGET);
            transform(TektonTransformation.WALKING);
            getAttributes().put(TRIGGERED, true);
        }
    }

    @Override
    public void onAddToLocalList(Player player) {
        super.onAddToLocalList(player);

        trigger();
    }

    private void retransform() {
        transform(getAttributes().getOrDefault(TRANSFORMATION));
    }

    private void transform(TektonTransformation transformation) {
        transform(transformation.getType());
        getAttributes().put(TRANSFORMATION, transformation);

        if (transformation == TektonTransformation.HAMMERING && getAttributes().is(ENRAGED)) {
            getAttributes().put(ENRAGED, false);
        }
        int animation = getAttributes().is(ENRAGED) ? transformation.getEnragedIdleAnimation() : transformation.getIdleAnimation();

        setIdleAnimation(animation);
        requestAnimation(animation);
    }

    @Override
    public final void transform(int id) {
        TektonTransformation transformationForId = Stream.of(TektonTransformation.values()).filter(t -> t.getType() == id).findAny().orElse(null);

        if (transformationForId == null) {
            return;
        }
        super.transform(id);
    }

    @Override
    public Npc copy(int index) {
        return new Tekton(index, npcType);
    }

    @Override
    public EntityCombatStrategy getCombatStrategyOrNull() {
        return strategy;
    }
}
