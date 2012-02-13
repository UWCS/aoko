package uk.co.probablyfine.aoko.service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.KeyNotFoundException;
import org.jaudiotagger.tag.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileMetadataTagger {

	private static Logger log = LoggerFactory.getLogger(FileMetadataTagger.class);
	
	public static Map<String,String> getMetaData(File file) {
		
		Map<String,String> metadata = new HashMap<String, String>();
		
		AudioFile f;
		try {
			f = AudioFileIO.read(file);
		} catch (Exception e) {
			log.error("Exception, ",e);
			return metadata;
		}
		Tag tag = f.getTag();
		
		for (FieldKey key : FieldKey.values()) {
			try {
				tag.getFirst(key);
				System.out.println("Found "+key.name()+": "+tag.getFirst(key));
				metadata.put(key.name().toLowerCase(), tag.getFirst(key));
			} catch (Exception e) {
				continue;
			}
		}
		
		return metadata;

	}
	
}
