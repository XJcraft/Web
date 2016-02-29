package org.jim.xj.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;

import org.jim.xj.AppContext;
import org.jim.xj.action.BaseAction;
import org.jim.xj.bean.Skin;
import org.jim.xj.bean.User;
import org.jim.xj.exception.SkinNotFoundException;
import org.jim.xj.skin.SkinLoadObjectImpl;
import org.jim.xj.skin.SkinUtil;
import org.jim.xj.skin.SkinValue;
import org.jim.xj.util.Assert;
import org.jim.xj.util.LRUCache;
import org.jim.xj.util.LazyLoadObject;
import org.jim.xj.util.MvcUtil;
import org.jim.xj.util.Skinmes;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.FieldFilter;
import org.nutz.json.Json;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.lang.util.Disks;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.repo.Base64;
import org.nutz.service.NameEntityService;
import org.nutz.trans.Atom;

import skinschanger.libs.com.mojang.api.profiles.Profile;
import skinschanger.shared.format.SkinProperty;
import skinschanger.shared.utils.DataUtils;

public class SkinServiceImpl2 extends NameEntityService<Skin>implements SkinService {

	private static Log log = Logs.get();
	private File home;
	private File temp;
	private File head;
	private LRUCache<String, LazyLoadObject> imageCache = new LRUCache<String, LazyLoadObject>(300);
	private LRUCache<String, LazyLoadObject> skinValueCache = new LRUCache<String, LazyLoadObject>(300);

	private ExecutorService executor = Executors.newFixedThreadPool(1);

	public SkinServiceImpl2(Dao dao, String path) {
		super(dao);
		home = new File(Disks.normalize(path));
		log.info("skin home: " + home.getAbsolutePath());
		if (!home.exists())
			home.mkdirs();
		temp = new File(home, ".temp");
		head = new File(home, ".head");
		Files.deleteDir(temp);
		temp.mkdirs();
		head.mkdirs();
	}

	public void fixAvatarsAsync() {
		executor.submit(new Runnable() {

			@Override
			public void run() {
				fixAvatars();
			}
		});
	}

	private void fixAvatars() {
		dao().each(User.class, Cnd.where("avatar", "is", null), new Each<User>() {

			@Override
			public void invoke(int index, User ele, int length) throws ExitLoop, ContinueLoop, LoopException {
				_generateAvatar(ele);
			}
		});
	}

	protected String write(File folder, BufferedImage image) throws IOException {
		String fileName = R.UU16() + ".png";
		File file = new File(temp, fileName);
		ImageIO.write(image, "PNG", file);
		String md5 = Lang.md5(file);
		fileName = md5 + ".png";
		File newFile = new File(folder, fileName);
		if (!newFile.exists()) {
			Files.copyFile(file, newFile);
			log.infof("Copy %s --> %s", file, newFile);
		} else {
			log.infof("%s exist", newFile);
		}
		return md5;
	}

	protected Skin profile(String playerId, String type, boolean readImage) throws IOException {
		Skin skin = fetch(Cnd.where("name", "=", playerId).and("type", "=", type));
		if (skin == null) {
			skin = new Skin();
			skin.set_id(BaseAction.generateId("S"));
			// skin.setUuid(name);
			skin.setName(playerId);
			skin.setType(type);
			BufferedImage img = null;
			try {
				img = Strings.equals(type, "skin") ? Skinmes.skin(playerId) : Skinmes.cloak(playerId);
				skin.setFilePath(write(home, img));
				skin.setImage(img);
			} catch (Exception e) {
				log.info(playerId + "没有皮肤就算了");
			}
			dao().insert(skin);
			return skin;
		} else {
			if (readImage)
				skin.setImage(read(home, skin.getFilePath()));
		}
		skin.setUrl(MvcUtil.basePath() + "SkinPath/" + skin.getFilePath() + ".png");
		return skin;
	}

	public BufferedImage head(final String uid) throws Exception {
		Skin skin = profile(uid, "skin", false);
		if (skin.getFilePath() == null) {
			// TODO 没有皮肤，用史蒂夫
			return head("steve");
		} else {
			// 先看一下头像缓存
			BufferedImage headImg = read(head, skin.getFilePath());
			if (headImg == null) {
				BufferedImage playerImg = read(home, skin.getFilePath());
				if (playerImg != null) {
					headImg = head(playerImg);
					ImageIO.write(headImg, "png", new File(head, skin.getFilePath() + ".png"));
				}
			}
			return headImg;
		}
	}

	public BufferedImage read(File folder, String path) throws IOException {
		String key = folder.getName() + path;
		LazyLoadObject l = imageCache.get(key);
		if (l == null) {
			synchronized (imageCache) {
				final File file = new File(folder, path + ".png");
				l = new LazyLoadObject() {

					@Override
					protected Object load() {
						if (file.exists())
							try {
								return ImageIO.read(file);
							} catch (IOException e) {
								e.printStackTrace();
							}
						return null;
					}
				};
				imageCache.put(key, l);
			}
		}
		return l.getData();
	}

	private BufferedImage head(BufferedImage image) throws Exception {
		return SkinUtil.head(image);
	}

	@Override
	public BufferedImage skin(String userId) throws Exception {
		Skin skin = profile(userId, "skin", true);
		if (skin == null || skin.getImage() == null) throw new SkinNotFoundException();
		return skin.getImage();
	}

    @Override
    public BufferedImage cloak(String userId) throws Exception {
        Skin skin = profile(userId, "cloak", true);
        if (skin == null || skin.getImage() == null) throw new SkinNotFoundException();
        return skin.getImage();
    }

	@Override
	public BufferedImage read(String path) throws IOException {
		return read(home, path);
	}

