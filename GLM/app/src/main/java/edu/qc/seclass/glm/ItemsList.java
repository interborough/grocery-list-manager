package edu.qc.seclass.glm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemsList implements Serializable {
    //Stores a reference to the all the lists created so far.
    public static Map<String, ItemsList> currLists = new HashMap<>();

    //Stores an association between an item and its quantity.
    public Map<Item, Integer> itemList;
    public List<Integer> checkedItems;
    public String listName;

    public ItemsList(String name) {
        itemList = new HashMap<>();
        checkedItems = new ArrayList<>();
        listName = name;
        currLists.put(listName, this);
    }

    public void addItem(Item item, int quantity) {
        currLists.remove(listName);
        itemList.put(item, quantity);
        currLists.put(listName, this);
    }

    public void removeItem(Item item) {
        currLists.remove(listName);
        itemList.remove(item);
        currLists.put(listName, this);
    }

    public void updateQuantity(Item item, int newQuantity) {
        removeItem(item);
        addItem(item, newQuantity);
    }

    public void renameList(String name) {
        currLists.remove(listName);
        listName = name;
        currLists.put(listName, this);
    }

    public ArrayList<Item> getItems()
    {
        ArrayList<Item> itemArrayList = new ArrayList<>();
        itemArrayList.addAll(itemList.keySet());
        return itemArrayList;
    }

    public ArrayList<Integer> getQuantities()
    {
        ArrayList<Integer> quantityArrayList = new ArrayList<>();
        quantityArrayList.addAll(itemList.values());
        return quantityArrayList;
    }
}
