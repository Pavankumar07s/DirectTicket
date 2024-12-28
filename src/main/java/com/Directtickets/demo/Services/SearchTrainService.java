package com.Directtickets.demo.Services;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class SearchTrainService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Value("${api.irctc.url}")
    private String apiUrl;

    @Value("${api.irctc.key}")
    private String apiKey;

    @Value("${api.irctc.host}")
    private String apiHost;

    public String searchTrain(String query) {
        String cacheKey = "searchTrain:" + query;
        String cachedResponse = (String) redisTemplate.opsForValue().get(cacheKey);

        if (cachedResponse != null) {
            return cachedResponse;
        }

        try {
            HttpResponse<String> response = Unirest.get("https://irctc1.p.rapidapi.com/api/v1/searchTrain?query=" + query)
                    .header("x-rapidapi-key", apiKey)
                    .header("x-rapidapi-host", apiHost)
                    .asString();

            redisTemplate.opsForValue().set(cacheKey, response.getBody(), 10, TimeUnit.MINUTES);
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error fetching train data";
        }
    }
}
