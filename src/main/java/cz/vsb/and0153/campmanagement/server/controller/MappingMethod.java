package cz.vsb.and0153.campmanagement.server.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public record MappingMethod(Object controller, Method method) {
	public void invoke(HttpServletRequest req, HttpServletResponse resp) throws InvocationTargetException, IllegalAccessException {
		method.invoke(controller, req, resp);
	}
}
