import java.io.Serializable;

public class Details_Toy implements Serializable {
    private String toy_code, toy_name, description, price, dom, batch_no, manufacturer_name, address, zip_code, country, message;

    public Details_Toy() {
    }

    public String getToy_code() {
        return toy_code;
    }

    public void setToy_code(String toy_code) {
        this.toy_code = toy_code;
    }

    public String getToy_name() {
        return toy_name;
    }

    public void setToy_name(String toy_name) {
        this.toy_name = toy_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDom() {
        return dom;
    }

    public void setDom(String dom) {
        this.dom = dom;
    }

    public String getBatch_no() {
        return batch_no;
    }

    public void setBatch_no(String batch_no) {
        this.batch_no = batch_no;
    }

    public String getManufacturer_name() {
        return manufacturer_name;
    }

    public void setManufacturer_name(String manufacturer_name) {
        this.manufacturer_name = manufacturer_name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZip_code() {
        return zip_code;
    }

    public void setZip_code(String zip_code) {
        this.zip_code = zip_code;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString (){

        return "Toy Name: " + toy_name + "\n"
                + "Toy Code: " + toy_code + "\n"
                + "Toy Description: " + description + "\n"
                + "Toy Price: " + price + "\n"
                + "Toy's Date of manufacture: " + dom + "\n"
                + "Toy's Batch number: " + batch_no + "\n"
                + "Company Name: " + manufacturer_name + "\n"
                + "Company Address: " + address + "\n"
                + "Company Zip-code: " + zip_code + "\n"
                + "Company Country: " + country + "\n"
                + "User's Message: " + message ;

    }
}
