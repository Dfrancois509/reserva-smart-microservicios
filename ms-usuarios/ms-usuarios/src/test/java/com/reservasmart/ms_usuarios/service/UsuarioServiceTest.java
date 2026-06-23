package com.reservasmart.ms_usuarios.service;

import com.reservasmart.ms_usuarios.dto.CrearUsuarioRequest;
import com.reservasmart.ms_usuarios.dto.UsuarioResponse;
import com.reservasmart.ms_usuarios.model.EstadoUsuario;
import com.reservasmart.ms_usuarios.model.Usuario;
import com.reservasmart.ms_usuarios.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void crearUsuario_DeberiaCrearUsuarioCorrectamente() {

        CrearUsuarioRequest request = new CrearUsuarioRequest();
        request.setNombre("Jean Kenel");
        request.setEmail("jean@test.com");
        request.setTelefono("50940000001");

        Usuario usuarioGuardado = Usuario.builder()
                .id(1L)
                .nombre(request.getNombre())
                .email(request.getEmail())
                .telefono(request.getTelefono())
                .estado(EstadoUsuario.ACTIVO)
                .fechaRegistro(LocalDateTime.now())
                .build();

        when(usuarioRepository.existsByEmail(request.getEmail()))
                .thenReturn(false);

        when(usuarioRepository.save(any(Usuario.class)))
                .thenReturn(usuarioGuardado);

        UsuarioResponse response = usuarioService.crear(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Jean Kenel", response.getNombre());
        assertEquals("jean@test.com", response.getEmail());
        assertEquals(EstadoUsuario.ACTIVO, response.getEstado());

        verify(usuarioRepository).existsByEmail(request.getEmail());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void crearUsuario_DeberiaLanzarErrorCuandoEmailExiste() {

        CrearUsuarioRequest request = new CrearUsuarioRequest();
        request.setNombre("Jean Kenel");
        request.setEmail("jean@test.com");
        request.setTelefono("50940000001");

        when(usuarioRepository.existsByEmail(request.getEmail()))
                .thenReturn(true);

        assertThrows(
                ResponseStatusException.class,
                () -> usuarioService.crear(request)
        );

        verify(usuarioRepository).existsByEmail(request.getEmail());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void buscarPorId_DeberiaRetornarUsuarioCuandoExiste() {

        Usuario usuario = Usuario.builder()
                .id(1L)
                .nombre("Jean Kenel")
                .email("jean@test.com")
                .telefono("50940000001")
                .estado(EstadoUsuario.ACTIVO)
                .fechaRegistro(LocalDateTime.now())
                .build();

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        UsuarioResponse response = usuarioService.buscarPorId(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Jean Kenel", response.getNombre());
        assertEquals(EstadoUsuario.ACTIVO, response.getEstado());

        verify(usuarioRepository).findById(1L);
    }

    @Test
    void desactivar_DeberiaCambiarEstadoAInactivo() {

        Usuario usuario = Usuario.builder()
                .id(1L)
                .nombre("Jean Kenel")
                .email("jean@test.com")
                .telefono("50940000001")
                .estado(EstadoUsuario.ACTIVO)
                .fechaRegistro(LocalDateTime.now())
                .build();

        Usuario usuarioDesactivado = Usuario.builder()
                .id(1L)
                .nombre("Jean Kenel")
                .email("jean@test.com")
                .telefono("50940000001")
                .estado(EstadoUsuario.INACTIVO)
                .fechaRegistro(usuario.getFechaRegistro())
                .build();

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(usuarioRepository.save(any(Usuario.class)))
                .thenReturn(usuarioDesactivado);

        UsuarioResponse response = usuarioService.desactivar(1L);

        assertNotNull(response);
        assertEquals(EstadoUsuario.INACTIVO, response.getEstado());

        verify(usuarioRepository).findById(1L);
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void existeUsuarioActivo_DeberiaRetornarTrueSiUsuarioEstaActivo() {

        Usuario usuario = Usuario.builder()
                .id(1L)
                .nombre("Jean Kenel")
                .email("jean@test.com")
                .telefono("50940000001")
                .estado(EstadoUsuario.ACTIVO)
                .fechaRegistro(LocalDateTime.now())
                .build();

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        boolean resultado = usuarioService.existeUsuarioActivo(1L);

        assertTrue(resultado);

        verify(usuarioRepository).findById(1L);
    }
}