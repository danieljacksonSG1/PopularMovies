package wsit.com.au.popularmovies;

/**
 * Created by guyb on 17/11/15.
 */
public class PopularMoviesConstants
{
        // Our API Key for MovieDB
        public static final String API_KEY = "ADD API KEY HERE";

        // JSON Keys, also used as keys for the intent extras
        public static final String JSON_RESULTS = "results";
        public static final String POSTER_PATH = "poster_path";
        public static final String ORIGINAL_TITLE = "original_title";
        public static final String PLOT_SYNOPSIS = "overview";
        public static final String VOTE_AVERAGE = "vote_average";
        public static final String RELEASE_DATE = "release_date";
        public static final String BACKDROP_PATH = "backdrop_path";

        // API Query URLs
        public static final String mostPopularURL = "http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=";
        public static final String highestRatedURL = "http://api.themoviedb.org/3/discover/movie?sort_by=vote_average.desc&api_key=";

        // Base URLs
        // Base URL;
        public static final String BASE_URL = "http://image.tmdb.org/t/p/w185";
        public static final String BASE_URL_BACKDROP = "http://image.tmdb.org/t/p/w500";

        // Settings
        public static final String KEY_SORT_ORDER = "KEY_SORT_ORDER";


}
