package br.edu.ifpr.pedidos;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PagamentoServiceTest {

    @Test
    void deveAprovarNaPrimeiraTentativa() {
        List<Long> chamadas = new ArrayList<>();
        PagamentoService service = new PagamentoService(total -> {
            chamadas.add(total);
            return true;
        });
        assertTrue(service.pagar(1000, 3));
        assertEquals(List.of(1000L), chamadas);
    }

    @Test
    void deveRecusarSemRepetirQuandoProcessadorRetornaFalse() {
        List<Long> chamadas = new ArrayList<>();
        PagamentoService service = new PagamentoService(total -> {
            chamadas.add(total);
            return false;
        });
        assertFalse(service.pagar(1000, 3));
        assertEquals(List.of(1000L), chamadas);
    }

    @Test
    void deveTentarNovamenteAposIndisponibilidadeEDepoisAprovar() {
        int[] tentativas = {0};
        PagamentoService service = new PagamentoService(total -> {
            tentativas[0]++;
            if (tentativas[0] == 1) throw new IllegalStateException();
            return true;
        });
        assertTrue(service.pagar(1000, 3));
        assertEquals(2, tentativas[0]);
    }

    @Test
    void deveEsgotarTentativasQuandoSempreIndisponivel() {
        int[] tentativas = {0};
        PagamentoService service = new PagamentoService(total -> {
            tentativas[0]++;
            throw new IllegalStateException();
        });
        assertFalse(service.pagar(1000, 3));
        assertEquals(3, tentativas[0]);
    }

    @Test
    void devePropagarOutrasExcecoes() {
        PagamentoService service = new PagamentoService(total -> {
            throw new IllegalArgumentException("erro definitivo");
        });
        assertThrows(IllegalArgumentException.class, () -> service.pagar(1000, 3));
    }

    @Test
    void deveRejeitarTotalZero() {
        PagamentoService service = new PagamentoService(total -> true);
        assertThrows(IllegalArgumentException.class, () -> service.pagar(0, 1));
    }

    @Test
    void deveRejeitarTotalNegativo() {
        PagamentoService service = new PagamentoService(total -> true);
        assertThrows(IllegalArgumentException.class, () -> service.pagar(-1, 1));
    }

    @Test
    void deveRejeitarLimiteDeTentativasZero() {
        PagamentoService service = new PagamentoService(total -> true);
        assertThrows(IllegalArgumentException.class, () -> service.pagar(1000, 0));
    }

    @Test
    void deveRejeitarMaisDeTresTentativas() {
        PagamentoService service = new PagamentoService(total -> true);
        assertThrows(IllegalArgumentException.class, () -> service.pagar(1000, 4));
    }

    @Test
    void deveRejeitarProcessadorNulo() {
        assertThrows(NullPointerException.class, () -> new PagamentoService(null));
    }
}
