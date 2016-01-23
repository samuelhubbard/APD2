// Release Date
// Samuel Hubbard

package com.samuelhubbard.android.releasedate.ListViewElements;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

public class SortTrackedGames {
    
    public static ArrayList<GameObject> sortArray(ArrayList<GameObject> a) {
        // time for array sorting
        ArrayList<GameObject> sortedArray = new ArrayList<>();

        Calendar c = Calendar.getInstance();
        int numericalYear = c.get(Calendar.YEAR);
        Log.i("TESTING", String.valueOf(numericalYear));
        int maxYearRange = numericalYear + 2;
        Log.i("TESTING", String.valueOf(maxYearRange));

        // this for loop handles sorting by year
        for (int t = numericalYear; t <= maxYearRange; t++) {
            ArrayList<GameObject> yearArray = new ArrayList<>();

            // loop through the entire array arranging all elements by year
            for (int r = 0; r < a.size(); r++) {
                GameObject game = a.get(r);
                String year = game.getYear();
                String stringYear = String.valueOf(t);

                if (Objects.equals(year, stringYear)) {
                    yearArray.add(game);
                }
            }

            // this for loop handles sorting by month
            for (int i = 1; i <= 12; i++) {
                ArrayList<GameObject> monthArray = new ArrayList<>();

                // this nested for loop actually runs through the full array looking for everything
                // corresponding to the month
                for (int o = 0; o < yearArray.size(); o++) {

                    GameObject game = yearArray.get(o);
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
                    ArrayList<GameObject> dayArray = new ArrayList<>();

                    // this for loop sorts through the new array and sorts it again by day
                    for (int u = 0; u < monthArray.size(); u++) {

                        GameObject game = monthArray.get(u);
                        String day = game.getDay();
                        String stringDay = String.valueOf(p);

                        if (Objects.equals(day, stringDay)) {
                            dayArray.add(game);
                        }
                    }

                    // finally, take that array that is now sorted by month and day...
                    // and sort it further, alphabetically by game name
                    Collections.sort(dayArray, new Comparator<GameObject>() {
                        public int compare(GameObject g1, GameObject g2) {
                            return g1.getName().compareTo(g2.getName());
                        }
                    });

                    // and put those contents into the array for return
                    for (int y = 0; y < dayArray.size(); y++) {

                        GameObject game = dayArray.get(y);
                        sortedArray.add(game);
                    }
                }
            }
        }
        return sortedArray;

    }
    
    public static ArrayList<GameObject> includeSectionHeaders(ArrayList<GameObject> a) {
        // create an array to populate the section headers into it
        ArrayList<GameObject> addingHeaders = new ArrayList<>();

        // header string for the for loop
        String header = "";

        // the for loop that integrates the section headers into the array full of games
        for(int i = 0; i < a.size(); i++)
        {
            // compare the header string to the numerical month from current array position
            // if they aren't equal, make them that way
            if (!Objects.equals(header, a.get(i).getMonth())) {

                // the game object that will be used as a section header
                GameObject sectionCell;

                // customize the game "header" object so it will display what I want it to display
                if (Objects.equals(a.get(i).getMonth(), "1")) {
                    sectionCell = new GameObject("January", "", "", "", a.get(i).getYear());
                } else if (Objects.equals(a.get(i).getMonth(), "2")) {
                    sectionCell = new GameObject("February", "", "", "", a.get(i).getYear());
                } else if (Objects.equals(a.get(i).getMonth(), "3")) {
                    sectionCell = new GameObject("March", "", "", "", a.get(i).getYear());
                } else if (Objects.equals(a.get(i).getMonth(), "4")) {
                    sectionCell = new GameObject("April", "", "", "", a.get(i).getYear());
                } else if (Objects.equals(a.get(i).getMonth(), "5")) {
                    sectionCell = new GameObject("May", "", "", "", a.get(i).getYear());
                } else if (Objects.equals(a.get(i).getMonth(), "6")) {
                    sectionCell = new GameObject("June", "", "", "", a.get(i).getYear());
                } else if (Objects.equals(a.get(i).getMonth(), "7")) {
                    sectionCell = new GameObject("July", "", "", "", a.get(i).getYear());
                } else if (Objects.equals(a.get(i).getMonth(), "8")) {
                    sectionCell = new GameObject("August", "", "", "", a.get(i).getYear());
                } else if (Objects.equals(a.get(i).getMonth(), "9")) {
                    sectionCell = new GameObject("September", "", "", "", a.get(i).getYear());
                } else if (Objects.equals(a.get(i).getMonth(), "10")) {
                    sectionCell = new GameObject("October", "", "", "", a.get(i).getYear());
                } else if (Objects.equals(a.get(i).getMonth(), "11")) {
                    sectionCell = new GameObject("November", "", "", "", a.get(i).getYear());
                } else if (Objects.equals(a.get(i).getMonth(), "12")) {
                    sectionCell = new GameObject("December", "", "", "", a.get(i).getYear());
                } else {
                    sectionCell = new GameObject("Unknown", "", "", "", "Unknown");
                }

                // set the game object as a section header
                sectionCell.setToSectionHeader();
                // add it to the working array
                addingHeaders.add(sectionCell);
                // set the header string to the current month so that the for loop will just skip
                // over all of the next entries with the same month value
                header = a.get(i).getMonth();
            }
            // add every entry from the initial array into the working array so that everything
            // stays in order
            addingHeaders.add(a.get(i));
        }

        // finally, return the array that will actually be used for list population
        return addingHeaders;
    }
}
