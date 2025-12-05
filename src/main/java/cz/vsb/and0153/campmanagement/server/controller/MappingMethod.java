package cz.vsb.and0153.campmanagement.server.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record MappingMethod(Object controller, Method method, String servletPath, Pattern regexPath) {
	private static final Pattern REGEX_SYMBOL = Pattern.compile("[.^$*+?()\\[\\]{}\\\\|]");

	public boolean invoke(HttpServletRequest req, HttpServletResponse resp) throws InvocationTargetException, IllegalAccessException {
		// Request URI must match the path pattern
		Matcher matcher = regexPath.matcher(req.getRequestURI().substring(req.getContextPath().length()));
		if (!matcher.matches()) return false;

		if (method.getParameterCount() == 2) {
			method.invoke(controller, req, resp);
		} else {
			method.invoke(controller, req, resp, matcher);
		}

		return true;
	}

	private static String toServletPath(String pathPattern) {
		if ("/".equals(pathPattern)) return ""; // Slash is special character `FOR ALL`, so we use empty string

		String[] segments = pathPattern.split("/"); // Split by slash to segments
		StringBuilder pathBuilder = new StringBuilder();

		// Skip the first segment as it's always empty
		for (int i = 1; i < segments.length; i++) {
			String segment = segments[i];
			pathBuilder.append("/");

			// If the segment contains a regex symbol, use wildcard
			if (REGEX_SYMBOL.matcher(segment).find()) {
				pathBuilder.append("*");
				break;
			} else if (!segment.isEmpty()) {
				pathBuilder.append(segment);
			}
		}

		return pathBuilder.toString();
	}

	private static Pattern toRegexPath(String pathPattern) {
		return Pattern.compile(String.format("^%s$", pathPattern));
	}

	private static MappingMethod getMapping(Object controller, Method method, String path) {
		Class<?>[] parameterTypes = method.getParameterTypes();

		// Method needs to take HttpServletRequest and HttpServletResponse as parameters
		// And optionally a Matcher as a third parameter
		if (
			parameterTypes.length < 2 || parameterTypes.length > 3 ||
				!HttpServletRequest.class.isAssignableFrom(parameterTypes[0]) ||
				!HttpServletResponse.class.isAssignableFrom(parameterTypes[1]) ||
				(parameterTypes.length == 3 && !Matcher.class.isAssignableFrom(parameterTypes[2]))
		) return null;

		// Add slash to the beginning if not exists
		if (!path.startsWith("/")) path = String.format("/%s", path);

		return new MappingMethod(controller, method, toServletPath(path), toRegexPath(path));
	}

	public enum MethodType {
		GET, POST;

		public MappingMethod getMapping(Object controller, Method method) {
			String path = switch (this) {
				case GET -> {
					GetMapping mapping = method.getAnnotation(GetMapping.class);
					if (mapping == null) yield null;
					yield mapping.value();
				}
				case POST -> {
					PostMapping mapping = method.getAnnotation(PostMapping.class);
					if (mapping == null) yield null;
					yield mapping.value();
				}
			};
			if (path == null) return null;

			return MappingMethod.getMapping(controller, method, path);
		}
	}
}
