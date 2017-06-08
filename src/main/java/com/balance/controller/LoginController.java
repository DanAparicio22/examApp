package com.balance.controller;

import javax.mail.MessagingException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.balance.Mail.SmtpMailSender;
import com.balance.model.*;
import com.balance.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.*;

@Controller
public class LoginController {

	public Double distancia(Integer x1,Integer y1,Integer x2, Integer y2){
		return Math.sqrt(((x2-x1)*(x2-x1))+((y2-y1)*(y2-y1)));
	}

	@Autowired
	private UserService userService;
	private TerminalService terminalService;
	private BandModelService bandModelService;
	private CaloriesHistoryService caloriesHistoryService;
	private PulseHistoryService pulseHistoryService;
	private StepsHistoryService stepsHistoryService;

	private LocationHistoryService locationHistoryService;

	@Autowired
	public void setLocationHistoryService(LocationHistoryService locationHistoryService) {
		this.locationHistoryService = locationHistoryService;
	}

	@Autowired
    public void setStepsHistoryService(StepsHistoryService stepsHistoryService) {
        this.stepsHistoryService = stepsHistoryService;
    }

	@Autowired
	public void setCaloriesHistoryService(CaloriesHistoryService caloriesHistoryService) {
		this.caloriesHistoryService = caloriesHistoryService;
	}
	@Autowired
	public void setPulseHistoryService(PulseHistoryService pulseHistoryService) {
		this.pulseHistoryService = pulseHistoryService;
	}

	@Autowired
	public void setTerminalService(TerminalService terminalService){
		this.terminalService=terminalService;
	}

	@Autowired
	public void setBandModelService(BandModelService bandModelService){
		this.bandModelService=bandModelService;
	}

	@RequestMapping(value={"/", "/login"}, method = RequestMethod.GET)
	public String login(){
		return "login";
	}

	@RequestMapping(value="/registration", method = RequestMethod.GET)
	public String registration(Model model){
		model.addAttribute("user", new User());
		model.addAttribute("bands",bandModelService.listAllBandModels());
		return "registration";
	}

	@RequestMapping(value = "/registration", method = RequestMethod.POST)
	public String createNewUser(@Valid User user, BindingResult bindingResult, Model model) {
		User userExists = userService.findUserByEmail(user.getEmail());
		if (userExists != null) {
			bindingResult.rejectValue("email", "error.user", "There is already a user registered with the email provided");
		}
		if(user.getTerminal()==null){
			return "redirect:/registration";
		}
		if(!user.getTerminal().getBandModel().getName().equals(user.getBand())){
			return "redirect:/registration";
		}
		if (!bindingResult.hasErrors() && !terminalService.getTerminalById(user.getTerminal().getSerial()).isActive()) {
			userService.saveUser(user);
			model.addAttribute("successMessage", "El usuario se registro correctamente");
			model.addAttribute("user", new User());
		}else{
			return "redirect:/registration";
		}

		return "registration";
	}

	@RequestMapping(value="/admin/home", method = RequestMethod.GET)
	public String home(Model model){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		if (user == null) {
			return "redirect:/";
		}
		model.addAttribute("userName", "Welcome " + user.getName() + " " + user.getLastName() + " (" + user.getEmail() + ")");
		model.addAttribute("adminMessage","Content Available Only for Users with Admin Role");
		model.addAttribute("userList", userService.listAllUsers());
		model.addAttribute("user", user);
		return "admin/home";
	}

    @RequestMapping(value="/user/home", method = RequestMethod.GET)
    public String homeExclusive(Model model){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());

        Integer steps = 0;
        Integer calories=0;
        long distance=0;
        Integer bpm=0;

