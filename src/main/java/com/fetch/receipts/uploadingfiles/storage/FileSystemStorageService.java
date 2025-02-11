package com.fetch.receipts.uploadingfiles.storage;

import java.util.Scanner;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileSystemStorageService implements StorageService {
	
	private HashMap<String, Integer> filePoints = new HashMap<String, Integer>();

	private final Path rootLocation;

	@Autowired
	public FileSystemStorageService(StorageProperties properties) {
        
        if(properties.getLocation().trim().length() == 0){
            throw new StorageException("File upload location can not be Empty."); 
        }

		this.rootLocation = Paths.get(properties.getLocation());
	}

	@Override
	public Stream<Path> loadAll() {
		try {
			return Files.walk(this.rootLocation, 1)
				.filter(path -> !path.equals(this.rootLocation))
				.map(this.rootLocation::relativize);
		}
		catch (IOException e) {
			throw new StorageException("Failed to read stored files", e);
		}

	}

	@Override
	public Path load(String filename) {
		return rootLocation.resolve(filename);
	}

	@Override
	public Resource loadAsResource(String filename) {
		try {
			Path file = load(filename);
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			}
			else {
				throw new StorageFileNotFoundException(
						"Could not read file: " + filename);

			}
		}
		catch (MalformedURLException e) {
			throw new StorageFileNotFoundException("Could not read file: " + filename, e);
		}
	}

	@Override
	public void deleteAll() {
		FileSystemUtils.deleteRecursively(rootLocation.toFile());
	}

	@Override
	public void init() {
		try {
			Files.createDirectories(rootLocation);
		}
		catch (IOException e) {
			throw new StorageException("Could not initialize storage", e);
		}
	}

	@Override
	public void store(MultipartFile file, String id) {
		try {
			String content = new String(file.getBytes());
			int points = calculatePoints(content);
			filePoints.put(id, points);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
			
	}
	
	@Override
	public int getPoints(String id) {
		return filePoints.get(id);
	}
	
	
	public int calculatePoints(String data) {
		int points = 0;
		
		JSONObject jsonString = new JSONObject(data);
		String retailer = jsonString.getString("retailer");
		
		for (int i = 0; i < retailer.length(); ++i) {
			if (Character.isDigit(retailer.charAt(i)) || Character.isLetter(retailer.charAt(i))) {
				points++;
			}
		}
		
		String total = jsonString.getString("total");
		if (total.charAt(total.length() - 1) == '0' && total.charAt(total.length() - 2) == '0') {
			points += 50;
		}
		
		if (Double.valueOf(total) % 0.25 == 0) {
			points += 25;
		}
		
		
		String purchaseDate = jsonString.getString("purchaseDate");
		if (purchaseDate.charAt(purchaseDate.length() - 1) % 2 != 0) {
			points += 6;
		}
		
		
		String purchaseTime = jsonString.getString("purchaseTime");
		String parsedTime = "";
		
		parsedTime += purchaseTime.charAt(0);
		parsedTime += purchaseTime.charAt(1);
		parsedTime += purchaseTime.charAt(3);
		parsedTime += purchaseTime.charAt(4);
		
		if (Integer.valueOf(parsedTime) >= 1400 && Integer.valueOf(parsedTime) <= 1600) {
			points += 10;
		}
		
		
		
		
		JSONArray items = jsonString.getJSONArray("items");
		int itemCount = items.length();

		int pairs = itemCount / 2;
		points += pairs * 5;


		for (int i = 0; i < itemCount; ++i) {
		    JSONObject item = items.getJSONObject(i);
		    String description = item.getString("shortDescription").trim();
		    
		    if (description.length() % 3 == 0) {
		        double price = item.getDouble("price") * 0.2;
		        points += (int) Math.ceil(price);  
		    }
		}
		return points;
		
	}
}
