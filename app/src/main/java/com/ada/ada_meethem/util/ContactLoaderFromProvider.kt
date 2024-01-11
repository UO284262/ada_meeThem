package com.ada.ada_meethem.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.ada.ada_meethem.R
import com.ada.ada_meethem.data.ContactProvider
import com.ada.ada_meethem.database.ContactDatabase
import com.ada.ada_meethem.database.entities.Contact
import com.google.android.material.snackbar.Snackbar

// Clase de utilidad para cargar contactos desde el proveedor de contactos, pidiendo los
// permisos de lectura requeridos
class ContactLoaderFromProvider(
    private val context: Context,
    private val requestPermissionLauncher: ActivityResultLauncher<String>,
    private val fragmentActivity: FragmentActivity
) {

    private var contacts: List<Contact> = ArrayList()

    // Carga los contactos si la apliación cuenta con los permisos adecuados
    fun loadContacts(): List<Contact> {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        )
            requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
        else
            getAndSafeContacts()
        return contacts
    }

    // Obtiene los contactos del proveedor y los guarda en una BD local
    private fun getAndSafeContacts() {
        try {
            contacts = ContactProvider(context).getContacts(null, null)
        } catch (e: Exception) {
            contacts = ArrayList()
        }
        safeContactsInLocalDB()
    }

    private fun safeContactsInLocalDB() {
        val cdao = ContactDatabase.getDatabase(context).contactDAO
        cdao.deleteAll() // Borramos todos los contactos
        for (contact in contacts) {
            cdao.add(contact) // Cargamos los contactos actualizados
        }
    }

    // Función de callback para el registerForActivityResult necesario para el requestPermissionLauncher
    fun requestPermissionLauncherCallback(isGranted: Boolean) {
        if (isGranted)
            getAndSafeContacts()
        else
            Snackbar.make(
                fragmentActivity.findViewById(android.R.id.content),
                R.string.read_contact_permissions_not_acepted,
                Snackbar.LENGTH_LONG
            )
                .show()
    }

}