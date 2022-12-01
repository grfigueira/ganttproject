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
package net.sourceforge.ganttproject.userStory;

import java.util.EnumSet;
import java.util.Set;

import net.sourceforge.ganttproject.CustomPropertyDefinition;
import net.sourceforge.ganttproject.TreeUtil;
import net.sourceforge.ganttproject.UserStoryDefaultColumn;

//Added @Catarina
public class UserStoryNode extends UserStoryTableNode {
    private static final Set<UserStoryDefaultColumn> ourApplicableColumns = EnumSet.of(UserStoryDefaultColumn.NAME,UserStoryDefaultColumn.STORY);

    private static final long serialVersionUID = 3834033541318392117L;

    private final UserStory userStory;

    public UserStoryNode(UserStory us) {
        super(us, ourApplicableColumns);
        assert us != null;
        userStory = us;
    }

    public void setName(String name) {
        userStory.setName(name);
    }

    public String getName() {
        return userStory.getName();
    }

    public void setStory(String story) {
        userStory.setStory(story);
    }

    public String getStory() { return userStory.getStory(); }

    @Override
    public Object getStandardField(UserStoryDefaultColumn def) {
        switch (def) {
            case NAME: return getName();
            case STORY: return getStory();
            default: return "";
        }
    }

    @Override
    public void setStandardField(UserStoryDefaultColumn def, Object value) {
        switch (def) {
            case NAME:
                setName(value.toString());
                return;
            case STORY:
                setStory(value.toString());
                return;
            }
    }

    /** @return the value of a custom field referenced by its title */
    @Override
    public Object getCustomField(CustomPropertyDefinition def) {
        return userStory.getCustomField(def);
    }

    /** sets the new value to the custom field referenced by its title */
    @Override
    public void setCustomField(CustomPropertyDefinition def, Object val) {
        userStory.setCustomField(def, val);
    }

    @Override
    public String toString() {
        if (userStory != null) {
            return userStory.getName();
        }
        return "-";
    }

    public UserStory getUserStory() {
        return userStory;
    }

    @Override
    public boolean equals(Object obj) {
        boolean res = false;
        if (this == obj) {
            return true;
        }
        if (obj instanceof UserStoryNode) {
            UserStoryNode usn = (UserStoryNode) obj;
            res = usn.getUserObject() != null && usn.getUserObject().equals(this.getUserObject());
        }
        return res;
    }

    public void removeAllChildren() {
        TreeUtil.removeAllChildren(this);
    }
}
