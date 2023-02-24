package de.pieczewski.qute.page;

import de.pieczewski.qute.annotations.ExcludeFromGeneratedCoverage;
import de.pieczewski.qute.htmx.HTMX;
import de.pieczewski.qute.page.services.PageSelector;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.smallrye.mutiny.Uni;
import java.util.Optional;
import javax.inject.Inject;
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
import org.jboss.resteasy.reactive.RestResponse.ResponseBuilder;

/** The base page. */
@Path("/p")
@Produces(MediaType.TEXT_HTML)
public class BasePage {

    private static final String DEFAULT_PAGE = "home";
    private static final String PAGE_TITLE = "SPA example";
    private static final Logger LOGGER = LogManager.getLogger(BasePage.class);

    @SuppressWarnings("java:S1075")
    private static final String PATH_PREFIX = "/p";

    private static final String SWAP_CONTENT = "content";
    private static final String SWAP_NAVIGATION = "navigation";
    private static final String COOKIE_PATH_KEY = "path";

    @CheckedTemplate
    @SuppressWarnings({"java:S1118", "java:S100", "java:S117"})
    @ExcludeFromGeneratedCoverage
    public static class Templates {
        public static native TemplateInstance home(String title, String path, Uni<String> content);

        public static native TemplateInstance home$navigation(String path);

        public static native TemplateInstance home$content(Uni<String> content);

        public static native TemplateInstance error_404(String path);
    }

    @Inject PageSelector pageSelector;

    /**
     * Render the base page with the given path.
     *
     * @param path the path to render
     * @param hxRequest the HX-Request cookie
     * @return the base page
     */
    @GET
    @Path("/{pageId:(?!content$|navigation$).*}")
    @ResponseHeader(name = HTMX.HX_TRIGGER_AFTER_SWAP, value = SWAP_CONTENT)
    public Uni<RestResponse<String>> basePage(
            @PathParam("pageId") Optional<String> path,
            @RestCookie("HX-Request") Boolean hxRequest) {
        String pageId = path.orElse(DEFAULT_PAGE); // get the page id
        LOGGER.debug("render page {} -> HX-Request {}", pageId, hxRequest);
        Uni<String> renderedBasePage = renderBasePage(path);
        return createResponse(pageId, getPushUri(path), SWAP_CONTENT, renderedBasePage);
    }

    /**
     * Render the base page with the given path.
     *
     * @param path the path to render
     * @param hxRequest the HX-Request cookie
     * @return the base page
     */
    private Uni<RestResponse<String>> createResponse(
            String path, String pushUri, String swap, Uni<String> page) {
        return page.map( // map the page to a response
                content -> // the content of the page
                ResponseBuilder.ok(content) // create the response
                                .header(HTMX.HX_TRIGGER_AFTER_SWAP, swap) // set the swap function
                                .header(HTMX.HX_PUSH, pushUri) // set the push uri
                                .cookie(
                                        unsecureCookieOf(
                                                COOKIE_PATH_KEY, path)) // set the path cookie
                                .build()); // build the response
    }

    /**
     * Render the base page with the given path.
     *
     * @param path the path to render
     * @return the base page
     */
    private Uni<String> renderBasePage(Optional<String> path) {
        return Templates.home(PAGE_TITLE, path.orElse(DEFAULT_PAGE), getContent(path)).createUni();
    }

    /**
     * Get the push uri for the given path.
     *
     * @param path the path
     * @return the push uri
     */
    private String getPushUri(Optional<String> path) {
        return path.map(p -> String.join("/", PATH_PREFIX, p)).orElse(PATH_PREFIX);
    }

    /**
     * Get the fresh navigation fragment.
     *
     * @param path the path
     * @return the fresh navigation fragment
     */
    @GET
    @Path("/navigation")
    public Uni<String> getNavigationFragment(@RestCookie(COOKIE_PATH_KEY) Optional<String> path) {
        LOGGER.debug("get fresh navigation fragment: {}", () -> path);
        return Templates.home$navigation(path.orElse(DEFAULT_PAGE))
                .createUni(); // render the navigation
    }

    /**
     * Get the fresh content fragment.
     *
     * @param pageId the path
     * @return the fresh content fragment
     */
    @GET
    @Path("/content/{pageId}")
    public Uni<RestResponse<String>> content(@PathParam("pageId") Optional<String> pageIdOpt) {
        LOGGER.debug("get fresh content fragment: {}", () -> pageIdOpt);
        String pageId = pageIdOpt.orElse("home");
        String pushUri = getPushUri(pageIdOpt);
        Uni<String> content = Templates.home$content(getContent(pageIdOpt)).createUni();
        return createResponse(pageId, pushUri, SWAP_NAVIGATION, content);
    }

    /**
     * Get the content for the given path.
     *
     * @param pageId the path
     * @return the content
     */
    @SuppressWarnings("java:S3655")
    private Uni<String> getContent(Optional<String> pageId) {
        return pageId.map(String::toUpperCase) // convert to upper case
                .map(this::renderPage) // render the page
                .get(); // renderPage is never empty
    }

    /**
     * Render the error page.
     *
     * @param pageId the page id
     * @return the error page
     */
    private Uni<String> renderErrorPage(String pageId) {
        return Templates.error_404(pageId).createUni();
    }

    /**
     * Render the page for the given path.
     *
     * @param path the path
     * @return the page
     */
    private Uni<String> renderPage(String path) {
        return pageSelector
                .select(path) // select the page
                .map(page -> page.renderTemplate()) // render the template
                .orElseGet(() -> renderErrorPage(path)); // render the error page
    }

    /**
     * Create a new cookie with the given name and value.
     *
     * @param name the name
     * @param value the value
     * @return the cookie
     */
    @SuppressWarnings("java:S2092")
    private NewCookie unsecureCookieOf(String name, String value) {
        LOGGER.debug("create cookie {}={}", name, value);
        return new NewCookie(name, value, PATH_PREFIX, null, null, -1, false, true);
    }
}
