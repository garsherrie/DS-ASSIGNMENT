import java.io.IOException;
import java.util.UUID;

public class ClientProtocol {

    private Message message;
    private SocketClient socketClient;

    public ClientProtocol(){
        socketClient = new SocketClient();
        message = new Message();
    }



    private void sendAllInfo(){

    }

    public void sendManufacturerDetails() throws IOException {
        message.setMessage(Constants.MESSAGE_C);
        message.setMessageCode(Constants.C);
        socketClient.updateMessage(Constants.MESSAGE_C);
    }
}
