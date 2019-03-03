package com.jerry.jerry.knowyourgovernment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class OfficialActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private TextView title;
    private  TextView name;
    private TextView party;
    private ImageView avatar;
    private TextView address;
    private TextView phone;
    private TextView email;
    private TextView website;
    private TextView location;
    String Fid="";
    String Tid="";
    String Gid="";
    String Yid="";
    String PhotoUrl;
    private Official thisofficial;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official);
        Intent intent = getIntent();
        title=(TextView)findViewById(R.id.officialtitle);
        name=(TextView)findViewById(R.id.officialname);
        party=(TextView)findViewById(R.id.officialparty);
        address=(TextView)findViewById(R.id.officialaddress);
        phone=(TextView)findViewById(R.id.officialphone);
        email=(TextView)findViewById(R.id.officialemail);
        website=(TextView)findViewById(R.id.officialwebsite);
        avatar=(ImageView)findViewById(R.id.avatar);
        location=(TextView)findViewById(R.id.officiallocation);
        ImageView fb = (ImageView) findViewById(R.id.facebook);
        ImageView tw = (ImageView) findViewById(R.id.twitter);
        ImageView gp = (ImageView) findViewById(R.id.googlplus);
        ImageView yt = (ImageView) findViewById(R.id.youtube);


        if (intent.hasExtra(Official.class.getName())) {


            Official o = (Official) intent.getSerializableExtra(Official.class.getName());
            thisofficial=o;
            Log.d(TAG,"Look channel id:"+o.getChannelYid()+o.getChannelGid()+o.getChannelTid()+o.getChannelFid());
            location.setText(o.getLocation());
            title.setText(o.getTitle());
            name.setText(o.getName());
            party.setText("("+o.getParty()+")");
            address.setText(o.getAddress());
            phone.setText(o.getNumber());
            email.setText(o.getEmail());
            website.setText(o.getWebsite());
            String imageURL=o.getPhotoUrl();
            PhotoUrl=imageURL;

                loadImage(imageURL);

            if(!o.getChannelFid().equals("No data provided")){
                Fid=o.getChannelFid();
            }
            else{
                fb.setVisibility(View.INVISIBLE);

            }
            if(!o.getChannelTid().equals("No data provided")){
                Tid=o.getChannelTid();
            }
            else{
                tw.setVisibility(View.INVISIBLE);

            }
            if(!o.getChannelGid().equals("No data provided")){
                Gid=o.getChannelGid();
                Log.d(TAG,"testing1"+Gid);
            }
            else{
                gp.setVisibility(View.INVISIBLE);
                Log.d(TAG,"testing2"+Gid);

            }
            if(!o.getChannelYid().equals("No data provided")){
                Yid=o.getChannelYid();
            }
            else{
                yt.setVisibility(View.INVISIBLE);


            }

        }
        Log.d(TAG,"party's name is: "+party.getText().toString());
        if(party.getText().toString().equals("(Democratic)")){
            View view = this.getWindow().getDecorView();
            view.setBackgroundColor(Color.BLUE);

        }
        else if(party.getText().toString().equals("(Republican)")){
            View view = this.getWindow().getDecorView();
            view.setBackgroundColor(Color.RED);
        }
        else{
            View view = this.getWindow().getDecorView();
            view.setBackgroundColor(Color.BLACK);

        }


        Linkify.addLinks(((TextView) findViewById(R.id.officialwebsite)), Linkify.WEB_URLS);
        Linkify.addLinks(((TextView) findViewById(R.id.officialphone)), Linkify.PHONE_NUMBERS);
        Linkify.addLinks(((TextView) findViewById(R.id.officialaddress)), Linkify.MAP_ADDRESSES);
        Linkify.addLinks(((TextView) findViewById(R.id.officialemail)), Linkify.EMAIL_ADDRESSES);
        address.setLinkTextColor(Color.WHITE);
        phone.setLinkTextColor(Color.WHITE);
        email.setLinkTextColor(Color.WHITE);
        website.setLinkTextColor(Color.WHITE);
    }


    private void loadImage(final String imageURL) {
        //     compile 'com.squareup.picasso:picasso:2.5.2'
        if ( !imageURL.equals("No data provided")) {

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
                                   .into(avatar);
                        }
                    })
                    .build();

            picasso.load(imageURL)
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.placeholder)
                    .into(avatar);
        }
        else {
            Log.d(TAG,"testing image loading empty");
            String nullimageUrl=null;
                Picasso.with(this).load(nullimageUrl)
                        .error(R.drawable.brokenimage)
                        .placeholder(R.drawable.missingimage)
                        .into(avatar);
            }
    }
    public void clickFacebook(View v) {

        String FACEBOOK_URL = "https://www.facebook.com/" + Fid;

        Intent intent = null;
        String urlToUse;
        try {
            getPackageManager().getPackageInfo("com.facebook.katana", 0);

            int versionCode = getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                urlToUse = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                urlToUse = "fb://page/" + Fid;
            }
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlToUse));
        } catch (Exception e) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(FACEBOOK_URL));
        }

        startActivity(intent);
    }

    public void clickTwitter(View v) {
        String twitterAppUrl = "twitter://user?screen_name=" + Tid;
        String twitterWebUrl = "https://twitter.com/" + Tid;

        Intent intent = null;
        try {
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitterAppUrl));
        } catch (Exception e) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitterWebUrl));
        }
        startActivity(intent);
    }

    public void googlePlusClicked(View v) {

        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setClassName("com.google.android.apps.plus",
                    "com.google.android.apps.plus.phone.UrlGatewayActivity");
            intent.putExtra("customAppUri", Gid);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://plus.google.com/" + Gid)));
        }
    }

    public void youTubeClicked(View v) {

        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse("https://www.youtube.com/" + Yid));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/" + Yid)));
        }
    }
    public void avatarClicked(View v) {
        if(PhotoUrl == "No data provided" | PhotoUrl.equals("No data provided")){

        }
        else {


            Intent intent = new Intent(OfficialActivity.this, AvatarView.class);
            intent.putExtra(Official.class.getName(), thisofficial);

            startActivity(intent);
        }
    }
}
