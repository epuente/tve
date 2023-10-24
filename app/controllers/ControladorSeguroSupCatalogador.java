package controllers;

import actions.Autenticar;
import actions.ConfirmarCatalogador;
import actions.ConfirmarSupCatalogador;
import play.mvc.Controller;
import play.mvc.With;

@With({Autenticar.class, ConfirmarSupCatalogador.class})
public abstract class ControladorSeguroSupCatalogador extends Controller {

}