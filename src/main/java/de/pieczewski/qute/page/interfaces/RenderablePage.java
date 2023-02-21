package de.pieczewski.qute.page.interfaces;

import io.smallrye.mutiny.Uni;

/** A renderable page. */
public interface RenderablePage {

    /**
     * Renders the page.
     *
     * @return the rendered page
     */
    Uni<String> renderTemplate();
}
