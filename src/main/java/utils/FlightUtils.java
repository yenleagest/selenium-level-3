package utils;

import lombok.extern.slf4j.Slf4j;
import reports.AllureManager;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class FlightUtils {

    public record CheapestFlights(LocalDate departureDate, String departurePrice, LocalDate returnDate, String returnPrice) {
    }

    public static CheapestFlights findCheapest(Map<LocalDate, String> departures, Map<LocalDate, String> returns, int duration) {
        int minSum = Integer.MAX_VALUE;
        CheapestFlights result = null;

        for (Map.Entry<LocalDate, String> dep : departures.entrySet()) {
            LocalDate departureDate = dep.getKey();
            LocalDate returnDate = departureDate.plusDays(duration);
            String departurePrice = dep.getValue().trim();

            if (!returns.containsKey(returnDate)) continue;  // when departure date + duration exceeds available return dates
            String returnPrice = returns.get(returnDate).trim();

            try {
                int depVal = Integer.parseInt(departurePrice.replaceAll("\\D", ""));
                int retVal = Integer.parseInt(returnPrice.replaceAll("\\D", ""));
                int sum = depVal + retVal;

                if (sum < minSum) {
                    minSum = sum;
                    result = new CheapestFlights(departureDate, departures.get(departureDate), returnDate, returns.get(returnDate));
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                        "Invalid price format for combination of %s".formatted(
                                new CheapestFlights(departureDate, departures.get(departureDate), returnDate, returns.get(returnDate))
                        ),
                        e
                );
            }
        }

        AllureManager.saveLog(
                "Cheapest flight found",
                      """
                        • Departure: %s — Price: %s
                        • Return   : %s — Price: %s
                      """.formatted(
                      Objects.requireNonNull(result).departureDate,
                      result.departurePrice,
                      result.returnDate,
                      result.returnPrice
                )
        );

        return result;
    }

    public static String formatDatePrices(Map<LocalDate, String> datePrices) {
        StringBuilder sb = new StringBuilder("\n");
        datePrices.forEach((date, price) -> sb.append("• %s: %s%n".formatted(date, price)));
        return sb.toString();
    }
}
