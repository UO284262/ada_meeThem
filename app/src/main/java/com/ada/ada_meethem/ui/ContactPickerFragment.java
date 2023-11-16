package com.ada.ada_meethem.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ada.ada_meethem.R;
import com.ada.ada_meethem.adapters.ContactListAdapter;
import com.ada.ada_meethem.data.ContactProvider;
import com.ada.ada_meethem.modelo.Contact;

import java.util.ArrayList;
import java.util.List;

public class ContactPickerFragment extends Fragment {

    // Contactos extraídos del móvil
    private List<Contact> contacts;

    public ContactPickerFragment() {
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
        View root = inflater.inflate(R.layout.fragment_contact_picker, container, false);

        // Load the contacts
        loadContacts();

        RecyclerView contactsRecyclerView = root.findViewById(R.id.contactPickerRecyclerView);
        contactsRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(root.getContext());
        contactsRecyclerView.setLayoutManager(layoutManager);

        ContactListAdapter clAdapter= new ContactListAdapter(contacts,
                plan -> { // TODO
                });
        contactsRecyclerView.setAdapter(clAdapter);

        return root;
    }

    // Extrae los contactos del proveedor de contactos del móvil
    private void loadContacts() {
        // TODO comprobar si están los permisos concedidos
        contacts = new ContactProvider(getContext()).getContacts(null, null);
    }
}