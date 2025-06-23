package data.models;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Data
@AllArgsConstructor
@Slf4j
public class Hotel {

    String name;
    String destination;
    int price;
    float rating;
    List<String> benefits;

    public boolean equals(Hotel other) {
        return this.name.equals(other.getName()) && other.getDestination().contains(this.destination) && this.price == other.getPrice() && this.rating == other.rating;
    }
}
