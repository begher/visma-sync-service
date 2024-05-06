package begh.vismasyncservice.auth;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;

@Component
public class FirebaseInitialization {

    @PostConstruct
    public void initialization() {
        try {
            FirebaseOptions options = new FirebaseOptions.Builder() //app/imports/key.json src/main/resources/key.json
                    .setCredentials(GoogleCredentials.fromStream(new FileInputStream("src/main/resources/key.json")))
                    .build();
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
