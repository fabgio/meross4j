package org.meross4j.communication;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.meross4j.record.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        var response=  manager.executeCommand("Scrivania",
                MerossEnum.Namespace.CONTROL_TOGGLEX.name(),"OFF");
        logger.info("SystemAll Response: {}",response);
        assertNotNull(response);
    }
    @Disabled
    @Test
    void testReceive()  {
        var manager = MerossManager.createMerossManager(merossHttpConnector);
        Response response =  manager.executeCommand("Scrivania",MerossEnum.Namespace.CONTROL_TOGGLEX.name());
        int  status = (Integer) response.map().get("onoff");
        assertEquals(status,0);
        logger.info("Status: {}",status);
    }
}
