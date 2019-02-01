package main.Controllers;

import java.util.concurrent.atomic.AtomicLong;

import main.MappingHelper;
import main.Marker;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MappingController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/greeting")
    public Marker greeting(@RequestParam(value="name", defaultValue="World") String name) {
        return new Marker(counter.incrementAndGet(),0, 0, 0);
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
