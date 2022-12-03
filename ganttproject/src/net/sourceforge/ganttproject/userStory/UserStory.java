package net.sourceforge.ganttproject.userStory;


import com.google.common.base.Strings;
import net.sourceforge.ganttproject.CustomProperty;
import net.sourceforge.ganttproject.CustomPropertyDefinition;
import net.sourceforge.ganttproject.CustomPropertyHolder;
import net.sourceforge.ganttproject.CustomPropertyManager;
import net.sourceforge.ganttproject.language.GanttLanguage;
import net.sourceforge.ganttproject.task.CustomColumnsException;
import net.sourceforge.ganttproject.task.CustomColumnsValues;
import net.sourceforge.ganttproject.task.Task;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class UserStory implements CustomPropertyHolder {
    /**
     * Can be turned (temporarily) off to prevent (a flood of) events
     */
    private boolean areEventsEnabled = true;

    private int ID;

    private String name;

    private String story;

    private List<Task> userStoryTasks;

    // Here just to follow the template of the others, in practise, I have no idea what to do with it
    private final CustomColumnsValues myCustomProperties;

    // It'll probably be useful, not sure right now...
    private final UserStoryManager myManager;

    UserStory(UserStoryManager manager) {
        this("", -1, manager);
    }

    public UserStory(String name, int ID, UserStoryManager manager) {
        this.ID = ID;
        this.name = name;
        this.myManager = manager;
        this.myCustomProperties = new CustomColumnsValues(myManager.getCustomPropertyManager());
        this.userStoryTasks = new LinkedList<>();
    }

    private UserStory(UserStory copy) {
        areEventsEnabled = false;
        setId(-1);
        String newName = GanttLanguage.getInstance().formatText("resource.copy.prefix",
                GanttLanguage.getInstance().getText("copy2"), copy.getName());
        setName(newName);
        setStory(copy.getStory());
        myManager = copy.myManager;
        areEventsEnabled = true;
        myCustomProperties = (CustomColumnsValues) copy.myCustomProperties.clone();
        userStoryTasks = copy.getUserStoryTasks();
    }

    //TODO - something equivalent with removeAllAssignments but about the tasks
    private void removeAllTasks() {
        for (Task t: userStoryTasks) {
            t.removeUserStory();
        }
    }

    public void delete() {
        //TODO - here it would need the removeAllAssignments equivalent
        removeAllTasks();
        myManager.remove(this);
    }

    public void setId(int id) {
        if (this.ID == -1) {
            // setting the id is only allowed when id is not assigned
            this.ID = id;
        }
    }

    public int getId() {
        return ID;
    }

    public void setName(String name) {
        this.name = name;
        fireResourceChanged();
    }

    public String getName() {
        return Strings.nullToEmpty(name);
    }

    public void setStory(String story) {
        this.story = story;
        fireResourceChanged();
    }

    public String getStory() {
        return story;
    }

    public List<Task> getUserStoryTasks() {
        return this.userStoryTasks;
    }

    Object getCustomField(CustomPropertyDefinition def) {
        return myCustomProperties.getValue(def);
    }

    public void setCustomField(CustomPropertyDefinition def, Object value) {
        try {
            myCustomProperties.setValue(def, value);
            fireResourceChanged();
        } catch (CustomColumnsException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //TODO - create assignments and other assignment related operations

    public UserStory unpluggedClone() {
        return new UserStory(this);
    }

    private void fireResourceChanged() {
        if (areEventsEnabled) {
            myManager.fireResourceChanged(this);
        }
    }

    private void fireAssignmentsChanged() {
        if (areEventsEnabled) {
            myManager.fireAssignmentsChanged(this);
        }
    }

    @Override
    public List<CustomProperty> getCustomProperties() {
        return myCustomProperties.getCustomProperties();
    }

    @Override
    public CustomProperty addCustomProperty(CustomPropertyDefinition definition, String valueAsString) {
        final CustomPropertyDefinition stubDefinition = CustomPropertyManager.PropertyTypeEncoder.decodeTypeAndDefaultValue(
                definition.getTypeAsString(), valueAsString);
        setCustomField(definition, stubDefinition.getDefaultValue());
        return new UserStory.CustomPropertyImpl(definition, stubDefinition.getDefaultValue());
    }

    private static class CustomPropertyImpl implements CustomProperty {
        private CustomPropertyDefinition myDefinition;
        private Object myValue;

        CustomPropertyImpl(CustomPropertyDefinition definition, Object value) {
            myDefinition = definition;
            myValue = value;
        }

        @Override
        public CustomPropertyDefinition getDefinition() {
            return myDefinition;
        }

        @Override
        public Object getValue() {
            return myValue;
        }

        @Override
        public String getValueAsString() {
            return UserStoryManager.getValueAsString(myValue);
        }
    }

    private void fireAssignmentChanged() {
        fireAssignmentsChanged();

    }



}
