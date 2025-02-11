package gov.nist.hit.auth.core.service;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;
import java.io.InputStream;

public class DefaultOAuth2AuthenticationFailureHandler  implements AuthenticationFailureHandler {

	private final String errorPage;

	public DefaultOAuth2AuthenticationFailureHandler(String errorPage) {
		this.errorPage = errorPage;
	}

	public DefaultOAuth2AuthenticationFailureHandler() {
		this.errorPage = "/default_oauth2_authentication_error.html";
	}

	@Override
	public void onAuthenticationFailure(
			HttpServletRequest request,
			HttpServletResponse response,
			AuthenticationException exception
	) throws IOException {
		InputStream defaultErrorPage = DefaultOAuth2AuthenticationFailureHandler.class.getResourceAsStream(this.errorPage);
		if(defaultErrorPage != null) {
			response.setContentType("text/html;charset=UTF-8");
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			IOUtils.copy(defaultErrorPage, response.getOutputStream());
		} else {
			response.sendRedirect(request.getContextPath() + "/error?login");
		}
	}
}
