package wsit.com.au.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import wsit.com.au.popularmovies.R;
import wsit.com.au.popularmovies.utils.MovieItems;
import wsit.com.au.popularmovies.utils.ReviewItems;

/**
 * Created by guyb on 3/12/15.
 */
public class ReviewItemsAdapter extends BaseAdapter
{

    public static final String TAG = ReviewItemsAdapter.class.getSimpleName();
    // Define our context
    private Context mContext;

    private ReviewItems[] mReviewItems;

    public ReviewItemsAdapter(Context context, ReviewItems[] reviewItems)
    {
        mContext = context;
        mReviewItems = reviewItems;

    }


    @Override
    public int getCount() {
        return mReviewItems.length;
    }

    @Override
    public Object getItem(int position) {
        return mReviewItems[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private static class ViewHolder
    {
        TextView authorName;
        TextView reviewContent;
        TextView reviewURL;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;

        if (convertView == null)
        {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_reviews, null);

            // Instantiate a new instance of the view holder
            holder = new ViewHolder();
            holder.authorName = (TextView) convertView.findViewById(R.id.reviewAuthorTextView);
            holder.reviewURL = (TextView) convertView.findViewById(R.id.reviewURLTextView);
            holder.reviewContent = (TextView) convertView.findViewById(R.id.reviewContentTextView);

            convertView.setTag(holder);

        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        final ReviewItems Item = mReviewItems[position];

        // Set the data

        try
        {
            holder.authorName.setText(Item.getReviewAuthor());
            holder.reviewURL.setText(Item.getReviewURL());
            holder.reviewContent.setText(Item.getReviewContent());
        }
        catch (NullPointerException e)
        {
            Log.i(TAG, "Error setting value in SetText: " + e.getMessage());
;       }

        holder.reviewURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Item.getReviewURL()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                mContext.getApplicationContext().startActivity(intent);
            }
        });


        return convertView;
    }
}
