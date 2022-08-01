import java.util.Properties;
import java.io.InputStream;
import groovy.json.JsonSlurper;
import groovy.json.JsonBuilder;
import com.boomi.execution.ExecutionUtil;

logger = ExecutionUtil.getBaseLogger();

for (int i = 0; i < dataContext.getDataCount(); i++) {
  String data = dataContext.getStream(i).text
  Properties props = dataContext.getProperties(i);

  def jsonData = new JsonSlurper().parseText(data);
  def shops = jsonData.shops;

  //logger.info("shops size:"+shops.size().toString());
  for (int j = 0; j < shops.size(); j++) {
    def shop = shops[j]
    def shop_additional_fields = shop.shop_additional_fields;
    //logger.info("shop #"+j+": shop_id is "+shop.shop_id);

    for (int k = 0; k < shop_additional_fields.size(); k++) {
      def cf = shop_additional_fields[k];

      // convert the array to simple; https://community.boomi.com/s/article/json-basics-for-boomi
      //logger.info(cf.code + " value:" + cf.value);
      if (cf.type == "MULTIPLE_VALUES_LIST") {
        if (!(cf.value instanceof String)) {
          cf.value = cf.value.join(',');
        }
        //logger.info(cf.code + " value updated:" + cf.value);
      }

    }
  }

  dataContext.storeStream(new ByteArrayInputStream(new JsonBuilder(jsonData).toString().getBytes()), props);
}