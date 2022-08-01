import java.util.Properties;
import java.io.InputStream;
import groovy.json.JsonSlurper;
import groovy.json.JsonBuilder;
import com.boomi.execution.ExecutionUtil;

/*
{"shops: [{
  "shop_additional_fields": [
	{
		"code": "billing-address1",
		"type": "STRING",
		"value": "123 Main St"
	},
	{
		"code": "full-mechanical",
		"type": "MULTIPLE_VALUES_LIST",
		"value": [
			"Electrical-Diagnostics",
			"Engine-Diagnostics",
			"Transmission"
		]
	}
  }]
}
*/

logger = ExecutionUtil.getBaseLogger();

for (int i = 0; i < dataContext.getDataCount(); i++) {
  String data = dataContext.getStream(i).text
  Properties props = dataContext.getProperties(i);

  def s20 = new JsonSlurper().parseText(data);

  for (int j = 0; j < s20.size(); j++) {
    def shop = s20.shops[j];
    def shop_additional_fields = shop.shop_additional_fields;

    for (int k = 0; k < shop_additional_fields.size(); k++) {
      def cf = shop_additional_fields[k];

      // convert the array to simple; https://community.boomi.com/s/article/json-basics-for-boomi
      if (cf.type == "MULTIPLE_VALUES_LIST") {
        if (!(cf.value instanceof String)) {
          cf.value = cf.value.join(',');
        }
		//logger.info(cf.code + " value:" +  cf.value.toString());
      }

    }
  }

  dataContext.storeStream(new ByteArrayInputStream(new JsonBuilder(shops).toString().getBytes()), props);
}