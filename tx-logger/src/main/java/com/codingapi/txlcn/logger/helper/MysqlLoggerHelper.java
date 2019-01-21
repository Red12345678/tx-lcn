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
package com.codingapi.txlcn.logger.helper;

import com.codingapi.txlcn.logger.db.LogDbHelper;
import com.codingapi.txlcn.logger.db.LogDbProperties;
import com.codingapi.txlcn.logger.db.TxLog;
import com.codingapi.txlcn.logger.exception.NotEnableLogException;
import com.codingapi.txlcn.logger.exception.TxLoggerException;
import com.codingapi.txlcn.logger.model.*;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.GenerousBeanProcessor;
import org.apache.commons.dbutils.RowProcessor;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * Company: CodingApi
 * Date: 2018/12/26
 *
 * @author codingapi
 */
public class MysqlLoggerHelper implements TxLcnLogDbHelper {

    /**
     * 当 开启enable时才能获取到.
     */
    @Autowired(required = false)
    private LogDbHelper dbHelper;

    @Autowired
    private LogDbProperties logDbProperties;

    private RowProcessor processor = new BasicRowProcessor(new GenerousBeanProcessor());


    @Override
    public void init() throws Exception {
        if (logDbProperties.isEnabled()) {
            String sql = "CREATE TABLE IF NOT EXISTS `t_logger`  (\n" +
                    "  `id` bigint(20) NOT NULL AUTO_INCREMENT,\n" +
                    "  `group_id` varchar(50)  NOT NULL ,\n" +
                    "\t`unit_id` varchar(50)  NOT NULL ,\n" +
                    "\t`tag` varchar(50)  NOT NULL ,\n" +
                    "\t`content` varchar(1024)  NOT NULL ,\n" +
                    "  `create_time` varchar(30) NOT NULL,\n" +
                    "  `app_name` varchar(50) NOT NULL,\n" +
                    "  PRIMARY KEY (`id`) USING BTREE\n" +
                    ") ";
            dbHelper.update(sql);
        }

    }


    @Override
    public int insert(TxLog txLoggerInfo) {
        if (logDbProperties.isEnabled()) {
            String sql = "insert into t_logger(group_id,unit_id,tag,content,create_time,app_name) values(?,?,?,?,?,?)";
            return dbHelper.update(sql, txLoggerInfo.getGroupId(), txLoggerInfo.getUnitId(), txLoggerInfo.getTag(), txLoggerInfo.getContent(), txLoggerInfo.getCreateTime(), txLoggerInfo.getAppName());
        } else {
            throw new NotEnableLogException("not enable logger");
        }
    }

    /**
     * 分页获取记录
     *
     * @param left      分页开始
     * @param right     分页结束
     * @param timeOrder 时间排序SQL
     * @return 结果集
     */
    @Override
    public List<TxLog> findByLimit(int left, int right, int timeOrder) {
        if (logDbProperties.isEnabled()) {
            String sql = "select * from t_logger " + timeOrderSql(timeOrder) + " limit " + left + ", " + right;
            return dbHelper.query(sql, new BeanListHandler<>(TxLog.class, processor));
        } else {
            throw new NotEnableLogException("not enable logger");
        }
    }


    /**
     * GroupID 和 Tag 查询
     *
     * @param left      分页左侧
     * @param right     分页右侧
     * @param groupId   groupId
     * @param tag       标签
     * @param timeOrder timeOrder
     * @return 数据集
     */
    @Override
    public List<TxLog> findByGroupAndTag(int left, int right, String groupId, String tag, int timeOrder) {
        if (logDbProperties.isEnabled()) {
            String sql = "select * from t_logger where group_id=? and tag=? " + timeOrderSql(timeOrder) + " limit "
                    + left + ", " + right;
            return dbHelper.query(sql, new BeanListHandler<>(TxLog.class, processor), groupId, tag);
        } else {
            throw new NotEnableLogException("not enable logger");
        }
    }


    /**
     * ag 查询
     *
     * @param left      分页左侧
     * @param right     分页右侧
     * @param tag       标签
     * @param timeOrder timeOrder
     * @return 数据集
     */
    @Override
    public List<TxLog> findByTag(int left, int right, String tag, int timeOrder) {
        if (logDbProperties.isEnabled()) {
            String sql = "select * from t_logger where tag =? " + timeOrderSql(timeOrder) + " limit " + left + ", " + right;
            return dbHelper.query(sql, new BeanListHandler<>(TxLog.class, processor), tag);
        } else {
            throw new NotEnableLogException("not enable logger");
        }
    }


