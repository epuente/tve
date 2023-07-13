package controllers;

import play.mvc.Controller;
import play.mvc.With;
import actions.Autenticar; 
import actions.ConfirmarAdministrador;

@With({Autenticar.class, ConfirmarAdministrador.class})
public abstract class ControladorSeguroAdministrador extends Controller {}