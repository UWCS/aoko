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
import org.springframework.web.multipart.MultipartFile;

import uk.co.probablyfine.aoko.service.FileUploadHandler;

@Controller
public class UploadController {
	
	@Autowired
	FileUploadHandler fuh;
	
	@RequestMapping(value = "/upload/", method = RequestMethod.POST)
	public String test(@ModelAttribute(value="FORM") FileUpload form,BindingResult result, Principal p) {
		 
		 System.out.println(form.getFile().getOriginalFilename());
		 if (null != p) {
			 try {
				fuh.processFile(form.getFile(), p.getName());
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }
		 
		 return "redirect:/";
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