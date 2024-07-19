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
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
        statusCode = connector.validateResponse().statusCode();
        logger.info("statusCode: {}", statusCode);
        assertEquals(200,statusCode);
    }

    @Test
    void testLoginResponseBodyIsNotNull()  {
        String responseBody;
            responseBody = connector.errorCodeFreeResponse().body();
        logger.info("responseBody: {}", responseBody);
            assertNotNull(responseBody);
    }

    @Test
    void testCredentialsIsNotNull(){
        CloudCredentials credentials = connector.getCloudCredentials();
        logger.info("credentials: {}", credentials);
        assertNotNull(credentials);
    }

    @Test
    void testDevicesNotNull() {
        ArrayList<Device> devices;
        devices = Objects.requireNonNull(connector.getDevices());
        logger.info(String.valueOf(devices));
        assertNotNull(devices);
    }
    @Test
    void testFilterTolomeo(){
        Optional<String> devName = connector.getDevices()
                .stream()
                .map(Device::devName)
                .filter(p->p.equals("tolomeo"))
                .findFirst();
        devName.ifPresent(s -> logger.info("devName: {}", s));
        assertEquals("tolomeo",devName.orElse(null));
    }

    @Test
    void testGetUUIDbyName(){
        String uuid = connector.getDevUUIDByDevName("tolomeo");
        logger.info("uuid for tolomeo: {}", uuid);
        assertNotNull(uuid);
    }
    @Test
    void testGetTypeByName(){
        String type = connector.getDevTypeByDevName("tolomeo");
        logger.info("devType: {}", type);
        assertNotNull(type);
    }
}
