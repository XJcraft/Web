package org.jim.xj.util;

import java.lang.reflect.Type;
import java.util.Iterator;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.mapl.Mapl;
import org.nutz.mvc.adaptor.AbstractAdaptor;
import org.nutz.mvc.adaptor.ParamExtractor;
import org.nutz.mvc.adaptor.ParamInjector;
import org.nutz.mvc.adaptor.Params;
import org.nutz.mvc.adaptor.injector.ObjectNaviNode;
import org.nutz.mvc.annotation.Param;

public class ObjectPairAdaptor extends AbstractAdaptor {

	@Override
	protected ParamInjector evalInjectorBy(Type type, Param param) {
		String pm = param.value();
		if ((pm.startsWith("::")) && (pm.length() >= 2)) {
		      return new ObjectNavlPairInjector(pm.substring(2), type);
		    }

		return null;
	}

	class ObjectNavlPairInjector implements ParamInjector {
		protected Mirror<?> mirror;
		private String prefix;
		private Type type;

		public ObjectNavlPairInjector(String prefix, Type type) {
			prefix = Strings.isBlank(prefix) ? "" : Strings.trim(prefix);
			this.prefix = prefix;
			this.mirror = Mirror.me(type);
			this.type = type;
		}

		public Object get(ServletContext sc, HttpServletRequest req,
				HttpServletResponse resp, Object refer) {
			ObjectNaviNode no = new ObjectNaviNode();
			if ("".equals(this.prefix))
				prefix = "node.";
			ParamExtractor pe = Params.makeParamExtractor(req, refer);
			for (Iterator localIterator = pe.keys().iterator(); localIterator
					.hasNext();) {
				Object name = localIterator.next();
				String na = (String) name;
				no.put(prefix + na, pe.extractor(na));
			}
			Object model = no.get();
			Object re = Mapl.maplistToObj(model, this.type);
			return re;
		}
	}

}
