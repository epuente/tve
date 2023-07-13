package controllers;

import play.mvc.Controller;
import play.mvc.With;
import actions.Autenticar; 
import actions.ConfirmarIngeniero;

@With({Autenticar.class, ConfirmarIngeniero.class})
public abstract class ControladorSeguroIngeniero extends Controller {

}