package wsit.com.au.popularmovies.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.concurrent.TransferQueue;

import wsit.com.au.popularmovies.adapters.TrailerItemsAdapter;
import wsit.com.au.popularmovies.utils.MovieItems;
import wsit.com.au.popularmovies.utils.PopularMoviesConstants;
import wsit.com.au.popularmovies.R;
import wsit.com.au.popularmovies.utils.TrailerItems;

public class DetailsActivity extends Activity
{

    // Log Tag
    public static final String TAG = DetailsActivity.class.getSimpleName();

    ImageView mBackdropImage;
    TextView mOriginalTitle;
    TextView mReleaseDate;
    TextView mOverview;
    TextView mVoteAverage;



    String movieTitle;
    String movieReleaseDate;
    String movieOverview;
    String movieVoteAverage;
    String backdropURL;
    String movieID;

    ListView trailerListView;

    public String trailerJSONString;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Find the XML IDs
        mBackdropImage = (ImageView) findViewById(R.id.detailsImageView);
        mOriginalTitle = (TextView) findViewById(R.id.originalTitle);
        mReleaseDate = (TextView) findViewById(R.id.releaseDate);
        mOverview = (TextView) findViewById(R.id.overViewTextView);
        mVoteAverage = (TextView) findViewById(R.id.voteAverageTextView);
        trailerListView = (ListView) findViewById(R.id.trailersListView);



        // Create an intent to get the data from MainActivity
        Intent intent = getIntent();

        // Get the Intent data from MainActivity
        getIntentData(intent);

        // Now set the Intent data in the UI
        setIntentData();


    }

    // Method to extract the movie release data and display as a year only
    public String extractYear(String date)
    {
        return date.substring(0, 4);
    }


    // Method to get the intent data
    public void getIntentData(Intent intent)
    {
        // Get the movie title
        movieTitle = intent.getStringExtra(PopularMoviesConstants.ORIGINAL_TITLE);
        // Get the release date
        movieReleaseDate = intent.getStringExtra(PopularMoviesConstants.RELEASE_DATE);
        // Get the plot
        movieOverview = intent.getStringExtra(PopularMoviesConstants.PLOT_SYNOPSIS);
        // Get the vote average
        movieVoteAverage = intent.getStringExtra(PopularMoviesConstants.VOTE_AVERAGE);
        // Get the backdrop URL
        backdropURL = intent.getStringExtra(PopularMoviesConstants.BACKDROP_PATH);
        // Get the movieID
        movieID = intent.getStringExtra(PopularMoviesConstants.MOVIE_ID);

    }

    // Method to set the intent data
    public void setIntentData()
    {

        // Load the backdrop URL into the imageView using Picasso
        Picasso.with(DetailsActivity.this)
                .load(backdropURL)

                // TODO: Add place holder and error drawables
                .placeholder(R.drawable.terminator) // Placeholder
                .error(R.drawable.terminator) // Handle the error
                .into(mBackdropImage);


        // Set the TextView fields and catch null values
        try
        {
            // Set the TextView fields
            mOriginalTitle.setText(movieTitle);
            mReleaseDate.setText(extractYear(movieReleaseDate));
            mOverview.setText(movieOverview);
            mVoteAverage.setText(movieVoteAverage + "/10");
        }
        catch(NullPointerException e)
        {
            Log.e(TAG, "Error setting data in textView: " + e);
        }

        // Get the trailer YouTube links
        getTrailers(movieID);

        // Get the Reviews in JSON Format
        // getReviews(movieID);



    }


    public void getTrailers(String id)
    {
        String QUERY_URL = "http://api.themoviedb.org/3/movie/" + id + "/videos?&api_key=" + PopularMoviesConstants.API_KEY;

        // Create an instance of the OKHttp class
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(QUERY_URL).build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e(TAG, "Error getting URL: " + e);

            }

            @Override
            public void onResponse(Response response) throws IOException
            {

                if (response.isSuccessful()) {
                    // Here is the trailers JSON

                     trailerJSONString = response.body().string();
                     Log.d(TAG, trailerJSONString);
                    parseJSONTrailers(trailerJSONString);

                } else {
                    Log.e(TAG, "Error getting trailers JSON");
                }

            }
        });


    }

    // Method to parse the JSON String retrieved by getTrailers()
    // Takes the JSON String as an argument
    private void parseJSONTrailers(String JSONData)
    {
        // Try to parse the JSON into a JSONObject, catch JSON Exceptions
        try
        {
            JSONObject jsonData = new JSONObject(JSONData);
            // Get the "results array"
            JSONArray jsonArrayData = jsonData.getJSONArray(PopularMoviesConstants.JSON_RESULTS);

            // Create an array of trailerItems for storing trailer objects
            final TrailerItems trailerItems[] = new TrailerItems[jsonArrayData.length()];

            // Loop through the JSON Array
            for (int i = 0; i < jsonArrayData.length(); i++)
            {
                // Create a trailerItem object of the getter and setter class
                TrailerItems mTrailerItem = new TrailerItems();

                // Get each object from the array
                JSONObject trailerItemJSON = jsonArrayData.getJSONObject(i);

                // Extract the string values from the object
                String youTubeKey = trailerItemJSON.getString(PopularMoviesConstants.YOUTUBE_KEY);
                String trailerName = trailerItemJSON.getString(PopularMoviesConstants.TRAILER_NAME);

                Log.d(TAG, "Youtube Key is: " + youTubeKey);
                Log.d(TAG, "Trailer Name is: " + trailerName);

                // Store the strings using the setter method
                mTrailerItem.setTrailerKey(youTubeKey);
                mTrailerItem.setTrailerName(trailerName);

                // Put the object into the array of objects
                trailerItems[i] = mTrailerItem;

            }

            runOnUiThread(new Runnable() {
                @Override
                public void run()
                {
                    final TrailerItemsAdapter adapter = new TrailerItemsAdapter(DetailsActivity.this, trailerItems);
                    trailerListView.setAdapter(adapter);

                    trailerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                        {

                            TrailerItems trailerItem = (TrailerItems) adapter.getItem(position);
                            String youtubeKey = trailerItem.getTrailerKey();

                            openVideo(youtubeKey);


                        }
                    });
                }
            });


        }
        catch (JSONException e)
        {
            Log.e(TAG, "Error parsing JSON " + e);
            e.printStackTrace();
        }
    }

    // Method to open the YouTube Video
    private void openVideo(String youTubeKey)
    {
        String fullURL = PopularMoviesConstants.YOUTUBE_BASE_URL + youTubeKey;
        Log.i(TAG, "YouTube URL is: " + fullURL);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(fullURL));
        startActivity(intent);

    }

    public void getReviews(String id)
    {
        String QUERY_URL = "http://api.themoviedb.org/3/movie/" + id + "/reviews?&api_key=" + PopularMoviesConstants.API_KEY;

        // Create an instance of the OKHttp class
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(QUERY_URL).build();


        client.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onFailure(Request request, IOException e)
            {
                Log.e(TAG, "Error getting URL: " + e);

            }

            @Override
            public void onResponse(Response response) throws IOException
            {

                if (response.isSuccessful())
                {
                    // Here is the trailers JSON
                    Log.i(TAG, "Reviews: " + response.body().string());
                    // TODO: Parse reviews JSON

                } else {
                    Log.e(TAG, "Error getting trailers JSON");
                }

            }
        });

    }
}
