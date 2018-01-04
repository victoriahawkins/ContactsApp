package org.boxtree.vic.contactsapp;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.boxtree.vic.contactsapp.vo.Contact;

import java.io.InputStream;
import java.util.ArrayList;

public class EditContactActivity extends AppCompatActivity implements View.OnClickListener {


    private Contact mContact;

    private EditText mNameView;
    private EditText mNickNameView;
    private EditText mPhoneView;
    private EditText mAddressView;
    private EditText mEmailView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // setup buttons, floating action one-touch calling, sms, save and delete
        FloatingActionButton callFab = (FloatingActionButton) findViewById(R.id.callActionButton);
        callFab.setOnClickListener(this);

        FloatingActionButton smsFab = (FloatingActionButton) findViewById(R.id.messageActionButton);
        smsFab.setOnClickListener(this);

        Button saveButton = (Button) findViewById(R.id.saveContactButton);
        saveButton.setOnClickListener(this);

        Button deleteButton = (Button) findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(this);


        Intent i = this.getIntent();
        mContact = i.getParcelableExtra("ContactParcelable");

        mNameView = (EditText) findViewById(R.id.nameEditText);
        mNameView.setText(getContact().getName());
        mNickNameView = (EditText) findViewById(R.id.nicknameEditText);
        mNickNameView.setText(getContact().getNickname());
        mPhoneView = (EditText) findViewById(R.id.phoneNumberEditText);
        mPhoneView.setText(getContact().getPhone());
        mAddressView = (EditText) findViewById(R.id.postalAddressEditText);
        mAddressView.setText(getContact().getAddress());
        mEmailView = (EditText) findViewById(R.id.emailEditText);
        mEmailView.setText(getContact().getEmail());


        Bitmap contactPhoto = loadContactPhoto();
        if (contactPhoto != null) { // replace default with Android Contacts picture
            ImageView contactpicture = (ImageView) findViewById(R.id.imageView);
            contactpicture.setImageBitmap(contactPhoto);
        }



    }

    @Override
    public void onClick(View v) {


        switch (v.getId()) {

            case R.id.callActionButton:
                callContact(); // one-touch call
                break;
            case R.id.messageActionButton:
                smsContact(); // one-touch sms
                break;
            case R.id.saveContactButton:

                // can click outside to save
                AlertDialog saveAlertDialog = new AlertDialog.Builder(EditContactActivity.this).create();
                saveAlertDialog.setTitle("Save Contact");
                saveAlertDialog.setMessage("Okay to save Contact?");
                saveAlertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
                                saveEdits();

                            }
                        });
                saveAlertDialog.show();
                break;

            case R.id.deleteButton:


                AlertDialog.Builder builder = new AlertDialog.Builder(EditContactActivity.this);
                builder.setTitle("Delete Contact");
                builder.setMessage("Are you sure you want to delete " + getContact().getName() + "?");
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        deleteContact();
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();

                break;
            default:
                break;
        }
    }

    private void deleteContact() {

        Contact c = getContact();

        int result = 0;
        ContentResolver cr = getContentResolver();
        try {
            Uri uri = Uri.withAppendedPath(
                    ContactsContract.Contacts.CONTENT_LOOKUP_URI, c.getLookupKey());
            result = cr.delete(uri, null, null);
            Log.d("EditContactActivity", "Deleted contact " + c);
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }

        Toast.makeText(getBaseContext(), "Contact Deleted", Toast.LENGTH_SHORT).show();

        // TODO something with result code
        Intent data = new Intent();
        setResult(RESULT_OK, data);

        finish();

    }

    private void saveEdits() {


        ContentResolver contentResolver = getContentResolver();


        ArrayList<ContentProviderOperation> ops = new ArrayList<android.content.ContentProviderOperation>();


        try {

            if (fieldChanged(mNameView, mContact.getName())) {
                // update name
                String updatedName = mNameView.getText().toString();

                ops.add(ContentProviderOperation
                        .newUpdate(ContactsContract.Data.CONTENT_URI)
                        .withSelection(
                                ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "=?",
                                new String[]{
                                        getContact().getId(),
                                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE})
                        .withValue(
                                ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                                updatedName).build());
            }

            if (fieldChanged(mNickNameView, mContact.getNickname())) {
                // TODO update nickanme

            }


            if (fieldChanged(mPhoneView, mContact.getPhone())) {
                // update phone

                String updatedPhone = mPhoneView.getText().toString();

                ops.add(ContentProviderOperation
                        .newUpdate(ContactsContract.Data.CONTENT_URI)
                        .withSelection(
                                ContactsContract.Data.LOOKUP_KEY + "=? AND " + ContactsContract.Data.MIMETYPE + "=?",
                                new String[]{getContact().getLookupKey(), ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE})
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, updatedPhone)
                        .build());

            }

            if (fieldChanged(mAddressView, mContact.getAddress())) {

                String updatedAddress = mAddressView.getText().toString();

                ops.add(ContentProviderOperation
                        .newUpdate(ContactsContract.Data.CONTENT_URI)
                        .withSelection(
                                ContactsContract.Data.LOOKUP_KEY + "=? AND " + ContactsContract.Data.MIMETYPE + "=?",
                                new String[]{getContact().getLookupKey(), ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE})
                        .withValue(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS, updatedAddress)
                        .build());

            }

            if (fieldChanged(mEmailView, mContact.getEmail())) {

                String updatedEmail = mEmailView.getText().toString();

                ops.add(ContentProviderOperation
                        .newUpdate(ContactsContract.Data.CONTENT_URI)
                        .withSelection(
                                ContactsContract.Data.LOOKUP_KEY + "=? AND " + ContactsContract.Data.MIMETYPE + "=?",
                                new String[]{getContact().getLookupKey(), ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE})
                        .withValue(ContactsContract.CommonDataKinds.Email.DATA, updatedEmail)
                        .build());

            }

            contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);


            Toast.makeText(getBaseContext(), "Contact Saved", Toast.LENGTH_SHORT).show();

            Intent data = new Intent();
            setResult(RESULT_OK, data);
            finish();
        } catch (Exception e) {

            Log.e("EditContactActivity", "Update contact failure for " + getContact() + " with message: " + e.getMessage());
        }

    }

    private boolean fieldChanged(EditText mView, String originalValue) {
        if (originalValue == null && mView.getText() != null) {
            return true;
        } else if (originalValue == null && mView.getText() == null) {
            return false;
        }

        return !originalValue.equals(mView.getText().toString());
    }

    // one touch calling
    private void callContact() {
//        Snackbar.make(v, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show();

        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + getContact().getPhone()));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }


    private void smsContact() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("smsto:" + getContact().getPhone()));  // This ensures only SMS apps respond
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }


    public Contact getContact() {
        return mContact;
    }

    public void setContact(Contact mContact) {
        this.mContact = mContact;
    }

    public Bitmap loadContactPhoto() {
        ContentResolver cr = getContentResolver();
        Uri uri = ContentUris.withAppendedId(
                ContactsContract.Contacts.CONTENT_URI, Long.valueOf(getContact().getId()));
        InputStream input = ContactsContract.Contacts
                .openContactPhotoInputStream(cr, uri);
        if (input == null) {
            return null;
        }
        return BitmapFactory.decodeStream(input);
    }
}
