package com.ada.ada_meethem.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ada.ada_meethem.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CreatePlanFragment extends Fragment {

    private TextView tvName; // TODO completar vista
    private TextView tvNumber;

    public CreatePlanFragment() {
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
        ContactPickerFragment contactPickerFragment = new ContactPickerFragment();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, contactPickerFragment).commit();
    }
}