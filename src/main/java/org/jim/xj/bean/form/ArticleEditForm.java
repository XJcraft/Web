package org.jim.xj.bean.form;

public class ArticleEditForm {

	private String title;
	private String content;
	private String refer;
	private String tagsList;
	private String replyToComment;
	private String source;
	
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getRefer() {
		return refer;
	}
	public void setRefer(String refer) {
		this.refer = refer;
	}
	public String getTagsList() {
		return tagsList;
	}
	public void setTagsList(String tagsList) {
		this.tagsList = tagsList;
	}
	public String getReplyToComment() {
		return replyToComment;
	}
	public void setReplyToComment(String replyToComment) {
		this.replyToComment = replyToComment;
	}
	
	
}
