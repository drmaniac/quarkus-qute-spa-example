package de.pieczewski.qute.page.services;

import io.quarkus.qute.EngineBuilder;
import io.quarkus.qute.UserTagSectionHelper;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

/** The tag configuration. */
@ApplicationScoped
public class TagConfiguration {

    /**
     * Configure the engine.
     *
     * @param builder the engine builder
     */
    void configureEngine(@Observes EngineBuilder builder) {
        builder.addSectionHelper(
                        new UserTagSectionHelper.Factory("navigation", "Index/navigation.html"))
                .build();
    }
}
