package edu.qc.seclass.glm;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = Item.class, version = 1)
public abstract class ItemDatabase extends RoomDatabase {
    private static volatile ItemDatabase instance;

    static ItemDatabase getInstance(final Context context) {
        if(instance == null) {
            synchronized (ItemDatabase.class) {
                if(instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            ItemDatabase.class, "item_database").allowMainThreadQueries().build();
                }
            }
        }

        return instance;
    }



    public abstract ItemDao itemDao();
}
