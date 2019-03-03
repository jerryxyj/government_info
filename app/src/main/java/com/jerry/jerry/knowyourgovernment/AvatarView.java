package com.jerry.jerry.knowyourgovernment;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class AvatarView extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private TextView title;
    private  TextView name;
    private TextView location;
    private ImageView avatarphoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar_view);
        Intent intent = getIntent();
        title=(TextView)findViewById(R.id.avatartitle);
        name=(TextView)findViewById(R.id.avatarname);
        location=(TextView)findViewById(R.id.avataraddress);
        avatarphoto=(ImageView)findViewById(R.id.avatarphoto);
        String party="";

        if (intent.hasExtra(Official.class.getName())) {
            Official o = (Official) intent.getSerializableExtra(Official.class.getName());
            party=o.getParty();
            location.setText(o.getLocation());
            title.setText(o.getTitle());
            name.setText(o.getName());
            String imageURL=o.getPhotoUrl();
            loadImage(imageURL);
        }
        if(party.equals("Democratic")){
            View view = this.getWindow().getDecorView();
            view.setBackgroundColor(Color.BLUE);

        }
        else if(party.equals("Republican")){
            View view = this.getWindow().getDecorView();
            view.setBackgroundColor(Color.RED);
        }
        else{
            View view = this.getWindow().getDecorView();
            view.setBackgroundColor(Color.BLACK);

        }
    }
    private void loadImage(final String imageURL) {
        //     compile 'com.squareup.picasso:picasso:2.5.2'
        if (imageURL != null | imageURL.isEmpty() | imageURL != "") {

            Log.d(TAG, "loadImage: " + imageURL);

            Picasso picasso = new Picasso.Builder(this)
                    .listener(new Picasso.Listener() {
                        @Override
                        public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                            Log.d(TAG, "onImageLoadFailed: ");
                            final String changedUrl = imageURL.replace("http:", "https:");
                            picasso.load(changedUrl)
                                    .error(R.drawable.brokenimage)
                                    .placeholder(R.drawable.placeholder)
                                    .into(avatarphoto);
                        }
                    })
                    .build();

            picasso.load(imageURL)
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.placeholder)
                    .into(avatarphoto);
        }
        else {
            Picasso.with(this).load(imageURL)
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.missingimage)
                    .into(avatarphoto);
        }
    }
}
