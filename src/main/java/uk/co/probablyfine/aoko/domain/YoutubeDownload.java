package uk.co.probablyfine.aoko.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class YoutubeDownload implements Comparable<YoutubeDownload> {
	
	@Id
	@GeneratedValue
	private int id;
	
	@Column(nullable=false)
	private String url;
	
	@Column(nullable=false)
	@Enumerated(EnumType.STRING)
	private DownloadState state;
	
	@Column(nullable = false)
	private int bucket;
	
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

	public int getBucket() {
		return bucket;
	}

	public void setBucket(int bucket) {
		this.bucket = bucket;
	}

	@Override
	public int compareTo(YoutubeDownload arg0) {
		if (arg0.getBucket() < this.getBucket()) {
			return 1;
		} else if (arg0.getBucket() > this.getBucket()) {
			return -1;
		} else if (arg0.getId() < this.getId()) {
			return 1;
		} else if (arg0.getId() > this.getId()) {
			return -1;
		} else {
			return 0;
		}
	}
	
	@Override
	public String toString() {
		return String.format("Id: %s, Bucket: %d, Id: %d", this.url,this.bucket,this.id);
	}

	
}
