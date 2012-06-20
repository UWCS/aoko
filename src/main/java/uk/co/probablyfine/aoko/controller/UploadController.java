package uk.co.probablyfine.aoko.controller;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;

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
	
	@Autowired
	FileUploadHandler fuh;
	
	@Autowired
	YoutubeDao ytDao;
	
	@RequestMapping(value = "upload", method = RequestMethod.POST)
	public String fileUpload(@ModelAttribute(value="FORM") FileUpload form,BindingResult result, Principal p) {
		 
		 System.out.println(form.getFile().getOriginalFilename());
		 if (null != p) {
			 try {
				fuh.processFile(form.getFile(), p.getName());
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		 }
		 
		 return "redirect:/";
	}
	
	@RequestMapping(value = "youtube", method = RequestMethod.POST)
	public String youtubeDownload(@RequestParam("url") String url, Principal p) {
		
		if (null != p) {
			System.out.println(url);
			YoutubeDownload yd = new YoutubeDownload(url);
			yd.setQueuedBy(p.getName());
			ytDao.queueDownload(yd);
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