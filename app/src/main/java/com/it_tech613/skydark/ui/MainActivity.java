package com.it_tech613.skydark.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.it_tech613.skydark.R;
import com.it_tech613.skydark.apps.Constants;
import com.it_tech613.skydark.apps.MyApp;
import com.it_tech613.skydark.models.SideMenu;
import com.it_tech613.skydark.ui.catchup.FragmentCatchupCategory;
import com.it_tech613.skydark.ui.catchup.FragmentCatchupDetail;
import com.it_tech613.skydark.ui.home.FragmentHome;
import com.it_tech613.skydark.ui.liveTv.FragmentExoLiveTv;
import com.it_tech613.skydark.ui.liveTv.FragmentIjkLiveTv;
import com.it_tech613.skydark.ui.liveTv.FragmentLiveTv;
import com.it_tech613.skydark.ui.movies.FragmentAllMovie;
import com.it_tech613.skydark.ui.movies.FragmentMovieDetail;
import com.it_tech613.skydark.ui.movies.FragmentMovies;
import com.it_tech613.skydark.ui.multi.FragmentMultiScreen;
import com.it_tech613.skydark.ui.multi.PinMultiScreenDlg;
import com.it_tech613.skydark.ui.series.FragmentEpisodes;
import com.it_tech613.skydark.ui.series.FragmentSeasons;
import com.it_tech613.skydark.ui.series.FragmentSeries;
import com.it_tech613.skydark.ui.series.FragmentSeriesHolder;
import com.it_tech613.skydark.ui.settings.FragmentSettings;
import com.it_tech613.skydark.ui.tvGuide.FragmentTvGuide;
import com.it_tech613.skydark.utils.MyFragment;
import com.it_tech613.skydark.vpn.fastconnect.core.OpenConnectManagementThread;
import com.it_tech613.skydark.vpn.fastconnect.core.OpenVpnService;
import com.it_tech613.skydark.vpn.fastconnect.core.VPNConnector;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    public List<MyFragment> fragmentList;
    private VPNConnector mConn;
    private ConstraintLayout rootview;
    int select_player = 0;
    private int selected_page=0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyApp.instance.setKpHUD(this);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        RecyclerView menu_recyclerview = findViewById(R.id.menu_recyclerview);
        rootview = findViewById(R.id.rootview);
        menu_recyclerview.setLayoutManager(new LinearLayoutManager(this));

        fragmentList = new ArrayList<>();
        fragmentList.add(new FragmentHome());
        fragmentList.add(new FragmentLiveTv());
        fragmentList.add(new FragmentMultiScreen());
        fragmentList.add(new FragmentMovies());
        fragmentList.add(new FragmentSeries());
        fragmentList.add(new FragmentTvGuide());
        fragmentList.add(new FragmentCatchupCategory());
        fragmentList.add(new FragmentSettings());

        fragmentList.add(new FragmentSeriesHolder());//7
        fragmentList.add(new FragmentSeasons());//8
        fragmentList.add(new FragmentAllMovie());//9
        fragmentList.add(new FragmentMovieDetail());//10
        fragmentList.add(new FragmentEpisodes());//11
        fragmentList.add(new FragmentCatchupDetail());//12

        getSupportFragmentManager().beginTransaction().add(R.id.container,fragmentList.get(0)).addToBackStack(null).commit();

        final List<SideMenu> list = new ArrayList<SideMenu>();
        list.add(new SideMenu("Home"));
        list.add(new SideMenu("Live Tv"));
        list.add(new SideMenu("Multi TV"));
        list.add(new SideMenu("Movies"));
        list.add(new SideMenu("Series"));
        list.add(new SideMenu("Tv Guide"));
        list.add(new SideMenu("Sports Guide"));
        list.add(new SideMenu("Catchup"));
        list.add(new SideMenu("Settings"));

        menu_recyclerview.setAdapter(new MenuAdapter(list, this, (sideMenu, position) -> {
            if (sideMenu.getName().equals("Multi TV")){
//                showScreenModeList();
                boolean remember_two=false;
                if (MyApp.instance.getPreference().get("remember_two_screen")!=null) remember_two=(boolean) MyApp.instance.getPreference().get("remember_two_screen");
                if (!remember_two){
                    PinMultiScreenDlg pinMultiScreenDlg=new PinMultiScreenDlg(MainActivity.this, new PinMultiScreenDlg.DlgPinListener() {
                        @Override
                        public void OnYesClick(Dialog dialog, String pin_code, boolean is_remember) {
                            if(!pin_code.equals(Constants.GetPin2(MainActivity.this))) {
                                Toast.makeText(MainActivity.this,"Invalid password!",Toast.LENGTH_LONG).show();
                                return;
                            }
                            MyApp.instance.getPreference().put("remember_two_screen",is_remember);
                            MyApp.num_screen = 2;
                            replaceFragment(fragmentList.get(2),2);
                        }

                        @Override
                        public void OnCancelClick(Dialog dialog, String pin_code) {

                        }
                    },remember_two);
                    pinMultiScreenDlg.show();
                }else {
                    MyApp.num_screen = 2;
                    replaceFragment(fragmentList.get(2),2);
                }
            }else if (sideMenu.getName().equals("Sports Guide")){
                startActivity(new Intent(this, WebViewActivity.class));
            }else {
                if (position>6) position-=1;
                replaceFragment(fragmentList.get(position),position);
            }
            return null;
        }));

        Constants.getVodFilter();
        Constants.getLiveFilter();
        Constants.getSeriesFilter();

        select_player = (int) MyApp.instance.getPreference().get(Constants.getCurrentPlayer());
        switch (select_player){
            case 0:
                fragmentList.set(1,new FragmentLiveTv());
                break;
            case 1:
                fragmentList.set(1,new FragmentIjkLiveTv());
                break;
            case 2:
                fragmentList.set(1,new FragmentExoLiveTv());
                break;
        }
    }

    private int selected_item;

    private void showScreenModeList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select One Mode");

        String[] screen_mode_list = {"Four Way Screen", "Three Way Screen", "Dual Screen"};

        builder.setSingleChoiceItems(screen_mode_list, 0,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selected_item = which;
                    }
                });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (selected_item==0) {
                    boolean remember_four=false;
                    if (MyApp.instance.getPreference().get("remember_four_screen")!=null) remember_four=(boolean) MyApp.instance.getPreference().get("remember_four_screen");
                    if (!remember_four){
                        PinMultiScreenDlg pinMultiScreenDlg=new PinMultiScreenDlg(MainActivity.this, new PinMultiScreenDlg.DlgPinListener() {
                            @Override
                            public void OnYesClick(Dialog dialog, String pin_code, boolean is_remember) {
                                if(!pin_code.equals(Constants.GetPin4(MainActivity.this))) {
                                    Toast.makeText(MainActivity.this,"Invalid password!",Toast.LENGTH_LONG).show();
                                    return;
                                }
                                MyApp.instance.getPreference().put("remember_four_screen",is_remember);
                                MyApp.num_screen = 4;
                                replaceFragment(fragmentList.get(2),2);
                            }

                            @Override
                            public void OnCancelClick(Dialog dialog, String pin_code) {

                            }
                        },remember_four);
                        pinMultiScreenDlg.show();
                    }else {
                        MyApp.num_screen = 4;
                        replaceFragment(fragmentList.get(2),2);
                    }
                }
                else if (selected_item==1) {
                    boolean remember_three=false;
                    if (MyApp.instance.getPreference().get("remember_three_screen")!=null) remember_three=(boolean) MyApp.instance.getPreference().get("remember_three_screen");
                    if (!remember_three){
                        PinMultiScreenDlg pinMultiScreenDlg=new PinMultiScreenDlg(MainActivity.this, new PinMultiScreenDlg.DlgPinListener() {
                            @Override
                            public void OnYesClick(Dialog dialog, String pin_code, boolean is_remember) {
                                if(!pin_code.equals(Constants.GetPin3(MainActivity.this))) {
                                    Toast.makeText(MainActivity.this,"Invalid password!",Toast.LENGTH_LONG).show();
                                    return;
                                }
                                MyApp.instance.getPreference().put("remember_three_screen",is_remember);
                                MyApp.num_screen = 3;
                                replaceFragment(fragmentList.get(2),2);
                            }

                            @Override
                            public void OnCancelClick(Dialog dialog, String pin_code) {

                            }
                        },remember_three);
                        pinMultiScreenDlg.show();
                    }else {
                        MyApp.num_screen = 3;
                        replaceFragment(fragmentList.get(2),2);
                    }
                }
                else {
                    boolean remember_two=false;
                    if (MyApp.instance.getPreference().get("remember_two_screen")!=null) remember_two=(boolean) MyApp.instance.getPreference().get("remember_two_screen");
                    if (!remember_two){
                        PinMultiScreenDlg pinMultiScreenDlg=new PinMultiScreenDlg(MainActivity.this, new PinMultiScreenDlg.DlgPinListener() {
                            @Override
                            public void OnYesClick(Dialog dialog, String pin_code, boolean is_remember) {
                                if(!pin_code.equals(Constants.GetPin2(MainActivity.this))) {
                                    Toast.makeText(MainActivity.this,"Invalid password!",Toast.LENGTH_LONG).show();
                                    return;
                                }
                                MyApp.instance.getPreference().put("remember_two_screen",is_remember);
                                MyApp.num_screen = 2;
                                replaceFragment(fragmentList.get(2),2);
                            }

                            @Override
                            public void OnCancelClick(Dialog dialog, String pin_code) {

                            }
                        },remember_two);
                        pinMultiScreenDlg.show();
                    }else {
                        MyApp.num_screen = 2;
                        replaceFragment(fragmentList.get(2),2);
                    }
                }
            }
        });
        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void replaceFragment(Fragment fragment,int selected_page_id){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container,fragment)
                .addToBackStack(null)
                .commit();
        if (selected_page_id<8)
            selected_page=selected_page_id;
    }

    private boolean is_full = false;

    public boolean getIs_full(){
        return is_full;
    }

    public void toggleFullScreen(boolean full){
        if (is_full==full) return;
        if (full){
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(rootview);
            constraintSet.setGuidelinePercent(R.id.guideline4, 0.0f);
            constraintSet.applyTo(rootview);
        }else {
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(rootview);
            constraintSet.setGuidelinePercent(R.id.guideline4, 0.15f);
            constraintSet.applyTo(rootview);
//            ((RecyclerView)findViewById(R.id.menu_recyclerview)).scrollToPosition(selected_page);
        }
        Log.e("MainActivity","togglefullscreen "+full);
        is_full=full;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP) {
            Log.e("keycode_back","clicked on MainActivity");
            for (MyFragment myFragment : fragmentList)
                if (myFragment.isVisible()) {
                    if (!myFragment.myOnKeyDown(event)) return false;
                }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(MyApp.is_vpn){
            findViewById(R.id.ly_vpn).setVisibility(View.VISIBLE);
        }else {
            findViewById(R.id.ly_vpn).setVisibility(View.GONE);
        }
        mConn = new VPNConnector(this, true) {
            @Override
            public void onUpdate(OpenVpnService service) {

            }
        };
    }

    private void stopVPN() {
        if (mConn.service.getConnectionState() ==
                OpenConnectManagementThread.STATE_DISCONNECTED) {
            mConn.service.startReconnectActivity(this);
        } else {
            mConn.service.stopVPN();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopVPN();
        System.exit(0);
    }
}
