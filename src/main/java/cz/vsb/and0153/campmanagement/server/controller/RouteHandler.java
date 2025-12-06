package cz.vsb.and0153.campmanagement.server.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.regex.Matcher;

@FunctionalInterface
public interface RouteHandler {
	@SuppressWarnings("java:S112") // Allow throwing any exception
	void apply(HttpServletRequest request, HttpServletResponse response, Matcher matcher) throws Exception;
}
