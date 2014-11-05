package com.hayksaakian.skillet;

import android.app.*;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.*;
import android.support.v13.app.*;
import android.support.v4.view.*;
import android.view.*;
import android.widget.*;
import android.widget.LinearLayout.*;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.*;
import android.util.*;
import android.net.*;

import android.support.v7.widget.CardView;
import com.andtinder.model.CardModel;
import com.andtinder.model.Orientations;
import com.andtinder.view.CardContainer;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class MainActivity extends Activity {

    String RECIPE_LIST = "http://api.bigoven.com/recipes?title_kw=olives&pg=1&rpp=20&api_key=dvxIJpI918Mtp38X944q334luLcaUUjb";
    List<Recipe> recipesToDecide;
//    Recipe currentRecipe;

//    ImageView main_photo;
//    TextView main_title;

    OkHttpClient http = new OkHttpClient();
    private CardContainer mCardContainer;
    CardModel.OnCardDimissedListener cardDismissListener;
    CardModel.OnClickListener cardClickListener;
    RecipeCardStackAdapter cardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState == null) {
            getRecipeList();
        }else{
            recipesToDecide = (ArrayList<Recipe>)savedInstanceState.getSerializable("recipesToDecide");
//            currentRecipe = (Recipe)savedInstanceState.getSerializable("currentRecipe");
        }


        mCardContainer = (CardContainer) findViewById(R.id.deck);
//        mCardContainer.setOrientation(Orientations.Orientation.Ordered); // vs Disordered
        mCardContainer.setOrientation(Orientations.Orientation.Disordered); // vs Disordered

        CardModel card = new CardModel("Title1", "Description goes here", getResources().getDrawable(R.drawable.picture1) );
        cardDismissListener = new CardModel.OnCardDimissedListener() {
            @Override
            public void onLike() {
                Log.d("Swipeable Card", "I liked it");
            }

            @Override
            public void onDislike() {
                Log.d("Swipeable Card", "I did not like it");
            }
        };
        cardClickListener = new CardModel.OnClickListener() {
            @Override
            public void OnClickListener() {
                Log.i("Swipeable Card","I am pressing the card");
            }
        };

        cardAdapter = new RecipeCardStackAdapter(this);
        cardAdapter.add(card);
        mCardContainer.setAdapter(cardAdapter);
    }

    // keep the targets from being garbage collected
    List<Target> targets = new ArrayList<Target>();

    void AddRecipe(final Recipe recipe){
        Log.i("Swipeable Card","I started a runOnUiThread");
        runOnUiThread(new Runnable() {
            public void run() {
                Log.i("Swipeable Card", "I started loading a card image");
                final Target target = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom arg1) {

                        Log.i("Swipeable Card", "I finished loading a card image");
                        // TODO Create your drawable from bitmap and append where you like.
                        Log.i("Swipeable Card", "Loaded Image" + " width: " + Integer.toString(bitmap.getWidth()) + " height: " + Integer.toString(bitmap.getHeight()));
                        CardModel card = new CardModel(recipe.name, "", new BitmapDrawable(getResources(), bitmap));
                        card.setOnCardDimissedListener(cardDismissListener);
                        card.setOnClickListener(cardClickListener);
//                        cardAdapter.
                        cardAdapter.add(card);
                        cardAdapter.notifyDataSetChanged();
                        mCardContainer.setAdapter(cardAdapter);

                        targets.remove(this);
                    }

                    @Override
                    public void onBitmapFailed(Drawable arg0) {
                        Log.i("Swipeable Card", "I failed to load a card image");
//                        CardModel card = new CardModel(recipe.name, "", arg0);
//                        cardAdapter.add(card);
//                        cardAdapter.notifyDataSetChanged();
//                            mCardContainer.setAdapter(cardAdapter);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        Log.i("Swipeable Card", "Target.onPrepareLoad");
                    }
                };
                targets.add(target);

                Picasso.with(getApplicationContext())
                        .load(recipe.picture_url)
                        .resize(1024, 1024)
                        .centerCrop()
                        .into(target);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState.putSerializable("recipesToDecide", (Serializable)recipesToDecide);
//        savedInstanceState.putSerializable("currentRecipe", (Serializable)currentRecipe);
        // etc.
    }

    void getRecipeList(){
        try {
            httpGet(RECIPE_LIST, new Callback() {
                @Override
                public void onFailure(Request request, IOException e) { e.printStackTrace();}

                @Override
                public void onResponse(Response response) throws IOException {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

//                    Headers responseHeaders = response.headers();
//                    for (int i = 0; i < responseHeaders.size(); i++) {
//                        System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
//                    }
                    try {
                        recipesToDecide = extractRecipeList(new JSONObject(response.body().string()));
                        for(Recipe recipe : recipesToDecide){
                            AddRecipe(recipe);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void httpGet(String url, Callback callback) throws Exception {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Accept", "application/json")
                .build();
        http.newCall(request).enqueue(callback);
    }

    ArrayList<Recipe> extractRecipeList(JSONObject json) throws JSONException{
        ArrayList<Recipe> list = new ArrayList<Recipe>();
        JSONArray results = json.getJSONArray("Results");
        int length = results.length();
        for (int i = 0; i < length; i++) {
            JSONObject result = results.getJSONObject(i);
            Recipe recipe = new Recipe();
            recipe.name = result.getString("Title");
            recipe.url = result.getString("WebURL");
            recipe.picture_url = result.getString("ImageURL");
            list.add(recipe);
        }
        return list;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
