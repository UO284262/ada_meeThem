package com.ada.ada_meethem.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ada.ada_meethem.R;
import com.ada.ada_meethem.adapters.ContactListAdapter;
import com.ada.ada_meethem.data.ContactProvider;
import com.ada.ada_meethem.modelo.Contact;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class ContactPickerFragment extends Fragment {

    // Contactos extraídos del móvil
    private List<Contact> contacts;
    private RecyclerView contactsRecyclerView;

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

        contactsRecyclerView = root.findViewById(R.id.contactPickerRecyclerView);
        contactsRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(root.getContext());
        contactsRecyclerView.setLayoutManager(layoutManager);

        // Load the contacts
        loadContacts();

        // Establece el adapter al Recycler view
        loadRecyclerContactListAdapter();
        return root;
    }

    private void loadRecyclerContactListAdapter() {
        if (contacts == null)
            return;
        if (contacts.isEmpty())
            Snackbar.make(getActivity().findViewById(android.R.id.content),
                    R.string.contact_list_is_empty,
                    Snackbar.LENGTH_LONG).show();

        ContactListAdapter clAdapter= new ContactListAdapter(contacts,
                plan -> { // TODO
                });
        contactsRecyclerView.setAdapter(clAdapter);
    }

    // Extrae los contactos del proveedor de contactos del móvil
    private void loadContacts() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED)
            requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS);
        else
            contacts = new ContactProvider(getContext()).getContacts(null, null);
    }

    // Define el modo en el que la app controla la respuesta del usuario a la solicitud del permiso.
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    contacts = new ContactProvider(getContext()).getContacts(null, null);
                    loadRecyclerContactListAdapter();
                } else
                    Snackbar.make(getActivity().findViewById(android.R.id.content),
                            R.string.read_contact_permissions_not_acepted,
                            Snackbar.LENGTH_LONG).show();
            });
}