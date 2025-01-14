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




//2 part hai
//package com.Directtickets.demo.Services;
//
//import com.Directtickets.demo.util.StationGraph;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.ObjectWriter;
//import com.fasterxml.jackson.databind.SerializationFeature;
//import com.fasterxml.jackson.databind.node.ObjectNode;
//import kong.unirest.HttpResponse;
//import kong.unirest.Unirest;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Service;
//
//import java.time.Duration;
//import java.time.LocalDate;
//import java.time.LocalTime;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.TimeUnit;
//
//import static org.springframework.security.util.FieldUtils.getFieldValue;
//
//@Service
//public class TrainSearchService {
//
//    @Autowired
//    private RedisTemplate<String, Object> redisTemplate;
//
//    @Autowired
//    private StationGraph stationGraph;
//
//    @Value("${api.irctc.url}")
//    private String apiUrl;
//
//    @Value("${api.irctc.key}")
//    private String apiKey;
//
//    @Value("${api.irctc.host}")
//    private String apiHost;
//
//    public String getTrainPath(String source, String destination, String date) throws Exception {
//        String cacheKey = "Train-Path-" + source + "-" + destination + "-" + date;
//
//        // Check cache
//        String cachedData = (String) redisTemplate.opsForValue().get(cacheKey);
//        if (cachedData != null) {
//            return cachedData;
//        }
//
//        List<List<String>> paths = stationGraph.yenKShortestPaths(source, destination, 1);
//        System.out.println("Found paths: " + paths);
//
//        Map<String, List<Object>> result = new HashMap<>();
//        result.put("directTrains", new ArrayList<>());
//        result.put("connectingTrains", new ArrayList<>());
//        boolean hasValidData = false;
//
//        // Process each path
//        for (List<String> path : paths) {
//            // Check for direct trains first
//            List<Object> directTrains = checkDirectTrains(path.get(0), path.get(path.size() - 1), date);
//            if (!directTrains.isEmpty()) {
//                result.get("directTrains").addAll(directTrains);
//                hasValidData = true;
//            }
//
//            // Check for connecting trains
//            List<Object> connectingTrains = checkConnectingTrains(path, date);
//            if (!connectingTrains.isEmpty()) {
//                result.get("connectingTrains").addAll(connectingTrains);
//                hasValidData = true;
//            }
//        }
//
//        if (!hasValidData) {
//            return "No trains found for the given source and destination.";
//        }
//
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.enable(SerializationFeature.INDENT_OUTPUT); // Enable pretty printing
//
//        ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
//        String trainPathJson = writer.writeValueAsString(result);
//
//        redisTemplate.opsForValue().set(cacheKey, trainPathJson, 5, TimeUnit.MINUTES);
//        System.out.println(trainPathJson);
//        return trainPathJson;
//    }
//
//    private List<Object> checkDirectTrains(String source, String destination, String date) throws Exception {
//        HttpResponse<String> response = makeApiRequest(source, destination, date);
//        System.out.println(response.getStatus() + " " + response.getBody());
//        if (response.getStatus() == 200) {
//            ObjectMapper objectMapper = new ObjectMapper();
//            JsonNode jsonNode = objectMapper.readTree(response.getBody());
//            return extractTrainDetails(jsonNode);
//        }
//        return new ArrayList<>();
//    }
//
//    private List<Object> checkConnectingTrains(List<String> path, String date) throws Exception {
//        List<Object> connectingTrains = new ArrayList<>();
//
//        for (int i = 0; i < path.size() - 2; i++) {
//            String firstSegmentStart = path.get(i);
//            String intermediateStation = path.get(i + 1);
//            String finalDestination = path.get(path.size() - 1);
//            System.out.println(firstSegmentStart+" "+intermediateStation+" "+finalDestination);
//            // Check first segment
//            List<Object> firstSegmentTrains = new ArrayList<>();
//            HttpResponse<String> firstResponse = makeApiRequest(firstSegmentStart, intermediateStation, date);
//            System.out.println(firstResponse.getStatus() + " " + firstResponse.getBody());
//            if (firstResponse.getStatus() == 200) {
//                ObjectMapper objectMapper = new ObjectMapper();
//                JsonNode jsonNode = objectMapper.readTree(firstResponse.getBody());
//                firstSegmentTrains = extractTrainDetails(jsonNode);
//            }
//
//            // If first segment trains found, check second segment
//            if (!firstSegmentTrains.isEmpty()) {
//                List<Object> secondSegmentTrains = new ArrayList<>();
//                HttpResponse<String> secondResponse = makeApiRequest(intermediateStation, finalDestination, date);
//                if (secondResponse.getStatus() == 200) {
//                    ObjectMapper objectMapper = new ObjectMapper();
//                    JsonNode jsonNode = objectMapper.readTree(secondResponse.getBody());
//                    secondSegmentTrains = extractTrainDetails(jsonNode);
//                }
//
//                // If both segments have trains, check for valid connections
//                if (!secondSegmentTrains.isEmpty()) {
//                    for (Object firstTrain : firstSegmentTrains) {
//                        for (Object secondTrain : secondSegmentTrains) {
//                            if (isValidConnection(firstTrain, secondTrain)) {
//                                Map<String, Object> connection = new HashMap<>();
//                                connection.put("firstTrain", firstTrain);
//                                connection.put("secondTrain", secondTrain);
//                                connection.put("connectionStation", intermediateStation);
//                                connectingTrains.add(connection);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        return connectingTrains;
//    }
//
//    private boolean isValidConnection(Object firstTrain, Object secondTrain) {
//        try {
//            String arrivalTime = (String) getFieldValue(firstTrain, "to_sta");
//            String arrivalDayStr = (String) getFieldValue(firstTrain, "to_day");
//            String departureTime = (String) getFieldValue(secondTrain, "from_std");
//            String departureDayStr = (String) getFieldValue(secondTrain, "from_day");
//
//            if (arrivalTime != null && arrivalDayStr != null && departureTime != null && departureDayStr != null) {
//                int arrivalDay = Integer.parseInt(arrivalDayStr);
//                int departureDay = Integer.parseInt(departureDayStr);
//
//                LocalTime arrival = LocalTime.parse(arrivalTime);
//                LocalTime departure = LocalTime.parse(departureTime);
//
//                long timeDifferenceInMinutes = Duration.between(arrival, departure).toMinutes();
//                if (departureDay > arrivalDay) {
//                    timeDifferenceInMinutes += 24 * 60;
//                }
//
//                System.out.println(timeDifferenceInMinutes + " time difference in minutes");
//                return timeDifferenceInMinutes >= 120 && timeDifferenceInMinutes <= 240;
//            }
//        } catch (Exception e) {
//            System.err.println("Error checking connection validity: " + e.getMessage());
//        }
//        return false;
//    }
//
//
//    private HttpResponse<String> makeApiRequest(String fromStation, String toStation, String date) throws Exception {
//        System.out.println("Date for API Request: " + date);
//        LocalDate journeyDate = LocalDate.parse(date); // Adjust pattern if necessary
//        if (journeyDate.isBefore(LocalDate.now())) {
//            throw new IllegalArgumentException("Date of journey cannot be in the past.");
//        }
//
//        return Unirest.get(apiUrl)
//                .queryString("fromStationCode", fromStation)
//                .queryString("toStationCode", toStation)
//                .queryString("dateOfJourney", date)
//                .header("x-rapidapi-key", apiKey)
//                .header("x-rapidapi-host", apiHost)
//                .asString();
//    }
//
//    private Object getFieldValue(Object train, String fieldName) {
//        try {
//            JsonNode trainNode = (JsonNode) train;
//            return trainNode.path(fieldName).asText(null); // Always return as String
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//
//    private List<Object> extractTrainDetails(JsonNode jsonNode) {
//        List<Object> trains = new ArrayList<>();
//        JsonNode trainList = jsonNode.path("data");
//        if (trainList.isArray()) {
//            for (JsonNode train : trainList) {
//                trains.add(train);
//            }
//        }
//        return trains;
//    }
//
//}


