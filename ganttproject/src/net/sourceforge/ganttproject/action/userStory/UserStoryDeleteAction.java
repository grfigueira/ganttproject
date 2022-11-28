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

import net.sourceforge.ganttproject.GanttProject;
import net.sourceforge.ganttproject.action.userStory.UserStoryAction;
import net.sourceforge.ganttproject.gui.UIFacade;
import net.sourceforge.ganttproject.gui.UIUtil;
import net.sourceforge.ganttproject.resource.HumanResource;
import net.sourceforge.ganttproject.resource.HumanResourceManager;
import net.sourceforge.ganttproject.resource.ResourceContext;
import net.sourceforge.ganttproject.userStory.UserStory;
import net.sourceforge.ganttproject.userStory.UserStoryContext;
import net.sourceforge.ganttproject.userStory.UserStoryManager;

import java.awt.event.ActionEvent;

/**
 * Action for deleting resources
 */
public class UserStoryDeleteAction extends UserStoryAction {
    private final UIFacade myUIFacade;

    private final GanttProject myProject;

    public UserStoryDeleteAction(UserStoryManager hrManager, UserStoryContext context, GanttProject project,
                                 UIFacade uiFacade) {
        this(hrManager, context, project, uiFacade, IconSize.TOOLBAR_SMALL);
    }

    private UserStoryDeleteAction(UserStoryManager hrManager, UserStoryContext context, GanttProject project,
                                 UIFacade uiFacade, IconSize size) {
        super("userStory.delete", hrManager, context, size);
        myUIFacade = uiFacade;
        myProject = project;
        setEnabled(hasResources());
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        final UserStory[] selectedUserStories = getSelection();
        if (selectedUserStories.length > 0) {
            myUIFacade.getUndoManager().undoableEdit(getLocalizedDescription(), new Runnable() {
                @Override
                public void run() {
                    deleteUserStories(selectedUserStories);
                    myUIFacade.refresh();
                }
            });
        }
    }

    private void deleteUserStories(UserStory[] userStories) {
        for (UserStory userStory : userStories) {
            userStory.delete();
        }
    }

    @Override
    public UserStoryDeleteAction asToolbarAction() {
        UserStoryDeleteAction result = new UserStoryDeleteAction(getManager(), getContext(), myProject, myUIFacade);
        result.setFontAwesomeLabel(UIUtil.getFontawesomeLabel(result));
        return result;
    }
}
