package com.ada.ada_meethem.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ada.ada_meethem.R;
import com.ada.ada_meethem.modelo.Plan;
//import com.squareup.picasso.Picasso;

import java.util.List;

public class PlanListAdapter extends RecyclerView.Adapter<PlanListAdapter.PlanViewHolder> {

    // Interfaz para manejar el evento click sobre un elemento
    public interface OnItemClickListener {
        void onItemClick(Plan item);
    }

    private List<Plan> plans;
    private final OnItemClickListener listener;

    public PlanListAdapter(List<Plan> plans, OnItemClickListener listener) {
        this.plans = plans;
        this.listener = listener;
    }

    // Indica el layout a inflar para usar en la vista
    @NonNull
    @Override
    public PlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Creamos la vista con el layout para un elemento
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_home_recycler_line, parent, false);
        return new PlanViewHolder(itemView);
    }

    // Asocia el contenido a los componentes de la vista
    @Override
    public void onBindViewHolder(@NonNull PlanViewHolder holder, int position) {
        Plan plan = plans.get(position);
        holder.bindPlan(plan, listener);
    }

    @Override
    public int getItemCount() {
        return plans.size();
    }


    public class PlanViewHolder extends RecyclerView.ViewHolder {

        private ImageView planImage;
        private TextView planName;
        private TextView planMembersNumber;
        private TextView planCreator;
        private TextView planGroup;

        public PlanViewHolder(@NonNull View itemView) {
            super(itemView);
            planImage = itemView.findViewById(R.id.planImage);
            planName = itemView.findViewById(R.id.planName);
            planMembersNumber = itemView.findViewById(R.id.planMembersNumber);
            planCreator = itemView.findViewById(R.id.planCreator);
            planGroup = itemView.findViewById(R.id.planGroup);
        }

        // asignar valores a los componentes
        public void bindPlan(final Plan plan, final OnItemClickListener listener) {
            planName.setText(plan.getTitle());
            String membersNumber = plan.getEnlisted().size() + "/" + plan.getMaxPeople();
            planMembersNumber.setText(membersNumber);
            planCreator.setText(plan.getCreator().getUsername());
            planGroup.setText(plan.getGroup().getGroupName());

            // cargar imagen
           // Picasso.get().load(plan.getImageUrl()).into(planImage);

            itemView.setOnClickListener(view -> listener.onItemClick(plan));
        }
    }
}
