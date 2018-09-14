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
import com.appdevgenie.bakingtime.activities.RecipeStepInfoActivity;
import com.appdevgenie.bakingtime.constants.Constants;
import com.appdevgenie.bakingtime.model.Step;
import com.appdevgenie.bakingtime.utils.SnackbarUtil;
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

import butterknife.BindView;
import butterknife.ButterKnife;


public class RecipeStepFragment extends Fragment implements View.OnClickListener,
        Player.EventListener {

    private static final String TAG = RecipeStepFragment.class.getSimpleName();

    @BindView(R.id.tvStepDetailLongDescription)
    TextView tvDescription;
    @BindView(R.id.tvStepDetailNumber)
    TextView tvStepNumber;
    @BindView(R.id.ibStepDetailNext)
    ImageButton ibNextStep;
    @BindView(R.id.ibStepDetailPrevious)
    ImageButton ibPreviousStep;

    private SimpleExoPlayer exoPlayer;
    private PlayerView playerView;
    private View view;
    private int stepID;
    private Context context;
    private boolean dualPane;
    private ArrayList<Step> stepsArrayList;
    private MediaSessionCompat mediaSession;
    private PlaybackStateCompat.Builder stateBuilder;
    private String videoString;
    private long playerPosition;
    private long playerLastPosition;
    private boolean playerPaused;

    public RecipeStepFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_step_details, container, false);
        ButterKnife.bind(this, view);

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
            playerLastPosition = savedInstanceState.getLong(Constants.EXO_PLAYER_POSITION);
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
            LinearLayout linearLayout = getActivity().findViewById(R.id.llDetailsInfo);
            linearLayout.setVisibility(View.GONE);
            hideSystemUI();
        }
        ibNextStep.setOnClickListener(this);
        ibPreviousStep.setOnClickListener(this);

        initMediaSession();
    }

    private void populateStep() {

        //releasePlayer();
        playerView = view.findViewById(R.id.playerView);
        videoString = stepsArrayList.get(stepID).getVideoURL();
        //initializePlayer(videoString);

        if (!TextUtils.isEmpty(videoString)) {
            playerView.setVisibility(View.VISIBLE);
        }else{
            playerView.setVisibility(View.INVISIBLE);
            SnackbarUtil.snackBarBuilder(getActivity().findViewById(android.R.id.content), getString(R.string.no_video_no_url)).show();
        }

        tvDescription.setText(stepsArrayList.get(stepID).getDescription());

        if (stepID == 0) {
            ibPreviousStep.setVisibility(View.INVISIBLE);
            tvStepNumber.setText(R.string.introduction);
        } else if (stepID == stepsArrayList.size() - 1) {
            ibNextStep.setVisibility(View.INVISIBLE);
        } else {
            ibPreviousStep.setVisibility(View.VISIBLE);
            ibNextStep.setVisibility(View.VISIBLE);
            tvStepNumber.setText(TextUtils.concat(getString(R.string.step), " ", String.valueOf(stepID)));
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.ibStepDetailPrevious:
                stepID--;
                populateStep();
                break;

            case R.id.ibStepDetailNext:
                stepID++;
                populateStep();
                break;
        }
    }

    private void initializePlayer() {

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
                    .createMediaSource(Uri.parse(videoString));

            /*DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(bandwidthMeter);
            DefaultTrackSelector trackSelector =
                    new DefaultTrackSelector(videoTrackSelectionFactory);
            LoadControl loadControl = new DefaultLoadControl();
            exoPlayer = ExoPlayerFactory.newSimpleInstance(
                    new DefaultRenderersFactory(playerView.getContext()),
                    trackSelector, loadControl);
            exoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
            playerView.setPlayer(exoPlayer);
            playerView.hideController();

            exoPlayer.addListener(this);

            String userAgent = Util.getUserAgent(context, getString(R.string.app_name));
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                    userAgent, bandwidthMeter);
            MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(videoString));*/

            exoPlayer.prepare(videoSource);
            if(playerLastPosition != 0 && !playerPaused) {
                exoPlayer.seekTo(playerLastPosition);
            }else{
                exoPlayer.seekTo(playerPosition);
            }
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
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23 || exoPlayer == null) {
            initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || exoPlayer == null)) {
            initializePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(exoPlayer != null){
            playerPosition = exoPlayer.getContentPosition();
            playerPaused = true;
        }
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
        outState.putLong(Constants.EXO_PLAYER_POSITION, playerPosition);
    }

    public boolean isLandscape() {
        return (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
    }

    private void hideSystemUI() {
        if(((RecipeStepInfoActivity) getActivity()).getSupportActionBar() != null) {
            ((RecipeStepInfoActivity) getActivity()).getSupportActionBar().hide();
        }
        if(getActivity().findViewById(R.id.llStepSelection) != null){
            getActivity().findViewById(R.id.llStepSelection).setVisibility(View.GONE);
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
