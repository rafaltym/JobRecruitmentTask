package App;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@org.springframework.stereotype.Controller
class Controller {

    @GetMapping("/write-10")
    @ResponseBody
    public String writeToFile() throws IOException {
        DataBase dataBase = new DataBase();
        dataBase.readJsonFile();
        List<Integer> listOfIndexes = dataBase.mostProbableResults();
        List<Event> eventList = dataBase.eventList;
            String[][] eventDetails = new String[10][4];
            FileWriter fileWriter = new FileWriter("src/main/resources/10results.txt",false);

            //fill array with details of events with 10 most probable results
            for (int i = 0; i < 10; i++) {
                Event thisEvent = eventList.get(listOfIndexes.get(i));
                String startDate = thisEvent.getStart_date().toString();
                Competitor homeTeam = thisEvent.getCompetitors().get(0);
                Competitor awayTeam = thisEvent.getCompetitors().get(1);
                eventDetails[i][0] = ("Start date: " + startDate.substring(0, 10) + " " + startDate.substring(11, 19) + ",<br>\n");
                eventDetails[i][1] = (homeTeam.getName() + " (" + homeTeam.getCountry() + ") vs. " + awayTeam.getName() + " (" + awayTeam.getCountry() + "),<br>\n");
                eventDetails[i][2] = ("Venue: " + thisEvent.getVenue().getName() + ",<br>\n");
                eventDetails[i][3] = ("Highest probable result: " + thisEvent.theMostProbableResultInEvent()+"<br>\n");

                //write details to text file
                fileWriter.write("--------------------------------------------" + (i + 1) + "--------------------------------------------<br>\n");
                fileWriter.write(eventDetails[i][0]);
                fileWriter.write(eventDetails[i][1]);
                fileWriter.write(eventDetails[i][2]);
                fileWriter.write(eventDetails[i][3]);
                fileWriter.write("<br>\n");
            }
            fileWriter.close();

        return "10 events have been written";
    }

    @GetMapping("/get-10")
    @ResponseBody
    public String content(){
        //Convert all bytes from text file to String
        try {
            String data = new String(Files.readAllBytes(Paths.get("src/main/resources/10results.txt")));
            return data;
        } catch(Exception e) {
            return "There is no file 10results.txt<br>To create one: /write-10";
        }
    }
}



