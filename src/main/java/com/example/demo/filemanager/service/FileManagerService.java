package com.example.demo.filemanager.service;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

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

import com.example.demo.filemanager.model.ColumnChartResponse;
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
		List<ColumnChartResponse> columnChartResponseList = new ArrayList<>();
		LineChartResponse lineChartResponse = new LineChartResponse();
		
		long positiveCount = 0;
		long negativeCount = 0;
		long neutralCount = 0;
		
		for(PythonResponse pythonResponse : pythonList) {
			
			SentimentTableResponse tableResponse = new SentimentTableResponse();
			tableResponse.setCleanText(pythonResponse.getClean_text());
			tableResponse.setSentiment(pythonResponse.getSentiment());
			tableResponse.setFocusArea(pythonResponse.getFocusArea());
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
		Map<String, long[]> sentimentByTopicMap = this.aggregateSentimentByTopic(pythonList);
		Map<String, String> trends = this.analyzeSentimentTrends(sentimentMap);
		
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
        
        List<List<Object>> columnChartData = new ArrayList<>();
        for (Map.Entry<String, long[]> entry : sentimentByTopicMap.entrySet()) {
            List<Object> dataEntry = new ArrayList<>();
            dataEntry.add(entry.getKey()); // Sentiment
            for (long count : entry.getValue()) {
                dataEntry.add(count); // Counts for each focus area
            }
            columnChartData.add(dataEntry);
        }
        
        // Get best and worst focus areas
        Map<String, String> performanceMap = determineBestAndWorstFocusAreas(sentimentByTopicMap);
        
		response.setLineChart(finalList);
		response.setPieChart(pieChartResponseList);
		response.setSentimentTable(sentimentTableResponseList);
		response.setTotalSize(pythonList.size());
		response.setTotalPos(positiveCount);
		response.setTotalNeg(negativeCount);
		response.setTotalNeu(neutralCount);
		response.setColumnChart(columnChartData);
		response.setBestFocusArea(performanceMap.get("bestFocusArea"));
	    response.setPoorFocusArea(performanceMap.get("poorFocusArea"));
	    response.setPositiveTrend(trends.get("positiveTrend"));
	    response.setNegativeTrend(trends.get("negativeTrend"));
		
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
	
	public Map<String, long[]> aggregateSentimentByTopic(List<PythonResponse> pythonList) {
	    // Initialize a map to hold the sentiment counts by focus area
	    Map<String, long[]> sentimentMap = new LinkedHashMap<>();

	    // Define the focus areas
	    String[] focusAreas = {"Speed & Stability", "Coverage", "Connectivity", "Customer Solution"};

	    // Initialize the sentiment map for each sentiment type
	    sentimentMap.put("Negative", new long[focusAreas.length]);
	    sentimentMap.put("Neutral", new long[focusAreas.length]);
	    sentimentMap.put("Positive", new long[focusAreas.length]);

	    Logger logger = Logger.getLogger(FileManagerService.class.getName());
	    
	    // Iterate over the PythonResponse list to populate the map
	    for (PythonResponse pythonResponse : pythonList) {
	        String sentiment = pythonResponse.getSentiment();
	        String focusArea = pythonResponse.getFocusArea();

	        // Determine the index of the focus area
	        int index = Arrays.asList(focusAreas).indexOf(focusArea);

	        if (index != -1) {
	            if ("Positive".equalsIgnoreCase(sentiment)) {
	                sentimentMap.get("Positive")[index]++;
	            } else if ("Negative".equalsIgnoreCase(sentiment)) {
	                sentimentMap.get("Negative")[index]++;
	            } else if ("Neutral".equalsIgnoreCase(sentiment)) {
	                sentimentMap.get("Neutral")[index]++;
	            }
	        }
	    }

	    return sentimentMap;
	}

	public Map<String, String> determineBestAndWorstFocusAreas(Map<String, long[]> sentimentByTopicMap) {
	    String[] focusAreas = {"Connectivity", "Speed & Stability", "Coverage", "Customer Solution"};

	    // Initialize variables to track the best and worst performing focus areas
	    String bestFocusArea = "";
	    String poorFocusArea = "";
	    long maxPositive = Long.MIN_VALUE;
	    long maxNegative = Long.MIN_VALUE;

	    // Identify the focus area with the highest positive and negative counts
	    for (int i = 0; i < focusAreas.length; i++) {
	        if (sentimentByTopicMap.get("Positive")[i] > maxPositive) {
	            maxPositive = sentimentByTopicMap.get("Positive")[i];
	            bestFocusArea = focusAreas[i];
	        }

	        else if (sentimentByTopicMap.get("Positive")[i] == maxPositive) {
		    bestFocusArea += " and " + focusAreas[i];
	        }
	        if (sentimentByTopicMap.get("Negative")[i] > maxNegative) {
	            maxNegative = sentimentByTopicMap.get("Negative")[i];
	            poorFocusArea = focusAreas[i];
	        }

	        else if (sentimentByTopicMap.get("Negative")[i] == maxNegative) {
		    poorFocusArea += " and " + focusAreas[i];
	        }
	    }

	    // Create and return a map with the results
	    Map<String, String> performanceMap = new LinkedHashMap<>();
	    performanceMap.put("bestFocusArea", bestFocusArea);
	    performanceMap.put("poorFocusArea", poorFocusArea);
	    
	    return performanceMap;
	}
	
	public Map<String, String> analyzeSentimentTrends(Map<String, int[]> sentimentMap) {
	    // Sort the sentiment map by month to ensure chronological order
	    List<Map.Entry<String, int[]>> sortedEntries = new ArrayList<>(sentimentMap.entrySet());
	    sortedEntries.sort(Map.Entry.comparingByKey()); // Assumes months are in a sortable format

	    int size = sortedEntries.size();
	    int[] positiveCounts = new int[size];
	    int[] negativeCounts = new int[size];

	    for (int i = 0; i < size; i++) {
	        Map.Entry<String, int[]> entry = sortedEntries.get(i);
	        positiveCounts[i] = entry.getValue()[0];
	        negativeCounts[i] = entry.getValue()[1];
	    }

	    // Debugging output to verify data
	    System.out.println("Positive Counts: " + Arrays.toString(positiveCounts));
	    System.out.println("Negative Counts: " + Arrays.toString(negativeCounts));

	    Map<String, String> trendMap = new LinkedHashMap<>();
	    trendMap.put("positiveTrend", determineTrend(positiveCounts));
	    trendMap.put("negativeTrend", determineTrend(negativeCounts));

	    return trendMap;
	}

	public String determineTrend(int[] counts) {
	    if (counts.length < 2) {
	        return "insufficient data";
	    }

	    int lastMonth = counts[counts.length - 1];
	    int secondLastMonth = counts[counts.length - 2];

	    System.out.println("Last month count: " + lastMonth);
	    System.out.println("Second last month count: " + secondLastMonth);

	    if (lastMonth > secondLastMonth) {
	        return "INCREASE";
	    } else if (lastMonth < secondLastMonth) {
	        return "DECREASE";
	    } else {
	        return "STAGNANT";
	    }
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
