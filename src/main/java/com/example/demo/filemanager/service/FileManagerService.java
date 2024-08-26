package com.example.demo.filemanager.service;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.filemanager.model.LineChartResponse;
import com.example.demo.filemanager.model.PieChartResponse;
import com.example.demo.filemanager.model.PythonResponse;
import com.example.demo.filemanager.model.UploadResponse;
import com.example.demo.filemanager.model.SentimentTableResponse;

@Service
public class FileManagerService {
	
	RestTemplate restTemplate = new RestTemplate();

	public UploadResponse uploadToPython(MultipartFile csvFile) throws Exception {
		
		try {
			// Set the headers
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);

			// Convert MultipartFile to InputStreamResource
			ByteArrayResource fileResource = new ByteArrayResource(csvFile.getBytes()) {
			    @Override
			    public String getFilename() {
			        return csvFile.getOriginalFilename();
			    }
			};

			// Create the body map with the file
			MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
			body.add("file", fileResource);

			// Create the HttpEntity containing the headers and the body
			HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
			String url = "http://127.0.0.1:5000/process";

			// Create a RestTemplate that disables SSL verification
			SSLContext sslContext = SSLContextBuilder
			        .create()
			        .loadTrustMaterial(TrustAllStrategy.INSTANCE)
			        .build();

			SSLConnectionSocketFactory socketFactory =  new SSLConnectionSocketFactory(sslContext);

			HttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
					        .setSSLSocketFactory(socketFactory)
					        .build();

					org.apache.hc.client5.http.impl.classic.CloseableHttpClient c5  = HttpClients.custom()
					.setConnectionManager(connectionManager)
					.evictExpiredConnections()
					.build();

			HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(c5);
			RestTemplate restTemplate = new RestTemplate(factory);
			
	        ResponseEntity<List<PythonResponse>> response = restTemplate.exchange(
	                url,
	                HttpMethod.POST,
	                requestEntity,
	                new ParameterizedTypeReference<List<PythonResponse>>() {}
	        );
			
	         if(response.hasBody()) {
	        	  return this.mapToUploadResponse(response.getBody());
	         } else {
	        	 System.out.println("No body");
	         }
	         
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RestClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return null;
	}
	
	public UploadResponse mapToUploadResponse (List<PythonResponse> pythonList) {
		UploadResponse response = new UploadResponse();
		List<PieChartResponse> pieChartResponseList = new ArrayList<PieChartResponse>();
		List<SentimentTableResponse> sentimentTableResponseList = new ArrayList<SentimentTableResponse>();
		LineChartResponse lineChartResponse = new LineChartResponse();
		
		long positiveCount = 0;
		long negativeCount = 0;
		long neutralCount = 0;
		
		for(PythonResponse pythonResponse : pythonList) {
			
			SentimentTableResponse tableResponse = new SentimentTableResponse();
			tableResponse.setCleanText(pythonResponse.getClean_text());
			tableResponse.setSentiment(pythonResponse.getSentiment());
			sentimentTableResponseList.add(tableResponse);
			
			
			if(pythonResponse.getSentiment().equalsIgnoreCase("positive")) {
				positiveCount++;
			} 
			
			else if(pythonResponse.getSentiment().equalsIgnoreCase("negative")) {
				negativeCount++;
			}
			
			else if(pythonResponse.getSentiment().equalsIgnoreCase("neutral")) {
				neutralCount++;
			}
			
		}
		
		PieChartResponse positivePieChart = new PieChartResponse("Positive", positiveCount);
		PieChartResponse negativePieChart = new PieChartResponse("Negative", negativeCount);
		PieChartResponse neutralPieChart = new PieChartResponse("Neutral", neutralCount);
		
		pieChartResponseList.add(positivePieChart);
		pieChartResponseList.add(negativePieChart);
		pieChartResponseList.add(neutralPieChart);
		
		Map<String, int[]> sentimentMap =this.aggregateSentimentsByMonth(pythonList);
		
        // Convert the map to a sorted list by month
        List<Map.Entry<String, int[]>> sortedList = new ArrayList<>(sentimentMap.entrySet());
        sortedList.sort(Comparator.comparingInt(entry -> monthIndex(entry.getKey())));

        // Create the final JSON-like structure
        List<List<Object>> finalList = new ArrayList<>();
        for (Map.Entry<String, int[]> entry : sortedList) {
            List<Object> innerList = new ArrayList<>();
            innerList.add(entry.getKey());
            innerList.add(entry.getValue()[0]);
            innerList.add(entry.getValue()[1]);
            finalList.add(innerList);
        }
        
		response.setLineChart(finalList);
		response.setPieChart(pieChartResponseList);
		response.setSentimentTable(sentimentTableResponseList);
		response.setTotalSize(pythonList.size());
		
		return response;
	}
	
	public Map<String, int[]> aggregateSentimentsByMonth(List<PythonResponse> pythonList) {
        // Create a map to hold the sentiment counts by month
        Map<String, int[]> sentimentMap = new LinkedHashMap<>();
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM");
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

        for (PythonResponse pythonResponse : pythonList) {
        	Date date;
        	
			try {
				date = dateFormat.parse(pythonResponse.getRecordedDate());
	            String month = monthFormat.format(date);
	            sentimentMap.putIfAbsent(month, new int[2]);

	            if ("Positive".equalsIgnoreCase(pythonResponse.getSentiment())) {
	                sentimentMap.get(month)[0]++;
	            } else if ("Negative".equalsIgnoreCase(pythonResponse.getSentiment())) {
	                sentimentMap.get(month)[1]++;
	            }
			} catch (ParseException e) {
				e.printStackTrace();
			}
        	
        }
        return sentimentMap;
    }
	
	
    private static int monthIndex(String month) {
        switch (month) {
            case "January": return 1;
            case "February": return 2;
            case "March": return 3;
            case "April": return 4;
            case "May": return 5;
            case "June": return 6;
            case "July": return 7;
            case "August": return 8;
            case "September": return 9;
            case "October": return 10;
            case "November": return 11;
            case "December": return 12;
            default: return 0;
        }
    }
	
}
