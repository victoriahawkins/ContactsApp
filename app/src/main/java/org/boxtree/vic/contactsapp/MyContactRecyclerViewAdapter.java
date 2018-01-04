package org.boxtree.vic.contactsapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.boxtree.vic.contactsapp.ContactFragment.OnListFragmentInteractionListener;
import org.boxtree.vic.contactsapp.vo.Contact;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Contact item} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class MyContactRecyclerViewAdapter extends RecyclerView.Adapter<MyContactRecyclerViewAdapter.ViewHolder> {


    private final List<Contact> mFullSet;
    private List<Contact> mValues; // displayed values

    private final OnListFragmentInteractionListener mListener;


    public MyContactRecyclerViewAdapter(List<Contact> items, OnListFragmentInteractionListener listener) {
        mFullSet = items;
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_contact, parent, false);
        return new ViewHolder(view);
    }

    /* Called by RecyclerView to display the data at the specified position. This method should
         * update the contents of the {@link ViewHolder#itemView} to reflect the item at the given
         * position. */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getId());  // TODO use getters
        holder.mContentView.setText(mValues.get(position).getName());  // TODO use getters

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    /*
    * Filter the  displayed list based on search arguments
     */
    public void filter(String newText) {

        List<Contact> temp = new ArrayList<>();
        for (Contact c : getmFullSet() ) { // start fresh values
            if (c.getName().toLowerCase().contains(newText.toLowerCase())) {
                temp.add(c);
            }
        }

        mValues = temp;
        notifyDataSetChanged();

    }

    public List<Contact> getmFullSet() {
        return mFullSet;
    }

    public void refreshContactListFromSource(List<Contact> contacts) {

        mValues = contacts;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public Contact mItem;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

}
