package uk.co.probablyfine.aoko.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class YoutubeDownload {
	
	@Id
	@GeneratedValue
	private int id;
	
	@Column(nullable=false)
	private String url;
	
	@Column(nullable=false)
	@Enumerated(EnumType.STRING)
	private DownloadState state;
	
	@Column(nullable = false)
	private String queuedBy;
	
	public YoutubeDownload() {}
	
	public YoutubeDownload(String url) {
		this.url = url;
		this.state = DownloadState.WAITING;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public DownloadState getState() {
		return state;
	}

	public void setState(DownloadState state) {
		this.state = state;
	}

	public void setQueuedBy(String queuedBy) {
		this.queuedBy = queuedBy;
	}

	public String getQueuedBy() {
		return queuedBy;
	}

	
}
