package com.Directtickets.demo.Services;

import com.Directtickets.demo.util.StationGraph;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class TrainSearchService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private StationGraph stationGraph;

    public String getTrainPath(String source, String destination, String date) throws JsonProcessingException {
        // Dynamic cache key for different station pairs and date
        String cacheKey = "Train-Path-" + source + "-" + destination + "-" + date;

        // Check cache
        String cachedData = (String) redisTemplate.opsForValue().get(cacheKey);
        if (cachedData != null) {
            return cachedData;
        }

        // Get the K shortest paths using StationGraph
        List<List<String>> paths = stationGraph.yenKShortestPaths(source, destination, 3);
        System.out.println(paths);

        // To hold the results for all paths
        List<List<Object>> trainResultsForAllPaths = new ArrayList<>();
        boolean hasValidData = false;

        // Iterate over each path
        for (List<String> path : paths) {
            List<Object> trainsForPath = new ArrayList<>();
            boolean validPath = true;

            // Iterate over each station pair in the path
            for (int i = 0; i < path.size() - 1; i++) {
                String fromStation = path.get(i);
                String toStation = path.get(i + 1);

                // Make API call to fetch train details
                HttpResponse<String> response = Unirest.get("https://irctc1.p.rapidapi.com/api/v3/trainBetweenStations")
                        .queryString("fromStationCode", fromStation)
                        .queryString("toStationCode", toStation)
                        .header("x-rapidapi-key", "c4f00fe4dcmsh8d534755e3441b1p19e015jsn2aef2c24c556")
                        .header("x-rapidapi-host", "irctc1.p.rapidapi.com")
                        .asString();

                if (response.getStatus() == 200) {
                    String responseBody = response.getBody();
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonNode = objectMapper.readTree(responseBody);

                    // Extract train details from the JSON response
                    List<Object> trains = extractTrainDetails(jsonNode);

                    if (!trains.isEmpty()) {
                        if (i > 0) {
                            // Check the time gap with the previous train
                            Object previousTrain = trainsForPath.get(trainsForPath.size() - 1);

                            String previousArrivalTime = (String) getFieldValue(previousTrain, "to_sta");
                            Integer previousArrivalDay = (Integer) getFieldValue(previousTrain, "to_day");

                            String currentDepartureTime = (String) getFieldValue(trains.get(0), "from_std");
                            Integer currentDepartureDay = (Integer) getFieldValue(trains.get(0), "from_day");

                            if (previousArrivalTime != null && previousArrivalDay != null && currentDepartureTime != null && currentDepartureDay != null) {
                                LocalTime prevArrival = LocalTime.parse(previousArrivalTime);
                                LocalTime currDeparture = LocalTime.parse(currentDepartureTime);

                                long timeDifferenceInMinutes = Duration.between(prevArrival, currDeparture).toMinutes();
                                if (currentDepartureDay > previousArrivalDay) {
                                    timeDifferenceInMinutes += 24 * 60;
                                }

                                if (timeDifferenceInMinutes < 120 || timeDifferenceInMinutes > 240) {
                                    validPath = false;
                                    break;
                                }
                            } else {
                                validPath = false;
                                break;
                            }
                        }

                        trainsForPath.addAll(trains);
                        hasValidData = true;
                    } else {
                        System.out.println("No trains found between " + fromStation + " and " + toStation);
                        validPath = false;
                        break;
                    }
                } else {
                    System.out.println("Failed to fetch data for " + fromStation + " to " + toStation + ": " + response.getStatus());
                    validPath = false;
                    break;
                }
            }

            if (validPath) {
                trainResultsForAllPaths.add(trainsForPath);
            }
        }

        if (!hasValidData) {
            return "No trains found for the given source and destination.";
        }

        String trainPathJson = new ObjectMapper().writeValueAsString(trainResultsForAllPaths);
        redisTemplate.opsForValue().set(cacheKey, trainPathJson, 5, TimeUnit.MINUTES);

        return trainPathJson;
    }

    private List<Object> extractTrainDetails(JsonNode jsonNode) {
        List<Object> trains = new ArrayList<>();
        JsonNode trainList = jsonNode.path("trains");
        if (trainList.isArray()) {
            for (JsonNode train : trainList) {
                // Extract train details and add to the list
                trains.add(train);
            }
        }
        return trains;
    }

    private Object getFieldValue(Object train, String fieldName) {
        try {
            return null; // Replace with actual field extraction logic
        } catch (NullPointerException e) {
            return null;
        }
    }
}
