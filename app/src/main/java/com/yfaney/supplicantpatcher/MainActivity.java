package com.yfaney.supplicantpatcher;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;


            public class MainActivity extends Activity {

                private boolean mSU_Allowed = false;

                @Override
                protected void onCreate(Bundle savedInstanceState) {
                    super.onCreate(savedInstanceState);
                    setContentView(R.layout.activity_main);

                }

                @Override
                protected void onStart(){
                    super.onStart();
                    new AsyncTask<Void, Process, Integer>(){

            @Override
            protected Integer doInBackground(Void... params) {
                try {
                    Process su = Runtime.getRuntime().exec("su -c whoami");
                    InputStream is = su.getInputStream();
                    int read;
                    byte[] buffer = new byte[512];
                    while((read= is.read(buffer))>0){
                        Log.d("Proecss", new String(buffer));
                    }
                    return su.waitFor();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return -1;
            }

            @Override
            public void onPostExecute(Integer result){
                Log.d("Process", "SU returns code :" + Integer.toString(result));
                if(result == 0){
                    Toast.makeText(getBaseContext(), "Root Permission Allowed.", Toast.LENGTH_SHORT).show();
                    readyFile();
                }else{
                    Toast.makeText(getBaseContext(), "This app needs a root permission.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }.execute();
    }

    @Override
    protected void onResume(){
        super.onResume();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void readyFile(){
        new AsyncTask<Void, Process, Integer>(){

            @Override
            protected Integer doInBackground(Void... params) {
                PackageManager m = getPackageManager();
                String s = getPackageName();
                try {
                    PackageInfo p = m.getPackageInfo(s, 0);
                    s = p.applicationInfo.dataDir;
                } catch (PackageManager.NameNotFoundException e) {
                    Log.d("yourtag", "Error Package name not found ", e);
                }
                AssetManager assetManager = getBaseContext().getAssets();
                String[] files = null;
                File backup = new File(s, "backup");
                backup.mkdirs();
                try {
                    files = assetManager.list("files");
                    for(String filename : files) {
                        InputStream in = null;
                        OutputStream out = null;
                        try {
                            in = assetManager.open("files/" + filename);
                            File outFile = new File(s, filename);
                            if(outFile.isDirectory()) continue;
                            out = new FileOutputStream(outFile);
                            copyFile(in, out);
                            in.close();
                            in = null;
                            out.flush();
                            out.close();
                            out = null;
                            Process su = Runtime.getRuntime().exec("su -c ./backup.sh");
                            InputStream is = su.getInputStream();
                            int read;
                            byte[] buffer = new byte[128];
                            while((read= is.read(buffer))>0){
                                Log.d("Process", new String(buffer));
                            }
                            int exitCode = su.waitFor();
                            if(exitCode != 0) return exitCode;
                        } catch(IOException e) {
                            Log.e("tag", "Failed to copy asset file: " + filename, e);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                File api19 = new File(s, "api19");
                if(api19.mkdirs()){
                    try {
                        files = assetManager.list("files.api19");
                        for(String filename : files) {
                            InputStream in = null;
                            OutputStream out = null;
                            try {
                                in = assetManager.open("files/api19/" + filename);
                                File outFile = new File(s + "/api19", filename);
                                out = new FileOutputStream(outFile);
                                copyFile(in, out);
                                in.close();
                                in = null;
                                out.flush();
                                out.close();
                                out = null;
                                return 0;
                            } catch(IOException e) {
                                Log.e("tag", "Failed to copy asset file: " + filename, e);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return -1;
            }
        }.execute();
    }
    public void onUpdateClicked(View view){
//        Resources res = getResources();
//        Scanner scan = new Scanner(res.openRawResource(R.raw.install));
        //FileInputStream file = (FileInputStream) res.openRawResource(R.raw.install);
//        String uri = "android.resource://" + getPackageName() + "/"+R.raw.install;
//        Uri filepath = Uri.parse(uri);
//        filepath.getEncodedPath();
//        Log.d("FIle Path is", filepath.getEncodedPath());
//        Environment.getDataDirectory();
        new AsyncTask<Void, Process, Integer>(){

            @Override
            protected Integer doInBackground(Void... params) {
                try {
                    Process su = Runtime.getRuntime().exec("su -c ./install.sh api19");
                    InputStream is = su.getInputStream();
                    int read;
                    byte[] buffer = new byte[128];
                    while((read= is.read(buffer))>0){
                        Log.d("Process", new String(buffer));
                    }
                    return su.waitFor();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return -1;
            }

            @Override
            public void onPostExecute(Integer result){
                Log.d("Process", "SU returns code :" + Integer.toString(result));
                if(result == 0){
                    Toast.makeText(getBaseContext(), "Install success.", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getBaseContext(), "Install failed.", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    public void onRollbackClicked(View view){
        new AsyncTask<Void, Process, Integer>(){
            @Override
            protected Integer doInBackground(Void... params) {
                try {
                    Process su = Runtime.getRuntime().exec("su -c ./rollback.sh");
                    InputStream is = su.getInputStream();
                    int read;
                    byte[] buffer = new byte[128];
                    while((read= is.read(buffer))>0){
                        Log.d("Process", new String(buffer));
                    }
                    return su.waitFor();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return -1;
            }

            @Override
            public void onPostExecute(Integer result){
                Log.d("Process", "SU returns code :" + Integer.toString(result));
                if(result == 0){
                    Toast.makeText(getBaseContext(), "Rollback success.", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getBaseContext(), "Rollback failed.", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();

    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }
}
