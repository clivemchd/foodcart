package shop.cart.foodcart;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Iterator;
import java.util.Map;

import static android.R.attr.data;
import static android.R.attr.id;
import static android.R.attr.name;
import static shop.cart.foodcart.R.layout.activity_main;
import static shop.cart.foodcart.R.layout.content_main;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        //get firebase Storage instance
        mStorageRef = FirebaseStorage.getInstance().getReference();

        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Log.d("Inside", String.valueOf(user));

        //get firebase realtime database
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
//       myRef.child("users").setValue("World!");


        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch sigup activity
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };



        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                long length = (long) map.get("count");
                for (int i = 1; i <= length; i++){
                    ImageButton imageBtn = new ImageButton(MainActivity.this);
                    LinearLayout linearLayout = (LinearLayout)findViewById(R.id.linear_layout_main);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.height = 700;

//                    Uri imgUri= Uri.parse("https://firebasestorage.googleapis.com/v0/b/foodcart-project.appspot.com/o/meal1.jpg?alt=media&token=3cde501f-9eed-4d95-86d7-4a95e73f741d");


//                    mStorageRef.child("users/me/profile.png").getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//                        @Override
//                        public void onSuccess(byte[] bytes) {
//                            // Use the bytes to display the image
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception exception) {
//                            // Handle any errors
//                        }
//                    });



//                    imageBtn.setImageURI(null);
//                    imageBtn.setImageURI(imgUri);
                    imageBtn.setImageResource(R.mipmap.meal2);
                    imageBtn.setScaleType(ImageButton.ScaleType.FIT_XY);
                    imageBtn.setLayoutParams(params);
                    linearLayout.addView(imageBtn);
                }
                Iterator<DataSnapshot> items = dataSnapshot.getChildren().iterator();
                while(items.hasNext()){
                    DataSnapshot item = items.next();
                    Log.d("Value", String.valueOf(item.child("name").getValue()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("faied data", "loadPost:onCancelled", databaseError.toException());
                // ...
            }

        };
        myRef = myRef.child("objects");
        myRef.addValueEventListener(postListener);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "No Items in Cart", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }






    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //sign out method
    public void signOut() {
        auth.signOut();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

        } else if (id == R.id.nav_cart) {
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "No Items in Cart", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        } else if (id == R.id.user_orders) {
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "No Items in Orders", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        } else if (id == R.id.nav_logout) {
            signOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }
}
