package com.ada.ada_meethem.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ada.ada_meethem.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ContactsFragment extends Fragment {

    private static final int PICK_CONTACT_REQUEST = 1;

    private TextView tvName;
    private TextView tvNumber;

    public ContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_contacts, container, false);

        tvName = root.findViewById(R.id.tvName);
        tvNumber = root.findViewById(R.id.tvNumber);

        FloatingActionButton fab = root.findViewById(R.id.fabPickContacts);
        fab.setOnClickListener(view -> pickContacts());

        return root;
    }


    private void pickContacts() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(Uri.parse("content://contacts"), ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(intent, PICK_CONTACT_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_CONTACT_REQUEST) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                Uri uri = data.getData();

                Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);

                if (cursor != null && cursor.moveToFirst()) {
                    int nameColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                    int numberColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                    String name = cursor.getString(nameColumn);
                    String number = cursor.getString(numberColumn);

                    tvName.setText(name);
                    tvNumber.setText(number);
                }
            }
        }
    }
}