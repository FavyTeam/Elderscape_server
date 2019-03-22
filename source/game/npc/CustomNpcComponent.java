package game.npc;

import java.lang.annotation.*;

import game.type.GameTypeIdentity;

/**
 * Created by Jason MacKeigan on 2018-06-06 at 2:20 PM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CustomNpcComponent {

	GameTypeIdentity[] identities();

}
