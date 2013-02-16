package uk.co.probablyfine.aoko.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import org.hibernate.annotations.Cascade;

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
	
	@ManyToOne(targetEntity=MusicFile.class,cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH} )
	@Cascade( org.hibernate.annotations.CascadeType.SAVE_UPDATE)
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
	public boolean equals(Object other) {
		if(!(other instanceof QueueItem)) {
			return false;
		}
		QueueItem item = (QueueItem) other;
		return this.bucket == item.bucket & this.position == item.position;
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
		return this.userName + "--" + musicFile.toString();		
	}
	
}
