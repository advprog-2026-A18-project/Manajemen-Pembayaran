package id.ac.ui.cs.advprog.manajemenpembayaran.service;

import id.ac.ui.cs.advprog.manajemenpembayaran.model.DummyData;
import id.ac.ui.cs.advprog.manajemenpembayaran.repository.DummyDataRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DummyDataServiceTest {

    @Mock
    private DummyDataRepository dummyDataRepository;

    @InjectMocks
    private DummyDataService dummyDataService;

    @Test
    void testGetAllDataWithDuplicates() {
        DummyData data1 = DummyData.builder().id("1").message("A").status("S").build();
        DummyData data2 = DummyData.builder().id("1").message("A").status("S").build();

        when(dummyDataRepository.findAll()).thenReturn(List.of(data1, data2));

        Set<DummyData> result = dummyDataService.getAllData();

        assertEquals(1, result.size());
    }
}