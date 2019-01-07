package com.codingapi.tx.spi.rpc;


import lombok.extern.slf4j.Slf4j;

/**
 * @author lorne
 * @date 2018/12/2
 * @description
 */
@Slf4j
public enum LCNCmdType {

    /**
     * 事务提交
     */
    notifyUnit("notify-unit", MessageConstants.ACTION_NOTIFY_UNIT),

    /**
     * 创建事务组
     * <p>
     * 简写 cg
     */
    createGroup("create-group", MessageConstants.ACTION_CREATE_GROUP),

    /**
     * 加入事务组
     * <p>
     * 简写 cg
     */
    joinGroup("join-group", MessageConstants.ACTION_JOIN_GROUP),

    /**
     * 通知事务组
     * 简写 clg
     */
    notifyGroup("notify-group", MessageConstants.ACTION_NOTIFY_GROUP),

    /**
     * 响应事务状态
     * 间写 ats
     */
    askTransactionState("ask-transaction-state", MessageConstants.ACTION_ASK_TRANSACTION_STATE),

    /**
     * 记录补偿
     * 简写 wc
     */
    writeCompensation("write-compensation", MessageConstants.ACTION_WRITE_COMPENSATION),


    /**
     * TxManager请求连接
     * 简写 nc
     */
    notifyConnect("notify-connect", MessageConstants.ACTION_NOTIFY_CONNECT),


    /**
     * 初始化客户端
     * 简写 ic
     */
    initClient("init-client", MessageConstants.ACTION_INIT_CLIENT),

    /**
     * 获取切面日志
     * 简写 gal
     */
    getAspectLog("get-aspect-log", MessageConstants.ACTION_GET_ASPECT_LOG);

    private String code;

    private String name;

    LCNCmdType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static LCNCmdType parserCmd(String cmd) {
        log.debug("parsed cmd: {}", cmd);
        switch (cmd) {
            case MessageConstants.ACTION_CREATE_GROUP:
                return createGroup;
            case MessageConstants.ACTION_NOTIFY_GROUP:
                return notifyGroup;
            case MessageConstants.ACTION_NOTIFY_UNIT:
                return notifyUnit;
            case MessageConstants.ACTION_JOIN_GROUP:
                return joinGroup;
            case MessageConstants.ACTION_ASK_TRANSACTION_STATE:
                return askTransactionState;
            case MessageConstants.ACTION_WRITE_COMPENSATION:
                return writeCompensation;
            case MessageConstants.ACTION_NOTIFY_CONNECT:
                return notifyConnect;
            case MessageConstants.ACTION_GET_ASPECT_LOG:
                return getAspectLog;
            case MessageConstants.ACTION_INIT_CLIENT:
                return initClient;
            default:
                throw new IllegalStateException("unsupported cmd.");
        }
    }
}
