package com.it_tech613.skydark.ui.liveTv;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.PlaybackPreparer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.drm.DefaultDrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.drm.FrameworkMediaDrm;
import com.google.android.exoplayer2.drm.HttpMediaDrmCallback;
import com.google.android.exoplayer2.drm.UnsupportedDrmException;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
import com.google.android.exoplayer2.offline.FilteringManifestParser;
import com.google.android.exoplayer2.source.BehindLiveWindowException;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.ads.AdsLoader;
import com.google.android.exoplayer2.source.ads.AdsMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.dash.manifest.DashManifestParser;
import com.google.android.exoplayer2.source.dash.manifest.RepresentationKey;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistParser;
import com.google.android.exoplayer2.source.hls.playlist.RenditionKey;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifestParser;
import com.google.android.exoplayer2.source.smoothstreaming.manifest.StreamKey;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.RandomTrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.DebugTextViewHelper;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.TrackSelectionView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.ErrorMessageProvider;
import com.google.android.exoplayer2.util.EventLogger;
import com.google.android.exoplayer2.util.Util;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.it_tech613.skydark.R;
import com.it_tech613.skydark.apps.Constants;
import com.it_tech613.skydark.apps.MyApp;
import com.it_tech613.skydark.models.DataModel;
import com.it_tech613.skydark.models.EPGChannel;
import com.it_tech613.skydark.models.EPGEvent;
import com.it_tech613.skydark.ui.MainActivity;
import com.it_tech613.skydark.ui.WebViewActivity;
import com.it_tech613.skydark.ui.movies.PackageDlg;
import com.it_tech613.skydark.utils.MyFragment;
import com.it_tech613.skydark.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

import static android.content.Context.WINDOW_SERVICE;

public class FragmentExoLiveTv extends MyFragment implements View.OnClickListener, PlaybackPreparer, PlayerControlView.VisibilityListener{

    private static final String LOG_TAG = FragmentExoLiveTv.class.getSimpleName();

    public static final String DRM_SCHEME_EXTRA = "drm_scheme";
    public static final String DRM_LICENSE_URL_EXTRA = "drm_license_url";
    public static final String DRM_KEY_REQUEST_PROPERTIES_EXTRA = "drm_key_request_properties";
    public static final String DRM_MULTI_SESSION_EXTRA = "drm_multi_session";
    public static final String PREFER_EXTENSION_DECODERS_EXTRA = "prefer_extension_decoders";

    public static final String AD_TAG_URI_EXTRA = "ad_tag_uri";

    public static final String ABR_ALGORITHM_EXTRA = "abr_algorithm";
    private static final String ABR_ALGORITHM_DEFAULT = "default";
    private static final String ABR_ALGORITHM_RANDOM = "random";

    private static final String DRM_SCHEME_UUID_EXTRA = "drm_scheme_uuid";

    private static final String KEY_TRACK_SELECTOR_PARAMETERS = "track_selector_parameters";
    private static final String KEY_WINDOW = "window";
    private static final String KEY_POSITION = "position";
    private static final String KEY_AUTO_PLAY = "auto_play";

    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private static final CookieManager DEFAULT_COOKIE_MANAGER;
    static {
        DEFAULT_COOKIE_MANAGER = new CookieManager();
        DEFAULT_COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }

    private PlayerView playerView;
    private FrameLayout frameLayout;
    private TextView debugTextView;
    private DataSource.Factory mediaDataSourceFactory;
    private SimpleExoPlayer player;
    private FrameworkMediaDrm mediaDrm;
    private MediaSource mediaSource;
    private DefaultTrackSelector trackSelector;
    private DefaultTrackSelector.Parameters trackSelectorParameters;
    private DebugTextViewHelper debugViewHelper;
    private TrackGroupArray lastSeenTrackGroupArray;

    private boolean startAutoPlay;
    private int startWindow;
    private long startPosition;

    private AdsLoader adsLoader;
    private Uri loadedAdTagUri;
    private ViewGroup adUiViewGroup;

    private String contentUri;

    private boolean is_full=false;
    private int categoryPos=0, channelPos=0, playChanelPos = 0;
    private LiveTvProgramsAdapter liveTvProgramsAdapter;
    private ConstraintLayout rootView;
    private Handler mEpgHandler = new Handler();
    private Runnable mEpgTicker,mTicker;

    private Button addFav;
    private CategoryAdapter categoryAdapter;
    private ImageView fav_icon, image_icon;
    private ChannelAdapter channelAdapter;
    private RecyclerView category_recyclerview, channel_recyclerview;
    private Handler mHandler = new Handler();
    private LinearLayout ly_bottom,ly_info,ly_resolution,ly_audio,ly_subtitle,ly_fav,ly_tv_schedule;
    private TextView txt_title,txt_dec, txt_channel, txt_time_passed, txt_remain_time, txt_last_time, txt_current_dec, txt_next_dec, txt_date, channel_name;
    private ImageView channel_logo, image_clock, image_star;
    private SeekBar seekbar;

    private int pro,osd_time,width,height;

    private List<EPGEvent> epgModelList;
    private List<String> pkg_datas;

    private boolean is_msg = false;
    private Handler rssHandler = new Handler();
    private TextView txt_rss;
    private RelativeLayout lay_header;
    private int msg_time = 0;
    private String rss="";
    private Runnable rssTicker;
    private int rss_time;

    private int lastPlayingCategoryPos=0, lastPlayingChannelPos=0;

