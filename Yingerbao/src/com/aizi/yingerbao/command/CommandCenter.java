package com.aizi.yingerbao.command;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;

import com.aizi.yingerbao.logging.SLog;

public class CommandCenter {

    private static Map<Long, CommandSendRequest> commandSendRequests;
    private static final String TAG = "CrossAppMessageCenter";

    public static synchronized void addCallbackRequest(CommandSendRequest commandsendRequest) {
        if (commandSendRequests == null) {
            commandSendRequests = Collections.synchronizedMap(new HashMap<Long, CommandSendRequest>());
        }
        if (commandSendRequests.containsKey(commandsendRequest.getRequestId())) {
            commandSendRequests.remove(commandsendRequest).getRequestId();
        }
        commandSendRequests.put(commandsendRequest.getRequestId(), commandsendRequest);

    }

    public static synchronized void removeCallbackRequest(long requestId) {
        if (commandSendRequests.containsKey(requestId)) {
            commandSendRequests.remove(commandSendRequests.get(requestId));
        }
    }

    public static void handleIntent(Context context, Intent intent) {
        SLog.d(TAG, "receiveIntent: " + intent.toUri(0));
    }
}
