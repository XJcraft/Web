import org.jim.xj.AppContext;
import org.jim.xj.service.RobotService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

/**
 * Created by jimli on 2016/2/23 0023.
 */
public class RobotTest {

    RobotService robotService;

    @Before
    public void setUp() {
        robotService = AppContext.getIoc().get(RobotService.class);
    }

    @Test
    public void testUserAgent() {
        String[] robots = {
                "Mozilla/5.0 (compatible; googlebot/2.1; +http://www.google.com/bot.html)",
                "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)",
                "Googlebot/2.1 (+http://www.googlebot.com/bot.html)",
                "Mozilla/5.0 (compatible; Yahoo! Slurp China; http://misc.yahoo.com.cn/help.html”)",
                "iaskspider/2.0(+http://iask.com/help/help_index.html”)",
                "Mozilla/5.0 (compatible; YodaoBot/1.0; http://www.yodao.com/help/webmaster/spider/”; )  ",
                "msnbot/1.0 (+http://search.msn.com/msnbot.htm”)",
                "Sogou web spider/3.0(+http://www.sogou.com/docs/help/webmasters.htm#07",
        };
        String[] notRobots = {
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.106 Safari/537.36",
                "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:45.0) Gecko/20100101 Firefox/45.0"
        };
        for (String userAgent : robots) {
            Assert.assertTrue(userAgent, robotService.isRobot(userAgent));
        }
        for (String userAgent : notRobots) {
            Assert.assertFalse(userAgent, robotService.isRobot(userAgent));
        }
    }

}
