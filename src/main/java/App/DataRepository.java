package App;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
@Repository
public class DataRepository {
    //List to store Event objects
    private List<Event> eventList = new ArrayList<>();

    private List<Integer> mostProbableResults = new ArrayList<>();

    public List<Event> getEventList() {
        readJsonFile();
        return eventList;
    }
    public List<Integer> getMostProbableResults() {
        readJsonFile();
        mostProbableResultsVoid();
        return mostProbableResults;
    }

    private void readJsonFile()  {
        byte[] jsonData = new byte[0];
        try {
        jsonData = Files.readAllBytes(Paths.get("src/main/resources/BE_data.json"));
        ObjectMapper objectMapper = new ObjectMapper();
        //create tree from Json
        JsonNode rootNode;
        rootNode = objectMapper.readTree(jsonData);
        //create eventsNode with all data under "Events" path
        JsonNode eventsNode = rootNode.path("Events");
        //map eventList  with Event class objects stored in JsonNode
        eventList = objectMapper.readValue(eventsNode.toString(), new TypeReference<List<Event>>() {});
        } catch (IOException e) {
            System.out.println("InReadJsonFileException");
        }
    }

    private void mostProbableResultsVoid() {
        //Mapping the value of the highest probability in particular event(Double) and the index of that event(Integer)
        HashMap<Double, Integer> map = new HashMap<>();
        //the list to store keys from HashMap
        List<Double> keysList = new ArrayList<>();
        //the list to store indexes of events in order from that with the highest probability result
        List<Integer> listOfIndexes = new ArrayList<>();

        int i = 0;
        for (Event event : eventList) {
            double max;
            double awayWin = event.getProbability_away_team_winner();
            double homeWin = event.getProbability_home_team_winner();
            double draw = event.getProbability_draw();
            //checking what is the highest probability result in the event
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
            listOfIndexes.add(map.get(key));
        }
        mostProbableResults = listOfIndexes;
    }

}
