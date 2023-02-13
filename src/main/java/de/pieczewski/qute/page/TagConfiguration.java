package de.pieczewski.qute.page;

import io.quarkus.qute.EngineBuilder;
import io.quarkus.qute.UserTagSectionHelper;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

@ApplicationScoped
public class TagConfiguration {

    void configureEngine(@Observes EngineBuilder builder) {
        builder.addSectionHelper(
                        new UserTagSectionHelper.Factory("navigation", "Index/navigation.html"))
                .build();
    }
}
