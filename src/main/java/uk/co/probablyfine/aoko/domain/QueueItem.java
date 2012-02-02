package uk.co.probablyfine.aoko.domain;

import javax.persistence.Entity;

@Entity
public class QueueItem implements Comparable<QueueItem>{

	private int id;
	private int bucket;
	private int position;
	private MusicFile file;
	private User user;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public int getBucket() {
		return bucket;
	}

	public void setBucket(int bucket) {
		this.bucket = bucket;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public MusicFile getFile() {
		return file;
	}

	public void setFile(MusicFile file) {
		this.file = file;
	}

	public int compareTo(QueueItem arg0) {
		return -1;
	}

	
}
