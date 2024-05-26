import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.meross4j.comunication.MerossHttpConnector;
import org.meross4j.comunication.MerossConstants;
import org.meross4j.record.CloudCredentials;
import java.util.Objects;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TestMerossConnector  {
    private MerossHttpConnector connector;
    private final String email ="giovanni.fabiani@outlook.com";
    private final String password ="bruce975";
    public static final String URL ="https://iotx-eu.meross.com";

    @BeforeEach
    void setUp() {
         connector = new MerossHttpConnector(URL, email, password);
    }

    @Test
    void testStatusCodeIs200() {
        int statusCode;
        statusCode = connector.response().statusCode();
        assertEquals(200,statusCode);

    }

    @Test
    void testResponseBodyIsNotNull() {
        String responseBody;
            responseBody = connector.response().body();
            assertNotNull(responseBody);
    }

    @Test
    void testCredentialsIsNotNull(){
        CloudCredentials credentials = connector.cloudCredentials();
        assertNotNull(credentials);
    }

    @Disabled
    @Test
    void testBodyIsNull() {
        String responseBody = connector.body();
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
    void testResponseBodyIsNull() {
        String responseBody;
        responseBody = Objects.requireNonNull(connector.response()).body();
        assertNull(responseBody);
    }
}
