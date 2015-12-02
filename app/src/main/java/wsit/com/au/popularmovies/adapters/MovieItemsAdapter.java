package wsit.com.au.popularmovies.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import wsit.com.au.popularmovies.R;
import wsit.com.au.popularmovies.utils.MovieItems;

/**
 * Created by guyb on 18/11/15.
 */
public class MovieItemsAdapter extends BaseAdapter
{

    public static final String TAG = MovieItemsAdapter.class.getSimpleName();

    // Define our context
    private Context mContext;
    // Create a MovieItems variable which will be used to store the data we input from the constructor
    private MovieItems[] mMovieItems;

    // Constructor
    // We pass in the the data from the items and store movieItems in mMovieItems
    public MovieItemsAdapter(Context context, MovieItems[] movieItems)
    {
        mContext = context;
        mMovieItems = movieItems;
    }

    @Override
    public int getCount()
    {
        return mMovieItems.length;
    }

    @Override
    public Object getItem(int position)
    {
        return mMovieItems[position];
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    // Our class which holds the views for the image and text
    private static class ViewHolder
    {
        ImageView movieImage;
        // We can add other TextViews in here in the future if we want to be able to display more items in the main GridView
        // E.G.
        // TextView movieTitle;
        // TextView moviePlot;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // Create a viewHolder variable
        ViewHolder holder;

        if (convertView == null)
        {
            // New setup
            convertView = LayoutInflater.from(mContext).inflate(R.layout.grid_view_movies, null);

            // Instantiate a new instance of the view holder
            holder = new ViewHolder();
            holder.movieImage = (ImageView) convertView.findViewById(R.id.movieThumbImageView);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        MovieItems Items = mMovieItems[position];

        // Now set the data
        // If the URL is invalid, then catch the exception
        try
        {
            Picasso.with(mContext).load(Items.getPosterURL()).into(holder.movieImage);
        }
        catch (Exception e)
        {
            Log.e(TAG, "Error getting URL " + e.getMessage());
        }




        return convertView;
    }
}
