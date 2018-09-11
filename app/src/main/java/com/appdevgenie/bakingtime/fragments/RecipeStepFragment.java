package com.appdevgenie.bakingtime.fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appdevgenie.bakingtime.R;
import com.appdevgenie.bakingtime.activities.RecipeDetailsActivity;
import com.appdevgenie.bakingtime.constants.Constants;
import com.appdevgenie.bakingtime.model.Step;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;


public class RecipeStepFragment extends Fragment implements View.OnClickListener, Player.EventListener {

    private static final String TAG = RecipeStepFragment.class.getSimpleName();

    private View view;
    private int stepID;
    private TextView tvDescription;
    private TextView tvStepNumber;
    private ArrayList<Step> stepsArrayList;
    private ImageButton ibNextStep;
    private ImageButton ibPreviousStep;
    private SimpleExoPlayer exoPlayer;
    private PlayerView playerView;
    private Context context;
    private boolean dualPane;
    private MediaSessionCompat mediaSession;
    private PlaybackStateCompat.Builder stateBuilder;

    public RecipeStepFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_step_details, container, false);

        if (savedInstanceState == null) {
            Bundle bundle = getArguments();
            if (bundle != null) {
                stepsArrayList = bundle.getParcelableArrayList(Constants.PARSE_ALL_STEPS);
                stepID = bundle.getInt(Constants.PARSE_STEP_ID);
                dualPane = bundle.getBoolean(Constants.PARSE_DUAL_PANE);
            }
        } else {
            stepsArrayList = savedInstanceState.getParcelableArrayList(Constants.SAVED_SELECTED_STEP_LIST);
            stepID = savedInstanceState.getInt(Constants.SAVED_SELECTED_STEP_ID);
        }
        setupVariables();
        populateStep();

        return view;

    }

    private void setupVariables() {

        context = getContext();

        CardView cvStepDescription = view.findViewById(R.id.cvStepDescription);
        LinearLayout llStepSelection = view.findViewById(R.id.llStepSelection);
        if(dualPane){
            llStepSelection.setVisibility(View.GONE);
            cvStepDescription.setVisibility(View.VISIBLE);
        }
        if(!dualPane && isLandscape()){
            hideSystemUI();
        }
        tvDescription = view.findViewById(R.id.tvStepDetailLongDescription);
        tvStepNumber = view.findViewById(R.id.tvStepDetailNumber);
        ibNextStep = view.findViewById(R.id.ibStepDetailNext);
        ibPreviousStep = view.findViewById(R.id.ibStepDetailPrevious);
        ibNextStep.setOnClickListener(this);
        ibPreviousStep.setOnClickListener(this);

        initMediaSession();
    }

    private void populateStep() {

        playerView = view.findViewById(R.id.playerView);
        String videoString = stepsArrayList.get(stepID).getVideoURL();

        if (!TextUtils.isEmpty(videoString)) {
            playerView.setVisibility(View.VISIBLE);
            initializePlayer(Uri.parse(videoString));
        }else{
            playerView.setVisibility(View.INVISIBLE);
        }

        tvDescription.setText(stepsArrayList.get(stepID).getDescription());

        if (stepID == 0) {
            ibPreviousStep.setVisibility(View.INVISIBLE);
        } else if (stepID == stepsArrayList.size() - 1) {
            ibNextStep.setVisibility(View.INVISIBLE);
        } else {
            ibPreviousStep.setVisibility(View.VISIBLE);
            ibNextStep.setVisibility(View.VISIBLE);
        }

        if (stepID == 0) {
            tvStepNumber.setText(R.string.introduction);
        } else {
            tvStepNumber.setText(TextUtils.concat(getString(R.string.step), " ", String.valueOf(stepID)));
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.ibStepDetailPrevious:

                releasePlayer();
                stepID--;
                populateStep();

                break;

            case R.id.ibStepDetailNext:

                releasePlayer();
                stepID++;
                populateStep();

                break;
        }
    }

    private void initializePlayer(Uri videoUri) {

        if (exoPlayer == null) {

            DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(bandwidthMeter);
            DefaultTrackSelector trackSelector =
                    new DefaultTrackSelector(videoTrackSelectionFactory);

            exoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
            playerView.setPlayer(exoPlayer);
            playerView.hideController();

            exoPlayer.addListener(this);

            String userAgent = Util.getUserAgent(context, getString(R.string.app_name));
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                    userAgent, bandwidthMeter);
            MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(videoUri);

            exoPlayer.prepare(videoSource);
            exoPlayer.setPlayWhenReady(true);
        }
    }

    private void releasePlayer() {
        if(exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(Constants.SAVED_SELECTED_STEP_LIST, stepsArrayList);
        outState.putInt(Constants.SAVED_SELECTED_STEP_ID, stepID);
    }

    public boolean isLandscape() {
        return (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
    }

    private void hideSystemUI() {
        if(((RecipeDetailsActivity) getActivity()).getSupportActionBar() != null) {
            ((RecipeDetailsActivity) getActivity()).getSupportActionBar().hide();
        }
        View decorView = getActivity().getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE
                            // Set the content to appear under the system bars so that the
                            // content doesn't resize when the system bars hide and show.
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            // Hide the nav bar and status bar
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    }

    public void initMediaSession (){

        mediaSession = new MediaSessionCompat(context, TAG);
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS);
        mediaSession.setMediaButtonReceiver(null);

        stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE
                );
        mediaSession.setPlaybackState(stateBuilder.build());
        mediaSession.setCallback(new MySessionCallback());
        mediaSession.setActive(true);
    }

    @Override
    public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

        if ((playbackState == Player.STATE_READY) && playWhenReady) {
            stateBuilder.setState(PlaybackStateCompat.STATE_PLAYING, exoPlayer.getCurrentPosition(), 1f);
        } else if ((playbackState == Player.STATE_READY)){
            stateBuilder.setState(PlaybackStateCompat.STATE_PAUSED, exoPlayer.getCurrentPosition(), 1f);
        }
        mediaSession.setPlaybackState(stateBuilder.build());
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }

    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            exoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            exoPlayer.setPlayWhenReady(false);
        }
    }
}
