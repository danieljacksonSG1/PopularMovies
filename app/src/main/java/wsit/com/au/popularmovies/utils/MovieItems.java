package wsit.com.au.popularmovies.utils;

/**
 * Created by guyb on 17/11/15.
 */
public class MovieItems
{
    public String originalTitle;
    public String posterURL;
    public String plotSynopsis;
    public String userRating;
    public String releaseDate;
    public String backDropPath;

    public String getBackDropPath() {
        return backDropPath;
    }

    public void setBackDropPath(String backDropPath) {
        this.backDropPath = backDropPath;
    }


    public String getOriginalTitle()
    {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle)
    {
        this.originalTitle = originalTitle;
    }

    public String getPosterURL()
    {
        return posterURL;
    }

    public void setPosterURL(String posterURL)
    {
        this.posterURL = posterURL;
    }

    public String getPlotSynopsis()
    {
        return plotSynopsis;
    }

    public void setPlotSynopsis(String plotSynopsis)
    {
        this.plotSynopsis = plotSynopsis;
    }

    public String getUserRating()
    {
        return userRating;
    }

    public void setUserRating(String userRating)
    {
        this.userRating = userRating;
    }

    public String getReleaseDate()
    {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate)
    {
        this.releaseDate = releaseDate;
    }


}
