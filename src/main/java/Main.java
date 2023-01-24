import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.springframework.aop.scope.ScopedProxyUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class Main {
    public static void main(String[] args) throws ParseException, FileNotFoundException, IOException {
        //read json file data to String
        byte[] jsonData = Files.readAllBytes(Paths.get("src/main/resources/BE_data.json"));

        //create ObjectMapper instance
        ObjectMapper objectMapper = new ObjectMapper();

        //create tree from Json
        JsonNode rootNode = objectMapper.readTree(jsonData);

        //create eventsNode with all data under "Events" path
        JsonNode eventsNode = rootNode.path("Events");
        //create List with Event class objects
        List<Event> eventList = objectMapper.readValue(eventsNode.toString(), new TypeReference<List<Event>>() {
        });

        List<Integer> list = new ArrayList<>();
        list = mostProbableResults(eventList);

        printEvents(eventList, list, 10);

    }
    //method which return list of indexes of events in order from the most probable result
    public static List<Integer> mostProbableResults(List<Event> eventList) {
        //Mapping the value of the highest probability in particular event(Double) and the index of that event(Integer)
        HashMap<Double, Integer> map = new HashMap<Double, Integer>();
        List<Double> keysList = new ArrayList<Double>();
        List<Integer> list = new ArrayList<Integer>();

        int i = 0;
        for (Event event : eventList) {

            double max = 0;
            double awayWin = event.getProbability_away_team_winner();
            double homeWin = event.getProbability_home_team_winner();
            double draw = event.getProbability_draw();
            //checking what is the highest probability result in this event
            if ((homeWin <= draw) && (awayWin <= draw)) {
                max = draw;
            } else if (draw <= homeWin & awayWin <= homeWin) {
                max = homeWin;
            } else {
                max = awayWin;
            }
            //set value of the highest probability as a key, and the index of event as value(value in HashMap)
            map.put(max, i);
            keysList.add(max);
            i += 1;
        }
        //sorting the List of keys in order from the highest
        Collections.sort(keysList, Collections.reverseOrder());
        //filling the list with indexes of events in order from that with the highest probability result
        for (Double key : keysList) {
            list.add(map.get(key));
        }
        return list;
    }

    //method which print user chosen quantity of matches in correct order
    public static void printEvents(List<Event> eventsList, List<Integer> list, int howManyMatches) {
        for (int i = 0; i < howManyMatches; i++) {
            Event thisEvent = eventsList.get(list.get(i));
            String startDate = thisEvent.getStart_date().toString();
            Competitor homeTeam = thisEvent.getCompetitors().get(0);
            Competitor awayTeam = thisEvent.getCompetitors().get(1);
            System.out.println("\n----------------------------------" + (i + 1) + "----------------------------------");
            System.out.println("Start date: " + startDate.substring(0, 10) + " " + startDate.substring(11, 19) + ",");
            System.out.println(homeTeam.getName() + " (" + homeTeam.getCountry() + ") vs. " + awayTeam.getName() + " (" + awayTeam.getCountry() + "),");
            try {
                System.out.println("Venue: " + thisEvent.getVenue().getName() + ",");
            } catch (Exception e) {
                System.out.println("Venue: null");
            }
            System.out.println("Highest probable result: " + thisEvent.theMostProbableResultInEvent());
            System.out.println("----------------------------------------------------------------------");

        }
    }
    }