package com.Directtickets.demo.Services;

import com.Directtickets.demo.util.StationGraph;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.annotation.PreDestroy;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static org.springframework.security.util.FieldUtils.getFieldValue;

@Service
public class TrainSearchService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private StationGraph stationGraph;

    @Value("${api.irctc.url}")
    private String apiUrl;

    @Value("${api.irctc.key}")
    private String apiKey;

    @Value("${api.irctc.host}")
    private String apiHost;

    // Increased thread pool size for better parallelization
    private final ExecutorService executorService = Executors.newFixedThreadPool(100);

    public String getTrainPath(String source, String destination, String date) throws Exception {
        String cacheKey = "Train-Path-" + source + "-" + destination + "-" + date;

        // Check cache
        String cachedData = (String) redisTemplate.opsForValue().get(cacheKey);
        if (cachedData != null) {
            return cachedData;
        }

        List<List<String>> paths = stationGraph.yenKShortestPaths(source, destination, 1);
        System.out.println("Found paths: " + paths);

        Map<String, List<Object>> result = new HashMap<>();
        result.put("directTrains", Collections.synchronizedList(new ArrayList<>()));
        result.put("connectingTrains", Collections.synchronizedList(new ArrayList<>()));

        // Process all paths in parallel
        CompletableFuture<?>[] futures = paths.stream()
                .flatMap(path -> Arrays.stream(new CompletableFuture<?>[]{
                        processDirectTrains(path, result, date),
                        processConnectingTrains(path, result, date)
                }))
                .toArray(CompletableFuture[]::new);

        // Wait for all futures to complete
        CompletableFuture.allOf(futures).get(30, TimeUnit.SECONDS);

        boolean hasValidData = result.values().stream()
                .anyMatch(list -> !list.isEmpty());

        if (!hasValidData) {
            return "No trains found for the given source and destination.";
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        String trainPathJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);

        redisTemplate.opsForValue().set(cacheKey, trainPathJson, 5, TimeUnit.MINUTES);
        System.out.println(trainPathJson);
        return trainPathJson;
    }

    private CompletableFuture<Void> processDirectTrains(List<String> path, Map<String, List<Object>> result, String date) {
        return CompletableFuture.runAsync(() -> {
            try {
                List<Object> directTrains = checkDirectTrains(path.get(0), path.get(path.size() - 1), date);
                if (!directTrains.isEmpty()) {
                    result.get("directTrains").addAll(directTrains);
                }
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, executorService);
    }

    private CompletableFuture<Void> processConnectingTrains(List<String> path, Map<String, List<Object>> result, String date) {
        return CompletableFuture.runAsync(() -> {
            try {
                List<Object> connectingTrains = checkConnectingTrains(path, date);
                if (!connectingTrains.isEmpty()) {
                    result.get("connectingTrains").addAll(connectingTrains);
                }
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, executorService);
    }

    private List<Object> checkDirectTrains(String source, String destination, String date) throws Exception {
        HttpResponse<String> response = makeApiRequest(source, destination, date);
        if (response.getStatus() == 200) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            return extractTrainDetails(jsonNode);
        }
        return new ArrayList<>();
    }

    private List<Object> checkConnectingTrains(List<String> path, String date) throws Exception {
        List<Object> connectingTrains = Collections.synchronizedList(new ArrayList<>());
        List<CompletableFuture<Void>> segmentFutures = new ArrayList<>();

        for (int i = 0; i < path.size() - 2; i++) {
            final int segmentIndex = i;
            CompletableFuture<Void> segmentFuture = CompletableFuture.runAsync(() -> {
                try {
                    String firstSegmentStart = path.get(segmentIndex);
                    String intermediateStation = path.get(segmentIndex + 1);
                    String finalDestination = path.get(path.size() - 1);

                    // Parallel API requests for both segments
                    CompletableFuture<List<Object>> firstSegmentFuture = CompletableFuture.supplyAsync(() -> {
                        try {
                            HttpResponse<String> response = makeApiRequest(firstSegmentStart, intermediateStation, date);
                            if (response.getStatus() == 200) {
                                ObjectMapper objectMapper = new ObjectMapper();
                                return extractTrainDetails(objectMapper.readTree(response.getBody()));
                            }
                            return new ArrayList<>();
                        } catch (Exception e) {
                            throw new CompletionException(e);
                        }
                    }, executorService);

                    CompletableFuture<List<Object>> secondSegmentFuture = CompletableFuture.supplyAsync(() -> {
                        try {
                            HttpResponse<String> response = makeApiRequest(intermediateStation, finalDestination, date);
                            if (response.getStatus() == 200) {
                                ObjectMapper objectMapper = new ObjectMapper();
                                return extractTrainDetails(objectMapper.readTree(response.getBody()));
                            }
                            return new ArrayList<>();
                        } catch (Exception e) {
                            throw new CompletionException(e);
                        }
                    }, executorService);

                    // Wait for both segment results
                    List<Object> firstSegmentTrains = firstSegmentFuture.get();
                    List<Object> secondSegmentTrains = secondSegmentFuture.get();

                    // Process valid connections
                    if (!firstSegmentTrains.isEmpty() && !secondSegmentTrains.isEmpty()) {
                        processConnections(firstSegmentTrains, secondSegmentTrains, intermediateStation, connectingTrains);
                    }
                } catch (Exception e) {
                    throw new CompletionException(e);
                }
            }, executorService);

            segmentFutures.add(segmentFuture);
        }

        // Wait for all segment processing to complete
        CompletableFuture.allOf(segmentFutures.toArray(new CompletableFuture[0])).join();
        return connectingTrains;
    }

    private void processConnections(List<Object> firstSegmentTrains, List<Object> secondSegmentTrains,
                                    String intermediateStation, List<Object> connectingTrains) {
        firstSegmentTrains.parallelStream().forEach(firstTrain ->
                secondSegmentTrains.parallelStream().forEach(secondTrain -> {
                    if (isValidConnection(firstTrain, secondTrain)) {
                        Map<String, Object> connection = new HashMap<>();
                        connection.put("firstTrain", firstTrain);
                        connection.put("secondTrain", secondTrain);
                        connection.put("connectionStation", intermediateStation);
                        connectingTrains.add(connection);
                    }
                })
        );
    }

    private boolean isValidConnection(Object firstTrain, Object secondTrain) {
        try {
            JsonNode firstTrainNode = (JsonNode) firstTrain;
            JsonNode secondTrainNode = (JsonNode) secondTrain;

            String arrivalTime = firstTrainNode.get("to_sta").asText(null);
            String arrivalDayStr = firstTrainNode.get("to_day").asText(null);
            String departureTime = secondTrainNode.get("from_std").asText(null);
            String departureDayStr = secondTrainNode.get("from_day").asText(null);

            if (arrivalTime != null && arrivalDayStr != null && departureTime != null && departureDayStr != null) {
                int arrivalDay = Integer.parseInt(arrivalDayStr);
                int departureDay = Integer.parseInt(departureDayStr);

                LocalTime arrival = LocalTime.parse(arrivalTime);
                LocalTime departure = LocalTime.parse(departureTime);

                long timeDifferenceInMinutes = Duration.between(arrival, departure).toMinutes();
                if (departureDay > arrivalDay) {
                    timeDifferenceInMinutes += 24 * 60;
                }
                System.out.println(timeDifferenceInMinutes + " time difference in minutes");
                return timeDifferenceInMinutes >= 120 && timeDifferenceInMinutes <= 240;

            }
        } catch (Exception e) {
            System.err.println("Error checking connection validity: " + e.getMessage());
            e.printStackTrace(); // Adding stack trace for better debugging
        }
        return false;
    }

    private HttpResponse<String> makeApiRequest(String fromStation, String toStation, String date) throws Exception {
        LocalDate journeyDate = LocalDate.parse(date);
        System.out.println("Date for API Request: " + date);
        if (journeyDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Date of journey cannot be in the past.");
        }

        return Unirest.get(apiUrl)
                .queryString("fromStationCode", fromStation)
                .queryString("toStationCode", toStation)
                .queryString("dateOfJourney", date)
                .header("x-rapidapi-key", apiKey)
                .header("x-rapidapi-host", apiHost)
                .asString();
    }

    private List<Object> extractTrainDetails(JsonNode jsonNode) {
        List<Object> trains = new ArrayList<>();
        JsonNode trainList = jsonNode.path("data");
        if (trainList.isArray()) {
            for (JsonNode train : trainList) {
                trains.add(train);
            }
        }
        return trains;
    }

    @PreDestroy
    public void shutdownExecutorService() {
        try {
            executorService.shutdown();
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}