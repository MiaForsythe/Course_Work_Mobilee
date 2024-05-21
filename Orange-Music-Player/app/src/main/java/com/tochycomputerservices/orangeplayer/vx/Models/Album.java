package com.tochycomputerservices.orangeplayer.vx.Models;

/**
 * Created by TOCHY on 12/01/2021.
 */

public class Album {
    public long _Id;
    public String _albumName;
    public String _artistName;
    public String _albumArt;

    public Album(long _Id, String _albumName, String _artistName, String _albumArt) {
        this._Id = _Id;
        this._albumName = _albumName;
        this._artistName = _artistName;
        this._albumArt = _albumArt;
    }

}
