package controllers;

import play.mvc.Controller;
import play.mvc.With;
import actions.Autenticar;

@With({Autenticar.class})
public abstract class ControladorSeguro extends Controller {}