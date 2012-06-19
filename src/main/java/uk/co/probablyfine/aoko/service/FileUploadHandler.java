package uk.co.probablyfine.aoko.service;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import uk.co.probablyfine.aoko.dao.AccountDao;
import uk.co.probablyfine.aoko.dao.MusicFileDao;
import uk.co.probablyfine.aoko.dao.QueueItemDao;
import uk.co.probablyfine.aoko.domain.FileType;
import uk.co.probablyfine.aoko.domain.MusicFile;
import uk.co.probablyfine.aoko.domain.QueueItem;
import uk.co.probablyfine.aoko.download.ArtDownloader;

import com.google.common.io.Files;

@Service
public class FileUploadHandler {

	@Autowired
	ArtDownloader arts;
	
	@Autowired
	MusicFileDao mfDao;
	
	@Autowired
	QueueItemDao qiDao;
	
	@Autowired
	AccountDao accounts;
	
	@Value("${media.repository}")
	private String downloadPath;
	
	public void processFile(MultipartFile file, String username) throws IOException, NoSuchAlgorithmException {
		
		File hashFile = File.createTempFile(file.getName(),null);
		Files.write(file.getBytes(), hashFile);
		
		String hash = new BigInteger(Files.getDigest(hashFile,MessageDigest.getInstance("SHA1"))).toString(16); 
		
		if (mfDao.containsFile(hash)) {
			
			qiDao.merge(new QueueItem(accounts.getFromUsername(username), mfDao.getFromUniqueId(hash)));
		
		} else {
			
			String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
			
			String newFileName = hash+extension;
			
			System.out.println("Moving file to "+downloadPath+newFileName);
			
			File newFile = new File(downloadPath+newFileName);
			
			Files.move(hashFile, newFile);
			
			Map<String,String> metadata = FileMetadataTagger.getMetaData(newFile);
			
			MusicFile mf = new MusicFile();

			try {
				arts.getAlbumArt(metadata, hash);
				mf.setArtLocation(hash+".jpg");
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RuntimeException e) {
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			
			metadata.put("originalname", file.getOriginalFilename());
			
			mf.setType(FileType.UPLOAD);
			mf.setLocation(newFileName);
			mf.setUniqueId(hash);
			mf.setMetaData(metadata);
			
			
			
			qiDao.queueTrack(accounts.getFromUsername(username), mf);
			
		} 
		
		hashFile.delete();
		
	}
}
