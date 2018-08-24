package net.ossfree.launcher4;


import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.DisplayMetrics;

import net.ossfree.launcher4.Logger.LLg;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.concurrent.TimeUnit;

@SuppressLint("DefaultLocale")
public class SystemInfo {

    private static SystemInfo systemInfo = new SystemInfo();
    private String intCard = Environment.getExternalStorageDirectory().toString();
    private String extSdCard = MainActivity.getSDPath();
    private Context context = null;
    private String extIP = "";


    public static final long SIZE_KB = 1024L;
    public static final long SIZE_MB = 1048576L;

    class RetrieveIP extends AsyncTask <String, Void, String> {
        protected String doInBackground(String... urls) {
            InputStreamReader inputStreamReader = null;
            BufferedReader bufferedReader = null;
            try {
                extIP = "";
                inputStreamReader = new InputStreamReader(new URL("http://checkip.amazonaws.com").openStream());
                bufferedReader = new BufferedReader(inputStreamReader);
                String line = bufferedReader.readLine();
                bufferedReader.close();
                inputStreamReader.close();
                if (!validIP(line)) line = "";
                extIP = line;
            } catch (Exception e) {
                extIP = "";
                LLg.e(e.getMessage());
            }
            return extIP;
        }
    }

    public static SystemInfo getSI() {
        if (systemInfo == null) systemInfo = new SystemInfo();
        return systemInfo;
    }



    public boolean  collectData(Context ctx) {
        context = ctx;
        new RetrieveIP().execute();
        return true;
    }


    public String getSDTotal() {
        return context.getString(R.string.sys_sdcard) + getGigTotal(new File(extSdCard));
    }

    public String getSDFree() {
        return getGigFree(new File(extSdCard));
    }

    public String getSDPrcnt() {
        return getFreePrcnt(new File(extSdCard));
    }

    public String getITTotal() {
        return context.getString(R.string.sys_internalcard)  + getGigTotal(new File(intCard));
    }

    public String getITFree() {
        return getGigFree(new File(intCard));
    }

    public String getITPrcnt() {
        return getFreePrcnt(new File(intCard));
    }

    public Bitmap getSDSpaceWPB() {
        return getBitmapDown(getFreeDecimal(new File(extSdCard)));
    }

    public Bitmap getITSpaceWPB() {
         return getBitmapDown(getFreeDecimal(new File(intCard)));
    }
    
