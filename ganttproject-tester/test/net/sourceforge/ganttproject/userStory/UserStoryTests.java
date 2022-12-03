package net.sourceforge.ganttproject.userStory;

import net.sourceforge.ganttproject.task.CustomColumnsManager;
import net.sourceforge.ganttproject.task.Task;
import net.sourceforge.ganttproject.test.task.TaskTestCase;

public class UserStoryTests extends TaskTestCase {

    public void testCreateStory() {
        UserStoryManager mgr = new UserStoryManager(new CustomColumnsManager());
        UserStory us = mgr.newUserStory();
        mgr.add(us);
        us.setName("1st User Story");
        us.setStory("Description of 1st User Story");
        assertTrue(mgr.getUserStories().contains(us));
    }

    public void testDeleteStory() {
        UserStoryManager mgr = new UserStoryManager(new CustomColumnsManager());
        UserStory us = mgr.newUserStory();
        mgr.add(us);
        us.setName("1st User Story");
        us.setStory("Description of 1st User Story");
        assertTrue(mgr.getUserStories().contains(us));
        mgr.remove(us);
        assertFalse(mgr.getUserStories().contains(us));
    }

    public void testDeleteStoryAssociatedWithTasks() {
        UserStoryManager mgr = new UserStoryManager(new CustomColumnsManager());
        UserStory us = mgr.newUserStory();
        mgr.add(us);
        us.setName("1st User Story");
        us.setStory("Description of 1st User Story");
        assertTrue(mgr.getUserStories().contains(us));

        Task t1 = createTask();
        t1.setUserStory(us);

        mgr.remove(us);
        assertFalse(mgr.getUserStories().contains(us));
        assertNull(t1.getUserStory());
    }

}
