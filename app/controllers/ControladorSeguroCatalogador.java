package controllers;

import actions.Autenticar;
import actions.ConfirmarCatalogador;
import play.mvc.Controller;
import play.mvc.With;

@With({Autenticar.class, ConfirmarCatalogador.class})
public abstract class ControladorSeguroCatalogador extends Controller {

}