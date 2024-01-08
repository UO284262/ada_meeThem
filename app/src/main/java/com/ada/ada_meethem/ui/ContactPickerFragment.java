package com.ada.ada_meethem.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ada.ada_meethem.R;
import com.ada.ada_meethem.adapters.ContactListAdapter;
import com.ada.ada_meethem.data.ContactProvider;
import com.ada.ada_meethem.database.ContactDatabase;
import com.ada.ada_meethem.database.daos.ContactDAO;
import com.ada.ada_meethem.database.entities.Contact;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class ContactPickerFragment extends Fragment {

    public static final String SELECTED_CONTACTS = "selected_contacts";

    // Contactos extraídos del móvil
    private List<Contact> contacts;
    private RecyclerView contactsRecyclerView;
    private ContactListAdapter clAdapter;
    private Bundle bundle; // para almacenar los datos del createPlanFragment y los contactos seleccionados

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

        // Listener del fab
        FloatingActionButton fab = root.findViewById(R.id.fabContactsSelected);
        fab.setOnClickListener(this::navigateToCreatePlan);
        return root;
    }

    // Recibe los datos del createPlanFragment y los guarda
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bundle = getArguments() != null ? getArguments() : new Bundle();

        // Se marcan los contactos que habían sido seleccionados previamente
        List<Contact> selectedContacts = getArguments().getParcelableArrayList(SELECTED_CONTACTS);
        if (selectedContacts != null)
            clAdapter.setSelectedContacts(selectedContacts);
    }

    // Guarda los contactos seleccionados en una lista y los envía al CreatePlanFragment
    private void navigateToCreatePlan(View view) {
        List<Contact> selectedContacts = clAdapter.getContactsSelected();
        bundle.putParcelableArrayList(SELECTED_CONTACTS, (ArrayList<Contact>) selectedContacts);
        Navigation.findNavController(view).navigate(R.id.action_contactPickerFragment_to_nav_plan_create, bundle);
    }

    private void loadRecyclerContactListAdapter() {
        if (contacts == null)
            return;
        if (contacts.isEmpty())
            Snackbar.make(getActivity().findViewById(android.R.id.content),
                    R.string.contact_list_is_empty,
                    Snackbar.LENGTH_LONG).show();

        clAdapter = new ContactListAdapter(contacts, plan -> {});
        contactsRecyclerView.setAdapter(clAdapter);
    }

    // Extrae los contactos del proveedor de contactos del móvil
    private void loadContacts() {
        ContactDAO cdb = ContactDatabase.getDatabase(getContext()).getContactDAO();
        contacts = cdb.getAll();
    }
}