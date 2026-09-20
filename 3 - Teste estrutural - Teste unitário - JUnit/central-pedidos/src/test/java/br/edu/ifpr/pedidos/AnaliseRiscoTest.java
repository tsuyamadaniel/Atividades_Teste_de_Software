package br.edu.ifpr.pedidos;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnaliseRiscoTest {

    private final AnaliseRisco risco = new AnaliseRisco();

    @Test
    void deveRejeitarTotalNegativo() {
        assertThrows(IllegalArgumentException.class,
            () -> risco.avaliar(new Cliente(false, false, 1), -1, false));
    }

    @Test
    void clienteBloqueadoEhRecusado() {
        assertEquals("RECUSADO", risco.avaliar(new Cliente(false, true, 5), 10_000, false));
    }

    @Test
    void novoClienteComTotalAcimaDeMilReaisVaiParaRevisao() {
        assertEquals("REVISAO", risco.avaliar(new Cliente(false, false, 0), 100_001, false));
    }

    @Test
    void novoClienteComEntregaExpressaVaiParaRevisao() {
        assertEquals("REVISAO", risco.avaliar(new Cliente(false, false, 0), 10_000, true));
    }

    @Test
    void novoClienteComTotalNoLimiteEAEntregaNormalEhAprovado() {
        assertEquals("APROVADO", risco.avaliar(new Cliente(false, false, 0), 100_000, false));
    }

    @Test
    void clienteComHistoricoAcimaDeCincoMilEComumVaiParaRevisao() {
        assertEquals("REVISAO", risco.avaliar(new Cliente(false, false, 1), 500_001, false));
    }

    @Test
    void clienteVipComHistoricoAcimaDeCincoMilEhAprovado() {
        assertEquals("APROVADO", risco.avaliar(new Cliente(true, false, 1), 500_001, false));
    }

    @Test
    void clienteComHistoricoNoLimiteEhAprovado() {
        assertEquals("APROVADO", risco.avaliar(new Cliente(false, false, 1), 500_000, false));
    }
}
