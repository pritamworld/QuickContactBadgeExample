package com.modxroidlabs.quickcontactbadgeexample;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.QuickContactBadge;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
{
    private static String DEBUG_TAG = MainActivity.class.getCanonicalName();
    private final static int CONTACT_PICKER_RESULT = 100;
    private final static int REQUEST_CONTACTS = 200;
    Button btnGetContact;
    TextView tvPhoneNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Call a check for runtime permissions
        checkRuntimePermissions();

        tvPhoneNumber = (TextView)findViewById(R.id.tvPhoneNumber);
        btnGetContact = (Button)findViewById(R.id.btnGetContact);
        btnGetContact.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                        ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode)
            {
                case CONTACT_PICKER_RESULT:
                    Uri contactUri = data.getData();
                    FrameLayout badgeLargeHolder = (FrameLayout) findViewById(R.id.frameContacts);

                    QuickContactBadge badgeLarge = new QuickContactBadge(this);
                    badgeLarge.assignContactUri(contactUri);
                    badgeLarge.setMode(ContactsContract.QuickContact.MODE_LARGE);
                    badgeLarge.setImageResource(R.drawable.ic_contact);
                    badgeLargeHolder.addView(badgeLarge);

                    //Display contact data log
                    try
                    {
                        displayContactDetails(data);
                    }catch (Exception ee){
                        ee.printStackTrace();
                    }

                    break;
            }
        }
    }

    private void displayContactDetails(Intent data)
    {
        Uri result = data.getData();
        Log.v(DEBUG_TAG, "Got a result: "
                + result.toString());

        // get the contact id from the Uri
        String id = result.getLastPathSegment();

        // query for everything email
        Cursor cursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=?",
                new String[]{id}, null);

        cursor.moveToFirst();
        String columns[] = cursor.getColumnNames();
        for (String column : columns)
        {
            int index = cursor.getColumnIndex(column);
            Log.v(DEBUG_TAG, "Column: " + column + " == ["
                    + cursor.getString(index) + "]");


        }

        if (cursor.moveToFirst())
        {
            int emailIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);
            String email = cursor.getString(emailIdx);
            Log.v(DEBUG_TAG, "Got email: " + email);
            tvPhoneNumber.setText(email);
        }
    }

    public void checkRuntimePermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            /* Permission has not been granted by user previously.  Request it now. The system
             will present a dialog to the user requesting the permission, with options "accept",
             "deny", and a box to check "don't ask again". When the user chooses, the system
             will then fire the onRequestPermissionsResult() callback, passing in the user-defined
             integer defining the type of permission request (REQUEST_CONTACTS in this case)
             and the "accept" or "deny" user response.  We deal appropriately
             with the user response in our override of onRequestPermissionsResult() below.*/

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CONTACTS);
        } else {
            Log.i(DEBUG_TAG, "Permission has been granted");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        // Since this method may handle more than one type of permission, distinguish which one by a
        // switch on the requestCode that you defined that is passed back to you by the system.

        switch (requestCode) {

            // The permission response was for fine location
            case REQUEST_CONTACTS:
                Log.i(DEBUG_TAG, "Read contacts permission granted: requestCode=" + requestCode);
                // If the request was canceled by user, the results arrays are empty
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Permission was granted. Carry on as we would without the permission request



                } else {
                    Log.i(DEBUG_TAG, "onRequestPermissionsResult - permission denied: requestCode=" + requestCode);
                }
                return;

        }
    }

}
