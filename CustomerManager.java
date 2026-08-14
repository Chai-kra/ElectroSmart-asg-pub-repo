import java.util.ArrayList;
import java.util.List;

public class CustomerManager{
    private List<Customer> customers = new ArrayList<>();
    
    public void addCustomer(Customer c) throws DuplicateCustomerException{
        for (Customer existing:customers){
            if(existing.getCustomerID().equalsIgnoreCase(c.getCustomerID())){
                throw new DuplicateCustomerException("Customer ID" + c.getCustomerID() + "already exists.");
            }
        }
        customers.add(c);
    }

    public Customer findByID(String customerID){
        for(Customer c: customers){
            if(c.getCustomerID().equalsIgnoreCase(customerID)){
                return c;
            }
        }
        return null; // caller checks for null
    }

    public List<Customer>getAllCustomers(){
        return customers;
    }
}
