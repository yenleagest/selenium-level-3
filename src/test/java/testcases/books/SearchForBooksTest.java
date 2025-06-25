package testcases.books;

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

    @Test(groups = {"smoke", "regression"}, description = "Search for books successfully")
    public void searchForBooks() {
        keyword = "playwright";

        homePage.searchForBook(keyword);

        softAssert.assertTrue(searchResultsPage.doAllResultsContainKeyword(keyword));

        softAssert.assertAll();
    }
}
