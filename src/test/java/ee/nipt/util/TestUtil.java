package ee.nipt.util;

import org.apache.commons.lang3.RandomUtils;

import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Random;

class TestUtil {
    static String nextString() {
        return new BigInteger(130, new Random()).toString(32);
    }

    static LocalDate nextDate() {
        return Instant.ofEpochMilli(Math.abs(System.currentTimeMillis() - RandomUtils.nextLong()))
                .atZone(ZoneId.of("Europe/Helsinki"))
                .toLocalDate();
    }
}
