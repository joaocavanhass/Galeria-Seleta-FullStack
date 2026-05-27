// ============================================================
// ARQUIVO: auth.service.ts
// FUNÇÃO: Serviço central de autenticação do frontend.
//
// RESPONSABILIDADES:
// - Fazer login e registro via API
// - Salvar/apagar tokens JWT e dados do usuário no localStorage
// - Expor o usuário logado como um Signal reativo (currentUser)
// - Verificar se o usuário está autenticado
// - Atualizar parcialmente os dados do perfil em memória
//
// O QUE SÃO SIGNALS (Angular 17+)?
// Signals são valores reativos — quando mudam, os componentes que
// os usam são atualizados automaticamente. É parecido com o conceito
// de "estado reativo" em outros frameworks.
//   signal(valor): cria um Signal com valor inicial
//   .set(novoValor): altera o valor do Signal
//   .asReadonly(): versão somente-leitura (não pode ser alterada de fora)
//   signal(): chama o Signal como função para ler o valor atual
//
// PERSISTÊNCIA NO LOCALSTORAGE:
// O localStorage é um "baú" do navegador onde guardamos dados entre sessões.
// Mesmo fechando e reabrindo o navegador, os dados persistem.
// Usamos 3 chaves: gs_access_token, gs_refresh_token e gs_user.
//
// OBSERVABLE (RxJS):
// Os métodos de API retornam Observable — um valor que chega de forma
// assíncrona (depois de um tempo, quando a resposta chegar do servidor).
// O pipe(tap(...)) executa uma ação secundária (salvarSessao) quando o
// Observable emite um valor, sem modificar o valor em si.
// ============================================================

// Injectable: marca a classe como serviço (pode ser injetada em outros lugares)
// signal: cria um valor reativo
// Inject/PLATFORM_ID: para verificar se está no browser ou no servidor
import { Injectable, signal, Inject, PLATFORM_ID } from '@angular/core';
// HttpClient: faz requisições HTTP para a API
import { HttpClient } from '@angular/common/http';
// isPlatformBrowser: verifica se o código está rodando no navegador
import { isPlatformBrowser } from '@angular/common';
// Observable: representa um valor assíncrono | tap: executa efeito colateral no pipe
import { Observable, tap } from 'rxjs';
// environment: variáveis de ambiente (URL base da API)
import { environment } from '../../../environments/environment';

// -------------------------------------------------------
// INTERFACES: definem os formatos de dados usados neste serviço
// -------------------------------------------------------

// Dados do usuário logado — recebidos do backend e armazenados em memória/localStorage
export interface UsuarioLogado {
  id: number;
  nome: string;
  email: string;
  telefone: string | null;
  cpf: string | null;
  papel: string;   // "cliente" ou "admin"
  criadoEm: string;
}

// Resposta da API de login e registro — contém os tokens e os dados do usuário
export interface AuthResponse {
  accessToken: string;   // Token de curta duração (usado em todas as requisições)
  refreshToken: string;  // Token de longa duração (usado só para renovar o accessToken)
  usuario: UsuarioLogado;
}

// Dados enviados para a API ao fazer login
export interface LoginRequest {
  email: string;
  senha: string;
}

// Dados enviados para a API ao criar uma nova conta
export interface RegistroRequest {
  nome: string;
  email: string;
  senha: string;
  telefone?: string; // "?" = opcional
  cpf?: string;      // "?" = opcional
}

// @Injectable({ providedIn: 'root' }): registra o serviço globalmente.
// "root" = existe uma única instância para toda a aplicação (Singleton).
// Qualquer componente que injetar AuthService recebe a mesma instância.
@Injectable({ providedIn: 'root' })
export class AuthService {

  // Chaves do localStorage — prefixadas com "gs_" (Galeria Seleta) para evitar conflito
  private readonly TOKEN_KEY   = 'gs_access_token';   // Token de acesso JWT
  private readonly REFRESH_KEY = 'gs_refresh_token';  // Token de atualização
  private readonly USER_KEY    = 'gs_user';           // Dados do usuário (JSON)

  // Signal com o usuário logado. null = não logado.
  // Inicializado com o valor salvo no localStorage (para persistir o login entre recarregamentos).
  currentUser = signal<UsuarioLogado | null>(this.carregarUsuarioStorage());

