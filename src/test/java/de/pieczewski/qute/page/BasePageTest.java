package de.pieczewski.qute.page;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;

import de.pieczewski.qute.helper.FileHelper;
import io.quarkus.test.junit.QuarkusTest;
import java.io.IOException;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class BasePageTest {

    @Test
    void testBasePage() throws IOException {
        String indexHtml = FileHelper.readResource("/META-INF/resources/index.html");
        given().when().get("/").then().statusCode(200).body(is(indexHtml));
    }

    @Test
    void testBaseKnownPageId() {
        given().when()
                .get("/p/todos")
                .then()
                .statusCode(200)
                .body(containsString("div id=\"todo-container\""));
    }

    @Test
    void testBaseUnknownPageId() {
        given().when()
                .get("/p/does_not_exist")
                .then()
                .statusCode(200)
                .body(containsString("Page DOES_NOT_EXIST not found!"));
    }

    @Test
    void testContentKnownPageId() {
        given().when()
                .get("/p/content/home")
                .then()
                .statusCode(200)
                .body(containsString("Page HOME not found!"));
    }

    @Test
    void testContentUnknownPageId() {
        given().when()
                .get("/p/content/content_does_not_exist")
                .then()
                .statusCode(200)
                .body(containsString("Page CONTENT_DOES_NOT_EXIST not found!"));
    }

    @Test
    void testGetNavigationFragment() {
        given().when()
                .get("/p/navigation")
                .then()
                .statusCode(200)
                .body(startsWith("<!-- navigation start -->"))
                .body(endsWith("<!-- navigation end -->"));
    }
}
