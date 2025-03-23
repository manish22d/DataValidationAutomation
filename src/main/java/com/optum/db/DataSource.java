package com.optum.db;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;
import com.optum.pojo.Datasource;

import java.beans.PropertyVetoException;

public class DataSource {
    static ComboPooledDataSource cpd;

    public static void init(Datasource datasource) {
        Integer maxPoolSize = datasource.getMaxPollSize();

        cpd = new ComboPooledDataSource();

        cpd.setJdbcUrl(datasource.getUrl());
        cpd.setUser(datasource.getUser());
        cpd.setPassword(datasource.getPassword());

        cpd.setAcquireRetryAttempts(3); // default is 30
        cpd.setAcquireRetryDelay(10000);

        cpd.setInitialPoolSize(5);
        cpd.setMinPoolSize(1);
        cpd.setAcquireIncrement(1);
        if (maxPoolSize == null || maxPoolSize == 0 || maxPoolSize > 500) {
            System.out.println("Poll size for '" + datasource.getName() + "' is incorrect (allowed 1-500). It will be set" +
                    " to 1.");
            cpd.setMaxPoolSize(1);
        } else {
            cpd.setMaxPoolSize(maxPoolSize);
        }

        try {
            cpd.setDriverClass(datasource.getDriver());
        } catch (PropertyVetoException e) {
            // we only log it but not quit application (even it is misconfigured) because we want to run all test
            // anyway and mark them as failed (instead of just one "silent" exception in logs). They will fail
            // while trying to getConnection (see method below).
            System.out.println(e);
        }
    }

    public static ComboPooledDataSource getDataSource() {
        return cpd;
    }
}
