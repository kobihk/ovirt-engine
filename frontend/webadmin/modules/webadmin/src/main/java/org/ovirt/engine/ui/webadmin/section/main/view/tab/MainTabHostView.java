package org.ovirt.engine.ui.webadmin.section.main.view.tab;

import java.util.LinkedList;
import java.util.List;

import org.ovirt.engine.core.common.businessentities.VDS;
import org.ovirt.engine.core.common.businessentities.VDSStatus;
import org.ovirt.engine.core.common.businessentities.VdsSpmStatus;
import org.ovirt.engine.core.common.mode.ApplicationMode;
import org.ovirt.engine.ui.common.idhandler.ElementIdHandler;
import org.ovirt.engine.ui.common.uicommon.model.MainModelProvider;
import org.ovirt.engine.ui.common.widget.action.ActionButtonDefinition;
import org.ovirt.engine.ui.common.widget.action.CommandLocation;
import org.ovirt.engine.ui.common.widget.table.SimpleActionTable;
import org.ovirt.engine.ui.common.widget.table.column.EnumColumn;
import org.ovirt.engine.ui.common.widget.table.column.TextColumnWithTooltip;
import org.ovirt.engine.ui.frontend.AsyncQuery;
import org.ovirt.engine.ui.frontend.INewAsyncCallback;
import org.ovirt.engine.ui.uicommonweb.ReportInit;
import org.ovirt.engine.ui.uicommonweb.UICommand;
import org.ovirt.engine.ui.uicommonweb.dataprovider.AsyncDataProvider;
import org.ovirt.engine.ui.uicommonweb.models.ApplicationModeHelper;
import org.ovirt.engine.ui.uicommonweb.models.hosts.HostListModel;
import org.ovirt.engine.ui.webadmin.ApplicationConstants;
import org.ovirt.engine.ui.webadmin.section.main.presenter.tab.MainTabHostPresenter;
import org.ovirt.engine.ui.webadmin.section.main.view.AbstractMainTabWithDetailsTableView;
import org.ovirt.engine.ui.webadmin.uicommon.ReportActionsHelper;
import org.ovirt.engine.ui.webadmin.widget.action.WebAdminButtonDefinition;
import org.ovirt.engine.ui.webadmin.widget.action.WebAdminMenuBarButtonDefinition;
import org.ovirt.engine.ui.webadmin.widget.table.column.HostStatusColumn;
import org.ovirt.engine.ui.webadmin.widget.table.column.PercentColumn;

import com.google.gwt.core.client.GWT;
import com.google.inject.Inject;

public class MainTabHostView extends AbstractMainTabWithDetailsTableView<VDS, HostListModel> implements MainTabHostPresenter.ViewDef {

    interface ViewIdHandler extends ElementIdHandler<MainTabHostView> {

        ViewIdHandler idHandler = GWT.create(ViewIdHandler.class);
    }

    int maxSpmPriority;
    int defaultSpmPriority;
    ApplicationConstants constants;

    @Inject
    public MainTabHostView(MainModelProvider<VDS, HostListModel> modelProvider, ApplicationConstants constants) {
        super(modelProvider);
        ViewIdHandler.idHandler.generateAndSetIds(this);

        this.constants = constants;

        InitSpmPriorities();
    }

    private void InitSpmPriorities() {
        AsyncDataProvider.GetMaxSpmPriority(new AsyncQuery(this, new INewAsyncCallback() {
            @Override
            public void OnSuccess(Object target, Object returnValue) {

                MainTabHostView view = (MainTabHostView) target;

                view.maxSpmPriority = (Integer) returnValue;
                InitSpmPriorities1();
            }
        }));
    }

    private void InitSpmPriorities1() {
        AsyncDataProvider.GetDefaultSpmPriority(new AsyncQuery(this, new INewAsyncCallback() {
            @Override
            public void OnSuccess(Object target, Object returnValue) {

                MainTabHostView view = (MainTabHostView) target;

                view.defaultSpmPriority = (Integer) returnValue;
                InitSpmPriorities2();
            }
        }));
    }

