package com.hayksaakian.skillet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.andtinder.model.CardModel;
import com.andtinder.view.CardStackAdapter;


/**
 * Created by Hayk on 11/4/2014.
 */
public final class RecipeCardStackAdapter extends CardStackAdapter {

    public RecipeCardStackAdapter(Context mContext) {
        super(mContext);
    }

    @Override
    public View getCardView(int position, CardModel model, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.recipe_card_inner, parent, false);
            assert convertView != null;
        }

        ((ImageView) convertView.findViewById(R.id.image)).setImageDrawable(model.getCardImageDrawable());
        ((TextView) convertView.findViewById(R.id.title)).setText(model.getTitle());
//        ((TextView) convertView.findViewById(R.id.description)).setText(model.getDescription());

        return convertView;
    }

    @Override
    public boolean shouldFillCardBackground() {
        return false;
    }
}