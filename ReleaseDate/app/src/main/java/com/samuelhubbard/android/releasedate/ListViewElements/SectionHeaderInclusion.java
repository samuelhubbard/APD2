// Release Date
// Samuel Hubbard

package com.samuelhubbard.android.releasedate.ListViewElements;

import java.util.ArrayList;
import java.util.Objects;

public class SectionHeaderInclusion {

    public static ArrayList<GameListObject> insertHeaders(ArrayList<GameListObject> a) {

        // create an array to populate the section headers into it
        ArrayList<GameListObject> addingHeaders = new ArrayList<>();

        // header string for the for loop
        String header = "";

        // the for loop that integrates the section headers into the array full of games
        for(int i = 0; i < a.size(); i++)
        {
            // compare the header string to the numerical month from current array position
            // if they aren't equal, make them that way
            if (!Objects.equals(header, a.get(i).getMonth())) {

                // the game object that will be used as a section header
                GameListObject sectionCell;

                // customize the game "header" object so it will display what I want it to display
                if (Objects.equals(a.get(i).getMonth(), "1")) {
                    sectionCell = new GameListObject("January", "", "", "", a.get(i).getReleaseYear());
                } else if (Objects.equals(a.get(i).getMonth(), "2")) {
                    sectionCell = new GameListObject("February", "", "", "", a.get(i).getReleaseYear());
                } else if (Objects.equals(a.get(i).getMonth(), "3")) {
                    sectionCell = new GameListObject("March", "", "", "", a.get(i).getReleaseYear());
                } else if (Objects.equals(a.get(i).getMonth(), "4")) {
                    sectionCell = new GameListObject("April", "", "", "", a.get(i).getReleaseYear());
                } else if (Objects.equals(a.get(i).getMonth(), "5")) {
                    sectionCell = new GameListObject("May", "", "", "", a.get(i).getReleaseYear());
                } else if (Objects.equals(a.get(i).getMonth(), "6")) {
                    sectionCell = new GameListObject("June", "", "", "", a.get(i).getReleaseYear());
                } else if (Objects.equals(a.get(i).getMonth(), "7")) {
                    sectionCell = new GameListObject("July", "", "", "", a.get(i).getReleaseYear());
                } else if (Objects.equals(a.get(i).getMonth(), "8")) {
                    sectionCell = new GameListObject("August", "", "", "", a.get(i).getReleaseYear());
                } else if (Objects.equals(a.get(i).getMonth(), "9")) {
                    sectionCell = new GameListObject("September", "", "", "", a.get(i).getReleaseYear());
                } else if (Objects.equals(a.get(i).getMonth(), "10")) {
                    sectionCell = new GameListObject("October", "", "", "", a.get(i).getReleaseYear());
                } else if (Objects.equals(a.get(i).getMonth(), "11")) {
                    sectionCell = new GameListObject("November", "", "", "", a.get(i).getReleaseYear());
                } else if (Objects.equals(a.get(i).getMonth(), "12")) {
                    sectionCell = new GameListObject("December", "", "", "", a.get(i).getReleaseYear());
                } else {
                    sectionCell = new GameListObject("Unknown", "", "", "", "Unknown");
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
