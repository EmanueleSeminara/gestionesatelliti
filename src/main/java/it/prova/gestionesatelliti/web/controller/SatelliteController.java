package it.prova.gestionesatelliti.web.controller;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.prova.gestionesatelliti.model.Satellite;
import it.prova.gestionesatelliti.model.StatoSatellite;
import it.prova.gestionesatelliti.service.SatelliteService;

@Controller
@RequestMapping(value = "/satellite")
public class SatelliteController {

	@Autowired
	private SatelliteService satelliteService;

	@GetMapping
	public ModelAndView listAll() {
		ModelAndView mv = new ModelAndView();
		List<Satellite> results = satelliteService.listAllElements();
		mv.addObject("satellite_list_attribute", results);
		mv.setViewName("satellite/list");
		return mv;
	}

	@GetMapping("/search")
	public String search() {
		return "satellite/search";
	}

	@PostMapping("/list")
	public String listByExample(Satellite example, ModelMap model) {
		List<Satellite> results = satelliteService.findByExample(example);
		model.addAttribute("satellite_list_attribute", results);
		return "satellite/list";
	}

	@GetMapping("/insert")
	public String create(Model model) {
		model.addAttribute("insert_satellite_attr", new Satellite());
		return "satellite/insert";
	}

	@PostMapping("/save")
	public String save(@Valid @ModelAttribute("insert_satellite_attr") Satellite satellite, BindingResult result,
			RedirectAttributes redirectAttrs) {

		if (satellite.getDataLancio() != null && satellite.getDataRientro() != null
				&& satellite.getDataLancio().after(satellite.getDataRientro())) {
			result.rejectValue("dataLancio", "dataLancio.dataRientro.rangeInvalid");
			result.rejectValue("dataRientro", "dataLancio.dataRientro.rangeInvalid");
		}

		if (result.hasErrors())
			return "satellite/insert";

		satelliteService.inserisciNuovo(satellite);

		redirectAttrs.addFlashAttribute("successMessage", "Operazione eseguita correttamente");
		return "redirect:/satellite";
	}

	@GetMapping("/show/{idSatellite}")
	public String show(@PathVariable(required = true) Long idSatellite, Model model) {
		model.addAttribute("show_satellite_attr", satelliteService.caricaSingoloElemento(idSatellite));
		return "satellite/show";
	}

	@GetMapping("/delete/{idSatellite}")
	public String delete(@PathVariable(required = true) Long idSatellite, Model model,
			RedirectAttributes redirectAttrs) {
		Satellite satelliteDaEliminare = satelliteService.caricaSingoloElemento(idSatellite);
		if ((satelliteDaEliminare.getStato() != StatoSatellite.DISATTIVATO
				&& satelliteDaEliminare.getDataRientro() != null
				&& satelliteDaEliminare.getDataRientro().before(new Date()))
				|| satelliteDaEliminare.getDataLancio() != null) {
			redirectAttrs.addFlashAttribute("errorMessage", "Impossibile eliminare il satellite!");
			return "redirect:/satellite";
		}
		model.addAttribute("delete_satellite_attr", satelliteService.caricaSingoloElemento(idSatellite));
		return "satellite/delete";
	}

	@PostMapping("/remove")
	public String remove(@RequestParam(required = true) Long idDaRimuovere, RedirectAttributes redirectAttrs) {

		satelliteService.rimuoviById(idDaRimuovere);

		redirectAttrs.addFlashAttribute("successMessage", "Operazione eseguita correttamente");
		return "redirect:/satellite";
	}

	@GetMapping("/edit/{idSatellite}")
	public String edit(@PathVariable(required = true) Long idSatellite, Model model) {
		model.addAttribute("edit_satellite_attr", satelliteService.caricaSingoloElemento(idSatellite));
		return "satellite/edit";
	}

	@PostMapping("/update")
	public String update(@Valid @ModelAttribute("edit_satellite_attr") Satellite satellite, BindingResult result,
			RedirectAttributes redirectAttrs) {
		if (satellite.getDataLancio() != null && satellite.getDataRientro() != null
				&& satellite.getDataLancio().after(satellite.getDataRientro())) {
			result.rejectValue("dataLancio", "dataLancio.dataRientro.rangeInvalid");
			result.rejectValue("dataRientro", "dataLancio.dataRientro.rangeInvalid");
		}
		
		if(satellite.getStato() == StatoSatellite.DISATTIVATO && satellite.getDataRientro() == null) {
			result.rejectValue("stato", "stato.invalid");
		}
		
		if (result.hasErrors())
			return "satellite/edit";

		satelliteService.aggiorna(satellite);

		redirectAttrs.addFlashAttribute("successMessage", "Operazione eseguita correttamente");
		return "redirect:/satellite";
	}
	
	@PostMapping("/launch")
	public String launch(@RequestParam(required = true) Long idSatellite, RedirectAttributes redirectAttrs) {

		satelliteService.launch(idSatellite);

		redirectAttrs.addFlashAttribute("successMessage", "Il satellite " + idSatellite + " è stato lanciato!!");
		return "redirect:/satellite";
	}
	
	@PostMapping("/returns")
	public String returns(@RequestParam(required = true) Long idSatellite, RedirectAttributes redirectAttrs) {

		satelliteService.returns(idSatellite);

		redirectAttrs.addFlashAttribute("successMessage", "Il satellite " + idSatellite + " è stato lanciato!!");
		return "redirect:/satellite";
	}
	
	@GetMapping("/launchedtwoyears")
	public ModelAndView launchedTwoYears() {
		ModelAndView mv = new ModelAndView();
		List<Satellite> results = satelliteService.lanciatiDaPiuDiDueAnni();
		mv.addObject("satellite_list_attribute", results);
		mv.setViewName("satellite/list");
		return mv;
	}
	
	@GetMapping("/fixedfortentears")
	public ModelAndView fixedForTenYears() {
		ModelAndView mv = new ModelAndView();
		List<Satellite> results = satelliteService.fissiDaDieciAnni();
		mv.addObject("satellite_list_attribute", results);
		mv.setViewName("satellite/list");
		return mv;
	}

}
