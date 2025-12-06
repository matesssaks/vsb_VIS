package cz.vsb.and0153.campmanagement.server.statics;

import cz.vsb.and0153.campmanagement.server.controller.Route;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class StaticFilesController {

	private final Resource staticFiles;

	public void get(HttpServletRequest req, HttpServletResponse resp, Matcher matcher) {
		try {
			String path = matcher.group(1);

			// Validate the path
			if (path.startsWith("/")) {
				resp.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}

			// Resolve the resource path
			Resource resource = staticFiles.createRelative(path);
			if (
				!resource.exists() ||
				!resource.isReadable() ||
				!resource.isFile() ||
				!resource.getFilePath().normalize().startsWith(staticFiles.getFilePath())
			) {
				resp.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}

			// Get the content type
			String mime = req.getServletContext().getMimeType(resource.getFilename());
			if (mime == null) mime = "application/octet-stream";

			// Set headers
			resp.setContentType(mime);
			resp.setStatus(HttpServletResponse.SC_OK);

			// Write the file to the output stream
			@Cleanup InputStream is = resource.getInputStream();
			@Cleanup OutputStream os = resp.getOutputStream();
			is.transferTo(os);
		} catch (Exception e) {
			try {
				log.error("Error while accessing static file", e);
				resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			} catch (Exception ignore) { /* ignore */ }
		}
	}

	public static Route getRoute(String route, Resource staticFiles) throws NoSuchMethodException {
		return Route.getRoute(
			route,
			new String[] { "*" }, // All users can access static files
			Route.RouteType.GET,
			new StaticFilesController(staticFiles),
			StaticFilesController.class.getMethod(
				"get",
				HttpServletRequest.class,
				HttpServletResponse.class,
				Matcher.class
			)
		);
	}
}
