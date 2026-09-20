import { test, expect } from '@playwright/test';

// Regras da página /frete:
//  - CEP com 8 dígitos e valor do pedido informados;
//  - CEP iniciado por 8: frete de R$ 15,00;
//  - demais CEPs: frete de R$ 25,00;
//  - pedidos a partir de R$ 200,00 têm frete grátis.

const FRETE_15 = 'Frete: R$ 15,00';
const FRETE_25 = 'Frete: R$ 25,00';
const GRATIS = 'Frete grátis';
const INVALIDO = 'Dados inválidos';

const casos = [
  // Caminhos válidos
  { cep: '80000000', valor: '100,00', esperado: FRETE_15, classe: 'CEP 8 e pedido abaixo de 200' },
  { cep: '01310100', valor: '100,00', esperado: FRETE_25, classe: 'demais CEPs e pedido abaixo de 200' },
  { cep: '80000000', valor: '500,00', esperado: GRATIS, classe: 'CEP 8 e pedido acima de 200' },
  { cep: '01310100', valor: '500,00', esperado: GRATIS, classe: 'demais CEPs e pedido acima de 200' },

  // Valores-limite do valor do pedido (R$ 200,00)
  { cep: '80000000', valor: '199,99', esperado: FRETE_15, classe: 'valor logo abaixo do limite (CEP 8)' },
  { cep: '01310100', valor: '199,99', esperado: FRETE_25, classe: 'valor logo abaixo do limite (demais CEPs)' },
  { cep: '80000000', valor: '200,00', esperado: GRATIS, classe: 'valor no limite de 200 (CEP 8)' },
  { cep: '01310100', valor: '200,00', esperado: GRATIS, classe: 'valor no limite de 200 (demais CEPs)' },
  { cep: '01310100', valor: '200,01', esperado: GRATIS, classe: 'valor logo acima do limite' },
  { cep: '01310100', valor: '200', esperado: GRATIS, classe: 'valor inteiro no limite' },

  // Valores-limite do prefixo do CEP (8)
  { cep: '79999999', valor: '100,00', esperado: FRETE_25, classe: 'CEP 79999999: último antes do prefixo 8' },
  { cep: '80000000', valor: '100,00', esperado: FRETE_15, classe: 'CEP 80000000: primeiro com prefixo 8' },
  { cep: '89999999', valor: '100,00', esperado: FRETE_15, classe: 'CEP 89999999: último com prefixo 8' },
  { cep: '90000000', valor: '100,00', esperado: FRETE_25, classe: 'CEP 90000000: primeiro depois do prefixo 8' },

  // Valores-limite inferiores do valor (deve ser maior que zero)
  { cep: '80000000', valor: '0,01', esperado: FRETE_15, classe: 'menor valor positivo (CEP 8)' },
  { cep: '01310100', valor: '0,01', esperado: FRETE_25, classe: 'menor valor positivo (demais CEPs)' },
  { cep: '80000000', valor: '0,00', esperado: INVALIDO, classe: 'valor zero' },
  { cep: '80000000', valor: '0', esperado: INVALIDO, classe: 'valor zero inteiro' },

  // Formato do valor
  { cep: '80000000', valor: '199.99', esperado: FRETE_15, classe: 'ponto como separador decimal' },
  { cep: '80000000', valor: '100,5', esperado: FRETE_15, classe: 'uma casa decimal' },
  { cep: '80000000', valor: '100,999', esperado: INVALIDO, classe: 'três casas decimais' },
  { cep: '80000000', valor: '1.000,00', esperado: INVALIDO, classe: 'separador de milhar' },
  { cep: '80000000', valor: '', esperado: INVALIDO, classe: 'valor vazio' },
  { cep: '80000000', valor: 'abc', esperado: INVALIDO, classe: 'valor com letras' },
  { cep: '80000000', valor: '-10,00', esperado: INVALIDO, classe: 'valor negativo' },

  // Classes inválidas de CEP
  { cep: '', valor: '100,00', esperado: INVALIDO, classe: 'CEP vazio' },
  { cep: '8000000', valor: '100,00', esperado: INVALIDO, classe: 'CEP com 7 dígitos' },
  { cep: '800000000', valor: '100,00', esperado: INVALIDO, classe: 'CEP com 9 dígitos' },
  { cep: '8000abcd', valor: '100,00', esperado: INVALIDO, classe: 'CEP com letras' },
  { cep: '80000-00', valor: '100,00', esperado: INVALIDO, classe: 'CEP com hífen' },
  { cep: '', valor: '', esperado: INVALIDO, classe: 'CEP e valor vazios' },
];

for (const caso of casos) {
  test(`CEP ${caso.cep || '(vazio)'} / valor ${caso.valor || '(vazio)'} — ${caso.classe}`, async ({ page }) => {
    await page.goto('/frete');
    await page.getByLabel('CEP').fill(caso.cep);
    await page.getByLabel('Valor do pedido').fill(caso.valor);
    await page.getByRole('button', { name: 'Calcular frete' }).click();

    const resultado = page.locator('#resultado');
    await expect(resultado).toBeVisible();
    await expect(resultado).toHaveText(caso.esperado);
    await expect(resultado).toHaveAttribute('role', caso.esperado === INVALIDO ? 'alert' : 'status');
  });
}

test('link "Voltar ao início" leva à página de login', async ({ page }) => {
  await page.goto('/frete');
  await page.getByRole('link', { name: 'Voltar ao início' }).click();
  await expect(page).toHaveURL(/\/login$/);
});
