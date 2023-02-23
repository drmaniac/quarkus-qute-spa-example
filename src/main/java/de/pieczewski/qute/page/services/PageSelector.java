package de.pieczewski.qute.page.services;

import de.pieczewski.qute.page.fragments.ListPage;
import de.pieczewski.qute.page.fragments.TodosPage;
import de.pieczewski.qute.page.interfaces.RenderablePage;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ApplicationScoped
public class PageSelector {

    private static final Logger LOGGER = LogManager.getLogger(PageSelector.class);

    private Map<String, RenderablePage> templates = new ConcurrentHashMap<>();

    @Inject private TodosPage todosPage;
    @Inject private ListPage listPage;

    /** Initializes the page selector. */
    @PostConstruct
    public void init() {
        templates.put("TODOS", todosPage);
        templates.put("LIST", listPage);
    }

    /**
     * Selects a page by its id.
     *
     * @param pageId the id of the page
     * @return the page or an empty optional
     */
    public Optional<RenderablePage> select(String pageId) {
        LOGGER.debug("Selecting page with id {}", pageId);
        var page = templates.get(pageId);
        return page == null ? Optional.empty() : Optional.of(page);
    }
}
