package persistence;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONObject;

import com.monnit.mine.MonnitMineAPI.SensorMessage;


public class FileDBManager {

	public FileDBManager() {
	}

	
	public void appendSensorReading(SensorMessage msg) {
		
	}
	
	
	public void writeInputTemplateJSON(String destination) {
		HashMap<String,Object> map = new HashMap<String,Object>();
		/*
		//resources
		HashMap<String,Integer> resourcesmap = new HashMap<String,Integer>();
		resourcesmap.put("pickers", inputVariables.getPickers());
		resourcesmap.put("pickers-queue-length", inputVariables.getPickers_queue_length());
		resourcesmap.put("packingstations", inputVariables.getPackers());
		resourcesmap.put("packingstations-queue-length", inputVariables.getPackers_queue_length());
		map.put("resources", new JSONObject(resourcesmap));
		
		//mmsetup
		HashMap<String,Object> mmsetupmap = new HashMap<String,Object>();
		mmsetupmap.put("capacity-picking-device", inputVariables.getMmsetup().getCapacity_picking_device());
		mmsetupmap.put("capacity-picklist", inputVariables.getMmsetup().getCapacity_picklist());
		map.put("mmsetup", new JSONObject(mmsetupmap));
				
		
		HashMap<String,Object> demandmap = new HashMap<String,Object>();
		demandmap.put("totalOutboundOrders", inputVariables.getOverallDemand());
		//order-pool
		HashMap<String,Object> orderpoolmap = new HashMap<String,Object>();
		HashMap<String,Object> ordermap, itemsmap, skumap;
		for (int o=0; o<orderpool.getNumberOfOrders(); o++) {
			ordermap = new HashMap<String,Object>();
			IOrder order = orderpool.getOrders().get(o);
			ordermap.put("OutboundOrderNo", order.getOrder_id());
			ordermap.put("Duetime", order.getDuetime());
			ordermap.put("PushTime", order.getPushtime());
			ordermap.put("OrderStructure", order.getOrderStructure());
			itemsmap = new HashMap<String,Object>();
			for(int i=0;i<order.getItems().size();i++) {
				skumap = new HashMap<String,Object>();
				Items item = order.getItems().get(i);
				skumap.put("nitems", item.getNitems());
				skumap.put("SKUCode", item.getSku().getSKU_id());
				skumap.put("barcode", item.getSku().getBarcode());
				skumap.put("description", item.getSku().getDescription());
				skumap.put("Volume", item.getSku().getVolume());
				skumap.put("Weight", item.getSku().getWeight());
				itemsmap.put(""+(i+1), new JSONObject(skumap));
				//itemsmap.put(item.getSku().getSKU_id(), new JSONObject(skumap));
			}
			ordermap.put("items", new JSONObject(itemsmap));
			//orderpoolmap.put(""+(o+1), new JSONObject(skumap));
			orderpoolmap.put(order.getOrder_id(), new JSONObject(ordermap));
		}
		demandmap.put("orderpool", new JSONObject(orderpoolmap));
		map.put("demand", new JSONObject(demandmap));
				
		
		JSONObject mapjson = new JSONObject(map);
		//Write the file
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmm");
		String outputfile = destination;
		if (!outputPath.endsWith("/")) outputfile+="/";
		outputfile += "inputJSON" +sdf.format(calendar.getTime());
		JSONManager.writeJSON(mapjson, outputfile);
		*/
	}
	
