/*
 * Copyright (C) 2015 AICP Project
 *               2017 The ParaSHIT Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package in.parashit.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.android.settings.R;

import java.lang.reflect.Method;

public class Helpers {
    // avoids hardcoding the tag
    private static final String TAG = Thread.currentThread().getStackTrace()[1].getClassName();

    public Helpers() {
        // dummy constructor
    }

    public static void restartSystemUI(Context context) {
        new RestartSystemUITask().execute(context);
    }

    public static void showSystemUIrestartDialog(Activity a) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(a);
        builder.setTitle(R.string.systemui_restart_title);
        builder.setMessage(R.string.systemui_restart_message);
        builder.setPositiveButton(R.string.print_restart,
                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                RestartSystemUITask task = new RestartSystemUITask() {
                    private ProgressDialog dialog;
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        dialog = new ProgressDialog(a);
                        dialog.setMessage(a.getResources().getString(R.string.restarting_ui));
                        dialog.setCancelable(false);
                        dialog.setIndeterminate(true);
                        dialog.show();
                    }
                    @Override
                    protected Void doInBackground(Context... params) {
                        // Give the user a second to see the dialog
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            // Ignore
                        }

                        // Restart the UI
                        super.doInBackground(params);
                        return null;
                    }
                    @Override
                    protected void onPostExecute(Void param) {
                        super.onPostExecute(param);
                        dialog.dismiss();
                    }
                };
                task.execute(a);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.show();
    }

    private static class RestartSystemUITask extends AsyncTask<Context, Void, Void> {
        private Context mContext;
        @Override
        protected Void doInBackground(Context... params) {
            try {
                if (params.length > 0) {
                    mContext = params[0].getApplicationContext();
                } else {
                    throw new Exception("Called RestartSystemUITask without context");
                }
                ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
                Class ActivityManagerNative = Class.forName("android.app.ActivityManagerNative");
                Method getDefault = ActivityManagerNative.getDeclaredMethod("getDefault", null);
                Object amn = getDefault.invoke(null, null);
                Method killApplicationProcess = amn.getClass().getDeclaredMethod("killApplicationProcess", String.class, int.class);
                mContext.stopService(new Intent().setComponent(new ComponentName("com.android.systemui", "com.android.systemui.SystemUIService")));
                am.killBackgroundProcesses("com.android.systemui");
                for (ActivityManager.RunningAppProcessInfo app : am.getRunningAppProcesses()) {
                    if ("com.android.systemui".equals(app.processName)) {
                        killApplicationProcess.invoke(amn, app.processName, app.uid);
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static boolean isPackageInstalled(String packageName, PackageManager pm) {
        try {
            String mVersion = pm.getPackageInfo(packageName, 0).versionName;
            if (mVersion == null) {
                return false;
            }
        } catch (NameNotFoundException notFound) {
            Log.i(TAG, "Package could not be found!", notFound);
            return false;
        }
        return true;
    }

    /**
    // DelayCallback usage
    int secs = 2; // Delay in seconds
    Helpers.delay(secs, new Utils.DelayCallback() {
        @Override
        public void afterDelay() {
            // Do something after delay
        }
    });
    */

    public interface DelayCallback{
        void afterDelay();
    }

    public static void delay(int secs, final DelayCallback delayCallback){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                delayCallback.afterDelay();
            }
        }, secs * 1000);
    }
}
