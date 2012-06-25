package uk.co.probablyfine.aoko.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;


@Entity
public class QueueItem implements Comparable<QueueItem>{

	public QueueItem() {
		this.setStatus(PlayerState.QUEUED);
	}
	
	@Id
	@GeneratedValue
	private int id;
	
	private int bucket;
	
	private int position;
	
	@ManyToOne(targetEntity=MusicFile.class,cascade=CascadeType.ALL)
	private MusicFile musicFile;
	
	@Column(nullable = false)
	private String userName;
	
	private PlayerState status;
	
	public QueueItem(Account user, MusicFile file) {
		this.userName = user.getUsername();
		this.musicFile = file;
		this.setStatus(PlayerState.QUEUED);
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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
		return musicFile;
	}

	public void setFile(MusicFile file) {
		this.musicFile = file;
	}

	@Override
	public int compareTo(QueueItem arg0) {
		if (arg0.getBucket() < this.getBucket()) {
			return 1;
		} else if (arg0.getBucket() > this.getBucket()) {
			return -1;
		} else if (arg0.getPosition() < this.getPosition()) {
			return 1;
		} else if (arg0.getPosition() > this.getPosition()) {
			return -1;
		} else {
			return 0;
		}
		
	}

	public void setStatus(PlayerState state) {
		this.status = state;
	}

	public PlayerState getStatus() {
		return status;
	}
	
	public String toString() {
		return musicFile.toString();		
	}
	
}
