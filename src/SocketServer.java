import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;

public class SocketServer implements Initializable {

    public Button btn_toyDetails;
    @FXML
    private Button btn_startServer;
    @FXML
    private TextArea output_window;
    @FXML
    private Button btn_setPort;
    @FXML
    private TextField txtPortNumber;
    @FXML
    private TextField input;
    @FXML
    private Button submit;
    @FXML
    private Button close;

    public int serverPort;


    private OutputStream outputStream;
    private ObjectOutputStream objectOutputStream;
    private InputStream inputStream;
    private ObjectInputStream objectInputStream;
    private Socket socket;
    private ServerSocket serverSocket;

    private SocketClient socketClient;
    private Message message;
    private ClientProtocol clientProtocol;
    private Details_Toy toyDetails;
    private ServerProtocol serverProtocol;

    private static int staticQueryIncrement = 0;

    public SocketServer(){
        toyDetails = new Details_Toy();
    }

    @FXML
    private int findFreePort(ActionEvent actionEvent){
        serverSocket = null;
        try {
            serverSocket = new ServerSocket(0);
//            serverSocket.setReuseAddress(true);

            serverPort = serverSocket.getLocalPort();
            System.out.println(serverPort);

            updatePortNumber();

            return serverPort;

        } catch (IOException e) {
            e.printStackTrace();

        }
        throw new IllegalStateException("Could not find a free TCP/IP port to start Server on");
    }

    /* Invokes the methods that are supposed to run when the server starts */
    public void initServer(ActionEvent actionEvent){

        updateMessage("\n Initializing Server \n");

        Runnable serverTask = () -> {
            try {
                ServerSocket serverSocket = new ServerSocket(0);
                serverPort = serverSocket.getLocalPort();
                //serverPort =53918;
                System.out.println(serverPort);

                updatePortNumber();

                updateMessage("No client has connected yet... \n To start a client, click on Start Client.\n");
                System.out.println("No client connected yet...");

                btn_setPort.isDisable();

                while (true) {
                    socket = serverSocket.accept();
                    initializeStreamConnection();
                    whileRunning();
                    btn_toyDetails.setOnAction(this::toyDetails);
                }
            } catch (IOException e) {
                System.err.println("Unable to process client request");
                e.printStackTrace();
            }
        };
        Thread serverThread = new Thread(serverTask);
        serverThread.start();
    }

    private ArrayList<String> setQuestions(){
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Kindly enter Toy identification details as follows:(toy code,toy name): ");
        arrayList.add("Kindly enter Toy information as follows:(description,price,date of manufacture,batch number): ");
        arrayList.add("Kindly enter Toy manufacturer details as follows: (company name,street address,zip-code,country): ");
        arrayList.add("Enter Thank you message:");
        return arrayList;
    }

    //while server is running(during conversation)
    public void whileRunning() throws IOException{
        String message = "";
        ArrayList<String> ToyQuestions = setQuestions();

        int queryIncrement = 1;

        do{
            try{

                if (staticQueryIncrement <ToyQuestions.size()){
                    //saveInfo(queryIncrement, message);
                    sendMessage(ToyQuestions.get(staticQueryIncrement));
                    message = (String) objectInputStream.readObject();
                    updateMessage("\n" + message);
                    saveInfo(staticQueryIncrement, message);
                } else if (staticQueryIncrement == ToyQuestions.size()){
                    saveInfo(staticQueryIncrement, message);
                    sendMessage("\n\n"+toyDetails.toString() + "\nThe toy's identification number is: " + new Random().nextInt(99999));
                    sendMessage("Kindly confirm if the returned details correct? (T/F)");
                    String response= (String) objectInputStream.readObject();
                    String[] temp = response.split(" - ");
                    response = temp[1];
                    if(response.equals("F")){
                        staticQueryIncrement=0;
                        whileRunning();
                    }else{
                        sendMessage("GoodBye, you have a great taste in toys!!");
                        closeConnection();
                    }

                }
            } catch(ClassNotFoundException classNotFoundException){
                updateMessage("\nObject not known");
            }
            staticQueryIncrement++;
        } while(!message.equals("USER - END"));
        System.out.println(toyDetails.toString());
    }

