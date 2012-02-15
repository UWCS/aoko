package uk.co.probablyfine.aoko.download;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class YouTubeArtDownloader {

	@Value("${media.art}")
	private String downloadPath;
		
	public void getAlbumArt(String youtubeId) throws IOException {
		
		String url = "http://img.youtube.com/vi/"+youtubeId+"/0.jpg";
		
		System.out.println(url);
		
		URL imageRequest = new URL(url);
	    
		ReadableByteChannel rbc = Channels.newChannel(imageRequest.openStream());
	    
		System.out.println(downloadPath+youtubeId+".jpg");
		
		FileOutputStream fos = new FileOutputStream(downloadPath+youtubeId+".jpg");
	    
		fos.getChannel().transferFrom(rbc, 0, 1 << 24);
		
		fos.close();
		
	}
	
}
