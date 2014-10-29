package com.hayksaakian.skillet;

import android.app.*;
import android.graphics.*;
import android.os.*;
import android.support.v13.app.*;
import android.support.v4.view.*;
import android.view.*;
import android.widget.*;
import android.widget.LinearLayout.*;
import java.util.*;
import android.util.*;
import android.net.*;


public class MainActivity extends Activity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



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
				}
				
			}
		}
		);
        mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setCurrentItem(1);

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

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position);
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
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }
		
		ListView list;
		
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            Bundle args = getArguments();
            int tab_num = args.getInt(ARG_SECTION_NUMBER);

            TextView label = (TextView)rootView.findViewById(R.id.section_label);
			String text = "Food Here";
			
			if(tab_num == 0){
				Integer.toString(tab_num);
				text = "??😀";
				rootView.setBackgroundColor(Color.GREEN);
				label.setGravity(Gravity.RIGHT);
			}else if (tab_num == 2){
				text = "😬";
				rootView.setBackgroundColor(Color.RED);
				label.setGravity(Gravity.LEFT);
			}else{
				text = "Food Here";
				ImageView photo = (ImageView)rootView.findViewById(R.id.photo);
				// some pics https://api.imgur.com/2/album/w9nHF/images.json
				photo.setImageURI(Uri.parse("http://i.imgur.com/NhDejjN.jpg"));
			}
			
            label.setText(text);
			
			//list = (ListView)rootView.findViewById(R.id.list);
			
            return rootView;
        }
    }

}
