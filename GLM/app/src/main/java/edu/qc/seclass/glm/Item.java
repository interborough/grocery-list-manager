package edu.qc.seclass.glm;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "item")
public class Item implements Comparable<Item>, Serializable {
    @PrimaryKey()
    @NonNull
    private String itemName;

    @NonNull
    @ColumnInfo(name = "type")
    private String itemType;

    //For identifiers like "Gallons" or "Quarts"
    @NonNull
    @ColumnInfo(name = "measurement")
    private String itemMeasurement;

    public Item(String itemName, String itemType, String itemMeasurement) {
        this.itemName = itemName;
        this.itemType = itemType;
        this.itemMeasurement = itemMeasurement;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemType() {
        return itemType;
    }

    public String getItemMeasurement() {
        return itemMeasurement;
    }

    public void setItemName(String name) {
        itemName = name;
    }

    public void setItemType(String type) {
        itemType = type;
    }

    public void setItemMeasurement(String measurement) {
        itemMeasurement = measurement;
    }

    @Override
    public int compareTo(Item o) {
        String typeOne = this.getItemType();
        String typeTwo = o.getItemType();

        return typeOne.compareTo(typeTwo);
    }
}
