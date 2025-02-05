package integration;

import app.foot.FootApi;
import app.foot.controller.rest.Player;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = FootApi.class)
@AutoConfigureMockMvc
@Transactional
class PlayerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    Player player1() {
        return Player.builder()
                .id(1)
                .name("J1")
                .isGuardian(false)
                .teamName("E1")
                .build();
    }

    Player player2() {
        return Player.builder()
                .id(2)
                .name("J2")
                .isGuardian(false)
                .teamName("E1")
                .build();
    }

    Player player3() {
        return Player.builder()
                .id(3)
                .name("J3")
                .isGuardian(false)
                .teamName("E2")
                .build();
    }



    @Test
    void read_players_ok() throws Exception {
        MockHttpServletResponse response = mockMvc
                .perform(get("/players"))
                .andReturn()
                .getResponse();
        List<Player> actual = convertFromHttpResponse(response);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertTrue(actual.containsAll(List.of(
                player1(),
                player2(),
                player3())));
    }

    @Test
    void update_players_ok() throws Exception {
        Player toUpdate = Player.builder()
                .id(2)
                .name("Joe Doe")
                .teamName("E1")
                .isGuardian(false)
                .build();
        MockHttpServletResponse response = mockMvc
                .perform(put("/players")
                        .content(objectMapper.writeValueAsString(List.of(toUpdate)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept("application/json"))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse();

        List<Player> actual = convertFromHttpResponse(response);

        assertEquals(1, actual.size());
        assertEquals(toUpdate, actual.get(0));
    }

    @Test
    void update_players_ko() throws Exception {
        Player toUpdate = Player.builder()
                .id(2)
                .name("Joe Doe")
                .teamName("E2")
                .isGuardian(false)
                .build();
        MockHttpServletResponse response = mockMvc
                .perform(put("/players")
                        .content(objectMapper.writeValueAsString(List.of(toUpdate)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept("application/json"))
                .andExpect(status().isMethodNotAllowed())
                .andReturn()
                .getResponse();
    }

    @Test
    void create_players_ok() throws Exception {
        Player toCreate = Player.builder()
                .name("Joe Doe")
                .isGuardian(false)
                .teamName("E1")
                .build();
        MockHttpServletResponse response = mockMvc
                .perform(post("/players")
                        .content(objectMapper.writeValueAsString(List.of(toCreate)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept("application/json"))
                .andReturn()
                .getResponse();
        List<Player> actual = convertFromHttpResponse(response);

        assertEquals(1, actual.size());
        assertEquals(toCreate, actual.get(0).toBuilder().id(null).build());
    }

    private List<Player> convertFromHttpResponse(MockHttpServletResponse response)
            throws JsonProcessingException, UnsupportedEncodingException {
        CollectionType playerListType = objectMapper.getTypeFactory()
                .constructCollectionType(List.class, Player.class);
        return objectMapper.readValue(
                response.getContentAsString(),
                playerListType);
    }
}
