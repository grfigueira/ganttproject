/*
Copyright 2014 BarD Software s.r.o

This file is part of GanttProject, an opensource project management tool.

GanttProject is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

GanttProject is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with GanttProject.  If not, see <http://www.gnu.org/licenses/>.
*/
package net.sourceforge.ganttproject.userStory;

import java.util.Set;

import net.sourceforge.ganttproject.CustomPropertyDefinition;
import net.sourceforge.ganttproject.ResourceDefaultColumn;

import net.sourceforge.ganttproject.UserStoryDefaultColumn;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;

//Added @Catarina
public abstract class UserStoryTableNode extends DefaultMutableTreeTableNode {
    private final Set<UserStoryDefaultColumn> myColumns;

    protected UserStoryTableNode(Object userObject, Set<UserStoryDefaultColumn> applicableColumns) {
        super(userObject);
        myColumns = applicableColumns;
    }

    public boolean isEditable(UserStoryDefaultColumn column) {
        return myColumns.contains(column) && column.isEditable();
    }

    public abstract void setCustomField(CustomPropertyDefinition def, Object val);
    public abstract Object getCustomField(CustomPropertyDefinition def);

    public abstract Object getStandardField(UserStoryDefaultColumn def);
    public abstract void setStandardField(UserStoryDefaultColumn resourceDefaultColumn, Object value);
}
