package de.pieczewski.qute.page;

import de.pieczewski.qute.htmx.HTMX;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.smallrye.mutiny.Uni;
import java.util.Optional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.resteasy.reactive.ResponseHeader;
import org.jboss.resteasy.reactive.RestCookie;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.jaxrs.RestResponseBuilderImpl;

@Path("/p")
@Produces(MediaType.TEXT_HTML)
public class BasePage {

    private static final Logger LOGGER = LogManager.getLogger(BasePage.class);
    private static final String PATH_PREFIX = "/p";

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance home(String title, String path, String content);

        public static native TemplateInstance home$navigation(String path);

        public static native TemplateInstance home$content(String content);

        public static native TemplateInstance error_404();
    }

    @GET
    @Path("/{pageId:(?!content$|navigation$).*}")
    @ResponseHeader(name = HTMX.HX_TRIGGER_AFTER_SWAP, value = "content")
    public Uni<RestResponse<String>> page(@PathParam("pageId") Optional<String> path) {
        LOGGER.debug("render page {}", () -> path.orElse("home"));
        return Uni.createFrom()
                .item(
                        createResponse(
                                path.orElse("home"),
                                getPushUri(path),
                                "content",
                                renderBasePage(path)));
    }

    private RestResponse<String> createResponse(
            String path, String pushUri, String swap, String page) {
        return RestResponseBuilderImpl.ok(page)
                .header(HTMX.HX_TRIGGER_AFTER_SWAP, swap)
                .header(HTMX.HX_PUSH, pushUri)
                .cookie(cookieOf("path", path))
                .build();
    }

    private String renderBasePage(Optional<String> path) {
        return Templates.home("SPA example", path.orElse("home"), getContent(path)).render();
    }

    private String getPushUri(Optional<String> path) {
        return path.map(p -> String.join("/", PATH_PREFIX, p)).orElse(PATH_PREFIX);
    }

    @GET
    @Path("/navigation")
    public Uni<String> getNavigationFragment(@RestCookie("path") Optional<String> path) {
        LOGGER.debug("get fresh navigation fragment: {}", () -> path);
        return Templates.home$navigation(path.orElse("home")).createUni();
    }

    @GET
    @Path("/content/{pageId}")
    public Uni<RestResponse<String>> content(@PathParam("pageId") Optional<String> path)
            throws InterruptedException {
        LOGGER.debug("get fresh content fragment: {}", () -> path);
        return Uni.createFrom()
                .item(
                        createResponse(
                                path.orElse("hpme"),
                                getPushUri(path),
                                "navigation",
                                Templates.home$content(getContent(path)).render()));
    }

    private String getContent(Optional<String> path) {
        return path.map(String::toUpperCase)
                .map(this::renderPage)
                .orElse(Templates.error_404().render());
    }

    private String renderPage(String path) {
        return switch (path) {
            case "LIST" -> ListPage.list().render();
            default -> Templates.error_404().render();
        };
    }

    private NewCookie cookieOf(String name, String value) {
        return new NewCookie(name, value, "/p", null, null, -1, false);
    }
}
