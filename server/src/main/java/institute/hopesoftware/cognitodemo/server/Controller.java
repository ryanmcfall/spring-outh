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
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api")
public class Controller {    
    private String getUsernameFromJWT(String jwt) {
        String[] chunks = jwt.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        JsonParser parser = JsonParserFactory.getJsonParser();
        for (String chunk : chunks) {
            String value = new String(decoder.decode(chunk));
            if (value.isEmpty()) {
                System.err.println("Uh Uh!");
            }
            Map<String, Object> json = parser.parseMap(value);
            Set<String> keys = json.keySet();
            if (keys.contains("username")) {
                String username = (String) json.get("username");
                return username;
            }
        }
        return null;
    }
    @GetMapping("/test")
    public ResponseEntity<String> testMethod(@RequestHeader(name="Bearer") String jwt) {        
        String username = getUsernameFromJWT(jwt);                
        return ResponseEntity.ok().body("Hello: " + username);        
    }
}
