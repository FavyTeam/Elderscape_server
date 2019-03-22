package game.player.pet;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Created by Jason MK on 2018-07-23 at 3:34 PM
 */
public class PlayerPetState {

    private static final String DELIMITER = "\t";

    private static final String SEPARATOR = "=";

    private static final Splitter SPLITTER = Splitter.on(",").omitEmptyStrings().trimResults(CharMatcher.anyOf("[] "));

    private final int[] equipment;

    private final int[] equipmentAmount;

    private final int[] appearance;

    private final boolean summoned;

    private final boolean mimicking;

    public PlayerPetState(String encoded) {
        PlayerPetState decoded = decode(encoded);

        this.equipment = decoded.equipment;
        this.equipmentAmount = decoded.equipmentAmount;
        this.appearance = decoded.appearance;
        this.summoned = decoded.summoned;
        this.mimicking = decoded.mimicking;
    }

    private PlayerPetState(Builder builder) {
        this.equipment = builder.equipment;
        this.equipmentAmount = builder.equipmentAmount;
        this.appearance = builder.appearance;
        this.summoned = builder.summoned;
        this.mimicking = builder.mimicking;
    }

    public static class Builder {

        private int[] equipment = new int[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };

        private int[] equipmentAmount = new int[] { 1, 1, 1, 0, 1, 1, 0, 1, 0, 1, 1, 0, 1, 0 };

        private int[] appearance = new int[] { 0, 1, 18, 26, 33, 36, 42, 10, 0, 0, 0, 0, 0 };

        private boolean summoned;

        private boolean mimicking;

        public Builder setEquipment(int[] equipment) {
            this.equipment = equipment;

            return this;
        }

        public Builder setEquipmentAmount(int[] equipmentAmount) {
            this.equipmentAmount = equipmentAmount;

            return this;
        }

        public Builder setAppearance(int[] appearance) {
            this.appearance = appearance;

            return this;
        }

        public Builder setSummoned(boolean summoned) {
            this.summoned = summoned;

            return this;
        }

        public Builder setMimicking(boolean mimicking) {
            this.mimicking = mimicking;

            return this;
        }

        public PlayerPetState build() {
            return new PlayerPetState(this);
        }

    }

    public String encode() {
        StringBuilder builder = new StringBuilder();

        for (PlayerPetStateProperty property : PlayerPetStatePropertySet.values()) {
            builder.append(property.key()).append(SEPARATOR);
            property.encode(builder, this);
            if (property == PlayerPetStatePropertySet.values()[PlayerPetStatePropertySet.values().length - 1]) {
                continue;
            }
            builder.append(DELIMITER);
        }

        return builder.toString();
    }

    enum PlayerPetStatePropertySet implements PlayerPetStateProperty {
        EQUIPMENT("equipment") {
            @Override
            public void encode(StringBuilder builder, PlayerPetState state) {
                builder.append(Arrays.toString(state.equipment));
            }

            @Override
            public void decode(String value, Builder builder) {
                builder.setEquipment(SPLITTER.splitToList(value).stream().mapToInt(Integer::parseInt).toArray());
            }
        },
        EQUIPMENT_AMOUNT("equipment-amount") {
            @Override
            public void encode(StringBuilder builder, PlayerPetState state) {
                builder.append(Arrays.toString(state.equipmentAmount));
            }

            @Override
            public void decode(String value, Builder builder) {
                builder.setEquipmentAmount(SPLITTER.splitToList(value).stream().mapToInt(Integer::parseInt).toArray());
            }
        },
        APPEARANCE("appearance") {
            @Override
            public void encode(StringBuilder builder, PlayerPetState state) {
                builder.append(Arrays.toString(state.appearance));
            }

            @Override
            public void decode(String value, Builder builder) {
                builder.setAppearance(SPLITTER.splitToList(value).stream().mapToInt(Integer::parseInt).toArray());
            }
        },
        SUMMONED("summoned") {
            @Override
            public void encode(StringBuilder builder, PlayerPetState state) {
                builder.append(Boolean.toString(state.summoned));
            }

            @Override
            public void decode(String value, Builder builder) {
                builder.setSummoned(Boolean.parseBoolean(value));
            }
        },
        MIMICKING("mimicking") {
            @Override
            public void encode(StringBuilder builder, PlayerPetState state) {
                builder.append(Boolean.toString(state.mimicking));
            }

            @Override
            public void decode(String value, Builder builder) {
                builder.setMimicking(Boolean.parseBoolean(value));
            }
        };


        private final String key;

        PlayerPetStatePropertySet(String key) {
            this.key = key;
        }

        @Override
        public String key() {
            return key;
        }
    }

    interface PlayerPetStateProperty {

        String key();

        void encode(StringBuilder builder, PlayerPetState state);

        void decode(String value, Builder builder);

    }

    private static PlayerPetState decode(String encoded) {
        String[] entries = encoded.split(DELIMITER);

        Builder builder = new Builder();

        for (String entry : entries) {
            String key = entry.split("=")[0];

            String value = entry.split("=")[1];

            if (key == null || value == null) {
                continue;
            }
            PlayerPetStateProperty property = Stream.of(PlayerPetStatePropertySet.values()).filter(p -> p.key.equals(key))
                    .findAny().orElse(null);

            if (property == null) {
                continue;
            }

            property.decode(value, builder);
        }
        return new PlayerPetState(builder);
    }

    public int[] getEquipment() {
        return equipment;
    }

    public int[] getEquipmentAmount() {
        return equipmentAmount;
    }

    public int[] getAppearance() {
        return appearance;
    }

    public boolean isSummoned() {
        return summoned;
    }

    public boolean isMimicking() {
        return mimicking;
    }
}
