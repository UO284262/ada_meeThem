package com.ada.ada_meethem.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ada.ada_meethem.R;
import com.ada.ada_meethem.database.entities.Contact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ContactViewHolder> implements Filterable {

    // Interfaz para manejar el evento click sobre un elemento
    public interface OnItemClickListener {
        void onItemClick(Contact item);
    }

    private List<Contact> contacts;
    private List<Contact> contactsFiltered;
    private List<Contact> selectedContacts; // contactos seleccionados anteriormente
    private final Map<Contact, Boolean> contactsSelectedMap = new HashMap<>(); // contactos seleccionados actualmente
    private final OnItemClickListener listener;

    public ContactListAdapter(List<Contact> contacts, OnItemClickListener listener) {
        this.contacts = contacts;
        this.contactsFiltered = contacts;
        this.listener = listener;
    }

    // Indica el layout a inflar para usar en la vista
    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Creamos la vista con el layout para un elemento
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_contact_picker_recycler_line, parent, false);
        return new ContactViewHolder(itemView);
    }

    // Asocia el contenido a los componentes de la vista
    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contacts.get(position);
        holder.bindContact(contact, listener);
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    // Devuelve una lista con los contactos seleccionados (checkbox a true)
    public List<Contact> getContactsSelected() {
        List<Contact> contactsSelected = new ArrayList<>();

        for (Map.Entry<Contact, Boolean> entry : contactsSelectedMap.entrySet())
            if (entry.getValue())
                contactsSelected.add(entry.getKey());

        if (selectedContacts != null)
            contactsSelected.addAll(selectedContacts);
        return contactsSelected;
    }

    public void setSelectedContacts(List<Contact> selectedContacts) {
        this.selectedContacts = selectedContacts;
    }

    // Filtro de contactos por nombre
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint == null || constraint.length() == 0) {
                    filterResults.values = contactsFiltered;
                    filterResults.count = contactsFiltered.size();
                } else {
                    String searchStr = constraint.toString().toLowerCase();
                    List<Contact> contactsAux = new ArrayList<>();
                    for (Contact contact : contactsFiltered)
                        if (contact.getContactName().toLowerCase().contains(searchStr))
                            contactsAux.add(contact);
                    filterResults.values = contactsAux;
                    filterResults.count = contactsAux.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                contacts = (List<Contact>) results.values;
                notifyDataSetChanged();
            }
        };
    }


    public class ContactViewHolder extends RecyclerView.ViewHolder {

        private ImageView contactImage;
        private TextView contactSurname;
        private TextView contactNumber;
        private CheckBox contactCheckBox;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            contactImage = itemView.findViewById(R.id.contactImage);
            contactSurname = itemView.findViewById(R.id.contactSurnameText);
            contactNumber = itemView.findViewById(R.id.contactNumberText);
            contactCheckBox = itemView.findViewById(R.id.contactCheckBox);
        }

        // asignar valores a los componentes
        public void bindContact(final Contact contact, final OnItemClickListener listener) {
            contactSurname.setText(contact.getContactName());
            contactNumber.setText(contact.getContactNumber());

            if (selectedContacts != null && selectedContacts.contains(contact) && contactsSelectedMap.get(contact) == null) {
                contactsSelectedMap.put(contact, true);
                selectedContacts.remove(contact);
            }

            boolean checked = contactsSelectedMap.get(contact) != null ? contactsSelectedMap.get(contact) : false;
            contactCheckBox.setChecked(checked);

            // cargar imagen
            // TODO Picasso.get().load(plan.getImageUrl()).into(contactImage);

            itemView.setOnClickListener(view -> {
                contactCheckBox.setChecked(!contactCheckBox.isChecked());
                contactsSelectedMap.put(contact, contactCheckBox.isChecked());
                listener.onItemClick(contact);
            });
        }
    }
}
