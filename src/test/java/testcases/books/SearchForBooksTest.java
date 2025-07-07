package testcases.books;

import data.enums.books.LocatorStrategy;
import drivers.DriverUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.books.HomePage;
import pages.books.SearchResultsPage;
import testcases.TestBase;

public class SearchForBooksTest extends TestBase {

    HomePage homePage;
    SearchResultsPage searchResultsPage;
    SoftAssert softAssert;
    String keyword;

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod() {
        DriverUtils.openURL(); // Navigate to https://books-pwakit.appspot.com/
        homePage = new HomePage();
        searchResultsPage = new SearchResultsPage();
        softAssert = new SoftAssert();
    }

    @Test(dataProvider = "locatorStrategy", groups = {"smoke", "regression"}, description = "Search for books successfully")
    public void searchForBooks(LocatorStrategy strategy) {
        keyword = "playwright";

        homePage.searchForBook(strategy, keyword);

        softAssert.assertTrue(searchResultsPage.doAllResultsContainKeyword(strategy, keyword));

        softAssert.assertAll();
    }
}
