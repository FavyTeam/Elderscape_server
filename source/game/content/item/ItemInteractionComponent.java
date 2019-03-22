package game.content.item;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import game.type.GameTypeIdentity;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ItemInteractionComponent {

	GameTypeIdentity[] identities();
}
