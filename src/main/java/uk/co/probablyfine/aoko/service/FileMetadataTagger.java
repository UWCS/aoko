package uk.co.probablyfine.aoko.service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FileMetadataTagger {

	private static Logger log = LoggerFactory.getLogger(FileMetadataTagger.class);
	
	@Autowired private FileUtils utils;
	
	public Map<String,String> getMetaData(File file) {
		
		Map<String,String> metadata = new HashMap<String, String>();
		
		final AudioFile audioFile;
		
		try {
			audioFile = utils.getAudioFile(file);
		} catch (Exception e) {
			log.warn("Cannot get AudioFile for {}, returning empty metadata", file.getName());
			return metadata;
		}
		
		final Tag tag = audioFile.getTag();
		
		for (final FieldKey key : FieldKey.values()) {
			
			String tagValue = null;
			
			try {
				tagValue = tag.getFirst(key);
			} catch (Exception e) {
				continue;
			}	
				
			log.debug("Found {}: {}",key.name(),tagValue);

			if (null == tagValue || tagValue == "" || tagValue.length() > 255) {
				continue;
			}

			metadata.put(key.name().toLowerCase(), tagValue);
			 
		}
		
		return metadata;

	}
	
}
