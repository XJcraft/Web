package org.jim.xj.service;

import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.WebAppResourceLoader;
import org.nutz.ioc.loader.annotation.IocBean;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Created by jimliang on 2016/2/24 0024.
 */
@IocBean
public class TemplateService {

    GroupTemplate groupTemplate;

    public TemplateService() throws IOException {
        groupTemplate = new GroupTemplate(new WebAppResourceLoader(), Configuration.defaultConfiguration());
    }

    public void render(ServletResponse resp, String template, Map data) throws IOException {
        Template temp =  groupTemplate.getTemplate("WEB-INF/"+template+".btl");
        temp.binding(data);
        temp.renderTo(resp.getOutputStream());
    }
}
