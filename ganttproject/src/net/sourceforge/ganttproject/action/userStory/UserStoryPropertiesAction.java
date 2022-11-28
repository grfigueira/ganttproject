/*
GanttProject is an opensource project management tool. License: GPL3
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

import net.sourceforge.ganttproject.IGanttProject;
import net.sourceforge.ganttproject.gui.GanttDialogPerson;
import net.sourceforge.ganttproject.gui.GanttDialogUserStory;
import net.sourceforge.ganttproject.gui.UIFacade;
import net.sourceforge.ganttproject.gui.UIUtil;
import net.sourceforge.ganttproject.resource.HumanResource;
import net.sourceforge.ganttproject.resource.ResourceContext;
import net.sourceforge.ganttproject.userStory.UserStory;
import net.sourceforge.ganttproject.userStory.UserStoryContext;
import net.sourceforge.ganttproject.userStory.UserStoryManager;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class UserStoryPropertiesAction extends UserStoryAction {
    private final IGanttProject myProject;
    private final UIFacade myUIFacade;

    public UserStoryPropertiesAction(IGanttProject project, UserStoryContext context, UIFacade uiFacade) {
        this(project, context, uiFacade, IconSize.MENU);
    }

    private UserStoryPropertiesAction(IGanttProject project, UserStoryContext context, UIFacade uiFacade, IconSize size) {
        super("userStory.properties", null, context, size);
        myProject = project;
        myUIFacade = uiFacade;
        setEnabled(hasResources());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (calledFromAppleScreenMenu(e)) {
            return;
        }
        UserStory[] selectedUserStories = getSelection();
        if (selectedUserStories.length > 0) {
            myUIFacade.getResourceTree().stopEditing();
            // TODO Allow to edit multiple resources (instead of [0])
            GanttDialogUserStory dp = new GanttDialogUserStory(myProject.getResourceCustomPropertyManager(), myUIFacade,
                    selectedUserStories[0]);
            dp.setVisible(true);
            if (dp.result()) {
                myProject.setModified(true);
            }
        }
    }

    @Override
    public UserStoryPropertiesAction asToolbarAction() {
        final UserStoryPropertiesAction result = new UserStoryPropertiesAction(myProject, getContext(), myUIFacade);
        result.setFontAwesomeLabel(UIUtil.getFontawesomeLabel(result));
        addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("enabled".equals(evt.getPropertyName())) {
                    result.setEnabled(UserStoryPropertiesAction.this.isEnabled());
                }
            }
        });
        return result;
    }
}
