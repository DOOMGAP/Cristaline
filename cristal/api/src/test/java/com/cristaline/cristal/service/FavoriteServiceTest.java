package com.cristaline.cristal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cristaline.cristal.model.Favorite;
import com.cristaline.cristal.model.User;
import com.cristaline.cristal.repository.FavoriteRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {

    @Mock
    private FavoriteRepository favoriteRepository;

    private FavoriteService favoriteService;

    @BeforeEach
    void setUp() {
        favoriteService = new FavoriteService();
        ReflectionTestUtils.setField(favoriteService, "favoriteRepository", favoriteRepository);
    }

    @Test
    void shouldReturnExistingFavoriteWhenAlreadyPresent() {
        User user = new User("neo", "neo@zion.io", "encoded");
        Favorite favorite = new Favorite(user, 12L);

        when(favoriteRepository.findByUserIdAndGameId(user.getId(), 12L))
            .thenReturn(Optional.of(favorite));

        Favorite result = favoriteService.addFavorite(user, 12L);

        assertThat(result).isSameAs(favorite);
        verify(favoriteRepository, never()).save(org.mockito.ArgumentMatchers.any(Favorite.class));
    }

    @Test
    void shouldDeleteFavoriteWhenPresent() {
        User user = new User("trinity", "trinity@zion.io", "encoded");
        Favorite favorite = new Favorite(user, 8L);

        when(favoriteRepository.findByUserIdAndGameId(user.getId(), 8L))
            .thenReturn(Optional.of(favorite));

        favoriteService.removeFavorite(user, 8L);

        verify(favoriteRepository).delete(favorite);
    }

    @Test
    void shouldListUserFavorites() {
        Favorite favorite = new Favorite(new User("morpheus", "m@zion.io", "encoded"), 4L);
        when(favoriteRepository.findByUserId(3L)).thenReturn(List.of(favorite));

        assertThat(favoriteService.getUserFavorites(3L)).containsExactly(favorite);
    }
}
