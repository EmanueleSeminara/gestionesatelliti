package it.prova.gestionesatelliti.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.prova.gestionesatelliti.model.Satellite;
import it.prova.gestionesatelliti.model.StatoSatellite;
import it.prova.gestionesatelliti.repository.SatelliteRepository;

@Service
public class SatelliteServiceImpl implements SatelliteService {
	@Autowired
	private SatelliteRepository repository;

	@Override
	@Transactional(readOnly = true)
	public List<Satellite> listAllElements() {
		return (List<Satellite>) repository.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Satellite caricaSingoloElemento(Long id) {
		return repository.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void aggiorna(Satellite satelliteInstance) {
		repository.save(satelliteInstance);
	}

	@Override
	@Transactional
	public void inserisciNuovo(Satellite satelliteInstance) {
		repository.save(satelliteInstance);

	}

	@Override
	@Transactional
	public void rimuovi(Satellite satelliteInstance) {
		repository.delete(satelliteInstance);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Satellite> findByExample(Satellite example) {
		Specification<Satellite> specificationCriteria = (root, query, cb) -> {

			List<Predicate> predicates = new ArrayList<Predicate>();

			if (StringUtils.isNotEmpty(example.getCodice()))
				predicates.add(cb.like(cb.upper(root.get("codice")), "%" + example.getCodice().toUpperCase() + "%"));

			if (StringUtils.isNotEmpty(example.getDenominazione()))
				predicates.add(cb.like(cb.upper(root.get("denominazione")),
						"%" + example.getDenominazione().toUpperCase() + "%"));

			if (example.getStato() != null)
				predicates.add(cb.equal(root.get("stato"), example.getStato()));

			if (example.getDataLancio() != null)
				predicates.add(cb.greaterThanOrEqualTo(root.get("dataLancio"), example.getDataLancio()));

			if (example.getDataRientro() != null)
				predicates.add(cb.greaterThanOrEqualTo(root.get("dataRientro"), example.getDataRientro()));

			return cb.and(predicates.toArray(new Predicate[predicates.size()]));
		};

		return repository.findAll(specificationCriteria);
	}

	@Override
	public void rimuoviById(Long idSatellite) {
		repository.deleteById(idSatellite);

	}

	@Override
	public void launch(Long idSatellite) {
		Satellite satelliteDaLanciare = repository.findById(idSatellite).orElse(null);
		satelliteDaLanciare.setDataLancio(new Date());
		satelliteDaLanciare.setStato(StatoSatellite.IN_MOVIMENTO);
		repository.save(satelliteDaLanciare);

	}

	@Override
	public void returns(Long idSatellite) {
		Satellite satelliteDaLanciare = repository.findById(idSatellite).orElse(null);
		satelliteDaLanciare.setDataRientro(new Date());
		satelliteDaLanciare.setStato(StatoSatellite.DISATTIVATO);
		repository.save(satelliteDaLanciare);

	}

	@Override
	public List<Satellite> lanciatiDaPiuDiDueAnni() {
		LocalDate data = LocalDate.now().minusYears(2).minusDays(1);
		return repository.findByDataLancioLessThanEqual(java.sql.Date.valueOf(data));
	}

}
