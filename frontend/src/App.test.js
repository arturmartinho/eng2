import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom';
import App from './App';

jest.mock('./services/api', () => ({
  autorService: { listar: () => Promise.resolve({ data: [] }) },
  livroService: { listar: () => Promise.resolve({ data: [] }) },
}));

test('renderiza o menu da Biblioteca Web', async () => {
  render(<App />);
  expect(screen.getByText(/Biblioteca Web/i)).toBeInTheDocument();
  expect(screen.getByRole('link', { name: 'Livros' })).toBeInTheDocument();
  expect(await screen.findByText(/Nenhum autor cadastrado/i)).toBeInTheDocument();
});
