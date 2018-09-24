package com.appdevgenie.bakingtime.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appdevgenie.bakingtime.R;
import com.appdevgenie.bakingtime.model.Step;

import java.util.List;

public class RecipeStepsListAdapter extends RecyclerView.Adapter<RecipeStepsListAdapter.RecipeStepsViewHolder> {

    private Context context;
    private List<Step> steps;
    private StepClickedListener stepClickedListener;
    private int rowId;

    public RecipeStepsListAdapter(Context context, List<Step> steps, StepClickedListener stepClickedListener, int rowId) {
        this.context = context;
        this.steps = steps;
        this.stepClickedListener = stepClickedListener;
        this.rowId = rowId;
    }

    @NonNull
    @Override
    public RecipeStepsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_steps_list, parent, false);
        return new RecipeStepsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecipeStepsViewHolder holder, int position) {
        Step step = steps.get(holder.getAdapterPosition());

        final int pos = holder.getAdapterPosition();
        if (pos == 0) {
            holder.tvNumber.setText(R.string.intro_abbr);
        } else {
            holder.tvNumber.setText(String.valueOf(pos));
        }
        holder.tvDescription.setText(step.getShortDescription());

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stepClickedListener.onStepClicked(steps, pos);
                rowId = pos;
                notifyDataSetChanged();
            }
        });
        if(rowId == pos){
            holder.linearLayout.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        }else{
            holder.linearLayout.setBackgroundColor(Color.TRANSPARENT);
        }

    }

    @Override
    public int getItemCount() {
        if (steps == null) {
            return 0;
        }
        return steps.size();
    }

    class RecipeStepsViewHolder extends RecyclerView.ViewHolder {
        TextView tvNumber;
        TextView tvDescription;
        LinearLayout linearLayout;

        RecipeStepsViewHolder(View itemView) {
            super(itemView);

            tvNumber = itemView.findViewById(R.id.tvStepItemNumber);
            tvDescription = itemView.findViewById(R.id.tvStepItemShortDescription);
            linearLayout = itemView.findViewById(R.id.llItemStepsList);
        }
    }

    public interface StepClickedListener {
        void onStepClicked(List<Step> steps, int position);
    }
}
