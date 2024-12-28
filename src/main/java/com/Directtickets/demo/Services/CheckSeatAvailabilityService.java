package com.Directtickets.demo.Services;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class CheckSeatAvailabilityService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Value("${api.irctc.url}")
    private String apiUrl;

    @Value("${api.irctc.key}")
    private String apiKey;

    @Value("${api.irctc.host}")
    private String apiHost;
    public String checkSeatAvailability(String trainNo, String fromStationCode, String toStationCode, String classType, String quota) {
        String cacheKey = "seatAvailability:" + trainNo + ":" + fromStationCode + ":" + toStationCode + ":" + classType + ":" + quota;
        String cachedResponse = (String) redisTemplate.opsForValue().get(cacheKey);

        if (cachedResponse != null) {
            return cachedResponse;
        }

        try {
            HttpResponse<String> response = Unirest.get("https://irctc1.p.rapidapi.com/api/v1/checkSeatAvailability?trainNo=" + trainNo +
                            "&fromStationCode=" + fromStationCode +
                            "&toStationCode=" + toStationCode +
                            "&classType=" + classType +
                            "&quota=" + quota)
                    .header("x-rapidapi-key", apiKey)
                    .header("x-rapidapi-host", apiHost)
                    .asString();

            redisTemplate.opsForValue().set(cacheKey, response.getBody(), 10, TimeUnit.MINUTES);
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error checking seat availability";
        }
    }
}

