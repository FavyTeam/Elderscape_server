package game.npc.impl.tekton;

/**
 * Created by Jason MK on 2018-08-10 at 11:42 AM
 */
public enum TektonTransformation {
    HAMMERING(7540, 7473, 7473),

    WALKING(7541, 7476, 7485),

    DEFENSIVE(7542, 7480, 7490),
    ;

    private final int type;

    private final int idleAnimation;

    private final int enragedIdleAnimation;

    TektonTransformation(int type, int idleAnimation, int enragedIdleAnimation) {
        this.type = type;
        this.idleAnimation = idleAnimation;
        this.enragedIdleAnimation = enragedIdleAnimation;
    }

    public int getType() {
        return type;
    }

    public int getIdleAnimation() {
        return idleAnimation;
    }

    public int getEnragedIdleAnimation() {
        return enragedIdleAnimation;
    }
}
