package com.it_tech613.skydark.ui.series;


import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.google.android.material.textfield.TextInputEditText;
import com.it_tech613.skydark.R;
import com.it_tech613.skydark.apps.Constants;
import com.it_tech613.skydark.apps.MyApp;
import com.it_tech613.skydark.ui.MainActivity;
import com.it_tech613.skydark.utils.MyFragment;

import java.util.List;


public class FragmentSeries extends MyFragment {

    private RecyclerView category_recyclerview;
    private SeriesCategoryAdapter seriesCategoryAdapter;
    private boolean isOnSearch=false;
    private int position=-1;
    private TextInputEditText textInputLayout;
    private SliderLayout mDemoSlider;
    private TextView title, subTitle, body;
    private RatingBar ratingBar;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_series, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        category_recyclerview = view.findViewById(R.id.category_recyclerview);
        category_recyclerview.setLayoutManager(new LinearLayoutManager(requireContext()));
        textInputLayout = view.findViewById(R.id.editText);
        title = view.findViewById(R.id.textView9);
        subTitle = view.findViewById(R.id.textView10);
        body = view.findViewById(R.id.textView11);
        mDemoSlider = view.findViewById(R.id.slider_viewpager);
        ratingBar = view.findViewById(R.id.vod_detail_ratingbar);
        textInputLayout.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                Log.e("ActionDone","clicked");
                if (i == EditorInfo.IME_ACTION_DONE){
                    if (!isOnSearch)
                        seriesCategoryAdapter.search(textInputLayout.getText().toString());
                }
                return false;
            }
        });
        Button submit = view.findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnSearch) return;
                seriesCategoryAdapter.search(textInputLayout.getText().toString());
            }
        });
        setRecyclerView();
    }

    private void setRecyclerView() {
        seriesCategoryAdapter = new SeriesCategoryAdapter(
                requireContext(),
                (categoryPosition, homeCategory, seriesModel, isClicked) -> {
                    if (isClicked){
                        //if clicked
                        MyApp.selectedSeriesModel = seriesModel;
                        List<MyFragment> myFragments = ((MainActivity)requireActivity()).fragmentList;
                        requireActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container,myFragments.get(myFragments.size()-5))//FragmentSeasons
                                .addToBackStack(null).commit();
                    }else {
                        //if focused
                        final List<String> slideModels = seriesModel.getBackdrop_path();
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
                        mDemoSlider.setDuration(slideModels.size()* Constants.GetSlideTime(requireContext()));
                        position=categoryPosition;
                        title.setText(seriesModel.getName());
                        subTitle.setText(seriesModel.getRating_5based()+"");
                        body.setText(seriesModel.getPlot());
                        try {
                            ratingBar.setRating(Float.valueOf(seriesModel.getRating()));
                        }catch (Exception e){
                            e.printStackTrace();
                            ratingBar.setRating(0);
                        }
                    }
                    return null;
                }
        ){
            @Override
            public void setBlocked(boolean isBlocked) {
                isOnSearch = isBlocked;
            }
        };
        seriesCategoryAdapter.setList(MyApp.series_categories_filter,true);
        category_recyclerview.setAdapter(seriesCategoryAdapter);
    }

    @Override
    public boolean myOnKeyDown(KeyEvent event){
        if (event.getAction()==KeyEvent.ACTION_UP){
            switch (event.getKeyCode()){
                case KeyEvent.KEYCODE_DPAD_UP:
                    if (position==0) textInputLayout.requestFocus();
                    return false;
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
