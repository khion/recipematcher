package ca.bcit.recipematcher;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class videoAdapter extends ArrayAdapter<Video> {
    private Activity context;
    private List<Video> videoList;

    public videoAdapter(Activity context, List<Video> videoList) {
        super(context, R.layout.list_video_layout, videoList);
        this.context = context;
        this.videoList = videoList;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.list_video_layout, null, true);

        TextView tvVideoTitle= listViewItem.findViewById(R.id.video_title);
        YouTubePlayerView youTubePlayerView = listViewItem.findViewById(R.id.video_view);


        Video video = videoList.get(position);
        tvVideoTitle.setText(video.getTitle());

        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                youTubePlayer.cueVideo(video.getVideo_id(), 0);
            }
        });


        return listViewItem;
    }

}
