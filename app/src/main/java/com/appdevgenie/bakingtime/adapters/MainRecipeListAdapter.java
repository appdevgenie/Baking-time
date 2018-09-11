package com.appdevgenie.bakingtime.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appdevgenie.bakingtime.R;
import com.appdevgenie.bakingtime.model.Recipe;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MainRecipeListAdapter extends RecyclerView.Adapter<MainRecipeListAdapter.RecipeAdapterViewHolder> {

    private Context context;
    private List<Recipe> recipes;
    private RecipeClickedListener recipeClickedListener;

    public MainRecipeListAdapter(Context context, RecipeClickedListener listener) {
        this.context = context;
        this.recipeClickedListener = listener;
    }

    @NonNull
    @Override
    public MainRecipeListAdapter.RecipeAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_main_list_recipe, parent, false);

        return new RecipeAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainRecipeListAdapter.RecipeAdapterViewHolder holder, int position) {

        Recipe recipe = recipes.get(holder.getAdapterPosition());

        String imageString = recipe.getImage();
        if(!TextUtils.isEmpty(imageString)) {
            Picasso.with(context)
                    .load(recipe.getImage())
                    .placeholder(R.drawable.cupcake)
                    .error(R.drawable.cupcake)
                    .centerCrop()
                    .into(holder.ivThumb);
        }

        int stepsAmount = recipe.getSteps().size();
        holder.tvSteps.setText(TextUtils.concat(
                String.valueOf(stepsAmount),
                " ",
                context.getString(R.string.steps)));

        holder.tvRecipe.setText(recipe.getName());
        holder.tvServings.setText(TextUtils.concat(
                String.valueOf(recipe.getServings()),
                " ",
                context.getString(R.string.servings)));

        int ingredientsAmount = recipe.getIngredients().size();
        holder.tvIngredients.setText(TextUtils.concat(
                String.valueOf(ingredientsAmount),
                " ",
                context.getString(R.string.ingredients)));

    }

    @Override
    public int getItemCount() {

        if(recipes == null){
            return 0;
        }
        return recipes.size();
    }

    public void setMainAdapterData(List<Recipe> recipeList){
        recipes = recipeList;
        notifyDataSetChanged();
    }

    class RecipeAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView ivThumb;
        TextView tvRecipe;
        TextView tvServings;
        TextView tvIngredients;
        TextView tvSteps;

        RecipeAdapterViewHolder(View itemView) {
            super(itemView);

            ivThumb = itemView.findViewById(R.id.ivMainListRecipeThumb);
            tvRecipe = itemView.findViewById(R.id.tvMainListRecipeName);
            tvServings = itemView.findViewById(R.id.tvMainListRecipeServings);
            tvIngredients = itemView.findViewById(R.id.tvMainListRecipeIngredients);
            tvSteps = itemView.findViewById(R.id.tvMainListRecipeSteps);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            Recipe recipe = recipes.get(getAdapterPosition());
            recipeClickedListener.onRecipeClicked(recipe);
        }
    }

    public interface RecipeClickedListener{
        void onRecipeClicked(Recipe selectedRecipe);
    }
}
