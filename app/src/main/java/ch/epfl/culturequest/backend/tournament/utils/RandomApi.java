package ch.epfl.culturequest.backend.tournament.utils;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Random;
import java.util.UUID;

public class RandomApi {
    private static final long SEED_COMPLEMENT = 517802433597L;

    private RandomApi() {
        // Private constructor to prevent instantiation
    }

    public static Random getRandom() { return new Random(getCurrentSeed());}

    // Get fresh seed associated to the current week number and year + a complement number to add entropy
    private static Long getCurrentSeed() {

        Calendar calendar = Calendar.getInstance();
        int weekNumber = calendar.get(Calendar.WEEK_OF_YEAR);
        int year = calendar.get(Calendar.YEAR);
        return (long) weekNumber + year + SEED_COMPLEMENT;
    }
    public static String getWeeklyTournamentPseudoRandomUUID() {

        long seed = getCurrentSeed();

        try {
            // Convert the seed to a byte array
            ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
            buffer.putLong(seed);
            byte[] seedBytes = buffer.array();

            // Generate the MD5 hash of the seed bytes
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(seedBytes);

            // Convert the hash bytes to a BigInteger
            BigInteger hashInt = new BigInteger(1, hashBytes);

            // Create a UUID using the hash value as the most significant bits
            UUID uuid = new UUID(hashInt.longValue(), 0L);

            return uuid.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
