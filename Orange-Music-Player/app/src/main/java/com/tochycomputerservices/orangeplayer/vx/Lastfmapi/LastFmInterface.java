package com.tochycomputerservices.orangeplayer.vx.Lastfmapi;

import com.tochycomputerservices.orangeplayer.vx.Lastfmapi.Models.AlbumModel;
import com.tochycomputerservices.orangeplayer.vx.Lastfmapi.Models.ArtistModel;
import com.tochycomputerservices.orangeplayer.vx.Lastfmapi.Models.BestMatchesModel;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by TOCHY on 12/01/2021.
 */

public interface LastFmInterface {

    @POST(ApiClient.BASE_PARAMETERS_ARTIST)
    Call<ArtistModel> getArtist(@Query("artist") String artistName);

    @POST(ApiClient.BASE_PARAMETERS_ALBUM)
    Call<AlbumModel> getAlbum(@Query("album") String albumName, @Query("artist") String artistName);

    /*@POST
    Call<ITunesArtistModel> getITunesAlbum(@Url String url, @Query("term") String termName, @Query("entity") String entityName);
*/
    @POST
    Call<BestMatchesModel> getITunesSong(@Url String url, @Query("term") String termName, @Query("entity") String entityName);

    @POST
    Call<ResponseBody> getJustin(@Url String url);

}
