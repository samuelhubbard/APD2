// Release Date
// Samuel Hubbard

package com.samuelhubbard.android.releasedate.Utility;

import com.samuelhubbard.android.releasedate.ListViewElements.GameListObject;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

public class ApiHandler {

    // API URL elements
    // Just a note, this looks complicated but now I will be able to piece together the url for
    // a multitude of different possible scenarios (which this app has)
    private static final String URL_BASE = "http://www.giantbomb.com/api/games/";
    private static final String API_KEY = "?api_key=50e1bd0527eca552647ed32ebe50f7bb8ee0e89e";
    private static final String API_FORMAT = "&format=json";
    private static final String API_FILTER_STARTER = "&filter=";
    private static final String API_FILTER_SEP = ",";
    private static final String API_APP_TRACKED_PLATFORMS = "platforms:146|94|145|139";
    private static final String API_FILTER_YEAR = "expected_release_year:";
    private static final String API_FILTER_QUARTER = "expected_release_quarter:";

    public static String retrieveUpcomingGames(String year, String quarter) {

        // constructing the API Url
        String urlString = URL_BASE + API_KEY + API_FORMAT + API_FILTER_STARTER + API_FILTER_YEAR
                + year + API_FILTER_SEP + API_FILTER_QUARTER + quarter + API_FILTER_SEP + API_APP_TRACKED_PLATFORMS;

        // try/catch - pull data from the API
        try {
            // cast the URL string into a URL
            URL url = new URL(urlString);

            // open the connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // connect to the server
            connection.connect();

            // start an input stream and put the contents into a string
            InputStream weatherStream = connection.getInputStream();
            String data = IOUtils.toString(weatherStream);

            // close the stream and disconnect from the server
            weatherStream.close();
            connection.disconnect();

            // return the string that holds the JSON
            return data;

            // if there was an issue, print the stack trace
        } catch (IOException e) {
            e.printStackTrace();
        }

        // if there was an issue, return null
        return null;
    }

