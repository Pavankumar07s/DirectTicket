//package com.Directtickets.demo.Services;
//
//import com.Directtickets.demo.util.StationGraph;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import kong.unirest.HttpResponse;
//import kong.unirest.Unirest;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Service;
//
//import java.time.Duration;
//import java.time.LocalTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//@Service
//public class TrainSearchService {
//
//    @Autowired
//    private RedisTemplate<String, Object> redisTemplate;
//
//    @Autowired
//    private StationGraph stationGraph;
//    @Value("${api.irctc.url}")
//    private String apiUrl;
//
//    @Value("${api.irctc.key}")
//    private String apiKey;
//
//    @Value("${api.irctc.host}")
//    private String apiHost;
//
//    public String getTrainPath(String source, String destination, String date) throws JsonProcessingException {
//        // Dynamic cache key for different station pairs and date
//        String cacheKey = "Train-Path-" + source + "-" + destination + "-" + date;
//
//        // Check cache
//        String cachedData = (String) redisTemplate.opsForValue().get(cacheKey);
//        if (cachedData != null) {
//            return cachedData;
//        }
//
//        // Get the K shortest paths using StationGraph
//        List<List<String>> paths = stationGraph.yenKShortestPaths(source, destination, 3);
//        System.out.println(paths);
//
//        // To hold the results for all paths
//        List<List<Object>> trainResultsForAllPaths = new ArrayList<>();
//        boolean hasValidData = false;
//
//        // Iterate over each path
//        for (List<String> path : paths) {
//            List<Object> trainsForPath = new ArrayList<>();
//            boolean validPath = true;
//
//            // Iterate over each station pair in the path
//            for (int i = 0; i < path.size() - 1; i++) {
//                String fromStation = path.get(i);
//                String toStation = path.get(i + 1);
//
//                // Make API call to fetch train details
//                HttpResponse<String> response = Unirest.get("https://irctc1.p.rapidapi.com/api/v3/trainBetweenStations")
//                        .queryString("fromStationCode", fromStation)
//                        .queryString("toStationCode", toStation)
//                        .header("x-rapidapi-key", apiKey)
//                        .header("x-rapidapi-host", apiHost)
//                        .asString();
//
//                if (response.getStatus() == 200) {
//                    String responseBody = response.getBody();
//                    ObjectMapper objectMapper = new ObjectMapper();
//                    JsonNode jsonNode = objectMapper.readTree(responseBody);
//
//                    // Extract train details from the JSON response
//                    List<Object> trains = extractTrainDetails(jsonNode);
//
//                    if (!trains.isEmpty()) {
//                        if (i > 0) {
//                            // Check the time gap with the previous train
//                            Object previousTrain = trainsForPath.get(trainsForPath.size() - 1);
//
//                            String previousArrivalTime = (String) getFieldValue(previousTrain, "to_sta");
//                            Integer previousArrivalDay = (Integer) getFieldValue(previousTrain, "to_day");
//
//                            String currentDepartureTime = (String) getFieldValue(trains.get(0), "from_std");
//                            Integer currentDepartureDay = (Integer) getFieldValue(trains.get(0), "from_day");
//
//                            if (previousArrivalTime != null && previousArrivalDay != null && currentDepartureTime != null && currentDepartureDay != null) {
//                                LocalTime prevArrival = LocalTime.parse(previousArrivalTime);
//                                LocalTime currDeparture = LocalTime.parse(currentDepartureTime);
//
//                                long timeDifferenceInMinutes = Duration.between(prevArrival, currDeparture).toMinutes();
//                                if (currentDepartureDay > previousArrivalDay) {
//                                    timeDifferenceInMinutes += 24 * 60;
//                                }
//
//                                if (timeDifferenceInMinutes < 120 || timeDifferenceInMinutes > 240) {
//                                    validPath = false;
//                                    break;
//                                }
//                            } else {
//                                validPath = false;
//                                break;
//                            }
//                        }
//
//                        trainsForPath.addAll(trains);
//                        hasValidData = true;
//                    } else {
//                        System.out.println("No trains found between " + fromStation + " and " + toStation);
//                        validPath = false;
//                        break;
//                    }
//                } else {
//                    System.out.println("Failed to fetch data for " + fromStation + " to " + toStation + ": " + response.getStatus());
//                    validPath = false;
//                    break;
//                }
//            }
//
//            if (validPath) {
//                trainResultsForAllPaths.add(trainsForPath);
//            }
//        }
//
//        if (!hasValidData) {
//            return "No trains found for the given source and destination.";
//        }
//
//        String trainPathJson = new ObjectMapper().writeValueAsString(trainResultsForAllPaths);
//        redisTemplate.opsForValue().set(cacheKey, trainPathJson, 5, TimeUnit.MINUTES);
//
//        return trainPathJson;
//    }
//
//    private List<Object> extractTrainDetails(JsonNode jsonNode) {
//        List<Object> trains = new ArrayList<>();
//        JsonNode trainList = jsonNode.path("trains");
//        if (trainList.isArray()) {
//            for (JsonNode train : trainList) {
//                // Extract train details and add to the list
//                trains.add(train);
//            }
//        }
//        return trains;
//    }
//
//    private Object getFieldValue(Object train, String fieldName) {
//        try {
//            return null; // Replace with actual field extraction logic
//        } catch (NullPointerException e) {
//            return null;
//        }
//    }
//}
package com.Directtickets.demo.Services;

