package org.boxtree.vic.contactsapp;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import org.boxtree.vic.contactsapp.vo.Contact;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ContactFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ContactFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ContactFragment newInstance(int columnCount) {
        ContactFragment fragment = new ContactFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }


    // A UI Fragment must inflate its View
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;

            // set adapter first and layout manager next, otherwise view can not be clicked
            recyclerView.setAdapter(new MyContactRecyclerViewAdapter(getContacts(), mListener));

            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
        }
        return view;
    }


    // called when fragment is first attatched to its context
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }




    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Contact item);

    }


    private List<Contact> getContacts() {

        List<Contact> contacts = new ArrayList<Contact>();

        ContentResolver contentResolver = getActivity().getContentResolver();
        String[] projectionFields = new String[]{ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.LOOKUP_KEY};
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, projectionFields, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

//        ContactsContract.CommonDataKinds.Nickname.NAME,
//                ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS,
//                ContactsContract.CommonDataKinds.Phone.NUMBER,
//                ContactsContract.CommonDataKinds.Email.ADDRESS,

        if (cursor != null) { // execute if something found

            try {
                if (cursor.getCount() > 0) { //
                    while (cursor.moveToNext()) {

                        // populate the Contacts info
                        String id = cursor.getString(0);
                        String name = cursor.getString(1);
                        String lookupKey = cursor.getString(2);
                        Contact contact = new Contact(id, name,  null, null, null, null, lookupKey);

                        Log.d("ContentFragmentDebug", "Name - id - lookup: " + name + " - " + id + " - " + " - " + lookupKey);


                        // email
//                        String[] emailFields = new String[]{ContactsContract.Data._ID, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.LABEL};
                        Cursor emails = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=" + id, null, null);
                        if (emails.getCount() > 0) {
                            emails.moveToNext(); // just get first one
                            String emailAddress = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                            Log.d("ContentFragmentDebug", "Email address is " + emailAddress);
                            contact.setEmail(emailAddress);
                        }
                        emails.close();

                        // nickname, phone, address
                        Cursor nickCur = contentResolver.query(ContactsContract.Data.CONTENT_URI, null, ContactsContract.Data.CONTACT_ID + "=" + id, null, null);
                        if (nickCur.getCount() > 0) {
                            while (nickCur.moveToNext()) {
                                String genericField = nickCur.getString(nickCur.getColumnIndex(ContactsContract.CommonDataKinds.Nickname.NAME));
                                String mimetype = nickCur.getString(nickCur.getColumnIndex(ContactsContract.Data.MIMETYPE)) ;//
                                Log.d("ContentFragmentDebug", "Nickname is " + genericField + " and mimetype is " + mimetype);


                                // nickname
                                if (mimetype.equals(ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE)) {
                                    contact.setNickname(genericField);
                                    Log.d("ContentFragmentDebug", "Nickname is " + genericField + " and mimetype is " + mimetype);

                                }
                                // phone number
                                else if (mimetype.equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {
                                    contact.setPhone(genericField);
                                    Log.d("ContentFragmentDebug", "Phone is " + genericField + " and mimetype is " + mimetype);


                                }
                                // address
                                else if (mimetype.equals(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)) {
                                    contact.setAddress((genericField));
                                    Log.d("ContentFragmentDebug", "Postals is " + genericField + " and mimetype is " + mimetype);

                                } else {
                                    Log.d("ContentFragmentDebug", "GenericField is " + genericField + " and mimetype is " + mimetype);

                                }

                            }
                        }
                        nickCur.close();

                        Log.d("ContentFragmentDebug", "final contact is " + contact);

                        contacts.add(contact);

                    }
                }
            } catch (Exception e) {
                Log.e("cursor_closure", "cursor not closed properly"); // TODO proper android error logging

            } finally {

                if (!cursor.isClosed()) {

                    cursor.close();
                }
            }

        }

        return contacts;
    }




    /* SearchView on Main Activity calls this */
    public void searchRequested(String newText) {

        Log.d ("ContactFrag", "New Filterable sent -- " + newText);

        RecyclerView view  = (RecyclerView) this.getView();
        MyContactRecyclerViewAdapter adapter = (MyContactRecyclerViewAdapter) view.getAdapter();

        adapter.filter(newText);


    }


    /* Save button on AddContact calls this */
    public void refreshRequsted() {


        Log.d ("ContactFrag", "New refresh request sent after contact added -- ");

        RecyclerView view  = (RecyclerView) this.getView();
        MyContactRecyclerViewAdapter adapter = (MyContactRecyclerViewAdapter) view.getAdapter();

        adapter.refreshContactListFromSource(getContacts());

    }


}
