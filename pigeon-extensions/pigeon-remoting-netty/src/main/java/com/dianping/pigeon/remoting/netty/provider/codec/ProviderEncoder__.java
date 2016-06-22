package com.dianping.pigeon.remoting.netty.provider.codec;

import com.dianping.pigeon.remoting.common.codec.SerializerFactory;
import com.dianping.pigeon.remoting.common.domain.InvocationResponse;
import com.dianping.pigeon.remoting.netty.codec.AbstractEncoder_;
import com.dianping.pigeon.remoting.netty.codec.AbstractEncoder__;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author qi.yin
 *         2016/06/21  下午3:35.
 */
public class ProviderEncoder__ extends AbstractEncoder__ {

    public Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        Object encoded = super.encode(ctx, channel, msg);
        return encoded;
    }

    @Override
    public void doFailResponse(Channel channel, InvocationResponse response) {
        Channels.write(channel, response);
    }

    @Override
    public void serialize(byte serializerType, OutputStream os, Object obj, Channel channel)
            throws IOException {
        SerializerFactory.getSerializer(serializerType).serializeResponse(os, obj);
    }

}
