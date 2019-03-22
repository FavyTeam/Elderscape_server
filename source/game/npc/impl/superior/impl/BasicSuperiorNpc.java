package game.npc.impl.superior.impl;

import core.GameType;
import game.content.skilling.Slayer;
import game.npc.CustomNpcComponent;
import game.npc.Npc;
import game.npc.impl.superior.SuperiorNpc;
import game.type.GameTypeIdentity;

import java.util.stream.Stream;

/**
 * Created by Jason MK on 2018-07-17 at 2:16 PM
 */
@CustomNpcComponent(identities = {
        @GameTypeIdentity(type = GameType.OSRS, identity = 7389),
        @GameTypeIdentity(type = GameType.OSRS, identity = 7392),
        @GameTypeIdentity(type = GameType.OSRS, identity = 7399),
        @GameTypeIdentity(type = GameType.OSRS, identity = 7390),
        @GameTypeIdentity(type = GameType.OSRS, identity = 7395),
        @GameTypeIdentity(type = GameType.OSRS, identity = 7393),
        @GameTypeIdentity(type = GameType.OSRS, identity = 7405),
        @GameTypeIdentity(type = GameType.OSRS, identity = 7394),
        @GameTypeIdentity(type = GameType.OSRS, identity = 7397),
        @GameTypeIdentity(type = GameType.OSRS, identity = 7388),
        @GameTypeIdentity(type = GameType.OSRS, identity = 7404),
        @GameTypeIdentity(type = GameType.OSRS, identity = 7396)
})
public class BasicSuperiorNpc extends SuperiorNpc {

    private Slayer.Task cachedTask;

    public BasicSuperiorNpc(int npcId, int npcType) {
        super(npcId, npcType);
    }

    @Override
    public int parent() {
        if (cachedTask == null) {
            cachedTask = Stream.of(Slayer.Task.values()).filter(t -> t.getSuperiorNpc() == npcType).findAny().orElse(null);

            if (cachedTask == null) {
                return 0;
            }
        }
        return cachedTask.getNpcId()[0];
    }

    @Override
    public Npc copy(int index) {
        BasicSuperiorNpc superiorNpc = new BasicSuperiorNpc(index, npcType);

        superiorNpc.cachedTask = cachedTask;

        return superiorNpc;
    }
}
