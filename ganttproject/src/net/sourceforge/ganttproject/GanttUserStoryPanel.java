/*
GanttProject is an opensource project management tool.
Copyright (C) 2003-2011 Thomas Alexandre, GanttProject Team

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
package net.sourceforge.ganttproject;

import com.google.common.collect.Lists;
import net.sourceforge.ganttproject.action.ActiveActionProvider;
import net.sourceforge.ganttproject.action.ArtefactDeleteAction;
import net.sourceforge.ganttproject.action.GPAction;
import net.sourceforge.ganttproject.action.resource.ResourceActionSet;
import net.sourceforge.ganttproject.action.userStory.UserStoryActionSet;
import net.sourceforge.ganttproject.action.userStory.UserStoryNewAction;
import net.sourceforge.ganttproject.chart.Chart;
import net.sourceforge.ganttproject.chart.gantt.ClipboardContents;
import net.sourceforge.ganttproject.chart.overview.ToolbarBuilder;
import net.sourceforge.ganttproject.gui.ResourceTreeUIFacade;
import net.sourceforge.ganttproject.gui.UIFacade;
import net.sourceforge.ganttproject.gui.UserStoryTreeUIFacade;
import net.sourceforge.ganttproject.language.GanttLanguage;
import net.sourceforge.ganttproject.resource.AssignmentContext;
import net.sourceforge.ganttproject.resource.AssignmentNode;
import net.sourceforge.ganttproject.resource.HumanResource;
import net.sourceforge.ganttproject.resource.ResourceContext;
import net.sourceforge.ganttproject.resource.ResourceEvent;
import net.sourceforge.ganttproject.resource.ResourceNode;
import net.sourceforge.ganttproject.resource.ResourceView;
import net.sourceforge.ganttproject.task.ResourceAssignment;
import net.sourceforge.ganttproject.task.Task;
import net.sourceforge.ganttproject.task.TaskSelectionManager;
import net.sourceforge.ganttproject.userStory.*;
import net.sourceforge.ganttproject.util.collect.Pair;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;

//Added @Catarina
public class GanttUserStoryPanel extends TreeTableContainer<UserStory, UserStoryTreeTable, UserStoryTreeTableModel>
        implements UserStoryView, UserStoryContext, UserStoryTreeUIFacade {

    public final GanttProject appli;

    private final UserStoryActionSet myUserStoryActionSet;
    private final GanttProjectBase.RowHeightAligner myRowHeightAligner;

    public UserStoryLoadGraphicArea area;

    private GPAction myTaskPropertiesAction;

    private final UIFacade myUIFacade;

    private static Pair<UserStoryTreeTable, UserStoryTreeTableModel> createTreeTable(IGanttProject project,
                                                                                   UIFacade uiFacade) {
        UserStoryTreeTableModel model = new UserStoryTreeTableModel(project.getUserStoryManager(),
                project.getTaskManager(), project.getResourceCustomPropertyManager());
        return Pair.create(new UserStoryTreeTable(project, model, uiFacade), model);
    }

    public GanttUserStoryPanel(final GanttProject prj, final UIFacade uiFacade) {
        super(createTreeTable(prj.getProject(), uiFacade));
        appli = prj;
        myUIFacade = uiFacade;

        prj.addProjectEventListener(getProjectEventListener());
        myUserStoryActionSet = new UserStoryActionSet(this, null, prj, uiFacade, getTreeTable());

        /*final GPAction resourceDeleteAction = myUserStoryActionSet.getUserStoryDeleteAction();
        final GPAction assignmentDeleteAction = myUserStoryActionSet.getAssignmentDelete();
        GPAction deleteAction = new ArtefactDeleteAction(new ActiveActionProvider() {
            @Override
            public AbstractAction getActiveAction() {
                if (getResourceAssignments().length > 0) {
                    return assignmentDeleteAction;
                }
                return resourceDeleteAction;
            }
        }, new Action[]{resourceDeleteAction, assignmentDeleteAction});
        setArtefactActions(myResourceActionSet.getResourceNewAction(),
                myResourceActionSet.getResourcePropertiesAction(),
                deleteAction);
        getTreeTable().setupActionMaps(myUserStoryActionSet.getResourceMoveUpAction(),
                myResourceActionSet.getResourceMoveDownAction(), null, null, deleteAction,
                appli.getCutAction(), appli.getCopyAction(), appli.getPasteAction(),
                myResourceActionSet.getResourcePropertiesAction());
        getTreeTable().addActionWithAccelleratorKey(myResourceActionSet.getAssignmentDelete());
        getTreeTable().setRowHeight(20);

        getTreeTable().insertWithLeftyScrollBar(this);
        area = new ResourceLoadGraphicArea(prj, prj.getZoomManager(), this) {
            @Override
            public boolean isExpanded(HumanResource hr) {
                return getResourceTreeTable().isExpanded(hr);
            }

            @Override
            protected int getRowHeight() {
                return getTreeTable().getRowHeight();
            }
        };
        prj.getZoomManager().addZoomListener(area.getZoomListener());
        area.getChartModel().setRowHeight(getTreeTable().getRowHeight());

        this.setBackground(new Color(0.0f, 0.0f, 0.0f));
        updateContextActions();
        // applyComponentOrientation(lang.getComponentOrientation());*/
        myRowHeightAligner = new GanttProjectBase.RowHeightAligner(this, this.area.getChartModel());
    }

    @Override
    protected void init() {
        getTreeTable().initTreeTable();
    }

    public GanttProjectBase.RowHeightAligner getRowHeightAligner() {
        return myRowHeightAligner;
    }

    private ProjectEventListener getProjectEventListener() {
        return new ProjectEventListener.Stub() {
            @Override
            public void projectClosed() {
                area.repaint();
                reset();
            }
        };
    }

    @Override
    protected void onSelectionChanged(List<DefaultMutableTreeTableNode> selection) {
        super.onSelectionChanged(selection);
        getPropertiesAction().setEnabled(!selection.isEmpty());
        updateContextActions();
        List<Task> selectedTasks = Lists.newArrayList();
        for (DefaultMutableTreeTableNode node : selection) {
            if (node instanceof AssignmentNode) {
                selectedTasks.add(((AssignmentNode) node).getTask());
            }
        }
        if (selectedTasks.isEmpty()) {
            myUIFacade.getTaskSelectionManager().clear();
        } else {
            myUIFacade.getTaskSelectionManager().setSelectedTasks(selectedTasks);
        }
    }

    private void updateContextActions() {
        /*myUserStoryActionSet.getResourcePropertiesAction().setEnabled(getResources().length == 1);
        myUserStoryActionSet.getResourceDeleteAction().setEnabled(getResources().length > 0);
        myUserStoryActionSet.getAssignmentDelete().setEnabled(getResourceAssignments().length > 0);*/
        appli.getViewManager().getCopyAction().setEnabled(getUserStories().length > 0);
        appli.getViewManager().getCutAction().setEnabled(getUserStories().length > 0);
    }

    @Override
    protected void handlePopupTrigger(MouseEvent e) {
        /*if (e.isPopupTrigger() || e.getButton() == MouseEvent.BUTTON3) {
            DefaultMutableTreeTableNode[] selectedNodes = getSelectedNodes();
            // TODO Allow to have multiple assignments selected as well!
            if (selectedNodes.length == 1 && selectedNodes[0] instanceof AssignmentNode) {
                // Clicked on an assignment node (ie a task assigned to a resource)
                AssignmentNode assignmentNode = (AssignmentNode) selectedNodes[0];
                getTaskSelectionManager().clear();
                getTaskSelectionManager().addTask(assignmentNode.getTask());
                Point popupPoint = getPopupMenuPoint(e);
                getUIFacade().showPopupMenu(this,
                        new Action[]{myTaskPropertiesAction, myResourceActionSet.getAssignmentDelete()}, popupPoint.x,
                        popupPoint.y);
            } else {
                createPopupMenu(e);
            }
        }*/
    }

    private Point getPopupMenuPoint(MouseEvent popupTriggerEvent) {
        final int x = popupTriggerEvent.getX();
        final int y = popupTriggerEvent.getY() + getTreeTable().getRowHeight();
        return new Point(x, y);
    }

    /**
     * Create the popup menu
     */
    private void createPopupMenu(MouseEvent e) {
        /*JPopupMenu menu = new JPopupMenu();
        AbstractAction[] resourceActions = myResourceActionSet.getActions();
        menu.add(resourceActions[0]);
        if (getSelectedNodes().length == 1) {
            for (int i = 1; i < resourceActions.length; i++) {
                menu.add(resourceActions[i]);
            }
            menu.add(myResourceActionSet.getResourceSendMailAction());
            menu.addSeparator();
            menu.add(myResourceActionSet.getResourceMoveUpAction());
            menu.add(myResourceActionSet.getResourceMoveDownAction());
            menu.addSeparator();
            menu.add(appli.getCutAction());
            menu.add(appli.getCopyAction());
            menu.add(appli.getPasteAction());
            menu.add(myResourceActionSet.getResourceDeleteAction());
        }
        menu.applyComponentOrientation(GanttLanguage.getInstance().getComponentOrientation());
        Point popupPoint = getPopupMenuPoint(e);
        menu.show(this, popupPoint.x, popupPoint.y);*/
    }

    @Override
    public void userStoryAdded(UserStoryEvent event) {
        newUserStory(event.getUserStory());
    }

    @Override
    public void userStoryRemoved(UserStoryEvent event) {
        /*getTreeTable().getTreeTable().editingStopped(new ChangeEvent(getTreeTable().getTreeTable()));
        getTreeModel().deleteResources(event.getResources());*/
    }

    @Override
    public void userStoryChanged(UserStoryEvent e) {
        /*getTreeModel().resourceChanged(e.getResource());
        e.getResource().resetLoads();
        repaint();*/
    }

    @Override
    public void userStoryAssignmentsChanged(UserStoryEvent e) {
        /*getTreeModel().resourceAssignmentsChanged(Arrays.asList(e.getResources()));
        repaint();*/
    }

    // //////////////////////////////////////////////////////////////////////////
    // ResourceContext interface
    @Override
    public UserStory[] getUserStories() {
        // ProjectResource[] res;
        // List allRes = model.getAllResouces();
        // res = new ProjectResource[allRes.size()];
        // model.getAllResouces().toArray(res);
        // return res;
        DefaultMutableTreeTableNode[] tNodes = getSelectedNodes();
        if (tNodes == null) {
            return new UserStory[0];
        }
        int nbUserStories = 0;
        for (int i = 0; i < tNodes.length; i++) {
            if (tNodes[i] instanceof UserStoryNode) {
                nbUserStories++;
            }
        }

        UserStory[] res = new UserStory[nbUserStories];
        for (int i = 0; i < nbUserStories; i++) {
            if (tNodes[i] instanceof UserStoryNode) {
                res[i] = (UserStory) ((UserStoryNode) tNodes[i]).getUserObject();
            }
        }
        return res;
    }

    /**
     * Create a new Human
     */
    public void newUserStory(UserStory userStory) {
        if (userStory != null) {
            try {
                DefaultMutableTreeTableNode result = getTreeModel().addUserStory(userStory);
                getTreeTable().getTree().scrollPathToVisible(TreeUtil.createPath(result));
            } catch (Exception e) {
                System.err.println("when adding this guy: " + userStory);
                e.printStackTrace();
            }
        }
    }

    /**
     * Return the list of the person
     */
    public List<UserStory> getAllUserStories() {
        return getTreeModel().getAllUserStories();
    }

    public UserStoryTreeTable getUserStoryTreeTable() {
        return getTreeTable();
    }

    public UserStoryTreeTableModel getUserStoryTreeTableModel() {
        return getTreeModel();
    }

    /**
     * Return the number of people on the list
     */
    public int nbPeople() {
        return getTreeModel().getAllUserStories().size();
    }

    /**
     * Reset all human...
     */
    public void reset() {
        getTreeModel().reset();
    }

    public UserStoryContext getContext() {
        return this;
    }

    /*@Override
    public ResourceAssignment[] getResourceAssignments() {
        ResourceAssignment[] res = null;
        DefaultMutableTreeTableNode[] tNodes = getSelectedNodes();
        if (tNodes != null) {
            int nbAssign = 0;
            for (int i = 0; i < tNodes.length; i++) {
                if (tNodes[i] instanceof AssignmentNode) {
                    nbAssign++;
                }
            }

            res = new ResourceAssignment[nbAssign];
            for (int i = 0; i < nbAssign; i++) {
                if (tNodes[i] instanceof AssignmentNode) {
                    res[i] = (ResourceAssignment) ((AssignmentNode) tNodes[i]).getUserObject();
                }
            }
        }
        return res;
    }*/

    public void copySelection(ClipboardContents clipboardContents) {
        saveSelectionToClipboard(clipboardContents, false);
    }

    public void cutSelection(ClipboardContents clipboardContents) {
        saveSelectionToClipboard(clipboardContents, true);
    }

    private void saveSelectionToClipboard(ClipboardContents clipboardContents, boolean cut) {
        DefaultMutableTreeTableNode selectedNodes[] = getSelectedNodes();

        if (selectedNodes == null) {
            return;
        }

        for (DefaultMutableTreeTableNode node : selectedNodes) {
            if (node instanceof UserStoryNode) {
                UserStory res = (UserStory) node.getUserObject();
                if (cut) {
                    this.appli.getUserStoryManager().remove(res, this.appli.getUndoManager());
                }
                clipboardContents.addUserStory(res);
            }
        }
    }

    @Override
    public void setSelected(UserStory userStory, boolean clear) {
        if (clear) {
            clearSelection();
        }
        getTree().getTreeSelectionModel().setSelectionPath(
                TreeUtil.createPath(getUserStoryTreeTableModel().getNodeForUserStory(userStory)));
    }

    @Override
    public void clearSelection() {
        getTree().clearSelection();
    }

    @Override
    public AbstractAction getMoveUpAction() {
        //return myUserStoryActionSet.getResourceMoveUpAction();
        return null;
    }

    @Override
    public AbstractAction getMoveDownAction() {
        //return myResourceActionSet.getResourceMoveDownAction();
        return null;
    }

    @Override
    public void startDefaultEditing(UserStory modelElement) {
    }

    @Override
    public AbstractAction[] getTreeActions() {
        return new AbstractAction[]{getMoveUpAction(), getMoveDownAction()};
    }

    @Override
    public void addToolbarActions(ToolbarBuilder builder) {
        /*builder.addButton(myUserStoryActionSet.getResourceMoveUpAction().asToolbarAction())
                .addButton(myUserStoryActionSet.getResourceMoveDownAction().asToolbarAction());*/
    }

    public UserStoryActionSet getUserStoryActionSet() {
        return myUserStoryActionSet;
    }

    void setTaskPropertiesAction(GPAction action) {
        myTaskPropertiesAction = action;
        getTreeTable().addActionWithAccelleratorKey(action);
    }

    private UIFacade getUIFacade() {
        return myUIFacade;
    }

    private TaskSelectionManager getTaskSelectionManager() {
        return getUIFacade().getTaskSelectionManager();
    }

    @Override
    protected DefaultMutableTreeTableNode getRootNode() {
        return (DefaultMutableTreeTableNode) getTreeModel().getRoot();
    }

    @Override
    protected Chart getChart() {
        return myUIFacade.getResourceChart();
    }
}
