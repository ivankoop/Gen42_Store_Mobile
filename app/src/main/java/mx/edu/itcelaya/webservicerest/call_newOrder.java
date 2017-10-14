package mx.edu.itcelaya.webservicerest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class call_newOrder extends AppCompatActivity {

    ListView lv;
    String jsonResult;
    String url = "http://gen-42-shop.000webhostapp.com/api/products&output_format=JSON&display=full";
    String prestashop_key = "IA8WQDU181IIFLN1INFKAK9V8R3F2NYD"; //"RM6FQ7HBHV654CC82SEFUI18ZHX5FAPU";
    ArrayList<abs_car_buys> car = new ArrayList<>();
    ArrayList<abs_Products> ProductsList = new ArrayList<>();
    public static call_newOrder fa;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buy_products);
        lv = (ListView) findViewById(R.id.listProducts);
        //lv.setOnItemLongClickListener(onlonglistener);
        lv.setOnItemClickListener(listenerList);
        cargarDatos();
        fa = this;
        //Toast.makeText(getApplicationContext(), "Toca para ver detalles\nManten presionado para editar",
          //      Toast.LENGTH_SHORT).show();
    }

    public ArrayList<abs_Products> getProductsList() {
        return ProductsList;
    }

    public ArrayList<abs_car_buys> getCar() {
        return car;
    }

    public void newOrder(View v){
        TextView suma = (TextView) findViewById(R.id.total_paid);
        Intent intentEndBuy = new Intent(getApplicationContext(), end_shopping.class);
        //Bundle bn = new Bundle();
        //bn.putSerializable("productList", ProductsList);
        //bn.putSerializable("carList", car);
        //intentEndBuy.putStringArrayListExtra("productList", ProductsList);// putExtra("productList", ProductsList);
        //intentEndBuy.putExtra("bundle",bn);
        //intentEndBuy.putExtra("carList", car);
        intentEndBuy.putExtra("totalPaid", suma.getText().toString());
        startActivity(intentEndBuy);
    }

    private AdapterView.OnItemClickListener listenerList = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(!view.isSelected()) {
                showProductData((String) view.getTag());
            }
        }
    };

    public void setCar(String count, String id) {
        TextView suma = (TextView) findViewById(R.id.total_paid);
        Double sumaCant = Double.parseDouble(suma.getText().toString().replace("$",""));
        for (int i = 0; i < ProductsList.size(); i++) {
            if (ProductsList.get(i).getId_product().equals(id))
                sumaCant += Integer.parseInt(count) * Double.parseDouble(ProductsList.get(i).getPrice());
        }
        suma.setText("$"+sumaCant);
        car.add(new abs_car_buys(count, id));
    }

    private void showProductData(String id_producto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater1 = getLayoutInflater();

        builder.setView(inflater1.inflate(R.layout.detalle_producto, null))
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        AlertDialog dialogProducto = builder.create();
        dialogProducto.show();

        TextView id = (TextView) dialogProducto.findViewById(R.id.product_id);
        TextView name = (TextView) dialogProducto.findViewById(R.id.product_name);
        TextView price = (TextView) dialogProducto.findViewById(R.id.product_price);
        TextView supplier = (TextView) dialogProducto.findViewById(R.id.product_supplier);
        TextView reference = (TextView) dialogProducto.findViewById(R.id.product_reference);
        TextView active = (TextView) dialogProducto.findViewById(R.id.product_active);
        TextView shortest = (TextView) dialogProducto.findViewById(R.id.product_shortest);
        TextView large  = (TextView) dialogProducto.findViewById(R.id.product_large);

        int searchListLength = ProductsList.size();
        for (int i = 0; i < searchListLength; i++) {
            if (ProductsList.get(i).getId_product().equals(id_producto)) {
                id.setText(ProductsList.get(i).getId_product());
                name.setText(ProductsList.get(i).getName());
                price.setText(ProductsList.get(i).getPrice());
                supplier.setText(ProductsList.get(i).getId_supplier());
                reference.setText(ProductsList.get(i).getReference());
                active.setText(ProductsList.get(i).getActivo());
                shortest.setText(ProductsList.get(i).getDescription_short());
                large.setText(ProductsList.get(i).getDescription());
                break;
            }
        }


    }

    private void cargarDatos() {
        loadListTask tarea = new loadListTask(this, prestashop_key);
        try {
            jsonResult = tarea.execute(new String[] { url }).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        llenarFilas();
    }

    public void llenarFilas(){
        try {
            JSONObject jsonResponse = new JSONObject(jsonResult);
            JSONArray jsonMainNode = jsonResponse.optJSONArray("products");
            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                JSONArray NameNode = jsonChildNode.optJSONArray("name"); // Parsing the "name" node
                JSONObject NameObject = NameNode.getJSONObject(0);       // to extract the "value"
                String name = NameObject.optString("value");            // from the node's element
                //String name = jsonChildNode.optString("name");
                Integer id_product = jsonChildNode.optInt("id");
                Integer id_image = jsonChildNode.optInt("id_default_image");
                ///////////////////  JSON PARSING
                JSONArray DescriptionNode = jsonChildNode.optJSONArray("description"); // Parsing the "description" node
                JSONObject DescriptionObject = DescriptionNode.getJSONObject(0);       // to extract the "value"
                String description_short  = DescriptionObject.optString("value");            // from the node's element
                //String description_short = jsonChildNode.optString("description_short");
                //String description = jsonChildNode.optString("description");
                description_short=description_short.replaceAll("<p>","");
                description_short=description_short.replaceAll("</p>","");
                String description= description_short;
                JSONArray SDescriptionNode = jsonChildNode.optJSONArray("description_short"); // Parsing the "description_short" node
                JSONObject SDescriptionObject = SDescriptionNode.getJSONObject(0);       // to extract the "value"
                description_short  = SDescriptionObject.optString("value");            // from the node's element
                description_short=description_short.replaceAll("<p>","");
                description_short=description_short.replaceAll("</p>","");
                if (description_short.isEmpty()) description_short=description;
                ///////////////////////////
                /*String description_short = jsonChildNode.optString("description_short");
                String description = jsonChildNode.optString("description");*/
                Double price = jsonChildNode.optDouble("price");
                String id_supplier = jsonChildNode.optString("id_supplier");
                String reference = jsonChildNode.optString("reference");
                String activo= ""+jsonChildNode.optString("active");
                if(activo.equalsIgnoreCase("1"))
                    activo="Yes";
                else
                    activo="No";
                String available = jsonChildNode.optString("available_now");
                ProductsList.add(new abs_Products(id_product, name, id_image, description_short, description, price,
                    id_supplier,reference,activo,available));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error" + e.toString(),
                    Toast.LENGTH_LONG).show();
        }
        lv.setAdapter(new row_new_order(this, ProductsList, this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
