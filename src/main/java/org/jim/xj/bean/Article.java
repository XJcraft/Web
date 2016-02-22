package org.jim.xj.bean;

import java.io.Serializable;
import java.util.List;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Many;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.One;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.lang.Strings;

@Table("article")
public class Article implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4358021489814988037L;
	@Name
	private String _id;
	@Column
	private String title;
	@Column
	private Long date;
	@Column
	private Long updateTime;
	@Column
	private Integer comments = 0;
	@Column
	private String authorId;
	@Column
	private long visitors =0;// 访问次数
	@Column
	private long hots;
	@Column
	private boolean display;
	@Column
	private Integer status; // 0 暂时未有作用
	// isAuthor
	@Column
	@ColDefine(width = 5000)
	private String content;
	@Column
	private String tags;
	@Column
	private String refer;
	@Column
	private String replyToComment;
	@Column
	private String source;
	@Column
	private Long lastTime;
	@Column
	private String favors;
	@Column
	private String opposes;
	@Column
	private boolean zding;//置顶
	@Column
	private boolean jing;//精华

	@One(target = User.class, field = "authorId")
	private User author;
	@Many(target = Article.class, field = "refer")
	private List<Article> commentsList;


	public boolean isDisplay() {
		return display;
	}

	public void setDisplay(boolean display) {
		this.display = display;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Long getDate() {
		return date;
	}

	public void setDate(Long date) {
		this.date = date;
	}

	public Long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}

	public Integer getComments() {
		return comments;
	}

	public void setComments(Integer comments) {
		this.comments = comments;
	}

	public String getAuthorId() {
		return authorId;
	}

	public void setAuthorId(String authorId) {
		this.authorId = authorId;
	}

	public long getVisitors() {
		return visitors;
	}

	public void setVisitors(long visitors) {
		this.visitors = visitors;
	}

	public long getHots() {
		return hots;
	}

	public void setHots(long hots) {
		this.hots = hots;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}


	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public String getRefer() {
		return refer;
	}

	public void setRefer(String refer) {
		this.refer = refer;
	}

	public String getReplyToComment() {
		return Strings.isEmpty(replyToComment)?null:replyToComment;
	}

	public void setReplyToComment(String replyToComment) {
		this.replyToComment = replyToComment;
	}

	public List<Article> getCommentsList() {
		return commentsList;
	}

	public void setCommentsList(List<Article> commentsList) {
		this.commentsList = commentsList;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Long getLastTime() {
		return lastTime;
	}

	public void setLastTime(Long lastTime) {
		this.lastTime = lastTime;
	}

	public String getFavors() {
		return favors;
	}

	public void setFavors(String favors) {
		this.favors = favors;
	}

	public String getOpposes() {
		return opposes;
	}

	public void setOpposes(String opposes) {
		this.opposes = opposes;
	}

	public boolean isZding() {
		return zding;
	}

	public void setZding(boolean zding) {
		this.zding = zding;
	}

	public boolean isJing() {
		return jing;
	}

	public void setJing(boolean jing) {
		this.jing = jing;
	}
	
}
