import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.meross4j.comunication.MerossHttpConnector;
import org.meross4j.record.CloudCredentials;
import org.meross4j.record.Device;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TestMerossHttpConnector {
    private final static Logger logger = LoggerFactory.getLogger(TestMerossHttpConnector.class);
    private MerossHttpConnector connector;
    private static final String email ="giovanni.fabiani@outlook.com";
    private static final String password = "bruce975";
    public static final String URL ="https://iotx-eu.meross.com";

    @BeforeEach
    void setUp() {
         connector = new MerossHttpConnector(URL, email, password);
    }

    @Test
    void testStatusCodeIs200() {
        int statusCode;
        statusCode = connector.getLoginResponse().statusCode();
        assertEquals(200,statusCode);

    }

    @Test
    void testResponseBodyIsNotNull() {
        String responseBody;
            responseBody = connector.loginResponseBody();
            assertNotNull(responseBody);
    }

    @Test
    void testCredentialsIsNotNull(){
        CloudCredentials credentials = connector.getCloudCredentials();
        assertNotNull(credentials);
    }

    @Disabled
    @Test
    void testLoginBodyIsNull() {
        String responseBody = connector.loginResponseBody();
        assertNull(responseBody);
    }
    @Disabled
    @Test
    void testCredentialsIsNull(){
        CloudCredentials credentials = connector.getCloudCredentials();
        assertNull(credentials);
    }

    @Disabled
    @Test
    void testLoginResponseBodyIsNull() {
        String responseBody;
        responseBody = Objects.requireNonNull(connector.loginResponseBody());
        assertNull(responseBody);
    }
    @Disabled
    @Test
    void testDevicesResponseBodyIsNull() {
        String deviceResponseBody;
        deviceResponseBody = Objects.requireNonNull(connector.deviceResponseBody());
        logger.info(deviceResponseBody);
        assertNull(deviceResponseBody);
    }
    @Disabled
    @Test
    void testDevicesNull() {
        ArrayList<Device> devices;
        devices = Objects.requireNonNull(connector.getDevices());
        logger.info(String.valueOf(devices));
        assertNull(devices);
    }
    @Test
    void testDevicesNotNull() {
        ArrayList<Device> devices;
        devices = Objects.requireNonNull(connector.getDevices());
        logger.info(String.valueOf(devices));
        assertNotNull(devices);
    }
    @Test
    void testNotFilterTolomeo(){
        Optional<String> devName = connector.getDevices()
                .stream()
                .map(Device::devName)
                .filter(p->p.equals("tolomeo"))
                .findFirst();
        assertNotEquals("tolomeo",devName.get());
    }
}
