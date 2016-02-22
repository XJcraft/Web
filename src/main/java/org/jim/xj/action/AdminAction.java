package org.jim.xj.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jim.xj.AppContext;
import org.jim.xj.ajax.AjaxReturn;
import org.jim.xj.bean.User;
import org.jim.xj.bean.form.PageForm;
import org.jim.xj.bean.form.UserForm;
import org.jim.xj.filter.AdminSession;
import org.jim.xj.service.UserService;
import org.jim.xj.util.ObjectPairAdaptor;
import org.nutz.castor.Castors;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.lang.util.Context;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.adaptor.VoidAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.By;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;

@At("/api")
@IocBean
@Filters(@By(type=AdminSession.class))
public class AdminAction extends BaseAction {
	
	@Inject
	private UserService userService;

	@At("/user/admin")
	@GET
	public Object userList(@Param("..") PageForm form){
		return userService.queryAll(form.create().create());
	}
	
	@At("/user/admin")
	@POST
	@AdaptBy(type=ObjectPairAdaptor.class)
	public Object userUpdate(@Param("::node.") HashMap map){
		List<User> us =new ArrayList<User>();
		System.out.println(Json.toJson(map));
		NutMap nMap = NutMap.WRAP(map);
		for(UserForm form : nMap.getList("data", UserForm.class)){
			User u = userService.fetch(form.get_id());
			if(form.getRole()!=null){
				u.setRole(form.getRole());
			}
			if(form.isLocked()!=null){
				u.setLocked(form.isLocked());
			}
			try{
				userService.update(u);
				us.add(u);
				if(u.isLocked()){
					// TODO 强制下线
					u.setRole(0);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return us;
	}
	
	@At("/index/admin")
	@GET
	public Object adminGlobal(){
		return  AppContext.getConfig().getInnerMap();
	}
	
	@At("/index/admin")
	@POST
	@AdaptBy(type=VoidAdaptor.class)
	public Object adminGlobalSubmit(HttpServletRequest request){
		Context context = AppContext.getConfig();
		Castors c = Castors.me();
		for(Object object : request.getParameterMap().entrySet()){
			Map.Entry<String, String[]> entry = (Map.Entry<String, String[]>) object;
			if(context.has(entry.getKey()) && !Lang.isEmpty(entry.getValue())){
				Object oldValue = context.get(entry.getKey());
				context.set(entry.getKey(), c.castTo(entry.getValue()[0], oldValue.getClass()));
			}
		}
		return context.getInnerMap();
	}
}
