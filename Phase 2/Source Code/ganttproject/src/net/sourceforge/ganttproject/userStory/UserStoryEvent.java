package net.sourceforge.ganttproject.userStory;

import java.util.EventObject;

//Added @Catarina
public class UserStoryEvent extends EventObject {

    public UserStoryEvent(UserStoryManager mgr, UserStory userStory) {
        super(mgr);
        myManager = mgr;
        myUserStory = userStory;
        myUserStories = new UserStory[] { userStory };
    }

    public UserStoryEvent(UserStoryManager mgr, UserStory[] userStories) {
        super(mgr);
        myManager = mgr;
        myUserStories = userStories;
        myUserStory = userStories.length > 0 ? userStories[0] : null;
    }

    public UserStoryManager getManager() {
        return myManager;
    }

    public UserStory getUserStory() {
        return myUserStory;
    }

    public UserStory[] getUserStories() {
        return myUserStories;
    }

    private UserStory[] myUserStories;

    private UserStoryManager myManager;

    private UserStory myUserStory;
}