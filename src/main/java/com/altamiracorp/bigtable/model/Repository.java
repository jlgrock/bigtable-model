package com.altamiracorp.bigtable.model;

import com.altamiracorp.bigtable.model.user.ModelUserContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class Repository<T extends Row> {
    private ModelSession modelSession;

    public Repository(ModelSession modelSession) {
        this.modelSession = modelSession;
    }

    public abstract T fromRow(Row row);

    public abstract Row toRow(T obj);

    public abstract String getTableName();

    public T findByRowKey(String rowKey, ModelUserContext user) {
        Row row = modelSession.findByRowKey(getTableName(), rowKey, user);
        if (row == null) {
            return null;
        }
        T r = fromRow(row);
        r.setDirtyBits(false);
        return r;
    }

    public T findByRowKey(String rowKey, Map<String, String> columnsToReturn, ModelUserContext user) {
        Row row = modelSession.findByRowKey(getTableName(), rowKey, columnsToReturn, user);
        if (row == null) {
            return null;
        }
        T r = fromRow(row);
        r.setDirtyBits(false);
        return r;
    }

    public List<T> findByRowStartsWith(String rowKeyPrefix, ModelUserContext user) {
        Collection<Row> rows = modelSession.findByRowStartsWith(getTableName(), rowKeyPrefix, user);
        return fromRows(rows);
    }

    public List<T> findAll(ModelUserContext user) {
        Collection<Row> rows = modelSession.findByRowStartsWith(getTableName(), null, user);
        return fromRows(rows);
    }

    public void save(T obj, ModelUserContext user) {
        Row r = toRow(obj);
        modelSession.save(r, user);
    }

    public void saveMany(Collection<T> objs, ModelUserContext user) {
        List<Row> rows = new ArrayList<Row>();
        String tableName = null;
        for (T obj : objs) {
            Row row = toRow(obj);
            if (tableName == null) {
                tableName = row.getTableName();
            }
            rows.add(row);
        }
        modelSession.saveMany(tableName, rows, user);
    }

    public List<T> fromRows(Collection<Row> rows) {
        ArrayList<T> results = new ArrayList<T>();
        for (Row row : rows) {
            T r = fromRow(row);
            r.setDirtyBits(false);
            results.add(r);
        }
        return results;
    }

    public void delete(RowKey rowKey, ModelUserContext user) {
        modelSession.deleteRow(getTableName(), rowKey, user);
    }

    protected ModelSession getModelSession() {
        return modelSession;
    }
}
