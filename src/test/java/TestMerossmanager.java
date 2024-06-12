import org.junit.jupiter.api.Test;
import org.meross4j.comunication.MerossHttpConnector;
import org.meross4j.comunication.MerossManager;

public class TestMerossmanager {
    private static final String email ="giovanni.fabiani@outlook.com";
    private static final String password = "bruce975";
    public static final String URL ="https://iotx-eu.meross.com";
    @Test
    void  testManager(){
         MerossHttpConnector merossHttpConnector=new MerossHttpConnector(URL,email,password);
        MerossManager.createMerossManager(merossHttpConnector).executeCommand("tolomeo","on");
}
}
