import java.util.LinkedList;
import java.net.Socket;
import java.net.ServerSocket;
import java.io.*;

public class Server {
    private static LinkedList<PrintWriter> clientWriterList = new LinkedList<>();
    private static int clientNum = 0;
    private static final int PORT = 8888;
    
    public static int getPort()
    {
        return PORT;
    }

    void startServe() throws IOException {
        ServerSocket clientSocket = new ServerSocket(PORT);
        Server.clientNum = 0;
        System.out.println("FreeChat Server Started");
        try
        {
            while (true) 
            {
                Socket socket = clientSocket.accept();
                Server.clientNum++;
                System.out.println("[Server] Client " + Server.clientNum + " has connected");
                PrintWriter clientWriter = new PrintWriter(new BufferedWriter(
                                            new OutputStreamWriter(socket.getOutputStream())),true);
                clientWriter.println("[Server] Client " + Server.clientNum + " has connected");
                clientWriter.flush();
                clientWriterList.add(clientWriter);
                ServerHandler reader = this.new ServerHandler(socket);
                Thread clientHandler = new Thread(reader);
                clientHandler.start();
            }
        } 
        finally 
        {
            clientSocket.close();
        }
    }

    public class ServerHandler implements Runnable {
        String msg;
        Socket socket;
        BufferedReader client_input;

        public ServerHandler(Socket s) {
            this.socket = s;
            try {
                client_input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException exc) {
                exc.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                while ((msg = client_input.readLine()) != null) {
                    for (PrintWriter writer : clientWriterList) {
                        writer.println(msg);
                        writer.flush();
                    }
                }
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }
    }

    
    public static void main(String[] args) throws IOException {
        Server chatServer = new Server();
        chatServer.startServe();
    }
}
