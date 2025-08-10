package data.models.sia;

import com.github.javafaker.Faker;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Traveller {

    private String name;
    private LocalDate dob;

    private static final Faker faker = new Faker();

    public static Traveller fromAge(int age) {
        return new Traveller(generateTravellerName(), calculateDOBFromAge(age));
    }

    private static String generateTravellerName() {
        // remove special characters disallowed by input validation
        return faker.name().fullName().replaceAll("[^a-zA-Z\\s]", "");
    }

    private static LocalDate calculateDOBFromAge(int age) {
        return LocalDate.now().minusYears(age);
    }
}