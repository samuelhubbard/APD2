// Release Date
// Samuel Hubbard

package com.samuelhubbard.android.releasedate.ListViewElements;

public class GameObject {

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
    }

    public GameObject(String name, String desc) {
        this(name);
        mDescription = desc;
    }

    public GameObject(String name, String desc, String day) {
        this(name, desc);
        mReleaseDay = day;
    }

    public GameObject(String name, String desc, String day, String month) {
        this(name, desc, day);
        mReleaseMonth = month;
    }

    public GameObject(String name, String desc, String day, String month, String year) {
        this(name, desc, day, month);
        mReleaseYear = year;
    }

    public GameObject(String name, String desc, String day, String month, String year, String platforms) {
        this(name, desc, day, month, year);
        mPlatforms = platforms;
    }

    public GameObject(String name, String desc, String day, String month, String year, String platforms, String image) {
        this(name, desc, day, month, year, platforms);
        mImage = image;
    }

    public GameObject(String name, String desc, String day, String month, String year, String platforms, String image,
                      String dev) {
        this(name, desc, day, month, year, platforms, image);
        mDeveloper = dev;
    }

    public GameObject(String name, String desc, String day, String month, String year, String platforms, String image,
                      String dev, String genre) {
        this(name, desc, day, month, year, platforms, image, dev);
        mGenre = genre;
    }

    public GameObject(String name, String desc, String day, String month, String year, String platforms, String image,
                      String dev, String genre, String id) {
        this(name, desc, day, month, year, platforms, image, dev, genre);
        mId = id;
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

    public String getGameId() {
        return mId;
    }
}
