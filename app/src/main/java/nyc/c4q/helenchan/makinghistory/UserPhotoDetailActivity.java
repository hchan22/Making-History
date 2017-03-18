package nyc.c4q.helenchan.makinghistory;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import me.anwarshahriar.calligrapher.Calligrapher;

/**
 * Created by akashaarcher on 3/16/17.
 */

public class UserPhotoDetailActivity extends AppCompatActivity {

    private TextView usernameTextView;
    private ImageView userPhotoDetailImageView;
    private DatabaseReference ref;

    private String TAG = "UserPhotoDetailActivity";
    private String userPhotoDetailUrl;
    private RelativeLayout userPhotoDetailLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_photo_detail);
        if(Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.parseColor("#5e454b"));
        }
        usernameTextView = (TextView) findViewById(R.id.username);
        userPhotoDetailImageView = (ImageView) findViewById(R.id.user_photo_detail);
        userPhotoDetailLayout = (RelativeLayout) findViewById(R.id.user_photo_activity);

        setFontType();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Your Photo");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("UserPhotoUri")) {
                userPhotoDetailUrl = extras.getString("UserPhotoUri");

                usernameTextView.setText(SignInActivity.mUsername);
                Glide.with(getApplicationContext())
                        .load(userPhotoDetailUrl)
                        .override(1200, 1200)
                        .centerCrop()
                        .into(userPhotoDetailImageView);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user_photo_detail_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    private void setFontType() {
        Calligrapher calligrapher = new Calligrapher(this);
        calligrapher.setFont(this, "ArimaMadurai-Bold.ttf", true);
        calligrapher.setFont(findViewById(R.id.user_photo_activity), "Raleway-Regular.ttf");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.user_photo_share):
                //do stuff
                return true;
            case android.R.id.home:

                NavUtils.navigateUpFromSameTask(this);
                return true;

            case (R.id.user_photo_delete):
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure you want to delete this picture?");
                builder.setCancelable(true);

                builder.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteUserPhoto();
                            }
                        });

                builder.setNegativeButton(
                        "Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });

                AlertDialog deleteAlert = builder.create();
                deleteAlert.show();
        }
        return super.onOptionsItemSelected(item);
    }

    public void deleteUserPhoto() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.i(TAG, "photo url: " + userPhotoDetailUrl);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query photoQuery = ref.child("Users").child(uid).child("ContentList").orderByChild("url").equalTo(userPhotoDetailUrl);
        photoQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot photoSnapshot : dataSnapshot.getChildren()) {
                    photoSnapshot.getRef().removeValue();
                    Log.i(TAG, "photo removed " + userPhotoDetailUrl);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
    }

}
