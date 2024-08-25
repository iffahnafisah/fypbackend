package com.example.demo.filemanager.service;

import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileManagerService {
	
	RestTemplate restTemplate = new RestTemplate();

	public void uploadToPython(MultipartFile csvFile) throws Exception {
        // Set the headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // Convert MultipartFile to InputStreamResource
        InputStreamResource fileResource = new InputStreamResource(csvFile.getInputStream()) {
            @Override
            public String getFilename() {
                return csvFile.getOriginalFilename();
            }
        };

        // Create the body map with the file
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("csvFile", fileResource);

        // Create the HttpEntity containing the headers and the body
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        String url = "URL FOR PYTHON";
        
        // Create a RestTemplate that disables SSL verification
        SSLContext sslContext = SSLContextBuilder
                .create()
                .loadTrustMaterial(TrustAllStrategy.INSTANCE)
                .build();

        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLContext(sslContext)
                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .build();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory((HttpClient) httpClient);
        RestTemplate restTemplate = new RestTemplate(factory);
    	
        ResponseEntity<JomComAuthResponse> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, JomComAuthResponse.class);
        
        if(response.hasBody()) {
//        	log.info("Info from JOMCOM auth: " + response.getBody());
        } else {
//        	log.info("Info from JOMCOM auth: No body returned with status " + response.getStatusCode());
        }
        
        return response.getBody();
	}
	
}
