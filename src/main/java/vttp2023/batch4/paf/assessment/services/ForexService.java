package vttp2023.batch4.paf.assessment.services;

import java.io.StringReader;
import java.math.BigDecimal;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

@Service
public class ForexService {
	private RestTemplate restTemplate = new RestTemplate();

	private static final String URL_FOREX = "https://api.frankfurter.app/latest";

	// TODO: Task 5 
	public float convert(String from, String to, float amount) {
		String uri = URL_FOREX.concat("?base=%s&symbols=%s".formatted(from, to));
		RequestEntity<Void> req = RequestEntity.get(uri).build();
		try {
			ResponseEntity<String> response = restTemplate.exchange(req, String.class);
			
			JsonReader reader = Json.createReader(new StringReader(response.getBody()));
			JsonObject obj = reader.readObject();

			JsonObject rates = obj.getJsonObject("rates");
			float rate = BigDecimal.valueOf(rates.getJsonNumber(to.toUpperCase()).doubleValue()).floatValue();

			return amount * rate;

		} catch (HttpStatusCodeException e) {
			return -1000f;
		}
	}
}
