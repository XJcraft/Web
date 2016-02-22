package org.jim.xj.skin;

import java.util.HashMap;
import java.util.Map;

import org.jim.xj.AppContext;
import org.jim.xj.bean.User;
import org.jim.xj.service.SkinService;
import org.jim.xj.service.UserService;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import skinschanger.libs.com.mojang.api.profiles.Profile;
import skinschanger.shared.format.SkinProperty;
import skinschanger.shared.utils.DataUtils;


public class SkinLoadObjectImpl extends SkinLoadObject{
	
	private static final Log log = Logs.get();
	private SkinService skinService;

	private String player;
	public SkinLoadObjectImpl(Dao dao,SkinService skinService, String key) {
		super(dao, key);
		this.skinService = skinService;
		player = key;
	}

	
	public User getUser(){
		//return dao().fetch(User.class,Cnd.where("nickName", "=", player));
		return AppContext.getIoc().get(UserService.class).getUser(player);
	}
	@Override
	public String getKey() {
		return player+"_skinValue";
	}


	@Override
	protected Object load() {
		SkinValue skinValue = new SkinValue();
		Map<String, String> textures = new HashMap<String, String>();
		try {
			Profile p = DataUtils.getProfile(getUser().getPlayerSkin());
			SkinProperty s = DataUtils.getProp(p.getId());
			textures.put("value", s.getValue());
			textures.put("signature", s.getSignature());
			skinValue.setTextures(textures);
		} catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
		return skinValue;
	}
	
	/**
	 * 先判断是否使用正版皮肤，否则使用盗版皮肤
	 */
	@SuppressWarnings("unchecked")
	@Override
	public synchronized <T> T getData() {
		T data = null;
		User user = getUser();
		if (!user.useOnlineSkin()) {
			data = (T) skinService.getOfflineSkin(user.getPlayerId());
		} else {
			data = super.getData();
			if (data == null) {
				data = (T) skinService.getOfflineSkin(user.getPlayerId());
			}
		}
		return data;
	}

}
