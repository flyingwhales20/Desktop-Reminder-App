/*This handles creating and positioning notes in the background, and dynamically
 handles repositioning of notes
 */
package Remindr;

import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowStateListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.BorderFactory;

public class NotificationHandler extends JFrame {

    private int notifCount = 1;
    private ArrayList<Notification> noteArray = new ArrayList<>();
    private ArrayList<String> noteDataList = new ArrayList<>();

    private JButton btnNewNote;
    private JButton btnSettings;
    private JButton btnTerminate;
    private JButton btnMinMax;

    private String title;
    private String dueDateMonth;
    private String dueDateDay;
    private String dueDateYear;
    private String hour;
    private String minute;
    private String AMPM;

    private boolean min = false;

    private Color backgroundColor;
    private Color borderColor;
    private Color textColor;

    Icon icNewNote, icNewNoteHL, icSettings, icSettingsHL, icTerminate, icTerminateHL, icMinimize,
            icMinimizeHL, icMaximize, icMaximizeHL;

    private PopupCounter pc;
    private NewReminder create;
    private Notification note;
    private Settings setting;

    //This constructor will handle loading saved notes on startup (if they exist)
    NotificationHandler() {
        loadSettings();
        loadImages();
        init();
        loadNoteData();
    }

    //Load image resources
    private void loadImages() {
        try {
            icNewNote = new ImageIcon(ImageIO.read(getClass().getResourceAsStream("RemindrIR/newIcon.png")));
            icNewNoteHL = new ImageIcon(ImageIO.read(getClass().getResourceAsStream("RemindrIR/newIconHL.png")));
            icSettings = new ImageIcon(ImageIO.read(getClass().getResourceAsStream("RemindrIR/settingsIcon2.png")));
            icSettingsHL = new ImageIcon(ImageIO.read(getClass().getResourceAsStream("RemindrIR/settingsIcon2HL.png")));
            icTerminate = new ImageIcon(ImageIO.read(getClass().getResourceAsStream("RemindrIR/terminateIcon.png")));
            icTerminateHL = new ImageIcon(ImageIO.read(getClass().getResourceAsStream("RemindrIR/terminateIconHL.png")));
            icMinimize = new ImageIcon(ImageIO.read(getClass().getResourceAsStream("RemindrIR/minimizeIcon.png")));
            icMinimizeHL = new ImageIcon(ImageIO.read(getClass().getResourceAsStream("RemindrIR/minimizeIconHL.png")));
            icMaximize = new ImageIcon(ImageIO.read(getClass().getResourceAsStream("RemindrIR/maximizeIcon.png")));
            icMaximizeHL = new ImageIcon(ImageIO.read(getClass().getResourceAsStream("RemindrIR/maximizeIconHL.png")));
        } catch (IOException e) {

        }
    }

    //Build note header fully
    private void init() {
        JPanel header = new JPanel(null);
        header.setBackground(backgroundColor);
        header.setBorder(BorderFactory.createLineBorder(borderColor, 2));
        buildFrame();
        buildButtons(header);

        add(header);

        setVisible(true);
    }

