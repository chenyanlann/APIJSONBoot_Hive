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

import apijson.RequestMethod;
import apijson.framework.APIJSONVerifier;
import apijson.orm.OnParseCallback;
import apijson.orm.SQLConfig;
import apijson.orm.SQLCreator;
import com.alibaba.fastjson.JSONObject;


/**权限验证器
 * @author Lemon
 */
public class DemoVerifier extends APIJSONVerifier {
	public static final String TAG = "DemoVerifier";

	public DemoVerifier() {
		super();
	}

	@Override
	public void verifyRole(String table, RequestMethod method, String role) throws Exception {
		super.verifyRole(table, method, role);
	}

	@Override
	public void verifyRepeat(String table, String key, Object value) throws Exception {
		super.verifyRepeat(table, key, value);
	}

	@Override
	public void verifyRepeat(String table, String key, Object value, long exceptId) throws Exception {
		super.verifyRepeat(table, key, value, exceptId);
	}

	@Override
	public JSONObject verifyRequest(RequestMethod method, String name, JSONObject target, JSONObject request, int maxUpdateCount, String database, String schema, SQLCreator creator) throws Exception {
		return super.verifyRequest(method, name, target, request, maxUpdateCount, database, schema, creator);
	}

	@Override
	public JSONObject verifyResponse(RequestMethod method, String name, JSONObject target, JSONObject response, String database, String schema, SQLCreator creator, OnParseCallback callback) throws Exception {
		return super.verifyResponse(method, name, target, response, database, schema, creator, callback);
	}
	// 重写方法来自定义字段名等
	//	@Override
	//	public String getVisitorIdKey(SQLConfig config) {
	//		return super.getVisitorIdKey(config);  // return "userid"; // return "uid" 等自定义的字段名
	//	}

}