    public native String getLiveUrl();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_exo_live_tv, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity)requireActivity()).toggleFullScreen(true);
        mediaDataSourceFactory = buildDataSourceFactory(true);
        requireActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);

        if (Constants.getRecentFullModel(MyApp.fullModels_filter).getChannels().size()==0) {
            lastPlayingCategoryPos=1;
            categoryPos=1;
        }
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            lastPlayingCategoryPos = bundle.getInt("lastPlayingCategoryPos");
            categoryPos = lastPlayingCategoryPos;
            lastPlayingChannelPos = bundle.getInt("lastPlayingChannelPos");
            channelPos = lastPlayingChannelPos;
        }
        pkg_datas = new ArrayList<>();
        pkg_datas.addAll(Arrays.asList(getResources().getStringArray(R.array.package_list2)));
        category_recyclerview = view.findViewById(R.id.category_recyclerview);
        channel_recyclerview = view.findViewById(R.id.subcategory_recyclerview);
        lay_header = view.findViewById(R.id.lay_header);
        RecyclerView programs_recyclerview = view.findViewById(R.id.programs_recyclerview);
        category_recyclerview.setLayoutManager(new LinearLayoutManager(requireContext()));
        channel_recyclerview.setLayoutManager(new LinearLayoutManager(requireContext()));
        programs_recyclerview.setLayoutManager(new LinearLayoutManager(requireContext()));
        liveTvProgramsAdapter=new LiveTvProgramsAdapter(new ArrayList<EPGEvent>());
        addFav = view.findViewById(R.id.button4);
        fav_icon = view.findViewById(R.id.fav_icon);
        if (getResources().getBoolean(R.bool.is_phone)) fav_icon.setVisibility(View.GONE);
        image_icon = view.findViewById(R.id.image_icon);
        channel_name = view.findViewById(R.id.channel_name);
        Glide.with(requireContext())
                .load(Constants.GetIcon(requireContext()))
                .apply(new RequestOptions().placeholder(R.drawable.icon).error(R.drawable.icon).signature(new ObjectKey("myKey0")))
                .into(image_icon);
        txt_rss = view.findViewById(R.id.txt_rss);
        //for info bar
        ly_bottom=view.findViewById(R.id.ly_bottom);
        ly_bottom.setVisibility(View.GONE);
        ly_info=view.findViewById(R.id.ly_info);
        ly_resolution=view.findViewById(R.id.ly_resolution);
        ly_audio=view.findViewById(R.id.ly_audio);
        ly_audio.setVisibility(View.INVISIBLE);
        ly_subtitle=view.findViewById(R.id.ly_subtitle);
        ly_subtitle.setVisibility(View.INVISIBLE);
        ly_fav=view.findViewById(R.id.ly_fav);
        ly_tv_schedule=view.findViewById(R.id.ly_tv_schedule);
        txt_title=view.findViewById(R.id.txt_title);
        txt_dec=view.findViewById(R.id.txt_dec);
        txt_channel=view.findViewById(R.id.txt_channel);
        txt_time_passed=view.findViewById(R.id.txt_time_passed);
        txt_remain_time=view.findViewById(R.id.txt_remain_time);
        txt_last_time=view.findViewById(R.id.txt_last_time);
        txt_current_dec=view.findViewById(R.id.txt_current_dec);
        txt_next_dec=view.findViewById(R.id.txt_next_dec);
        channel_logo=view.findViewById(R.id.channel_logo);
        image_clock=view.findViewById(R.id.image_clock);
        image_star=view.findViewById(R.id.image_star);
        seekbar=view.findViewById(R.id.seekbar);
        txt_date = view.findViewById(R.id.txt_date);

        ly_subtitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSubtitlesOptions();
            }
        });
        ly_resolution.setOnClickListener(v -> {

        });
        ly_fav.setOnClickListener(v -> addToFav());
        ly_tv_schedule.setOnClickListener(v -> startActivity(new Intent(requireContext(), WebViewActivity.class)));
        ly_audio.setOnClickListener(v -> showAudioOptions());
        addFav.setOnClickListener(v -> addToFav());
        
        setAddFavText();
        osd_time = (int) MyApp.instance.getPreference().get(Constants.getOSD_TIME());
        channelAdapter =new ChannelAdapter(MyApp.fullModels_filter.get(categoryPos).getChannels(), new Function2<EPGChannel, Integer, Unit>() {
            @Override
            public Unit invoke(EPGChannel channelModel, Integer integer) {
                if (playChanelPos == integer){
                    toggleFullScreen();
                }else {
                    playChannel(channelModel,integer);
                }
                return null;
            }
        }, (epgChannel, integer) -> {
            channelPos = integer;
            setAddFavText();
            EpgTimer();
            return null;
        });
        categoryAdapter = new CategoryAdapter(MyApp.live_categories_filter, (categoryModel, position, is_clicked) -> {
            if (categoryModel.getId() == Constants.xxx_category_id && position!=0 && is_clicked) {
                PinDlg pinDlg = new PinDlg(requireContext(), new PinDlg.DlgPinListener() {
                    @Override
                    public void OnYesClick(Dialog dialog, String pin_code) {
                        dialog.dismiss();
                        String pin = (String) MyApp.instance.getPreference().get(Constants.getPIN_CODE());
                        if (pin_code.equalsIgnoreCase(pin)) {
                            dialog.dismiss();
                            doWork(is_clicked, position);
                        } else {
                            dialog.dismiss();
                            Toast.makeText(requireContext(), "Your Pin code was incorrect. Please try again", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void OnCancelClick(Dialog dialog, String pin_code) {
                        dialog.dismiss();
                    }
                });
                pinDlg.show();
            } else doWork(is_clicked, position);

            return null;
        });
        category_recyclerview.setAdapter(categoryAdapter);
        channel_recyclerview.setAdapter(channelAdapter);
        programs_recyclerview.setAdapter(liveTvProgramsAdapter);
        rootView =view.findViewById(R.id.rootview);

        frameLayout = view.findViewById(R.id.rootVideoPlayerMovieActivity);
        playerView = view.findViewById(R.id.player_view);
        playerView.setControllerVisibilityListener(this);
        playerView.setErrorMessageProvider(new PlayerErrorMessageProvider());
        playerView.requestFocus();
        playerView.setOnClickListener(this);

        playerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                toggleFullScreen();
                return false;
            }
        });

        debugTextView = view.findViewById(R.id.debug_text_view);

        width = (int) (MyApp.SCREEN_WIDTH*0.6*0.86);
        height = (int) (MyApp.SCREEN_HEIGHT*0.6);
        changeVideoViewSize(width,height);
        if (savedInstanceState != null) {
            trackSelectorParameters = savedInstanceState.getParcelable(KEY_TRACK_SELECTOR_PARAMETERS);
            startAutoPlay = savedInstanceState.getBoolean(KEY_AUTO_PLAY);
            startWindow = savedInstanceState.getInt(KEY_WINDOW);
            startPosition = savedInstanceState.getLong(KEY_POSITION);
        } else {
            trackSelectorParameters = new DefaultTrackSelector.ParametersBuilder().build();
            clearStartPosition();
        }
        EpgTimer();
        playChannel(MyApp.fullModels_filter.get(lastPlayingCategoryPos).getChannels().get(lastPlayingChannelPos),lastPlayingChannelPos);
    }

    private void doWork(boolean is_clicked, int position) {
        if (!is_clicked) return;
        categoryPos=position;
        channelAdapter.setList(MyApp.fullModels_filter.get(categoryPos).getChannels());
        channelPos=0;
        playChanelPos=-1;
        if (lastPlayingCategoryPos==categoryPos) {
            playChanelPos=lastPlayingChannelPos;
            category_recyclerview.scrollToPosition(lastPlayingChannelPos);
        }
        channelAdapter.setSelected(playChanelPos);
        channelAdapter.setFocus(0);
        setAddFavText();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            playChannel(MyApp.fullModels_filter.get(lastPlayingCategoryPos).getChannels().get(lastPlayingChannelPos),lastPlayingChannelPos);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Utils.deleteCache(requireContext());
        if (Util.SDK_INT <= 23 || player == null) {
            playChannel(MyApp.fullModels_filter.get(lastPlayingCategoryPos).getChannels().get(lastPlayingChannelPos),lastPlayingChannelPos);
        }
    }

    private void getRespond(){
        try {
            String string = MyApp.instance.getIptvclient().login(Constants.GetKey(requireActivity()));
            try {
                JSONObject object = new JSONObject(string);
                if (object.getBoolean("status")) {
                    JSONObject data = object.getJSONObject("data");
                    Gson gson = new Gson();
                    DataModel dataModel = gson.fromJson(data.toString(), DataModel.class);
                    Constants.userDataModel = dataModel;
                    is_msg = !dataModel.getMessage_on_off().equalsIgnoreCase("0");
                    try {
                        msg_time = Integer.parseInt(dataModel.getMessage_time());
                    }catch (Exception e){
                        msg_time = 20;
                    }
                    String rss_feed = "                 "+dataModel.getMessage()+"                 ";
                    if(rss.equalsIgnoreCase(rss_feed)){
                        lay_header.setVisibility(View.GONE);
                    }else {
                        rss =rss_feed;
                        lay_header.setVisibility(View.VISIBLE);
                    }
                    if(is_msg){
                        lay_header.setVisibility(View.VISIBLE);
                        txt_rss.setText(rss);
                        Animation bottomToTop = AnimationUtils.loadAnimation(requireContext(), R.anim.bottom_to_top);
                        txt_rss.clearAnimation();
                        txt_rss.startAnimation(bottomToTop);
                    }else {
                        lay_header.setVisibility(View.GONE);
                    }
                    rssTimer();
                } else {
                    Toast.makeText(requireContext(), "Server Error!", Toast.LENGTH_SHORT).show();
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void rssTimer() {
        rss_time = msg_time;
        rssTicker = new Runnable() {
            public void run() {
                if (rss_time < 1) {
                    lay_header.setVisibility(View.GONE);
                    return;
                }
                runRssTicker();
            }
        };
        rssTicker.run();
    }

    private void runRssTicker() {
        rss_time --;
        long next = SystemClock.uptimeMillis() + 1000;
        rssHandler.postAtTime(rssTicker, next);
    }

    private void playChannel(EPGChannel channelModel, Integer integer) {
        playChanelPos = integer;
        channelPos = integer;
        if (channelModel.getCategory_id() == Constants.xxx_category_id && categoryPos == 0) {
            PinDlg pinDlg = new PinDlg(requireContext(), new PinDlg.DlgPinListener() {
                @Override
                public void OnYesClick(Dialog dialog, String pin_code) {
                    dialog.dismiss();
                    String pin = (String) MyApp.instance.getPreference().get(Constants.getPIN_CODE());
                    if (pin_code.equalsIgnoreCase(pin)) {
                        dialog.dismiss();
                        playVideo();
                    } else {
                        dialog.dismiss();
                        Toast.makeText(requireContext(), "Your Pin code was incorrect. Please try again", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void OnCancelClick(Dialog dialog, String pin_code) {
                    dialog.dismiss();
                }
            });
            pinDlg.show();
        } else playVideo();
        setAddFavText();
        EpgTimer();
        channelAdapter.setSelected(playChanelPos);
    }

    int maxTime;
    private void listTimer() {
        maxTime = osd_time;
        mTicker = new Runnable() {
            public void run() {
                if (maxTime < 1) {
                    ly_bottom.setVisibility(View.GONE);
                    return;
                }
                runNextTicker();
            }
        };
        mTicker.run();
    }
    private void runNextTicker() {
        maxTime--;
        long next = SystemClock.uptimeMillis() + 1000;
        mHandler.postAtTime(mTicker, next);
    }

    private void updateProgressBar() {
        mHandler.removeCallbacks(mUpdateTimeTask);
        mHandler.postDelayed(mUpdateTimeTask, 0);
    }
    
    private Runnable mUpdateTimeTask = new Runnable() {
        @SuppressLint("SetTextI18n")
        public void run() {
            if (playerView != null) {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("HH.mm a EEE MM/dd");
                long nowLocalTimeStamp = System.currentTimeMillis();
                final EPGChannel playChannel = MyApp.fullModels_filter.get(lastPlayingCategoryPos).getChannels().get(lastPlayingChannelPos);
                if(epgModelList!=null && epgModelList.size()>0){
                    try {
                        EPGEvent epgEvent = epgModelList.get(0);
                        long startStamp =epgEvent.getStartTime().getTime()+ Constants.SEVER_OFFSET;
                        long endStamp = epgEvent.getEndTime().getTime()+ Constants.SEVER_OFFSET;
                        if(nowLocalTimeStamp>startStamp){
                            txt_title.setText(new String (Base64.decode(epgEvent.getTitle(), Base64.DEFAULT)));
                            txt_dec.setText(new String (Base64.decode(epgEvent.getDec(), Base64.DEFAULT)));
                            try {
                                txt_channel.setText(playChannel.getNumber() + " " + playChannel.getName());
                            }catch (Exception e1){
                                txt_channel.setText("    ");
                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @SuppressLint("SetTextI18n")
                                    @Override
                                    public void run() {
                                        txt_channel.setText(playChannel.getNumber() + " " + playChannel.getName());
                                    }
                                }, 5000);
                            }
                            txt_date.setText(dateFormat.format(new Date()));
                            int pass_min = (int) ((nowLocalTimeStamp - startStamp)/(1000*60));
                            int remain_min = (int)(endStamp-nowLocalTimeStamp)/(1000*60);
                            int progress = (int) pass_min*100/(int)((epgEvent.getEndTime().getTime()-epgEvent.getStartTime().getTime())/60/1000);
                            pro  = progress;
                            seekbar.setProgress(progress);
                            txt_time_passed.setText("Started " + pass_min +" mins ago");
                            txt_remain_time.setText("+"+remain_min+" min");
                            txt_last_time.setText(sdf.format(new Date(endStamp)));
//                            Log.e(TAG,epgEvent.getTitle()+" "+epgModelList.get(1).getTitle());
                            txt_current_dec.setText(new String (Base64.decode(epgEvent.getTitle(), Base64.DEFAULT)));
                            txt_next_dec.setText(new String (Base64.decode(epgModelList.get(1).getTitle(), Base64.DEFAULT)));
                            if(playChannel.is_favorite()){
                                image_star.setVisibility(View.VISIBLE);
                            }else {
                                image_star.setVisibility(View.GONE);
                            }
                            if(playChannel.getTv_archive()==1){
                                image_clock.setVisibility(View.VISIBLE);
                            }else {
                                image_clock.setVisibility(View.GONE);
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    txt_title.setText("No Information");
                    txt_dec.setText("No Information");
                    try {
                        txt_channel.setText(playChannel.getNumber() + " " + playChannel.getName());
                    }catch (Exception e2){
                        txt_channel.setText("    ");
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                txt_channel.setText(playChannel.getNumber() + " " + playChannel.getName());
                            }
                        }, 5000);
                    }
                    txt_date.setText(dateFormat.format(new Date()));
                    txt_time_passed.setText("      mins ago");
                    txt_remain_time.setText("      min");
                    txt_last_time.setText("         ");
                    seekbar.setProgress(0);
                    txt_current_dec.setText("No Information");
                    txt_next_dec.setText("No Information");
                }
            }
            mHandler.postDelayed(this, 500);
        }
    };

    private void setAddFavText(){
        if(MyApp.fullModels_filter.size()>0&& MyApp.fullModels_filter.get(categoryPos).getChannels().size()>0 ){
            channel_name.setText(MyApp.fullModels_filter.get(categoryPos).getChannels().get(channelPos).getName());
            if (MyApp.fullModels_filter.get(categoryPos).getChannels().get(channelPos).is_favorite()) {
                addFav.setText(getResources().getString(R.string.remove_favorites));
                fav_icon.setImageResource(R.drawable.heart_filled);
            }
            else {
                addFav.setText(getResources().getString(R.string.add_to_favorite));
                fav_icon.setImageResource(R.drawable.heart_unfilled);
            }
        }
    }

    private void addToFav() {
        Log.e("OnAddFavClick","received");
        EPGChannel selectedChannel = MyApp.fullModels_filter.get(categoryPos).getChannels().get(channelPos);
        if (selectedChannel.is_favorite()) {
            pkg_datas.set(0, "Add to Fav");
            selectedChannel.setIs_favorite(false);
            boolean is_exist = false;
            int pp = 0;
            for (int i = 0; i < MyApp.fullModels.get(1).getChannels().size(); i++) {
                if (MyApp.fullModels.get(1).getChannels().get(i).getName().equals(selectedChannel.getName())) {
                    is_exist = true;
                    pp = i;
                }
            }
            if (is_exist) {
                MyApp.fullModels.get(1).getChannels().remove(pp);
            }
            //get favorite channel names list
            List<String> fav_channel_names=new ArrayList<>();
            for (EPGChannel epgChannel: MyApp.fullModels.get(1).getChannels()){
                fav_channel_names.add(epgChannel.getName());
            }
            //set
            MyApp.instance.getPreference().put(Constants.getFAV_LIVE_CHANNELS(), fav_channel_names);
            Log.e("ADD_FAV","removed");
        } else {
            pkg_datas.set(0, "Remove from Fav");
            selectedChannel.setIs_favorite(true);
            MyApp.fullModels.get(1).getChannels().add(selectedChannel);
            //get favorite channel names list
            List<String> fav_channel_names=new ArrayList<>();
            for (EPGChannel epgChannel: MyApp.fullModels.get(1).getChannels()){
                fav_channel_names.add(epgChannel.getName());
            }
            //set
            MyApp.instance.getPreference().put(Constants.getFAV_LIVE_CHANNELS(), fav_channel_names);
            Log.e("LIVE_RATIO","added");
        }
        if (categoryPos==1) categoryAdapter.notifyDataSetChanged();
        setAddFavText();
    }

    private void EpgTimer(){
        mEpgHandler.removeCallbacks(mEpgTicker);
//        epg_time = 1;
        mEpgTicker = new Runnable() {
            public void run() {
                new Thread(()->getEpg()).start();
                runNextEpgTicker();
            }
        };
        mEpgTicker.run();
    }

    private void runNextEpgTicker() {
//        epg_time--;
        long next = SystemClock.uptimeMillis() + 60000;
        mEpgHandler.postAtTime(mEpgTicker, next);
    }

    private void releaseMediaPlayer() {
        if (player != null) {
            updateTrackSelectorParameters();
            updateStartPosition();
            debugViewHelper.stop();
            debugViewHelper = null;
            player.release();
            player = null;
            mediaSource = null;
            trackSelector = null;
        }
        releaseMediaDrm();
    }

//    private String TAG = this.getClass().getSimpleName();

    private void toggleFullScreen(){
        if (is_full){
//            Log.e(TAG,"Small screen");
            is_full=false;
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(rootView);
            constraintSet.setGuidelinePercent(R.id.guideline1, 0.2f);
            constraintSet.setGuidelinePercent(R.id.guideline2, 0.4f);
            constraintSet.setGuidelinePercent(R.id.guideline3, 0.6f);

            constraintSet.applyTo(rootView);
            width = (int) (MyApp.SCREEN_WIDTH*0.6*0.86);
            height = (int) (MyApp.SCREEN_HEIGHT*0.6);
            changeVideoViewSize(width,height);
//            ((MainActivity) requireActivity()).toggleFullScreen(is_full);
            ly_bottom.setVisibility(View.GONE);
            lay_header.setVisibility(View.GONE);
        }else {
//            Log.e(TAG,"Full screen");
            is_full=true;
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(rootView);
            constraintSet.setGuidelinePercent(R.id.guideline1, 0.0f);
            constraintSet.setGuidelinePercent(R.id.guideline2, 0.0f);
            constraintSet.setGuidelinePercent(R.id.guideline3, 1.0f);

            constraintSet.applyTo(rootView);
//            ((MainActivity) requireActivity()).toggleFullScreen(is_full);
            width = MyApp.SCREEN_WIDTH;
            height = MyApp.SCREEN_HEIGHT;
            changeVideoViewSize(width,height);
            showInfoBar();
        }
    }

    private int current_position = 0;
    private void showInternalPlayers(){
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
        builder.setTitle("Player Option");
        String[] screen_mode_list = {"VLC Player", "IJK Player", "Exo Player"};
        if (MyApp.instance.getPreference().get(Constants.getCurrentPlayer())!=null)
            current_position = (int) MyApp.instance.getPreference().get(Constants.getCurrentPlayer());
        else current_position = 0;
        builder.setSingleChoiceItems(screen_mode_list, current_position,
                (dialog, which) -> current_position =which);
        builder.setPositiveButton("OK", (dialog, which) -> {
            switch (current_position){
                case 0:
                    ((MainActivity)requireActivity()).fragmentList.set(1,new FragmentLiveTv());
                    break;
                case 1:
                    ((MainActivity)requireActivity()).fragmentList.set(1,new FragmentIjkLiveTv());
                    break;
                case 2:
                    ((MainActivity)requireActivity()).fragmentList.set(1,new FragmentExoLiveTv());
                    break;
            }
            MyApp.instance.getPreference().put(Constants.getCurrentPlayer(), current_position);
            MainActivity parent = (MainActivity)requireActivity();
            Bundle bundle = new Bundle();
            bundle.putInt("lastPlayingCategoryPos", lastPlayingCategoryPos);
            bundle.putInt("lastPlayingChannelPos",lastPlayingChannelPos);
            parent.fragmentList.get(1).setArguments(bundle);
            parent.replaceFragment(parent.fragmentList.get(1), 1);
        });
        builder.setNegativeButton("Cancel", null);

        androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showPackageDlg(){
        if (MyApp.fullModels_filter.get(categoryPos).getChannels().get(playChanelPos).is_favorite()) {
            pkg_datas.set(0,"Remove from Fav");
        }else {
            pkg_datas.set(0,"Add to Fav");
        }
        PackageDlg packageDlg = new PackageDlg(requireContext(), pkg_datas, new PackageDlg.DialogPackageListener() {
            @Override
            public void OnItemClick(Dialog dialog, int position) {
                dialog.dismiss();
                switch (position) {
                    case 0:
                        addToFav();
                        break;
                    case 1:
                        MyApp.instance.getPreference().put(Constants.getCATEGORY_POS(),categoryPos);
                        SearchDlg searchDlg = new SearchDlg(requireContext(), new SearchDlg.DialogSearchListener() {
                            @Override
                            public void OnSearchClick(Dialog dialog, EPGChannel sel_Channel) {
                                dialog.dismiss();
                                for(int i = 0; i< MyApp.fullModels_filter.get(categoryPos).getChannels().size(); i++){
                                    if(MyApp.fullModels_filter.get(categoryPos).getChannels().get(i).getName().equalsIgnoreCase(sel_Channel.getName())){
                                        channelPos = i;
                                        playChanelPos = i;
                                        channel_recyclerview.scrollToPosition(channelPos);
                                        channelAdapter.setSelected(playChanelPos);
                                        MyApp.instance.getPreference().put(Constants.getCHANNEL_POS(),channelPos);
                                        mEpgHandler.removeCallbacks(mEpgTicker);
                                        EpgTimer();
//                                        Log.e(TAG,"channel pos: "+channelPos+sel_Channel.getName());
                                        break;
                                    }
                                }
                            }
                        });
                        searchDlg.show();
                        break;
                    case 2:

                        break;
                    case 3:
                        startActivity(new Intent(requireContext(), WebViewActivity.class));
                        break;
                    case 4:
                        showInternalPlayers();
                        break;
                }
            }
        });
        packageDlg.show();
    }


    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releaseMediaPlayer();
        }
        mHandler.removeCallbacks(mUpdateTimeTask);
        mEpgHandler.removeCallbacks(mEpgTicker);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releaseMediaPlayer();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseAdsLoader();
    }

    private void checkAddedRecent(EPGChannel showModel) {
        Iterator<EPGChannel> iter =  Constants.getRecentFullModel(MyApp.fullModels_filter).getChannels().iterator();
        while(iter.hasNext()){
            EPGChannel movieModel = iter.next();
            if (movieModel.getName().equals(showModel.getName()))
                iter.remove();
        }
    }

    private void playVideo() {
        lastPlayingCategoryPos = categoryPos;
        lastPlayingChannelPos = playChanelPos;
        releaseMediaPlayer();
        contentUri = MyApp.instance.getIptvclient().buildLiveStreamURL(MyApp.user, MyApp.pass,
                MyApp.fullModels_filter.get(categoryPos).getChannels().get(playChanelPos).getStream_id()+"","ts");
        Log.e("url",contentUri);
        //add recent series
        EPGChannel epgChannel = MyApp.fullModels_filter.get(categoryPos).getChannels().get(playChanelPos);
        checkAddedRecent(epgChannel);
        Constants.getRecentFullModel(MyApp.fullModels_filter).getChannels().add(0,epgChannel);
        //get recent series names list
        List<String> recent_series_names = new ArrayList<>();
        for (EPGChannel channel: Constants.getRecentFullModel(MyApp.fullModels_filter).getChannels()){
            recent_series_names.add(channel.getName());
        }
        //set
        MyApp.instance.getPreference().put(Constants.getRecentChannels(), recent_series_names);
        Log.e(getClass().getSimpleName(),"added");
        try {
            if (player == null) {
                Intent intent = requireActivity().getIntent();

                Uri[] uris = new Uri[1];
                String[] extensions = new String[1];

                uris[0] = Uri.parse(contentUri);

                DefaultDrmSessionManager<FrameworkMediaCrypto> drmSessionManager = null;
                if (intent.hasExtra(DRM_SCHEME_EXTRA) || intent.hasExtra(DRM_SCHEME_UUID_EXTRA)) {
                    String drmLicenseUrl = intent.getStringExtra(DRM_LICENSE_URL_EXTRA);
                    String[] keyRequestPropertiesArray =
                            intent.getStringArrayExtra(DRM_KEY_REQUEST_PROPERTIES_EXTRA);
                    boolean multiSession = intent.getBooleanExtra(DRM_MULTI_SESSION_EXTRA, false);
                    int errorStringId = R.string.error_drm_unknown;
                    if (Util.SDK_INT < 18) {
                        errorStringId = R.string.error_drm_not_supported;
                    } else {
                        try {
                            String drmSchemeExtra = intent.hasExtra(DRM_SCHEME_EXTRA) ? DRM_SCHEME_EXTRA
                                    : DRM_SCHEME_UUID_EXTRA;
                            UUID drmSchemeUuid = Util.getDrmUuid(intent.getStringExtra(drmSchemeExtra));
                            if (drmSchemeUuid == null) {
                                errorStringId = R.string.error_drm_unsupported_scheme;
                            } else {
                                drmSessionManager =
                                        buildDrmSessionManagerV18(
                                                drmSchemeUuid, drmLicenseUrl, keyRequestPropertiesArray, multiSession);
                            }
                        } catch (UnsupportedDrmException e) {
                            errorStringId = e.reason == UnsupportedDrmException.REASON_UNSUPPORTED_SCHEME
                                    ? R.string.error_drm_unsupported_scheme : R.string.error_drm_unknown;
                        }
                    }
                    if (drmSessionManager == null) {
                        showToast(errorStringId);
                        releaseMediaPlayer();
                        return;
                    }
                }

                TrackSelection.Factory trackSelectionFactory;
                String abrAlgorithm = intent.getStringExtra(ABR_ALGORITHM_DEFAULT);
                if (abrAlgorithm == null || ABR_ALGORITHM_DEFAULT.equals(abrAlgorithm)) {
                    trackSelectionFactory = new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
                } else if (ABR_ALGORITHM_RANDOM.equals(abrAlgorithm)) {
                    trackSelectionFactory = new RandomTrackSelection.Factory();
                } else {
                    showToast(R.string.error_unrecognized_abr_algorithm);
                    releaseMediaPlayer();
                    return;
                }

                boolean preferExtensionDecoders =
                        intent.getBooleanExtra(PREFER_EXTENSION_DECODERS_EXTRA, false);
                @DefaultRenderersFactory.ExtensionRendererMode int extensionRendererMode =
                        MyApp.instance.useExtensionRenderers()
                                ? (preferExtensionDecoders ? DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER
                                : DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON)
                                : DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF;
                DefaultRenderersFactory renderersFactory =
                        new DefaultRenderersFactory(requireContext(), extensionRendererMode);

                trackSelector = new DefaultTrackSelector(trackSelectionFactory);
                trackSelector.setParameters(trackSelectorParameters);
                lastSeenTrackGroupArray = null;

                player = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector, drmSessionManager);
                player.addListener(new PlayerEventListener());
                player.setPlayWhenReady(startAutoPlay);
                player.addAnalyticsListener(new EventLogger(trackSelector));
                playerView.setPlayer(player);
                playerView.setPlaybackPreparer(this);

                debugViewHelper = new DebugTextViewHelper(player, debugTextView);
                debugViewHelper.start();

                MediaSource[] mediaSources = new MediaSource[uris.length];
                for (int i = 0; i < uris.length; i++) {
                    mediaSources[i] = buildMediaSource(uris[i], extensions[i]);
                }
                mediaSource =
                        mediaSources.length == 1 ? mediaSources[0] : new ConcatenatingMediaSource(mediaSources);
                String adTagUriString = intent.getStringExtra(AD_TAG_URI_EXTRA);
                if (adTagUriString != null) {
                    Uri adTagUri = Uri.parse(adTagUriString);
                    if (!adTagUri.equals(loadedAdTagUri)) {
                        releaseAdsLoader();
                        loadedAdTagUri = adTagUri;
                    }
                    MediaSource adsMediaSource = createAdsMediaSource(mediaSource, Uri.parse(adTagUriString));
                    if (adsMediaSource != null) {
                        mediaSource = adsMediaSource;
                    } else {
                        showToast(R.string.ima_not_loaded);
                    }
                } else {
                    releaseAdsLoader();
                }
            }
            boolean haveStartPosition = startWindow != C.INDEX_UNSET;
            if (haveStartPosition) {
                player.seekTo(startWindow, startPosition);
            }
            player.prepare(mediaSource, !haveStartPosition, false);

            categoryAdapter.setSelected(categoryPos);
            category_recyclerview.scrollToPosition(categoryPos);
            channel_recyclerview.scrollToPosition(playChanelPos);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error in creating player!", Toast
                    .LENGTH_LONG).show();
        }
    }

    private void getEpg(){
        try {
            String response = MyApp.instance.getIptvclient().getShortEPG(MyApp.user, MyApp.pass,
                    MyApp.fullModels_filter.get(categoryPos).getChannels().get(channelPos).getStream_id()+"",4);
            Log.e(getClass().getSimpleName(),response);
            Gson gson=new Gson();
            response=response.replaceAll("[^\\x00-\\x7F]", "");
            if (!response.contains("null_error_response")){
                Log.e("response",response);
                try {
                    JSONObject jsonObject= new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("epg_listings");
                    epgModelList = new ArrayList<>();
                    epgModelList.addAll(gson.fromJson(jsonArray.toString(), new TypeToken<List<EPGEvent>>(){}.getType()));
                    requireActivity().runOnUiThread(()->liveTvProgramsAdapter.setEpgModels(epgModelList));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean myOnKeyDown(KeyEvent event){
        //do whatever you want here
        AudioManager audioManager = (AudioManager) requireActivity().getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        View view = requireActivity().getCurrentFocus();
        if (event.getAction() == KeyEvent.ACTION_UP){
            switch (event.getKeyCode()){
                case KeyEvent.KEYCODE_MENU:
//                    toggleFullScreen();
                        showPackageDlg();
                    break;
                case KeyEvent.KEYCODE_BACK:
                    if (is_full) {
                        toggleFullScreen();
                        return false;
                    }else {
                        releaseMediaPlayer();
                        ((MainActivity)requireActivity()).toggleFullScreen(false);
                        requireActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container,((MainActivity)requireActivity()).fragmentList.get(0))//FragmentCatchupDetail
                                .addToBackStack(null).commit();
                        return false;
                    }
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if (!is_full){
//                        if (is_focus_in_channel_recyclerview)
//                            addFav.requestFocus();
                    }else {
                        if (audioManager != null) {
                            audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if (is_full){
                        if (audioManager != null) {
                            audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if (is_full && MyApp.fullModels_filter.get(categoryPos).getChannels().size()-1>playChanelPos) {
                        playChannel(MyApp.fullModels_filter.get(categoryPos).getChannels().get(playChanelPos+1),playChanelPos+1);
                        showInfoBar();
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_UP:
                    if (is_full && 0<playChanelPos) {
                        playChannel(MyApp.fullModels_filter.get(categoryPos).getChannels().get(playChanelPos-1),playChanelPos-1);
                        showInfoBar();
                    }
                    break;
            }
        }
        return super.myOnKeyDown(event);
    }

    private void showInfoBar() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateProgressBar();
                ly_bottom.setVisibility(View.VISIBLE);
                listTimer();
            }
        }, 100);
        getRespond();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.player_view:
//                showMenu();
                toggleFullScreen();
                playerView.requestFocus();
                break;
        }
    }

    //ExoPlayer

    private DataSource.Factory buildDataSourceFactory(boolean useBandwidthMeter) {
        return MyApp.instance.buildDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
    }

    @Override
    public void preparePlayback() {

    }

    @Override
    public void onVisibilityChange(int visibility) {

    }

    private class PlayerErrorMessageProvider implements ErrorMessageProvider<ExoPlaybackException> {

        @Override
        public Pair<Integer, String> getErrorMessage(ExoPlaybackException e) {
            String errorString = getString(R.string.error_generic);
            if (e.type == ExoPlaybackException.TYPE_RENDERER) {
                Exception cause = e.getRendererException();
                if (cause instanceof MediaCodecRenderer.DecoderInitializationException) {
                    MediaCodecRenderer.DecoderInitializationException decoderInitializationException =
                            (MediaCodecRenderer.DecoderInitializationException) cause;
                    if (decoderInitializationException.decoderName == null) {
                        if (decoderInitializationException.getCause() instanceof MediaCodecUtil.DecoderQueryException) {
                            errorString = getString(R.string.error_querying_decoders);
                        } else if (decoderInitializationException.secureDecoderRequired) {
                            errorString =
                                    getString(
                                            R.string.error_no_secure_decoder, decoderInitializationException.mimeType);
                        } else {
                            errorString =
                                    getString(R.string.error_no_decoder, decoderInitializationException.mimeType);
                        }
                    } else {
                        errorString =
                                getString(
                                        R.string.error_instantiating_decoder,
                                        decoderInitializationException.decoderName);
                    }
                }
            }
            return Pair.create(0, errorString);
        }
    }

    private void changeVideoViewSize(int witdth,int height){
        WindowManager wm = (WindowManager) requireActivity().getSystemService(WINDOW_SERVICE);
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        if (wm != null) {
            wm.getDefaultDisplay().getMetrics(displayMetrics);
            ViewGroup.LayoutParams paramsPlayerView=frameLayout.getLayoutParams();
            paramsPlayerView.height = height;
            paramsPlayerView.width = witdth;
            frameLayout.setLayoutParams(paramsPlayerView);
        }
    }

    private void clearStartPosition() {
        startAutoPlay = true;
        startWindow = C.INDEX_UNSET;
        startPosition = C.TIME_UNSET;
    }

    private void releaseAdsLoader() {
        if (adsLoader != null) {
            adsLoader.release();
            adsLoader = null;
            loadedAdTagUri = null;
            playerView.getOverlayFrameLayout().removeAllViews();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (grantResults.length == 0) {
            return;
        }
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            initializePlayer();
        } else {
            showToast(R.string.storage_permission_denied);
            releaseMediaPlayer();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        updateTrackSelectorParameters();
        updateStartPosition();
        outState.putParcelable(KEY_TRACK_SELECTOR_PARAMETERS, trackSelectorParameters);
        outState.putBoolean(KEY_AUTO_PLAY, startAutoPlay);
        outState.putInt(KEY_WINDOW, startWindow);
        outState.putLong(KEY_POSITION, startPosition);

        super.onSaveInstanceState(outState);
    }

    private void updateTrackSelectorParameters() {
        if (trackSelector != null) {
            trackSelectorParameters = trackSelector.getParameters();
        }
    }

    private void updateStartPosition() {
        if (player != null) {
            startAutoPlay = player.getPlayWhenReady();
            startWindow = player.getCurrentWindowIndex();
            startPosition = Math.max(0, player.getContentPosition());
        }
    }

    private void showToast(int messageId) {
        showToast(getString(messageId));
    }
    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
    }

    private void releaseMediaDrm() {
        if (mediaDrm != null) {
            mediaDrm.release();
            mediaDrm = null;
        }
    }

    private List<?> getOfflineStreamKeys(Uri uri) {
        return MyApp.instance.getDownloadTracker().getOfflineStreamKeys(uri);
    }

    private DefaultDrmSessionManager<FrameworkMediaCrypto> buildDrmSessionManagerV18(
            UUID uuid, String licenseUrl, String[] keyRequestPropertiesArray, boolean multiSession)
            throws UnsupportedDrmException {
        HttpDataSource.Factory licenseDataSourceFactory =
                MyApp.instance.buildHttpDataSourceFactory(/* listener= */ null);
        HttpMediaDrmCallback drmCallback =
                new HttpMediaDrmCallback(licenseUrl, licenseDataSourceFactory);
        if (keyRequestPropertiesArray != null) {
            for (int i = 0; i < keyRequestPropertiesArray.length - 1; i += 2) {
                drmCallback.setKeyRequestProperty(keyRequestPropertiesArray[i],
                        keyRequestPropertiesArray[i + 1]);
            }
        }
        releaseMediaDrm();
        mediaDrm = FrameworkMediaDrm.newInstance(uuid);
        return new DefaultDrmSessionManager<>(uuid, mediaDrm, drmCallback, null, multiSession);
    }

    private class PlayerEventListener extends Player.DefaultEventListener {

        private  final String LOG_TAG =  PlayerEventListener.class.getSimpleName();

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            super.onPlaybackParametersChanged(playbackParameters);
            Log.d(LOG_TAG,"onPlaybackParametersChanged");
        }

        @Override
        public void onSeekProcessed() {
            super.onSeekProcessed();
            Log.d(LOG_TAG,"onSeekProcessed");
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            if (playbackState == Player.STATE_ENDED) {
                showControls();
            }
        }

        @Override
        public void onPositionDiscontinuity(@Player.DiscontinuityReason int reason) {
            if (player.getPlaybackError() != null) {
                updateStartPosition();
            }
        }

        @Override
        public void onPlayerError(ExoPlaybackException e) {
            if (isBehindLiveWindow(e)) {
                clearStartPosition();
                playVideo();
            } else {
                updateStartPosition();
                showControls();
            }
        }

        @Override
        @SuppressWarnings("ReferenceEquality")
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            if (trackGroups != lastSeenTrackGroupArray) {
                MappingTrackSelector.MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
                if (mappedTrackInfo != null) {
                    if (mappedTrackInfo.getTypeSupport(C.TRACK_TYPE_VIDEO)
                            == MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS) {
                        showToast(R.string.error_unsupported_video);
                    }
                    if (mappedTrackInfo.getTypeSupport(C.TRACK_TYPE_AUDIO)
                            == MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS) {
                        showToast(R.string.error_unsupported_audio);
                    }
                }
                lastSeenTrackGroupArray = trackGroups;
            }
        }
    }

    private void showControls() {
        if (is_full)
            ly_bottom.setVisibility(View.VISIBLE);
    }

    private static boolean isBehindLiveWindow(ExoPlaybackException e) {
        if (e.type != ExoPlaybackException.TYPE_SOURCE) {
            return false;
        }
        Throwable cause = e.getSourceException();
        while (cause != null) {
            if (cause instanceof BehindLiveWindowException) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }
    private MediaSource buildMediaSource(Uri uri) {
        return buildMediaSource(uri, null);
    }
    private MediaSource buildMediaSource(Uri uri, @Nullable String overrideExtension) {
        @C.ContentType int type = Util.inferContentType(uri, overrideExtension);
        switch (type) {
            case C.TYPE_DASH:
                return new DashMediaSource.Factory(
                        new DefaultDashChunkSource.Factory(mediaDataSourceFactory),
                        buildDataSourceFactory(false))
                        .setManifestParser(
                                new FilteringManifestParser<>(
                                        new DashManifestParser(), (List<RepresentationKey>) getOfflineStreamKeys(uri)))
                        .createMediaSource(uri);
            case C.TYPE_SS:
                return new SsMediaSource.Factory(
                        new DefaultSsChunkSource.Factory(mediaDataSourceFactory),
                        buildDataSourceFactory(false))
                        .setManifestParser(
                                new FilteringManifestParser<>(
                                        new SsManifestParser(), (List<StreamKey>) getOfflineStreamKeys(uri)))
                        .createMediaSource(uri);
            case C.TYPE_HLS:
                return new HlsMediaSource.Factory(mediaDataSourceFactory)
                        .setPlaylistParser(
                                new FilteringManifestParser<>(
                                        new HlsPlaylistParser(), (List<RenditionKey>) getOfflineStreamKeys(uri)))
                        .createMediaSource(uri);
            case C.TYPE_OTHER:
                return new ExtractorMediaSource.Factory(mediaDataSourceFactory).createMediaSource(uri);
            default: {
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }
    }

    private @Nullable
    MediaSource createAdsMediaSource(MediaSource mediaSource, Uri adTagUri) {
        try {
            Class<?> loaderClass = Class.forName("com.google.android.exoplayer2.ext.ima.ImaAdsLoader");
            if (adsLoader == null) {
                Constructor<? extends AdsLoader> loaderConstructor =
                        loaderClass
                                .asSubclass(AdsLoader.class)
                                .getConstructor(Context.class, Uri.class);
                adsLoader = loaderConstructor.newInstance(this, adTagUri);
                adUiViewGroup = new FrameLayout(requireContext());
                playerView.getOverlayFrameLayout().addView(adUiViewGroup);
            }
            AdsMediaSource.MediaSourceFactory adMediaSourceFactory =
                    new AdsMediaSource.MediaSourceFactory() {
                        @Override
                        public MediaSource createMediaSource(Uri uri) {
                            return FragmentExoLiveTv.this.buildMediaSource(uri);
                        }

                        @Override
                        public int[] getSupportedTypes() {
                            return new int[] {C.TYPE_DASH, C.TYPE_SS, C.TYPE_HLS, C.TYPE_OTHER};
                        }
                    };
            return new AdsMediaSource(mediaSource, adMediaSourceFactory, adsLoader, adUiViewGroup);
        } catch (ClassNotFoundException e) {
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void showAudioOptions(){
        MappingTrackSelector.MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
        if (mappedTrackInfo != null) {
            CharSequence title = "Audio";
            int rendererIndex = 1;
            /*int rendererType = mappedTrackInfo.getRendererType(rendererIndex);
            boolean allowAdaptiveSelections =
                    rendererType == C.TRACK_TYPE_VIDEO
                            || (rendererType == C.TRACK_TYPE_AUDIO
                            && mappedTrackInfo.getTypeSupport(C.TRACK_TYPE_VIDEO)
                            == MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_NO_TRACKS);*/
            Pair<AlertDialog, TrackSelectionView> dialogPair =
                    TrackSelectionView.getDialog(requireActivity(), title, trackSelector, rendererIndex);
            dialogPair.second.setShowDisableOption(false);
            dialogPair.second.setAllowAdaptiveSelections(false);
            dialogPair.first.show();
        }
    }

    private void showSubtitlesOptions(){
        MappingTrackSelector.MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
        if (mappedTrackInfo != null) {
            CharSequence title = "Subtitles";
            int rendererIndex = 2;
            /*int rendererType = mappedTrackInfo.getRendererType(rendererIndex);
            boolean allowAdaptiveSelections =
                    rendererType == C.TRACK_TYPE_VIDEO
                            || (rendererType == C.TRACK_TYPE_AUDIO
                            && mappedTrackInfo.getTypeSupport(C.TRACK_TYPE_VIDEO)
                            == MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_NO_TRACKS);*/
            Pair<AlertDialog, TrackSelectionView> dialogPair =
                    TrackSelectionView.getDialog(requireActivity(), title, trackSelector, rendererIndex);
            dialogPair.second.setShowDisableOption(true);
            dialogPair.second.setAllowAdaptiveSelections(false);
            dialogPair.second.removeViewAt(4);
            dialogPair.first.show();
        }
    }

}
