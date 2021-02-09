import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ClientWindow extends JFrame implements ActionListener, TCPConnectionObserver {

    private static final String IP_ADDR = "127.0.0.1";
    private static final int PORT = 8000;
    private static final int WIDTH = 400;
    private static final int HEIGHT = 600;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ClientWindow::new);
    }

    private TCPConnection connection;
    private final JTextArea log = new JTextArea();
    private final JTextField fieldText = new JTextField();
    private final JTextField fieldNick = new JTextField("Unknown User");

    private ClientWindow() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);

        log.setEditable(false);
        log.setLineWrap(true);
        add(log, BorderLayout.CENTER);

        add(fieldText, BorderLayout.SOUTH);
        fieldText.addActionListener(this);

        add(fieldNick, BorderLayout.NORTH);

        setVisible(true);
        try {
            connection = new TCPConnection(this, IP_ADDR, PORT);
        } catch (IOException e) {
            printMessage("Connection exception: " + e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String message = fieldText.getText();
        if(message.equals("")) return;
        fieldText.setText(null);
        connection.sendMessage(fieldNick.getText() + ": " + message);
    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        printMessage("Connection established!");
    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String value) {
        printMessage(value);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        printMessage("Connection closed.");
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        printMessage("Connection exception: " + e);
    }

    private synchronized void printMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            log.append(message + "\n");
            log.setCaretPosition(log.getDocument().getLength());
        });
    }
}
