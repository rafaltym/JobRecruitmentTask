package App;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class DataBase {

    //List to store Event objects
    List<Event> eventList = new ArrayList<>();
    //the list to store indexes of events in order from that with the highest probability result
    List<Integer> listOfIndexes = new ArrayList<>();

    //read json file data to String
    byte[] jsonData;

    public void readJsonFile() throws IOException {
        jsonData = Files.readAllBytes(Paths.get("src/main/resources/BE_data.json"));
        //create ObjectMapper instance
        ObjectMapper objectMapper = new ObjectMapper();
        //create tree from Json
        JsonNode rootNode;
        rootNode = objectMapper.readTree(jsonData);
        //create eventsNode with all data under "Events" path
        JsonNode eventsNode = rootNode.path("Events");
        //map eventList  with Event class objects stored in JsonNode
        eventList = objectMapper.readValue(eventsNode.toString(), new TypeReference<List<Event>>() {});
    }

    public List<Integer> mostProbableResults() {
        //Mapping the value of the highest probability in particular event(Double) and the index of that event(Integer)
        HashMap<Double, Integer> map = new HashMap<Double, Integer>();
        List<Double> keysList = new ArrayList<Double>();

        int i = 0;
        for (Event event : eventList) {
            double max = 0;
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
        return listOfIndexes;
    }

}
