package utils;

import com.github.javafaker.Faker;

import java.time.LocalDate;

public class TravellerUtils {

    public static String getTravellerName() {
        Faker faker = new Faker();
        // remove any special characters from the name, like "Ms." "O'Connell"
        // which are not allowed by the input validation
        return faker.name().fullName().replaceAll("[^a-zA-Z\\s]", "");
    }

    public static LocalDate getTravellerDOB(int age) {
        return LocalDate.now().minusYears(age);
    }
}
