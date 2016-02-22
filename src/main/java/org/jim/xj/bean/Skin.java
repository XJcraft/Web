package org.jim.xj.bean;

import java.awt.image.BufferedImage;
import java.io.Serializable;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.json.JsonField;

@Table("skin")
public class Skin implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2153348542234790124L;
	@Name
	private String _id;
	//@Column
	//private String uuid;//uuid
	@Column
	private String name;
	@Column
	private String filePath;//相对路径,可能为空
	@Column
	private String type;
	
	private String url;
	@JsonField(ignore=true)
	private BufferedImage image;

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
		
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}
	
}
