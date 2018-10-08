//Custom message box to alert user a field is blank.
package Remindr;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

public class PopupMessage extends JFrame {

    private JLabel lblMessage;
    
    private String text;
    
    private Color backgroundColor;
    private Color borderColor;
    private Color textColor;
    
    Font fSize = new Font("Serif",Font.PLAIN,18);
    
    PopupMessage(int x, int y, String t) {
        loadSettings();
        //Popup directly underneath main window
        int xCoord = x;
        int yCoord = y;
        text = t;
        JPanel popup = new JPanel(null);
        popup.setBackground(backgroundColor);
        popup.setBorder(BorderFactory.createLineBorder(borderColor, 2));
        popup.addMouseListener(new onClickClose());

        startTimer();
        buildFrame(xCoord, yCoord);
        buildMessage(popup);

        add(popup);

        setVisible(true);
    }

    //Build frame
    private void buildFrame(int x, int y) {
        setSize(300, 30);
        setLocation(x, y + 90);
        setUndecorated(true);
        setResizable(false);
        setAlwaysOnTop(true);
    }

    //Build message for popup and format according to size
    private void buildMessage(JPanel p) {
        lblMessage = new JLabel(text);
        lblMessage.setFont(fSize);
        lblMessage.setHorizontalAlignment(SwingConstants.CENTER);
        lblMessage.setBounds(0, getHeight() / 2-10, getWidth(), 20);
        lblMessage.setBackground(backgroundColor);
        lblMessage.setForeground(textColor);

        p.add(lblMessage);
    }

    //Start timer to dispose popup after set interval of time
    private void startTimer() {
        Timer timer = new Timer(2000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    //Or user can click to exit to main window
    private class onClickClose extends MouseAdapter {

        public void mousePressed(MouseEvent e) {
            dispose();
        }
    }

    //Load settings set by user
    private void loadSettings() {
        try {
            Properties prop = new Properties();
            InputStream input = new FileInputStream("C:\\Remindr\\RemindrSettings.properties");
       
            prop.load(input);
            
            backgroundColor = new Color(Integer.parseInt(prop.getProperty("bgCol")));
            borderColor = new Color(Integer.parseInt(prop.getProperty("bdrCol")));
            textColor = new Color(Integer.parseInt(prop.getProperty("txtCol")));
        } 
        catch (Exception ex) {
            backgroundColor = Color.LIGHT_GRAY;
            borderColor = Color.BLACK;
            textColor = Color.BLACK;
        }
    }
}
