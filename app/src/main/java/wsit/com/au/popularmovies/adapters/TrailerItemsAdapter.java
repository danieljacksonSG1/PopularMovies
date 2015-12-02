package wsit.com.au.popularmovies.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import wsit.com.au.popularmovies.R;
import wsit.com.au.popularmovies.utils.TrailerItems;

/**
 * Created by guyb on 30/11/15.
 */
public class TrailerItemsAdapter extends BaseAdapter
{

    public static final String TAG = TrailerItemsAdapter.class.getSimpleName();
    private Context mContext;
    private TrailerItems[] mTrailerItems;

    public TrailerItemsAdapter(Context context, TrailerItems[] trailerItems)
    {
        mContext = context;
        mTrailerItems = trailerItems;


    }

    @Override
    public int getCount()
    {
        return mTrailerItems.length;
    }

    @Override
    public Object getItem(int position)
    {
        return mTrailerItems[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private static class ViewHolder
    {
        ImageView playButton;
        TextView trailerName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;

        // New setup
        if(convertView == null)
        {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_trailers, null);

            // Instantiate a new instance of the view holder
            holder = new ViewHolder();

            holder.trailerName = (TextView) convertView.findViewById(R.id.trailerName_TextView);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        TrailerItems Items = mTrailerItems[position];

        // Set the data
        holder.trailerName.setText(Items.getTrailerName());


        return convertView;
    }
}
