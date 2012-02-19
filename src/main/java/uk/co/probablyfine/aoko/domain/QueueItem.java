package uk.co.probablyfine.aoko.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import com.google.common.base.Joiner;


@Entity
public class QueueItem implements Comparable<QueueItem>{

	public QueueItem() {
		this.setState(PlayerState.QUEUED);
	}
	
	@Id
	@GeneratedValue
	private int id;
	
	private int bucket;
	
	private int position;
	
	@OneToOne(targetEntity=MusicFile.class,cascade=CascadeType.ALL)
	private MusicFile musicFile;
	
	@Column(nullable = false)
	private String userName;
	
	private PlayerState status;
	
	public QueueItem(Account user, MusicFile file) {
		this.userName = user.getUsername();
		this.musicFile = file;
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
		return musicFile;
	}

	public void setFile(MusicFile file) {
		this.musicFile = file;
	}

	public int compareTo(QueueItem arg0) {
		if (arg0.getBucket() < this.getBucket()) {
			return 1;
		} else if (arg0.getBucket() > this.getBucket()) {
			return -1;
		} else if (arg0.getPosition() < this.getPosition()) {
			return 1;
		} else if (arg0.getPosition() < this.getPosition()) {
			return -1;
		} else {
			return 0;
		}
		
	}

	public void setState(PlayerState state) {
		this.status = state;
	}

	public PlayerState getState() {
		return status;
	}

	public String toHTMLString() {
		List<String> params = new ArrayList<String>();
		Map<String, String> data = musicFile.getMetaData();
		
		if (data.containsKey("artist") && data.get("artist") != "") {
			params.add("<span class='artist'>"+data.get("artist"));
		}
		
		if (data.containsKey("title") && data.get("artist") != "") {
			params.add(data.get("title"));
		}
		
		if (data.containsKey("album") && data.get("album") != "") {
			params.add(data.get("album"));
		}
		
		if (params.isEmpty()) {
			params.add(data.get("originalname"));
		}
		
		return Joiner.on(" -- ").skipNulls().join(params);
		
	}
	
	
	public String toString() {
		List<String> params = new ArrayList<String>();
		Map<String, String> data = musicFile.getMetaData();
		
		if (data.containsKey("artist") && data.get("artist") != "") {
			//TODO: MAKE ANOTHER THING LIKE THIS.
			params.add(data.get("artist"));
		}
		
		if (data.containsKey("title") && data.get("artist") != "") {
			params.add(data.get("title"));
		}
		
		if (data.containsKey("album") && data.get("album") != "") {
			params.add(data.get("album"));
		}
		
		if (params.isEmpty()) {
			params.add(data.get("originalname"));
		}
		
		return Joiner.on(" -- ").skipNulls().join(params);
		
	}
	
}
