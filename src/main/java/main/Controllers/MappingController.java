package main.Controllers;

import main.MappingHelper;
import main.graph.MarkerNode;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MappingController {

    @RequestMapping("/greeting")
    public MarkerNode greeting(@RequestParam(value="name", defaultValue="World") String name) {
        return new MarkerNode(0, 0, 0);
    }

    @CrossOrigin
    @RequestMapping("/testReturn")
    public String testReturn(@RequestParam(value="name", defaultValue="World") String name) {
        return "Test Return: " + name;
    }

    // Recieves coords, gets elevations and creates Marker objects (held inside the Mapping Helper for now)
    @CrossOrigin
    @RequestMapping("/requestMap")
    public String requestMap(@RequestParam(value="coords", defaultValue="") String coords) {

        MappingHelper helper = new MappingHelper(coords);

        return helper.createMarkerObjects();
    }


}
