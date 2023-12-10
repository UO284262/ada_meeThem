package com.ada.ada_meethem.modelo

import android.os.Parcel
import android.os.Parcelable

// Representa un contacto del móvil
data class Contact(
    val number: String?,
    val username: String?
): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(number)
        parcel.writeString(username)
    }

    override fun describeContents(): Int {
        return 0
    }

    fun toROM() : com.ada.ada_meethem.database.entities.Contact {
        return com.ada.ada_meethem.database.entities.Contact(
            number!!,
            "",
            username,
        )
    }

    companion object CREATOR : Parcelable.Creator<Contact> {
        override fun createFromParcel(parcel: Parcel): Contact {
            return Contact(parcel)
        }

        override fun newArray(size: Int): Array<Contact?> {
            return arrayOfNulls(size)
        }
    }
}