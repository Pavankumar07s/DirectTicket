package com.Directtickets.demo.Controllers;

import com.Directtickets.demo.Services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/trains")
public class TrainController {

    @Autowired
    private SearchTrainService searchTrainService;

    @Autowired
    private GetTrainLiveStatusService getTrainLiveStatusService;

    @Autowired
    private GetTrainScheduleService getTrainScheduleService;

    @Autowired
    private GetPNRStatusService getPNRStatusService;

    @Autowired
    private CheckSeatAvailabilityService checkSeatAvailabilityService;

    @Autowired
    private GetFareService getFareService;

    @GetMapping("/search")
    public ResponseEntity<String> searchTrain(@RequestParam String query) {
        String response = searchTrainService.searchTrain(query);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/liveStatus")
    public ResponseEntity<String> getLiveStatus(@RequestParam String trainNo, @RequestParam int startDay) {
        String response = getTrainLiveStatusService.getLiveStatus(trainNo, startDay);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/schedule")
    public ResponseEntity<String> getTrainSchedule(@RequestParam String trainNo) {
        String response = getTrainScheduleService.getSchedule(trainNo);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pnrStatus")
    public ResponseEntity<String> getPNRStatus(@RequestParam String pnr) {
        String response = getPNRStatusService.getPNRStatus(pnr);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/seatAvailability")
    public ResponseEntity<String> checkSeatAvailability(@RequestParam String trainNo,
                                                        @RequestParam String fromStationCode,
                                                        @RequestParam String toStationCode,
                                                        @RequestParam String classType,
                                                        @RequestParam String quota) {
        String response = checkSeatAvailabilityService.checkSeatAvailability(trainNo, fromStationCode, toStationCode, classType, quota);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/fare")
    public ResponseEntity<String> getFare(@RequestParam String trainNo,
                                          @RequestParam String fromStationCode,
                                          @RequestParam String toStationCode) {
        String response = getFareService.getFare(trainNo, fromStationCode, toStationCode);
        return ResponseEntity.ok(response);
    }
}
