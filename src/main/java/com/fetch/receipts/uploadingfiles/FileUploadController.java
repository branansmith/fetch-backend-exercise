package com.fetch.receipts.uploadingfiles;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fetch.receipts.uploadingfiles.storage.StorageFileNotFoundException;
import com.fetch.receipts.uploadingfiles.storage.StorageService;

@Controller
@RequestMapping("/receipts")
public class FileUploadController {

    private final StorageService storageService;

    @Autowired
    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/process")
    public String showUploadForm(Model model) {
        return "uploadForm";  
    }

    @GetMapping("/{id}/points")
    @ResponseBody
    public ResponseEntity<Object> getPoints(@PathVariable("id") String id) {
    	JSONObject entity = new JSONObject();
    	entity.put("points", storageService.getPoints(id));

    	return new ResponseEntity<Object>(entity.get("points"), HttpStatus.OK);
    }
    
    @PostMapping("/process")
    @ResponseBody
    public ResponseEntity<Object> handleFileUpload(@RequestParam("file") MultipartFile file) {
    	
    	JSONObject entity = new JSONObject();
        String uniqueId = UUID.randomUUID().toString();
        entity.put("id", uniqueId);
        
        storageService.store(file, uniqueId);
        
        return new ResponseEntity<Object>(entity.get("id"), HttpStatus.OK);
    }
    

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = storageService.loadAsResource(filename);
        if (file == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }
}
