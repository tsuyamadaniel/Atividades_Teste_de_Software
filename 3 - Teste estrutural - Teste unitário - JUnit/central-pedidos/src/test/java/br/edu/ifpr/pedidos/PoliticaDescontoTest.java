package br.edu.ifpr.pedidos;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PoliticaDescontoTest {

    private final PoliticaDesconto politica = new PoliticaDesconto();

    @Test
    void vipRecebeDezPorCento() {
        assertEquals(1000L, politica.calcular(new Cliente(true, false, 2), 10_000, null));
    }

    @Test
    void comumRecebeCincoPorCentoAcimaDoLimite() {
        assertEquals(2500L, politica.calcular(new Cliente(false, false, 1), 50_000, null));
    }

    @Test
    void comumAbaixoDoLimiteNaoRecebeDesconto() {
        assertEquals(0L, politica.calcular(new Cliente(false, false, 1), 49_999, null));
    }

    @Test
    void cupomNuloMantemDesconto() {
        assertEquals(2500L, politica.calcular(new Cliente(false, false, 1), 50_000, null));
    }

    @Test
    void cupomEmBrancoMantemDesconto() {
        assertEquals(2500L, politica.calcular(new Cliente(false, false, 1), 50_000, "   "));
    }

    @Test
    void bemVindoConcedeVinteReaisParaNovoClienteElegivel() {
        assertEquals(2000L, politica.calcular(new Cliente(false, false, 0), 10_000, " bemvindo "));
    }

    @Test
    void bemVindoNaoConcedeComComprasAnteriores() {
        assertEquals(0L, politica.calcular(new Cliente(false, false, 1), 10_000, "BEMVINDO"));
    }

    @Test
    void bemVindoNaoConcedeAbaixoDeCemReais() {
        assertEquals(0L, politica.calcular(new Cliente(false, false, 0), 9_999, "BEMVINDO"));
    }

    @Test
    void extra10ConcedeDezPorCento() {
        assertEquals(3000L, politica.calcular(new Cliente(false, false, 1), 30_000, "EXTRA10"));
    }

    @Test
    void extra10NaoConcedeAbaixoDeDuzentosReais() {
        assertEquals(0L, politica.calcular(new Cliente(false, false, 1), 19_999, "EXTRA10"));
    }

    @Test
    void descontoCombinadoFicaLimitadoAVintePorCento() {
        assertEquals(4000L, politica.calcular(new Cliente(true, false, 0), 20_000, "EXTRA10"));
    }

    @Test
    void cupomConhecidoSemElegibilidadeMantemDescontoBase() {
        assertEquals(2500L, politica.calcular(new Cliente(false, false, 1), 50_000, "BEMVINDO"));
    }

    @Test
    void cupomDesconhecidoLancaExcecao() {
        assertThrows(IllegalArgumentException.class,
            () -> politica.calcular(new Cliente(false, false, 1), 10_000, "DESCONTO99"));
    }

    @Test
    void subtotalNegativoEhRejeitado() {
        assertThrows(IllegalArgumentException.class,
            () -> politica.calcular(new Cliente(false, false, 1), -1, null));
    }
}
