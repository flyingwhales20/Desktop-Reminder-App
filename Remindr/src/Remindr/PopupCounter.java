/*When the notes are minimized, launch a popup counter next to the handler to
 display how many notes are hidden.
 */
package Remindr;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PopupCounter extends JFrame {

    private NotificationHandler handler;
    private int counter;
    private JLabel lblCounter;
    
    private Color backgroundColor;
    private Color borderColor;
    private Color textColor;
    
    PopupCounter(int count, NotificationHandler h) {
        loadSettings();
        
        counter = count;
        handler = h;
    }

    //Create counter next to header when minimized (shows how many notes are hidden!)
    public void init() {
        JPanel countP = new JPanel(null);

        buildFrame();
        buildComponents(counter,countP);
        
        countP.setBackground(backgroundColor);
        countP.setBorder(BorderFactory.createLineBorder(borderColor,2));

        add(countP);

        setVisible(true);
    }

    //Launch main frame
    public void buildFrame() {
        setSize(15, 15);
        setLocation(handler.getX() - getWidth(), handler.getY());
        setType(Type.UTILITY);
        setUndecorated(true);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    
    //Add components
    public void buildComponents(int c, JPanel panel){        
        lblCounter = new JLabel(Integer.toString(c));
        lblCounter.setBounds(0,0,getWidth(),getHeight());
        lblCounter.setHorizontalAlignment(JLabel.CENTER);
        lblCounter.setVerticalAlignment(JLabel.CENTER);
        lblCounter.setForeground(textColor);
        lblCounter.setBackground(backgroundColor);
        
        panel.add(lblCounter);       
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
