/*
GanttProject is an opensource project management tool.
Copyright (C) 2011 GanttProject Team

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 3
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package net.sourceforge.ganttproject.action.userStory;

import net.sourceforge.ganttproject.action.ActionDelegate;
import net.sourceforge.ganttproject.action.ActionStateChangedListener;
import net.sourceforge.ganttproject.action.GPAction;
import net.sourceforge.ganttproject.userStory.UserStory;
import net.sourceforge.ganttproject.userStory.UserStoryContext;
import net.sourceforge.ganttproject.userStory.UserStoryManager;

import java.util.ArrayList;
import java.util.List;

//TODO Add listener for changed resource selection, see TaskActionBase

//Added @Catarina
abstract class UserStoryAction extends GPAction implements ActionDelegate {
    private final UserStoryManager myManager;
    private final List<ActionStateChangedListener> myListeners = new ArrayList<ActionStateChangedListener>();
    private final UserStoryContext myContext;

    public UserStoryAction(String name, UserStoryManager usManager) {
        this(name, usManager, null, IconSize.NO_ICON);
    }

    protected UserStoryAction(String name, UserStoryManager usManager, UserStoryContext context, IconSize size) {
        super(name, size.asString());
        myManager = usManager;
        myContext = context;
    }

    @Override
    public void addStateChangedListener(ActionStateChangedListener l) {
        myListeners.add(l);
    }

    protected UserStoryManager getManager() {
        return myManager;
    }

    protected UserStoryContext getContext() {
        return myContext;
    }

    protected boolean hasResources() {
        UserStory[] selection = myContext.getUserStories();
        return selection != null && selection.length > 0;
    }

    protected UserStory[] getSelection() {
        UserStory[] selection = myContext.getUserStories();
        return selection == null ? new UserStory[0] : selection;
    }

    @Override
    public void setEnabled(boolean newValue) {
        super.setEnabled(newValue);
        for (ActionStateChangedListener l : myListeners) {
            l.actionStateChanged();
        }
    }
}
