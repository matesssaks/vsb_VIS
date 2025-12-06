package cz.vsb.and0153.campmanagement.server.controller;

import cz.vsb.and0153.campmanagement.server.security.AuthUser;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;
import java.util.*;
import java.util.regex.Matcher;

@Slf4j
public class ControllerServlet extends HttpServlet {

	@Serial
	private static final long serialVersionUID = 5394513874009973020L;

	private final transient Map<Route.RouteType, List<Route>> routes = new EnumMap<>(Route.RouteType.class);
	private final String unregisteredPath;

	public ControllerServlet(String unregisteredPath) {
		this.unregisteredPath = unregisteredPath;
		Arrays.stream(Route.RouteType.values()).forEach(type -> routes.put(type, new ArrayList<>()));
	}

	public void registerRoute(Route route) {
		log.debug("Registering route: {}", route.route());
		routes.get(route.type()).add(route);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		log.debug("GET - '{}'", req.getRequestURI());
		call(Route.RouteType.GET, req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		log.debug("POST - '{}'", req.getRequestURI());
		call(Route.RouteType.POST, req, resp);
	}

	private void call(Route.RouteType type, HttpServletRequest req, HttpServletResponse resp) {
		// Get user from a session
		AuthUser user = AuthUser.getUser(req);

		try {
			// Set encoding
			req.setCharacterEncoding("UTF-8");
			resp.setCharacterEncoding("UTF-8");

			// Find valid method handler
			for (Route route : routes.get(type)) {
				Matcher matcher = route.matcher(req);

				// Check if the route matches the request
				if (matcher == null || !matcher.matches()) continue;

				// Check if the user has the required permissions
				if (!route.canAccess(user)) {
					resp.sendRedirect(unregisteredPath);
					return;
				}

				// Invoke the route handler
				route.invoke(req, resp, matcher);
				return;
			}

			// Otherwise, return 404
			resp.sendError(HttpServletResponse.SC_NOT_FOUND); // Method isn't allowed
		} catch (Exception e) {
			try {
				log.error("Error while processing request", e);

				// Try to send an error response, but when the response is committed, this does nothing
				resp.reset();
				resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			} catch (Exception ignore) { /* ignore */ }
		}
	}
}