import com.Directtickets.demo.util.StationGraph;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TrainSearchService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final StationGraph stationGraph;
    private final ObjectMapper objectMapper;
    private final ExecutorService executorService;

    @Value("${api.irctc.url}")
    private String apiUrl;

    @Value("${api.irctc.key}")
    private String apiKey;

    @Value("${api.irctc.host}")
    private String apiHost;

    private static final int CACHE_DURATION_MINUTES = 5;
    private static final int MIN_TRANSFER_TIME_MINUTES = 120;
    private static final int MAX_TRANSFER_TIME_MINUTES = 240;
    private static final int MAX_PATHS = 3;

    public TrainSearchService(RedisTemplate<String, Object> redisTemplate,
                              StationGraph stationGraph,
                              ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.stationGraph = stationGraph;
        this.objectMapper = objectMapper;
        this.executorService = Executors.newFixedThreadPool(10);
    }

    public String getTrainPath(String source, String destination, String date) throws JsonProcessingException {
        String cacheKey = generateCacheKey(source, destination, date);

        return Optional.ofNullable(getCachedResult(cacheKey))
                .orElseGet(() -> searchAndCacheTrains(source, destination, date, cacheKey));
    }

    private String generateCacheKey(String source, String destination, String date) {
        return String.format("Train-Path-%s-%s-%s", source, destination, date);
    }

    private String getCachedResult(String cacheKey) {
        return (String) redisTemplate.opsForValue().get(cacheKey);
    }

    private String searchAndCacheTrains(String source, String destination, String date, String cacheKey) {
        try {
            List<List<String>> paths = stationGraph.yenKShortestPaths(source, destination, MAX_PATHS);
            System.out.println(paths);
            List<CompletableFuture<List<Object>>> futures = paths.stream()
                    .map(path -> CompletableFuture.supplyAsync(() -> processPath(path), executorService))
                    .collect(Collectors.toList());

            List<List<Object>> validPaths = futures.stream()
                    .map(CompletableFuture::join)
                    .filter(trainList -> !trainList.isEmpty())
                    .collect(Collectors.toList());

            if (validPaths.isEmpty()) {
                return "No trains found for the given source and destination.";
            }

            String result = objectMapper.writeValueAsString(validPaths);
            cacheResult(cacheKey, result);
            return result;

        } catch (Exception e) {
            log.error("Error searching trains: ", e);
            return "Error occurred while searching trains.";
        }
    }

    private List<Object> processPath(List<String> path) {
        List<Object> trainsForPath = new ArrayList<>();

        for (int i = 0; i < path.size() - 1; i++) {
            Optional<List<Object>> trainSegment = fetchTrainSegment(path.get(i), path.get(i + 1));

            if (!trainSegment.isPresent() || !isValidConnection(trainsForPath, trainSegment.get())) {
                return new ArrayList<>();
            }

            trainsForPath.addAll(trainSegment.get());
        }

        return trainsForPath;
    }

    private Optional<List<Object>> fetchTrainSegment(String fromStation, String toStation) {
        try {
            HttpResponse<String> response = Unirest.get("https://irctc1.p.rapidapi.com/api/v3/trainBetweenStations")
                    .queryString("fromStationCode", fromStation)
                    .queryString("toStationCode", toStation)
                    .header("x-rapidapi-key", apiKey)
                    .header("x-rapidapi-host", apiHost)
                    .asString();

            if (response.getStatus() == 200) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                List<Object> trains = extractTrainDetails(jsonNode);
                return Optional.of(trains);
            }
        } catch (Exception e) {
            log.error("Error fetching train segment: {} to {}", fromStation, toStation, e);
        }
        return Optional.empty();
    }

    private boolean isValidConnection(List<Object> existingTrains, List<Object> newTrains) {
        if (existingTrains.isEmpty() || newTrains.isEmpty()) {
            return true;
        }

        Object lastTrain = existingTrains.get(existingTrains.size() - 1);
        Object nextTrain = newTrains.get(0);

        LocalTime prevArrival = parseTime(getFieldValue(lastTrain, "to_sta"));
        LocalTime nextDeparture = parseTime(getFieldValue(nextTrain, "from_std"));

        if (prevArrival == null || nextDeparture == null) {
            return false;
        }

        long timeDifference = Duration.between(prevArrival, nextDeparture).toMinutes();
        Integer dayDifference = (Integer) getFieldValue(nextTrain, "from_day") -
                (Integer) getFieldValue(lastTrain, "to_day");

        timeDifference += (dayDifference * 24 * 60);

        return timeDifference >= MIN_TRANSFER_TIME_MINUTES &&
                timeDifference <= MAX_TRANSFER_TIME_MINUTES;
    }

    private LocalTime parseTime(Object timeStr) {
        try {
            return timeStr != null ? LocalTime.parse((String) timeStr) : null;
        } catch (Exception e) {
            return null;
        }
    }

    private void cacheResult(String cacheKey, String result) {
        redisTemplate.opsForValue().set(cacheKey, result, CACHE_DURATION_MINUTES, TimeUnit.MINUTES);
    }

    private List<Object> extractTrainDetails(JsonNode jsonNode) {
        List<Object> trains = new ArrayList<>();
        JsonNode trainList = jsonNode.path("trains");
        if (trainList.isArray()) {
            trainList.forEach(trains::add);
        }
        return trains;
    }

    private Object getFieldValue(Object train, String fieldName) {
        try {
            return ((JsonNode) train).get(fieldName).asText();
        } catch (Exception e) {
            return null;
        }
    }
}