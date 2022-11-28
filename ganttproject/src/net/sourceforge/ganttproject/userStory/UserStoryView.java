package net.sourceforge.ganttproject.userStory;

//Added @Catarina
public interface UserStoryView {

    void userStoryAdded(UserStoryEvent event);

    void userStoryRemoved(UserStoryEvent event);

    void userStoryChanged(UserStoryEvent event);

    //void userStoryAssignedTasksChanged(UserStoryEvent event);

    void userStoryAssignmentsChanged(UserStoryEvent e);
}
