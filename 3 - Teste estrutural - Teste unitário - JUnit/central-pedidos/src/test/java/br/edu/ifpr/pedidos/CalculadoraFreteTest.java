package br.edu.ifpr.pedidos;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CalculadoraFreteTest {

    private final CalculadoraFrete frete = new CalculadoraFrete();

    private Pedido pedido(String uf, boolean expresso, ItemPedido... itens) {
        return new Pedido(List.of(itens), uf, expresso, null);
    }

    @Test
    void deveUsarTarifaDoParana() {
        assertEquals(1200L, frete.calcular(pedido("PR", false,
            new ItemPedido("A", 1000, 1, 1, 1000, false)),
            new Cliente(false, false, 1), 10_000));
    }

    @Test
    void deveUsarTarifaDeSaoPaulo() {
        assertEquals(2000L, frete.calcular(pedido("SP", false,
            new ItemPedido("A", 1000, 1, 1, 1000, false)),
            new Cliente(false, false, 1), 10_000));
    }

    @Test
    void deveUsarTarifaDoRioDeJaneiro() {
        assertEquals(2000L, frete.calcular(pedido("RJ", false,
            new ItemPedido("A", 1000, 1, 1, 1000, false)),
            new Cliente(false, false, 1), 10_000));
    }

    @Test
    void deveUsarTarifaPadraoParaOutraUf() {
        assertEquals(3000L, frete.calcular(pedido("MG", false,
            new ItemPedido("A", 1000, 1, 1, 1000, false)),
            new Cliente(false, false, 1), 10_000));
    }

    @Test
    void deveCobrarUmaParcelaPorKgOuFracaoAcimaDeDoisKg() {
        assertEquals(1500L, frete.calcular(pedido("PR", false,
            new ItemPedido("A", 1000, 1, 1, 2001, false)),
            new Cliente(false, false, 1), 10_000));
    }

    @Test
    void deveCobrarTresParcelasParaQuatroKgEFracao() {
        assertEquals(2100L, frete.calcular(pedido("PR", false,
            new ItemPedido("A", 1000, 1, 1, 4001, false)),
            new Cliente(false, false, 1), 10_000));
    }

    @Test
    void freteNormalEhGratisQuandoLiquidoAtingeTrezentosReais() {
        assertEquals(0L, frete.calcular(pedido("PR", false,
            new ItemPedido("A", 1000, 1, 1, 5000, false)),
            new Cliente(false, false, 1), 30_000));
    }

    @Test
    void freteExpressoNaoFicaGratis() {
        assertEquals(3600L, frete.calcular(pedido("PR", true,
            new ItemPedido("A", 1000, 1, 1, 5000, false)),
            new Cliente(false, false, 1), 30_000));
    }

    @Test
    void vipPagaMetadeDoFrete() {
        assertEquals(600L, frete.calcular(pedido("PR", false,
            new ItemPedido("A", 1000, 1, 1, 1000, false)),
            new Cliente(true, false, 1), 10_000));
    }

    @Test
    void expressoAcrescentaQuinzeReais() {
        assertEquals(2700L, frete.calcular(pedido("PR", true,
            new ItemPedido("A", 1000, 1, 1, 1000, false)),
            new Cliente(false, false, 1), 10_000));
    }

    @Test
    void fragilAcrescentaCincoReais() {
        assertEquals(1700L, frete.calcular(pedido("PR", false,
            new ItemPedido("A", 1000, 1, 1, 1000, true)),
            new Cliente(false, false, 1), 10_000));
    }

    @Test
    void adicionaisContinuamQuandoFreteBaseFoiZerado() {
        assertEquals(3200L, frete.calcular(pedido("PR", true,
            new ItemPedido("A", 1000, 1, 1, 1000, true)),
            new Cliente(false, false, 1), 30_000));
    }

    @Test
    void freteNegativoEhRejeitado() {
        assertThrows(IllegalArgumentException.class,
            () -> frete.calcular(pedido("PR", false,
                new ItemPedido("A", 1000, 1, 1, 1000, false)),
                new Cliente(false, false, 1), -1));
    }
}
