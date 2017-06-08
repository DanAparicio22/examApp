package com.balance.controller;

import com.balance.model.CaloriesHistory;
import com.balance.model.LocationHistory;
import com.balance.service.LocationHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * Created by da_20 on 6/6/2017.
 */
@RestController
public class LocationHistoryController {
    private LocationHistoryService locationHistoryService;


    public Double distancia(Integer x1,Integer y1,Integer x2, Integer y2){
        return Math.sqrt(((x2-x1)*(x2-x1))+((y2-y1)*(y2-y1)));
    }

    @Autowired
    public void setLocationHistoryService(LocationHistoryService locationHistoryService) {
        this.locationHistoryService = locationHistoryService;
    }

    /*@RequestMapping(value = "/locations", method = RequestMethod.GET)
    public ResponseEntity<Iterable<LocationHistory>> getLocationHistories() {
        return new ResponseEntity(locationHistoryService.listAllLocationHistory(), HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/getLocations/{id}", method = RequestMethod.GET)
    public LocationHistory getLocations(@PathVariable Integer id) {
        float latitude = 0;
        float longitude = 0;
        Iterator<LocationHistory> iterator = locationHistoryService.listAllLocationHistory().iterator();
        List<LocationHistory> myList=new ArrayList<>();
        Date fechaactual = new Date();
        while(iterator.hasNext()){
            myList.add(iterator.next());
        }

        for(LocationHistory lh:myList){
            if(lh.getUser().equals(id) &&
                    fechaactual.getDay()==lh.getDate().getDay() &&
                    fechaactual.getMonth()==lh.getDate().getMonth() &&
                    fechaactual.getYear()==lh.getDate().getYear()){
                latitude+=lh.getLatitude();
                longitude+=lh.getLongitude();
            }
        }

        LocationHistory locationHistory = new LocationHistory();
        locationHistory.setLatitude(latitude);
        locationHistory.setLongitude(longitude);
        locationHistory.setId(67620L);
        return locationHistory;

    }*/
    @RequestMapping(value = "/distance/{id}",method = RequestMethod.GET)
    public Double getDistance(@PathVariable Integer id){
        Iterator<LocationHistory> iterator = locationHistoryService.listAllLocationHistory().iterator();
        double distance=0;
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
                distance+=distancia(0,0,myList.get(i).getX(),myList.get(i).getY());
            }else{
                if((i+1)>myList.size()){
                    distance+=0;
                }else{
                    distance+=distancia(myList.get(i).getX(),myList.get(i).getY(),myList.get(i+1).getX(),myList.get(i+1).getY());
                }
            }
        }
        return distance;
    }
}
