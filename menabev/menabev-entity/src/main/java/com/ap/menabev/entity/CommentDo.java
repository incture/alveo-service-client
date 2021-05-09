package com.ap.menabev.entity;



import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "COMMENT")
public @Data class CommentDo {
	
	@Id
	@Column(name="COMMENT_ID")
	private String commentId;
	
	@Column(name = "REQUEST_ID",nullable=false)
	private String requestId;
	
	@Column(name="COMMENT")
	private String comment;
	
	@Column(name="CREATED_BY")
	private String createdBy;
	
	@Column(name="CREATED_AT")
	private Long createdAt;
	
	@Column(name="UPDATED_BY")
	private String updatedBy;
	
	@Column(name="UPDATED_AT")
	private Long updatedAt;
	
	@Column(name="USER",nullable=false)
	private String user;
}
