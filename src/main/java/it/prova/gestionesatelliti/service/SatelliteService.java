package it.prova.gestionesatelliti.service;

import java.util.List;

import it.prova.gestionesatelliti.model.Satellite;

public interface SatelliteService {
	public List<Satellite> listAllElements();

	public Satellite caricaSingoloElemento(Long id);

	public void aggiorna(Satellite satelliteInstance);

	public void inserisciNuovo(Satellite satelliteInstance);

	public void rimuovi(Satellite satelliteInstance);

	public void rimuoviById(Long idSatellite);

	public List<Satellite> findByExample(Satellite example);

	public void launch(Long idSatellite);

	public void returns(Long idSatellite);

	public List<Satellite> lanciatiDaPiuDiDueAnni();

	public List<Satellite> disattivatiMaNonRientrati();

	public List<Satellite> fissiDaDieciAnni();

}
