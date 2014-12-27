/*******************************************************************************
 * Copyright (C) 2013, 2014 Swirly Cloud Limited. All rights reserved.
 *******************************************************************************/
package com.swirlycloud.twirly.exception;

import java.io.IOException;

import com.swirlycloud.twirly.function.UnaryFunction;
import com.swirlycloud.twirly.util.Jsonifiable;

public abstract class ServException extends Exception implements Jsonifiable {

    private static final long serialVersionUID = 1L;

    private final int num;

    public ServException(int num, String msg) {
        super(msg);
        this.num = num;
    }

    public ServException(int num, String msg, Throwable cause) {
        super(msg, cause);
        this.num = num;
    }

    @Override
    public final void toJson(UnaryFunction<String, String> params, Appendable out)
            throws IOException {
        out.append("{\"num\":");
        out.append(String.valueOf(num));
        out.append(",\"msg\":\"");
        out.append(getMessage());
        out.append("\"}");
    }

    public final int getNum() {
        return num;
    }
}