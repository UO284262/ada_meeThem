package com.ada.ada_meethem.ui;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ada.ada_meethem.R;
import com.ada.ada_meethem.adapters.SelectedContactListAdapter;
import com.ada.ada_meethem.database.PlanDatabase;
import com.ada.ada_meethem.database.entities.Contact;
import com.ada.ada_meethem.modelo.Plan;
import com.ada.ada_meethem.util.Util;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CreatePlanFragment extends Fragment {

    // Claves para guardar el estado del fragmento
    private static final String PLAN_IMAGE_URI = "plan_image_uri";
    private static final String PLAN_NAME = "plan_name";
    private static final String PLAN_MAX_PEOPLE = "plan_max_people";

    private List<Contact> selectedContacts = new ArrayList<>();
    private String createdPlanId;
    private Uri planImageUri;
    private ImageView planImage;
    private TextInputLayout layoutPlanName;
    private TextInputEditText etPlanName;
    private TextInputLayout layoutMaxPeople;
    private TextInputEditText etMaxPeople;
    private RecyclerView rvSelectedContacts;

    public CreatePlanFragment() {
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
        View root = inflater.inflate(R.layout.fragment_create_plan, container, false);

        planImage = root.findViewById(R.id.planImage);
        layoutPlanName = root.findViewById(R.id.planNameTextInputLayout);
        etPlanName = root.findViewById(R.id.textInputPlanName);
        layoutMaxPeople = root.findViewById(R.id.planMaxPeopleTextInputLayout);
        etMaxPeople = root.findViewById(R.id.textInputPlanMaxPeople);
        rvSelectedContacts = root.findViewById(R.id.selectedContactsRecyclerView);

        Button planImagePickerButton = root.findViewById(R.id.planImagePickerButton);
        planImagePickerButton.setOnClickListener(view -> pickPlanImage());

        Button btPickContacts = root.findViewById(R.id.buttonPickContacts);
        btPickContacts.setOnClickListener(this::pickContacts);

        FloatingActionButton fab = root.findViewById(R.id.fabCreatePlan);
        fab.setOnClickListener(this::createPlan);

        // Configuramos el recycler view
        rvSelectedContactsSetUp(root);

        return root;
    }

    // Recibe los datos del contactPickerFragment y los procesa
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            // Recuperamos la lista de contactos seleccionados
            selectedContacts = getArguments().getParcelableArrayList(ContactPickerFragment.SELECTED_CONTACTS);
            SelectedContactListAdapter sclAdapter = new SelectedContactListAdapter(selectedContacts, contact -> {});
            rvSelectedContacts.setAdapter(sclAdapter);

            // Restauramos el estado del fragment
            String planImageUriString = getArguments().getString(PLAN_IMAGE_URI);
            if (planImageUriString != null) {
                planImageUri = Uri.parse(planImageUriString);
                planImage.setImageURI(planImageUri);
            }
            etPlanName.setText(getArguments().getString(PLAN_NAME));
            etMaxPeople.setText(getArguments().getString(PLAN_MAX_PEOPLE));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        planImage.setImageURI(planImageUri); // carga la imagen al reanudar el fragmento (navegación hacia atrás por ejemplo)
    }

    private void pickPlanImage() {
        // Lanza el photo picker y permite al usuario escoger 1 imagen.
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    private void pickContacts(View view) {
        Bundle bundle = new Bundle();
        bundle.putString(PLAN_IMAGE_URI, planImageUri != null ? planImageUri.toString() : null);
        bundle.putString(PLAN_NAME, etPlanName.getText().toString());
        bundle.putString(PLAN_MAX_PEOPLE, etMaxPeople.getText().toString());
        bundle.putParcelableArrayList(ContactPickerFragment.SELECTED_CONTACTS, (ArrayList<Contact>) selectedContacts);

        Navigation.findNavController(view).navigate(R.id.action_nav_plan_create_to_contactPickerFragment, bundle);
    }

    // Registers a photo picker activity launcher in single-select mode.
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                // Callback is invoked after the user selects a media item or closes the
                // photo picker.
                if (uri != null) {
                    planImageUri = uri;
                    planImage.setImageURI(uri);
                }
            });


    private void createPlan(View view) {
        if (!validatePlan(view))
            return;

        createdPlanId = UUID.randomUUID().toString();
        subirImagen(planImageUri); // se sube la imagen a firebase

        String planName = etPlanName.getText().toString();
        int maxPeople = Integer.parseInt(etMaxPeople.getText().toString());
        String creatorPlanNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        Contact user = Util.getCurrentUser();

        Plan plan = new Plan(planName, user, maxPeople, "", new ArrayList<>(), createdPlanId);

        // Añadimos los contactos seleccionados al plan
        for(Contact contact : selectedContacts)
            plan.addToPlan(contact.getContactNumber());

        // Añadimos el creador del plan al plan
        plan.addToPlan(creatorPlanNumber);

        // Se añade el plan a Firebase
        PlanDatabase.getReference().child(plan.getPlanId()).setValue(plan);

        Snackbar.make(getActivity().findViewById(android.R.id.content),
                R.string.plan_created_ok, Snackbar.LENGTH_LONG).show();

        // Volvemos a Home
        Navigation.findNavController(view).navigate(R.id.action_global_nav_home);
    }

    private boolean validatePlan(View view) {
        boolean isPlanNameInvalid = TextUtils.isEmpty(etPlanName.getText());
        boolean isMaxPeopleInvalid = TextUtils.isEmpty(etMaxPeople.getText());
        layoutPlanName.setErrorEnabled(false);
        layoutMaxPeople.setErrorEnabled(isMaxPeopleInvalid);

        if (planImageUri == null) {
            Snackbar.make(view, R.string.plan_creation_no_image, Snackbar.LENGTH_LONG).show();
            return false;
        }
//        if (!isMaxPeopleInvalid && selectedContacts != null
//                && selectedContacts.size() > Integer.parseInt(etMaxPeople.getText().toString())) {
//            Snackbar.make(view, R.string.plan_creation_too_much_people, Snackbar.LENGTH_LONG).show();
//            return false;
//        }
        if (isPlanNameInvalid)
            layoutPlanName.setError(getString(R.string.plan_creation_no_name));
        if (isMaxPeopleInvalid)
            layoutMaxPeople.setError(getString(R.string.plan_creation_no_max_people));

        return !isPlanNameInvalid && !isMaxPeopleInvalid;
    }

    private void rvSelectedContactsSetUp(View root) {
        rvSelectedContacts.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(root.getContext(), 3);
        rvSelectedContacts.setLayoutManager(layoutManager);
    }

    private void subirImagen(Uri selectedImageUri) {
        // Obtiene una referencia a tu Firebase Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        String path = "planPhotos/plan-" + UUID.randomUUID();

        // Crea una referencia para la imagen en Storage
        StorageReference imageReference = storageReference.child(path);

        // Sube la imagen
        imageReference.putFile(selectedImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                PlanDatabase.getReference(createdPlanId).child("imageUrl").setValue(uri.toString());
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Maneja el error de la subida de la imagen
                    }
                });
    }

}