package uk.co.probablyfine.aoko.service;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import uk.co.probablyfine.aoko.dao.AccountDao;
import uk.co.probablyfine.aoko.dao.MusicFileDao;
import uk.co.probablyfine.aoko.domain.FileType;
import uk.co.probablyfine.aoko.domain.MusicFile;
import uk.co.probablyfine.aoko.download.ArtDownloader;

@Service
public class FileUploadHandler {

	@Value("${media.repository}") private String downloadPath;

	@Autowired private ArtDownloader arts;
	@Autowired private MusicFileDao music;
	@Autowired private QueueService queue;
	@Autowired private AccountDao accounts;
	@Autowired private FileUtils utils;
	@Autowired private FileMetadataTagger tagger;
	
	private final Logger log = LoggerFactory.getLogger(FileUploadHandler.class);
	
	public void processFile(MultipartFile file, String username) throws IOException, NoSuchAlgorithmException {
		
		File download = utils.downloadToTemporaryFile(file);
		String hash = utils.getHashFromFile(download);
		
		MusicFile musicFile;
		
		if (music.containsFile(hash)) {
			musicFile = music.getFromUniqueId(hash);
		} else {
			musicFile = getAudioTagsAndArt(download, hash, file.getOriginalFilename());
		}
		
		queue.queueTrack(accounts.getFromUsername(username), musicFile);
		
		download.delete();
	}
			
	public MusicFile getAudioTagsAndArt(File download, String hash, String originalName) throws IOException {
			
		String extension = originalName.substring(originalName.lastIndexOf("."));
		String newFileName = hash+extension;
		
		log.debug("Moving file to {}",downloadPath+newFileName);
			
		File newFile = utils.moveFile(download, downloadPath+newFileName);
		MusicFile mf = new MusicFile();
		
		Map<String,String> metadata = tagger.getMetaData(newFile);
		metadata.put("originalname", originalName);

		try {
			arts.getAlbumArt(metadata, hash);
			mf.setArtLocation(hash+".jpg");
		} catch (ParserConfigurationException e) {
			log.error("Badly configured parser, abandoning getting art.");
			log.error("{}",e);
		} catch (SAXException e) {
			log.error("XML exception, abandoning getting art.");
			log.error("{}",e);
		} catch (IOException e) {
			log.error("IO Exception getting art, abandoning.");
			log.error("{}",e);
		} catch (RuntimeException e) {
			log.error("Cannot get album art data, using filename instead");
			log.error("{}",e);
		}
		
		mf.setType(FileType.UPLOAD);
		mf.setLocation(newFileName);
		mf.setUniqueId(hash);
		mf.setMetaData(metadata);
		
		return mf;
		
	} 
}
