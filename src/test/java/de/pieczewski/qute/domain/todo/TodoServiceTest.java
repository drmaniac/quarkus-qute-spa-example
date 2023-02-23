package de.pieczewski.qute.domain.todo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import java.time.Duration;
import javax.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class TodoServiceTest {

    @Inject TodoService todoService;

    @Test
    void testFindById() {
        var result = todoService.findById(10000L).await().atMost(Duration.ofSeconds(1L));
        assertNotNull(result);
        assertNotNull(result.getId());
    }

    @Test
    void testFindByIdNotFound() {
        var result = todoService.findById(0L).await().atMost(Duration.ofSeconds(2L));
        assertNotNull(result);
        assertNull(result.getId());
    }

    @Test
    void testUpdateByRef() {
        var ref = todoService.findById(10000L).await().atMost(Duration.ofSeconds(2L));
        var oldSummary = ref.getSummary();
        var oldDescription = ref.getDescription();

        var todoToUpdate = new Todo();
        todoToUpdate.setSummary("new summary");
        todoToUpdate.setDescription("new description");

        var updated =
                todoService.updateByRef(todoToUpdate, ref).await().atMost(Duration.ofSeconds(1L));
        assertNotNull(updated);
        assertEquals(10000L, updated.id);
        assertNotEquals(oldSummary, updated.summary);
        assertNotEquals(oldDescription, updated.description);
    }

    @Test
    void testUpdateByRefWithNotPersistedEntity001() {
        var ref = new Todo();
        var todoToUpdate = new Todo();
        var res = todoService.updateByRef(todoToUpdate, ref);
        UniAssertSubscriber<Todo> subscriber =
                res.subscribe().withSubscriber(UniAssertSubscriber.create());
        subscriber
                .awaitFailure()
                .assertFailedWith(NullPointerException.class, "reference is not persisted");
    }

    @Test
    void testUpdateByRefWithNotPersistedEntity002() {
        var ref = new Todo();
        ref.setId(0L);
        var todoToUpdate = new Todo();
        var res = todoService.updateByRef(todoToUpdate, ref);
        UniAssertSubscriber<Todo> subscriber =
                res.subscribe().withSubscriber(UniAssertSubscriber.create());
        subscriber
                .awaitFailure()
                .assertFailedWith(NullPointerException.class, "reference is not persisted");
    }

    @Test
    void testUpdateByRefWithNotPersistedEntity003() {
        var todoToUpdate = new Todo();
        var res = todoService.updateByRef(todoToUpdate, null);
        UniAssertSubscriber<Todo> subscriber =
                res.subscribe().withSubscriber(UniAssertSubscriber.create());
        subscriber
                .awaitFailure()
                .assertFailedWith(NullPointerException.class, "reference is not persisted");
    }
}
