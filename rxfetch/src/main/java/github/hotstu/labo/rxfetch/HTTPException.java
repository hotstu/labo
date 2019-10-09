package github.hotstu.labo.rxfetch;

import java.io.IOException;

public  class HTTPException extends IOException {
        public int code;
        public String result;

        HTTPException(int code, String result) {
            super("code=" + code);
            this.code = code;
            this.result = result;
        }
    }