package guru.springframework.sfgpetclinic.services.springdatajpa;

import guru.springframework.sfgpetclinic.model.Visit;
import guru.springframework.sfgpetclinic.repositories.VisitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@Tag("behaviorDrivenDevelopment")
class VisitSDJpaServiceBddTest {
    @Mock
    VisitRepository visitRepository;
    @InjectMocks
    VisitSDJpaService service;

    Visit visit;

    @BeforeEach
    void setUp() {
        // given
        visit = new Visit(1l, LocalDate.now());
    }

    @Test
    @DisplayName("BDD - Get all visits registered")
    void findAll() {
        // given
        List<Visit> visits = new ArrayList<>();
        visits.addAll(Arrays.asList(visit, visit, new Visit()));
        given(visitRepository.findAll()).willReturn(visits);

        // when
        Set<Visit> foundVisits = service.findAll();

        // then
        assertAll(() -> then(visitRepository).should().findAll(),
                () -> assertThat(visits).hasSize(3),
                () -> assertThat(foundVisits).hasSize(2)
        );
    }

    @Test
    @DisplayName("BDD - Find registered visit through ID")
    void findById() {
        // given
        visit.setDescription("Going to Water 7");
        given(visitRepository.findById(anyLong())).willReturn(Optional.of(visit));

        // when
        Visit foundVisit = service.findById(15l);

        // then
        assertAll(() -> then(visitRepository).should().findById(anyLong()),
                () -> assertThat(foundVisit).isNotNull(),
                () -> assertThat(foundVisit.getDescription()).isEqualTo("Going to Water 7")
        );

    }

    @Test
    @DisplayName("BDD - Save a visit")
    void save() {
        // when
        service.save(visit);
        // then
        then(visitRepository).should().save(any(Visit.class));
    }

    @Test
    @DisplayName("BDD erase a visit object")
    void delete() {
        // when
        service.delete(visit);
        // then
        then(visitRepository).should().delete(visit);
    }

    @Test
    @DisplayName("BDD - use visit ID to erase it")
    void deleteById() {
        // when
        service.deleteById(4l);
        // then
        then(visitRepository).should().deleteById(anyLong());
    }
}
