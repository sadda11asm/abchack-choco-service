package ca.javajeff.choco;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by Саддам on 17.09.2017.
 */

public class DetailActivity extends Activity {
    TextView setTitle;
    TextView setShortTitle;
    TextView setAddress;
    TextView setPrice;
    TextView setFullPrice;
    TextView setSchedule;
    TextView setDiscount;
    ImageView setImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        setAddress = (TextView) findViewById(R.id.address);
        setTitle = (TextView) findViewById(R.id.titlee);
        setShortTitle = (TextView) findViewById(R.id.short_titlee);
        setFullPrice = (TextView) findViewById(R.id.full_price);
        setPrice = (TextView) findViewById(R.id.price);
        setSchedule = (TextView) findViewById(R.id.schedule);
        setImage  =(ImageView) findViewById(R.id.imagee);
        Bundle bundle = getIntent().getExtras();
        String title = bundle.getString("title").toString();
        String shortTitle = bundle.getString("short_title");
        String address = bundle.getString("address");
        String price = bundle.getString("price");
        String fullPrice = bundle.getString("full_price");
        String discount = bundle.getString("discount");
        String image = bundle.getString("image");
        String schedule = bundle.getString("schedule");
        String ID = bundle.getString("ID");
        Log.v("title", title);
        setTitle.setText(title);
        setShortTitle.setText(shortTitle);
        setPrice.setText("Your Price: " + price);
        setFullPrice.setText("Full Price: " + fullPrice);
        setAddress.setText(address);
        setSchedule.setText(schedule);
        Picasso.with(this).load(image).resize(1200,1200).into(setImage);


    }
}