    //Build note header frame
    private void buildFrame() {
        setSize(120, 30);
        //x pos relative to screen width
        setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() - getWidth() - 10, 10);
        setType(Type.UTILITY);
        setUndecorated(true);
        setResizable(false);
    }

    //Add buttons to header
    private void buildButtons(JPanel h) {
        btnNewNote = new JButton(icNewNote);
        btnNewNote.setBorder(null);
        btnNewNote.setRolloverIcon(icNewNoteHL);
        btnNewNote.setBounds(getWidth() - 115, 5, 20, 20);
        btnNewNote.setToolTipText("Add new Remindr");
        btnNewNote.addActionListener(new onClickCreate());

        btnMinMax = new JButton(icMinimize);
        btnMinMax.setBorder(null);
        btnMinMax.setRolloverIcon(icMinimizeHL);
        btnMinMax.setBounds(getWidth() - 85, 5, 20, 20);
        btnMinMax.addActionListener(new onClickHideable());
        btnMinMax.setToolTipText("Minimize Remindrs");

        btnSettings = new JButton(icSettings);
        btnSettings.setBorder(null);
        btnSettings.setRolloverIcon(icSettingsHL);
        btnSettings.setBounds(getWidth() - 55, 5, 20, 20);
        btnSettings.setToolTipText("Change app settings");
        btnSettings.addActionListener(new onClickSettings());

        btnTerminate = new JButton(icTerminate);
        btnTerminate.setBorder(null);
        btnTerminate.setRolloverIcon(icTerminateHL);
        btnTerminate.setBounds(getWidth() - 25, 5, 20, 20);
        btnTerminate.setToolTipText("Fully exit program");
        btnTerminate.addActionListener(new onClickTerminate());

        h.add(btnNewNote);
        h.add(btnMinMax);
        h.add(btnSettings);
        h.add(btnTerminate);
    }

    //Save new note to file and create program folder directory
    private void saveNoteData(String t, String dueM, String dueD, String dueY, String h, String m, String ap) {
        try {
            //user.home should allow program to run on any OS without issues...
            FileWriter newNote = new FileWriter("C:\\Remindr\\SavedNotifications.txt", true);
            String text = t + "|" + dueM + "|" + dueD + "|" + dueY + "|" + h + "|"
                    + m + "|" + ap;
            newNote.write(text);
            newNote.write(System.getProperty("line.separator"));
            newNote.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "There was an error while saving notifications.");
        }
    }

    //Load saved note data on startup and populate
    private void loadNoteData() {
        try {         
            Scanner loadEntry = new Scanner("C:/Remindr/SavedNotifications.txt");
            
            while (loadEntry.hasNextLine()) {
                //Split text line into data segments, and pass data to new note 
                //(load note!)
                String temp = loadEntry.nextLine();
                String[] tempSplit = temp.split("\\|");

                note = new Notification(
                        tempSplit[0], tempSplit[1], tempSplit[2], tempSplit[3],
                        tempSplit[4], tempSplit[5], tempSplit[6], notifCount, NotificationHandler.this);
                note.init();

                noteArray.add(note);

                setNewLocation();
            }

            loadEntry.close();
        } catch (NullPointerException e) {
            System.out.println(e);
            JOptionPane.showMessageDialog(null, "This appears to be the first run of the program.\nA new directory will be created under your home directory." + e);
        }
    }

    //If a line matches passed values, remove it
    public void removeNoteData(String t, String dueDateM, String dueDateD, String dueDateY,
            String h, String m, String ap) {
        try {
            //Resets data to sync properly
            noteDataList.clear();
            Scanner oldEntry = new Scanner(new File("C:\\Remindr\\SavedNotifications.txt"));
            //Populate list with file contents for comparison
            while (oldEntry.hasNextLine()) {
                noteDataList.add(oldEntry.nextLine());
            }
            oldEntry.close();
            //remove only if matching current note data.
            for (int i = 0; i < noteDataList.size(); i++) {
                if (noteDataList.get(i).equals(t + "|" + dueDateM + "|" + dueDateD
                        + "|" + dueDateY + "|" + h + "|" + m + "|" + ap)) {
                    noteDataList.remove(i);
                }
            }//Go through object list to remove matching data note
            for (int i = 0; i < noteArray.size(); i++) {
                if (noteArray.get(i).getTitle().equals(t)
                        && noteArray.get(i).getDueDateMonth().equals(dueDateM)
                        && noteArray.get(i).getDueDateDay().equals(dueDateD)
                        && noteArray.get(i).getDueDateYear().equals(dueDateY)
                        && noteArray.get(i).getHour().equals(h)
                        && noteArray.get(i).getMinute().equals(m)
                        && noteArray.get(i).getAMPM().equals(ap)) {
                    noteArray.remove(i);
                }
            }
            //write edited data to file
            FileWriter removeEntry = new FileWriter("C:\\Remindr\\SavedNotifications.txt");
            for (int i = 0; i < noteDataList.size(); i++) {
                removeEntry.write(noteDataList.get(i));
                removeEntry.write(System.getProperty("line.separator"));
            }
            removeEntry.close();

            setNewLocation();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "There was an error writing to the file.");
        }
    }

    //Allows other classes to disable/enable button (prevent lots of extra windows)
    public void toggleNewNoteButton(boolean visible) {
        btnNewNote.setEnabled(visible);
    }

    //Pass data to handler, create new note
    public void setValues(String t, String dueM, String dueD, String dueY, String h, String m, String ap) {
        title = t;
        dueDateMonth = dueM;
        dueDateDay = dueD;
        dueDateYear = dueY;
        hour = h;
        minute = m;
        AMPM = ap;

        if (!checkIfDuplicate(title, dueDateMonth, dueDateDay, dueDateYear)) {

            maximizeNotes(pc);

            note = new Notification(title, dueDateMonth,
                    dueDateDay, dueDateYear, hour, minute, AMPM, notifCount, NotificationHandler.this);
            note.init();

            noteArray.add(note);

            setNewLocation();

            saveNoteData(title, dueDateMonth, dueDateDay, dueDateYear, hour, minute, AMPM);
        }
        else {
            JOptionPane.showMessageDialog(null, "There is already a notification with this information.");
        }

    }

    //Launch new reminder builder when 'new' button pressed
    public class onClickCreate implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!checkIfMaxNotes()) {
                create = new NewReminder(NotificationHandler.this);
                create.init();
                toggleNewNoteButton(false);
            }
            else {
                JOptionPane.showMessageDialog(null, "The maximum number of"
                        + " notifications has been reached.");
            }
        }
    }

    //TESTINGGGGGGGGGGGG
    private void startTimer() {
        Timer timer = new Timer(2000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println(NotificationHandler.this.isFocused());
            }
        });
        timer.setRepeats(true);
        timer.start();
    }
    //toggle minimize and maximize states and respective icons
    private class onClickHideable implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            JButton minMax = (JButton) e.getSource();
            if (!min && pc == null) {
                minimizeNotes();
                pc = new PopupCounter(noteArray.size(), NotificationHandler.this);
                pc.init();
            }
            else if (min) {
                maximizeNotes(pc);
            }
        }
    }

    //Open settings window to adjust various components
    private class onClickSettings implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            setting = new Settings(NotificationHandler.this);
            setting = null;
        }
    }

    //Terminate program
    private class onClickTerminate implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }

    //Update note positions every 60 ms
    public void setNewLocation() {
        notifCount = noteArray.size();
        sortDaysLeft();
        toggleDismissButton(false);

        Timer timer = new Timer(10, new ActionListener() {
            int i = 0;
            int c = notifCount;

            public void actionPerformed(ActionEvent e) {
                if (i < c) {
                    noteArray.get(i).updateLocation(i + 1);
                    i++;
                }
                else {
                    toggleDismissButton(true);
                    ((Timer) e.getSource()).stop();
                }
            }
        }
        );
        timer.start();
    }

    //Sort note array objects by daysLeft
    public void sortDaysLeft() {
        for (int i = 0; i < noteArray.size(); i++) {
            for (int j = noteArray.size() - 1; j > i; j--) {
                if (noteArray.get(i).getDaysLeft() > noteArray.get(j).getDaysLeft()) {
                    noteArray.set(i, noteArray.set(j, noteArray.get(i)));
                }
            }
        }
    }

    //Enable/disable note buttons while sorting(prevent crashing)
    public void toggleDismissButton(boolean enable) {
        //Disable dismiss button to avoid crashing
        for (int i = 0; i < noteArray.size(); i++) {
            noteArray.get(i).enableDismissButton(enable);
        }
    }

    //Checks to make sure next note created is < screen height
    public boolean checkIfMaxNotes() {
        Rectangle screenSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        if ((noteArray.size() + 1) * 40 >= screenSize.getHeight() - 40) {
            return true;
        }
        else {
            return false;
        }
    }

    //Check if current note is a duplicate for one to be made
    public boolean checkIfDuplicate(String t, String dueM, String dueD, String dueY) {
        for (int i = 0; i < noteArray.size(); i++) {
            if (noteArray.get(i).getTitle().equals(t)
                    && noteArray.get(i).getDueDateMonth().equals(dueM)
                    && noteArray.get(i).getDueDateDay().equals(dueD)
                    && noteArray.get(i).getDueDateYear().equals(dueY)) {
                return true;
            }
        }
        return false;
    }

    //Hide all notes (minimize)
    public void minimizeNotes() {
        for (int i = 0; i < noteArray.size(); i++) {
            noteArray.get(i).setVisible(false);
        }
        btnMinMax.setIcon(icMaximize);
        btnMinMax.setRolloverIcon(icMaximizeHL);
        btnMinMax.setToolTipText("Maximize Remindrs");
        min = true;
    }

    //Show all notes (Maximize)
    public void maximizeNotes(PopupCounter pc) {
        for (int i = 0; i < noteArray.size(); i++) {
            noteArray.get(i).setVisible(true);
        }
        //Make sure only 1 instance of counter exists, then reset
        if (this.pc != null) {
            pc.dispose();
            this.pc = null;
        }
        btnMinMax.setIcon(icMinimize);
        btnMinMax.setRolloverIcon(icMinimizeHL);
        btnMinMax.setToolTipText("Minimize Remindrs");
        min = false;
    }

    //Load settings set by user
    public void loadSettings() {
        try {
            Properties prop = new Properties();
            InputStream input = new FileInputStream("C:\\Remindr\\RemindrSettings.properties");

            prop.load(input);

            backgroundColor = new Color(Integer.parseInt(prop.getProperty("bgCol")));
            borderColor = new Color(Integer.parseInt(prop.getProperty("bdrCol")));
            textColor = new Color(Integer.parseInt(prop.getProperty("txtCol")));
        } catch (Exception ex) {
            backgroundColor = Color.LIGHT_GRAY;
            borderColor = Color.BLACK;
            textColor = Color.BLACK;
        }
    }

    /*Reset whole application and apply settings
    Settings is not reset here, as it controls the program reset.
    The settings class disposes itself AFTER creating a new handler.
    Clear program counter reference
    Clear note creation reference
    Clear all current note references
    Clear the handler
    */
    public void reset() {
        if (pc != null) {
            pc.dispose();
        }
        if (create != null) {
            create.dispose();
        }
        for(int i=0; i <noteArray.size(); i++){
            noteArray.get(i).dispose();
        }
        if (note != null) {
            note.dispose();
        }
        if (this != null) {
            NotificationHandler.this.dispose();
        }
    }
}
