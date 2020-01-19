package ru.job4j.tourist.dbutils;

import android.content.Context;
import android.graphics.Movie;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import ru.job4j.tourist.model.Point;
import ru.job4j.tourist.model.Track;

public class MainModel {
    private static TracksDataBase database;
    private Context context;
    private List<Track> tracks;

    public MainModel(Context context) {
        this.context = context;
        database = TracksDataBase.detInstance(context);

    }



    public List<Track> getAllTracks() {
        List<Track> result = new ArrayList<>();

        GetAllTracksTask gt = new GetAllTracksTask();
        try {
            result = gt.execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }


    public Track getTrackByID(int trackId) {
        try {
            return new GetTrackTask().execute(trackId).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteAllTracks() {
        new DeleteAllTracksTask().execute();
    }

    public long insertTrack(Track track) {
        try {
            return new InsertTrackTask().execute(track).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void updateTrack(final Track track) {

        UpdateTrackTask st = new UpdateTrackTask();
        st.execute(track);
    }

    /*public void insertPoint(Point point) {
        new InsertPointTask().execute(point);
    }*/

    public void deleteTrack(Track track) {
        new DeleteTrackTask().execute(track);
    }

    public  static class UpdateTrackTask extends AsyncTask<Track, Void, Void> {
        @Override
        protected Void doInBackground(Track... tracks) {
            database
                    .tracksDao()
                    .updateTrack(tracks[0]);
         return null;
        }

    }

    public static class GetAllTracksTask extends AsyncTask<Void, Void, List<Track>> {

        @Override
        protected List<Track> doInBackground(Void... voids) {
            List<Track> tracks = database
                    .tracksDao()
                    .getAllTracks();
            return tracks;
        }

        @Override
        protected void onPostExecute(final List<Track> tracks) {
            super.onPostExecute(tracks);
        }
    }

    /*private static class InsertPointTask extends AsyncTask<Point, Void, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.i("my_gps", "inserted");
        }

        @Override
        protected Void doInBackground(Point... points) {
            if (points != null && points.length > 0) {
                database.tracksDao().insertPoint(points[0]);
            }
            return null;
        }
    }*/

    private class GetTrackTask extends AsyncTask<Integer, Void, Track> {
        @Override
        protected Track doInBackground(Integer... integers) {
            if (integers != null && integers.length > 0) {
                return database.tracksDao().getTrackById(integers[0]);
            }
            return null;
        }
    }

    private static class DeleteAllTracksTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... integers) {
            database.tracksDao().deletAllTracks();
            return null;
        }
    }

    private static class InsertTrackTask extends AsyncTask<Track, Void, Long> {

        @Override
        protected Long doInBackground(Track... tracks) {
            if (tracks != null && tracks.length > 0) {
                return database.tracksDao().insertTrack(tracks[0]);
            }
            return null;
        }
    }

    private static class DeleteTrackTask extends AsyncTask<Track, Void, Void> {

        @Override
        protected Void doInBackground(Track... tracks) {
            if (tracks != null && tracks.length > 0) {
                database.tracksDao().deleteTrack(tracks[0]);
            }
            return null;
        }
    }


}
