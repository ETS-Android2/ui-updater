package org.literacyapp.ui.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

public class StudentUpdateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(getClass().getName(), "onReceive");

        // Customize the user interface to match the current Student's level
        ArrayList<String> availableLetters = intent.getStringArrayListExtra("availableLetters");
        Log.i(getClass().getName(), "availableLetters: " + availableLetters);
        ArrayList<String> availableNumbers = intent.getStringArrayListExtra("availableNumbers");
        Log.i(getClass().getName(), "availableNumbers: " + availableNumbers);
        ArrayList<String> availableLiteracySkills = intent.getStringArrayListExtra("availableLiteracySkills");
        Log.i(getClass().getName(), "availableLiteracySkills: " + availableLiteracySkills);
        ArrayList<String> availableNumeracySkills = intent.getStringArrayListExtra("availableNumeracySkills");
        Log.i(getClass().getName(), "availableNumeracySkills: " + availableNumeracySkills);

        if (availableNumbers != null) {
            // Update Calculator application
            Intent updateCalculatorIntent = new Intent();
            updateCalculatorIntent.setPackage("org.literacyapp.calculator");
            updateCalculatorIntent.setAction("literacyapp.intent.action.STUDENT_UPDATED");
            updateCalculatorIntent.putStringArrayListExtra("availableNumbers", availableNumbers);
            Log.i(getClass().getName(), "Sending broadcast to " + updateCalculatorIntent.getPackage());
            context.sendBroadcast(updateCalculatorIntent);
        }

        if (availableLetters != null) {
            // Update Walezi application
            Intent updateCalculatorIntent = new Intent();
            updateCalculatorIntent.setPackage("org.literacyapp.walezi");
            updateCalculatorIntent.setAction("literacyapp.intent.action.STUDENT_UPDATED");
            updateCalculatorIntent.putStringArrayListExtra("availableLetters", availableLetters);
            Log.i(getClass().getName(), "Sending broadcast to " + updateCalculatorIntent.getPackage());
            context.sendBroadcast(updateCalculatorIntent);
        }

        // TODO: update Chat application


        // Obtain permission to change system settings
        try {
            runAsRoot(new String[] {"pm grant org.literacyapp.ui android.permission.WRITE_SECURE_SETTINGS"});
        } catch (IOException | InterruptedException e) {
            Log.e(getClass().getName(), null, e);
        }



        // The following is based on the document "Customize The User Interface"

        // Activate "Adaptive brightness"
        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 85); // 33% = 85/255
        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);

        // Set LCD density to maximum DPI
        boolean isDensityChanged = false;
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        Log.i(getClass().getName(), "density: " + displayMetrics.density);
        Log.i(getClass().getName(), "densityDpi: " + displayMetrics.densityDpi);
        Log.i(getClass().getName(), "heightPixels: " + displayMetrics.heightPixels);
        Log.i(getClass().getName(), "widthPixels: " + displayMetrics.widthPixels);
        Log.i(getClass().getName(), "scaledDensity: " + displayMetrics.scaledDensity);
        Log.i(getClass().getName(), "xdpi: " + displayMetrics.xdpi);
        Log.i(getClass().getName(), "ydpi: " + displayMetrics.ydpi);
        String deviceModel = Build.MODEL;
        Log.i(getClass().getName(), "deviceModel: " + deviceModel);
        if ("Pixel C".equals(deviceModel)) {
            if (displayMetrics.densityDpi < 360) {
//                displayMetrics.density = 2.25f;
//                displayMetrics.densityDpi = 360;
//                displayMetrics.heightPixels = 1692;
//                displayMetrics.widthPixels = 2560;
//                displayMetrics.scaledDensity = 2.925f;
//                displayMetrics.xdpi = 360.0f;
//                displayMetrics.ydpi = 360.0f;
//                context.getResources().getDisplayMetrics().setTo(displayMetrics);
//                isDensityChanged = true;
                Toast.makeText(context, "Please set the LCD density to 360 DPI", Toast.LENGTH_LONG).show();
            }
        } else if ("KFFOWI".equals(deviceModel)) {
            // Amazon Fire
            if (displayMetrics.densityDpi < 200) {
                Toast.makeText(context, "Please set the LCD density to 200 DPI", Toast.LENGTH_LONG).show();
            }
        } else if ("Nexus 7".equals(deviceModel)) {
            if (displayMetrics.densityDpi < 360) {
                Toast.makeText(context, "Please set the LCD density to 360 DPI", Toast.LENGTH_LONG).show();
            }
        }
        // TODO: add support for more devices

        // Set font size to 130% ("Huge")
        try {
            float fontScale = Settings.System.getFloat(context.getContentResolver(), Settings.System.FONT_SCALE);
            Log.i(getClass().getName(), "fontScale: " + fontScale);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(getClass().getName(), null, e);
        }
        Settings.System.putFloat(context.getContentResolver(), Settings.System.FONT_SCALE, 1.3f);


        // Set automatic display mode ("LiveDisplay")
        // TODO

        // Disable screen lock
        // TODO

        // Set display to sleep after 5 minutes of inactivity
        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 5 * 60 * 1000);

        // Remove options from power button menu
        // TODO

        // Volume buttons: activate “Wake up device” and “Reorient”
        // TODO

        // Activate "Show touches" in Developer options
        // TODO: revert this setting before submitting to XPRIZE
