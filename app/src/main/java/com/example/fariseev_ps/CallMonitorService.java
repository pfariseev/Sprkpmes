package com.example.fariseev_ps;

import android.net.Uri;
import android.os.Build;
import android.telecom.CallRedirectionService;
import android.telecom.PhoneAccountHandle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.Q)
public class CallMonitorService extends CallRedirectionService {
    @Override
    public void onPlaceCall(@NonNull Uri uri, @NonNull PhoneAccountHandle phoneAccountHandle, boolean b) {

    }

}
