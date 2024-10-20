package com.psik22a.uas.aplikasisensorjuliamaranatha;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

public class MainActivity extends AppCompatActivity {

    private TextView batteryLevelTextView;
    private TextView batteryTempTextView;
    private TextView batteryVoltageTextView;
    private TextView batteryHealthTextView;
    private TextView batteryStatusTextView;
    private TextView batteryPlugStatusTextView;
    private TextView batteryTechnologyTextView;
    private TextView batteryUsageTextView;
    private ProgressBar batteryProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize TextViews
        batteryLevelTextView = findViewById(R.id.batteryLevelTextView);
        batteryTempTextView = findViewById(R.id.batteryTempTextView);
        batteryVoltageTextView = findViewById(R.id.batteryVoltageTextView);
        batteryHealthTextView = findViewById(R.id.batteryHealthTextView);
        batteryStatusTextView = findViewById(R.id.batteryStatusTextView);
        batteryPlugStatusTextView = findViewById(R.id.batteryPlugStatusTextView);
        batteryTechnologyTextView = findViewById(R.id.batteryTechnologyTextView);
        batteryUsageTextView = findViewById(R.id.batteryUsageTextView);
        batteryProgressBar = findViewById(R.id.batteryProgressBar);

        // Register BroadcastReceiver
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryInfoReceiver, ifilter);
    }

    private final BroadcastReceiver batteryInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get battery properties
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
            int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
            int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            int plugStatus = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            String technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);

            // Update TextViews
            batteryLevelTextView.setText("Battery Level: " + (level * 100 / scale) + "%");
            batteryTempTextView.setText("Battery Temperature: " + (temperature / 10.0) + " °C");
            batteryVoltageTextView.setText("Voltage: " + (voltage != -1 ? voltage + " mV" : "Not Available"));
            batteryHealthTextView.setText("Health: " + getBatteryHealth(health));
            batteryStatusTextView.setText("Status: " + getBatteryStatus(status));
            batteryPlugStatusTextView.setText("Plugged Status: " + getPlugStatus(plugStatus));
            batteryTechnologyTextView.setText("Technology: " + (technology != null ? technology : "Not Available"));

            // Update ProgressBar
            batteryProgressBar.setProgress((level * 100) / scale);

            // Check battery level alert
            int lowBatteryThreshold = 20; // 20%
            if (level <= lowBatteryThreshold) {
                Toast.makeText(context, "Alert: Battery level is low! Please charge your device.", Toast.LENGTH_LONG).show();
            }

            // Show battery notification
            showBatteryNotification(level);

            // Estimate battery usage
            String usageEstimation = estimateUsageTime(level, scale);
            batteryUsageTextView.setText("Estimated Usage Time: " + usageEstimation);
        }
    };

    private void showBatteryNotification(int level) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "battery_channel";

        // Create Notification Channel for Android O and above
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Battery Status", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher) // Ganti dengan ikon baterai yang sesuai
                .setContentTitle("Battery Level Alert")
                .setContentText("Current battery level is " + level + "%")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify(1, builder.build());
    }

    private String estimateUsageTime(int level, int scale) {
        int batteryPercent = (level * 100) / scale;
        int hoursRemaining = batteryPercent / 10; // Misal konsumsi 10% per jam
        return hoursRemaining > 0 ? hoursRemaining + " hours remaining" : "Charging or not in use";
    }

    private String getBatteryHealth(int health) {
        switch (health) {
            case BatteryManager.BATTERY_HEALTH_GOOD:
                return "Good";
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                return "Overheat";
            case BatteryManager.BATTERY_HEALTH_DEAD:
                return "Dead";
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                return "Over Voltage";
            case BatteryManager.BATTERY_HEALTH_UNKNOWN:
            default:
                return "Unknown";
        }
    }

    private String getBatteryStatus(int status) {
        switch (status) {
            case BatteryManager.BATTERY_STATUS_CHARGING:
                return "Charging";
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                return "Discharging";
            case BatteryManager.BATTERY_STATUS_FULL:
                return "Full";
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                return "Not Charging";
            case BatteryManager.BATTERY_STATUS_UNKNOWN:
            default:
                return "Unknown";
        }
    }

    private String getPlugStatus(int status) {
        switch (status) {
            case BatteryManager.BATTERY_PLUGGED_AC:
                return "AC Charger";
            case BatteryManager.BATTERY_PLUGGED_USB:
                return "USB Charger";
            case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                return "Wireless Charger";
            case BatteryManager.BATTERY_PLUGGED_DOCK:
            default:
                return "Not Plugged";
        }
    }
}
