package controls;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import data.enums.Environment;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

import static com.codeborne.selenide.Condition.visible;
import static common.Constants.ENGLISH_YEAR_MONTH_FORMATTER;
import static common.Constants.ENVIRONMENT;
import static common.Constants.STANDARD_YEAR_MONTH_FORMATTER;

@Slf4j
public class DatePicker {

    private final SelenideElement calendar;
    private final SelenideElement nextMonthBtn;
    private final SelenideElement previousMonthBtn;
    private final SelenideElement selectableDate;

    public DatePicker(SelenideElement calendar, SelenideElement nextMonthBtn, SelenideElement previousMonthBtn, SelenideElement targetDate) {
        this.calendar = calendar;
        this.nextMonthBtn = nextMonthBtn;
        this.previousMonthBtn = previousMonthBtn;
        this.selectableDate = targetDate;
    }

    public void selectDate(LocalDate targetDate) {
        alignDatePickerToMonth(targetDate);
        selectableDate.click();
    }

    public DatePicker alignDatePickerToMonth(LocalDate targetDate) {
        YearMonth current = getCurrentMonth();
        YearMonth target = YearMonth.from(targetDate);
        while (!Objects.requireNonNull(current).equals(target)) {
            if (current.isBefore(target)) nextMonthBtn.click();
            else previousMonthBtn.click();
            current = getCurrentMonth();
        }
        return this;
    }

    private YearMonth getCurrentMonth() {
        String text = calendar.shouldBe(visible).shouldNotHave(Condition.exactText("")).getText().trim();
        DateTimeFormatter formatter;
        if (ENVIRONMENT == Environment.VJ_VI) {
            text = text.split(" ", 2)[1].trim();
            formatter = STANDARD_YEAR_MONTH_FORMATTER;
        } else
            formatter = ENGLISH_YEAR_MONTH_FORMATTER;

        try {
            return YearMonth.parse(text, formatter);
        } catch (DateTimeParseException e) {
            log.error("Failed to parse year and month from text: '{}'. Error: {}", text, e.getMessage());
            return null;
        }
    }
}
