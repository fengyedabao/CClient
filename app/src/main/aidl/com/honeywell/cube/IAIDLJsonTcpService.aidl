package com.honeywell.cube;

interface IAIDLJsonTcpService {
    void JSONPacketSend(String msgId,in byte [] jsonData, int len);
}