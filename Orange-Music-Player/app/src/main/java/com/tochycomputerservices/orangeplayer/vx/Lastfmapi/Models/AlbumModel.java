package com.tochycomputerservices.orangeplayer.vx.Lastfmapi.Models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by TOCHY on 12/01/2021..
 */

public class AlbumModel {

    @SerializedName("album")
    public Album album;

    public static class Image {
        @SerializedName("#text")
        public String url;
        @SerializedName("size")
        public String size;
    }

    public static class Album {
        @SerializedName("name")
        public String name;
        @SerializedName("artist")
        public String artist;
        @SerializedName("mbid")
        public String mbid;
        @SerializedName("url")
        public String url;
        @SerializedName("image")
        public ArrayList<Image> image;
    }
}
