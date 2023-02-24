package de.pieczewski.qute.page.fragments;

import de.pieczewski.qute.annotations.ExcludeFromGeneratedCoverage;
import de.pieczewski.qute.page.interfaces.RenderablePage;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.smallrye.mutiny.Uni;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** The list page. */
@Path("/p/content/fragment/list")
public class ListPage implements RenderablePage {

    private static final Logger LOGGER = LogManager.getLogger(ListPage.class);

    private static final int SIZE = 15;
    private static final int MAX_PAGES = 1000;

    @CheckedTemplate
    @SuppressWarnings({"java:S1118", "java:S100", "java:S117"})
    @ExcludeFromGeneratedCoverage
    public static class ListTemplates {
        public static native TemplateInstance list(List<String> elements, Integer nextPage);

        public static native TemplateInstance list$listElements(
                List<String> elements, Integer nextPage, Boolean element_hasNext);
    }

    /**
     * Render the list page.
     *
     * @return the list page
     */
    public Uni<String> renderTemplate() {
        LOGGER.info("load list first time");
        return ListTemplates.list(getList(0), 1).createUni();
    }

    /**
     * Render the list page.
     *
     * @param page the page to render
     * @return the list page
     */
    @GET
    public Uni<String> getListElements(@QueryParam("page") Optional<Integer> page) {
        var nextPage = page.map(p -> p > MAX_PAGES ? -1 : p + 1).orElse(1);
        LOGGER.info("page: {}", () -> page);
        LOGGER.info("next page: {}", () -> nextPage);

        return ListTemplates.list$listElements(getList(nextPage - 1), nextPage, null).createUni();
    }

    /**
     * Get the list for the given page.
     *
     * @param page the page
     * @return the list
     */
    private static List<String> getList(int page) {
        if (page < 0) {
            return List.of();
        }
        var start = (page * SIZE) + 1;
        var end = start + SIZE;
        return IntStream.range(start, end).mapToObj(ListPage::getElement).toList();
    }

    private static String getElement(int i) {
        return String.format("Element%04d", i);
    }
}
