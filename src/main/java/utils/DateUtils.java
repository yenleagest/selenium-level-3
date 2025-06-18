package utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;


public class DateUtils {

    /**
     * returns a formatted date string or day of month based on the given weekday and week offset
     *
     * @param offset  the relative week (this, next, last)
     * @param weekday the target's day of the week
     * @return LocalDate object representing the target date
     */
    public static LocalDate getLocalDate(WeekOffset offset, Weekday weekday) {
        LocalDate today = LocalDate.now();
        DayOfWeek currentDayOfWeek = today.getDayOfWeek();
        DayOfWeek targetDayOfWeek = DayOfWeek.valueOf(weekday.name());
        int dayDifference = targetDayOfWeek.getValue() - currentDayOfWeek.getValue();
        return today.plusDays(dayDifference + offset.getOffsetWeeks() * 7L);
    }

    // enum representing the relative position of a target day (this, next, or last week).
    @Getter
    @RequiredArgsConstructor
    public enum WeekOffset {
        THIS(0),
        NEXT(1),
        LAST(-1);

        private final int offsetWeeks;

        public static WeekOffset fromString(String name) {
            try {
                return WeekOffset.valueOf(name.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("No week offset found with name: %s".formatted(name));
            }
        }
    }

    // enum representing days of the week
    public enum Weekday {
        MONDAY,
        TUESDAY,
        WEDNESDAY,
        THURSDAY,
        FRIDAY,
        SATURDAY,
        SUNDAY;

        public static Weekday fromString(String name) {
            try {
                return Weekday.valueOf(name.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("No weekday found with name: %s".formatted(name));
            }
        }
    }
}
