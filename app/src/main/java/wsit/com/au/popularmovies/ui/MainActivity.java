package wsit.com.au.popularmovies.ui;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.NetworkInterface;

import javax.sql.ConnectionEvent;

import wsit.com.au.popularmovies.db.PopularMoviesDBHelper;
import wsit.com.au.popularmovies.ui.fragments.DetailsFragment;
import wsit.com.au.popularmovies.utils.MovieItems;
import wsit.com.au.popularmovies.adapters.MovieItemsAdapter;
import wsit.com.au.popularmovies.utils.PopularMoviesConstants;
import wsit.com.au.popularmovies.R;

public class MainActivity extends AppCompatActivity
{
    public static final String TAG = MainActivity.class.getSimpleName();

    protected GridView mainGridView;
    protected ProgressBar moviesLoading;
    public String JSONDataString;
    private boolean mTwoPane;

    // Array of MovieItems to hold the URL
    MovieItems movieItems[];

    // Custom Adapter for the GridView
    MovieItemsAdapter adapter;

    OrientationEventListener mOrientiantionEventListener;

    // Network check textView
    TextView checkNetwork;




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Test saved state
        if (savedInstanceState != null)
        {
            String testVal = savedInstanceState.getString("KEY_TEST");
            Log.i(TAG, "Test Val is: " + testVal);
        }

        mainGridView = (GridView) findViewById(R.id.mainGridView);
        moviesLoading = (ProgressBar) findViewById(R.id.moviesLoadingProgressBar);
        checkNetwork = (TextView) findViewById(R.id.noLinkTextView);


        if (findViewById(R.id.movie_detail_container) != null)
        {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
            mainGridView.setNumColumns(2);
        }

        // Hide the progress bar
        moviesLoading.setVisibility(View.INVISIBLE);

        // hide the check network textview
        checkNetwork.setVisibility(View.INVISIBLE);

