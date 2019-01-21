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
package com.codingapi.txlcn.logger.db;

import com.codingapi.txlcn.logger.TxLogger;
import com.codingapi.txlcn.logger.helper.TxLcnLogDbHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 * Company: CodingApi
 * Date: 2018/12/26
 *
 * @author codingapi
 */
@Slf4j
public class DefaultTxLogger implements TxLogger {

    private final String appId;

    private final TxLcnLogDbHelper txLoggerHelper;

    private final LogDbProperties dbProperties;

    private final ExecutorService executor;

    public DefaultTxLogger(LogDbProperties dbProperties, TxLcnLogDbHelper txLoggerHelper,
                           ConfigurableEnvironment environment, ServerProperties serverProperties) {
        this.dbProperties = dbProperties;
        this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.txLoggerHelper = txLoggerHelper;
        String name = environment.getProperty("spring.application.name");
        this.appId = (StringUtils.hasText(name) ? name : "application") + ":" + Optional.ofNullable(serverProperties.getPort()).orElse(0);

        // 等待线程池任务完成
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            executor.shutdown();
            try {
                executor.awaitTermination(10, TimeUnit.MINUTES);
            } catch (InterruptedException ignored) {
            }
        }));
    }

    private String getTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
        return format.format(new Date());
    }


    @Override
    public void trace(String groupId, String unitId, String tag, String content) {
        if (dbProperties.isEnabled()) {
            TxLog txLog = new TxLog();
            txLog.setContent(content);
            txLog.setGroupId(groupId);
            txLog.setTag(tag);
            txLog.setUnitId(Objects.isNull(unitId) ? "" : unitId);
            txLog.setAppName(appId);
            txLog.setCreateTime(getTime());
            log.debug("txLoggerInfoEvent->{}", txLog);
            this.executor.execute(() -> txLoggerHelper.insert(txLog));
        }
    }
}
