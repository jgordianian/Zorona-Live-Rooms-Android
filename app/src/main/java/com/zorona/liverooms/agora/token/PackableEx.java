package com.app.liverooms.agora.token;

public interface PackableEx extends Packable {
    void unmarshal(ByteBuf in);
}
