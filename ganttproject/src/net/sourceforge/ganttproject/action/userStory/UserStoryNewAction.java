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

import net.sourceforge.ganttproject.action.GPAction;
import net.sourceforge.ganttproject.gui.GanttDialogPerson;
import net.sourceforge.ganttproject.gui.GanttDialogUserStory;
import net.sourceforge.ganttproject.gui.UIFacade;
import net.sourceforge.ganttproject.gui.UIUtil;
import net.sourceforge.ganttproject.resource.HumanResource;
import net.sourceforge.ganttproject.resource.HumanResourceManager;
import net.sourceforge.ganttproject.roles.RoleManager;
import net.sourceforge.ganttproject.userStory.UserStory;
import net.sourceforge.ganttproject.userStory.UserStoryManager;

import java.awt.event.ActionEvent;

//Added @Catarina
public class UserStoryNewAction extends UserStoryAction {
    private final UIFacade myUIFacade;

    public UserStoryNewAction(UserStoryManager usManager, UIFacade uiFacade) {
        super("resource.new", usManager);
        myUIFacade = uiFacade;
    }

    private UserStoryNewAction(UserStoryManager hrManager, UIFacade uiFacade, IconSize size) {
        super("resource.new", hrManager, null, size);
        myUIFacade = uiFacade;
    }

    @Override
    public GPAction withIcon(IconSize size) {
        return new UserStoryNewAction(getManager(), myUIFacade, size);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (calledFromAppleScreenMenu(event)) {
            return;
        }
        final UserStory userStory = getManager().newUserStory();
        GanttDialogUserStory dp = new GanttDialogUserStory(getManager().getCustomPropertyManager(), myUIFacade, userStory);
        dp.setVisible(true);
        if (dp.result()) {
            myUIFacade.getUndoManager().undoableEdit(getLocalizedDescription(), new Runnable() {
                @Override
                public void run() {
                    getManager().add(userStory);
                    myUIFacade.getUserStoryTree().setSelected(userStory, true); //TODO - resource tree>>userStoryTree
                }
            });
        }
    }

    @Override
    public void updateAction() {
        super.updateAction();
    }

    @Override
    public UserStoryNewAction asToolbarAction() {
        UserStoryNewAction result = new UserStoryNewAction(getManager(), myUIFacade);
        result.setFontAwesomeLabel(UIUtil.getFontawesomeLabel(result));
        return result;
    }
}