	@Override
	public SkinValue getSkinValue(User user) {
		LazyLoadObject obj = skinValueCache.get(user.getPlayerId());
		if (obj == null) {
			obj = new SkinLoadObjectImpl(dao(), this, user.getNickName());
			skinValueCache.put(user.getPlayerId(), obj);
		}
		return obj.getData();
	}

	@Override
	public SkinValue getOfflineSkin(String playerId) {
		SkinValue skinValue = new SkinValue();
		try {
			Skin skin = profile(playerId, "skin", false);
			if (skin != null && skin.getFilePath() != null)
				skinValue.setSkin(skin);
			Skin cloak = profile(playerId, "cloak", false);
			if (cloak != null && cloak.getFilePath() != null)
				skinValue.setCloak(cloak);
		} catch (IOException e) {
			log.warn("getOfflineSkin error", e);
		}
		return skinValue;
	}

	@Override
	public void updateSkin(User user, String md5) throws IOException {
		addSkinFile(md5, new File(getUploadDir() + File.separator + md5 + ".png"));
		Skin skin = profile(user.getPlayerId(), "skin", false);
		skin.setFilePath(md5);
		dao().update(skin);
		skinValueCache.remove(user.getPlayerId());
		generateAvatar(user);
	}

	@Override
	public void updateCloak(User user, String md5) throws IOException {
		addSkinFile(md5, new File(getUploadDir() + File.separator + md5 + ".png"));
		Skin skin = profile(user.getPlayerId(), "cloak", false);
		skin.setFilePath(md5);
		dao().update(skin);
		skinValueCache.remove(user.getPlayerId());
	}

	@Override
	public void deleteCloak(String name) {
		Skin skin = null;
		try {
			skin = profile(name, "cloak", false);
		} catch (IOException e) {
			throw Lang.wrapThrow(e);
		}
		if (skin != null) {
			skin.setFilePath(null);
			dao().update(skin);
		}

	}

	@Override
	public void deleteSkin(String name) {
		Skin skin = null;
		try {
			skin = profile(name, "skin", false);
		} catch (IOException e) {
			throw Lang.wrapThrow(e);
		}
		if (skin != null) {
			skin.setFilePath(null);
			dao().update(skin);
			generateAvatar(AppContext.getIoc().get(UserService.class).getUser(name));
		}
	}

	public String getUploadDir() {
		return Mvcs.getServletContext().getRealPath("/upload/");
	}

	public void addSkinFile(String md5, File file) throws IOException {
		Assert.isTrue(file.exists(), file.getAbsolutePath() + "不存在！");
		File f = new File(home, md5 + ".png");
		if (f.exists()) {
			return;
		}
		Files.copyFile(file, f);
	}

	@SuppressWarnings("rawtypes")
	private String getOnlineSkin(String playerId) throws Exception {
		Profile p = DataUtils.getProfile(playerId);
		SkinProperty s = DataUtils.getProp(p.getId());
		String json = new String(Base64.decode(s.getValue()), "UTF-8");
		Map<String, Object> map = Json.fromJsonAsMap(Object.class, json);
		Map textures = (Map) map.get("textures");
		Map skin = (Map) textures.get("SKIN");
		Assert.notNull(skin, "Skin not found!");
		String url = skin.get("url").toString();
		return url;
	}

	public void generateAvatar(final User user) {
		executor.submit(new Runnable() {

			@Override
			public void run() {
				_generateAvatar(user);
			}
		});
	}

	private void _generateAvatar(final User user) {
		boolean haveOld = user.getAvatar() != null;
		try {
			if (user.useOnlineSkin()) {
				generateAvatarOnline(user);
			} else {
				generateAvatarOffline(user);
			}

		} catch (Exception e) {
			// user.setAvatar(MvcUtil.host() + "avatar/steve.png");
		}
		boolean have = user.getAvatar() != null;
		if ((haveOld && !have) || !haveOld && have)
			avatarFilter.run(new Atom() {
				@Override
				public void run() {
					AppContext.getIoc().get(UserService.class).update(user);
				}
			});
	}

	private void generateAvatarOnline(User user) throws Exception {
		try {
			String url = getOnlineSkin(user.getPlayerSkin());
			BufferedImage image = ImageIO.read(new URL(url));
			BufferedImage head = head(image);
			String md5 = write(MvcUtil.webFile("/avatar"), head);
			user.setAvatar(MvcUtil.host() + "avatar/" + md5 + ".png");
		} catch (Exception e) {
			log.error("在线头像处理出错了!", e);
			throw e;
		}
	}

	private void generateAvatarOffline(User user) throws Exception {
		Skin skin = profile(user.getPlayerId(), "skin", true);
		if (skin == null || skin.getImage() == null) {
			throw new SkinNotFoundException();
		}
		try {
			BufferedImage head = head(skin.getImage());
			String md5 = write(MvcUtil.webFile("/avatar"), head);
			user.setAvatar(MvcUtil.host() + "avatar/" + md5 + ".png");
			log.info(user.getPlayerId() + " avatar used " + md5);
		} catch (Exception e) {
			log.error("离线头像处理出错了!", e);
			throw e;
		}
	}

	private FieldFilter avatarFilter = FieldFilter.create(User.class, "avatar");
	private FieldFilter playerSkinFilter = FieldFilter.create(User.class, "^playerSkin$");

	@Override
	public void updateOnlineSkin(final User user, final String name) {
		playerSkinFilter.run(new Atom() {
			public void run() {
				user.setPlayerSkin(name);
				AppContext.getIoc().get(UserService.class).update(user);
			}
		});
		generateAvatar(user);
	}
}
