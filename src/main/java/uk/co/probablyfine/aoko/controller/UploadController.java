package uk.co.probablyfine.aoko.controller;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class UploadController {
	
	@RequestMapping(value = "/upload/", method = RequestMethod.POST)
	public String test(@ModelAttribute(value="FORM") FileUpload form,BindingResult result) {
		 
		 System.out.println(form.getFile().getOriginalFilename());
		 
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