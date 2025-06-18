package utils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CurrencyConverter {

    public static int convert(int amount, String symbol) {
        int conversionRate = Currency.getRateByCurrencySymbol(symbol);
        int convertedAmt = amount / conversionRate;
        log.info("Converted amount: %d %s".formatted(convertedAmt, symbol));
        return convertedAmt;
    }

    // when running on Jenkins, agoda will display the server currency
    @AllArgsConstructor
    enum Currency {
        SGD("SGD", 20000),
        USD("$", 25000),
        KRW("₩", 19);

        private final String currencySymbol;
        private final int conversionRate;

        public static int getRateByCurrencySymbol(String symbol) {
            for (Currency currency : Currency.values()) {
                if (currency.currencySymbol.equals(symbol)) {
                    return currency.conversionRate;
                }
            }
            return 1; // default rate if not matched
        }
    }
}
