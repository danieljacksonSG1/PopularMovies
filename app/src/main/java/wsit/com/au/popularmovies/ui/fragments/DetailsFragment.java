package wsit.com.au.popularmovies.ui.fragments;


import android.app.VoiceInteractor;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
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

import java.io.IOException;

import wsit.com.au.popularmovies.R;
import wsit.com.au.popularmovies.adapters.ReviewItemsAdapter;
import wsit.com.au.popularmovies.adapters.TrailerItemsAdapter;
import wsit.com.au.popularmovies.db.PopularMoviesDBHelper;
import wsit.com.au.popularmovies.utils.PopularMoviesConstants;
import wsit.com.au.popularmovies.utils.ReviewItems;
import wsit.com.au.popularmovies.utils.TrailerItems;

/**
 * Created by guyb on 5/01/16.
 */
public class DetailsFragment extends Fragment
{
    ImageView mBackdropImage;
    TextView mOriginalTitle;
    TextView mReleaseDate;
    TextView mOverview;
    TextView mVoteAverage;
    TextView reviewsTextView;

    String movieTitle;
    String movieReleaseDate;
    String movieOverview;
    String movieVoteAverage;
    String backdropURL;
    String movieID;
    String posterURL;

    ListView trailerListView;
    ListView reviewsListView;

    // Progessbars for the listviews
    ProgressBar trailersProgress;
    ProgressBar reviewsProgress;

    // Add to favorites checkbox
    CheckBox favorites;

    PopularMoviesDBHelper dbHelper;
    SQLiteDatabase database;

    public String trailerJSONString;

    public static final String TAG = DetailsFragment.class.getSimpleName();




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Bundle bundle = getArguments();


        // Get the data from the MainActivity Bundle
        getBundleData(bundle);

        View rootView = inflater.inflate(R.layout.details_view_movies_fragment, container, false);

        // Find the XML IDs
        mBackdropImage = (ImageView) rootView.findViewById(R.id.detailsImageView);
        mOriginalTitle = (TextView) rootView.findViewById(R.id.originalTitle);
        mReleaseDate = (TextView) rootView.findViewById(R.id.releaseDate);
        mOverview = (TextView) rootView.findViewById(R.id.overViewTextView);
        mVoteAverage = (TextView) rootView.findViewById(R.id.voteAverageTextView);
        trailerListView = (ListView) rootView.findViewById(R.id.trailersListView);
        reviewsListView = (ListView) rootView.findViewById(R.id.reviewsListView);
        trailersProgress = (ProgressBar) rootView.findViewById(R.id.trailersProgressBar);
        reviewsProgress = (ProgressBar) rootView.findViewById(R.id.reviewsProgressBar);
        favorites = (CheckBox) rootView.findViewById(R.id.favoritesCheckBox);
        reviewsTextView = (TextView) rootView.findViewById(R.id.reviewsTitleTextView);

        // Hide the listViews progress bars initially
        trailersProgress.setVisibility(View.INVISIBLE);
        reviewsProgress.setVisibility(View.INVISIBLE);

        setBundleData();

        // Check if movie is in favorties DB
        queryFavoritesDB();

        favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                // If the checkbox was not click previously, then it will be now so we we want to add it to the database
                if (favorites.isChecked())
                {
                    // Movie checkbox not check
                    // Add to favorites
                    addMovieToFavorites();

                }
                // If it was checked before, now it is not so this will be true
                else if(!favorites.isChecked())
                {
                    removeMovieFromFavorites();
                }

            }

        });


        return rootView;
    }

    private void removeMovieFromFavorites()
    {
        Log.i(TAG, "Removing movie from favorites");
        Toast.makeText(getActivity(), "Removing movie from favorites", Toast.LENGTH_LONG).show();

        // Setup access to the db
        dbHelper = new PopularMoviesDBHelper(getActivity());
        database = dbHelper.getWritableDatabase();

        // Delete from Favorites where movieTitle = the movieTitle
        database.delete(PopularMoviesDBHelper.TABLE_FAVORITES, PopularMoviesDBHelper.COLUMN_TITLE + "='" + movieTitle + "'", null);


        database.close();




    }

    private void addMovieToFavorites() {

        if (checkIfmovieIsFavorite()) {
            Toast.makeText(getActivity(), "Movie already in favorites", Toast.LENGTH_LONG).show();
        } else {
            Log.i(TAG, "Adding movie to favorites");
            Toast.makeText(getActivity(), "Added to favorites", Toast.LENGTH_LONG).show();
            // Setup access to the db
            dbHelper = new PopularMoviesDBHelper(getActivity());
            database = dbHelper.getWritableDatabase();

            final ContentValues values = new ContentValues();
            values.put(PopularMoviesDBHelper.COLUMN_TITLE, movieTitle);
            values.put(PopularMoviesDBHelper.COLUMN_RELEASE_YEAR, movieReleaseDate);
            values.put(PopularMoviesDBHelper.COLUMN_PLOT, movieOverview);
            values.put(PopularMoviesDBHelper.COLUMN_RATING, movieVoteAverage);
            values.put(PopularMoviesDBHelper.COLUMN_POSTER_IMAGE, posterURL);
            values.put(PopularMoviesDBHelper.COLUMN_BACKDROP_IMAGE, backdropURL);


            database.insert(PopularMoviesDBHelper.TABLE_FAVORITES, null, values);

            database.close();
        }
    }


    public boolean checkIfmovieIsFavorite()
    {

        boolean found = false;
        Log.i(TAG, "Checking if movie is in favorites");

        // Setup access to the db
        dbHelper = new PopularMoviesDBHelper(getActivity());
        database = dbHelper.getWritableDatabase();

        String allColums[] = {
                PopularMoviesDBHelper.COLUMN_ID,
                PopularMoviesDBHelper.COLUMN_TITLE,
                PopularMoviesDBHelper.COLUMN_RELEASE_YEAR,
                PopularMoviesDBHelper.COLUMN_PLOT,
                PopularMoviesDBHelper.COLUMN_RATING };

        Cursor cursor = database.query(PopularMoviesDBHelper.TABLE_FAVORITES, allColums, null, null, null, null, null);

        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++)
        {

            if (cursor.getString(1).equals(movieTitle))
            {
                found = true;
            }

            cursor.moveToNext();


        }

        if (found)
        {
            cursor.close();
            database.close();
            return true;
        }
        else
        {
            cursor.close();
            database.close();
            return false;
        }



    }

    private void queryFavoritesDB()
    {
        Log.i(TAG, "Checking if movie is in movieDB");
        // If it is then set the checkbox to true
        if (checkIfmovieIsFavorite())
        {
            favorites.setChecked(true);
        }
        else
        {
            favorites.setChecked(false);
        }


    }

    // Method to extract the movie release data and display as a year only
    public String extractYear(String date)
    {
        return date.substring(0, 4);
    }

    public void getBundleData(Bundle bundle)
    {

        movieTitle = bundle.getString(PopularMoviesConstants.ORIGINAL_TITLE);
        movieReleaseDate = bundle.getString(PopularMoviesConstants.RELEASE_DATE);
        movieOverview = bundle.getString(PopularMoviesConstants.PLOT_SYNOPSIS);
        movieVoteAverage = bundle.getString(PopularMoviesConstants.VOTE_AVERAGE);
        backdropURL = bundle.getString(PopularMoviesConstants.BACKDROP_PATH);
        movieID = bundle.getString(PopularMoviesConstants.MOVIE_ID);
        posterURL = bundle.getString(PopularMoviesConstants.POSTER_PATH);
    }

    public void setBundleData()
    {
        // Load the backdrop URL into the imageView using Picasso
        Picasso.with(getActivity())
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
        getReviews(movieID);
    }

    public void getTrailers(String id)
    {
        // Start showing the trailers progress while we query the API
        trailersProgress.setVisibility(View.VISIBLE);

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
            public void onResponse(Response response) throws IOException {

                if (response.isSuccessful()) {
                    // Here is the trailers JSON

                    trailerJSONString = response.body().string();

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



            getActivity().runOnUiThread(new Runnable()
            {

                @Override
                public void run() {

                    // Now hide the trailers Progress
                    trailersProgress.setVisibility(View.INVISIBLE);
                    final TrailerItemsAdapter adapter = new TrailerItemsAdapter(getActivity(), trailerItems);
                    trailerListView.setAdapter(adapter);

                    trailerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

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
        //Log.i(TAG, "YouTube URL is: " + fullURL);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(fullURL));
        startActivity(intent);

    }

    // Gets the movie review in JSON format
    // Takes the movie ID as an argument
    public void getReviews(String id)
    {
        // Show the reviews Progress
        reviewsProgress.setVisibility(View.VISIBLE);

        String QUERY_URL = "http://api.themoviedb.org/3/movie/" + id + "/reviews?&api_key=" + PopularMoviesConstants.API_KEY;
        //Log.i(TAG, "Review URL is: " + QUERY_URL);

        // Create an instance of the OKHttp class
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder().url(QUERY_URL).build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e(TAG, "Error getting URL: " + e);

            }

            @Override
            public void onResponse(Response response) throws IOException {

                if (response.isSuccessful()) {
                    // Here is the trailers JSON
                    String reviewsJSON = response.body().string();

                    parseJSONReviews(reviewsJSON);

                } else {
                    Log.e(TAG, "Error getting trailers JSON");
                }

            }
        });

    }

    // Parse the JSON reviews
    private void parseJSONReviews(String JSONData)
    {


        try
        {
            // Create the string to JSON
            JSONObject reviewsJSON = new JSONObject(JSONData);
            // Load the array into an object
            JSONArray reviewsArray = reviewsJSON.getJSONArray(PopularMoviesConstants.JSON_RESULTS);


            // Check if we have any reviews by examing the length of the JSON Array
            // If it's 0 then hide the Reviews TextView
            if (reviewsArray.length() == 0)
            {
                Log.i(TAG, "No reviews for this one");
                // Hide the Reviews set text
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        reviewsTextView.setVisibility(View.GONE);
                    }
                });



            }

            final ReviewItems mReviewItems[] = new ReviewItems[reviewsArray.length()];



            // Loop through the array
            for (int i = 0; i < reviewsArray.length(); i++)
            {
                // Create an instance to store the JSON Data
                ReviewItems items = new ReviewItems();

                // Get the review into an object
                JSONObject JSONReviewItem = reviewsArray.getJSONObject(i);

                String author = JSONReviewItem.getString(PopularMoviesConstants.REVIEW_AUTHOR);
                String content = JSONReviewItem.getString(PopularMoviesConstants.REVIEW_CONTENT);
                String reviewURL = JSONReviewItem.getString(PopularMoviesConstants.REVIEW_URL);

                items.setReviewAuthor(author);
                items.setReviewContent(content);
                items.setReviewURL(reviewURL);

                mReviewItems[i] = items;

                //Log.i(TAG, author);
                //Log.i(TAG, reviewURL);
                //Log.i(TAG, content);

            }



            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    // Now hide the reviews Progress
                    reviewsProgress.setVisibility(View.GONE);

                    ReviewItemsAdapter reviewItemsAdapter = new ReviewItemsAdapter(getActivity(), mReviewItems);
                    reviewsListView.setAdapter(reviewItemsAdapter);
                }
            });




        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }


}
