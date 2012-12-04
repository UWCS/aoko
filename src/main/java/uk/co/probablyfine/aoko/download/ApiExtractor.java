package uk.co.probablyfine.aoko.download;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;

@Service
public class ApiExtractor {

	@Autowired private ArtDownloader artDownloader;
	
	private final XPath xpath = XPathFactory.newInstance().newXPath();
	
	private final Logger log = LoggerFactory.getLogger(ApiExtractor.class);
	
	public Map<String,String> getVimeoData(String videoId) {
		
		final String url = "http://vimeo.com/api/v2/video/"+videoId+".xml";
		
		log.debug("Attempting to parse {}",url);
		
		final Map<String,String> results = Maps.newHashMap();
		
		try {
			final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new URL(url).openStream());
		
			final String title = xpath.evaluate("/videos/video/title", doc); 
		
			log.debug("Video title: {}",title);
			
			xpath.reset();
			
			results.put("name", title);
			
			final String artUrl = xpath.evaluate("/videos/video/thumbnail_medium", doc);
		
			artDownloader.getVimeoArt(artUrl, videoId);
		
			results.put("artlocation", artUrl);
			
		} catch (MalformedURLException e) {
			log.error("Badly formed url, returning");
			log.error("Exception: {}",e);
		} catch (SAXException e) {
			log.error("Error parsing XML feed");
			log.error("Exception: {}",e);
		} catch (IOException e) {
			log.error("Error connecting to feed");
			log.error("Exception: {}",e);
		} catch (ParserConfigurationException e) {
			log.error("Badly configured parser");
			log.error("Exception: {}",e);
		} catch (XPathExpressionException e) {
			log.error("Could not parse XPath expression");
			log.error("Exception: {}",e);
		}
		
		return results;
	}
	
	public String getAsinFromMusicbrainz(Map<String,String> metadata) throws UnsupportedEncodingException {
		
		//TODO: Xpath
		
		List<String> queryString = new ArrayList<String>();
		
		if (metadata.containsKey("album") && (metadata.containsKey("artist") || metadata.containsKey("album_artist"))) {
				queryString.add("release:"+metadata.get("album"));
				if (metadata.containsKey("artist")) {
					queryString.add("artist:"+metadata.get("artist"));
				} else {
					queryString.add("artist:"+metadata.get("album_artist"));
				}
		} else {
			throw new RuntimeException("Insufficient data for album art");
		}
		
		String url = "http://musicbrainz.org/ws/2/release/?type=xml&query="+URLEncoder.encode(Joiner.on(" ").join(queryString),"UTF-8");
		
		Document doc;
		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new URL(url).openStream());
		} catch (Exception e) {
			log.error("Unable to retrieve information from {}", url);
			return "";
		}
		
		NodeList nodes = doc.getElementsByTagName("asin");
		
		return nodes.item(0).getChildNodes().item(0).getNodeValue();
		
	}
	
	
}
