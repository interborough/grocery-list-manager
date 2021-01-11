# Grocery List Manager

## 1) System Overview
This Android app allows users to create, read, update, and delete various grocery lists. A grocery list consists of items or products that a grocery store has that a user wishes to purchase. 

## 2) Quick Start Guide

### 2.1) Creating a New List
Upon launching the app for the first time, you will be greeted with an empty screen with a purple button (with a plus inside of it) in the bottom righthand corner of the screen. To create a new grocery list, click on this button. A prompt will appear asking you for the name of the new list that you wish to create. Enter a suitable name into the text box, and click on the "Create" button when you are satisfied with the name you have chosen. After doing this, you should now see the list name displayed on the screen. 

### 2.2) Renaming an Existing List
To rename an existing list, you must first click on the name of the list that you wish to rename. A prompt will appear with four options on it. Click on "Rename List" from these options. Another prompt will appear, asking you for the new name that you wish to rename the existing list to. After entering an acceptable name, click on the "Rename" button. After doing this, the new list name should be displayed on the screen. 

### 2.3) Deleting a List
To delete an existing list, you must first click on the name of the list that you wish to delete. A prompt will appear with four options on it. Click on "Delete List" from these options. After doing this, the list you removed will no longer appear on the screen, and a message should appear on the bottom of the screen informing you that the list has been deleted successfully. 

### 2.4) Editing a List
The section below deals with adding, updating, and removing items from a particular list. To access this functionality, first click on the name of the list which you wish to edit. A prompt will appear with four options on it. Click on "Edit List" from these options. After doing this, you will be taken to a new empty screen with another purple button (with a plus inside of it) in the bottom righthand corner of the screen. The title of this window can be seen on the top of the screen, and it should match the name of the list that you are editing. The below sections assume that you are already in this new window view.

#### 2.4.1) Adding an Item to a List 
To add an item to a list, click on the purple button in the bottom righthand corner of the screen. A prompt will appear asking you for the method by which you wish to add an item to the list. The "Select Item from List" option will provide you with a list of all the item types on file in the item database, and you must choose one from these options. After this, you will choose the specific item to add of that type (ex: Choose type dairy, and then choose milk). The "Enter Item Name" option allows you to enter the name of the item that you wish to add. The item database will be searched, and if a similar item is found it will be added to the list. If there are no matches, however, you will be allowed to add the item to the database and then add it to your list. 

#### 2.4.2) Removing an Item From a List
To remove an item from a list, click on the name of the item which you wish to remove. A prompt will appear with four options on it. Click on "Delete Item" from these options. After doing this, the item you removed will no longer appear on the screen, and a message should appear on the bottom of the screen informing you that the item has been deleted successfully. 

#### 2.4.3) Changing the Quantity of an Item
To change the quantity of an item, first click on the quantity (the number) that you wish to change. A prompt will appear asking you for the new quantity. The new quantity must be a positive integer (greater than 0) for it to be valid. Enter the new quantity, and click on the "Change Quantity" button when you are finished. The quantity of the item should now be updated. 

#### 2.4.4) Checking and Unchecking an Item
To check off an item in a list, click on the checkbox that is in the correct row for the item that you wish to check. If the item is unchecked it will be checked off, and if the item is already checked it will be unchecked. 

#### 2.4.5) Unchecking All Items 
To uncheck all previously checked items at once, click on the name of any item in the list. A prompt will appear with four options on it. Click on "Uncheck All Items". All checkmarks should now be removed from the list items.

### 2.5) Exiting the Application
To exit the application, simply click on the square button that is part of the Android phone system tray. From here, you can swipe the current process away and thereby close the application. All of your previously created lists will be saved.

## 3) Implementation Notes

### 3.1) Application Creation Details
This app was created as a part of my Software Engineering class (CSCI 370) at CUNY Queens College. While this assignment is a group project, I was the only programmer/developer in my group, meaning that all of the implementation and code was done by myself. This readme was also created by myself. 

### 3.2) Room Database Functionality
This application utlizes the built in Room Database functionality provided by Android. This means that the items that the user adds to a grocery list are saved in a local database for future use, allowing the user to select items from a list instead of having to manually enter their names every time they wish to add an item. For example, let's consider a scenario where the user wishes to add apples to two separate grocery lists. The first time the user adds them to a list they must enter in the name of the item, which adds it to both the list itself and the local database. The second time, however, the user can simply select the item from a list of the items in the database.  

### 3.3) Known Issues
- The database queries are performed on the main thread of the application. This is not ideal as it can potentially cause issues with updating the UI. However, this application doesn't seem to be greatly impacted by this flaw, and works as intended. Since this application is rather simple and isn't intended for widespread use (being only a class assignment) I felt that it was unnecessary to implement this functionality in the strictly correct way. Not to mention that this assignment was my second time using Android Studio at all, meaning that there was a steep learning curve in order to get the basic functionality working in the first place!

- The functions to save and load grocery lists are repeated in separate two classes. 