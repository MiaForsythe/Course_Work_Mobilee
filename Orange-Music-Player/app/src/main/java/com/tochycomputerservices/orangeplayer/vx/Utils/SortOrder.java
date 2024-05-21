package com.tochycomputerservices.orangeplayer.vx.Utils;

/*
*/


import android.provider.MediaStore;

import com.tochycomputerservices.orangeplayer.vx.Database.DataBaseHelper;

/**
 * Holds all of the sort orders for each list type.
 *
 * @author Andrew Neal (andrewdneal@gmail.com)
 */
public final class SortOrder {

    /**
     * This class is never instantiated
     */
    private SortOrder() {
    }

    /**
     * Artist sort order entries.
     */
    public interface ArtistSortOrder {

        String ARTIST_NAME = DataBaseHelper.ARTIST_NAME;
        String ARTIST_NUMBER_OF_SONGS = DataBaseHelper.NO_OF_TRACKS_BY_ARTIST;
        String ARTIST_NUMBER_OF_ALBUMS = DataBaseHelper.NO_OF_ALBUMS_BY_ARTIST;
    }


    /**
     * Album sort order entries.
     */
    public interface AlbumSortOrder {
        String ALBUM_DEFAULT = MediaStore.Audio.Albums.DEFAULT_SORT_ORDER;
        String ALBUM_NAME = MediaStore.Audio.Albums.ALBUM;
        String ALBUM_NUMBER_OF_SONGS = MediaStore.Audio.Albums.NUMBER_OF_SONGS;
        String ALBUM_ARTIST = MediaStore.Audio.Albums.ARTIST;
        String ALBUM_YEAR = MediaStore.Audio.Albums.FIRST_YEAR;
    }

    /**
     * Song sort order entries.
     */
    public interface SongSortOrder {
        String SONG_DEFAULT = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;
        String SONG_DISPLAY_NAME = MediaStore.Audio.Media.DISPLAY_NAME;
        String SONG_TRACK_NO = MediaStore.Audio.Media.TRACK;
        String SONG_DURATION = MediaStore.Audio.Media.DURATION;
        String SONG_YEAR = MediaStore.Audio.Media.YEAR;
        String SONG_DATE = MediaStore.Audio.Media.DATE_ADDED;
        String SONG_ALBUM = MediaStore.Audio.Media.ALBUM;
        String SONG_ARTIST = MediaStore.Audio.Media.ARTIST;
        String SONG_FILENAME = MediaStore.Audio.Media.DATA;
    }


}
