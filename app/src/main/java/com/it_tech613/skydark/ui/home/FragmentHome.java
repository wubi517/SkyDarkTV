package com.it_tech613.skydark.ui.home;

import android.app.Dialog;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.it_tech613.skydark.R;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.it_tech613.skydark.apps.Constants;
import com.it_tech613.skydark.apps.MyApp;
import com.it_tech613.skydark.models.*;
import com.it_tech613.skydark.ui.LiveExoPlayActivity;
import com.it_tech613.skydark.ui.LiveIjkPlayActivity;
import com.it_tech613.skydark.ui.LivePlayActivity;
import com.it_tech613.skydark.ui.MainActivity;
import com.it_tech613.skydark.ui.liveTv.PinDlg;
import com.it_tech613.skydark.utils.MyFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FragmentHome extends MyFragment {

    private RecyclerView bannerRecyclerview;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bannerRecyclerview = view.findViewById(R.id.banner_recyclerview);
        bannerRecyclerview.setLayoutManager(new GridLayoutManager(requireContext(),3));
        RecyclerView category_recyclerview = view.findViewById(R.id.category_recyclerview);

        category_recyclerview.setLayoutManager(new LinearLayoutManager(requireContext()));
        category_recyclerview.setAdapter(new HomeListAdapter(requireContext(), (categoryPos, itemPos, epgChannel, movieModel, seriesModel) -> {
            switch (categoryPos){
                case 1:
                    if (epgChannel.getCategory_id()==Constants.xxx_category_id){
                        PinDlg pinDlg = new PinDlg(requireContext(), new PinDlg.DlgPinListener() {
                            @Override
                            public void OnYesClick(Dialog dialog, String pin_code) {
                                dialog.dismiss();
                                String pin = (String)MyApp.instance.getPreference().get(Constants.getPIN_CODE());
                                if(pin_code.equalsIgnoreCase(pin)){
                                    dialog.dismiss();
                                    playVideo(epgChannel);
                                }else {
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
                    }else playVideo(epgChannel);

                    break;
                case 0:
                    MyApp.subMovieModels = new ArrayList<>();
                    MyApp.subMovieModels.add(movieModel);
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container,((MainActivity)requireActivity()).fragmentList.get(((MainActivity)requireActivity()).fragmentList.size()-3))//FragmentMovieDetail
                            .addToBackStack(null).commit();
                    break;
                case 2:
                    MyApp.selectedSeriesModel = seriesModel;
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container,((MainActivity)requireActivity()).fragmentList.get(((MainActivity)requireActivity()).fragmentList.size()-5))//FragmentSeriesHolder
                            .addToBackStack(null).commit();
                    break;
            }
            return null;
        }));
    }

    private void playVideo(EPGChannel epgChannel) {
        String url = MyApp.instance.getIptvclient().buildLiveStreamURL(MyApp.user,MyApp.pass,String.valueOf(epgChannel.getStream_id()),"ts");
        Log.e("Iptvclient",url);
        int current_player = (int) MyApp.instance.getPreference().get(Constants.getCurrentPlayer());
        Intent intent;
        switch (current_player){
            case 1:
                intent = new Intent(requireContext(), LiveIjkPlayActivity.class);
                break;
            case 2:
                intent = new Intent(requireContext(), LiveExoPlayActivity.class);
                break;
            default:
                intent = new Intent(requireContext(), LivePlayActivity.class);
                break;
        }
        intent.putExtra("title",epgChannel.getName());
        intent.putExtra("img",epgChannel.getImageURL());
        intent.putExtra("url",url);
        intent.putExtra("stream_id",epgChannel.getStream_id());
        intent.putExtra("is_live",true);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("FragmentHome","onResume");
        final ArrayList<String> slideModels = getSlides();
        bannerRecyclerview.setAdapter(new BannerAdapter(slideModels));
    }

    private ArrayList<String> getSlides() {
        ArrayList<String> list = new ArrayList<>();
        Picasso.with(requireContext()).invalidate(Constants.GetAd1(requireContext()));
        list.add(Constants.GetAd1(requireContext()));
        Picasso.with(requireContext()).invalidate(Constants.GetAd2(requireContext()));
        list.add(Constants.GetAd2(requireContext()));
        Picasso.with(requireContext()).invalidate(Constants.GetAd3(requireContext()));
        list.add(Constants.GetAd3(requireContext()));
        Picasso.with(requireContext()).invalidate(Constants.GetAd4(requireContext()));
        list.add(Constants.GetAd4(requireContext()));
        return list;
    }

    @Override
    public boolean myOnKeyDown(KeyEvent event){
        //do whatever you want here
        if (event.getAction()==KeyEvent.ACTION_UP){
            switch (event.getKeyCode()){
                case KeyEvent.KEYCODE_BACK:
                    requireActivity().finish();
                    Log.e("keycode_back","clicked on home");
                    return false;
            }
        }
        return super.myOnKeyDown(event);
    }
}
