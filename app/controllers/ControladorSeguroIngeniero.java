package controllers;

import actions.Encabezados;
import play.mvc.Controller;
import play.mvc.With;
import actions.Autenticar; 
import actions.ConfirmarIngeniero;

@With({Encabezados.class, Autenticar.class, ConfirmarIngeniero.class})
public abstract class ControladorSeguroIngeniero extends Controller {

}