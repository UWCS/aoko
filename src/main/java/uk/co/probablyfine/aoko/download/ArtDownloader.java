package uk.co.probablyfine.aoko.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.common.base.Joiner;

@Service
public class ArtDownloader {

	@Value("${media.art}")
	private String downloadPath;
		
	private final Logger log = LoggerFactory.getLogger(ArtDownloader.class);
	
	public void getVimeoArt(String vimeoUrl, String vimeoId) throws IOException {

		final String path = new File(downloadPath,vimeoId+".jpg").getAbsolutePath();
		
		log.debug("Downloading art from {} to {}", vimeoUrl, path);
		
		URL imageRequest = new URL(vimeoUrl);
	    
		ReadableByteChannel rbc = Channels.newChannel(imageRequest.openStream());
		
		FileOutputStream fos = new FileOutputStream(path);
	    
		fos.getChannel().transferFrom(rbc, 0, 1 << 24);
		
		fos.close();
		
	}
	
	public void getYoutubeArt(String youtubeId) throws IOException {
		
		final String url = "http://img.youtube.com/vi/"+youtubeId+"/1.jpg";
		
		final String path = new File(downloadPath,youtubeId+".jpg").getAbsolutePath();
		
		log.debug("Downloading art from {} to {}", url, path);
		
		URL imageRequest = new URL(url);
	    
		ReadableByteChannel rbc = Channels.newChannel(imageRequest.openStream());
		
		FileOutputStream fos = new FileOutputStream(path);
	    
		fos.getChannel().transferFrom(rbc, 0, 1 << 24);
		
		fos.close();
		
	}
	
	public void getAlbumArt(Map<String,String> args, String filename) throws ParserConfigurationException, MalformedURLException, SAXException, IOException {
		
		List<String> queryString = new ArrayList<String>();
		
		String artUrl;
		
		if (args.containsKey("amazon_id")) {
			artUrl = "http://ec1.images-amazon.com/images/P/"+args.get("amazon_id")+".jpg";
		} else {
			
			if (args.containsKey("album") && (args.containsKey("artist") || args.containsKey("album_artist"))) {
					queryString.add("release:"+args.get("album"));
					if (args.containsKey("artist")) {
						queryString.add("artist:"+args.get("artist"));
					} else {
						queryString.add("artist:"+args.get("album_artist"));
					}
			} else {
				throw new RuntimeException("Insufficient data for album art");
			}
			
			String url = "http://musicbrainz.org/ws/2/release/?type=xml&query="+URLEncoder.encode(Joiner.on(" ").join(queryString),"UTF-8");
			
			System.out.println(url);
			
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new URL(url).openStream());
			
			NodeList nodes = doc.getElementsByTagName("asin");
			
			System.out.println(nodes.toString());
			
			String asin = nodes.item(0).getChildNodes().item(0).getNodeValue();
			
			if (asin == null) {
				throw new RuntimeException("GET request did not return asin");
			}
			
			artUrl = "http://ec1.images-amazon.com/images/P/"+asin+".jpg";
		}
				
		System.out.println(artUrl);
		
		URL imageRequest = new URL(artUrl);
	    
		ReadableByteChannel rbc = Channels.newChannel(imageRequest.openStream());
	    
		System.out.println(downloadPath+filename+".jpg");
		
		FileOutputStream fos = new FileOutputStream(downloadPath+filename+".jpg");
	    
		fos.getChannel().transferFrom(rbc, 0, 1 << 24);
		
		fos.close();
		
		
	}
	
}
