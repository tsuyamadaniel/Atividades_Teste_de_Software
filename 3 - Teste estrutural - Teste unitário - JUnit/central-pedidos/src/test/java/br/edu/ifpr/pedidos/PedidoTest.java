package br.edu.ifpr.pedidos;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PedidoTest {

    @Test
    void deveCalcularSubtotalIgnorandoLinhasInativas() {
        ItemPedido ativo = new ItemPedido("A", 1000, 2, 2, 500, false);
        ItemPedido inativo = new ItemPedido("B", 9000, 0, 0, 500, false);
        Pedido pedido = new Pedido(List.of(ativo, inativo), "PR", false, null);
        assertEquals(2000L, pedido.subtotalCentavos());
    }

    @Test
    void deveCalcularPesoDasLinhas() {
        ItemPedido a = new ItemPedido("A", 1000, 2, 2, 500, false);
        ItemPedido b = new ItemPedido("B", 1000, 1, 1, 700, false);
        Pedido pedido = new Pedido(List.of(a, b), "PR", false, null);
        assertEquals(1700, pedido.pesoGramas());
    }

    @Test
    void deveIdentificarFragilAtivo() {
        ItemPedido fragil = new ItemPedido("A", 1000, 1, 1, 500, true);
        Pedido pedido = new Pedido(List.of(fragil), "PR", false, null);
        assertTrue(pedido.temFragil());
    }

    @Test
    void naoDeveConsiderarFragilDeLinhaInativa() {
        ItemPedido fragil = new ItemPedido("A", 1000, 0, 0, 500, true);
        Pedido pedido = new Pedido(List.of(fragil), "PR", false, null);
        assertFalse(pedido.temFragil());
    }

    @Test
    void deveInformarEstoqueSuficiente() {
        Pedido pedido = new Pedido(List.of(
            new ItemPedido("A", 1000, 1, 1, 500, false),
            new ItemPedido("B", 1000, 2, 3, 500, false)), "PR", false, null);
        assertTrue(pedido.estoqueSuficiente());
    }

    @Test
    void devePararQuandoEncontrarFaltaDeEstoque() {
        Pedido pedido = new Pedido(List.of(
            new ItemPedido("A", 1000, 2, 1, 500, false),
            new ItemPedido("B", 1000, 1, 1, 500, false)), "PR", false, null);
        assertFalse(pedido.estoqueSuficiente());
    }

    @Test
    void deveAceitarListaVazia() {
        Pedido pedido = new Pedido(List.of(), "PR", false, null);
        assertEquals(0, pedido.subtotalCentavos());
        assertEquals(0, pedido.pesoGramas());
        assertFalse(pedido.temFragil());
        assertTrue(pedido.estoqueSuficiente());
    }

    @Test
    void deveFazerCopiaDefensivaDaLista() {
        List<ItemPedido> itens = new ArrayList<>();
        itens.add(new ItemPedido("A", 1000, 1, 1, 500, false));
        Pedido pedido = new Pedido(itens, "PR", false, null);
        itens.clear();
        assertEquals(1000L, pedido.subtotalCentavos());
        assertThrows(UnsupportedOperationException.class,
            () -> pedido.itens().add(new ItemPedido("B", 1000, 1, 1, 500, false)));
    }

    @Test
    void deveRejeitarListaNula() {
        assertThrows(IllegalArgumentException.class,
            () -> new Pedido(null, "PR", false, null));
    }

    @Test
    void deveRejeitarMaisDeCemItens() {
        List<ItemPedido> itens = new ArrayList<>();
        for (int i = 0; i < 101; i++) {
            itens.add(new ItemPedido("SKU" + i, 100, 1, 1, 1, false));
        }
        assertThrows(IllegalArgumentException.class,
            () -> new Pedido(itens, "PR", false, null));
    }

    @Test
    void deveRejeitarElementoNulo() {
        assertThrows(NullPointerException.class,
            () -> new Pedido(java.util.Arrays.asList((ItemPedido) null), "PR", false, null));
    }

    @Test
    void deveRejeitarUfInvalida() {
        assertThrows(IllegalArgumentException.class,
            () -> new Pedido(List.of(), "Pr", false, null));
    }

    @Test
    void deveAceitarUfDesconhecidaComDuasMaiusculas() {
        Pedido pedido = new Pedido(List.of(), "XX", false, null);
        assertEquals("XX", pedido.uf());
    }
}
