package cz.vsb.and0153.campmanagement.server.controller;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;

@Slf4j
public class ControllerServlet extends HttpServlet {

	@Serial
	private static final long serialVersionUID = 5394513874009973020L;

	private final transient MappingMethod getMethod;
	private final transient MappingMethod postMethod;
	private final String name;

	@Builder()
	public ControllerServlet(MappingMethod getMethod, MappingMethod postMethod) {
		this.getMethod = getMethod;
		this.postMethod = postMethod;

		StringBuilder nameBuilder = new StringBuilder();
		if (getMethod != null) {
			nameBuilder.append(getMethod.controller().getClass().getSimpleName());
			nameBuilder.append("_");
			nameBuilder.append(getMethod.method().getName());
		}
		if (postMethod != null) {
			if (getMethod != null) {
				nameBuilder.append("-");
			}
			nameBuilder.append(postMethod.controller().getClass().getSimpleName());
			nameBuilder.append("_");
			nameBuilder.append(postMethod.method().getName());
		}

		this.name = nameBuilder.toString();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		log.debug("GET request received for '{}'", req.getRequestURI());
		call(getMethod, req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		log.debug("POST request received for '{}'", req.getRequestURI());
		call(postMethod, req, resp);
	}

	private void call(MappingMethod method, HttpServletRequest req, HttpServletResponse resp) {
		try {
			if (method != null) {
				method.invoke(req, resp);
				return;
			}
			resp.sendError(HttpServletResponse.SC_NOT_FOUND); // Method isn't allowed
		} catch (Exception e) {
			try {
				log.error("Error while processing request", e);
				resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			} catch (Exception ignore) { /* ignore */ }
		}
	}

	@Override
	public String toString() {
		return this.name;
	}
}
