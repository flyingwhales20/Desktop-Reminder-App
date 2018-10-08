/*This class allows the user to create a new note through a GUI.*/
package Remindr;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.Date;
import java.util.Properties;
import javax.imageio.ImageIO;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;

public class NewReminder extends JFrame {

    private String title;
    private String dueDateMonth;
    private String dueDateDay;
    private String dueDateYear;
    private String hour;
    private String minute;
    private String AMPM;

    private JLabel lblDueDate;

    private JTextField txtTitle;
    private JComboBox<String> cmboDueDateMonth;
    private JComboBox<String> cmboDueDateDay;
    private JComboBox<String> cmboDueDateYear;

    private JButton btnSave;
    private JButton btnClose;

    private String[] tip = new String[7];
    private JLabel lblTip;

    private JSpinner spinHour;
    private JSpinner spinMinute;
    private JSpinner spinAMPM;

    private JCheckBox chkTime;

    //Settings
    private Color backgroundColor;
    private Color textColor;
    private boolean emailing;
    private String firstName;
    private String lastName;
    private String address;

    NotificationHandler handler;

    //Add font types and stuff
    Font fItalic = new Font("Serif", Font.ITALIC, 14);

    Icon icSave, icSaveHL, icClose, icCloseHL;

    NewReminder(NotificationHandler h) {
        super("New Reminder");
        loadSettings();
        handler = h;
        loadImages();
    }

    //Load image resources
    private void loadImages() {
        try {
            icSave = new ImageIcon(ImageIO.read(getClass().getResourceAsStream("RemindrIR/saveIcon2.png")));
            icSaveHL = new ImageIcon(ImageIO.read(getClass().getResourceAsStream("RemindrIR/saveIcon2HL.png")));
            icClose = new ImageIcon(ImageIO.read(getClass().getResourceAsStream("RemindrIR/closeIcon2.png")));
            icCloseHL = new ImageIcon(ImageIO.read(getClass().getResourceAsStream("RemindrIR/closeIcon2HL.png")));
        } catch (IOException e) {

        }
    }

    //Initialize frame
    public void init() {
        JPanel newRem = new JPanel(null);
        newRem.setBackground(backgroundColor);

        //Tips show up when mouse hover over components
        tip[0] = "Enter the name of your Reminder.";
        tip[1] = "Enter the month element of the due date.";
        tip[2] = "Enter the day element of the due date.";
        tip[3] = "Enter the year element of the due date.";
        tip[4] = "Save as new Reminder; creates desktop notification.";
        tip[5] = "Close window without saving - Program still runs.";
        tip[6] = "Check this to add the time to the due date.";

        buildFrame();
        buildTitle(newRem);
        buildDueDate(newRem);
        buildTime(newRem);
        buildButtons(newRem);
        buildTips(newRem);

        //Allow user to save new note without pressing button (press enter)
        this.getRootPane().setDefaultButton(btnSave);

        add(newRem);
        setVisible(true);

        //No components selected by default at startup
        requestFocusInWindow();
    }

    //Build frame
    private void buildFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(340, 150);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    //Add title components
    private void buildTitle(JPanel reminder) {
        txtTitle = new JTextField("--Title--");
        txtTitle.setBounds(10, 5, 250, 20);
        txtTitle.addMouseListener(new mouseEnterComponent());
        txtTitle.setName("Title");
        txtTitle.addFocusListener(new FocusListener() {
            //This sets default text if not focused, and no text typed
            public void focusGained(FocusEvent e) {
                JTextField text = (JTextField) e.getSource();
                if (text.getText().equals("--Title--")) {
                    text.setText("");
                }
            }

            public void focusLost(FocusEvent e) {
                JTextField text = (JTextField) e.getSource();
                if (text.getText().equals("")) {
                    text.setText("--Title--");
                }
            }
        });

        reminder.add(txtTitle);
    }

