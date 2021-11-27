package ca.bcit.recipematcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.ArrayList;
import java.util.List;


public class VideoStreamingActivity extends AppCompatActivity {
    private Menu menu_bar;

    YouTubePlayerView youTubePlayerView;

    private EditText search_view;
    private FirebaseFirestore mRef;
    private ListView lvVideoList;
    private List<Video> videoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_streaming);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        search_view = findViewById(R.id.edit_Search);

        mRef = FirebaseFirestore.getInstance();
        lvVideoList = findViewById(R.id.searchList);
        videoList = new ArrayList<Video>();

        lvVideoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Video video = videoList.get(i);
                String youtubeUrl = video.getVideo_id();
                String youtubeTitle = video.getTitle();

                Intent intent = new Intent(VideoStreamingActivity.this, VideoPlayer.class);
                intent.putExtra("URL", youtubeUrl);
                intent.putExtra("title", youtubeTitle);
                startActivity(intent);
            }
        });

    }


    /**
     * On click method that will display a list of recipes corresponding to
     * the search string values
     *
     * @param view view
     */
    public void onSearchClick(View view) {

        String search_string = search_view.getText().toString().trim();
        if (TextUtils.isEmpty(search_string)) {
            Toast.makeText(VideoStreamingActivity.this, "Enter some keywords", Toast.LENGTH_SHORT).show();
        } else {
            mRef.collection("video_stream")
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Video video = document.toObject(Video.class);
                            String videoTitle = video.getTitle();
                            if (videoTitle.contains(search_string)) {
                                videoList.add(video);
                            }
                        }
                        videoAdapter adapter = new videoAdapter(VideoStreamingActivity.this, videoList);
                        lvVideoList.setAdapter(adapter);
                    }
                }
            });
        }
        videoList.clear();
    }

    @Override
    protected void onStart() {
        super.onStart();

        List<Video> fireStoreVideo = new ArrayList<Video>();;
        mRef.collection("video_stream")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Video video = document.toObject(Video.class);
                        fireStoreVideo.add(video);
                    }

                    for (int i = 0; i < 5; i++) {
                        int random = (int )(Math.random() * (fireStoreVideo.size()-1) + 1);
                        videoList.add(fireStoreVideo.get(random));
                    }
                    videoAdapter adapter = new videoAdapter(VideoStreamingActivity.this, videoList);
                    lvVideoList.setAdapter(adapter);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void onLogoutClick(MenuItem menu) {
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        fAuth.signOut();
        Intent intent = new Intent(VideoStreamingActivity.this, LandingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * This will check if the user signed in and if user is signed in,
     * it will direct to UserProfile Activity be the button is clicked
     * @param menu menu
     */
    public void onUserProfileClick(MenuItem menu) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent;
        if (currentUser != null) {
            intent = new Intent(VideoStreamingActivity.this, UserProfileActivity.class);
        } else {
            intent = new Intent(VideoStreamingActivity.this, LandingActivity.class);
        }
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}