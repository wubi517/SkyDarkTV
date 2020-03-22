package com.it_tech613.skydark.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EpisodeInfoModel {
    @Expose
    @SerializedName("duration")
    private String duration;
    @Expose
    @SerializedName("name")
    private String name;
    @Expose
    @SerializedName("rating")
    private String  rating = "";
    @Expose
    @SerializedName("releasedate")
    private String releasedate;
    @Expose
    @SerializedName("plot")
    private String plot;
    @Expose
    @SerializedName("movie_image")
    private String movie_image;

    public String getDuration() {
        return duration;
    }


    public String getName() {
        return name;
    }

    public float  getRating() {
        if(rating==null || rating.isEmpty()){
            return 0;
        }else {
            return Float.parseFloat(rating);
        }

    }

    public String getReleasedate() {
        return releasedate;
    }

    public String getPlot() {
        return plot;
    }

    public String getMovie_image() {
        return movie_image;
    }
}
