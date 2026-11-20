import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { autorService } from '../services/api';

const vazio = { nome: '', nacionalidade: '', dataNascimento: '' };

function AutorForm() {
  const { id } = useParams(); // existe só na rota /autores/editar/:id
  const navigate = useNavigate();
  const [autor, setAutor] = useState(vazio);
  const [erro, setErro] = useState('');

  useEffect(() => {
    if (id) {
      autorService
        .buscar(id)
        .then((res) =>
          setAutor({
            nome: res.data.nome || '',
            nacionalidade: res.data.nacionalidade || '',
            dataNascimento: res.data.dataNascimento || '',
          })
        )
        .catch(() => setErro('Autor não encontrado.'));
    }
  }, [id]);

  const alterar = (e) => setAutor({ ...autor, [e.target.name]: e.target.value });

  const salvar = (e) => {
    e.preventDefault();
    const dados = { ...autor, dataNascimento: autor.dataNascimento || null };
    const req = id ? autorService.atualizar(id, dados) : autorService.criar(dados);
    req
      .then(() => navigate('/autores'))
      .catch(() => setErro('Erro ao salvar. Verifique os campos.'));
  };

  return (
    <div>
      <h2>{id ? 'Editar autor' : 'Novo autor'}</h2>
      {erro && <p className="erro">{erro}</p>}
      <form onSubmit={salvar}>
        <label>
          Nome *
          <input name="nome" value={autor.nome} onChange={alterar} maxLength={100} required />
        </label>
        <label>
          Nacionalidade
          <input name="nacionalidade" value={autor.nacionalidade} onChange={alterar} maxLength={50} />
        </label>
        <label>
          Data de nascimento
          <input type="date" name="dataNascimento" value={autor.dataNascimento} onChange={alterar} />
        </label>
        <div className="acoes">
          <button type="submit" className="btn">Salvar</button>
          <button type="button" onClick={() => navigate('/autores')}>Cancelar</button>
        </div>
      </form>
    </div>
  );
}

export default AutorForm;
