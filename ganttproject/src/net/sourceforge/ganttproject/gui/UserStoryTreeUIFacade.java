package net.sourceforge.ganttproject.gui;

import net.sourceforge.ganttproject.userStory.UserStory;

import javax.swing.*;

public interface UserStoryTreeUIFacade extends TreeUiFacade<UserStory> {
    AbstractAction getMoveUpAction();

    AbstractAction getMoveDownAction();
}
