package controllers;

import actions.Autenticar;
import actions.ConfirmarAdminEA;
import actions.Encabezados;
import play.mvc.Controller;
import play.mvc.With;

@With({Encabezados.class, Autenticar.class, ConfirmarAdminEA.class})
public abstract class ControladorSeguroAdminEA extends Controller {

}