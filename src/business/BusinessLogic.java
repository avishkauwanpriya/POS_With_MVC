package business;

import dao.DataLayer;
import db.DBConnection;
import util.CustomerTM;
import util.ItemTM;
import util.OrderDetailTM;
import util.OrderTM;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BusinessLogic {

    public static String getNewItemCode(){
        String lastItemCode = DataLayer.getLastItemCode();
        if (lastItemCode == null){
            return "I001";
        }else{
            int maxId=  Integer.parseInt(lastItemCode.replace("I",""));
            maxId = maxId + 1;
            String id = "";
            if (maxId < 10) {
                id = "I00" + maxId;
            } else if (maxId < 100) {
                id = "I0" + maxId;
            } else {
                id = "I" + maxId;
            }
            return id;
        }
    }

    public static String getNewCustomerId(){
        String lastCustomerId = DataLayer.getLastCustomerId();
        if (lastCustomerId == null){
            return "C001";
        }else{
           int maxId=  Integer.parseInt(lastCustomerId.replace("C",""));
            maxId = maxId + 1;
            String id = "";
            if (maxId < 10) {
                id = "C00" + maxId;
            } else if (maxId < 100) {
                id = "C0" + maxId;
            } else {
                id = "C" + maxId;
            }
            return id;
        }
    }

    public static List<CustomerTM> getAllCustomers(){
        return DataLayer.getAllCustomers();
    }

    public static boolean saveCustomer(String id, String name, String address){
        return DataLayer.saveCustomer(new CustomerTM(id,name,address));
    }

    public static boolean deleteCustomer(String customerId){
        return DataLayer.deleteCustomer(customerId);
    }

    public static boolean updateCustomer(String name, String address, String customerId){
        return DataLayer.updateCustomer(new CustomerTM(customerId, name, address));
    }

    public static List<ItemTM> getAllItems(){
        return DataLayer.getAllItems();
    }

    public static boolean saveItem(String code, String description, int qtyOnHand, double unitPrice){
        return DataLayer.saveItem(new ItemTM(code, description, qtyOnHand, unitPrice));
    }

    public static boolean deleteItem(String itemCode){
        return DataLayer.deleteItem(itemCode);
    }

    public static boolean updateItem(String description, int qtyOnHand, double unitPrice, String itemCode){
        return DataLayer.updateItem(new ItemTM(itemCode, description, qtyOnHand, unitPrice));
    }

    public static boolean placeOrder(OrderTM order, List<OrderDetailTM> orderDetails){
        Connection connection = DBConnection.getInstance().getConnection();

        try {
            connection.setAutoCommit(false);

            int i = DataLayer.insertOrder(order);
            if(i == 0){
                connection.rollback();
                return false;
            }

            for (OrderDetailTM orderDetail : orderDetails) {

                i = DataLayer.insertOrderDetail(orderDetail, order.getOrderId());

                if(i == 0){
                    connection.rollback();
                    return false;
                }

                i = DataLayer.updateItemQTY(orderDetail);

                if(i == 0){
                    connection.rollback();
                    return false;
                }

            }
            connection.commit();
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();

            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        //return DataLayer.placeOrder(order, orderDetails);
    }

    public static String autoGeneratePlaceOrderID(){
        String oldID = DataLayer.autoGeneratePlaceOrderID();

        oldID = oldID.substring(2, 5);

        int newID = Integer.parseInt(oldID) + 1;

        if (newID < 10) {
            return  "OD00" + newID;
        } else if (newID < 100) {
            return  "OD0" + newID;
        } else {
            return  "OD" + newID;
        }
    }
}
