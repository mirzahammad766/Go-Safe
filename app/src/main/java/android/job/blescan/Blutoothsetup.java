package android.job.blescan;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

public class Blutoothsetup {
    static ParcelUuid parcelUuid;
    static BluetoothLeAdvertiser advertiser;
    private static AdvertiseCallback advertiseCallback;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void advertise(BluetoothAdapter bluetoothAdapter) {
        advertiser = bluetoothAdapter.getBluetoothLeAdvertiser();

        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_LOW)
                .setTimeout(0)
                .setConnectable(false)
                .build();

        int serviceUuid = 0xFEAA;
        byte[] serviceUuidBytes = new byte[]{
                (byte) (serviceUuid & 0xff),
                (byte) ((serviceUuid >> 8) & 0xff)};
        parcelUuid = parseUuidFrom(serviceUuidBytes);
        AdvertiseData data = new AdvertiseData.Builder()
                .setIncludeDeviceName(true)
                .addServiceUuid(parcelUuid)
                .build();

        advertiseCallback = new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                Log.i("log", "Advertising onStartSuccess");
                super.onStartSuccess(settingsInEffect);
            }

            @Override
            public void onStartFailure(int errorCode) {
                Log.e("log", "Advertising onStartFailure: " + errorCode);
                super.onStartFailure(errorCode);
            }
        };


        advertiser.startAdvertising(settings, data, advertiseCallback);

    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

    public static void stopadvertise(){ advertiser.stopAdvertising(advertiseCallback);
    }

    private static ParcelUuid parseUuidFrom(byte[] uuidBytes) {

        final int UUID_BYTES_16_BIT = 2;

        final int UUID_BYTES_32_BIT = 4;

        final int UUID_BYTES_128_BIT = 16;
        final ParcelUuid BASE_UUID =
                ParcelUuid.fromString("00000000-0000-1000-8000-00805F9B34FB");
        if (uuidBytes == null) {
            throw new IllegalArgumentException("uuidBytes cannot be null");
        }
        int length = uuidBytes.length;
        if (length != UUID_BYTES_16_BIT && length != UUID_BYTES_32_BIT &&
                length != UUID_BYTES_128_BIT) {
            throw new IllegalArgumentException("uuidBytes length invalid - " + length);
        }

        if (length == UUID_BYTES_128_BIT) {
            ByteBuffer buf = ByteBuffer.wrap(uuidBytes).order(ByteOrder.LITTLE_ENDIAN);
            long msb = buf.getLong(8);
            long lsb = buf.getLong(0);
            return new ParcelUuid(new UUID(msb, lsb));
        }

        long shortUuid;
        if (length == UUID_BYTES_16_BIT) {
            shortUuid = uuidBytes[0] & 0xFF;
            shortUuid += (uuidBytes[1] & 0xFF) << 8;
        } else {
            shortUuid = uuidBytes[0] & 0xFF ;
            shortUuid += (uuidBytes[1] & 0xFF) << 8;
            shortUuid += (uuidBytes[2] & 0xFF) << 16;
            shortUuid += (uuidBytes[3] & 0xFF) << 24;
        }
        long msb = BASE_UUID.getUuid().getMostSignificantBits() + (shortUuid << 32);
        long lsb = BASE_UUID.getUuid().getLeastSignificantBits();
        return new ParcelUuid(new UUID(msb, lsb));
    }
}

