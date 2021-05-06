package it.polito.tdp.extflightdelays.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graphs;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class Model {

	private SimpleWeightedGraph<Airport, DefaultWeightedEdge> grafo;
	private ExtFlightDelaysDAO dao;
	private Map<Integer,Airport> idMap;
	private Map<Airport,Airport> visita;
	
	public Model() {
		dao= new ExtFlightDelaysDAO();
		idMap= new HashMap<>();
		dao.loadAllAirports(idMap);
	}
	
	public void creaGrafo(int x) {//minimo delle compagnie aeree
		this.grafo= new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		Graphs.addAllVertices(grafo, dao.getVertici(x, idMap));
		
		//aggiungo gli archi
		for (Rotta r : dao.getRotte(idMap)) {
			if (this.grafo.containsVertex(r.getA1()) && this.grafo.containsVertex(r.getA2())) {
				DefaultWeightedEdge e = this.grafo.getEdge(r.getA1(), r.getA2());
				if (e==null) {//non c'e arco tra i due vertici
					Graphs.addEdgeWithVertices(grafo, r.getA1(), r.getA2(), r.getN());
				} else {
					double pesoVecchio= this.grafo.getEdgeWeight(e);
					double pesoNuovo= pesoVecchio+r.getN();
					this.grafo.setEdgeWeight(e, pesoNuovo);
				}
			}
		}
		System.out.println("Grafo Creato!!");
		System.out.println("# Vertici: "+grafo.vertexSet().size());
		System.out.println("# Archi : "+grafo.edgeSet().size());
	}

	public Set<Airport> getVertici() {
		return this.grafo.vertexSet();
	}
	
	public List<Airport> trovaPercorso(Airport a1, Airport a2){
		List<Airport> percorso = new LinkedList<>();
		BreadthFirstIterator<Airport,DefaultWeightedEdge> it= new BreadthFirstIterator<>(grafo,a1);
		
		visita = new HashMap<>(); //salvo albero di visita
		visita.put(a1, null); //salvo la radice
		it.addTraversalListener(new TraversalListener<Airport,DefaultWeightedEdge>() {
			@Override
			public void connectedComponentFinished(ConnectedComponentTraversalEvent e) {
			}

			@Override
			public void connectedComponentStarted(ConnectedComponentTraversalEvent e) {	
			}

			@Override
			public void edgeTraversed(EdgeTraversalEvent<DefaultWeightedEdge> e) {
				Airport airp1= grafo.getEdgeSource(e.getEdge());
				Airport airp2= grafo.getEdgeTarget(e.getEdge());
				if (visita.containsKey(airp1) && !visita.containsKey(airp2)) {
					visita.put(airp2, airp1); //airp2 scoperto da airp1. airp1 e' il padre di airp2
				} else if(visita.containsKey(airp2) && !visita.containsKey(airp1)) {
					visita.put(airp1, airp2); //airp1 scoperto da airp2
				}
			}

			@Override
			public void vertexTraversed(VertexTraversalEvent<Airport> e) {
			}

			@Override
			public void vertexFinished(VertexTraversalEvent<Airport> e) {
			}
		});
		
		while(it.hasNext()) {
			it.next();
		}
		percorso.add(a2);
		Airport step= a2;
		while(visita.get(step)!=null) {
			step= visita.get(step);
			percorso.add(step); //percorso sara' al contrario
		}
		return percorso;
	}
	
	
	
}
