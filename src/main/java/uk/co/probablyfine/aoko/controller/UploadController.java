package uk.co.probablyfine.aoko.controller;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import uk.co.probablyfine.aoko.dao.YoutubeDao;
import uk.co.probablyfine.aoko.domain.YoutubeDownload;
import uk.co.probablyfine.aoko.service.FileUploadHandler;

@Controller
@RequestMapping(value = "/submit/")
public class UploadController {
	
	private final Logger log = LoggerFactory.getLogger(UploadController.class);
	
	@Autowired private FileUploadHandler handler;
	@Autowired private YoutubeDao videos;
	
	@RequestMapping(value = "upload", method = RequestMethod.POST)
	public String fileUpload(@ModelAttribute(value="FORM") FileUpload form,BindingResult result, Principal p) {
		 
		 if (null != p) {
			 try {
				 log.debug("Processing upload of {} from {}",form.getFile().getName(), p.getName());
				 handler.processFile(form.getFile(), p.getName());
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		 }
		 
		 return "redirect:/";
	}
	
	@RequestMapping(value = "youtube", method = RequestMethod.POST)
	public String videoDownload(@RequestParam("url") String url, Principal p) {
		
		if (null != p) {
			System.out.println(url);
			YoutubeDownload yd = new YoutubeDownload(url);
			yd.setQueuedBy(p.getName());
			videos.queueDownload(yd);
		} else {
			//return error?
		}
		
		return "redirect:/youtube/";
	}
	
}

class FileUpload {
	
	private MultipartFile file;
	

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}
	
}