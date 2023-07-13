package controllers;

import actions.Autenticar;
import actions.ConfirmarAdminEA;
import play.mvc.Controller;
import play.mvc.With;

@With({Autenticar.class, ConfirmarAdminEA.class})
public abstract class ControladorSeguroAdminEA extends Controller {

}