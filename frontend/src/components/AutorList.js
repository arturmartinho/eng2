import React, { useCallback, useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { autorService } from '../services/api';

function AutorList() {
  const [autores, setAutores] = useState([]);
  const [erro, setErro] = useState('');
  const navigate = useNavigate();

  const carregar = useCallback(() => {
    autorService
      .listar()
      .then((res) => setAutores(res.data))
      .catch(() => setErro('Erro ao carregar autores. O backend está rodando?'));
  }, []);

  useEffect(() => {
    carregar();
  }, [carregar]);

  const excluir = (id) => {
    if (!window.confirm('Deseja excluir este autor?')) return;
    autorService
      .deletar(id)
      .then(carregar)
      .catch((e) =>
        setErro(e.response?.data?.erro || 'Erro ao excluir. Exclua os livros do autor antes.')
      );
  };

  return (
    <div>
      <div className="cabecalho">
        <h2>Autores</h2>
        <Link to="/autores/novo" className="btn">+ Novo autor</Link>
      </div>
      {erro && <p className="erro">{erro}</p>}
      <table>
        <thead>
          <tr>
            <th>Nome</th>
            <th>Nacionalidade</th>
            <th>Nascimento</th>
            <th>Ações</th>
          </tr>
        </thead>
        <tbody>
          {autores.length === 0 && (
            <tr><td colSpan="4">Nenhum autor cadastrado.</td></tr>
          )}
          {autores.map((a) => (
            <tr key={a.id}>
              <td>{a.nome}</td>
              <td>{a.nacionalidade}</td>
              <td>{a.dataNascimento ? a.dataNascimento.split('-').reverse().join('/') : ''}</td>
              <td>
                <button onClick={() => navigate(`/autores/editar/${a.id}`)}>Editar</button>
                <button className="perigo" onClick={() => excluir(a.id)}>Excluir</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default AutorList;
