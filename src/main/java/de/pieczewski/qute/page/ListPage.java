package de.pieczewski.qute.page;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.smallrye.mutiny.Uni;

@Path("/p/content/template/list")
public class ListPage {

    private static final Logger LOGGER = LogManager.getLogger(ListPage.class);

    private static final int SIZE = 15;

    @CheckedTemplate
    public static class ListTemplates {
        public static native TemplateInstance list(List<String> elements,Integer nextPage);

        public static native TemplateInstance list$listElements(
                List<String> elements, Integer nextPage, Boolean element_hasNext);
    }

    public static TemplateInstance list() {
        LOGGER.info("load list first time");
        return ListTemplates.list(getList(0), 1);
    }

    @GET
    public Uni<String> getListElements(@QueryParam("page") Optional<Integer> page){
        var nextPage = page.map(p -> p > 3 ? -1 : p+1).orElse(1);
        LOGGER.info("page: {}", () -> page);
        LOGGER.info("next page: {}", () -> nextPage);
        
        return ListTemplates.list$listElements(
            getList(nextPage-1), 
            nextPage, null).createUni();
    }

    private static List<String> getList(int page) {
        if(page < 0) {
            return List.of();
        }
        var start = (page*SIZE)+1;
        var end = start+SIZE;
        return IntStream.range(start,end).mapToObj(i -> String.format("Element%04d", i)).toList();
    }
}
