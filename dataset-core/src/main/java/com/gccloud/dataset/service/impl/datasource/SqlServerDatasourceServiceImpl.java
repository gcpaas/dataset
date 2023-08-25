package com.gccloud.dataset.service.impl.datasource;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerStatementParser;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gccloud.common.vo.PageVO;
import com.gccloud.dataset.constant.DatasetConstant;
import com.gccloud.dataset.dao.DatasourceDao;
import com.gccloud.dataset.entity.DatasourceEntity;
import com.gccloud.dataset.service.IBaseDatasourceService;
import com.gccloud.dataset.utils.DBUtils;
import com.gccloud.dataset.vo.DataVO;
import com.gccloud.dataset.vo.DbDataVO;
import com.gccloud.dataset.vo.FieldInfoVO;
import com.gccloud.dataset.vo.TableInfoVO;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * sqlserver数据源服务实现类
 */
@Slf4j
@Service(DatasetConstant.DatasourceType.SQLSERVER)
public class SqlServerDatasourceServiceImpl extends ServiceImpl<DatasourceDao, DatasourceEntity> implements IBaseDatasourceService {
    @Override
    public DataVO executeSql(DatasourceEntity datasource, String sql) {
        DbDataVO dbDataVO = DBUtils.getSqlValue(sql, datasource);
        return new DataVO(dbDataVO.getData(), dbDataVO.getStructure());
    }

    @Override
    public DataVO executeSqlPage(DatasourceEntity datasource, String sql, Integer current, Integer size) {
        if (sql.endsWith(";")) {
            sql = sql.substring(0, sql.length() - 1);
        }
        // 组装计算总条数的sql
        String countSql = "SELECT COUNT(1) AS COUNT FROM (" + sql + ") AS t";
        log.info("数据集数据详情计算总条数 sql语句：{}", countSql);
        DbDataVO countData = DBUtils.getSqlValue(countSql, datasource);
        Object count = countData.getData().get(0).get("COUNT");
        int total = Integer.parseInt(count.toString());
        // 组装分页sql
        int start = (current - 1) * size;

        // 查询表的字段
        String[] forms = sql.split("FROM");
        // 使用druid的sql解析，获取查询的数据库名称
        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        SQLStatement sqlStatement = parser.parseStatement();
        SQLServerSchemaStatVisitor visitor = new SQLServerSchemaStatVisitor();
        sqlStatement.accept(visitor);
        Map<TableStat.Name, TableStat> tables = visitor.getTables();
        List<String> allTableName = Lists.newArrayList();
        for (TableStat.Name t : tables.keySet()) {
            allTableName.add(t.getName());
        }

        log.info("查询的表名称:{}", allTableName.get(0));
        // 字段需要从数据库中查询
        List<FieldInfoVO> tableColumnList = this.getTableColumnList(datasource, allTableName.get(0));
        String columnName = tableColumnList.get(0).getColumnName();
        String pageSql = "select * from (" + sql + ") as t order by " + columnName + " offset "+ start +" rows fetch next " + 10 + " rows only";
        log.info("数据集数据详情分页 sql语句：{}", pageSql);
        DbDataVO pageData = DBUtils.getSqlValue(pageSql, datasource);
        PageVO<Map<String, Object>> page = new PageVO<>();
        page.setCurrent(current);
        page.setSize(size);
        page.setTotalCount(total);
        page.setTotalPage((total + size - 1) / size);
        page.setList(pageData.getData());
        return new DataVO(page, pageData.getStructure());
    }

    @Override
    public List<TableInfoVO> getTableList(DatasourceEntity datasource) {
        String sql = "select * from sysobjects where type='U'";
        DbDataVO dataVO = DBUtils.getSqlValue(sql, datasource);
        List<Map<String, Object>> data = dataVO.getData();
        List<TableInfoVO> tableList = Lists.newArrayList();
        if (CollectionUtils.isEmpty(data)) {
            return tableList;
        }
        for (Map<String, Object> map : data) {
            TableInfoVO tableInfoVO = new TableInfoVO();
            tableInfoVO.setName(String.valueOf(map.get("name")));
            tableInfoVO.setStatus(0);
            tableList.add(tableInfoVO);
        }
        return tableList;
    }

    @Override
    public List<TableInfoVO> getViewList(DatasourceEntity datasource) {
        String sql = "SELECT name FROM sys.views";
        DbDataVO dataVO = DBUtils.getSqlValue(sql, datasource);
        List<Map<String, Object>> data = dataVO.getData();
        List<TableInfoVO> tableInfoVOS = Lists.newArrayList();
        if (CollectionUtils.isEmpty(data)){
            return tableInfoVOS;
        }
        for (Map<String, Object> map : data) {
            TableInfoVO tableInfoVO = new TableInfoVO();
            tableInfoVO.setName(String.valueOf(map.get("name")));
            tableInfoVO.setStatus(0);
            tableInfoVOS.add(tableInfoVO);
        }
        return tableInfoVOS;
    }

    @Override
    public List<FieldInfoVO> getTableColumnList(DatasourceEntity datasource, String tableName) {
        String sql = "select * from information_schema.columns where table_name = '" + tableName + "'";
        DbDataVO dataVO = DBUtils.getSqlValue(sql, datasource);
        List<Map<String, Object>> data = dataVO.getData();
        List<FieldInfoVO> fieldInfoVOS = Lists.newArrayList();
        if (CollectionUtils.isEmpty(data)) {
            return fieldInfoVOS;
        }
        // 获取表中字段注释
        String columnDescSql = "select a.name  table_name,b.name  column_name, c.value  column_description from sys.tables a inner join sys.columns b on b.object_id = a.object_id left join sys.extended_properties c on c.major_id = b.object_id and c.minor_id = b.column_id where a.name = '" + tableName + "'";
        DbDataVO sqlValue = DBUtils.getSqlValue(columnDescSql, datasource);
        List<Map<String, Object>> columnDescMap = sqlValue.getData();
        List<FieldInfoVO> columnDescList = Lists.newArrayList();
        for (Map<String, Object> columnDesc : columnDescMap) {
            FieldInfoVO fieldInfoVO = new FieldInfoVO();
            fieldInfoVO.setColumnName(String.valueOf(columnDesc.get("column_name")));
            fieldInfoVO.setColumnComment(String.valueOf(columnDesc.get("column_description")));
            columnDescList.add(fieldInfoVO);
        }
        Map<String, String> collect = columnDescList.stream().collect(Collectors.toMap(FieldInfoVO::getColumnName, FieldInfoVO::getColumnComment));
        for (Map<String, Object> map : data) {
            FieldInfoVO fieldInfoVO = new FieldInfoVO();
            fieldInfoVO.setColumnName(String.valueOf(map.get("COLUMN_NAME")));
            fieldInfoVO.setColumnType(String.valueOf(map.get("DATA_TYPE")));
            fieldInfoVO.setColumnComment(collect.get(String.valueOf(map.get("COLUMN_NAME"))));
            fieldInfoVOS.add(fieldInfoVO);
        }
        return fieldInfoVOS;
    }

    @Override
    public DataVO executeProcedure(DatasourceEntity datasource, String procedure, Integer current, Integer size) {
        DbDataVO dbDataVO = DBUtils.call(procedure, datasource, current, size);
        boolean pageFlag = current != null && size != null;
        if (pageFlag) {
            return new DataVO(dbDataVO.getPageData(), dbDataVO.getStructure());
        }
        return new DataVO(dbDataVO.getData(), dbDataVO.getStructure());
    }
}
