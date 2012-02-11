package uk.co.probablyfine.aoko.service;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import uk.co.probablyfine.aoko.dao.AccountDao;
import uk.co.probablyfine.aoko.dao.MusicFileDao;
import uk.co.probablyfine.aoko.dao.QueueItemDao;
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
	
	@Value("#{settings['media.repository']}")
	private String downloadPath;
	
	public void processFile(MultipartFile file, String username) throws IOException, NoSuchAlgorithmException {
		
		File hashFile = File.createTempFile(file.getName(),null);
		Files.write(file.getBytes(), hashFile);
		
		String hash = new BigInteger(Files.getDigest(hashFile,MessageDigest.getInstance("SHA1"))).toString(); 
		
		if (mfDao.containsFile(hash)) {
			
			qiDao.merge(new QueueItem(accounts.getFromUsername(username), mfDao.getFromUniqueId(username)));
		
		} else {
			String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
			
			String newFileName = downloadPath+hash+extension;
			
			System.out.println("Moving file to "+newFileName);
			
			Files.move(hashFile, new File(newFileName));
			
		}
		
		
		
	}
}
