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
package com.codingapi.txlcn.client.message.init;

import com.codingapi.txlcn.client.config.TxClientConfig;
import com.codingapi.txlcn.client.message.helper.MessageCreator;
import com.codingapi.txlcn.spi.message.ClientInitCallBack;
import com.codingapi.txlcn.spi.message.RpcClient;
import com.codingapi.txlcn.spi.message.dto.MessageDto;
import com.codingapi.txlcn.spi.message.exception.RpcException;
import com.codingapi.txlcn.spi.message.params.InitClientParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Description:
 * Company: CodingApi
 * Date: 2018/12/29
 *
 * @author codingapi
 */
@Component
@Slf4j
public class TxClientClientInitCallBack implements ClientInitCallBack {

    private final RpcClient rpcClient;

    private final TxClientConfig txClientConfig;

    private ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();

    @Value("${spring.application.name}")
    private String appName;

    @Value("${server.port}")
    private Integer port;

    @Autowired
    public TxClientClientInitCallBack(RpcClient rpcClient, TxClientConfig txClientConfig) {
        this.rpcClient = rpcClient;
        this.txClientConfig = txClientConfig;
    }

    @Override
    public void connected(String remoteKey) {
        String modId = appName + ":" + port;
        log.info("TC[{}] connect TM[{}] successfully!", modId, remoteKey);
        singleThreadExecutor.submit(() -> {
            try {
                log.info("Send init message to TM", remoteKey);
                MessageDto msg = rpcClient.request(remoteKey, MessageCreator.initClient(modId));
                if (msg.getData() != null) {
                    //每一次建立连接时将会获取最新的时间
                    InitClientParams resParams = msg.loadBean(InitClientParams.class);
                    long dtxTime = resParams.getDtxTime();
                    txClientConfig.setDtxTime(dtxTime);
                    log.info("Determined dtx time {}ms.", dtxTime);
                }
            } catch (RpcException e) {
                log.error("Send init message error: {}", e.getMessage());
            }
        });
    }
}
