package guru.springframework.sfgpetclinic.services.springdatajpa;

import guru.springframework.sfgpetclinic.model.Visit;
import guru.springframework.sfgpetclinic.repositories.VisitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VisitSDJpaServiceTest {
    @Mock
    VisitRepository visitRepository;
    @InjectMocks
    VisitSDJpaService service;

    Visit visit;

    @BeforeEach
    void setUp() {
        visit = new Visit(1l, LocalDate.now());
    }

    @Test
    void findAll() {
        List<Visit> visits = new ArrayList<>();
        visits.addAll(Arrays.asList(visit, visit, new Visit()));

        assertEquals(3, visits.size());

        when(visitRepository.findAll()).thenReturn(visits);

        Set<Visit> foundVisits = service.findAll();

        verify(visitRepository).findAll();
        assertThat(foundVisits).hasSize(2);
    }

    @Test
    void findById() {
        visit.setDescription("Going to Water 7");
        when(visitRepository.findById(anyLong())).thenReturn(Optional.of(visit));

        Visit foundVisit = service.findById(15l);
        assertAll("Test findById",
                () -> verify(visitRepository).findById(anyLong()),
                () -> assertThat(foundVisit).isNotNull(),
                () -> assertThat(foundVisit.getDescription()).isEqualTo("Going to Water 7")
        );
    }

    @Test
    void save() {
        service.save(visit);
        verify(visitRepository).save(any(Visit.class));
    }

    @Test
    void delete() {
        service.delete(visit);
        verify(visitRepository).delete(visit);
    }

    @Test
    void deleteById() {
        service.deleteById(4l);
        verify(visitRepository).deleteById(anyLong());
    }
}