    private void InitSpmPriorities2() {
        initTable();
        initWidget(getTable());
    }

    void initTable() {
        getTable().enableColumnResizing();

        getTable().addColumn(new HostStatusColumn<VDS>(), constants.empty(), "30px"); //$NON-NLS-1$

        TextColumnWithTooltip<VDS> nameColumn = new TextColumnWithTooltip<VDS>() {
            @Override
            public String getValue(VDS object) {
                return object.getVdsName();
            }
        };
        getTable().addColumn(nameColumn, constants.nameHost(), "150px"); //$NON-NLS-1$

        TextColumnWithTooltip<VDS> hostColumn = new TextColumnWithTooltip<VDS>() {
            @Override
            public String getValue(VDS object) {
                return object.getHostName();
            }
        };
        getTable().addColumn(hostColumn, constants.ipHost(), "150px"); //$NON-NLS-1$

        TextColumnWithTooltip<VDS> clusterColumn = new TextColumnWithTooltip<VDS>() {
            @Override
            public String getValue(VDS object) {
                return object.getVdsGroupName();
            }
        };
        getTable().addColumn(clusterColumn, constants.clusterHost(), "150px"); //$NON-NLS-1$

        if (ApplicationModeHelper.getUiMode() != ApplicationMode.GlusterOnly) {
            TextColumnWithTooltip<VDS> dcColumn = new TextColumnWithTooltip<VDS>() {
                @Override
                public String getValue(VDS object) {
                    return object.getStoragePoolName();
                }
            };
            getTable().addColumn(dcColumn, constants.dcHost(), "150px"); //$NON-NLS-1$
        }

        TextColumnWithTooltip<VDS> statusColumn = new EnumColumn<VDS, VDSStatus>() {
            @Override
            public VDSStatus getRawValue(VDS object) {
                return object.getStatus();
            }
        };
        getTable().addColumn(statusColumn, constants.statusHost(), "100px"); //$NON-NLS-1$

        if (ApplicationModeHelper.getUiMode() != ApplicationMode.GlusterOnly) {
            TextColumnWithTooltip<VDS> vmCountColumn = new TextColumnWithTooltip<VDS>() {
                @Override
                public String getValue(VDS object) {
                    return String.valueOf(object.getVmCount());
                }
            };
            getTable().addColumn(vmCountColumn, constants.vmsCount(), "110px"); //$NON-NLS-1$
        }

        PercentColumn<VDS> memColumn = new PercentColumn<VDS>() {
            @Override
            public Integer getProgressValue(VDS object) {
                return object.getUsageMemPercent();
            }
        };
        getTable().addColumn(memColumn, constants.memoryHost(), "60px"); //$NON-NLS-1$

        PercentColumn<VDS> cpuColumn = new PercentColumn<VDS>() {
            @Override
            public Integer getProgressValue(VDS object) {
                return object.getUsageCpuPercent();
            }
        };
        getTable().addColumn(cpuColumn, constants.cpuHost(), "60px"); //$NON-NLS-1$

        PercentColumn<VDS> netColumn = new PercentColumn<VDS>() {
            @Override
            public Integer getProgressValue(VDS object) {
                return object.getUsageNetworkPercent();
            }
        };
        getTable().addColumn(netColumn, constants.networkHost(), "60px"); //$NON-NLS-1$

        if (ApplicationModeHelper.getUiMode() != ApplicationMode.GlusterOnly) {
            TextColumnWithTooltip<VDS> spmColumn = new TextColumnWithTooltip<VDS>() {
                @Override
                public String getValue(VDS object) {
                    int value = object.getVdsSpmPriority();
                    int lowValue = defaultSpmPriority / 2;
                    int highValue = defaultSpmPriority + (maxSpmPriority - defaultSpmPriority) / 2;

                    if (object.getSpmStatus() != VdsSpmStatus.None){
                        return object.getSpmStatus().name();
                    }

                    if (value == -1) {
                        return constants.spmNeverText();
                    } else if (value == lowValue) {
                        return constants.spmLowText();
                    } else if (value == defaultSpmPriority) {
                        return constants.spmNormalText();
                    } else if (value == highValue) {
                        return constants.spmHighText();
                    }

                    return constants.spmCustomText();
                }
            };
            getTable().addColumn(spmColumn, constants.spmPriorityHost(), "100px"); //$NON-NLS-1$
        }

        getTable().addActionButton(new WebAdminButtonDefinition<VDS>(constants.newHost()) {
            @Override
            protected UICommand resolveCommand() {
                return getMainModel().getNewCommand();
            }
        });
        getTable().addActionButton(new WebAdminButtonDefinition<VDS>(constants.editHost()) {
            @Override
            protected UICommand resolveCommand() {
                return getMainModel().getEditCommand();
            }
        });
        getTable().addActionButton(new WebAdminButtonDefinition<VDS>(constants.removeHost()) {
            @Override
            protected UICommand resolveCommand() {
                return getMainModel().getRemoveCommand();
            }
        });
        // TODO: separator
        getTable().addActionButton(new WebAdminButtonDefinition<VDS>(constants.activateHost()) {
            @Override
            protected UICommand resolveCommand() {
                return getMainModel().getActivateCommand();
            }
        });
        getTable().addActionButton(new WebAdminButtonDefinition<VDS>(constants.maintenanceHost()) {
            @Override
            protected UICommand resolveCommand() {
                return getMainModel().getMaintenanceCommand();
            }
        });
        getTable().addActionButton(new WebAdminButtonDefinition<VDS>(constants.confirmRebootedHost(),
                CommandLocation.OnlyFromFromContext) {
            @Override
            protected UICommand resolveCommand() {
                return getMainModel().getManualFenceCommand();
            }
        });
        // TODO: separator
        getTable().addActionButton(new WebAdminButtonDefinition<VDS>(constants.approveHost()) {
            @Override
            protected UICommand resolveCommand() {
                return getMainModel().getApproveCommand();
            }
        });
        getTable().addActionButton(new WebAdminButtonDefinition<VDS>(constants.configureLocalStorageHost()) {
            @Override
            protected UICommand resolveCommand() {
                return getMainModel().getConfigureLocalStorageCommand();
            }
        });

        List<ActionButtonDefinition<VDS>> pmSubActions = new LinkedList<ActionButtonDefinition<VDS>>();

        pmSubActions.add(new WebAdminButtonDefinition<VDS>(constants.restartHost()) {
            @Override
            protected UICommand resolveCommand() {
                return getMainModel().getRestartCommand();
            }
        });

        pmSubActions.add(new WebAdminButtonDefinition<VDS>(constants.startHost()) {
            @Override
            protected UICommand resolveCommand() {
                return getMainModel().getStartCommand();
            }
        });

        pmSubActions.add(new WebAdminButtonDefinition<VDS>(constants.stopHost()) {
            @Override
            protected UICommand resolveCommand() {
                return getMainModel().getStopCommand();
            }
        });

        if (ApplicationModeHelper.getUiMode() != ApplicationMode.GlusterOnly)
        {
            getTable().addActionButton(new WebAdminMenuBarButtonDefinition<VDS>(constants.pmHost(),
                    pmSubActions,
                    CommandLocation.OnlyFromToolBar));
        }

        getTable().addActionButton(new WebAdminButtonDefinition<VDS>(constants.assignTagsHost()) {
            @Override
            protected UICommand resolveCommand() {
                return getMainModel().getAssignTagsCommand();
            }
        });

        if (ReportInit.getInstance().isReportsEnabled()) {
            List<ActionButtonDefinition<VDS>> resourceSubActions =
                    ReportActionsHelper.getInstance().getResourceSubActions("Host", getModelProvider()); //$NON-NLS-1$
            if (resourceSubActions != null && resourceSubActions.size() > 0) {
                getTable().addActionButton(new WebAdminMenuBarButtonDefinition<VDS>(constants.showReportHost(), resourceSubActions));
            }
        }
    }

    @Override
    public SimpleActionTable<VDS> getTable() {
        return super.getTable();
    }

}
