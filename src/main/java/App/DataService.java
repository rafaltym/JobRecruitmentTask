package App;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@Service
public class DataService {

    @Autowired
    private DataRepository dataRepository =new DataRepository();
    private List<Integer> listOfIndexes = dataRepository.getMostProbableResults();
    private List<Event> eventList = dataRepository.getEventList();

    public String printEventsDetails(Long number) {

        if(number <= eventList.size() & number >= 0) {
            try {
                writeToFile(number);
            } catch (IOException e) {
                return "!writeToFile method Exception!";
            }

            //Convert all bytes from text file to String
            try {
                return new String(Files.readAllBytes(Paths.get("src/main/resources/resultsDetails.txt")));
            } catch(Exception e) {
                return "There is no file in the database to display.";
            }
        } else {
            return "There is only " + eventList.size() + " matches in the database.";
        }
    }

    private void writeToFile(long number) throws IOException {
        FileWriter fileWriter = new FileWriter("src/main/resources/resultsDetails.txt", false);
        //fill array with details of events with 10 most probable results
        for (int i = 0; i < number; i++) {
            Event thisEvent = eventList.get(listOfIndexes.get(i));
            String startDate = thisEvent.getStart_date();
            Competitor homeTeam = thisEvent.getCompetitors().get(0);
            Competitor awayTeam = thisEvent.getCompetitors().get(1);

            //write details to text file
            fileWriter.write("--------------------------------------------" + (i + 1) + "--------------------------------------------<br>\n");
            fileWriter.write("Start date: " + startDate.substring(0, 10) + " " + startDate.substring(11, 19) + ",<br>\n");
            fileWriter.write(homeTeam.getName() + " (" + homeTeam.getCountry() + ") vs. " + awayTeam.getName() + " (" + awayTeam.getCountry() + "),<br>\n");
            try {
                fileWriter.write("Venue: " + thisEvent.getVenue().getName() + ",<br>\n");
            } catch (Exception e) {
                fileWriter.write("Venue: " + "null" + ",<br>\n");
            }
            fileWriter.write("Highest probable result: " + thisEvent.theMostProbableResultInEvent() + "<br>\n");
            fileWriter.write("<br>\n");
        }
        fileWriter.close();
    }

    public String printTeamsList() throws IOException {
        HashSet<String> teamsSet = new HashSet<>();
        //dataRepository.readJsonFile();
        List<Event> eventList = dataRepository.getEventList();

        FileWriter fileWriter = new FileWriter("src/main/resources/teamsNames.txt", false);

        //adding all teams' names to the HashSet
        for (Event events : eventList) {
            teamsSet.add(events.getCompetitors().get(0).getName());
            teamsSet.add(events.getCompetitors().get(1).getName());
        }
        //converting HashSet to Array List in order to sort teams' names alphabetically
        List<String> teamsListInOrder = new ArrayList<>(teamsSet);
        Collections.sort(teamsListInOrder);
        int i = 0;
        //write all teams' names to the file
        for (String string : teamsListInOrder) {
            i +=1;
            fileWriter.write(i + ". " + string + "<br>");
        }
        fileWriter.close();

        //Convert all bytes from text file to String and return
        try {
            return new String(Files.readAllBytes(Paths.get("src/main/resources/teamsNames.txt")));
        } catch(Exception e) {
            return "There is no file in the database to display.";
        }
    }
}
