package com.reactnativeturbodeviceinfo;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.module.annotations.ReactModule;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import androidx.annotation.NonNull;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.Promise;
import com.reactnativeturbodeviceinfo.NativeTurboDeviceInfoSpec;
import static android.os.BatteryManager.BATTERY_STATUS_CHARGING;
import static android.os.BatteryManager.BATTERY_STATUS_FULL;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Collections;
import java.util.List;
import java.util.Locale;



@ReactModule(name = TurboDeviceInfoModule.NAME)
public class TurboDeviceInfoModule extends NativeTurboDeviceInfoSpec {
    public static final String NAME = "TurboDeviceInfo";
  private static String BATTERY_STATE = "batteryState";
  private static String BATTERY_LEVEL= "batteryLevel";
  private static String LOW_POWER_MODE = "lowPowerMode";


    public TurboDeviceInfoModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

     @Override
    public String getIpAddress() {

        try {
            return
                    InetAddress.getByAddress(
                            ByteBuffer
                                    .allocate(4)
                                    .order(ByteOrder.LITTLE_ENDIAN)
                                    .putInt(getWifiInfo().getIpAddress())
                                    .array())
                            .getHostAddress();
        } catch (Exception e) {
            return "unknown";
        }

    }
  @SuppressWarnings("MissingPermission")
    private WifiInfo getWifiInfo() {
        WifiManager manager = (WifiManager) getReactApplicationContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (manager != null) {
            return manager.getConnectionInfo();
        }
        return null;
    }

    @SuppressLint({"HardwareIds", "MissingPermission"})
    @Override
    public String getMacAddress() {

        WifiInfo wifiInfo = getWifiInfo();
        String macAddress = "";
        if (wifiInfo != null) {
            macAddress = wifiInfo.getMacAddress();
        }

        String permission = "android.permission.INTERNET";
        int res = getReactApplicationContext().checkCallingOrSelfPermission(permission);

        if (res == PackageManager.PERMISSION_GRANTED) {
            try {
                List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
                for (NetworkInterface nif : all) {
                    if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                    byte[] macBytes = nif.getHardwareAddress();
                    if (macBytes == null) {
                        macAddress = "";
                    } else {

                        StringBuilder res1 = new StringBuilder();
                        for (byte b : macBytes) {
                            res1.append(String.format("%02X:",b));
                        }

                        if (res1.length() > 0) {
                            res1.deleteCharAt(res1.length() - 1);
                        }

                        macAddress = res1.toString();
                    }
                }
            } catch (Exception ex) {
                // do nothing
            }
        }

        return macAddress;
    }
  private BigInteger getDirTotalCapacity(StatFs dir) {
    boolean intApiDeprecated = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
    long blockCount = intApiDeprecated ? dir.getBlockCountLong() : dir.getBlockCount();
    long blockSize = intApiDeprecated ? dir.getBlockSizeLong() : dir.getBlockSize();
    return BigInteger.valueOf(blockCount).multiply(BigInteger.valueOf(blockSize));
  }

  @Override
  public double getTotalDiskCapacity() {
    try {
      StatFs rootDir = new StatFs(Environment.getRootDirectory().getAbsolutePath());
      StatFs dataDir = new StatFs(Environment.getDataDirectory().getAbsolutePath());

      BigInteger rootDirCapacity = getDirTotalCapacity(rootDir);
      BigInteger dataDirCapacity = getDirTotalCapacity(dataDir);

      return rootDirCapacity.add(dataDirCapacity).doubleValue();
    } catch (Exception e) {
      return -1;
    }
  }


  private long getTotalAvailableBlocks(StatFs dir, Boolean intApiDeprecated) {
    return (intApiDeprecated ? dir.getAvailableBlocksLong() : dir.getAvailableBlocks());
  }

  private long getBlockSize(StatFs dir, Boolean intApiDeprecated) {
    return (intApiDeprecated ? dir.getBlockSizeLong() : dir.getBlockSize());
  }

