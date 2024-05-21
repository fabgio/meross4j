import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.meross4j.comunication.MerossHttpConnector;
import org.meross4j.comunication.MerossConstants;
import java.util.concurrent.ExecutionException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestMerossConnector  {
    private MerossHttpConnector connector;
    private final String email ="giovanni.fabiani@outlook.com";
    private final String password="bruce975";

    @BeforeEach
    void setUp() {
         connector = new MerossHttpConnector(MerossConstants.EUROPE_BASE_URL, email, password);
    }

    @Test
    void testStatusCodeIs200() {
        try {
            int  statusCode = connector.responseToLogin().statusCode();
            assertEquals(200,statusCode);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    void testSResponseIsNotNull() {
        try {
            String responseBody = connector.responseToLogin().body();
            assertNotNull(responseBody,responseBody);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
