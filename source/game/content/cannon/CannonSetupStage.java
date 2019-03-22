package game.content.cannon;

/**
 * Created by Jason MK on 2018-07-20 at 12:27 PM
 */
public enum CannonSetupStage {
    UNBEGUN(0, -1) {
        @Override
        public CannonSetupStage next() {
            return CannonSetupStage.BASE;
        }
    },

    BASE(0,7) {
        @Override
        public CannonSetupStage next() {
            return CannonSetupStage.STAND;
        }
    },

    STAND(0,8) {
        @Override
        public CannonSetupStage next() {
            return CannonSetupStage.BARRELS;
        }
    },

    BARRELS(0,9) {
        @Override
        public CannonSetupStage next() {
            return CannonSetupStage.FURNACE;
        }
    },

    FURNACE(0,9) {
        @Override
        public CannonSetupStage next() {
            return CannonSetupStage.FINISHED;
        }
    },

    FINISHED(0, 6) {
        @Override
        public CannonSetupStage next() {
            throw new IllegalStateException("No next state after being finished.");
        }
    }
    ;

    private final int itemIdRequired;

    private final int objectId;

    CannonSetupStage(int itemIdRequired, int objectId) {
        this.itemIdRequired = itemIdRequired;
        this.objectId = objectId;
    }

    public int getItemIdRequired() {
        return itemIdRequired;
    }

    public int getObjectId() {
        return objectId;
    }

    public abstract CannonSetupStage next();
}