        // Register a listener for checking the screen orientation
        // http://www.informit.com/articles/article.aspx?p=2262133&seqNum=4
        mOrientiantionEventListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int orientation)
            {

                if (orientation == 270)
                {
                    mainGridView.setNumColumns(4);
                }
                else if (orientation == 90)
                {
                    mainGridView.setNumColumns(4);
                }
                else if (orientation == 0)
                {
                    mainGridView.setNumColumns(3);
                }

            }
        };

        if (checkNetworkState() || getSortOrderFromSettings().equals("Favorites"))
        {
            // Check what the settings is set to then get the JSON bases on that.
            sortMovies();
        }
        else
        {
            checkNetwork.setVisibility(View.VISIBLE);
        }





    }


    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState)
    {
        super.onSaveInstanceState(outState, outPersistentState);

        outState.putString("KEY_TEST", "Test Value");

    }

    // Checks what we have the sort setting on then use that URL to get the JSON
    private void sortMovies()
    {
        if (getSortOrderFromSettings().equals("Most Popular"))
        {
            // Sort by most popular

            // Get the JSON Data and print it as a string
            getJSON(PopularMoviesConstants.mostPopularURL + PopularMoviesConstants.API_KEY);
        }
        else if(getSortOrderFromSettings().equals("Highest Rating"))
        {
            // Sort by highest Rating

            // Get the JSON Data and print it as a string
            getJSON(PopularMoviesConstants.highestRatedURL + PopularMoviesConstants.API_KEY);
        }
        else if(getSortOrderFromSettings().equals("Favorites"))
        {
            // Sort by favorites

            // TODO: Write method to query database and sort movies from favorites
            Log.i(TAG, "Sorting movies by favorites");
            showFavorites();

        }
    }

    private void showFavorites()
    {
            // Query database
            PopularMoviesDBHelper dbHelper = new PopularMoviesDBHelper(this);
            SQLiteDatabase database = dbHelper.getWritableDatabase();

        String allColums[] = {
                PopularMoviesDBHelper.COLUMN_ID,
                PopularMoviesDBHelper.COLUMN_TITLE,
                PopularMoviesDBHelper.COLUMN_RELEASE_YEAR,
                PopularMoviesDBHelper.COLUMN_PLOT,
                PopularMoviesDBHelper.COLUMN_RATING,
                PopularMoviesDBHelper.COLUMN_BACKDROP_IMAGE,
                PopularMoviesDBHelper.COLUMN_POSTER_IMAGE};

        Cursor cursor = database.query(PopularMoviesDBHelper.TABLE_FAVORITES, allColums, null, null, null, null, null);

        // Move to the first position
        cursor.moveToFirst();

        // Create the MovieItems object to store the data for the adapter
        final MovieItems movieItems[] = new MovieItems[cursor.getCount()];

        // Cycle through each row
        for (int i = 0; i < cursor.getCount(); i++)
        {

            MovieItems mItem = new MovieItems();


            mItem.setPosterURL(cursor.getString(6)); // Poster URL

            movieItems[i] = mItem;

            cursor.moveToNext();

        }

            // Display data in gridview

        runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                adapter = new MovieItemsAdapter(MainActivity.this, movieItems);
                mainGridView.setAdapter(adapter);

                mainGridView.setOnItemClickListener(null);
            }
        });

        cursor.close();
        database.close();


    }


    // Helper method which gets our sort setting from the SharedPreferences
    public String getSortOrderFromSettings()
    {
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String movieSortOrder = mSharedPreferences.getString(PopularMoviesConstants.KEY_SORT_ORDER, "Most Popular");
        Log.i(TAG, movieSortOrder);

        return movieSortOrder;
    }


    // Get the JSON Data from MovieDB - takes a URL as a parameter
    // Using OKHttp to get the thd data - http://square.github.io/okhttp/

    public void getJSON(String URL)
    {

        // Create an instance of the OKHttp class
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(URL).build();


        // Create a new call and show the progress bar while we load the data
        moviesLoading.setVisibility(View.VISIBLE);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e(TAG, "Error getting URL: " + e);

            }

            @Override
            public void onResponse(Response response) throws IOException {


                if (response.isSuccessful()) {
                    JSONDataString = response.body().string();
                    Log.i(TAG, JSONDataString);

                    // Parse the JSON
                    parseJSON(JSONDataString);

                } else {
                    Log.e(TAG, "Error getting JSON");
                }

            }
        });



    }

    // Parse the JSON String and output the URLs of the images
    private void parseJSON(String JSONData)
    {

        try
        {
            // Create a JSON object using the JSON String data
            JSONObject jsonData = new JSONObject(JSONData);
            // Get the "results" array
            JSONArray JSONResults = jsonData.getJSONArray(PopularMoviesConstants.JSON_RESULTS);

            // Create an array of movieItems which we can load into the custom adapter
            // Make it equal to the size of the JSON Results array
            movieItems = new MovieItems[JSONResults.length()];

            for (int i = 0; i < JSONResults.length(); i++)
            {
                // Create a new MovieItems object of the getter and setter class so we can set the URL we get from the JSON
                MovieItems mMovieItemsGetterSetter = new MovieItems();


                // Now get the data from the JSON
                JSONObject result = JSONResults.getJSONObject(i);
                // Get the poster path
                String posterPathURL = result.getString(PopularMoviesConstants.POSTER_PATH);
                // Get the Title
                String originalTitle = result.getString(PopularMoviesConstants.ORIGINAL_TITLE);
                // Get the overview (Plot Synopsis)
                String plotSynopsis = result.getString(PopularMoviesConstants.PLOT_SYNOPSIS);
                // Get the user rating (Vote Average)
                String voteAverage = result.getString(PopularMoviesConstants.VOTE_AVERAGE);
                // Get the release date
                String releaseDate = result.getString(PopularMoviesConstants.RELEASE_DATE);
                // Get the backdrop poster path
                String backdropPath = result.getString(PopularMoviesConstants.BACKDROP_PATH);
                // Get the movieID
                String movieID = result.getString(PopularMoviesConstants.MOVIE_ID);


                // Set the URL in the getter setter object
                mMovieItemsGetterSetter.setPosterURL(PopularMoviesConstants.BASE_URL + posterPathURL);

                mMovieItemsGetterSetter.setOriginalTitle(originalTitle);
                mMovieItemsGetterSetter.setPlotSynopsis(plotSynopsis);
                mMovieItemsGetterSetter.setUserRating(voteAverage);
                mMovieItemsGetterSetter.setReleaseDate(releaseDate);
                mMovieItemsGetterSetter.setBackDropPath(PopularMoviesConstants.BASE_URL_BACKDROP + backdropPath);
                mMovieItemsGetterSetter.setMovieID(movieID);

                // Store the MovieItems instance in the MovieItems array
                movieItems[i] = mMovieItemsGetterSetter;
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run()
                {
                    // OK now we are done so lets set the data in the gridView
                    // Instantiate an instance of the custom adapter
                    adapter = new MovieItemsAdapter(MainActivity.this, movieItems);
                    mainGridView.setAdapter(adapter);

                    // Once we done setting the adapter hide the progress bar
                    moviesLoading.setVisibility(View.INVISIBLE);


                    // Create an onClick listener for the gridViewItem
                    mainGridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                    {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                        {

                            // Cast the adapter's getItem method Object to a MovieItem
                            MovieItems movieItem = (MovieItems) adapter.getItem(position);
                            // Check if we are on a tablet
                            if (mTwoPane)
                            {
                                // Create a bundle object to store strings for access in the fragment
                                Bundle bundle = new Bundle();
                                // Put the data into the bundle
                                bundle.putString(PopularMoviesConstants.ORIGINAL_TITLE, movieItem.getOriginalTitle());
                                bundle.putString(PopularMoviesConstants.BACKDROP_PATH, movieItem.getBackDropPath());
                                bundle.putString(PopularMoviesConstants.POSTER_PATH, movieItem.getPosterURL());
                                bundle.putString(PopularMoviesConstants.PLOT_SYNOPSIS, movieItem.getPlotSynopsis());
                                bundle.putString(PopularMoviesConstants.VOTE_AVERAGE, movieItem.getUserRating());
                                bundle.putString(PopularMoviesConstants.RELEASE_DATE, movieItem.getReleaseDate());
                                bundle.putString(PopularMoviesConstants.MOVIE_ID, movieItem.getMovieID());

                                // Start the fragment in the fragment
                                DetailsFragment detailsFragment = new DetailsFragment();
                                detailsFragment.setArguments(bundle);
                                getSupportFragmentManager().beginTransaction().replace(R.id.movie_detail_container, detailsFragment).commit();

                            }
                            // If we reach here, then we are not using a Two pane layout
                            else
                            {
                                //startDetailsView(movieItem);

                                // Create a bundle object to store strings for access in the fragment
                                Bundle bundle = new Bundle();
                                // Put the data into the bundle
                                bundle.putString(PopularMoviesConstants.ORIGINAL_TITLE, movieItem.getOriginalTitle());
                                bundle.putString(PopularMoviesConstants.BACKDROP_PATH, movieItem.getBackDropPath());
                                bundle.putString(PopularMoviesConstants.POSTER_PATH, movieItem.getPosterURL());
                                bundle.putString(PopularMoviesConstants.PLOT_SYNOPSIS, movieItem.getPlotSynopsis());
                                bundle.putString(PopularMoviesConstants.VOTE_AVERAGE, movieItem.getUserRating());
                                bundle.putString(PopularMoviesConstants.RELEASE_DATE, movieItem.getReleaseDate());
                                bundle.putString(PopularMoviesConstants.MOVIE_ID, movieItem.getMovieID());

                                // Start the fragment in the fragment
                                DetailsFragment detailsFragment = new DetailsFragment();
                                detailsFragment.setArguments(bundle);
                                // Replace the gridViewFragment with the details view fragment
                                getSupportFragmentManager().beginTransaction().replace(R.id.gridViewFragment, detailsFragment).addToBackStack(null).commit();
                            }

                        }
                    });
                }
            });


        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }

    }

    public void startDetailsView(MovieItems movieItem)
    {
        // Show the progress bar while we
        moviesLoading.setVisibility(View.VISIBLE);
        Intent detailsIntent = new Intent(MainActivity.this, DetailsActivity.class);
        // Add all the data to the intent
        detailsIntent.putExtra(PopularMoviesConstants.ORIGINAL_TITLE, movieItem.getOriginalTitle());
        detailsIntent.putExtra(PopularMoviesConstants.POSTER_PATH, movieItem.getPosterURL());
        detailsIntent.putExtra(PopularMoviesConstants.PLOT_SYNOPSIS, movieItem.getPlotSynopsis());
        detailsIntent.putExtra(PopularMoviesConstants.VOTE_AVERAGE, movieItem.getUserRating());
        detailsIntent.putExtra(PopularMoviesConstants.RELEASE_DATE, movieItem.getReleaseDate());
        detailsIntent.putExtra(PopularMoviesConstants.BACKDROP_PATH, movieItem.getBackDropPath());
        detailsIntent.putExtra(PopularMoviesConstants.MOVIE_ID, movieItem.getMovieID());

        startActivity(detailsIntent);

    }

    @Override
    protected void onResume()
    {
        super.onResume();

        moviesLoading.setVisibility(View.INVISIBLE);

        mOrientiantionEventListener.enable();


        if (checkNetworkState() || getSortOrderFromSettings().equals("Favorites"))
        {
            // Check what the settings is set to then get the JSON bases on that.
            sortMovies();
            checkNetwork.setVisibility(View.INVISIBLE);
        }
        else
        {
            checkNetwork.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mOrientiantionEventListener.disable();
    }

    // Menu options


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        int id = item.getItemId();

        switch (id)
        {
            case R.id.action_settings:
                // Start the settings
                Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;

        }


        return super.onOptionsItemSelected(item);
    }


    // Method to check if we are online
    //http://developer.android.com/training/basics/network-ops/connecting.html#AsyncTask
    public boolean checkNetworkState()
    {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected())
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
