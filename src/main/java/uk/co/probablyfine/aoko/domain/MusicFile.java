package uk.co.probablyfine.aoko.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.google.common.base.Joiner;


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
	 * File location for uploads + youtube
	 * Grooveshark url for grooveshark tracks
	 * Spotify id for Spotify
	 */
	@Column(nullable = false)
	private String location;
	
	private String artLocation;
	
	/*
	 * File metadata, for display/view purposes
	 */
	@ElementCollection(fetch = FetchType.EAGER)
	private Map<String,String> metaData = new HashMap<String, String>();
	
	/*
	 * Filehash for uploads, song/video id for others
	 */
	@Column(nullable = false)
	private String uniqueId;

	public MusicFile() {
		
	}
	
	public MusicFile(FileType type, String location, String uniqueId) {
		this.type = type;
		this.location = location;
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

	public String getArtLocation() {
		return artLocation;
	}

	public void setArtLocation(String artLocation) {
		this.artLocation = artLocation;
	}
	
	@Override
	public String toString() {
		List<String> params = new ArrayList<String>();
				
		if (metaData.containsKey("artist") && metaData.get("artist") != "") {
			params.add(metaData.get("artist"));
		}
		
		if (metaData.containsKey("title") && metaData.get("artist") != "") {
			params.add(metaData.get("title"));
		}
		
		if (metaData.containsKey("album") && metaData.get("album") != "") {
			params.add(metaData.get("album"));
		}
		
		if (params.isEmpty()) {
			params.add(metaData.get("originalname"));
		}
		
		return Joiner.on(" -- ").skipNulls().join(params).replace("_", " ");
	}
	
}