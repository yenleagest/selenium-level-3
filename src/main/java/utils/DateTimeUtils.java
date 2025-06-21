package utils;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateTimeUtils {

    public static String getTimeRelation(String monthYear, LocalDate localDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);
        YearMonth parsed = YearMonth.parse(monthYear, formatter);
        YearMonth current = YearMonth.from(localDate);

        if (parsed.isAfter(current)) {
            return "next";
        } else if (parsed.isBefore(current)) {
            return "previous";
        } else {
            return "current";
        }
    }
}
