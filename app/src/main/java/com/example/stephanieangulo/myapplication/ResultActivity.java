package com.example.stephanieangulo.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by stephanieangulo on 3/1/18.
 */

public class ResultActivity extends AppCompatActivity {
    ListView mListView;
    Context mContext;
    TextView resultsTextView;
    Map<String, ArrayList<Integer>> prepTimeMap = new HashMap<>();
    Map<String, ArrayList<Integer>> servingsMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        mContext = this;

        ArrayList<Recipe> recipes =  Recipe.getRecipesFromJSON("recipes.json", mContext);

        String dietLabel = this.getIntent().getExtras().getString("dietLabel");
        String servings = this.getIntent().getExtras().getString("servings");
        String prepTime = this.getIntent().getExtras().getString("prepTime");

        makeServingsMap(recipes, servings);
        makePrepTimeMap(recipes, prepTime);

        ArrayList<Recipe> results = filterResults(recipes, dietLabel, servings, prepTime);
        RecipeAdapter adapter = new RecipeAdapter(mContext, results);

        resultsTextView = findViewById(R.id.resultsFoundTextView);
        mListView = findViewById(R.id.resultListView);
        mListView.setAdapter(adapter);

        String noRecipesFound = "No recipes found! Redefine your search parameters.";
        String recipesFound = String.valueOf(results.size()) + " recipes found.";

        if(results.isEmpty())
            resultsTextView.setText(noRecipesFound);
         else
            resultsTextView.setText(recipesFound);

    }

    private ArrayList<Recipe> filterResults(ArrayList<Recipe> recipes, String dietLabel, String servings, String prepTime) {
        if(isAllUnSpecified(dietLabel, servings, prepTime))
            return recipes;

        ArrayList<Recipe> filteredRecipes = new ArrayList<>();
        for(int i = 0; i < recipes.size(); i++) {
            Recipe recipe = recipes.get(i);
            if(isSpecifiedDietLabel(dietLabel, recipe) && servingsMap.containsKey(servings)
                    && prepTimeMap.containsKey(prepTime)) {
                ArrayList<Integer> servingValues = servingsMap.get(servings);
                ArrayList<Integer> prepTimeValues = prepTimeMap.get(prepTime);
                boolean yeet = false;
                for(int j = 0; j < servingValues.size(); j++) {
                    for(int k = 0; k < prepTimeValues.size(); k++) {
                        int servingValue = servingValues.get(j);
                        int prepTimeValue = prepTimeValues.get(k);
                        if(recipe.servings == servingValue
                                && recipe.prepTimeConverted == prepTimeValue)
                            yeet = true;
                    }
                }
                if(yeet) filteredRecipes.add(recipe);
            }
        }
        return filteredRecipes;
    }

    private boolean isSpecifiedDietLabel(String dietLabel, Recipe recipe) {
        return dietLabel.equals(recipe.dietLabel) || dietLabel.equals(" ");
    }

    private boolean isAllUnSpecified(String dietLabel, String servings, String prepTime) {
        return dietLabel.equals(" ") && servings.equals(" ") && prepTime.equals(" ");
    }

    private void makeServingsMap(ArrayList<Recipe> recipes, String parameter) {
        for(int i = 0; i < recipes.size(); i++) {
            int servings = recipes.get(i).servings;
            if(!servingsMap.containsKey(parameter)) {
                servingsMap.put(parameter, new ArrayList<Integer>());
            } else {
                ArrayList<Integer> list = servingsMap.get(parameter);
                if(parameter.equals("Less than 4") && servings < 4) {
                    list.add(servings);
                    servingsMap.put(parameter, list);
                } else if(parameter.equals("4-6") && (servings >= 4 && servings <= 6)) {
                    list.add(servings);
                    servingsMap.put(parameter, list);
                } else if(parameter.equals("7-9")&& (servings >= 7 && servings <= 9)) {
                    list.add(servings);
                    servingsMap.put(parameter, list);
                } else if(parameter.equals("More than 10")&& (servings >= 10 )) {
                    list.add(servings);
                    servingsMap.put(parameter, list);
                }
                if(parameter.equals(" ")) {
                    list.add(servings);
                    servingsMap.put(parameter, list);
                }
            }

        }
    }

    private void makePrepTimeMap(ArrayList<Recipe> recipes, String parameter) {
        for(int i = 0; i < recipes.size(); i++) {
            int prepTimeInMinutes = recipes.get(i).prepTimeConverted;
            if(!prepTimeMap.containsKey(parameter)) {
                prepTimeMap.put(parameter, new ArrayList<Integer>());
            } else {
                ArrayList<Integer> list = prepTimeMap.get(parameter);
                if(parameter.equals("30 min or less") && prepTimeInMinutes <= 30) {
                    list.add(prepTimeInMinutes);
                    prepTimeMap.put(parameter, list);
                }
                if(parameter.equals("Less than 1 hour") && prepTimeInMinutes <= 60) {
                    list.add(prepTimeInMinutes);
                    prepTimeMap.put(parameter, list);
                }
                if(parameter.equals("More than 1 hour") &&  prepTimeInMinutes > 60) {
                    list.add(prepTimeInMinutes);
                    prepTimeMap.put(parameter, list);
                }
                if(parameter.equals(" ")) {
                    list.add(prepTimeInMinutes);
                    prepTimeMap.put(parameter, list);
                }
            }
        }
    }
}