        Iterator<CaloriesHistory> iteratorC = caloriesHistoryService.listAllCaloriesHistorys().iterator();
        Iterator<StepsHistory> iteratorS = stepsHistoryService.listAllStepsHistory().iterator();
        Iterator<PulseHistory> iteratorP = pulseHistoryService.listAllPulseHistory().iterator();
		Iterator<LocationHistory> iterator = locationHistoryService.listAllLocationHistory().iterator();
		double distancem=0;
		List<LocationHistory> myList=new ArrayList<>();
		while(iterator.hasNext()){
			myList.add(iterator.next());
		}
		Collections.sort(myList, new Comparator<LocationHistory>() {
			public int compare(LocationHistory o1, LocationHistory o2) {
				return o1.getDate().after(o2.getDate()) ? -1 : 1;
			}
		});

		for(int i=0;i<myList.size();i++){
			if(i==0){
				distancem+=distancia(0,0,myList.get(i).getX(),myList.get(i).getY());
			}else{
				if((i+1)>myList.size()){
					distancem+=0;
				}else{
					distancem+=distancia(myList.get(i).getX(),myList.get(i).getY(),myList.get(i+1).getX(),myList.get(i+1).getY());
				}
			}
		}

        StepsHistory auxS = new StepsHistory();
        CaloriesHistory auxC = new CaloriesHistory();
        PulseHistory auxP = new PulseHistory();
		Date fechaactual=new Date();
        while(iteratorS.hasNext()){
            auxS = iteratorS.next();
            if(auxS.getUser().equals(user.getId()) &&
					auxS.getDate().getDay()==fechaactual.getDay() &&
					auxS.getDate().getMonth()==fechaactual.getMonth() &&
					auxS.getDate().getYear()==fechaactual.getYear() ) {
				steps += auxS.getSteps();
				distance += auxS.getDistance();
			}
        };


        while(iteratorC.hasNext()){
            auxC = iteratorC.next();
            if(auxC.getUser().equals(user.getId()) &&
					auxC.getDate().getDay()==fechaactual.getDay() &&
					auxC.getDate().getMonth()==fechaactual.getMonth() &&
					auxC.getDate().getYear()==fechaactual.getYear() ) {
				calories += auxC.getCalories();
			}
        }

        PulseHistory fechaMayor = null;

		while(iteratorP.hasNext()){
			auxP = iteratorP.next();
			if(auxP.getUser().equals(user.getId()) &&
					auxP.getDate().getDay()==fechaactual.getDay() &&
					auxP.getDate().getMonth()==fechaactual.getMonth() &&
					auxP.getDate().getYear()==fechaactual.getYear() ) {

				if(fechaMayor == null) {
					fechaMayor = auxP;
					bpm = auxP.getBpm();
				}
				else {
					if(auxP.getDate().after(fechaMayor.getDate())) {
						fechaMayor = auxP;
						bpm = auxP.getBpm();
					}
				}
			}
		}

		model.addAttribute("userName", "Welcome " + user.getName() + " " + user.getLastName() + " (" + user.getEmail() + ")");
		model.addAttribute("userMessage","Content Available Only for Users with Limited Role");
		model.addAttribute("user", user);
        model.addAttribute("countSteps",steps);
        model.addAttribute("countCalories",calories);
        model.addAttribute("countDistance",distance);
        model.addAttribute("countBpm",bpm);
        model.addAttribute("distanceCount",distancem);
        model.addAttribute("id",user.getId());
		return "user/home";
    }

	@RequestMapping(value="/default", method = RequestMethod.GET)
	public String defaultAfterLogin()
	{
		Set<String> roles = AuthorityUtils.authorityListToSet(SecurityContextHolder.getContext().getAuthentication().getAuthorities());
		if (roles.contains("ADMIN")) {
			return "redirect:/admin/home";
		}
		return "redirect:/user/home";
	}

	@RequestMapping(value = "/access-denied", method = RequestMethod.GET)
	public String accesoDenegado() {
		return "access-denied"; //Solo devuelve un mensaje
	}

	@RequestMapping(value = "/admin/users", method = RequestMethod.GET)
	public String listUsers(Model model) {
		model.addAttribute("Users",userService.listAllUsers());
		return "users";
	}

}