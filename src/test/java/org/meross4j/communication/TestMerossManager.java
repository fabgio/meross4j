package org.meross4j.communication;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.meross4j.record.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
    void testSend()  {
        var manager = MerossManager.createMerossManager(merossHttpConnector);
        var response=  manager.executeCommand("Comodino", "OFF");
        logger.info("SystemAll Response: {}",response.map().get("method"));
        assertNotNull(response);
    }

    @Test
    void testReceive()  {
        var manager = MerossManager.createMerossManager(merossHttpConnector);
        Response response =  manager.executeCommand("Scrivania");
        logger.info("SystemAll Response: {}",response);
        assertNotNull(response);
    }
}
