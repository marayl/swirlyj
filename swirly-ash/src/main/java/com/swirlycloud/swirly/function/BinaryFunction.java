/*******************************************************************************
 * Copyright (C) 2013, 2015 Swirly Cloud Limited. All rights reserved.
 *******************************************************************************/
package com.swirlycloud.swirly.function;

public interface BinaryFunction<R, T, U> {
    R call(T lhs, U rhs);
}
