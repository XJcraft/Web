package org.jim.xj.ajax;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.JsonFormat;
import org.nutz.lang.Strings;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.View;

public class AjaxView implements View {

    private boolean uc;

    public AjaxView() {
        uc = true;
    }

    public AjaxView(String useCompact) {
        uc = Strings.isBlank(useCompact) ? true : Boolean.parseBoolean(useCompact);
    }

    public JsonFormat getJsonFormat() {
        if (uc) {
            // 紧凑模式(生产)
            return JsonFormat.compact();
        } else {
            // 一般模式(开发)
            return JsonFormat.nice();
        }
    }

    @Override
    public void render(HttpServletRequest req, HttpServletResponse resp, Object obj)
            throws IOException {
        AjaxReturn re;
        // 空
        if (null == obj) {
            re = AjaxReturn.ok(null);
        }
        // 异常
        else if (obj instanceof Throwable) {
        	re = AjaxReturn.error((Throwable)obj);
        }
        // AjaxReturn
        else if (obj instanceof AjaxReturn) {
            re = (AjaxReturn) obj;
        }
        // 数据对象
        else {
            re = AjaxReturn.ok(obj);
        }

        // 写入返回
        Mvcs.write(resp, re, getJsonFormat());
    }

}