//        Settings.System.putInt(context.getContentResolver(), "show_touches", 1);
        // java.lang.RuntimeException: Unable to start receiver org.literacyapp.ui.receiver.StudentUpdateReceiver: java.lang.IllegalArgumentException: You cannot change private secure settings.
        // See https://code.google.com/p/android/issues/detail?id=194376&can=4&colspec=ID%20Status%20Priority%20Owner%20Summary%20Stars%20Reporter%20Opened


        // Set volume level to 70%
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        Log.i(getClass().getName(), "audioManager.getStreamMaxVolume(audioManager.STREAM_ALARM): " + audioManager.getStreamMaxVolume(audioManager.STREAM_ALARM));
        Log.i(getClass().getName(), "audioManager.getStreamMaxVolume(audioManager.STREAM_MUSIC): " + audioManager.getStreamMaxVolume(audioManager.STREAM_MUSIC));
        Log.i(getClass().getName(), "audioManager.getStreamMaxVolume(audioManager.STREAM_NOTIFICATION): " + audioManager.getStreamMaxVolume(audioManager.STREAM_NOTIFICATION));
        Log.i(getClass().getName(), "audioManager.getStreamMaxVolume(audioManager.STREAM_SYSTEM): " + audioManager.getStreamMaxVolume(audioManager.STREAM_SYSTEM));
        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, audioManager.getStreamMaxVolume(audioManager.STREAM_ALARM) * 20/100, 0);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(audioManager.STREAM_MUSIC) * 80/100, 0);
        audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, audioManager.getStreamMaxVolume(audioManager.STREAM_NOTIFICATION) * 80/100, 0);
        audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, audioManager.getStreamMaxVolume(audioManager.STREAM_SYSTEM) * 80/100, 0);


        // Install/adjust custom keyboard
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo("org.literacyapp.keyboard", PackageManager.GET_ACTIVITIES);
            Log.i(getClass().getName(), "The application is installed: org.literacyapp.keyboard");
            String defaultInputMethod = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);
            Log.i(getClass().getName(), "defaultInputMethod: " + defaultInputMethod);
            if (!defaultInputMethod.startsWith("org.literacyapp.keyboard")) {
//                // Encourage user to enable the LiteracyApp keyboard and switch to it
//                InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
//                inputMethodManager.showInputMethodPicker();
                try {
                    runAsRoot(new String[] {
                            "ime enable org.literacyapp.keyboard/.ImeService",
                            "ime set org.literacyapp.keyboard/.ImeService"
                    });
                } catch (IOException | InterruptedException e) {
                    Log.e(getClass().getName(), null, e);
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(getClass().getName(), "The application is not installed: org.literacyapp.keyboard", e);
        }

        if ((availableLetters != null) || (availableNumbers != null)) {
            // Update Keyboard application
            Intent updateCalculatorIntent = new Intent();
            updateCalculatorIntent.setPackage("org.literacyapp.keyboard");
            updateCalculatorIntent.setAction("literacyapp.intent.action.STUDENT_UPDATED");
            if (availableLetters != null) {
                updateCalculatorIntent.putStringArrayListExtra("availableLetters", availableLetters);
            }
            if (availableNumbers != null) {
                updateCalculatorIntent.putStringArrayListExtra("availableNumbers", availableNumbers);
            }
            Log.i(getClass().getName(), "Sending broadcast to " + updateCalculatorIntent.getPackage());
            context.sendBroadcast(updateCalculatorIntent);
        }


        // Install/adjust custom font
        boolean isDefaultFontReplaced = false;
        // See http://www.androidauthority.com/how-to-change-the-fonts-on-your-android-phone-32078/
        File filesDirectory = context.getFilesDir();
        Log.i(getClass().getName(), "filesDirectory.getAbsolutePath(): " + filesDirectory.getAbsolutePath());
        File fontsDirectory = new File(filesDirectory, "fonts");
        Log.i(getClass().getName(), "fontsDirectory.getAbsolutePath(): " + fontsDirectory.getAbsolutePath());
        Log.i(getClass().getName(), "fontsDirectory.exists(): " + fontsDirectory.exists());
        if (!fontsDirectory.exists()) {
            fontsDirectory.mkdir();
        }
        String fontFileName = "AndikaLowerCase-Regular.ttf";
        File fontFile = new File(fontsDirectory, fontFileName);
        Log.i(getClass().getName(), "fontFile.getAbsolutePath(): " + fontFile.getAbsolutePath());
        Log.i(getClass().getName(), "fontFile.exists(): " + fontFile.exists());
        if (!fontFile.exists()) {
            try {
                // Copy font from the "assets" folder to the application's "files/fonts" folder
                InputStream inputStream = context.getAssets().open("fonts/" + fontFileName);
                OutputStream outputStream = new FileOutputStream(fontFile);
                IOUtils.copy(inputStream, outputStream);
                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                Log.e(getClass().getName(), "fontFileName: " + fontFileName, e);
            }
        }
        if (fontFile.exists()) {
            // Make /system directory writable
            try {
                runAsRoot(new String[]{"mount -o rw,remount -t ext4 /system"});
            } catch (IOException | InterruptedException e) {
                Log.e(getClass().getName(), null, e);
            }

            // Copy new font to /system/fonts/
            File fontFileInSystemDirectory = new File("/system/fonts/" + fontFileName);
            Log.i(getClass().getName(), "fontFileInSystemDirectory.exists(): " + fontFileInSystemDirectory.exists());
            if (!fontFileInSystemDirectory.exists()) {
                try {
                    runAsRoot(new String[]{"cp " + fontFile.getAbsolutePath() + " /system/fonts"});
                } catch (IOException | InterruptedException e) {
                    Log.e(getClass().getName(), null, e);
                }
            }
            if (fontFileInSystemDirectory.exists()) {
                // Ensure that the copied file is readable
                try {
                    runAsRoot(new String[]{"chmod 644 " + fontFileInSystemDirectory.getAbsolutePath()});
                } catch (IOException | InterruptedException e) {
                    Log.e(getClass().getName(), null, e);
                }

                // Backup default font
                File backupBold = new File("/system/fonts/Roboto-Bold.ttf.backup");
                Log.i(getClass().getName(), "backupBold.exists(): " + backupBold.exists());
                if (!backupBold.exists()) {
                    try {
                        runAsRoot(new String[]{"cp /system/fonts/Roboto-Bold.ttf /system/fonts/Roboto-Bold.ttf.backup"});
                    } catch (IOException | InterruptedException e) {
                        Log.e(getClass().getName(), null, e);
                    }
                }
                File backupItalic = new File("/system/fonts/Roboto-Italic.ttf.backup");
                Log.i(getClass().getName(), "backupItalic.exists(): " + backupItalic.exists());
                if (!backupItalic.exists()) {
                    try {
                        runAsRoot(new String[]{"cp /system/fonts/Roboto-Italic.ttf /system/fonts/Roboto-Italic.ttf.backup"});
                    } catch (IOException | InterruptedException e) {
                        Log.e(getClass().getName(), null, e);
                    }
                }
                File backupLight = new File("/system/fonts/Roboto-Light.ttf.backup");
                Log.i(getClass().getName(), "backupLight.exists(): " + backupLight.exists());
                if (!backupLight.exists()) {
                    try {
                        runAsRoot(new String[]{"cp /system/fonts/Roboto-Light.ttf /system/fonts/Roboto-Light.ttf.backup"});
                    } catch (IOException | InterruptedException e) {
                        Log.e(getClass().getName(), null, e);
                    }
                }
                File backupMedium = new File("/system/fonts/Roboto-Medium.ttf.backup");
                Log.i(getClass().getName(), "backupMedium.exists(): " + backupMedium.exists());
                if (!backupMedium.exists()) {
                    try {
                        runAsRoot(new String[]{"cp /system/fonts/Roboto-Medium.ttf /system/fonts/Roboto-Medium.ttf.backup"});
                    } catch (IOException | InterruptedException e) {
                        Log.e(getClass().getName(), null, e);
                    }
                }
                File backupRegular = new File("/system/fonts/Roboto-Regular.ttf.backup");
                Log.i(getClass().getName(), "backupRegular.exists(): " + backupRegular.exists());
                if (!backupRegular.exists()) {
                    try {
                        runAsRoot(new String[]{"cp /system/fonts/Roboto-Regular.ttf /system/fonts/Roboto-Regular.ttf.backup"});
                    } catch (IOException | InterruptedException e) {
                        Log.e(getClass().getName(), null, e);
                    }
                }
                File backupThin = new File("/system/fonts/Roboto-Thin.ttf.backup");
                Log.i(getClass().getName(), "backupThin.exists(): " + backupThin.exists());
                if (!backupThin.exists()) {
                    try {
                        runAsRoot(new String[]{"cp /system/fonts/Roboto-Thin.ttf /system/fonts/Roboto-Thin.ttf.backup"});
                    } catch (IOException | InterruptedException e) {
                        Log.e(getClass().getName(), null, e);
                    }
                }

                // Replace default font
                if (backupBold.exists()
                        && backupItalic.exists()
                        && backupLight.exists()
                        && backupMedium.exists()
                        && backupRegular.exists()
                        && backupThin.exists()) {
                    // TODO: skip this step if the replacement has been done previously
                    try {
                        runAsRoot(new String[]{
                                "cp /system/fonts/" + fontFileName + " /system/fonts/Roboto-Bold.ttf",
                                "cp /system/fonts/" + fontFileName + " /system/fonts/Roboto-Italic.ttf",
                                "cp /system/fonts/" + fontFileName + " /system/fonts/Roboto-Light.ttf",
                                "cp /system/fonts/" + fontFileName + " /system/fonts/Roboto-Medium.ttf",
                                "cp /system/fonts/" + fontFileName + " /system/fonts/Roboto-Regular.ttf",
                                "cp /system/fonts/" + fontFileName + " /system/fonts/Roboto-Thin.ttf"
                        });
                        isDefaultFontReplaced = true;
                    } catch (IOException | InterruptedException e) {
                        Log.e(getClass().getName(), null, e);
                    }
                }
            }
        }



        // The following is based on the document "Disable Unnecessary Apps"
        try {
            runAsRoot(new String[]{
                    "pm disable com.android.browser",
                    "pm disable org.cyanogenmod.gello.browser",
                    "pm disable com.android.calendar",
                    "pm disable com.android.providers.calendar",
                    "pm disable com.android.calculator2",
                    "pm disable com.android.contacts",
                    "pm disable com.android.providers.downloads",
                    "pm disable com.android.providers.downloads.ui",
                    "pm disable com.android.email",
                    "pm disable com.android.nfc",
                    "pm disable org.cyanogenmod.screencast",

                    "am force-stop com.android.providers.telephony",

                    "am force-stop com.android.smspush",
                    "pm disable com.android.smspush",

                    "am force-stop com.android.exchange",
                    "pm disable com.android.exchange",

                    "am force-stop com.android.server.telecom",

                    "am force-stop com.android.dragonkeyboard",

                    "am force-stop com.android.printspooler",
                    "pm disable com.android.printspooler",

                    "am force-stop org.cyanogenmod.setupwizard",

                    "am force-stop com.android.providers.userdictionary",
                    "pm disable com.android.providers.userdictionary",

                    "am force-stop com.android.vpndialogs",

                    "am force-stop org.cyanogenmod.weather.provider",
                    "am force-stop org.cyanogenmod.weatherservice",
                    "pm disable org.cyanogenmod.weatherservice",

                    "am force-stop com.android.mms.service"
            });
        } catch (IOException | InterruptedException e) {
            Log.e(getClass().getName(), null, e);
        }

        if ((availableLiteracySkills != null) || (availableNumeracySkills != null)) {
            // Update Appstore application
            Intent updateAppstoreIntent = new Intent();
            updateAppstoreIntent.setPackage("org.literacyapp.appstore");
            updateAppstoreIntent.setAction("literacyapp.intent.action.STUDENT_UPDATED");
            if (availableLiteracySkills != null) {
                updateAppstoreIntent.putStringArrayListExtra("availableLiteracySkills", availableLiteracySkills);
            }
            if (availableNumeracySkills != null) {
                updateAppstoreIntent.putStringArrayListExtra("availableNumeracySkills", availableNumeracySkills);
            }
            Log.i(getClass().getName(), "Sending broadcast to " + updateAppstoreIntent.getPackage());
            context.sendBroadcast(updateAppstoreIntent);
        }



        // Install custom boot animation
        String zipFileName = "bootanimation_literacyapp.zip";
        File zipFile = new File(filesDirectory, zipFileName);
        Log.i(getClass().getName(), "zipFile.getAbsolutePath(): " + zipFile.getAbsolutePath());
        Log.i(getClass().getName(), "zipFile.exists(): " + zipFile.exists());
        if (!zipFile.exists()) {
            try {
                // Copy ZIP file from the "assets" folder to the application's "files" folder
                InputStream inputStream = context.getAssets().open(zipFileName);
                OutputStream outputStream = new FileOutputStream(zipFile);
                IOUtils.copy(inputStream, outputStream);
                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                Log.e(getClass().getName(), "zipFileName: " + zipFileName, e);
            }
        }
        if (zipFile.exists()) {
            // Make /system directory writable
            try {
                runAsRoot(new String[]{"mount -o rw,remount -t ext4 /system"});
            } catch (IOException | InterruptedException e) {
                Log.e(getClass().getName(), null, e);
            }

            // Copy new animation to /system/media/
            File zipFileInMediaDirectory = new File("/system/media/" + zipFileName);
            Log.i(getClass().getName(), "zipFileInMediaDirectory.exists(): " + zipFileInMediaDirectory.exists());
            if (!zipFileInMediaDirectory.exists()) {
                try {
                    runAsRoot(new String[]{"cp " + zipFile.getAbsolutePath() + " /system/media"});
                } catch (IOException | InterruptedException e) {
                    Log.e(getClass().getName(), null, e);
                }
            }
            if (zipFileInMediaDirectory.exists()) {
                // Ensure that the copied file is readable
                try {
                    runAsRoot(new String[]{"chmod 644 " + zipFileInMediaDirectory.getAbsolutePath()});
                } catch (IOException | InterruptedException e) {
                    Log.e(getClass().getName(), null, e);
                }

                // Backup default animation
                File animationBackup = new File("/system/media/bootanimation.zip.backup");
                Log.i(getClass().getName(), "animationBackup.exists(): " + animationBackup.exists());
                if (!animationBackup.exists()) {
                    try {
                        runAsRoot(new String[]{"cp /system/media/bootanimation.zip /system/media/bootanimation.zip.backup"});
                    } catch (IOException | InterruptedException e) {
                        Log.e(getClass().getName(), null, e);
                    }
                }

                // Replace default animation
                if (animationBackup.exists()) {
                    // TODO: skip this step if the replacement has been done previously
                    try {
                        runAsRoot(new String[]{
                                "cp /system/media/" + zipFileName + " /system/media/bootanimation.zip",
                        });
                    } catch (IOException | InterruptedException e) {
                        Log.e(getClass().getName(), null, e);
                    }
                }
            }
        }



        // If LCD density or font was changed, reboot device
        // TODO: check if LCD density was changed
        if (isDensityChanged /**|| isDefaultFontReplaced*/) {
            // TODO: do not reboot if other apps are still working (e.g. if the Appstore is still downloading APK files)
//            try {
//                runAsRoot(new String[]{"reboot"});
//            } catch (IOException | InterruptedException e) {
//                Log.e(getClass().getName(), null, e);
//            }
        }
    }

    private void runAsRoot(String[] commands) throws IOException, InterruptedException {
        Log.i(getClass().getName(), "runAsRoot");

        Process process = Runtime.getRuntime().exec("su");

        DataOutputStream dataOutputStream = new DataOutputStream(process.getOutputStream());
        for (String command : commands) {
            Log.i(getClass().getName(), "command: " + command);
            dataOutputStream.writeBytes(command + "\n");
        }
        dataOutputStream.writeBytes("exit\n");
        dataOutputStream.flush();

        process.waitFor();
        int exitValue = process.exitValue();
        Log.i(getClass().getName(), "exitValue: " + exitValue);

        InputStream inputStreamSuccess = process.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStreamSuccess));
        String successMessage = bufferedReader.readLine();
        Log.i(getClass().getName(), "successMessage: " + successMessage);

        InputStream inputStreamError = process.getErrorStream();
        bufferedReader = new BufferedReader(new InputStreamReader(inputStreamError));
        String errorMessage = bufferedReader.readLine();
        if (TextUtils.isEmpty(errorMessage)) {
            Log.i(getClass().getName(), "errorMessage: " + errorMessage);
        } else {
            Log.e(getClass().getName(), "errorMessage: " + errorMessage);
        }
    }
}
