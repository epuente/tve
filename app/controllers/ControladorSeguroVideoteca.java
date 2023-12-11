package controllers;

import actions.Autenticar;
import actions.ConfirmarIngeniero;
import actions.ConfirmarVideoteca;
import actions.Encabezados;
import play.mvc.Controller;
import play.mvc.With;

@With({Encabezados.class, Autenticar.class, ConfirmarVideoteca.class})
public abstract class ControladorSeguroVideoteca extends Controller {

}