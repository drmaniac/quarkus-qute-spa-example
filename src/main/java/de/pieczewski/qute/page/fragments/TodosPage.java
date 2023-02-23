package de.pieczewski.qute.page.fragments;

import de.pieczewski.qute.domain.todo.Todo;
import de.pieczewski.qute.domain.todo.TodoService;
import de.pieczewski.qute.page.interfaces.RenderablePage;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.smallrye.mutiny.Uni;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** The todos page. */
@SuppressWarnings("java:S1135")
@Path("/p/content/fragment/todo")
@Produces(MediaType.TEXT_HTML)
public class TodosPage implements RenderablePage {

    private static final Logger LOGGER = LogManager.getLogger(TodosPage.class);

    @Inject TodoService todoService;

    @CheckedTemplate
    @SuppressWarnings({"java:S1118", "java:S100", "java:S117"})
    public static class TodoTemplates {
        public static native TemplateInstance todos(List<Todo> todos, Long id, Todo todo);

        public static native TemplateInstance todos$element(Todo it);

        public static native TemplateInstance todos$element_edit(Long id, Todo todo);
    }

    /**
     * Render the todos page.
     *
     * @return the todos page
     */
    public Uni<String> renderTemplate() {
        LOGGER.info("load todos first time");
        return todoService
                .listAll()
                .map(todos -> TodoTemplates.todos(todos, 0L, new Todo()))
                .map(TemplateInstance::render);
    }

    @GET
    @Path("/{id}")
    public Uni<String> getElementEdit(@PathParam("id") Long id) {
        if (id == 0) {
            LOGGER.info("create new todo {} ", () -> id);
            return Uni.createFrom().item(TodoTemplates.todos$element_edit(id, new Todo()).render());
        } else {
            LOGGER.info("edit todo: {}", id);
            return todoService
                    .findById(id)
                    .invoke(todo -> LOGGER.info("found todo: {}", todo))
                    .map(todo -> TodoTemplates.todos$element_edit(todo.getId(), todo).render());
        }
    }

    /**
     * Http Form post to add a new todo.
     *
     * @param newTodo the todo to add
     * @return the todos page
     */
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Uni<String> postTodo(@FormParam("id") Long id, Todo newTodo) {
        LOGGER.debug("post todo data: {}", newTodo);
        return todoService
                .findById(id)
                .invoke(oldTodo -> LOGGER.info("found old todo: {}", oldTodo))
                .chain(oldTodo -> createOrUpdate(newTodo, oldTodo))
                .map(TodoTemplates::todos$element)
                .map(TemplateInstance::render);
    }

    /**
     * Create or update a todo.
     *
     * @param newTodo the new todo
     * @param oldTodo the old todo
     * @return the updated todo
     */
    private Uni<Todo> createOrUpdate(Todo newTodo, Todo oldTodo) {
        return isOldTodoPersisted(oldTodo)
                ? todoService.create(newTodo)
                : todoService.updateByRef(newTodo, oldTodo);
    }

    private boolean isOldTodoPersisted(Todo oldTodo) {
        return oldTodo == null || oldTodo.getId() == null || oldTodo.getId() == 0;
    }

    /**
     * Http delete to delete a todo.
     *
     * @param id the id of the todo to delete
     * @return empty string
     */
    @DELETE
    @Path("/{id}")
    public Uni<String> delete(@PathParam("id") Long id) {
        LOGGER.debug("delete todo: {}", id);
        return todoService.delete(id).map(ignore -> "");
    }
}