    public  String getUpTime() {
        long millis = SystemClock.elapsedRealtime();
        return String.format(context.getString(R.string.sys_uptime),
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)) );
    }

    public  String getSleepTime() {
        long millis = SystemClock.elapsedRealtime() - SystemClock.uptimeMillis() ;
        return String.format(context.getString(R.string.sys_sleeptime),
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)) );
    }


    @SuppressWarnings("deprecation")
    public  String getWifiIP() {
        try {
            @SuppressLint("WifiManagerPotentialLeak")
            WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            return   "IP: "+Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress())+"  "+extIP;
        }   catch (final Exception e) {
            LLg.e(e.getMessage());
            return "IP: off";
        }
    }

    public  String getSSID() {
        try{
            WifiManager wm = (WifiManager) context.getSystemService (Context.WIFI_SERVICE);
            WifiInfo info = wm.getConnectionInfo ();
            return info.getSSID().replace("\"", "");
        }   catch (final Exception e) {
            LLg.e(e.getMessage());
            return "";
        }
    }

    public  String getMemTotal (){
        MemoryInfo mi = new MemoryInfo();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        double totalMegs = mi.totalMem / SIZE_MB;
        return String.format(context.getString(R.string.sys_freemem),totalMegs);
    }

    public  String getMemFree (){
        MemoryInfo mi = new MemoryInfo();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        double availableMegs = mi.availMem / SIZE_MB;
        return String.format(" %.0f MB", availableMegs);
    }

    public String getMemPrcnt() {
        try {
            MemoryInfo mi = new MemoryInfo();
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            activityManager.getMemoryInfo(mi);
            double availableMegs = mi.availMem / SIZE_MB;
            double totalMegs = mi.totalMem / SIZE_MB;
            return String.format(" %.1f", ((totalMegs - availableMegs) / totalMegs) * 100) + "% ";
        } catch (final Exception e) {
            LLg.e(e.getMessage());
            return " 0.0 % ";
        }
    }


    public Bitmap getMemSpaceWPB() {
            MemoryInfo mi = new MemoryInfo();
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            activityManager.getMemoryInfo(mi);
            double availableMegs = mi.availMem / SIZE_MB;
            double totalMegs = mi.totalMem / SIZE_MB;
            return getBitmapDown((int)(( (totalMegs-availableMegs) / totalMegs) * 100));
    }


    public  String  getGigFree(File f) {
        try {
            double SIZE_GB = SIZE_KB * SIZE_KB * SIZE_KB;
            StatFs stat = new StatFs(f.getPath());
            double  availableSpace = ((double)stat.getBlockSize() * (double)stat.getAvailableBlocks()) / SIZE_GB;
            return String.format(" %.1f GB ", availableSpace);
        }catch (Exception e) {
            //Toast.makeText(context.getApplicationContext(), "Unable to stat mount point - Check Preferences", Toast.LENGTH_LONG).show();
            LLg.e(e.getMessage());
            return " 0.0 GB ";
        }
    }

    public  String  getGigTotal(File f) {
        try {
            double SIZE_GB = SIZE_KB * SIZE_KB * SIZE_KB;
            StatFs stat = new StatFs(f.getPath());
            double  availableSpace = ((double)stat.getBlockSize() * (double)stat.getBlockCount()) / SIZE_GB;
            return String.format(" %.1f GB ", availableSpace);
        }catch (Exception e) {
            LLg.e(e.getMessage());
            return " 0.0 GB ";
        }
    }


    public  String  getFreePrcnt(File f) {
        try {
            double SIZE_GB = SIZE_KB * SIZE_KB * SIZE_KB;
            StatFs stat = new StatFs(f.getPath());
            double  availableSpace = ((double)stat.getBlockSize() * (double)stat.getAvailableBlocks()) / SIZE_GB;
            double  totalSpace =     ((double)stat.getBlockSize() * (double)stat.getBlockCount()) / SIZE_GB;
            return String.format(" %.1f", ((totalSpace-availableSpace)/totalSpace) * 100 ) + "% ";
        } catch (final Exception e) {
            LLg.e(e.getMessage());
            return " 0.0 % ";
        }
    }


    private  int  getFreeDecimal(File f) {
        try {
            double SIZE_GB = SIZE_KB * SIZE_KB * SIZE_KB;
            StatFs stat = new StatFs(f.getPath());
            double  availableSpace = ((double)stat.getBlockSize() * (double)stat.getAvailableBlocks()) / SIZE_GB;
            double  totalSpace =     ((double)stat.getBlockSize() * (double)stat.getBlockCount()) / SIZE_GB;
            return  (int) ( ((totalSpace-availableSpace)/totalSpace) * 100 );

        }catch (final Exception e) {
            LLg.e(e.getMessage());
            return 0;
        }
    }

    public  String getNetworkOperatorName() {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm.isNetworkRoaming())
                return context.getString(R.string.sys_network) +"  "+ tm.getNetworkOperatorName() + " (roaming)";
            else
                return context.getString(R.string.sys_network) +"  "+  tm.getNetworkOperatorName();
        } catch (final Exception e) {
            LLg.e(e.getMessage());
        }
        return context.getString(R.string.sys_networknone);
    }

    public  String getHostName() {
        try {
            Method getString = Build.class.getDeclaredMethod("getString", String.class);
            getString.setAccessible(true);
            return "Host Name: " + getString.invoke(null, "net.hostname").toString();
        } catch (Exception ex) {
            LLg.e(ex.getMessage());
            return "";
        }
    }


    public int getBatryStat() {
        try {
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, ifilter);
            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int x = (int) ((level / (float) scale) * 100);
            if (x < 20)
                return -999;
            else
                return batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        } catch (Exception ex) {
            LLg.e(ex.getMessage());
            return -999;
        }


    }

    public  String getBatryLevel () {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        return  String.format(context.getString(R.string.sys_battery), (level / (float)scale)  * 100) + "% ";
    }


    public  String getBatryVoltage () {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
        return  String.format(" %.2f Vlt ", (level /  (float)1000)) ;
    }

    public  String getBatryTemp () {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
        return  String.format(" %.1f ° ", (level / (float)10) ) ;
    }


    public Bitmap  getBatryLevelWPB() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        return  getBitmapUp((int) ((level / (float)scale)  * 100));
    }


    private boolean validIP(String ip) {
        try {
            if (ip == null || ip.isEmpty()) return false;
            String[] parts = ip.split("\\.");
            if (parts.length != 4) return false;
            for (String s : parts) {
                int i = Integer.parseInt(s);
                if ((i < 0) || (i > 255)) return false;
            }
            if (ip.endsWith(".")) 	return false;
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public static Boolean isNumeric(String string) {return (string.matches("^[0-9]+$"))?true:false;}


    private static int getw() {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return metrics.widthPixels - 320;
    }

    public static Bitmap getBitmapUp(int p) {
        int w = getw();
        double f = w / 100.0;
        Bitmap bitmap = Bitmap.createBitmap(w, 30, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.GRAY);
        Paint paint = new Paint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);

        if(p > 30) paint.setColor(Color.GREEN);
        else if(p > 15) paint.setColor(Color.YELLOW);
        else paint.setColor(Color.RED);

        paint.setStrokeWidth(22);
        canvas.drawLine(0, 10, (int)(p*f), 10, paint);
        return bitmap;

    }


    public static Bitmap getBitmapDown(int p) {
        int w = getw();
        double f = w / 100.0;
        Bitmap bitmap = Bitmap.createBitmap(w, 30, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.GRAY);
        Paint paint = new Paint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);


        if(p > 85) paint.setColor(Color.RED);
        else if(p > 70) paint.setColor(Color.YELLOW);
        else paint.setColor(Color.GREEN);

        paint.setStrokeWidth(22);
        canvas.drawLine(0, 10, (int)(p*f), 10, paint);
        return bitmap;
    }

}