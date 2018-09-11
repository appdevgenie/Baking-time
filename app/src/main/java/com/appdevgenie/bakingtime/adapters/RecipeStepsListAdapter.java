package com.appdevgenie.bakingtime.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appdevgenie.bakingtime.R;
import com.appdevgenie.bakingtime.model.Step;

import java.util.List;

public class RecipeStepsListAdapter extends RecyclerView.Adapter<RecipeStepsListAdapter.RecipeStepsViewHolder> {

    private Context context;
    private List<Step> steps;
    private StepClickedListener stepClickedListener;

    public RecipeStepsListAdapter(Context context, List<Step> steps, StepClickedListener stepClickedListener) {
        this.context = context;
        this.steps = steps;
        this.stepClickedListener = stepClickedListener;
    }

    @NonNull
    @Override
    public RecipeStepsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_steps_list, parent, false);
        return new RecipeStepsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeStepsViewHolder holder, int position) {
        Step step = steps.get(holder.getAdapterPosition());

        int pos = holder.getAdapterPosition();
        if (pos == 0) {
            holder.tvNumber.setText(R.string.intro_abbr);
        } else {
            holder.tvNumber.setText(String.valueOf(pos));
        }
        holder.tvDescription.setText(step.getShortDescription());

    }

    @Override
    public int getItemCount() {
        if (steps == null) {
            return 0;
        }
        return steps.size();
    }

    class RecipeStepsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvNumber;
        TextView tvDescription;

        RecipeStepsViewHolder(View itemView) {
            super(itemView);

            tvNumber = itemView.findViewById(R.id.tvStepItemNumber);
            tvDescription = itemView.findViewById(R.id.tvStepItemShortDescription);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            stepClickedListener.onStepClicked(steps, getAdapterPosition());
        }
    }

    public interface StepClickedListener {
        void onStepClicked(List<Step> steps, int position);
    }
}
