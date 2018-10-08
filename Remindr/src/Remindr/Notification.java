/* This class is the note the user creates that will be saved to the desktop.
 It displays the info given in the previous window and calculates how many
 days are left until the deadline, and colors that number accordingly based
 on critical level.
 */
package Remindr;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Properties;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class Notification extends JFrame {

    private String title;
    private String dueDateMonth;
    private String dueDateDay;
    private String dueDateYear;
    private String hour;
    private String minute;
    private String AMPM;
    private int daysLeft;

    private int notifCount;

    private JLabel lblTitle;
    private JLabel lblDueDate;
    private JLabel lblDaysLeft;

    private JLabel lblBackground;

    private JButton btnDismiss;

    //Settings
    private Color backgroundColor;
    private Color borderColor;
    private Color textColor;
    private int lower;
    private int upper;
    private String layoutState;

    NotificationHandler handler;

    Icon icDismiss, icDismissHL;

    Notification(String t, String dueDateM, String dueDateD,
            String dueDateY, String hr, String m, String ap, int count, NotificationHandler h) {
        loadSettings();
        loadImages();
        //Passing data from previous frame to current
        title = t;
        dueDateMonth = dueDateM;
        dueDateDay = dueDateD;
        dueDateYear = dueDateY;
        notifCount = count;
        hour = hr;
        minute = m;
        AMPM = ap;

        handler = h;
    }

    //Load image Resources
    private void loadImages() {
        try {
            icDismiss = new ImageIcon(ImageIO.read(getClass().getResourceAsStream("RemindrIR/saveIcon2.png")));
            icDismissHL = new ImageIcon(ImageIO.read(getClass().getResourceAsStream("RemindrIR/saveIcon2HL.png")));
        } catch (IOException e) {

        }
    }

    //Initialize frame
    public void init() {
        JPanel notif = new JPanel(null);
        notif.setBackground(backgroundColor);
        notif.setBorder(BorderFactory.createLineBorder(borderColor, 2));

        buildFrame();
        buildButtons(notif);
        buildText(notif);

        notif.add(lblBackground);
        add(notif);

        setVisible(true);
    }

    //Build frame
    private void buildFrame() {
        if (layoutState.equals("normal")) {
            setSize(300, 40);
            //x pos relative to screen width
            setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() - 310, 40 * notifCount);
        }
        else {
            setSize(150, 40);
            //x pos relative to screen width
            setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() - 150 - 10, 40 * notifCount);
        }
        //Hides from on taskbar
        setType(Type.UTILITY);
        setUndecorated(true);
        setResizable(false);

        //Set popup background color (easier way)
        lblBackground = new JLabel();
        lblBackground.setBounds(0, 0, getWidth(), getHeight());
    }

    //Add button components
    private void buildButtons(JPanel reminder) {
        btnDismiss = new JButton(icDismiss);
        btnDismiss.setBorder(null);
        btnDismiss.setRolloverIcon(icDismissHL);
        btnDismiss.setBounds(getWidth() - 25, 10, 20, 20);
        btnDismiss.addActionListener(new onClickClose());
        btnDismiss.setToolTipText("Dismiss!");

        reminder.add(btnDismiss);
    }

    //Add text to note
    private void buildText(JPanel note) {
        lblTitle = new JLabel();
        if (!hour.equals("0")) {
            lblTitle.setText(title + "  @ " + hour + ":" + minute + " " + AMPM);
        }
        else {
            lblTitle.setText(title);
        }
        if (layoutState.equals("normal")) {
            lblTitle.setBounds(6, 6, 250, 15);
        }
        else {
            lblTitle.setBounds(6, 6, 100, 15);
        }
        lblTitle.setBackground(backgroundColor);
        lblTitle.setForeground(textColor);

        lblDueDate = new JLabel(dueDateMonth + "/" + dueDateDay + "/" + dueDateYear);
        if (layoutState.equals("normal")) {
            lblDueDate.setBounds(15, 22, 100, 15);
        }
        else {
            lblDueDate.setBounds(50, 22, 100, 15);
        }
        lblDueDate.setBackground(backgroundColor);
        lblDueDate.setForeground(textColor);

        lblDaysLeft = new JLabel("",SwingConstants.CENTER);

        calcDaysLeft();
        if (layoutState.equals("normal")) {
            lblDaysLeft.setText(" " + daysLeft + " days left!");
            lblDaysLeft.setBounds(120, 22, 90, 15);
        }
        else{
            lblDaysLeft.setText(String.valueOf(daysLeft));
            lblDaysLeft.setBounds(5, 22, 25, 15);
        }
        lblDaysLeft.setOpaque(true);
        lblDaysLeft.setBackground(Color.black);

        note.add(lblTitle);
        note.add(lblDueDate);
        note.add(lblDaysLeft);
    }

    //Calculate how many days are left until item is due
    private void calcDaysLeft() {
        String endDate = dueDateYear + "-" + dueDateMonth + "-" + dueDateDay;
        LocalDate startDate = LocalDate.now();

        //Calculates how many days are left between dates, min zero
        daysLeft = (int) ChronoUnit.DAYS.between(startDate, LocalDate.parse(endDate));
        if (daysLeft < 0) {
            daysLeft = 0;
        }

        //Change text color based on days left (green,yellow,red)
        //Values may be changeable through settings?
        if (daysLeft >= upper) {
            lblDaysLeft.setForeground(Color.green);
        }
        else if (daysLeft >= lower) {
            lblDaysLeft.setForeground(Color.yellow);
        }
        else if (daysLeft < lower) {
            lblDaysLeft.setForeground(Color.red);
        }

    }

    //Dismiss note
    private class onClickClose implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            //Will also need to call handler to update locations
            handler.removeNoteData(title, dueDateMonth, dueDateDay, dueDateYear, hour, minute, AMPM);
            dispose();
        }
    }

    //Update note position if others are dismissed
    public void updateLocation(int count) {
        if (layoutState.equals("normal")) {
            setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() - 310, 40 * count);
        }
        else {
            setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() - 150 - 10, 40 * count);
        }
    }

    //Return daysLeft
    public int getDaysLeft() {
        return daysLeft;
    }

    //Return title
    public String getTitle() {
        return title;
    }

    //Return dueDateMonth
    public String getDueDateMonth() {
        return dueDateMonth;
    }

    //Return dueDateDay
    public String getDueDateDay() {
        return dueDateDay;
    }

    //Return dueDateYear
    public String getDueDateYear() {
        return dueDateYear;
    }

    //Return hour
    public String getHour() {
        return hour;
    }

    //Return minute
    public String getMinute() {
        return minute;
    }

    //Return AM or PM
    public String getAMPM() {
        return AMPM;
    }

    //Enable/disable dismiss button while sorting
    public void enableDismissButton(boolean visible) {
        btnDismiss.setEnabled(visible);
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
            upper = Integer.parseInt(prop.getProperty("upper"));
            lower = Integer.parseInt(prop.getProperty("low"));
            layoutState = prop.getProperty("layout");
        } catch (Exception ex) {
            backgroundColor = Color.LIGHT_GRAY;
            borderColor = Color.BLACK;
            textColor = Color.BLACK;
            upper = 20;
            lower = 10;
            layoutState = "normal";
        }
    }
}
