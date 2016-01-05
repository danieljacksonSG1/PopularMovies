package wsit.com.au.popularmovies.ui.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import wsit.com.au.popularmovies.R;


/**
 * Created by guyb on 4/01/16.
 */
public class MoviesFragment extends Fragment
{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        return inflater.inflate(R.layout.grid_view_movies_fragment, container, false);

    }



}
