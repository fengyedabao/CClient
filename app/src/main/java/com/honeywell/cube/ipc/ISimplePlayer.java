package com.honeywell.cube.ipc;

public interface ISimplePlayer {

    public void onPlayStart();

    public void onReceiveState(int state);
}
