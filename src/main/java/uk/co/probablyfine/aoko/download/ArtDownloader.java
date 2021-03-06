package uk.co.probablyfine.aoko.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

@Service
public class ArtDownloader {

	@Value("${media.art}") private String downloadPath;
	@Autowired private ApiExtractor apiExtractor;
		
	private final Logger log = LoggerFactory.getLogger(ArtDownloader.class);
	
	public void getVimeoArt(String vimeoUrl, String vimeoId) throws IOException {
		final String path = new File(downloadPath,vimeoId+".jpg").getAbsolutePath();
		downloadFile(vimeoUrl, path);
	}
	
	public void getYoutubeArt(String youtubeId) throws IOException {
		final String url = "http://img.youtube.com/vi/"+youtubeId+"/1.jpg";
		final String path = new File(downloadPath,youtubeId+".jpg").getAbsolutePath();
		downloadFile(path, url);
	}
	
	public void getAlbumArt(Map<String,String> args, String filename) throws ParserConfigurationException, MalformedURLException, SAXException, IOException {
		
		String artUrl;
		
		if (args.containsKey("amazon_id")) {
			artUrl = "http://ec1.images-amazon.com/images/P/"+args.get("amazon_id")+".jpg";
		} else {
			String asin = apiExtractor.getAsinFromMusicbrainz(args);
			if (asin.equals("")) {
				throw new RuntimeException("GET request did not return asin");
			}
			artUrl = "http://ec1.images-amazon.com/images/P/"+asin+".jpg";
		}
				
		downloadFile(artUrl, downloadPath+filename+".jpg");

	}
	
	public void downloadFile(String url, String downloadLocation) throws IOException {
		log.debug("Downloading art from {} to {}", url, downloadLocation);
		URL imageRequest = new URL(url);
		ReadableByteChannel rbc = Channels.newChannel(imageRequest.openStream());
		FileOutputStream fos = new FileOutputStream(downloadLocation);
		fos.getChannel().transferFrom(rbc, 0, 1 << 24);
		fos.close();
	}
	
}
