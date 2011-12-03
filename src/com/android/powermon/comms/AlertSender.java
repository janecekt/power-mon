package com.android.powermon.comms;

import java.util.List;

public interface AlertSender {
    void sendAlert(List<String> phoneNumbers, String message);
}
