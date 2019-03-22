package network.sql;

/**
 * Created by Jason MK on 2018-08-30 at 10:11 AM
 */
public class SQLNetworkUtils {

    private static String trimCreateStatement(String create) {
        int indexOfAutoIncrement = create.indexOf("AUTO_INCREMENT=");

        if (indexOfAutoIncrement != -1) {
            int indexOfNextSpace = create.indexOf(" ", indexOfAutoIncrement);

            if (indexOfNextSpace == -1) {
                throw new IllegalStateException("Cannot find space after AUTO_INCREMENT=");
            }
            create = create.replace(create.substring(indexOfAutoIncrement, indexOfNextSpace), "");
        }
        return create.replaceAll("`", "").
                replaceAll("IF NOT EXISTS", "").
                replaceAll("\n", "").
                replaceAll(";", "").
                replaceAll(" ", "").
                trim().toUpperCase();
    }

    public static boolean createMatches(String first, String second) {
        return trimCreateStatement(first).equals(trimCreateStatement(second));
    }
}
