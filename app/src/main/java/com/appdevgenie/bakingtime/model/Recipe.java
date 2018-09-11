package com.appdevgenie.bakingtime.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "recipe")
public class Recipe implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int itemId;
    private Integer id;
    private String name;
    private List<Ingredient> ingredients;
    private List<Step> steps;
    private Integer servings;
    private String image;

    public Recipe() {
    }

    @Ignore
    public Recipe(Integer recipeId, String recipeName, List<Ingredient> ingredients, List<Step> steps, Integer servings, String recipeImage) {
        this.id = recipeId;
        this.name = recipeName;
        this.ingredients = ingredients;
        this.steps = steps;
        this.servings = servings;
        this.image = recipeImage;
    }

    public Recipe(int _id, Integer recipeId, String recipeName, List<Ingredient> ingredients, List<Step> steps, Integer servings, String recipeImage) {
        this.itemId = _id;
        this.id = recipeId;
        this.name = recipeName;
        this.ingredients = ingredients;
        this.steps = steps;
        this.servings = servings;
        this.image = recipeImage;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int _id) {
        this.itemId = _id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer recipeId) {
        this.id = recipeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String recipeName) {
        this.name = recipeName;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public Integer getServings() {
        return servings;
    }

    public void setServings(Integer servings) {
        this.servings = servings;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String recipeImage) {
        this.image = recipeImage;
    }


    protected Recipe(Parcel in) {
        id = in.readByte() == 0x00 ? null : in.readInt();
        name = in.readString();
        if (in.readByte() == 0x01) {
            ingredients = new ArrayList<>();
            in.readList(ingredients, Ingredient.class.getClassLoader());
        } else {
            ingredients = null;
        }
        if (in.readByte() == 0x01) {
            steps = new ArrayList<>();
            in.readList(steps, Step.class.getClassLoader());
        } else {
            steps = null;
        }
        servings = in.readByte() == 0x00 ? null : in.readInt();
        image = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(id);
        }
        dest.writeString(name);
        if (ingredients == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(ingredients);
        }
        if (steps == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(steps);
        }
        if (servings == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(servings);
        }
        dest.writeString(image);
    }

    public static final Parcelable.Creator<Recipe> CREATOR = new Parcelable.Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };
}