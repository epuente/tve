package controllers;

import actions.Autenticar;
import actions.ConfirmarIngeniero;
import actions.ConfirmarVideoteca;
import play.mvc.Controller;
import play.mvc.With;

@With({Autenticar.class, ConfirmarVideoteca.class})
public abstract class ControladorSeguroVideoteca extends Controller {

}