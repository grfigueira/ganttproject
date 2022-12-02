package net.sourceforge.ganttproject;

import biz.ganttproject.core.option.*;
import net.sourceforge.ganttproject.action.CancelAction;
import net.sourceforge.ganttproject.action.OkAction;
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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class UserStoriesDialog extends JFrame {

    private static final GanttLanguage language = GanttLanguage.getInstance();

    private JTabbedPane tabbedPane;

    private final StringOption myNameField = new DefaultStringOption("name");
    private final StringOption myStoryField = new DefaultStringOption("Story");
    private final GPOptionGroup myGroup;

    private final UIFacade myUIFacade;

    private JTextArea myNewStory;
    private JTextField myNewName;

    private JComboBox<UserStory> myPrivateJCBox;

    private boolean refresh;


    private final UserStoryManager myUserStoryManager;

    public UserStoriesDialog(IGanttProject project, UIFacade uiFacade) {
        myUserStoryManager = project.getUserStoryManager();
        myUIFacade = uiFacade;

        myGroup = new GPOptionGroup("description", new GPOption[] { myNameField, myStoryField });
        myGroup.setTitled(true);
        refresh = false;
    }

    public boolean refresh() {
        return refresh;
    }

    public void setVisible(boolean isVisible) {
        if (isVisible) {
            refresh = false;
            Component contentPane = getComponent2();
            OkAction okAction = new OkAction("Apply Changes") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    myGroup.commit();
                    //okButtonActionPerformed();
                    refresh = true;
                }
            };
            CancelAction cancelAction = new CancelAction("Close") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    myGroup.rollback();
                    //change = false;
                }
            };
            myUIFacade.createDialog(contentPane, new Action[] { okAction, cancelAction }, "User Stories").show();
        }
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

        tabbedPane.addTab("New User Story", new ImageIcon(getClass().getResource("/icons/new_16.gif")),
                constructAddStoryPanel());
        tabbedPane.addTab("All User Stories", new ImageIcon(getClass().getResource("/icons/properties_16.gif")),
                constructAllStoriesPanelScrollable());
        tabbedPane.addTab("Edit User Story", new ImageIcon(getClass().getResource("/icons/note_16.gif")),
                constructEditAndDeletePanel());

        return tabbedPane;
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
        myNewName = new JTextField(30);
        namePanel.add(myNewName);

        JPanel storyPanel = new JPanel();
        storyPanel.add(new Label("Story "));
        myNewStory = new JTextArea(5, 30);
        myNewStory.setLineWrap(true);
        myNewStory.setWrapStyleWord(true);
        myNewStory.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.gray),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)));

        storyPanel.add(myNewStory);

        JButton saveBtn = new JButton("Save Story");

        saveBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { applyChanges();}
        });

        main.add(namePanel);
        main.add(storyPanel);
        main.add(saveBtn);

        return main;
    }

    private JPanel constructEditAndDeletePanel() {
        // Creation of the main/container panel and definition of the components' orientation (vertical)
        final JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));

        // Creation of the interior panels
        final JPanel dropdownPanel = new JPanel();
        final JPanel userStoryProperties = new JPanel();
        final JPanel namePanel = new JPanel();
        final JPanel storyPanel = new JPanel();
        final JPanel buttonsContainer = new JPanel();

        // Creation of the text fields for input
        final JTextField myChangedName = new JTextField(30);
        final JTextArea myChangedStory = new JTextArea(5, 30);

        // Components properties definition
        userStoryProperties.setLayout(new BoxLayout(userStoryProperties, BoxLayout.Y_AXIS));
        myChangedStory.setLineWrap(true);
        myChangedStory.setWrapStyleWord(true);
        myChangedStory.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.gray),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)));

        // Creation of the dropdown for choosing the user story
        final JComboBox[] userStoriesDropDown = {getUSDropdown()};

        // Temporary variables to store information about the selected user story
        final UserStory[] us = {null};
        final int[] index = {-2};

        // Buttons creation
        JButton getBtn = new JButton("Select Story");
        JButton saveBtn = new JButton("Save Changes");
        JButton deleteBtn = new JButton("Delete Story");

        // Adding components to the corresponding panels
        dropdownPanel.add(userStoriesDropDown[0]);
        dropdownPanel.add(getBtn);
        namePanel.add(new Label("Name"));
        namePanel.add(myChangedName);
        storyPanel.add(new Label("Story "));
        storyPanel.add(myChangedStory);
        userStoryProperties.add(namePanel);
        userStoryProperties.add(storyPanel);
        buttonsContainer.add(saveBtn);
        buttonsContainer.add(deleteBtn);
        main.add(dropdownPanel);
        main.add(userStoryProperties);
        main.add(buttonsContainer);


        // Buttons action definition
        // Select story button
        getBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int p = userStoriesDropDown[0].getSelectedIndex();
                us[0] = myPrivateJCBox.getItemAt(p);
                index[0] = p;
                myChangedName.setText(us[0].getName());
                myChangedStory.setText(us[0].getStory());
            }
        });

        // Save button
        saveBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (us[0] != null) {
                    us[0].setName(myChangedName.getText());
                    us[0].setStory(myChangedStory.getText());
                }
            }
        });

        // Delete Button
        deleteBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (us[0] != null && index[0] != -2) {
                    myPrivateJCBox.remove(index[0]);
                    userStoriesDropDown[0].remove(index[0]);
                    myUserStoryManager.remove(us[0]);
                    myChangedName.setText(null);
                    myChangedStory.setText(null);
                    userStoriesDropDown[0] = getUSDropdown();
                }
            }
        });
        return main;
    }



    private JComboBox getUSDropdown() {
        final JComboBox userStoriesDropDown = new JComboBox();
        myPrivateJCBox = new JComboBox();

        for (UserStory us : myUserStoryManager.getUserStories()) {
            userStoriesDropDown.addItem(us.getName());
            myPrivateJCBox.addItem(us);
        }
        userStoriesDropDown.setEditable(false);
        userStoriesDropDown.setPrototypeDisplayValue("No user story selected...");
        myPrivateJCBox.setPrototypeDisplayValue(null);
        return userStoriesDropDown;
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
