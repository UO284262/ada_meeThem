package com.ada.ada_meethem.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ada.ada_meethem.R;
import com.ada.ada_meethem.adapters.PlanListAdapter;
import com.ada.ada_meethem.modelo.Group;
import com.ada.ada_meethem.modelo.Person;
import com.ada.ada_meethem.modelo.Plan;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "text";

    private String text;

    private List<Plan> plans;
    private RecyclerView plansRecyclerView;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param text Parameter 1.
     * @return A new instance of fragment HomeFragment.
     */
    public static HomeFragment newInstance(String text) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, text);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            text = getArguments().getString(ARG_PARAM1);
        }

        generatePlans();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        plansRecyclerView = root.findViewById(R.id.plansRecyclerView);
        plansRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(root.getContext());
        plansRecyclerView.setLayoutManager(layoutManager);

        PlanListAdapter plAdapter= new PlanListAdapter(plans,
                plan -> {
                });
        plansRecyclerView.setAdapter(plAdapter);

        return root;
    }

    private void generatePlans() {
        Person person1 = new Person("Maricarmen");
        Person person2 = new Person("Alex");
        Person person3 = new Person("Abel");
        Person person4 = new Person("Diego");

        Group group1 = new Group("Montañeros raperos", person1);
        Group group2 = new Group("Molamos", person2);

        Plan plan1 = new Plan("Ruta del Cares", group1, person1, 10);
        Plan plan2 = new Plan("Padelcito", group2, person3, 3);
        Plan plan3 = new Plan("Surf en rodiles", group2, person4, 5);

        plan3.addToPlan(person1);
        plan3.addToPlan(person3);
        plan2.addToPlan(person1);
        plan2.addToPlan(person2);
        plan2.addToPlan(person4);

        plans = new ArrayList<>();
        plans.add(plan1);
        plans.add(plan2);
        plans.add(plan3);
    }

}