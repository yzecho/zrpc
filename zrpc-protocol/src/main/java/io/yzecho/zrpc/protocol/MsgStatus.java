package io.yzecho.zrpc.protocol;

import lombok.Getter;

/**
 * @author bc.yzecho
 */
public enum MsgStatus {

    SUCCESS(0),
    FAIL(1);

    @Getter
    private final int code;

    MsgStatus(int code) {
        this.code = code;
    }

}
