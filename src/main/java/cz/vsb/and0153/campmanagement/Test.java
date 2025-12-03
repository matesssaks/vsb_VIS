package cz.vsb.and0153.campmanagement;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.io.PrintWriter;

@Controller
public class Test {

	@GetMapping("/test")
	public void test(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/plain;charset=UTF-8");
		try (PrintWriter writer = resp.getWriter()) {
			writer.write("Hello World!");
		}
	}
}
