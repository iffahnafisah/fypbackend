package com.example.demo.filemanager.controller;

import java.io.InputStreamReader;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.filemanager.service.FileManagerService;
import com.opencsv.CSVReader;

@RestController
@RequestMapping("/file-manager")
public class FileManagerController {
	
	@Autowired
	private FileManagerService fileManagerService;

	@PostMapping("/read")
	public String readFile(@RequestParam("file") MultipartFile file) {
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            List<String[]> records = csvReader.readAll();
            for (String[] record : records) {
                System.out.println( String.join(", ", record));
            }
            return "File uploaded and processed successfully";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to process the file";
        }
	}
	
	@PostMapping("/upload")
	public String uploadFile(@RequestParam("file") MultipartFile file) {
        try {
        	fileManagerService.uploadToPython(file);
            return "File uploaded and processed successfully";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to process the file";
        }
	}
	
}
