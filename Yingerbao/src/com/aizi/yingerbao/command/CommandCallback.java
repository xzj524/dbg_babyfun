package com.aizi.yingerbao.command;

import android.content.Intent;

public interface CommandCallback {

    public void onCallback(int errorCode, Intent intent);

}
