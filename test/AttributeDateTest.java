import game.entity.attributes.AttributeKey;
import game.entity.attributes.AttributeMap;
import game.entity.attributes.TransientAttributeKey;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jason MK on 2018-07-05 at 12:20 PM
 */
public class AttributeDateTest {

    private static final AttributeKey<Integer> AMOUNT_KEY = new TransientAttributeKey<>(0);

    private static final AttributeKey<ZonedDateTime> TIME_KEY = new TransientAttributeKey<>(null);

    private static final boolean ARTIFICIAL_TEST = true;

    private static final int ARTIFICIAL_DAY_OFFSET = 2;

    private static final int ARTIFICIAL_AMOUNT = 50;

    private static final AttributeMap attributes = new AttributeMap();

    public static void main(String[] args) {
        // this is the current date and time including the zone of the computer clock
        ZonedDateTime now = ZonedDateTime.now();

        // if this is true, we want to set the value for the time key to the current date and time minus
        // that of the artifical day offset. This would test for example if the value was 2 would test
        // if a player last logged in 2 days ago.
        if (ARTIFICIAL_TEST) {
            attributes.put(TIME_KEY, now.minusDays(ARTIFICIAL_DAY_OFFSET));
        }

        // this is the current value for the time
        ZonedDateTime valueForTime = attributes.getOrDefault(TIME_KEY);

        // this check if the value for time is null, or if the duration between now and the
        // time is greater than or equal to 24 hours.
        if (valueForTime == null || Duration.between(valueForTime, now).toHours() >= 24) {
            attributes.put(AMOUNT_KEY, 0);
        }

        // this is the current amount of 'kills' we have gotten. If we are doing an artifical test
        // then the artificial amount will be used.
        int amount = attributes.getOrDefault(AMOUNT_KEY, ARTIFICIAL_TEST ? ARTIFICIAL_AMOUNT : 0);

        System.out.println(String.format("Amount = %s", amount));
    }
}
