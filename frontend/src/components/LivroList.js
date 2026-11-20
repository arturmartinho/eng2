import React, { useCallback, useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { livroService } from '../services/api';

function LivroList() {
  const [livros, setLivros] = useState([]);
  const [erro, setErro] = useState('');
  const navigate = useNavigate();

  const carregar = useCallback(() => {
    livroService
      .listar()
      .then((res) => setLivros(res.data))
      .catch(() => setErro('Erro ao carregar livros. O backend está rodando?'));
  }, []);

  useEffect(() => {
    carregar();
  }, [carregar]);

  const excluir = (id) => {
    if (!window.confirm('Deseja excluir este livro?')) return;
    livroService.deletar(id).then(carregar).catch(() => setErro('Erro ao excluir livro.'));
  };

  return (
    <div>
      <div className="cabecalho">
        <h2>Livros</h2>
        <Link to="/livros/novo" className="btn">+ Novo livro</Link>
      </div>
      {erro && <p className="erro">{erro}</p>}
      <table>
        <thead>
          <tr>
            <th>Título</th>
            <th>Autor</th>
            <th>ISBN</th>
            <th>Ano</th>
            <th>Gênero</th>
            <th>Ações</th>
          </tr>
        </thead>
        <tbody>
          {livros.length === 0 && (
            <tr><td colSpan="6">Nenhum livro cadastrado.</td></tr>
          )}
          {livros.map((l) => (
            <tr key={l.id}>
              <td>{l.titulo}</td>
              <td>{l.autor ? l.autor.nome : '-'}</td>
              <td>{l.isbn}</td>
              <td>{l.anoPublicacao}</td>
              <td>{l.genero}</td>
              <td>
                <button onClick={() => navigate(`/livros/editar/${l.id}`)}>Editar</button>
                <button className="perigo" onClick={() => excluir(l.id)}>Excluir</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default LivroList;
