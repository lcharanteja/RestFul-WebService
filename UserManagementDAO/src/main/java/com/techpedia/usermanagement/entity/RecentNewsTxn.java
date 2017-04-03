
package com.techpedia.usermanagement.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author charan.teja
 *
 */
@Entity
@Table(name="college_recent_news")
public class RecentNewsTxn implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6799408723338067178L;

	@Id
	@GeneratedValue
	@Column (name ="NEWS_DATE")
	private String newsDate;
	
	@Column(name="COLG_ID")
	private long collegeId;
	
	@Column(name="NEWS_HEADLINE")
	private String newsHeadline;
	
	@Column(name="NEWS_DESCRIPTION")
	private String newsDescription;

	public String getNewsDate() {
		return newsDate;
	}

	public void setNewsDate(String newsDate) {
		this.newsDate = newsDate;
	}

	public long getCollegeId() {
		return collegeId;
	}

	public void setCollegeId(long collegeId) {
		this.collegeId = collegeId;
	}

	public String getNewsHeadline() {
		return newsHeadline;
	}

	public void setNewsHeadline(String newsHeadline) {
		this.newsHeadline = newsHeadline;
	}

	public String getNewsDescription() {
		return newsDescription;
	}

	public void setNewsDescription(String newsDescription) {
		this.newsDescription = newsDescription;
	}
	
}
