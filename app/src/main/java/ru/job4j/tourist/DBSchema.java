package ru.job4j.tourist;

public class DBSchema {
    public static final class LocationsTable {
        public static final String NAME = "locations";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                LocationsTable.NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                LocationsTable.Cols.NAME + " TEXT, " +
                Cols.LATITUDE + " TEXT, " +
                Cols.LONGITUDE + " TEXT " +
                 ")";


        public static final class Cols {
            public static final String ID = "id";
            public static final String NAME = "name";
            public static final String LATITUDE = "latitude";
            public static final String LONGITUDE = "longitude";
        }
    }


}
