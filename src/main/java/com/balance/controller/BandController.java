package com.balance.controller;

import com.balance.model.*;
import com.balance.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by da_20 on 31/5/2017.
 */
@RestController
public class BandController {

    public static int cont=0;
    private BandService bandService;
    private UserService userService;
    private CaloriesHistoryService caloriesHistoryService;
    private PulseHistoryService pulseHistoryService;
    private StepsHistoryService stepsHistoryService;
    private LocationHistoryService locationHistoryService;

    public Double distancia(Integer x1,Integer y1,Integer x2, Integer y2){
        return Math.sqrt(((x2-x1)*(x2-x1))+((y2-y1)*(y2-y1)));
    }

    @Autowired
    public void setStepsHistoryService(StepsHistoryService stepsHistoryService) {
        this.stepsHistoryService = stepsHistoryService;
    }

    @Autowired
    public void setLocationHistoryService(LocationHistoryService locationHistoryService) {
        this.locationHistoryService = locationHistoryService;
    }

    @Autowired
    public void setPulseHistoryService(PulseHistoryService pulseHistoryService) {
        this.pulseHistoryService = pulseHistoryService;
    }

    @Autowired
    public void setCaloriesHistoryService(CaloriesHistoryService caloriesHistoryService) {
        this.caloriesHistoryService = caloriesHistoryService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setBandService(BandService bandService) {
        this.bandService = bandService;
    }

    @RequestMapping(value = "/band", method = RequestMethod.POST)
    public Band saveBand(@Valid Band band, BindingResult bindingResult, Model model) {
        if(distancia(0,0,band.getX(),band.getY())<1000){
            if(cont%2!=0){
                Date yesterday = new Date();
                yesterday.setDate(yesterday.getDate()-1);
                System.out.println(yesterday);
                band.setFecha_registro(yesterday);
                cont++;
            }else{
                band.setFecha_registro(new Date());
                cont++;
            }
            caloriesHistoryService.saveCaloriesHistory(new CaloriesHistory(band.getCalories(), band.getUser(), band.getFecha_registro()));
            pulseHistoryService.savePulseHistory(new PulseHistory(band.getBpm(), band.getFecha_registro(), band.getUser()));
            stepsHistoryService.saveStepsHistory(new StepsHistory(band.getSteps(), band.getDistance(), band.getUser(), band.getFecha_registro()));
            locationHistoryService.saveLocationHistory(new LocationHistory(band.getX(),band.getY(),band.getUser(),band.getFecha_registro()));
            bandService.saveBand(band);

            return band;
        }
        return null;
    }

    @RequestMapping(value = "/bands", method = RequestMethod.GET)
    public ResponseEntity<Iterable<Band>> getBands() {
        return new ResponseEntity(bandService.listAllBands(), HttpStatus.NOT_FOUND);
    }

}