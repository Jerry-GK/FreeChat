import java.net.Socket;

import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.JButton; 
import javax.swing.JFrame;
import javax.swing.ScrollPaneConstants;
import javax.swing.JOptionPane;
import java.awt.BorderLayout;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.io.*;

public class Client extends JFrame {
    private int ID;
    private boolean ID_set = false;
    private Socket socket;
    private PrintWriter outWriter;
    private BufferedReader inReader;
    private static final int PORT = Server.getPort();
    private static final int W_WIDTH = 700;
    private static final int W_HEIGHT = 600;

    JTextArea msgArea;
    JTextArea inputField;
    JScrollPane scroll;
    JPanel myPanel;
    JButton sendBtn;

    public Client() {
        setTitle("FreeChat");
        setSize(W_WIDTH, W_HEIGHT);
        setLayout(new BorderLayout(8, 8));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        msgArea = new JTextArea();
        msgArea.setFocusable(false);
        scroll = new JScrollPane(msgArea);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        getContentPane().add(BorderLayout.CENTER, scroll);

        myPanel = new JPanel();
        myPanel.setLayout(new BorderLayout(5,5));
        inputField = new JTextArea();
        inputField.setColumns(50);
        inputField.setRows(10);
        inputField.setLineWrap(true);

        sendBtn = new JButton("Send");
        myPanel.add(BorderLayout.CENTER, inputField);
        myPanel.add(BorderLayout.EAST, sendBtn);
        getContentPane().add(BorderLayout.SOUTH, myPanel);

        setVisible(true);
    }

    public static void main(String[] args) throws IOException {
        Client c = new Client();
        c.connectToServer();

        ClientReceiver receiver = c.new ClientReceiver();
        Thread t = new Thread(receiver);
        t.start();
    }

    public void connectToServer() throws IOException {
        try {
            socket = new Socket("localhost", PORT);

            outWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
            inReader = new BufferedReader( new InputStreamReader(socket.getInputStream()));
        } 
        catch (IOException exc) 
        {
            JOptionPane.showMessageDialog(null, "Cannot connect to server!");
            if (socket != null)
                socket.close();
        }

        ActionListener sendListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = inputField.getText();
                if (message != null && !message.equals("")) {
                    try {
                        outWriter.println("Client " + ID + ": " + message);
                        outWriter.flush();
                    } catch (Exception exc) {
                        exc.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Message should not be empty!");
                }
                inputField.setText("");
            }
        };
        sendBtn.addActionListener(sendListener);
    }

    public class ClientReceiver implements Runnable {
        String message;

        @Override
        public void run() {
            try {
                while ((message = inReader.readLine()) != null) {
                    if (!ID_set) {
                        String[] str = message.split(" ");
                        if(str[0].equals("[Server]"))
                        {
                            ID = Integer.parseInt(str[2]);
                            setTitle("FreeChat Client " + ID);
                            ID_set = true;
                        }
                    }
                    msgArea.append(message + "\n");
                }
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }
    }
}