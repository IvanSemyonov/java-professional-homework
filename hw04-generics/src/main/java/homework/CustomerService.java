package homework;


import java.util.*;

public class CustomerService {

    private final TreeMap<Customer, String> customerData = new TreeMap<>(Comparator.comparingLong(Customer::getScores));

    public Map.Entry<Customer, String> getSmallest() {
        Map.Entry<Customer, String> customerDataEntry = customerData.firstEntry();
        if (customerDataEntry == null) {
            return null;
        }
        return cloneEntry(customerDataEntry);
    }

    public Map.Entry<Customer, String> getNext(Customer customer) {
        Map.Entry<Customer, String> customerDataEntry = customerData.higherEntry(customer);
        if (customerDataEntry == null) {
            return null;
        }
        return cloneEntry(customerDataEntry);
    }

    private Map.Entry<Customer, String> cloneEntry(Map.Entry<Customer, String> customerDataEntry) {
        return Map.entry(customerDataEntry.getKey().clone(), customerDataEntry.getValue());
    }

    public void add(Customer customer, String data) {
        customerData.put(customer.clone(), data);
    }
}
