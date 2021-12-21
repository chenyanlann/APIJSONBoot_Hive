/*Copyright ©2016 TommyLemon(https://github.com/TommyLemon/APIJSON)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.*/

package apijson.demo;

import java.sql.*;
import java.util.Map;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import apijson.Log;
import apijson.RequestMethod;
import apijson.boot.DemoApplication;
import apijson.framework.APIJSONSQLExecutor;
import apijson.framework.APIJSONSQLConfig;
import apijson.orm.SQLConfig;
import com.alibaba.fastjson.JSONObject;


/**SQL 执行
 * @author Lemon
 */
public class DemoSQLExecutor extends APIJSONSQLExecutor {
	public static final String TAG = "DemoSQLExecutor";


	//  可重写以下方法，支持 Redis 等单机全局缓存或分布式缓存
	//	@Override
	//	public List<JSONObject> getCache(String sql, int type) {
	//		return super.getCache(sql, type);
	//	}
	//	@Override
	//	public synchronized void putCache(String sql, List<JSONObject> list, int type) {
	//		super.putCache(sql, list, type);
	//	}
	//	@Override
	//	public synchronized void removeCache(String sql, int type) {
	//		super.removeCache(sql, type);
	//	}

	// 适配连接池，如果这里能拿到连接池的有效 Connection，则 SQLConfig 不需要配置 dbVersion, dbUri, dbAccount, dbPassword
	@Override
	public Connection getConnection(SQLConfig config) throws Exception {
		Log.d(TAG, "getConnection  config.getDatasource() = " + config.getDatasource());
		
		Connection c = connectionMap.get(config.getDatabase());
		if (c == null || c.isClosed()) {
			try {
				DataSource ds = DemoApplication.getApplicationContext().getBean(DataSource.class);
				// 另一种方式是 DruidConfig 初始化获取到 Datasource 后给静态变量 DATA_SOURCE 赋值： ds = DruidConfig.DATA_SOURCE.getConnection();
				connectionMap.put(config.getDatabase(), ds == null ? null : ds.getConnection());
			} catch (Exception e) {
				Log.e(TAG, "getConnection   try { "
						+ "DataSource ds = DemoApplication.getApplicationContext().getBean(DataSource.class); .."
						+ "} catch (Exception e) = " + e.getMessage());
			}
		}
//		else if(config.isMySQL()){
//			Statement stmt = c.createStatement();
//			stmt.execute("set hive.support.concurrency=true");
//			stmt.execute("set hive.exec.dynamic.partition.mode=nonstrict");
//			stmt.execute("set hive.txn.manager=org.apache.hadoop.hive.ql.lockmgr.DbTxnManager");
//		}

		// 必须最后执行 super 方法，因为里面还有事务相关处理。
		// 如果这里是 return c，则会导致 增删改 多个对象时只有第一个会 commit，即只有第一个对象成功插入数据库表
		return super.getConnection(config);
	}

	@Override
	protected String getKey(SQLConfig config, ResultSet rs, ResultSetMetaData rsmd, int tablePosition, JSONObject table, int columnIndex, Map<String, JSONObject> childMap) throws Exception {
		String key = super.getKey(config, rs, rsmd, tablePosition, table, columnIndex, childMap);
		String table_name = config.getTable();
		if(APIJSONSQLConfig.TABLE_KEY_MAP.containsKey(table_name)) table_name = APIJSONSQLConfig.TABLE_KEY_MAP.get(table_name);
		String pattern = "^" + table_name + "\\." + "[a-zA-Z]+$";
		boolean isMatch = Pattern.matches(pattern, key);
		if(isMatch) key = key.split("\\.")[1];
		return key;
	}



	@Override
	public ResultSet execute(Statement statement, String sql) throws Exception {
		return super.execute(statement, sql);
	}

	@Override
	public JSONObject execute(SQLConfig config, boolean unknowType) throws Exception {
		return super.execute(config, unknowType);
	}

	@Override
	public ResultSet executeQuery(Statement statement, String sql) throws Exception {
		return super.executeQuery(statement, sql);
	}

	@Override
	public int executeUpdate(Statement statement, String sql) throws Exception {
		return super.executeUpdate(statement, sql);
	}

	@Override
	public int executeUpdate(SQLConfig config) throws Exception {
		PreparedStatement s = this.getStatement(config);
		int count = 1;
		if (config.getMethod() == RequestMethod.POST && config.getId() == null) {
			ResultSet rs = s.getGeneratedKeys();
			if (rs != null && rs.next()) {
				config.setId(rs.getLong(1));
			}
		}

		return count;
	}

	@Override
	public void begin(int transactionIsolation) throws SQLException {
		super.begin(transactionIsolation);
		this.connection.setAutoCommit(true);
	}

	@Override
	public void setTransactionIsolation(int transactionIsolation) {
		super.setTransactionIsolation(transactionIsolation);
	}

	// 取消注释支持 !key 反选字段 和 字段名映射，需要先依赖插件 https://github.com/APIJSON/apijson-column
	//	@Override
	//	protected String getKey(SQLConfig config, ResultSet rs, ResultSetMetaData rsmd, int tablePosition, JSONObject table,
	//			int columnIndex, Map<String, JSONObject> childMap) throws Exception {
	//		return ColumnUtil.compatOutputKey(super.getKey(config, rs, rsmd, tablePosition, table, columnIndex, childMap), config.getTable(), config.getMethod());
	//	}
	
	// 不需要隐藏字段这个功能时，取消注释来提升性能
	//	@Override
	//	protected boolean isHideColumn(SQLConfig config, ResultSet rs, ResultSetMetaData rsmd, int tablePosition,
	//			JSONObject table, int columnIndex, Map<String, JSONObject> childMap) throws SQLException {
	//		return false;
	//	}

}