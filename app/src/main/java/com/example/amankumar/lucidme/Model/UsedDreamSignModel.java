package com.example.amankumar.lucidme.Model;

import java.util.ArrayList;

/**
 * Created by AmanKumar on 5/17/2016.
 */
public class UsedDreamSignModel {
   ArrayList<String> userDreamSigns;

    public UsedDreamSignModel() {
    }

    public UsedDreamSignModel(ArrayList<String> DREAMSIGNS) {
        this.userDreamSigns = DREAMSIGNS;
    }

    public ArrayList<String > getDREAMSIGNS() {
        return userDreamSigns;
    }
}
