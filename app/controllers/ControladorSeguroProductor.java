package controllers;

import actions.Autenticar;
import actions.ConfirmarProductor;
import actions.ConfirmarVideoteca;
import actions.Encabezados;
import play.mvc.Controller;
import play.mvc.With;

@With({Encabezados.class, Autenticar.class, ConfirmarProductor.class})
public abstract class ControladorSeguroProductor extends Controller {

}