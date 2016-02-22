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

import org.jim.xj.service.SkinService;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.view.RawView;

public class SkinFilter implements Filter {

	private static final String URLSKIN = "/Minecraft(Skins|Cloaks)/(\\S+).png";
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
				skinService = Mvcs.ctx.getDefaultIoc().get(SkinService.class);
			}
			try {
				if (Strings.equalsIgnoreCase("Skins", m.group(1)))
					view.render(req, resp, skinService.skin(m.group(2)));
				// ImageIO.write(skinService.skin(m.group(2)), "PNG",
				// response.getOutputStream());
				else {
					view.render(req, resp, skinService.cloak(m.group(2)));
					// ImageIO.write(skinService.cloak(m.group(2)), "PNG",
					// response.getOutputStream());
				}
			} catch (Throwable e) {
				log.infof("%s of %s not found!", m.group(1), m.group(2));
				resp.sendError(404);
			}
		} else {
			chain.doFilter(request, response);
		}
	}

}