  @Override
  public double getFreeDiskStorage() {
    try {
      StatFs rootDir = new StatFs(Environment.getRootDirectory().getAbsolutePath());
      StatFs dataDir = new StatFs(Environment.getDataDirectory().getAbsolutePath());

      Boolean intApiDeprecated = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
      long rootAvailableBlocks = getTotalAvailableBlocks(rootDir, intApiDeprecated);
      long rootBlockSize = getBlockSize(rootDir, intApiDeprecated);
      double rootFree = BigInteger.valueOf(rootAvailableBlocks).multiply(BigInteger.valueOf(rootBlockSize)).doubleValue();

      long dataAvailableBlocks = getTotalAvailableBlocks(dataDir, intApiDeprecated);
      long dataBlockSize = getBlockSize(dataDir, intApiDeprecated);
      double dataFree = BigInteger.valueOf(dataAvailableBlocks).multiply(BigInteger.valueOf(dataBlockSize)).doubleValue();

      return rootFree + dataFree;
    } catch (Exception e) {
      return -1;
    }
  }

  @Override
  public String getCarrierSync() {
    TelephonyManager telMgr = (TelephonyManager) getReactApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
    if (telMgr != null) {
      return telMgr.getNetworkOperatorName();
    } else {
      System.err.println("Unable to get network operator name. TelephonyManager was null");
      return "unknown";
    }
  }

  @Override
  public boolean isBatteryCharging() {
    IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
    Intent batteryStatus = getReactApplicationContext().registerReceiver(null, ifilter);
    int status = 0;
    if (batteryStatus != null) {
      status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
    }
    return status == BATTERY_STATUS_CHARGING;
  }

  private WritableMap getPowerStateFromIntent (Intent intent) {
    if(intent == null) {
      return null;
    }

    int batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
    int batteryScale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
    int isPlugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
    int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

    float batteryPercentage = batteryLevel / (float)batteryScale;

    String batteryState = "unknown";

    if(isPlugged == 0) {
      batteryState = "unplugged";
    } else if(status == BATTERY_STATUS_CHARGING) {
      batteryState = "charging";
    } else if(status == BATTERY_STATUS_FULL) {
      batteryState = "full";
    }

    PowerManager powerManager = (PowerManager)getReactApplicationContext().getSystemService(Context.POWER_SERVICE);
    boolean powerSaveMode = false;
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
      powerSaveMode = powerManager.isPowerSaveMode();
    }

    WritableMap powerState = Arguments.createMap();
    powerState.putString(BATTERY_STATE, batteryState);
    powerState.putDouble(BATTERY_LEVEL, batteryPercentage);
    powerState.putBoolean(LOW_POWER_MODE, powerSaveMode);

    return powerState;
  }

  @Override
  public double getBatteryLevel() {
    Intent intent = getReactApplicationContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    WritableMap powerState = getPowerStateFromIntent(intent);

    if(powerState == null) {
      return 0;
    }

    return powerState.getDouble(BATTERY_LEVEL);
  }

  @Override
  public boolean isAirplaneMode() {
    boolean isAirplaneMode;
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
      isAirplaneMode = Settings.System.getInt(getReactApplicationContext().getContentResolver(),Settings.System.AIRPLANE_MODE_ON, 0) != 0;
    } else {
      isAirplaneMode = Settings.Global.getInt(getReactApplicationContext().getContentResolver(),Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
    }
    return isAirplaneMode;
  }

  @Override
  public boolean isHeadphonesConnected() {
    AudioManager
      audioManager = (AudioManager)getReactApplicationContext().getSystemService(Context.AUDIO_SERVICE);
    return audioManager.isWiredHeadsetOn() || audioManager.isBluetoothA2dpOn();
  }

  @Override
    public boolean isEmulator() {

     return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.toLowerCase(Locale.ROOT).contains("droid4x")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu")
                || Build.HARDWARE.contains("vbox86")
                || Build.PRODUCT.contains("sdk")
                || Build.PRODUCT.contains("google_sdk")
                || Build.PRODUCT.contains("sdk_google")
                || Build.PRODUCT.contains("sdk_x86")
                || Build.PRODUCT.contains("vbox86p")
                || Build.PRODUCT.contains("emulator")
                || Build.PRODUCT.contains("simulator")
                || Build.BOARD.toLowerCase(Locale.ROOT).contains("nox")
                || Build.BOOTLOADER.toLowerCase(Locale.ROOT).contains("nox")
                || Build.HARDWARE.toLowerCase(Locale.ROOT).contains("nox")
                || Build.PRODUCT.toLowerCase(Locale.ROOT).contains("nox")
                || Build.SERIAL.toLowerCase(Locale.ROOT).contains("nox")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"));
    }

    @Override
    @NonNull
    public String getName() {
        return NAME;
    }
}
