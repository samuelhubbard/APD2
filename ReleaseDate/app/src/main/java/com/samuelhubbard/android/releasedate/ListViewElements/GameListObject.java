package com.samuelhubbard.android.releasedate.ListViewElements;

import java.io.Serializable;

public class GameListObject implements Serializable {

    // serial number for file saving
    private static final long serialVersionUID = 963936401L;

    // member variables
    private String mName;
    private String mThumbnailPath;
    private String mReleaseDay;
    private String mReleaseMonth;
    private String mReleaseYear;
    private String mReleaseQuarter;
    private String mPlatforms;
    private String mId;

    // constructors
    public GameListObject() {
        mName = "";
        mThumbnailPath = "";
        mReleaseDay = "";
        mReleaseMonth = "";
        mReleaseYear = "";
        mReleaseQuarter = "";
        mPlatforms = "";
        mId = "";
    }

    public GameListObject(String name) {
        this();
        mName = name;
    }

    public GameListObject(String name, String thumb) {
        this(name);
        mThumbnailPath = thumb;
    }

    public GameListObject(String name, String thumb, String day) {
        this(name, thumb);
        mReleaseDay = day;
    }

    public GameListObject(String name, String thumb, String day, String month) {
        this(name, thumb, day);
        mReleaseMonth = month;
    }

    public GameListObject(String name, String thumb, String day, String month, String year) {
        this(name, thumb, day, month);
        mReleaseYear = year;
    }

    public GameListObject(String name, String thumb, String day, String month, String year, String quarter) {
        this(name, thumb, day, month, year);
        mReleaseQuarter = quarter;
    }

    public GameListObject(String name, String thumb, String day, String month, String year, String quarter, String platforms) {
        this(name, thumb, day, month, year, quarter);
        mPlatforms = platforms;
    }

    public GameListObject(String name, String thumb, String day, String month, String year, String quarter, String platforms, String id) {
        this(name, thumb, day, month, year, quarter, platforms);
        mId = id;
    }

    // get methods
    public String getName() {
        return mName;
    }

    public String getThumbnailPath() {
        return mThumbnailPath;
    }

    public String getFullReleaseDay() {
        return mReleaseMonth + "/" + mReleaseDay + "/" + mReleaseYear;
    }

    public String getReleaseYear() {
        return mReleaseYear;
    }

    public String getReleaseQuarter() {
        return mReleaseQuarter;
    }

    public String getPlatforms() {
        return mPlatforms;
    }

    public String getGameId() {
        return mId;
    }
}
