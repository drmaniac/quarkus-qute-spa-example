package de.pieczewski.qute.page.fragments;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.IsEmptyString.emptyString;

import io.quarkus.test.junit.QuarkusTest;
import java.time.Duration;
import javax.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class ListPageTest {

    @Inject ListPage listPage;

    @Test
    void testGetListElements() {
        given().when()
                .get("/p/content/fragment/list")
                .then()
                .statusCode(200)
                .body(containsString("hx-get=\"/p/content/fragment/list?page=1"));
    }

    @Test
    void testGetListElementsSecondPage() {
        given().when()
                .get("/p/content/fragment/list?page=1")
                .then()
                .statusCode(200)
                .body(containsString("hx-get=\"/p/content/fragment/list?page=2"));
    }

    @Test
    void testGetListElementsLastPage() {
        given().when()
                .get("/p/content/fragment/list?page=" + ListPage.MAX_PAGES + 1)
                .then()
                .statusCode(200)
                .body(emptyString());
    }

    @Test
    void testRenderTemplate() {
        var listContent = listPage.renderTemplate().await().atMost(Duration.ofSeconds(1L));
        assertThat(listContent, containsString("div id=\"list-container\""));
    }
}
