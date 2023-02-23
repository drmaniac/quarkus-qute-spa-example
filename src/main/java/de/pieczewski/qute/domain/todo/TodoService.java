package de.pieczewski.qute.domain.todo;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.smallrye.mutiny.Uni;
import java.util.List;
import java.util.Objects;
import javax.enterprise.context.RequestScoped;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Service class for managing todos. */
@RequestScoped
@SuppressWarnings("java:S3252")
public class TodoService {

    private static final Logger LOGGER = LogManager.getLogger(TodoService.class);

    /**
     * Add a new todo.
     *
     * @param todo
     * @return
     */
    @ReactiveTransactional
    public Uni<Todo> create(Todo todo) {
        LOGGER.debug("create todo: {}", todo);
        todo.setId(null); // Reset id
        return todo.persist();
    }

    /**
     * Delete a todo.
     *
     * @param id the id of the todo to delete
     */
    @ReactiveTransactional
    public Uni<Void> delete(Long id) {
        LOGGER.debug("delete todo: {}", id);
        return Todo.findById(id).chain(todo -> todo.delete());
    }

    /**
     * Update a todo.
     *
     * @param todo the todo to update
     * @return the updated todo
     */
    @ReactiveTransactional
    public Uni<Todo> update(Todo todo) {
        LOGGER.debug("update todo: {}", todo);
        return Todo.findById(todo.id).chain(entity -> updateByRef(todo, entity));
    }

    /**
     * List all todos.
     *
     * @return the list of todos
     */
    public Uni<List<Todo>> listAll() {
        LOGGER.debug("list all todo's");
        return Todo.listAll();
    }

    /**
     * Update a todo entity.
     *
     * @param todo the todo to update
     * @param ref the entity to update
     * @return the updated todo
     */
    @ReactiveTransactional
    public Uni<Todo> updateByRef(Todo todo, PanacheEntityBase ref) {
        LOGGER.debug("update todo entity: {} with new: {}", ref, todo);
        Todo oldTodo = (Todo) ref; // Cast to Todo
        if (Objects.isNull(oldTodo) || Objects.isNull(oldTodo.getId()) || oldTodo.getId() == 0L) {
            return Uni.createFrom().failure(new NullPointerException("reference is not persisted"));
        }
        oldTodo.setDescription(todo.description);
        oldTodo.setSummary(todo.summary);
        return oldTodo.persist();
    }

    /**
     * Find a todo by id.
     *
     * @param id the id of the todo to find
     * @return the todo
     */
    public Uni<Todo> findById(Long id) {
        LOGGER.debug("find todo by id: {}", id);
        return Todo.findById(id)
                .map(Todo.class::cast)
                .chain(
                        todo ->
                                todo != null
                                        ? Uni.createFrom().item(todo)
                                        : Uni.createFrom().item(new Todo()));
    }
}
