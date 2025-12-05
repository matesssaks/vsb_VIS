package cz.vsb.and0153.campmanagement.server.statics;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.io.OutputStream;

@Slf4j
public class StaticFilesServlet extends HttpServlet {

	private final transient Resource staticFiles;

	public StaticFilesServlet(Resource staticFiles) {
		this.staticFiles = staticFiles;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		log.debug("STATIC-FILE - '{}'", req.getRequestURI());

		try {
			String pathInfo = req.getPathInfo();

			// Invalid path info
			if (pathInfo == null) {
				resp.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}

			// Clean up path info
			if (pathInfo.startsWith("/")) pathInfo = pathInfo.substring(1);

			// Resolve the resource path
			Resource resource = staticFiles.createRelative(pathInfo);
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

	@Override
	public String toString() {
		return "static";
	}
}
