package guru.springframework.sfgpetclinic.controllers;

import guru.springframework.sfgpetclinic.fauxspring.BindingResult;
import guru.springframework.sfgpetclinic.fauxspring.Model;
import guru.springframework.sfgpetclinic.model.Owner;
import guru.springframework.sfgpetclinic.services.OwnerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OwnerControllerTest {
    private static final String OWNERS_CREATE_OR_UPDATE_OWNER_FORM = "owners/createOrUpdateOwnerForm";
    @Mock
    OwnerService ownerService;

    @InjectMocks
    OwnerController controller;

    BindingResult result;
    Owner owner;

    @Captor //permet d'inspecter le contenu des arguments lors d'un appel de méthode
    ArgumentCaptor<String> stringArgumentCaptor;

    @BeforeEach
    void setUp() {
        result = mock(BindingResult.class);
        owner = new Owner(1l, "Macodou", "NDIAYE");
        // Inline captor creation example
        //final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
    }

    @Test
    void processCreationFormWithResultError() {
        // given
        given(result.hasErrors()).willReturn(true);
        //when(result.hasErrors()).thenReturn(true);

        // when
        String view = controller.processCreationForm(owner, result);

        // then
        then(result).should().hasErrors();
        //verify(result).hasErrors();
        assertEquals(OWNERS_CREATE_OR_UPDATE_OWNER_FORM, view);
    }

    @Test
    void processCreationFormNoError() {
        // given
        Owner o = new Owner(5L,"", "");
        given(result.hasErrors()).willReturn(false);
        given(ownerService.save(owner)).willReturn(o);
        //when(result.hasErrors()).thenReturn(false);
        //when(ownerService.save(owner)).thenReturn(o);

        // when
        String view = controller.processCreationForm(owner, result);

        // then
        then(result).should().hasErrors();
        then(ownerService).should().save(owner);
        //verify(result).hasErrors();
        //verify(ownerService).save(owner);
        assertEquals("redirect:/owners/5", view);
    }

    @Nested
    @DisplayName("All scenarios for method processFindFormTest")
    class ProcessFindFormTest {
        @Mock
        Model model;

        @BeforeEach
        void setUp() {
            // marche aussi avec when().thenAnswer()
            // permet d'affiner le comportement d'une invocation de méthode dans les cas complexes
            given(ownerService.findAllByLastNameLike(stringArgumentCaptor.capture()))
                    .willAnswer(invocation -> {
                        List<Owner> owners = new ArrayList<>();
                        String lastName = invocation.getArgument(0); // first method argument
                        switch (lastName) {
                            case "%NDIAYE%":
                                owners.add(owner);
                                return owners;
                            case "%%":
                                return owners;
                            case "%many%":
                                owners.add(owner);
                                owners.add(new Owner(2L, "Jo", "SARR"));
                                return owners;
                        }
                        throw new RuntimeException("Invalid argument");
                    });
        }

        @Test
        void processFindFormNoneFound() {
            // when
            owner.setLastName(null);
            String viewZero = controller.processFindForm(owner, result, null);

            // then
            assertAll(
                    () -> assertThat(stringArgumentCaptor.getValue()).isEqualToIgnoringCase("%%"),
                    () -> assertThat(viewZero).isEqualToIgnoringCase("owners/findOwners")
            );
            verifyNoInteractions(model);
        }

        @Test
        void processFindFormOneFound() {
            // when
            String viewOne = controller.processFindForm(owner, result, null);

            // then
            assertAll(
                    () -> assertThat(stringArgumentCaptor.getValue()).isEqualToIgnoringCase("%NDIAYE%"),
                    () -> assertThat(viewOne).isEqualToIgnoringCase("redirect:/owners/1")
            );
            verifyNoInteractions(model);
        }

        @Test
        void processFindFormSeveralFound() {
            // given
            InOrder inOrder = inOrder(model, ownerService);

            // when
            owner.setLastName("many");
            String viewMany = controller.processFindForm(owner, result, model);

            // then
            assertAll(() -> assertThat(stringArgumentCaptor.getValue()).isEqualToIgnoringCase("%many%"),
                    () -> assertThat(viewMany).isEqualToIgnoringCase("owners/ownersList")
            );
                // inOrder assertions must follow method calls order
                // seemingly works well with assertAll
            assertAll(
                    () -> inOrder.verify(ownerService).findAllByLastNameLike(anyString()),
                    () -> inOrder.verify(model, times(1)).addAttribute(anyString(), anyCollection())
            );
            verifyNoMoreInteractions(model);
        }
    }
}
