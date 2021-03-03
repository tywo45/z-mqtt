package com.zjcoding.zmqttbroker.processor.message;

import com.zjcoding.zmqttcommon.message.CommonMessage;
import com.zjcoding.zmqttstore.message.IMessageStore;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.util.AttributeKey;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * PUBREL控制包处理器
 *
 * @author ZhangJun
 * @date 21:45 2021/3/3
 */

@Component
public class PubRelProcessor {

    @Resource
    private PublishProcessor publishProcessor;

    @Resource
    private IMessageStore messageStore;

    /**
     * 处理PUBREL控制包
     *
     * @param ctx:                     ChannelHandler上下文
     * @param messageIdVariableHeader: PUBREL控制包可变头部
     * @author ZhangJun
     * @date 22:00 2021/3/3
     */
    public void processPubRel(ChannelHandlerContext ctx, MqttMessageIdVariableHeader messageIdVariableHeader) {
        String clientId = ctx.channel().attr(AttributeKey.valueOf("clientId")).toString();
        int messageId = messageIdVariableHeader.messageId();
        CommonMessage dumpMessage = messageStore.getDump(clientId, messageId);
        if (dumpMessage != null) {
            publishProcessor.forwardPublishMessages(dumpMessage.getPayloadBytes(), dumpMessage.getTopic(), dumpMessage.getQos());
        }
        messageStore.removeDump(clientId, messageId);
        // todo
    }

}
