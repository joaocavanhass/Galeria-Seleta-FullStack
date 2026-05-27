// ============================================================
// ARQUIVO: categoria.model.ts
// FUNÇÃO: Define a estrutura (tipo) de uma categoria no frontend.
//
// O QUE É UMA INTERFACE EM TYPESCRIPT?
// Uma interface define o "formato" de um objeto — quais campos ele tem
// e quais são os tipos de cada campo. É como um molde ou contrato.
// Se um objeto diz ser do tipo Categoria, o TypeScript garante que
// ele tem exatamente esses campos com esses tipos.
//
// POR QUE CRIAR MODELOS NO FRONTEND?
// O backend envia dados como JSON. Sem interfaces, o TypeScript
// não sabe o que esperar (seria tipo `any`). Com interfaces,
// temos autocompletar, validação de tipos e código mais seguro.
//
// DIFERENÇA DO MODELO DO BACKEND:
// O backend usa o campo "nomeUrl" como slug; aqui usamos "slug".
// O CategoriaService faz o mapeamento entre os dois nomes.
// ============================================================

// interface: palavra-chave do TypeScript para definir a estrutura de um objeto.
// export: torna a interface disponível para ser importada em outros arquivos.
export interface Categoria {
  id: number;          // Identificador único da categoria no banco de dados
  cat_pai_id: number | null; // ID da categoria "mãe" (null se for categoria raiz)
  nome: string;        // Nome exibido na interface (ex: "Bolsas")
  slug: string;        // Versão da URL do nome (ex: "bolsas") — vem do campo "nomeUrl" do backend
  descricao: string | null; // Descrição opcional da categoria (null se não preenchida)
  ativo: boolean;      // true = visível no site | false = oculta
}
