package edu.qc.seclass.glm;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ItemDao {
    @Query("Select * from item")
    List<Item> getItemList();

    @Query("Select * from item where itemName like :item")
    List<Item> itemSearch(String item);

    @Query("Select * from item where type = :type")
    List<Item> typeSearch(String type);

    @Insert
    void insertItem(Item item);

    @Update
    void updateItem(Item item);

    @Delete
    void deleteItem(Item item);
}
