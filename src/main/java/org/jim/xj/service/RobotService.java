package org.jim.xj.service;

import org.jim.xj.AppContext;
import org.nutz.ioc.loader.annotation.IocBean;

import java.util.regex.Pattern;

/**
 * Created by jimli on 2016/2/23 0023.
 */
@IocBean(create = "onCreate")
public class RobotService {

    private Pattern userAgentPattern;

    public RobotService() {
    }

    public void onCreate() {
        userAgentPattern = Pattern.compile(AppContext.getConstant("robot").getString("userAgentPattern"),Pattern.CASE_INSENSITIVE);
    }

    public boolean isRobot(String userAgent) {
        return userAgentPattern.matcher(userAgent).find();
    }
}
