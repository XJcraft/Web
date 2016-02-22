package org.jim.xj.filter;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jim.xj.AppContext;
import org.jim.xj.service.SkinService;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.view.RawView;

public class SkinPathFilter implements Filter {

	private static final String URLSKIN = "/SkinPath/(\\S+).png";
	private Pattern pattern;
	private SkinService skinService;
	private static final Log log = Logs.get();
	private static RawView view = new RawView("png");

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.pattern = Pattern.compile(URLSKIN, 2);

	}

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		String url = req.getPathInfo();
		if (url == null)
			url = req.getServletPath();
		Matcher m = pattern.matcher(url);
		if (m.find()) {
			if (skinService == null) {
				skinService = AppContext.getIoc().get(SkinService.class);
			}
			try {
				view.render(req, resp, skinService.read(m.group(1)));
			} catch (Throwable e) {
				log.error("skin "+ m.group(1)+" not found!",e);
				resp.sendError(404);
			}
		} else {
			chain.doFilter(request, response);
		}
	}

}
