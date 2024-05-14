package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import data_structures.ArrayList;
import data_structures.ArrayListStack;
import data_structures.HashSet;
import data_structures.HashTableSC;
import data_structures.LinkedListStack;
import data_structures.LinkedStack;
import data_structures.SimpleHashFunction;
import interfaces.List;
import interfaces.Map;
import interfaces.Set;
import interfaces.Stack;


/**
 * class that manages train stations and provides functionality to find
 * shortest distances between stations, calculate travel times, and trace routes.
 */
public class TrainStationManager {
	
	private Map<String,List<Station>> Stations;
	private Map<String,Station> Shortest_distance;
	private Map<String, Double> travelTimes;
	
	private Stack<Station> toVisit_Stack;
	private Set<Station> Visited_Set;
	
	
    /**
     * Constructor to initialize TrainStationManager with station data and their routes from a file.
     *
     * @param station_file the name of the file containing station data
     */
	public TrainStationManager(String station_file) {
		
		this.Stations = new HashTableSC<String, List<Station>>(1, new SimpleHashFunction<String>());
		this.Shortest_distance = new HashTableSC<String, Station>(1, new SimpleHashFunction<String>());
		this.travelTimes = new HashTableSC<String, Double>(1, new SimpleHashFunction<String>());
		
		this.toVisit_Stack = new LinkedStack<Station>();
		this.Visited_Set = new HashSet<Station>();
		
		try{
			BufferedReader reader_stations = new BufferedReader(new FileReader("inputFiles/" + station_file));
			String data = "";
			reader_stations.readLine();

			while((data = reader_stations.readLine()) != null) {
			
				
				String[] splitted = data.split(",");
				
				if (!Stations.containsKey(splitted[0])) {
					ArrayList<Station> lista = new ArrayList<Station>();
					Stations.put(splitted[0], lista);
				} 
				
				if (!Stations.containsKey(splitted[1])) {		
					ArrayList<Station> lista = new ArrayList<Station>();
					Stations.put(splitted[1], lista);
				} 
				
				Station estacion0 = new Station(splitted[0],Integer.parseInt(splitted[2]));
				Station estacion1 = new Station(splitted[1],Integer.parseInt(splitted[2]));
				
				
				Stations.get(splitted[0]).add(estacion1);
				Stations.get(splitted[1]).add(estacion0);
				
				
			}
			reader_stations.close();
			
		}catch(IOException e){
			System.out.println("Problema reading stations, from:" + station_file);
			e.printStackTrace();
		}
		
		findShortestDistance();

	}
	
	
	
    /**
     * Finds the shortest distances between stations using Dijkstra's algorithm.
     */
	private void findShortestDistance() {
				
		for (String station: Stations.getKeys()) { 
			
			Shortest_distance.put(station, new Station("Westside", Integer.MAX_VALUE));
		}
		
		Shortest_distance.get("Westside").setDistance(0);
		toVisit_Stack.push(Shortest_distance.get("Westside"));

		
		while (!toVisit_Stack.isEmpty()) {
			
			Station currentStation = toVisit_Stack.pop();
			Visited_Set.add(currentStation);
			

			
			for (Station neighbor: Stations.get(currentStation.getCityName())) {
				
				int A = Shortest_distance.get(neighbor.getCityName()).getDistance();
				int B = Shortest_distance.get(currentStation.getCityName()).getDistance();
				int C = neighbor.getDistance();
				int D = B + C;
			
				
				if (A > D) {
					Shortest_distance.get(neighbor.getCityName()).setCityName(currentStation.getCityName());
					Shortest_distance.get(neighbor.getCityName()).setDistance(D);
				}
				
				
				if (!Visited_Set.isMember(neighbor)) {
					toVisit_Stack.push(neighbor);
					sortStack(neighbor,toVisit_Stack);
				}
				
			}
		}
		
		
		
	}

	
	
	
    /**
     * Sorts the given stack based on station distances.
     *
     * @param station the station to be added
     * @param stackToSort the stack to be sorted
     */
	public void sortStack(Station station, Stack<Station> stackToSort) {
		
		ArrayListStack<Station> tempStack = new ArrayListStack<Station>();
		
		while (!stackToSort.isEmpty() && station.getDistance() > stackToSort.top().getDistance()) {
			tempStack.push(stackToSort.pop());
		}
		
		stackToSort.push(station);
		
		while(!tempStack.isEmpty()) {
			stackToSort.push(tempStack.pop());
		}
	}
	
	
	
	
    /**
     * Calculates the travel times from "Westside" to each station based on shortest distances.s
     *
     * @return a Map containing station names as keys and corresponding travel times in minutes as values
     */
	public Map<String, Double> getTravelTimes() {
		// 5 minutes per kilometer
		// 15 min per station
		
		 travelTimes.put("Westside", 0.0);
		 
		 for (String station : Shortest_distance.getKeys()) {
		 
		        if (station.equals("Westside")) { 
		        	continue;
		        }
		   
		        String currentstation = station;
		        int count = 0;
		        
		        while (!currentstation.equals("Westside")) {

		            count++;
		            currentstation = Shortest_distance.get(currentstation).getCityName();
		        }
	
		        int distancia = Shortest_distance.get(station).getDistance();
		        double tiempo = (distancia * 2.5) + (15 * (count - 1)); 
		        
		        travelTimes.put(station, tiempo);
		    }
		    
		    return travelTimes;

	}
	
	
	
	/**
     * Retrieves the map of stations.
     *
     * @return the map containing station names as keys and lists of stations as values
     */
	public Map<String, List<Station>> getStations() {
		return this.Stations;
	}


    /**
     * Sets the map of stations.
     *
     * @param cities the map containing station names as keys and lists of stations as values
     */

	public void setStations(Map<String, List<Station>> cities) {
		this.Stations = cities;
	}


    /**
     * Retrieves the map of shortest routes.
     *
     * @return the map containing station names as keys and shortest route stations as values
     */
	public Map<String, Station> getShortestRoutes() {
		return this.Shortest_distance;
	}


    /**
     * Sets the map of shortest routes.
     *
     * @param shortestRoutes the map containing station names as keys and shortest route stations as values
     */
	public void setShortestRoutes(Map<String, Station> shortestRoutes) {
		this.Shortest_distance = shortestRoutes;
	}
	
	/**
	 * BONUS EXERCISE THIS IS OPTIONAL
	 * Returns the path to the station given. 
	 * The format is as follows: Westside->stationA->.....stationZ->stationName
	 * Each station is connected by an arrow and the trace ends at the station given.
	 * 
	 * @param stationName - Name of the station whose route we want to trace
	 * @return (String) String representation of the path taken to reach stationName.
	 */
	public String traceRoute(String stationName) {
		
	    if (stationName.equals("Westside")) {
	        return stationName;
	    }

	    String prevstation = Shortest_distance.get(stationName).getCityName();
	    
	    String route = traceRoute(prevstation);

	    return route + "->" + stationName;
		
		
	}

}