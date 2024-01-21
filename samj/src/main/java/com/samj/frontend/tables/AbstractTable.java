package com.samj.frontend.tables;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.List;

public abstract class AbstractTable<T> {

    protected TableView<T> table;

    protected ObservableList<T> tableData;

    public AbstractTable(ObservableList<T> tableData) {
        this.tableData = tableData;

        initializeTable();
        addClassesToTableComponents();
    }

    protected void initializeTable() {
        table = new TableView<>();

        setTableColumns();
        addColumnsToTheTable();
        setUpCellValueFactoriesForColumns();
        setSearchInputFields();
        setSearchFunctionalityForEachColumnInTable();
    }

    protected abstract void setTableColumns();

    protected abstract void addClassesToTableComponents();

    protected abstract void addColumnsToTheTable();

    protected abstract void setUpCellValueFactoriesForColumns();

    protected abstract void setSearchInputFields();

    protected abstract void setSearchFunctionalityForEachColumnInTable();

    protected abstract void updatePredicate(FilteredList<T> filteredData);

    public abstract List<TextField> getSearchFields();

    public abstract List<TableColumn<T, String>> getColumns();

    public TableView<T> getTable() {
        return table;
    }

    public void setTable(TableView<T> table) {
        this.table = table;
    }

    public ObservableList<T> getTableData() {
        return tableData;
    }

    public void setTableData(ObservableList<T> tableData) {
        this.tableData = tableData;
    }
}
