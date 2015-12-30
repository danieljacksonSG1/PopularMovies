package wsit.com.au.popularmovies.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by guyb on 29/12/15.
 */
public class PopularMoviesDBHelper extends SQLiteOpenHelper
{

    // Debug tag
    public static final String TAG = PopularMoviesDBHelper.class.getSimpleName();

    // DB
    public static final String DATABASE_NAME = "movies.db";
    public static final int DATABASE_VERSION = 3;

    // Table
    public static final String TABLE_FAVORITES = "favorites";

    // Schema
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_RELEASE_YEAR = "release_year";
    public static final String COLUMN_PLOT = "plot";
    public static final String COLUMN_RATING = "rating";
    public static final String COLUMN_BACKDROP_IMAGE = "movie_image";
    public static final String COLUMN_POSTER_IMAGE = "movie_grid_image";

    // DB Creation Statement
    public static final String DATABASE_CREATE = "CREATE TABLE "
            + TABLE_FAVORITES
            + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_TITLE + " TEXT NOT NULL, "
            + COLUMN_RELEASE_YEAR + " TEXT NOT NULL,"
            + COLUMN_PLOT + " TEXT NOT NULL,"
            + COLUMN_RATING + " TEXT NOT NULL,"
            + COLUMN_BACKDROP_IMAGE + " TEXT,"
            + COLUMN_POSTER_IMAGE + " TEXT)";


    public PopularMoviesDBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        sqLiteDatabase.execSQL(DATABASE_CREATE);
        Log.i(TAG, "SQL: " + DATABASE_CREATE);
    }



    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion)
    {
        Log.v(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
        onCreate(sqLiteDatabase);
    }
}
