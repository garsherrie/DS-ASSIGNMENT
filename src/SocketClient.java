import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class SocketClient implements Initializable {

    public Button btn_submit;
    public TextField txt_input;
    @FXML
    private Button btn_connect;
    @FXML
    private TextField server_name;
    @FXML
    private TextArea output_window;
    @FXML
    private Button submit;
    @FXML
    private Button close;
    @FXML
    private TextField txtPortNumber;

    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private InputStream inputStream;
    private OutputStream outputStream;

    private Socket clientSocket;

    private SocketServer socketServer;
    private ServerProtocol serverProtocol;

    private String message;

    private void connectToServer(ActionEvent actionEvent){
        if(!txtPortNumber.getText().isEmpty() && !server_name.getText().isEmpty()){
            Integer serverPort = Integer.parseInt(txtPortNumber.getText());
            String serverName = server_name.getText();

            Runnable serverTask = () -> {
                try {
                    clientSocket = new Socket(serverName,serverPort);
                    System.out.println("Client Connected...");
                    displayConnectedMessage();

                    setUpCommunicationStreams();
                    whileRunning();

                } catch (IOException e) {
                    System.err.println("\n Error Connecting to Server");
                    updateMessage("\n You are NOT connected to the Server");
                    e.printStackTrace();
                }
            };
            Thread serverThread = new Thread(serverTask);
            serverThread.start();
        }else {
            updateMessage("Please Input the server address and the port number");
        }
    }

    private void setUpCommunicationStreams() throws IOException{
        objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
        objectOutputStream.flush();
        objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
        updateMessage("\nAwesome!! You can now communicate with the server");
    }

    public void updateMessage(final String text){
        output_window.appendText(text);
    }

    //while communicating with the server
    private void whileRunning() throws IOException{
        do{
            try{
                message = (String) objectInputStream.readObject();
                updateMessage("\n\n" + message);
            }catch(ClassNotFoundException classNotFoundException){
                updateMessage("\nObject not recognized");
            }

        }while(!message.equals("SERVER - END"));
    }

    private void displayConnectedMessage(){
        updateMessage("\n\nHurray!! Connection Successful ");
        updateMessage("You are connected to " + clientSocket.getInetAddress().getHostName());
    }

    //sending messages to the server
    private void sendMessage(ActionEvent actionEvent){
        message = txt_input.getText().trim();
        if(serverProtocol.isBlank(message)){
            updateMessage("\nInput some text in the message bar");
        }else{
            try{
                objectOutputStream.writeObject("CLIENT - " + message);
                objectOutputStream.flush();
                updateMessage("\nCLIENT - " + message);
                txt_input.clear();
            }catch(IOException ioException){
                updateMessage("\nMessage was interfered with");
            }
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        socketServer = new SocketServer();
        serverProtocol = new ServerProtocol();

        message = "";

        btn_connect.setOnAction(this::connectToServer);
        btn_submit.setOnAction(this::sendMessage);
    }
}
