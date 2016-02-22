package org.jim.xj.bean;

import java.io.Serializable;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.json.JsonField;
import org.nutz.lang.Strings;

@Table("xj_user")
public class User implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5659681329981812776L;
	@Name
	private String _id;
	@Column
	private String name;
	@Column
	@JsonField("nickname")
	private String nickName;
	@Column
	private String social;
	@Column
	private String email;
	@Column
	private String avatar;
	@Column
	@JsonField(ignore=true)
	@ColDefine(width=500)
	private String passwd;
	@Column
	@JsonField(ignore=true)
	private String resetKey;
	@Column
	@JsonField(ignore=true)
	private Long resetDate;
	@Column
	@JsonField(ignore=true)
	private Integer loginAttempts;
	@Column
	private boolean locked;
	@Column
	private String sex;
	@Column
	private Integer role;
	@Column
	private String descp;
	@Column
	private Integer score = 0;
	@Column
	private Long readtimestamp = 0l;
	@Column
	private Long lastLoginDate;
	@Column
	private String qq;
	@Column
	private Long date;
	@Column
	//@JsonField(ignore=true)
	private String playerId;
	@Column
	private String playerSkin;
	
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getSocial() {
		return social;
	}
	public void setSocial(String social) {
		this.social = social;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public String getPasswd() {
		return passwd;
	}
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	public String getResetKey() {
		return resetKey;
	}
	public void setResetKey(String resetKey) {
		this.resetKey = resetKey;
	}
	
	public Long getResetDate() {
		return resetDate;
	}
	public void setResetDate(Long resetDate) {
		this.resetDate = resetDate;
	}
	public Integer getLoginAttempts() {
		return loginAttempts;
	}
	public void setLoginAttempts(Integer loginAttempts) {
		this.loginAttempts = loginAttempts;
	}
	public boolean isLocked() {
		return locked;
	}
	public void setLocked(boolean locked) {
		this.locked = locked;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public Integer getRole() {
		return role;
	}
	public void setRole(Integer role) {
		this.role = role;
	}
	
	public String getDescp() {
		return descp;
	}
	public void setDescp(String descp) {
		this.descp = descp;
	}
	public Integer getScore() {
		return score;
	}
	public void setScore(Integer score) {
		this.score = score;
	}
	public Long getReadtimestamp() {
		return readtimestamp;
	}
	public void setReadtimestamp(Long readtimestamp) {
		this.readtimestamp = readtimestamp;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getLastLoginDate() {
		return lastLoginDate;
	}
	public void setLastLoginDate(Long lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}
	public String getQq() {
		return qq;
	}
	public void setQq(String qq) {
		this.qq = qq;
	}
	public Long getDate() {
		return date;
	}
	public void setDate(Long date) {
		this.date = date;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_id == null) ? 0 : _id.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (_id == null) {
			if (other._id != null)
				return false;
		} else if (!_id.equals(other._id))
			return false;
		return true;
	}
	public String getPlayerId() {
		return playerId;
	}
	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}
	public boolean isEditor(){
		return role>=4;
	}
	public String getPlayerSkin() {
		return playerSkin;
	}
	public void setPlayerSkin(String playerSkin) {
		this.playerSkin = playerSkin;
	}
	public boolean useOnlineSkin(){
		return !Strings.isEmpty(getPlayerSkin());
	}
	@Override
	public String toString() {
		return "User [_id=" + _id + ", name=" + name + "]";
	}
	
}
