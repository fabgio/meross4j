import                                                                                                                    org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.meross4j.comunication.MerossHttpConnector;
import org.meross4j.comunication.MerossManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

public class TestMerossManager {
    private static final String email = "giovanni.fabiani@outlook.com";
    private static final String password = "bruce975";
    private static final String URL = "https://iotx-eu.meross.com";
    private final static Logger logger = LoggerFactory.getLogger(TestMerossManager.class);
    private MerossHttpConnector merossHttpConnector;
    @BeforeEach
    void setUp(){
        merossHttpConnector = new MerossHttpConnector(URL, email, password);
    }

    @Test
    void testManager()  {
        var manager = MerossManager.createMerossManager(merossHttpConnector);
        manager.executeCommand("tolomeo", "off");


    }
}
