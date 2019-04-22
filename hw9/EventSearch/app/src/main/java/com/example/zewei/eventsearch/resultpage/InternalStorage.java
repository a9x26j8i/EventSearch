package com.example.zewei.eventsearch.resultpage;

import android.content.Context;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InternalStorage {
    public static String KEY = "saved_events";
    private InternalStorage() {}
    public static void writeObject(Context context, String key, Object object) throws IOException {

        FileOutputStream fos = context.openFileOutput(key, Context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(object);
        oos.close();
        fos.close();
    }

    public static Object readObject(Context context, String key) throws IOException,
            ClassNotFoundException {
        FileInputStream fis = context.openFileInput(key);
        ObjectInputStream ois = new ObjectInputStream(fis);
        Object object = ois.readObject();
        return object;
    }

    public static void addObject(Context context, SearchItem searchItem) {
        try {
            List<SearchItem> list = (List<SearchItem>) readObject(context, KEY);
            if (!list.contains(searchItem)){
                list.add(searchItem);
                Toast.makeText(context, searchItem.getName() + " was added to favorites", Toast.LENGTH_SHORT).show();
                writeObject(context, KEY, list);
            } else{
                Toast.makeText(context, "yijing you le!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            List<SearchItem> list = new ArrayList<>();
            list.add(searchItem);
            Toast.makeText(context, searchItem.getName() + " was added to favorites", Toast.LENGTH_SHORT).show();
            try {
                writeObject(context, KEY, list);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public static void removeObject(Context context, SearchItem searchItem) {
        try {
            List<SearchItem> list = (List<SearchItem>) readObject(context, KEY);
            if (list.contains(searchItem)) {
                list.remove(searchItem);
                Toast.makeText(context, searchItem.getName() + " was removed from favorites", Toast.LENGTH_SHORT).show();

            }else{
                Toast.makeText(context, "mei cun ne!", Toast.LENGTH_SHORT).show();
            }
            writeObject(context, KEY, list);
        } catch (Exception e) {
            Toast.makeText(context, "mei cun ne!", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean contains(Context context, SearchItem searchItem){
        try {
            List<SearchItem> list = (List<SearchItem>) readObject(context, KEY);
            if (list.contains(searchItem)) {
                return true;
            }else{
                return false;
            }
        } catch (Exception e) {
//            Toast.makeText(context, "error!!!!", Toast.LENGTH_SHORT).show();
            return false;
        }

    }
}
