package edu.qc.seclass.glm;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ListManager extends AppCompatActivity {

    ListView itemsView = null;
    ListView quantityView = null;
    ListView measurementView = null;

    ArrayAdapter<Item> itemAdapter = null;
    ArrayAdapter<Integer> quantityAdapter = null;
    ArrayAdapter<String> measurementAdapter = null;

    ArrayList<String> itemsList = new ArrayList<>();
    ArrayList<String> quantityList = new ArrayList<>();
    ArrayList<String> measurementList = new ArrayList<>();

    ArrayList<Item> itemArrayList = new ArrayList<>();
    ArrayList<Integer> quantityArrayList = new ArrayList<>();

    final ListManager listContext = this;
    String listName = null;
    File file = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_manager);

        //Get name of List that was clicked and set the title of the activity to that value.
        Bundle extras = getIntent().getExtras();
        if(extras != null)
        {
            listName = extras.getString("ListName");
            file = (File) extras.get("file");
        }

        this.setTitle(listName);

        //Get the actual list and its contents.
        loadLists(file);
        final ItemsList currList = ItemsList.currLists.get(listName);

        itemArrayList = currList.getItems();
        quantityArrayList = currList.getQuantities();

        for(Item item : itemArrayList)
        {
            String itemName = item.getItemName();
            String itemMeasurement = item.getItemMeasurement();

            itemsList.add(itemName);
            measurementList.add(itemMeasurement);
        }

        for(Integer quantity : quantityArrayList)
        {
            String number = String.valueOf(quantity);
            quantityList.add(number);
        }

        //Display the list contents.
        itemAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, itemsList);
        quantityAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, quantityList);
        measurementAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, measurementList);

        itemsView = findViewById(R.id.ItemList);
        itemsView.setAdapter(itemAdapter);

        quantityView = findViewById(R.id.QuantityList);
        quantityView.setAdapter(quantityAdapter);

        measurementView = findViewById(R.id.MeasurementList);
        measurementView.setAdapter(measurementAdapter);
        measurementView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        //Set Persistent Checkmarks
        if(!currList.checkedItems.isEmpty()) {
            for (Integer position : currList.checkedItems) {
                measurementView.setItemChecked(position, true);
            }
        }

        //Handle item name on click event.
        itemsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                final String listClicked = itemsList.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(listContext);
                builder.setTitle("Manage Item " + listClicked);

                builder.setItems(new CharSequence[]
                                {"Uncheck All Items", "Delete Item", "Sort Items by Type", "Cancel"},
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //"Which" contains the index position of the button clicked.
                                switch (which) {
                                    case 0:
                                        currList.checkedItems.clear();
                                        saveLists(file);
                                        updateUI(currList);
                                        break;
                                    case 1:
                                        Item currItem = itemArrayList.get(position);
                                        currList.removeItem(currItem);
                                        saveLists(file);
                                        updateUI(currList);
                                        Toast.makeText(listContext, listClicked + " Deleted Successfully",
                                                Toast.LENGTH_SHORT).show();
                                        break;
                                    case 2:
                                        sortByType(currList);
                                        break;
                                    case 3:
                                        dialog.cancel();
                                        break;
                                }
                            }
                        });

                builder.create().show();
            }
        });

        //Handle quantity number on click event.
        quantityView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(listContext);
                builder.setTitle("Change Item Quantity");

                final EditText quantity = new EditText(listContext);
                builder.setView(quantity);
                builder.setPositiveButton("Change Quantity", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newQuantityString = quantity.getText().toString();
                        int newQuantity;
                        Item currItem = itemArrayList.get(position);

                        try {
                            newQuantity = Integer.parseInt(newQuantityString);
                        }
                        catch(Exception e)
                        {
                            Toast.makeText(listContext, "Enter a valid whole number", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if(newQuantity < 1) {
                            Toast.makeText(listContext, "Enter a valid whole number", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        currList.updateQuantity(currItem, newQuantity);
                        saveLists(file);
                        updateUI(currList);
                        Toast.makeText(listContext, "Quantity Changed Successfully",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        measurementView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currList.checkedItems.contains(position)) {
                    currList.checkedItems.remove((Integer) position);
                }
                else {
                    currList.checkedItems.add(position);
                }

                saveLists(file);
            }
        });

        //Handle plus button on click event.
        FloatingActionButton fab = findViewById(R.id.addListButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(listContext);
                builder.setTitle("Add New Item");

                //Method of Adding Item #1: Selecting Item Type from List, and then the Item itself.
                builder.setPositiveButton("Add New Item From List", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final ItemDatabase database = ItemDatabase.getInstance(listContext);
                        final List<Item> allItems = database.itemDao().getItemList();

                        if(allItems.isEmpty()) {
                            Toast.makeText(listContext, "Error: No items in database. " +
                                    "Please use the \"Enter Item Name\" option to add an item to the " +
                                    "database.", Toast.LENGTH_LONG).show();
                        }
                        else {
                            List<String> itemTypes = new ArrayList<>();

                            for(Item item : allItems) {
                                itemTypes.add(item.getItemType());
                            }

                            ArrayAdapter<String> listAdapter = new ArrayAdapter(listContext,
                                    android.R.layout.simple_spinner_item, itemTypes);

                            final Spinner typeSpinner = new Spinner(listContext);
                            typeSpinner.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT));
                            typeSpinner.setAdapter(listAdapter);

                            AlertDialog.Builder typeList = new AlertDialog.Builder(listContext);
                            typeList.setTitle("Add New Item From List");
                            typeList.setMessage("Select the type of the item that you wish to add.");
                            typeList.setView(typeSpinner);

                            //User selects type of item to be added, show them all items of that type.
                            typeList.setPositiveButton("Select", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String selection = typeSpinner.getSelectedItem().toString();
                                    List<Item> itemsOfTypeSelected = database.itemDao().typeSearch(selection);
                                    List<String> itemNames = new ArrayList<>();

                                    for(Item item : itemsOfTypeSelected) {
                                        itemNames.add(item.getItemName());
                                    }

                                    ArrayAdapter<String> itemAdapter = new ArrayAdapter(listContext,
                                            android.R.layout.simple_spinner_item, itemNames);

                                    final Spinner itemSpinner = new Spinner(listContext);
                                    itemSpinner.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT));
                                    itemSpinner.setAdapter(itemAdapter);

                                    LinearLayout layout = new LinearLayout(listContext);
                                    layout.setOrientation(LinearLayout.VERTICAL);
                                    layout.addView(itemSpinner);

                                    final EditText itemQuantityInput = new EditText(listContext);
                                    itemQuantityInput.setHint("Enter Quantity");
                                    layout.addView(itemQuantityInput);

                                    AlertDialog.Builder itemsList = new AlertDialog.Builder(listContext);
                                    itemsList.setTitle("Add New Item From List");
                                    itemsList.setMessage("Select an item from the list and enter the quantity you wish to add.");
                                    itemsList.setView(layout);

                                    //User selects item to be added. Ask User for Quantity.
                                    itemsList.setPositiveButton("Add Item", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String itemSelection = itemSpinner.getSelectedItem().toString();
                                            String quantityString = itemQuantityInput.getText().toString();
                                            int quantity = 0;
                                            Item toBeAdded = null;

                                            try {
                                                quantity = Integer.parseInt(quantityString);
                                            }
                                            catch(Exception e) {
                                                Toast.makeText(listContext, "Enter a valid whole number for " +
                                                        "the item quantity.", Toast.LENGTH_SHORT).show();
                                                return;
                                            }

                                            if(quantity < 1) {
                                                Toast.makeText(listContext, "Enter a valid whole number for " +
                                                        "the item quantity.", Toast.LENGTH_SHORT).show();
                                                return;
                                            }

                                            for(Item item : allItems) {
                                                if(item.getItemName().equals(itemSelection)) {
                                                    toBeAdded = item;
                                                }
                                            }

                                            currList.addItem(toBeAdded, quantity);
                                            saveLists(file);
                                            updateUI(currList);
                                        }
                                    });
                                    itemsList.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                            Toast.makeText(listContext, "If you don't see the item you wish to add, " +
                                                    "please use the \"Enter Item Name\" option to add an item to the " +
                                                    "database.", Toast.LENGTH_LONG).show();
                                        }
                                    });

                                    itemsList.create().show();
                                }
                            });
                            typeList.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    Toast.makeText(listContext, "If you don't see the type of the item you wish to add, " +
                                            "please use the \"Enter Item Name\" option to add an item to the " +
                                            "database.", Toast.LENGTH_LONG).show();
                                }
                            });

                            typeList.create().show();
                        }
                    }
                });
                builder.setNegativeButton("Enter Item Name", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog.Builder nameInput = new AlertDialog.Builder(listContext);
                        nameInput.setTitle("Enter Item Name");

                        final EditText itemName = new EditText(listContext);
                        itemName.setHint("Enter Item Name");
                        nameInput.setView(itemName);

                        //Method of Adding Item #2: Entering Item Name.
                        nameInput.setPositiveButton("Query Database", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        final String name = itemName.getText().toString();

                                        final ItemDatabase database = ItemDatabase.getInstance(listContext);
                                        List<Item> nameMatches = database.itemDao().itemSearch(name);

                                        if(nameMatches.isEmpty()) {
                                            //No match found, ask user to add to database
                                            AlertDialog.Builder addDialog = new AlertDialog.Builder(listContext);
                                            addDialog.setTitle("Add to Database");
                                            addDialog.setMessage("No match was found. Please enter " +
                                                    "the item type (ex: Dairy), the item measurement (ex: Gallons), " +
                                                    "and the quantity of the item that you wish to purchase.");

                                            LinearLayout layout = new LinearLayout(listContext);
                                            layout.setOrientation(LinearLayout.VERTICAL);

                                            final EditText itemTypeInput = new EditText(listContext);
                                            itemTypeInput.setHint("Enter Type");
                                            layout.addView(itemTypeInput);

                                            final EditText itemMeasurementInput = new EditText(listContext);
                                            itemMeasurementInput.setHint("Enter Measurement");
                                            layout.addView(itemMeasurementInput);

                                            final EditText itemQuantityInput = new EditText(listContext);
                                            itemQuantityInput.setHint("Enter Quantity");
                                            layout.addView(itemQuantityInput);

                                            addDialog.setView(layout);

                                            addDialog.setPositiveButton("Add to Database", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    String type = itemTypeInput.getText().toString();
                                                    String measurement = itemMeasurementInput.getText().toString();
                                                    String quantityString = itemQuantityInput.getText().toString();
                                                    int quantity;

                                                    try {
                                                        quantity = Integer.parseInt(quantityString);
                                                    }
                                                    catch(Exception e) {
                                                        Toast.makeText(listContext, "Enter a valid whole number for " +
                                                                "the item quantity.", Toast.LENGTH_SHORT).show();
                                                        return;
                                                    }

                                                    if(quantity < 1) {
                                                        Toast.makeText(listContext, "Enter a valid whole number for " +
                                                                "the item quantity.", Toast.LENGTH_SHORT).show();
                                                        return;
                                                    }

                                                    Item toBeAdded =  new Item(name, type, measurement);
                                                    database.itemDao().insertItem(toBeAdded);
                                                    currList.addItem(toBeAdded, quantity);
                                                    saveLists(file);
                                                    updateUI(currList);
                                                }
                                            });
                                            addDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                }
                                            });

                                            addDialog.create().show();
                                        }
                                        else {
                                            //Matches found, ask user to select from matches.
                                            ArrayList<String> itemNames = new ArrayList<>();

                                            for(Item item : nameMatches) {
                                                itemNames.add(item.getItemName());
                                            }

                                            ArrayAdapter<String> listAdapter = new ArrayAdapter(listContext,
                                                    android.R.layout.simple_spinner_item, itemNames);

                                            final Spinner sp = new Spinner(listContext);
                                            sp.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                                    LinearLayout.LayoutParams.WRAP_CONTENT));
                                            sp.setAdapter(listAdapter);

                                            LinearLayout layout = new LinearLayout(listContext);
                                            layout.setOrientation(LinearLayout.VERTICAL);
                                            layout.addView(sp);

                                            final EditText itemQuantityInput = new EditText(listContext);
                                            itemQuantityInput.setHint("Enter Quantity");
                                            layout.addView(itemQuantityInput);

                                            AlertDialog.Builder itemsList = new AlertDialog.Builder(listContext);
                                            itemsList.setTitle("Evaluate Possible Matches");
                                            itemsList.setMessage("The database has found potential " +
                                                    "matches for the item that you wish to add. " +
                                                    "If you see the item you wish to add, select it " +
                                                    "from the list and enter the quantity that you desire." +
                                                    " If you still do not see your item, click on " +
                                                    "the button labeled \"Item Not Found\""); 
                                            itemsList.setView(layout);

                                            itemsList.setPositiveButton("Add Item", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    String itemSelection = sp.getSelectedItem().toString();
                                                    String quantityString = itemQuantityInput.getText().toString();
                                                    List<Item> allItems = database.itemDao().getItemList();
                                                    Item toBeAdded = null;
                                                    int quantity;

                                                    try {
                                                        quantity = Integer.parseInt(quantityString);
                                                    }
                                                    catch(Exception e) {
                                                        Toast.makeText(listContext, "Enter a valid whole number for " +
                                                                "the item quantity.", Toast.LENGTH_SHORT).show();
                                                        return;
                                                    }

                                                    if(quantity < 1) {
                                                        Toast.makeText(listContext, "Enter a valid whole number for " +
                                                                "the item quantity.", Toast.LENGTH_SHORT).show();
                                                        return;
                                                    }

                                                    for(Item item : allItems) {
                                                        if(item.getItemName().equals(itemSelection)) {
                                                            toBeAdded = item;
                                                        }
                                                    }

                                                    currList.addItem(toBeAdded, quantity);
                                                    saveLists(file);
                                                    updateUI(currList);
                                                }
                                            });
                                            itemsList.setNegativeButton("Item Not Found", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    AlertDialog.Builder addDialog = new AlertDialog.Builder(listContext);
                                                    addDialog.setTitle("Add to Database");
                                                    addDialog.setMessage("No match was found. Please enter " +
                                                            "the item type (ex: Dairy), the item measurement (ex: Gallons), " +
                                                            "and the quantity of the item that you wish to purchase.");

                                                    LinearLayout layout = new LinearLayout(listContext);
                                                    layout.setOrientation(LinearLayout.VERTICAL);

                                                    final EditText itemTypeInput = new EditText(listContext);
                                                    itemTypeInput.setHint("Enter Type");
                                                    layout.addView(itemTypeInput);

                                                    final EditText itemMeasurementInput = new EditText(listContext);
                                                    itemMeasurementInput.setHint("Enter Measurement");
                                                    layout.addView(itemMeasurementInput);

                                                    final EditText itemQuantityInput = new EditText(listContext);
                                                    itemQuantityInput.setHint("Enter Quantity");
                                                    layout.addView(itemQuantityInput);

                                                    addDialog.setView(layout);

                                                    addDialog.setPositiveButton("Add to Database", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            String type = itemTypeInput.getText().toString();
                                                            String measurement = itemMeasurementInput.getText().toString();
                                                            String quantityString = itemQuantityInput.getText().toString();
                                                            int quantity;

                                                            try {
                                                                quantity = Integer.parseInt(quantityString);
                                                            }
                                                            catch(Exception e) {
                                                                Toast.makeText(listContext, "Enter a valid whole number for " +
                                                                        "the item quantity.", Toast.LENGTH_SHORT).show();
                                                                return;
                                                            }

                                                            if(quantity < 1) {
                                                                Toast.makeText(listContext, "Enter a valid whole number for " +
                                                                        "the item quantity.", Toast.LENGTH_SHORT).show();
                                                                return;
                                                            }

                                                            Item toBeAdded =  new Item(name, type, measurement);
                                                            database.itemDao().insertItem(toBeAdded);
                                                            currList.addItem(toBeAdded, quantity);
                                                            saveLists(file);
                                                            updateUI(currList);
                                                        }
                                                    });
                                                    addDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.cancel();
                                                        }
                                                    });

                                                    addDialog.create().show();
                                                }
                                            });
                                            itemsList.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                }
                                            });

                                            itemsList.create().show();
                                        }
                                    }
                                });
                                nameInput.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });

                        nameInput.show();
                    }
                });
                builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
    }

    public void updateUI(ItemsList currList) {
        itemArrayList.clear();
        quantityArrayList.clear();

        itemsList.clear();
        quantityList.clear();
        measurementList.clear();

        itemArrayList = currList.getItems();
        quantityArrayList = currList.getQuantities();

        for(Item item : itemArrayList) {
            String itemName = item.getItemName();
            String itemMeasurement = item.getItemMeasurement();

            itemsList.add(itemName);
            measurementList.add(itemMeasurement);
        }

        for(Integer value : quantityArrayList) {
            String number = String.valueOf(value);
            quantityList.add(number);
        }

        itemsView.setAdapter(itemAdapter);
        quantityView.setAdapter(quantityAdapter);
        measurementView.setAdapter(measurementAdapter);

        if(!currList.checkedItems.isEmpty()) {
            for (Integer position : currList.checkedItems) {
                measurementView.setItemChecked(position, true);
            }
        }
    }
    
    public void sortByType(ItemsList currList) {
        Map<Item, Integer> currListMap = currList.itemList;
        List<Item> itemList = currList.getItems();
        List<Integer> quantityList = new ArrayList<>();

        //Sort Items by Type (See Item Class for CompareTo Method)
        Collections.sort(itemList);
        TreeMap<Item, Integer> sortedItems = new TreeMap<>();

        //Get quantities of sorted items
        for(Item item : itemList) {
           quantityList.add(currListMap.get(item));
        }

        //Associate sorted items with their quantities
        if(itemList.size() == quantityList.size()) {
            for(int i = 0; i < itemList.size(); i++) {
                Item currItem = itemList.get(i);
                Integer currQuantity = quantityList.get(i);
                sortedItems.put(currItem, currQuantity);
            }
        }
        else {
            Toast.makeText(listContext, "There was an error sorting. Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        //Replace the existing itemlist with the sorted one.
        currList.itemList = sortedItems;
        updateUI(currList);
    }

    public static void saveLists(File file) {
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));
            outputStream.writeObject(ItemsList.currLists);
            outputStream.flush();
            outputStream.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadLists(File file) {
        try {
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
            Map<String, ItemsList> savedLists = (Map<String, ItemsList>) inputStream.readObject();
            ItemsList.currLists = savedLists;
            inputStream.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}