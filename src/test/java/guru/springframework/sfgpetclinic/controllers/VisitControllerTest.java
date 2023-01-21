package guru.springframework.sfgpetclinic.controllers;

import guru.springframework.sfgpetclinic.model.Pet;
import guru.springframework.sfgpetclinic.model.Visit;
import guru.springframework.sfgpetclinic.services.VisitService;
import guru.springframework.sfgpetclinic.services.map.PetMapService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class VisitControllerTest {
    @Mock
    VisitService visitService;
    @Spy //@Mock
    PetMapService petService;
    @InjectMocks
    VisitController visitController;

    @Test
    void loadPetWithVisit() {
        // given
        Map<String, Object> model = new HashMap<>();
        Pet pet = new Pet(12L);
        Pet pet4 = new Pet(4L);
        petService.save(pet);
        petService.save(pet4);

        given(petService.findById(anyLong())).willCallRealMethod();

        // when
        Visit visit = visitController.loadPetWithVisit(12L, model);

        // then
        assertNotNull(visit);
        assertNotNull(visit.getPet());
        assertEquals(12L, visit.getPet().getId());
    }

    @Test
    void loadPetWithVisitWithStubbing() {
        // given
        Map<String, Object> model = new HashMap<>();
        Pet pet = new Pet(12L);
        Pet pet4 = new Pet(4L);
        petService.save(pet);
        petService.save(pet4);

        given(petService.findById(anyLong())).willReturn(pet4);

        // when
        Visit visit = visitController.loadPetWithVisit(12L, model);

        // then
        assertNotNull(visit);
        assertNotNull(visit.getPet());
        assertEquals(4L, visit.getPet().getId()); // ATTENTION
    }
}
