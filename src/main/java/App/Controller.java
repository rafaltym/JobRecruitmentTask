package App;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@org.springframework.stereotype.Controller
class Controller {
    @RequestMapping("/get/results/{number}")
    @ResponseBody
    public String writeToFile(@PathVariable("number") Long number) throws IOException {
        //instance of DataBase.class
        DataBase dataBase = new DataBase();
        dataBase.readJsonFile();
        List<Integer> listOfIndexes = dataBase.mostProbableResults();
        List<Event> eventList = dataBase.getEventList();
        if(number <= eventList.size() & number >= 0) {
            //instance of FileWriter with the path to text file which is going to be created or overwriting
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

    @RequestMapping("/get/teams")
    @ResponseBody
    public String printTeamsList() throws IOException {
        //creating data structure to store unique team names
        HashSet<String> teamsSet = new HashSet<>();
        DataBase dataBase = new DataBase();
        dataBase.readJsonFile();
        List<Event> eventsList = dataBase.getEventList();

        //instance of FileWriter with the path to text file which is going to be created or overwriting
        FileWriter fileWriter = new FileWriter("src/main/resources/teamsNames.txt", false);

        //adding all teams' names to the HashSet
        for (Event events : eventsList) {
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



