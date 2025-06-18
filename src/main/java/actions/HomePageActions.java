package actions;

import data.models.Occupancy;
import io.qameta.allure.Step;
import pages.agoda.HomePage;
import pages.agoda.HomePage.OccupancyType;

import static pages.agoda.HomePage.OccupancyType.ADULTS;
import static pages.agoda.HomePage.OccupancyType.CHILDREN;
import static pages.agoda.HomePage.OccupancyType.ROOMS;

public class HomePageActions {

    private final HomePage homePage;

    public HomePageActions(HomePage homePage) {
        this.homePage = homePage;
    }

    @Step("Select occupancy: {target}")
    public void setOccupancyTo(Occupancy target) {
        Occupancy current = homePage.getCurrentOccupancy();

        adjustUntilEqual(ROOMS, current.getRooms(), target.getRooms());
        adjustUntilEqual(ADULTS, current.getAdults(), target.getAdults());
        adjustUntilEqual(CHILDREN, current.getChildren(), target.getChildren());
        homePage.selectOccupancyContainer(); // to close the container
    }

    @Step("Set {type} to {target}")
    private void adjustUntilEqual(OccupancyType type, int current, int target) {
        if (current < target) {
            for (int i = current; i < target; i++) {
                homePage.adjustOccupancy(type, true);
            }
        } else if (current > target) {
            for (int i = current; i > target; i--) {
                homePage.adjustOccupancy(type, false);
            }
        }
    }
}
