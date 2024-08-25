package org.meross4j.communication;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.meross4j.record.CloudCredentials;
import org.meross4j.record.Device;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestMerossHttpConnector {
    private final static Logger logger = LoggerFactory.getLogger(TestMerossHttpConnector.class);
    private MerossHttpConnector connector;
    private static final String email ="giovanni.fabiani@outlook.com";
    private static final String password = "bruce975";
    public static final String URL ="https://iotx-eu.meross.com";
    private static final String DEVICE_NAME="Scrivania";



    @BeforeEach
    void setUp() {
         connector = new MerossHttpConnector(URL, email, password);
    }
    @Test
    void testStatusCodeIs200() {
        int statusCode = connector.login().statusCode();
        logger.info("statusCode: {}", statusCode);
        assertEquals(200,statusCode);
    }

    @Test
    void testLoginResponseBodyIsNotNull()  {
        String responseBody = connector.errorCodeFreeLogin().body();
        logger.info("responseBody: {}", responseBody);
            assertNotNull(responseBody);
    }

    @Test
    void testCredentialsIsNotNull(){
        CloudCredentials credentials = connector.fetchCredentials();
        logger.info("credentials: {}", credentials);
        assertNotNull(credentials);
    }

    @Test
    void testDevicesNotNull() {
        ArrayList<Device> devices;
        devices = Objects.requireNonNull(connector.fetchDevicesInternal());
        logger.info(String.valueOf(devices));
        assertNotNull(devices);
    }
    @Test
    void testFilterDeviceName(){
        Optional<String> devName = connector.fetchDevicesInternal()
                .stream()
                .map(Device::devName)
                .filter(p->p.equals(DEVICE_NAME))
                .findFirst();
        devName.ifPresent(s -> logger.info("devName: {}", s));
        assertEquals(DEVICE_NAME,devName.orElse(null));
    }

    @Test
    void testFilterOnlineByDevName(){
        int status=connector.getDevStatusByDevName(DEVICE_NAME);
        logger.info("status: {}", status);
        assertEquals(1, status);
    }

    @Test
    void testGetUUIDbyName(){
        String uuid = connector.getDevUUIDByDevName(DEVICE_NAME);
        logger.info("uuid for %s {}".formatted(DEVICE_NAME), uuid);
        assertNotNull(uuid);
    }
    @Test
    void testSaveCloudCredentialIsNotThrown() {
        CloudCredentials credentials = connector.fetchCredentialsInternal();
        assertDoesNotThrow(() -> connector.saveCredentials(credentials));
    }
    @Test
    void testDevicesIsNotThrown() {
        var  devices = connector.fetchDevicesInternal();
        assertDoesNotThrow(() -> connector.saveDevices(devices));
    }
}
