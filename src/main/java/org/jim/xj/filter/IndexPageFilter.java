package org.jim.xj.filter;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class IndexPageFilter implements Filter {

	private static final String IGNORE = "^.+\\.(jsp|png|gif|jpg|js|css|jspx|jpeg|swf|ico|html)$";
	private Pattern ignorePtn;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.ignorePtn = Pattern.compile(IGNORE, 2);

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		String url = req.getPathInfo();
		if (url == null)
			url = req.getServletPath();
		if (url.startsWith("/api") || url.startsWith("/session")) {
			chain.doFilter(request, response);
			return;
		}
		if (url.contains(".") || ignorePtn.matcher(url).find()) {
			chain.doFilter(request, response);
			return;
		}
		request.getRequestDispatcher("/").forward(request, response);
	}

	@Override
	public void destroy() {

	}

}
