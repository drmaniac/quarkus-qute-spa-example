package de.pieczewski.qute.page.fragments;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import de.pieczewski.qute.domain.todo.Todo;
import de.pieczewski.qute.domain.todo.TodoService;
import io.quarkus.test.junit.QuarkusTest;
import java.time.Duration;
import javax.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
class TodosPageTest {

    @Inject TodoService todoService;
    @Inject TodosPage todosPage;

    @Test
    void testDelete() {
        // create a todoEntity
        var todo = new Todo();
        todo.setSummary("delete me");
        todo.setDescription("delete me");
        var todoEntity = todoService.create(todo).await().atMost(Duration.ofSeconds(1L));

        // delete it via rest api
        given().when()
                .delete("/p/content/fragment/todo/" + todoEntity.getId())
                .then()
                .statusCode(200)
                .body(is("")); // empty body
    }

    @Test
    void testGetElementEdit() {
        given().when()
                .get("/p/content/fragment/todo/91001")
                .then()
                .statusCode(200)
                .body(containsString(" value=\"TodoPageTest-testGetElementEdit\""));
    }

    @Test
    void testPostTodoCreate() {
        given().when()
                .contentType("multipart/form-data")
                .multiPart("id", 0L)
                .multiPart("summary", "new todo summary")
                .multiPart("description", "new todo description")
                .post("/p/content/fragment/todo")
                .then()
                .statusCode(200)
                .body(containsString("id=\"element-"))
                .body(containsString("new todo summary"))
                .body(containsString("new todo description"));
    }

    @Test
    void testPostTodoEdit() {
        var todoToUpdate = todoService.findById(91000L).await().atMost(Duration.ofSeconds(1L));
        assertNotNull(todoToUpdate);
        given().when()
                .contentType("multipart/form-data")
                .multiPart("id", todoToUpdate.getId())
                .multiPart("summary", todoToUpdate.getSummary())
                .multiPart("description", "update todo description")
                .post("/p/content/fragment/todo")
                .then()
                .statusCode(200)
                .body(containsString("id=\"element-91000\""))
                .body(containsString("TodoPageTest-testPostTodoEdit"))
                .body(containsString("update todo description"));
    }

    @Test
    void testRenderTemplate() {
        var todoContent = todosPage.renderTemplate().await().atMost(Duration.ofSeconds(1L));
        assertThat(todoContent, containsString("\n<div class=\"container-xl overflow-auto\">"));
        assertThat(todoContent, containsString("<div id=\"todo-container\""));
        assertThat(todoContent, endsWith("</div>"));
    }
}
