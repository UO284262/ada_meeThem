package com.ada.ada_meethem.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
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
import com.ada.ada_meethem.util.ContactLoaderFromProvider;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class ContactPickerFragment extends Fragment {

    public static final String SELECTED_CONTACTS = "selected_contacts";

    // Contactos extraídos del móvil
    private List<Contact> contacts;
    private RecyclerView contactsRecyclerView;
    private ProgressBar progressBar;
    private ContactListAdapter clAdapter;
    private Bundle bundle; // para almacenar los datos del createPlanFragment y los contactos seleccionados
    private ContactLoaderFromProvider contactLoader;

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

        progressBar = root.findViewById(R.id.progressBar);
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

        // Habilitamos el menú
        setHasOptionsMenu(true);

        // Crea el contactLoader
        contactLoader = new ContactLoaderFromProvider(requireContext(), requestPermissionLauncher, requireActivity());
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

    // Extrae los contactos cacheados de la base de datos
    private void loadContacts() {
        ContactDAO cdb = ContactDatabase.getDatabase(getContext()).getContactDAO();
        contacts = cdb.getAll();
    }

    // Crea el menú de este fragmento (contiene el botón de buscar y refrescar contactos)
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.contact_picker_menu, menu);
        searchContactsViewSetUp(menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    // Configura el searchView usado para buscar contactos
    private void searchContactsViewSetUp(Menu menu) {
        MenuItem menuItemBuscar = menu.findItem(R.id.menu_search_contacts);
        SearchView searchView = (SearchView) menuItemBuscar.getActionView();
        searchView.setQueryHint(getString(R.string.contacts_search_view_query_hint));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                clAdapter.getFilter().filter(query); // filtra contactos
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                clAdapter.getFilter().filter(newText); // filtra contactos
                return false;
            }
        });
    }

    // Añade funcionalidad a los elementos del menú
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_search_contacts)
            return true;

        if (item.getItemId() == R.id.menu_refresh_contacts) {
            progressBar.setVisibility(View.VISIBLE);
            contacts = contactLoader.loadContacts();

            // Establece el adapter al Recycler view
            loadRecyclerContactListAdapter();

            Snackbar.make(requireActivity().findViewById(android.R.id.content),
                            R.string.contacts_refresh_OK,
                            Snackbar.LENGTH_SHORT)
                    .show();
            progressBar.setVisibility(View.GONE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                isGranted -> contactLoader.requestPermissionLauncherCallback(isGranted));

}