package wsit.com.au.popularmovies;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailsActivity extends Activity
{

    // Log Tag
    public static final String TAG = DetailsActivity.class.getSimpleName();

    ImageView mBackdropImage;
    TextView mOriginalTitle;
    TextView mReleaseDate;
    TextView mOverview;
    TextView mVoteAverage;
    RatingBar mRatingBar;


    String movieTitle;
    String movieReleaseDate;
    String movieOverview;
    String movieVoteAverage;
    String backdropURL;

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
        mRatingBar = (RatingBar) findViewById(R.id.detailsRatingBar);


        // Set the rating bar fill and empty colours.
        setRatingBarColours();



        // Create an intent to get the data from MainActivity
        Intent intent = getIntent();

        // Get the Intent data
        getIntentData(intent);

        // Now set the Intent data in the UI
        setIntentData();



    }

    // Method to set the custom colours of the rating bar
    private void setRatingBarColours() {
        // Set the rating bar colours
        LayerDrawable stars = (LayerDrawable) mRatingBar.getProgressDrawable();
        stars.getDrawable(0).setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(2).setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
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
    }

    // Method to set the intent data
    public void setIntentData()
    {
        // Load the backdrop URL into the imageView using Picasso
        Picasso.with(DetailsActivity.this).load(backdropURL).into(mBackdropImage);

        // Set the vote average in the rating bar - The vote average is out of 10 so divide by 2 then set that in a 5 star rating bar
        mRatingBar.setRating(Float.parseFloat(movieVoteAverage) / 2);

        // Set the TextView fields
        mOriginalTitle.setText(movieTitle);
        mReleaseDate.setText(movieReleaseDate);
        mOverview.setText(movieOverview);
        mVoteAverage.setText(movieVoteAverage);


    }
}
