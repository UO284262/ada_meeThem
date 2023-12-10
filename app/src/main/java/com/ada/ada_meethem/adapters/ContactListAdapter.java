package com.ada.ada_meethem.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ada.ada_meethem.R;
import com.ada.ada_meethem.database.entities.Contact;

import java.util.ArrayList;
import java.util.List;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ContactViewHolder> {

    // Interfaz para manejar el evento click sobre un elemento
    public interface OnItemClickListener {
        void onItemClick(Contact item);
    }

    private List<Contact> contacts;
    private List<CheckBox> checkBoxes = new ArrayList<>();
    private final OnItemClickListener listener;

    public ContactListAdapter(List<Contact> contacts, OnItemClickListener listener) {
        this.contacts = contacts;
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

        for (int i = 0; i < checkBoxes.size(); i++)
            if (checkBoxes.get(i).isChecked())
                contactsSelected.add(contacts.get(i));
        return contactsSelected;
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

            // cargar imagen
            // TODO Picasso.get().load(plan.getImageUrl()).into(contactImage);

            itemView.setOnClickListener(view -> {
                contactCheckBox.setChecked(!contactCheckBox.isChecked());
                listener.onItemClick(contact);
            });
        }
    }
}