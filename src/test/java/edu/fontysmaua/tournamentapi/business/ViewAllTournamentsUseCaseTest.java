    package edu.fontysmaua.tournamentapi.business;

    import edu.fontysmaua.tournamentapi.business.impl.ViewAllTournamentsUseCaseImpl;
    import edu.fontysmaua.tournamentapi.domain.Tournament;
    import edu.fontysmaua.tournamentapi.persistence.TournamentRepository;
    import edu.fontysmaua.tournamentapi.persistence.entity.TournamentEntity;
    import org.junit.jupiter.api.BeforeEach;
    import org.junit.jupiter.api.Test;
    import org.junit.jupiter.api.extension.ExtendWith;
    import org.mockito.InjectMocks;
    import org.mockito.Mock;
    import org.mockito.junit.jupiter.MockitoExtension;
    import org.springframework.data.domain.Sort;

    import java.util.List;

    import static org.junit.jupiter.api.Assertions.*;
    import static org.mockito.Mockito.*;
    import static org.mockito.ArgumentMatchers.any;

    @ExtendWith(MockitoExtension.class)
    class ViewAllTournamentsUseCaseTest {
        @Mock
        private TournamentRepository tournamentRepo;

        @InjectMocks
        private ViewAllTournamentsUseCaseImpl tournamentViewAll;

        private TournamentEntity tournamentEntity1;
        private TournamentEntity tournamentEntity2;

        @BeforeEach
        void setUp() {
            TournamentEntity t1 = new TournamentEntity();
            t1.setId(1L);
            t1.setName("Tournament 1");

            TournamentEntity t2 = new TournamentEntity();
            t2.setId(2L);
            t2.setName("Tournament 2");
        }

        @Test
        void viewAllTournaments_success() {
            when(tournamentRepo.findAll(any(Sort.class))).thenReturn(List.of(tournamentEntity1, tournamentEntity2));

            List<Tournament> tournaments = tournamentViewAll.viewAllTournaments();

            assertNotNull(tournaments);
            assertEquals(2, tournaments.size());
            assertEquals("Tournament 1", tournaments.get(0).getName());
            assertEquals("Tournament 2", tournaments.get(1).getName());

            verify(tournamentRepo, times(1)).findAll(any(Sort.class));
        }

        @Test
        void viewAllTournaments_empty() {
            when(tournamentRepo.findAll(any(Sort.class))).thenReturn(List.of());

            List<Tournament> tournaments = tournamentViewAll.viewAllTournaments();

            assertNotNull(tournaments);
            assertTrue(tournaments.isEmpty());

            verify(tournamentRepo, times(1)).findAll(any(Sort.class));
        }

        @Test
        void viewAllTournaments_exception() {
            when(tournamentRepo.findAll(any(Sort.class))).thenThrow(new RuntimeException("Database error"));

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> tournamentViewAll.viewAllTournaments());

            assertEquals("Database error", exception.getMessage());
            verify(tournamentRepo, times(1)).findAll(any(Sort.class));
        }
    }