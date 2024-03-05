package institute.hopesoftware.cognitodemo.server;

import java.util.Base64;
import java.util.Map;
import java.util.Set;

import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api")
public class Controller {    
    @GetMapping("/test")
    public ResponseEntity<String> testMethod(@RequestAttribute(name="currentUser") String username) {        
        return ResponseEntity.ok().body("Hello: " + username);        
    }
}
