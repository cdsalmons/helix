package com.linkedin.clustermanager.core.listeners;

import java.util.List;

import com.linkedin.clustermanager.core.NotificationContext;
import com.linkedin.clustermanager.model.ZNRecord;

public interface ExternalViewChangeListener
{

    public void onExternalViewChange(List<ZNRecord> externalViewList,
            NotificationContext changeContext);

}