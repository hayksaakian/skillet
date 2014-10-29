package com.hayksaakian.skillet;

import android.app.*;
import android.graphics.*;
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

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class MainActivity extends Activity {

    String RECIPE_LIST = "http://api.bigoven.com/recipes?title_kw=olives&pg=1&rpp=20&api_key=dvxIJpI918Mtp38X944q334luLcaUUjb";
    List<Recipe> recipesToDecide;
    Recipe currentRecipe;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    ImageView main_photo;
    TextView main_title;

    OkHttpClient http = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState == null) {
            getRecipeList();
        }else{
            recipesToDecide = (ArrayList<Recipe>)savedInstanceState.getSerializable("recipesToDecide");
            currentRecipe = (Recipe)savedInstanceState.getSerializable("currentRecipe");
        }

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setOnPageChangeListener(
		new ViewPager.SimpleOnPageChangeListener(){
			@Override
			public void onPageSelected(int pos){
				String t = Long.toString(System.currentTimeMillis());
				String text = "";
				if(pos == 0){
					text = "Yes";
				}else if(pos == 2){
					text = "No";
				}
				if(pos != 1){
					Toast.makeText(getApplicationContext(), text + " @ " + t, Toast.LENGTH_SHORT).show();
					
					new android.os.Handler().postDelayed(
						new Runnable() {
							public void run() {
								mViewPager.setCurrentItem(1);
								//Log.i("tag", "This'll run 300 milliseconds later");
							}
						}, 
						300);

                    recipesToDecide.remove(0);
                    if(recipesToDecide.size() > 0) {
                        SetRecipe(recipesToDecide.get(0));
                    }else{
                        getRecipeList();
                    }
				}
			}
		}
		);
        mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setCurrentItem(1);
    }

    void SetRecipe(Recipe recipe){
        currentRecipe = recipe;
        Log.d("nullcheck main.photo", Boolean.toString(main_photo == null));
        Log.d("nullcheck main.title", Boolean.toString(main_title == null));
        if(main_photo != null && main_title != null) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Picasso.with(getApplicationContext())
                            .load(currentRecipe.picture_url)
                            .resize(1024, 1024)
                            .centerCrop()
                            .into(main_photo);
                    main_title.setText(currentRecipe.name);
                }
            });
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
//        savedInstanceState.putBoolean("MyBoolean", true);
//        savedInstanceState.putDouble("myDouble", 1.9);
//        savedInstanceState.putInt("MyInt", 1);
//        savedInstanceState.putString("MyString", "Welcome back to Android");
        savedInstanceState.putSerializable("recipesToDecide", (Serializable)recipesToDecide);
        savedInstanceState.putSerializable("currentRecipe", (Serializable)currentRecipe);
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
                        SetRecipe(recipesToDecide.get(0));
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

    

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {


        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        Fragment main;

        @Override
        public Fragment getItem(int position) {
            Log.d("SPA.getItem", Integer.toString(position));
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if(position == 1){
                if(main == null) {
                    main = PlaceholderFragment.newInstance(position);
                }
                return main;
            }else {
                return PlaceholderFragment.newInstance(position);
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            Log.d("F.newInstance", Integer.toString(sectionNumber));
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            Bundle args = getArguments();
            int tab_num = args.getInt(ARG_SECTION_NUMBER);
            Log.d("InflatingTab", Integer.toString(tab_num));

            TextView label = (TextView)rootView.findViewById(R.id.section_label);
			String text = "Food Here";
			
			if(tab_num == 0){
				Integer.toString(tab_num);
				text = "😀";
                label.setText(text);
				rootView.setBackgroundColor(Color.GREEN);
				label.setGravity(Gravity.RIGHT);
			}else if (tab_num == 2){
				text = "😬";
                label.setText(text);
				rootView.setBackgroundColor(Color.RED);
				label.setGravity(Gravity.LEFT);
			}else{
                MainActivity activity = ((MainActivity)getActivity());
                activity.main_photo = (ImageView)rootView.findViewById(R.id.photo);
                activity.main_title = label;
                if(activity.currentRecipe != null){
                    activity.SetRecipe(activity.currentRecipe);
                }
			}

            return rootView;
        }
        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            setRetainInstance(true);
        }

    }

}