    /**
     * GroupId 查询
     *
     * @param left      分页左侧
     * @param right     分页右侧
     * @param groupId   标签
     * @param timeOrder timeOrder
     * @return 数据集
     */
    @Override
    public List<TxLog> findByGroupId(int left, int right, String groupId, int timeOrder) {
        if (logDbProperties.isEnabled()) {
            String sql = "select * from t_logger where group_id=? " + timeOrderSql(timeOrder) + " limit " + left + ", " + right;
            return dbHelper.query(sql, new BeanListHandler<>(TxLog.class, processor), groupId);
        } else {
            throw new NotEnableLogException("not enable logger");
        }
    }


    /**
     * 按筛选条件获取记录数
     *
     * @param where  where条件部分
     * @param params 参数
     * @return 总共记录数
     */
    private long total(String where, Object... params) {
        if (logDbProperties.isEnabled()) {
            return dbHelper.query("select count(*) from t_logger where " + where, new ScalarHandler<>(), params);
        } else {
            throw new NotEnableLogException("not enable logger");
        }
    }

    /**
     * 时间排序SQL
     *
     * @param timeOrder 排序方式
     * @return orderSql
     */
    private String timeOrderSql(int timeOrder) {
        return "order by create_time " + (timeOrder == 1 ? "asc" : "desc");
    }

    /**
     * 分页获取记录所有记录数
     *
     * @return 总数
     */
    @Override
    public long findByLimitTotal() {
        return total("1=1");
    }

    /**
     * GroupId 和 Tag 查询记录数
     *
     * @param groupId groupId
     * @param tag     标示
     * @return 数量
     */
    @Override
    public long findByGroupAndTagTotal(String groupId, String tag) {
        return total("group_id=? and tag=?", groupId, tag);
    }

    /**
     * Tag 查询记录数
     *
     * @param tag 标示
     * @return 数量
     */
    @Override
    public long findByTagTotal(String tag) {
        return total("tag=?", tag);
    }

    /**
     * GroupId 查询记录数
     *
     * @param groupId GroupId
     * @return 总数
     */
    @Override
    public long findByGroupIdTotal(String groupId) {
        return total("group_id=?", groupId);
    }

    @Override
    public void deleteByFields(List<Field> fields) throws TxLoggerException {
        if (Objects.isNull(dbHelper)) {
            throw new TxLoggerException("系统日志被禁用");
        }
        StringBuilder sql = new StringBuilder("delete from t_logger where 1=1 and ");
        List<String> values = whereSqlAppender(sql, fields);
        dbHelper.update(sql.toString(), values.toArray(new Object[0]));
    }

    private List<String> whereSqlAppender(StringBuilder sql, List<Field> fields) {
        List<String> values = new ArrayList<>(fields.size());
        fields.forEach(field -> {
            if (field instanceof GroupId) {
                sql.append("group_id=? and ");
                values.add(((GroupId) field).getGroupId());
            } else if (field instanceof Tag) {
                sql.append("tag=? and ");
                values.add(((Tag) field).getTag());
            } else if (field instanceof StartTime) {
                sql.append("create_time > ? and ");
                values.add(((StartTime) field).getStartTime());
            } else if (field instanceof StopTime) {
                sql.append("create_time < ? and ");
                values.add(((StopTime) field).getStopTime());
            }
        });
        sql.delete(sql.length() - 4, sql.length());
        return values;
    }

    @Override
    public LogList findByLimitAndFields(int page, int limit, int timeOrder, List<Field> list) throws TxLoggerException {
        if (Objects.isNull(dbHelper)) {
            throw new TxLoggerException("系统日志被禁用");
        }
        StringBuilder countSql = new StringBuilder("select count(*) from t_logger where 1=1 and ");
        StringBuilder sql = new StringBuilder("select * from t_logger where 1=1 and ");
        List<String> values = whereSqlAppender(sql, list);
        whereSqlAppender(countSql, list);
        Object[] params = values.toArray(new Object[0]);
        long total = dbHelper.query(countSql.toString(), new ScalarHandler<>(), params);
        if (total < (page - 1) * limit) {
            page = 1;
        }
        sql.append(timeOrderSql(timeOrder)).append(" limit ").append((page - 1) * limit).append(", ").append(limit);
        List<TxLog> txLogs = dbHelper.query(sql.toString(), new BeanListHandler<>(TxLog.class, processor), params);

        LogList logList = new LogList();
        logList.setTotal(total);
        logList.setTxLogs(txLogs);
        return logList;
    }

}
