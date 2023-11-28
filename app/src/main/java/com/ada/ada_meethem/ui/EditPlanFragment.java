package com.ada.ada_meethem.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ada.ada_meethem.R;
import com.ada.ada_meethem.database.PlanDatabase;
import com.ada.ada_meethem.modelo.Plan;
import com.ada.ada_meethem.modelo.pinnable.ChatMessage;
import com.google.firebase.auth.FirebaseAuth;


public class EditPlanFragment extends Fragment {

    private Plan plan;

    private Button pinMsgButton;

    private EditText pinMsgText;
    public EditPlanFragment() {
        // Required empty public constructor
    }

    public static EditPlanFragment newInstance() {
        return new EditPlanFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_edit_plan, container, false);
        plan = (Plan) getArguments().getParcelable("plan");

        TextView planTitle = (TextView) root.findViewById(R.id.plan_title) ;
        planTitle.setText(plan.getTitle());

        pinMsgText = (EditText) root.findViewById(R.id.et_pin_msg);

        pinMsgButton = (Button) root.findViewById(R.id.pin_msg_btn);
        pinMsgButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String text = pinMsgText.getText().toString();
                if(!text.isEmpty()) anclarMensaje(text);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(pinMsgText.getWindowToken(), 0);
                showPlan();
            }
        });

        return root;
    }

    private void anclarMensaje(String text) {
        String phoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        ChatMessage message = new ChatMessage(text,phoneNumber);
        PlanDatabase.pinMessage(message,plan.getPlanId());
    }

    private void showPlan() {
        PlanFragment planFragment= PlanFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putParcelable("plan", plan);
        planFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, planFragment).commit();
    }
}