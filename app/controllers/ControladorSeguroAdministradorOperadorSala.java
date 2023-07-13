package controllers;

import play.mvc.Controller;
import play.mvc.With;
import actions.Autenticar; 
import actions.ConfirmarAdministradorOperadorSala;

@With({Autenticar.class, ConfirmarAdministradorOperadorSala.class})
public abstract class ControladorSeguroAdministradorOperadorSala extends Controller {}