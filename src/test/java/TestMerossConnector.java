import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.meross4j.comunication.MerossHttpConnector;
import org.meross4j.record.CloudCredentials;
import java.util.Objects;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TestMerossConnector  {
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
        CloudCredentials credentials = connector.cloudCredentials();
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
        CloudCredentials credentials = connector.cloudCredentials();
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
        assertNull(deviceResponseBody);
    }
}
