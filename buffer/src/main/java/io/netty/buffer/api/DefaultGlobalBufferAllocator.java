/*
 * Copyright 2021 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License, version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.netty.buffer.api;

import io.netty.buffer.api.internal.UncloseableBufferAllocator;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.util.Locale;

import static io.netty.buffer.api.BufferAllocator.offHeapPooled;
import static io.netty.buffer.api.BufferAllocator.offHeapUnpooled;
import static io.netty.buffer.api.BufferAllocator.onHeapPooled;
import static io.netty.buffer.api.BufferAllocator.onHeapUnpooled;
import static io.netty.util.internal.PlatformDependent.directBufferPreferred;

/**
 * A {@link BufferAllocator} which is {@link #close() disposed} when the {@link Runtime} is shutdown.
 */
public final class DefaultGlobalBufferAllocator extends UncloseableBufferAllocator {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(DefaultGlobalBufferAllocator.class);
    public static final BufferAllocator DEFAULT_GLOBAL_BUFFER_ALLOCATOR;

    static {
        String allocType = SystemPropertyUtil.get(
                "io.netty.allocator.type", PlatformDependent.isAndroid() ? "unpooled" : "pooled");
        allocType = allocType.toLowerCase(Locale.US).trim();

        BufferAllocator alloc;
        if ("unpooled".equals(allocType)) {
            alloc = directBufferPreferred() ? offHeapUnpooled() : onHeapUnpooled();
            logger.debug("-Dio.netty.allocator.type: {}", allocType);
        } else if ("pooled".equals(allocType)) {
            alloc = directBufferPreferred() ? offHeapPooled() : onHeapPooled();
            logger.debug("-Dio.netty.allocator.type: {}", allocType);
        } else {
            alloc = directBufferPreferred() ? offHeapPooled() : onHeapPooled();
            logger.debug("-Dio.netty.allocator.type: pooled (unknown: {})", allocType);
        }
        DEFAULT_GLOBAL_BUFFER_ALLOCATOR = new DefaultGlobalBufferAllocator(alloc);
    }

    private DefaultGlobalBufferAllocator(BufferAllocator delegate) {
        super(delegate, "Global buffer allocator can not be closed explicitly.");
    }
}
