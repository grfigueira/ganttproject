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
package net.sourceforge.ganttproject;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;


import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;


import net.sourceforge.ganttproject.resource.HumanResource;

import net.sourceforge.ganttproject.resource.ResourceNode;
import net.sourceforge.ganttproject.resource.ResourceTableNode;
import net.sourceforge.ganttproject.task.ResourceAssignment;
import net.sourceforge.ganttproject.task.Task;
import net.sourceforge.ganttproject.task.TaskManager;
import net.sourceforge.ganttproject.task.event.TaskHierarchyEvent;
import net.sourceforge.ganttproject.task.event.TaskListenerAdapter;
import net.sourceforge.ganttproject.task.event.TaskScheduleEvent;

import net.sourceforge.ganttproject.userStory.UserStory;
import net.sourceforge.ganttproject.userStory.UserStoryManager;
import net.sourceforge.ganttproject.userStory.UserStoryNode;
import net.sourceforge.ganttproject.userStory.UserStoryTableNode;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

//Added @Catarina
public class UserStoryTreeTableModel extends DefaultTreeTableModel {
    private static final int STANDARD_COLUMN_COUNT = ResourceDefaultColumn.values().length;
    /** Column indexer */
    private static int index = -1;

    private DefaultMutableTreeTableNode root = null;

    private final UserStoryManager myUserStoryManager;

    private final TaskManager myTaskManager;

    private TreeSelectionModel mySelectionModel;

    private final CustomPropertyManager myCustomPropertyManager;

    public UserStoryTreeTableModel(UserStoryManager usMgr, TaskManager taskManager,
                                  CustomPropertyManager customPropertyManager) {
        super();
        myCustomPropertyManager = customPropertyManager;
        myUserStoryManager = usMgr;
        myTaskManager = taskManager;
        myTaskManager.addTaskListener(new TaskListenerAdapter() {
            @Override
            public void taskScheduleChanged(TaskScheduleEvent e) {
                Set<UserStory> affected = Sets.newHashSet();
                List<Task> subtree = Lists.newArrayList(myTaskManager.getTaskHierarchy().getDeepNestedTasks(e.getTask()));
                subtree.add(e.getTask());
            }
        });
        root = buildTree();
        this.setRoot(root);
    }

    public int useNextIndex() {
        index++;
        return index;
    }

    public MutableTreeTableNode getNodeForAssigment(ResourceAssignment assignement) {
        /*for (MutableTreeTableNode an : ImmutableList.copyOf(Iterators.forEnumeration(getNodeForUserStory(
                assignement.getResource()).children()))) {
            if (assignement.equals(an.getUserObject())) {
                return an;
            }
        }*/
        return null;
    }

    private DefaultMutableTreeTableNode buildTree() {

        DefaultMutableTreeTableNode root = new DefaultMutableTreeTableNode();
        List<UserStory> listUserStories = myUserStoryManager.getUserStories();
        Iterator<UserStory> itUS = listUserStories.iterator();

        while (itUS.hasNext()) {
            UserStory us = itUS.next();
            UserStoryNode usnUS = new UserStoryNode(us); // the first for the resource
            root.add(usnUS);
        }
        return root;
    }

    public void updateUserStories() {
        UserStory[] listUserStories = myUserStoryManager.getUserStoriesArray();

        for (int idxUS = 0; idxUS < listUserStories.length; idxUS++) {
            UserStory us = listUserStories[idxUS];

            UserStoryNode usnUS = getNodeForUserStory(us);
            if (usnUS == null) {
                usnUS = new UserStoryNode(us);
            }
            buildAssignmentsSubtree(usnUS);
            // for (int i = 0; i < tra.length; i++) {
            // AssignmentNode an = exists(rnRes, tra[i]);
            // if (an == null) {
            // an = new AssignmentNode(tra[i]);
            // rnRes.add(an);
            // }
            // }
            if (getNodeForUserStory(us) == null) {
                root.add(usnUS);
            }
        }
        // this.setRoot(root);

    }

