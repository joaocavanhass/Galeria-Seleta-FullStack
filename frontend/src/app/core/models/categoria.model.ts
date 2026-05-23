export interface Categoria {
  id: number;
  cat_pai_id: number | null;
  nome: string;
  slug: string;
  descricao: string | null;
  ativo: boolean;
}
