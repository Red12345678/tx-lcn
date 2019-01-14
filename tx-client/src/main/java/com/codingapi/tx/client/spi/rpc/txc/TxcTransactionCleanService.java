package com.codingapi.tx.client.spi.rpc.txc;

import com.codingapi.tx.client.spi.transaction.txc.resource.sql.def.TxcService;
import com.codingapi.tx.client.support.common.TransactionCleanService;
import com.codingapi.tx.commons.exception.TransactionClearException;
import com.codingapi.tx.logger.TxLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

/**
 * Description:
 * Date: 2018/12/13
 *
 * @author ujued
 */
@Component
@Slf4j
public class TxcTransactionCleanService implements TransactionCleanService {

    private final TxcService txcService;

    @Autowired
    public TxcTransactionCleanService(TxcService txcService) {
        this.txcService = txcService;
    }

    @Autowired
    private TxLogger txLogger;

    @Override
    public void clear(String groupId, int state, String unitId, String unitType) throws TransactionClearException {
        try {

            txLogger.trace(groupId,unitId,"txc","start clear state:"+state);

            // 若需要回滚读undo_log，进行回滚
            if (state != 1 && state != -1) {
                txcService.undo(groupId, unitId);
            }

            log.debug("清理TXC");
            txcService.cleanTxc(groupId, unitId);
        } catch (SQLException e) {
            log.error("txc > clean transaction error. {}", e.getMessage());
            throw new TransactionClearException(e.getMessage());
        }
    }
}
