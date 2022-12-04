/*
Copyright 2003-2012 Dmitry Barashev, GanttProject Team

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
package net.sourceforge.ganttproject.parser;

import net.sourceforge.ganttproject.userStory.UserStory;
import net.sourceforge.ganttproject.userStory.UserStoryManager;
import org.xml.sax.Attributes;

import java.awt.*;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

public class UserStoryTagHandler extends AbstractTagHandler implements ParsingListener {
    private final ParsingContext myContext;
    private final UserStoryManager myManager;
    //private final UserStoryTreeUIFacade myTreeFacade;
    //private final Map<Integer, Boolean> myTaskIdToExpansionState = Maps.newHashMap();
    public UserStoryTagHandler(UserStoryManager mgr, ParsingContext context) {
        super("userStory");
        myManager = mgr;
        myContext = context;
        //myTreeFacade = treeFacade;
    }

    @Override
    protected boolean onStartElement(Attributes attrs) {
        loadUserStory(attrs);
        return true;
    }



    private void loadUserStory(Attributes attrs) {
        String userStoryIdAsString = attrs.getValue("id");
        int userStoryId;
        try {
            userStoryId = Integer.parseInt(userStoryIdAsString);
        } catch (NumberFormatException e) {
            throw new RuntimeException(
                    "Failed to parse the value '" + userStoryIdAsString + "' of attribute 'id' of tag <task>", e);
        }
        //TaskBuilder builder = getManager().newTaskBuilder().withId(userStoryId);
        UserStory us = getManager().newUserStory();
        us.setId(userStoryId);

        String userStoryName = attrs.getValue("name");
        if (userStoryName != null) {
            us.setName(userStoryName);
        }

        String userStoryStory = attrs.getValue("story");
        if (userStoryStory != null) {
            us.setStory(userStoryStory);
        }

        myManager.add(us);
    }

    private UserStoryManager getManager() {
        return myManager;
    }

    @Override
    public void parsingStarted() {
    }

    @Override
    public void parsingFinished() {
    }
}
