import java.util.Properties;
import java.io.InputStream;
import groovy.json.JsonSlurper;

for (int i = 0; i < dataContext.getDataCount(); i++) {
  InputStream is = dataContext.getStream(i);
  Properties props = dataContext.getProperties(i);

  def jsonSlurper = new JsonSlurper();
  String data = is.text;
  def jsonData = jsonSlurper.parseText(data);
  def shop = jsonData.shops[0];
  def shop_additional_fields = shop.shop_additional_fields;

  // set Dynamic Document Property for 'shop_id'; must be STRING
  props.setProperty("document.dynamic.userdefined.shop_id", shop.shop_id.toString());

  // set DDP for 'location_type' for SL12 List Locations
  for (int j = 0; j < shop_additional_fields.size(); j++) {
    def saf = shop_additional_fields[j];

    CustomField cf = new CustomField(code: saf.code, type: saf.type, value: saf.value);

    if (cf.code == 'location-type') {
      def locaction_type = '';

      if (cf.value == 'SERVICE') {
        locaction_type = 'AT_SHOP_LOCATION';
      } else if (cf.value == 'MOBILE') {
        locaction_type = 'AT_CUSTOMER_LOCATION';
      } else {
        locaction_type = 'HEADQUARTERS';
      }
      props.setProperty("document.dynamic.userdefined.location_type", locaction_type);
      
      break;
    }
  }
  
  is.reset(); //data was previouisly consumed so reset

  dataContext.storeStream(is, props);
}

class CustomField {
  String code
  String type
  String value
}