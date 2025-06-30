package data.enums.agoda;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Facilities {

    NONE_SMOKING("Non-smoking"),
    INTERNET("Internet"),
    CAR_PARKING("Car park");

    private final String facility;
}
