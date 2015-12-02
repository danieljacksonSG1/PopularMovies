package wsit.com.au.popularmovies.utils;

/**
 * Created by guyb on 17/11/15.
 */
public class PopularMoviesConstants
{
        // Our API Key for MovieDB
        public static final String API_KEY = "4a0ef3c693045b63c407d7f5b520f647";

        // JSON Keys, also used as keys for the intent extras
        public static final String JSON_RESULTS = "results";
        public static final String POSTER_PATH = "poster_path";
        public static final String ORIGINAL_TITLE = "original_title";
        public static final String PLOT_SYNOPSIS = "overview";
        public static final String VOTE_AVERAGE = "vote_average";
        public static final String RELEASE_DATE = "release_date";
        public static final String BACKDROP_PATH = "backdrop_path";
        public static final String MOVIE_ID = "id";
        public static final String YOUTUBE_KEY = "key";
        public static final String TRAILER_NAME = "name";

        // API Query URLs
        public static final String mostPopularURL = "http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=";
        public static final String highestRatedURL = "http://api.themoviedb.org/3/discover/movie?sort_by=vote_average.desc&api_key=";

        // Base URLs
        // Base URL;
        public static final String BASE_URL = "http://image.tmdb.org/t/p/w185";
        public static final String BASE_URL_BACKDROP = "http://image.tmdb.org/t/p/w500";

        // Settings
        public static final String KEY_SORT_ORDER = "KEY_SORT_ORDER";

        // YouTube Base URL
        public static final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";


}
