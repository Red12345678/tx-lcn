package com.codingapi.tx.spi.rpc.dto;

import com.codingapi.tx.commons.exception.SerializerException;
import com.codingapi.tx.commons.util.serializer.SerializerContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * @author lorne
 * @date 2018/12/2
 * @description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class MessageDto implements Serializable {

    /**
     * 请求动作
     */
    private String action;

    /**
     * 事务组
     */
    private String groupId;

    /**
     * 请求参数
     */
    private byte[] bytes;

    public <T> T loadData(Class<T> tClass) throws SerializerException {
        return SerializerContext.getInstance().deSerialize(bytes,tClass);
    }

}
