package id.ac.ui.cs.advprog.manajemenpembayaran.controller;

import id.ac.ui.cs.advprog.manajemenpembayaran.model.DummyData;
import id.ac.ui.cs.advprog.manajemenpembayaran.service.DummyDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/dummy")
@CrossOrigin(origins = "*")
public class DummyController {

    @Autowired
    private DummyDataService dummyDataService;

    @GetMapping("/data")
    public ResponseEntity<Set<DummyData>> getDummyData() {
        Set<DummyData> dataFromDatabase = dummyDataService.getAllData();

        return ResponseEntity.ok(dataFromDatabase);
    }
}