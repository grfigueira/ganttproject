/*
 * Created on 22.10.2005
 */
package net.sourceforge.ganttproject;

import net.sourceforge.ganttproject.chart.Chart;
import net.sourceforge.ganttproject.chart.overview.GPToolbar;
import net.sourceforge.ganttproject.chart.overview.ToolbarBuilder;
import net.sourceforge.ganttproject.gui.UIFacade;
import net.sourceforge.ganttproject.gui.view.GPView;

import javax.swing.*;
import java.awt.*;

class UserStoryChartTabContentPanel extends ChartTabContentPanel implements GPView {
    private TreeTableContainer myTreeFacade;
    private Component myUserStoryChart;
    private JComponent myTabContentPanel;

    UserStoryChartTabContentPanel(IGanttProject project, UIFacade workbenchFacade, TreeTableContainer userStoryTree,
                                 Component userStoryChart) {
        super(project, workbenchFacade, workbenchFacade.getResourceChart());
        myTreeFacade = userStoryTree;
        myUserStoryChart = userStoryChart;
        addTableResizeListeners(userStoryTree.getTreeComponent(), myTreeFacade.getTreeTable().getScrollPane().getViewport());
    }

    JComponent getComponent() {
        if (myTabContentPanel == null) {
            myTabContentPanel = createContentComponent();
        }
        return myTabContentPanel;
    }

    @Override
    protected Component createButtonPanel() {
        ToolbarBuilder builder = new ToolbarBuilder()
                .withHeight(24)
                .withSquareButtons()
                .withDpiOption(getUiFacade().getDpiOption())
                .withLafOption(getUiFacade().getLafOption(), null);
        myTreeFacade.addToolbarActions(builder);
        final GPToolbar toolbar = builder.build();
        return toolbar.getToolbar();
    }

    @Override
    protected Component getChartComponent() {
        return myUserStoryChart;
    }

    @Override
    protected Component getTreeComponent() {
        return myTreeFacade.getTreeComponent();
    }

    @Override
    public Chart getChart() {
        return getUiFacade().getResourceChart();
    }

    @Override
    public Component getViewComponent() {
        return getComponent();
    }
}
