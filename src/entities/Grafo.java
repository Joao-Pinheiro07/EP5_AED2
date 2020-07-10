package entities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Grafo {
	private static Map<String, Set<String>> listaDeAdjacencia = new HashMap<>(); //lista de adjacencia
	private static Set<String> verticesGigantes = new HashSet<>(); //set contendo os vertices que formam a componente gigante
	private static Map<String, Character> status = new HashMap<>(); //situação dos vértices (S, I ou R)
	
	private static final double probContagio = 0.7; //Probabilidades
	private static final double probRecuperacao = 0.3;
	
	private static void adicionar_aresta(String id1, String id2) {	//Adiciona um vértice na lista de adjacência do outro
		Set<String> set = listaDeAdjacencia.get(id1);
		if (set == null) set = new HashSet<String>();
		set.add(id2);
		listaDeAdjacencia.put(id1, set);
	}
	
	public static void gerar_lista_de_adjacencia(String arquivo) {
		try {
			BufferedReader bf = new BufferedReader(new FileReader(arquivo)); //le o arquivo com os pares e converte pra lista
			bf.readLine(); // vertices										// de adjacencia
			bf.readLine(); // arestas
			String linha = bf.readLine();
			int i = 1;
			while (linha != null) {
				String[] dados = linha.split(" ");

				adicionar_aresta(dados[0], dados[1]);
				adicionar_aresta(dados[1], dados[0]);

				System.out.println("Linha " + i++ + " lida...");				
				linha = bf.readLine();
			}
			bf.close();
		}catch (IOException e) {
			e.getMessage();
			e.printStackTrace();
		}
    }
	
	public static void setarVerticesGigantes(String vertices) {
		try {
			BufferedReader bf = new BufferedReader(new FileReader(vertices));	//le o arquivo que contem os vertices
			String linha = bf.readLine();										// da componente gigante e coloca no set
			while(linha != null) {
				verticesGigantes.add(linha);
				linha = bf.readLine();
			}
			bf.close();
		}catch(IOException e) {
			e.getMessage();
			e.printStackTrace();
		}
		System.out.println(verticesGigantes.size() + " vértices");
	}
	
	public static void simular_contagio(String saida) {		
		for(String vertice: listaDeAdjacencia.keySet()) status.put(vertice, 'S'); //colocando S em todo mundo
		String inicio = get_vertice_aleatorio();
		status.put(inicio, 'I');
		int saudaveis = verticesGigantes.size() - 1;
		int infectados = 1;
		int removidos = 0;
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(saida));
			bw.write("Passo, Infectados, Removidos, Saudaveis");
			int passos = 1;
			Deque<String> filaDeInfectados = new ArrayDeque<>();	//fila de vertices que precisaremos visitar os vizinhos
			filaDeInfectados.add(inicio);
			while(!filaDeInfectados.isEmpty()) {
				String verticeAtual = filaDeInfectados.remove();
				if(se_recuperou()) {
					status.put(verticeAtual, 'R');
					removidos++;
					infectados--;
				}
				else {
					for(String vizinho: listaDeAdjacencia.get(verticeAtual)) {
						if(status.get(vizinho) == 'S' && se_contaminou()) {
							saudaveis--;
							infectados++;
							status.put(vizinho, 'I');
							filaDeInfectados.add(vizinho);
						}						
					}
					filaDeInfectados.add(verticeAtual);
				}
				System.out.println("Passo " + passos + "...");
				bw.newLine();
				bw.write(passos++ + ", " + infectados + ", " + removidos+ ", " + saudaveis);				
			}			
			bw.close();
		}catch(IOException e){
			e.getMessage();
			e.printStackTrace();
		}
	}
	
	private static String get_vertice_aleatorio() {
		String[] vertices = verticesGigantes.toArray(new String[verticesGigantes.size()]);	//retorna um vertice aleatorio
		int aleatorio = (int) (Math.random() * verticesGigantes.size());
		return vertices[aleatorio];
	}
	
	private static boolean se_recuperou() {
		double random = Math.random();
		if(random <= probRecuperacao) return true;
		else return false;
	}
	
	private static boolean se_contaminou() {
		double random = Math.random();
		if(random <= probContagio) return true;
		else return false;
	}
}
