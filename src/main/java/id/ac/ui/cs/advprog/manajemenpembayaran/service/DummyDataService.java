package id.ac.ui.cs.advprog.manajemenpembayaran.service;

import id.ac.ui.cs.advprog.manajemenpembayaran.model.DummyData;
import id.ac.ui.cs.advprog.manajemenpembayaran.repository.DummyDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DummyDataService {

    @Autowired
    private DummyDataRepository dummyDataRepository;

    public Set<DummyData> getAllData() {
        List<DummyData> allData = dummyDataRepository.findAll();

        return allData.stream().collect(Collectors.toSet());
    }
}