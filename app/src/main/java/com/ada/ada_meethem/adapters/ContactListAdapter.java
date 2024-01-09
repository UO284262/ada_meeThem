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
import java.util.List;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ContactViewHolder> implements Filterable {

    // Interfaz para manejar el evento click sobre un elemento
    public interface OnItemClickListener {
        void onItemClick(Contact item);
    }

    private List<Contact> contacts;
    private List<Contact> contactsFiltered;
    private List<ContactViewHolder> viewHolders = new ArrayList<>();
    private List<Contact> selectedContacts; // contactos seleccionados anteriormente
    private List<CheckBox> checkBoxes = new ArrayList<>();
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
        viewHolders.add(holder);
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    // Devuelve una lista con los contactos seleccionados (checkbox a true)
    public List<Contact> getContactsSelected() {
        List<Contact> contactsSelected = new ArrayList<>();

        for (int i = 0; i < checkBoxes.size(); i++)
            if (checkBoxes.get(i).isChecked())
                contactsSelected.add(contacts.get(i));
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
                List<Contact> resultContacts = (List<Contact>) results.values;
                for (Contact contact : contacts) {
                    int i = contacts.indexOf(contact);
                    View itemView = viewHolders.get(i).itemView;

                    // Activa o desactiva el item del reclycler view
                    if (resultContacts.contains(contact)) {
                        itemView.setVisibility(View.VISIBLE);
                        float dp = itemView.getContext().getResources().getDisplayMetrics().density;
                        itemView.getLayoutParams().height = (int) (72 * dp); // reestablece la altura
                    } else {
                        itemView.setVisibility(View.GONE);
                        itemView.getLayoutParams().height = 0; // para que no queden huecos
                    }
                }
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
            checkBoxes.add(contactCheckBox);

            if (selectedContacts != null && selectedContacts.contains(contact))
                contactCheckBox.setChecked(true);

            // cargar imagen
            // TODO Picasso.get().load(plan.getImageUrl()).into(contactImage);

            itemView.setOnClickListener(view -> {
                contactCheckBox.setChecked(!contactCheckBox.isChecked());
                listener.onItemClick(contact);
            });
        }
    }
}