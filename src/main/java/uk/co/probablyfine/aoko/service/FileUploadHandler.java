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

import uk.co.probablyfine.aoko.dao.MusicFileDao;

import com.google.common.io.Files;

@Service
public class FileUploadHandler {

	@Autowired
	MusicFileDao dao;
	
	@Value("#{settings['media.downloadtarget']}")
	private String downloadPath;
	
	public void processFile(MultipartFile file, String username) throws IOException, NoSuchAlgorithmException {
		
		long marker = System.currentTimeMillis();
		
		File outFile = new File(downloadPath+marker);
		Files.write(file.getBytes(), outFile);
		
		String hash = new BigInteger(Files.getDigest(outFile,MessageDigest.getInstance("SHA1"))).toString(); 
		
		if (dao.containsFile(hash)) {
			
		}
		
	}
}
