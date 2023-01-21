package guru.springframework.sfgpetclinic.services.springdatajpa;

import guru.springframework.sfgpetclinic.model.Speciality;
import guru.springframework.sfgpetclinic.repositories.SpecialtyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpecialitySDJpaServiceTest {

    @Mock(lenient = false)
    SpecialtyRepository specialtyRepository;

    @InjectMocks
    SpecialitySDJpaService service;

    @Test
    void deleteById() {
        service.deleteById(1l);
        service.deleteById(1l);

        // On vérifie que la méthode deleteById est bien invoquée avec le Repository injecté
        verify(specialtyRepository, times(2)).deleteById(1l);
        verify(specialtyRepository, atLeastOnce()).deleteById(1l);
        verify(specialtyRepository, atMost(3)).deleteById(1l);
        verify(specialtyRepository, never()).deleteById(5l);
    }

    @Test
    void testDelete() {
        service.delete(new Speciality());
    }

    @Test
    void findByIdTest() {
        Speciality speciality = new Speciality();

        // Argument type on thenReturn must match return type of methodCall param in when
        when(specialtyRepository.findById(1l)).thenReturn(Optional.of(speciality));

        Thread t = new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Speciality foundSpeciality = service.findById(1L);
            assertThat(foundSpeciality).isNotNull();
            service.findById(2L);
        });
        t.start();
        // Timeout verificationMode to check method invocation within timeframe
        verify(specialtyRepository, timeout(200).atLeast(2)).findById(anyLong());

        // Use of ArgumentMatcher anyLong()
        verify(specialtyRepository, times(2)).findById(anyLong());
    }

    @Test
    void findByIdBddTest() {
        // given
        Speciality speciality = new Speciality();
        given(specialtyRepository.findById(1l)).willReturn(Optional.of(speciality));

        // when
        Speciality foundSpeciality = service.findById(1l);

        assertThat(foundSpeciality).isNotNull();

        // then
        //then(specialtyRepository).should().findById(anyLong());
        then(specialtyRepository).should(times(1)).findById(anyLong()); //équivaut à la ligne précédente comme verify
        then(specialtyRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void testDeleteObject() {
        Speciality speciality = new Speciality();

        service.delete(speciality);

        // The argument matcher verifies method call with param of type Speciality
        verify(specialtyRepository).delete(any(Speciality.class));
    }

    @Test
    void testDoThrow() {
        // Le when ci-dessous n'a rien à voir avec le 1er
        // Cette syntaxe marche aussi avec les méthodes renvoyant void
        doThrow(new RuntimeException("boom")).when(specialtyRepository).delete(any());

        assertThrows(RuntimeException.class, () -> service.delete(new Speciality()));

        verify(specialtyRepository).delete(any());
    }

    @Test
    void testFindByIdThrowsBDD() {
        // La méthode given n'accepte pas les méthodes renvoyant "void"
        given(specialtyRepository.findById(anyLong())).willThrow(new RuntimeException("boom"));

        assertThrows(RuntimeException.class, () -> service.findById(1l));
        then(specialtyRepository).should().findById(1l);
    }

    @Test
    void testDeleteByIdBDD() {
        // This syntax works with void return type methods
        willThrow(new RuntimeException("boom")).given(specialtyRepository).deleteById(any());

        assertThrows(RuntimeException.class, () -> service.deleteById(1l));

        then(specialtyRepository).should().deleteById(any());
    }

    @Test
    void testSaveLambda() {
        // given
        final String MATCH_ME = "match me";
        Speciality speciality = new Speciality();
        speciality.setDescription(MATCH_ME);

        Speciality savedSpeciality = new Speciality();
        savedSpeciality.setId(1l);

        Speciality notSavedSpe = new Speciality();
        notSavedSpe.setDescription("not a match");

        // need mock to only return on match MATCH_ME string
        given(specialtyRepository.save(argThat(argument -> MATCH_ME.equals(argument.getDescription())))).willReturn(savedSpeciality);
        //when(specialtyRepository.save(argThat(arg -> MATCH_ME.equals(argument.getDescription())))).thenReturn(savedSpeciality); // Equivalent

        // when
        Speciality returnedSpeciality = service.save(speciality);
        Speciality nullValue = service.save(notSavedSpe);

        // then
        assertThat(returnedSpeciality.getId()).isEqualTo(1l);
        assertNull(nullValue);
    }

    @Test
    void testSaveLambdaNoMatch() {
        // given
        final String MATCH_ME = "match me";
        Speciality speciality = new Speciality();
        speciality.setDescription("not a match");

        Speciality savedSpeciality = new Speciality();
        savedSpeciality.setId(1l);

        // need mock to only return on match MATCH_ME string
        // on utilise lenient pour que Mockito nous laisse passer en param de save une valeur qui ne correspond
        // pas à la règle argThat
        lenient().when(specialtyRepository.save(argThat(argument -> MATCH_ME.equals(argument.getDescription())))).thenReturn(savedSpeciality);

        // when
        Speciality returnedSpeciality = service.save(speciality);

        // then
        assertNull(returnedSpeciality);
    }
}
