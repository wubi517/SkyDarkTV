package com.it_tech613.skydark.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MovieInfoModel implements Serializable {
    @SerializedName("movie_image")
    private String movie_img = "";
    @SerializedName("youtube_trailer")
    private String youtube = "";
    @SerializedName("genre")
    private String genre = "";
    @SerializedName("plot")
    private String plot = "";
    @SerializedName("cast")
    private String cast = "";
    @SerializedName("director")
    private String director = "";
    @SerializedName("duration")
    private String duration = "";
    @SerializedName("actors")
    private String actors = "";
    @SerializedName("age")
    private String age = "";
    @SerializedName("description")
    private String description = "";
    @SerializedName("country")
    private String country = "";
    @SerializedName("releasedate")
    private String releasedate="";
    @SerializedName("rating")
    private double rating=0.0;
    @SerializedName("backdrop_path")
    private List<String> backdrop_path = new ArrayList<>();

    public String getMovie_img() {
        return movie_img;
    }

    public void setMovie_img(String movie_img) {
        this.movie_img = movie_img;
    }

    public String getYoutube() {
        return youtube;
    }

    public void setYoutube(String youtube) {
        this.youtube = youtube;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getCast() {
        return cast;
    }

    public void setCast(String cast) {
        this.cast = cast;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public List<String> getBackdrop_path() {
        return backdrop_path;
    }

    public void setBackdrop_path(List<String> backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public String getReleasedate() {
        return releasedate;
    }

    public void setReleasedate(String releasedate) {
        this.releasedate = releasedate;
    }
}
