package com.it_tech613.skydark.ui.series;

import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.it_tech613.skydark.R;
import com.it_tech613.skydark.apps.Constants;
import com.it_tech613.skydark.apps.MyApp;
import com.it_tech613.skydark.models.*;
import com.it_tech613.skydark.ui.VideoExoPlayActivity;
import com.it_tech613.skydark.ui.VideoIjkPlayActivity;
import com.it_tech613.skydark.ui.VideoPlayActivity;
import com.it_tech613.skydark.utils.MyFragment;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FragmentSeasons extends MyFragment{

    private TextView title, subTitle, plot, cast, director, genre,score;
    private SeasonListAdapter seasonListAdapter;
    private Button addFav;
    private ImageView fav_icon;
    private Disposable bookSubscription;
    @NotNull
    public FragmentSeriesHolder parent;
    private RecyclerView seriesRecyclerView;
    private EpisodeListAdapter episodeListAdapter;
    private String TAG=getClass().getSimpleName();
    @Override
    public void onStop() {
        super.onStop();
        if (bookSubscription!=null && !bookSubscription.isDisposed()) {
            bookSubscription.dispose();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bookSubscription != null && !bookSubscription.isDisposed()) {
            bookSubscription.dispose();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_seasons, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        title = view.findViewById(R.id.textView9);
        subTitle = view.findViewById(R.id.textView10);
        plot = view.findViewById(R.id.textView11);
        cast = view.findViewById(R.id.textView16);
        director = view.findViewById(R.id.textView18);
        genre = view.findViewById(R.id.textView20);
        score = view.findViewById(R.id.textView21);
//        RecyclerView categoryRecyclerView = view.findViewById(R.id.movies_recyclerview);
//        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
//        categoryRecyclerView.setAdapter(new CategoryAdapter(MyApp.series_categories_filter, new Function2<CategoryModel,Integer, Unit>() {
//            @Override
//            public Unit invoke(CategoryModel categoryModel, Integer position) {
//                startGetSeries(categoryModel.getId());
//                return null;
//            }
//        }));
        title.setText(MyApp.selectedSeriesModel.getName());
        subTitle.setText(MyApp.selectedSeriesModel.getReleaseDate());
        plot.setText(MyApp.selectedSeriesModel.getPlot());
        cast.setText(MyApp.selectedSeriesModel.getCast());
        director.setText(MyApp.selectedSeriesModel.getDirector());
        genre.setText(MyApp.selectedSeriesModel.getGenre());
        score.setText(MyApp.selectedSeriesModel.getRating()+"");
        final List<String> slideModels = getSlides(MyApp.selectedSeriesModel.getBackdrop_path());
        SliderLayout mDemoSlider = view.findViewById(R.id.slider_viewpager);
        for(String url : slideModels){
            DefaultSliderView textSliderView = new DefaultSliderView (requireContext());
            // initialize a SliderLayout
            textSliderView
                    //                    .description(name)
                    .image(url)
                    .setScaleType(BaseSliderView.ScaleType.FitCenterCrop);

            //add your extra information
            //            textSliderView.bundle(new Bundle());
            //            textSliderView.getBundle()
            //                    .putString("extra",name);

            mDemoSlider.addSlider(textSliderView);
        }
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        //        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(slideModels.size()*Constants.GetSlideTime(requireContext()));

        addFav = (Button)view.findViewById(R.id.button4);
        fav_icon = (ImageView)view.findViewById(R.id.fav_icon);
        addFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToFav();
            }
        });
        setAddFavText();
        seriesRecyclerView = view.findViewById(R.id.video_list_recyclerview);
        seriesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(),RecyclerView.HORIZONTAL,false));
        seasonListAdapter =new SeasonListAdapter(new ArrayList<>(), true, (integer, seasonModel) -> {
            //onclicklistener
            if (seasonModel.getEpisodeModels()==null || seasonModel.getEpisodeModels().isEmpty()) return null;
            MyApp.selectedSeasonModel=seasonModel;
            episodeListAdapter.setEpisodeModels(seasonModel.getEpisodeModels());
//            parent.replaceFragment(((MainActivity)requireActivity()).fragmentList.get(((MainActivity)requireActivity()).fragmentList.size()-2));
            return null;
        }, (integer, seasonModel) -> {
            //onFocusListener
//            title.setText(seasonModel.getName());
            seriesRecyclerView.scrollToPosition(integer);
            return null;
        });
        seriesRecyclerView.setAdapter(seasonListAdapter);
        RecyclerView episodeRecyclerView = view.findViewById(R.id.episode_list_recyclerview);
        episodeListAdapter = new EpisodeListAdapter(new ArrayList<>(), new Function2<Integer, EpisodeModel, Unit>() {
            @Override
            public Unit invoke(Integer integer, EpisodeModel episodeModel) {
                //add recent series
                checkAddedRecent(MyApp.selectedSeriesModel);
                Constants.getRecentCatetory(MyApp.series_categories).getSeriesModels().add(0,MyApp.selectedSeriesModel);
                //get recent series names list
                List<String> recent_series_names = new ArrayList<>();
                for (SeriesModel seriesModel:Constants.getRecentCatetory(MyApp.series_categories).getSeriesModels()){
                    recent_series_names.add(seriesModel.getName());
                }
                //set
                MyApp.instance.getPreference().put(Constants.getRecentSeries(), recent_series_names);
                Log.e(getClass().getSimpleName(),"added");

                //onclicklistener
                String episode_url = MyApp.instance.getIptvclient().buildSeriesStreamURL(MyApp.user,MyApp.pass,episodeModel.getStream_id(),episodeModel.getContainer_extension());
                Log.e(getClass().getSimpleName(),episode_url);
                int current_player = (int) MyApp.instance.getPreference().get(Constants.getCurrentPlayer());
                Intent intent;
                switch (current_player){
                    case 0:
                        intent = new Intent(requireContext(), VideoPlayActivity.class);
                        break;
                    case 1:
                        intent = new Intent(requireContext(), VideoIjkPlayActivity.class);
                        break;
                    case 2:
                        intent = new Intent(requireContext(), VideoExoPlayActivity.class);
                        break;
                    default:
                        intent = new Intent(requireContext(), VideoPlayActivity.class);
                        break;
                }
                intent.putExtra("title",episodeModel.getTitle());
                intent.putExtra("img",episodeModel.getEpisodeInfoModel().getMovie_image());
                intent.putExtra("url",episode_url);
                startActivity(intent);
                return null;
            }
        }, (integer, episodeInfoModel) -> {
            //onFocusListener
//            if (episodeInfoModel.getEpisodeInfoModel().getMovie_image()!=null && !episodeInfoModel.getEpisodeInfoModel().getMovie_image().equals("")) image.setImageURI(Uri.parse(episodeInfoModel.getEpisodeInfoModel().getMovie_image()));
//            else image.setImageResource(R.drawable.icon);
//            desc.setText(episodeInfoModel.getTitle());
            episodeRecyclerView.scrollToPosition(integer);
            return null;
        });
        if (MyApp.selectedSeriesModel.getSeasonModels()==null || MyApp.selectedSeriesModel.getSeasonModels().size()==0) {
            Observable<List<SeasonModel>> booksObservable =
                    Observable.fromCallable(this::startGetSeries);
            bookSubscription = booksObservable.
                    subscribeOn(Schedulers.io()).
                    observeOn(AndroidSchedulers.mainThread()).
                    subscribe(seasonModels -> {
                        MyApp.selectedSeriesModel.setSeasonModels(seasonModels);
                        seasonListAdapter.setSeasonModels(seasonModels);
                        if(MyApp.selectedSeriesModel.getSeasonModels()==null || MyApp.selectedSeriesModel.getSeasonModels().size()==0 || MyApp.selectedSeriesModel.getSeasonModels().get(0).getEpisodeModels() == null || MyApp.selectedSeriesModel.getSeasonModels().get(0).getEpisodeModels().size()==0){
                        }else {
                            episodeListAdapter.setEpisodeModels(MyApp.selectedSeriesModel.getSeasonModels().get(0).getEpisodeModels());
                        }
                    });
        }
        else {
            seasonListAdapter.setSeasonModels(MyApp.selectedSeriesModel.getSeasonModels());
            episodeListAdapter.setEpisodeModels(MyApp.selectedSeriesModel.getSeasonModels().get(0).getEpisodeModels());
        }
        episodeRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        //onclicklistener
        //onFocusListener
        episodeRecyclerView.setAdapter(episodeListAdapter);
    }

    private void checkAddedRecent(SeriesModel showModel) {
        Iterator<SeriesModel> iter = Constants.getRecentCatetory(MyApp.series_categories).getSeriesModels().iterator();
        while(iter.hasNext()){
            SeriesModel seriesModel = iter.next();
            if (seriesModel.getName().equals(showModel.getName()))
                iter.remove();
        }
    }

    private void setAddFavText(){
        if (MyApp.selectedSeriesModel.isIs_favorite()) {
            addFav.setText(getResources().getString(R.string.remove_favorites));
            fav_icon.setImageResource(R.drawable.heart_filled);
        }
        else {
            addFav.setText(getResources().getString(R.string.add_to_favorite));
            fav_icon.setImageResource(R.drawable.heart_unfilled);
        }
    }
    private void addToFav() {
        Log.e("OnAddFavClick","received");
        if (MyApp.selectedSeriesModel.isIs_favorite()) {
            MyApp.selectedSeriesModel.setIs_favorite(false);
            boolean is_exist = false;
            int pp = 0;
            for (int i = 0; i < MyApp.favSeriesModels.size(); i++) {
                if (MyApp.favSeriesModels.get(i).getName().equals(MyApp.selectedSeriesModel.getName())) {
                    is_exist = true;
                    pp = i;
                }
            }
            if (is_exist)
                MyApp.favSeriesModels.remove(pp);
            //get favorite channel names list
            List<String> fav_series_names=new ArrayList<>();
            for (SeriesModel seriesModel:MyApp.favSeriesModels){
                fav_series_names.add(seriesModel.getName());
            }
            //set
            MyApp.instance.getPreference().put(Constants.getFAV_SERIES(), fav_series_names);
            Log.e("SERIES_FAV","removed");
        } else {
            MyApp.selectedSeriesModel.setIs_favorite(true);
            //get favorite channel names list
            MyApp.favSeriesModels.add(MyApp.selectedSeriesModel);
            List<String> fav_series_names=new ArrayList<>();
            for (SeriesModel seriesModel:MyApp.favSeriesModels){
                fav_series_names.add(seriesModel.getName());
            }
            //set
            MyApp.instance.getPreference().put(Constants.getFAV_SERIES(), fav_series_names);
            Log.e("SERIES_FAV","added");
        }
        Constants.getFavoriteCatetory(MyApp.series_categories).setSeriesModels(MyApp.favSeriesModels);
        setAddFavText();
    }

    private List<SeasonModel> startGetSeries(){
        try {
            String requestBody = MyApp.instance.getIptvclient().getSeriesInfo(MyApp.user,MyApp.pass,MyApp.selectedSeriesModel.getSeries_id());
            Log.e(getClass().getSimpleName(),requestBody);
            JSONObject jsonObject = new JSONObject(requestBody);
            Gson gson=new Gson();
            try {
                JSONArray seasons=jsonObject.getJSONArray("seasons");
                List<SeasonModel> seasonModels = new ArrayList<>(gson.fromJson(seasons.toString(), new TypeToken<List<SeasonModel>>() {}.getType()));
//                JSONObject info= map.getJSONObject("info");
//                SeriesModel seriesModel = gson.fromJson(info.toString(),SeriesModel.class);
//                MyApp.selectedSeriesModel.setBackdrop_path(seriesModel.getBackdrop_path());
                if (seasonModels.size()>0){
                    try {
                        JSONObject episodes=jsonObject.getJSONObject("episodes");
                        Log.e(TAG,episodes.toString());
                        for (SeasonModel seasonModel:seasonModels){
                            try {
                                JSONArray i_episodes=episodes.getJSONArray(String.valueOf(seasonModel.getSeason_number()));
                                List<EpisodeModel> episodeModels=new ArrayList<>();
                                for (int i=0;i<i_episodes.length();i++){
                                    try {
                                        JSONObject object_episode = i_episodes.getJSONObject(i);
                                        EpisodeModel episodeModel = gson.fromJson(object_episode.toString(),EpisodeModel.class);
                                        try {
                                            JSONObject info_object= object_episode.getJSONObject("info");
                                            EpisodeInfoModel episodeInfoModel = gson.fromJson(info_object.toString(),EpisodeInfoModel.class);
                                            episodeModel.setEpisodeInfoModel(episodeInfoModel);
                                            episodeModels.add(episodeModel);
                                        }catch (JSONException ignored){
                                            Log.e(TAG,"There is an error in getting info model " + seasonModel.getSeason_number());
                                        }
                                    }catch (JSONException ignored){
                                        Log.e(TAG,"There is an error in getting episode model " + seasonModel.getSeason_number());
                                    }
                                }
//                        episodeModels.addAll((Collection<? extends EpisodeModel>) gson.fromJson(i_episodes.toString(), new TypeToken<List<EpisodeModel>>(){}.getType()));
                                seasonModel.setEpisodeModels(episodeModels);
                            }catch (JSONException ignored){
                                Log.e(TAG,"There is no episodes in " + seasonModel.getSeason_number());
                            }
                        }
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    try {
                        JSONObject episodes=jsonObject.getJSONObject("episodes");
                        Log.e(TAG,episodes.toString());
                        Iterator<String> keys = episodes.keys();
                        while (keys.hasNext()){
                            String key = keys.next();
                            SeasonModel seasonModel = new SeasonModel(Integer.valueOf(key),"Season "+key);
                            try {
                                JSONArray i_episodes=episodes.getJSONArray(key);
                                List<EpisodeModel> episodeModels=new ArrayList<>();
                                for (int i=0;i<i_episodes.length();i++){
                                    try {
                                        JSONObject object_episode = i_episodes.getJSONObject(i);
                                        EpisodeModel episodeModel = gson.fromJson(object_episode.toString(),EpisodeModel.class);
                                        try {
                                            JSONObject info_object= object_episode.getJSONObject("info");
                                            EpisodeInfoModel episodeInfoModel = gson.fromJson(info_object.toString(),EpisodeInfoModel.class);
                                            episodeModel.setEpisodeInfoModel(episodeInfoModel);
                                            episodeModels.add(episodeModel);
                                        }catch (JSONException ignored){
                                            Log.e(TAG,"There is an error in getting info model " + seasonModel.getSeason_number());
                                        }
                                    }catch (JSONException ignored){
                                        Log.e(TAG,"There is an error in getting episode model " + seasonModel.getSeason_number());
                                    }
                                }
//                        episodeModels.addAll((Collection<? extends EpisodeModel>) gson.fromJson(i_episodes.toString(), new TypeToken<List<EpisodeModel>>(){}.getType()));
                                seasonModel.setEpisodeModels(episodeModels);
                            }catch (JSONException ignored){
                                Log.e(TAG,"There is no episodes in " + seasonModel.getSeason_number());
                            }
                            seasonModels.add(seasonModel);
                        }
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                Log.e(getClass().getSimpleName(),seasonModels.size()+"");
                return seasonModels;
            } catch (JSONException e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public boolean myOnKeyDown(KeyEvent event){
        //do whatever you want here
        View view = requireActivity().getCurrentFocus();
        switch (event.getKeyCode()){
            case KeyEvent.KEYCODE_DPAD_UP:
                if (view.getId()==R.id.button4)
                    seriesRecyclerView.performClick();
                break;
        }
        return super.myOnKeyDown(event);
    }

    private List<String> getSlides(List<String> strings) {
        return new ArrayList<>(strings);
    }
}