    public void toyDetails(ActionEvent actionEvent) {
        sendMessage(Constants.MESSAGE_A);
        String message = null;
        try {
            message = (String) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        String[] array = message.split(",");

        if(serverProtocol.Question1(array).equals("OK")){
            toyDetails.setToy_code(array[0]);
            toyDetails.setToy_name(array[1]);
            System.out.println(array[0]+array[1]);
        }else {
            sendMessage("Invalid Input, follow ths order 'code,name'...Retry!!\n");
        }

    }

    private void saveInfo(int queryIncrement, String message) {
        String[] temp = message.split(" - ");
        message = temp[1];
        System.out.println(queryIncrement + "<==>" + message);
        serverProtocol = new ServerProtocol();
        String[]  array = temp[1].split(",");
        switch(queryIncrement){
            case 0:
                if(serverProtocol.Question1(array).equals("OK")){
                    toyDetails.setToy_code(array[0]);
                    toyDetails.setToy_name(array[1]);
                    System.out.println(array[0]+array[1]);
                }else{
                    sendMessage("Invalid Input, follow ths order 'code,name'...Retry!!\n");
                    try {
                        whileRunning();
                        staticQueryIncrement = queryIncrement;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 1:
                if(serverProtocol.Question2(array).equals("OK")){
                    toyDetails.setDescription(array[0]);
                    toyDetails.setPrice(array[1]);
                    toyDetails.setDom(array[2]);
                    toyDetails.setBatch_no(array[3]);
                }else{
                    sendMessage("Invalid Input, follow this order 'description,price,'...Retry!!\n");
                    try {
                        whileRunning();
                        staticQueryIncrement = queryIncrement;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 2:
                if(serverProtocol.Question3(array).equals("OK")){
                    toyDetails.setManufacturer_name(array[0]);
                    toyDetails.setAddress(array[1]);
                    toyDetails.setZip_code(array[2]);
                    toyDetails.setCountry(array[3]);
                }else{
                    sendMessage("Invalid Input, follow ths order 'Company name,address,'...Retry!!\n");
                    try {
                        whileRunning();
                        staticQueryIncrement = queryIncrement;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 3:
                toyDetails.setMessage(message);
                break;
            default:
                break;

        }
    }

    //send a message to the client
    private void sendMessage(String message){
        try{
            objectOutputStream.writeObject("SERVER - "+ message);
            objectOutputStream.flush();
            updateMessage("\n\nSERVER - " + message);
        }catch(IOException ioException){
            updateMessage("\nERROR: MESSAGE NOT SENT!");
        }
    }

    private void updatePortNumber(){
        txtPortNumber.setText(String.valueOf(serverPort));
        output_window.appendText("Hey, your port number is: " + serverPort + "\n");
    }

    /* Update Output on Message window */
    public void updateMessage(final String text){
        output_window.appendText(text);
    }

    /* wait for connection at startup */
    private void waitForConnection()  throws IOException{
        updateMessage("Waiting for connection from client...\n");
        socket = serverSocket.accept();
        displayConnectedMessage();

    }

    private void displayConnectedMessage(){
        updateMessage("\n Guess what!! Connection is Successful ");
        updateMessage("You are connected to " + socket.getInetAddress().getHostName());
    }

    /* method to allow for sending and receiving of data */
    private void initializeStreamConnection() throws IOException{
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectOutputStream.flush();
        objectInputStream = new ObjectInputStream(socket.getInputStream());
        updateMessage("\n Hurray!! The client is connected");
    }

    /* Close Connection */
    public void closeConnection(){
        updateMessage("Closing Connection ... \n");
        try{
            objectOutputStream.close();
            objectInputStream.close();
            socket.close();
            updateMessage("\n Connection to " + socket.getInetAddress().getHostName() + " closed!");
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            txtPortNumber.appendText("");
            updateMessage("Connection closed");
        }
    }

    public void sendWelcomeMessage() throws IOException{
        message = new Message();
        message.setMessageCode(Constants.WELCOME);
        message.setMessage(Constants.WELCOME_MESSAGE);
        objectOutputStream.writeObject(message);
    }

    private void requestToyName(){
        try{
            message = new Message();
            message.setMessage(Constants.MESSAGE_A);
            message.setMessageCode(Constants.A);
            objectOutputStream.writeObject(message);
        } catch (IOException io){
            io.printStackTrace();
        }

    }

    @FXML
    private void sendManufacturerDetails(){
        try {
            clientProtocol.sendManufacturerDetails();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @FXML
    public void openClientWindow(){
        if (!txtPortNumber.getText().isEmpty()){
            try{
                Parent parent = FXMLLoader.load(getClass().getResource("client_ui.fxml"));
                Stage stage = new Stage();
                stage.setTitle("Client Side");
                Scene scene = new Scene(parent);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else{

            updateMessage("\n Oopsss!!! you must key in a port number first! \n");
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String welcome = "Whatspopping!!!! \n Your are on the server's interface. Please set up the port number above. \n";
        output_window.setText(welcome);

        socket = new Socket();

        if(!txtPortNumber.getText().isEmpty()){
            updateMessage("Server is already running");
        }else{
            btn_setPort.setOnAction(this::initServer);
        }

        clientProtocol = new ClientProtocol();

    }
}
