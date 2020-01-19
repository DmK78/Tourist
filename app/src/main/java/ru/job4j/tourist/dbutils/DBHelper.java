package ru.job4j.tourist.dbutils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ru.job4j.tourist.model.Point;
import ru.job4j.tourist.model.Track;

public class DBHelper extends SQLiteOpenHelper {
    SQLiteDatabase dbRead = getReadableDatabase();
    SQLiteDatabase dbWrite = getWritableDatabase();
    private static DBHelper mInstance;

    public static final String DB = "locations.db";
    public static final int VERSION = 8;
    private Context context;

    public static DBHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DBHelper(context);
        }
        return mInstance;
    }

    private DBHelper(Context context) {
        super(context, DB, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(DBSchema.LocationsTable.CREATE_TABLE);
        db.execSQL(DBSchema.TracksTable.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + DBSchema.LocationsTable.NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DBSchema.TracksTable.NAME);
        onCreate(db);
    }

    public List<Point> getAllPoints() {
        List<Point> result = new ArrayList<>();
        Cursor cursor = dbRead.query(
                DBSchema.LocationsTable.NAME,
                null, null, null,
                null, null, null
        );
        if (cursor.moveToFirst()) {
            do {
                Location location = new Location("");
                location.setLatitude(Double.valueOf(cursor.getString(cursor.getColumnIndex(DBSchema.LocationsTable.Cols.LATITUDE))));
                location.setLongitude(Double.valueOf(cursor.getString(cursor.getColumnIndex(DBSchema.LocationsTable.Cols.LONGITUDE))));
                Log.i("point", String.valueOf("get loc " + location.getLatitude() + " " + location.getLongitude()));
                result.add(new Point(
                        cursor.getInt(cursor.getColumnIndex(DBSchema.LocationsTable.Cols.ID)),
                        cursor.getString(cursor.getColumnIndex(DBSchema.LocationsTable.Cols.NAME)), location));
            } while (cursor.moveToNext());
        }
        return result;
    }

    public List<Point> getPointsByTrack(Track track) {
        List<Point> result = new ArrayList<>();
        String selectionJobs = DBSchema.LocationsTable.Cols.TRACK_ID + " =?";
        String[] selectionArgsQuestion = new String[]{String.valueOf(track.getId())};
        Cursor cursor = dbRead.query(
                DBSchema.LocationsTable.NAME,
                null, selectionJobs, selectionArgsQuestion,
                null, null, null
        );
        if (cursor.moveToFirst()) {
            do {
                Location location = new Location("");

                location.setLatitude((double) cursor.getFloat(cursor.getColumnIndex(DBSchema.LocationsTable.Cols.LATITUDE)));
                location.setLongitude((double) cursor.getFloat(cursor.getColumnIndex(DBSchema.LocationsTable.Cols.LONGITUDE)));
                Point point = new Point(cursor.getInt(cursor.getColumnIndex("id")),
                        null, location,
                        cursor.getInt(cursor.getColumnIndex(DBSchema.LocationsTable.Cols.TRACK_ID)));
                result.add(point);
            } while (cursor.moveToNext());
        }
        return result;


    }

    public List<Track> getAllTracks() {
        List<Track> result = new ArrayList<>();
        Cursor cursor = dbRead.query(
                DBSchema.TracksTable.NAME,
                null, null, null,
                null, null, null
        );
        if (cursor.moveToFirst()) {
            do {
                Track track = new Track(
                        cursor.getInt(cursor.getColumnIndex(DBSchema.TracksTable.Cols.ID)),
                        cursor.getString(cursor.getColumnIndex(DBSchema.TracksTable.Cols.NAME)),
                        new ArrayList<>());
                track.setPoints(getPointsByTrack(track));
                result.add(track);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    public Track addTrack(Track track) {


        ContentValues value = new ContentValues();
        value.put(DBSchema.TracksTable.Cols.NAME, track.getName());
        long trackId = dbWrite.insert(DBSchema.TracksTable.NAME, null, value);
        for (Point point : track.getPoints()) {
            addLocation(point);
        }
        track.setId((int)trackId);
        return track;
    }


    public void addLocation(Point point) {
        Log.i("point", String.valueOf(point.getLocation().getLatitude() + " " + point.getLocation().getLongitude()));
        ContentValues value = new ContentValues();
        value.put(DBSchema.LocationsTable.Cols.NAME, point.getName());
        value.put(DBSchema.LocationsTable.Cols.LATITUDE, point.getLocation().getLatitude());
        value.put(DBSchema.LocationsTable.Cols.LONGITUDE, point.getLocation().getLongitude());
        value.put(DBSchema.LocationsTable.Cols.TRACK_ID, point.getTrackId());
        dbWrite.insert(DBSchema.LocationsTable.NAME, null, value);
    }

    public Point getLocation(int id) {
        Point result = null;
        String selectionExam = "id =?";
        String[] selectionArgsExam = new String[]{String.valueOf(id)};
        Cursor cursor = dbRead.query(
                DBSchema.LocationsTable.NAME,
                null, selectionExam, selectionArgsExam,
                null, null, null
        );
        cursor.moveToFirst();
        Location location = new Location("");
        location.setLatitude(cursor.getDouble(cursor.getColumnIndex(DBSchema.LocationsTable.Cols.LATITUDE)));
        location.setLongitude(cursor.getDouble(cursor.getColumnIndex(DBSchema.LocationsTable.Cols.LONGITUDE)));
        result = new Point(
                cursor.getInt(cursor.getColumnIndex(DBSchema.LocationsTable.Cols.ID)),
                cursor.getString(cursor.getColumnIndex(DBSchema.LocationsTable.Cols.NAME)), location);
        cursor.close();
        return result;
    }

    public void upadateLocation(Point point) {
        ContentValues value = new ContentValues();
        value.put(DBSchema.LocationsTable.Cols.NAME, point.getName());
        value.put(DBSchema.LocationsTable.Cols.LATITUDE, String.valueOf(point.getLocation().getLatitude()));
        value.put(DBSchema.LocationsTable.Cols.LONGITUDE, String.valueOf(point.getLocation().getLongitude()));
        dbWrite.update(DBSchema.LocationsTable.NAME, value, "id =?",
                new String[]{String.valueOf(point.getPointId())});

    }

    public void deleteLocation(Point loc) {
        dbWrite.delete(DBSchema.LocationsTable.NAME, "id = ?", new String[]{String.valueOf(loc.getPointId())});
    }

    public void deleteAllLocation() {
        dbWrite.execSQL("delete from " + DBSchema.LocationsTable.NAME);
    }

    public void deleteAllTracks(){
        List<Track> tracks = getAllTracks();
        for(Track track: tracks){
            for(Point point:track.getPoints()){
                deleteLocation(point);
            }
            dbWrite.delete(DBSchema.TracksTable.NAME, "id = ?", new String[]{String.valueOf(track.getId())});
        }
    }
}
