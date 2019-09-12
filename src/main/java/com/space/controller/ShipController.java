package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/rest")
public class ShipController {
    private final ShipService shipService;

    @Autowired
    public ShipController(ShipService shipService) {
        this.shipService = shipService;
    }

    @RequestMapping(value = "/ships", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<Ship>> getShips(@RequestParam(value = "name", required = false) String name,
                                               @RequestParam(value = "planet", required = false) String planet,
                                               @RequestParam(value = "shipType", required = false) ShipType shipType,
                                               @RequestParam(value = "after", required = false) Long after,
                                               @RequestParam(value = "before", required = false) Long before,
                                               @RequestParam(value = "isUsed", required = false) Boolean isUsed,
                                               @RequestParam(value = "minSpeed", required = false) Double minSpeed,
                                               @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
                                               @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
                                               @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
                                               @RequestParam(value = "minRating", required = false) Double minRating,
                                               @RequestParam(value = "maxRating", required = false) Double maxRating,
                                               @RequestParam(value = "order", required = false, defaultValue = "ID") ShipOrder order,
                                               @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
                                               @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize) {
        Specification<Ship> spec = getUserSpecificationBuilder(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating).build();
        Page<Ship> allShips = shipService.findAll(spec, PageRequest.of(pageNumber, pageSize, Sort.by(order.getFieldName())));
        return new ResponseEntity<>(allShips.getContent(), HttpStatus.OK);
    }

    //getCount
    @RequestMapping(value = "/ships/count", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Long> getCount(@RequestParam(value = "name", required = false) String name,
                                         @RequestParam(value = "planet", required = false) String planet,
                                         @RequestParam(value = "shipType", required = false) ShipType shipType,
                                         @RequestParam(value = "after", required = false) Long after,
                                         @RequestParam(value = "before", required = false) Long before,
                                         @RequestParam(value = "isUsed", required = false) Boolean isUsed,
                                         @RequestParam(value = "minSpeed", required = false) Double minSpeed,
                                         @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
                                         @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
                                         @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
                                         @RequestParam(value = "minRating", required = false) Double minRating,
                                         @RequestParam(value = "maxRating", required = false) Double maxRating) {
        Specification<Ship> spec = getUserSpecificationBuilder(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating).build();
        if (spec != null) {
            List<Ship> ships = shipService.findAll(spec);
            return new ResponseEntity<>(Long.valueOf(ships.size()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(shipService.size(), HttpStatus.OK);
        }
    }

    //Create
    @RequestMapping(value = "/ships", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Ship> createShip(@RequestBody Ship ship) {
        try {
            if (ship.getProdDate() != null) {
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(ship.getProdDate());
                int mYear = c.get(Calendar.YEAR);

                if (ship.getName() == null && ship.getPlanet() == null && ship.getShipType() == null && ship.getProdDate() == null && ship.getUsed() == null && ship.getSpeed() == null && ship.getCrewSize() == null && ship.getRating() == null) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                } else if ((ship.getName() == null || ship.getName().isEmpty()) || (ship.getPlanet() == null || ship.getPlanet().isEmpty()) || (ship.getShipType() == null || ship.getPlanet().isEmpty()) || ship.getProdDate() == null || ship.getSpeed() == null || ship.getCrewSize() == null) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                } else if (ship.getName().length() > 50 || ship.getPlanet().length() > 50 || mYear < 2800 || mYear > 3019 || ship.getSpeed() < 0.01 || ship.getSpeed() > 0.99 || ship.getCrewSize() > 9999 || ship.getCrewSize() < 1) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                } else {
                    ship.setUsed(ship.getUsed() == null ? false : (ship.getUsed()));
                    double rating = ((80 * ship.getSpeed() * (ship.getUsed() == true ? (0.5) : 1)) / ((3019 - mYear) + 1));
                    double round_rating = Math.round(rating * 100.0) / 100.0;
                    ship.setRating(round_rating);
                    shipService.saveShip(ship);
                    return new ResponseEntity<>(ship, HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    //Get
    @RequestMapping(value = "/ships/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Ship> getShip(@PathVariable("id") Long id) {
        try {
            if (id <= 0) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } else if (shipService.existsById(id)) {
                return new ResponseEntity<>(shipService.getShipById(id), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    //Update
    @RequestMapping(value = "/ships/{id}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Ship> updateShip(@RequestBody Ship ship, @PathVariable("id") Long id)
    {
        if (id <= 0) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        else if (shipService.existsById(id)) {

            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(ship.getProdDate() == null ? shipService.getShipById(id).getProdDate() : ship.getProdDate());
            long mYear = c.get(Calendar.YEAR);

            HashMap<String, Object> map = new HashMap<>();

            map.put("name", ship.getName() == null ? null : ship.getName());
            map.put("planet", ship.getPlanet() == null ? null : ship.getPlanet());
            map.put("shipType", ship.getShipType() == null ? null : ship.getShipType());
            map.put("prodDate", ship.getProdDate() == null ? null : ship.getProdDate());
            map.put("isUsed", ship.getUsed() == null ? null : ship.getUsed());
            map.put("speed", ship.getSpeed() == null ? null : ship.getSpeed());
            map.put("crewSize", ship.getCrewSize() == null ? null : ship.getCrewSize());

            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (entry.getValue() != null) {
                    if (entry.getKey().equals("name")) {
                        if (entry.getValue().toString().isEmpty() || entry.getValue().toString().length() > 50) {
                            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                        }
                    } else if (entry.getKey().equals("planet")) {
                        if (entry.getValue().toString().isEmpty() || entry.getValue().toString().length() > 50) {
                            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                        }
                    } else if (entry.getKey().equals("shipType")) {
                        if (entry.getValue().toString().isEmpty()) {
                            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                        }
                    } else if (entry.getKey().equals("prodDate")) {
                        if (mYear < 2800 || mYear > 3019) {
                            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                        }
                    } else if (entry.getKey().equals("speed")) {
                        double speed = Double.parseDouble(entry.getValue().toString());
                        if (speed < 0.01 || speed > 0.99) {
                            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                        }
                    } else if (entry.getKey().equals("crewSize")) {
                        int crewSize = Integer.parseInt(entry.getValue().toString());
                        if (crewSize > 9999 || crewSize < 1) {
                            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                        }
                    }
                }
            }
            if (map.values().stream().allMatch(s -> s == null)) {
                return new ResponseEntity<>(shipService.getShipById(id), HttpStatus.OK);
            } else {
                ship = shipService.getShipById(id);
                for (Map.Entry<String, Object> entry : map.entrySet()) {

                    if (entry.getKey().equals("name")) {
                        ship.setName(entry.getValue() == null ? ship.getName() : entry.getValue().toString());
                    } else if (entry.getKey().equals("planet")) {
                        ship.setPlanet(entry.getValue() == null ? ship.getPlanet() : entry.getValue().toString());
                    } else if (entry.getKey().equals("shipType")) {
                        ship.setShipType(entry.getValue() == null ? ship.getShipType() : ShipType.valueOf(entry.getValue().toString()));
                    } else if (entry.getKey().equals("prodDate")) {
                        ship.setProdDate(entry.getValue() == null ? ship.getProdDate() : Long.parseLong(entry.getValue().toString()));
                    } else if (entry.getKey().equals("isUsed")) {
                        ship.setUsed(entry.getValue() == null ? ship.getUsed() : Boolean.valueOf(entry.getValue().toString()));
                    } else if (entry.getKey().equals("speed")) {
                        ship.setSpeed(entry.getValue() == null ? ship.getSpeed() : Double.parseDouble(entry.getValue().toString()));
                    } else if (entry.getKey().equals("crewSize")) {
                        ship.setCrewSize(entry.getValue() == null ? ship.getCrewSize() : Integer.parseInt(entry.getValue().toString()));
                    }
                }
                Double rating = ((80 * ship.getSpeed() * (ship.getUsed() == true ? (0.5) : 1)) / ((3019 - mYear) + 1));
                double roundOff = Math.round(rating * 100.0) / 100.0;
                ship.setRating(roundOff);
                shipService.updateShip(ship);
                return new ResponseEntity<>(ship, HttpStatus.OK);
            }

        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //Delete
    @RequestMapping(value = "/ships/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteShip(@PathVariable("id") Long id) {
        try {
            if (id <= 0) return new ResponseEntity(HttpStatus.BAD_REQUEST);
            else if (shipService.existsById(id)) {
                shipService.deleteShipById(id);
                return new ResponseEntity(HttpStatus.OK);
            } else {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    private UserSpecificationsBuilder getUserSpecificationBuilder(String name,
                                                                  String planet,
                                                                  ShipType shipType,
                                                                  Long after,
                                                                  Long before,
                                                                  Boolean isUsed,
                                                                  Double minSpeed,
                                                                  Double maxSpeed,
                                                                  Integer minCrewSize,
                                                                  Integer maxCrewSize,
                                                                  Double minRating,
                                                                  Double maxRating) {
        UserSpecificationsBuilder builder = new UserSpecificationsBuilder();
        if (name != null) {
            builder.with("name", ":", name);
        }
        if (planet != null) {
            builder.with("planet", ":", planet);
        }
        if (shipType != null) {
            builder.with("shipType", ":", shipType);
        }
        if (after != null) {
            builder.with("prodDate", ">", after);
        }
        if (before != null) {
            builder.with("prodDate", "<", before);
        }
        if (isUsed != null) {
            builder.with("isUsed", ":", isUsed);
        }
        if (minSpeed != null) {
            builder.with("speed", ">", minSpeed);
        }
        if (maxSpeed != null) {
            builder.with("speed", "<", maxSpeed);
        }
        if (minCrewSize != null) {
            builder.with("crewSize", ">", minCrewSize);
        }
        if (maxCrewSize != null) {
            builder.with("crewSize", "<", maxCrewSize);
        }
        if (minRating != null) {
            builder.with("rating", ">", minRating);
        }
        if (maxRating != null) {
            builder.with("rating", "<", maxRating);
        }
        return builder;
    }
}