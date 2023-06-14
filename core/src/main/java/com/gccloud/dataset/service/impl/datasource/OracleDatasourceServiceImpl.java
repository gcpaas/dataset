package com.gccloud.dataset.service.impl.datasource;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gccloud.common.exception.GlobalException;
import com.gccloud.common.vo.PageVO;
import com.gccloud.dataset.constant.DatasetConstant;
import com.gccloud.dataset.dao.DatasourceDao;
import com.gccloud.dataset.entity.DatasourceEntity;
import com.gccloud.dataset.service.IBaseDatasourceService;
import com.gccloud.dataset.utils.DBUtils;
import com.gccloud.dataset.vo.DataVO;
import com.gccloud.dataset.vo.FieldInfoVO;
import com.gccloud.dataset.vo.TableInfoVO;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Oracle数据源服务实现类
 * @author hongyang
 * @version 1.0
 * @date 2023/6/2 11:06
 */
@Slf4j
@Service(DatasetConstant.DatasourceType.ORACLE)
public class OracleDatasourceServiceImpl extends ServiceImpl<DatasourceDao, DatasourceEntity> implements IBaseDatasourceService {

    @Override
    public DataVO executeSql(DatasourceEntity datasource, String sql) {
        return DBUtils.getSqlValue(sql, datasource);
    }

    @Override
    public DataVO executeSqlPage(DatasourceEntity datasource, String sql, Integer current, Integer size) {
        if (sql.endsWith(";")) {
            sql = sql.substring(0, sql.length() - 1);
        }
        // 组装计算总条数的sql
        String countSql = "SELECT COUNT(1) AS COUNT FROM (" + sql + ")";
        log.info("数据集数据详情计算总条数 sql语句：{}", countSql);
        DataVO countData = DBUtils.getSqlValue(countSql, datasource);
        Object count = countData.getData().get(0).get("COUNT");
        int total = Integer.parseInt(count.toString());
        // 组装分页sql
        int start = (current - 1) * size;
        int end = current * size;
        if (end > total) {
            end = total;
        }
        String pageSql = "SELECT * FROM ( SELECT TMP.*, ROWNUM ROW_ID FROM ( " + sql + " ) TMP WHERE ROWNUM <=" + end + " ) WHERE ROW_ID > " + start;
        log.info("数据集数据详情分页 sql语句：{}", pageSql);
        DataVO pageData = DBUtils.getSqlValue(pageSql, datasource);
        PageVO<Map<String, Object>> page = new PageVO<>();
        page.setCurrent(current);
        page.setSize(size);
        page.setTotalCount(total);
        page.setTotalPage((total + size - 1) / size);
        page.setList(pageData.getData());
        pageData.setPageData(page);
        pageData.setData(null);
        return pageData;
    }

    @Override
    public DataVO executeProcedure(DatasourceEntity datasource, String procedure, Integer current, Integer size) {
        // jdbc执行sql时，如果以{}包裹，表示是一个存储过程
        if (!procedure.startsWith("{") && !procedure.endsWith("}")) {
            procedure = "{" + procedure + "}";
        }
        return DBUtils.call(procedure, datasource, current, size);
    }

    @Override
    public List<TableInfoVO> getTableList(DatasourceEntity datasource) {
        String sql = " SELECT TABLE_NAME FROM USER_TABLES ORDER BY TABLE_NAME ";
        DataVO dataVO = DBUtils.getSqlValue(sql, datasource);
        List<Map<String, Object>> data = dataVO.getData();
        List<TableInfoVO> tableList = Lists.newArrayList();
        if (data == null || data.size() == 0) {
            return tableList;
        }
        for (Map<String, Object> map : data) {
            TableInfoVO tableInfoVO = new TableInfoVO();
            tableInfoVO.setName(map.get("TABLE_NAME").toString());
            tableInfoVO.setStatus(0);
            tableList.add(tableInfoVO);
        }
        return tableList;
    }

    @Override
    public List<FieldInfoVO> getTableColumnList(DatasourceEntity datasource, String tableName) {
        Connection conn = DBUtils.getConnection(datasource);
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<FieldInfoVO> columnList = Lists.newArrayList();
        try {
            String sql = "select * from " + tableName + " where ROWNUM = 0";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            // 获取字段注释
            String commentSql = "SELECT a.column_name,b.comments FROM user_tab_columns a, user_col_comments b WHERE a.table_name = '" + tableName + "' and b.table_name = '" + tableName + "' and a.column_name = b.column_name";
            PreparedStatement commentPs = conn.prepareStatement(commentSql);
            ResultSet commentRs = commentPs.executeQuery();
            Map<String, String> commentMap = new HashMap<>();
            while (commentRs.next()) {
                commentMap.put(commentRs.getString("COLUMN_NAME"), commentRs.getString("COMMENTS"));
            }
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                FieldInfoVO fieldInfoVO = new FieldInfoVO();
                fieldInfoVO.setColumnName(metaData.getColumnName(i));
                fieldInfoVO.setColumnType(metaData.getColumnTypeName(i));
                fieldInfoVO.setColumnComment(commentMap.get(metaData.getColumnName(i)));
                columnList.add(fieldInfoVO);
            }
            return columnList;
        } catch (SQLException e) {
            log.error("获取表字段信息失败，tableName：{}", tableName);
            log.error(ExceptionUtils.getStackTrace(e));
            throw new GlobalException("获取表字段信息失败");
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                log.error("数据源连接关闭异常,{}", e.getMessage());
            }
        }
    }

    @Override
    public List<TableInfoVO> getViewList(DatasourceEntity datasource) {
        String sql = "SELECT VIEW_NAME FROM USER_VIEWS ORDER BY VIEW_NAME";
        DataVO dataVO = DBUtils.getSqlValue(sql, datasource);
        List<Map<String, Object>> data = dataVO.getData();
        List<TableInfoVO> tableList = Lists.newArrayList();
        if (data == null || data.size() == 0) {
            return tableList;
        }
        for (Map<String, Object> map : data) {
            TableInfoVO tableInfoVO = new TableInfoVO();
            tableInfoVO.setName(map.get("VIEW_NAME").toString());
            tableInfoVO.setStatus(0);
            tableList.add(tableInfoVO);
        }
        return tableList;
    }

    @Override
    public List<FieldInfoVO> getViewColumnList(DatasourceEntity datasource, String viewName) {
        // 视图的字段信息和表的字段信息查询方法一致
        return getTableColumnList(datasource, viewName);
    }
}
