package ee.nipt.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.GenericValidator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class IdUtils {
    private static final String NON_ESTONIAN_ID_DATE_PATTERN = "ddMMyyyy";

    /**
     * Control that Estonian national identification code is valid (https://www.riigiteataja.ee/akt/12862791?leiaKehtiv)
     * or if not Estonian ID then value must contain
     * keyword 'A' following date of birth in format of ddMMyyyy
     * Example: 2349354A04091994
     *
     * @param value to be checked
     * @return <code>true</code> if correct erse <code>false</code>
     */
    public static boolean isValid(String value) {
        return value != null && (isValidEstonianId(value) || isValidNonEstonianId(value));
    }

    /**
     * @return date of birth calculated from Estonian national identification number or from special id containing
     * keyword A following date of birth of format ddMMyyyy OR null if is invalid
     */
    public static LocalDate calculateDateOfBirth(String id) {
        if (isValidNonEstonianId(id))
            return getDateFromNonEstonianId(id);
        else if (isValidEstonianId(id))
            return getDateFromEstonianId(id);
        else
            return null;
    }

    private static boolean isValidNonEstonianId(String value) {
        if (!value.contains("A")) return false;
        String[] array = value.split("A");
        return array.length == 2 && GenericValidator.isDate(array[1], NON_ESTONIAN_ID_DATE_PATTERN, true);
    }

    private static LocalDate getDateFromNonEstonianId(String id) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern(NON_ESTONIAN_ID_DATE_PATTERN);
        return LocalDate.parse(id.split("A")[1], format);
    }

    private static LocalDate getDateFromEstonianId(String id) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern(NON_ESTONIAN_ID_DATE_PATTERN);
        //format.setLenient(false);
        StringBuilder dateAsString = new StringBuilder();

        dateAsString.append(String.valueOf(id.charAt(5)));
        dateAsString.append(String.valueOf(id.charAt(6)));

        dateAsString.append(String.valueOf(id.charAt(3)));
        dateAsString.append(String.valueOf(id.charAt(4)));

        switch (String.valueOf(id.charAt(0))) {
            case "1":
            case "2":
                dateAsString.append("18");
                break;
            case "3":
            case "4":
                dateAsString.append("19");
                break;
            case "5":
            case "6":
                dateAsString.append("20");
                break;
            default:
                return null;
        }

        dateAsString.append(String.valueOf(id.charAt(1)));
        dateAsString.append(String.valueOf(id.charAt(2)));
        String d = dateAsString.toString();

        if (GenericValidator.isDate(d, NON_ESTONIAN_ID_DATE_PATTERN, true))
            return LocalDate.parse(d, format);
        else
            return null;
    }

    private static boolean isValidEstonianId(String value) {
        if (value == null || !StringUtils.isNumeric(value) || value.length() != 11) return false;

        Integer lastNumber = Integer.valueOf(String.valueOf(value.charAt(value.length() - 1)));

        List<Integer> quotients = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 1);
        int sum = 0;
        for (int i = 0; i < quotients.size(); i++) {
            sum += (quotients.get(i) * Character.getNumericValue(value.charAt(i)));
        }


        if (sum % 11 == lastNumber)
            return true;

        if (sum % 11 == 10) {
            quotients = Arrays.asList(3, 4, 5, 6, 7, 8, 9, 1, 2, 3);

            sum = 0;
            for (int i = 0; i < quotients.size(); i++) {
                sum += (quotients.get(i) * Character.getNumericValue(value.charAt(i)));
            }
            return sum % 11 == lastNumber;
        }
        return false;
    }
}
