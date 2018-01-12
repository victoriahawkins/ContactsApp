package org.boxtree.vic.contactsapp;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;

import org.boxtree.vic.contactsapp.vo.Contact;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ContactFragment.OnListFragmentInteractionListener  {

    private ListView mListView;
    private SearchView mSearchView;


    ArrayList<Contact> contactArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // set button click listener to add contact
        ImageButton addContactButton = (ImageButton) findViewById(R.id.addContactButton);
        addContactButton.setOnClickListener(this);

        // Fragment is used to display Contacts in List (RecyclerView) in main layout
        // Main activity implements OnListFragmentInteractionListener for coordination


        // search
        mSearchView = (SearchView) findViewById(R.id.searchView);
        mSearchView.setOnQueryTextListener( new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                Log.d ("SearchView", "Search Text entered is " + newText);


                // communicate with the fragment that a search input was typed
                ContactFragment contactFrag = (ContactFragment) getSupportFragmentManager().findFragmentById(R.id.contactFragment);

                contactFrag.searchRequested(newText);

                return true;
            }
        });


    }


    final private static int ADD_ACTIVITY = 1;
    @Override
    public void onClick(View v) {

        Intent addContactIntent = new Intent(MainActivity.this, AddContactActivity.class);
        startActivityForResult(addContactIntent, ADD_ACTIVITY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode== Activity.RESULT_OK){

            // refresh contact list
            ContactFragment contactFrag = (ContactFragment) getSupportFragmentManager().findFragmentById(R.id.contactFragment);
            contactFrag.refreshRequested();

            Log.d ("MainActivity", "Contact refresh requested for add/edit/delete");


        }
        else if(resultCode==Activity.RESULT_CANCELED){
            //Do nothing
            Log.d ("MainActivity", "Contact interaction Canceled");
        }


    }

    /* rev 1 backing arraylist and return list of names for listview */
    private String[] populateContacts() {

//        contactArrayList.add(new Contact("William Murray", "Bill", "200 Parmer Lane, Palm Springs, CA", "5-GROUNDHOG", "ilovepajamas@aol.com"));
//        contactArrayList.add(new Contact("John Christopher Reilly", "", "858 Aloha Dr, New York, NY", "345-335-5555", "donttouchmydrumset@media.net"));
//        contactArrayList.add(new Contact("John William Ferrell", "Will", "2323 NiceToKnowYou St, Irvine, CA", "234-444-6666", "fancysauce@gmail.com"));


        String[] listItems = new String[contactArrayList.size()];

        for (int i = 0; i < contactArrayList.size(); i++) {

            listItems[i] = contactArrayList.get(i).getName();
        }

        return listItems;
    }

    final private static int EDIT_OR_DELETE_ACTIVITY = 2;

    @Override
    public void onListFragmentInteraction(Contact item) {


        // when contact list item clicked, go to edit view, passing object as Parcelable
        Intent i = new Intent(this, EditContactActivity.class);
        i.putExtra("ContactParcelable", item);
        startActivityForResult(i, EDIT_OR_DELETE_ACTIVITY);



    }


    public static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        Log.d("ContactFrag", "received permission result");


        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    Log.d("ContactFrag", "read contacts permission is granted, refresh recycler view");

//                    refreshRequested();

                    // refresh contact list
                    ContactFragment contactFrag = (ContactFragment) getSupportFragmentManager().findFragmentById(R.id.contactFragment);
                    contactFrag.refreshRequested();

                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


}
