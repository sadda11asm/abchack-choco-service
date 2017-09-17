package ca.javajeff.choco;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Саддам on 17.09.2017.
 */

public class MainActivity extends AppCompatActivity implements Adapter.AdapterOnClickHandler {

    public static Adapter mAdapter;
    public static ArrayList<String> titles = new ArrayList<String>();
    public static ArrayList<String> shortTitles = new ArrayList<String>();
    public static ArrayList<String> IDs = new ArrayList<String>();
    public static ArrayList<String> adresses = new ArrayList<String>();
    public static ArrayList<String> schedules = new ArrayList<String>();
    public static ArrayList<String> discounts = new ArrayList<String>();
    public static ArrayList<String> prices = new ArrayList<String>();
    public static ArrayList<String> fullPrices = new ArrayList<String>();
    public static ArrayList<String> images = new ArrayList<String>();
    RecyclerView mRecyclerView;
    JSONObject response = new JSONObject();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new Adapter(this);

        mRecyclerView.setAdapter(mAdapter);
        response = GPS_service.responseObject;
        JSONArray results = new JSONArray();
        try {
            results=response.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        titles.clear();
        shortTitles.clear();
        adresses.clear();
        schedules.clear();
        images.clear();
        IDs.clear();
        discounts.clear();
        for(int i=0;i<results.length();i++) {
            JSONObject result = new JSONObject();
            try {
                result = results.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                titles.add(result.get("title").toString());
                shortTitles.add(result.get("title_short").toString());
                adresses.add(result.get("address").toString());
                schedules.add(result.get("schedule").toString());
                images.add(result.get("image_url").toString());
                IDs.add(result.get("deal_id").toString());
                discounts.add(result.get("discount").toString());
                Log.v("result", result.toString());
                Log.v("discounts", result.get("discount").toString());
                Log.v("price", result.get("price").toString());
                prices.add(result.get("price").toString());
                fullPrices.add(result.get("full_price").toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        putData();

    }

    private void putData() {
        mAdapter.setData(titles, adresses, schedules, images, IDs, discounts, prices, fullPrices, shortTitles);
    }

    @Override
    public void onClick(String company) {

    }
}
