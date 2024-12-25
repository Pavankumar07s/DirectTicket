package com.Directtickets.demo.Controllers;

import com.Directtickets.demo.Services.TrainSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/public/train-search")
public class TrainSearchController {

    @Autowired
    private TrainSearchService trainSearchService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> getRunningStatus(@RequestBody Map<String, Object> requestData) {
        try {
            String source = (String) requestData.get("From");
            String destination = (String) requestData.get("To");
            String date = (String) requestData.get("Date");

            if (source == null || destination == null || date == null) {
                throw new IllegalArgumentException("Missing 'From', 'To', or 'Date' parameters");
            }

            // Validate the date format if necessary
            if (!isValidDate(date)) {
                throw new IllegalArgumentException("Invalid date format. Expected format: YYYY-MM-DD");
            }

            String status = trainSearchService.getTrainPath(source, destination, date);

            Map<String, Object> response = new HashMap<>();
            response.put("status", status);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            // Log the error (optional)
            System.err.println("Invalid request: " + e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Log the error (optional)
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isValidDate(String date) {
        // Add validation logic for the date (e.g., regex or a date parsing library)
        return date.matches("\\d{4}-\\d{2}-\\d{2}");
    }
}
