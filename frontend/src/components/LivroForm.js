import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { autorService, livroService } from '../services/api';

const vazio = { titulo: '', isbn: '', anoPublicacao: '', genero: '', autorId: '' };

function LivroForm() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [livro, setLivro] = useState(vazio);
  const [autores, setAutores] = useState([]);
  const [erro, setErro] = useState('');

  useEffect(() => {
    // carrega os autores para o <select>
    autorService.listar().then((res) => setAutores(res.data)).catch(() => {});

    if (id) {
      livroService
        .buscar(id)
        .then((res) => {
          const l = res.data;
          setLivro({
            titulo: l.titulo || '',
            isbn: l.isbn || '',
            anoPublicacao: l.anoPublicacao ?? '',
            genero: l.genero || '',
            autorId: l.autor ? String(l.autor.id) : '',
          });
        })
        .catch(() => setErro('Livro não encontrado.'));
    }
  }, [id]);

  const alterar = (e) => setLivro({ ...livro, [e.target.name]: e.target.value });

  const salvar = (e) => {
    e.preventDefault();
    // o autor vai só com o id, igual ao "contato": {"id": 1} da Agenda
    const dados = {
      titulo: livro.titulo,
      isbn: livro.isbn,
      anoPublicacao: livro.anoPublicacao === '' ? null : Number(livro.anoPublicacao),
      genero: livro.genero,
      autor: { id: Number(livro.autorId) },
    };
    const req = id ? livroService.atualizar(id, dados) : livroService.criar(dados);
    req
      .then(() => navigate('/livros'))
      .catch(() => setErro('Erro ao salvar. Verifique os campos e o autor.'));
  };

  return (
    <div>
      <h2>{id ? 'Editar livro' : 'Novo livro'}</h2>
      {erro && <p className="erro">{erro}</p>}
      <form onSubmit={salvar}>
        <label>
          Título *
          <input name="titulo" value={livro.titulo} onChange={alterar} maxLength={200} required />
        </label>
        <label>
          Autor *
          <select name="autorId" value={livro.autorId} onChange={alterar} required>
            <option value="">Selecione um autor</option>
            {autores.map((a) => (
              <option key={a.id} value={a.id}>{a.nome}</option>
            ))}
          </select>
        </label>
        <label>
          ISBN
          <input name="isbn" value={livro.isbn} onChange={alterar} maxLength={20} />
        </label>
        <label>
          Ano de publicação
          <input type="number" name="anoPublicacao" value={livro.anoPublicacao} onChange={alterar} />
        </label>
        <label>
          Gênero
          <input name="genero" value={livro.genero} onChange={alterar} maxLength={50} />
        </label>
        <div className="acoes">
          <button type="submit" className="btn">Salvar</button>
          <button type="button" onClick={() => navigate('/livros')}>Cancelar</button>
        </div>
      </form>
    </div>
  );
}

export default LivroForm;
