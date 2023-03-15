package com.task;

public class Airport {
    private final String[] date;
    private final int searchColumn;

    public Airport(String[] date, int searchColumn) {
        this.date = date;
        this.searchColumn = searchColumn - 1;
    }

    public String getData(int index) {
        return date[index];
    }

    public String getSearchValue() {
        return date[searchColumn];
    }

    public String getSearchString() {
        return String.join(",", date);
    }

    public int getSizeColumn() {
        return getSearchString().split(",").length;
    }
}
