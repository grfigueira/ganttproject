package net.sourceforge.ganttproject.userStory;

import biz.ganttproject.core.time.GanttCalendar;
import net.sourceforge.ganttproject.CustomPropertyManager;
import net.sourceforge.ganttproject.undo.GPUndoManager;
import java.io.OutputStream;
import java.util.*;

//Added @Catarina
public class UserStoryManager {

    public abstract static class UserStoryBuilder {
        String myName;
        Integer myID;
        String myStory;

        public UserStoryManager.UserStoryBuilder withName(String name) {
            myName = name;
            return this;
        }

        public UserStoryManager.UserStoryBuilder withID(String id) {
            myID = Integer.valueOf(id);
            return this;
        }

        public UserStoryManager.UserStoryBuilder withStory(String story) {
            myStory = story;
            return this;
        }

        public abstract UserStory build();
    }

    private List<UserStoryView> myViews = new ArrayList<UserStoryView>();

    private List<UserStory> userStories = new ArrayList<UserStory>();

    private int nextFreeId = 0;

    private final CustomPropertyManager myCustomPropertyManager;

    public UserStoryManager(CustomPropertyManager customPropertyManager) {
        myCustomPropertyManager = customPropertyManager;
    }

    public UserStory newUserStory() {
        return new UserStory(this);
    }


    public UserStoryManager.UserStoryBuilder newUserStoryBuilder() {
        return new UserStoryManager.UserStoryBuilder() {

            @Override
            public UserStory build() {
                if (myName == null || myID == null) {
                    return null;
                }
                UserStory result = new UserStory(myName, myID, UserStoryManager.this);
                result.setStory(myStory);
                add(result);
                return result;
            }

        };
    }
    public UserStory create(String name, int i) {
        UserStory us = new UserStory(name, i, this);
        add(us);
        return us;
    }

    public void add(UserStory userStory) {
        if (userStory.getId() == -1) {
            userStory.setId(nextFreeId);
        }
        if (userStory.getId() >= nextFreeId) {
            nextFreeId = userStory.getId() + 1;
        }
        userStories.add(userStory);
        fireResourceAdded(userStory);
    }

    public UserStory getById(int id) {
        // Linear search is not really efficient, but we do not have so many
        // resources !?
        UserStory pr = null;
        for (int i = 0; i < userStories.size(); i++)
            if (userStories.get(i).getId() == id) {
                pr = userStories.get(i);
                break;
            }
        return pr;
    }

    public List<UserStory> getUserStories() {
        return userStories;
    }

    public UserStory[] getUserStoriesArray() {
        return userStories.toArray(new UserStory[userStories.size()]);
    }

    public void remove(UserStory userStory) {
        fireResourcesRemoved(new UserStory[] { userStory });
        userStory.removeAllTasks();
        userStories.remove(userStory);
    }

    public void remove(UserStory userStory, GPUndoManager myUndoManager) {
        final UserStory us = userStory;
        myUndoManager.undoableEdit("Delete User Story OK", new Runnable() {
            @Override
            public void run() {
                fireResourcesRemoved(new UserStory[] { us });
                userStories.remove(us);
            }
        });
    }

    public void save(OutputStream target) {
    }

    public void clear() {
        fireCleanup();
        userStories.clear();
    }

    public void addView(UserStoryView view) {
        myViews.add(view);
    }

    private void fireResourceAdded(UserStory userStory) {
        UserStoryEvent e = new UserStoryEvent(this, userStory);
        for (Iterator<UserStoryView> i = myViews.iterator(); i.hasNext();) {
            UserStoryView nextView = i.next();
            nextView.userStoryAdded(e);
        }
    }

    void fireResourceChanged(UserStory userStory) {
        UserStoryEvent e = new UserStoryEvent(this, userStory);
        for (Iterator<UserStoryView> i = myViews.iterator(); i.hasNext();) {
            UserStoryView nextView = i.next();
            nextView.userStoryChanged(e);
        }
    }

    private void fireResourcesRemoved(UserStory[] userStories) {
        UserStoryEvent e = new UserStoryEvent(this, userStories);
        for (int i = 0; i < myViews.size(); i++) {
            UserStoryView nextView = myViews.get(i);
            nextView.userStoryRemoved(e);
        }
    }

    public void fireAssignmentsChanged(UserStory userStory) {
        UserStoryEvent e = new UserStoryEvent(this, userStory);
        for (Iterator<UserStoryView> i = myViews.iterator(); i.hasNext();) {
            UserStoryView nextView = i.next();
            nextView.userStoryAssignmentsChanged(e);
        }
    }

    private void fireCleanup() {
        fireResourcesRemoved(userStories.toArray(new UserStory[userStories.size()]));
    }


    /** Move up the resource number index */
    public void up(UserStory us) {
        int index = userStories.indexOf(us);
        assert index >= 0;
        userStories.remove(index);
        userStories.add(index - 1, us);
        fireResourceChanged(us);
    }


    /** Move down the resource number index */
    public void down(UserStory us) {
        int index = userStories.indexOf(us);
        assert index >= 0;
        userStories.remove(index);
        userStories.add(index + 1, us);
        fireResourceChanged(us);

    }

    /*
    TODO - there was here a importData function that I don't know if it was important... We'll check and
    if it is, consult HumanResourceManager.java
     */

    public CustomPropertyManager getCustomPropertyManager() {
        return myCustomPropertyManager;
    }

    static String getValueAsString(Object value) {
        final String result;
        if (value != null) {
            if (value instanceof GanttCalendar) {
                result = ((GanttCalendar) value).toXMLString();
            } else {
                result = String.valueOf(value);
            }
        } else {
            result = null;
        }
        return result;
    }
}
