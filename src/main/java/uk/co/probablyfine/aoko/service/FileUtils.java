package uk.co.probablyfine.aoko.service;

import static com.google.common.io.Files.getDigest;
import static com.google.common.io.Files.move;
import static java.io.File.createTempFile;
import static java.security.MessageDigest.getInstance;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.io.Files;

@Component
public class FileUtils {

	public File downloadToTemporaryFile(MultipartFile file) throws IOException {
		File hashFile = createTempFile(file.getName(),null);
		Files.write(file.getBytes(), hashFile);
		return hashFile;
	}
	
	public String getHashFromFile(File file) throws NoSuchAlgorithmException, IOException {
		return new BigInteger(getDigest(file,getInstance("SHA1"))).toString(16); 
	}

	public File moveFile(File file, String destination) throws IOException {
		File newFile = new File(destination);
		move(file, newFile);
		return newFile;
	}
	
	public AudioFile getAudioFile(File file) throws CannotReadException, IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException {
		return AudioFileIO.read(file);
	}
	
}