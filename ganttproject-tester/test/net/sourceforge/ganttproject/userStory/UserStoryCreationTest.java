package net.sourceforge.ganttproject.userStory;

import junit.framework.TestCase;
import net.sourceforge.ganttproject.task.CustomColumnsManager;

public class UserStoryCreationTest extends TestCase {

    public void createStory() {
        UserStoryManager mgr = new UserStoryManager(new CustomColumnsManager());
        UserStory us = mgr.newUserStory();
        us.setName("1st User Story");
        us.setStory("Description of 1st User Story");
        assertTrue(mgr.getUserStories().contains(us));
    }
}
