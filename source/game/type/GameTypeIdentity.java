package game.type;

import core.GameType;
import game.npc.CustomNpcComponent;
import game.npc.CustomNpcMap;

import java.lang.annotation.*;

/**
 * Created by Jason MacKeigan on 2018-06-21 at 12:23 PM
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface GameTypeIdentity {

    GameType type();

    int[] identity();

}
