package net.vanroy.cloud.dashboard.model;

import java.util.Date;

/**
 * @author: Julien Roy
 * @version: $Id$
 */
public class InstanceHistory {

    private final String id;
    private final Date date;

    public InstanceHistory(String id, Date date) {
        this.id = id;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }
}
