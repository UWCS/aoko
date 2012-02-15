package uk.co.probablyfine.aoko.service;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import uk.co.probablyfine.aoko.dao.AccountDao;
import uk.co.probablyfine.aoko.dao.MusicFileDao;
import uk.co.probablyfine.aoko.dao.QueueItemDao;
import uk.co.probablyfine.aoko.domain.FileType;
import uk.co.probablyfine.aoko.domain.MusicFile;
import uk.co.probablyfine.aoko.domain.QueueItem;

import com.google.common.io.Files;

@Service
public class FileUploadHandler {

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
			
			String newFileName = downloadPath+hash+extension;
			
			System.out.println("Moving file to "+newFileName);
			
			File newFile = new File(newFileName);
			
			Files.move(hashFile, newFile);
			
			Map<String,String> metadata = FileMetadataTagger.getMetaData(newFile);
			metadata.put("originalname", file.getOriginalFilename());
			
			MusicFile mf = new MusicFile();
			mf.setType(FileType.UPLOAD);
			mf.setLocation(newFileName);
			mf.setUniqueId(hash);
			mf.setMetaData(metadata);
			
			qiDao.queueTrack(accounts.getFromUsername(username), mf);
			
		} 
		
		hashFile.delete();
		
	}
}
