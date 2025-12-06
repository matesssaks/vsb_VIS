package cz.vsb.and0153.campmanagement.server.controller;

import cz.vsb.and0153.campmanagement.server.security.AuthUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record Route(
	Pattern route,
	String[] permissions,
	RouteType type,
	RouteHandler handler
) {
	/**
	 * Checks if the request matches the route
	 */
	public Matcher matcher(HttpServletRequest req) {
		// Check if the request method matches the type
		if (!req.getMethod().equals(type.name())) return null;

		// Check if the request URI matches the path pattern
		return route.matcher(req.getRequestURI().substring(req.getContextPath().length()));
	}

	/**
	 * Checks if the user has the required permissions to access the route
	 */
	public boolean canAccess(AuthUser user) {
		return Arrays.stream(permissions).anyMatch(user.getPermissions()::contains);
	}

	/**
	 * Invokes the route handler
	 */
	public void invoke(HttpServletRequest req, HttpServletResponse resp, Matcher matcher) throws Exception {
		handler.apply(req, resp, matcher);
	}

	public static Route getRoute(
		String path,
		String[] permissions,
		RouteType type,
		Object controller,
		Method method
	) {
		Class<?>[] parameterTypes = method.getParameterTypes();

		// Method needs to take HttpServletRequest and HttpServletResponse as parameters
		// Optionally takes a Matcher as a third parameter
		if (
			parameterTypes.length < 2 || parameterTypes.length > 3 ||
			!HttpServletRequest.class.isAssignableFrom(parameterTypes[0]) ||
			!HttpServletResponse.class.isAssignableFrom(parameterTypes[1])
		) return null;

		// Create a handler
		RouteHandler handler;
		if (parameterTypes.length == 3) {
			if (!Matcher.class.isAssignableFrom(parameterTypes[2])) return null;

			handler = (req, resp, matcher) -> {
				method.invoke(controller, req, resp, matcher);
			};
		} else {
			handler = (req, resp, matcher) -> {
				method.invoke(controller, req, resp);
			};
		}

		// Add slash to the beginning if not exists
		if (!path.startsWith("/")) path = String.format("/%s", path);

		// Create a route
		return new Route(
			Pattern.compile(String.format("^%s$", path)),
			permissions,
			type,
			handler
		);
	}

	public enum RouteType {
		GET(method -> {
			GetMapping mapping = method.getAnnotation(GetMapping.class);
			if (mapping == null) return null;
			return new Mapping(mapping.path(), mapping.permissions());
		}),
		POST(method -> {
			PostMapping mapping = method.getAnnotation(PostMapping.class);
			if (mapping == null) return null;
			return new Mapping(mapping.path(), mapping.permissions());
		});

		// Mapping annotation tuple
		private record Mapping(String path, String[] permissions) { }

		private final Function<Method, Mapping> mappingExtractor;

		RouteType(Function<Method, Mapping> mappingExtractor) {
			this.mappingExtractor = mappingExtractor;
		}

		public Route getRoute(Object controller, Method method) {
			Mapping mapping = mappingExtractor.apply(method);
			if (mapping == null) return null;
			return Route.getRoute(mapping.path(), mapping.permissions(), this, controller, method);
		}
	}
}
