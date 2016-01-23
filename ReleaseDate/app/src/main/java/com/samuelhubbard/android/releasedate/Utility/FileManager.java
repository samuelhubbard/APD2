// Release Date
// Samuel Hubbard

package com.samuelhubbard.android.releasedate.Utility;

import android.content.Context;

import com.samuelhubbard.android.releasedate.ListViewElements.GameObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Objects;

public class FileManager {
    public static boolean saveToFile(GameObject g, Context c) {
        try {

            ArrayList<GameObject> array;

            String filename = "trackedgames.bin";
            File file = new File(c.getFilesDir(), filename);

            ObjectInputStream inStream;

            // defining the input stream
            if (file.exists()) {
                inStream = new ObjectInputStream(new FileInputStream(file));
                array = (ArrayList<GameObject>) inStream.readObject();
            } else {
                array = new ArrayList<>();
            }
            // saving the contents of the file to the array variable

            array.add(g);

            // defining the output stream
            ObjectOutputStream outStream = new ObjectOutputStream(new FileOutputStream(file));

            // writing to the file
            outStream.writeObject(array);

            // flushing and closing the stream
            outStream.flush();
            outStream.close();

            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean updateFile(ArrayList<GameObject> a, Context c) {
        try {

            String filename = "trackedgames.bin";
            File file = new File(c.getFilesDir(), filename);

            // defining the output stream
            ObjectOutputStream outStream = new ObjectOutputStream(new FileOutputStream(file));

            // writing to the file
            outStream.writeObject(a);

            // flushing and closing the stream
            outStream.flush();
            outStream.close();

            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static ArrayList<GameObject> loadFromFile(File f) {
        try {

            ArrayList<GameObject> array;
            ObjectInputStream stream;

            // defining the input stream
            if (f.exists()) {
                stream = new ObjectInputStream(new FileInputStream(f));
                // saving the contents of the file to the array variable
                array = (ArrayList<GameObject>) stream.readObject();
            } else {
                return null;
            }

            // return the array
            return array;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (OptionalDataException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isTracked(File f, String id) {

        try {
            ArrayList<GameObject> array;
            ObjectInputStream stream;

            if (f.exists()) {
                // pull in the array
                stream = new ObjectInputStream(new FileInputStream(f));
                array = (ArrayList<GameObject>) stream.readObject();

                // loop through that array, checking to see if the id is present.
                // if it is, return true
                for (int i = 0; i < array.size(); i++) {
                    if (Objects.equals(array.get(i).getGameId(), id)) {
                        return true;
                    }
                }
                return false;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (OptionalDataException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
