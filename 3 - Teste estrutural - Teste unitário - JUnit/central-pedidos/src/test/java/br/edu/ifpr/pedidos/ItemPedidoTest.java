package br.edu.ifpr.pedidos;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemPedidoTest {

    @Test
    void deveCalcularTotalDoItem() {
        ItemPedido item = new ItemPedido("SKU-1", 2500, 4, 10, 500, false);
        assertEquals(10_000L, item.totalCentavos());
    }

    @Test
    void deveIndicarItemDisponivelQuandoQuantidadeCabeNoEstoque() {
        ItemPedido item = new ItemPedido("SKU-1", 2500, 4, 4, 500, false);
        assertTrue(item.disponivel());
    }

    @Test
    void deveIndicarItemIndisponivelQuandoQuantidadeExcedeEstoque() {
        ItemPedido item = new ItemPedido("SKU-1", 2500, 5, 4, 500, false);
        assertFalse(item.disponivel());
    }

    @Test
    void deveAceitarQuantidadeZeroComoLinhaInativa() {
        ItemPedido item = new ItemPedido("SKU-1", 2500, 0, 0, 500, true);
        assertEquals(0L, item.totalCentavos());
        assertTrue(item.disponivel());
    }

    @Test
    void deveRejeitarSkuNulo() {
        assertThrows(IllegalArgumentException.class,
            () -> new ItemPedido(null, 100, 1, 1, 1, false));
    }

    @Test
    void deveRejeitarSkuEmBranco() {
        assertThrows(IllegalArgumentException.class,
            () -> new ItemPedido("   ", 100, 1, 1, 1, false));
    }

    @Test
    void deveRejeitarPrecoZero() {
        assertThrows(IllegalArgumentException.class,
            () -> new ItemPedido("SKU", 0, 1, 1, 1, false));
    }

    @Test
    void deveRejeitarPrecoAcimaDoLimite() {
        assertThrows(IllegalArgumentException.class,
            () -> new ItemPedido("SKU", 1_000_001, 1, 1, 1, false));
    }

    @Test
    void deveRejeitarQuantidadeNegativa() {
        assertThrows(IllegalArgumentException.class,
            () -> new ItemPedido("SKU", 100, -1, 1, 1, false));
    }

    @Test
    void deveRejeitarQuantidadeAcimaDoLimite() {
        assertThrows(IllegalArgumentException.class,
            () -> new ItemPedido("SKU", 100, 101, 101, 1, false));
    }

    @Test
    void deveRejeitarEstoqueNegativo() {
        assertThrows(IllegalArgumentException.class,
            () -> new ItemPedido("SKU", 100, 1, -1, 1, false));
    }

    @Test
    void deveRejeitarPesoZero() {
        assertThrows(IllegalArgumentException.class,
            () -> new ItemPedido("SKU", 100, 1, 1, 0, false));
    }

    @Test
    void deveRejeitarPesoAcimaDoLimite() {
        assertThrows(IllegalArgumentException.class,
            () -> new ItemPedido("SKU", 100, 1, 1, 100_001, false));
    }
}
