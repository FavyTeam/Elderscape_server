package game.npc.impl.superior;

import game.content.skilling.Skill;
import game.content.skilling.Skilling;
import game.content.skilling.Slayer;
import game.entity.attributes.AttributeKey;
import game.entity.attributes.TransientAttributeKey;
import game.entity.combat_strategy.EntityCombatStrategy;
import game.item.GameItem;
import game.item.ItemAssistant;
import game.npc.Npc;
import game.npc.NpcDrops;
import game.player.Player;
import game.player.PlayerHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jason MK on 2018-07-06 at 3:00 PM
 */
public abstract class SuperiorNpc extends Npc {

    public static AttributeKey<String> SPAWNED_FOR = new TransientAttributeKey<>(null);

    public static AttributeKey<Long> LAST_INTERACTION = new TransientAttributeKey<>(0L);

    private final SuperiorNpcCombatStrategy strategy = new SuperiorNpcCombatStrategy();

    public SuperiorNpc(int npcId, int npcType) {
        super(npcId, npcType);
        getAttributes().put(LAST_INTERACTION, System.currentTimeMillis());
    }

    public abstract int parent();

    @Override
    public void onSequence() {
        super.onSequence();

        if (!isDead() && System.currentTimeMillis() - getAttributes().getOrDefault(LAST_INTERACTION) >= TimeUnit.MINUTES.toMillis(2)) {
            setItemsDroppable(false);
            killIfAlive();

            Player player = PlayerHandler.getPlayerForName(getAttributes().getOrDefault(SPAWNED_FOR, ""));

            if (player == null) {
                return;
            }
            player.getPA().sendMessage("The superior monster has vanished...");
        }
    }

    @Override
    public void afterDeath() {
        super.afterDeath();

        if (!isItemsDroppable()) {
            return;
        }
        Player player = PlayerHandler.players[getKilledBy()];

        if (player == null || !player.getPlayerName().equalsIgnoreCase(getAttributes().getOrDefault(SPAWNED_FOR, ""))) {
            return;
        }
        Slayer.Task task = Slayer.getTask(parent());

        if (task == null) {
            return;
        }
        Skilling.addSkillExperience(player, task.getSuperiorExperience(), Skill.SLAYER.getId(), false);

        if (parent() != 0 && NpcDrops.exists(parent())) {
            for (int i = 0; i < 3; i++) {
                NpcDrops.giveDropTableDrop(player, false, parent(), getDropPosition());
            }
        }

        if (meetsRequirement(task.getLevelReq())) {
            List<GameItem> drop = new ArrayList<>();

            if (ThreadLocalRandom.current().nextInt(0, 8) == 0) {
                drop.add(new GameItem(ThreadLocalRandom.current().nextBoolean() ? 21270 : 20724));
            }

            if (ThreadLocalRandom.current().nextInt(0, 8) <= 2) {
                drop.add(new GameItem(ThreadLocalRandom.current().nextBoolean() ? 20730 : 20736));
            }

            drop.forEach(item -> {
                ItemAssistant.addItemToInventoryOrDrop(player, item.getId(), item.getAmount());
                player.getPA().sendMessageF("The superior monster dropped a %s.", item.getDefinition() == null ? "unarmed" :  item.getDefinition().name);
            });
        }
    }

    private boolean meetsRequirement(double slayerRequirement) {
        return ThreadLocalRandom.current().nextInt(0, (int) chance(slayerRequirement)) == 0;
    }

    private double chance(double slayerRequirement) {
        return Math.floor(200 - Math.pow(slayerRequirement + 55, 2) / 125D);
    }

    @Override
    public EntityCombatStrategy getCombatStrategyOrNull() {
        return strategy;
    }

    public abstract Npc copy(int index);

}
