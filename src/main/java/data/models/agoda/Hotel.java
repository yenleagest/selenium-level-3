package data.models.agoda;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
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
