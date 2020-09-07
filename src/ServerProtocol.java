public class ServerProtocol {

    private SocketServer socketServer;
    private SocketClient socketClient;



    public void postToyDetails(Details_Toy toyDetails) {
        socketServer.updateMessage(toyDetails.getToy_name() + ", " + toyDetails.getBatch_no());
    }

    String Question1(String[] Message){
        if (Message.length==2)
            return "OK";
        else
            return "NO";
    }
    String Question2(String[] Message){
        if (Message.length==4)
            return "OK";
        else
            return "NO";
    }
    String Question3(String[] Message){
        if (Message.length==4)
            return "OK";
        else
            return "NO";
    }

    public Boolean isBlank(String message){
        return message.isEmpty();
    }
}
