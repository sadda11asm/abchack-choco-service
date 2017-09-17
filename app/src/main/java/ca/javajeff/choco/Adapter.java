package ca.javajeff.choco;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by Саддам on 17.09.2017.
 */

public class Adapter extends RecyclerView.Adapter<Adapter.AdapterViewHolder> {
    private ArrayList<String> title = new ArrayList<String>();
    private ArrayList<String> schedule = new ArrayList<String>();
    private ArrayList<String> adress = new ArrayList<String>();
    private ArrayList<String> image = new ArrayList<String>();
    private ArrayList<String> discount = new ArrayList<String>();
    private ArrayList<String> price = new ArrayList<String>();
    private ArrayList<String> fullPrice = new ArrayList<String>();
    private ArrayList<String> shortTitle = new ArrayList<String>();
    private ArrayList<String> ID = new ArrayList<String>();
    private final AdapterOnClickHandler mClickHandler;
    public interface AdapterOnClickHandler {
        void onClick(String company);
    }
    public Adapter(AdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public class AdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView theTitle;
        TextView theDiscount;
        TextView shortTItle;


        public AdapterViewHolder(View view, Context context) {
            super(view);
            theDiscount = (TextView) view.findViewById(R.id.discount);
            theTitle  = (TextView) view.findViewById(R.id.title);
            shortTItle = (TextView) view.findViewById(R.id.short_title);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            try {
                int adapterPosition = getAdapterPosition();
                Context context = view.getContext();
                final Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("title", title.get(adapterPosition));
                intent.putExtra("short_title", shortTitle.get(adapterPosition));
                intent.putExtra("schedule", schedule.get(adapterPosition));
                intent.putExtra("image", image.get(adapterPosition));
                intent.putExtra("price", price.get(adapterPosition));
                intent.putExtra("full_price", fullPrice.get(adapterPosition));
                intent.putExtra("discount", discount.get(adapterPosition));
                intent.putExtra("address", adress.get(adapterPosition));
                intent.putExtra("ID", ID.get(adapterPosition));
                mClickHandler.onClick(title.get(getAdapterPosition()));
                sendRequest(adapterPosition);
                context.startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(view.getContext(), "Check your internet connection", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void sendRequest(int position) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("token", "1223");
            params.put("deal_id", MainActivity.IDs.get(position));

            JsonObjectRequest req = new JsonObjectRequest("http://192.168.43.31:8000/api/update/", new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                VolleyLog.v("Response:%n %s", response.toString(4));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.e("Error: ", error.getMessage());
                }
            });

// add the request object to the queue to be executed
            Log.v("reqq", String.valueOf(req));
            ApplicationController.getInstance().addToRequestQueue(req);

    }

    public AdapterViewHolder onCreateViewHolder (ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutForListItem = R.layout.list_item;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        boolean shouldAttachToParent = false;
        View view;
        view = layoutInflater.inflate(layoutForListItem, viewGroup, shouldAttachToParent);
        return new AdapterViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(AdapterViewHolder holder, int position) {
        String titlee = title.get(position);
        String discountt = discount.get(position);
        String shortt = shortTitle.get(position);
        holder.shortTItle.setText(shortt);
        holder.theTitle.setText(titlee);
        holder.theDiscount.setText(discountt);
    }

    @Override
    public int getItemCount() {
        if(null == title)
        return 0;
        return title.size();
    }

    public void setData(ArrayList<String> titles, ArrayList<String> adresses, ArrayList<String> schedules, ArrayList<String> images, ArrayList<String> IDs, ArrayList<String> discounts, ArrayList<String> prices, ArrayList<String> fullPrices, ArrayList<String> shortTitles) {
        image=images;
        title=titles;
        adress=adresses;
        schedule=schedules;
        ID=IDs;
        price=prices;
        fullPrice=fullPrices;
        discount=discounts;
        shortTitle=shortTitles;
    }


}
