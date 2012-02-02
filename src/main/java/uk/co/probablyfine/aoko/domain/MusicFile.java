package uk.co.probablyfine.aoko.domain;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import uk.co.probablyfine.aoko.util.FileType;

@Embeddable
@Entity
public class MusicFile {

	@Id
	@GeneratedValue
	private int id;
	
	/*
	 * Distinguish what type of file this is.
	 */
	@Column(nullable = false)
	private FileType type;
		
	/*
	 * File location for uploads
	 * Video id for YouTube
	 * Grooveshark url for grooveshark tracks
	 * Spotify id for Spotify
	 */
	@Column(nullable = false)
	private String location;
	
	/*
	 * File metadata, for display/view purposes
	 */
	@Column(nullable = false)
	private Map<String,String> metaData;
	
	/*
	 * Filehash for uploads, song/video id for others
	 */
	@Column(nullable = false)
	private String uniqueId;

	public MusicFile(FileType type, String location,
			Map<String, String> metaData, String uniqueId) {
		super();
		this.type = type;
		this.location = location;
		this.metaData = metaData;
		this.uniqueId = uniqueId;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public FileType getType() {
		return type;
	}

	public void setType(FileType type) {
		this.type = type;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Map<String, String> getMetaData() {
		return metaData;
	}

	public void setMetaData(Map<String, String> metaData) {
		this.metaData = metaData;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}
	
}