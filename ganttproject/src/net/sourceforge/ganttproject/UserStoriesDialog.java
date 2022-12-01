package net.sourceforge.ganttproject;

import biz.ganttproject.core.option.*;
import net.sourceforge.ganttproject.action.CancelAction;
import net.sourceforge.ganttproject.gui.UIFacade;
import net.sourceforge.ganttproject.gui.options.OptionsPageBuilder;
import net.sourceforge.ganttproject.language.GanttLanguage;
import net.sourceforge.ganttproject.task.Task;
import net.sourceforge.ganttproject.userStory.UserStory;
import net.sourceforge.ganttproject.userStory.UserStoryManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class UserStoriesDialog extends JPanel {

    private static final GanttLanguage language = GanttLanguage.getInstance();

    private JTabbedPane tabbedPane;

    private final StringOption myNameField = new DefaultStringOption("name");
    private final StringOption myStoryField = new DefaultStringOption("Story");
    private final GPOptionGroup myGroup;

    private final UIFacade myUIFacade;

    private JTextArea myNewStory;
    private JTextField myNewName;

    private JComboBox<UserStory> myPrivateJCBox;

    JFrame myFrame;


    private final UserStoryManager myUserStoryManager;

    public UserStoriesDialog(JFrame frame, IGanttProject project, UIFacade uiFacade) {
        //frame.setSize(700, 400);
        myUserStoryManager = project.getUserStoryManager();
        myUIFacade = uiFacade;
        myFrame = frame;

        myGroup = new GPOptionGroup("description", new GPOption[] { myNameField, myStoryField });
        myGroup.setTitled(true);
    }

    public void setVisible(boolean isVisible) {
        if (isVisible) {
            loadFields();
            Component contentPane = getComponent2();
            CancelAction cancelAction = new CancelAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    myGroup.rollback();
                    //change = false;
                }
            };
            myUIFacade.createDialog(contentPane, new Action[] { }, "User Stories").show();
        }
    }

    private void loadFields() {
        myNameField.setValue("                                                                                                                ");
        myStoryField.setValue("");
    }


    private Component getComponent2() {
        OptionsPageBuilder builder = new OptionsPageBuilder();
        OptionsPageBuilder.I18N i18n = new OptionsPageBuilder.I18N() {
            @Override
            public String getOptionLabel(GPOptionGroup group, GPOption<?> option) {
                return getValue(option.getID());
            }
        };
        builder.setI18N(i18n);

        final JComponent mainPage = builder.buildPlanePage(new GPOptionGroup[] { myGroup });
        Dimension d = new Dimension(); d.setSize(700, 700);
        mainPage.setMinimumSize(d);

        mainPage.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        tabbedPane = new JTabbedPane();

        /*tabbedPane.addTab("New User Story", new ImageIcon(getClass().getResource("/icons/properties_16.gif")),
                mainPage);*/
        tabbedPane.addTab("New User Story", new ImageIcon(getClass().getResource("/icons/new_16.gif")),
                constructAddStoryPanel());
        /*tabbedPane.addTab("All User Stories", new ImageIcon(getClass().getResource("/icons/holidays_16.gif")),
                constructAllStoriesPanel());*/
        tabbedPane.addTab("All User Stories", new ImageIcon(getClass().getResource("/icons/properties_16.gif")),
                constructAllStoriesPanelScrollable());
        tabbedPane.addTab("Edit User Story", new ImageIcon(getClass().getResource("/icons/properties_16.gif")),
                constructEditAndDeletePanel());







        return tabbedPane;
    }

    // Working panel to show all stories (non-scrollable)
    private JPanel constructAllStoriesPanel() {
        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));

        //main.add(new JSeparator(SwingConstants.HORIZONTAL));

        if (myUserStoryManager.getUserStoriesArray().length == 0) {
            //main.add(new JSeparator(SwingConstants.HORIZONTAL));
            JPanel empty = new JPanel();
            empty.add(new JLabel("No User Stories yet"));
            main.add(empty);
        } else {
            JPanel titlePanel = new JPanel();
            titlePanel.add(new JLabel("User Stories List"));
            main.add(titlePanel);
        }


        for (UserStory us : myUserStoryManager.getUserStories()) {
            //main.add(new JSeparator(SwingConstants.HORIZONTAL));
            JPanel usPanel = new JPanel();
            usPanel.setLayout(new BoxLayout(usPanel, BoxLayout.Y_AXIS));
            //usPanel.setAlignmentX(LEFT_ALIGNMENT);

            // A panel for the user story
            /*JPanel storyPanel = new JPanel();
            storyPanel.add(new JLabel(us.getStory()));
            usPanel.add(storyPanel);*/

            // Wrappable story area
            JTextArea storyArea = new JTextArea(1, 30);
            storyArea.setText(us.getStory());
            storyArea.setEditable(false);
            storyArea.setLineWrap(true);
            storyArea.setWrapStyleWord(true);

            usPanel.add(storyArea);

            usPanel.setBorder(BorderFactory.createTitledBorder(us.getName()));

            main.add(usPanel);
        }

        return main;
    }

    private JScrollPane constructAllStoriesPanelScrollable() {

        // Main panel where all components will be added
        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));

        if (myUserStoryManager.getUserStoriesArray().length == 0) {
            JPanel empty = new JPanel();
            empty.add(new JLabel("No User Stories yet"));
            main.add(empty);
        } else {
            JPanel titlePanel = new JPanel();
            titlePanel.add(new JLabel("User Stories List"));
            main.add(titlePanel);
        }

        for (UserStory us : myUserStoryManager.getUserStories()) {

            // Panel for each user story
            JPanel usPanel = new JPanel();
            usPanel.setLayout(new BoxLayout(usPanel, BoxLayout.Y_AXIS)); // Sets the orientation

            // Area where the story will be presented
            JTextArea storyArea = new JTextArea(1, 30);
            storyArea.setText(us.getStory());
            storyArea.setEditable(false);
            storyArea.setLineWrap(true);
            storyArea.setWrapStyleWord(true);

            usPanel.add(storyArea);
            // Border of each user story with its name
            usPanel.setBorder(BorderFactory.createTitledBorder(us.getName()));
            main.add(usPanel);
        }

        // Make the panel scrollable, by setting the size and the scroll bars properties
        JScrollPane scrollable = new JScrollPane(main);
        scrollable.setSize(500, 250);
        scrollable.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollable.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        return scrollable;
    }

    private JPanel constructAddStoryPanel() {
        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));

        JPanel infoPanel = new JPanel();
        infoPanel.add(new Label("Insert the information to create a new User Story"));
        main.add(infoPanel);

        JPanel namePanel = new JPanel();
        namePanel.add(new Label("Name"));
        //JTextField myNewName = new JTextField(30);
        myNewName = new JTextField(30);
        namePanel.add(myNewName);

        JPanel storyPanel = new JPanel();
        storyPanel.add(new Label("Story "));
        //JTextArea storyArea = new JTextArea(7, 30);
        myNewStory = new JTextArea(5, 30);
        myNewStory.setLineWrap(true);
        myNewStory.setWrapStyleWord(true);
        myNewStory.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.gray),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)));

        storyPanel.add(myNewStory);

        JButton saveBtn = new JButton("Save");

        saveBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { applyChanges(); }
        });

        main.add(namePanel);
        main.add(storyPanel);
        main.add(saveBtn);

        return main;
    }



    private JPanel constructEditAndDeletePanel() {


        final JTextField myChangedName = new JTextField(30);
        final JTextArea myChangedStory = new JTextArea(5, 30);


        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));

        JPanel dropDownPanel = new JPanel();
        //dropDownPanel.setSize(new Dimension(200, 20));

        final JComboBox userStoriesDropDown = new JComboBox();
        myPrivateJCBox = new JComboBox();
        //userStoriesDropDown.setSize(10, userStoriesDropDown.getPreferredSize().height);

        for (UserStory us : myUserStoryManager.getUserStories()) {
            userStoriesDropDown.addItem(us.getName());
            myPrivateJCBox.addItem(us);
        }
        userStoriesDropDown.setEditable(false);

        dropDownPanel.add(userStoriesDropDown);


        final UserStory[] us = {null};

        JButton getBtn = new JButton("See properties");
        getBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int p = userStoriesDropDown.getSelectedIndex();
                us[0] = myPrivateJCBox.getItemAt(p);
                myChangedName.setText(us[0].getName());
                myChangedStory.setText(us[0].getStory());
            }
        });
        dropDownPanel.add(getBtn);

        //User Story Panel to edit name and story
        JPanel userStoryProperties = new JPanel();
        userStoryProperties.setLayout(new BoxLayout(userStoryProperties, BoxLayout.Y_AXIS));

        /////////////////////////////////
        JPanel namePanel = new JPanel();
        namePanel.add(new Label("Name"));
        namePanel.add(myChangedName);

        JPanel storyPanel = new JPanel();
        storyPanel.add(new Label("Story "));
        myChangedStory.setLineWrap(true);
        myChangedStory.setWrapStyleWord(true);
        myChangedStory.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.gray),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)));

        storyPanel.add(myChangedStory);

        userStoryProperties.add(namePanel); userStoryProperties.add(storyPanel);

        JButton saveBtn = new JButton("Save");
        saveBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (us[0] != null) {
                    us[0].setName(myChangedName.getText());
                    us[0].setStory(myChangedStory.getText());
                }
            }
        });
        userStoryProperties.add(saveBtn);
        /////////////////////////////////



        main.add(dropDownPanel);
        main.add(userStoryProperties);
        return main;
    }



    private void okButtonActionPerformed() {
        //applyChanges();
    }


    private void applyChanges() {
        UserStory us = myUserStoryManager.newUserStory();
        myUserStoryManager.add(us);

        // Changes are only applied if the fields are not null
        if (myNewName.getText() != null && myNewStory.getText() != null) {
            us.setName(myNewName.getText().trim());
            us.setStory(myNewStory.getText().trim());
            myNewName.setText(null);
            myNewStory.setText(null);
        }
    }



}
