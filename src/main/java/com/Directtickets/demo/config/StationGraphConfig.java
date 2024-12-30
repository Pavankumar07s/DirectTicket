package com.Directtickets.demo.config;

import com.Directtickets.demo.util.StationGraph;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StationGraphConfig {

    @Bean
    public StationGraph stationGraph() {
        StationGraph graph = new StationGraph();

        // Add major stations (nodes) with station codes
        graph.addStation("PUNE");   // Pune
        graph.addStation("BVI");    // Mumbai (Bandra Terminus)
        graph.addStation("NDLS");   // New Delhi
        graph.addStation("MAS");    // Chennai
        graph.addStation("SBC");    // Bangalore
        graph.addStation("HYB");    // Hyderabad
        graph.addStation("HKG");    // Kolkata
        graph.addStation("JP");     // Jaipur
        graph.addStation("ADI");    // Ahmedabad
        graph.addStation("LKO");    // Lucknow
        graph.addStation("BPL");    // Bhopal
        graph.addStation("PNBE");    // Patna
        graph.addStation("NGP");    // Nagpur
        graph.addStation("CTC");    // Cuttack
        graph.addStation("VSKP");   // Visakhapatnam
        graph.addStation("GNT");    // Guntur
        graph.addStation("AGC");    // Agra
        graph.addStation("GZB");    // Ghaziabad
        graph.addStation("SBP");    // Sambalpur
        graph.addStation("JPR");    // Jodhpur
        graph.addStation("SUR");    // Surat
        graph.addStation("INDB");   // Indore
        graph.addStation("MDU");    // Madurai

        // Add routes (edges) with distances (weights) using station shortcuts
        graph.addRoute("PUNE", "BVI", 3);    // Pune to Mumbai
        graph.addRoute("PUNE", "NDLS", 6);   // Pune to Delhi
        graph.addRoute("BVI", "NDLS", 4);    // Mumbai to Delhi
        graph.addRoute("NDLS", "MAS", 5);    // Delhi to Chennai
        graph.addRoute("MAS", "SBC", 2);     // Chennai to Bangalore
        graph.addRoute("SBC", "HYB", 2);     // Bangalore to Hyderabad
        graph.addRoute("HYB", "NGP", 3);     // Hyderabad to Nagpur
        graph.addRoute("NGP", "BPL", 4);     // Nagpur to Bhopal
        graph.addRoute("BPL", "NDLS", 2);    // Bhopal to Delhi
        graph.addRoute("ADI", "BVI", 4);     // Ahmedabad to Mumbai
        graph.addRoute("ADI", "JP", 3);      // Ahmedabad to Jaipur
        graph.addRoute("JP", "NDLS", 2);     // Jaipur to Delhi
        graph.addRoute("JP", "LKO", 5);      // Jaipur to Lucknow
        graph.addRoute("LKO", "PNBE", 3);     // Lucknow to Patna
        graph.addRoute("PNBE", "HKG", 4);     // Patna to Kolkata
        graph.addRoute("HKG", "VSKP", 5);    // Kolkata to Visakhapatnam
        graph.addRoute("VSKP", "HYB", 2);    // Visakhapatnam to Hyderabad
        graph.addRoute("HYB", "GNT", 2);     // Hyderabad to Guntur
        graph.addRoute("GNT", "MAS", 3);     // Guntur to Chennai
        graph.addRoute("MAS", "CTC", 4);     // Chennai to Cuttack
        graph.addRoute("CTC", "SBP", 3);     // Cuttack to Sambalpur
        graph.addRoute("SBP", "BPL", 2);     // Sambalpur to Bhopal
        graph.addRoute("BPL", "GZB", 6);     // Bhopal to Ghaziabad
        graph.addRoute("GZB", "JP", 3);      // Ghaziabad to Jaipur
        graph.addRoute("JP", "PUNE", 7);     // Jaipur to Pune
        graph.addRoute("PUNE", "GNT", 8);    // Pune to Guntur
        graph.addRoute("ADI", "GZB", 5);     // Ahmedabad to Ghaziabad
        graph.addRoute("SUR", "SBC", 5);     // Surat to Bangalore
        graph.addRoute("SBC", "INDB", 3);    // Bangalore to Indore
        graph.addRoute("INDB", "BPL", 4);    // Indore to Bhopal
        graph.addRoute("MDU", "MAS", 2);     // Madurai to Chennai
        graph.addRoute("AGC", "NDLS", 4);    // Agra to Delhi
        graph.addRoute("NDLS", "LKO", 3);    // Delhi to Lucknow
        graph.addRoute("LKO", "BPL", 2);     // Lucknow to Bhopal
        graph.addRoute("BPL", "SUR", 4);     // Bhopal to Surat
        graph.addRoute("SUR", "HKG", 6);     // Surat to Kolkata
        graph.addRoute("HKG", "VSKP", 7);    // Kolkata to Visakhapatnam
        graph.addRoute("VSKP", "AGC", 5);    // Visakhapatnam to Agra
        graph.addRoute("AGC", "GZB", 4);     // Agra to Ghaziabad
        graph.addRoute("GZB", "SBP", 6);     // Ghaziabad to Sambalpur
        graph.addRoute("SBP", "SBC", 7);     // Sambalpur to Bangalore
        graph.addRoute("SBC", "HYB", 3);     // Bangalore to Hyderabad
        graph.addRoute("HYB", "GNT", 3);     // Hyderabad to Guntur
        graph.addRoute("GNT", "AGC", 4);     // Guntur to Agra

        return graph;
    }
}
