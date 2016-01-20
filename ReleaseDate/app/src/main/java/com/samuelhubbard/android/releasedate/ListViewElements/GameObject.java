// Release Date
// Samuel Hubbard

package com.samuelhubbard.android.releasedate.ListViewElements;

import java.io.Serializable;

public class GameObject implements Serializable {

    // serial number for file saving
    private static final long serialVersionUID = 833736401L;

    // member variables
    private String mName;
    private String mDescription;
    private String mReleaseDay;
    private String mReleaseMonth;
    private String mReleaseYear;
    private String mPlatforms;
    private String mImage;
    private String mDeveloper;
    private String mGenre;
    private String mId;
    private boolean isSectionHeader;

    // constructors
    public GameObject() {
        mName = "";
        mDescription = "";
        mReleaseDay = "";
        mReleaseMonth = "";
        mReleaseYear = "";
        mPlatforms = "";
        mImage = "";
        mDeveloper = "";
        mGenre = "";
        mId = "";
    }

    public GameObject(String name) {
        this();
        mName = name;
        isSectionHeader = false;
    }

    public GameObject(String name, String desc) {
        this(name);
        mDescription = desc;
        isSectionHeader = false;
    }

    public GameObject(String name, String desc, String day) {
        this(name, desc);
        mReleaseDay = day;
        isSectionHeader = false;
    }

    public GameObject(String name, String desc, String day, String month) {
        this(name, desc, day);
        mReleaseMonth = month;
        isSectionHeader = false;
    }

    public GameObject(String name, String desc, String day, String month, String year) {
        this(name, desc, day, month);
        mReleaseYear = year;
        isSectionHeader = false;
    }

    public GameObject(String name, String desc, String day, String month, String year, String platforms) {
        this(name, desc, day, month, year);
        mPlatforms = platforms;
        isSectionHeader = false;
    }

    public GameObject(String name, String desc, String day, String month, String year, String platforms, String image) {
        this(name, desc, day, month, year, platforms);
        mImage = image;
        isSectionHeader = false;
    }

    public GameObject(String name, String desc, String day, String month, String year, String platforms, String image,
                      String dev) {
        this(name, desc, day, month, year, platforms, image);
        mDeveloper = dev;
        isSectionHeader = false;
    }

    public GameObject(String name, String desc, String day, String month, String year, String platforms, String image,
                      String dev, String genre) {
        this(name, desc, day, month, year, platforms, image, dev);
        mGenre = genre;
        isSectionHeader = false;
    }

    public GameObject(String name, String desc, String day, String month, String year, String platforms, String image,
                      String dev, String genre, String id) {
        this(name, desc, day, month, year, platforms, image, dev, genre);
        mId = id;
        isSectionHeader = false;
    }

    // get methods
    public String getName() {
        return mName;
    }

    public String getImage() {
        return mImage;
    }

    public String getFullReleaseDay() {
        return mReleaseMonth + "/" + mReleaseDay + "/" + mReleaseYear;
    }

    public String getPlatforms() {
        return mPlatforms;
    }

    public String getDescription() { return mDescription; }

    public String getDeveloper() { return mDeveloper; }

    public String getGenre() { return mGenre; }

    public String getDay() { return mReleaseDay; }

    public String getMonth() { return mReleaseMonth; }

    public String getYear() { return mReleaseYear; }

    public String getGameId() {
        return mId;
    }

    // Section header identifier and setter
    public void setToSectionHeader()
    {
        isSectionHeader = true;
    }

    public boolean isSectionHeader()
    {
        return isSectionHeader;
    }
}
