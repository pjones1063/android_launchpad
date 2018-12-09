package net.ossfree.launcher4;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import net.ossfree.launcher4.Structures.TabPage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@SuppressLint({"DefaultLocale", "ValidFragment"})
public class StatusList<O> extends AppsList implements View.OnClickListener {

    View status;

    @SuppressLint("ValidFragment")
    public StatusList(TabPage pg) {
        super(pg);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SystemInfo.getSI().collectData(getActivity());
        status = inflater.inflate(R.layout.status, container, false);

        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(getActivity());
        final Drawable wp = wallpaperManager.getDrawable();
        status.setBackground(wp);
        ((Button) status.findViewById(R.id.refresh)).setOnClickListener(this);

        ((TextView) status.findViewById(R.id.textDate1)).setOnClickListener(this);
        ((TextView) status.findViewById(R.id.uptime)).setOnClickListener(this);
        ((TextView) status.findViewById(R.id.network)).setOnClickListener(this);
        ((TextView) status.findViewById(R.id.ip)).setOnClickListener(this);

        updateInfo();
        return status;
    }


    private void updateInfo() {
        ((TextView) status.findViewById(R.id.location)).setText(SystemInfo.getSI().getCityLocation());

        ((TextView) status.findViewById(R.id.batDat1)).setText(SystemInfo.getSI().getBatryLevel());
        ((TextView) status.findViewById(R.id.batDat2)).setText(SystemInfo.getSI().getBatryVoltage());
        ((TextView) status.findViewById(R.id.batDat3)).setText(SystemInfo.getSI().getBatryTemp());
        ((ImageView) status.findViewById(R.id.batWPB)).setImageBitmap(SystemInfo.getSI().getBatryLevelWPB());

        ((TextView) status.findViewById(R.id.textDate1)).setText(new SimpleDateFormat("EEEE MMM dd yyyy").format(new Date()));
        ((TextView) status.findViewById(R.id.uptime)).setText(SystemInfo.getSI().getUpTime() + "  " + SystemInfo.getSI().getSleepTime());
        ((TextView) status.findViewById(R.id.network)).setText(SystemInfo.getSI().getNetworkOperatorName() + "   " + SystemInfo.getSI().getSSID());

        ((TextView) status.findViewById(R.id.ip)).setText(SystemInfo.getSI().getWifiIP());

        ((TextView) status.findViewById(R.id.freeM1)).setText(SystemInfo.getSI().getMemTotal());
        ((TextView) status.findViewById(R.id.freeM2)).setText(SystemInfo.getSI().getMemFree());
        ((TextView) status.findViewById(R.id.freeM3)).setText(SystemInfo.getSI().getMemPrcnt());
        ((ImageView) status.findViewById(R.id.freeMwpb)).setImageBitmap(SystemInfo.getSI().getMemSpaceWPB());

        ((TextView) status.findViewById(R.id.freeSD1)).setText(SystemInfo.getSI().getSDTotal());
        ((TextView) status.findViewById(R.id.freeSD2)).setText(SystemInfo.getSI().getSDFree());
        ((TextView) status.findViewById(R.id.freeSD3)).setText(SystemInfo.getSI().getSDPrcnt());
        ((TextView) status.findViewById(R.id.freeSD4)).setText(SystemInfo.getSI().getSDPath());
        ((ImageView) status.findViewById(R.id.freeSDwpb)).setImageBitmap(SystemInfo.getSI().getSDSpaceWPB());

        ((TextView) status.findViewById(R.id.freeIT1)).setText(SystemInfo.getSI().getITTotal());
        ((TextView) status.findViewById(R.id.freeIT2)).setText(SystemInfo.getSI().getITFree());
        ((TextView) status.findViewById(R.id.freeIT3)).setText(SystemInfo.getSI().getITPrcnt());
        ((TextView) status.findViewById(R.id.freeIT4)).setText(SystemInfo.getSI().getITPath());
        ((ImageView) status.findViewById(R.id.freeITwpb)).setImageBitmap(SystemInfo.getSI().getITSpaceWPB());

     /*   try {
            WebView webView = (WebView) status.findViewById(R.id.webView);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setAppCacheEnabled(true);
            webView.loadUrl(MainActivity.getWeather_uri());
        }catch (Exception e) {
            LLg.e(e.getMessage());
        }
     */


        switch (SystemInfo.getSI().getBatryStat()) {
            case BatteryManager.BATTERY_STATUS_FULL:
                ((TextView) status.findViewById(R.id.batSt)).setText(R.string.sys_charged);
                ((TextView) status.findViewById(R.id.batSt)).setTextColor(Color.GREEN);
                break;
            case BatteryManager.BATTERY_STATUS_CHARGING:
                ((TextView) status.findViewById(R.id.batSt)).setText(R.string.sys_charging);
                ((TextView) status.findViewById(R.id.batSt)).setTextColor(Color.CYAN);
                break;
            case -999:
                ((TextView) status.findViewById(R.id.batSt)).setText(R.string.sys_chargelow);
                ((TextView) status.findViewById(R.id.batSt)).setTextColor(Color.RED);
                break;
            default:
                ((TextView) status.findViewById(R.id.batSt)).setText(R.string.sys_discharge);
                ((TextView) status.findViewById(R.id.batSt)).setTextColor(Color.WHITE);
                break;
        }
    }


    private void showSettings() {
        startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
    }

    private void showCalendar() {
        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        long time = cal.getTime().getTime();
        Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
        builder.appendPath("time");
        builder.appendPath(Long.toString(time));
        startActivity(new Intent(Intent.ACTION_VIEW, builder.build()));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.refresh:
                updateInfo();
                break;

            case R.id.textClock1:   case R.id.textDate1:
                showCalendar();
                break;

             default:
                 showSettings();

        }
    }
}