	private void readDemandJSON(String source){
		/*
		List<IOrder> demand = new LinkedList<IOrder> ();
		String datafile = source+"inputJSON.json";
		JSONObject data = JSONManager.readJSON(datafile);
		//System.out.println("data:" + data);
		
		JSONObject demandjson = (JSONObject) data.get("demand");
		this.overallDemand = ((Long) demandjson.get("totalOutboundOrders")).intValue();		
		JSONObject orderpooljson = (JSONObject) demandjson.get("orderpool");
		//System.out.println(orderpooljson);
		for(Iterator iterator = orderpooljson.keySet().iterator(); iterator.hasNext();) {
		    String key = (String) iterator.next();
			JSONObject jsonOrder = (JSONObject) orderpooljson.get(key);
			//System.out.println(jsonOrder);
			IOrder order = new Order((String)jsonOrder.get("OutboundOrderNo"));
			if (demand.contains(order))
				order=demand.get(demand.indexOf(order));
			else {
				order.setPushtime((String) jsonOrder.get("PushTime"));
				String duetime = (String) jsonOrder.get("Duetime");
				if (duetime==null || duetime.isEmpty())
					order.setDuetime((String) jsonOrder.get("PushTime"), 24);
				demand.add(order);
			}
			JSONObject jsonItems = (JSONObject) jsonOrder.get("items");
			for(Iterator iteratorit = jsonItems.keySet().iterator(); iteratorit.hasNext();) {
				String keyit = (String) iteratorit.next();
				JSONObject jsonitem = (JSONObject) jsonItems.get(keyit);
				Items item=new Items(((Long) jsonitem.get("nitems")).intValue(), (String) jsonitem.get("SKUCode"));
				if (!order.contains(item)) {
					order.addItem(item);
				} else {
					item=order.getItems().get(order.getItems().indexOf(item));
					item.addItems(((Long) jsonitem.get("nitems")).intValue());
				}
			}
		}
        //System.out.println(printDemand(demand));
		return demand;
		
		*/
	}

	
	private void writeReportJSON() {
		HashMap<String,Object> map = new HashMap<String,Object>();
		/*
		//Metadata: Expected KPIs, order pool details, input details, date,...
		HashMap<String,Object> metadatamap = new HashMap<String,Object>();
		metadatamap.put("ExpectedExecutionTime", outputVariables.getMinimumProcessingTime());
		metadatamap.put("ExpectedMakespan", outputVariables.getMakespan());
		metadatamap.put("ExpectedLabourEfficiency", outputVariables.getLabourEfficiency());
		metadatamap.put("ExpectedPickingEfficiency", outputVariables.getPickingEfficiency());
		metadatamap.put("ExpectedPackingEfficiency", outputVariables.getPackingEfficiency());
		metadatamap.put("Pickers", outputVariables.getNumberOfPickers());
		metadatamap.put("PackingStations", outputVariables.getNumberOfPackingStations());
		metadatamap.put("PicklistsCreated", outputVariables.getNumberOfPicklists());
		metadatamap.put("OrdersProcessed", outputVariables.getNumberOfOrdersinPicklists());
		JSONObject metadata = new JSONObject(metadatamap);
		map.put("metadata", metadata);
		
		
		//Picklists
		HashMap<String,Object> plsmap = new HashMap<String,Object>();
		HashMap<String,Object> plmap, routemap, locationmap;
		IPicklist pl;
		List<IOrder> orders;
		ILocation loc;
		for (int p=0; p<outputVariables.getNPicklists(); p++) {
			plmap = new HashMap<String,Object>();
			pl = outputVariables.getPicklists().get(p);
			plmap.put("picklistNo", pl.getPicklist_id());
			routemap = new HashMap<String,Object>();
			for(int s=0;s<pl.getNSKUs();s++) {
				locationmap = new HashMap<String,Object>();
				loc = inputVariables.getLayout().getLocation(pl.getSKUs().get(s));
				locationmap.put("location", loc.getId());
				locationmap.put("SKUNo", pl.getSKUs().get(s).getSKU_id());
				locationmap.put("barcode", loc.getSku().getBarcode());
				locationmap.put("SKUdescription", loc.getSku().getDescription());
				locationmap.put("totalItemsInPicklist", pl.getNitems(pl.getSKUs().get(s)));
				
				HashMap<String, Integer> itemsperordermap = new HashMap<String, Integer>();
				//HashMap<String, Integer> ordernoidmap = new HashMap<String, Integer>();
				orders = pl.getOrders();
				for (int o=0; o<orders.size(); o++) {
					if(orders.get(o).getSKUs().contains(pl.getSKUs().get(s))) {
						itemsperordermap.put(""+(o+1), orders.get(o).getNitems(pl.getSKUs().get(s)));
						//ordernoidmap.put(orders.get(o).getOrder_id(), orders.get(o).getNitems(pl.getSKUs().get(s)));
					}
				}
				JSONObject itemsperorderjson = new JSONObject(itemsperordermap);
				locationmap.put("itemsperOrder", itemsperorderjson);
				//JSONObject ordernoidjson = new JSONObject(ordernoidmap);
				//locationmap.put("itemsperOrder", ordernoidjson);
				JSONObject location = new JSONObject(locationmap);
				routemap.put(inputVariables.getLayout().getLocation(pl.getSKUs().get(s)).getId(), location);
			}
			JSONObject route = new JSONObject(routemap);
			plmap.put("route", route);
			plmap.put("picker", outputVariables.getPicker(pl).getPicker_id());
			plmap.put("pickerQueuePosition", outputVariables.getPicker(pl).getPicker_queue().indexOf(pl));
			plmap.put("packingStation", outputVariables.getPackingStation(pl).getpackingStation_id());
			plmap.put("packingStationQueuePosition", outputVariables.getPackingStation(pl).getpackingStation_queue().indexOf(pl));
			JSONObject plmapjson = new JSONObject(plmap);
			plsmap.put(pl.getPicklist_id(), plmapjson);
		}
		JSONObject pls = new JSONObject(plsmap);
		map.put("picklists", pls);
		
		JSONObject mapjson = new JSONObject(map);

		//Write the file
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmm");
		String outputfile = outputPath;
		if (!outputPath.endsWith("/")) outputfile+="/";
		outputfile += "outputreport" +sdf.format(calendar.getTime());
		JSONManager.writeJSON(mapjson, outputfile);
		*/
	}

}