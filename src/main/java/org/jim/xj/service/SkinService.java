package org.jim.xj.service;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.jim.xj.bean.User;
import org.jim.xj.skin.SkinValue;

public interface SkinService {

	public BufferedImage skin(final String userId) throws Exception;

	public BufferedImage cloak(final String userId) throws Exception;

	public BufferedImage read(String path) throws IOException;

	public BufferedImage head(final String uid) throws Exception;
	/**
	 * 获取皮肤属性
	 * @param user
	 * @return
	 */
	public SkinValue getSkinValue(final User user);

	public SkinValue getOfflineSkin(String playerId);

	public void updateSkin(User user, String md5) throws IOException;

	public void updateCloak(User user, String md5) throws IOException;

	public void updateOnlineSkin(User user,String name);
	
	public void deleteCloak(String name);

	public void deleteSkin(String name);

}
