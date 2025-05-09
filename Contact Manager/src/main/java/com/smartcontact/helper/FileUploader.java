package com.smartcontact.helper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileUploader {
	
	 
	
	public boolean fileUploaded(MultipartFile file, String imageName) {
		boolean f = false;
		
		try {
			
			File uploadFile = new ClassPathResource("static/image/contact").getFile();
			String contactPath = uploadFile.getAbsolutePath();
			
			// file uploading using new technique
            Files.copy(file.getInputStream(), Paths.get(contactPath + File.separator + imageName),
                    StandardCopyOption.REPLACE_EXISTING);
            f = true;
			
		} catch (Exception e) {
			e.printStackTrace();
			f = false;
		}
		
		
		return f;
	}
	
	public boolean fileDeleted(MultipartFile file, String imageUrl) {
		boolean f = false;
		System.out.println(";akjdfka fafkjsadfkjsadkdjfkj");
		try {
			
			File deleteFile = new ClassPathResource("static/image/contact").getFile();
			File deleteImage = new File(deleteFile,imageUrl);
			deleteImage.delete();
			f = true;
			
		} catch (Exception e) {
			// TODO: handle exception
			f = false;
			e.printStackTrace();
		}
		
		return f;
	}
	
	public File getFileByFileName(String filename) throws IOException {
	
			File file = new ClassPathResource("static/image/contact").getFile();
			File imagefile = new File(file,filename);
			return imagefile; 
	}
	
}
