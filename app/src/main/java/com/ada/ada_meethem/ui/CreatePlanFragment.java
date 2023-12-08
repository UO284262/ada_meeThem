package com.ada.ada_meethem.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.ada.ada_meethem.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CreatePlanFragment extends Fragment {

    private TextView tvName; // TODO completar vista
    private TextView tvNumber;

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

        tvName = root.findViewById(R.id.tvName);
        tvNumber = root.findViewById(R.id.tvNumber);

        FloatingActionButton fab = root.findViewById(R.id.fabPickContacts);
        fab.setOnClickListener(view -> Navigation.findNavController(view).navigate(R.id.action_nav_plan_create_to_contactPickerFragment));

        return root;
    }

}