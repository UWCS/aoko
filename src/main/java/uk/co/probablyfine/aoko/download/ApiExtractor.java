package uk.co.probablyfine.aoko.download;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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
import org.xml.sax.SAXException;

import com.google.common.collect.Maps;

@Service
public class ApiExtractor {

	@Autowired
	ArtDownloader artDownloader;
	
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
	
	
	
}
