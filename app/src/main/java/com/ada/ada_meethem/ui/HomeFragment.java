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

public class HomeFragment extends Fragment {

    private List<Plan> plans;

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        generatePlans();

        RecyclerView plansRecyclerView = root.findViewById(R.id.plansRecyclerView);
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

        Plan plan1 = new Plan("Ruta del Cares", group1, person1, 10, "https://www.escapadarural.com/blog/wp-content/uploads/2019/11/ruta-cares.jpg");
        Plan plan2 = new Plan("Padelcito", group2, person3, 3, "https://www.covb.cat/wp-content/uploads/2023/04/padel-g9a1278dbc_1280.jpg");
        Plan plan3 = new Plan("Surf en rodiles", group2, person4, 5, "https://img.redbull.com/images/q_auto,f_auto/redbullcom/2015/01/22/1331701014293_2/iv%C3%A1n-villalba-en-rodiles-%28asturias%29-semeyadetoral.com.jpg");

        plan3.addToPlan(person1);
        plan3.addToPlan(person3);
        plan2.addToPlan(person1);
        plan2.addToPlan(person2);
        plan2.addToPlan(person4);

        plans = new ArrayList<>();
        plans.add(plan1);
        plans.add(plan2);
        plans.add(plan3);
        plans.add(plan1);
        plans.add(plan2);
        plans.add(plan3);
        plans.add(plan1);
        plans.add(plan2);
        plans.add(plan3);
        plans.add(plan1);
        plans.add(plan2);
        plans.add(plan3);
    }

}