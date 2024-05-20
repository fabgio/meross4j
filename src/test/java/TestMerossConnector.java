import org.junit.jupiter.api.Test;
import org.meross4j.comunication.MerossConnector;
import org.meross4j.comunication.MerossConstants;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestMerossConnector  {
    private final String email ="giovanni.fabiani@outlook.com";
    private final String password="bruce975";

    @Test
    void testGetStatusCodeIs200() {
        MerossConnector connector = new MerossConnector(MerossConstants.EUROPE_BASE_URL,email, password);
        try {
            int  statusCode=connector.responseToLogin().statusCode();
            assertEquals(200,statusCode);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