  // Construtor: o Angular injeta HttpClient e o identificador de plataforma
  constructor(
    private http: HttpClient,
    @Inject(PLATFORM_ID) private platformId: object // 'browser' ou 'server'
  ) {}

  // -------------------------------------------------------
  // login(): envia email/senha para a API e salva a sessão
  //
  // Observable<AuthResponse>: o método retorna um Observable.
  // Quem chamar login() precisa usar .subscribe() para receber o resultado.
  // .pipe(tap(...)): tap executa salvarSessao() quando a API responde com sucesso,
  //                  SEM alterar o valor retornado pelo Observable.
  // -------------------------------------------------------
  login(dados: LoginRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${environment.apiUrl}/auth/login`, dados)
      .pipe(tap(res => this.salvarSessao(res))); // Salva tokens e usuário ao receber resposta
  }

  // -------------------------------------------------------
  // registrar(): cria nova conta e já faz login automático
  // -------------------------------------------------------
  registrar(dados: RegistroRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${environment.apiUrl}/auth/register`, dados)
      .pipe(tap(res => this.salvarSessao(res))); // Salva a sessão logo após o registro
  }

  // -------------------------------------------------------
  // logout(): limpa todos os dados da sessão
  // Remove tokens do localStorage e zera o Signal currentUser.
  // -------------------------------------------------------
  logout(): void {
    if (isPlatformBrowser(this.platformId)) {
      // Remove cada chave individualmente do localStorage
      localStorage.removeItem(this.TOKEN_KEY);
      localStorage.removeItem(this.REFRESH_KEY);
      localStorage.removeItem(this.USER_KEY);
    }
    this.currentUser.set(null); // Zera o usuário logado (null = não autenticado)
  }

  // Retorna o token de acesso JWT (ou null se não logado/não estiver no browser)
  getToken(): string | null {
    if (!isPlatformBrowser(this.platformId)) return null;
    return localStorage.getItem(this.TOKEN_KEY);
  }

  // Verifica se há um usuário logado (currentUser() !== null)
  isAutenticado(): boolean {
    return this.currentUser() !== null;
  }

  // -------------------------------------------------------
  // patchUsuario(): atualiza parcialmente os dados do usuário em memória
  //
  // Partial<UsuarioLogado>: todos os campos de UsuarioLogado são opcionais aqui.
  // Spread operator (...): { ...atual, ...dados } mescla os objetos,
  //   os campos de "dados" sobrescrevem os de "atual", os demais são mantidos.
  // -------------------------------------------------------
  patchUsuario(dados: Partial<UsuarioLogado>): void {
    const atual = this.currentUser();
    if (!atual) return; // Não faz nada se não há usuário logado

    const atualizado = { ...atual, ...dados }; // Mescla os dados antigos com os novos
    if (isPlatformBrowser(this.platformId)) {
      localStorage.setItem(this.USER_KEY, JSON.stringify(atualizado)); // Persiste no localStorage
    }
    this.currentUser.set(atualizado); // Atualiza o Signal (componentes serão re-renderizados)
  }

  // -------------------------------------------------------
  // salvarSessao(): salva tokens e usuário no localStorage e atualiza o Signal
  // Método privado — chamado internamente por login() e registrar()
  // -------------------------------------------------------
  private salvarSessao(res: AuthResponse): void {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.setItem(this.TOKEN_KEY,   res.accessToken);
      localStorage.setItem(this.REFRESH_KEY, res.refreshToken);
      localStorage.setItem(this.USER_KEY,    JSON.stringify(res.usuario)); // Serializa o objeto como JSON
    }
    this.currentUser.set(res.usuario); // Atualiza o Signal com o usuário recebido
  }

  // -------------------------------------------------------
  // carregarUsuarioStorage(): lê o usuário salvo no localStorage ao iniciar o app
  // Chamado na inicialização do Signal para restaurar a sessão após recarregar a página.
  // try/catch: evita erros se o JSON estiver corrompido ou o localStorage inacessível.
  // -------------------------------------------------------
  private carregarUsuarioStorage(): UsuarioLogado | null {
    try {
      if (typeof localStorage === 'undefined') return null; // Proteção para SSR
      const raw = localStorage.getItem(this.USER_KEY);
      return raw ? JSON.parse(raw) : null; // JSON.parse: converte string JSON → objeto
    } catch {
      return null; // Se der erro ao parsear, considera não logado
    }
  }
}
