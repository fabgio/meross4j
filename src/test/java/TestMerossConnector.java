import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.meross4j.comunication.MerossHttpConnector;
import org.meross4j.comunication.MerossConstants;
import java.util.Map;
import java.util.Objects;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TestMerossConnector  {
    private MerossHttpConnector connector;
    private final String email ="giovanni.fabiani@outlook.com";
    private final String password ="bruce975";

    @BeforeEach
    void setUp() {
         connector = new MerossHttpConnector(MerossConstants.EUROPE_BASE_URL, email, password);
    }

    @Test
    void testStatusCodeIs200() {
        int statusCode;
        statusCode = connector.responseToLogin().statusCode();
        assertEquals(200,statusCode);

    }
    @Disabled
    @Test
    void testResponseBodyIsNull() {
        String responseBody;
        responseBody = Objects.requireNonNull(connector.responseToLogin()).body();
        assertNull(responseBody);
    }

    @Test
    void testResponseBodyIsNotNull() {
        String responseBody;
            responseBody = connector.responseToLogin().body();
            assertNotNull(responseBody);
    }

    @Test
    void testResponseBodyAsMapIsNotNull() {
        Map<String,String> responseBody;
        responseBody = connector.responseBodyAtLogin();
        assertNotNull(responseBody);
    }
    }
