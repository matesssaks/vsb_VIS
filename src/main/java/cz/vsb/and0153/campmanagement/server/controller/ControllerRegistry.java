package cz.vsb.and0153.campmanagement.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ControllerRegistry {

	private final ApplicationContext applicationContext;

	public void registerControllers(Context context) {
		Collection<Object> controllers = applicationContext.getBeansWithAnnotation(Controller.class).values();

		Map<String, ControllerServlet.ControllerServletBuilder> servlets = new HashMap<>();

		controllers.forEach(controller -> {
			Class<?> controllerClass = controller.getClass();

			for (Method method : controllerClass.getMethods()) {
				GetMapping getMapping = method.getAnnotation(GetMapping.class);
				if (getMapping != null && getMapping.value().length > 0) {
					String path = getMapping.value()[0];
					servlets.putIfAbsent(path, ControllerServlet.builder());
					servlets.get(path).getMethod(new MappingMethod(controller, method));

					continue;
				}

				PostMapping postMapping = method.getAnnotation(PostMapping.class);
				if (postMapping != null && postMapping.value().length > 0) {
					String path = postMapping.value()[0];
					servlets.putIfAbsent(path, ControllerServlet.builder());
					servlets.get(path).postMethod(new MappingMethod(controller, method));
				}
			}
		});

		servlets.forEach((path, builder) -> {
			ControllerServlet servlet = builder.build();

			log.debug("Registering servlet '{}' for path '{}'", servlet, path);

			Tomcat.addServlet(context, servlet.toString(), servlet);
			context.addServletMappingDecoded(path, servlet.toString());
		});
	}
}