    public static ArrayList<GameListObject> parseUpcomingGames(String current, String next) {

        // create the array list to hold all of the game objects this parser creates
        ArrayList<GameListObject> workingArray = new ArrayList<>();

        // run a try/catch statement for the parsing in case of a JSONException
        try {
            // parse into the JSON object
            JSONObject quarterOneResponse = new JSONObject(current);

            // Here's the array that contains all of the game information for quarter 1
            JSONArray quarterOneResults = quarterOneResponse.getJSONArray("results");

            // the for loop that will go through the entire list of games
            // and end up generating the array
            for (int i = 0; i < quarterOneResults.length(); i++) {
                // create the JSON object for the current JSON array location
                JSONObject gameObject = quarterOneResults.getJSONObject(i);
                // Create the image object and just make it null for now
                JSONObject image = null;

                // if the actual object inside of the json isn't null
                if (!gameObject.isNull("image")) {
                    // set the image JSONObject to the image object inside the JSON
                    image = gameObject.getJSONObject("image");
                }

                // link to the platforms array inside of the game object
                JSONArray jsonPlatforms = gameObject.getJSONArray("platforms");

                // initialize the string that will hold all of the available platforms
                String workingPlatforms = "";

                // for loop through the platform array
                for (int p = 0; p < jsonPlatforms.length(); p++) {
                    // link a jason object to the current position of the platform array
                    JSONObject singlePlatform = jsonPlatforms.getJSONObject(p);

                    // as long as the platform is being tracked by this app, add it to the string
                    if (Objects.equals(singlePlatform.getString("name"), "PC") ||
                            Objects.equals(singlePlatform.getString("name"), "PlayStation 4") ||
                            Objects.equals(singlePlatform.getString("name"), "Xbox One") ||
                            Objects.equals(singlePlatform.getString("name"), "Wii U")) {
                        workingPlatforms = workingPlatforms + singlePlatform.getString("name") + ", ";
                    }
                }

                // get the name of the game
                String name = gameObject.getString("name");

                // make a thumbnail variable for the path
                String thumbnail;

                // as long as the image JSONObject isn't still null
                if (image != null) {
                    // get the thumbnail url from the JSON
                    thumbnail = image.getString("thumb_url");
                } else {
                    // otherwise set it to "no_image" to eventually use a placeholder
                    thumbnail = "no_image";
                }

                // get the release date and quarter in pieces
                String day = gameObject.getString("expected_release_day");
                String month = gameObject.getString("expected_release_month");
                String year = gameObject.getString("expected_release_year");
                String quarter = gameObject.getString("expected_release_quarter");

                // create the string to go into the custom object and remove the final two characters
                // from the string as it is just a comma and space that were generated there
                String platforms = workingPlatforms;
                if (platforms.endsWith(", ")) {
                    platforms = platforms.substring(0, platforms.length() - 2);
                }

                // get the game id for the API
                // need this for the detail view
                String id = gameObject.getString("id");

                // check to make sure that there is a full release date
                // if there is, create the game object and add it to the array
                if (!Objects.equals(day, "null") && !Objects.equals(month, "null") && !Objects.equals(year, "null")) {
                    GameListObject obj = new GameListObject(name, thumbnail, day, month, year, quarter, platforms, id);
                    workingArray.add(obj);
                }
            }

            // the JSON information for the second pulled quarter
            JSONObject quarterTwoResponse = new JSONObject(next);

            // the array that holds all of the game information
            JSONArray quarterTwoResults = quarterTwoResponse.getJSONArray("results");

            // the for loop that will populate the array with all of the game objects
            for (int i = 0; i < quarterTwoResults.length(); i++) {
                // create the object for the current game in the array
                JSONObject gameObject = quarterTwoResults.getJSONObject(i);

                // set the image jsonobject to null
                JSONObject image = null;
                // if the game objects image section isn't null...
                if (!gameObject.isNull("image")) {
                    // set the image jsonobject to the game's image section
                    image = gameObject.getJSONObject("image");
                }
                // link into the platforms array
                JSONArray jsonPlatforms = gameObject.getJSONArray("platforms");

                // the string to hold all of the tracked platforms
                String workingPlatforms = "";

                // loop through the platforms array and pull out all of the platforms that apply
                // to this app
                for (int p = 0; p < jsonPlatforms.length(); p++) {
                    JSONObject singlePlatform = jsonPlatforms.getJSONObject(p);

                    if (Objects.equals(singlePlatform.getString("name"), "PC") ||
                            Objects.equals(singlePlatform.getString("name"), "PlayStation 4") ||
                            Objects.equals(singlePlatform.getString("name"), "Xbox One")) {
                        workingPlatforms = workingPlatforms + singlePlatform.getString("name") + ", ";
                    }
                }

                // get the game name
                String name = gameObject.getString("name");

                // if the image json object isn't null, set it to the game object's image section
                // otherwise, set it to indicate that the placeholder needs to be loaded
                String thumbnail;
                if (image != null) {
                    thumbnail = image.getString("thumb_url");
                } else {
                    thumbnail = "no_image";
                }

                // get the release date and quarter in pieces
                String day = gameObject.getString("expected_release_day");
                String month = gameObject.getString("expected_release_month");
                String year = gameObject.getString("expected_release_year");
                String quarter = gameObject.getString("expected_release_quarter");

                // remove all of the extra spaces and commas from the platform string
                String platforms = workingPlatforms;
                if (platforms.endsWith(", ")) {
                    platforms = platforms.substring(0, platforms.length() - 2);
                }
                String id = gameObject.getString("id");

                // if there is a full release date, populate the game object and save it to the array
                if (!Objects.equals(day, "null") && !Objects.equals(month, "null") && !Objects.equals(year, "null")) {
                    GameListObject obj = new GameListObject(name, thumbnail, day, month, year, quarter, platforms, id);
                    workingArray.add(obj);
                }


            }

            // time for array sorting
            ArrayList<GameListObject> sortedArray = new ArrayList<>();

            // this for loop handles sorting by month
            for (int i = 1; i <= 12; i++) {
                ArrayList<GameListObject> monthArray = new ArrayList<>();

                // this nested for loop actually runs through the full array looking for everything
                // corresponding to the month
                for (int o = 0; o < workingArray.size(); o++) {

                    GameListObject game = workingArray.get(o);
                    String month = game.getMonth();
                    String stringMonth = String.valueOf(i);

                    // if the month is equal to the iterator for the initial for loop
                    // add it to the working array
                    if (Objects.equals(month, stringMonth)) {
                        monthArray.add(game);
                    }
                }

                // the for loop that handles sorting the new array by day
                for (int p = 1; p <= 31; p++) {
                    ArrayList<GameListObject> dayArray = new ArrayList<>();

                    // this for loop sorts through the new array and sorts it again by day
                    for (int u = 0; u < monthArray.size(); u++) {

                        GameListObject game = monthArray.get(u);
                        String day = game.getDay();
                        String stringDay = String.valueOf(p);

                        if (Objects.equals(day, stringDay)) {
                            dayArray.add(game);
                        }
                    }

                    // finally, take that array that is now sorted by month and day...
                    // and sort it further, alphabetically by game name
                    Collections.sort(dayArray, new Comparator<GameListObject>() {
                        public int compare(GameListObject g1, GameListObject g2) {
                            return g1.getName().compareTo(g2.getName());
                        }
                    });

                    // and put those contents into the array for return
                    for (int y = 0; y < dayArray.size(); y++) {

                        GameListObject game = dayArray.get(y);
                        sortedArray.add(game);
                    }
                }
            }
            return sortedArray;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
