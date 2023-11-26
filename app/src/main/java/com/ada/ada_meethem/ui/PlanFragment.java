package com.ada.ada_meethem.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.ada.ada_meethem.R;
import com.ada.ada_meethem.adapters.MessageListAdapter;
import com.ada.ada_meethem.adapters.PinnedItemsAdapter;
import com.ada.ada_meethem.database.ChatMessageDatabase;
import com.ada.ada_meethem.database.PlanDatabase;
import com.ada.ada_meethem.modelo.Plan;
import com.ada.ada_meethem.modelo.pinnable.ChatMessage;
import com.ada.ada_meethem.modelo.pinnable.DateSurvey;
import com.ada.ada_meethem.modelo.pinnable.Pinnable;
import com.ada.ada_meethem.modelo.pinnable.PlanImage;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlanFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlanFragment extends Fragment {

    private Plan plan;

    private PinnedItemsAdapter adapter;

    private ListView listView;

    public PlanFragment() {
        // Required empty public constructor
    }

    public static PlanFragment newInstance() {
        return  new PlanFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root= inflater.inflate(R.layout.fragment_plan, container, false);
        plan = (Plan) getArguments().getParcelable("plan");

        ((TextView) root.findViewById(R.id.planName)).setText(plan.getTitle());
        ((TextView) root.findViewById(R.id.participantes)).setText(String.format("%d/%d",plan.getEnlisted().size(),plan.getMaxPeople()));
        ((TextView) root.findViewById(R.id.creadorPlan)).setText(plan.getCreator().getUsername());
        //((ImageView) root.findViewById(R.id.imagenPlan))

        FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.fabChat);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirChat(plan);
            }
        });

        FloatingActionButton fab2 = (FloatingActionButton) root.findViewById(R.id.fabEditPlan);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirEdit(plan);
            }
        });

        listView = root.findViewById(R.id.pinnedList);
        adapter = new PinnedItemsAdapter(root.getContext(), new ArrayList<Pinnable>());
        listView.setAdapter(adapter);
        displayPinned(root);

        return root;
    }

    private void abrirChat(Plan plan) {
        ChatFragment chatFragment=ChatFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putParcelable("plan", plan);
        chatFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, chatFragment).commit();

    }

    private void abrirEdit(Plan plan) {
        EditPlanFragment editPlanFragment=EditPlanFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putParcelable("plan", plan);
        editPlanFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, editPlanFragment).commit();

    }

    public void displayPinned(View root) {

        PlanDatabase.getReference(plan.getPlanId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Pinnable> pins = new ArrayList<>();
                for (DataSnapshot chatSnapshot : dataSnapshot.getChildren()) {
                    ChatMessage chatMessage = chatSnapshot.getValue(ChatMessage.class);
                    DateSurvey dateSurvey = chatSnapshot.getValue(DateSurvey.class);
                    PlanImage imageItem = chatSnapshot.getValue(PlanImage.class);
                    if (chatMessage != null) {
                        pins.add(chatMessage);
                    } else if (dateSurvey != null) {
                        pins.add(dateSurvey);
                    } else {
                        pins.add(imageItem);
                    }
                }
                adapter.update(pins);
                listView.setSelection(adapter.getCount() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejo de errores
            }
        });
    }
}