package ch.epfl.culturequest.backend.tournament.utils;

import static ch.epfl.culturequest.backend.tournament.apis.SeedApi.getCurrentSeed;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Random;
import java.util.UUID;

public class RandomApi {

    private RandomApi() {
        // Private constructor to prevent instantiation
    }

    public static Random getRandom() { return new Random(getCurrentSeed());}


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

    public static Calendar generateWeeklyTournamentDate() {

        Random random = getRandom();
        int randomHour = random.nextInt(24);
        int randomMinute = random.nextInt(60);
        int randomSecond = random.nextInt(60);
        int randomDay = random.nextInt(7);


        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, randomHour);
        calendar.set(Calendar.MINUTE, randomMinute);
        calendar.set(Calendar.SECOND, randomSecond);
        calendar.set(Calendar.DAY_OF_WEEK, randomDay);

        return calendar;
    }

}
