package controllers;

import models.Personal;

public class PersonalController extends ControladorSeguro {

    public static Personal buscar(Long id){
        return Personal.find.byId(id);
    }
}
