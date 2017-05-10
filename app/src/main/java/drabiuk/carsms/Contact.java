package drabiuk.carsms;
public class Contact {

    //private variables
    int _id;
    String _name;
    String _phone_number;
    int group_id;

    // Empty constructor
    public Contact(){
    }

    // constructor
    public Contact(int id, String name, String _phone_number, int gid){
        this._id = id;
        this._name = name;
        this._phone_number = _phone_number;
        this.group_id = gid;
    }

    // constructor
    public Contact(String name, String _phone_number,int gid){
        this._name = name;
        this._phone_number = _phone_number;
        this.group_id = gid;
    }
    // getting ID
    public int getID(){
        return this._id;
    }

    // getting ID
    public int getGroupID(){
        return this.group_id;
    }

    // setting id
    public void setID(int id){
        this._id = id;
    }

    // getting ID
    public void setGroupID(int id){this.group_id=id;}

    // getting name
    public String getName(){
        return this._name;
    }

    // setting name
    public void setName(String name){
        this._name = name;
    }

    // getting phone number
    public String getPhoneNumber(){
        return this._phone_number;
    }

    // setting phone number
    public void setPhoneNumber(String phone_number){
        this._phone_number = phone_number;
    }
}