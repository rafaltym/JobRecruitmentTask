package App;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@org.springframework.stereotype.Controller
class Controller {

    @Autowired
    private DataRepository dataRepository = new DataRepository();
    @Autowired
    private DataService dataService = new DataService();

    @RequestMapping("/get/results/{number}")
    @ResponseBody
    public String eventsDetails(@PathVariable("number") Long number) {
        //dataRepository.readJsonFile();
        return dataService.printEventsDetails(number);

    }


    @RequestMapping("/get/teams")
    @ResponseBody
    public String teamsList() {
        //dataRepository.readJsonFile();
        try {
            return dataService.printTeamsList();
        } catch (IOException e) {
            return "InPrintTeamsListException";
        }

    }
}



