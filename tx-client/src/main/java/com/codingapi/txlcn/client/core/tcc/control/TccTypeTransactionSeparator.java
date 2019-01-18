/*
 * Copyright 2017-2019 CodingApi .
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.codingapi.txlcn.client.core.tcc.control;

import com.codingapi.txlcn.client.bean.TxTransactionInfo;
import com.codingapi.txlcn.client.support.CustomizableTransactionSeparator;
import com.codingapi.txlcn.client.support.TXLCNTransactionState;
import com.codingapi.txlcn.client.support.TransactionUnitTypeList;
import com.codingapi.txlcn.client.support.cache.TransactionAttachmentCache;
import com.codingapi.txlcn.commons.exception.TransactionException;
import com.codingapi.txlcn.commons.util.Transactions;
import com.codingapi.txlcn.spi.sleuth.TracerHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Description:
 * Date: 2018/12/5
 *
 * @author ujued
 */
@Slf4j
@Component("transaction_state_resolver_tcc")
public class TccTypeTransactionSeparator extends CustomizableTransactionSeparator {

    private final TransactionAttachmentCache transactionAttachmentCache;

    private final TracerHelper tracerHelper;

    @Autowired
    public TccTypeTransactionSeparator(TransactionAttachmentCache transactionAttachmentCache, TracerHelper tracerHelper) {
        this.transactionAttachmentCache = transactionAttachmentCache;
        this.tracerHelper = tracerHelper;
    }

    @Override
    public TXLCNTransactionState loadTransactionState(TxTransactionInfo txTransactionInfo) throws TransactionException {
        // 不存在GroupId时不自定义
        if (tracerHelper.getGroupId() == null) {
            return super.loadTransactionState(txTransactionInfo);
        }

        // 一个模块存在多个TCC类型的事务单元在一个事务内时不支持
        Optional<TransactionUnitTypeList> sameTransUnitTypeList =
                transactionAttachmentCache.attachment(tracerHelper.getGroupId(), TransactionUnitTypeList.class);
        if (sameTransUnitTypeList.isPresent() && sameTransUnitTypeList.get().contains(Transactions.LCN)) {
            throw new TransactionException("unsupported operate : TCC unit call TCC unit.");
        }
        return super.loadTransactionState(txTransactionInfo);
    }
}
