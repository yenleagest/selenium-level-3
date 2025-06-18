package data.models;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@AllArgsConstructor
@Slf4j
public class CardContainer {

    String destination;
    int price;
    int rating;
}
