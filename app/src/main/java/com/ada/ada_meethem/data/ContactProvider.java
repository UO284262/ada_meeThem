package com.ada.ada_meethem.data;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;

import androidx.core.content.ContextCompat;

import com.ada.ada_meethem.database.entities.Contact;

import java.util.ArrayList;
import java.util.List;

// Clase usada para extraer los contactos del proveedor de contactos
public class ContactProvider {

    private final Context context;

    public ContactProvider(Context context) {
        this.context = context;
    }

    /**
     * Hace una consulta al proveedor de contenido para obtener los contactos del móvil.
     * @param selection string con el criterio para seleccionar contactos, null implica
     *                   recuperar todos los contactos.
     * @param selectionArgs array de los argumentos usados en el string selection. Null si
     *                      no se emplean parámetros en la claúsula de selección.
     * @return lista de contactos encontrados o null si no encuentra ninguno.
     */
    public List<Contact> getContacts(String selection, String[] selectionArgs) {
        // Antes de nada, hay que comprobar que el usuario haya aceptado los permisos de lectura
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED)
            return null;

        // Columnas que se recuperarán de cada contacto
        String[] projection = {
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.HAS_PHONE_NUMBER
        };
        // Columna por la que se ordenará el resultado
        String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;

        Cursor cursor = context.getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
        );

        return getContactListFromCursor(cursor);
    }

    private List<Contact> getContactListFromCursor(Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0) // cursor inexistente o vacío
            return null;

        List<Contact> contacts = new ArrayList<>();
        while (cursor.moveToNext()) {
            int hasPhoneNumberColumn = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
            int hasPhoneNumber = cursor.getInt(hasPhoneNumberColumn);

            // El contacto se añade únicamente si tiene al menos un número de teléfono asociado
            if (hasPhoneNumber != 0) {
                int nameColumn = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                int contactIdColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID);

                String name = cursor.getString(nameColumn);
                String contactId = cursor.getString(contactIdColumn);
                List<String> numbers = getNumberListFromContactId(contactId);

                // Se crea un contacto por cada número de teléfono existente
                for (String number: numbers)
                    contacts.add(new Contact(parsePhoneNumber(number),"",name));
            }
        }
        cursor.close();
        return contacts;
    }

    // Recupera la lista de teléfonos asociados con el contacto
    // Devuelve una lista vacía si no se encuentran teléfonos
    private List<String> getNumberListFromContactId(String contactId) {
        String[] projection = {
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.TYPE
        };
        String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "= ?";
        String[] selectionArgs = {contactId};
        Cursor cursor = context.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null
        );

        // Si el cursor es inválido o no tiene elementos, se devuelve una lista vacía
        if (cursor == null || cursor.getCount() == 0)
            return new ArrayList<>();

        List<String> phones = new ArrayList<>();
        while (cursor.moveToNext()) {
            int phoneColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            String phone = cursor.getString(phoneColumn);
            phones.add(phone);
        }
        cursor.close();
        return  phones;
    }

    private String parsePhoneNumber(String number) {
        number = number.replace("(","");
        number = number.replace(")","");
        number = number.replace("-","");
        number = number.replace(" ","");
        switch(number.length()) {
            case 9: return "+34" + number;
            case 10: return "+1" + number;
            default: return number;
        }
    }

}
