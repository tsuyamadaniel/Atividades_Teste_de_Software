import { test, expect } from '@playwright/test';

// Regras da página /senha:
//  - de 8 a 20 caracteres;
//  - ao menos uma letra maiúscula, uma minúscula e um número;
//  - espaços não são permitidos;
//  - a confirmação deve repetir a senha.

const SUCESSO = 'Senha cadastrada';
const FORA_PADRAO = 'Senha fora do padrão';
const NAO_COINCIDEM = 'As senhas não coincidem';

// Monta uma senha válida (1 maiúscula + minúsculas + 1 número) com o tamanho exato.
const comTamanho = (n: number) => 'A' + 'b'.repeat(n - 2) + '1';

const casos = [
  // Caminhos válidos
  { senha: 'Senha123', esperado: SUCESSO, classe: 'senha típica válida' },
  { senha: 'Senha@123', esperado: SUCESSO, classe: 'válida com símbolo' },
  { senha: 'aB3dE6gH9', esperado: SUCESSO, classe: 'válida com letras alternadas' },

  // Valores-limite de tamanho (8 a 20)
  { senha: comTamanho(7), esperado: FORA_PADRAO, classe: 'tamanho 7: abaixo do mínimo' },
  { senha: comTamanho(8), esperado: SUCESSO, classe: 'tamanho 8: limite mínimo' },
  { senha: comTamanho(9), esperado: SUCESSO, classe: 'tamanho 9: acima do mínimo' },
  { senha: comTamanho(19), esperado: SUCESSO, classe: 'tamanho 19: abaixo do máximo' },
  { senha: comTamanho(20), esperado: SUCESSO, classe: 'tamanho 20: limite máximo' },
  { senha: comTamanho(21), esperado: FORA_PADRAO, classe: 'tamanho 21: acima do máximo' },

  // Classes inválidas de composição
  { senha: '', esperado: FORA_PADRAO, classe: 'senha vazia' },
  { senha: 'senha1234', esperado: FORA_PADRAO, classe: 'sem maiúscula' },
  { senha: 'SENHA1234', esperado: FORA_PADRAO, classe: 'sem minúscula' },
  { senha: 'SenhaSegura', esperado: FORA_PADRAO, classe: 'sem número' },
  { senha: '12345678', esperado: FORA_PADRAO, classe: 'somente números' },
  { senha: 'abcdefgh', esperado: FORA_PADRAO, classe: 'somente minúsculas' },
  { senha: 'ABCDEFGH', esperado: FORA_PADRAO, classe: 'somente maiúsculas' },

  // Espaços
  { senha: 'Senha 1234', esperado: FORA_PADRAO, classe: 'espaço no meio' },
  { senha: ' Senha1234', esperado: FORA_PADRAO, classe: 'espaço no início' },
  { senha: 'Senha1234 ', esperado: FORA_PADRAO, classe: 'espaço no fim' },

  // Confirmação (só é avaliada quando a senha tem formato válido)
  { senha: 'Senha1234', confirmacao: 'Senha4321', esperado: NAO_COINCIDEM, classe: 'confirmação diferente' },
  { senha: 'Senha1234', confirmacao: 'senha1234', esperado: NAO_COINCIDEM, classe: 'confirmação difere na caixa' },
  { senha: 'Senha1234', confirmacao: '', esperado: NAO_COINCIDEM, classe: 'confirmação vazia' },
  { senha: 'abc', confirmacao: 'xyz', esperado: FORA_PADRAO, classe: 'senha inválida e confirmação diferente: prevalece o padrão' },
];

for (const caso of casos) {
  const rotulo = caso.senha === '' ? '(vazia)' : `${caso.senha.length} caracteres`;

  test(`senha ${rotulo} — ${caso.classe}`, async ({ page }) => {
    await page.goto('/senha');
    await page.getByLabel('Nova senha').fill(caso.senha);
    await page.getByLabel('Confirmar senha').fill(caso.confirmacao ?? caso.senha);
    await page.getByRole('button', { name: 'Cadastrar senha' }).click();

    const resultado = page.locator('#resultado');
    await expect(resultado).toBeVisible();
    await expect(resultado).toHaveText(caso.esperado);
    await expect(resultado).toHaveAttribute('role', caso.esperado === SUCESSO ? 'status' : 'alert');
  });
}

test('após o cadastro válido, os campos são limpos', async ({ page }) => {
  await page.goto('/senha');
  await page.getByLabel('Nova senha').fill('Senha1234');
  await page.getByLabel('Confirmar senha').fill('Senha1234');
  await page.getByRole('button', { name: 'Cadastrar senha' }).click();

  await expect(page.locator('#resultado')).toHaveText(SUCESSO);
  await expect(page.getByLabel('Nova senha')).toHaveValue('');
  await expect(page.getByLabel('Confirmar senha')).toHaveValue('');
});

test('após uma senha rejeitada, os campos são mantidos', async ({ page }) => {
  await page.goto('/senha');
  await page.getByLabel('Nova senha').fill('senha1234');
  await page.getByLabel('Confirmar senha').fill('senha1234');
  await page.getByRole('button', { name: 'Cadastrar senha' }).click();

  await expect(page.locator('#resultado')).toHaveText(FORA_PADRAO);
  await expect(page.getByLabel('Nova senha')).toHaveValue('senha1234');
});

test('campos de senha ficam mascarados', async ({ page }) => {
  await page.goto('/senha');
  await expect(page.getByLabel('Nova senha')).toHaveAttribute('type', 'password');
  await expect(page.getByLabel('Confirmar senha')).toHaveAttribute('type', 'password');
});

test('link "Voltar ao início" leva à página de login', async ({ page }) => {
  await page.goto('/senha');
  await page.getByRole('link', { name: 'Voltar ao início' }).click();
  await expect(page).toHaveURL(/\/login$/);
});
