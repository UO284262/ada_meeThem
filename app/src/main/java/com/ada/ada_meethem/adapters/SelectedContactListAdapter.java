package com.ada.ada_meethem.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ada.ada_meethem.R;
import com.ada.ada_meethem.database.entities.Contact;

import java.util.List;

public class SelectedContactListAdapter extends RecyclerView.Adapter<SelectedContactListAdapter.SelectedContactViewHolder> {

    // Interfaz para manejar el evento click sobre un elemento
    public interface OnItemClickListener {
        void onItemClick(Contact item);
    }

    private List<Contact> contacts;
    private final OnItemClickListener listener;

    public SelectedContactListAdapter(List<Contact> contacts, OnItemClickListener listener) {
        this.contacts = contacts;
        this.listener = listener;
    }

    // Indica el layout a inflar para usar en la vista
    @NonNull
    @Override
    public SelectedContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Creamos la vista con el layout para un elemento
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_create_plan_recycler_line, parent, false);
        return new SelectedContactViewHolder(itemView);
    }

    // Asocia el contenido a los componentes de la vista
    @Override
    public void onBindViewHolder(@NonNull SelectedContactViewHolder holder, int position) {
        Contact contact = contacts.get(position);
        holder.bindContact(contact, listener);
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }


    public class SelectedContactViewHolder extends RecyclerView.ViewHolder {

        private ImageView contactImage;
        private TextView contactSurname;

        public SelectedContactViewHolder(@NonNull View itemView) {
            super(itemView);
            contactImage = itemView.findViewById(R.id.contactImage);
            contactSurname = itemView.findViewById(R.id.contactSurnameText);
        }

        // asignar valores a los componentes
        public void bindContact(final Contact contact, final OnItemClickListener listener) {
            contactSurname.setText(contact.getContactName());

            // cargar imagen
            // TODO Picasso.get().load(plan.getImageUrl()).into(contactImage);

            itemView.setOnClickListener(view -> listener.onItemClick(contact));
        }
    }
}
