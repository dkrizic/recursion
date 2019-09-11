package com.prodyna.demo.recursion;

import java.util.*;
import java.util.stream.Stream;

import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.graphdb.*;
import org.neo4j.logging.Log;
import org.neo4j.procedure.Context;
import org.neo4j.procedure.Procedure;

import static org.neo4j.graphdb.Direction.INCOMING;
import static org.neo4j.procedure.Mode.READ;
import static org.neo4j.procedure.Mode.WRITE;

public class Recursion {

    @Context
    public GraphDatabaseService db;

    @Context
    public Log log;

    @Procedure(name = "recursion.rootNode", mode = READ )
    public Stream<NodeResult> rootNode() {
        List<NodeResult> nodes = new ArrayList<>();
        final Result result = db.execute("MATCH (root:Station) OPTIONAL MATCH (root)-[r:CABLE]->(:Station) WITH root,count(r) AS rc WHERE rc = 0 RETURN root");
        while( result.hasNext() ) {
            Node node = (Node) result.next().get("root");
            log.info( "Found root node " + node );
            nodes.add( new NodeResult( node ));
        }
        return nodes.stream();
    }

    @Procedure( name = "recursion.effectiveLoad", mode = WRITE )
    public Stream<NodeResult> effectiveLoad() {
        List<NodeResult> nodes = new ArrayList<>();
        Iterator<NodeResult> rootNodes = rootNode().iterator();
        while( rootNodes.hasNext() ) {
            NodeResult rootNode = rootNodes.next();
            nodes.add( rootNode );
            cables( rootNode.node );
        }
        return nodes.stream();
    }

    private long cables( Node parentStation ) {
        long totalLoad = 0;
        Iterator<Relationship> cables = parentStation.getRelationships(INCOMING).iterator();
        while (cables.hasNext()) {
            Relationship cable = cables.next();
            if( cable.getType().name().equals("CABLE") ) {
                long load = stations( cable );
                totalLoad += load;
            }
        }
        long ownLoad = (long) parentStation.getProperty("load");
        totalLoad += ownLoad;
        parentStation.setProperty( "effectiveLoad", totalLoad );
        return totalLoad;
    }

    private long stations( Relationship parentCable ) {
        long totalLoad = 0;
        Node station = parentCable.getStartNode();
        totalLoad += cables( station );
        return totalLoad;
    }

    public class NodeResult {

        public final Node node;

        public NodeResult(Node node) {
            this.node = node;
        }
    }


}
