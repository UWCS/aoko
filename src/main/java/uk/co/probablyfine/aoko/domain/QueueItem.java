package uk.co.probablyfine.aoko.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import uk.co.probablyfine.aoko.util.PlayerState;

@Entity
public class QueueItem implements Comparable<QueueItem>{

	@Id
	@GeneratedValue
	private int id;
	
	private int bucket;
	
	private int position;
	
	private MusicFile file;
	
	@Column(nullable = false)
	private String userName;
	
	private PlayerState state;
	
	public QueueItem(User user, MusicFile file) {
		this.userName = user.getUsername();
		this.file = file;
		this.setState(PlayerState.QUEUED);
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
		return file;
	}

	public void setFile(MusicFile file) {
		this.file = file;
	}

	public int compareTo(QueueItem arg0) {
		//TODO: Make this not utterly broken		
		return -1;
		
	}

	public void setState(PlayerState state) {
		this.state = state;
	}

	public PlayerState getState() {
		return state;
	}

	
}
