package drabiuk.carsms;

import java.io.Serializable;

/**
 * Created by Magdalena on 2017-04-19.
 */

public class ObjectGroup implements Serializable {
    int _id;
    public   String name;
    public   String msg;


    // Empty constructor
    public ObjectGroup(){
    }

        public ObjectGroup(int i, String name, String msg) {
        this._id = i;
            this.name=name;
        this.msg=msg;
    }

    public ObjectGroup( String name, String msg) {
        this.name=name;
        this.msg=msg;
    }

    // getting ID
    public int getID(){
        return this._id;
    }

    // setting id
    public void setID(int id){
        this._id = id;
    }


    public  String getName() {
            return name;
        }
        public  String getMsg() {
            return msg;
        }
        public void setName(String nazwa) { this.name=nazwa; }
        public void setMsg(String wiadomosc) { this.msg=wiadomosc; }
}
