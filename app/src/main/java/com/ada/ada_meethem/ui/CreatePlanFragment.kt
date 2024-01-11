package com.ada.ada_meethem.ui

import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.PickVisualMediaRequest.Builder
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ada.ada_meethem.R
import com.ada.ada_meethem.adapters.SelectedContactListAdapter
import com.ada.ada_meethem.database.PlanDatabase
import com.ada.ada_meethem.database.entities.Contact
import com.ada.ada_meethem.modelo.Plan
import com.ada.ada_meethem.util.DatePickerFragment
import com.ada.ada_meethem.util.Util
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class CreatePlanFragment : Fragment() {

    companion object {
        // Claves para guardar el estado del fragmento
        private const val PLAN_IMAGE_URI = "plan_image_uri"
        private const val PLAN_NAME = "plan_name"
        private const val PLAN_MAX_PEOPLE = "plan_max_people"
    }

    private var selectedContacts: List<Contact>? = ArrayList()
    private var createdPlanId: String? = null
    private var planImageUri: Uri? = null
    private var planImage: ImageView? = null
    private var layoutPlanName: TextInputLayout? = null
    private var etPlanName: TextInputEditText? = null
    private var layoutMaxPeople: TextInputLayout? = null
    private var etMaxPeople: TextInputEditText? = null
    private var rvSelectedContacts: RecyclerView? = null
    private var dateTextView: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_create_plan, container, false)
        planImage = root.findViewById(R.id.planImage)
        layoutPlanName = root.findViewById(R.id.planNameTextInputLayout)
        etPlanName = root.findViewById(R.id.textInputPlanName)
        layoutMaxPeople = root.findViewById(R.id.planMaxPeopleTextInputLayout)
        etMaxPeople = root.findViewById(R.id.textInputPlanMaxPeople)
        rvSelectedContacts = root.findViewById(R.id.selectedContactsRecyclerView)

        val planImagePickerButton = root.findViewById<Button>(R.id.planImagePickerButton)
        planImagePickerButton.setOnClickListener { view: View? -> pickPlanImage() }

        dateTextView = root.findViewById<View>(R.id.dateTextView) as EditText
        dateTextView!!.setOnClickListener {showDatePickerDialog()}

        val btPickContacts = root.findViewById<Button>(R.id.buttonPickContacts)
        btPickContacts.setOnClickListener { view: View -> pickContacts(view) }
        val fab = root.findViewById<FloatingActionButton>(R.id.fabCreatePlan)
        fab.setOnClickListener { view: View -> createPlan(view) }

        // Configuramos el recycler view
        rvSelectedContactsSetUp(root)
        return root
    }

    // Recibe los datos del contactPickerFragment y los procesa
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            // Recuperamos la lista de contactos seleccionados
            selectedContacts =
                requireArguments().getParcelableArrayList(ContactPickerFragment.SELECTED_CONTACTS)
            val sclAdapter = SelectedContactListAdapter(selectedContacts) { contact: Contact? -> }
            rvSelectedContacts!!.adapter = sclAdapter

            // Restauramos el estado del fragment
            val planImageUriString = requireArguments().getString(PLAN_IMAGE_URI)
            if (planImageUriString != null) {
                planImageUri = Uri.parse(planImageUriString)
                planImage!!.setImageURI(planImageUri)
            }
            etPlanName!!.setText(requireArguments().getString(PLAN_NAME))
            etMaxPeople!!.setText(requireArguments().getString(PLAN_MAX_PEOPLE))
        }
    }

    override fun onResume() {
        super.onResume()
        planImage!!.setImageURI(planImageUri) // carga la imagen al reanudar el fragmento (navegación hacia atrás por ejemplo)
    }

    private fun showDatePickerDialog() {
        val newFragment =
            DatePickerFragment.newInstance { datePicker, year, month, day -> // +1 because January is zero
                val selectedDate = day.toString() + "-" + (month + 1) + "-" + year
                dateTextView!!.setText(selectedDate)
            }
        newFragment.show(requireActivity().supportFragmentManager, "datePicker")
    }

    private fun pickPlanImage() {
        // Lanza el photo picker y permite al usuario escoger 1 imagen.
        pickMedia.launch(
            Builder()
                .setMediaType(ImageOnly)
                .build()
        )
    }

    private fun pickContacts(view: View) {
        val bundle = Bundle()
        bundle.putString(PLAN_IMAGE_URI,
            if (planImageUri != null) planImageUri.toString() else null
        )
        bundle.putString(PLAN_NAME, etPlanName!!.text.toString())
        bundle.putString(PLAN_MAX_PEOPLE, etMaxPeople!!.text.toString())
        bundle.putParcelableArrayList(ContactPickerFragment.SELECTED_CONTACTS, selectedContacts as java.util.ArrayList<Contact?>?)

        findNavController(view).navigate(
            R.id.action_nav_plan_create_to_contactPickerFragment,
            bundle
        )
    }

    // Registers a photo picker activity launcher in single-select mode.
    var pickMedia =
        registerForActivityResult<PickVisualMediaRequest, Uri>(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                planImageUri = uri
                planImage!!.setImageURI(uri)
            }
        }

    private fun createPlan(view: View) {
        if (!validatePlan(view)) return
        createdPlanId = UUID.randomUUID().toString()
        subirImagen(planImageUri) // se sube la imagen a firebase
        val planName = etPlanName!!.text.toString()
        val maxPeople = etMaxPeople!!.text.toString().toInt()
        val creatorPlanNumber = FirebaseAuth.getInstance().currentUser!!.phoneNumber
        val user = Util.getCurrentUser()
        val fecha = dateTextView!!.text.toString()
        val plan = Plan(planName, user, maxPeople, "", ArrayList<String>(), createdPlanId, fecha)

        // Añadimos los contactos seleccionados al plan
        for (contact in selectedContacts!!) plan.addToPlan(contact.contactNumber)

        // Añadimos el creador del plan al plan
        plan.addToPlan(creatorPlanNumber)

        // Se añade el plan a Firebase
        PlanDatabase.getReference().child(plan.planId).setValue(plan)
        Snackbar.make(
            requireActivity().findViewById(android.R.id.content),
            R.string.plan_created_ok, Snackbar.LENGTH_LONG
        ).show()

        // Volvemos a Home
        findNavController(view).navigate(R.id.action_global_nav_home)
    }

    private fun validatePlan(view: View): Boolean {
        val isPlanNameInvalid = TextUtils.isEmpty(etPlanName!!.text)
        val isMaxPeopleInvalid = TextUtils.isEmpty(etMaxPeople!!.text)
        layoutPlanName!!.isErrorEnabled = false
        layoutMaxPeople!!.isErrorEnabled = isMaxPeopleInvalid
        if (planImageUri == null) {
            Snackbar.make(view, R.string.plan_creation_no_image, Snackbar.LENGTH_LONG).show()
            return false
        }
        if (isPlanNameInvalid) layoutPlanName!!.error = getString(R.string.plan_creation_no_name)
        if (isMaxPeopleInvalid) layoutMaxPeople!!.error =
            getString(R.string.plan_creation_no_max_people)
        return !isPlanNameInvalid && !isMaxPeopleInvalid
    }

    private fun rvSelectedContactsSetUp(root: View) {
        rvSelectedContacts!!.setHasFixedSize(true)
        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(root.context, 3)
        rvSelectedContacts!!.layoutManager = layoutManager
    }

    private fun subirImagen(selectedImageUri: Uri?) {
        // Obtiene una referencia a tu Firebase Storage
        val storageReference = FirebaseStorage.getInstance().reference
        val path = "planPhotos/plan-" + UUID.randomUUID()

        // Crea una referencia para la imagen en Storage
        val imageReference = storageReference.child(path)

        // Sube la imagen
        imageReference.putFile(selectedImageUri!!)
            .addOnSuccessListener {
                imageReference.downloadUrl.addOnSuccessListener { uri ->
                    PlanDatabase.getReference(
                        createdPlanId
                    ).child("imageUrl").setValue(uri.toString())
                }
            }
            .addOnFailureListener {
                // Maneja el error de la subida de la imagen
            }
    }

}