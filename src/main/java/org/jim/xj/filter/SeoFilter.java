package org.jim.xj.filter;

import org.jim.xj.AppContext;
import org.jim.xj.bean.Article;
import org.jim.xj.service.ArticleService;
import org.jim.xj.service.RobotService;
import org.jim.xj.service.TemplateService;
import org.jim.xj.util.Pagination;
import org.nutz.lang.util.Context;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by jimliang on 2016/2/24 0024.
 */
public class SeoFilter implements Filter {

    private Pattern indexPattern = Pattern.compile("^\\/$|^\\/index", Pattern.CASE_INSENSITIVE);
    private Pattern articlePattern = Pattern.compile("^\\/A\\S+", Pattern.CASE_INSENSITIVE);
    private Pattern sitemapPattern = Pattern.compile("^\\/sitemap.xml|^\\/sitmap.xml", Pattern.CASE_INSENSITIVE);

    private static final Log log = Logs.get();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {


    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        String ua = req.getHeader("User-Agent");
        String url = req.getPathInfo();
        if (url == null)
            url = req.getServletPath();

        if (sitemapPattern.matcher(url).find()) {
            Context global = AppContext.getConfig();
            List<Map> sitemap = new LinkedList<>();

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            for (Article article : AppContext.getIoc().get(ArticleService.class).hot(new Pagination(20, 1))) {
                sitemap.add(new NutMap()
                        .setv("url", global.get("url") + article.get_id())
                        .setv("date", df.format(new Date(article.getDate())))
                        .setv("freq", "daily")// always hourly daily weekly monthly yearly never
                        .setv("priority", 0.8)
                );
            }
            response.setContentType("application/xml");
            AppContext.getIoc().get(TemplateService.class).render(response, "seo/sitemap", new NutMap().setv("sitemap", sitemap));
        }

        if (!AppContext.getIoc().get(RobotService.class).isRobot(ua)) {
            chain.doFilter(request, response);
            return;
        }

        log.info("robot find: " + ua + "-->" + url);

        try {
            doIt(url, request, response, chain);
        } catch (Exception e) {
            e.printStackTrace();
            chain.doFilter(request, response);
        }
    }

    private void doIt(String url, ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (indexPattern.matcher(url).find()) {
            NutMap map = new NutMap()
                    .setv("global", AppContext.getConfig())
                    .setv("articles", AppContext.getIoc().get(ArticleService.class).hot(new Pagination(20, 1)));
            response.setContentType("text/html");
            AppContext.getIoc().get(TemplateService.class).render(response, "seo/robotIndex", map);

        } else if (articlePattern.matcher(url).find()) {
            Article article = AppContext.getIoc().get(ArticleService.class).detail(url.substring(1), new Pagination(20, 1));
            if (article == null) {
                chain.doFilter(request, response);
                return;
            }
            NutMap map = new NutMap()
                    .setv("global", AppContext.getConfig())
                    .setv("article", article);
            response.setContentType("text/html");
            AppContext.getIoc().get(TemplateService.class).render(response, "seo/robotArticle", map);
        }
    }

    @Override
    public void destroy() {

    }
}
