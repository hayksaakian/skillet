package com.hayksaakian.skillet;

import com.andtinder.model.CardModel;

/**
 * Created by Hayk on 11/4/2014.
 */
public class RecipeCardModel extends CardModel{
    public Recipe recipe;

    public RecipeCardModel() {
        super();
    }

    public RecipeCardModel(java.lang.String title, java.lang.String description, android.graphics.drawable.Drawable cardImage) {
        super(title, description, cardImage);
    }

    public RecipeCardModel(java.lang.String title, java.lang.String description, android.graphics.Bitmap cardImage) {
        super(title, description, cardImage);
    }

}
