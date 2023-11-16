package com.ada.ada_meethem.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ada.ada_meethem.R;
import com.ada.ada_meethem.modelo.Plan;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlanFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlanFragment extends Fragment {

    private Plan plan;

    public PlanFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PlanFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PlanFragment newInstance() {
        PlanFragment fragment = new PlanFragment();
        return fragment;
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
        plan = (Plan) getArguments().getSerializable("plan");


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
        Log.d("queCojones","queCojones");
        return root;
    }

    private void abrirChat(Plan plan) {
        ChatFragment chatFragment=ChatFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putSerializable("plan", plan);
        chatFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, chatFragment).commit();

    }
}