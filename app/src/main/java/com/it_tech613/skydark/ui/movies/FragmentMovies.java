package com.it_tech613.skydark.ui.movies;


import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.it_tech613.skydark.R;
import com.it_tech613.skydark.apps.Constants;
import com.it_tech613.skydark.apps.MyApp;
import com.it_tech613.skydark.models.MovieInfoModel;
import com.it_tech613.skydark.models.MovieModel;
import com.it_tech613.skydark.ui.MainActivity;
import com.it_tech613.skydark.utils.MyFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class FragmentMovies extends MyFragment {

    private RecyclerView category_recyclerview;
    private MovieCategoryAdapter movieCategoryAdapter;
    private boolean isOnSearch=false;
    private SliderLayout mDemoSlider;
    private TextView title, subTitle, body;
    private RatingBar ratingBar;
    private MovieModel showMovieModel;
    boolean isInApiCalling = false;
    Handler handler = new Handler();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_movies, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        category_recyclerview = view.findViewById(R.id.category_recyclerview);
        final TextInputEditText textInputLayout = view.findViewById(R.id.editText);
        textInputLayout.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE){
                Log.e("ActionDone","clicked");
                if (!isOnSearch)
                    movieCategoryAdapter.search(textInputLayout.getText().toString());
            }
            return false;
        });
        Button submit = view.findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnSearch) return;
                movieCategoryAdapter.search(textInputLayout.getText().toString());
            }
        });
        title = view.findViewById(R.id.textView9);
        subTitle = view.findViewById(R.id.textView10);
        body = view.findViewById(R.id.textView11);
        mDemoSlider = view.findViewById(R.id.slider_viewpager);
        ratingBar = view.findViewById(R.id.vod_detail_ratingbar);
        category_recyclerview.setLayoutManager(new LinearLayoutManager(requireContext()));
        setRecyclerView();
    }

    private void setRecyclerView() {
        movieCategoryAdapter = new MovieCategoryAdapter(
                requireContext(),
                (position, homeCategory, movieModels, b) -> {
                    if (movieModels.size()==0) return null;
                    if (b){
                        //if clicked
                        MyApp.subMovieModels = movieModels;
                        if(position==0){
                            requireActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.container,((MainActivity)requireActivity()).fragmentList.get(((MainActivity)requireActivity()).fragmentList.size()-4))//FragmentAllMovie
                                    .addToBackStack(null).commit();
                        }else{
                            requireActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.container,((MainActivity)requireActivity()).fragmentList.get(((MainActivity)requireActivity()).fragmentList.size()-3))//FragmentMovieDetail
                                    .addToBackStack(null).commit();
                        }
                    }else {
                        //if focused
                        showMovieModel = movieModels.get(0);
                        if (showMovieModel.getMovieInfoModel()==null){
                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    //Do something after 100ms
                                    new Thread(FragmentMovies.this::getMovieInfo).start();
                                }
                            };
                            handler.removeCallbacksAndMessages(null);
                            handler.postDelayed(runnable, 600);
                        }
                        else setModelInfo();
                    }

                    return null;
                }
        ){
            @Override
            public void setBlocked(boolean isBlocked) {
                isOnSearch = isBlocked;
            }
        };
        movieCategoryAdapter.setList(MyApp.vod_categories_filter,true);
        category_recyclerview.setAdapter(movieCategoryAdapter);
    }

    private void getMovieInfo(){
        try {
            if (isInApiCalling) return;
            isInApiCalling = true;
            String response = MyApp.instance.getIptvclient().getVodInfo(MyApp.user,MyApp.pass, showMovieModel.getStream_id());
            Log.e(getClass().getSimpleName(),response);
            JSONObject jsonObject = new JSONObject(response);
            MovieInfoModel movieInfoModel = new MovieInfoModel();
            try{
                JSONObject info_obj = jsonObject.getJSONObject("info");
                Gson gson = new Gson();
                try {
                    movieInfoModel = gson.fromJson(info_obj.toString(),MovieInfoModel.class);
                }catch (Exception e){
                    e.printStackTrace();
                    movieInfoModel.setMovie_img(info_obj.getString("movie_image"));
                    movieInfoModel.setGenre(info_obj.getString("genre"));
                    movieInfoModel.setPlot(info_obj.getString("plot"));
                    movieInfoModel.setCast(info_obj.getString("cast"));
                    try {
                        movieInfoModel.setRating(info_obj.getDouble("rating"));
                    }catch (Exception e1){
                        movieInfoModel.setRating(0.0);
                    }
                    try {
                        movieInfoModel.setYoutube(info_obj.getString("youtube_trailer"));
                    }catch (Exception e1){
                        e1.printStackTrace();
                    }
                    movieInfoModel.setDirector(info_obj.getString("director"));
                    movieInfoModel.setDuration(info_obj.getString("duration"));
                    try {
                        movieInfoModel.setActors(info_obj.getString("actors"));
                        movieInfoModel.setDescription(info_obj.getString("description"));
                        movieInfoModel.setAge(info_obj.getString("age"));
                        movieInfoModel.setCountry(info_obj.getString("country"));
                    }catch (Exception e1){
                        e1.printStackTrace();
                    }
                    JSONArray jsonArray = info_obj.getJSONArray("backdrop_path");
                    List<String> stringList = new ArrayList<>();
                    for (int i=0;i<jsonArray.length();i++){
                        stringList.add(jsonArray.getString(i));
                    }
                    movieInfoModel.setBackdrop_path(stringList);
                }
            }catch (Exception e){
                e.printStackTrace();
                Log.e("error","info_parse_error");
            }
            showMovieModel.setMovieInfoModel(movieInfoModel);
            isInApiCalling = false;
            requireActivity().runOnUiThread(this::setModelInfo);
        } catch (Exception e) {
            e.printStackTrace();
            isInApiCalling = false;
            requireActivity().runOnUiThread(()->{
                DefaultSliderView textSliderView = new DefaultSliderView (requireContext());
                // initialize a SliderLayout
                textSliderView
//                    .description(name)
                        .image(showMovieModel.getStream_icon())
                        .setScaleType(BaseSliderView.ScaleType.FitCenterCrop);

                //add your extra information
                //            textSliderView.bundle(new Bundle());
                //            textSliderView.getBundle()
                //                    .putString("extra",name);

                mDemoSlider.addSlider(textSliderView);
                mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
                //        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                mDemoSlider.setCustomAnimation(new DescriptionAnimation());
                mDemoSlider.setDuration(Integer.MAX_VALUE);
            });
        }
    }

    private void setModelInfo() {
//        image.setImageURI(Uri.parse(showMovieModel.getMovieInfoModel().getMovie_img()));
        mDemoSlider.removeAllSliders();
//        deleteCache(requireContext());
        if (showMovieModel.getMovieInfoModel()!=null && showMovieModel.getMovieInfoModel().getBackdrop_path()!=null){
            for(String url : showMovieModel.getMovieInfoModel().getBackdrop_path()){
                if (url==null || url.equalsIgnoreCase("")) continue;
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
            mDemoSlider.setDuration(showMovieModel.getMovieInfoModel().getBackdrop_path().size()* Constants.GetSlideTime(requireContext()));
            title.setText(showMovieModel.getName());
            subTitle.setText(showMovieModel.getMovieInfoModel().getCast());
            body.setText(showMovieModel.getMovieInfoModel().getPlot());
            try {
                ratingBar.setRating(Float.valueOf(showMovieModel.getRating10()));
            }catch (Exception e){
                e.printStackTrace();
                ratingBar.setRating(0);
            }
        }
    }

    @Override
    public boolean myOnKeyDown(KeyEvent event){
        if (event.getAction()==KeyEvent.ACTION_UP){
            switch (event.getKeyCode()){
                case KeyEvent.KEYCODE_BACK:
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container,((MainActivity)requireActivity()).fragmentList.get(0))//FragmentCatchupDetail
                            .addToBackStack(null).commit();
                    return false;
            }
        }
        return super.myOnKeyDown(event);
    }
}