    public UserStoryNode getNodeForUserStory(final UserStory us) {
        try {
            return (UserStoryNode) Iterators.find(Iterators.forEnumeration(root.children()),
                    new Predicate<MutableTreeTableNode>() {
                        @Override
                        public boolean apply(MutableTreeTableNode input) {
                            return input.getUserObject().equals(us);
                        }
                    });
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    public void changeUserStories(List<UserStory> userStories) {
        Iterator<UserStory> it = userStories.iterator();
        while (it.hasNext()) {
            addUserStory(it.next());
        }
    }

    public DefaultMutableTreeTableNode addUserStory(UserStory userStory) {
        DefaultMutableTreeTableNode result = new UserStoryNode(userStory);
        insertNodeInto(result, root, root.getChildCount());
        myUserStoryManager.toString();
        return result;
    }

    public void deleteUserStories(UserStory[] userStories) {
        for (int i = 0; i < userStories.length; i++) {
            deleteUserStory(userStories[i]);
        }
    }

    public void deleteUserStory(UserStory us) {
        removeNodeFromParent(getNodeForUserStory(us));
        // myResourceManager.remove(people);
    }

    /** Move Up the selected resource */
    public boolean moveUp(UserStory us) {
        myUserStoryManager.up(us);
        UserStoryNode usn = getNodeForUserStory(us);
        int index = TreeUtil.getPrevSibling(root, usn);
        if (index == -1) {
            return false;
        }
        removeNodeFromParent(usn);
        insertNodeInto(usn, root, index);
        return true;
    }

    public boolean moveDown(UserStory us) {
        myUserStoryManager.down(us);
        UserStoryNode usn = getNodeForUserStory(us);
        int index = TreeUtil.getNextSibling(root, usn);
        if (index == -1) {
            return false;
        }
        removeNodeFromParent(usn);
        insertNodeInto(usn, root, index);
        return true;
    }

    public void reset() {
        myUserStoryManager.clear();
    }

    public List<UserStory> getAllUserStories() {
        return myUserStoryManager.getUserStories();
    }

    @Override
    public int getColumnCount() {
        return STANDARD_COLUMN_COUNT + myCustomPropertyManager.getDefinitions().size();
    }

    // public ArrayList<ResourceColumn> getColumns()
    // {
    // return new ArrayList<ResourceColumn>(columns.values());
    // }
    //
    // /** @return the ResourceColumn associated to the given index */
    // public ResourceColumn getColumn(int index) {
    // return columns.get(new Integer(index));
    // }

    private CustomPropertyDefinition getCustomProperty(int columnIndex) {
        return myCustomPropertyManager.getDefinitions().get(columnIndex - STANDARD_COLUMN_COUNT);
    }

    @Override
    public Class<?> getColumnClass(int column) {
        if (column < 0) {
            return null;
        }
        if (column >= 0 && column < STANDARD_COLUMN_COUNT) {
            return UserStoryDefaultColumn.values()[column].getValueClass();
        }
        CustomPropertyDefinition customColumn = getCustomProperty(column);
        Class<?> result = customColumn == null ? String.class : customColumn.getType();
        return result;
    }

    @Override
    public String getColumnName(int column) {
        if (column < STANDARD_COLUMN_COUNT) {
            return UserStoryDefaultColumn.values()[column].getName();
        }
        CustomPropertyDefinition customColumn = getCustomProperty(column);
        return customColumn.getName();
    }

    @Override
    public boolean isCellEditable(Object node, int column) {
        if (false == node instanceof UserStoryTableNode) {
            return false;
        }
        if (column >= STANDARD_COLUMN_COUNT) {
            return true;
        }
        UserStoryDefaultColumn standardColumn = UserStoryDefaultColumn.values()[column];
        UserStoryTableNode resourceNode = (UserStoryTableNode) node;
        return resourceNode.isEditable(standardColumn);
    }

    @Override
    public Object getValueAt(Object obj, int column) {
        if (false == obj instanceof UserStoryTableNode) {
            return "";
        }
        UserStoryTableNode node = (UserStoryTableNode)obj;
        if (column >= STANDARD_COLUMN_COUNT) {
            return node.getCustomField(getCustomProperty(column));
        }
        return node.getStandardField(UserStoryDefaultColumn.values()[column]);
    }

    @Override
    public void setValueAt(Object value, Object obj, int column) {
        if (false == obj instanceof UserStoryTableNode) {
            return;
        }
        UserStoryTableNode node = (UserStoryTableNode)obj;
        if (column >= STANDARD_COLUMN_COUNT) {
            node.setCustomField(getCustomProperty(column), value);
            return;
        }
        if (isCellEditable(node, column)) {
            node.setStandardField(UserStoryDefaultColumn.values()[column], value);
        }
    }

    public void resourceChanged(UserStory us) {
        UserStoryNode node = getNodeForUserStory(us);
        if (node == null) {
            return;
        }
        modelSupport.firePathChanged(TreeUtil.createPath(node));
    }

    //TODO
    public void resourceAssignmentsChanged(Iterable<HumanResource> resources) {
        for (HumanResource resource : resources) {
            //ResourceNode nextNode = getNodeForResource(resource);
            //SelectionKeeper selectionKeeper = new SelectionKeeper(mySelectionModel, nextNode);
            //buildAssignmentsSubtree(nextNode);
            //selectionKeeper.restoreSelection();
        }
    }

    //TODO
    private void buildAssignmentsSubtree(UserStoryNode userStoryNode) {
        /*HumanResource resource = resourceNode.getResource();
        resourceNode.removeAllChildren();
        ResourceAssignment[] assignments = resource.getAssignments();
        int[] indices = new int[assignments.length];
        TreeNode[] children = new TreeNode[assignments.length];
        if (assignments.length > 0) {
            for (int i = 0; i < assignments.length; i++) {
                indices[i] = i;
                AssignmentNode an = new AssignmentNode(assignments[i]);
                children[i] = an;
                resourceNode.add(an);
            }
        }
        modelSupport.fireTreeStructureChanged(TreeUtil.createPath(resourceNode));*/
    }

    void decreaseCustomPropertyIndex(int i) {
        index -= i;
    }

    void setSelectionModel(TreeSelectionModel selectionModel) {
        mySelectionModel = selectionModel;
    }

    private class SelectionKeeper {
        private final DefaultMutableTreeTableNode myChangingSubtreeRoot;
        private final TreeSelectionModel mySelectionModel;
        private boolean hasWork = false;
        private Object myModelObject;

        SelectionKeeper(TreeSelectionModel selectionModel, DefaultMutableTreeTableNode changingSubtreeRoot) {
            mySelectionModel = selectionModel;
            myChangingSubtreeRoot = changingSubtreeRoot;
            TreePath selectionPath = mySelectionModel.getSelectionPath();
            if (selectionPath != null && TreeUtil.createPath(myChangingSubtreeRoot).isDescendant(selectionPath)) {
                hasWork = true;
                DefaultMutableTreeTableNode lastNode = (DefaultMutableTreeTableNode) selectionPath.getLastPathComponent();
                myModelObject = lastNode.getUserObject();
            }
        }

        void restoreSelection() {
            if (!hasWork) {
                return;
            }
            for (MutableTreeTableNode node : TreeUtil.collectSubtree(myChangingSubtreeRoot)) {
                if (node.getUserObject().equals(myModelObject)) {
                    mySelectionModel.setSelectionPath(TreeUtil.createPath(node));
                    break;
                }
            }
        }
    }
}
