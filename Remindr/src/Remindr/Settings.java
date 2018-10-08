/*
 This class/window allows the user to change various settings for Remindr. This
 includes color schemes, modular note sizes, email capability, and settings daysLeft
 boundaries. Header colors will always be slightly darker than the actual note colors.
 */
package Remindr;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Properties;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Settings extends JFrame {

    private JLabel lblBGCol;
    private JLabel lblBCol;
    private JLabel lblTextCol;
    private JLabel lblSample;
    private JLabel lblFName;
    private JLabel lblLName;
    private JLabel lblEmail;
    private JLabel lblDisabled;
    private JLabel lblUpper;
    private JLabel lblLower;

    private JComboBox<String> cmboBGCol;
    private JComboBox<String> cmboBCol;
    private JComboBox<String> cmboTextCol;

    private JSlider sldUpper;
    private JSlider sldLower;

    private JTextField txtFName;
    private JTextField txtLName;
    private JTextField txtEmail;

    private JButton btnSave;
    private JButton btnClose;

    private JCheckBox chkEmail;

    private JRadioButton radLayoutNorm;
    private JRadioButton radLayoutSmall;

    //Settings
    private Color backgroundColor;
    private Color borderColor;
    private Color textColor;
    private boolean emailing;
    private String firstName;
    private String lastName;
    private String address;
    private int lower;
    private int upper;
    String layoutState;

    private static int COUNT = 0;

    String[] colors = {"Black", "White", "Gray", "Light Gray", "Red",
        "Yellow", "Orange", "Blue", "Light Blue",
        "Green", "Light Green"};

    ArrayList<Color> col = new ArrayList<>();

    Icon icSave, icSaveHL, icClose, icCloseHL;
    
    NotificationHandler handler;

    Settings(NotificationHandler h) {
        super("Settings");
        handler = h;
        loadSettings();
        loadImages();

        if (COUNT == 0) {
            populateColors();

            init();
        }
        COUNT = 1;
    }

    //Load Image Resources
    private void loadImages() {
        try {
            icSave = new ImageIcon(ImageIO.read(getClass().getResourceAsStream("RemindrIR/saveIcon2.png")));
            icSaveHL = new ImageIcon(ImageIO.read(getClass().getResourceAsStream("RemindrIR/saveIcon2HL.png")));
            icClose = new ImageIcon(ImageIO.read(getClass().getResourceAsStream("RemindrIR/closeIcon2.png")));
            icCloseHL = new ImageIcon(ImageIO.read(getClass().getResourceAsStream("RemindrIR/closeIcon2HL.png")));
        } catch (IOException e) {

        }
    }

    //Create window and components
    private void init() {
        JPanel pComponents = new JPanel(null);
        pComponents.setBackground(backgroundColor.darker());
        pComponents.setBorder(BorderFactory.createLineBorder(borderColor, 2));

        buildFrame();
        buildTabComponents(pComponents);
        buildButtons(pComponents);

        add(pComponents);
        //Auto position to screen center
        setLocationRelativeTo(null);
        setVisible(true);
    }

    //Build frame - using flow layout instead of abs pos! Be careful!
    private void buildFrame() {
        setSize(400, 300);
        //Hides from on taskbar
        setType(Type.UTILITY);
        setUndecorated(true);
        setResizable(false);
    }

    //Build main tab and their displays
    private void buildTabComponents(JPanel c) {
        JTabbedPane settingsTab = new JTabbedPane();
        settingsTab.setBounds(0, 0, getWidth(), getHeight() - 30);
        settingsTab.setBackground(backgroundColor);
        settingsTab.setForeground(textColor);
        settingsTab.setBorder(BorderFactory.createLineBorder(borderColor, 2));

        JPanel pColors = new JPanel(null);
        pColors.setBackground(backgroundColor);
        pColors.setBorder(BorderFactory.createLineBorder(borderColor, 2));
        buildColorPanel(pColors);

        JPanel pEmail = new JPanel(null);
        pEmail.setBackground(backgroundColor);
        pEmail.setBorder(BorderFactory.createLineBorder(borderColor, 2));
        buildEmailPanel(pEmail);

        JPanel pDaysLeft = new JPanel(null);
        pDaysLeft.setBackground(backgroundColor);
        pDaysLeft.setBorder(BorderFactory.createLineBorder(borderColor, 2));
        buildDaysLeftPanel(pDaysLeft);

        JPanel pLayout = new JPanel(null);
        pLayout.setBackground(backgroundColor);
        pLayout.setBorder(BorderFactory.createLineBorder(borderColor, 2));
        buildLayoutPanel(pLayout);

        settingsTab.addTab("Colors", null, pColors, "Change color schemes.");
        settingsTab.addTab("Email", null, pEmail, "Get email Remindrs.");
        settingsTab.addTab("Days Left", null, pDaysLeft, "Set Days Left bounds.");
        settingsTab.addTab("Layout", null, pLayout, "Change layout settings.");

        c.add(settingsTab);
    }

    //Add color changing components
    private void buildColorPanel(JPanel c) {
        lblBGCol = new JLabel("Background Color", JLabel.CENTER);
        lblBGCol.setBackground(backgroundColor);
        lblBGCol.setForeground(textColor);

        cmboBGCol = new JComboBox<>(colors);

        cmboBGCol.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateSampleColors();
            }
        });

        JPanel pBGCol = new JPanel(new BorderLayout());
        pBGCol.setBackground(backgroundColor);
        pBGCol.add(lblBGCol, BorderLayout.NORTH);
        pBGCol.add(cmboBGCol, BorderLayout.SOUTH);
        pBGCol.setBounds(5, 60, 120, 60);

        lblBCol = new JLabel("Border Color", JLabel.CENTER);
        lblBCol.setBackground(backgroundColor);
        lblBCol.setForeground(textColor);

        cmboBCol = new JComboBox<>(colors);
        cmboBCol.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateSampleColors();
            }
        });

        JPanel pBCol = new JPanel(new BorderLayout());
        pBCol.setBackground(backgroundColor);
        pBCol.add(lblBCol, BorderLayout.NORTH);
        pBCol.add(cmboBCol, BorderLayout.SOUTH);
        pBCol.setBounds(135, 60, 120, 60);

        lblTextCol = new JLabel("Text Color", JLabel.CENTER);
        lblTextCol.setBackground(backgroundColor);
        lblTextCol.setForeground(textColor);

        cmboTextCol = new JComboBox<>(colors);
        cmboTextCol.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateSampleColors();
            }
        });

        JPanel pTextCol = new JPanel(new BorderLayout());
        pTextCol.setBackground(backgroundColor);
        pTextCol.add(lblTextCol, BorderLayout.NORTH);
        pTextCol.add(cmboTextCol, BorderLayout.SOUTH);
        pTextCol.setBounds(265, 60, 120, 60);

        lblSample = new JLabel("Sample Text", JLabel.CENTER);
        lblSample.setOpaque(true);
        lblSample.setBounds(50, 180, 300, 40);
        updateSampleColors();

        //Set drop downs to the saved values
        for (int i = 0; i < col.size(); i++) {
            if (backgroundColor.equals(col.get(i))) {
                cmboBGCol.setSelectedIndex(i);
            }
        }
        for (int i = 0; i < col.size(); i++) {
            if (borderColor.equals(col.get(i))) {
                cmboBCol.setSelectedIndex(i);
            }
        }
        for (int i = 0; i < col.size(); i++) {
            if (textColor.equals(col.get(i))) {
                cmboTextCol.setSelectedIndex(i);
            }
        }

        c.add(pBGCol);
        c.add(pBCol);
        c.add(pTextCol);
        c.add(lblSample);
    }

    //add layout changing components
    //Allow user to change size of notes from normal to smaller (take less space on desktop)
    private void buildLayoutPanel(JPanel l) {
        radLayoutNorm = new JRadioButton("<html>Normal Sized<br><br>Notifications appear full sized"
                + " and all components are spread out for easier reading. Full details are shown.</html>");
        radLayoutNorm.setBounds(30, 20, 150, 150);
        radLayoutNorm.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                radLayoutNorm.setSelected(true);
                radLayoutSmall.setSelected(false);
            }
        });
        radLayoutNorm.setVerticalTextPosition(SwingConstants.TOP);
        radLayoutNorm.setToolTipText("Notifications are full sized");
        radLayoutNorm.setBackground(backgroundColor);
        radLayoutNorm.setForeground(textColor);

        radLayoutSmall = new JRadioButton("<html>Small Sized<br><br>Notifications are smaller sized"
                + " to save space on the desktop. Only important details are shown.</html>");
        radLayoutSmall.setBounds(220, 20, 150, 150);
        radLayoutSmall.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                radLayoutSmall.setSelected(true);
                radLayoutNorm.setSelected(false);
            }
        });
        radLayoutSmall.setVerticalTextPosition(SwingConstants.TOP);
        radLayoutSmall.setToolTipText("Notifications are smaller sized");
        radLayoutSmall.setBackground(backgroundColor);
        radLayoutSmall.setForeground(textColor);
        
        if(layoutState.equals("normal")){
            radLayoutNorm.setSelected(true);
        }
        else{
            radLayoutSmall.setSelected(true);
        }

        l.add(radLayoutNorm);
        l.add(radLayoutSmall);
    }

    //add email functionality components
    private void buildEmailPanel(JPanel e) {
        JPanel emailParts = new JPanel(null);
        emailParts.setBackground(backgroundColor);

        lblDisabled = new JLabel("Enable to be able to send an email Remindr to yourself!");
        lblDisabled.setBounds(15, 120, getWidth(), 20);
        lblDisabled.setFont(new Font("Serif", Font.BOLD, 16));
        lblDisabled.setBackground(backgroundColor);
        lblDisabled.setForeground(textColor);

        emailParts.setBounds(10, getHeight() / 2 - 140, 350, 200);
        chkEmail = new JCheckBox();
        chkEmail.setSelected(emailing);
        if (emailing) {
            chkEmail.setText("Emailing is enabled");
        }
        else {
            chkEmail.setText("Emailing is disabled");
        }
        chkEmail.setVerticalTextPosition(SwingConstants.CENTER);
        chkEmail.setHorizontalTextPosition(SwingConstants.RIGHT);
        chkEmail.setBounds(20, 20, getWidth() - 50, 20);
        chkEmail.setToolTipText("Toggle email functionality");
        chkEmail.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!chkEmail.isSelected()) {
                    chkEmail.setText("Emailing is disabled");
                    toggleEmailComponents(false, emailParts);
                }
                else {
                    chkEmail.setText("Emailing is enabled");
                    toggleEmailComponents(true, emailParts);
                }
            }
        });
        chkEmail.setBackground(backgroundColor);
        chkEmail.setForeground(textColor);

        lblFName = new JLabel("First name");
        lblFName.setBounds(50, 70, 60, 20);
        lblFName.setBackground(backgroundColor);
        lblFName.setForeground(textColor);

        txtFName = new JTextField();
        txtFName.setText(firstName);
        txtFName.setBounds(115, 70, 130, 20);

        lblLName = new JLabel("Last name");
        lblLName.setBounds(50, 100, 60, 20);
        lblLName.setBackground(backgroundColor);
        lblLName.setForeground(textColor);

        txtLName = new JTextField();
        txtLName.setText(lastName);
        txtLName.setBounds(115, 100, 130, 20);

        lblEmail = new JLabel("Email address");
        lblEmail.setBounds(30, 130, 90, 20);
        lblEmail.setBackground(backgroundColor);
        lblEmail.setForeground(textColor);

        txtEmail = new JTextField();
        txtEmail.setText(address);
        txtEmail.setBounds(115, 130, 180, 20);

        e.add(chkEmail);
        emailParts.add(lblFName);
        emailParts.add(txtFName);
        emailParts.add(lblLName);
        emailParts.add(txtLName);
        emailParts.add(lblEmail);
        emailParts.add(txtEmail);

        toggleEmailComponents(emailing, emailParts);

        e.add(emailParts);
        e.add(lblDisabled);
    }

    //toggle email components
    private void toggleEmailComponents(boolean enabled, JPanel p) {
        p.setVisible(enabled);
        lblDisabled.setVisible(!enabled);
    }

    //add daysLeft boundary setting components
    private void buildDaysLeftPanel(JPanel dl) {
        sldUpper = new JSlider(10, 50,upper);
        sldUpper.setBounds(90, 80, 225, 20);
        sldUpper.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                lblUpper.setText("Days Left turns yellow at: " + sldUpper.getValue() + " days");
            }
        });
        sldUpper.setBackground(backgroundColor);

        lblUpper = new JLabel("Days Left turns yellow at: " + sldUpper.getValue() + " days");
        lblUpper.setBounds(100, 60, 220, 20);
        lblUpper.setBackground(backgroundColor);
        lblUpper.setForeground(textColor);

        sldLower = new JSlider(2, 20,lower);
        sldLower.setBounds(90, 140, 225, 20);
        sldLower.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                lblLower.setText("Days Left turns red at: " + sldLower.getValue() + " day(s)");
            }
        });
        sldLower.setBackground(backgroundColor);

        lblLower = new JLabel("Days Left turns red at: " + sldLower.getValue() + " day(s)");
        lblLower.setBounds(100, 120, 220, 20);
        lblLower.setBackground(backgroundColor);
        lblLower.setForeground(textColor);

        JButton btnDefault = new JButton("Default Values");
        btnDefault.setBounds(140, 180, 120, 30);
        btnDefault.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sldUpper.setValue(20);
                sldLower.setValue(10);
            }
        });
        btnDefault.setToolTipText("Reset sliders to their default values");

        dl.add(sldUpper);
        dl.add(lblUpper);
        dl.add(sldLower);
        dl.add(lblLower);
        dl.add(btnDefault);
    }

    //Add save and close buttons
    private void buildButtons(JPanel b) {
        btnSave = new JButton(icSave);
        btnSave.setBorder(null);
        btnSave.setRolloverIcon(icSaveHL);
        btnSave.setBounds(345, getHeight() - 25, 20, 20);
        btnSave.addActionListener(new onClickSave());
        btnSave.setToolTipText("Save changes to settings");

        btnClose = new JButton(icClose);
        btnClose.setBorder(null);
        btnClose.setRolloverIcon(icCloseHL);
        btnClose.setBounds(btnSave.getX() + 30, btnSave.getY(), 20, 20);
        btnClose.addActionListener(new onClickClose());
        btnClose.setToolTipText("Cancel changes to settings");

        b.add(btnSave);
        b.add(btnClose);
    }

    //Update sample colors parts
    private void updateSampleColors() {
        lblSample.setBackground(col.get(cmboBGCol.getSelectedIndex()));
        lblSample.setForeground(col.get(cmboTextCol.getSelectedIndex()));
        lblSample.setBorder(BorderFactory.createLineBorder(col.get(cmboBCol.getSelectedIndex()), 2));
    }

    //Populate colors list
    private void populateColors() {
        /*
         new colors here
         */
        col.add(new Color(0, 0, 0)); //Black
        col.add(new Color(255, 255, 255)); //White
        col.add(new Color(110, 110, 110)); //Gray
        col.add(new Color(180, 180, 180)); //Light Gray
        col.add(new Color(255, 0, 0)); //Red
        col.add(new Color(255, 255, 0)); //Yellow
        col.add(new Color(255, 130, 0)); //Orange
        col.add(new Color(0, 0, 255)); //Blue
        col.add(new Color(0, 150, 255)); //Light Blue
        col.add(new Color(0, 150, 0)); //Green
        col.add(new Color(0, 255, 0)); //Light Green
    }

    //Save all changes to the settings. They will be applied immediately!
    //Save to file, , then load file from handler????
    private class onClickSave implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if ((txtFName.getText().equals("") || txtLName.getText().equals("")
                    || txtEmail.getText().equals("")) && chkEmail.isSelected()) {
                PopupMessage pm = new PopupMessage((int) getX() + 30, (int) getY() + 115,
                        "Email fields cannot be blank.");
            }
            else {
                try {
                    Properties prop = new Properties();
                    OutputStream out = new FileOutputStream("C:\\Remindr\\RemindrSettings.properties");

                    //Save color layout
                    prop.setProperty("bgCol", Integer.toString(col.get(cmboBGCol.getSelectedIndex()).getRGB()));
                    prop.setProperty("bdrCol", Integer.toString(col.get(cmboBCol.getSelectedIndex()).getRGB()));
                    prop.setProperty("txtCol", Integer.toString(col.get(cmboTextCol.getSelectedIndex()).getRGB()));

                    //Save email settings
                    prop.setProperty("email", String.valueOf(chkEmail.isSelected()));
                    prop.setProperty("fname", txtFName.getText());
                    prop.setProperty("lname", txtLName.getText());
                    prop.setProperty("addr", txtEmail.getText());

                    //Save days left settings
                    prop.setProperty("low", String.valueOf(sldLower.getValue()));
                    prop.setProperty("upper", String.valueOf(sldUpper.getValue()));

                    //Save notification layout settings
                    if (radLayoutNorm.isSelected()) {
                        prop.setProperty("layout", "normal");
                    }
                    else {
                        prop.setProperty("layout", "small");
                    }

                    prop.store(out, null);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "There was a problem saving changes." + ex);
                }
                COUNT = 0;
                
                //Reset program to apply changes immediately
                handler.reset();
                handler = new NotificationHandler();
                dispose();
            }
        }
    }

    //Close main window, while program continues to run (maintain notes)
    private class onClickClose implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            COUNT = 0;
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
            emailing = Boolean.parseBoolean(prop.getProperty("email"));
            firstName = prop.getProperty("fname");
            lastName = prop.getProperty("lname");
            address = prop.getProperty("addr");
            lower = Integer.parseInt(prop.getProperty("low"));
            upper = Integer.parseInt(prop.getProperty("upper"));
            layoutState = prop.getProperty("layout");                    
        } catch (Exception ex) {
            backgroundColor = Color.LIGHT_GRAY;
            borderColor = Color.BLACK;
            textColor = Color.BLACK;
            emailing = false;
            firstName = "";
            lastName = "";
            address = "";
            lower = 10;
            upper = 20;
            layoutState = "normal";
            
        }
    }
}