    //Add due date components
    private void buildDueDate(JPanel reminder) {

        //Replace with combo boxes. to populate days just use switch
        //based on month selected?
        lblDueDate = new JLabel("Deadline (mm/dd/yyyy)");
        lblDueDate.setBounds(10, 40, 130, 20);
        lblDueDate.setBackground(backgroundColor);
        lblDueDate.setForeground(textColor);

        cmboDueDateMonth = new JComboBox<>();
        cmboDueDateMonth.setBounds(140, 40, 60, 20);
        populateMonth();
        cmboDueDateMonth.addMouseListener(new mouseEnterComponent());
        cmboDueDateMonth.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                populateDay();
            }
        });
        cmboDueDateMonth.setName("Month");

        cmboDueDateDay = new JComboBox<>();
        cmboDueDateDay.setBounds(cmboDueDateMonth.getX() + 60, cmboDueDateMonth.getY(), 60, 20);
        cmboDueDateDay.addMouseListener(new mouseEnterComponent());
        cmboDueDateDay.setName("Day");

        cmboDueDateYear = new JComboBox<>();
        cmboDueDateYear.setBounds(cmboDueDateDay.getX() + 60, cmboDueDateDay.getY(), 60, 20);
        populateYear();
        cmboDueDateYear.addMouseListener(new mouseEnterComponent());
        cmboDueDateYear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                populateDay();
            }
        });
        cmboDueDateYear.setName("Year");

        reminder.add(lblDueDate);
        reminder.add(cmboDueDateMonth);
        reminder.add(cmboDueDateDay);
        reminder.add(cmboDueDateYear);
    }

    //Populate month field
    private void populateMonth() {
        //Populates field in 00 format
        cmboDueDateMonth.addItem("");
        for (int i = 1; i <= 12; i++) {
            if (i < 10) {
                cmboDueDateMonth.addItem("0" + Integer.toString(i));
            }
            else {
                cmboDueDateMonth.addItem(Integer.toString(i));
            }
        }
        cmboDueDateMonth.setSelectedIndex(0);
    }

    //populate day field
    private void populateDay() {
        //Dynamically populate day by month and (leap) year selected: need better way

        int dayPos = cmboDueDateDay.getSelectedIndex();
        int maxDays = 0;
        int year = Integer.valueOf((String) cmboDueDateYear.getSelectedItem());
        cmboDueDateDay.removeAllItems();
        cmboDueDateDay.addItem("");
        switch (cmboDueDateMonth.getSelectedIndex()) {
        case 1:
            maxDays = 31;
            break;
        case 3:
            maxDays = 31;
            break;
        case 5:
            maxDays = 31;
            break;
        case 7:
            maxDays = 31;
            break;
        case 8:
            maxDays = 31;
            break;
        case 10:
            maxDays = 31;
            break;
        case 12:
            maxDays = 31;
            break;
        case 4:
            maxDays = 30;
            break;
        case 6:
            maxDays = 30;
            break;
        case 9:
            maxDays = 30;
            break;
        case 11:
            maxDays = 30;
            break;
        case 2:
            if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
                maxDays = 29;
                break;
            }
            else {
                maxDays = 28;
                break;
            }
        }
        for (int i = 1; i <= maxDays; i++) {
            if (i < 10) {
                cmboDueDateDay.addItem("0" + Integer.toString(i));
            }
            else {
                cmboDueDateDay.addItem(Integer.toString(i));
            }
        }
        if (dayPos > maxDays) {//Maybe user can set to clear, or set to max Days?
            cmboDueDateDay.setSelectedIndex(0);
        }
        else {
            cmboDueDateDay.setSelectedIndex(dayPos);
        }
    }

    //Populate year field
    private void populateYear() {
        //cmboDueDateYear.addItem("");
        for (int i = Year.now().getValue(); i <= Year.now().getValue() + 4; i++) {
            cmboDueDateYear.addItem(Integer.toString(i));
        }
        cmboDueDateYear.setSelectedIndex(0);
    }

    //Build time components
    private void buildTime(JPanel reminder) {
        chkTime = new JCheckBox("Time");
        chkTime.setBackground(backgroundColor);
        chkTime.setForeground(textColor);
        chkTime.setVerticalTextPosition(SwingConstants.TOP);
        chkTime.setHorizontalTextPosition(SwingConstants.CENTER);
        chkTime.setBounds(10, 65, 40, 35);
        chkTime.setName("Time");
        chkTime.addMouseListener(new mouseEnterComponent());
        chkTime.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!chkTime.isSelected()) {
                    toggleTime(false);
                }
                else {
                    toggleTime(true);
                }
            }
        });

        //Sets Spinner default values
        Date time1 = null;
        Date time2 = null;
        SimpleDateFormat date1 = new SimpleDateFormat("h");
        SimpleDateFormat date2 = new SimpleDateFormat("mm");
        try {
            time1 = date1.parse("0");
            time2 = date2.parse("00");
        } catch (ParseException ex) {
            System.exit(0);
        }

        SpinnerDateModel spinH = new SpinnerDateModel();
        spinHour = new JSpinner(spinH);
        spinHour.setEditor(new JSpinner.DateEditor(spinHour, "h"));
        spinHour.setValue(time1);
        spinHour.setBounds(50, 75, 35, 25);

        SpinnerDateModel spinM = new SpinnerDateModel();
        spinMinute = new JSpinner(spinM);
        spinMinute.setEditor(new JSpinner.DateEditor(spinMinute, "mm"));
        spinMinute.setValue(time2);
        spinMinute.setBounds(spinHour.getX() + 40, spinHour.getY(), 35, 25);

        SpinnerDateModel spinAP = new SpinnerDateModel();
        spinAMPM = new JSpinner(spinAP);
        spinAMPM.setEditor(new JSpinner.DateEditor(spinAMPM, "a"));
        spinAMPM.setBounds(spinHour.getX() + 80, spinHour.getY(), 40, 25);

        toggleTime(false);

        reminder.add(chkTime);
        reminder.add(spinHour);
        reminder.add(spinMinute);
        reminder.add(spinAMPM);
    }

    //Enable/disable spinner buttons on check box checked
    private void toggleTime(boolean e) {
        spinHour.setEnabled(e);
        spinMinute.setEnabled(e);
        spinAMPM.setEnabled(e);
    }

    //Add button components
    private void buildButtons(JPanel reminder) {
        btnSave = new JButton(icSave);
        btnSave.setBorder(null);
        btnSave.setRolloverIcon(icSaveHL);
        btnSave.setBounds(268, 75, 20, 20);
        btnSave.addMouseListener(new mouseEnterComponent());
        btnSave.addActionListener(new onClickCreate());
        btnSave.setName("Save");

        btnClose = new JButton(icClose);
        btnClose.setBorder(null);
        btnClose.setRolloverIcon(icCloseHL);
        btnClose.setBounds(btnSave.getX() + 30, btnSave.getY(), 20, 20);
        btnClose.addMouseListener(new mouseEnterComponent());
        btnClose.addActionListener(new onClickClose());
        btnClose.setName("Close");

        reminder.add(btnSave);
        reminder.add(btnClose);
    }

    //Add tip components (Show up at bottom of window)
    private void buildTips(JPanel reminder) {
        lblTip = new JLabel();
        lblTip.setBounds(5, 100, 320, 20);
        lblTip.setFont(fItalic);
        lblTip.setBackground(backgroundColor);
        lblTip.setForeground(textColor);

        reminder.add(lblTip);
    }

    //Tips show up when component is hovered over, and clears when exited.
    private class mouseEnterComponent extends MouseAdapter {

        @Override
        public void mouseEntered(MouseEvent e) {
            Component component = (Component) e.getSource();

            if (component.getName().equals("Title")) {
                lblTip.setText(tip[0]);
            }
            if (component.getName().equals("Month")) {
                lblTip.setText(tip[1]);
            }
            if (component.getName().equals("Day")) {
                lblTip.setText(tip[2]);
            }
            if (component.getName().equals("Year")) {
                lblTip.setText(tip[3]);
            }
            if (component.getName().equals("Save")) {
                lblTip.setText(tip[4]);
            }
            if (component.getName().equals("Close")) {
                lblTip.setText(tip[5]);
            }
            if (component.getName().equals("Time")) {
                lblTip.setText(tip[6]);
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            lblTip.setText("");
        }

    }

    //Close main window, while program continues to run (maintain notes)
    private class onClickClose implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            handler.toggleNewNoteButton(true);
            dispose();
        }
    }

    //Close current window and create desktop note object through handler
    private class onClickCreate implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (verifyData()) {
                deriveData();
                handler.setValues(title, dueDateMonth, dueDateDay, dueDateYear, hour, minute, AMPM);
                handler.toggleNewNoteButton(true);
                if (emailing) {
                    //Send email in parallel thread to avoid lag
                    Email email = new Email();
                    email.start();
                }
                dispose();
            }
        }
    }

    //Pull data from text fields and assign/convert to variables
    private void deriveData() {
        title = txtTitle.getText();
        dueDateMonth = (String) cmboDueDateMonth.getSelectedItem();
        dueDateDay = (String) cmboDueDateDay.getSelectedItem();
        dueDateYear = (String) cmboDueDateYear.getSelectedItem();
        if (chkTime.isSelected()) {
            hour = new SimpleDateFormat("h").format(spinHour.getValue());
            minute = new SimpleDateFormat("mm").format(spinMinute.getValue());
            AMPM = new SimpleDateFormat("a").format(spinAMPM.getValue());
        }
        else {
            hour = "0";
            minute = "00";
            AMPM = "AM";
        }
    }

    //Returns false if a field is not filled (reminder thus not created)
    private boolean verifyData() {
        if (txtTitle.getText().equals("--Title--")
                || txtTitle.getText().equals("")
                || cmboDueDateMonth.getSelectedIndex() == 0
                || cmboDueDateDay.getSelectedIndex() == 0) {
            PopupMessage vd = new PopupMessage((int) getX()+20, (int) getY()+10,
                    "Title and Due Date must be filled!");
            
            return false;
        }
        else {
            return true;
        }
    }

    //Send Remindr email to user if enabled: need to rework for settings (diff emails for diff users)
    private void sendEmail() {
        //This allows the programmer to send authorized emails. Change to company address...
        final String username = "nybbledesignsolutions@gmail.com";
        final String password = "ShootingStar23";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username, "Remindr"));
            message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(address));
            message.setSubject("New Remindr!");
            if (!hour.equals("0")) {
                message.setText(
                        "Hello, " + firstName + " " + lastName + "!\n"+
                        title + " will be due:\n\n    " + dueDateMonth + "/" + dueDateDay + "/" + dueDateYear
                        + "\n    At " + hour + ":" + minute + " " + AMPM
                        + "\n\n\n\nIf you wish to stop receiving Remindr emails: "
                        + "Open Remindr->Settings->Email and disable email"
                        + " capability.");
            }
            else {
                message.setText(
                        "Hello, " + firstName + " " + lastName + "!\n"+
                        title + " will be due:\n\n    " + dueDateMonth + "/" + dueDateDay + "/" + dueDateYear
                        + "\n    No time specified"
                        + "\n\n\n\nIf you wish to stop receiving Remindr emails: "
                        + "Open Remindr->Settings->Email and disable email"
                        + " capability.");
            }
            Transport.send(message);
            PopupMessage p1 = new PopupMessage(
                    (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth()-430, -80, 
                    "Email sent!");
        } catch (MessagingException | UnsupportedEncodingException e) {
            PopupMessage p2 = new PopupMessage(
                    (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth()-430, -80, 
                    "Email was unable to be sent.");
            JOptionPane.showMessageDialog(null,e);
        }
    }

    //Separate thread to remove lag of sending email
    public class Email extends Thread {

        public void run() {
            sendEmail();
        }
    }

    //Load settings set by user
    private void loadSettings() {
        try {
            Properties prop = new Properties();
            InputStream input = new FileInputStream("C:\\Remindr\\RemindrSettings.properties");

            prop.load(input);

            backgroundColor = new Color(Integer.parseInt(prop.getProperty("bgCol")));
            textColor = new Color(Integer.parseInt(prop.getProperty("txtCol")));
            emailing = Boolean.parseBoolean(prop.getProperty("email"));
            firstName = prop.getProperty("fname");
            lastName = prop.getProperty("lname");
            address = prop.getProperty("addr");
        } catch (Exception ex) {
            backgroundColor = Color.LIGHT_GRAY;
            textColor = Color.BLACK;
            emailing = false;
            firstName = "";
            lastName = "";
            address = "";
        }
    }
}
