package edu.qc.seclass.glm;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    ListView lv = null;
    ArrayAdapter<ItemsList> adapter = null;
    ArrayList<String> namesList = new ArrayList<>();
    final MainActivity mainContext = this;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final File file = new File(getDir("data", MODE_PRIVATE), "map");
        loadLists(file);
        namesList.addAll(ItemsList.currLists.keySet());

        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, namesList);
        lv = findViewById(R.id.ItemList);
        lv.setAdapter(adapter);

        //Handling list item click event.
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final String listClicked = namesList.get(position);
                final AlertDialog.Builder builder = new AlertDialog.Builder(mainContext);
                builder.setTitle("Manage " + listClicked);
                builder.setItems(new CharSequence[]
                                {"Edit List", "Rename List", "Delete List", "Cancel"},
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //"Which" contains the index position of the button clicked.
                                switch (which) {
                                    case 0:
                                        Intent intent = new Intent(mainContext, ListManager.class);
                                        intent.putExtra("ListName", listClicked);
                                        intent.putExtra("file", file);
                                        startActivity(intent);
                                        break;
                                    case 1:
                                        AlertDialog.Builder rename = new AlertDialog.Builder(mainContext);
                                        rename.setTitle("Rename List");

                                        final EditText newNameInput = new EditText(mainContext);
                                        newNameInput.setHint("Enter New List Name");
                                        rename.setView(newNameInput);
                                        rename.setPositiveButton("Rename", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                String newName = newNameInput.getText().toString();
                                                ItemsList originalList = ItemsList.currLists.get(listClicked);
                                                originalList.renameList(newName);
                                                namesList.clear();
                                                namesList.addAll(ItemsList.currLists.keySet());
                                                saveLists(file);
                                                lv.setAdapter(adapter);
                                            }
                                        });
                                        rename.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        });

                                        rename.show();
                                        break;
                                    case 2:
                                        ItemsList currList = ItemsList.currLists.get(listClicked);
                                        currList = null;
                                        ItemsList.currLists.remove(listClicked);
                                        namesList.remove(listClicked);
                                        saveLists(file);
                                        lv.setAdapter(adapter);
                                        Toast.makeText(mainContext, listClicked + " Deleted Successfully",
                                                Toast.LENGTH_SHORT).show();
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

        //Handling click event for add button.
        FloatingActionButton fab = findViewById(R.id.addListButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mainContext);
                builder.setTitle("Create New List");

                final EditText listName = new EditText(mainContext);
                listName.setHint("Enter List Name");
                builder.setView(listName);
                builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = listName.getText().toString();
                        ItemsList list = new ItemsList(name);
                        namesList.add(name);
                        saveLists(file);
                        lv.setAdapter(adapter);
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